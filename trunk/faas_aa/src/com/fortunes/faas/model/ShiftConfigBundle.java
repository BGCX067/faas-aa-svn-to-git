package com.fortunes.faas.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fortunes.fjdp.admin.model.Organization;

import net.fortunes.core.Model;

@Entity
public class ShiftConfigBundle extends Model{
	
	@Id @GeneratedValue
	private long id;
	
	private String name;
	
	private int day;//1-7
	
	private boolean nextBundle = false;
	
	@ManyToOne
	private Organization organization;
	
	public ShiftConfigBundle() {
    }
    
    public ShiftConfigBundle(long id) {
    	this.id = id;
    }
    
    @Override
	public String toString() {
		return "";
	}
    
    public boolean isWeekend(){
    	return this.day == 6 || this.day == 7;
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

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getDay() {
		return day;
	}

	public void setNextBundle(boolean nextBundle) {
		this.nextBundle = nextBundle;
	}

	public boolean isNextBundle() {
		return nextBundle;
	}
	

}
