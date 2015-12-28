package com.fortunes.fjdp;

public class Constants {
	
	public static enum Environment {
		DEVELOPMENT,DEBUG,PRODUCTION
	}
	
	public static final String PROJECT_CNAME = "深圳市建设银行考勤系统";
	public static final String PROJECT_ENAME = "FJDP";
	public static final String MANUAL_DOC_PATH_NAME = "/doc/manual";
	public static final Environment ENVIRONMENT = Environment.DEVELOPMENT;
	
	public static final int USER_STATUS_LOGOUT = 0;//注销状态
	public static final int USER_STATUS_LOGIN = 1;//登陆状态
	
	public static final String MANAGE_POST_FIRST_DEPT_PRIME_SUPERINTENDENT ="一级部门主要负责人";	
	public static final String MANAGE_POST_SECOND_DEPT_PRIME_SUPERINTENDENT ="一级部门其他负责人、二级部（中心）主要负责人、对公区域团队主要负责人";	
	public static final String MANAGE_POST_THIRD_DEPT_PRIME_SUPERINTENDENT ="二级部（中心）其他负责人、对公区域团队其他负责人、区域消费信贷部负责人、区域总经理、副总经理";	
	public static final String MANAGE_POST_FOURTH_DEPT_PRIME_SUPERINTENDENT ="支行行长、支行副行长";
	
	public static final String LEAVE_TYPE_WELFARE = "福利假";
	public static final String LEAVE_TYPE_SICK_LEAVE = "病假";
	public static final String LEAVE_TYPE_LEAVE = "请假";
	public static final String LEAVE_TYPE_MATERNITY = "生育假";
	public static final String LEAVE_TYPE_RELATED_INJURY_LEAVE = "工伤假";
	/////////2012-09-24//////////////////////////////
	public static final String LEAVE_TYPE_TAKECARE_LEAVE = "看护假";
	public static final String LEAVE_TYPE_ABROAD="驻外";
	public static final String LEAVE_TYPE_OVERTIME="加班";
	public static final String LEAVE_TYPE_TRAVEL="出差";
	public static final String LEAVE_TYPE_OTHER = "其他";
	
	public static final String WELFARES_LEAVE = "年休假、探亲假、福利假_看护假、婚假、丧假";
	public static final String MATERNITY_LEAVE = "产假、哺乳假、生育假_看护假、计划生育假";
	
	public static final String UNIT_LEAVE_APPROVE = "单位请假审批人";
	public static final String DEPT_LEAVE_APPROVE = "部门请假审批人";
	public static final String HR_LEAVE_APPROVE = "人力部请假审批人";
	public static final String BRANCH_BANK_LEADER_LEAVE_APPROVE = "分管行领导请假审批人";
	public static final String BRANCH_MANAGER_LEAVE_APPROVE = "行长请假审批人";
	
	public static final String POST_TYPE_EMPLOYEE = "员工";
	public static final String POST_TYPE_MANAGER = "管理岗位";
	
	
	public static final String ORGANIZATION_HR = "人力资源部";
	
	public static final int MY_TASK_HOLIDAYAPPLY= 1;//等待我去审批的请假单
	public static final int MY_WAIT_APPROVE_HOLIDAYAPPLY = 2;//等待别人去审批的请假单
	public static final int MY_APPROVED_HOLIDAYAPPLY = 3;//我的已审批的请假单
	public static final int MY_UNAPPROVED_HOLIDAYAPPLY = 4;//审批未通过的请假单
	
	public static final String ORGANIZATION_UNIT = "单位";
	public static final String ORGANIZATION_DEPT = "部门";
	
	public static final int DEVICE_STATUS_OFFLINE = 0;
	public static final int DEVICE_STATUS_ONLINE = 1;
	
	public static final String ATTSHIFT_STATUS_WEEKDAY = "正常工作";
	public static final String ATTSHIFT_STATUS_RESTDAY = "休息";
	public static final String ATTSHIFT_STATUS_ABSENT = "旷工";
	public static final String ATTSHIFT_STATUS_LATE = "迟到";
	public static final String ATTSHIFT_STATUS_EARLY = "早退";
	public static final String ATTSHIFT_STATUS_EXCEPTION = "例外";
	public static final String ATTSHIFT_STATUS_OVERTIME = "加班";
	public static final String ATTSHIFT_STATUS_ONLYONE = "因只打卡1次而旷工";
	public static final String ATTSHIFT_STATUS_LATELIMIT = "因迟到而旷工";
	public static final String ATTSHIFT_STATUS_EARLYLIMIT = "因早退而旷工";
	
	public static final String ATTSHIFT_UNIT_DAY = "天";
	
	public static final String IS_CERTIGIER = "YES";
	public static final String NOT_CERTIGIER = "NO";
}
