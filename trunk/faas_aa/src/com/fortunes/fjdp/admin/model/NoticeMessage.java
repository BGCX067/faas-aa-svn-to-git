package com.fortunes.fjdp.admin.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class NoticeMessage extends UserMessage {
	
	@ManyToOne
	private Notice notice;

	public NoticeMessage() {
	}

	public Notice getNotice() {
		return notice;
	}

	public void setNotice(Notice notice) {
		this.notice = notice;
	}
	
	
	
}
