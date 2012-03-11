package scripts.before_add_message.itsm;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import scripts.itsm.CommonITSM;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredMessageTriggerBean;

/**
* Триггер возвращает инцидент на первую линию специалисту, который контактировал с клиентом по этому инциденту. А с другой стороны - это лишь один из вариантов распределения,
* та же стратегия. KnownOperator, FreeOperator, RandomOperator
 */
public class ReturnToPrevious extends CommonITSM implements OperationTrigger{
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {

        List<SecuredMessageBean> allMessages = message.getTask().getMessages();
        List<String> users = KernelManager.getStep().getHandlerList(message.getMstatusId(), message.getTaskId());
        String firstLiner = null;
        if (allMessages!=null && !allMessages.isEmpty()){
            for (ListIterator<SecuredMessageBean> iterator = allMessages.listIterator(allMessages.size()); iterator.hasPrevious();) {
                SecuredMessageBean m = iterator.previous();
                if ( users.contains(m.getSubmitterId())){
                ArrayList statuses = KernelManager.getAcl().getEffectiveStatuses(message.getTaskId(), m.getSubmitterId());
                    if (statuses.contains(FIRST_LINE_ROLE_ID) && !statuses.contains(ESCALATOR_BOT_ROLE)) {
                        firstLiner = m.getSubmitterId();
                        break;
                    } else if (statuses.contains(SECOND_LINE_ROLE_ID) && !statuses.contains(ESCALATOR_BOT_ROLE)){
                        firstLiner = m.getSubmitterId();
                        break;
                    } else if(statuses.contains(THIRD_LINE_ROLE_ID) && !statuses.contains(ESCALATOR_BOT_ROLE)){
                        firstLiner = m.getSubmitterId();
                        break;
                    }
            }
        }
    }
        message.setHandlerGroupId(null);
        message.setHandlerUserId(firstLiner);
        return message;
}
}
