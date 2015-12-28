package com.fortunes.faas.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fortunes.fjdp.admin.model.Employee;

import net.fortunes.core.Model;

@Entity
public class MeetOrTrain extends Model{
	@Id @GeneratedValue
	private long id;
	
	private String name;
	
	private String address;
	
	private String theme;
	@Column @Temporal(TemporalType.DATE)
	private Date startDate;
	@Column @Temporal(TemporalType.DATE)
	private Date endDate;

	private String organizationCode;//会议或培训主办
	
	private String orgName;

	private boolean isVerify=false;//领导是否确认true为确认，false为没确认
	
	@ManyToOne
	private Employee departmentVerify;//部门确认人
	
	@ManyToOne
	private Employee personnelVerify;//人事部确认人
	
	@ManyToOne
	private Employee currentVerify;//当前确认人

	@OneToMany(mappedBy = "meetOrTrain")
	private List<MoveAttInfo> mouceAttInfos=new ArrayList<MoveAttInfo>();

	public List<MoveAttInfo> getMouceAttInfos() {
		return mouceAttInfos;
	}

	public void setMouceAttInfos(List<MoveAttInfo> mouceAttInfos) {
		this.mouceAttInfos = mouceAttInfos;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}
	
	public boolean isVerify() {
		return isVerify;
	}

	public void setVerify(boolean isVerify) {
		this.isVerify = isVerify;
	}
	
	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public Employee getDepartmentVerify() {
		return departmentVerify;
	}

	public void setDepartmentVerify(Employee departmentVerify) {
		this.departmentVerify = departmentVerify;
	}

	public Employee getPersonnelVerify() {
		return personnelVerify;
	}

	public void setPersonnelVerify(Employee personnelVerify) {
		this.personnelVerify = personnelVerify;
	}

	public Employee getCurrentVerify() {
		return currentVerify;
	}

	public void setCurrentVerify(Employee currentVerify) {
		this.currentVerify = currentVerify;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

}
