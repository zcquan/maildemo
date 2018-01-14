<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>fm对null值处理</title>
</head>
<body>
	<h1>?对null值处理:</h1>
	<h2>获取值: ${name?default('0')}</h2>
	
	<hr color="red" size="2">
	<h1>!对null值处理:</h1>
	<h2>获取值: ${name!"默认值"}</h2>
	<h2>获取值: ${name!}</h2>

	<hr color="red" size="2">
	<h1>if对null值处理:</h1>
	<h2>获取值: <#if name??> ${name} </#if></h2>

</body>
</html>