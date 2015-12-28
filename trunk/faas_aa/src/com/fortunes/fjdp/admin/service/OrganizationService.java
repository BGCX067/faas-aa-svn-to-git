package com.fortunes.fjdp.admin.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.fortunes.core.log.annotation.LoggerClass;
import net.fortunes.core.service.GenericService;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;

@Component
@LoggerClass
public class OrganizationService extends GenericService<Organization> {
	
	public static final boolean SINGLE_ORGANIZATION = true;
	
	@Resource private EmployeeService employeeService;
	
	
	@Override
	public Organization add(Organization entity) throws Exception {
		
		super.add(entity);
		if(entity.getParent() != null)
			entity.getParent().setLeaf(false);
		return entity;
	}
	
	public List<Organization> getOrganizationsByOrganizations(String organizations){
		String hql = "select org from Organization org where org.id in("+organizations+")";
		System.out.println(organizations);
		List<Organization> list = this.getDefDao().findByQueryString(hql);
		System.out.println();
		return list;
	}
	
	@Override
	public void del(Organization entity) throws Exception {
		Organization parent = entity.getParent();
		super.del(entity);
		if(parent != null && parent.getChildren().size() <= 0)
			parent.setLeaf(true);
	}
	
	public void addEmployee(String organizationId,String employeeId){
		Organization organization = this.get(organizationId);
		Employee employee = employeeService.get(employeeId);
		//在组织中加入员工
		organization.getEmployees().add(employee);
	}
	
	public void removeEmployee(String organizationId,String employeeId){
		Organization organization = this.get(organizationId);
		Employee employee = employeeService.get(employeeId);
		
		//从组织中移除员工
		organization.getEmployees().remove(employee);
		this.getDefDao().getHibernateTemplate().flush();
	}

	/**
	 * 查找不属于某一组织的员工
	 * @param organizationId
	 * @return
	 */
	public List<Employee> getUnassignEmployeesByOrganizationId(String organizationId) {
		return getDefDao().findByQueryString(
				" select e from Employee as e left join fetch e.user where e not in " +
				" (select oe from Organization as o join o.employees as oe where o.id =  ?)", 
				Long.parseLong(organizationId));
	}
	public Organization getOrganizationByCode(String code){
		List<Organization> organizations = getDefDao().findByQueryString("select o from Organization as o where o.code = ?", code);
		if(organizations.size()>0){
			return organizations.get(0);
		}
		return null;
	}
	public Organization getOrganizationByName(String name){
		List<Organization> orgList = getDefDao().findByQueryString("select o from Organization as o where o.name = ?", name);
		if(orgList.size()>0){
			return orgList.get(0);
		}
		return null;
	}
	
	public List<Organization> getOrganizationByType(String type){
		return getDefDao().findByQueryString("select o from Organization as o where o.type.id=?",type);
	}
	
	public List<Organization> getOrganizationByTypeForBranch(String type){
		return getDefDao().findByQueryString("select o from Organization as o where o.type.id=? and o.parent.id = 1",type);
	}
	
	//添加一个导入组织机构列表的东西
	public void bathImportOrganization(List<Organization> list) throws Exception{
		for(Organization org:list){
			if(org.getId() ==1){
				continue;
			}
			add(org);
		}
	}
	
	public Organization getOrganizationById(String orgId){
		String hql = "select new Organization(o.id,o.code,o.name,o.shortName) from Organization o where o.id ='"+orgId+"'";
		List<Organization> list = this.getDefDao().findByQueryString(hql);
		return list.size()>0?list.get(0):null;
	}
	/**
	 * 查找不属于任何组织的员工(所有未分配的员工)
	 * @return
	 */
	public List<Employee> getUnassignEmployees() {
		return getDefDao().findByQueryString(
				" select e from Employee as e left join fetch e.organization as o where o is null" );
	}
    @SuppressWarnings("unchecked")
	public List<Organization> listTree(String id){
    	List<Organization> list = getDefDao().findByQueryString("select new com.fortunes.fjdp.admin.model.Organization(org.id,org.code,org.name,org.shortName,org.fullName,org.parent,org.address,org.leaf) from Organization as org where org.parent.id = ?", Long.parseLong(id));
    	return list;
    }
    
    public List<Organization> getSimpleOrg(String id){
    	List<Organization> list = getDefDao().findByQueryString("select new com.fortunes.fjdp.admin.model.Organization(org.id,org.code,org.name,org.shortName,org.fullName,org.address,org.leaf) from Organization as org where org.parent.id ="+id);
    	return list;
    }
    
    public void delOrganizationFirstStep(String id){
    	JdbcTemplate jdbcTemplate = this.getDefDao().getJdbcTemplate();
    	String sql = "delete Tuser_organization where organizations_id = '"+id+"'";
    	jdbcTemplate.execute(sql);
    }
    
    public String getFirstOrganization(String name){
//    	Employee e = employeeService.getEmployee(code);
    	Organization org = this.getOrganizationByName(name);
    	Organization firstOrganization ;
    	Organization parent = org.getParent();
    	if(parent.getId()==1){
    		firstOrganization = org;
    	}else{
    		firstOrganization = parent;
    	}
    	String depteName = firstOrganization.getName();
    	return depteName;
    }
    
//    public List<String> getPeopleTypeOfOrgTotal(String orgId){
//    	String HQL = "select count(*) from Employee e where e.peopleType_id='peopleType_01' and e.organization.id = ?";
//    	List list = getDefDao().findByQueryString(HQL, orgId);
//    	int total = 0;
//    	if(){
//    		
//    	}
//    }
}
