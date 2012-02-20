package scripts.before_add_message.itsm;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.*;
import scripts.before_create_task.itsm.NewIncidentProcessing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ClassifyIncident  extends NewIncidentProcessing implements OperationTrigger {

    protected String setPriority(SecuredMessageTriggerBean messageTriggerBean) throws GranException {
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
                messageTriggerBean.setPriorityId(p.getId());
                usedPriority = p.getName();
                break;
            }
        }
        return usedPriority;
    }

    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        String clientUDFName = KernelManager.getFind().findUdf(INCIDENT_CLIENT_UDFID).getCaption();
        String clientDataUDFName = KernelManager.getFind().findUdf(INCIDENT_CLIENTDATA_UDFID).getCaption();
        SecuredTaskBean t = message.getTask();
        String client = message.getUdfValue(clientUDFName); //ability to change client

        SecuredUDFValueBean oldClientUDFValue = t.getUDFValues().get(INCIDENT_CLIENT_UDFID);
        if (oldClientUDFValue !=null || oldClientUDFValue.getValue()!=null)
        {
            if (client == null) {
                    Object value = oldClientUDFValue.getValue();
                    if (value != null) {
                        client =  value.toString();
                    }
            }
        }
        
        String usedPriority = setPriority(message);
        SecuredUserBean clientUser = message.getSecure().getUser();
        if (client!=null && client.length()>0) 
        	clientUser = AdapterManager.getInstance().getSecuredUserAdapterManager().findByName(message.getSecure(), client);
        else {
            message.setUdfValue(clientUDFName, clientUser.getName());
        }

            StringBuffer bf = new StringBuffer();
            bf.append("электронная почта: ").append(clientUser.getEmail()).append("\r\n");
            bf.append("телефон: ").append(clientUser.getTel()).append("\r\n");
            bf.append("компания: ").append(clientUser.getCompany()).append("\r\n");


            List<SecuredUDFValueBean> udfvalues = clientUser.getUDFValuesList();
            SecuredUDFBean deadlineUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(message.getSecure(), INCIDENT_DEADLINE_UDFID);
            for (SecuredUDFValueBean udf : udfvalues) {
                if (udf.getCaption().equals("SLA - " + usedPriority)) {
                    // use this one for parameters
                    Object o = udf.getValue();
                    if (o != null) {
                        int hoursAdvance1 = 0;
                        String sla = o.toString();
                        String slaPattern = "([0-9]*)\\s?(\\(\\s?([0-9]*)\\s?\\))?";
                        Pattern slaPat = Pattern.compile(slaPattern);
                        Matcher patternMat = slaPat.matcher(sla);
                        if (patternMat.find()) {
                            String longTerm = patternMat.group(1);
                            String shortTerm = patternMat.group(3);
                            if (longTerm != null && longTerm.length() > 0) {
                                hoursAdvance1 = Integer.parseInt(longTerm);
                                Calendar longCal = Calendar.getInstance();
                                longCal.add(Calendar.HOUR, hoursAdvance1);
                                message.setDeadline(longCal);
                                String deadline = message.getSecure().getUser().getDateFormatter().parse(longCal);
                                bf.append("срок завершения: ").append(deadline).append("\r\n");
                                message.setUdfValue(deadlineUdf.getCaption(), deadline);
                            }
                            if (shortTerm != null && shortTerm.length() > 0) {
                                hoursAdvance1 = Integer.parseInt(shortTerm);
                                Calendar shortCal = Calendar.getInstance();
                                shortCal.add(Calendar.HOUR, hoursAdvance1);
                                message.setDeadline(shortCal);
                                bf.append("срок реакции: ").append(message.getSecure().getUser().getDateFormatter().parse(shortCal)).append("\r\n");
                            }

                        }
                    }

                    break;
                }
            }

            message.setUdfValue(clientDataUDFName, bf.toString());

        if (message.getHandlerUserId()==null){
            // set Assignee

                ArrayList statuses = KernelManager.getAcl().getEffectiveStatuses(t.getId(), t.getSubmitterId());
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
