package scripts.before_add_message.itsm;

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

public class CloseProblemAsDuplicate extends CommonITSM implements OperationTrigger {

    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {

        SecuredTaskBean task = message.getTask();
        SecuredUDFBean duplicateUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(task.getSecure(), PROBLEM_DUPLICATE_UDFID);
        SecuredUDFBean workaroundUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(task.getSecure(), INCIDENT_WORKAROND_UDFID);
        if (duplicateUdf == null) return message;

        String udfValue = message.getUdfValue(duplicateUdf.getCaption());
        String workarounds = "";
        if (udfValue != null && udfValue.length() != 0) {

            for (String number: udfValue.split(",")){
                SecuredTaskBean b = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(message.getSecure(), number);
                SecuredUDFValueBean udf = b.getUDFValues().get(PROBLEM_WORKAROND_UDFID);
                if (udf!=null){
                    Object value = udf.getValue();
                List<SecuredTaskBean> workaroundsInvolved = null;
                if (value != null) {
                workaroundsInvolved = (List<SecuredTaskBean>) value;

                    for (SecuredTaskBean p: workaroundsInvolved){
                        workarounds+=";#"+p.getNumber();
                    }
                }

            }
            }
                if (workarounds.length()>0) workarounds = workarounds.substring(1);
            EggBasket<SecuredUDFValueBean, SecuredTaskBean> refs = AdapterManager.getInstance().getSecuredIndexAdapterManager().getReferencedTasksForTask(task);
            SecuredUDFBean relatedUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(task.getSecure(), INCIDENT_RELATED_PROBLEM_UDFID);

            if (refs != null)
                for (Entry<SecuredUDFValueBean, List<SecuredTaskBean>> entry : refs.entrySet())
                    if (entry.getKey().getUdfId().equals(relatedUdf.getId())){
                List<SecuredTaskBean> incidentsInvolved = entry.getValue();

                        if (incidentsInvolved != null)
                            for (SecuredTaskBean p : incidentsInvolved)  {
                                AdapterManager.getInstance().getSecuredUDFAdapterManager().setTaskUDFValueSimple(task.getSecure(), p.getId(), relatedUdf.getCaption(), udfValue);
                                AdapterManager.getInstance().getSecuredUDFAdapterManager().setTaskUDFValueSimple(task.getSecure(), p.getId(), workaroundUdf.getCaption(), workarounds);
                            }
                    }
        }
        return message;
    }


}
