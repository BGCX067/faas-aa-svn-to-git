package com.fortunes.faas.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.fortunes.core.service.GenericService;
import org.springframework.stereotype.Component;

import com.fortunes.faas.exception.NoBundleToApplyedException;
import com.fortunes.faas.model.DefaultTime;
import com.fortunes.faas.model.ShiftConfig;
import com.fortunes.faas.model.ShiftConfigApply;
import com.fortunes.faas.model.ShiftConfigBundle;
import com.fortunes.faas.model.ShiftConfigTemplate;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.model.Employee;

@Component
public class ShiftConfigBundleService extends GenericService<ShiftConfigBundle> {

	@Resource private ShiftConfigTemplateService shiftConfigTemplateService;
	@Resource private DefaultTimeService defaultTimeService;
	public List<ShiftConfigBundle> getBundles(long orgnizationId, int day) {
		return this.getDefDao().findByQueryString(
				"from ShiftConfigBundle s where s.organization.id = ? and day = ?",
				orgnizationId,day);
	}

//	public List<ShiftConfigTemplate> getTemplate(long bundleId, long areaId) {
//		return this.getDefDao().findByQueryString(
//				"from ShiftConfigTemplate st where st.shiftConfigBundle.id = ? and st.workingArea.id = ?",
//				bundleId,areaId);
//	}
	
	public void delTemplete(long bundleId){
		String hql = "delete ShiftConfigTemplate where shiftConfigBundle.id = ?";
		this.getDefDao().bulkUpdate(hql,bundleId);
	}
	
	public List<ShiftConfigTemplate> getTemplate(long bundleId) {
		return this.getDefDao().findByQueryString(
				"select new ShiftConfigTemplate(st.id,st.startTime,st.endTime,e.id,e.code,e.name,e.sex.id,e.sex.text,pos.id,pos.text,e.birthday,st.workingArea.id)from ShiftConfigTemplate st left join st.employee e left join e.position pos where st.shiftConfigBundle.id = ?",
				bundleId);
	}

	public List<Employee> getAvailableEmployees(long bundleId,long organizationId) {
		List<Employee> list = this.getDefDao().findByQueryString(
				"select new Employee(e.id,e.code,e.name,e.sex,pos,e.birthday) from Employee as e left join e.position as pos where e.organization.id = ? and e.id not in(" +
				" select sc.employee.id from ShiftConfigTemplate sc where sc.shiftConfigBundle.id = ? ) and e.turnAway = false",
				organizationId,bundleId);
		return list;
	}
	

	public ShiftConfigTemplate findOrCreate(long boundleId, Employee employee) {
		List<ShiftConfigTemplate> list = this.getDefDao().findByQueryString(
				"select s from ShiftConfigTemplate s where s.shiftConfigBundle.id = ? and s.employee.id = ?",boundleId,employee.getId());
		return (list != null && list.size() > 0)? list.get(0):new ShiftConfigTemplate();
	}

	public void createOrUpateBranchShiftConfig(ShiftConfigTemplate configTemplate) {
		this.getDefDao().getHibernateTemplate().saveOrUpdate(configTemplate);
	}

	public void deleteConfig(long boundleId, Employee employee) {
		this.getDefDao().bulkUpdate("delete from ShiftConfigTemplate st where st.shiftConfigBundle.id = ? and st.employee.id =?",
				boundleId,employee.getId());
	}

	public boolean checkApplyed(long organizationId, String yearMonth) {
		List<ShiftConfigApply> list = this.getDefDao().findByQueryString(
				"from ShiftConfigApply st where st.organization.id = ? and st.yearMonth =?",
				organizationId,yearMonth);
		return (list != null && list.size() > 0)? true:false;
	}

	public void applyTemplate(long organizationId, String yearMonth) throws Exception {
		//prepare template bundle
		List<ShiftConfigBundle> bundles = this.getDefDao().findByQueryString(
				"from ShiftConfigBundle t where t.organization.id = ?",
				organizationId);
		
		Map<Integer,List<ShiftConfigBundle>> bundlesMap = new HashMap<Integer, List<ShiftConfigBundle>>();
		for(int i = 1;i <= 7;i++){
			List<ShiftConfigBundle> dayList = new ArrayList<ShiftConfigBundle>();
			for(ShiftConfigBundle bundle : bundles){
				if(bundle.getDay() == i){
					dayList.add(bundle);
				}
			}
			bundlesMap.put(i, dayList);
		}
		
		
		//compute first date and last date in a mouth
		Date firstDate = AppHelper.toDate(yearMonth+"-01");
		Calendar cal = new GregorianCalendar();
		cal.setTime(firstDate);
		int lastDateInt = cal.getActualMaximum(Calendar.DATE);
		Date lastDate = AppHelper.toDate(yearMonth+"-"+lastDateInt);
		
		while(cal.getTime().before(lastDate) || cal.getTime().equals(lastDate)){
			System.out.println(cal);
			int day = cal.get(Calendar.DAY_OF_WEEK)-1;
			if(day == 0){
				day = 7;
			}
			
			//pick one bundle from many bundles in one day in week
			ShiftConfigBundle toBeApplyedBundle = null;
			ShiftConfigBundle nextToBeApplyedBundle = null;
			List<ShiftConfigBundle> dayList = bundlesMap.get(day);
			if(dayList.size() == 1){
				toBeApplyedBundle = dayList.get(0);
			}else{
				for(int k = 0; k < dayList.size();k++){
					if(dayList.get(k).isNextBundle()){
						toBeApplyedBundle = dayList.get(k);
						if(k+1 >= dayList.size()){
							nextToBeApplyedBundle = dayList.get(0);
						}else{
							nextToBeApplyedBundle = dayList.get(k+1);
						}
						toBeApplyedBundle.setNextBundle(false);
						nextToBeApplyedBundle.setNextBundle(true);
						System.out.println(toBeApplyedBundle.getName());
						this.update(toBeApplyedBundle);
						this.update(nextToBeApplyedBundle);
						break;
					}
				}
				if(toBeApplyedBundle == null){
					throw new NoBundleToApplyedException();
				}
			}
			
			
			List<ShiftConfigTemplate> templates = this.getDefDao().findByQueryString(
					"from ShiftConfigTemplate t where t.shiftConfigBundle.id = ?",
					toBeApplyedBundle.getId());
			
			//把默认上下班时间添加给模板以便遍历
			
			DefaultTime  dt = defaultTimeService.getDefaultTimeByOrg(organizationId);
			for(ShiftConfigTemplate template:templates){
				template.setStartTime(dt==null?template.getStartTime():dt.getStartTime());
				template.setEndTime(dt==null?template.getEndTime():dt.getEndTime());
				template.setBunchBreakTime(dt==null?template.getBunchBreakTime():dt.getBunchBreakTime());
			}
			
			for(ShiftConfigTemplate template : templates){
				ShiftConfig config = new ShiftConfig();
				config.setDate(cal.getTime());
				config.setWeekEnd((cal.getTime().getDay() == 0 || cal.getTime().getDay() == 6) ? true : false);
				config.setEmployee(template.getEmployee());
				config.setStartTime(template.getStartTime());
				config.setEndTime(template.getEndTime());
				
				config.setBunchBreakTime(template.getBunchBreakTime());
				
				config.setWorkingArea(template.getWorkingArea());
				this.getDefDao().getHibernateTemplate().save(config);
			}
			
			cal.add(Calendar.DATE, 1);
		}
		
		ShiftConfigApply apply = new ShiftConfigApply();
		apply.setOrganization(AppHelper.toOrganization(organizationId+""));
		apply.setYearMonth(yearMonth);
		this.getDefDao().getHibernateTemplate().save(apply);
		
	}
	
	public void pasteShiftConfigTemplateToCurrentDay(long organizationId,int currentDayInWeek,int dayInWeek)throws Exception{
		List<ShiftConfigBundle> currentBundles = this.getDefDao().findByQueryString(
				"from ShiftConfigBundle t where t.organization.id = ? and t.day = ?", 
				organizationId,dayInWeek);
		ShiftConfigBundle currentBundle = currentBundles.get(0);
		List<ShiftConfigTemplate> sctlist = this.getDefDao().findByQueryString(
				"from ShiftConfigTemplate sct where sct.shiftConfigBundle.id = ?", 
				currentBundle.getId());
		
		List<ShiftConfigBundle> bundles = this.getDefDao().findByQueryString(
				"from ShiftConfigBundle t where t.organization.id = ? and t.day = ?", 
				organizationId,currentDayInWeek);
		ShiftConfigBundle scb = bundles.get(0);
		
		this.deleteShiftConfigTemplateByDay(organizationId, currentDayInWeek);
		
		for(ShiftConfigTemplate sct:sctlist){
			ShiftConfigTemplate currentSc = new ShiftConfigTemplate();
			currentSc.setEndTime(sct.getEndTime());
			currentSc.setEmployee(sct.getEmployee());
			currentSc.setShiftConfigBundle(scb);
			currentSc.setStartTime(sct.getStartTime());
			currentSc.setWorkingArea(sct.getWorkingArea());
			currentSc.setBunchBreakTime(sct.getBunchBreakTime());
			shiftConfigTemplateService.add(currentSc);
		}
	}
	public void deleteShiftConfigTemplateByDay(long organizationId,int day)throws Exception{
		List<ShiftConfigBundle> scbList = this.getDefDao().findByQueryString(
				"from ShiftConfigBundle t where t.organization.id = ? and t.day = ?",
				organizationId,day);
		if(scbList.size()>0){
			ShiftConfigBundle scb = scbList.get(0);
			this.getDefDao().bulkUpdate(
				"delete from ShiftConfigTemplate st where st.shiftConfigBundle.id = ?", 
				scb.getId());
		}
	}
}
