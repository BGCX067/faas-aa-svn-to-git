<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<div class="msg-body">
	<p class="welcome">
		共有<span class="countNumber"><s:property value="onlineUsers.size"/></span>个用户在线！
		<a class="block refreshBt" href="#" onclick="Ext.getCmp('loginStat-portal').refresh();">刷新</a>
	</p>
	<table class="body-table"><tbody>
		<s:iterator value="onlineUsers" status="rowStatus">
		<tr class="msg-item <s:if test="#rowStatus.even">even</s:if>">
			<td style="width:80px" class="userName"><s:property value="displayName"/></td> 
			<td>上次登陆时间：<span class="date"><s:date name="loginSession.lastLoginTime" format="yyyy-MM-dd hh:mm:ss" /></span></td>
		</tr>
		</s:iterator>
	</tbody></table>		
</div>
<script type="text/javascript">

</script>
