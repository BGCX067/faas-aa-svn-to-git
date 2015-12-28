package com.fortunes.faas.vo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import net.fortunes.core.Model;

@Entity
public class MoveAttInfoVO extends Model{

	private long id;
	
	private String name;
	
	private String code;
	
	private Date attDate;
	
	private String organization;
	
	private boolean isAttPlus;//是否是补刷的
	
	private String plusInfo;//补刷原因
	
	private boolean isVerify;//领导是否确认
	
	private String status;

	private long meId;
	
	public MoveAttInfoVO(){}
	
	public long getMeId() {
		return meId;
	}
	public void setMeId(long meId) {
		this.meId = meId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Date getAttDate() {
		return attDate;
	}
	public void setAttDate(Date attDate) {
		this.attDate = attDate;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getPlusInfo() {
		return plusInfo;
	}
	public void setPlusInfo(String plusInfo) {
		this.plusInfo = plusInfo;
	}
	public boolean getAttPlus() {
		return isAttPlus;
	}
	public void setAttPlus(boolean isAttPlus) {
		this.isAttPlus = isAttPlus;
	}
	public boolean getVerify() {
		return isVerify;
	}
	public void setVerify(boolean isVerify) {
		this.isVerify = isVerify;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
