package scripts.bulk;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scripts.itsm.CommonITSM;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.exception.CantFindObjectException;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskBulkProcessor;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UDFCacheItem;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.kernel.manager.TSPropertyManager;
import com.trackstudio.model.Priority;
import com.trackstudio.model.Udf;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserAclBean;
import com.trackstudio.securedkernel.SecuredUDFAdapterManager;

public class Upgrade extends CommonITSM implements TaskBulkProcessor {
	private static Logger log = Logger.getLogger(Upgrade.class.getName());
	private static final String INITIATOR_ROLE = "000000002f9b2134012f9b59533500fa";
	
	private static final String CONFIG_ITEMS="4028818212b7e87b0112be28559c0606";
	private static final String WORKAROUND="ff8081812e6bb868012e6bcc372a010d";
	private static final String URGENCY="297eef002e045fd5012e0462c9370004";
	private static final String INC_TYPE="ff8081812e6bb868012e6c52ef830536";
	private static final String IMPACT="297eef002e045fd5012e046392f0004a";
	private static final String DEADLINE="ff8081812e762c70012e774f955a000b";
	private static final String FOUND_DES="ff8081812ebde5d9012ebea618c50059";
	private static final String LINKED_PRO="ff8081812f06d861012f06ff59560036";
	private static final String NEW_VERS="ff808181301c8a1301301cd28d83004b";
	
	
	@Override
	public SecuredTaskBean execute(SecuredTaskBean task) throws GranException {
		SessionContext sc = task.getSecure();
		if (checkVersion10()){
			log.log(Level.INFO, "Обновляем TrackStudio ITSM с 1.0 до 1.2");
		removeInitiatorRole(sc);
		removeNewUser(sc);
		modifyClientUDF(sc);
		intoduceClientUDF(sc);
		rearrangeUDF(sc);
		moveACL(sc);
		update11to12(sc);
		log.log(Level.INFO, "Закончили обновление. Теперь ваша конфигурация соответствует версии 1.2");
		} else if (checkVersion11()){
			log.log(Level.INFO, "Обновляем TrackStudio ITSM с 1.1 до 1.2");
			update11to12(sc);
			log.log(Level.INFO, "Закончили обновление. Теперь ваша конфигурация соответствует версии 1.2");
		}
		TSPropertyManager.getInstance().set("ITSM_VERSION", "1.2");
		return task;
	}

	 protected boolean checkVersion10() throws GranException{
		try{ 
			if (TSPropertyManager.getInstance().get("ITSM_VERSION")==null){
		 KernelManager.getFind().findPrstatus(INITIATOR_ROLE);
		 return true;
			} else return false;
		}
		 catch (CantFindObjectException ke){
			 return false;
		 }
	}
	 protected boolean checkVersion11() throws GranException{
		 try{
			KernelManager.getFind().findUdf(INCIDENT_CLIENTDATA_UDFID);
			return true;
	 }
	 catch (CantFindObjectException ke){
		 return false;
	 }
	}
	 private void update11to12(SessionContext sc) throws GranException {
			
			// check email udf
			List<UDFCacheItem> udfs = KernelManager.getWorkflow().getUDFs(INCIDENT_WORKFLOW);
			String incidentsRoot = KernelManager.getTask().findByNumber("50");
			String permissionUdfId = CSVImport.findUDFIdByName(INCIDENT_CLIENT_UDF);
			String introEmail = introduceField(sc, udfs, INCIDENT_EMAIL_UDF, 2, permissionUdfId);
			String introCompany = introduceField(sc, udfs, INCIDENT_COMPANY_UDF, 4, permissionUdfId);
			String introPhone = introduceField(sc, udfs, INCIDENT_PHONE_UDF, 3, permissionUdfId);
			TaskRelatedManager.getInstance().invalidateWhenChangeWorkflow();
			// если поля были созданы, копируем туда значения из поля INCIDENT_CLIENTDATA_UDFID
			if (introEmail!=null && introCompany!=null && introPhone!=null){
				// лезем по всем инцидентам и достаем строчку "электронная почта:"
				SecuredTaskBean tR = new SecuredTaskBean(incidentsRoot, sc);
				for (SecuredTaskBean t: tR.getChildren()){
					HashMap<String, SecuredUDFValueBean> v = t.getUDFValues();
					SecuredUDFValueBean bean = v.get(INCIDENT_CLIENTDATA_UDFID);
					if (bean!=null){
						Object value = bean.getValue();
						if (value!=null) {
							String clientData = value.toString();
							Pattern emailPat = Pattern.compile(EMAIL_PATTERN);
			                Pattern phonePat = Pattern.compile(PHONE_PATTERN);
			                Pattern companyPat = Pattern.compile(COMPANY_PATTERN);
			                Matcher emailMat = emailPat.matcher(clientData);
			                Matcher phoneMat = phonePat.matcher(clientData);
			                Matcher companyMat = companyPat.matcher(clientData);
			                String phone = "";
			                String company = "";
			                if (emailMat.find()) {
			                    while (phoneMat.find()) {
			                        phone += ", " + phoneMat.group(1);
			                    }
			                    if (phone.length() > 0) phone = phone.substring(2);
			                    if (companyMat.find()) {
			                        company = companyMat.group(1);
			                    }
			                    String userEmail = emailMat.group(3);
			                    AdapterManager.getInstance().getSecuredUDFAdapterManager().setTaskUdfValue(sc, introEmail, t.getId(), userEmail);
			                    AdapterManager.getInstance().getSecuredUDFAdapterManager().setTaskUdfValue(sc, introPhone, t.getId(), phone);
			                    AdapterManager.getInstance().getSecuredUDFAdapterManager().setTaskUdfValue(sc, introCompany, t.getId(), company);
			                }
						
					}
				}
				}
				log.log(Level.INFO, "Разместили значения из старого поля 'Контактные данные клиента' в новые");
				AdapterManager.getInstance().getSecuredUDFAdapterManager().deleteWorkflowUdf(sc, INCIDENT_CLIENTDATA_UDFID);
				log.log(Level.INFO, "Удалили поле '"+"Контактные данные клиента"+"'");
				copyAccessToClients(sc);
				addAbilityToEditIncidentType(sc);
				removeAbilityToEditIncidentType(sc);
				trimPriorities(sc);
			}
			
		}	 
	 private void trimPriorities(SessionContext sc) throws GranException{
		 List<Priority> list = KernelManager.getWorkflow().getPriorityList(INCIDENT_WORKFLOW);
		 for (Priority p: list){
			 KernelManager.getWorkflow().updatePriority(p.getId(), SafeString.createSafeString(p.getName().trim()), SafeString.createSafeString(p.getDescription()), p.getOrder(), p.isDefault());
		 }
		 log.log(Level.INFO, "Подрезали лишние пробелы в названиях приоритетов");
		}
	 
	 private void addAbilityToEditIncidentType(SessionContext sc) throws GranException{
		 KernelManager.getUdf().setMstatusUDFRule(INCIDENT_TYPE_UDFID, "ff8081812f90bce8012f90d1de8f0058", CategoryConstants.EDIT_ALL);	
		 KernelManager.getUdf().setMstatusUDFRule(INCIDENT_TYPE_UDFID, "ff8081812f90bce8012f90d1de8f0058", CategoryConstants.VIEW_ALL);
		 log.log(Level.INFO, "Добавили возможность редактировать тип инцидента в Заметке");
		}
	 
	 private void removeAbilityToEditIncidentType(SessionContext sc) throws GranException{
		 KernelManager.getUdf().resetUDFRule(INCIDENT_TYPE_UDFID, CLIENT_ROLE_ID);	
		 log.log(Level.INFO, "Удалили возможность для Клиента редактировать тип инцидента");
		}
	private void removeInitiatorRole(SessionContext sc) throws GranException{
		try{
		AdapterManager.getInstance().getSecuredPrstatusAdapterManager().deletePrstatus(sc, INITIATOR_ROLE);
		log.log(Level.INFO, "Удалили неиспользуемую роль Инициатор изменений с ID "+INITIATOR_ROLE);
		} catch (GranException ge){
			log.log(Level.WARNING, "Не удалось удалить роль "+INITIATOR_ROLE+". Видимо, она используется. Пусть и дальше используется.");
		}
		
	}
	private void removeNewUser(SessionContext sc) throws GranException{
		try{
			KernelManager.getFind().findUser("ff8080811295943d011295a30c240059");
			AdapterManager.getInstance().getSecuredUserAdapterManager().deleteUser(sc, "ff8080811295943d011295a30c240059");
			log.log(Level.INFO, "Удалили ненужного пользователя '* новый клиент'");
	 }
	 catch (CantFindObjectException ke){
		
	 }
		
	}
	private void modifyClientUDF(SessionContext sc) throws GranException{
		SecuredUDFAdapterManager suam = AdapterManager.getInstance().getSecuredUDFAdapterManager();
		suam.updateWorkflowUdf(sc, INCIDENT_CLIENTLINK_UDFID, INCIDENT_CLIENTLINK_UDF, "Инциденты клиента", 0, "", false, false, "itsm.ClientLink.class", null, false, false, null);
		log.log(Level.INFO, "Изменили тип поля Клиент и переименовали его в Ссылку на клиента");
	}
	private void createGroupACL(SessionContext sc, String taskId, String groupId, String prstatusId) throws GranException{
		String aclid = AdapterManager.getInstance().getSecuredAclAdapterManager().createAcl(sc, taskId, null,
                null, groupId);
         AdapterManager.getInstance().getSecuredAclAdapterManager().updateTaskAcl(sc, aclid, prstatusId, false);
         log.log(Level.INFO, "Добавили доступ для группы '" + groupId + "' с ролью '" + prstatusId + "' к задаче '" + taskId + "'");
	}
	
	private void createUserACL(SessionContext sc, String taskId, String userId, String prstatusId) throws GranException{
		String aclid = AdapterManager.getInstance().getSecuredAclAdapterManager().createAcl(sc, taskId, null,
                userId, null);
         AdapterManager.getInstance().getSecuredAclAdapterManager().updateTaskAcl(sc, aclid, prstatusId, false);
         log.log(Level.INFO, "Добавили доступ для пользователя '" + userId + "' с ролью '" + prstatusId + "' к задаче '" + taskId + "'");
	}
	private void moveACL(SessionContext sc) throws GranException {
		String incidentsRoot = KernelManager.getTask().findByNumber("50");
		// удаляем старые ACL из 32-й задачи
		try{
		AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, "ff8081812f8bae79012f8bb83129002c");
		} catch (CantFindObjectException e) {
			log.log(Level.WARNING, "Не удалось удалить ACL. Кто-то успел сделать это раньше. Ничего страшного.");
		}
		try{
		AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, "ff8081812f8bae79012f8bb7f894002b");
		} catch (CantFindObjectException e) {
			log.log(Level.WARNING, "Не удалось удалить ACL. Кто-то успел сделать это раньше. Ничего страшного.");
		}
		try{
		AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, "ff8081812e9fac85012e9fb55842009b");
		} catch (CantFindObjectException e) {
			log.log(Level.WARNING, "Не удалось удалить ACL. Кто-то успел сделать это раньше. Ничего страшного.");
		}
		try{
		AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, "ff8081812f8bae79012f8bb1f7c60004");
		} catch (CantFindObjectException e) {
			log.log(Level.WARNING, "Не удалось удалить ACL. Кто-то успел сделать это раньше. Ничего страшного.");
		}
		try{
		AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, "ff8081812ec8f533012ec94b481d007b");
		} catch (CantFindObjectException e) {
			log.log(Level.WARNING, "Не удалось удалить ACL. Кто-то успел сделать это раньше. Ничего страшного.");
		}
		try{
		AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, "ff8081812e8123b2012e812ff6860102");
		} catch (CantFindObjectException e) {
			log.log(Level.WARNING, "Не удалось удалить ACL. Кто-то успел сделать это раньше. Ничего страшного.");
		}
		try{
		AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, "ff8081812e8123b2012e812fadd30101");
		} catch (CantFindObjectException e) {
			log.log(Level.WARNING, "Не удалось удалить ACL. Кто-то успел сделать это раньше. Ничего страшного.");
		}
		try{
		AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, "ff8081812e8123b2012e812f78e20100");
		} catch (CantFindObjectException e) {
			log.log(Level.WARNING, "Не удалось удалить ACL. Кто-то успел сделать это раньше. Ничего страшного.");
		}
		try{
		AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, "ff8081812e8123b2012e8130dbc20109");
		} catch (CantFindObjectException e) {
			log.log(Level.WARNING, "Не удалось удалить ACL. Кто-то успел сделать это раньше. Ничего страшного.");
		}
		try{
		AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, "ff8081812e8123b2012e8130b0a50103");
		} catch (CantFindObjectException e) {
			log.log(Level.WARNING, "Не удалось удалить ACL. Кто-то успел сделать это раньше. Ничего страшного.");
		}
		try{
		AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, "ff8081812e8123b2012e8130db920106");
		} catch (CantFindObjectException e) {
			log.log(Level.WARNING, "Не удалось удалить ACL. Кто-то успел сделать это раньше. Ничего страшного.");
		}
		try{
		AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, "ff8081812e8123b2012e8130dbd3010a");
		} catch (CantFindObjectException e) {
			log.log(Level.WARNING, "Не удалось удалить ACL. Кто-то успел сделать это раньше. Ничего страшного.");
		}
		try{
		AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, "ff8081812e8123b2012e8130dbb70108");
		} catch (CantFindObjectException e) {
			log.log(Level.WARNING, "Не удалось удалить ACL. Кто-то успел сделать это раньше. Ничего страшного.");
		}
		try{
		AdapterManager.getInstance().getSecuredAclAdapterManager().deleteTaskAcl(sc, "ff8081812e8123b2012e8130dba60107");
		} catch (CantFindObjectException e) {
			log.log(Level.WARNING, "Не удалось удалить ACL. Кто-то успел сделать это раньше. Ничего страшного.");
		}
		
		// создаем новые ACL в 50-й для групп
		
		createGroupACL(sc, incidentsRoot, "ff8081812e80827a012e8110f1ca0100", SECOND_LINE_ROLE_ID);
		createGroupACL(sc, incidentsRoot, "ff8081812e5c7497012e5c8dc2eb03b6", FIRST_LINE_ROLE_ID);
		createGroupACL(sc, incidentsRoot, "ff8081812ea45053012ea46f2684005f", THIRD_LINE_ROLE_ID);
		createGroupACL(sc, incidentsRoot, THIRD_LINE_MANAGER_ROLE_ID, THIRD_LINE_MANAGER_ROLE_ID);
		createGroupACL(sc, incidentsRoot, SECOND_LINE_MANAGER_ROLE_ID, SECOND_LINE_MANAGER_ROLE_ID);
		createGroupACL(sc, incidentsRoot, FIRST_LINE_MANAGER_ROLE_ID, FIRST_LINE_MANAGER_ROLE_ID);
		createGroupACL(sc, incidentsRoot, FIRST_LINE_ROLE_ID, FIRST_LINE_ROLE_ID);
		createGroupACL(sc, incidentsRoot, SECOND_LINE_ROLE_ID, SECOND_LINE_ROLE_ID);
		createGroupACL(sc, incidentsRoot, THIRD_LINE_ROLE_ID, THIRD_LINE_ROLE_ID);
		createGroupACL(sc, incidentsRoot, "402881821204446701124cffdf95036d", "402881821204446701124cffdf95036d"); // Клиент-Клиент
		
		// создаем новые ACL в 50-й для пользователей
		//createUserACL(sc, incidentsRoot, "ff8081812f8bae79012f8bb4a1e30028", SECOND_LINE_MANAGER_ROLE_ID);
		createUserACL(sc, incidentsRoot, "ff8081812e9fac85012e9fb4521d0099", FIRST_LINE_ROLE_ID);
		createUserACL(sc, incidentsRoot, "ff8081812e9fac85012e9fb4521d0099", SECOND_LINE_ROLE_ID);
		createUserACL(sc, incidentsRoot, "ff8081812e9fac85012e9fb4521d0099", THIRD_LINE_ROLE_ID);
		
		log.log(Level.INFO, "Обновили правила доступа к задачам.");
		
	}
	
	private void updateUDFOrder(SessionContext sc, String id, int order) throws GranException{
		SecuredUDFAdapterManager suam = AdapterManager.getInstance().getSecuredUDFAdapterManager();
		SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, id);
		if (udf!=null){
		suam.updateWorkflowUdf(sc, udf.getId(), udf.getCaption(), udf.getReferencedbycaption(), order, udf.getDefaultUDF(), udf.isRequired(), udf.isHtmlview(), udf.getScript(), udf.getLookupscript(), udf.isLookuponly(), udf.isCachevalues(), udf.getInitial());
		log.log(Level.INFO, "Установили для поля '"+ udf.getCaption() + "' вес "+order);
		}
	}
private void rearrangeUDF(SessionContext sc) throws GranException {
		
	updateUDFOrder(sc, CONFIG_ITEMS, 5);
	updateUDFOrder(sc,  WORKAROUND, 45);
	updateUDFOrder(sc,  URGENCY, 10);
	updateUDFOrder(sc,  INC_TYPE, 50);
	updateUDFOrder(sc,  IMPACT, 20);
	updateUDFOrder(sc,  DEADLINE, 30);
	updateUDFOrder(sc,  FOUND_DES, 40);
	updateUDFOrder(sc,  LINKED_PRO, 60);
	updateUDFOrder(sc,  NEW_VERS, 70);
	

		TaskRelatedManager.getInstance().invalidateWhenChangeWorkflow();
		log.log(Level.INFO, "Упорядочили дополнительные поля на форме");
		
	

	}
	
	private String intoduceClientUDF(SessionContext sc) throws GranException {
		
		SecuredUDFAdapterManager suam = AdapterManager.getInstance().getSecuredUDFAdapterManager();
		String newUDFId = suam.createWorkflowUdf(sc, INCIDENT_WORKFLOW, INCIDENT_CLIENT_UDF, null, 1, null, null, true, false, Udf.STRING, null, "itsm.GetClientsList.class", false, true, null);
		setPermissionUDF(sc, newUDFId, INCIDENT_CLIENTLINK_UDFID);
		TaskRelatedManager.getInstance().invalidateWhenChangeWorkflow();
		log.log(Level.INFO, "Добавили новое поле Клиент с выбором из списка");
		return newUDFId;
	

	}

	private String introduceField(SessionContext sc, List<UDFCacheItem> udfs,
			String caption, int order, String permissionsUdfId) throws GranException {
		boolean udfExists = false;
		for (UDFCacheItem u: udfs){
			if (u.getCaption().equals(caption)){
				udfExists = true;
				break;
			}
		}
		
		if (!udfExists){
		SecuredUDFAdapterManager suam = AdapterManager.getInstance().getSecuredUDFAdapterManager();
		
		String newUDFId = suam.createWorkflowUdf(sc, INCIDENT_WORKFLOW, caption, null, order, null, null, false, false, Udf.STRING, null, null, false, false, null);
		
		// скопировать права с поля Клиент
        
		setPermissionUDF(sc, newUDFId, permissionsUdfId);
        log.log(Level.INFO, "Создали дополнительное поле '"+caption + "'");
		return newUDFId;
	}
	return null;
	}
	private static final String MANAGER = "297eef002e0058c6012e00802dcc0997";
	private static final String SPECIALIST = "ff8081812e80827a012e8110f1ca0100";
	private static final String ENGINEER = "ff8081812ea45053012ea46f2684005f";
	
		private void copyAccessToClients(SessionContext sc) throws GranException {
        SecuredUserAclBean sourceAcl = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserAclById(sc, "ff8081812e6bb868012e6c67183705e9");
        AdapterManager.getInstance().getSecuredAclAdapterManager().updateUserAcl(sc, sourceAcl.getId(), sourceAcl.getPrstatusId(), false);
        // check for existing rules
        
            if (sourceAcl!=null && sourceAcl.canManage()){
                String aclid = AdapterManager.getInstance().getSecuredAclAdapterManager().createAcl(sc, null, sourceAcl.getToUserId(),
                        null, MANAGER);
                 AdapterManager.getInstance().getSecuredAclAdapterManager().updateUserAcl(sc, aclid, sourceAcl.getPrstatusId(), false);
                 aclid = AdapterManager.getInstance().getSecuredAclAdapterManager().createAcl(sc, null, sourceAcl.getToUserId(),
                         null, SPECIALIST);
                  AdapterManager.getInstance().getSecuredAclAdapterManager().updateUserAcl(sc, aclid, sourceAcl.getPrstatusId(), false);
                  aclid = AdapterManager.getInstance().getSecuredAclAdapterManager().createAcl(sc, null, sourceAcl.getToUserId(),
                          null, ENGINEER);
                   AdapterManager.getInstance().getSecuredAclAdapterManager().updateUserAcl(sc, aclid, sourceAcl.getPrstatusId(), false);
                   log.log(Level.INFO, "Скопировали права доступа к клиентам для менеджеров, специалистов и инженеров");
    }
	}
}
