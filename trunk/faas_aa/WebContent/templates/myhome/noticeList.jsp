<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<div class="msg-body">
	<p class="welcome">
		您共有<span class="countNumber"><s:property value="noticeMessages.size"/></span>条公告信息未读！
		<a id="myhome-notice-refresh" class="block refreshBt" href="#" 
			onclick="Ext.getCmp('notice-portal').refresh();">刷新</a>
	</p>
	<s:iterator value="noticeMessages" status="rowStatus">
		<div class="msg-item <s:if test="#rowStatus.even">even</s:if>">
			<p>
				<span class="message"><s:property value="notice.contents"/></span>
				发布于<span class="date"><s:date name="notice.createDateTime" format="yyyy-MM-dd hh:mm" /></span>
				<a class="block reading" href="#" onclick="Ext.getCmp('notice-portal').tag(this,<s:property value="id"/>);">标记为已阅</a>
				<a class="block feedback" href="mailto:admin@sz.pbc.org.cn" >反馈</a>
				
			</p>
		</div>
	</s:iterator>	
</div>
<script type="text/javascript">

</script>
