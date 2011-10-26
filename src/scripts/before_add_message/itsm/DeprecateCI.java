package scripts.before_add_message.itsm;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.*;
import com.trackstudio.tools.EggBasket;
import scripts.itsm.CommonITSM;
import scripts.itsm.assignee.PeekAssigneeStrategy;
import scripts.itsm.assignee.RandomAssignee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeprecateCI extends CommonITSM implements OperationTrigger {
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        String text = message.getDescription();
        SecuredTaskBean task = message.getTask();
        SecuredUDFValueBean relatedProblems = task.getUDFValues().get(RFC_PRODUCT_UDFID);
                Object value = relatedProblems.getValue();
                List<SecuredTaskBean> problemsInvolved = null;
                if (value != null) {
                    problemsInvolved = (List<SecuredTaskBean>) value;
                    for (SecuredTaskBean p : problemsInvolved) {
                                    executeOperation(PRODUCT_DEPRECATE_OPERATION, p, text, message.getUdfValues());
                    }
        }
        return message;
}
    
}
