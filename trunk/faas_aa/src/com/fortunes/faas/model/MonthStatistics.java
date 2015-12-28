package com.fortunes.faas.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import net.fortunes.core.Model;

import com.fortunes.fjdp.admin.model.Employee;
/**
 * 
 * @author Leo
 * @version 2011-4-8
 */
@Entity
public class MonthStatistics extends Model {
	
	@Id
	@GeneratedValue
	private long id;
	
	private long employeeId;
	private String employeeCode;
	private String employeeName;
	private double salary;
	private double regionalAllowance;
	private int years;
	private int months;
	private String deptName;
	private long organizationId;
	private double lates;
	private double earlys;
	private double absents;
	private double lateTimes;
	private double earlyTimes;
	private double weekdays;
	private double restday;
	private double leaves;
	private double sickLeave;
	private double annualLeave;
	private double homeLeave;
	private double maternityLeave;
	private double familyPlanningLeave;
	private double nurseLeave;//福利假
	private double marriageLeave;//婚嫁
	private double funeralLeave;//丧假
	private double feedLeave;//哺育假
	private double lookAfterLeave;//生育假
	private double injuryLeave;//工伤假
	//////2012-09-24//////////////////////////
	private double takeCareLeave;//看护假
	private double holidays;
	private double travel;
	private double overTime;
	@ManyToOne
	private Employee managerAffirm;
	@ManyToOne
	private Employee leaderAffirm;
	
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public long getEmployeeId() {
		return employeeId;
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


	public double getRegionalAllowance() {
		return regionalAllowance;
	}


	public void setRegionalAllowance(double regionalAllowance) {
		this.regionalAllowance = regionalAllowance;
	}


	@ManyToOne
	public Employee getManagerAffirm() {
		return managerAffirm;
	}


	public void setManagerAffirm(Employee managerAffirm) {
		this.managerAffirm = managerAffirm;
	}


	@ManyToOne
	public Employee getLeaderAffirm() {
		return leaderAffirm;
	}


	public void setLeaderAffirm(Employee leaderAffirm) {
		this.leaderAffirm = leaderAffirm;
	}


	public String getDeptName() {
		return deptName;
	}


	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}


	public long getOrganizationId() {
		return organizationId;
	}


	public void setOrganizationId(long organizationId) {
		this.organizationId = organizationId;
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


	public double getRestday() {
		return restday;
	}


	public void setRestday(double restday) {
		this.restday = restday;
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


	public int getYears() {
		return years;
	}


	public void setYears(int years) {
		this.years = years;
	}


	public int getMonths() {
		return months;
	}


	public void setMonths(int months) {
		this.months = months;
	}


	public double getWeekdays() {
		return weekdays;
	}


	public void setWeekdays(double weekdays) {
		this.weekdays = weekdays;
	}


	public double getLeaves() {
		return leaves;
	}


	public void setLeaves(double leaves) {
		this.leaves = leaves;
	}


	public double getTakeCareLeave() {
		return takeCareLeave;
	}


	public void setTakeCareLeave(double takeCareLeave) {
		this.takeCareLeave = takeCareLeave;
	}

}