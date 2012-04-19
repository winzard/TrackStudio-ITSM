package scripts.before_add_message.itsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import scripts.itsm.CommonITSM;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.securedkernel.SecuredStepAdapterManager;
import com.trackstudio.tools.EggBasket;

public class CloseProblemAsDuplicate extends CommonITSM implements OperationTrigger {
	/**
	 * Закрытие проблемы как дубля.<br>
	 * Дубли будут появляться потому, что при передаче любого инцидента на третью линию автоматически создается проблема (лучше перебдеть).
	 * Третья линия, которая рассматривает и расследует проблемы, должна обнаружить, что вот эта конкретная проблема является дублирующей.<br>
	 * Далее проблема связывается с дублирующей и закрывается. Вся остальная работа должна вестись с исходной проблемой.
	 * При этом:<br>
	 * <li>инциденты, привязанные к дублирующей проблеме, должны привязаться к исходной.</li>
	 * <li>инциденты от дублируемой проблемы никак с дублем не связываются, потому что реально это не ЕЩЕ ОДНА проблема с тем же инцидентом,
	 *  а наоборот, еще один инцидент к ранее зарегистрированной проблеме.</li>
	 *  Закрытие проблемы как дубля в любом случае должно отражаться на инцидентах, связанных с этой проблемой. А именно:
	 *  если для исходной проблемы указано обходное решение, оно должно быть привязано и к этим новым инцидентам,
	 *  а сами инциденты переведены в состояние "Предложено новое решение".
	 *  В этих инцидентах должна быть установлена связь со старой проблемой.
	 *  Ответственный за инцидент должен меняться на ответственного по исходному инциденту. Если отвественных несколько - непорядок.
	 * 
	 */
    @Override
	public SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) throws GranException {

        SecuredTaskBean problemToClose = message.getTask();
        SessionContext sc = message.getSecure();
        String text = message.getDescription();
        SecuredStepAdapterManager stepManager = AdapterManager.getInstance().getSecuredStepAdapterManager();
        SecuredUDFBean duplicateUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, PROBLEM_DUPLICATE_UDFID);
        SecuredUDFBean workaroundUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, INCIDENT_WORKAROND_UDFID);
        if (duplicateUdf == null) return message;

        String duplicate = message.getUdfValue(duplicateUdf.getCaption());
        String workarounds = "";
        if (duplicate != null && duplicate.length() != 0) {
        	   String targetHandler = null;
               String targetRoleHandler = null;
               SecuredStatusBean targetState = null;
            if (!duplicate.contains(";")){
                SecuredTaskBean dub = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(message.getSecure(), duplicate.trim());
                if (dub!=null){
                SecuredUDFValueBean udf = dub.getUDFValues().get(PROBLEM_WORKAROND_UDFID);
                if (udf!=null){
                    Object value = udf.getValue();
                List<SecuredTaskBean> workaroundsInvolved = null;
                if (value != null) {
                workaroundsInvolved = (List<SecuredTaskBean>) value;

                    for (SecuredTaskBean p: workaroundsInvolved){
                        workarounds+=";#"+p.getNumber();
                    }
                }

            }
             
                EggBasket<SecuredUDFValueBean, SecuredTaskBean> sourceReferences = AdapterManager.getInstance().getSecuredIndexAdapterManager().getReferencedTasksForTask(dub);
                for (Entry<SecuredUDFValueBean, List<SecuredTaskBean>> entry : sourceReferences.entrySet())
                    if (entry.getKey().getUdfId().equals(INCIDENT_RELATED_PROBLEM_UDFID)){
                List<SecuredTaskBean> sourceIncidents = entry.getValue();
                if (sourceIncidents!=null && !sourceIncidents.isEmpty()){
                	// берем первый
                	SecuredTaskBean source = sourceIncidents.get(0);
                	// состояние его считываем и смотрим, можем ли мы перевести инциденты от дубля в то же состояние. Ответственного тоже запоминаем
                	targetHandler = source.getHandlerUserId();
                	targetRoleHandler = source.getHandlerGroupId();
                	targetState = source.getStatus();
                }

                }
            }
                if (workarounds.length()>0) workarounds = workarounds.substring(1);
            EggBasket<SecuredUDFValueBean, SecuredTaskBean> refs = AdapterManager.getInstance().getSecuredIndexAdapterManager().getReferencedTasksForTask(problemToClose);
            SecuredUDFBean relatedProblemUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, INCIDENT_RELATED_PROBLEM_UDFID);

            if (refs != null && targetState!=null)
                for (Entry<SecuredUDFValueBean, List<SecuredTaskBean>> entry : refs.entrySet())
                    if (entry.getKey().getUdfId().equals(relatedProblemUdf.getId())){
                List<SecuredTaskBean> incidentsInvolved = entry.getValue();

                        if (incidentsInvolved != null)
                            for (SecuredTaskBean incidentToLink : incidentsInvolved)  {
                    			String prevValue = AdapterManager.getInstance().getSecuredUDFAdapterManager().getTaskUDFValue(sc, incidentToLink.getId(), relatedProblemUdf.getCaption());
                            	String appendedProblem = duplicate;
                            	if (prevValue!=null && !prevValue.isEmpty())
                            		appendedProblem	+="; "+prevValue;
                            	if (!workarounds.isEmpty()){
                            		SecuredStatusBean s = stepManager.getNextStatus(sc, incidentToLink.getId(), WORKAROUND_IN_INCIDENT_OPERATION);
                            		if (s!=null){
                            			HashMap<String, String> udfMap = new HashMap<String, String>();
                            			udfMap.put(workaroundUdf.getCaption(), workarounds); // связываем с имеющимися решениями
                            			udfMap.put(duplicateUdf.getCaption(), appendedProblem);
                            			TriggerManager.getInstance().createMessage(incidentToLink.getSecure(), incidentToLink.getId(), WORKAROUND_IN_INCIDENT_OPERATION, text, null, targetHandler, targetRoleHandler, null, incidentToLink.getPriorityId(), incidentToLink.getDeadline(), incidentToLink.getBudget(), udfMap!=null ? (HashMap)udfMap: null, true, null );
                            	
                            		}
                            	} 	
                            	
                            	// потому что нет прав на редактирование этого поля в операции
                            	AdapterManager.getInstance().getSecuredUDFAdapterManager().setTaskUDFValueSimple(sc, incidentToLink.getId(), relatedProblemUdf.getCaption(), appendedProblem);
                    }
                    }
            } else throw new UserException("Вы должны указать только одну проблему в качестве дублируемой", true);
        }
        return message;
    }


}
