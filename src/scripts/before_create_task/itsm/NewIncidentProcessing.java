package scripts.before_create_task.itsm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scripts.itsm.CommonITSM;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredPriorityBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.Null;

/**
 * электронная почта: max.vasenkov@gmail.com, m@vasenkov.gmail.com
 * телефон: 84993434, 8-960-555-55-55, 8 960 555 55 55, +7 (960) 55 55 55, +7-960-555-55-55
 * имя:
 * компания:
 */
public class NewIncidentProcessing extends CommonITSM implements TaskTrigger {

	public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        
        //String clientDataUDFName = KernelManager.getFind().findUdf(INCIDENT_CLIENTDATA_UDFID).getCaption();
        String client = task.getUdfValue(INCIDENT_CLIENT_UDF);
        introduceNewClient(task, client);
        String usedPriority = setPriority(task);
        // now we set first Deadline according with SLA in User custom fields

        SecuredUserBean clientUser = task.getSecure().getUser();
        if (client!=null && client.length()>0) 
        	clientUser = AdapterManager.getInstance().getSecuredUserAdapterManager().findByName(task.getSecure(), client);
        else {
            task.setUdfValue(INCIDENT_CLIENT_UDF, clientUser.getName());
            task.setUdfValue(INCIDENT_EMAIL_UDF, clientUser.getEmail());
    		task.setUdfValue(INCIDENT_PHONE_UDF, clientUser.getTel());
    		task.setUdfValue(INCIDENT_COMPANY_UDF, clientUser.getCompany());
        }
        
          List<SecuredUDFValueBean> udfvalues = clientUser.getUDFValuesList();
          applySLA(task, usedPriority, udfvalues);

        if (task.getHandlerUserId()==null){
            // set Assignee

                ArrayList statuses = KernelManager.getAcl().getEffectiveStatuses(task.getId(), task.getSubmitterId());
                    if (statuses.contains(FIRST_LINE_ROLE_ID) && !statuses.contains(ESCALATOR_BOT_ROLE)) {
                        task.setHandlerGroupId(null);
                        task.setHandlerUserId(task.getSubmitterId());
                    } else if (statuses.contains(CLIENT_ROLE_ID)){
                        task.setHandlerGroupId(FIRST_LINE_ROLE_ID);
                    }
        }
        return task;
    }

	public void applySLA(SecuredTaskTriggerBean task, String usedPriority,
			List<SecuredUDFValueBean> udfvalues) throws GranException {
		SecuredUDFBean deadlineUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(task.getSecure(), INCIDENT_DEADLINE_UDFID);
		for (SecuredUDFValueBean udf : udfvalues) {
		    if (udf.getCaption().equals("SLA - " + usedPriority.trim())) {
		        // use this one for parameters
		        Object o = udf.getValue();
		        if (o != null) {
		            int hoursAdvance = 0;
		            String sla = o.toString();
		            String slaPattern = "([0-9]*)\\s?(\\(\\s?([0-9]*)\\s?\\))?";
		            Pattern slaPat = Pattern.compile(slaPattern);
		            Matcher patternMat = slaPat.matcher(sla);
		            if (patternMat.find()) {
		                String longTerm = patternMat.group(1);
		                String shortTerm = patternMat.group(3);
		                if (longTerm != null && longTerm.length() > 0) {
		                    hoursAdvance = Integer.parseInt(longTerm);
		                    Calendar longCal = Calendar.getInstance();
		                    longCal.add(Calendar.HOUR, hoursAdvance);
		                    task.setDeadline(longCal);
		                    String deadline = task.getSecure().getUser().getDateFormatter().parse(longCal);
		                    
		                    task.setUdfValue(deadlineUdf.getCaption(), deadline);
		                }
		                if (shortTerm != null && shortTerm.length() > 0) {
		                    hoursAdvance = Integer.parseInt(shortTerm);
		                    Calendar shortCal = Calendar.getInstance();
		                    shortCal.add(Calendar.HOUR, hoursAdvance);
		                    task.setDeadline(shortCal);
		                    
		                }

		            }
		        }

		        break;
		    }
		}
	}

    protected int parseInt(String s) {
        Pattern pricePattern = Pattern.compile("\\D*(\\d+)\\D*");
        Matcher matcher = pricePattern.matcher(s);
        if (matcher.find()) {
            String priceCapture = matcher.group(1);
            return Integer.parseInt(priceCapture);

        }
        return 0;
    }

    protected String setPriority(SecuredTaskTriggerBean task) throws GranException {
        String impactUDFName = KernelManager.getFind().findUdf(INCIDENT_IMPACT_UDFID).getCaption();
        String urgencyUDFName = KernelManager.getFind().findUdf(INCIDENT_URGENCY_UDFID).getCaption();
        String urgency = task.getUdfValue(urgencyUDFName);
        String impact = task.getUdfValue(impactUDFName);
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

        List<SecuredPriorityBean> priorities = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getPriorityList(task.getSecure(), INCIDENT_WORKFLOW);
        String usedPriority = task.getPriorityId()!=null ? task.getPriority().getName() : "";
        for (SecuredPriorityBean p : priorities) {
            if (p.getOrder() == priority) {
                task.setPriorityId(p.getId());
                usedPriority = p.getName();
                break;
            }
        }
        return usedPriority;
    }

    protected void introduceNewClient(SecuredTaskTriggerBean task, String client) throws GranException {
        if (client != null && client.length()>0) {
        	SecuredUserBean clientUser = AdapterManager.getInstance().getSecuredUserAdapterManager().findByName(task.getSecure(), client);
        	if (clientUser==null){
            String clientEmail = task.getUdfValue(INCIDENT_EMAIL_UDF);
            String clientPhone = task.getUdfValue(INCIDENT_PHONE_UDF);
            String clientCompany = task.getUdfValue(INCIDENT_COMPANY_UDF);
            if (clientEmail != null && clientEmail.length() > 0) {

                    
                    String fId = KernelManager.getUser().findUserIdByEmailNameProject(clientEmail, clientEmail, task.getParentId());
                    if (fId == null) {
                        String id = AdapterManager.getInstance().getSecuredUserAdapterManager().createUser(task.getSecure(),
                                CLIENT_ROOT_ID, clientEmail, client, Null.beNull(CLIENT_ROLE_ID));
                        AdapterManager.getInstance().getSecuredUserAdapterManager().updateUser(task.getSecure(), id, clientEmail, client, clientPhone, clientEmail,
                                CLIENT_ROLE_ID, CLIENT_ROOT_ID, task.getSecure().getUser().getTimezone(), task.getSecure().getUser().getLocale(), clientCompany, null, null, null, null, true);
                        try {
                            String pwd = "";
                              for (int i = 0; i < 7; ++i) {
                        if ((int) (Math.random() * 26) % 2 == 0)
                            pwd += (char) ((int) 'a' + ((int) (Math.random() * 26)));
                        else
                            pwd += (char) ((int) '0' + ((int) (Math.random() * 10)));
                    }

                            AdapterManager.getInstance().getAuthAdapterManager().changePassword(id, pwd);
                            KernelManager.getRegistration().sendRegisterMessage(id, pwd);
                        } catch (Exception e) {
                            e.printStackTrace();  
                        }
                        SecuredUserBean clientRoot = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(task.getSecure(), CLIENT_ROOT_ID);
                        if (clientRoot != null) {
                            List<SecuredUDFValueBean> udfvalues = clientRoot.getUDFValuesList();
                            for (SecuredUDFValueBean udf : udfvalues) {
                                Object value = udf.getValue();
                                if (value != null)
                                    AdapterManager.getInstance().getSecuredUDFAdapterManager().setUserUdfValue(task.getSecure(), udf.getUdfId(), id, value.toString());
                            }
                        }

                    } else throw new UserException("Пользователь с указанным email уже существует. Выберите его из списка или укажите другой email.");
            } else throw new UserException("Вы должны указать email для нового пользователя");
        }
        	else {
        		task.setUdfValue(INCIDENT_EMAIL_UDF, clientUser.getEmail());
        		task.setUdfValue(INCIDENT_PHONE_UDF, clientUser.getTel());
        		task.setUdfValue(INCIDENT_COMPANY_UDF, clientUser.getCompany());
        	}
        }
        
    }
}