<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>请假单</title>
<%
//在weblogic中设置的默认的字符集编码是utf-8，那么在jsp中设置了字符集编码为utf-8，那么就可以不用转换。当然转换也是对的
	request.setCharacterEncoding("UTF-8");
	String name=new String(request.getParameter("name"));
	String organization=new String(request.getParameter("organization").getBytes("UTF-8"),"UTF-8");
	String position=new String(request.getParameter("position").getBytes("UTF-8"),"UTF-8");
	String leaveType=new String(request.getParameter("leaveType").getBytes("UTF-8"),"UTF-8");
	String applyReason=new String(request.getParameter("applyReason").getBytes("UTF-8"),"UTF-8");
	String site=new String(request.getParameter("site").getBytes("UTF-8"),"UTF-8");
	String leaveApplyType = new String(request.getParameter("leaveApplyType").getBytes("UTF-8"),"UTF-8");
	List<String[]> list = new ArrayList<String[]>();
	int cer = -1;
	for(int i = 0;i<5;i++){
		String id = request.getParameter("id"+i);
		if(id!=null&&!"".equals(id)){
			String[] approves = new String[7];
			approves[0] = new String(request.getParameter("suggestion"+i).getBytes("UTF-8"),"UTF-8");
			approves[1] = new String(request.getParameter("approveStatus"+i).getBytes("UTF-8"),"UTF-8");
			approves[2] = new String(request.getParameter("leaveApprove"+i).getBytes("UTF-8"),"UTF-8");
			approves[3] = new String(request.getParameter("approveDate"+i).getBytes("UTF-8"),"UTF-8");
			approves[4] = new String(request.getParameter("approveOrganization"+i).getBytes("UTF-8"),"UTF-8");
			approves[5] = new String(request.getParameter("postType"+i).getBytes("UTF-8"),"UTF-8");
			approves[6] = new String(request.getParameter("isCertigier"+i).getBytes("UTF-8"),"UTF-8");
			list.add(approves);
			if(approves[6].equals("YES")){
				cer = i;
			}
		}
	}
%>
</head>
<body>
<p><br/><br/><br/></p>
<% 
if(leaveApplyType.equals("leader")){

%>
<center><font size="5"><strong>中国建设银行深圳市分行一级机构、二级部（中心）</strong></font></center>
<br/>
<center><font size="5"><strong>负责人请假审批表</strong></font></center><br/><%}else if(leaveApplyType.equals("branch")){ %>
<center><font size="5"><strong>中国建设银行深圳市分行网点负责人请假审批表</strong></font></center><br />
<%}else{ %>
<center><font size="5"><strong>中国建设银行深圳市分行员工请假审批表</strong></font></center>
<%} %>
<table border="1px" cellspacing="0px" style="border-collapse:collapse" align="center">
	<tr>
		<td width="65" height="50" align="center">姓&nbsp;&nbsp;名</td><td width="80"><%=name %></td>
		<td width="65" align="center">单&nbsp;&nbsp;位</td><td width="145" colspan="2"><%=organization %></td>
		<td width="65" align="center">职&nbsp;&nbsp;务</td><td width="145" colspan="2"><%=position %></td>
	</tr>
	<tr>
		<td width="65" height="60" align="center">婚&nbsp;&nbsp;姻状&nbsp;&nbsp;况</td><td width="80"><%=request.getParameter("isMarry") %></td>
		<td width="65" align="center">参&nbsp;&nbsp;加工&nbsp;&nbsp;作时&nbsp;&nbsp;间</td><td width="80"><%=request.getParameter("joinWorkDate") %></td>
		<td width="65" align="center">正式调<br/>入建行<br/>时&nbsp;&nbsp;间</td><td width="80"><%=request.getParameter("hireDate") %></td>
		<td width="65" align="center">拟休假<br/>类&nbsp;&nbsp;别</td><td width="80"><%=leaveType %></td>
	</tr>
	<tr>
		<td width="65" height="50" align="center">休&nbsp;&nbsp;假天&nbsp;&nbsp;数</td><td width="80"><%=request.getParameter("leaveDays") %></td>
		<td width="65" align="center">假期开<br/>始时间</td><td width="80"><%=request.getParameter("startDate") %></td>
		<td width="65" align="center">假期终<br/>止时间</td><td width="80"><%=request.getParameter("endDate") %></td>
		<td width="65" align="center">回行上<br/>班时间</td><td width="80"><%=request.getParameter("backDate") %></td>
	</tr>
	<tr>		
		<td width="65" height="50" align="center">拟&nbsp;&nbsp;往地&nbsp;&nbsp;点</td><td width="80"><%=site %></td>
		<td width="65" align="center">外出联<br/>系电话</td><td><%=request.getParameter("goOutPhone") %></td>
		<td width="65" align="center">详&nbsp;&nbsp;细<br/>事&nbsp;&nbsp;由</td><td colspan="3"><%=applyReason %></td>
	</tr>
	
	<%
				if(leaveApplyType.equals("leader")){
					String[] approve = new String[7];
					String[] times = new String[3];
					String[] passage = new String[7];
					String[] passTimes = new String[3];
					String[] president = new String[7];
					String[] presTimes = new String[3];
					
					if(list.size()>0){
						approve = list.get(0);
						times = approve[3].split("-");
					}
					if(list.size()>1){
						passage = list.get(1);
						passTimes = passage[3].split("-");
					}
					if(list.size()>2){
						president = list.get(2);
						presTimes = president[3].split("-");
					}
					 
			%>
	<tr>
		<td width = "60" align="center" height="110">所&nbsp;&nbsp;在<br/>一&nbsp;&nbsp;级<br/>机&nbsp;&nbsp;构<br/>意&nbsp;&nbsp;见</td>
		<td colspan="8">
			<table>
				<tr>
					<td>
						<ol>
							<li>是否同意上述休假申请：&nbsp;1）同意<input type="checkBox"/>2）不同意<input type="checkBox"/>3）暂缓休假<input type="checkBox"/></li>
							<li>该领导离行期间工作安排情况：<br>&nbsp;1)单位负责人离行期间工作由&nbsp;&nbsp;<%=approve[2]==null?"":approve[2] %>&nbsp;&nbsp;负责，请进行公务授权；<br>&nbsp;2)该负责人离行期间工作已作好安排</li>
							<li>其他需说明的情况：<br><br></li>
						</ol>
					</td>
				</tr>
				<tr>
					<td><p style="text-align:right;margin-left:2px">主要负责人签名:&nbsp;&nbsp;<%=approve[2]==null?"":approve[2] %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=times[0]==null?"":times[0] %>年&nbsp;&nbsp;<%=times[1]==null?"":times[1] %>月&nbsp;&nbsp;<%=times[2]==null?"":times[2] %>日&nbsp;</p></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td width = "60" height="80" align="center">分管行<br/>领&nbsp;&nbsp;导意&nbsp;&nbsp;见</td>
		<td colspan="8"><%=passage[0]==null?"":passage[0] %><p style="text-align:right">签名：<%=passage[2]==null?"":passage[2] %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=times[0]==null?"":times[0] %>年&nbsp;&nbsp;<%=times[1]==null?"":times[1] %>月&nbsp;&nbsp;<%=times[2]==null?"":times[2] %>日&nbsp;&nbsp;&nbsp;&nbsp;</p></td>
	</tr>
	<tr>
		<td width = "60" height="80" align="center">分&nbsp;&nbsp;行<br/>行&nbsp;&nbsp;长<br/>意&nbsp;&nbsp;见</td>
		<td colspan="8"><%=president[0]==null?"":president[0] %><p style="text-align:right">签名：<%=president[2]==null?"":president[2] %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=presTimes[0]==null?"":presTimes[0] %>年&nbsp;&nbsp;<%=presTimes[1]==null?"":presTimes[1] %>月&nbsp;&nbsp;<%=presTimes[2]==null?"":presTimes[2] %>日&nbsp;&nbsp;&nbsp;&nbsp;</p></td>
	</tr>
	<tr>
		<td width = "60" height="70" align="center">办公室<br/>备&nbsp;&nbsp;案</td><td colspan="3"></td>
		<td width="60" align="center">人力资<br/>源&nbsp;&nbsp;部备&nbsp;&nbsp;案</td><td colspan="3"></td>
	</tr>	
	<tr>
		<table border="0px" cellspacing="0px" style="border-collapse:collapse" align="center">
		<tr><td>
			<p>备注：</p><ol>
			
				<li>一级机构、二级部（中心）负责人请假需<strong>提前3个工作日</strong>报分管行领导审批（核）；</li>
				<li>一级机构主要负责人请假需由授权人签字确认；</li>
				<li><strong>一级机构主要负责人离开深圳，须报分行行长审批。</strong></li>
			</ol>
		</td></tr>	
		</table>
	</tr>
	
	
	<%}else if(leaveApplyType.equals("branch")){ 
		String[] approve = new String[7];
		String[] times = new String[3];
		String[] passage = new String[7];
		String[] passTimes = new String[3];
		String[] president = new String[7];
		String[] presTimes = new String[3];
		if(list.size()>0){
			approve = list.get(0);
			times = approve[3].split("-");
		}
		if(list.size()>1){
			passage = list.get(1);
			passTimes = passage[3].split("-");
		}
		if(list.size()>2){
			president = list.get(2);
			presTimes = president[3].split("-");
		}
	%>
	<tr>
		<td width = "60" align="center" height="230">所&nbsp;&nbsp;在<br/>网&nbsp;&nbsp;点<br/>意&nbsp;&nbsp;见</td>
		<td colspan="8">
			<table>
				<tr>
					<td>
						<ol>
							<li>是否同意上述休假申请：&nbsp;1）同意<input type="checkBox"/>2）不同意<input type="checkBox"/>3）暂缓休假<input type="checkBox"/></li>
							<li>该领导离行期间工作安排情况：<br>&nbsp;1)单位负责人离行期间工作由&nbsp;&nbsp;<%=approve[2]==null?"":approve[2] %>&nbsp;&nbsp;负责，请进行公务授权；<br>&nbsp;2)该负责人离行期间工作已作好安排</li>
							<li>其他需说明的情况：<br><br></li>
						</ol>
					</td>
				</tr>
				<tr>
					<td><p style="text-align:right;margin-left:2px">负责人签名:&nbsp;&nbsp;<%=approve[2]==null?"":approve[2] %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=times[0]==null?"":times[0] %>年&nbsp;&nbsp;<%=times[1]==null?"":times[1] %>月&nbsp;&nbsp;<%=times[2]==null?"":times[2] %>日&nbsp;&nbsp;&nbsp;&nbsp;</p></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td width = "60" height="130" align="center">支&nbsp;&nbsp;行</br>意&nbsp;&nbsp;见</td>
		<td colspan="8"><%=passage[0]==null?"":passage[0] %><br/>&nbsp;<br/>&nbsp;<br/>&nbsp;<br/>&nbsp;<p style="text-align:right">主要负责人签名：<%if(passage[2]!=null){ %><%=passage[2]==null?"":passage[2] %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%}else{ %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%}%><%=times[0]==null?"":times[0] %>年&nbsp;&nbsp;<%=times[1]==null?"":times[1] %>月&nbsp;&nbsp;<%=times[2]==null?"":times[2] %>日&nbsp;&nbsp;&nbsp;&nbsp;</p></td>
	</tr>
	<tr>
		<td width="60" align="center" height="130">人&nbsp;&nbsp;力<br/>资&nbsp;&nbsp;源<br/>部意见</td>
		<td colspan="8"><%=president[0]==null?"":president[0] %><br/>&nbsp;<br/>&nbsp;<br/>&nbsp;<br/>&nbsp;<p style="text-align:right">主要负责人签名：<%if(president[2]!=null){ %><%=president[2]==null?"":president[2] %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%}else{ %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%}%><%=presTimes[0]==null?"":presTimes[0] %>年&nbsp;&nbsp;<%=presTimes[1]==null?"":presTimes[1] %>月&nbsp;&nbsp;<%=presTimes[2]==null?"":presTimes[2] %>日&nbsp;&nbsp;&nbsp;&nbsp;</p></td>
	</tr>	
	<tr>
		<table border="0px" cellspacing="0px" style="border-collapse:collapse" align="center">
		<tr><td>
	<pre>
注：1、网点负责人请假，提前3个工作日报支行审批（核）；2、网点负责人请假需由授权人签
字确认。</pre>
		</td></tr>	
		</table>
	</tr>
	<%}else if(leaveApplyType.equals("employee")){ 
		for(String[] approve:list){
		%>
	<tr>
			<td><%=approve[4] %>意见</td>
			<td colspan="8">
				<table>
					<tr>
						<td colspan="2" height="50" ><%=approve[0] %></td>
					</tr>
					<tr>
						<td>审批</td>
						<td><%=approve[1] %></td>
					</tr>
					<tr>
						<td>领导签名:</td>
						<td><%=approve[2] %></td>
					</tr>
					<tr>
						<td>日期:</td>
						<td><%=approve[3] %></td>
					</tr>
				</table>
			</td>
	</tr>
	<%}} %>
</table>
</body>
</html>