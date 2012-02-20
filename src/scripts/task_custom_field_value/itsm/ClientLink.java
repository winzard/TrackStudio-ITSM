package scripts.task_custom_field_value.itsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import scripts.itsm.CommonITSM;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;

public class ClientLink extends CommonITSM implements TaskUDFValueScript {

	@Override
	public Object calculate(SecuredTaskBean task) throws GranException {
		HashMap<String, SecuredUDFValueBean> m = task.getUDFValues();
        SecuredUDFValueBean clientUDFValue = m.get(INCIDENT_CLIENT_UDFID);
        if (clientUDFValue!=null){
        Object value = clientUDFValue.getValue();
        if (value != null) {
        	SecuredUserBean clientUser = AdapterManager.getInstance().getSecuredUserAdapterManager().findByName(task.getSecure(), value.toString());
        	if (clientUser!=null) {
        		List<String> clients = new ArrayList<String>();
        		clients.add(clientUser.getLogin());
        		return clients;
        	}
        }
		
	}
		return null;
	}

}
