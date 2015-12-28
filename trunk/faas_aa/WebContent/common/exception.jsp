<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<% 
	System.err.println("异常信息 : "+request.getAttribute("exceptionStack"));	
	response.setStatus(500);
	response.setContentType("text/html");
	out.print("{successs:false}");
%>

