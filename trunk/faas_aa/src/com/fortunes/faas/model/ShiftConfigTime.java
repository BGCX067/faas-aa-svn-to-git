package com.fortunes.faas.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.fortunes.core.Model;

@Entity
public class ShiftConfigTime extends Model{
	@Id 
	@GeneratedValue
	private long id; //序列号
	
	
	private String teamName;//技能组别名
	
	
	private String shiftConfigType;//班次
	
	@Column @Temporal(TemporalType.TIME)
	private Date startWorkTime;
	
	@Column @Temporal(TemporalType.TIME)
	private Date endWorkTime;
	
	private boolean isNextDay;
	
	public ShiftConfigTime(){
		
	}

	public ShiftConfigTime(long id, String teamName, String shiftConfigType) {
		this.id = id;
		this.teamName = teamName;
		this.shiftConfigType = shiftConfigType;
	}
	
	public ShiftConfigTime(long id, String teamName, String shiftConfigType,
			Date startWorkTime, Date endWorkTime, boolean isNextDay) {
		super();
		this.id = id;
		this.teamName = teamName;
		this.shiftConfigType = shiftConfigType;
		this.startWorkTime = startWorkTime;
		this.endWorkTime = endWorkTime;
		this.isNextDay = isNextDay;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getShiftConfigType() {
		return shiftConfigType;
	}

	public void setShiftConfigType(String shiftConfigType) {
		this.shiftConfigType = shiftConfigType;
	}

	public Date getStartWorkTime() {
		return startWorkTime;
	}

	public void setStartWorkTime(Date startWorkTime) {
		this.startWorkTime = startWorkTime;
	}

	public Date getEndWorkTime() {
		return endWorkTime;
	}

	public void setEndWorkTime(Date endWorkTime) {
		this.endWorkTime = endWorkTime;
	}

	public boolean isNextDay() {
		return isNextDay;
	}

	public void setNextDay(boolean isNextDay) {
		this.isNextDay = isNextDay;
	}
	
	
}
