package scripts.after_create_task.itsm;

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
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredPriorityBean;
import com.trackstudio.secured.SecuredTaskBean;
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
public class ApplySLA extends CommonITSM implements TaskTrigger {
	protected void updateDeadlineAndPriority(SecuredTaskBean task, String priorityId, Calendar deadline) throws GranException{
        KernelManager.getTask().updateTask(task.getId(), SafeString.createSafeString(task.getShortname()), SafeString.createSafeString(task.getName()), SafeString.createSafeString(task.getDescription()), task.getBudget(), deadline, priorityId, task.getParentId(), task.getHandlerUserId(), task.getHandlerGroupId(), null, null);
    }
	public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        
        //String clientDataUDFName = KernelManager.getFind().findUdf(INCIDENT_CLIENTDATA_UDFID).getCaption();
        String client = task.getUdfValue(INCIDENT_CLIENT_UDF);
        
        SecuredPriorityBean usedPriority = getPriority(task);
        // now we set first Deadline according with SLA in User custom fields

        SecuredUserBean clientUser = task.getSecure().getUser();
        if (client!=null && client.length()>0) 
        	clientUser = AdapterManager.getInstance().getSecuredUserAdapterManager().findByName(task.getSecure(), client);
        
          List<SecuredUDFValueBean> udfvalues = clientUser.getUDFValuesList();
          String deadline = applySLA(task, usedPriority, udfvalues);
          if (deadline!=null){
          AdapterManager.getInstance().getSecuredUDFAdapterManager().setTaskUdfValue(task.getSecure(), INCIDENT_DEADLINE_UDFID, task.getId(), deadline);
          }

        return task;
    }

	protected String applySLA(SecuredTaskBean task,
			SecuredPriorityBean usedPriority, List<SecuredUDFValueBean> udfvalues)
			throws GranException {
		String deadline = null;
		if (usedPriority!=null){
		for (SecuredUDFValueBean udf : udfvalues) {
		    if (udf.getCaption().equals("SLA - " + usedPriority.getName().trim())) {
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
		                Calendar time = null;
		                if (longTerm != null && longTerm.length() > 0) {
		                    hoursAdvance = Integer.parseInt(longTerm);
		                    Calendar longCal = (Calendar)task.getSubmitdate().clone();
		                    longCal.add(Calendar.HOUR, hoursAdvance);
		                    
		                    deadline = task.getSecure().getUser().getDateFormatter().parse(longCal);
		                    time = longCal;
		                }
		                if (shortTerm != null && shortTerm.length() > 0) {
		                    hoursAdvance = Integer.parseInt(shortTerm);
		                    Calendar shortCal = (Calendar)task.getSubmitdate().clone();
		                    shortCal.add(Calendar.HOUR, hoursAdvance);
		                    time = shortCal;
		                    
		                }
		                updateDeadlineAndPriority(task, usedPriority.getId(), time);
		            }
		        }

		        break;
		    }
		}
		}
		return deadline;
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

    protected SecuredPriorityBean getPriority(SecuredTaskTriggerBean task) throws GranException {
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
        for (SecuredPriorityBean p : priorities) {
            if (p.getOrder() == priority) {
                return p;
                
            }
        }
        return null;
    }

    
}