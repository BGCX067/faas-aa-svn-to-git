package com.fortunes.faas.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.fortunes.faas.model.ShiftConfig;
import com.fortunes.faas.model.WorkingArea;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.service.EmployeeService;

import net.fortunes.core.service.GenericService;

@Component
public class WorkingAreaService extends GenericService<WorkingArea> {

	@Resource private EmployeeService employeeService;
	@Resource private ShiftConfigService shiftConfigService;

	public List<Employee> getAvailableEmployees(Date selectedDate,
			long organizationId) {
		List<Employee> list = this.getDefDao().findByQueryString(
				"select new Employee(e.id,e.code,e.name,e.sex.id,e.sex.text,pos.id,pos.text,e.birthday) from Employee as e left join e.position as pos where e.organization.id = ? and e.id not in(" +
				" select sc.employee.id from ShiftConfig sc where sc.date = ? and sc.employee.organization.id = ?) and e.turnAway = false",
				organizationId,selectedDate,organizationId);
		return list;
	}
	
	public List<Employee> getAttLowThreeDay(long organization,int days,Date sDate,Date eDate){
		List<Employee> list = this.getDefDao().findByQueryString(
				"select e from Employee e where e.organization.id = ? and e.id not in ("+
				"select sc.employee.id from ShiftConfig sc where sc.date >=? and sc.date<=?"+
				" group by sc.employee.id having sum(CASE WHEN sc.weekEnd = true THEN 1 ELSE 0 END)>=?)",
				organization,sDate,eDate,Long.parseLong(days+""));
		return list;
	}
	
	public List<WorkingArea> getWorkingAreaByParent(long id){
		List<WorkingArea> list = this.getDefDao().findByQueryString("select new WorkingArea(w.id,w.name,w.leaf,w.template,w.code) from WorkingArea w where w.parent.id = ?", id);
		return list;
	}
	
	public List<WorkingArea> getAllWoringArea()throws Exception{
		String hql = "select w from WorkingArea w where w.parent is not null";
		List<WorkingArea> list = this.getDefDao().findByQueryString(hql);
		return list;
	}
	
	public List<WorkingArea> getAllWorkingAreaByOrg(long orgId) throws Exception{
		String hql = "select w from WorkingArea w where w.parent is not null and w.org.id =  "+orgId;
		List<WorkingArea> list = this.getDefDao().findByQueryString(hql);
		return list;
	}
	
	public void createOrUpateBranchShiftConfig(ShiftConfig config) throws Exception{
		shiftConfigService.addOrUpdate(config);
	}
	
	public List<WorkingArea> getChildres(WorkingArea workingArea,List<WorkingArea> list){
		List<WorkingArea> areas = new ArrayList<WorkingArea>();
		for(WorkingArea w : list){
			if(w.getParent().getId()== workingArea.getId()){
				areas.add(w);
			}
		}
		return areas;
	}
}
