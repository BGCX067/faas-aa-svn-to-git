package com.fortunes.faas.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;

import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;

import net.fortunes.core.Model;

@Entity
public class ShiftConfig extends Model{
	
	@Id @GeneratedValue
	private long id;
	
	@Temporal(TemporalType.DATE)
	private Date date;//日期
	
	private boolean weekEnd;//是否周末
	
	@Temporal(TemporalType.TIME)
	private Date startTime;//上班时间
	
	@Temporal(TemporalType.TIME)
	private Date endTime;//下班时间
	
	private boolean nextDay = false;//到次日
	
	@ManyToOne
	private Employee employee;//对应的员工
	
	@ManyToOne
	private Organization configOrganization;//员工该天排班所在的部门，网点排班时可能跨部门调配员工
	
	@ManyToOne
	private WorkingArea workingArea;
	
	private boolean isCancel=false;//是否是取消该天的排版默认是否
	
	private double bunchBreakTime = 1;

	public boolean isCancel() {
		return isCancel;
	}

	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	public ShiftConfig() {
    }
    
    public ShiftConfig(long id) {
    	this.id = id;
    }
    
    public ShiftConfig(Long id,Date date,boolean weekEnd,Date startTime,Date endTime,boolean nextDay,boolean isCancel
    		,Long employeeid,String employeeCode,Organization configOrganization,WorkingArea workingArea){
    	this.id = id;
    	this.date = date;
    	this.weekEnd = weekEnd;
    	this.startTime = startTime;
    	this.endTime = endTime;
    	this.nextDay = nextDay;
    	this.employee = new Employee(employeeid,employeeCode);
//    	if(employeeid!=null&&employeeid!=0)
//    	this.employee = AdminHelper.toEmployee(employeeid+"");
//    	else
//    	this.employee = null;
    	this.configOrganization = configOrganization;
//    	if(configOrganizationid!=null&&configOrganizationid!=0)
//    	this.configOrganization = AdminHelper.toOrganization(configOrganizationid+"");
//    	else
//    	this.configOrganization= null;
//    	if(workingAreaid!=null&&workingAreaid!=0)
//    	this.workingArea = AdminHelper.toWorkingArea(workingAreaid+"");
//    	else
//    	this.workingArea = null;
    	this.workingArea = workingArea;
    	this.isCancel = isCancel;
    }
    
    public ShiftConfig(Long id,Date date,boolean weekEnd,Date startTime,Date endTime,boolean nextDay,
    		Long employeeid,Long configOrganizationid,Long workingAreaid,boolean isCancel){
    	this.id = id;
    	this.date = date;
    	this.weekEnd = weekEnd;
    	this.startTime = startTime;
    	this.endTime = endTime;
    	this.nextDay = nextDay;
    	if(employeeid!=null&&employeeid!=0)
    	this.employee = AdminHelper.toEmployee(employeeid+"");
    	else
    	this.employee = null;
    	if(configOrganizationid!=null&&configOrganizationid!=0)
    	this.configOrganization = AdminHelper.toOrganization(configOrganizationid+"");
    	else
    	this.configOrganization= null;
    	if(workingAreaid!=null&&workingAreaid!=0)
    	this.workingArea = AdminHelper.toWorkingArea(workingAreaid+"");
    	else
    	this.workingArea = null;
    	this.isCancel = isCancel;
    }
    public ShiftConfig(Long id,Date date,boolean weekEnd,Date startTime,Date endTime,boolean nextDay,boolean isCancel,
    		long employeeId,String employeeCode,String employeeName,String sexId,String sexText,Date birthDay,String positionId,
    		String positionText,long workingAreaId){
    	this.id = id;
    	this.date = date;
    	this.weekEnd = weekEnd;
    	this.startTime = startTime;
    	this.endTime = endTime;
    	this.nextDay = nextDay;
    	this.isCancel = isCancel;
    	if(StringUtils.isNotEmpty(employeeName)&&employeeId!=0)
        	this.employee = new Employee(employeeId,employeeCode,employeeName,sexId,sexText,positionId,positionText,birthDay);
    	else
        	this.employee = null;
    	this.workingArea = AdminHelper.toWorkingArea(Long.toString(workingAreaId));
    }
    @Override
	public String toString() {
		return "";
	}
    
    /*=============== setter and getter =================*/
    
    public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public boolean isNextDay() {
		return nextDay;
	}

	public void setNextDay(boolean nextDay) {
		this.nextDay = nextDay;
	}

	public Employee getEmployee() {
		return employee;
	}

	public Organization getConfigOrganization() {
		return configOrganization;
	}

	public void setConfigOrganization(Organization configOrganization) {
		this.configOrganization = configOrganization;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void setWorkingArea(WorkingArea workingArea) {
		this.workingArea = workingArea;
	}

	public WorkingArea getWorkingArea() {
		return workingArea;
	}

	public void setWeekEnd(boolean weekEnd) {
		this.weekEnd = weekEnd;
	}

	public boolean isWeekEnd() {
		return weekEnd;
	}

	public double getBunchBreakTime() {
		return bunchBreakTime;
	}

	public void setBunchBreakTime(double bunchBreakTime) {
		this.bunchBreakTime = bunchBreakTime;
	}
	
	

}
