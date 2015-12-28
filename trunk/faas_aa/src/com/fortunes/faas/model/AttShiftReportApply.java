package com.fortunes.faas.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;

import net.fortunes.core.Model;
@Entity
public class AttShiftReportApply extends Model {
	@Id@GeneratedValue
	private long id;
	@ManyToOne
	private Organization organization;
	
	private String yearMonth;
	
	@ManyToOne
	private Employee managerAffirm;
	
	@ManyToOne
	private Employee leaderAffirm;

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

	public String getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}

	public Employee getManagerAffirm() {
		return managerAffirm;
	}

	public void setManagerAffirm(Employee managerAffirm) {
		this.managerAffirm = managerAffirm;
	}

	public Employee getLeaderAffirm() {
		return leaderAffirm;
	}

	public void setLeaderAffirm(Employee leaderAffirm) {
		this.leaderAffirm = leaderAffirm;
	}
	
	
	
}
