package com.fortunes.faas.model;

import java.util.Date;

import javax.persistence.Column;
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
@SuppressWarnings("serial")
public class AttShift extends Model{
	
	@Id @GeneratedValue
	private long id;
	private int year;
	private int month;
	@Column @Temporal(TemporalType.DATE)
	private Date attDate;//考勤日期
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date checkInTime;//上班签到时间
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date checkOutTime;//上班签退时间
	
	@Temporal(TemporalType.TIME)
	private Date startTime;//开始考勤时间
	
	@Temporal(TemporalType.TIME)
	private Date endTime;//结束考勤时间
	
	private double late = 0;//迟到
	private double early = 0;//早退
	private double lateTime = 0;//迟到时间
	private double earlyTime = 0;//早退时间
	private double absent = 0;//是否矿工
	private String statusInfo;//记录详细的考勤计算信息
	
	private double weekday = 0;//平日

	private double restday = 0;//休息日
	private double holiday = 0;//节假日
//	@ManyToOne
	private String holidayType;
	
	private String exception;//例外情况
	private double exceptionTime = 0;//例外时间
	private double overTime = 0;//加班时间
	private boolean isAffirm = false;//考勤信息是否确认，true为确认，false为未确认
	@ManyToOne
	private Employee employee;//对应的员工
	
//	public AttShift(long id,long employeeId,String employeeName,int restday){
//		this.id = id;
//		this.employee = new Employee(employeeId, "", employeeName);
//		this.restday = restday;
//	}
	
//	public AttShift(long id,Employee employee,int restday){
//		this.id = id;
//		this.employee = employee;
//		this.restday = restday;
//	}
	
	public AttShift(){}
	public AttShift(long id){
		this.id = id;
	}
	
	public AttShift(long id, int year, int month, Date attDate,
			Date checkInTime, Date checkOutTime, Date startTime, Date endTime,
			double late, double early, double lateTime, double earlyTime,
			double absent, String statusInfo, double weekday, double restday,
			double holiday, String holidayType, String exception,
			double exceptionTime, double overTime, boolean isAffirm,
			Employee employee) {
		super();
		this.id = id;
		this.year = year;
		this.month = month;
		this.attDate = attDate;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
		this.startTime = startTime;
		this.endTime = endTime;
		this.late = late;
		this.early = early;
		this.lateTime = lateTime;
		this.earlyTime = earlyTime;
		this.absent = absent;
		this.statusInfo = statusInfo;
		this.weekday = weekday;
		this.restday = restday;
		this.holiday = holiday;
		this.holidayType = holidayType;
		this.exception = exception;
		this.exceptionTime = exceptionTime;
		this.overTime = overTime;
		this.isAffirm = isAffirm;
		this.employee = employee;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public Date getAttDate() {
		return attDate;
	}
	public void setAttDate(Date attDate) {
		this.attDate = attDate;
	}
	public Date getCheckInTime() {
		return checkInTime;
	}
	public void setCheckInTime(Date checkInTime) {
		this.checkInTime = checkInTime;
	}
	public Date getCheckOutTime() {
		return checkOutTime;
	}
	public void setCheckOutTime(Date checkOutTime) {
		this.checkOutTime = checkOutTime;
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
	public double getLate() {
		return late;
	}
	public void setLate(double late) {
		this.late = late;
	}
	public double getEarly() {
		return early;
	}
	public void setEarly(double early) {
		this.early = early;
	}
	
	public double getLateTime() {
		return lateTime;
	}
	public void setLateTime(double lateTime) {
		this.lateTime = lateTime;
	}
	public double getEarlyTime() {
		return earlyTime;
	}
	public void setEarlyTime(double earlyTime) {
		this.earlyTime = earlyTime;
	}
	public double getAbsent() {
		return absent;
	}
	public void setAbsent(double absent) {
		this.absent = absent;
	}
	
	public String getStatusInfo() {
		return statusInfo;
	}
	public void setStatusInfo(String statusInfo) {
		this.statusInfo = statusInfo;
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
	public double getHoliday() {
		return holiday;
	}
	public void setHoliday(double holiday) {
		this.holiday = holiday;
	}
	public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
	public double getOverTime() {
		return overTime;
	}
	public void setOverTime(double overTime) {
		this.overTime = overTime;
	}
	public double getExceptionTime() {
		return exceptionTime;
	}
	public void setExceptionTime(double exceptionTime) {
		this.exceptionTime = exceptionTime;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public String getHolidayType() {
		return holidayType;
	}
	public void setHolidayType(String holidayType) {
		this.holidayType = holidayType;
	}
	public boolean isAffirm() {
		return isAffirm;
	}
	public void setAffirm(boolean isAffirm) {
		this.isAffirm = isAffirm;
	}
}
