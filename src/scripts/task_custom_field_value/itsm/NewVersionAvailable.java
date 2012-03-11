package scripts.task_custom_field_value.itsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import scripts.itsm.CommonITSM;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;

public class NewVersionAvailable  extends CommonITSM implements TaskUDFValueScript {

    public Object calculate(SecuredTaskBean task) throws GranException {
        HashMap<String, SecuredUDFValueBean> m = task.getUDFValues();
        SecuredUDFValueBean ci = m.get(INCIDENT_PRODUCT_UDFID);
        List<String> bf = new ArrayList<String>();

        if (ci != null) {
            Object value = ci.getValue();
            if (value != null) {
                List<SecuredTaskBean> productsInvolved = (List<SecuredTaskBean>) value;
                if (!productsInvolved.isEmpty()) {
                    for (SecuredTaskBean product : productsInvolved) {
                        HashMap<String, SecuredUDFValueBean> udfValues = product.getUDFValues();
                        SecuredUDFValueBean replacement = udfValues.get(PRODUCT_REPLACEMENT_UDFID);
                        Object replacementValue = replacement.getValue();
                        if (replacementValue!=null){
                            List<SecuredTaskBean> newCI = (List<SecuredTaskBean>) replacementValue;
                            if (!newCI.isEmpty())
                                for (SecuredTaskBean c : newCI)
                                    if (c.getStatusId().equals(PRODUCT_STATE_IN_USE))
                                    bf.add("#"+c.getNumber());


                        }
                    }

                }
            }
               return bf;
        }
        else return null;
    }
}
