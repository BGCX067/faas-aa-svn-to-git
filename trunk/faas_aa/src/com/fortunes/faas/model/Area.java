package com.fortunes.faas.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import net.fortunes.core.Model;

import com.fortunes.fjdp.admin.model.Employee;
@Entity
public class Area extends Model{
	
	@Id @GeneratedValue
	private long id;
	
	private String name;
	
	private String address;
	
	@ManyToOne
	private Area parent;
	
	@OneToMany(mappedBy = "parent")
	private List<Area> children = new ArrayList<Area>();
	
	private boolean leaf = true;
	
	@OneToMany(mappedBy = "area",fetch = FetchType.EAGER)
	private List<Device> devices = new ArrayList<Device>();
	
	@ManyToMany(mappedBy = "areas")
	private List<Employee> employees = new ArrayList<Employee>();
	
	public Area(){
		
	}
	
	public Area(long id){
		this.id = id;
	}

	public Area(Area parent,String name){
		this.parent = parent;
		this.name = name;
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

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	public Area getParent() {
		return parent;
	}

	public void setParent(Area parent) {
		this.parent = parent;
	}

	public List<Area> getChildren() {
		return children;
	}

	public void setChildren(List<Area> children) {
		this.children = children;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	
}
