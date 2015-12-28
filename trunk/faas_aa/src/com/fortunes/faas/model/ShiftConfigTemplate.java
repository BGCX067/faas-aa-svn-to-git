package com.fortunes.faas.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;

import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.Employee;

import net.fortunes.core.Model;

@Entity
public class ShiftConfigTemplate extends Model {
	
	@Id @GeneratedValue
	private long id;
	
	@ManyToOne
	private Employee employee;//对应的员工
	
	@ManyToOne
	private WorkingArea workingArea;
	
	@Temporal(TemporalType.TIME)
	private Date startTime;//上班时间
	
	@Temporal(TemporalType.TIME)
	private Date endTime;//下班时间
	
	@ManyToOne
	private ShiftConfigBundle shiftConfigBundle;
	
	private double bunchBreakTime = 1;
	
	public ShiftConfigTemplate() {
    }
    
    public ShiftConfigTemplate(long id) {
    	this.id = id;
    }
    public ShiftConfigTemplate(long id,Date startTime,Date endTime,
    		long employeeId,String employeeCode,String employeeName,String sexId,String sexText,String positionId,String positionText,Date birthDay,long workingAreaId){
    	this.id = id;
    	this.startTime = startTime;
    	this.endTime = endTime;
    	Dict position = null;
    	if(StringUtils.isNotEmpty(positionId)&&StringUtils.isNotEmpty(positionText)){
    		position = new Dict(positionId,positionText);
    	}
    	this.employee = new Employee(employeeId,employeeCode,employeeName, new Dict(sexId,sexText),position,birthDay );
    	this.workingArea = AdminHelper.toWorkingArea(Long.toString(workingAreaId));
    }
    
    @Override
	public String toString() {
		return "";
	}
    
    
    /*=============== setter and getter =================*/
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public WorkingArea getWorkingArea() {
		return workingArea;
	}

	public void setWorkingArea(WorkingArea workingArea) {
		this.workingArea = workingArea;
	}

	public void setShiftConfigBundle(ShiftConfigBundle shiftConfigBundle) {
		this.shiftConfigBundle = shiftConfigBundle;
	}

	public ShiftConfigBundle getShiftConfigBundle() {
		return shiftConfigBundle;
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

	public double getBunchBreakTime() {
		return bunchBreakTime;
	}

	public void setBunchBreakTime(double bunchBreakTime) {
		this.bunchBreakTime = bunchBreakTime;
	}

}
