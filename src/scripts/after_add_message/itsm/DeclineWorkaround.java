package scripts.after_add_message.itsm;

import java.util.List;
import java.util.Map.Entry;

import scripts.itsm.CommonITSM;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.tools.EggBasket;


public class DeclineWorkaround extends CommonITSM implements OperationTrigger {
      public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        SecuredTaskBean task = message.getTask();
        if (task.getCategoryId().equals(PROBLEM_CATEGORY_ID)) return executeProblem(message);
        else if (task.getWorkflowId().equals(INCIDENT_WORKFLOW)) return executeIncident(message);
        else return message;
    }



    public SecuredMessageTriggerBean executeIncident(SecuredMessageTriggerBean message) throws GranException {
        String text = message.getDescription();
        SecuredTaskBean task = message.getTask();
        SecuredUDFValueBean relatedProblems = task.getUDFValues().get(INCIDENT_RELATED_PROBLEM_UDFID);
                Object value = relatedProblems.getValue();
                List<SecuredTaskBean> problemsInvolved = null;
                if (value != null) {
                    problemsInvolved = (List<SecuredTaskBean>) value;
                    for (SecuredTaskBean p : problemsInvolved) {
                                    executeOperation(PROBLEM_DECLINE_OPERATION, p, text, message.getUdfValues());
                    }
        }
        return message;
    }

    public SecuredMessageTriggerBean executeProblem(SecuredMessageTriggerBean message) throws GranException {
        String text = message.getDescription();
        SecuredTaskBean task = message.getTask();
        EggBasket<SecuredUDFValueBean, SecuredTaskBean> refs = AdapterManager.getInstance().getSecuredIndexAdapterManager().getReferencedTasksForTask(task);
        SecuredUDFBean relatedUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(task.getSecure(), INCIDENT_RELATED_PROBLEM_UDFID);
        if (refs!=null )
        {
            for (Entry<SecuredUDFValueBean, List<SecuredTaskBean>> entry : refs.entrySet()){
                if (entry.getKey().getUdfId().equals(relatedUdf.getId())){
            List<SecuredTaskBean> incidentsInvolved = entry.getValue();
                if (incidentsInvolved != null) {
                    for (SecuredTaskBean p : incidentsInvolved) {
                                    executeOperation(INCIDENT_DECLINE_OPERATION, p, text, message.getUdfValues());
                            }
                }
                    }
                }
        }



        return message;
    }
   
}
