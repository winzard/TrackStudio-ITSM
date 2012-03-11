package scripts.task_custom_field_lookup.itsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import scripts.itsm.CommonITSM;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFLookupScript;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;


public class GetCategories extends CommonITSM implements TaskUDFLookupScript {

    public Object calculate(SecuredTaskBean task) throws GranException {
        HashMap<String, SecuredUDFValueBean> m = task.getUDFValues();
        SecuredUDFValueBean product = m.get(INCIDENT_PRODUCT_UDFID);
        List<String> list = new ArrayList<String>();
        if (product != null) {
            Object value = product.getValue();
            if (value != null) {
                List<SecuredTaskBean> productsInvolved = (List<SecuredTaskBean>) value;
                if (!productsInvolved.isEmpty()) {
                    for (SecuredTaskBean pro : productsInvolved) {
                        recursiveGetCategory(list, pro);
                    }
                }
            }
        }
        return list;
    }

    public void recursiveGetCategory(List<String> list, SecuredTaskBean pro) throws GranException {
        SecuredUDFValueBean contentFAQ = pro.getUDFValues().get(PRODUCT_CATEGORY_UDFID);
        if (contentFAQ != null) {
            Object cFaq = contentFAQ.getValue();
            if (cFaq != null) {
                String v = cFaq.toString();
                for (String s: v.split("[\r\n]")){
                    if (!list.contains(s)) list.add(s);
                }
            }
         else if (pro.getParentId()!=null){
            SecuredTaskBean npro = pro.getParent();
            if (npro.getCategoryId().equals(PRODUCT_CATEGORY_ID)) recursiveGetCategory(list, npro);
        }
        }
    }


}
