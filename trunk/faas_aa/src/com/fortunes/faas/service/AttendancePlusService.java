package com.fortunes.faas.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;


import com.fortunes.faas.model.AttShift;
import com.fortunes.faas.model.AttendancePlus;
import com.fortunes.faas.model.AttendancePlus.AttendancePlusType;
import com.fortunes.faas.model.AttendancePlus.TimeRange;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.Constants;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.service.DictService;
import com.fortunes.fjdp.admin.service.EmployeeService;

import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class AttendancePlusService extends GenericService<AttendancePlus> {
	@Resource AttShiftService attShiftService;
	@Resource EmployeeService employeeService;
	@Resource DictService dictService;

	public String createPlusInfo(Calendar start, Calendar end, String employeeId,
			String timeRange, String attendancePlusType,String reason,String osTime,String oeTime) throws Exception {
		String returnStr = "";
		while(end.compareTo(start) >= 0){
			Date d = start.getTime();
			if(start.equals(end)||(d.getDay() !=0 && d.getDay()!=6)){
				if(attendancePlusType.equals("LEAVE")){
					AttendancePlus info = getOrCreate(start.getTime(),employeeId);
					info.setEmployee(AppHelper.toEmployee(employeeId));
					info.setDate(start.getTime());
					info.setTimeRange(TimeRange.valueOf(timeRange));
					info.setLeaveType(AdminHelper.toDict(reason));
					
					if(StringUtils.isNotBlank(reason)) {
						Dict leaveType = dictService.get(reason);
						info.setLeaveType(leaveType);
					}
					
					info.setType(AttendancePlusType.valueOf(attendancePlusType));
					info.setReason(dictService.get(reason).getText());
					this.addOrUpdate(info);
					if(getBeforeOrLater(start)){
						attShiftService.mendCard(employeeId, start.getTime());
					}
				}else{
					AttShift attshift = attShiftService.getAttShiftByDateAndId(employeeId, start.getTime());
					if(!attshift.isAffirm()){
						AttendancePlus info = getOrCreate(start.getTime(),employeeId);
						info.setEmployee(AppHelper.toEmployee(employeeId));
						info.setDate(start.getTime());
						info.setTimeRange(TimeRange.valueOf(timeRange));
						info.setType(AttendancePlusType.valueOf(attendancePlusType));
						Date overtimeStart = AppHelper.toDate(Tools.date2String(start.getTime())+" "+osTime+":00");
						Date overTimeEnd = AppHelper.toDate(Tools.dateToString(start.getTime())+" "+oeTime+":00");
						if(attendancePlusType.equals("OVERTIME")){
							Date checkInTime = attshift.getCheckInTime();
							Date checkOutTime = attshift.getCheckOutTime();
							if(attshift.getCheckInTime()==null || attshift.getCheckOutTime()==null){
								return "没有"+Tools.date2String(start.getTime())+"打卡记录,请检查后在增补加班";
							}
							if(checkInTime.compareTo(overtimeStart)<=0&&checkOutTime.compareTo(overTimeEnd)>=0){
								info.setOverTimeStart(overtimeStart);
								info.setOverTimeEnd(overTimeEnd);
							}
						}else if(attendancePlusType.equals("OTHER")){
							info.setReason(reason);
							info.setOverTimeStart(null);
							info.setOverTimeEnd(null);
						}else{
							info.setOverTimeStart(null);
							info.setOverTimeEnd(null);
						}
						this.addOrUpdate(info);
						if(getBeforeOrLater(start)){
							if(attendancePlusType.equals("OVERTIME")){
								SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
								String sT = sf.format(overtimeStart);
								String eT = sf.format(overTimeEnd);
								attshift.setStatusInfo(attshift.getStatusInfo()+"<span style='color:blue'>(<font color:blue>"+sT+"~~"+eT+"</font>)</span>");
							}
							if(attendancePlusType.equals("OTHER")){
								attshift.setStatusInfo(attshift.getStatusInfo()+"<span style='color:blue'>(<font color:blue>"+info.getReason()+")</font></span>");
							}
							attShiftService.del(attshift);
							attShiftService.mendCard(employeeId, start.getTime());
						}
					}else{
						returnStr = returnStr+AdminHelper.toDateString(start.getTime())+",";
					}
				}
			}
			start.add(Calendar.DATE, 1);
		}
		return returnStr;
	}
	
	public AttShift statisticsEveryDayAttPuls(AttShift attShift,AttendancePlusType attendancePlusType){
		attShift.setException(attendancePlusType.name());
		attShift.setExceptionTime(1);
		attShift.setAbsent(0);
		attShift.setWeekday(1);
		attShift.setLate(0);
		attShift.setEarly(0);
		attShift.setLateTime(0);
		attShift.setEarlyTime(0);
		AttendancePlus att = new AttendancePlus();
		att.setType(attendancePlusType);
		attShift.setStatusInfo(getLeaveText(att));
		return attShift;
	}
	public String getLeaveText(AttendancePlus att){
		if(att.getType().equals(AttendancePlus.AttendancePlusType.TRAVEL)){
			return Constants.LEAVE_TYPE_TRAVEL;
		}else if(att.getType().equals(AttendancePlus.AttendancePlusType.OTHER)){
			return Constants.LEAVE_TYPE_OTHER;
		}else if(att.getType().equals(AttendancePlus.AttendancePlusType.LEAVE)){
			return Constants.LEAVE_TYPE_LEAVE;
		}else{
			return Constants.LEAVE_TYPE_OVERTIME;
		}
	}
	public List<AttendancePlus> getPlusInfosByEmployee(String employeeId,Date start,Date end) {
		List<AttendancePlus> list = this.getDefDao().findByQueryString(
				"select a from AttendancePlus a where  a.employee.id = ? and (a.date between ? and ?)",
				Long.parseLong(employeeId),start,end);
		return list;
	}
	
	private AttendancePlus getOrCreate(Date date,String employeeId){
		List<AttendancePlus> list = this.getDefDao().findByQueryString(
				"select a from AttendancePlus a where a.date = ? and a.employee.id = ?",date,Long.parseLong(employeeId));
		if(list != null && list.size() >= 1){
			return list.get(0);
		}else{
			return new AttendancePlus();
		}
	}
	
	/**
	 * 查询某一个月内的所有考勤增补
	 * @param date
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AttendancePlus> getMonthAttPlus(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.roll(Calendar.MONTH, -1);//日期回滚一个月
		if(c.get(Calendar.MONTH)==Calendar.DECEMBER){
			  c.roll(Calendar.YEAR, -1);
		}
		c.set(Calendar.DATE,1);//把日期设置为当月第一天
		Date start = c.getTime();
		c.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
		Date end = c.getTime();
		String HQL = "SELECT a FROM AttendancePlus AS a WHERE a.date >= ? AND a.date <= ?";
		List<AttendancePlus> list = this.getDefDao().findByQueryString(HQL, start,end);
		return list;
	}

	public void clearPlusInfo(Calendar date, String employeeId) throws Exception {
		this.getDefDao().bulkUpdate(
				"delete from AttendancePlus a where a.date = ? and a.employee.id = ?",
				date.getTime(),Long.parseLong(employeeId));
		this.getDefDao().bulkUpdate("delete from AttShift a where a.attDate = ? and a.employee.id = ? and a.holidayType is not null",
				date.getTime(),Long.parseLong(employeeId));
		attShiftService.mendCard(employeeId, date.getTime());
	}
	
	public void clearPlusInfos(Date startDate,Date endDate,String employeeId) throws Exception{
		this.getDefDao().bulkUpdate(
				"delete from AttendancePlus a where (a.date between ? and ?) and a.employee.id = ?",
				startDate,endDate,Long.parseLong(employeeId));
		this.getDefDao().bulkUpdate("delete from AttShift a where (a.attDate between ? and ?) and a.employee.id = ? and a.holidayType is not null",
				startDate,endDate,Long.parseLong(employeeId));
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		while(endDate.compareTo(start.getTime())>=0){
			if(getBeforeOrLater(start)){
				attShiftService.mendCard(employeeId, start.getTime());
			}
			start.add(Calendar.DATE, 1);
		}
		
	}
	/**
	 * 对比当期日期是否是当天之前，当天，之后
	 * @throws ParseException 
	 */
	public boolean getBeforeOrLater(Calendar CDate) throws ParseException{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String as = df.format(new Date());
		Date now = df.parse(as);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		if(CDate.compareTo(calendar)<0){
			return true;//如果是当天或当天以前则返回true
		}else{
			return false;
		}
	}
}
