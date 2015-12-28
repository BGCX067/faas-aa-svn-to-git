package com.fortunes.fjdp.admin.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.User;

import net.fortunes.core.Model;

@Entity
public class Notice extends Model {
	
	@Id @GeneratedValue
	private long id;
	
	@Column @Temporal(TemporalType.TIMESTAMP)
	private Date createDateTime;
	
	@ManyToOne
	private User creator;
	
	@Column(length = 2000)
	private String contents;
	
	@ManyToOne
	private Dict level;
	
	public Notice() {
	}
	
	public Notice(long id){
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public void setLevel(Dict level) {
		this.level = level;
	}

	public Dict getLevel() {
		return level;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public User getCreator() {
		return creator;
	}
	
}
