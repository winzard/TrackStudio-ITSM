<#assign ContentType="text/html;\n charset=\"${charset}\""/>
<#assign Subject="<${task.projectAlias} | ${reasonForSubject} | #${task.number}>: ${task.name}"/>
<#if serverEmail?exists>
    <#assign FromEmail="${serverEmail}"/>
</#if>
<#if fromUserName?exists>
    <#assign FromUser="${fromUserName}"/>
</#if>
<#assign Headers={"X-Meta":"data"}/>
<!DOCTYPE HTML PUBLIC "-//W3C/DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<TITLE>TrackStudio: ${task.name?html}</TITLE>
<style>
<#compress>
.title{
    font-family: Tahoma, sans-serif;
    color: #666666;
    font-size: 11px;
    font-weight: normal;
}
div.data{
    font-family: Tahoma, sans-serif;
    color: #000000;
    font-size: 20px;
    margin-top: 0px;
    padding-left: 20px;
}
BODY {
    font-family: Tahoma;
    font-size: 12px;
}
A {
    color: black;
    font-weight: bold;
    font-size: 11px;
    text-decoration: underline;
}
div.fullpath {
    padding: 4px 4px 4px 4px;
    vertical-align: middle;
    margin-left: 20px;
}
div.fullpath A {
    color: black;
    font-weight: bold;
    padding-top: 4px;
    padding-bottom: 4px;
    padding-left: 2px;
    padding-right: 2px;
    text-decoration: underline;
    font-size: 11px;
}
div.fullpath A:hover{
    color: black;
    text-decoration: underline;
}
div.fullpath A.last {
    color: black;
    text-decoration: none;
}
span.date{
    color: black;
    font-weight: normal;
    font-size: 12px;
    font-family: Tahoma;
}
div.viewtask{
    padding: 4px 4px 4px 4px;
}
em.number{
color: inherit;
background-color: inherit;
font-weight: inherit;
font-style: normal;
padding-left: 8px;

}
em.number:before{
content: ' [';
}
em.number:after{
content: ']';
}
div.viewtask h1{
    font-size: 16px;
    font-family: Tahoma;
    color: black;
    margin-bottom: 0px;
    padding-bottom: 0px;
}
div.viewtask a{
    text-decoration:none;
}
div.viewtask td{
    padding: 4px 4px 4px 4px;
}
div.viewtask td.submitted{
    font-size: 12px;
    font-family: Tahoma;
    color: #666666;
    text-align: left;
}
div.viewtask td.time{
    text-align: right;
    font-size: 12px;
    font-family: Tahoma;
}

div.viewtask span.state{
    font-size: 12px;
    font-family: Tahoma;
    color: #666666;
    text-align: right;
    padding: 4px 4px 4px 4px;
}
div.viewtask span.user{
    color: black;
    font-weight: bold;
    font-size: 12px;
}
div.description{
    background-color: #F8F8F8;
    font-family: Tahoma;
    font-size: 11px;
}
table.change th{
    color: red;
    font-family: Arial, sans-serif;
    font-size: 24px;
}
table.change td{
    font-family: Tahoma, Tahoma, sans-serif;
    font-size: 11px;
    padding-left: 8px;
}
table.state th{
    background-color: #F8DBA1;
    color: black;
    font-family: Tahoma;
    font-size: 11px;
    font-weight: bold;
}
table.state td{
    font-family: Tahoma;
    font-size: 11px;
    font-weight: bolder;
    padding-top: 4px;
    padding-bottom: 4px;
    padding-left: 4px;
    padding-right: 4px;
}
span.state{
    color: #F8DBA1;
    font-family: Arial, sans-serif;
    font-size: 24px;
}
div.newmessage{
    border: #F8DBA1 1px solid;
    padding-top: 4px;
    padding-bottom: 4px;
    padding-left: 4px;
    padding-right: 4px;
}
TABLE.general {
    font-family: Tahoma, Arial, Helvetica, sans-serif;
    color: #000000;
    font-size: 11px;
    background-color: #84B0C7;
    vertical-align: middle;
    width: 100%;
}

TABLE.general CAPTION {
    font-family: Tahoma, Arial, Helvetica, sans-serif;
    color: #000000;
    font-size: 11px;
    text-align: left;
    font-weight: normal;
    background-color: #B3CDDA;
    border-top: #84B0C7 1px solid;
    border-left: #84B0C7 1px solid;
    border-right: #84B0C7 1px solid;
    padding-left: 6px;
    padding-top: 2px;
    padding-bottom: 2px;
}
TABLE.general CAPTION A {
    color: #000000;
    font-weight: bold;
    font-size: 11px;
    text-decoration: underline;
}
TABLE.general TH {
    color: #000000;
    text-align: right;
    font-weight: bold;
    background-color: #EBF1F2;
    vertical-align: middle;
    font-size: 11px;
    padding-right: 12px;
}
TABLE.general TD {
    background-color: #F8F8F8;
    vertical-align: middle;
    font-size: 11px;
}
TABLE.general TR.line0 TD{
    background-color: #F8F8F8;
    vertical-align: middle;
    font-size: 11px;
}
TABLE.general TH.subs{
    text-align:center;
}
TABLE.general TR.line1 TD {
    background-color: #F0F0F0;
    vertical-align: middle;
    font-size: 11px;
}
TABLE.general TD IMG {
    margin-left: 0px;
    vertical-align: middle;
    margin-bottom: 0px;
}
TABLE.general TD A {
    color: #000000;
    font-weight: bold;
    font-size: 11px;
    text-decoration: underline;
}
TABLE.general TH A {
    color: #000000;
    font-weight: bold;
    font-size: 11px;
    text-decoration: underline;
}
div.subtasks{
    padding: 4px 4px 4px 4px;
}
div.subtasks a{
    text-decoration:none !important;
}
div.subtasks td{
    padding: 4px 4px 4px 4px;
}
</#compress>
</style>
</HEAD>
<BODY>
<#if reason.code == "N" || reason.code == "NA">
<table class="change">
<tr>
<th>!</th>
<td>
<#if ((reason.task.getHandlerUserId()?exists || reason.task.getHandlerGroupId()?exists))>
    <#assign addTaskFor><#if reason.task.handlerGroup?exists>${reason.task.handlerGroup.name}<#else>${reason.task.handlerUser.name}</#if></#assign>
    <@std.I18n key="CHANGE_ADD_TASK_FOR" value=["<b>"+reason.by.name+"</b>", "<b>"+reason.task.category.name+"</b>", "<b>"+addTaskFor+"</b>"]/>
<#else>
    <@std.I18n key="CHANGE_ADD_TASK" value=["<b>"+reason.by.name+"</b>", "<b>"+reason.task.category.name+"</b>"]/>
</#if>
<#if reason.code == "NA">
<br>
<#list reason.attachments as attachment>
<#assign attlink>
<a href="${link}/download/task/${attachment.task.number}/${attachment.id}/${attachment.name}">${attachment.name} (<#assign atSize = attachment.size/><#if atSize< 1024>&lt;1 kB<#else>${(atSize/1024)?int} kB</#if>)</a>
</#assign>
<@std.I18n key="CHANGE_ADD_ATTACHMENT" value=["<b>"+reason.by.name+"</b>", "<b>"+attlink+"</b>"]/><br>
</#list>
</#if>
</td>
</tr>
</table>
<#if task.description?exists && task.description?has_content>
<div class="newmessage">
${Util.getWikiText(task.description)}
</div>
</#if>
</#if>
<#if reason.code == "S">
<table class="change">
<tr>
<th>!</th>
<td><@std.I18n key="SUBSCRIPTION_REASON" value=["<b>"+reason.by.name+"</b>", "<b>"+DateFormatter.parse(reason.when)+"</b>"]/></td>
</tr>
</table>
</#if>
<#if reason.code == "U">
<table class="change">
<tr>
<th>!</th>
<td><@std.I18n key="CHANGE_UPDATE_TASK" value=["<b>"+reason.by.name+"</b>", "<b>"+reason.task.category.name+"</b>"]/></td>
</tr>
</table>
</#if>
<#if reason.code == "A">
<table class="change">
<tr>
<th>!</th>
<td>
<#list reason.attachments as attachment>
<#assign attlink>
<a href="${link}/download/task/${attachment.task.number}/${attachment.id}/${attachment.name}">${attachment.name} (<#assign atSize = attachment.size/><#if atSize< 1024>&lt;1 kB<#else>${(atSize/1024)?int} kB</#if>)</a>
</#assign>
<@std.I18n key="CHANGE_ADD_ATTACHMENT" value=["<b>"+reason.by.name+"</b>", "<b>"+attlink+"</b>"]/><br>
</#list>
</td>
</tr>
</table>
</#if>
<#if reason.code == "M" || reason.code == "MA">
<table class="change">
<tr>
<th>!</th>
<td>
<#assign addMsgType><#if reason.message.resolution?exists>${reason.message.mstatus.name} (${reason.message.resolution.name})<#else>${reason.message.mstatus.name}</#if></#assign>
<#if ((reason.message.getHandlerUserId()?exists || reason.message.getHandlerGroupId()?exists))>
    <#assign addMsgFor><#if reason.message.handlerGroup?exists>${reason.message.handlerGroup.name}<#else>${reason.message.handlerUser.name}</#if></#assign>
    <@std.I18n key="CHANGE_ADD_MESSAGE_FOR" value=["<b>"+reason.by.name+"</b>", "<b>"+addMsgType+"</b>", "<b>"+addMsgFor+"</b>"]/>
<#else>
    <@std.I18n key="CHANGE_ADD_MESSAGE" value=["<b>"+reason.by.name+"</b>", "<b>"+addMsgType+"</b>"]/>
</#if>
<#if reason.code == "MA">
<br>
<#list reason.attachments as attachment>
<#assign attlink>
<a href="${link}/download/task/${attachment.task.number}/${attachment.id}/${attachment.name}">${attachment.name} (<#assign atSize = attachment.size/><#if atSize< 1024>&lt;1 kB<#else>${(atSize/1024)?int} kB</#if>)</a>
</#assign>
<@std.I18n key="CHANGE_ADD_ATTACHMENT" value=["<b>"+reason.by.name+"</b>", "<b>"+attlink+"</b>"]/><br>
</#list>
</#if>
</td>
</tr>
</table>
<div class="newmessage">
${Util.getWikiText(reason.message.description)}
      <#if reason.message.priorityId?exists || reason.message.deadline?exists || reason.message.budgetAsString != "" || reason.message.actualBudgetAsString != "">
                <table class="general" cellpadding="0" cellspacing="0">

                    <#if reason.message.priorityId?exists>
                        <tr>
                            <th width="20%"><@std.I18n key="MESSAGE_PRIORITY"/></th>
                            <td width="80%">${reason.message.priority.name}</td>
                        </tr>
                   </#if>
                    <#if reason.message.deadline?exists>
                        <tr>
                            <th width="20%"><@std.I18n key="MESSAGE_DEADLINE"/></th>
                            <td width="80%">${DateFormatter.parse(reason.message.deadline)}</td>
                        </tr>
                   </#if>

                    <#if reason.message.budgetAsString != "">
                        <tr>
                            <th width="20%"><@std.I18n key="BUDGET"/></th>
                            <td width="80%">${reason.message.budgetAsString}</td>
                        </tr>
                   </#if>

                    <#if reason.message.actualBudgetAsString != "">
                        <tr>
                            <th width="20%"><@std.I18n key="MESSAGE_ABUDGET"/></th>
                            <td width="80%">${reason.message.actualBudgetAsString}</td>
                        </tr>
                   </#if>
                </table>
           </#if>
</div>
</#if>
<#if reason.code=="T">
<table class="change">
<tr>
<th>!</th>
<td><@std.I18n key="CHANGE_TEST" value=[reason.notification.name, "<b>"+reason.by.name+"</b>", "<b>"+DateFormatter.parse(reason.when)+"</b>"]/></td>
</tr>
</table>
</#if>
<div class="viewtask">
<#if task.parent?exists>
<div class="fullpath">
<@std.path to=task.parent.number ; number, name>
/&nbsp;<a href="${link}/task/${number}?thisframe=true">${name}</a>
</@std.path>
</div>
</#if>
<a href="${link}/task/${task.number}?thisframe=true" style="font-size:medium;">${task.name?html} <em class="number">#${task.number}</em></a>
</div>
<div class="viewtask">
<#if task.description?exists>
<div class="description">
${Util.getWikiText(task.description)}
</div>
<br/>
</#if>
<table class="general" cellpadding="4" cellspacing="1">
<tr>
<td width="50%">
<table class="general" cellpadding="4" cellspacing="1">
<caption><@std.I18n key="STATE_TRACKING"/></caption>
<tr>
<th width="150"><@std.I18n key="CATEGORY"/></th><td><#if task.category?exists>${task.category.name}</#if></td>
</tr>
<tr>
<th><@std.I18n key="TASK_STATE"/></th><td><#if task.status?exists>${task.status.name}</#if></td>
</tr>
<tr>
<th><@std.I18n key="RESOLUTION"/></th><td><#if (task.resolution?exists)>${task.resolution.name}</#if></td>
</tr>
<tr>
<th><@std.I18n key="PRIORITY"/></th><td><#if (task.priority?exists)>${task.priority.name}</#if></td>
</tr>
<tr>
<th><@std.I18n key="SUBMITTER"/></th><td><#if (task.submitter?exists)>${task.submitter.name}</#if></td>
</tr>
<tr>
<th><@std.I18n key="HANDLER"/></th><td><#if ((task.getHandlerUserId()?exists || task.getHandlerGroupId()?exists))><#if task.handlerGroup?exists>${task.handlerGroup.name}<#else>${task.handlerUser.name}</#if></#if></td>
</tr>
</table>
</td>
<td width="50%">
<table class="general" cellpadding="4" cellspacing="1">
<caption><@std.I18n key="TIME_TRACKING"/></caption>
<tr>
<th width="150"><@std.I18n key="SUBMIT_DATE"/></th><td><#if (task.submitdate?exists)>${DateFormatter.parse(task.submitdate)}</#if></td>
</tr>
<tr>
<th><@std.I18n key="UPDATE_DATE"/></th><td><#if (task.updatedate?exists)>${DateFormatter.parse(task.updatedate)}</#if></td>
</tr>
<tr>
<th><@std.I18n key="CLOSE_DATE"/></th><td><#if (task.closedate?exists)>${DateFormatter.parse(task.closedate)}</#if></td>
</tr>
<tr>
<th><@std.I18n key="DEADLINE"/></th><td><#if (task.deadline?exists)>${DateFormatter.parse(task.deadline)}</#if></td>
</tr>
<tr>
<th><@std.I18n key="BUDGET"/></th><td><#if task.budget?exists && (task.budget>0)>${task.budgetAsString}</#if></td>
</tr>
<tr>
<th><@std.I18n key="ABUDGET"/></th><td><#if task.actualBudget?exists && (task.actualBudget>0)>${task.actualBudgetAsString}</#if></td>
</tr>
</table>
</td>
</tr>
<#if task.filteredUDFValues?exists && (task.filteredUDFValues?size>0)>
<tr>
<td colspan="2">
<table class="general" cellpadding="4" cellspacing="1">
<caption><@std.I18n key="CUSTOM_FIELDS"/></caption>
<COLGROUP>
<COL width="20%">
<COL width="80%">
</COLGROUP>
<#if (viewUdfList?exists)>
<#list viewUdfList as udf>
<#if udf?exists>
<#assign uValue = udf.value?default("")>
<#if uValue?exists>
<#if udf.type == 'date'>
<tr>
<th>${udf.caption}</th>
<td><#if uValue!="">${DateFormatter.parse(uValue)}</#if></td>
</tr>
</#if>
<#if udf.type == 'float'>
<tr>
<th>${udf.caption}</th>
<td>${uValue}</td>
</tr>
</#if>
<#if udf.type == 'integer'>
<tr>
<th>${udf.caption}</th>
<td>${uValue}</td>
</tr>
</#if>
<#if udf.type == 'string'>
<tr>
<th>${udf.caption}</th>
<td>${uValue}</td>
</tr>
</#if>
<#if udf.type == 'list'>
<tr>
<th>${udf.caption}</th>
<td><#if uValue!="">${uValue.value}</#if></td>
</tr>
</#if>
<#if udf.type == 'multilist'>
<tr>
<th>${udf.caption}</th>
<td><#if udf.value?exists>
        <#list uValue as ym>
            ${ym.value}<br>
        </#list></td>
    </#if>
</tr>
</#if>
<#if udf.type == 'task'>
<tr>
<th>${udf.caption}</th>
<td><#if udf.value?exists>
        <#list uValue as taskUDF>
            ${taskUDF.name} [<em class="number"#${taskUDF.number}</em>]<br>
        </#list>
    </#if>
</td>
</tr>
</#if>
<#if udf.type == 'user'>
<tr>
<th>${udf.caption}</th>
<td><#if udf.value?exists>
        <#list uValue as userUDF>
            ${userUDF.name} [@<em class="number">${userUDF.login}</em>]<br>
        </#list>
    </#if>
</td>
</tr>
</#if>
<#if udf.type == 'memo'>
<tr>
<th>${udf.caption}</th>
<td>${uValue}</td>
</tr>
</#if>
<#if udf.type == 'url'>
<tr>
<th>${udf.caption}</th>
<td><#if uValue!="">${uValue.link}</#if></td>
</tr>
</#if>
</#if>
</#if>
</#list>
</#if>
</table>
</td>
</tr>
</#if>
</table>
<#if task.attachments?exists && (task.attachments?size>0)>
<br/>
<table class="general" cellpadding="4" cellspacing="1">
<caption><@std.I18n key="ATTACHMENTS"/></caption>
<tr>
<td><ul>
<#list task.attachments as attachment>
<li><a href="${link}/download/task/${attachment.task.number}/${attachment.id}/${attachment.name}">${attachment.name?html} (<#assign atSize = attachment.size/><#if atSize< 1024>&lt;1 kB<#else>${(atSize/1024)?int} kB</#if>)</a> <#if attachment.description?exists>${attachment.description}</#if></li>
</#list>
</ul>
</td>
</tr>
</table>
</#if>
</div>
<#assign subtasks = Util.currentSubtask()/>
<#if (reason.code == "S") && (subtasks?size>0)>
<div class="viewtask">
<table class="general" cellpadding="4" cellspacing="1">
<caption><@std.I18n key="SUBTASKS"/></caption>
<tr>
<td>
<div class="subtasks">
<table class="general" cellpadding="4" cellspacing="1">
<tr>
<#if filter.TASKNUMBER>
<th class="subs"><@std.I18n key="NUMBER"/></th>
</#if>
<#if filter.FULLPATH>
<th class="subs"><@std.I18n key="FULL_PATH"/></th>
</#if>
<#if filter.NAME>
<th class="subs"><@std.I18n key="NAME"/></th>
</#if>
<#if (filter.ALIAS)>
<th class="subs"><@std.I18n key="ALIAS"/></th>
</#if>
<#if filter.CATEGORY>
<th class="subs"><@std.I18n key="CATEGORY"/></th>
</#if>
<#if filter.STATUS>
<th class="subs"><@std.I18n key="TASK_STATE"/></th>
</#if>
<#if (filter.RESOLUTION)>
<th class="subs"><@std.I18n key="RESOLUTION"/></th>
</#if>
<#if (filter.PRIORITY)>
<th class="subs"><@std.I18n key="PRIORITY"/></th>
</#if>
<#if filter.SUBMITTER>
<th class="subs"><@std.I18n key="SUBMITTER"/></th>
</#if>
<#if (filter.SUBMITTERSTATUS)>
<th class="subs"><@std.I18n key="SUBMITTER_STATUS"/></th>
</#if>
<#if filter.HANDLER>
<th class="subs"><@std.I18n key="HANDLER"/></th>
</#if>
<#if filter.HANDLERSTATUS>
<th class="subs"><@std.I18n key="HANDLER_STATUS"/></th>
</#if>
<#if (filter.SUBMITDATE)>
<th class="subs"><@std.I18n key="SUBMIT_DATE"/></th>
</#if>
<#if (filter.UPDATEDATE)>
<th class="subs"><@std.I18n key="UPDATE_DATE"/></th>
</#if>
<#if (filter.CLOSEDATE)>
<th class="subs"><@std.I18n key="CLOSE_DATE"/></th>
</#if>
<#if (filter.DEADLINE)>
<th class="subs"><@std.I18n key="DEADLINE"/></th>
</#if>
<#if filter.BUDGET>
<th class="subs"><@std.I18n key="BUDGET"/></th>
</#if>
<#if filter.ABUDGET>
<th class="subs"><@std.I18n key="ABUDGET"/></th>
</#if>
</tr>
<#list subtasks as item>
<tr>
<#if filter.TASKNUMBER>
<td>
<a href="${link}/task/${task.number}?thisframe=true">
#${item.number}
</a>
</td>
</#if>
<#if filter.FULLPATH>
<td>
<@std.path from=task to=item ; number, name>
/&nbsp;<a href="${link}/task/${number}?thisframe=true">${name}</a>
</@std.path>
<em class="number">#${item.number}</em>
</td>
</#if>
<#if filter.NAME>
<td>
<a href="${link}/task/${item.number}?thisframe=true">
${item.name?html}
</a>
<em class="number">#${item.number}</em>
</td>
</#if>
<#if (filter.ALIAS)>
<td>
<#if (item.shortname?exists)>
${item.shortname}
</#if>
</td>
</#if>
<#if filter.CATEGORY>
<td>
${item.category.name}
</td>
</#if>
<#if filter.STATUS>
<td>
${item.status.name}
</td>
</#if>
<#if (filter.RESOLUTION)>
<td>
<#if (item.resolution?exists)>
${item.resolution.name}
</#if>
</td>
</#if>
<#if (filter.PRIORITY)>
<td>
<#if (item.priority?exists)>
${item.priority.name}
</#if>
</td>
</#if>
<#if filter.SUBMITTER>
<td>
<#if (item.submitter?exists)>
${item.submitter.name}
</#if>
</td>
</#if>
<#if (filter.SUBMITTERSTATUS)>
<td>
<#if (item.submitter?exists)>
<#list item.submitterPrstatuses as sstatus>${sstatus} </#list>
</#if>
</td>
</#if>
<#if filter.HANDLER>
<td>
<#if ((item.getHandlerUserId()?exists || item.getHandlerGroupId()?exists))>
<#if item.handlerGroup?exists>${item.handlerGroup.name}<#else>${item.handlerUser.name}</#if>
</#if>
</td>
</#if>
<#if filter.HANDLERSTATUS>
<td>
<#if ((item.getHandlerUserId()?exists || item.getHandlerGroupId()?exists))>
<#list item.handlerPrstatuses as hstatus>${hstatus} </#list>
</#if>
</td>
</#if>
<#if (filter.SUBMITDATE)>
<td>
<#if (item.submitdate?exists)>
${DateFormatter.parse(item.submitdate)}
</#if>
</td>
</#if>
<#if (filter.UPDATEDATE)>
<td>
<#if (item.updatedate?exists)>
${DateFormatter.parse(item.updatedate)}
</#if>
</td>
</#if>
<#if (filter.CLOSEDATE)>
<td>
<#if (item.closedate?exists)>
${DateFormatter.parse(item.closedate)}
</#if>
</td>
</#if>
<#if (filter.DEADLINE)>
<td>
<#if (item.deadline?exists)>
${DateFormatter.parse(item.deadline)}
</#if>
</td>
</#if>
<#if filter.BUDGET>
<td>
${item.budgetAsString}
</td>
</#if>
<#if filter.ABUDGET>
<td>
${item.actualBudgetAsString}
</td>
</#if>
</tr>
</#list>
</table>
</div>
</td>
</tr>
</table>
</div>
</#if>
<#assign taskMessages = Util.getSortedMessages(task)/>
<#if (taskMessages?size>0)>

<#if  showhistory?exists>
<#assign mc = showhistory?int/>
<#else>
<#assign mc = 0/>
</#if>
 <#if mc!=0>
<div class="viewtask">
<table class="general" cellpadding="4" cellspacing="1">
<caption><@std.I18n key="HISTORY"/></caption>
<tr><td>
<#list taskMessages as msg>
    <#if mc &gt; -1 && (msg_index+1) &gt; mc>
<#break />
</#if>
<div class="viewtask">
<table class="general" cellpadding="4" cellspacing="1">
<COLGROUP>
<COL width="20%">
<COL width="80%">
</COLGROUP>
<tr>
<th><@std.I18n key="MESSAGE_SUBMIT_DATE"/></th><td>${DateFormatter.parse(msg.time)}</td>
</tr>
<tr>
<th><@std.I18n key="MESSAGE_SUBMITTER"/></th><td>${msg.submitter.name}</td>
</tr>
<tr>
<th><@std.I18n key="MESSAGE_TYPE"/></th><td>${msg.mstatus.name}</td>
</tr>
<#if msg.resolution?exists>
<tr>
<th><@std.I18n key="MESSAGE_RESOLUTION"/></th><td>${msg.resolution.name}</td>
</tr>
</#if>
<#if msg.handlerUserId?exists || msg.handlerGroupId?exists>
<#if msg.handlerUserId?exists>
<tr>
<th><@std.I18n key="MESSAGE_HANDLER"/></th><td>${msg.handlerUser.name}</td>
</tr>
</#if>
<#if msg.handlerGroupId?exists>
<tr>
<th><@std.I18n key="MESSAGE_HANDLER"/></th><td>${msg.handlerGroup.name}</td>
</tr>
</#if>
</#if>
<#if msg.priorityId?exists>
<tr>
<th><@std.I18n key="MESSAGE_PRIORITY"/></th><td>${msg.priority.name}</td>
</tr>
</#if>
<#if msg.deadline?exists>
<tr>
<th><@std.I18n key="MESSAGE_DEADLINE"/></th><td>${DateFormatter.parse(msg.deadline)}</td>
</tr>
</#if>
<#if msg.budget?exists && (msg.budget>0)>
<tr>
<th><@std.I18n key="MESSAGE_BUDGET"/></th><td>${msg.budgetAsString}</td>
</tr>
</#if>
<#if msg.hrs?exists && (msg.hrs>0)>
<tr>
<th><@std.I18n key="MESSAGE_ABUDGET"/></th><td>${msg.actualBudgetAsString}</td>
</tr>
</#if>
<#if msg.description?exists>
<tr>
<td colspan="2">
<div class="description">${Util.getWikiText(msg.description)}</div>
</td>
</tr>
</#if>
</table>
</div>
</#list>
</td>
</tr>
</table>
</div>
</#if>
</#if>
<div class="title">
    <@std.I18n key="CHANGE_NOTIFICATION" value=[source.getName(), source.getFilter(), source.getTask()]/>
    <br/>
    <a target="blanc" href="${link}/unsubscribe?notificationId=${source.id}"><@std.I18n key="NOTIFICATION_UNSUBSCRIBE"/></a>
</div>
</BODY>
</HTML>
