<%@ page language="java" import="java.util.*" contentType="textml; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="textml; charset=UTF-8">
<title>加班表单</title>
<%
//在weblogic中设置的默认的字符集编码是utf-8，那么在jsp中设置了字符集编码为utf-8，那么就可以不用转换。当然转换也是对的
	request.setCharacterEncoding("UTF-8");
	String name=new String(request.getParameter("name"));
	String organization=new String(request.getParameter("organization").getBytes("UTF-8"),"UTF-8");
	String position=new String(request.getParameter("position").getBytes("UTF-8"),"UTF-8");
	String applyReason=new String(request.getParameter("applyReason").getBytes("UTF-8"),"UTF-8");
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
<center><h3>中国建设银行深圳市分行员工加班审批表</h3></center>
<table border="1px" cellspacing="0px" style="border-collapse:collapse" align="center">
	<tr>
		<td width="80" height="40">姓名</td><td width="120"><%=name %></td>
		<td width="80" height="40">单位</td><td width="120"><%=organization %></td>
		<td width="80" height="40">岗位</td><td width="120"><%=position %></td>
	</tr>
	<tr>
		<td width="80" height="40">申请日期</td><td width="120"><%=request.getParameter("applyDate") %></td>
		<td width="80" height="40">开始日期</td><td><%=request.getParameter("workOverTime") %></td>
		<td width="80" height="40">结束日期</td><td><%=request.getParameter("eWorkOverTimeDate") %></td>
	</tr>
	<tr>
		<td width="80" height="40">开始时间</td><td width="120"  colspan="2"><%=request.getParameter("workOverTimeStart") %></td>
		<td width="80" height="40">结束时间</td><td  colspan="2"><%=request.getParameter("eWorkOverTimeDate") %></td>
		
	</tr>
	<tr><td width="80" height="40">详情</td><td width="120"  colspan="5"><%=applyReason %></td></tr>
	<%
		for(String[] approve : list){
	%>
			<tr>
			<td><%=approve[4] %>意见</td>
			<td colspan="5">
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
	<%
		}
	%>
	
</table>
</body>
</html>
