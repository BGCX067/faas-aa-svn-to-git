package com.fortunes.faas.vo;

import net.fortunes.core.Model;

@SuppressWarnings("serial")
public class AttShifts extends Model{
	private long employeeId;
	private String employeeCode;
	private String employeeName;
	private double salary;
	private double regionalAllowance;
	private String institution;  //一级机构
	private String deptName;
	private long organizationId;
	private double lates;
	private double earlys;
	private double absents;
	private double lateTimes;
	private double earlyTimes;
	private double weekday;
	private double restday;
	private double leave;
	private double sickLeave;
	private double annualLeave;
	private double homeLeave;
	private double maternityLeave;
	private double familyPlanningLeave;
	private double nurseLeave;//看护假
	private double marriageLeave;//婚嫁
	private double funeralLeave;//丧假
	private double feedLeave;//哺育假
	private double lookAfterLeave;
	private double injuryLeave;
	/////////2012-09-24///////////////////////////
	private double takeCareLeave;//看护假
	private double holidays;
	private double travel;
	private double overTime;
	
	public AttShifts(long employeeId,String employeeCode,String employeeName,double salary,String institution,String deptName,long organizationId,
			double lates,double earlys,double absents,double lateTimes,double earlyTimes,
			double weekday,double restday,
			double leave,double sickLeave,double annualLeave,double homeLeave,double maternityLeave,double familyPlanningLeave,
			double nurseLeave,double marriageLeave,double funeralLeave,double feedLeave,double lookAfterLeave,double injuryLeave,
			double holidays,double travel,double overTime){
		this.employeeId = employeeId;this.employeeCode = employeeCode;this.employeeName = employeeName;this.institution = institution;
		this.salary = salary;this.deptName = deptName;this.organizationId = organizationId;
		this.lates = lates;this.earlys = earlys;this.absents = absents;
		this.lateTimes = lateTimes;this.earlyTimes = earlyTimes;
		this.weekday = weekday;
		this.restday = restday;
		this.leave = leave;
		this.sickLeave = sickLeave;
		this.annualLeave = annualLeave;
		this.homeLeave = homeLeave;
		this.maternityLeave = maternityLeave;
		this.familyPlanningLeave = familyPlanningLeave;
		this.nurseLeave = nurseLeave;
		this.marriageLeave = marriageLeave;
		this.funeralLeave = funeralLeave;
		this.feedLeave = feedLeave;
		this.lookAfterLeave = lookAfterLeave;
		this.injuryLeave = injuryLeave;
		this.holidays = holidays;
		this.travel = travel;
		this.overTime = overTime;
		
	}
	
	public AttShifts(long employeeId,String employeeCode,String employeeName,double salary,String institution,String deptName,long organizationId,
			double lates,double earlys,double absents,double lateTimes,double earlyTimes,
			double weekday,double restday,
			double leave,double sickLeave,double annualLeave,double homeLeave,double maternityLeave,double familyPlanningLeave,
			double nurseLeave,double marriageLeave,double funeralLeave,double feedLeave,double lookAfterLeave,double injuryLeave,
			double takeCareLeave,double holidays,double travel,double overTime){
		this.employeeId = employeeId;this.employeeCode = employeeCode;this.employeeName = employeeName;this.institution = institution;
		this.salary = salary;this.deptName = deptName;this.organizationId = organizationId;
		this.lates = lates;this.earlys = earlys;this.absents = absents;
		this.lateTimes = lateTimes;this.earlyTimes = earlyTimes;
		this.weekday = weekday;
		this.restday = restday;
		this.leave = leave;
		this.sickLeave = sickLeave;
		this.annualLeave = annualLeave;
		this.homeLeave = homeLeave;
		this.maternityLeave = maternityLeave;
		this.familyPlanningLeave = familyPlanningLeave;
		this.nurseLeave = nurseLeave;
		this.marriageLeave = marriageLeave;
		this.funeralLeave = funeralLeave;
		this.feedLeave = feedLeave;
		this.lookAfterLeave = lookAfterLeave;
		this.injuryLeave = injuryLeave;
		this.takeCareLeave=takeCareLeave;
		this.holidays = holidays;
		this.travel = travel;
		this.overTime = overTime;
		
	}
	
	public AttShifts(long employeeId,String employeeCode,String employeeName,double salary,double regionalAllowance ,String institution,String deptName,
			long organizationId,double lates,double earlys,double absents,double lateTimes,double earlyTimes,
			double weekday,double restday,
			double leave,double sickLeave,double annualLeave,double homeLeave,double maternityLeave,double familyPlanningLeave,
			double nurseLeave,double marriageLeave,double funeralLeave,double feedLeave,double lookAfterLeave,double injuryLeave,
		    double takeCareLeave,double holidays,double travel,double overTime){
		this.employeeId = employeeId;this.employeeCode = employeeCode;this.employeeName = employeeName;
		this.salary = salary;this.regionalAllowance = regionalAllowance;this.institution = institution;this.deptName = deptName;
		this.organizationId = organizationId;
		this.lates = lates;this.earlys = earlys;this.absents = absents;
		this.lateTimes = lateTimes;this.earlyTimes = earlyTimes;
		this.weekday = weekday;
		this.restday = restday;
		this.leave = leave;
		this.sickLeave = sickLeave;
		this.annualLeave = annualLeave;
		this.homeLeave = homeLeave;
		this.maternityLeave = maternityLeave;
		this.familyPlanningLeave = familyPlanningLeave;
		this.nurseLeave = nurseLeave;
		this.marriageLeave = marriageLeave;
		this.funeralLeave = funeralLeave;
		this.feedLeave = feedLeave;
		this.lookAfterLeave = lookAfterLeave;
		this.injuryLeave = injuryLeave;
		this.takeCareLeave=takeCareLeave;
		this.holidays = holidays;
		this.travel = travel;
		this.overTime = overTime;
		
	}
	
	public AttShifts(long employeeId,String employeeCode,String employeeName,double salary,double regionalAllowance ,String institution,String deptName,
			long organizationId,double lates,double earlys,double absents,double lateTimes,double earlyTimes,
			double weekday,double restday,
			double leave,double sickLeave,double annualLeave,double homeLeave,double maternityLeave,double familyPlanningLeave,
			double nurseLeave,double marriageLeave,double funeralLeave,double feedLeave,double lookAfterLeave,double injuryLeave,
		    double holidays,double travel,double overTime){
		this.employeeId = employeeId;this.employeeCode = employeeCode;this.employeeName = employeeName;
		this.salary = salary;this.regionalAllowance = regionalAllowance;this.institution = institution;this.deptName = deptName;
		this.organizationId = organizationId;
		this.lates = lates;this.earlys = earlys;this.absents = absents;
		this.lateTimes = lateTimes;this.earlyTimes = earlyTimes;
		this.weekday = weekday;
		this.restday = restday;
		this.leave = leave;
		this.sickLeave = sickLeave;
		this.annualLeave = annualLeave;
		this.homeLeave = homeLeave;
		this.maternityLeave = maternityLeave;
		this.familyPlanningLeave = familyPlanningLeave;
		this.nurseLeave = nurseLeave;
		this.marriageLeave = marriageLeave;
		this.funeralLeave = funeralLeave;
		this.feedLeave = feedLeave;
		this.lookAfterLeave = lookAfterLeave;
		this.injuryLeave = injuryLeave;
		this.holidays = holidays;
		this.travel = travel;
		this.overTime = overTime;
		
	}
	
	public double getRegionalAllowance() {
		return regionalAllowance;
	}

	public void setRegionalAllowance(double regionalAllowance) {
		this.regionalAllowance = regionalAllowance;
	}

	public long getEmployeeId() {
		return employeeId;
	}

	public long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(long organizationId) {
		this.organizationId = organizationId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}
	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public double getLates() {
		return lates;
	}
	public void setLates(double lates) {
		this.lates = lates;
	}
	public double getEarlys() {
		return earlys;
	}
	public void setEarlys(double earlys) {
		this.earlys = earlys;
	}
	public double getAbsents() {
		return absents;
	}
	public void setAbsents(double absents) {
		this.absents = absents;
	}
	public double getLateTimes() {
		return lateTimes;
	}
	public void setLateTimes(double lateTimes) {
		this.lateTimes = lateTimes;
	}
	public double getEarlyTimes() {
		return earlyTimes;
	}
	public void setEarlyTimes(double earlyTimes) {
		this.earlyTimes = earlyTimes;
	}
	
	public double getWeekday() {
		return weekday;
	}

	public void setWeekday(double weekday) {
		this.weekday = weekday;
	}

	public double getRestday() {
		return restday;
	}

	public void setRestday(double restday) {
		this.restday = restday;
	}

	public double getLeave() {
		return leave;
	}

	public void setLeave(double leave) {
		this.leave = leave;
	}

	public double getSickLeave() {
		return sickLeave;
	}

	public void setSickLeave(double sickLeave) {
		this.sickLeave = sickLeave;
	}

	public double getAnnualLeave() {
		return annualLeave;
	}

	public void setAnnualLeave(double annualLeave) {
		this.annualLeave = annualLeave;
	}

	public double getHomeLeave() {
		return homeLeave;
	}

	public void setHomeLeave(double homeLeave) {
		this.homeLeave = homeLeave;
	}

	public double getMaternityLeave() {
		return maternityLeave;
	}

	public void setMaternityLeave(double maternityLeave) {
		this.maternityLeave = maternityLeave;
	}

	public double getFamilyPlanningLeave() {
		return familyPlanningLeave;
	}

	public void setFamilyPlanningLeave(double familyPlanningLeave) {
		this.familyPlanningLeave = familyPlanningLeave;
	}

	public double getNurseLeave() {
		return nurseLeave;
	}

	public void setNurseLeave(double nurseLeave) {
		this.nurseLeave = nurseLeave;
	}

	public double getMarriageLeave() {
		return marriageLeave;
	}

	public void setMarriageLeave(double marriageLeave) {
		this.marriageLeave = marriageLeave;
	}

	public double getFuneralLeave() {
		return funeralLeave;
	}

	public void setFuneralLeave(double funeralLeave) {
		this.funeralLeave = funeralLeave;
	}

	public double getFeedLeave() {
		return feedLeave;
	}

	public void setFeedLeave(double feedLeave) {
		this.feedLeave = feedLeave;
	}

	public double getLookAfterLeave() {
		return lookAfterLeave;
	}

	public void setLookAfterLeave(double lookAfterLeave) {
		this.lookAfterLeave = lookAfterLeave;
	}

	public double getInjuryLeave() {
		return injuryLeave;
	}

	public void setInjuryLeave(double injuryLeave) {
		this.injuryLeave = injuryLeave;
	}

	public double getHolidays() {
		return holidays;
	}

	public void setHolidays(double holidays) {
		this.holidays = holidays;
	}

	public double getTravel() {
		return travel;
	}

	public void setTravel(double travel) {
		this.travel = travel;
	}

	public double getOverTime() {
		return overTime;
	}

	public void setOverTime(double overTime) {
		this.overTime = overTime;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public double getTakeCareLeave() {
		return takeCareLeave;
	}

	public void setTakeCareLeave(double takeCareLeave) {
		this.takeCareLeave = takeCareLeave;
	}
	
	
}
