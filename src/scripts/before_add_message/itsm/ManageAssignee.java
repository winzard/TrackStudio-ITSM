package scripts.before_add_message.itsm;


import java.util.ArrayList;
import java.util.List;

import scripts.itsm.CommonITSM;
import scripts.itsm.assignee.PeekAssigneeStrategy;
import scripts.itsm.assignee.RandomAssignee;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMessageTriggerBean;


public class ManageAssignee extends CommonITSM implements OperationTrigger {
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {

        ArrayList<String> possibleHandlers = new ArrayList<String>();
        List<String> users = KernelManager.getStep().getHandlerList(message.getMstatusId(), message.getTaskId());
        for (String userId : users) {
            ArrayList<String> statuses = KernelManager.getAcl().getEffectiveStatuses(message.getTaskId(), userId);
            if (statuses.contains(SECOND_LINE_ROLE_ID) && !statuses.contains(ESCALATOR_BOT_ROLE)) {
                possibleHandlers.add(userId);

            }
            if (statuses.contains(THIRD_LINE_ROLE_ID) && !statuses.contains(ESCALATOR_BOT_ROLE)) {
                possibleHandlers.add(userId);

            }
        }
       
        PeekAssigneeStrategy generator = new RandomAssignee(possibleHandlers);
        message.setHandlerUserId(generator.peek().toString());
        message.setHandlerGroupId(null);
        return message;
    }
}
