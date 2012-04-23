package scripts.before_create_task.itsm;

import java.util.ArrayList;
import java.util.List;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.Null;

import scripts.itsm.CommonITSM;

public class IntroduceClient  extends CommonITSM implements TaskTrigger{
public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        
        //String clientDataUDFName = KernelManager.getFind().findUdf(INCIDENT_CLIENTDATA_UDFID).getCaption();
        String client = task.getUdfValue(INCIDENT_CLIENT_UDF);
        introduceNewClient(task, client);


        SecuredUserBean clientUser = task.getSecure().getUser();
        if (client!=null && client.length()>0) 
        	clientUser = AdapterManager.getInstance().getSecuredUserAdapterManager().findByName(task.getSecure(), client);
        else {
            task.setUdfValue(INCIDENT_CLIENT_UDF, clientUser.getName());
            task.setUdfValue(INCIDENT_EMAIL_UDF, clientUser.getEmail());
    		task.setUdfValue(INCIDENT_PHONE_UDF, clientUser.getTel());
    		task.setUdfValue(INCIDENT_COMPANY_UDF, clientUser.getCompany());
        }
        
        if (task.getHandlerUserId()==null){
            // set Assignee

                ArrayList statuses = KernelManager.getAcl().getEffectiveStatuses(task.getId(), task.getSubmitterId());
                    if (statuses.contains(FIRST_LINE_ROLE_ID) && !statuses.contains(ESCALATOR_BOT_ROLE)) {
                        task.setHandlerGroupId(null);
                        task.setHandlerUserId(task.getSubmitterId());
                    } else if (statuses.contains(CLIENT_ROLE_ID)){
                        task.setHandlerGroupId(FIRST_LINE_ROLE_ID);
                    }
        }
        return task;
    }

protected void introduceNewClient(SecuredTaskTriggerBean task, String client) throws GranException {
    if (client != null && client.length()>0) {
    	SecuredUserBean clientUser = AdapterManager.getInstance().getSecuredUserAdapterManager().findByName(task.getSecure(), client);
    	if (clientUser==null){
        String clientEmail = task.getUdfValue(INCIDENT_EMAIL_UDF);
        String clientPhone = task.getUdfValue(INCIDENT_PHONE_UDF);
        String clientCompany = task.getUdfValue(INCIDENT_COMPANY_UDF);
        if (clientEmail != null && clientEmail.length() > 0) {

                
                String fId = KernelManager.getUser().findUserIdByEmailNameProject(clientEmail, clientEmail, task.getParentId());
                if (fId == null) {
                    String id = AdapterManager.getInstance().getSecuredUserAdapterManager().createUser(task.getSecure(),
                            CLIENT_ROOT_ID, clientEmail, client, Null.beNull(CLIENT_ROLE_ID));
                    AdapterManager.getInstance().getSecuredUserAdapterManager().updateUser(task.getSecure(), id, clientEmail, client, clientPhone, clientEmail,
                            CLIENT_ROLE_ID, CLIENT_ROOT_ID, task.getSecure().getUser().getTimezone(), task.getSecure().getUser().getLocale(), clientCompany, null, null, null, null, true);
                    try {
                        String pwd = "";
                          for (int i = 0; i < 7; ++i) {
                    if ((int) (Math.random() * 26) % 2 == 0)
                        pwd += (char) ((int) 'a' + ((int) (Math.random() * 26)));
                    else
                        pwd += (char) ((int) '0' + ((int) (Math.random() * 10)));
                }

                        AdapterManager.getInstance().getAuthAdapterManager().changePassword(id, pwd);
                        KernelManager.getRegistration().sendRegisterMessage(id, pwd);
                    } catch (Exception e) {
                        e.printStackTrace();  
                    }
                    SecuredUserBean clientRoot = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(task.getSecure(), CLIENT_ROOT_ID);
                    if (clientRoot != null) {
                        List<SecuredUDFValueBean> udfvalues = clientRoot.getUDFValuesList();
                        for (SecuredUDFValueBean udf : udfvalues) {
                            Object value = udf.getValue();
                            if (value != null)
                                AdapterManager.getInstance().getSecuredUDFAdapterManager().setUserUdfValue(task.getSecure(), udf.getUdfId(), id, value.toString());
                        }
                    }

                } else throw new UserException("Пользователь с указанным email уже существует. Выберите его из списка или укажите другой email.", true);
        } else throw new UserException("Вы должны указать email для нового пользователя", true);
    }
    	else {
    		task.setUdfValue(INCIDENT_EMAIL_UDF, clientUser.getEmail());
    		task.setUdfValue(INCIDENT_PHONE_UDF, clientUser.getTel());
    		task.setUdfValue(INCIDENT_COMPANY_UDF, clientUser.getCompany());
    	}
    }
    
}
}
