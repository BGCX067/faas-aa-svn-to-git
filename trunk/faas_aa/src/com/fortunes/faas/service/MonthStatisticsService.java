package com.fortunes.faas.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import net.fortunes.core.ListData;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;

import com.fortunes.faas.model.HolidayApply;
import com.fortunes.faas.model.MonthStatistics;
import com.fortunes.faas.vo.AttShifts;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.sun.accessibility.internal.resources.accessibility;

/**
 * 
 * @author Leo
 * @version 2011-4-8
 */
@Component
@SuppressWarnings("unchecked")
public class MonthStatisticsService extends GenericService<MonthStatistics>{
	@Resource private AttShiftReportApplyService attShiftReportApplyService;

	public void batchSave(List<HolidayApply> list) throws Exception {
		for(HolidayApply ha : list){
			saveMonthStatistics(ha);
		}
	}
	
	public void saveMonthStatistics(HolidayApply ha) throws Exception {
//		Date startDate = ha.getStartDate();
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(startDate);
//		MonthStatistics monthLeave = null;
//		String yearmonthContent = Tools.date2Date(startDate)+"~"+Tools.date2Date(ha.getEndDate());
//		monthLeave = this.get(ha.getLeaveApply(), cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1);
//		if(monthLeave.getDateContents()!=null){
//			yearmonthContent = monthLeave.getDateContents()+","+yearmonthContent;
//		}
//		monthLeave.setDateContents(yearmonthContent);
//		this.update(monthLeave);
	}
	
	public MonthStatistics get(Employee employee,int year,int month){
		String hql = "select m from MonthStatistics m where m.employee = ? and m.year = ? and m.month = ? ";
		List<MonthStatistics> mls = this.getDefDao().findByQueryString(hql,employee,year,month);
		return mls!=null&&mls.size()>0?mls.get(0):null;
	}
	
	public List<MonthStatistics> getYear(int year) {
		String hql = "select new MonthStatistics(e as employee,sum(m.leaveTotal) as leaveTotal" +
				",sum(m.leaves) as leaves,sum(m.sickLeaves) as sickLeave,sum(m.absents) as absents)  " +
				"from MonthStatistics m join m.employee e where m.year = ? group by e.id";
		List<MonthStatistics> mls = this.getDefDao().findByQueryString(hql,year);
		return mls;
	}
	
	public List<MonthStatistics> getOrganizationMonthStatistics(int year,int month,String organizationId){
		String hql = "select m from MonthStatistics m where m.organizationId in ("+organizationId+") and m.years= ? and m.months=?";
		List<MonthStatistics> list = this.getDefDao().findByQueryString(hql,0,0,year,month);
		return list;
	}
	
	public ListData<MonthStatistics> getcon(int start,int limit,Map<String, String> queryMap){
		int total = 0;
		List<MonthStatistics> list = new ArrayList<MonthStatistics>();
		if(!StringUtils.isNotEmpty(queryMap.get("isAffirm"))){
			String searchType = queryMap.get("searchType");
			int year = Integer.parseInt(queryMap.get("year"));
			int month = Integer.parseInt(queryMap.get("month"));
			StringBuffer hql = new StringBuffer("from MonthStatistics m where m.months = ? and m.years=?");
//			StringBuffer hql2 = new StringBuffer("from MonthStatistics m left join Organization o on m.deptName = o.name where m.months = ? and m.years=?");
			if(searchType.equals("employee")&&StringUtils.isNotEmpty(queryMap.get("employee"))){
				hql.append(" and m.employeeId = '");
				hql.append(queryMap.get("employee")).append("'");
				
//				hql2.append(" and m.employeeId = '");
//				hql2.append(queryMap.get("employee")).append("'");
			}
			if(searchType.equals("organization")&&StringUtils.isNotEmpty(queryMap.get("organizations"))){
				hql.append(" and m.organizationId in(").append(queryMap.get("organizations")).append(")");
				
//				hql2.append(" and m.organizationId in(").append(queryMap.get("organizations")).append(")");
//				hql2.append(" order by o.id");
			}
			
			if(limit>0){
				List ls = this.getDefDao().findByQueryString("select count(*) "+hql.toString(), 0,0,month,year);
				if(ls.size()>0)
				total = Integer.parseInt(ls.get(0).toString());
			}
			hql.append(" order by m.organizationId desc");
			list = this.getDefDao().findByQueryString("select m "+hql.toString(), start,limit,month,year);
		}
		return new ListData<MonthStatistics>(list,total);
	}
	
	public List<MonthStatistics> getMonth(int year,int month) {
		String hql = "select m from MonthStatistics m where m.year = ? and m.month = ?";
		List<MonthStatistics> mls = this.getDefDao().findByQueryString(hql,new Object[]{year,month});
		return mls;
	}
	
	public void delAffirmInfo(String year, String month,String organizations) {
		String yearMonth = year+"-"+month;
		JdbcTemplate jdbcTemplete = this.getDefDao().getJdbcTemplate();
		jdbcTemplete.execute("delete from MonthStatistics where years = '"+year+"' and months = '"+month+"' and organizationId in("+organizations+")");
		jdbcTemplete.execute("delete from AttShiftReportApply where yearMonth='"+yearMonth+"' and organization_id in("+organizations+")");
	}
	public void bathAdd(List<MonthStatistics> list) throws Exception {
		for(MonthStatistics m : list){
			this.add(m);
		}
	}
}
