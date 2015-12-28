package com.fortunes.fjdp.admin.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;

import net.fortunes.core.Model;

@Entity
public class Privilege  extends Model {
	
	@Id 
	private String code;
	private String text;
	private boolean leaf;
	private int orderPlace;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Privilege parent;
	
	@OneToMany(mappedBy = "parent")
	@OrderBy("orderPlace")
	private List<Privilege> children = new ArrayList<Privilege>();

	private String description;

    public Privilege() {
    }
    
	public Privilege(String privilegeCode) {
		this.code = privilegeCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setParent(Privilege parent) {
		this.parent = parent;
	}

	public Privilege getParent() {
		return parent;
	}

	public void setChildren(List<Privilege> children) {
		this.children = children;
	}

	public List<Privilege> getChildren() {
		return children;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setOrderPlace(int orderPlace) {
		this.orderPlace = orderPlace;
	}

	public int getOrderPlace() {
		return orderPlace;
	}



}


