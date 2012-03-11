package scripts.instead_of_add_message.itsm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import scripts.itsm.CommonITSM;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredMessageTriggerBean;

public class MoveToLine extends CommonITSM implements OperationTrigger {
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
                List<SecuredMessageBean> allMessages = message.getTask().getMessages();
       // List<String> users = KernelManager.getStep().getHandlerList(message.getMstatusId(), message.getTaskId());
        String firstLiner = message.getHandlerUserId();
        String mstatusId = message.getMstatusId();
        if (allMessages!=null && !allMessages.isEmpty()){
            for (ListIterator<SecuredMessageBean> iterator = allMessages.listIterator(allMessages.size()); iterator.hasPrevious();) {
                SecuredMessageBean m = iterator.previous();
                //if ( users.contains(m.getSubmitterId())){
                ArrayList statuses = KernelManager.getAcl().getEffectiveStatuses(message.getTaskId(), m.getSubmitterId());
                    if (statuses.contains(SECOND_LINE_ROLE_ID) && !statuses.contains(ESCALATOR_BOT_ROLE)){
                        firstLiner = m.getSubmitterId();
                        mstatusId = INCIDENT_RETURN_2_LINE_OPERATION;
                        break;
                    } else if(statuses.contains(THIRD_LINE_ROLE_ID) && !statuses.contains(ESCALATOR_BOT_ROLE)){
                        firstLiner = m.getSubmitterId();
                        mstatusId = INCIDENT_RETURN_3_LINE_OPERATION;
                        break;
                    }

        }
    }

        SecuredMessageTriggerBean createMessage = new SecuredMessageTriggerBean(
                    null /* индентификатор */,
                    message.getDescription() /* текст комментария */,
                    Calendar.getInstance() /* время выполнения операции */,
                    null /* потраченное время */,
                    message.getDeadline() /* Сроки выполнения задачи (deadline) */,
                    message.getBudget() /* бюджет */,
                    message.getTaskId() /* задача */,
                    message.getSecure().getUserId() /* автор операции */,
                    null /* резолюция */,
                    message.getTask().getPriorityId() /* приоритет */,
                    message.getHandlerId() /* ответственные */,
                    firstLiner /* ответственный */,
                    null /* ответственный, если нужно задать группу в качестве ответственного */,
                    mstatusId /* тип операции */,
                    null /* Map с дополнительными полями */,
                    message.getSecure() /* SessionContext */,
                    null /* вложения */);


        
        return createMessage.create(true);
}
}
