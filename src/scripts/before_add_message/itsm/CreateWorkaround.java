package scripts.before_add_message.itsm;


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scripts.itsm.CommonITSM;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;

public class CreateWorkaround extends CommonITSM implements OperationTrigger {


    private String createMessage(String mstatusId, SecuredTaskBean task, String text, Map<String, String> udfMap) throws GranException {

        SecuredMessageTriggerBean createMessage = new SecuredMessageTriggerBean(
                    null /* индентификатор */,
                    text /* текст комментария */,
                    Calendar.getInstance() /* время выполнения операции */,
                    null /* потраченное время */,
                    task.getDeadline() /* Сроки выполнения задачи (deadline) */,
                    task.getBudget() /* бюджет */,
                    task.getId() /* задача */,
                    task.getSecure().getUserId() /* автор операции */,
                    null /* резолюция */,
                    task.getPriorityId() /* приоритет */,
                    null /* ответственные */,
                    task.getHandlerUserId() /* ответственный */,
                    task.getHandlerGroupId() /* ответственный, если нужно задать группу в качестве ответственного */,
                    mstatusId /* тип операции */,
                    udfMap /* Map с дополнительными полями */,
                    task.getSecure() /* SessionContext */,
                    null /* вложения */);
        return createMessage.create(true).getId();
    }

    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        SecuredTaskBean task = message.getTask();
        if (task.getCategoryId().equals(PROBLEM_CATEGORY_ID)) return executeProblem(message);
        else if (task.getWorkflowId().equals(INCIDENT_WORKFLOW)) return executeIncident(message);
        else return message;
    }

    public SecuredMessageTriggerBean executeProblem(SecuredMessageTriggerBean message) throws GranException {
        String text = message.getDescription();
        SecuredTaskBean task = message.getTask();
        SecuredUDFBean workaroundUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(task.getSecure(), PROBLEM_WORKAROND_UDFID);
        if (workaroundUdf == null) return message;
        if (task.getUDFValues().get(PROBLEM_WORKAROND_UDFID)==null || task.getUDFValues().get(PROBLEM_WORKAROND_UDFID).getValue()==null)
        {
        if (message.getUdfValue(workaroundUdf.getCaption()) == null || message.getUdfValue(workaroundUdf.getCaption()).length() == 0) {
            if (text != null && text.length() > 0) {
                HashMap<String, String> udfMap = new HashMap<String, String>();

                SecuredUDFValueBean udf = task.getUDFValues().get(PROBLEM_PRODUCT_UDFID);
                if (udf != null) {
                    Object value = udf.getValue();
                    List<SecuredTaskBean> productsInvolved = null;
                    if (value != null) {
                        productsInvolved = (List<SecuredTaskBean>) value;
                        String products = "";
                        for (SecuredTaskBean p : productsInvolved) {
                            products += ";#" + p.getNumber();
                        }
                        if (products.length() > 0) udfMap.put(udf.getCaption(), products.substring(1));
                    }
                }
                SecuredTaskTriggerBean solution = new SecuredTaskTriggerBean(WORKAROUND_ROOT_ID, text, message.getTask().getName(),
                        null, null, null, null, null, null,
                        null, null, message.getSubmitterId(), null,
                        null, null, WORKAROUND_ROOT_ID, WORKAROUND_CATEGORY_ID,
                        null, null, null, null, udfMap, message.getSecure()).create();
                message.setUdfValue(workaroundUdf.getCaption(), "#" + solution.getNumber());
            }
        }


        }

        return message;
    }



    public SecuredMessageTriggerBean executeIncident(SecuredMessageTriggerBean message) throws GranException {
        String text = message.getDescription();
        SecuredTaskBean task = message.getTask();
        SecuredUDFBean workaroundUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(task.getSecure(), INCIDENT_WORKAROND_UDFID);
        if (task.getUDFValues().get(INCIDENT_WORKAROND_UDFID)==null || task.getUDFValues().get(INCIDENT_WORKAROND_UDFID).getValue()==null)
        {
        if (message.getUdfValue(workaroundUdf.getCaption()) == null || message.getUdfValue(workaroundUdf.getCaption()).length() == 0) {
            if (text != null && text.length() > 0) {
                HashMap<String, String> udfMap = new HashMap<String, String>();

                SecuredUDFValueBean udf = task.getUDFValues().get(INCIDENT_PRODUCT_UDFID);
                if (udf != null) {
                    Object value = udf.getValue();
                    List<SecuredTaskBean> productsInvolved = null;
                    if (value != null) {
                        productsInvolved = (List<SecuredTaskBean>) value;
                        String products = "";
                        for (SecuredTaskBean p : productsInvolved) {
                            products += ";#" + p.getNumber();
                        }
                        if (products.length() > 0) udfMap.put(udf.getCaption(), products.substring(1));
                    }
                }
                SecuredTaskTriggerBean solution = new SecuredTaskTriggerBean(WORKAROUND_ROOT_ID, text, message.getTask().getName(),
                        null, null, null, null, null, null,
                        null, null, message.getSubmitterId(), null,
                        null, null, WORKAROUND_ROOT_ID, WORKAROUND_CATEGORY_ID,
                        null, null, null, null, udfMap, message.getSecure()).create();
                message.setUdfValue(workaroundUdf.getCaption(), "#" + solution.getNumber());
            }
        }


        }
        return message;
    }


}
