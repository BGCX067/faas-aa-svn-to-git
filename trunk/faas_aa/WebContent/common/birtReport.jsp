<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/WEB-INF/tld/birt.tld" prefix="birt"%>
<html>
<head>
<title>东莞公安治安卡口应用系统-报表</title>
<link rel="shortcut icon" href="<s:url value="/include/image/icon/favicon.ico"/>" type="image/x-icon" />
<style type="text/css">
body{
	margin: 0px;
	pading: 0px;
	overflow: hidden;
}
</style>
</head>
<body>
<iframe src="/report-viewer/run?__report=report/overSpeed.rptdesign" height="100%" width="100%" frameborder="0"></iframe>
</body>
</html>