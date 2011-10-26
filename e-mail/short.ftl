<#assign ContentType="text/html;\n charset=\"${charset}\""/>
<#assign Subject="#${task.number}: ${task.parent.name} > ${task.name}"/>
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
    border-top: 1px gray solid;
    border-bottom: 1px gray solid;
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

<#if reason.code == "M" || reason.code == "MA">
<div class="newmessage">
${Util.getWikiText(reason.message.description)}
</div>
</#if>

<#if task.description?exists>
<br/>
<@std.I18n key="DESCRIPTION"/>
<div class="description">
${Util.getWikiText(task.description)}
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
<COL width="40%">
<COL width="60%">
</COLGROUP>
<tr>
<th>${DateFormatter.parse(msg.time)}</th><th>${msg.submitter.name}</th>
</tr>
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

</BODY>
</HTML>
