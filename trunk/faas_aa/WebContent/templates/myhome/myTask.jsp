<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="msg-body">
	<p class="welcome">
		单笔退款和批量退款确认任务列表！
		<a id="myhome-notice-refresh" class="block refreshBt" href="#" 
			onclick="Ext.getCmp('task-portal').refresh();">刷新</a>
	</p>
	
	<p>
		您共有<span id="refundApplyCount" class="countNumber"><s:property value="refundList.size"/></span>个单笔退款确认任务！
	</p>
	<div id="refundApplyDiv" style="margin-top: 10px">
		<table cellspacing="0" cellpadding="0" style="border: 1px solid black; border-collapse: collapse">
			<tr>
				<td class="tdRefundTitle">经办人</td>
				<td class="tdRefundTitle">金额</td>
				<td class="tdRefundTitle">提交时间</td>
				<td class="tdRefundTitle">操作</td>
			</tr>
			<s:iterator value="refundList" status="rowStatus">
			<tr id="refundApply-<s:property value='id'/>">
				<td class="tdRefundDetail"><s:property value="applyMan"/></td>
				<td class="tdRefundDetail"><s:property value="amount"/></td>
				<td class="tdRefundDetail"><s:date name="operDate" format="yyyy-MM-dd hh:mm" /></td>
				<td class="tdRefundDetail"><a class="block feedback" href="#" onclick="Ext.getCmp('task-portal').displayInfo(<s:property value='id'/>)" >查看</a></td>
			</tr>
			</s:iterator>
		</table>
	</div>

	<p style="margin-top: 20px">
		您共有<span id="batchRefundApplyCount" class="countNumber"><s:property value="batchRefundList.size"/></span>个批量退款确认任务！
	</p>	
	<div id="batchRefundApplyDiv" style="margin-top: 10px">
		<table cellspacing="0" cellpadding="0" style="border: 1px solid black; border-collapse: collapse">
			<tr>
				<td class="tdRefundTitle">经办人</td>
				<td class="tdRefundTitle">金额</td>
				<td class="tdRefundTitle">提交时间</td>
				<td class="tdRefundTitle">操作</td>
			</tr>
			<s:iterator value="batchRefundList" status="rowStatus">
			<tr id="batchRefundApply-<s:property value='id'/>">
				<td class="tdRefundDetail"><s:property value="applyMan"/></td>
				<td class="tdRefundDetail"><s:property value="amount"/></td>
				<td class="tdRefundDetail"><s:date name="operDate" format="yyyy-MM-dd hh:mm" /></td>
				<td class="tdRefundDetail"><a class="block feedback" href="#" onclick="Ext.getCmp('task-portal').displayBatchRefundInfo(<s:property value='id'/>)" >查看</a></td>
			</tr>
			</s:iterator>
		</table>
	</div>
</div>
<script type="text/javascript">

</script>
