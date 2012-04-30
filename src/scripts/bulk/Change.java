package scripts.bulk;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import scripts.itsm.CommonITSM;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskBulkProcessor;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUserUDFBean;
import com.trackstudio.securedkernel.SecuredPrstatusAdapterManager;

public class Change extends CommonITSM implements TaskBulkProcessor {
	protected static final String GENERAL_USER = "4028802833a09ed20133a2117fb10414";
	protected static final String OBSERVER = "ff8081812e5c7497012e5cd72e4c05f5";
	private static Logger log = Logger.getLogger(Upgrade.class.getName());
	
	
	
	@Override
	public SecuredTaskBean execute(SecuredTaskBean task) throws GranException {
		SessionContext sc = task.getSecure();
		
		moveSLAtoCI(sc);
		addACLtoUsers(sc);
		addACLtoClients(sc);
		allowCreateUsers(sc);
		log.log(Level.INFO, "Закончили обновление");
		return task;
	}
	
	private void allowCreateUsers(SessionContext sc)
			throws GranException {
	 SecuredPrstatusAdapterManager pam = AdapterManager.getInstance().getSecuredPrstatusAdapterManager();
	 
	 List<String> allowed = new ArrayList<String>();
	 List<String> denied = new ArrayList<String>();
	 
	 allowed.add(Action.editUserHimself.toString());
	 allowed.add(Action.editUserChildren.toString());
	 allowed.add(Action.createUser.toString());
	 
	 
	 pam.setRoles(sc, OBSERVER, allowed, denied);

         }
	
	private void addACLtoUsers(SessionContext sc) throws GranException{
		String aclid = AdapterManager.getInstance().getSecuredAclAdapterManager().createAcl(sc, null, "8a80808a3413e7f50134140255f20005",
                null, GENERAL_USER);
         AdapterManager.getInstance().getSecuredAclAdapterManager().updateUserAcl(sc, aclid, GENERAL_USER, false);
	}
	private void addACLtoClients(SessionContext sc) throws GranException{
		String aclid = AdapterManager.getInstance().getSecuredAclAdapterManager().createAcl(sc, null, CLIENT_ROOT_ID,
                null, GENERAL_USER);
         AdapterManager.getInstance().getSecuredAclAdapterManager().updateUserAcl(sc, aclid, OBSERVER, false);
	}
	private void setPermissionUDF(SessionContext sc, String newUDFId)
			throws GranException {
		
		String incidentsRoot = PRODUCT_ROOT_ID;
		Set<SecuredPrstatusBean> prstatusSet = new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()));
		for (SecuredPrstatusBean spb : prstatusSet) {
            if (spb.isAllowedByACL() || spb.getUser().getSecure().allowedByACL(incidentsRoot)) {
                List<String> types = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, spb.getId(), PRODUCT_CATEGORY_UDFID);
                KernelManager.getUdf().resetUDFRule(newUDFId, spb.getId());
        		for (String type: types)
        		KernelManager.getUdf().setUDFRule(newUDFId, spb.getId(), type);
            }
            }

		
		// для mstatus нужно отдельно доставать
		List<String> editableIds = KernelManager.getUdf().getOperationsWhereUDFIsEditable(PRODUCT_CATEGORY_UDFID);
        List<String> viewableIds = KernelManager.getUdf().getOperationsWhereUDFIsViewable(PRODUCT_CATEGORY_UDFID);
        
		for (String mstatusId: editableIds)
			KernelManager.getUdf().setMstatusUDFRule(newUDFId, mstatusId, CategoryConstants.EDIT_ALL);	
		
		for (String mstatusId: viewableIds)
			KernelManager.getUdf().setMstatusUDFRule(newUDFId, mstatusId, CategoryConstants.VIEW_ALL);
	}
	 private void moveSLAtoCI(SessionContext sc) throws GranException {
			
			// check email udf
		 List<SecuredUserUDFBean> list = AdapterManager.getInstance().getSecuredUDFAdapterManager().getAvailableUserUdfList(sc, CLIENT_ROOT_ID);
		 SecuredCategoryBean cat = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, PRODUCT_CATEGORY_ID);
		 for (SecuredUDFBean udf: list){
			 if (udf.getCaption().startsWith("SLA -")){
			 String udfId = AdapterManager.getInstance().getSecuredUDFAdapterManager().createWorkflowUdf(sc, cat.getWorkflowId(), udf.getCaption(), udf.getReferencedbycaption(), udf.getOrder(), udf.getDefaultUDF(), null, udf.isRequired(), udf.isHtmlview(), udf.getType(), null, null, false, false, udf.getInitial());
			 setPermissionUDF(sc, udfId);
			 }
		 }
			
			
		}	 
	 }
