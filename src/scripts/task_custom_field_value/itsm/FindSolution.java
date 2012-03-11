package scripts.task_custom_field_value.itsm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import scripts.itsm.CommonITSM;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredSearchTaskItem;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;

public class FindSolution extends CommonITSM implements TaskUDFValueScript {
    public Object calculate(SecuredTaskBean securedTaskBean) throws GranException {
        HashMap<SecuredTaskBean, Float> tasks =
                AdapterManager.getInstance().getSecuredTaskAdapterManager().findSimilar(securedTaskBean.getSecure(), securedTaskBean.getId());
        ArrayList<SecuredSearchTaskItem> results = new ArrayList<SecuredSearchTaskItem>();
        for (Map.Entry e : tasks.entrySet()) {
            Float ratio = (Float) e.getValue();
            SecuredSearchTaskItem sstask = new SecuredSearchTaskItem(0, ratio, (SecuredTaskBean) e.getKey(), "", "");
            results.add(sstask);
        }
        Collections.sort(results);
        TreeSet<SecuredTaskBean> solutionsViaIncidents = new TreeSet<SecuredTaskBean>();
        TreeSet<SecuredTaskBean> solutionsViaSimilar = new TreeSet<SecuredTaskBean>();
        TreeSet<SecuredTaskBean> solutionsFAQ = new TreeSet<SecuredTaskBean>();
        HashMap<String, SecuredUDFValueBean> m = securedTaskBean.getUDFValues();
        SecuredUDFValueBean product = m.get(INCIDENT_PRODUCT_UDFID);
        List<SecuredTaskBean> productsMentioned = new ArrayList<SecuredTaskBean>();
                if (product != null) {
            Object value = product.getValue();
            if (value != null) {
                productsMentioned = (List<SecuredTaskBean>) value;
            }
        }
        List<SecuredTaskBean>  productsInvolved  = new ArrayList<SecuredTaskBean>();
        for (SecuredTaskBean t: productsMentioned){
            SecuredTaskBean p = t.getParent();
            while (p!=null && p.getCategoryId().equals(PRODUCT_CATEGORY_ID)){
                productsInvolved.add(p);
                p = p.getParent();
            }
            productsInvolved.add(t);
        }
        for (SecuredSearchTaskItem res: results){
            SecuredTaskBean t = res.getTask();
            if (t.getWorkflowId().equals(INCIDENT_WORKFLOW)){
                if (t.getStatus().isFinish()){


                    HashMap<String, SecuredUDFValueBean> map = t.getUDFValues();
                    if (map!=null && map.containsKey(INCIDENT_WORKAROND_UDFID)){
                        Object prod = map.get(INCIDENT_PRODUCT_UDFID).getValue();
                        boolean thatProduct = false;
                        if (prod!=null){
                            List<SecuredTaskBean> numbers = (ArrayList<SecuredTaskBean>)prod;
                            numbers.retainAll(productsInvolved);
                            if (!numbers.isEmpty()) thatProduct = true;
                        }
                     if (thatProduct){
                        Object val = map.get(INCIDENT_WORKAROND_UDFID).getValue();
                        if (val!=null){

                            List<SecuredTaskBean> numbers = (ArrayList<SecuredTaskBean>)val;
                            solutionsViaIncidents.addAll(numbers);
                            
                        }
                    }
                    }
                }
            } else if (t.getCategoryId().equals(WORKAROUND_CATEGORY_ID)){
                HashMap<String, SecuredUDFValueBean> map = t.getUDFValues();
                if (map!=null && map.containsKey(WORKAROUND_PRODUCT_UDFID)){
                        Object prod = map.get(WORKAROUND_PRODUCT_UDFID).getValue();
                        boolean thatProduct = false;
                        if (prod!=null){
                            List<SecuredTaskBean> numbers = (ArrayList<SecuredTaskBean>)prod;
                            numbers.retainAll(productsInvolved);
                            if (!numbers.isEmpty()) thatProduct = true;
                        }
                     if (thatProduct)
                            solutionsViaSimilar.add(t);
                    }
            }
        }
        SecuredTaskBean solutionRoot = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(securedTaskBean.getSecure(), WORKAROUND_ROOT_ID);
        for (SecuredTaskBean sol: solutionRoot.getChildren()){
            HashMap<String, SecuredUDFValueBean> map = sol.getUDFValues();
            Object value = map.get(WORKAROUND_FAQ_UDFID).getValue();
            if (value!=null)
            {
            String vv = value.toString();
            if (vv.equals(WORKAROUND_FAQ_YES)){

                 HashMap<String, SecuredUDFValueBean> map2 = sol.getUDFValues();
                if (map2!=null && map2.containsKey(WORKAROUND_PRODUCT_UDFID)){
                    SecuredUDFValueBean securedUDFValueBean = map2.get(WORKAROUND_PRODUCT_UDFID);
                    Object prod = securedUDFValueBean.getValue();
                        boolean thatProduct = false;
                        if (prod!=null){
                            List<SecuredTaskBean> numbers = (ArrayList<SecuredTaskBean>)prod;
                            numbers.retainAll(productsInvolved);
                            if (!numbers.isEmpty()) thatProduct = true;
                        }
                     if (thatProduct)
                            solutionsFAQ.add(sol);
                    }
            }
        }
        }
        StringBuffer buf = new StringBuffer();
        if (!solutionsViaIncidents.isEmpty() || !solutionsViaSimilar.isEmpty()){
        buf.append("<H1>");
        buf.append("Найденные решения");
        buf.append("</H1>");

        for (SecuredTaskBean task: solutionsViaIncidents){
            if (!task.getStatus().isFinish()){
            buf.append("<H3>#");
            buf.append(task.getNumber());
            buf.append("</H3>");
            buf.append("<H2>");
            buf.append(task.getName());
            buf.append("</H2>");
            buf.append("<div style=\"max-height: 200px; overflow-y: auto\">");
            buf.append(task.getDescription());
            buf.append("</div>");
            buf.append("<BR>");
            }
        }
        solutionsViaSimilar.removeAll(solutionsViaIncidents);
        for (SecuredTaskBean task: solutionsViaSimilar){
            if (!task.getStatus().isFinish()){
            buf.append("<H3>#");
            buf.append(task.getNumber());
            buf.append("</H3>");
            buf.append("<H2>");
            buf.append(task.getName());
            buf.append("</H2>");
           buf.append("<div style=\"max-height: 200px; overflow-y: auto\">");
            buf.append(task.getDescription());
            buf.append("</div>");
            buf.append("<BR>");
            }
        }
        }
        solutionsFAQ.removeAll(solutionsViaIncidents);
        solutionsFAQ.removeAll(solutionsViaSimilar);
        if (!solutionsFAQ.isEmpty()){
        buf.append("<H1>");
        buf.append("Частые ситуации");
        buf.append("</H1>");
        for (SecuredTaskBean task: solutionsFAQ){
            if (!task.getStatus().isFinish()){
            buf.append("<H3>#");
            buf.append(task.getNumber());
            buf.append("</H3>");
            buf.append("<H2>");
            buf.append(task.getName());
            buf.append("</H2>");
            buf.append("<div style=\"max-height: 200px; overflow-y: auto\">");
            buf.append(task.getDescription());
            buf.append("</div>");
            buf.append("<BR>");
            }
        }

        }
        return buf.toString();
    }

}
