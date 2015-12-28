package com.fortunes.faas.vo;

import java.util.Date;

import javax.persistence.Entity;

import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.Organization;

import net.fortunes.core.Model;

@SuppressWarnings("serial")
public class ShiftConfigVo extends Model {
	private long id;
	private Date date;//日期
	private boolean weekEnd;//是否周末
	private Date startTime;//上班时间
	private Date endTime;//下班时间
	private boolean nextDay = false;//到次日
	private long employeeId;
	private String employeeCode;//对应的员工
	private String employeeName;
	private long organizationId;
	private String organizationName;
	private String sex;
	private Date birthDay;
	private String position;
	private Organization configOrganization;
	private Long workingAreaId;
	private String workingAreaName;
	private boolean isCancel=false;//是否是取消该天的排版默认是否
	
	public ShiftConfigVo(){}
	
	public ShiftConfigVo(long id,Date date,boolean weekEnd,Date startTime,Date endTime,boolean nextDay,long employeeId,
			String employeeCode,String employeeName,String position,long organizationId,String organizationName,Date birthDay,Organization configOrganization,String sex,boolean isCancel){
		this.id = id;
		this.date = date;
		this.weekEnd = weekEnd;
		this.startTime = startTime;
		this.endTime = endTime;
		this.nextDay= nextDay;
		this.employeeId = employeeId;
		this.employeeCode = employeeCode;
		this.employeeName = employeeName;
		this.organizationId = organizationId;
		this.organizationName = organizationName;
		this.birthDay = birthDay;
		this.configOrganization = configOrganization;
		this.isCancel = isCancel;
		this.position = position;
		this.sex = sex;
		
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public boolean isWeekEnd() {
		return weekEnd;
	}
	public void setWeekEnd(boolean weekEnd) {
		this.weekEnd = weekEnd;
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
	
	public Organization getConfigOrganization() {
		return configOrganization;
	}

	public void setConfigOrganization(Organization configOrganization) {
		this.configOrganization = configOrganization;
	}

	public Long getWorkingAreaId() {
		return workingAreaId;
	}
	public void setWorkingAreaId(Long workingAreaId) {
		this.workingAreaId = workingAreaId;
	}
	public String getWorkingAreaName() {
		return workingAreaName;
	}
	public void setWorkingAreaName(String workingAreaName) {
		this.workingAreaName = workingAreaName;
	}
	public boolean isCancel() {
		return isCancel;
	}
	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}
	public Date getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	public long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(long organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
	
}
