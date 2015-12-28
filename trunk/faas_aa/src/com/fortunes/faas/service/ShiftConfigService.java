package com.fortunes.faas.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import com.fortunes.faas.model.ShiftConfig;
import com.fortunes.faas.vo.ShiftConfigVo;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.service.EmployeeService;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class ShiftConfigService extends GenericService<ShiftConfig> {
	@Resource private EmployeeService employeeService;
	
	public void batchCreate(String[] dates, String[] employeeIds, String startTime,
			String endTime,boolean nextDay,boolean cancelShiftConfig) throws Exception {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm");
		for(String date:dates){
			for(String employeeId:employeeIds){
				Date d = Tools.string2Date(date);
				Employee e = AppHelper.toEmployee(employeeId);
				ShiftConfig config = findOrCreate(d,e);
				config.setEmployee(e);
				config.setDate(d);
				config.setWeekEnd((d.getDay() == 0 || d.getDay() == 6) ? true : false);
				config.setCancel(cancelShiftConfig);
				
//				config.setBunchBreakTime(1);
				if(!cancelShiftConfig){
					config.setStartTime(timeFormat.parse(startTime));
					config.setEndTime(timeFormat.parse(endTime));
				}
				config.setNextDay(nextDay);
				this.add(config);
			}
		}
	}
	
	public void delAllShiftConfig(Date date,String orgId){
		String hql = "delete ShiftConfig where date = ? and (configOrganization.id = ? or employee.id in (select e.id from Employee e where e.organization.id = ?))";
		this.getDefDao().bulkUpdate(hql, date,Long.parseLong(orgId),Long.parseLong(orgId));
	}
	
	public void addBatch(List<ShiftConfig> list) throws Exception{
		for(ShiftConfig sc : list){
			this.addOrUpdate(sc);
		}
	}
	
	public void batchCreateAuto() throws Exception{
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat sdf = new SimpleDateFormat("HH:ss:mm");
		List<Long> list = employeeService.wipeBranchEmployee();
		Calendar calendar = Calendar.getInstance();
		calendar.add(calendar.MONTH, 1);
		Date sDate = Tools.string2Date(sf.format(calendar.getTime())+"-01");
		Date eDate = Tools.string2Date(sf.format(calendar.getTime())+"-"+calendar.getActualMaximum(calendar.DAY_OF_MONTH));
		calendar.setTime(sDate);
		Date sTime = sdf.parse("08:30:00");
		Date eTime = sdf.parse("17:30:00");
		while (calendar.getTime().before(eDate) || calendar.getTime().compareTo(eDate)==0) {
			Date d = calendar.getTime();
			if(d.getDay() !=0 && d.getDay()!=6){
				for(Long e : list){
					ShiftConfig config = findOrCreate(d,AdminHelper.toEmployee(e+""));
					config.setEmployee(AdminHelper.toEmployee(e+""));
					config.setDate(d);
					config.setWeekEnd((d.getDay() == 0 || d.getDay() == 6) ? true : false);
					config.setStartTime(sTime);
					config.setEndTime(eTime);
					config.setNextDay(false);
					this.add(config);
				}
			}
			calendar.add(calendar.DATE, 1);
		}
	}
	
	public void createNextMonth(List<Long> list) throws Exception{
//		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
//		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.MONTH, 1);
//		Date sDate = Tools.string2Date(sf.format(calendar.getTime())+"-01");
//		Date eDate = Tools.string2Date(sf.format(calendar.getTime())+"-"+calendar.getActualMaximum(calendar.DAY_OF_MONTH));
//		calendar.setTime(sDate);
////		sTime = sTime+":00";
////		eTime = eTime+":00";
//		Date startTime = sdf.parse("08:30:00");
//		Date endTime = sdf.parse("17:30:00");
//		while (calendar.getTime().before(eDate) || calendar.getTime().compareTo(eDate)==0) {
//			Date d = calendar.getTime();
//			if(d.getDay() !=0 && d.getDay()!=6){
		String sql = "select e.id from employee as e,organization as o where e.organization_id = o.id and o.type_id ='organizationType_02'";
		JdbcTemplate jdbc = this.getDefDao().getJdbcTemplate();
		List<Employee> emps = jdbc.query(sql, new EmployeeMapper());
				for(Employee e : emps){
					ShiftConfig config = new ShiftConfig();
					config.setEmployee(e);
					config.setDate(Tools.stringToDate("2012-04-04"));
					config.setWeekEnd(false);
					config.setStartTime(null);
					config.setEndTime(null);
					config.setNextDay(false);
					config.setCancel(true);
					this.add(config);
				}
//			}
//			calendar.add(calendar.DATE, 1);
//		}
	}
	private static final class EmployeeMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			Employee employee = new Employee();
			employee.setId(rs.getLong("id"));
	        return employee;
	    }
	}
	
	public ShiftConfig findOrCreate(Date date,Employee employee){
		List<ShiftConfig> list = this.getDefDao().findByQueryString(
				"select s from ShiftConfig s where s.date = ? and s.employee.id = ?",date,employee.getId());
		return (list != null && list.size() > 0)? list.get(0):new ShiftConfig();
	}

	public List<ShiftConfig> queryShiftConfigs(String employeeId, Date start,
			Date end) {
		List<ShiftConfig> list = this.getDefDao().findByQueryString(
				"select s from ShiftConfig s where  s.employee.id = ? and (s.date >= ? and s.date <= ?)",
				Long.parseLong(employeeId),start,end);
		return list;
	}
	
	public Employee getEmployeeByShiftConfig(Long id){
		List<Employee> list = this.getDefDao().findByQueryString(
				"select new Employee(e.id,e.code,e.name,e.sex,pos,e.birthday) from Employee e left join e.position pos where e.id = (select sc.employee.id from ShiftConfig sc where sc.id = ?)", id);
		return list.size()>0?list.get(0):null;
	}
	
	/**
	 * 
	 * @param date 格式：yyyy-mm-dd
	 * @return List<ShiftConfig>
	 * @throws Exception
	 */
	public List<ShiftConfig> queryShiftConfigs(Date date,boolean nextDay) {
		try{
			List<ShiftConfig> list = getDefDao().findByQueryString("SELECT new ShiftConfig(sc.id,sc.date,sc.weekEnd,sc.startTime,sc.endTime,sc.nextDay,sc.employee.id,sc.configOrganization.id,sc.workingArea.id,sc.isCancel) FROM ShiftConfig sc WHERE sc.date = ? and sc.nextDay = ?", date,nextDay);
			return list;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	
	public void deleteMistakeShiftConfig(){
		StringBuffer sql = new StringBuffer("delete from  shiftConfig where employee_id in(select e.id from employee as e where e.organization_id in");
			sql.append("(select o.id from organization as o where o.type_id = 'organizationType_01')) and workingArea_id is null");
		this.getDefDao().getJdbcTemplate().execute(sql.toString());
	}

	public ShiftConfig queryShiftConfigs(String employeeId, Date date) {
		List<ShiftConfig> list = this.getDefDao().findByQueryString(
				"select s from ShiftConfig s where  s.employee.id = ? and s.date = ? ",
				Long.parseLong(employeeId),date);
		return (list != null && list.size() > 0)? list.get(0):null;
	}
	public List<ShiftConfig> getConfigs(Date startDate,Date endDate, long organizationId) {
		List<ShiftConfig> list = this.getDefDao().findByQueryString(
				"select sc from ShiftConfig sc where ((sc.employee.organization.id = ? and sc.configOrganization is null) or sc.configOrganization.id = ?) and sc.date >= ? and sc.date <= ? ",
				organizationId,organizationId,startDate,endDate);
		return list;
	}

	public void deleteConfig(Date selectedDate, Employee employee) {
		this.getDefDao().bulkUpdate("delete from ShiftConfig sc where sc.date = ? and sc.employee.id =?",
				selectedDate,employee.getId());
	}
	//根据部门和时间清楚某网点，的排版设置，和排班
	public void deleteConfigByOrg(String year,String month,String organizationId) throws ParseException{
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(year));
		c.set(Calendar.MONTH, Integer.parseInt(month));
		String yearMonth = year+"-"+(month.length()==1?"0"+month:month);
		Date sDate = Tools.stringToDate(yearMonth+"-01");
		Date eDate = Tools.stringToDate(yearMonth+"-"+c.getActualMaximum(c.DAY_OF_MONTH));
		this.getDefDao().bulkUpdate("delete from ShiftConfig sc where sc.employee.id in (select e.id from Employee e where e.organization.id = ?) and sc.date between ? and ? ", Long.parseLong(organizationId),sDate,eDate);
		this.getDefDao().bulkUpdate("delete from ShiftConfigApply sc where sc.yearMonth = ? and sc.organization.id = ?",yearMonth,Long.parseLong(organizationId));
	}
//	public List<ShiftConfig> getConfigs(Date selectedDate, long organizationId,
//			long workingAreaId) {
//		List<ShiftConfig> list = this.getDefDao().findByQueryString(
//				"select sc from ShiftConfig sc where ((sc.employee.organization.id = ? and sc.configOrganization is null) or sc.configOrganization.id = ?) and sc.date = ? and sc.workingArea.id = ?",
//				organizationId,organizationId,selectedDate,workingAreaId);
//		return list;
//	}
	@SuppressWarnings("unchecked")
	public List<ShiftConfig> getConfigs(Date selectedDate, long organizationId) {
		List<ShiftConfig> list = this.getDefDao().findByQueryString(
				"select new ShiftConfig(sc.id,sc.date,sc.weekEnd,sc.startTime,sc.endTime,sc.nextDay,sc.isCancel,e.id,e.code,e.name,e.sex.id,e.sex.text,e.birthday,pos.id,pos.text,sc.workingArea.id) from ShiftConfig sc left join sc.employee e left join e.position pos where ((sc.employee.organization.id = ? and sc.configOrganization is null) or sc.configOrganization.id = ?) and sc.date = ?",
				organizationId,organizationId,selectedDate);
		return list;
	}
	
	public void pasteShiftConfigToCurrentday(Date selectDate,Date currentDay,String organizationId) throws Exception{
		String SelectHQL = "select sc from ShiftConfig sc where sc.date = ? and sc.employee.id in (select e.id from Employee e where e.organization.id = ?)";

		this.delAllShiftConfig(currentDay, organizationId);
		List<ShiftConfig> selectDateLsit;
		try {
			selectDateLsit = this.getDefDao().findByQueryString(
					SelectHQL, selectDate, Long.parseLong(organizationId));
			for (ShiftConfig sc : selectDateLsit) {
				ShiftConfig config = new ShiftConfig();
				config.setDate(currentDay);
				config.setEmployee(sc.getEmployee());
				config.setEndTime(sc.getEndTime());
				config.setStartTime(sc.getStartTime());
				config.setWorkingArea(sc.getWorkingArea());
				this.add(config);
			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			throw e;
		}
		
	}
	
	public void deleteOldScheduleByOrg(String year, String month,
			String employeeId) throws ParseException {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(year));
		c.set(Calendar.MONTH, Integer.parseInt(month));
		String yearMonth = year + "-"
				+ (month.length() == 1 ? "0" + month : month);
		Date sDate = Tools.stringToDate(yearMonth + "-01");
		Date eDate = Tools.stringToDate(yearMonth + "-"
				+ c.getActualMaximum(c.DAY_OF_MONTH));
		this
				.getDefDao()
				.bulkUpdate(
						"delete from ShiftConfig sc where sc.employee.id = ? and sc.date between ? and ? ",
						Long.parseLong(employeeId), sDate, eDate);
		// this.getDefDao().bulkUpdate("delete from ShiftConfigApply sc where sc.yearMonth = ? and sc.organization.id = ?",yearMonth,Long.parseLong(organizationId));
	}
	
	public void setEmployeeTime(String employeeId,Date date,String startTime,String endTime,double bunchBreakTime) throws Exception {
		ShiftConfig sc= queryShiftConfigs(employeeId,date);
		DateFormat timeFormat = new SimpleDateFormat("HH:mm");
		sc.setStartTime(timeFormat.parse(startTime));
		sc.setEndTime(timeFormat.parse(endTime));
		sc.setBunchBreakTime(bunchBreakTime);
		this.update(sc);
	}
	
}
