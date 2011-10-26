<#assign ContentType="text/plain;\n charset=\"${charset}\""/>
<#assign Subject="<${task.projectAlias} | ${reasonForSubject} | #${task.number}>: ${task.name}"/>
<#if serverEmail?exists>
    <#assign FromEmail="${serverEmail}"/>
</#if>
<#if fromUserName?exists>
    <#assign FromUser="${fromUserName}"/>
</#if>
<#assign Headers={"X-Meta":"data"}/>
<#if reason.code == "N" || reason.code == "NA">
<@table columns=2 positions="0,40,160">
[!]::<#if ((reason.task.getHandlerUserId()?exists || reason.task.getHandlerGroupId()?exists))><#assign addTaskFor><#if reason.task.handlerGroup?exists>${reason.task.handlerGroup.name}<#else>${reason.task.handlerUser.name}</#if></#assign><@std.I18n key="CHANGE_ADD_TASK_FOR" value=[reason.by.name, reason.task.category.name, addTaskFor]/><#else><@std.I18n key="CHANGE_ADD_TASK" value=[reason.by.name, reason.task.category.name]/></#if>
    <#if reason.code == "NA">
        <#list reason.attachments as attachment>
            <#assign attlink>
            ${attachment.name} (<#assign atSize = attachment.size/><#if atSize< 1024>&lt;1 kB<#else>${(atSize/1024)?int} kB</#if>) ${link}/download/task/${attachment.task.number}/${attachment.id}/${attachment.name}
            </#assign>
        [!]::<@std.I18n key="CHANGE_ADD_ATTACHMENT" value=[reason.by.name, attlink]/>
        </#list>
    </#if>
</@table>
</#if>
<#if reason.code == "S">
<@table columns=2 positions="0,40,160">
[!]::<@std.I18n key="SUBSCRIPTION_REASON" value=[reason.by.name, DateFormatter.parse(reason.when)]/>
</@table>
</#if>
<#if reason.code == "U">
<@table columns=2 positions="0,40,160">
[!]::<@std.I18n key="CHANGE_UPDATE_TASK" value=[reason.by.name, reason.task.category.name]/>
</@table>
</#if>
<#if reason.code == "A">
<@table columns=2 positions="0,40,160">
    <#list reason.attachments as attachment>
        <#assign attlink>
        ${attachment.name} (<#assign atSize = attachment.size/><#if atSize< 1024>&lt;1 kB<#else>${(atSize/1024)?int} kB</#if>) ${link}/download/task/${attachment.task.number}/${attachment.id}/${attachment.name}
        </#assign>
    [!]::<@std.I18n key="CHANGE_ADD_ATTACHMENT" value=[reason.by.name, attlink]/>
    </#list>
</@table>
</#if>
<#if reason.code == "M" || reason.code == "MA">
<@table columns=2 positions="0,40,160">
[!]::<#assign addMsgType><#if reason.message.resolution?exists>${reason.message.mstatus.name} (${reason.message.resolution.name})<#else>${reason.message.mstatus.name}</#if></#assign><#if ((reason.message.getHandlerUserId()?exists || reason.message.getHandlerGroupId()?exists))><#assign addMsgFor><#if reason.message.handlerGroup?exists>${reason.message.handlerGroup.name}<#else>${reason.message.handlerUser.name}</#if></#assign><@std.I18n key="CHANGE_ADD_MESSAGE_FOR" value=[reason.by.name, addMsgType, addMsgFor]/><#else><@std.I18n key="CHANGE_ADD_MESSAGE" value=[reason.by.name, addMsgType]/></#if>
    <#if reason.code == "MA">
        <#list reason.attachments as attachment>
            <#assign attlink>
            ${attachment.name} (<#assign atSize = attachment.size/><#if atSize< 1024>&lt;1 kB<#else>${(atSize/1024)?int} kB</#if>) ${link}/download/task/${attachment.task.number}/${attachment.id}/${attachment.name}
            </#assign>
        [!]::<@std.I18n key="CHANGE_ADD_ATTACHMENT" value=[reason.by.name, attlink]/>
        </#list>
    </#if>
>::${reason.message.description}
    <#if reason.message.priorityId?exists || reason.message.deadline?exists || reason.message.budgetAsString != "">

        <#if reason.message.priorityId?exists>
        <@std.I18n key="MESSAGE_PRIORITY"/> :: ${reason.message.priority.name}
        </#if>
        <#if reason.message.deadline?exists>
        <@std.I18n key="MESSAGE_DEADLINE"/> :: ${DateFormatter.parse(reason.message.deadline)}
        </#if>

        <#if reason.message.budgetAsString != "">
        <@std.I18n key="BUDGET"/> :: ${reason.message.budgetAsString}
        </#if>

        <#if reason.message.actualBudgetAsString != "">
        <@std.I18n key="MESSAGE_ABUDGET"/> :: ${reason.message.actualBudgetAsString}
        </#if>
    </#if>
</@table>
</#if>
<#if reason.code=="T">
<@table columns=2 positions="0,40,160">
[!]::<@std.I18n key="CHANGE_TEST" value=[reason.notification.name, reason.by.name, DateFormatter.parse(reason.when)]/>
</@table>
</#if>
${link}/task/${task.number}?thisframe=true
====================================================================================================
${task.name} [#${task.number}]
====================================================================================================
<#if task.textDescription?exists>
${task.textDescription}
====================================================================================================
</#if>
<@table columns=2 positions="0,40,160">
<@std.I18n key="FULL_PATH"/>::    <#if task.parent?exists><@std.path to=task.parent.number ; number, name>/ ${name}</@std.path></#if>
    <#if (task.shortname?exists)>
    <@std.I18n key="ALIAS"/>::        ${task.shortname}
    </#if>
    <#if (task.category?exists)>
    <@std.I18n key="CATEGORY"/>::     ${task.category.name}
    </#if>
    <#if (task.status?exists) >
    <@std.I18n key="TASK_STATE"/>::  ${task.status.name}
    </#if>
    <#if (task.resolution?exists)>
    <@std.I18n key="RESOLUTION"/>::   ${task.resolution.name}
    </#if>
    <#if (task.priority?exists)>
    <@std.I18n key="PRIORITY"/>::     ${task.priority.name}
    </#if>
    <#if (task.submitter?exists)>
    <@std.I18n key="SUBMITTER"/>::    ${task.submitter.name}
    </#if>
    <#if task.handlerUserId?exists || task.handlerGroupId?exists>
    <@std.I18n key="HANDLER"/>::      ${task.handler.name}
    </#if>
    <#if (task.submitdate?exists)>
    <@std.I18n key="SUBMIT_DATE"/>::  ${DateFormatter.parse(task.submitdate)}
    </#if>
    <#if (task.updatedate?exists)>
    <@std.I18n key="UPDATE_DATE"/>::  ${DateFormatter.parse(task.updatedate)}
    </#if>
    <#if (task.closedate?exists)>
    <@std.I18n key="CLOSE_DATE"/>::   ${DateFormatter.parse(task.closedate)}
    </#if>
    <#if (task.deadline?exists)>
    <@std.I18n key="DEADLINE"/>::     ${DateFormatter.parse(task.deadline)}
    </#if>
    <#if (task.budget?exists && (task.budget>0))>
    <@std.I18n key="BUDGET"/>::       ${task.budgetAsString}
    </#if>
    <#if (task.actualBudget?exists && (task.actualBudget>0))>
    <@std.I18n key="ABUDGET"/>::      ${task.actualBudgetAsString}
    </#if>
    <#if (viewUdfList?exists)>
        <#list viewUdfList as udf>
            <#if udf?exists>
                <#assign uValue = udf.value?default("")>
                <#if uValue?exists>
                    <#if udf.type == 'date'>
                    ${udf.caption} ::          <#if uValue!="">${DateFormatter.parse(uValue)}</#if>
                    </#if>
                    <#if udf.type == 'float'>
                    ${udf.caption} ::                         ${uValue}
                    </#if>
                    <#if udf.type == 'string'>
                    ${udf.caption} ::                         ${uValue}
                    </#if>
                    <#if udf.type == 'integer'>
                    ${udf.caption} ::                         ${uValue}
                    </#if>
                    <#if udf.type == 'list'>
                    ${udf.caption} ::           <#if uValue!="">${uValue.value}</#if>
                    </#if>
                    <#if udf.type == 'multilist'>
                    ${udf.caption} ::
                        <#if udf.value?exists><#list uValue as ym>
                        ${ym.value}
                        </#list>
                        </#if>
                    </#if>
                    <#if udf.type == 'task'>
                    ${udf.caption} ::
                        <#if udf.value?exists><#list uValue as taskUDF>
                        ${taskUDF.name}  [#${taskUDF.number}]
                        </#list>
                        </#if>
                    </#if>
                    <#if udf.type == 'user'>
                    ${udf.caption}
                        <#if udf.value?exists><#list uValue as userUDF>
                        ${userUDF.name}   [${userUDF.login}]
                        </#list>
                        </#if>
                    </#if>
                    <#if udf.type == 'memo'>
                    ${udf.caption} ::                         ${uValue}
                    </#if>
                    <#if udf.type == 'url'>
                    ${udf.caption} :: <#if uValue!="">                    ${uValue.link}        </#if>
                    </#if>
                </#if>
            </#if>
        </#list>
    </#if>
<#--</@compress>-->
</@table>
<#if task.attachments?exists && (task.attachments?size>0)>
====================================================================================================
<@std.I18n key="ATTACHMENTS"/>

    <#list task.attachments as att>
        <#assign atSize = att.size/>
    ----------------------------------------------------------------------------------------------------
    <@table columns=2 positions="0,40,160">
    <@std.I18n key="FILE"/>::          ${att.name}
    <@std.I18n key="FILE_SIZE"/>::      <#if (atSize< 1024)><1 kB<#else>${(atSize/1024)?int} kB</#if>
    <@std.I18n key="DESCRIPTION"/>::  <#if att.description?exists>${att.description}</#if>
    <@std.I18n key="LINK"/>::   ${link}/download/task/${att.task.number}/${att.id}
    </@table>
    </#list>
</#if>
<#assign subtasks = Util.currentSubtask()/>
<#if (reason.code == "S") && (subtasks?size>0)>

===============================================================================
<@std.I18n key="SUBTASKS"/>
===============================================================================

    <#list subtasks as item>
    ${link}/task/${item.number}
    <@table columns=2 positions="0,30,80">
        <#if filter.TASKNUMBER>
        <@std.I18n key="NUMBER"/>::    #${item.number}
        </#if>
        <#if filter.FULLPATH>
        <@std.I18n key="FULL_PATH"/>::      <@std.path from=task to=item ; number, name > / ${name}</@std.path> [#${item.number}]
        </#if>
        <#if filter.NAME>
        <@std.I18n key="NAME"/>::           ${item.name} [#${item.number}]
        </#if>
        <#if (filter.ALIAS && item.shortname?exists)>
        <@std.I18n key="ALIAS"/>:: ${item.shortname}
        </#if>
        <#if filter.CATEGORY>
        <@std.I18n key="CATEGORY"/>:: ${item.category.name}
        </#if>
        <#if filter.STATUS>
        <@std.I18n key="TASK_STATE"/>:: ${item.status.name}
        </#if>
        <#if (filter.RESOLUTION && item.resolution?exists)>
        <@std.I18n key="RESOLUTION"/>:: ${item.resolution.name}
        </#if>
        <#if (filter.PRIORITY && item.priority?exists)>
        <@std.I18n key="PRIORITY"/>:: ${item.priority.name}
        </#if>
        <#if (item.submitter?exists)>
            <#if filter.SUBMITTER>
            <@std.I18n key="SUBMITTER"/>:: ${item.submitter.name}
            </#if>
            <#if (filter.SUBMITTERSTATUS)>
            <@std.I18n key="SUBMITTER_STATUS"/>:: <#list item.submitterPrstatuses as sstatus>${sstatus} </#list>
            </#if>
        </#if>
        <#if ((item.getHandlerUserId()?exists || item.getHandlerGroupId()?exists))>
            <#if filter.HANDLER>
            <@std.I18n key="HANDLER"/>:: <#if item.handlerGroup?exists>${item.handlerGroup.name}<#else>${item.handlerUser.name}</#if>
            </#if>
            <#if filter.HANDLERSTATUS>
            <@std.I18n key="HANDLER_STATUS"/>:: <#list item.handlerPrstatuses as hstatus>${hstatus} </#list>
            </#if>
        </#if>
        <#if (filter.SUBMITDATE && item.submitdate?exists)>
        <@std.I18n key="SUBMIT_DATE"/>:: ${DateFormatter.parse(item.submitdate)}
        </#if>
        <#if (filter.UPDATEDATE && item.updatedate?exists)>
        <@std.I18n key="UPDATE_DATE"/>:: ${DateFormatter.parse(item.updatedate)}
        </#if>
        <#if (filter.CLOSEDATE && item.closedate?exists)>
        <@std.I18n key="CLOSE_DATE"/>:: ${DateFormatter.parse(item.closedate)}
        </#if>
        <#if (filter.DEADLINE && item.deadline?exists)>
        <@std.I18n key="DEADLINE"/>:: ${DateFormatter.parse(item.deadline)}
        </#if>
        <#if filter.BUDGET>
        <@std.I18n key="BUDGET"/>:: ${item.budget}
        </#if>
        <#if filter.ABUDGET>
        <@std.I18n key="ABUDGET"/>:: ${item.actualBudget}
        </#if>
        <#if (item.filteredUDFValues?exists)>
            <#list item.filteredUDFValues as y>
                <#if y?exists && fields.contains("UDF"+y.id)>
                    <#assign aValue = y.getValue(item)?default("")>
                    <#if aValue?exists>
                    ${y.caption}::                         ${aValue}
                    </#if>
                </#if>
            </#list>
        </#if>
    </@table>
        <#if item.description?exists>
        ----------------------------------------------------------------------------------------------------
        <@std.I18n key="DESCRIPTION"/>:
        ${item.description}
        </#if>
    ====================================================================================================
    </#list>
</#if>
<#assign taskMessages = Util.getSortedMessages(task)/>
<#if (taskMessages?size>0)>
    <#if  showhistory?exists>
        <#assign mc = showhistory?int/>
        <#else>
            <#assign mc = 0/>
    </#if>

    <#if mc!=0>
    ====================================================================================================
    <@std.I18n key="HISTORY"/>
    ----------------------------------------------------------------------------------------------------
        <#list taskMessages as msg>
            <#if mc &gt; -1 && (msg_index+1) &gt; mc>
                <#break />
            </#if>
        <@table columns=2 positions="0,40,160">
        <@std.I18n key="MESSAGE_SUBMIT_DATE"/> :: ${DateFormatter.parse(msg.time)}
        <@std.I18n key="MESSAGE_SUBMITTER"/> :: ${msg.submitter.name}
        <@std.I18n key="MESSAGE_TYPE"/> :: ${msg.mstatus.name}
            <#if msg.resolution?exists>
            <@std.I18n key="MESSAGE_SUBMIT_DATE"/> :: ${msg.resolution.name}
            </#if>
            <#if msg.handlerUserId?exists || msg.handlerGroupId?exists>
                <#if msg.handlerUserId?exists>
                <@std.I18n key="MESSAGE_HANDLER"/> :: ${msg.handlerUser.name}
                </#if>
                <#if msg.handlerGroupId?exists>
                <@std.I18n key="MESSAGE_HANDLER"/> :: ${msg.handlerGroup.name}
                </#if>
            </#if>
            <#if msg.priorityId?exists>
            <@std.I18n key="MESSAGE_PRIORITY"/> :: ${msg.priority.name}
            </#if>
            <#if msg.deadline?exists>
            <@std.I18n key="MESSAGE_DEADLINE"/> :: ${DateFormatter.parse(msg.deadline)}
            </#if>
            <#if msg.budget?exists && (msg.budget>0)>
            <@std.I18n key="MESSAGE_BUDGET"/> :: ${msg.budgetAsString}
            </#if>
            <#if msg.hrs?exists && (msg.hrs>0)>
            <@std.I18n key="MESSAGE_ABUDGET"/> :: ${msg.actualBudgetAsString}
            </#if>
        </@table>
        ----------------------------------------------------------------------------------------------------
        <@table columns=2 positions="0,190,10">
        ${msg.textDescription}
        </@table>
        ====================================================================================================
        </#list>
    </#if>
</#if>

<@std.I18n key="CHANGE_NOTIFICATION" value=[source.getName(), source.getFilter(), source.getTask()]/>

<@std.I18n key="NOTIFICATION_UNSUBSCRIBE"/> ${link}/unsubscribe?notificationId=${source.id}