<#assign ContentType="text/html;\n charset=\"${charset}\""/>
<#assign Subject="TrackStudio: Инцидент решен #${reason.message.task.number}"/>
<#assign task = reason.message.task/>
<#assign simpleTask = Util.simplify(task)/>
<#assign product = simpleTask.udfValues["Конфигурационные единицы"]/>
<#if simpleTask.udfValues["Временное решение"]??>
<#assign solution = simpleTask.udfValues["Временное решение"]/>
</#if>
<html>
<body>
<h2>Ваш инцидент решен</h2>

<div>Уважаемый(ая) ${user.name}<br>

Созданный вами инцидент <b>"${task.name}"</b> успешно решен.<br>

<div style="background-color: #CCCCCC; padding: 8px 8px 8px 8px">
    ${Util.getWikiText(reason.message.description)}
 <#if solution??>
<#assign solutionTask = Util.findTask(solution)/>
            <br>
<#if solutionTask??>
<h2>Решение</h2>
${Util.getWikiText(solutionTask.description)}
</#if>
</#if>
    </div>

<br><br>
Со всеми вопросами и за справкой вы можете обращаться по телефону <b>8 (800) 700-66-77</b> в будние дни с 9:00 до 18:00 по Московскому времени. При обращении назовите специалисту службы поддержки номер инцидента <strong>#${task.number}</strong>.<br><br>
<br><br>

<table><caption>Регистрационные данные вашего инцидента</caption>
    <tr><td>Заголовок</td><td>${task.name}</td></tr>
    <tr><td>Дата и время регистрации</td><td><#if (task.submitdate??)>${DateFormatter.parse(task.submitdate)}</#if></td></tr>
    <tr><td>Состояние</td><td><#if task.status??>${task.status.name}</#if></td></tr>
    <tr><td>Срок ответа</td><td><#if (task.deadline??)>${DateFormatter.parse(task.deadline)}</#if></td></tr>
    <tr><td>Срок решения</td><td>${simpleTask.udfValues["Срок решения"]!""}</td></tr>
    <tr><th>Решено</th><th><#if (task.closedate??)>${DateFormatter.parse(task.closedate)}</#if></th></tr>
    <tr><td>Продукт или услуга</td><td><#if product??>
<#assign productTask = Util.findTask(product)/>
<#if productTask??>
${productTask.name}
</#if>
</#if></td></tr>
    <tr><td>Решение</td><td>
<#if solutionTask??>
${solutionTask.name} #${solutionTask.number}
</#if>
</td></tr>
    </table>
    <#if task.description??>
<h3>Описание инцидента</h3>
<div style="background-color: #CCCCCC; padding: 8px 8px 8px 8px">
${Util.getWikiText(task.description)}
</div>
</#if>
<br>

Для того, чтобы отписаться от дальнейших оповещений <b>по этому инциденту</b>, <a target="blank" href="${link}/unsubscribe?notificationId=${source.id}">перейдите по ссылке</a><br>
Для того, чтобы отписаться от всех уведомлений от системы Service Desk, <a target="blank" href="${link}/unsubscribe?notificationId=${source.id}">перейдите по ссылке</a>.<br>
<br>
&nbsp;&nbsp;&nbsp;&nbsp;С уважением, Служба поддержки компании TrackStudio
</div>
 </body>
</html>