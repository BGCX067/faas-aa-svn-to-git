package com.fortunes.fjdp.admin.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import sun.util.logging.resources.logging;

import net.fortunes.core.Model;


@Entity
public class Log extends Model{
	
	@Id 
	@GeneratedValue
	private long id;
	private String opType;
	private String opUser;	
	
	@Column @Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	
	private String contents;
	
	public Log(){}
	
	public Log(String opUser,String opType,String contents,Date createTime){
		this.opType = opType;
		this.opUser = opUser;
		this.contents = contents;
		this.createTime = createTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOpType() {
		return opType;
	}

	public void setOpType(String opType) {
		this.opType = opType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public void setOpUser(String opUser) {
		this.opUser = opUser;
	}

	public String getOpUser() {
		return opUser;
	}
	

}