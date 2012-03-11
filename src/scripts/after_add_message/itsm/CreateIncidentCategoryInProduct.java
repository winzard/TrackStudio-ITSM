package scripts.after_add_message.itsm;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import scripts.itsm.CommonITSM;
import scripts.task_custom_field_lookup.itsm.GetCategories;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;

public class CreateIncidentCategoryInProduct extends CommonITSM implements OperationTrigger {
    public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {
        SecuredTaskBean task = message.getTask();
        SecuredUDFBean typeUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(task.getSecure(), INCIDENT_TYPE_UDFID);
        HashMap<String, SecuredUDFValueBean> m = task.getUDFValues();
         SecuredUDFValueBean product = m.get(INCIDENT_PRODUCT_UDFID);
        String udfValue = message.getUdfValue(typeUdf.getCaption());
        if (udfValue !=null && udfValue.length()>0){
         List<String> list = new ArrayList<String>();
            List<SecuredTaskBean> productsInvolved = null;
        if (product != null) {
            Object value = product.getValue();
            if (value != null) {
                productsInvolved = (List<SecuredTaskBean>) value;
                if (!productsInvolved.isEmpty()) {
                    GetCategories gc = new GetCategories();
                    for (SecuredTaskBean pro : productsInvolved) {
                        gc.recursiveGetCategory(list, pro);
                    }
                }
            }
        }

          if (!list.contains(udfValue)){
              if (productsInvolved!=null && !productsInvolved.isEmpty()){
                  for (SecuredTaskBean pro : productsInvolved) {
                        SecuredUDFValueBean contentFAQ = pro.getUDFValues().get(PRODUCT_CATEGORY_UDFID);
                        if (contentFAQ != null) {
                            Object cFaq = contentFAQ.getValue();
                            String v = "";
                            if (cFaq != null) {
                                v = cFaq.toString();
                                v = v+"\r\n" + udfValue;
                            } else v = udfValue;
                                AdapterManager.getInstance().getSecuredUDFAdapterManager().setTaskUdfValue(message.getSecure(), PRODUCT_CATEGORY_UDFID, pro.getId(), v);

                        }
                    }
              }
          }
        }

        return message;
}
}
