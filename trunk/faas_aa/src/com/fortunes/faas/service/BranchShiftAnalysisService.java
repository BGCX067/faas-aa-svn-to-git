package com.fortunes.faas.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.AttShift;
import com.fortunes.faas.model.BranchShiftAnalysis;
import com.fortunes.faas.model.ShiftConfig;
import com.fortunes.faas.model.BranchShiftAnalysis.AnalysisType;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.service.OrganizationService;

import net.fortunes.core.ListData;
import net.fortunes.core.service.GenericService;

@Component
public class BranchShiftAnalysisService extends GenericService<BranchShiftAnalysis> {
	@Resource ShiftConfigService shiftConfigService;
	@Resource OrganizationService organizationService;
	@Resource AttShiftService attShiftService;
	
	public List<Object[]> searchMoreInWeekend(long organizationId,long days, Date firstDate, Date lastDate) {
		
		StringBuffer sb=new StringBuffer();
		sb.append(" select sc.employee.id ,sc.employee.name, (")
		.append(" 	select count(s.id) from ShiftConfig s ")
		.append("   where sc.employee.id = s.employee.id ")
		.append("   and (s.date >= ?  and s.date <= ?)")
		.append(" ),sum(CASE WHEN sc.weekEnd = true THEN 1 ELSE 0 END) ")
		.append(" from ShiftConfig sc ");
		if(organizationId==00){
			sb.append("where sc.employee.organization.id in(select o.id from Organization o where o.type.id= ?)");
		}else{
			Organization org = organizationService.get(Long.toString(organizationId));
			if(org.getType().getId().equals("organizationType_02")){
				sb.append("where sc.employee.organization.id in (select o.id from Organization o where o.parent.id = ?)");
			}else{
				sb.append(" where sc.employee.organization.id = ?");
			}
		}
		sb.append(" and (sc.date between ?  and ?) ").append(" and sc.weekEnd = true ")
		.append(" group by sc.employee.id,sc.employee.name").append(" having sum(CASE WHEN sc.weekEnd = true THEN 1 ELSE 0 END) >= ?");
		List<Object[]> list = this.getDefDao().findByQueryString(sb.toString(),
				firstDate,lastDate,organizationId,firstDate,lastDate,days);
		return list;
	}
	public List<Object[]> searchEqualForDaysInWeekend(long organizationId,long days, Date firstDate, Date lastDate) {
		StringBuffer sb = new StringBuffer();
		sb.append("select e.id,e.name,( select count(sa.id) from ShiftConfig sa where sc.employee.id = sa.employee.id")
		.append(" and sa.date >= ? and sa.date <=?),sum(case when sc.weekEnd = true then 1 else 0 end)")
		.append(" from ShiftConfig sc right join sc.employee e ");
		if(organizationId==00){
			sb.append(" where sc.employee.organization.id in (select o.id from Organization o where o.type.id = ?)");
		}else{
			Organization org = organizationService.get(Long.toString(organizationId));
			if(org.getType().getId().equals("organizationType_02")){
				sb.append("where sc.employee.organization.id in (select o.id from Organization o where o.parent.id = ?)");
			}else{
				sb.append(" where sc.employee.organization.id = ?");
			}
		}
		sb.append(" and sc.date between ? and ? group by e.id,e.name,sc.employee")
		.append(" having sum(case when sc.weekEnd = true then 1 else 0 end)=?");
		List<Object[]> list = this.getDefDao().findByQueryString(sb.toString(),
				firstDate,lastDate,organizationId,firstDate,lastDate,days);
		return list;
	}

	public List<Object[]> searchLessInTotal(long organizationId,long days, Date firstDate,
			Date lastDate) {
//		StringBuffer sb=new StringBuffer();
//		sb.append(" select sc.employee.id ,sc.employee.name, (")
//		.append(" 	select count(s.id) from ShiftConfig s ")
//		.append("   where sc.employee.id = s.employee.id ")
//		.append("   and (s.date >= ?  and s.date <= ?)")
//		.append(" ),sum(CASE WHEN sc.weekEnd = true THEN 1 ELSE 0 END) ")
//		.append(" from ShiftConfig sc ");
//		if(organizationId==00){
//			sb.append("where sc.employee.organization.id in(select o.id from Organization o where o.type.id= ?)");
//		}else{
//			Organization org = organizationService.get(Long.toString(organizationId));
//			if(org.getType().getId().equals("organizationType_02")){
//				sb.append("where sc.employee.organization.id in (select o.id from Organization o where o.parent.id = ?)");
//			}else{
//				sb.append(" where sc.employee.organization.id = ?");
//			}
//		}
//		sb.append(" or sc.date is null and (sc.date >= ?  and sc.date <= ?) ").append(" and sc.weekEnd = true ")
//		.append(" group by sc.employee.id,sc.employee.name").append(" having sum(CASE WHEN sc.weekEnd = true THEN 1 ELSE 0 END) < ?");
		StringBuffer sb = new StringBuffer();
		sb.append("select e.id,e.name,( select count(sa.id) from ShiftConfig sa where sc.employee.id = sa.employee.id")
		.append(" and sa.date >= ? and sa.date <=?),sum(case when sc.weekEnd = true then 1 else 0 end)")
		.append(" from ShiftConfig sc right join sc.employee e ");
		if(organizationId==00){
			sb.append(" where sc.employee.organization.id in (select o.id from Organization o where o.type.id = ?)");
		}else{
			Organization org = organizationService.get(Long.toString(organizationId));
			if(org.getType().getId().equals("organizationType_02")){
				sb.append("where sc.employee.organization.id in (select o.id from Organization o where o.parent.id = ?)");
			}else{
				sb.append(" where sc.employee.organization.id = ?");
			}
		}
		sb.append(" and sc.date between ? and ? group by e.id,e.name,sc.employee")
		.append(" having sum(case when sc.weekEnd = true then 1 else 0 end)<?");
		List<Object[]> list = this.getDefDao().findByQueryString(sb.toString(),
				firstDate,lastDate,organizationId,firstDate,lastDate,days);
		return list;
	}
	public List<Object[]> searchBranchInTotal(long organizationId,long days, Date firstDate,
			Date lastDate) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select e.id ,e.name, count(e.id),(")
		.append(" sum(CASE WHEN sc.weekEnd = true THEN 1 ELSE 0 END))")
		.append(" from ShiftConfig sc right join sc.employee e");
		if(organizationId==00){
			sb.append("where sc.employee.organization.id in(select o.id from Organization o where o.type.id= ?)");
		}else{
			Organization org = organizationService.get(Long.toString(organizationId));
			if(org.getType().getId().equals("organizationType_02")){
				sb.append(" where sc.employee.organization.id in (select o.id from Organization o where o.parent.id = ?)");
			}else{
				sb.append(" where sc.employee.organization.id = ?");
			}
		}
		sb.append(" and (sc.date >= ?  and sc.date <= ?) ")
		.append(" group by e.id,e.name");
		List<Object[]> list = this.getDefDao().findByQueryString(sb.toString(),
				organizationId,firstDate,lastDate);
		return list;
	}
	
	public List<Object[]> searchOtherBranchInTotal(long organizationId,long days, Date firstDate,
			Date lastDate) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select e.id ,e.name, count(sc.employee.id),(")
		.append(" sum(CASE WHEN sc.weekEnd = true THEN 1 ELSE 0 END))")
		.append(" from ShiftConfig sc right join sc.employee e");
		if(organizationId!=00){
			Organization org = organizationService.get(Long.toString(organizationId));
			if(org.getType().getId().equals("organizationType_02")){
				sb.append(" where sc.configOrganization.id in (select o.id from Organization o where o.parent.id = ?)");
			}else{
				sb.append(" where sc.configOrganization.id = ?");
			}
//			sb.append(" and sc.configOrganization.id is not null ");
		}else{
//			sb.append(" where sc.configOrganization.id is not null ");
		}
		
		sb.append(" and (sc.date between ? and ?) ")
		.append(" group by e.id,e.name having count(e.id)>0");
		List<Object[]> list = this.getDefDao().findByQueryString(sb.toString(),
				organizationId,firstDate,lastDate);
		return list;
	}
	
	/**
	 * 列出休息日大于等于7天的员工信息
	 * @param organizationId
	 * @param days
	 * @param firstDate
	 * @param lastDate
	 * @return
	 */
	public List<Object[]> searchRestdayMoreThanTotal(long organizationId,long days, Date firstDate,
			Date lastDate) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select e.id ,e.name,(")
		.append(" sum(CASE WHEN a.restday = 1 THEN 1 ELSE 0 END))")
		.append(" from AttShift a right join a.employee e");
		if(organizationId==00){
			sb.append("where a.employee.organization.id in(select o.id from Organization o where o.type.id= ?)");
		}else{
			Organization org = organizationService.get(Long.toString(organizationId));
			if(org.getType().getId().equals("organizationType_02")){
				sb.append(" where a.employee.organization.id in (select o.id from Organization o where o.parent.id = ?)");
			}else{
				sb.append(" where a.employee.organization.id = ?");
			}
		}
		sb.append(" and (a.attDate >= ?  and a.attDate <= ?) and a.restday=1")
		.append(" group by e.id,e.name");
		sb.append("  having sum(CASE WHEN a.restday = 1 THEN 1 ELSE 0 END)>=?");
		
		String ss = sb.toString();
		List<Object[]> list = this.getDefDao().findByQueryString(sb.toString(),
				organizationId,firstDate,lastDate,days);
		return list;
	}
	
	
	@SuppressWarnings("unchecked")
	public ListData<BranchShiftAnalysis> getBranchShiftAnalysis(
			long organizationId,String yearMonth,int days,String type,int start,int limit){
		StringBuffer hql = new StringBuffer(" from BranchShiftAnalysis a");
		hql.append(" where a.yearMonth = '").append(yearMonth).append("'").
		append(" and analysisType = '").append(type).append("'");
		
		if(!type.equals(AnalysisType.REST_MORE_THAN.name())){
			hql.append(" and a.days = ").append(days);
		}
		
//			.append(" and a.days = ").append(days)
//			.append(" and analysisType = '").append(type).append("'");
		if(organizationId==00){
			hql.append(" and a.organization.id in(select o.id from Organization o where o.type.id='organizationType_01')");
		}else{
			if(organizationId>0){
				Organization org = organizationService.get(Long.toString(organizationId));
				if(org.getType().getId().equals("organizationType_02")){
					hql.append(" and a.organization.id in(select o.id from Organization o where o.parent.id = "+organizationId+")");
				}else{
					hql.append(" and a.organization.id = "+organizationId);
				}
			}
//			if(organizationId>0){
				
//				.append(organizationId);
//			}
		}
		if(type.equals(AnalysisType.REST_MORE_THAN.name())){
			hql.append(" and a.restdays is not null and  a.restdays>=7");
		}
 
	 
		List list = getDefDao().findByQueryString("select count(*)"+hql.toString());
		int total = Integer.parseInt(list.get(0).toString());
		List<BranchShiftAnalysis> bsas=null;
		if(type.equals(AnalysisType.REST_MORE_THAN.name())){
			bsas = getDefDao().findByQueryString("select new BranchShiftAnalysis(a.id,a.employee.id,a.employee.name,a.employee.organization.id,a.employee.organization.shortName,a.organization.id,a.organization.name,a.organization.shortName,a.workdays,a.weekdays,a.notesDesc,a.restdays)"
					+hql.toString(),start,limit);
		}else{
			bsas = getDefDao().findByQueryString("select new BranchShiftAnalysis(a.id,a.employee.id,a.employee.name,a.employee.organization.id,a.employee.organization.shortName,a.organization.id,a.organization.name,a.organization.shortName,a.workdays,a.weekdays,a.notesDesc,a.restdays,a.branchShiftTimeTotal)"
				+hql.toString(),start,limit);
		}
		ListData<BranchShiftAnalysis> listData = new ListData<BranchShiftAnalysis>(bsas,total);
		return listData;
	}
	
	

	public void batchCreateOrUpdate(long organizationId,String yearMonth,
			int days,String type,List<Object[]> statObjects) throws Exception {
		if(days<30){
			String employeeIds = "";
			for(Object[] o : statObjects){
				if(!employeeIds.equals("")){
					employeeIds = employeeIds + ",";
				}
				employeeIds = employeeIds + o[0];
			}
			if(!employeeIds.equals("")){
				this.clearNotExist(organizationId,yearMonth, type, employeeIds);
			}
			if(statObjects.size()==0){
				this.truncateExist(organizationId,yearMonth,type);
			}
		}
		for(Object[] o : statObjects){
			long employeeId = (Long)o[0];
			String  employeeName = (String)o[1];
			BranchShiftAnalysis a = this.findOrCreate(organizationId,yearMonth,days,type,employeeId);
			
			if(type.equals(AnalysisType.REST_MORE_THAN.name())){
				Long restdays = (Long)o[2];
				SimpleDateFormat df = new SimpleDateFormat("yyyy-M-dd");
				Date firstDate = df.parse(yearMonth+"-01");
				Calendar cal = new GregorianCalendar();
				cal.setTime(firstDate);
				int lastDateInt = cal.getActualMaximum(Calendar.DATE);
				Date lastDate = df.parse(yearMonth+"-"+lastDateInt);
				List<AttShift> list = attShiftService.queryShiftConfigs(Long.toString(employeeId), firstDate, lastDate);
				if(list.size()<1){
					restdays = Long.parseLong("0");
				}
				
//					Long weekdays = Long.parseLong("0");
//					Long workdays = Long.parseLong("0");
					a.setRestdays(restdays.intValue());
					a.setWeekdays(0);
					a.setWorkdays(0);
					a.setBranchShiftTimeTotal(0);
				
			}else{
				
				Long workdays = (Long)o[2];
				
				double branchShiftTimeTotal = 0;
				SimpleDateFormat df = new SimpleDateFormat("yyyy-M-dd");
				Date firstDate = df.parse(yearMonth+"-01");
				Calendar cal = new GregorianCalendar();
				cal.setTime(firstDate);
				int lastDateInt = cal.getActualMaximum(Calendar.DATE);
				Date lastDate = df.parse(yearMonth+"-"+lastDateInt);
				List<ShiftConfig> list = shiftConfigService.queryShiftConfigs(Long.toString(employeeId), firstDate, lastDate);
				if(workdays==1 && list.size()<1){
//					if(list.size()<1){
						workdays = Long.parseLong("0");
//					/}
//					else{
//						for(ShiftConfig ss:list){
//							branchShiftTimeTotal += ((ss.getEndTime().getTime() - ss.getStartTime().getTime())/(1000*3600)) - ss.getBunchBreakTime(); 
//						}	
//					}
				}
				if(list!=null&&list.size()>0){
					double temp=0;
					for(ShiftConfig ss:list){
						ShiftConfig b = ss;
						double aa = ((ss.getEndTime().getTime() - ss.getStartTime().getTime())/(1000*3600));
						
						temp += ((ss.getEndTime().getTime() - ss.getStartTime().getTime())/(1000*3600)) - ss.getBunchBreakTime(); 
					}
					branchShiftTimeTotal = temp;
				}
				Long weekdays = (Long)o[3];
				a.setWorkdays(workdays.intValue());
				a.setWeekdays(weekdays.intValue());
				a.setRestdays(0);
				a.setBranchShiftTimeTotal(branchShiftTimeTotal);
			}
			
			a.setOrganization(AppHelper.toOrganization(organizationId+""));
			a.setYearMonth(yearMonth);
			a.setAnalysisType(type);
			a.setDays(days);
			Employee e = new Employee(employeeId, "", employeeName);
			a.setEmployee(e);
			this.addOrUpdate(a);
		}
	}
	
	private BranchShiftAnalysis findOrCreate(long organizationId,String yearMonth,
			int days,String type,long employeeId){
		List<BranchShiftAnalysis> list = this.getDefDao().findByQueryString(
				" select new BranchShiftAnalysis(a.id,a.employee.id,a.employee.name,a.workdays,a.weekdays,a.notesDesc,a.branchShiftTimeTotal)" +
				" from BranchShiftAnalysis a " +
				" where a.organization.id = ? " +
				" and a.yearMonth = ? and a.days = ? and a.analysisType = ? and a.employee.id = ?" ,
				organizationId,yearMonth,days,type,employeeId);
		return (list != null && list.size() > 0) ? list.get(0) : new BranchShiftAnalysis();
	}
	
	private List<BranchShiftAnalysis> find(long organizationId,String yearMonth,
			int days,String type){
		List<BranchShiftAnalysis> list = this.getDefDao().findByQueryString(
				" select new BranchShiftAnalysis(a.id,a.employee.id,a.employee.name,a.workdays,a.weekdays,a.notesDesc)" +
				" from BranchShiftAnalysis a " +
				" where a.organization.id = ? " +
				" and a.yearMonth = ? and a.days = ? and a.analysisType = ? " ,
				organizationId,yearMonth,days,type);
		return list;
	}
	
	public void clearNotExist(long organizationId,String yearMonth, String type,String employeeIds) {
		JdbcTemplate jdbcTemplate = getDefDao().getJdbcTemplate();
		String sql = "delete from branchShiftAnalysis where yearMonth ='"+yearMonth+"'"
		+" and analysisType = '"+type+"'"+" and employee_id not in ("+employeeIds+") and organization_id="+organizationId;
		jdbcTemplate.execute(sql);
	}
	
	public void truncateExist(long organizationId,String yearMonth,String type){
		JdbcTemplate jdbcTemplate = getDefDao().getJdbcTemplate();
		String sql = "delete from branchShiftAnalysis where yearMonth ='"+yearMonth+"'"
		+" and analysisType = '"+type+"' and organization_id="+organizationId;
		jdbcTemplate.execute(sql);
	}
}
