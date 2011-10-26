package scripts.before_create_task.itsm;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.RegistrationManager;
import com.trackstudio.secured.*;
import com.trackstudio.tools.Null;
import scripts.itsm.CommonITSM;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * электронная почта: max.vasenkov@gmail.com, m@vasenkov.gmail.com
 * телефон: 84993434, 8-960-555-55-55, 8 960 555 55 55, +7 (960) 55 55 55, +7-960-555-55-55
 * имя:
 * компания:
 */
public class NewIncidentProcessing extends CommonITSM implements TaskTrigger {


    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
        String clientUDFName = KernelManager.getFind().findUdf(INCIDENT_CLIENT_UDFID).getCaption();
        String clientDataUDFName = KernelManager.getFind().findUdf(INCIDENT_CLIENTDATA_UDFID).getCaption();
        String client = task.getUdfValue(clientUDFName);
        client = introduceNewClient(task, clientUDFName, clientDataUDFName, client);
        String usedPriority = setPriority(task);
        // now we set first Deadline according with SLA in User custom fields

        SecuredUserBean clientUser = task.getSecure().getUser();
        if (client!=null && client.length()>0) clientUser = AdapterManager.getInstance().getSecuredUserAdapterManager().findByLogin(task.getSecure(), client.substring(1));
        else {
            task.setUdfValue(clientUDFName, "@"+clientUser.getLogin());
        }
        
            StringBuffer bf = new StringBuffer();
            bf.append("электронная почта: ").append(clientUser.getEmail()).append("\r\n");
            bf.append("телефон: ").append(clientUser.getTel()).append("\r\n");
            bf.append("имя: ").append(clientUser.getName()).append("\r\n");
            bf.append("компания: ").append(clientUser.getCompany()).append("\r\n");


            List<SecuredUDFValueBean> udfvalues = clientUser.getUDFValuesList();
            SecuredUDFBean deadlineUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(task.getSecure(), INCIDENT_DEADLINE_UDFID);
            for (SecuredUDFValueBean udf : udfvalues) {
                if (udf.getCaption().equals("SLA - " + usedPriority)) {
                    // use this one for parameters
                    Object o = udf.getValue();
                    if (o != null) {
                        int hoursAdvance1 = 0;
                        String sla = o.toString();
                        String slaPattern = "([0-9]*)\\s?(\\(\\s?([0-9]*)\\s?\\))?";
                        Pattern slaPat = Pattern.compile(slaPattern);
                        Matcher patternMat = slaPat.matcher(sla);
                        if (patternMat.find()) {
                            String longTerm = patternMat.group(1);
                            String shortTerm = patternMat.group(3);
                            if (longTerm != null && longTerm.length() > 0) {
                                hoursAdvance1 = Integer.parseInt(longTerm);
                                Calendar longCal = Calendar.getInstance();
                                longCal.add(Calendar.HOUR, hoursAdvance1);
                                task.setDeadline(longCal);
                                String deadline = task.getSecure().getUser().getDateFormatter().parse(longCal);
                                bf.append("срок завершения: ").append(deadline).append("\r\n");
                                task.setUdfValue(deadlineUdf.getCaption(), deadline);
                            }
                            if (shortTerm != null && shortTerm.length() > 0) {
                                hoursAdvance1 = Integer.parseInt(shortTerm);
                                Calendar shortCal = Calendar.getInstance();
                                shortCal.add(Calendar.HOUR, hoursAdvance1);
                                task.setDeadline(shortCal);
                                bf.append("срок реакции: ").append(task.getSecure().getUser().getDateFormatter().parse(shortCal)).append("\r\n");
                            }

                        }
                    }

                    break;
                }
            }

            task.setUdfValue(clientDataUDFName, bf.toString());

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

    protected int parseInt(String s) {
        Pattern pricePattern = Pattern.compile("\\D*(\\d+)\\D*");
        Matcher matcher = pricePattern.matcher(s);
        if (matcher.find()) {
            String priceCapture = matcher.group(1);
            return Integer.parseInt(priceCapture);

        }
        return 0;
    }

    protected String setPriority(SecuredTaskTriggerBean task) throws GranException {
        String impactUDFName = KernelManager.getFind().findUdf(INCIDENT_IMPACT_UDFID).getCaption();
        String urgencyUDFName = KernelManager.getFind().findUdf(INCIDENT_URGENCY_UDFID).getCaption();
        String urgency = task.getUdfValue(urgencyUDFName);
        String impact = task.getUdfValue(impactUDFName);
        int priority = 1;

        try {
            int ur = parseInt(urgency);
            int im = parseInt(impact);
            priority = ur * im;
            if (priority == 4) priority = 3;
        } catch (NumberFormatException e) {

        }
        catch (NullPointerException ne){
            
        }

        List<SecuredPriorityBean> priorities = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getPriorityList(task.getSecure(), INCIDENT_WORKFLOW);
        String usedPriority = task.getPriorityId()!=null ? task.getPriority().getName() : "";
        for (SecuredPriorityBean p : priorities) {
            if (p.getOrder() == priority) {
                task.setPriorityId(p.getId());
                usedPriority = p.getName();
                break;
            }
        }
        return usedPriority;
    }

    protected String introduceNewClient(SecuredTaskTriggerBean task, String clientUDFName, String clientDataUDFName, String client) throws GranException {
        if (client != null && client.startsWith("@*")) {
            String clientData = task.getUdfValue(clientDataUDFName);
            if (clientData != null && clientData.length() > 0) {
                String emailPattern = "электронная почта:\\s*\\\"?(\\S+\\s*\\S+[^\\\"])?\\\"?\\s+(<|&lt;)?(([-A-Za-z0-9!#$%&'*+/=?^_`{|}~]+(\\.[-A-Za-z0-9!#$%&'*+/=?^_`{|}~]+)*)@([A-Za-z0-9.]+))(&gt;|>)?\\r\\n";
                String phonePattern = "телефон:\\s*([0-9\\+\\s\\-\\(\\)]+)+\\r\\n";
                String namePattern = "имя:\\s*(\\S+[\\s\\S]*?)\\r\\n";
                String companyPattern = "компания:\\s*(\\S+[\\s\\S]*?)\\r\\n";

                // From: max.vasenkov@gmail.com
                // From: Maxim Vasenkov <max.vasenkov@gmail.com>
                // From: Maxim Vasenkov &lt;max.vasenkov@gmail.com&gt;
                // From: Winzard <i@winzard.ru>
                // From: Admin <admin@localhost>
                // электронная почта: Denis.Ardan@localhost
                // From: "Максим Васенков" <vasenkov@any.place.com>
                // From: "Максим Васенков" &lt;vasenkov@any.place.com&gt;
                Pattern emailPat = Pattern.compile(emailPattern);
                Pattern phonePat = Pattern.compile(phonePattern);
                Pattern namePat = Pattern.compile(namePattern);
                Pattern companyPat = Pattern.compile(companyPattern);
                Matcher emailMat = emailPat.matcher(clientData);
                Matcher phoneMat = phonePat.matcher(clientData);
                Matcher nameMat = namePat.matcher(clientData);
                Matcher companyMat = companyPat.matcher(clientData);
                String phone = "";
                String name = "";
                String company = "";
                if (emailMat.find()) {
                    while (phoneMat.find()) {
                        phone += ", " + phoneMat.group(1);
                    }
                    if (phone.length() > 0) phone = phone.substring(2);
                    if (nameMat.find()) {
                        name = nameMat.group(1);
                    }
                    if (companyMat.find()) {
                        company = companyMat.group(1);
                    }

                    String userName = emailMat.group(1);
                    String userEmail = emailMat.group(3);
                    if (userName == null) userName = emailMat.group(4);
                    String fId = KernelManager.getUser().findUserIdByEmailNameProject(userEmail, userEmail, task.getParentId());
                    if (fId == null) {
                        String id = AdapterManager.getInstance().getSecuredUserAdapterManager().createUser(task.getSecure(),
                                CLIENT_ROOT_ID, userEmail, name.length() == 0 ? userName : name, Null.beNull(CLIENT_ROLE_ID));
                        AdapterManager.getInstance().getSecuredUserAdapterManager().updateUser(task.getSecure(), id, userEmail, name.length() == 0 ? userName : name, phone, userEmail,
                                CLIENT_ROLE_ID, CLIENT_ROOT_ID, task.getSecure().getUser().getTimezone(), task.getSecure().getUser().getLocale(), company, null, null, null, null, true);
                        try {
                            String pwd = "";
                              for (int i = 0; i < 7; ++i) {
                        if ((int) (Math.random() * 26) % 2 == 0)
                            pwd += (char) ((int) 'a' + ((int) (Math.random() * 26)));
                        else
                            pwd += (char) ((int) '0' + ((int) (Math.random() * 10)));
                    }

                            AdapterManager.getInstance().getAuthAdapterManager().changePassword(id, pwd);
                            RegistrationManager.sendRegistrationInfo(UserRelatedManager.getInstance().find(id), pwd);
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

                        task.setUdfValue(clientUDFName, "@" + userEmail);
                        return "@"+userEmail;
                    }
                }

            }
        }
        return task.getUdfValue(clientUDFName);
    }
}