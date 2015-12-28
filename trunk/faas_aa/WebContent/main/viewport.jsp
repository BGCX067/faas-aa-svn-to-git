<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
  	<title>考勤系统-中国建设银行深圳分行</title>
  	<s:include value="/common/header.jsp" />
	
	<!-- framework -->
	<script type="text/javascript" src="<s:url value="/main/frame.js"/>"></script>
    <script type="text/javascript" src="<s:url value="/main/viewport.js"/>"></script>
	
	<!-- widget js -->
	<script type="text/javascript" src="<s:url value="/widget/myhome.js"/>"></script>
	<s:include value="/common/widgetJs.jsp" />
	
</head>
<body>
<!-- Fields required for history management -->
<form id="history-form" class="x-hidden">
    <input type="hidden" id="x-history-field" />
    <iframe id="x-history-frame"></iframe>
</form>
<div id="loading-mask" style=""></div>
<div id="loading">
  <div class="loading-indicator">
  <img src="<s:url value="/include/image/large-loading.gif"/>" style="margin-right:8px;" align="middle"/>正在加载...</div>
</div>
<div id="logo-banner">
	<img src="<s:url value="/include/image/theme/barner-logo.png"/>" style="float:left;margin-left:1px;width:350px;height:38px;margin-top:1px;"/>
	<img src="<s:url value="/include/image/theme/barner-img.png"/>" style="float:right;margin-right:1px;width:234px;height:38px;margin-top:1px;"/>
</div>
</body>
</html>