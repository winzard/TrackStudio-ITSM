<#assign ContentType="text/html;\n charset=\"${charset}\""/>
<#assign Subject="TrackStudio: Найдено решение #${reason.message.task.number}"/>
<#assign task = reason.message.task/>
<#assign simpleTask = Util.simplify(task)/>
<#assign product = simpleTask.udfValues["Конфигурационные единицы"]/>
<#if simpleTask.udfValues["Временное решение"]??>
<#assign solution = simpleTask.udfValues["Временное решение"]/>
</#if>
<html>
<body>
<#if user.prstatus.id == "402881821204446701124cffdf95036d">
<h2>Найдено возможное решение</h2>

<div>Уважаемый(ая) ${user.name}<br>

Для созданного вами инцидента <b>"${task.name}"</b> найдено решение.<br>

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
Вы можете самостоятельно связаться со службой поддежки по телефону <b>8 (800) 700-66-77</b> в будние дни с 9:00 до 18:00 по Московскому времени, и подтвердить или опровергнуть правильность предложенного вам решения.
При обращении назовите специалисту службы поддержки номер инцидента <strong>#${task.number}</strong>.<br><br>
<div>Вы также можете ответить на это письмо по электронной почте. Ваш ответ будет добавлен к инциденту и служба поддержки его прочтет.</div>
<br><br>

<table><caption>Регистрационные данные вашего инцидента</caption>
    <tr><td>Заголовок</td><td>${task.name}</td></tr>
    <tr><td>Дата и время регистрации</td><td><#if (task.submitdate??)>${DateFormatter.parse(task.submitdate)}</#if></td></tr>
    <tr><td>Состояние</td><td><#if task.status??>${task.status.name}</#if></td></tr>
    <tr><td>Срок ответа</td><td><#if (task.deadline??)>${DateFormatter.parse(task.deadline)}</#if></td></tr>
    <tr><td>Срок решения</td><td>${simpleTask.udfValues["Срок решения"]!""}</td></tr>
    <tr><td>Продукт или услуга</td><td><#if product??>
    <#list product?split(";") as prod>
<#assign productTask = Util.findTask(prod?trim)/>
<#if productTask??>
${productTask.name}<br>
</#if>
</#list>
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
<br>
&nbsp;&nbsp;&nbsp;&nbsp;С уважением, Служба поддержки компании TrackStudio
</div>
<#else>
<h2>Найдено возможное решение</h2>

<div>Уважаемый(ая) ${user.name}<br>

Для созданного вами инцидента <b>"${task.name}"</b> найдено решение.<br>

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
Вы можете самостоятельно связаться со службой поддежки по телефону <b>8 (800) 700-66-77</b> в будние дни с 9:00 до 18:00 по Московскому времени, и подтвердить или опровергнуть правильность предложенного вам решения.
При обращении назовите специалисту службы поддержки номер инцидента <strong>#${task.number}</strong>.<br><br>
<div>Вы также можете ответить на это письмо по электронной почте. Ваш ответ будет добавлен к инциденту и служба поддержки его прочтет.</div>
<br><br>

<table><caption>Регистрационные данные вашего инцидента</caption>
    <tr><td>Заголовок</td><td>${task.name}</td></tr>
    <tr><td>Дата и время регистрации</td><td><#if (task.submitdate??)>${DateFormatter.parse(task.submitdate)}</#if></td></tr>
    <tr><td>Состояние</td><td><#if task.status??>${task.status.name}</#if></td></tr>
    <tr><td>Срок ответа</td><td><#if (task.deadline??)>${DateFormatter.parse(task.deadline)}</#if></td></tr>
    <tr><td>Срок решения</td><td>${simpleTask.udfValues["Срок решения"]!""}</td></tr>
    <tr><td>Продукт или услуга</td><td><#if product??>
    <#list product?split(";") as prod>
<#assign productTask = Util.findTask(prod?trim)/>
<#if productTask??>
${productTask.name}<br>
</#if>
</#list>
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
<br>
&nbsp;&nbsp;&nbsp;&nbsp;С уважением, Служба поддержки компании TrackStudio
</div>
</#if>
 </body>
</html>
