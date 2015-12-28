package com.fortunes.faas.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transaction.JDBCTransaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import com.fortunes.faas.model.AttShift;
import com.fortunes.faas.model.AttendanceLog;
import com.fortunes.faas.model.AttendancePlus;
import com.fortunes.faas.model.HolidayApply;
import com.fortunes.faas.model.ShiftConfig;
import com.fortunes.faas.model.AttendancePlus.AttendancePlusType;
import com.fortunes.faas.vo.AttShifts;
import com.fortunes.faas.vo.AttshiftMoreThanMidole;
import com.fortunes.faas.vo.AttshiftsOrg;
import com.fortunes.faas.vo.HolidayCountVO;
import com.fortunes.faas.vo.SickHoliday;
import com.fortunes.fjdp.Constants;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Config;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Log;
import com.fortunes.fjdp.admin.model.Config.ConfigKey;
import com.fortunes.fjdp.admin.service.ConfigService;
import com.fortunes.fjdp.admin.service.DictService;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.LogService;
import com.fortunes.fjdp.admin.service.OrganizationService;

import net.fortunes.core.ListData;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;

@Component
public class AttShiftService extends GenericService<AttShift> {
	
	@Resource EmployeeService employeeService;
	@Resource AttendanceLogService attendanceLogService;
	@Resource ShiftConfigService shiftConfigService;
	@Resource AttendancePlusService attendancePlusService;
	@Resource ConfigService configService;
	@Resource HolidayApplyService holidayApplyService;
	@Resource MonthStatisticsService monthStatisticsService;
	@Resource LogService logService;
	@Resource OrganizationService organizationService;
	@Override
	protected DetachedCriteria getConditions(String query,Map<String, String> queryMap) {
		DetachedCriteria criteria = super.getConditions(query, queryMap);
		if(queryMap != null && StringUtils.isNotEmpty(queryMap.get("employee"))){
			criteria.add(Restrictions.eq("employee.id", Long.parseLong(queryMap.get("employee"))));
		}
		if(queryMap != null && StringUtils.isNotEmpty(queryMap.get("organizations"))){
			criteria.createAlias("employee", "e");
			criteria.createAlias("e.organization", "o");
			String[] organizations = queryMap.get("organizations").split(",");
			List<Long> ids = new ArrayList<Long>();
			for(String o:organizations){
				ids.add(Long.parseLong(o));
			}
			criteria.add(Restrictions.in("o.id", ids));
		}
		if(queryMap != null && StringUtils.isNotEmpty(queryMap.get("beginDate"))){
			criteria.add(Restrictions.ge("attDate", Tools.string2Date(queryMap.get("beginDate"))));
		}
		if(queryMap != null && StringUtils.isNotEmpty(queryMap.get("endDate"))){
			criteria.add(Restrictions.le("attDate", Tools.string2Date(queryMap.get("endDate"))));
		}
		String isVer = queryMap.get("isVer");
		if(isVer.equals("ver")){
			criteria.add(Restrictions.eq("isAffirm", true));
		}
		if(isVer.equals("notVer")){
			criteria.add(Restrictions.eq("isAffirm", false));
		}
		if(queryMap != null && StringUtils.isNotEmpty(queryMap.get("notWell"))){
			if(queryMap.get("notWell").equals("true")){
				criteria.add(Expression.not(Expression.eq("statusInfo", "正常工作")));
			}
		}
		if(queryMap != null && StringUtils.isNotEmpty(queryMap.get("notRest"))){
			if(queryMap.get("notRest").equals("true")){
				criteria.add(Expression.not(Expression.eq("statusInfo", "休息")));
			}
		}
//		criteria.setProjection(Projections.alias(Projections.groupProperty("attDate"), "attDate"));
		Order.desc("attDate");
		return criteria;
	}
	@Override
	protected Order getOrder() {
		return Order.desc("attDate");
	}
	
	public void affirmAttendanceInfo(List<AttShift> list){
		for(AttShift att : list){
			this.update(att);
		}
	}
	
	public List<AttShift> getAttshiftList(String organizations){
//		String [] orgs = organizations.split(",");
		String hql = "select a from AttShift a where a.employee.organization.id in("+organizations+")";
		List<AttShift> list = this.getDefDao().findByQueryString(hql);
		return list;
	}
	
	public void mendCard(String employeeId,Date date) throws Exception {
		ShiftConfig sc = shiftConfigService.queryShiftConfigs(employeeId, date);
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
		Config start = configService.getConfigBykey(ConfigKey.START_WORK_IN_MORNING_TIME);
		Config end = configService.getConfigBykey(ConfigKey.OVER_WORK_IN_AFTERNOON_TIME);
		Date startTime = sf.parse(start.getConfigValue()+":00");
		Date endTime = sf.parse(end.getConfigValue()+":00");
		HolidayApply ha = holidayApplyService.getHolidayApplyByDateAndEmployee(date, Long.parseLong(employeeId));
		if(sc!=null&&sc.isCancel()){
			sc=null;
		}else{
			Employee e = employeeService.getEmployeeToPushCard(employeeId);
			if(e!=null&&!e.getOrganization().getType().getId().equals("organizationType_01")){
				if(date.getDay()!=0&&date.getDay()!=6){
					if(sc==null){
						sc= new ShiftConfig();
						sc.setDate(date);sc.setStartTime(startTime);sc.setEndTime(endTime);
						sc.setEmployee(e);sc.setNextDay(false);sc.setWeekEnd(false);
					}
				}
			}
		}
		AttendanceLog att = attendanceLogService.getAttendanceLogs(employeeId, date);
		AttShift attShift;
		if(sc == null){
			attShift = new AttShift();
		}else{
			attShift = this.getDateOfAttShift(sc.getEmployee().getCode(), date);
			attShift.setAffirm(false);
		}
		attCompute(employeeService.get(employeeId),sc,att,date,attShift,ha);
		this.addOrUpdate(attShift);
	}
			
	/**
	 * 天定时任务，计算员工考勤信息
	 * @param date 定时时间点
	 * @throws Exception 
	 */
	public void dayTask(Date date) throws Exception{
		List<Employee> employees = employeeService.getEmployeeToAttshit();
		List<AttShift> list = new ArrayList<AttShift>();//每一天真实的处理
		List<ShiftConfig> scs = shiftConfigService.queryShiftConfigs(date,false);
		List<AttendanceLog> attLogs = attendanceLogService.getAttendanceLogs(date);
		AttShift attShift = null;
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
		Config start = configService.getConfigBykey(ConfigKey.START_WORK_IN_MORNING_TIME);
		Config end = configService.getConfigBykey(ConfigKey.OVER_WORK_IN_AFTERNOON_TIME);
		Date startTime = sf.parse(start.getConfigValue()+":00");
		Date endTime = sf.parse(end.getConfigValue()+":00");
		try {
			for(Employee e:employees){
				if(turnAwayDeal(e,date)){//离职处理
					continue;
				}
				ShiftConfig sc = findEmployeeShiftConfig(e.getId(),scs);
				if(sc!=null&&!sc.isCancel()){//排班表没有，且没取消
				}else if(sc!=null&&sc.isCancel()){
					sc=null;
				}else{
					System.out.println(e.getOrganization().getName());
					if(!e.getOrganization().getType().getId().equals("organizationType_01")){//员工部门不是网点的
						if(date.getDay()!=0&&date.getDay()!=6){//不是周天0或周六6
							if(sc==null){
								sc= new ShiftConfig();
								sc.setDate(date);sc.setStartTime(startTime);sc.setEndTime(endTime);
								sc.setEmployee(e);sc.setNextDay(false);sc.setWeekEnd(false);
							}
						}
					}
				}
				HolidayApply ha = holidayApplyService.getHolidayApplyByDateAndEmployee(date, e.getId());
				AttendanceLog att = findAttLog(e.getId(),attLogs);
				attShift = new AttShift();
				attCompute(e,sc,att,date,attShift,ha);
				list.add(attShift);
			}
			bathAttShift(list);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	/**
	 * 计算班次工作到次日到定时任务
	 * @param date：任务开始执行之前到那天日期
	 * @throws Exception
	 */
	public void dayTaskOfShiftConfigForNextDay(ShiftConfig sc) throws Exception {
		Calendar c = Calendar.getInstance();
		c.setTime(sc.getDate());
		c.set(Calendar.DATE, c.get(Calendar.DATE)+1);
		Date nextday = c.getTime();
		//拿到所有到昨天班次为工作到次日到排班记录
//		List<ShiftConfig> scs = shiftConfigService.queryShiftConfigs(yesterday,true);
		String fnextday = Tools.date2Date(nextday);
		String fdate = Tools.dateToString(sc.getDate());
		List<AttShift> list = new ArrayList<AttShift>();
		
		int ahead = Integer.parseInt(configService.get(ConfigKey.WORK_TO_MORROW_EARLYEST_AHEAD));
		int remit = Integer.parseInt(configService.get(ConfigKey.WORK_TO_MORROW_LATEST_REMIT));
		AttShift attShift = null;
//		for(ShiftConfig sc : scs){
			c.setTime(sc.getStartTime());
			c.set(Calendar.HOUR, c.get(Calendar.HOUR)-ahead);
			String sTime = Tools.Time2String(c.getTime());
			
			c.setTime(sc.getEndTime());
			c.set(Calendar.HOUR, c.get(Calendar.HOUR)+remit);
			String eTime = Tools.Time2String(c.getTime());
			
//			sTime = fyesterday+" "+sTime;
//			eTime = fdate+" "+eTime;
			
			sTime = fdate +" "+sTime;
			eTime = fnextday+ " "+eTime;
			AttendanceLog att = attendanceLogService.getAttendanceLog(sc.getEmployee().getId()+"", Tools.string2Date(sTime), Tools.string2Date(eTime));
			attShift = new AttShift();
			HolidayApply ha = holidayApplyService.getHolidayApplyByDateAndEmployee(nextday, sc.getEmployee().getId());
			attCompute(sc.getEmployee(),sc,att,sc.getDate(),attShift,ha);
			list.add(attShift);
//		}
		bathUpdateAttShift(list,sc.getDate());
	}
	/**
	 * 从考勤原始记录里找到对应员工的考勤原始记录
	 * @param code 员工ID
	 * @param attLogs 考勤原始记录列表
	 * @return null或者对应员工考勤原始记录
	 */
	private AttendanceLog findAttLog(long code,List<AttendanceLog> attLogs){
		AttendanceLog attLog= null;
		for(AttendanceLog att : attLogs){
			if(code==Long.parseLong(att.getEmployeeCode())){
				attLog = att;
				break;
			}
		}
		return attLog;
	}
	/**
	 * 从排班列表找指定员工id的排班信息
	 * @param id
	 * @param scs
	 * @return
	 */
	private ShiftConfig findEmployeeShiftConfig(long id,List<ShiftConfig> scs){
		ShiftConfig shiftConfig = null;
		for(ShiftConfig sc : scs){
			if(id==sc.getEmployee().getId()){
				shiftConfig = sc;
				break;
			}
		}
		return shiftConfig;
	}
	/**
	 * 离职员工处理
	 * @param employee
	 * @param date
	 * @return 在date之前离职返回true，否则返回false
	 */
	private boolean turnAwayDeal(Employee employee,Date date) {
		if(employee.getTurnAway()){
			Date turnAwayDate = employee.getTurnAwayDate();
			if(date.compareTo(turnAwayDate)>0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 月定时任务
	 * @param date 
	 */
//	public void monthTask(Date date) throws Exception{
//		//统计考勤增补
//		statisticsEveryMonthOfAttPlus(date);
//		
//		Calendar c = Calendar.getInstance();
//		c.setTime(date);
//		c.roll(Calendar.MONTH, -1);//日期回滚一个月
//		if(c.get(Calendar.MONTH)==Calendar.DECEMBER){
//			  c.roll(Calendar.YEAR, -1);
//		}
//		
//		//清除原有的月统计表该月所有记录
////		monthStatisticsService.clearMonthLeave(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1);//清除掉该年该月的月统计
//		//统计考勤信息并入月库
//		List<AttShifts> list = this.getAttShifts();
////		monthStatisticsService.bathAdd(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, list);
//		//统计该月请假情况并入月库
//		List<HolidayApply> holidayApplys = statisticsEveryMonthOfLeave(date);
//		monthStatisticsService.batchSave(holidayApplys);
//	}
	/**
	 * 统计每个月的考勤增补
	 * @param date
	 * @throws Exception
	 */
//	public void statisticsEveryMonthOfAttPlus(Date date) throws Exception {
//		List<AttendancePlus> attPlusList = attendancePlusService.getMonthAttPlus(date);
//		for(AttendancePlus attPlus : attPlusList){
//			if(turnAwayDeal(attPlus.getEmployee(),attPlus.getDate())){
//				continue;
//			}
//			AttShift attShift = this.getDateOfAttShift(attPlus.getEmployee().getCode(),attPlus.getDate());
//			if(attPlus.getType().equals(AttendancePlus.AttendancePlusType.OVERTIME)){
//				AttendanceLog attLog = attendanceLogService.getAttendanceLogs(attPlus.getEmployee().getId()+"",attPlus.getDate());
//				if(!isEmpty(attLog)){
//					attShift.setCheckInTime(attLog.getCheckInTime());
//					attShift.setCheckOutTime(attLog.getCheckOutTime());
//					attShift.setOverTime(8);
//					attShift.setStatusInfo(Constants.ATTSHIFT_STATUS_OVERTIME);
//				}		
//			}else{
//				attShift.setAbsent(0);
//			}
//			attShift.setException(attPlus.getType().name());
//			addOrUpdate(attShift);
//		}
//	}
	/**
	 * 统计每个月的请假情况
	 * @param 根据这个日期，回滚一个月
	 */
//	public List<HolidayApply> statisticsEveryMonthOfLeave(Date date) throws Exception {
//		Calendar c = Calendar.getInstance();
//		c.setTime(date);
//		c.roll(Calendar.MONTH, -1);//日期回滚一个月
//		if(c.get(Calendar.MONTH)==Calendar.DECEMBER){
//			  c.roll(Calendar.YEAR, -1);
//		}
//		c.set(Calendar.DATE,1);//把日期设置为当月第一天
//		Date monthFirst = c.getTime();
//		c.set(Calendar.DATE,c.getActualMaximum(Calendar.DAY_OF_MONTH));//把日期设置为当月第一天
//		Date monthLast = c.getTime();
//		List<HolidayApply> holidayApplys = holidayApplyService.getHolidayApplys(monthFirst,monthLast);
//		for(HolidayApply ha : holidayApplys){
//			Date start = ha.getStartDate();
//			Date end = ha.getEndDate();
//			if(start.compareTo(monthFirst)<=0&&(end.compareTo(monthFirst)>=0&&end.compareTo(monthLast)<=0)){//如果start<monthFirst和monthFirst<end<monthLast
//				saveEveryMonthOfLeave(ha,monthFirst,end);
//			}
//			else if(start.compareTo(monthFirst)<=0&&end.compareTo(monthLast)>=0){//start<monthFirst和end>monthLast
//				saveEveryMonthOfLeave(ha,monthFirst,monthLast);
//			}
//			else if(start.compareTo(monthFirst)>=0&&end.compareTo(monthLast)<=0){//如果start>monthFirst和end<monthLast
//				saveEveryMonthOfLeave(ha,start,end);
//			}
//			else if((start.compareTo(monthFirst)>=0&&start.compareTo(monthLast)<=0)&&end.compareTo(monthLast)>=0){//monthFirst<start<monthLast和end>monthLast
//				saveEveryMonthOfLeave(ha,start,monthLast);
//			}
//		}
//		return holidayApplys;
//	}
//	public void saveEveryMonthOfLeave(HolidayApply ha,Date start,Date end) throws Exception {
//		Employee leaveApply = ha.getLeaveApply();
//		int days = 0;
//		List<ShiftConfig> shiftConfigs = shiftConfigService.queryShiftConfigs(leaveApply.getId()+"", start, end);
//		for(ShiftConfig shiftConfig : shiftConfigs){
//			if(turnAwayDeal(leaveApply,shiftConfig.getDate())){
//				continue;
//			}
//			AttShift attShift = this.getDateOfAttShift(leaveApply.getCode(),shiftConfig.getDate());
//			attShift.setAbsent(0);
//			attShift.setHolidayType(ha.getLeaveType().getId());
//			attShift.setHoliday(1);
//			attShift.setLate(0);
//			attShift.setEarly(0);
//			attShift.setLateTime(0);
//			attShift.setEarlyTime(0);
//			days++;
//			attShift.setStatusInfo(ha.getLeaveType().getText());
//			addOrUpdate(attShift);
//		}
////		monthStatisticsService.saveMonthStatistics(ha,days);//保存该员工的该月的请假情况
//	}
//	public void cancelHoliday(HolidayApply ha,Date startDate,Date endDate) throws Exception{
//		Employee leaveApply = ha.getLeaveApply();
//		int days = 0;
//		List<ShiftConfig> shiftConfigs = shiftConfigService.queryShiftConfigs(leaveApply.getId()+"", startDate, endDate);
//		for(ShiftConfig shiftConfig : shiftConfigs){
//			if(turnAwayDeal(leaveApply,shiftConfig.getDate())){
//				continue;
//			}
//			AttShift attShift = this.getDateOfAttShift(leaveApply.getCode(),shiftConfig.getDate());
//			if(attShift!=null){
//				delDayAndEmployee(Long.toString(attShift.getId()));
//			}
//			AttendanceLog att = attendanceLogService.getAttendanceLog(shiftConfig.getEmployee().getId()+"", startDate,endDate);
//			
//			attCompute(shiftConfig.getEmployee(),shiftConfig,att,shiftConfig.getDate(),attShift);
//			days++;
//			add(attShift);
//		}
//	}
//	public AttShift statisticsEveryDayAttPuls(AttShift attShift,AttendancePlusType attendancePlusType){
//		attShift.setException(attendancePlusType.name());
//		attShift.setExceptionTime(1);
//		attShift.setAbsent(0);
//		attShift.setWeekday(1);
//		attShift.setLate(0);
//		attShift.setEarly(0);
//		attShift.setLateTime(0);
//		attShift.setEarlyTime(0);
//		AttendancePlus att = new AttendancePlus();
//		att.setType(attendancePlusType);
//		attShift.setStatusInfo(getLeaveText(att));
//		return attShift;
//	}
	/**
	 * 判断该员工是否当天打卡否
	 * @param attLog
	 * @return
	 */
	private boolean isEmpty(AttendanceLog attLog){
		if(null==attLog){
			return true;
		}
		return false;
	}
	
	private String getLeaveText(AttendancePlus attPlus){
		if (attPlus.getType().equals(AttendancePlus.AttendancePlusType.OVERTIME)){
			return Constants.LEAVE_TYPE_OVERTIME;
		}else if (attPlus.getType().equals(AttendancePlus.AttendancePlusType.TRAVEL)){
			return Constants.LEAVE_TYPE_TRAVEL;
		}else if(attPlus.getType().equals(AttendancePlus.AttendancePlusType.OTHER)){
			return Constants.LEAVE_TYPE_OTHER;
		}else{
			return Constants.LEAVE_TYPE_LEAVE;
		}
	}
	public AttShift holidayAnalyse(Employee e,ShiftConfig sc,AttShift attShift,HolidayApply ha,Date attDate){
		//如果当前请假人为网点
		String dictId = ha.getLeaveType().getId();
		if((attDate.getDay()==0&&((sc==null&&ha.getLeaveType().getId().equals("leaveType_03_1"))||(sc==null&&ha.getLeaveType().getId().equals("leaveType_01"))))||(attDate.getDay()==6&&((sc==null&&ha.getLeaveType().getId().equals("leaveType_03_1"))||(sc==null&&ha.getLeaveType().getId().equals("leaveType_01"))))){
			attShift.setRestday(1);
			attShift.setStatusInfo(Constants.ATTSHIFT_STATUS_RESTDAY);
		}else{
			if(dictId.contains("leaveType")){
				attShift.setHolidayType(dictId);
				if(ha.getLeaveDays()>=1){
					attShift.setHoliday(1);
					if(attDate.equals(ha.getEndDate())){
						Double days = ha.getLeaveDays();
						double small = days-days.intValue();
						attShift.setHoliday(small>0?small:1);
					}
				}else{
					attShift.setHoliday(ha.getLeaveDays());
				}
			}
			
	//////////////////////////////////////////////
//			else{
//				if(dictId.equals("TRAVEL")||dictId.equals("TRAINING")){
//					attShift.setException(dictId);
//					if(ha.getLeaveDays()>=1){
//						attShift.setExceptionTime(1);
//						if(attDate.equals(ha.getEndDate())){
//							Double days = ha.getLeaveDays();
//							double small = days-days.intValue();
//							attShift.setExceptionTime(small>0?small:1);
//						}
//					}else{
//						attShift.setExceptionTime(ha.getLeaveDays());
//					}
//					 
//				} else {
//					 	attShift.setHolidayType(dictId);
//					if(ha.getLeaveDays()>=1){
//						attShift.setHoliday(1);
//						if(attDate.equals(ha.getEndDate())){
//							Double days = ha.getLeaveDays();
//							double small = days-days.intValue();
//							attShift.setHoliday(small>0?small:1);
//						}
//					}else{
//						attShift.setHoliday(ha.getLeaveDays());
//					}		
//				 
//				}
 
		/////////////////2012-10-17///解决“培训”与“出差”统计到假期中的问题////////////////////////////////////	
			
			
				
			else{
  			if(dictId.equals("a333fc17-3b11-4dc2-b06d-3048a6cb39eb")){//出差
					attShift.setException("TRAVEL");
					if(ha.getLeaveDays()>=1){
						attShift.setExceptionTime(1);
						if(attDate.equals(ha.getEndDate())){
							Double days = ha.getLeaveDays();
							double small = days-days.intValue();
							attShift.setExceptionTime(small>0?small:1);
						}
					}else{
						attShift.setExceptionTime(ha.getLeaveDays());
					}
					 
				}
				if(dictId.equals("62031966-6443-4561-a51f-fe70a7e05410")){//培训
					attShift.setException("TRAINING");
					if(ha.getLeaveDays()>=1){
						attShift.setExceptionTime(1);
						if(attDate.equals(ha.getEndDate())){
							Double days = ha.getLeaveDays();
							double small = days-days.intValue();
							attShift.setExceptionTime(small>0?small:1);
						}
					}else{
						attShift.setExceptionTime(ha.getLeaveDays());
					}
				}
				else {
					if(!dictId.equals("62031966-6443-4561-a51f-fe70a7e05410")&&!dictId.equals("a333fc17-3b11-4dc2-b06d-3048a6cb39eb")){
					attShift.setHolidayType(dictId);
					if(ha.getLeaveDays()>=1){
						attShift.setHoliday(1);
						if(attDate.equals(ha.getEndDate())){
							Double days = ha.getLeaveDays();
							double small = days-days.intValue();
							attShift.setHoliday(small>0?small:1);
						}
					}else{
						attShift.setHoliday(ha.getLeaveDays());
					}		
				  }
				}
				
				
		//////////////////2012-10-17///////////////////
				
				
			}
			attShift.setStatusInfo(ha.getLeaveType().getText());
		}
		return attShift;
	}
	
	public static void main(String[] args) throws Exception {
		Double as = 0.5;
		double sa = as-as.intValue();
//		System.out.println(sa);
		AttShiftService dd = new AttShiftService();
//		AttshiftsOrg ao = dd.attShiftOrgStatistics("2", "7", "2012");
//		AttshiftsOrg ao = dd.getAttshiftsOrg(2+"", "2012", "8");
//		System.out.println(ao.getDxTotal()+"===+"+ao.getLongOrMiddleTermTotal());
	}
	public AttShift overTimeAnalyse(Employee  e,ShiftConfig sc,AttShift attShift,HolidayApply ha,Date attDate){
		String date = Tools.date2Date(attDate);
		String startTime = date+" "+ha.getOverTimeStart()+":00";
		String endTime = date+" "+ha.getOverTimeEnd()+":00";
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		double overTimes = 0.0;
		try {
			Date start = sf.parse(startTime);
			Date end = sf.parse(endTime);
			overTimes = (end.getTime()-start.getTime())/1000/60/60;
			attShift.setOverTime(overTimes);
			attShift.setException("OVERTIME");
			attShift.setStatusInfo("加班<span style='color:blue'>(<font color:blue>"+ha.getOverTimeStart()+"~~"+ha.getOverTimeEnd()+"</font>)</span>");
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return attShift;
	}
	/**
	 * 考勤计算
	 * @param e 员工对象
	 * @param sc 对应员工的排班情况
	 * @param attLog 对应员工的考勤原始记录
	 * @param attDate 考勤日期
	 * @param attParam 考勤参数
	 * @return AttShift 考勤明细对象
	 */
	private AttShift attCompute(Employee e,ShiftConfig sc,AttendanceLog attLog,Date attDate,AttShift attShift,HolidayApply ha){
		attShift.setAttDate(attDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(attDate);
		attShift.setYear(cal.get(Calendar.YEAR));
		attShift.setMonth(cal.get(Calendar.MONTH)+1);
		attShift.setEmployee(e);
		List<AttendancePlus> atts = attendancePlusService.getPlusInfosByEmployee(Long.toString(e.getId()), attDate, attDate);
		//员工有请假，没有考勤记录，那么考勤记为请假
		if((ha!=null && ha.getApplyType().equals("holiday")&&ha.getCancelDate()!=null&&ha.getCancelDate().compareTo(attDate)>=0)||(ha!=null && ha.getApplyType().equals("holiday")&&ha.getCancelDate()==null)){
			attShift.setAbsent(0);
			attShift.setWeekday(0);
			if(ha.getLeaveDays()!=0){
				attShift = holidayAnalyse(e,sc,attShift,ha,attDate);
			}
			return attShift;
		}else if(ha!=null&&ha.getApplyType().equals("overTime")){
			attShift.setAbsent(0);
			attShift = overTimeAnalyse(e, sc, attShift, ha, attDate);
			return attShift;
		}else if(atts.size()>0 && atts.get(0) != null){
			AttendancePlus att =atts.get(0);
			attShift.setException(att.getType().name());
			if(att.getType().name().equals("LEAVE")){
				attShift.setHoliday(1);
				if(att.getLeaveType()!=null){
					attShift.setHolidayType(att.getLeaveType().getId());
				}
				attShift.setStatusInfo(att.getReason()==null?"请假":att.getReason());
			}else{
				attShift.setStatusInfo(getLeaveText(att));
			}
			attShift.setAbsent(0);
			attShift.setExceptionTime(1);
			overTimeCheck(attShift,att,attLog);
			return attShift;
		}
		// 1. 该员工该天没有排班，而且没有考勤原始记录，考勤明细记其为休息日
		else if(null==sc&&isEmpty(attLog)){
			attShift.setRestday(1);
			attShift.setStatusInfo(Constants.ATTSHIFT_STATUS_RESTDAY);
			return attShift;
		}
		// 2. 该员工该天没有排班，但是当天却来上班了有考勤原始记录，记该员工为加班吗
		else if(null==sc&&!isEmpty(attLog)){
			attShift.setRestday(1);
			attShift.setStatusInfo(Constants.ATTSHIFT_STATUS_RESTDAY);
			return attShift;
		}
		// 3. 该员工该天有排班，但是没有考勤原始记录，记该员工为旷工
		else if(null!=sc&&isEmpty(attLog)){
			attShift.setAbsent(1);
			attShift.setWeekday(1);
			attShift.setStatusInfo(Constants.ATTSHIFT_STATUS_ABSENT);
		}
		// 4. 该员工该天有排班，而且有考勤原始记录
		else if(null!=sc&&!isEmpty(attLog)){
			String statusInfo = Constants.ATTSHIFT_STATUS_WEEKDAY;
			Date checkInTime = attLog.getCheckInTime();//获取原始考勤记录签入签出时间
			Date checkOutTime = attLog.getCheckOutTime();
			if(checkInTime.compareTo(checkOutTime)==0){//只打卡一次
				attShift.setCheckInTime(checkInTime);
				attShift.setStartTime(sc.getStartTime());
				attShift.setEndTime(sc.getEndTime());
				double absent = Double.parseDouble(configService.get(ConfigKey.ATTSHIFT_PUSH_CARD_ONLYONE));
				attShift.setAbsent(absent);
				attShift.setStatusInfo(Constants.ATTSHIFT_STATUS_ONLYONE+absent+Constants.ATTSHIFT_UNIT_DAY);
				return attShift;
			}
			attShift.setCheckInTime(checkInTime);
			attShift.setCheckOutTime(checkOutTime);
			
			Date startTime = sc.getStartTime();//获取该员工排班信息规定的上下班时间
			Date endTime = sc.getEndTime();
		
			Calendar c = Calendar.getInstance();
			c.setTime(checkInTime);//格式化签入签出时间
			int checkInTime_hour = c.get(Calendar.HOUR_OF_DAY);
			int checkInTime_minute = c.get(Calendar.MINUTE);
			c.setTime(checkOutTime);
			int checkOutTime_hour = c.get(Calendar.HOUR_OF_DAY);
			int checkOutTime_minute = c.get(Calendar.MINUTE);
			
			//----迟到----
			c.setTime(startTime);
			int startTime_hour = c.get(Calendar.HOUR_OF_DAY);
			int startTime_minute = c.get(Calendar.MINUTE);
			int lateTime = (checkInTime_hour - startTime_hour)*60 + checkInTime_minute - startTime_minute;//为正数时迟到
			//迟到时间大于系统指定的时间时，记录相关旷工信息
			double sysLateTime = Double.parseDouble(configService.get(ConfigKey.LATE_GREATER_THAN_TIME));
			if(lateTime>sysLateTime){
				double lateGreaterThanTimeForParam = Double.parseDouble(configService.get(ConfigKey.LATE_GREATER_THAN_TIME_FOR_PARAM));
				attShift.setAbsent(lateGreaterThanTimeForParam);
				logger.info(e.getName()+":迟到"+lateTime+"分钟，超过系统指定的"+
						sysLateTime+"分钟，计算为旷工"+lateGreaterThanTimeForParam+"天");
				statusInfo = Constants.ATTSHIFT_STATUS_LATELIMIT+lateGreaterThanTimeForParam+Constants.ATTSHIFT_UNIT_DAY;
			}else{
				attShift.setLateTime(lateTime>0?lateTime:0);
				double late = lateTime>0?1:0;
				if(statusInfo.equals("正常工作")&&late>0){
					statusInfo = "";
				}
				statusInfo += late>0?Constants.ATTSHIFT_STATUS_LATE:"";
				attShift.setLate(late);
			}
			
			//----早退----
			c.setTime(endTime);
			int endTime_hour = c.get(Calendar.HOUR_OF_DAY);
			int endTime_minute = c.get(Calendar.MINUTE);
			int earlyTime = (endTime_hour - checkOutTime_hour)*60 + endTime_minute - checkOutTime_minute;//为正数时早退
			//早退时间大于系统指定的时间时，记录相关旷工信息
			double sysEarlyTime =Double.parseDouble(configService.get(ConfigKey.EARLY_GREATER_THAN_TIME));
			if(earlyTime>sysEarlyTime){
				double earlyGreaterThanTimeForParam = Double.parseDouble(configService.get(ConfigKey.EARLY_GREATER_THAN_TIME_FOR_PARAM));
				attShift.setAbsent(earlyGreaterThanTimeForParam+attShift.getAbsent());
				logger.info(e.getName()+"早退"+earlyTime+"分钟，超过系统指定的"+
							sysLateTime+"分钟，计算为旷工"+earlyGreaterThanTimeForParam+"天");
				statusInfo = Constants.ATTSHIFT_STATUS_EARLYLIMIT+earlyGreaterThanTimeForParam+Constants.ATTSHIFT_UNIT_DAY;
				if(attShift.getAbsent()==1){
					statusInfo = "迟到早退旷工1天";
				}
			}else{
				attShift.setEarlyTime(earlyTime>0?earlyTime:0);
				double early = earlyTime>0?1:0;
				if(statusInfo.equals("正常工作")&&early>0){
					statusInfo = "";
				}
				statusInfo += early>0?Constants.ATTSHIFT_STATUS_EARLY:"";
				attShift.setEarly(early);
			}
			attShift.setStatusInfo(statusInfo);
			attShift.setWeekday(1);
		}
		attShift.setStartTime(sc.getStartTime());
		attShift.setEndTime(sc.getEndTime());
		return attShift;
	}
	
	
	/**
	 * 员工考勤信息统计
			////"输出报表" 按钮
	 * @param queryMap
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ListData<AttShifts> attShiftStatistics(Map<String,String> queryMap,int start,int limit) throws Exception{
		StringBuffer HQL = new StringBuffer();
		StringBuffer SELETHQL = new StringBuffer();
		SELETHQL.append("SELECT new com.fortunes.faas.vo.AttShifts(a.employee.id AS employeeId,a.employee.code AS employeeCode,a.employee.name AS employeeName")
				.append(",a.employee.salary as salary,a.employee.regionalAllowance as regionalAllowance," +
						"(CASE WHEN a.employee.organization.parent.id =1 THEN a.employee.organization.name ELSE a.employee.organization.parent.name END) AS institution," +
						"(CASE WHEN a.employee.organization IS NULL THEN '' ELSE a.employee.organization.name END) AS deptName")
				.append(",a.employee.organization.id AS organizationId")
				.append(",sum(a.late) AS lates,sum(a.early) AS earlys")
				.append(",sum(a.absent) AS absents,sum(a.lateTime) AS lateTimes,sum(a.earlyTime) AS earlyTimes")
				.append(",sum(a.weekday) AS weekday,sum(a.restday) AS restday")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_01'  THEN a.holiday ELSE 0 END) AS leave")
				.append(",sum(CASE WHEN (a.holidayType = 'leaveType_02' or a.holidayType = 'b45ed16b-f9ec-4e87-8a87-1cb4fb2d859e'  or a.holidayType = 'bce240b5-1294-4e8f-a1bb-74152f0e0ff4') THEN holiday ELSE 0 END) AS sickLeave")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_03_1' THEN a.holiday ELSE 0 END) AS annualLeave")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_03_2' THEN a.holiday ELSE 0 END) AS homeLeave")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_04_1' THEN a.holiday ELSE 0 END) AS maternityLeave")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_04_4' THEN a.holiday ELSE 0 END) AS familyPlanningLeave")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_03_3' THEN a.holiday ELSE 0 END) AS nurseLeave")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_03_4' THEN a.holiday ELSE 0 END) AS marriageLeave")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_03_5' THEN a.holiday ELSE 0 END) AS funeralLeave")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_04_2' THEN a.holiday ELSE 0 END) AS feedLeave")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_04_3' THEN a.holiday ELSE 0 END) AS lookAfterLeave")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_05' THEN a.holiday ELSE 0 END) AS injuryLeave")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_04_5' THEN a.holiday ELSE 0 END) AS takeCareLeave")
				.append(",sum(a.holiday) AS holidays")
				.append(",sum(CASE WHEN a.exception = 'TRAVEL' THEN exceptionTime ELSE 0 END) AS travel")
				.append(",sum(CASE WHEN a.exception = 'OVERTIME' THEN overTime ELSE 0 END) AS overTime)");
		StringBuffer SELECTCOUNT = new StringBuffer("SELECT COUNT(DISTINCT a.employee.id)");
		HQL.append(" FROM AttShift AS a");
		HQL.append(" WHERE ");
		String reportType = queryMap.get("reportType");
		if(reportType.equals("defined")){
			HQL.append(" a.attDate BETWEEN '").append(queryMap.get("startDate")).append("' and '").append(queryMap.get("endDate")).append("'");
		}
		else if(reportType.equals("year")){
			HQL.append(" year = ").append(Integer.parseInt(queryMap.get("ayear")));
		}else{
			HQL.append(" year = ").append(Integer.parseInt(queryMap.get("year"))).append(" and month = ")
				.append(Integer.parseInt(queryMap.get("month")));
		}
		String searchType = queryMap.get("searchType");
		if(searchType.equals("employee")&&StringUtils.isNotEmpty(queryMap.get("employee"))){
			HQL.append(" and a.employee.id = ").append(Long.parseLong(queryMap.get("employee")));
		}
		else if(StringUtils.isNotEmpty(queryMap.get("organizations"))){
			HQL.append(" and a.employee.organization IN (").append(queryMap.get("organizations")).append(")");
		}
		String organizations = queryMap.get("organizations");
		String[] orgId = organizations.split(",");
		String ORDERBY;
		if(orgId.length==1){
			ORDERBY  = "  order by a.employee.name";
		}else{
			ORDERBY = "  order by a.employee.organization.parent.id";
		}
		
		String GROUPBY = " GROUP BY a.employee.id,a.employee.organization.parent.id,a.employee.code,a.employee.name,a.employee.salary,a.employee.regionalAllowance,a.employee.organization.parent.name,a.employee.organization,a.employee.organization.name "+ORDERBY;
		int total = 0;
		if(limit>0){
			List list = getDefDao().findByQueryString(SELECTCOUNT.toString()+HQL.toString());
			total = Integer.parseInt(list.get(0).toString());
		}
		List<AttShifts> attShifts = getDefDao().findByQueryString(SELETHQL.append(HQL).append(GROUPBY).toString(),start,limit);
		return new ListData<AttShifts>(attShifts,total);
	}
	
	
	
	public AttshiftsOrg attShiftOrgStatistics(String orgId,String year,String month) throws Exception{
		StringBuffer HQL = new StringBuffer();
		StringBuffer SELETHQL = new StringBuffer();
		SELETHQL.append("SELECT new com.fortunes.faas.vo.AttshiftsOrg(o.id AS orgId,o.name AS orgName")
				.append(",sum(CASE WHEN e.peopleType_id = 'peopleType_01' THEN 1 ELSE 0 END) AS longOrMiddleTermTotal ,sum(CASE WHEN e.peopleType_id = 'peopleType_03' THEN 1 ELSE 0 END) AS shortTermTotal")
				.append(",sum(CASE WHEN e.peopleType_id = 'peopleType_02' THEN 1 ELSE 0 END) AS laoWuTotal")
				.append(",sum(CASE WHEN e.peopleType_id = 'ddf3bbb8-e3ad-4213-a9f6-dab75722f3a2' THEN 1 ELSE 0 END) AS dxTotal") //定向招聘劳动合同员工
				.append(",sum(CASE WHEN e.peopleType_id = '6938ac58-c6ce-4328-adbc-0745cbf49fb9' THEN 1 ELSE 0 END) AS outOfSystemTotal") //系统外员工
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_03_1' THEN holiday ELSE 0 END) AS annualLeave")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_03_2' THEN holiday ELSE 0 END) AS familyPlanningLeave")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_04_1' THEN holiday ELSE 0 END) AS maternityLeave")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_02' THEN holiday ELSE 0 END) AS sickBaoLeave")
				.append(",sum(CASE WHEN a.holidayType = 'leaveType_01' THEN holiday ELSE 0 END) AS thingsLeave")
				.append(",sum(a.absent) AS absents")
				.append(",sum(a.late) AS lates,sum(a.early) AS earlys");
//				.append(",sum(a.weekday) AS weekday,sum(a.restday) AS restday")
//				.append(",sum(CASE WHEN a.holidayType = 'leaveType_04_4' THEN holiday ELSE 0 END) AS familyPlanningLeave")
//				.append(",sum(CASE WHEN a.holidayType = 'leaveType_03_3' THEN holiday ELSE 0 END) AS nurseLeave")
//				.append(",sum(CASE WHEN a.holidayType = 'leaveType_03_4' THEN holiday ELSE 0 END) AS marriageLeave")
//				.append(",sum(CASE WHEN a.holidayType = 'leaveType_03_5' THEN holiday ELSE 0 END) AS funeralLeave")
//				.append(",sum(CASE WHEN a.holidayType = 'leaveType_04_2' THEN holiday ELSE 0 END) AS feedLeave")
//				.append(",sum(CASE WHEN a.holidayType = 'leaveType_04_3' THEN holiday ELSE 0 END) AS lookAfterLeave")
//				.append(",sum(CASE WHEN a.holidayType = 'leaveType_05' THEN holiday ELSE 0 END) AS injuryLeave")
//				.append(",sum(a.holiday) AS holidays");
//				.append(",sum(CASE WHEN a.exception = 'TRAVEL' THEN exceptionTime ELSE 0 END) AS travel")
//				.append(",sum(CASE WHEN a.exception = 'OVERTIME' THEN overTime ELSE 0 END) AS overTime)");
		StringBuffer SELECTCOUNT = new StringBuffer("SELECT COUNT(DISTINCT a.employee.id)");
		HQL.append(" FROM AttShift AS a ");
		HQL.append(" left join Employee a.employee as e left join e.organization as o ");
		HQL.append(" WHERE a.employee.id = e.id and e.organization.id = o.id");
//		String reportType = queryMap.get("reportType");
//		if(reportType.equals("defined")){
//			HQL.append(" a.attDate BETWEEN '").append(queryMap.get("startDate")).append("' and '").append(queryMap.get("endDate")).append("'");
//		}
//		else if(reportType.equals("year")){
//			HQL.append(" year = ").append(Integer.parseInt(queryMap.get("ayear")));
//		}else{
			HQL.append(" and a.year = ").append(year).append(" and a.month = ")
				.append(month);
//		}
//		String searchType = queryMap.get("searchType");
//		if(StringUtils.isNotEmpty(queryMap.get("organizations"))){
			HQL.append(" and a.employee.organzation = ").append(orgId);
//		}
		String GROUPBY = " GROUP BY o.id,o.name";
//		int total = 0;
//		if(limit>0){
//			List list = getDefDao().findByQueryString(SELECTCOUNT.toString()+HQL.toString());
//			total = Integer.parseInt(list.get(0).toString());
//		}
//		AttshiftsOrg attShifts = getDefDao().findByQueryString(SELETHQL.append(HQL).append(GROUPBY).toString(),start,limit);
		String sql = SELETHQL.append(HQL).append(GROUPBY).toString();
		AttshiftsOrg attShifts = (AttshiftsOrg) this.getDefDao().findByQueryString(SELETHQL.append(HQL).append(GROUPBY).toString()).get(0);
		return attShifts;
	}
	
	///////////////输出报表
	@SuppressWarnings("unchecked")
	public List<AttShifts> getAttShifts(){
		StringBuffer HQL = new StringBuffer();
		StringBuffer SELETHQL = new StringBuffer();
		SELETHQL.append("SELECT new com.fortunes.faas.vo.AttShifts(a.employee.id AS employeeId,a.employee.code AS employeeCode,a.employee.name AS employeeName")
				.append(",a.employee.salary as salary,(CASE WHEN a.employee.organization.parent.id=1 THEN a.employee.organization.name ELSE a.employee.organization.parent.name END) AS deptName,(CASE WHEN a.employee.organization IS NULL THEN '' ELSE a.employee.organization.name END) AS deptName")
				.append(",sum(a.late) AS lates,sum(a.early) AS earlys")
				.append(",sum(a.absent) AS absents,sum(a.lateTime) AS lateTimes,sum(a.earlyTime) AS earlyTimes")
				.append(",sum(a.weekday) AS weekday,sum(a.restday) AS restday")
				.append(",sum(CASE WHEN a.holidayType = 'LEAVE' THEN holiday ELSE 0 END) AS leave")
				.append(",sum(CASE WHEN a.holidayType = 'SICK_LEAVE' THEN holiday ELSE 0 END) AS sickLeave")
				.append(",sum(CASE WHEN a.holidayType = 'ANNUAL_LEAVE' THEN holiday ELSE 0 END) AS annualLeave")
				.append(",sum(CASE WHEN a.holidayType = 'HOME_LEAVE' THEN holiday ELSE 0 END) AS homeLeave")
				.append(",sum(CASE WHEN a.holidayType = 'MATERNITY_LEAVE' THEN holiday ELSE 0 END) AS maternityLeave")
				.append(",sum(CASE WHEN a.holidayType = 'FAMILYPLANNING_LEAVE' THEN holiday ELSE 0 END) AS familyPlanningLeave")
				.append(",sum(CASE WHEN a.holidayType = 'NURSE_LEAVE' THEN holiday ELSE 0 END) AS nurseLeave")
				.append(",sum(CASE WHEN a.holidayType = 'MARRIAGE_LEAVE' THEN holiday ELSE 0 END) AS marriageLeave")
				.append(",sum(CASE WHEN a.holidayType = 'FUNERAL_LEAVE' THEN holiday ELSE 0 END) AS funeralLeave")
				.append(",sum(CASE WHEN a.holidayType = 'FEED_LEAVE' THEN holiday ELSE 0 END) AS feedLeave")
				.append(",sum(CASE WHEN a.holidayType = 'LOOKAFTER_LEAVE' THEN holiday ELSE 0 END) AS lookAfterLeave")
				.append(",sum(CASE WHEN a.holidayType = 'INJURY_LEAVE' THEN holiday ELSE 0 END) AS injuryLeave")
				////2012-09-24//////////////////////////
				.append(",sum(CASE WHEN a.holidayType = 'TAKECARE_LEAVE' THEN holiday ELSE 0 END) AS takeCareLeave")
				
				.append(",sum(a.holiday) AS holidays")
				.append(",sum(CASE WHEN a.exception = 'TRAVEL' THEN exceptionTime ELSE 0 END) AS travel")
				.append(",sum(CASE WHEN a.exception = 'OVERTIME' THEN exceptionTime ELSE 0 END) AS overTime)");
		HQL.append(" FROM AttShift AS a");
		String GROUPBY = " GROUP BY a.employee.id,a.employee.organization.parent.id order by a.employee.organization.parent.id";
		List<AttShifts> list = getDefDao().findByQueryString(SELETHQL.append(HQL).append(GROUPBY).toString());
		return list;
	}
	/**
	 * 批量增加考勤明细
	 * @param list
	 * @throws Exception
	 */
	public void bathAttShift(List<AttShift> list) throws Exception {
		int count = 0;
		for(AttShift att : list){
			add(att);
			++count;
		}
	}
	
	public AttShift overTimeCheck(AttShift attshift,AttendancePlus att,AttendanceLog attLog){
		if(att.getType().name().equals("OVERTIME")){
			Date overtimeStart = att.getOverTimeStart();
			Date overTimeEnd = att.getOverTimeEnd();
			Date checkInTime = attLog.getCheckInTime();
			Date checkOutTime = attLog.getCheckOutTime();
			if(checkInTime.compareTo(overtimeStart)<=0&&checkOutTime.compareTo(overTimeEnd)>=0){
				SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
				String sT = sf.format(overtimeStart);
				String eT = sf.format(overTimeEnd);
				attshift.setOverTime((overTimeEnd.getTime()-overtimeStart.getTime())/1000/60/60);
				attshift.setStatusInfo(attshift.getStatusInfo()+"<span style='color:blue'>(<font color:blue>"+sT+"~~"+eT+"</font>)</span>");
				return attshift;
			}else{
				attshift.setStatusInfo("加班时间与打卡时间不符，请检查");
				return attshift;
			}
		}
		return attshift;
	}
	
	public void bathUpdateAttShift(List<AttShift> list,Date date) throws Exception {
		delDayAttShift(list,date);
		bathAttShift(list);
	}
	public void delDayAttShift(List<AttShift> list, Date date) throws Exception {
		JdbcTemplate jdbcTemplate = getDefDao().getJdbcTemplate();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String fdate = df.format(date);
		for(AttShift attShift : list){
			String sql = "delete from attshift where attDate ='"+fdate+"'"+" and employee_id = "+attShift.getEmployee().getId();
			jdbcTemplate.execute(sql);
		}
	}
	/**
	 * 获取指定日期的员工考勤信息
	 * @param code
	 * @param date
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AttShift getDateOfAttShift(String code,Date date){
		String HQL = "SELECT a FROM AttShift AS a WHERE a.attDate = ? AND a.employee.code= ?";
		List<AttShift> list = this.getDefDao().findByQueryString(HQL, date,code);
		return null!=list&&list.size()>0?list.get(0):new AttShift();
	}
	
	public void delDayAttShift(Date date) throws Exception {
		JdbcTemplate jdbcTemplate = getDefDao().getJdbcTemplate();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String fdate = df.format(date);
		String sql = "delete from Attshift where attDate ='"+fdate+"'";
		jdbcTemplate.execute(sql);
	}
	
	public void delDayAndEmployee(String id,Date date){
		JdbcTemplate jdbcTemplate = getDefDao().getJdbcTemplate();
		String sql = "delete from attshift where employee_id ='"+id+"' and attDate ='"+Tools.dateToString(date)+"'";
		jdbcTemplate.execute(sql);
	}
	
	public AttShift getAttShiftByDateAndId(String employeeId, Date date){
		String hql = "Select a from AttShift as a where a.attDate = ? and a.employee.id = ?";
		List<AttShift> list = this.getDefDao().findByQueryString(hql, date,Long.parseLong(employeeId));
		return null != list&&list.size()>0?list.get(0):new AttShift();
	}
	
	
	public void analyAttshiftForADay(Date date){
		JDBCTransaction jt = new JDBCTransaction(null, null);
		try{
			this.delDayAttShift(date);
			this.dayTask(date);
		}catch(Exception e){
			
		}
		
	}
	
	public void analyAttshiftForDayAndEmployees(Date date,List<Employee> employees){
		try{
			for(Employee em : employees){
				String empId = Long.toString(em.getId());
				this.delDayAndEmployee(empId,date);
				this.mendCard(empId, date);
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public HolidayCountVO analyseYearHoliday(int year,long employeeId){
		String sql = "select new com.fortunes.faas.vo.HolidayCountVO(a.employee.id,a.employee.code,sum(CASE WHEN a.holidayType = 'leaveType_03_1' THEN holiday ELSE 0 END)) from AttShift a where a.year = ? and a.employee.id = ? group by a.employee.id,a.employee.code";
		List<HolidayCountVO> list = this.getDefDao().findByQueryString(sql, year,employeeId);
		return list.size()>0?list.get(0):null;
	}
	
	
	public void analyAttshiftForEmployee(Date date,String employeeId){
		try{
			this.delDayAndEmployee(employeeId,date);
			this.mendCard(employeeId, date);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	/**
	 * 全行
	 * 人员考勤统计
	 * */
	@SuppressWarnings("unchecked")
	public AttshiftsOrg getAttshiftsOrg(String orgId,Map<String,String> queryMap){
		JdbcTemplate jdbcTemplate = this.getDefDao().getJdbcTemplate();
		StringBuffer HQL = new StringBuffer();
		
//		HQL.append("SELECT count(DISTINCT(CASE WHEN a.holiday>0 THEN e.id ELSE 0 END) ) as holidayPersonTotal");
//		HQL.append(",COUNT(distinct(CASE WHEN (e.peopleType_id = 'peopleType_01' and a.holiday >0) THEN e.id ELSE 0 END)) AS longOrMiddleTermTotal ");
//		HQL.append(",COUNT(distinct(CASE WHEN (e.peopleType_id = 'peopleType_03' and a.holiday >0) THEN e.id ELSE 0 END)) AS shortTermTotal");
//		HQL.append(",COUNT(distinct(CASE WHEN (e.peopleType_id = 'peopleType_02' and a.holiday >0) THEN e.id ELSE 0 END)) AS laoWuTotal");
//		HQL.append(",COUNT(distinct(CASE WHEN (e.peopleType_id = 'f7314da5-81ac-4451-9d2a-bfeacaf20ca1' and a.holiday >0) THEN e.id ELSE 0 END)) AS dxTotal");
//		HQL.append(",COUNT(distinct(CASE WHEN (e.peopleType_id = '6cd0deec-60ae-4209-863e-d2852735eedf' and a.holiday >0) THEN e.id ELSE 0 END)) AS outOfSystemTotal");
//		
//		HQL.append(",COUNT(distinct(CASE WHEN (e.position_id = 'position_05' and a.holiday >0) THEN e.id ELSE 0 END)) AS cabinetLeaves");
//		HQL.append(",COUNT(distinct(CASE WHEN (e.position_id IN ('16758e01-aaaa-4125-b8f6-ae0e44953e60','2b92d83d-e001-465e-bc86-e78b727762fe','2d4de503-1026-443e-8307-6a3e0d615b22','61df9e5b-2aca-4ef9-a06a-af576c03546b','667b7990-676d-4e05-8172-209721c059f4','8a5b1366-23dc-4e04-aa86-39009a536364','b7acd412-cca5-4d6e-abc5-3d027915feab','c6a6006a-8719-4718-bb83-c4e6ad2a9c45','d6839df1-20da-484a-8b1a-9f979826df58','d730ddd3-0b64-4fa3-a132-dc95b8b58ba9','f3d99a96-2c9a-41c0-9db0-e0bf57b484f3','position_03','position_07')  and a.holiday >0) THEN e.id ELSE 0 END)) AS managerPostLeaves ");
//		HQL.append(",COUNT(distinct(CASE WHEN (e.position_id IN ( 'position_04','ce0b3234-dc2d-42bb-8721-28f6a5830016','efda9b52-78b2-42dd-b86e-d117b1c5aad2','f5272ee5-d85b-4dbb-ba02-34f69e149b8d') and a.holiday >0) THEN e.id ELSE 0 END)) AS customerManageLeaves");
//		HQL.append(",COUNT(distinct(CASE WHEN (e.position_id NOT IN ('position_05','position_04','ce0b3234-dc2d-42bb-8721-28f6a5830016','efda9b52-78b2-42dd-b86e-d117b1c5aad2','f5272ee5-d85b-4dbb-ba02-34f69e149b8d','16758e01-aaaa-4125-b8f6-ae0e44953e60','2b92d83d-e001-465e-bc86-e78b727762fe','2d4de503-1026-443e-8307-6a3e0d615b22','61df9e5b-2aca-4ef9-a06a-af576c03546b','667b7990-676d-4e05-8172-209721c059f4','8a5b1366-23dc-4e04-aa86-39009a536364','b7acd412-cca5-4d6e-abc5-3d027915feab','c6a6006a-8719-4718-bb83-c4e6ad2a9c45','d6839df1-20da-484a-8b1a-9f979826df58','d730ddd3-0b64-4fa3-a132-dc95b8b58ba9','f3d99a96-2c9a-41c0-9db0-e0bf57b484f3','position_03','position_07') and a.holiday >0) THEN e.id ELSE 0 END)) AS oherLeaves ");
 	
		
	 
 	
		//////////////2012-10-30 按人次统计///////////////////////////////////////////
		HQL.append("SELECT COUNT(distinct(CASE WHEN a.holiday>0 THEN (a.statusInfo+cast(e.id as char)) ELSE 'null' END)) as holidayPersonTotal");
		HQL.append(",COUNT(distinct(CASE WHEN (e.peopleType_id = 'peopleType_01' and a.holiday >0) THEN (a.statusInfo+cast(e.id as char)) ELSE 'null' END)) AS longOrMiddleTermTotal ");
		HQL.append(",COUNT(distinct(CASE WHEN (e.peopleType_id = 'peopleType_03' and a.holiday >0) THEN (a.statusInfo+cast(e.id as char)) ELSE 'null' END)) AS shortTermTotal");
		HQL.append(",COUNT(distinct(CASE WHEN (e.peopleType_id = 'peopleType_02' and a.holiday >0) THEN (a.statusInfo+cast(e.id as char)) ELSE 'null' END)) AS laoWuTotal");
		HQL.append(",COUNT(distinct(CASE WHEN (e.peopleType_id = 'f7314da5-81ac-4451-9d2a-bfeacaf20ca1' and a.holiday >0) THEN (a.statusInfo+cast(e.id as char)) ELSE 'null' END)) AS dxTotal");
		HQL.append(",COUNT(distinct(CASE WHEN (e.peopleType_id = '6cd0deec-60ae-4209-863e-d2852735eedf' and a.holiday >0) THEN (a.statusInfo+cast(e.id as char)) ELSE 'null' END)) AS outOfSystemTotal");
		
		HQL.append(",COUNT(distinct(CASE WHEN (e.position_id = 'position_05' and a.holiday >0) THEN (a.statusInfo+cast(e.id as char)) ELSE 'null' END)) AS cabinetLeaves");
		HQL.append(",COUNT(distinct(CASE WHEN (e.position_id IN ('16758e01-aaaa-4125-b8f6-ae0e44953e60','2b92d83d-e001-465e-bc86-e78b727762fe','2d4de503-1026-443e-8307-6a3e0d615b22','61df9e5b-2aca-4ef9-a06a-af576c03546b','667b7990-676d-4e05-8172-209721c059f4','8a5b1366-23dc-4e04-aa86-39009a536364','b7acd412-cca5-4d6e-abc5-3d027915feab','c6a6006a-8719-4718-bb83-c4e6ad2a9c45','d6839df1-20da-484a-8b1a-9f979826df58','d730ddd3-0b64-4fa3-a132-dc95b8b58ba9','f3d99a96-2c9a-41c0-9db0-e0bf57b484f3','position_03','position_07')  and a.holiday >0) THEN (a.statusInfo+cast(e.id as char)) ELSE 'null' END)) AS managerPostLeaves ");
		HQL.append(",COUNT(distinct(CASE WHEN (e.position_id IN ( 'position_04','ce0b3234-dc2d-42bb-8721-28f6a5830016','efda9b52-78b2-42dd-b86e-d117b1c5aad2','f5272ee5-d85b-4dbb-ba02-34f69e149b8d') and a.holiday >0) THEN (a.statusInfo+cast(e.id as char)) ELSE 'null' END)) AS customerManageLeaves");
		HQL.append(",COUNT(distinct(CASE WHEN (e.position_id NOT IN ('position_05','position_04','ce0b3234-dc2d-42bb-8721-28f6a5830016','efda9b52-78b2-42dd-b86e-d117b1c5aad2','f5272ee5-d85b-4dbb-ba02-34f69e149b8d','16758e01-aaaa-4125-b8f6-ae0e44953e60','2b92d83d-e001-465e-bc86-e78b727762fe','2d4de503-1026-443e-8307-6a3e0d615b22','61df9e5b-2aca-4ef9-a06a-af576c03546b','667b7990-676d-4e05-8172-209721c059f4','8a5b1366-23dc-4e04-aa86-39009a536364','b7acd412-cca5-4d6e-abc5-3d027915feab','c6a6006a-8719-4718-bb83-c4e6ad2a9c45','d6839df1-20da-484a-8b1a-9f979826df58','d730ddd3-0b64-4fa3-a132-dc95b8b58ba9','f3d99a96-2c9a-41c0-9db0-e0bf57b484f3','position_03','position_07') and a.holiday >0) THEN (a.statusInfo+cast(e.id as char)) ELSE 'null' END)) AS oherLeaves ");
		/////////2012-10-30//////////////////////////////////////////////
		
	 
		
		HQL.append(",COUNT(distinct(CASE WHEN (a.holidayType = 'leaveType_03_1' and a.holiday >0) THEN e.id ELSE 0 END)) AS annualLeave");
		HQL.append(",COUNT(distinct(CASE WHEN (a.holidayType = 'leaveType_03_2' and a.holiday >0) THEN e.id ELSE 0 END)) AS familyPlanningLeave");
		HQL.append(",COUNT(distinct(CASE WHEN (a.holidayType = 'leaveType_04_1' and a.holiday >0) THEN e.id ELSE 0 END)) AS maternityLeave");
		HQL.append(",COUNT(distinct(CASE WHEN (a.holidayType = 'bce240b5-1294-4e8f-a1bb-74152f0e0ff4' and a.holiday >0) THEN e.id ELSE 0 END)) AS sickBaoLeave");
//		HQL.append(",COUNT(distinct(CASE WHEN ((a.holidayType = 'leaveType_02' or a.holidayType = 'b45ed16b-f9ec-4e87-8a87-1cb4fb2d859e')  and a.holiday >0) THEN e.id ELSE 0 END)) AS sickFeiBaoLeave");
		HQL.append(",COUNT(distinct(CASE WHEN ((a.holidayType = 'leaveType_02' or a.holidayType = 'b45ed16b-f9ec-4e87-8a87-1cb4fb2d859e')  and a.holiday >0) THEN (a.statusInfo+cast(e.id as char)) ELSE 'null'  END)) AS sickFeiBaoLeave");
		
			
		HQL.append(",COUNT(distinct(CASE WHEN (a.holidayType = 'leaveType_01' and a.holiday >0) THEN e.id ELSE 0 END)) AS thingsLeave");
//		HQL.append(",COUNT(distinct(CASE WHEN (a.holidayType NOT IN('leaveType_02' ,'leaveType_04_1','leaveType_03_2','leaveType_03_1','leaveType_01','bce240b5-1294-4e8f-a1bb-74152f0e0ff4','b45ed16b-f9ec-4e87-8a87-1cb4fb2d859e') and a.holiday >0) THEN e.id ELSE 0 END)) AS otherHoldiayLeave");
		
		HQL.append(",COUNT(distinct(CASE WHEN (a.holidayType NOT IN('leaveType_02' ,'leaveType_04_1','leaveType_03_2','leaveType_03_1','leaveType_01','bce240b5-1294-4e8f-a1bb-74152f0e0ff4','b45ed16b-f9ec-4e87-8a87-1cb4fb2d859e') and a.holiday >0) THEN (a.statusInfo+cast(e.id as char)) ELSE 'null' END)) AS otherHoldiayLeave");
		
		HQL.append(",COUNT(distinct(CASE WHEN a.absent >0 THEN e.id ELSE 0 END)) AS absents");
		HQL.append(",COUNT(distinct(CASE WHEN a.late >0 THEN e.id  ELSE 0 END)) AS lates");
		HQL.append(",COUNT(distinct(CASE WHEN a.early>0 THEN e.id  ELSE 0 END)) AS earlys ");
		
		HQL.append("  FROM employee e left join attshift a on e.id  = a.employee_id ");
//		HQL.append(" WHERE  e.organization_id =?  and  ");
		HQL.append("  WHERE e.organization_id =").append(Long.parseLong(orgId)).append("  AND  ");
//		HQL.append(" WHERE  e.organization_id = ").append(orgId).append("  and  ");
		
		String reportType = queryMap.get("reportType");
		if(reportType.equals("defined")){
			HQL.append(" a.attDate BETWEEN '").append(queryMap.get("startDate")).append("' and '").append(queryMap.get("endDate")).append("'");
		}
		else if(reportType.equals("year")){
			HQL.append(" a.year = ").append(Integer.parseInt(queryMap.get("ayear")));
		}else{
			HQL.append(" a.year = ").append(Integer.parseInt(queryMap.get("year"))).append(" and  a.month = ")
				.append(Integer.parseInt(queryMap.get("month")));
//			HQL.append("  a.year = ?").append(" and  a.month = ? ");
		}
		
		String sql = HQL.toString();
		//new Object[]{orgId,queryMap.get("year"),queryMap.get("month")},
		List<AttshiftsOrg> list = jdbcTemplate.query(HQL.toString(), new AttshiftsOrgMapper());
		AttshiftsOrg ss= list.get(0);
		return null!=list&&list.size()>0?list.get(0):null;
	}
	private static final class AttshiftsOrgMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			AttshiftsOrg ao = new AttshiftsOrg();
			String holidayPersonTotal = rs.getString("holidayPersonTotal");
			
			String longOrMiddleTermTotal = rs.getString("longOrMiddleTermTotal");
			String shortTermTotal = rs.getString("shortTermTotal");
			String laoWuTotal = rs.getString("laoWuTotal");
			String dxTotal = rs.getString("dxTotal");
			String outOfSystemTotal = rs.getString("outOfSystemTotal");
			
			String cabinetLeaves =rs.getString("cabinetLeaves") ;
			String managerPostLeaves = rs.getString("managerPostLeaves");
			String customerManageLeaves = rs.getString("customerManageLeaves");
			String oherLeaves = rs.getString("oherLeaves");
			
			String annualLeave = rs.getString("annualLeave");
			String familyPlanningLeave = rs.getString("familyPlanningLeave");
			String maternityLeave = rs.getString("maternityLeave");
			String sickBaoLeave = rs.getString("sickBaoLeave");
			String sickFeiBaoLeave = rs.getString("sickFeiBaoLeave");
			String thingsLeave = rs.getString("thingsLeave");
			String otherHoldiayLeave = rs.getString("otherHoldiayLeave");
			
			String absents = rs.getString("absents");
			String lates = rs.getString("lates");
			String earlys = rs.getString("earlys");
			
			ao.setHolidayPersonTotal(holidayPersonTotal==null||"".equals(holidayPersonTotal)?0:Integer.parseInt(holidayPersonTotal));
			
			ao.setLongOrMiddleTermTotal(longOrMiddleTermTotal==null||"".equals(longOrMiddleTermTotal)?0:Double.parseDouble(longOrMiddleTermTotal));
			ao.setShortTermTotal(shortTermTotal==null||"".equals(shortTermTotal)?0:Double.parseDouble(shortTermTotal));
			ao.setLaoWuTotal(laoWuTotal==null||"".equals(laoWuTotal)?0:Double.parseDouble(laoWuTotal));
			ao.setDxTotal(dxTotal==null||"".equals(dxTotal)?0:Double.parseDouble(dxTotal));
			ao.setOutOfSystemTotal(outOfSystemTotal==null||"".equals(outOfSystemTotal)?0:Double.parseDouble(outOfSystemTotal));
			
			ao.setCabinetLeaves(cabinetLeaves==null||"".equals(cabinetLeaves)?0:Double.parseDouble(cabinetLeaves));
			ao.setManagerPostLeaves(managerPostLeaves==null||"".equals(managerPostLeaves)?0:Double.parseDouble(managerPostLeaves));
			ao.setCustomerManageLeaves(customerManageLeaves==null||"".equals(customerManageLeaves)?0:Double.parseDouble(customerManageLeaves));
			ao.setOherLeaves(oherLeaves==null||"".equals(oherLeaves)?0:Double.parseDouble(oherLeaves));
			
			ao.setAnnualLeave(annualLeave==null||"".equals(annualLeave)?0:Double.parseDouble(annualLeave));
			ao.setFamilyPlanningLeave(familyPlanningLeave==null||"".equals(familyPlanningLeave)?0:Double.parseDouble(familyPlanningLeave));
			ao.setMaternityLeave(maternityLeave==null||"".equals(maternityLeave)?0:Double.parseDouble(maternityLeave));
			ao.setSickBaoLeave(sickBaoLeave==null||"".equals(sickBaoLeave)?0:Double.parseDouble(sickBaoLeave));
			ao.setSickFeiBaoLeave(sickFeiBaoLeave ==null||"".equals(sickFeiBaoLeave)?0:Double.parseDouble(sickFeiBaoLeave));
			ao.setThingsLeave(thingsLeave==null||"".equals(thingsLeave)?0:Double.parseDouble(thingsLeave));
			ao.setOtherHoldiayLeave(otherHoldiayLeave==null||"".equals(otherHoldiayLeave)?0:Double.parseDouble(otherHoldiayLeave));
			
			ao.setAbsents(absents==null||"".equals(absents)?0:Double.parseDouble(absents));
			ao.setLates(lates==null||"".equals(lates)?0:Double.parseDouble(lates));
			ao.setEarlys(earlys==null||"".equals(earlys)?0:Double.parseDouble(earlys));
	        return ao;
	    }
	}
	
	/**
	 * 输出员工请假详情
	 * */
	@SuppressWarnings("unchecked")
	public List<AttshiftMoreThanMidole> getXiuJia(String orgId,Map<String,String> queryMap){
		JdbcTemplate jdbcTemplate = this.getDefDao().getJdbcTemplate();
		StringBuffer HQL = new StringBuffer();
		HQL.append("SELECT e.id as employeeId,e.name as empName,SUM(a.holiday) AS holidays ");
		HQL.append(" FROM attshift a LEFT JOIN employee e ON a.employee_id = e.id");
		// WHERE e.organization_id =? AND a.year =?  AND a.month=? GROUP BY e.id,e.name
//		HQL.append("  WHERE e.organization_id =? AND  ");
		HQL.append("  WHERE e.organization_id =").append(Long.parseLong(orgId)).append("  AND  ");
		
		String reportType = queryMap.get("reportType");
		if(reportType.equals("defined")){
			HQL.append(" a.attDate BETWEEN '").append(queryMap.get("startDate")).append("' and '").append(queryMap.get("endDate")).append("'");
		}
		else if(reportType.equals("year")){
			HQL.append(" a.year = ").append(Integer.parseInt(queryMap.get("ayear")));
		}else{
			HQL.append(" a.year = ").append(Integer.parseInt(queryMap.get("year"))).append(" and  a.month = ")
				.append(Integer.parseInt(queryMap.get("month")));
		}
		
		HQL.append("  GROUP BY e.id,e.name");
		String hql = HQL.toString();
		//new Object[]{orgId},
		List<AttshiftMoreThanMidole> list = jdbcTemplate.query(HQL.toString(),  new AttshiftMoreThanMidoleMapper());
		return list.size()==0?null:list;
	}
	
	private static final class AttshiftMoreThanMidoleMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			AttshiftMoreThanMidole amm = new AttshiftMoreThanMidole();
			amm.setEmployeeId(Long.parseLong(rs.getString("employeeId")));
			amm.setEmpName(rs.getString("empName"));
			amm.setHolidays(Double.parseDouble(rs.getString("holidays")));
	        return amm;
	    }
	}

	
	/**
	 * 查询某人一段时间的attshift
	 */
	public List<AttShift> queryShiftConfigs(String employeeId, Date start,
			Date end) {
		String ss;
		List<AttShift> list = this.getDefDao().findByQueryString(
				"select a from AttShift a where  a.employee.id = ? and (a.attDate >= ? and a.attDate <= ?)",
				Long.parseLong(employeeId),start,end);
		AttShift  a = list.get(0);
		return list;
	}
	
	/**
	 * 人员休病假报表
	 * */
	public List<SickHoliday> findSickHolidays(String orgId,Map<String,String> queryMap){
		JdbcTemplate jdbcTemplate = this.getDefDao().getJdbcTemplate();
		StringBuffer HQL = new StringBuffer();
		HQL.append("SELECT e.id as employeeId,e.name as empName,SUM(CASE WHEN (a.holidayType='leaveType_02' OR a.holidayType ='b45ed16b-f9ec-4e87-8a87-1cb4fb2d859e') THEN a.holiday ELSE 0 END) AS sickFeiBaoHolidays ");
		HQL.append(" FROM attshift a LEFT JOIN employee e ON a.employee_id = e.id");
		// WHERE e.organization_id =? AND a.year =?  AND a.month=? GROUP BY e.id,e.name
		HQL.append("  WHERE e.organization_id =").append(Long.parseLong(orgId)).append("  AND  ");
//		HQL.append("  WHERE e.organization_id = ?  and  ");
		String reportType = queryMap.get("reportType");
		if(reportType.equals("defined")){
			String dyear = queryMap.get("endDate").split("-")[0];
			HQL.append(" a.attDate < '").append(queryMap.get("endDate")).append("'");
			HQL.append("  and  a.year = '").append(dyear).append("'");
		}
		else if(reportType.equals("year")){
			HQL.append(" a.year = ").append(Integer.parseInt(queryMap.get("ayear")));
		}else{
			HQL.append(" a.year = ").append(Integer.parseInt(queryMap.get("year"))).append(" and  a.month <= ")
				.append(Integer.parseInt(queryMap.get("month")));
		}
		
		HQL.append("  GROUP BY e.id,e.name");
		String hql = HQL.toString();
		//new Object[]{orgId},
		List<SickHoliday> list = jdbcTemplate.query(HQL.toString(),  new SickHolidayMapper());
		return list;
	}
	
	private static final class SickHolidayMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			SickHoliday sh = new SickHoliday();
			sh.setEmployeeId(Long.parseLong(rs.getString("employeeId")));
			sh.setEmpName(rs.getString("empName"));
			String sickFeiBaoHolidays = rs.getString("sickFeiBaoHolidays");
			sh.setSickFeiBaoHolidays(sickFeiBaoHolidays==null||"".equals(sickFeiBaoHolidays)?0:Double.parseDouble(sickFeiBaoHolidays));
	        return sh;
	    }
	}
}
