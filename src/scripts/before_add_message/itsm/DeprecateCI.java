package scripts.before_add_message.itsm;

import java.util.List;

import scripts.itsm.CommonITSM;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;

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
