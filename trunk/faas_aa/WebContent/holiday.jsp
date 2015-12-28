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
if(cer>=0){

%>
<center><font size="5"><strong>中国建设银行深圳市分行一级机构、二级部（中心）</strong></font></center>
<br/>
<center><font size="5"><strong>负责人请假审批表</strong></font></center><%}else{ %>
<center><h3>中国建设银行深圳市分行员工请假审批表</h3></center>
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
		<td width="65" align="center">正式调入建行时&nbsp;&nbsp;&nbsp;间</td><td width="80"><%=request.getParameter("hireDate") %></td>
		<td width="65" align="center">拟休假类&nbsp;&nbsp;&nbsp;别</td><td width="80"><%=leaveType %></td>
	</tr>
	<tr>
		<td width="65" height="50" align="center">休&nbsp;&nbsp;假天&nbsp;&nbsp;数</td><td width="80"><%=request.getParameter("leaveDays") %></td>
		<td width="65" align="center">假期开始时间</td><td width="80"><%=request.getParameter("startDate") %></td>
		<td width="65" align="center">假期终止时间</td><td width="80"><%=request.getParameter("endDate") %></td>
		<td width="65" align="center">回行上班时间</td><td width="80"><%=request.getParameter("backDate") %></td>
	</tr>
	<tr>		
		<td width="65" height="50" align="center">拟&nbsp;&nbsp;往地&nbsp;&nbsp;点</td><td width="80"><%=site %></td>
		<td width="65" align="center">外出联系电话</td><td><%=request.getParameter("goOutPhone") %></td>
		<td width="65" align="center">详&nbsp;&nbsp;细事&nbsp;&nbsp;由</td><td colspan="3"><%=applyReason %></td>
	</tr>
	
	<%
				if(cer>=0){
					String[] approve = list.get(cer);
					String [] times = approve[3].split("-");
			%>
	<tr>
		<td width = "60" align="center" height="100">所&nbsp;&nbsp;&nbsp;在<br/>一级&nbsp;&nbsp;&nbsp;机构意&nbsp;&nbsp;&nbsp;见</td>
		<td colspan="8">
			<table>
				<tr>
					<td>
						<ol>
							<li>是否同意上述休假申请：&nbsp;1）同意<input type="checkBox"/>2）不同意<input type="checkBox"/>3）暂缓休假<input type="checkBox"/></li>
							<li>该领导离行期间工作安排情况：<br>&nbsp;1)单位负责人离行期间工作由&nbsp;&nbsp;<%=approve[2] %>&nbsp;&nbsp;负责，请进行公务授权；<br>&nbsp;2)该负责人离行期间工作已作好安排</li>
							<li>其他需说明的情况：<br></li>
						</ol>
					</td>
				</tr>
				<tr>
					<td><p style="text-align:right;margin-left:2px">主要负责人签名:&nbsp;&nbsp;<%=approve[2] %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=times[0] %>年&nbsp;&nbsp;<%=times[1] %>月&nbsp;&nbsp;<%=times[2] %>日&nbsp;</p></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td width = "60" height="70" align="center">分管行领&nbsp;&nbsp;&nbsp;导意&nbsp;&nbsp;&nbsp;见</td>
		<td colspan="8"><p style="text-align:right">签名：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;日&nbsp;&nbsp;&nbsp;&nbsp;</p></td>
	</tr>
	<tr>
		<td width = "60" height="70" align="center">分&nbsp;&nbsp;&nbsp;行行&nbsp;&nbsp;&nbsp;长意&nbsp;&nbsp;&nbsp;见</td>
		<td colspan="8"><p style="text-align:right">签名：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;日&nbsp;&nbsp;&nbsp;&nbsp;</p></td>
	</tr>
	<tr>
		<td width = "60" height="70" align="center">办公室备&nbsp;&nbsp;&nbsp;&nbsp;案</td><td colspan="3"></td>
		<td width="60" align="center">人力资源&nbsp;&nbsp;&nbsp;&nbsp;部备&nbsp;&nbsp;&nbsp;&nbsp;案</td><td colspan="3"></td>
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
	
	
	<%}else{ 
		for(String[] approve : list){
			if(approve[6].equals("NO")){
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
	<%}}} %>
</table>
</body>
</html>