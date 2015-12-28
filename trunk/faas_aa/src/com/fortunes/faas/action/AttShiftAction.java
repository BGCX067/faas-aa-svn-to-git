package com.fortunes.faas.action;

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.fortunes.core.Helper;
import net.fortunes.core.ListData;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fortunes.faas.exception.DictTypeException;
import com.fortunes.faas.model.AttShift;
import com.fortunes.faas.model.AttShiftReportApply;
import com.fortunes.faas.model.MonthStatistics;
import com.fortunes.faas.model.ShiftConfig;
import com.fortunes.faas.service.AttShiftReportApplyService;
import com.fortunes.faas.service.AttShiftService;
import com.fortunes.faas.service.ExcelHelperService;
import com.fortunes.faas.service.HolidayApplyService;
import com.fortunes.faas.service.MonthStatisticsService;
import com.fortunes.faas.service.ShiftConfigService;
import com.fortunes.faas.vo.AttShiftColumns;
import com.fortunes.faas.vo.AttShifts;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.OrganizationService;
import com.fortunes.fjdp.admin.service.UserService;
import com.sun.accessibility.internal.resources.accessibility;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class AttShiftAction extends GenericAction<AttShift> {
	@Resource private UserService userService;
	@Resource private MonthStatisticsService monthStatisticsService;
	@Resource private AttShiftReportApplyService attShiftReportApplyService;
	@Resource private ShiftConfigService shiftConfigService;
	
	private File excelFile;
	public File getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}

	@Override
	public String list() throws Exception {
		JSONArray roleArray = JSONArray.fromObject(this.getSessionMap().get(Helper.ROLE_CODE));
		User user = userService.getUserByAuthedUserId(authedUser.getId());
		Employee e = user.getEmployee();
		String organizations = p("organizations");
		if(roleArray.size() == 1 && roleArray.contains(Role.SYSTEM_GENERAL_STAFF)){
			queryMap.put("employee", e.getId()+"");
		}else{
			if(p("searchType").equals("employee")){
				if(p("employee").equals("")){
					queryMap.put("employee", e.getId()+"");
				}else{
					queryMap.put("employee", p("employee"));
				}
			}else{
				if(!StringUtils.isNotEmpty(organizations)){
					queryMap.put("organizations", Helper.getOrganizationId()+"");
				}else{
					queryMap.put("organizations", p("organizations"));
				}
			}
		}
		queryMap.put("beginDate", p("beginDate"));
		queryMap.put("endDate", p("endDate"));
		queryMap.put("isVer", p("isVer"));
		queryMap.put("notWell", p("notWell"));
		queryMap.put("notRest", p("notRest"));
		return super.list();
	}
		
	@SuppressWarnings("unchecked")
	public String attShiftStatistics() throws Exception {
		condition();
		JSONArray ja = new JSONArray();
		ListData<MonthStatistics> listData = null;
		if(queryMap.get("reportType").equals("yearmonth")){
			listData =  monthStatisticsService.getcon(start, limit, queryMap);
			jo.put(TOTAL_COUNT_KEY, listData.getTotal());
		}
		if(listData!=null && listData.getTotal()>0){
			for(MonthStatistics mo : listData.getList()){
				ja.add(tojsonObject(mo));
			}
		}else{
			ListData<AttShifts> attshifts = attShiftService.attShiftStatistics(queryMap, start, limit);
			int i=0;
			for(AttShifts attShift :attshifts.getList()){
				System.out.println(attShift.getEmployeeId()+attShift.getEmployeeName()+attShift.getEmployeeCode());i++;
			}
			 System.out.println(attshifts.getList().size());
			 System.out.println(attshifts.getTotal());
			 System.out.println("i="+i);
			jo.put(TOTAL_COUNT_KEY, attshifts.getTotal());
			for(AttShifts a:attshifts.getList()){
				ja.add(toJsonObject(a));
			}
		}
		jo.put(DATA_KEY, ja);
		return render(jo);
	}
	
	public void condition()throws Exception{
		User user = userService.getUserByAuthedUserId(authedUser.getId());
		Enumeration<String> params=request.getParameterNames();
		while(params.hasMoreElements()){
			String name = params.nextElement();
			queryMap.put(name, request.getParameter(name));
		}
		ListData<AttShifts> listData = null;
		try {
			JSONArray roleArray = JSONArray.fromObject(this.getSessionMap().get(Helper.ROLE_CODE));
			//如果只是考勤普通用户，则只可查看自己的考勤信息
			if(roleArray.size() == 1 && roleArray.contains(Role.SYSTEM_GENERAL_STAFF)){
				queryMap.put("searchType", "employee");
				queryMap.put("employee", user.getEmployee().getId()+"");
				queryMap.put("organizations", "");
				//如果还有其他权限，则可查看本部门
			}else{
				Organization org = organizationService.getOrganizationById(Helper.getOrganizationId()+"");
				if(queryMap.get("employee").equals("")){
					queryMap.put("employee", user.getEmployee().getId()+"");
				}
				if(queryMap.get("organizations").equals("")){
					String organizations = org.getId()+"";
					queryMap.put("organizations", organizations);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			setJsonMessage(true, "统计出错");
		}
	}
	
	public String removeReport()throws Exception{
		String years = p("year");
		String month = p("month");
		String organizations = p("organizations");
		if(organizations.equals("")){
			organizations = Long.toString(AdminHelper.getOrganizationId());
		}
		monthStatisticsService.delAffirmInfo(years,month,organizations);
		setJsonMessage(true, "成功取消确认");
		return render(jo);
	}
	
	public String affirmLogReportByManager()throws Exception{
		String yearMonths = p("year")+"-"+p("month");
		String organizations = p("organizations");
		if(organizations.equals("")){
			organizations = Long.toString(Helper.getOrganizationId()); 
		}
		AttShiftReportApply asra = attShiftReportApplyService.getAttshiftReportApply(organizations, yearMonths);
		if(asra==null){
			condition();
			ListData<AttShifts> listData = attShiftService.attShiftStatistics(queryMap, start, limit);
			User user = userService.getUserByAuthedUserId(authedUser.getId());
			List<MonthStatistics> list = new ArrayList<MonthStatistics>();
			int year = Integer.parseInt(p("year"));
			int month = Integer.parseInt(p("month"));
			for(AttShifts a : listData.getList()){
				MonthStatistics mo = new MonthStatistics();
				mo.setYears(year);
				mo.setMonths(month);
				mo.setSalary(a.getSalary());
				mo.setRegionalAllowance(a.getRegionalAllowance());
				mo.setAbsents(a.getAbsents());
				mo.setAnnualLeave(a.getAnnualLeave());
				mo.setDeptName(a.getDeptName());
				mo.setOrganizationId(a.getOrganizationId());
				mo.setEarlys(a.getEarlys());
				mo.setEarlyTimes(a.getEarlyTimes());
				mo.setEmployeeCode(a.getEmployeeCode());
				mo.setEmployeeId(a.getEmployeeId());
				mo.setEmployeeName(a.getEmployeeName());
				mo.setFamilyPlanningLeave(a.getFamilyPlanningLeave());
				mo.setFeedLeave(a.getFeedLeave());
				mo.setFuneralLeave(a.getFuneralLeave());
				mo.setHolidays(a.getHolidays());
				mo.setHomeLeave(a.getHomeLeave());
				mo.setInjuryLeave(a.getInjuryLeave());
				mo.setLates(a.getLates());
				mo.setLateTimes(a.getLateTimes());
				mo.setLeaves(a.getLeave());
				mo.setLookAfterLeave(a.getLookAfterLeave());
				mo.setMarriageLeave(a.getMarriageLeave());
				mo.setMaternityLeave(a.getMaternityLeave());
				mo.setNurseLeave(a.getNurseLeave());
				mo.setOverTime(a.getOverTime());
				mo.setRestday(a.getRestday());
				mo.setSickLeave(a.getSickLeave());
				mo.setTravel(a.getTravel());
				mo.setWeekdays(a.getWeekday());
				mo.setManagerAffirm(user.getEmployee());
				list.add(mo);
			}
			monthStatisticsService.bathAdd(list);
			String orgs = queryMap.get("organizations");
			String [] org = orgs.split(",");
			String yearMonth = p("year")+"-"+p("month");
			for(String o : org){
				AttShiftReportApply attshiftReportApply = new AttShiftReportApply();
				attshiftReportApply.setManagerAffirm(user.getEmployee());
				attshiftReportApply.setOrganization(AdminHelper.toOrganization(o));
				attshiftReportApply.setYearMonth(yearMonth);
				attShiftReportApplyService.add(attshiftReportApply);
			}
			
			
			setJsonMessage(true, "查询出的考勤记录统计已确认");
		}else{
			setJsonMessage(true, "记录以确认");
		}
		
		return render(jo);
	}
	
	public String attShiftStatisticsByOrganization()throws Exception{
		String year = p("year");
		String month = p("month");
		queryMap.put("year", year);
		queryMap.put("month", month);
		queryMap.put("searchType", "organization");
		if(!StringUtils.isNotEmpty(p("ids"))){
			queryMap.put("organizations", Long.toString(Helper.getOrganizationId()));
		}else{
			queryMap.put("organizations", p("ids"));
		}
		queryMap.put("isAffirm", p("isAffirm"));
		ListData<MonthStatistics> listData = monthStatisticsService.getcon(start, limit, queryMap);
		JSONArray ja = new JSONArray();
		for(MonthStatistics mo : listData.getList()){
			ja.add(tojsonObject(mo));
		}
		jo.put(DATA_KEY, ja);
		jo.put(TOTAL_COUNT_KEY, listData.getTotal());
		return render(jo);
	}
	
	public String affirmLogReportByLeader()throws Exception{
		int year = Integer.parseInt(p("year"));
		int month = Integer.parseInt(p("month"));
		Employee e = userService.getUserByAuthedUserId(authedUser.getId()).getEmployee();
		String organizationId = "";
		if(StringUtils.isNotEmpty(p("ids"))){
			organizationId = p("ids");
		}else{
			organizationId = Long.toString(Helper.getOrganizationId());
		}
		List<MonthStatistics> list = monthStatisticsService.getOrganizationMonthStatistics(year, month, organizationId);
		for(MonthStatistics m : list){
			m.setLeaderAffirm(e);
			this.monthStatisticsService.addOrUpdate(m);
		}
		setJsonMessage(true, "成功确认");
		return render(jo);
	}
	/**
	 * 月报表
	 * */
	@SuppressWarnings("unchecked")
	public String exportAttShift() throws Exception{
		String fileName = p("year")+p("month")+"人员考勤统计报表.xls";
	    Enumeration<String> params=request.getParameterNames();
		while(params.hasMoreElements()){
			String name = params.nextElement();
			queryMap.put(name, request.getParameter(name));
		}
		if(queryMap.get("organizations")==null || queryMap.get("organizations")==""){
			queryMap.put("organizations", Helper.getOrganizationId()+"");
		}
		File file = null;
		ListData<MonthStatistics> listData =  monthStatisticsService.getcon(start, limit, queryMap);
		List<MonthStatistics> lists = listData.getList();
		int len = lists.size();
//		if(listData.getTotal()>0){
		if(listData.getList().size()>0){
			file = excelHelperService.exportExcelAffirmed(fileName, listData.getList(),Integer.parseInt(queryMap.get("year")),Integer.parseInt(queryMap.get("month")));
		}else{
			ListData<AttShifts> list = attShiftService.attShiftStatistics(queryMap,0,0);
			file = excelHelperService.exportExcel(fileName, list.getList(),Integer.parseInt(queryMap.get("year")),Integer.parseInt(queryMap.get("month")));
		}
		byte [] bytes = FileUtils.readFileToByteArray(file);
		return renderFile(bytes, fileName);
	}

	public String computerShiftConfigNextDay() throws Exception{
		
		Date today =Tools.stringToDate(p("startDate"));
		List<ShiftConfig> scList = shiftConfigService.queryShiftConfigs(today,true);
		if (scList.size() > 0) {
			for (ShiftConfig sc : scList) {
				attShiftService.delDayAndEmployee(Long.toString(sc
						.getEmployee().getId()), sc.getDate());
				attShiftService.mendCard(Long
						.toString(sc.getEmployee().getId()), sc.getDate());
				attShiftService.dayTaskOfShiftConfigForNextDay(sc);
			}
		}
		setJsonMessage(true,"计算完成");
		return render(jo);
	}
	
	public String importedAttshift()throws Exception{
		try{
			User u = userService.getUserByAuthedUserId(authedUser.getId());
			Employee employee = u.getEmployee();
			excelHelperService.importAttshift(excelFile,employee);
			setJsonMessage(true, "导入成功");
		}catch (DictTypeException e) {
			setJsonMessage(true, e.toString());
		}catch (Exception e){
			e.printStackTrace();
			setJsonMessage(true,"excel导入出错");
		}
		return render(jo.toString());
	}
	
	/**
	 * 输出员工请假详细
	 * */
	public String exportHolidayInfo()throws Exception{
	    Enumeration<String> params=request.getParameterNames();
		while(params.hasMoreElements()){
			String name = params.nextElement();
			queryMap.put(name, request.getParameter(name));
		}
		
//		String fileName = p("year")+"年请假详细报表.xls";
		String fileName = "";
		if(queryMap.get("organizations")==null || queryMap.get("organizations")==""){
			queryMap.put("organizations", Helper.getOrganizationId()+"");
		}
		String reportType = queryMap.get("reportType");
		if(reportType.equals("defined")){
			fileName = queryMap.get("startDate")+"到"+queryMap.get("endDate")+"请假详细报表.xls";
		}else if(reportType.equals("year")){
			fileName = p("year")+"年请假详细报表.xls";
		}else{
			fileName = queryMap.get("year")+"年"+queryMap.get("month")+"月请假详细报表.xls";
		}
		
		File file = excelHelperService.exportHolidayInfoExcel(fileName,queryMap);
		byte[] bytes = FileUtils.readFileToByteArray(file);
		return renderFile(bytes,fileName);
	}
	
	/**
	 * 
	 * 人员休病假报表
	 * */
	public String exportMonthHolidayInfo()throws Exception{
		String yearMonth= p("year")+"-"+p("month");
		 Enumeration<String> params=request.getParameterNames();
		 while(params.hasMoreElements()){
			String name = params.nextElement();
			queryMap.put(name, request.getParameter(name));
		}
		String fileName = "";
		String reportType = queryMap.get("reportType");
		if(reportType.equals("defined")){
			fileName=queryMap.get("startDate")+"到"+queryMap.get("endDate")+"全行在岗人员休病假明细表.xls";
		}else if(reportType.equals("year")){
			fileName=queryMap.get("ayear")+"年全行在岗人员休病假明细表.xls";
		}else{
			fileName=p("year")+"年"+p("month")+"月,全行在岗人员休病假明细表.xls";
		}
		
		File file=excelHelperService.exportHolidayMonthInfoExcel(fileName,yearMonth,queryMap);
		byte[] bytes = FileUtils.readFileToByteArray(file);
		return renderFile(bytes, fileName);
	}
/**
 * 人员考勤统计表
 * 
 * */	
	public String yearAttendanceReport()throws Exception{
		 Enumeration<String> params=request.getParameterNames();
		 while(params.hasMoreElements()){
			String name = params.nextElement();
			queryMap.put(name, request.getParameter(name));
		 	}
		
		String fileName = null;
		String reportType = queryMap.get("reportType");
		if("yearmonth".equals(reportType)){
			fileName=p("year")+"年"+p("month")+"月,全行在岗人员考勤统计表.xls";
		}else if("defined".equals(reportType)){
			fileName=queryMap.get("startDate")+"到"+queryMap.get("endDate")+"全行在岗人员考勤统计表.xls";
		}else{
			fileName=queryMap.get("ayear")+"年全行在岗人员考勤统计表.xls";
		}
		String yearMonth= p("year")+"-"+p("month");
		File file=excelHelperService.exportMonthAttendanceExcel(fileName,yearMonth,queryMap);
		byte[] bytes = FileUtils.readFileToByteArray(file);
		return renderFile(bytes, fileName);
	}
	
	//添加一个导出excel表格的方法
	public String exportAttendance()throws Exception{
		Date date = new Date();
		String fileName = Tools.date2String(date).split(" ")[0];
	    String employee = p("employee");
		String startDate = p("startDate");
		String endDate = p("endDate");
		String organizations = p("organizations");
		if(organizations == "" || organizations == null){
			organizations = Helper.getOrganizationId()+"";
		}
		String searchType = p("searchType");
		String notWell = p("notWell");
		File file = excelHelperService.exportAttShiftExcel(fileName, employee,organizations,Tools.string2Date(startDate),Tools.string2Date(endDate),searchType,notWell);
		byte[] bytes = FileUtils.readFileToByteArray(file);
		return renderFile(bytes, fileName+"考勤明细.xls");
	}

	public String computeCurrentDayAttShift() throws Exception {
		Date date = new Date();
		attShiftService.delDayAttShift(date);
		attShiftService.dayTask(date);
		setJsonMessage(true,"计算完成");
		return render(jo);
	}
//	public String computeCurrentMonthAttShift() throws Exception {
//		Date date = new Date();
//		Calendar c = Calendar.getInstance();
//		c.setTime(date);
//		c.set(Calendar.MONTH, c.get(Calendar.MONTH)+1);
//		date = c.getTime();
//		attShiftService.monthTask(date);
//		setJsonMessage(true,"计算完成");
//		return render(jo);
//	}
	
	public String getColumns() throws Exception {
		AttShiftColumns[] acs = AttShiftColumns.values();
		JSONArray ja = new JSONArray();
		for(AttShiftColumns ac : acs){
			AdminHelper record = new AdminHelper();
			record.put("id", ac.name());
			record.put("text", ac.getLabel());
			ja.add(record.getJsonObject());
		}
		jo.put(DATA_KEY, ja);
		return render(jo);
	}
	/**
	 * 输出报表

	 * */
	@SuppressWarnings("unchecked")
	public String generateReport() throws Exception {
		String columns = p("columns");
		Date date = new Date();
		String fileName = Tools.date2String(date).split(" ")[0]+"人员考勤统计报表.xls";
//		OutputStream os = response.getOutputStream();
//		response.reset();  
//	    response.setHeader("Content-Disposition","attachment;filename="+fileName+"人员考勤统计报表.xls");//指定下载的文件名  
//	    response.setContentType("application/vnd.ms-excel");  
//	    response.setHeader("Pragma", "no-cache");  
//	    response.setHeader("Cache-Control", "no-cache");  
//	    response.setDateHeader("Expires", 0);
	    Enumeration<String> params=request.getParameterNames();
		while(params.hasMoreElements()){
			String name = params.nextElement();
			queryMap.put(name, request.getParameter(name));
		}
		if(queryMap.get("organizations")==null || queryMap.get("organizations")==""){
			queryMap.put("organizations", Helper.getOrganizationId()+"");
		}
	    ListData<AttShifts> list = attShiftService.attShiftStatistics(queryMap,0,0);
	    File file = excelHelperService.generateReport(fileName,columns, list.getList());
	    byte[] bytes = FileUtils.readFileToByteArray(file);
		return renderFile(bytes, fileName);
	}
	
	public ListData<AttShift> getListDateForAttShift(){
		User user = userService.getUserByAuthedUserId(authedUser.getId());
		Employee e = user.getEmployee();
		String organizations = p("organizations");
		if(p("searchType").equals("employee")){
			if(p("employee").equals("")){
				queryMap.put("employee", e.getId()+"");
			}else{
				queryMap.put("employee", p("employee"));
			}
		}else{
			if(!StringUtils.isNotEmpty(organizations)){
				queryMap.put("organizations", Helper.getOrganizationId()+"");
			}else{
				queryMap.put("organizations", p("organizations"));
			}
		}
		queryMap.put("beginDate", p("beginDate"));
		queryMap.put("endDate", p("endDate"));
		queryMap.put("isVer", p("isVer"));
		queryMap.put("notWell", p("notWell"));
		
		ListData<AttShift> listDate = this.getDefService().getListData(query, queryMap, 0, 0);
		return listDate;
	}
	
	public String affirmAttendanceInfo()throws Exception{
		List<AttShift> list = new ArrayList<AttShift>();
		ListData<AttShift> listDate = getListDateForAttShift();
		for(AttShift att : listDate.getList()){
			if(!att.isAffirm()){
				att.setAffirm(true);
				list.add(att);
			}
		}
		attShiftService.affirmAttendanceInfo(list);
		setJsonMessage(true, "查询出的考勤记录以确认");
		return render(jo);
	}
	public String cancelAffirmAttendanceInfo()throws Exception{
		List<AttShift> list = new ArrayList<AttShift>();
		ListData<AttShift> listDate = getListDateForAttShift();
		for(AttShift att : listDate.getList()){
			if(att.isAffirm()){
				att.setAffirm(false);
				list.add(att);
			}
		}
		attShiftService.affirmAttendanceInfo(list);
		setJsonMessage(true, "查询出的考勤记录以取消确认");
		return render(jo);
	}
	
	public String removeThings()throws Exception{
		JSONObject normal = new JSONObject();
		normal.put("id", "normal");
		normal.put("text", "正常工作");
		normal.put("leaf", true);
		normal.put("checked", false);
		JSONObject rest = new JSONObject();
		rest.put("id", "rest");
		rest.put("text", "休息");
		rest.put("leaf", true);
		rest.put("checked", false);
//		JSONObject late = new JSONObject();
		JSONArray ja = new JSONArray();
		ja.add(normal);ja.add(rest);
		return render(ja);
	}
	
	@Override
	protected void setEntity(AttShift entity) throws Exception {
		
	}

	@Override
	protected JSONObject toJsonObject(AttShift a) throws Exception {
		AdminHelper record = new AdminHelper();
		record.put("id", a.getId());
		record.put("employeeCode", a.getEmployee().getCode());
		record.put("employeeName", a.getEmployee().getName());
		Organization organization = a.getEmployee().getOrganization();
		record.put("deptName", null!=organization?organization.getName():"");
		record.put("attDate", a.getAttDate());
		record.put("affirm", a.isAffirm());
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		JSONObject details = new JSONObject();
		details.put("id", a.getId());
		details.put("status", a.getStatusInfo());
		details.put("startTime", Tools.date2String(a.getStartTime()));
		details.put("endTime", Tools.date2String(a.getEndTime()));
		details.put("checkInTime", a.getCheckInTime() == null ? "" : df.format(a.getCheckInTime()));
		details.put("checkOutTime", a.getCheckOutTime() == null ? "" : df.format(a.getCheckOutTime()));
		details.put("late", a.getLate());
		details.put("early", a.getEarly());
		details.put("lateTime", a.getLateTime());
		details.put("earlyTime", a.getEarlyTime());
		details.put("absent", a.getAbsent());
		details.put("overTime", a.getOverTime());
		details.put("weekday", a.getWeekday());
		details.put("restday", a.getRestday());
		record.put("details", details);
		return record.getJsonObject();
	}	

	protected JSONObject toJsonObject(AttShifts a) throws Exception {
		AdminHelper record = new AdminHelper();
		record.put("employeeCode", a.getEmployeeCode());
		record.put("employeeName", a.getEmployeeName());
		record.put("deptName", a.getDeptName());
		record.put("lates", a.getLates());
		record.put("earlys", a.getEarlys());
		record.put("absents", a.getAbsents());
		record.put("lateTimes", a.getLateTimes());
		record.put("earlyTimes", a.getEarlyTimes());
		record.put("weekday", a.getWeekday());
		record.put("restday", a.getRestday());
		record.put("holidays", a.getHolidays());
		record.put("leave", a.getLeave());
		record.put("sickLeave", a.getSickLeave());
		record.put("annualLeave", a.getAnnualLeave());
		record.put("homeLeave", a.getHomeLeave());
		record.put("maternityLeave", a.getMaternityLeave());
		record.put("familyPlanningLeave", a.getFamilyPlanningLeave());
		record.put("travel", a.getTravel());
		record.put("overTime", a.getOverTime());
		
		return record.getJsonObject();
	}
	public JSONObject tojsonObject(MonthStatistics a){
		AdminHelper record = new AdminHelper();
		record.put("employeeCode", a.getEmployeeCode());
		record.put("employeeName", a.getEmployeeName());
		record.put("deptName", a.getDeptName());
		record.put("lates", a.getLates());
		record.put("earlys", a.getEarlys());
		record.put("absents", a.getAbsents());
		record.put("lateTimes", a.getLateTimes());
		record.put("earlyTimes", a.getEarlyTimes());
		record.put("weekday", a.getWeekdays());
		record.put("restday", a.getRestday());
		record.put("holidays", a.getHolidays());
		record.put("leave", a.getLeaves());
		record.put("sickLeave", a.getSickLeave());
		record.put("annualLeave", a.getAnnualLeave());
		record.put("homeLeave", a.getHomeLeave());
		record.put("maternityLeave", a.getMaternityLeave());
		record.put("familyPlanningLeave", a.getFamilyPlanningLeave());
		record.put("travel", a.getTravel());
		record.put("overTime", a.getOverTime());
		record.put("managerAffirm", a.getManagerAffirm());
		record.put("leaderAffirm", a.getLeaderAffirm());
		
		return record.getJsonObject();
	}

	@Override
	public GenericService<AttShift> getDefService() {
		return attShiftService;
	}
	@Resource private ExcelHelperService excelHelperService;
	@Resource private AttShiftService attShiftService;
	@Resource private OrganizationService organizationService;
	@Resource private HolidayApplyService holidayApplyService;
 
}
