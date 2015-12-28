package com.fortunes.faas.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fortunes.fjdp.admin.model.Organization;

import net.fortunes.core.Model;

@Entity
public class ShiftConfigApply extends Model {
	
	@Id @GeneratedValue
	private long id;
	
	@ManyToOne
	private Organization organization;
	
	private String yearMonth;//2010-12
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Organization getOrganization() {
		return organization;
	}

}
