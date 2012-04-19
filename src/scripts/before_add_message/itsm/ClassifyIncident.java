package scripts.before_add_message.itsm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scripts.after_create_task.itsm.ApplySLA;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredPriorityBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;


public class ClassifyIncident  extends ApplySLA implements OperationTrigger {

    protected SecuredPriorityBean getPriority(SecuredMessageTriggerBean messageTriggerBean) throws GranException {
        String impactUDFName = KernelManager.getFind().findUdf(INCIDENT_IMPACT_UDFID).getCaption();
        String urgencyUDFName = KernelManager.getFind().findUdf(INCIDENT_URGENCY_UDFID).getCaption();
        String urgency = messageTriggerBean.getUdfValue(urgencyUDFName);
        String impact = messageTriggerBean.getUdfValue(impactUDFName);
        int priority = 1;

        try {
            int ur = parseInt(urgency);
            int im = parseInt(impact);
            priority = ur * im;
            if (priority == 4) priority = 3;
        } catch (NumberFormatException e) {

        }
        catch (NullPointerException ne){

        }

        List<SecuredPriorityBean> priorities = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getPriorityList(messageTriggerBean.getSecure(), INCIDENT_WORKFLOW);
        String usedPriority = messageTriggerBean.getPriorityId()!=null ? messageTriggerBean.getPriority().getName() : "";
        for (SecuredPriorityBean p : priorities) {
            if (p.getOrder() == priority) {
                return p;
            }
        }
        return null;
    }

    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        //String clientUDFName = KernelManager.getFind().findUdf(INCIDENT_CLIENT_UDFID).getCaption();
        SecuredTaskBean t = message.getTask();
        String client = message.getUdfValue(INCIDENT_CLIENT_UDF); //ability to change client
        ArrayList<SecuredUDFValueBean> map = t.getUDFValuesList();
        Object value = getUDFValueByCaption(t, INCIDENT_CLIENT_UDF);
            if (client == null && value!=null) {
                        client =  value.toString();
            }
        
        
        SecuredPriorityBean usedPriority = getPriority(message);
        SecuredUserBean clientUser = message.getSecure().getUser();
        if (client!=null && client.length()>0) {
        	clientUser = AdapterManager.getInstance().getSecuredUserAdapterManager().findByName(message.getSecure(), client);
        	message.setUdfValue(INCIDENT_EMAIL_UDF, clientUser.getEmail());
        	message.setUdfValue(INCIDENT_PHONE_UDF, clientUser.getTel());
        	message.setUdfValue(INCIDENT_COMPANY_UDF, clientUser.getCompany());
        }
        else {
            message.setUdfValue(INCIDENT_CLIENT_UDF, clientUser.getName());
            message.setUdfValue(INCIDENT_EMAIL_UDF, clientUser.getEmail());
            message.setUdfValue(INCIDENT_PHONE_UDF, clientUser.getTel());
            message.setUdfValue(INCIDENT_COMPANY_UDF, clientUser.getCompany());
        }
            List<SecuredUDFValueBean> udfvalues = clientUser.getUDFValuesList();
            String deadline = applySLA(message.getTask(), usedPriority, udfvalues);
            SecuredUDFBean deadlineUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(message.getSecure(), INCIDENT_DEADLINE_UDFID);
            message.setUdfValue(deadlineUdf.getCaption(), deadline);


        if (message.getHandlerUserId()==null){
            // set Assignee

                ArrayList<String> statuses = KernelManager.getAcl().getEffectiveStatuses(t.getId(), t.getSubmitterId());
                    if (statuses.contains(FIRST_LINE_ROLE_ID) && !statuses.contains(ESCALATOR_BOT_ROLE)) {
                        message.setHandlerGroupId(null);
                        message.setHandlerUserId(t.getSubmitterId());
                    } else if (statuses.contains(CLIENT_ROLE_ID)){
                        message.setHandlerGroupId(FIRST_LINE_ROLE_ID);
                    }
        }
        return message;
    }

    
	
}
