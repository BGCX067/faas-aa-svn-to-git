package com.fortunes.fjdp.admin.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.model.Privilege;
import com.fortunes.fjdp.admin.model.Role;

import net.fortunes.core.log.annotation.LoggerClass;
import net.fortunes.core.service.GenericService;

@Component @LoggerClass
public class RoleService extends GenericService<Role> {
	
	public void updatePrivileges(String roleId,String[] privilegeCodes){
		Role role = this.get(roleId);
		role.getPrivileges().clear();
		for(String privilegeCode : privilegeCodes){
			role.getPrivileges().add(new Privilege(privilegeCode));
		}
	}
	@SuppressWarnings("unchecked")
	public Role getRole(String roleName){
		String selectByName = "select r from Role r where r.name = ?";
		List<Role> roles = this.getDefDao().findByQueryString(selectByName, roleName);
		return null!=roles&&roles.size()>0?roles.get(0):new Role(roleName,Role.SYSTEM_ROLE);
	}
	
	public Role getRoleByCode(String roleCode){
		String hql = "select new Role(r.id,r.name,r.code,r.roleType) from Role as r where r.code = ?";
		List<Role> roles = this.getDefDao().findByQueryString(hql, roleCode);
		return roles.size()>0?roles.get(0):new Role();
	}
}
