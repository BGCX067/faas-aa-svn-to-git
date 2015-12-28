package com.fortunes.fjdp.admin.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import net.fortunes.core.Model;

import org.jbpm.api.identity.Group;


@Entity
public class Role extends Model implements Group{
	
	public static final String SYSTEM_ROLE = "system";
	public static final String SYSTEM_MANAGER = "SYSMANAGER";//考勤管理员code
	public static final String SYSTEM_BOSSAFFIRM = "BOSSAFFIRM";
	public static final String SYSTEM_GENERAL_STAFF = "GENERAL_STAFF";//普通用户code
	public static final String SYSTEM_OPERATETYPE_GENERAL = "GENERAL";//一般用户角色
	public static final String SYSTEM_OPERATETYPE_LEAVE="LEAVE";//用户角色：请假审批人

	@Id 
	@GeneratedValue
	private long dbId;
	
	private String code;//角色的code

	@Column(nullable=false, unique=true)
	private String name;
	
	private String roleType;//角色类型：有请假审批角色和普通角色
	
	private String operateType;


	@ManyToMany(mappedBy = "roles")
	private List<User> users = new ArrayList<User>();
	
	@ManyToMany
	private List<Privilege> privileges = new ArrayList<Privilege>();
	
	private String description;

    public Role() {
    }
    
    public Role(long dbId) {
    	this.dbId = dbId;
    }
    public Role(long dbId,String code){
    	this.dbId = dbId;
    	this.code = code;
    }
    
    public Role(String name,String roleType,String operateType,String code) {
    	this.name = name;
    	this.code = code;
    	this.roleType = roleType;
    	this.operateType = operateType;
    } 
    
    public Role(String name ,String roleType,String code){
    	this.name = name;
    	this.roleType = roleType;
    	this.code = code;
    }
    
    public Role(String name,String code){
    	this.name = name;
    	this.code = code;
    }
    
    public Role(long id,String name,String code,String roleType){
    	this.dbId = id;
    	this.name = name;
    	this.code = code;
    	this.roleType = roleType;
    }
    
    @Override
    public String toString() {
    	return "角色:"+name;
    }
    
    //================= jbpm4 Group impl ====================
    @Override
	public String getId() {
		return this.name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getType() {
		return this.getRoleType();
	}
	
	//================= setter and getter ====================	
	public void setName(String name) {
		this.name = name;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getRoleType() {
		return roleType;
	}

	public long getDbId() {
		return dbId;
	}

	public void setDbId(long dbId) {
		this.dbId = dbId;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
	
	@Override
	public boolean equals(Object obj) {
		Role role = (Role)obj;
		if(this.dbId ==role.dbId){
			return true;
		}
		return false;
	}
}
