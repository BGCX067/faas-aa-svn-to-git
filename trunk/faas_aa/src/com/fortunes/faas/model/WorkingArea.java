package com.fortunes.faas.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Organization;

import net.fortunes.core.Model;

@Entity
public class WorkingArea extends Model{
	
	@Id @GeneratedValue
	private long id;
	
	private String name;
	
	@ManyToOne
	private WorkingArea parent;
	
	@OneToMany(mappedBy = "parent")
	private List<WorkingArea> children = new ArrayList<WorkingArea>();
	
	private boolean leaf = true;
	
	private boolean template = false;
	
	private String code;
	
	@ManyToOne
	private Organization org;


	public WorkingArea(){
		
	}
	
	public WorkingArea(long id){
		this.id = id;
	}

	public WorkingArea(WorkingArea parent,String name){
		this.parent = parent;
		this.name = name;
	}
	
	public WorkingArea(WorkingArea parent,String name,String code){
		this.parent = parent;
		this.name = name;
		this.code = code;
	}
	public WorkingArea (long id,String name,boolean leaf,boolean template,String code){
		this.id = id;
		this.name = name;
		this.leaf = leaf;
		this.template = template;
		this.code = code;
	}
	public WorkingArea(long id ,String name ,long parentId){
		this.id = id;
		this.name = name;
		this.parent = AdminHelper.toWorkingArea(Long.toString(parentId));
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

	public WorkingArea getParent() {
		return parent;
	}

	public void setParent(WorkingArea parent) {
		this.parent = parent;
	}

	public List<WorkingArea> getChildren() {
		return children;
	}

	public void setChildren(List<WorkingArea> children) {
		this.children = children;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public void setTemplate(boolean template) {
		this.template = template;
	}

	public boolean isTemplate() {
		return template;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Organization getOrg() {
		return org;
	}

	public void setOrg(Organization org) {
		this.org = org;
	}
}
