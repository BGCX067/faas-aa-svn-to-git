package com.fortunes.faas.service;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import javax.annotation.Resource;

import net.fortunes.core.service.BaseService;
import net.fortunes.util.Tools;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.fortunes.core.service.GenericService;
import com.fortunes.faas.model.AttendanceLog;
import com.fortunes.faas.model.ShiftConfig;
import com.fortunes.faas.util.ClosureService;
import com.fortunes.faas.vo.AttendanceLogVo;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Log;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.LogService;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.mysql.jdbc.Connection;


@Component
public class TimerTaskService extends BaseService {
	
	private static final Class<? extends Throwable>[] Exception = null;
	@Resource AttShiftService attShiftService;
	@Resource DeviceService deviceService;
	@Resource ClosureService closureService;
	@Resource LogService logService;
	@Resource ShiftConfigService shiftConfigService;
	//自己添加的
	@Resource JdbcTemplate closureJdbcTemplate;
	@Resource JdbcTemplate jdbcTemplate;
	@Resource EmployeeService employeeService; 
	@Resource AttendanceLogService  attendanceLogService;
	/**
	 * 天定时任务
	 * @throws Exception
	 */
	
	public void addAttShift(){
		logger.info("dayTask start......");
		Date date = new Date();
		String fdate = Tools.dateToString(date);
		Date dateF = Tools.string2Date(fdate);
		boolean isError = false;
		try{
			attShiftService.delDayAttShift(dateF);
			closureService.syncLogsOfDate(dateF);
			attShiftService.dayTask(dateF);
		}catch(Exception e){
			isError = true;
			e.printStackTrace();
		}finally{
			if(isError){
				log("系统", "计算", date+":计算当日考勤时发生异常1，请检测");
			}
		}
	}
	
	public void log(String opUser,String opType,String contents){
		Log newLog = new Log(opUser,opType,contents,new Date());
		try {
			logService.add(newLog);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 月定时任务
	 * @throws Exception
	 */
//	public void updateAttShift() throws Exception {
//		logger.info("monthTask start......");
//		Date date = new Date();
////		Calendar c = Calendar.getInstance();
////		c.setTime(date);
////		c.set(Calendar.MONTH, c.get(Calendar.MONTH)+1);
////		date = c.getTime();
//		String fdate = Tools.dateToString(date);
//		Date dateF = Tools.string2Date(fdate);
//		attShiftService.monthTask(dateF);
//	}
	/**
	 * 年定时任务
	 * @throws Exception
	 */
	public void annualLeaveJob()throws Exception{
		logger.info("YearTask start......");
		List<Employee> emps = employeeService.getAll();
		employeeService.yearTask(emps);
		logger.info("YearTask start.....");
	}
	
	public void monthAnnualHoliday()throws Exception{
		logger.info("每月年假定时计算开始");
		List<Employee> emps = employeeService.getAll();
		employeeService.yearTask(emps);
		logger.info("每月年假定时计算结束");
		
	}
	
	/**
	 * 月定时排班
	 * @throws Exception
	 */
	public void workScheduleByMouth() throws Exception{
		logger.info("workScheduleByMouth start........");
		shiftConfigService.batchCreateAuto();
		logger.info("workScheduleByMouth end.......");
	} 
	
	/**
	 * 定时计算前一天工作到次日的考勤信息
	 * @throws Exception
	 */
	public void computerShiftConfigNextDay() throws Exception{
		
		Date today = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.set(Calendar.DATE, c.get(Calendar.DATE)-1);
		Date yesterday = c.getTime();
		
		List<ShiftConfig> scList = shiftConfigService.queryShiftConfigs(yesterday,true);
//		List<ShiftConfig> scList =shiftConfigService.findShiftconfigNextDayIsTrue();
		if (null != scList && scList.size() > 0) {
			for (ShiftConfig sc : scList) {
				attShiftService.delDayAndEmployee(Long.toString(sc
						.getEmployee().getId()), sc.getDate());
				attShiftService.mendCard(Long
						.toString(sc.getEmployee().getId()), sc.getDate());
				attShiftService.dayTaskOfShiftConfigForNextDay(sc);
			}
		}
	}
	
//	public void backDataBase()throws Exception{
//		logger.info("back up database start.....");
//		String url = "D:\\sqlDataBaseBackUp\\";
//		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
//		String name = sf.format(new Date())+".bak";
//		String sql = "backup database faas to disk = '"+url+name+"' with format,name = 'full backup of faas'";
//		jdbcTemplate.execute(sql);
//		logger.info("back up database end......");
//	}
	
	public void deviceStatus() throws Exception {
		logger.info("deviceStatusTask start......");
		deviceService.refressDeviceStatus();
	}
	
	public JdbcTemplate getClosureJdbcTemplate() {
		return closureJdbcTemplate;
	}
	public void setClosureJdbcTemplate(JdbcTemplate closureJdbcTemplate) {
		this.closureJdbcTemplate = closureJdbcTemplate;
	}
}
