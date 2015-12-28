<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<s:iterator value="#session.widgets">
<script type="text/javascript" src="<%= request.getContextPath() %><s:property/>"></script>
</s:iterator>
<script type="text/javascript" src="<%= request.getContextPath() %>/widget/calendar.js"></script>