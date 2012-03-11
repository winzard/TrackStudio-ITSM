package scripts.task_custom_field_value.itsm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import scripts.itsm.CommonITSM;
import scripts.itsm.assignee.PeekAssigneeStrategy;
import scripts.itsm.assignee.RandomAssignee;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;

public class FirstLineEscalate extends CommonITSM implements TaskUDFValueScript {

    public Object calculate(SecuredTaskBean task) throws GranException {
        Calendar deadline = task.getDeadline();
        SessionContext sc = task.getSecure();
        Calendar now = Calendar.getInstance();

        if (task.getClosedate()==null && deadline != null && deadline.before(now) && (task.getHandlerUserId() != null || task.getHandlerGroupId() != null)) {
            SecuredUserBean handlerUser = task.getHandlerUser();
            String escalateFromGroup = null;
            String escalateToGroup = null;
            if (FIRST_LINE_ROLE_ID.equals(task.getHandlerGroupId())) {
                escalateFromGroup = FIRST_LINE_ROLE_ID;
                escalateToGroup = FIRST_LINE_MANAGER_ROLE_ID;
            }
            if (SECOND_LINE_ROLE_ID.equals(task.getHandlerGroupId())) {
                escalateFromGroup = SECOND_LINE_ROLE_ID;
                escalateToGroup = SECOND_LINE_MANAGER_ROLE_ID;
            }
            if (THIRD_LINE_ROLE_ID.equals(task.getHandlerGroupId())) {
                escalateFromGroup = THIRD_LINE_ROLE_ID;
                escalateToGroup = THIRD_LINE_MANAGER_ROLE_ID;
            }

            if (escalateFromGroup ==null && handlerUser != null) {
                for (SecuredPrstatusBean p : AdapterManager.getInstance().getSecuredAclAdapterManager().getAllowedPrstatusList(task.getSecure(), task.getId(), task.getHandlerUserId())) {
                    if (FIRST_LINE_ROLE_ID.equals(p.getId())) {
                        escalateFromGroup = FIRST_LINE_ROLE_ID;
                        escalateToGroup = FIRST_LINE_MANAGER_ROLE_ID;
                    }
                    if (SECOND_LINE_ROLE_ID.equals(p.getId())) {
                        escalateFromGroup = SECOND_LINE_ROLE_ID;
                        escalateToGroup =SECOND_LINE_MANAGER_ROLE_ID;
                    }
                    if (THIRD_LINE_ROLE_ID.equals(p.getId())) {
                        escalateFromGroup = THIRD_LINE_ROLE_ID;
                        escalateToGroup = THIRD_LINE_MANAGER_ROLE_ID;
                    }
                    if (escalateFromGroup!=null) {
                        break;
                    }
                }

            }
            if (escalateFromGroup!=null) {

                String newHandlerUser = null;

                List<String> users = KernelManager.getStep().getHandlerList(ESCALATE_OPERATION, task.getId());
                ArrayList<SecuredUserBean> allowedManagers = new ArrayList<SecuredUserBean>();
                for (String u: users){
                     for (SecuredPrstatusBean p : AdapterManager.getInstance().getSecuredAclAdapterManager().getAllowedPrstatusList(task.getSecure(), task.getId(), u)) {
                         if (p.getId().equals(escalateToGroup)) {
                             allowedManagers.add(new SecuredUserBean(u, sc));
                             break;
                         }
                     }
                }
                PeekAssigneeStrategy strategy = new RandomAssignee(allowedManagers);
                if (!allowedManagers.isEmpty()) {
                    newHandlerUser = ((SecuredUserBean)strategy.peek()).getId();
                    escalateToGroup = null;
                }

                /**
                 * Создаем SecuredMessageTriggerBean
                 */
                SecuredMessageTriggerBean createMessage = new SecuredMessageTriggerBean(
                        null /* индентификатор */,
                        "задача была просрочена" /* текст комментария */,
                        Calendar.getInstance() /* время выполнения операции */,
                        null /* потраченное время */,
                        task.getDeadline() /* Сроки выполнения задачи (deadline) */,
                        task.getBudget() /* бюджет */,
                        task.getId() /* задача */,
                        task.getSecure().getUserId() /* автор операции */,
                        null /* резолюция */,
                        task.getPriorityId() /* приоритет */,
                        null /* ответственные */,
                        newHandlerUser /* ответственный */,
                        escalateToGroup /* ответственный, если нужно задать группу в качестве ответственного */,
                        ESCALATE_OPERATION /* тип операции */,
                        null /* Map с дополнительными полями */,
                        task.getSecure() /* SessionContext */,
                        null /* вложения */);
                /**
                 * выполняем
                 */
                createMessage.create(true);

                return "просрочена";
            }

        }

        return "в работе";
    }

}