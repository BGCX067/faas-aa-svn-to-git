package com.fortunes.faas.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.Area;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.service.EmployeeService;

import net.fortunes.core.service.GenericService;

@Component
public class AreaService extends GenericService<Area> {

	@Resource private EmployeeService employeeService;
	
	@Override
	protected DetachedCriteria getConditions(String query,Map<String, String> queryMap) {
		DetachedCriteria criteria = super.getConditions(query, queryMap);
		if(query !=  null){
			criteria.add(Restrictions.eq("id", Long.parseLong(query)));
		}
		return criteria;
	}
	
	@Override
	public Area add(Area entity) throws Exception {
		
		super.add(entity);
		if(entity.getParent() != null)
			entity.getParent().setLeaf(false);
		return entity;
	}
	
	@Override
	public void del(Area entity) throws Exception {
		Area parent = entity.getParent();
		super.del(entity);
		if(parent != null && parent.getChildren().size() <= 0)
			parent.setLeaf(true);
	}
	
	/**
	 * 查找不属于任何区域的员工(所有未分配的员工)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Area> getUnassignEmployees() {
		return getDefDao().findByQueryString(
				" select e from Employee as e left join fetch e.areas as o where o is null" );
	}
	
//	public void removeAreaEmployees(long[] checked,String areaId){
//		for(long l:checked){
//			Employee e = employeeService.get(Long.toString(l));
//			List<Area> areas = e.getAreas();
//			List<Area> list = new ArrayList<Area>();
//			for(Area a : areas){
//				if(a.getId()!=Long.valueOf(areaId)){
//					list.add(a);
//				}
//			}
//			e.setAreas(list);
//			employeeService.update(e);
//		}
//	}
	
	public Area getArea(){
		StringBuffer sb = new StringBuffer("select a from Area a where a.parent is not null");
		List<Area> list =  this.getDefDao().findByQueryString(sb.toString());
		return list.get(0);
	}
	
	/**
	 * 移动员工到区域
	 * @param employeeIds
	 * @param areaIds
	 */
//	public void batchMoveEmployeesToAreas(long[] employeeIds,long areaId){
//		Area area = new Area(areaId);
//		for(long employeeId:employeeIds){
//			Employee e = employeeService.get(Long.toString(employeeId));
//			List<Area> areas = e.getAreas();
//			if(areas!=null){
//				if(areas.contains(area)){
//					continue;
//				}
//			}
//			areas.add(area);
//			e.setAreas(areas);
//			employeeService.update(e);
//		}
//	}
	@SuppressWarnings("unchecked")
	public List<Area> listTree(String id){
    	return getDefDao().findByQueryString("select area from Area as area where area.parent.id = ?", Long.parseLong(id));
    }
	
//	public void area2Orgs(Map<String,List<Organization>> hash) throws Exception {
//		List<Area> areas = getAll();
//		Set<String> keys = hash.keySet();
//		for(String key:keys){
//			Area tmpArea = null;
//			for(Area area:areas){
//				if(key.equals(area.getName())){
//					tmpArea = area;
//					break;
//				}
//			}
//			if(tmpArea!=null){
//				List<Organization> orgs = hash.get(key);
//				for(Organization org : orgs){
//					List<Employee> empsOrg = org.getEmployees();
//					for(Employee emp : empsOrg){
//						List<Area> empAreas = emp.getAreas();
//						if(empAreas!=null){
//							if(empAreas.contains(tmpArea)){
//								continue;
//							}
//							empAreas.add(tmpArea);
//							emp.setAreas(empAreas);
//							employeeService.update(emp);
//						}
//					}
//				}
//			}
//		}
//	}
}
