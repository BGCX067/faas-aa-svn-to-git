package com.fortunes.faas.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fortunes.fjdp.admin.model.Organization;

@Entity
public class DefaultTime {
	
	@Id@GeneratedValue
	private long id;
	@OneToOne
	private Organization organization;
	@Temporal(TemporalType.TIME)
	private Date startTime;
	@Temporal(TemporalType.TIME)
	private Date endTime;
	
	private double bunchBreakTime = 1;
	public DefaultTime(){}
	
	public DefaultTime(Organization organization,Date startTime,Date endTime){
		this.organization = organization;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public DefaultTime(Organization organization,Date startTime,Date endTime,double bunchBreakTime){
		this.organization = organization;
		this.startTime = startTime;
		this.endTime = endTime;
		this.bunchBreakTime = bunchBreakTime;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
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
