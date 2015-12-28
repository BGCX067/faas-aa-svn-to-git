package com.fortunes.fjdp.admin.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.TemporalType;
import javax.persistence.Temporal;

@Embeddable
public class LoginSession implements Serializable{
	
	private boolean logined;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLoginTime;

	public boolean isLogined() {
		return logined;
	}

	public void setLogined(boolean logined) {
		this.logined = logined;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	
}
