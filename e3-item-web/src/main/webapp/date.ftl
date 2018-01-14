<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>获取时间类型数据</title>
</head>
<body>
<h1>获取时间类型数据</h1>
<h1>时间:${today?time}</h1>
<h1>日期:${today?date}</h1>
<h1>日期时间:${today?datetime}</h1>
<h1>格式化:${today?string('yyyy-MM-dd')}</h1>
</body>
</html>