package com.fortunes.faas.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.fortunes.core.Model;
@Entity
public class OperateCmd extends Model {
	
	@Id @GeneratedValue
	private long id;
	
	private String content;
	
	private String operateType;
	
	private boolean isUpload;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date operateDate;
	
	@ManyToOne
	private Device device;
	
	public OperateCmd(){}
	
	public OperateCmd(long id,String content,String operateType){
		this.id = id;
		this.content = content;
		this.operateType = operateType;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public boolean isUpload() {
		return isUpload;
	}

	public void setUpload(boolean isUpload) {
		this.isUpload = isUpload;
	}

	public Date getOperateDate() {
		return operateDate;
	}

	public void setOperateDate(Date operateDate) {
		this.operateDate = operateDate;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}
	
}
