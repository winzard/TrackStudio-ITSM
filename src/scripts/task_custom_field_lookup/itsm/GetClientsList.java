package scripts.task_custom_field_lookup.itsm;

import java.util.ArrayList;
import java.util.List;

import scripts.itsm.CommonITSM;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskUDFLookupScript;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;

public class GetClientsList extends CommonITSM implements TaskUDFLookupScript {

	public Object calculate(SecuredTaskBean task) throws GranException {
		List<String> list = new ArrayList<String>();

		for (SecuredUserBean u : AdapterManager.getInstance()
				.getSecuredUserAdapterManager()
				.getUserAndChildrenList(task.getSecure(), CLIENT_ROOT_ID)) {

			if (CLIENT_ROLE_ID.equals(u.getPrstatus().getId())) {
				list.add(u.getName());
			}
		}
		return list;
	}

}
