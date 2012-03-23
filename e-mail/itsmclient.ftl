<#assign ContentType="text/html;\n charset=\"${charset}\""/>
<#assign Subject="TrackStudio: Подтверждение регистрации инцидента #${task.number}"/>
<#assign simpleTask = Util.simplify(task)/>
<#if simpleTask.udfValues["Конфигурационные единицы"]??>
<#assign product = simpleTask.udfValues["Конфигурационные единицы"]/>
</#if>

<html>
<body>
<h2>Подтверждение регистрации инцидента</h2>

<div>Уважаемый(ая) ${user.name}<br>

Инцидент <b>"${task.name}"</b>, о котором вы сообщили, зарегистрирован в службе поддержки компании TrackStudio.<br>
<div style="background-color: yellow">Регистрационный номер запроса <b>#${task.number}</b></div><br>
В настоящее время ваш запрос обрабатывает <#if ((task.getHandlerUserId()?? || task.getHandlerGroupId()??))><#if task.handlerGroup??>${task.handlerGroup.name}<#else>${task.handlerUser.name}</#if></#if>. Специалист службы поддержки свяжется с вами <#if (user.tel??)>по указанному телефону <b>${user.tel}</b>, либо </#if>по этому адресу электронной почты в срок до <strong><#if (task.deadline??)>${DateFormatter.parse(task.deadline)}<#else>${simpleTask.udfValues["Срок решения"]!""}</#if></strong>.<br>
В соответствии с договором, срок решения вашего инцидента <b>${simpleTask.udfValues["Срок решения"]!""}</b><br>
<br><br>

<table><caption>Регистрационные данные вашего инцидента</caption>
    <tr><td>Заголовок</td><td>${task.name}</td></tr>
    <tr><td>Дата и время регистрации</td><td><#if (task.submitdate??)>${DateFormatter.parse(task.submitdate)}</#if></td></tr>
    <tr><td>Состояние</td><td><#if task.status??>${task.status.name}</#if></td></tr>
    <tr><td>Срок ответа</td><td><#if (task.deadline??)>${DateFormatter.parse(task.deadline)}<#else>${simpleTask.udfValues["Срок решения"]!""}</#if></td></tr>
    <tr><td>Срок решения</td><td>${simpleTask.udfValues["Срок решения"]!""}</td></tr>
    <tr><td>Продукт или услуга</td><td><#if product??>
    <#list product?split(";") as prod>
<#assign productTask = Util.findTask(prod?trim)/>
<#if productTask??>
${productTask.name}<br>
</#if>
</#list>
</#if></td></tr>
    <tr><td>Текущий ответственный</td><td><#if ((task.getHandlerUserId()?? || task.getHandlerGroupId()??))><#if task.handlerGroup??>${task.handlerGroup.name}<#else>${task.handlerUser.name}</#if></#if></td></tr>
    </table>
    <#if task.description??>
<h3>Описание инцидента</h3>
<div style="background-color: #CCCCCC; padding: 8px 8px 8px 8px">
${Util.getWikiText(task.description)}
</div>
</#if>
<br>

Со всеми вопросами и за справкой вы можете обращаться по телефону <b>8 (800) 700-66-77</b> в будние дни с 9:00 до 18:00 по Московскому времени. При обращении назовите специалисту службы поддержки номер инцидента <strong>#${task.number}</strong>.<br><br>

<div>Вы также можете ответить на это письмо по электронной почте. Ваш ответ будет добавлен к инциденту и служба поддержки его прочтет.</div>
<br><br>
Для того, чтобы отписаться от дальнейших оповещений <b>по этому инциденту</b>, <a target="blank" href="${link}/unsubscribe?notificationId=${source.id}">перейдите по ссылке</a><br>
<br>
&nbsp;&nbsp;&nbsp;&nbsp;С уважением, Служба поддержки компании TrackStudio
</div>
 </body>
</html>
