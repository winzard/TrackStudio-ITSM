<#assign ContentType="text/html;\n charset=\"${charset}\""/>
<#assign Subject="TrackStudio: Инцидент #${task.number} был просрочен"/>
<#assign simpleTask = Util.simplify(task)/>
<html>
<body>
<h2>Был просрочен инцидент</h2>

<div>Уважаемый(ая) ${user.name}<br>

Инцидент <b>"${task.name}"</b>, созданный ${DateFormatter.parse(task.submitdate)} пользователем ${task.submitter.name} был просрочен. В соответствии с SLA, инцидент должен был быть решен к <strong><#if (task.deadline??)>${DateFormatter.parse(task.deadline)}<#else>${simpleTask.udfValues["Срок решения"]!""}</#if></strong>.
<br>
&nbsp;&nbsp;&nbsp;&nbsp;С уважением, Служба поддержки компании TrackStudio
<br>
<a href="${link}/task/${task.number}?thisframe=true" style="font-size:medium;">Перейти к инциденту #${task.number}</a>
</div>

<div>
    <@std.I18n key="CHANGE_NOTIFICATION" value=[source.getName(), source.getFilter(), source.getTask()]/>
    <br/>
    <a target="blanc" href="${link}/unsubscribe?notificationId=${source.id}"><@std.I18n key="NOTIFICATION_UNSUBSCRIBE"/></a>
</div>
 </body>
</html>
