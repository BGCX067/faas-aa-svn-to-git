package com.fortunes.faas.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fortunes.fjdp.admin.model.Employee;

import net.fortunes.core.Model;

@Entity
public class AttendanceLog extends Model{
	//门禁系统数据
	public static final String RECORD_SOURCE_ACCESS = "access_control";
	
	
	@Id @GeneratedValue
	private long id;
	
	@Transient
	private String employeeCode;//员工编号

	@Column @Temporal(TemporalType.DATE)
	private Date attDate;//考勤日期
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date checkInTime;//刷卡时间
	
	@Transient
	private Date checkOutTime;
	
	//考勤记录的来源，如果为空则是考勤系统中数据，如果为access_control则为门禁系统数据
	private String recordSource;

	@ManyToOne
	private Employee employee;//对应的员工
	
	@ManyToOne
	private Employee plusCardMan;//补卡批准人
	
	private String reason;
	
	private String pushSite;//员工打卡地点
	
    public AttendanceLog() {
    }
    
    public AttendanceLog(long id) {
    	this.id = id;
    }
    
    public AttendanceLog(String cardNo,String employeeName,Date checkInTime,Date checkOutTime){
//    	this.cardNo = cardNo;
//    	this.employeeName = employeeName;
    	this.checkInTime = checkInTime;
    	this.checkOutTime = checkOutTime;
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
	
//	public void setCardNo(String cardNo) {
//		this.cardNo = cardNo;
//	}
//
//	public String getCardNo() {
//		return cardNo;
//	}
//	public void setEmployeeName(String employeeName) {
//		this.employeeName = employeeName;
//	}
//
//	public String getEmployeeName() {
//		return employeeName;
//	}
//	
	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public Date getAttDate() {
		return attDate;
	}

	public void setAttDate(Date attDate) {
		this.attDate = attDate;
	}

	public void setCheckInTime(Date checkInTime) {
		this.checkInTime = checkInTime;
	}

	public Date getCheckInTime() {
		return checkInTime;
	}

	public Date getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(Date checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void setPlusCardMan(Employee plusCardMan) {
		this.plusCardMan = plusCardMan;
	}

	public Employee getPlusCardMan() {
		return plusCardMan;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}
	public String getRecordSource() {
		return recordSource;
	}

	public void setRecordSource(String recordSource) {
		this.recordSource = recordSource;
	}

	public String getPushSite() {
		return pushSite;
	}

	public void setPushSite(String pushSite) {
		this.pushSite = pushSite;
	}

}
