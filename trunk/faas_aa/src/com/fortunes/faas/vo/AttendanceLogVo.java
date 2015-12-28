package com.fortunes.faas.vo;

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

public class AttendanceLogVo extends Model{
	private int id;
	private Date attDate;
	private Date CheckInTime;
	private String desc3;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getDesc3() {
		return desc3;
	}
	public Date getAttDate() {
		return attDate;
	}
	public void setAttDate(Date attDate) {
		this.attDate = attDate;
	}
	public Date getCheckInTime() {
		return CheckInTime;
	}
	public void setCheckInTime(Date checkInTime) {
		CheckInTime = checkInTime;
	}
	public void setDesc3(String desc3) {
		this.desc3 = desc3;
	}
}
