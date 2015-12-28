package com.fortunes.fjdp.admin.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

import com.fortunes.fjdp.admin.model.User;

import net.fortunes.core.Model;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
public class UserMessage extends Model {
	
	@Id @GeneratedValue
	private long id;
	
	@ManyToOne
	private User user;
	
	private boolean readed;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isReaded() {
		return readed;
	}

	public void setReaded(boolean readed) {
		this.readed = readed;
	}

}
