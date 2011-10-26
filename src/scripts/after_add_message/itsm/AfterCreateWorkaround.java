package scripts.after_add_message.itsm;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.tools.EggBasket;
import scripts.itsm.CommonITSM;

import java.util.List;


public class AfterCreateWorkaround extends CommonITSM implements OperationTrigger {


     public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        SecuredTaskBean task = message.getTask();
        if (task.getCategoryId().equals(PROBLEM_CATEGORY_ID)) return followLinksProblem(message);
        else if (task.getWorkflowId().equals(INCIDENT_WORKFLOW)) return followLinksIncident(message);
        else return message;
    }
    private SecuredMessageTriggerBean followLinksProblem(SecuredMessageTriggerBean message) throws GranException {
        String text = message.getDescription();
        SecuredTaskBean task = message.getTask();
        EggBasket<SecuredUDFValueBean, SecuredTaskBean> refs = AdapterManager.getInstance().getSecuredIndexAdapterManager().getReferencedTasksForTask(task);
        SecuredUDFBean relatedUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(task.getSecure(), INCIDENT_RELATED_PROBLEM_UDFID);

        if (refs!=null )
        {
                for (SecuredUDFValueBean bean : refs.keySet()){
                    if (bean.getUdfId().equals(relatedUdf.getId())){
                List<SecuredTaskBean> incidentsInvolved = refs.get(bean);
                if (incidentsInvolved != null) {
                    for (SecuredTaskBean p : incidentsInvolved) {
                        SecuredUDFValueBean aWorkaround = p.getUDFValues().get(INCIDENT_WORKAROND_UDFID);
                                Object value_ = aWorkaround.getValue();
                                if (value_ == null)
                                    executeOperation(INCIDENT_CONFIRM_OPERATION, p, text, message.getUdfValues());
                            }
                }
                    }
                }
        }
        return message;
    }

    private SecuredMessageTriggerBean followLinksIncident(SecuredMessageTriggerBean message) throws GranException {
        String text = message.getDescription();
        SecuredTaskBean task = message.getTask();
        SecuredUDFValueBean relatedProblems = task.getUDFValues().get(INCIDENT_RELATED_PROBLEM_UDFID);
        Object value = relatedProblems.getValue();
        List<SecuredTaskBean> problemsInvolved = null;
        if (value != null) {
            problemsInvolved = (List<SecuredTaskBean>) value;
            for (SecuredTaskBean p : problemsInvolved) {
                SecuredUDFValueBean problemWorkaround = p.getUDFValues().get(PROBLEM_WORKAROND_UDFID);
                        Object value_ = problemWorkaround.getValue();
                        if (value_ == null)
                            executeOperation(PROBLEM_CONFIRM_OPERATION, p, text, message.getUdfValues());

            }

}
        return message;
    }
}
