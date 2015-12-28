package com.fortunes.faas.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.Employee;

import net.fortunes.core.Model;

@Entity
public class AttendancePlus extends Model{
	
	public enum AttendancePlusType{
		LEAVE,//请假
		SICK_LEAVE,//病假
		ANNUAL_LEAVE,//年假
		HOME_LEAVE,//探亲假
		MATERNITY_LEAVE,//产假
		FAMILYPLANNING_LEAVE,//计划生育假
		TRAVEL,//出差
		TRAIN,//培训
		OVERTIME,//加班
		OTHER
	}
	
	public enum TimeRange{
		ALL_DAY,
		AM,
		PM
	}
	
	@Id @GeneratedValue
	private long id;
	
	@Temporal(TemporalType.DATE)
	private Date date;//发生日期
	
	@Enumerated(EnumType.STRING)
	private TimeRange timeRange = TimeRange.ALL_DAY;//时间范围
	
	@Enumerated(EnumType.STRING)
	private AttendancePlusType type;//增补类型
	
	@ManyToOne
	private Employee employee; 
	
	private Date overTimeStart;
	
	private Date overTimeEnd;
	
	private String reason;
	
	@ManyToOne
	private Dict leaveType;
	
	public AttendancePlus() {
    }
    
    public AttendancePlus(long id) {
    	this.id = id;
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
	
	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setTimeRange(TimeRange timeRange) {
		this.timeRange = timeRange;
	}

	public TimeRange getTimeRange() {
		return timeRange;
	}

	public AttendancePlusType getType() {
		return type;
	}

	public void setType(AttendancePlusType type) {
		this.type = type;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
    public Date getOverTimeStart() {
		return overTimeStart;
	}

	public void setOverTimeStart(Date overTimeStart) {
		this.overTimeStart = overTimeStart;
	}

	public Date getOverTimeEnd() {
		return overTimeEnd;
	}

	public void setOverTimeEnd(Date overTimeEnd) {
		this.overTimeEnd = overTimeEnd;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Dict getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(Dict leaveType) {
		this.leaveType = leaveType;
	}
}
