package com.fortunes.faas.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.dom4j.Branch;

import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;

import net.fortunes.core.Model;

/**
 * @author Neo
 *
 */
/**
 * @author Neo
 *
 */
@Entity
public class BranchShiftAnalysis extends Model {
	
	public enum AnalysisType{
		MORE_IN_WEEKEND,
		LESS_IN_TOTAL,
		EQUAL_IN_WEEKEND,
		BRANCH_IN_TOTAL,
		OTHER_BRANCH_IN_TOTAL,
		REST_MORE_THAN    //休息超过8天
	}
	
	@Id @GeneratedValue
	private long id;
	
	private String yearMonth;
	
	@ManyToOne
	private Organization organization;
	
	private String analysisType;
	
	private int days;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Employee employee;
	
	private int workdays;
	
	private int weekdays;
	
	private String notesDesc;
	
	private int restdays;
	
	private double branchShiftTimeTotal;
	
	public BranchShiftAnalysis() {
	}
	
	public BranchShiftAnalysis(long id) {
		this.id = id;
	}
	
	
	public BranchShiftAnalysis(long id,long employeeId,String employeeName,int workdays,int weekdays,String notesDesc,int restdays) {
		this.id = id;
		this.employee = new Employee(employeeId, "", employeeName);
		this.workdays = workdays;
		this.weekdays = weekdays;
		this.notesDesc = notesDesc;
		this.restdays = restdays;
	}
	
	public BranchShiftAnalysis(long id,long employeeId,String employeeName,int workdays,int weekdays,String notesDesc,int restdays,double branchShiftTimeTotal) {
		this.id = id;
		this.employee = new Employee(employeeId, "", employeeName);
		this.workdays = workdays;
		this.weekdays = weekdays;
		this.notesDesc = notesDesc;
		this.restdays = restdays;
		this.branchShiftTimeTotal = branchShiftTimeTotal;
	}
	
	public BranchShiftAnalysis(long id,long employeeId,String employeeName,int workdays,int weekdays,String notesDesc,double branchShiftTimeTotal) {
		this.id = id;
		this.employee = new Employee(employeeId, "", employeeName);
		this.workdays = workdays;
		this.weekdays = weekdays;
		this.notesDesc = notesDesc;
		this.branchShiftTimeTotal = branchShiftTimeTotal;
	}
	
	public BranchShiftAnalysis(long id,long employeeId,String employeeName,long eOrgId ,String eOrgName,long organizationId,String organizationName,String organizationShortName,int workdays,int weekdays,String notesDesc,int restdays){
		this.id = id;
		this.employee = new Employee(employeeId,"",employeeName,eOrgId,eOrgName);
		this.organization = new Organization(organizationId,organizationName,organizationShortName);
		this.workdays = workdays;
		this.weekdays = weekdays;
		this.notesDesc = notesDesc;
		this.restdays = restdays;
	}
	
	public BranchShiftAnalysis(long id,long employeeId,String employeeName,long eOrgId ,String eOrgName,long organizationId,String organizationName,String organizationShortName,int workdays,int weekdays,String notesDesc,int restdays,double branchShiftTimeTotal){
		this.id = id;
		this.employee = new Employee(employeeId,"",employeeName,eOrgId,eOrgName);
		this.organization = new Organization(organizationId,organizationName,organizationShortName);
		this.workdays = workdays;
		this.weekdays = weekdays;
		this.notesDesc = notesDesc;
		this.restdays = restdays;
		this.branchShiftTimeTotal = branchShiftTimeTotal;
	}
	
	public BranchShiftAnalysis(long id,long employeeId,String employeeName,long eOrgId ,String eOrgName,long organizationId,String organizationName,String organizationShortName,int workdays,int weekdays,String notesDesc){
		this.id = id;
		this.employee = new Employee(employeeId,"",employeeName,eOrgId,eOrgName);
		this.organization = new Organization(organizationId,organizationName,organizationShortName);
		this.workdays = workdays;
		this.weekdays = weekdays;
		this.notesDesc = notesDesc;
	}
	
	public BranchShiftAnalysis(long id,long employeeId,String employeeName,String notesDesc,int restdays){
		this.id = id;
		this.employee = new Employee(employeeId,"",employeeName);
		this.notesDesc = notesDesc;
		this.restdays = restdays;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public String getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(String analysisType) {
		this.analysisType = analysisType;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public int getWorkdays() {
		return workdays;
	}

	public void setWorkdays(int workdays) {
		this.workdays = workdays;
	}

	public int getWeekdays() {
		return weekdays;
	}

	public void setWeekdays(int weekdays) {
		this.weekdays = weekdays;
	}


	public void setNotesDesc(String notesDesc) {
		this.notesDesc = notesDesc;
	}

	public String getNotesDesc() {
		return notesDesc;
	}

	public int getRestdays() {
		return restdays;
	}

	public void setRestdays(int restdays) {
		this.restdays = restdays;
	}

	public double getBranchShiftTimeTotal() {
		return branchShiftTimeTotal;
	}

	public void setBranchShiftTimeTotal(double branchShiftTimeTotal) {
		this.branchShiftTimeTotal = branchShiftTimeTotal;
	}

}
