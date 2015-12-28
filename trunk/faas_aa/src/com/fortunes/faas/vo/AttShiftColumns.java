package com.fortunes.faas.vo;

/**
 * 
 * @author Leo
 * @version 2011-4-21
 */
public enum AttShiftColumns {
	employeeCode("员工编号"),
	employeeName("员工姓名"),
	institution("一级机构"),
	deptName("所在部门"),
	lates("迟到次数"),
	earlys("早退次数"),
	absents("旷工天数"),
	lateTimes("迟到总时间(分钟)"),
	earlyTimes("早退总时间(分钟)"),
	weekday("工作日"),
	restday("休息日"),
	leave("事假天数"),
	sickLeave("病假天数"),
	annualLeave("年休假天数"),
	homeLeave("探亲假天数"),
	maternityLeave("产假天数"),
	familyPlanningLeave("计划生育假天数"),
	nurseLeave("福利假"),//看护假
	marriageLeave("婚假"),//婚嫁
	funeralLeave("丧假"),//丧假
	feedLeave("哺育假"),//哺育假
	lookAfterLeave("生育假"),
	injuryLeave("工伤假"),
/////2012-09-24//////////////////////////
	takeCareLeave("看护假"),//看护假
	holidays("总休假天数"),
	travel("出差天数"),
	overTime("加班时间(小时)");
	
	private String label;
	private AttShiftColumns(String label){
		this.label = label;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
}
