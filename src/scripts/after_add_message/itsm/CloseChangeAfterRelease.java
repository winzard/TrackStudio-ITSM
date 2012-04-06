package scripts.after_add_message.itsm;

import java.util.List;
import scripts.itsm.CommonITSM;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;


public class CloseChangeAfterRelease extends CommonITSM implements OperationTrigger {

        public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        	String text = message.getDescription();
            SecuredTaskBean task = message.getTask();
            SecuredUDFValueBean relatedProblems = task.getUDFValues().get("4028801233e8d2940133e8d884700002");
            Object value = relatedProblems.getValue();
            List<SecuredTaskBean> problemsInvolved = null;
            if (value != null) {
                problemsInvolved = (List<SecuredTaskBean>) value;
                for (SecuredTaskBean p : problemsInvolved) {
                	   executeOperation("000000002f9b2134012f9b46a5d40059", p, text, message.getUdfValues());
                	
                }

    }
            return message;
        }


    }


