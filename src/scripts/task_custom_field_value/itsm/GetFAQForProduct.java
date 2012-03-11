package scripts.task_custom_field_value.itsm;

import java.util.HashMap;
import java.util.List;

import scripts.itsm.CommonITSM;

import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;


public class GetFAQForProduct extends CommonITSM implements TaskUDFValueScript {


    public Object calculate(SecuredTaskBean task) throws GranException {
        HashMap<String, SecuredUDFValueBean> m = task.getUDFValues();
        SecuredUDFValueBean faq = m.get(INCIDENT_PRODUCT_UDFID);
        StringBuffer bf = new StringBuffer();
        boolean emptyFAQ = true;
        if (faq != null) {
            Object value = faq.getValue();
            if (value != null) {
                List<SecuredTaskBean> productsInvolved = (List<SecuredTaskBean>) value;
                if (!productsInvolved.isEmpty()) {
                    bf.append("<div style=\"max-height: 200px; overflow-y: auto\">");

                    for (SecuredTaskBean product : productsInvolved) {
                        SecuredUDFValueBean contentFAQ = product.getUDFValues().get(WORKAROUND_PRODUCT_UDFID);
                        if (contentFAQ != null) {
                            Object cFaq = contentFAQ.getValue();
                            if (cFaq != null) {
                                String v = cFaq.toString();
                                if (v.length()>0) {
                                bf.append(v);
                                emptyFAQ = false;
                                }
                            }
                        }

                    }
                    bf.append("</div>");
                }
            }
        }
        if (!emptyFAQ) return bf.toString();
        else return "";
    }
}
