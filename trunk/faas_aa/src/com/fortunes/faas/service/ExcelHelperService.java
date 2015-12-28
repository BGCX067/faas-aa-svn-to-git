package com.fortunes.faas.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.fortunes.core.ListData;
import net.fortunes.util.Tools;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.fortunes.faas.exception.DictTypeException;
import com.fortunes.faas.model.AttShift;
import com.fortunes.faas.model.AttendanceLog;
import com.fortunes.faas.model.BranchShiftAnalysis;
import com.fortunes.faas.model.HolidayApply;
import com.fortunes.faas.model.MeetOrTrain;
import com.fortunes.faas.model.MonthStatistics;
import com.fortunes.faas.model.ShiftConfig;
import com.fortunes.faas.model.ShiftConfigTime;
import com.fortunes.faas.util.CardHelper;
import com.fortunes.faas.vo.AttShiftColumns;
import com.fortunes.faas.vo.AttShifts;
import com.fortunes.faas.vo.AttshiftMoreThanMidole;
import com.fortunes.faas.vo.AttshiftsOrg;
import com.fortunes.faas.vo.EmployeeClosure;
import com.fortunes.faas.vo.EmployeeTotal;
import com.fortunes.faas.vo.SickHoliday;
import com.fortunes.faas.vo.SickPeopleTotal;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.model.Config.ConfigKey;
import com.fortunes.fjdp.admin.service.ConfigService;
import com.fortunes.fjdp.admin.service.DictService;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.OrganizationService;
import com.fortunes.fjdp.admin.service.UserService;
@Component
public class ExcelHelperService {

	@Resource private DictService dictService;
	@Resource private OrganizationService organizationService;
	@Resource private ConfigService configService;
	@Resource private MonthStatisticsService monthStatisticsService;
	@Resource private EmployeeService employeeService;
	@Resource private BranchShiftAnalysisService branchShiftAnalysisService;
	@Resource private AttShiftService attShiftService;
	@Resource private UserService userService;
	@Resource private HolidayApplyService holidayApplyService;
	@Resource private ShiftConfigService shiftConfigService;
	@Resource private ShiftConfigTimeService shiftConfigTimeService;
	@Resource private AttendanceLogService attendanceLogService;
	
	//添加一个导出excel表格的方法
//	public File exportAttShiftExcel(String fileName,String employee,String organization,Date startDate,Date endDate,String seachType,
//			String notWell){
//		String temporaryFile = System.getProperty("java.io.tmpdir");
//		File tmpFile = new File(temporaryFile+fileName+".xls");
//		WritableWorkbook book;
//		try{
//			book = Workbook.createWorkbook(tmpFile);
//			//设置样式
//			WritableFont fontTitle1 = new WritableFont(WritableFont.createFont("标题一"), 16, WritableFont.BOLD);
//			WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle1);
//			formatTitle1.setAlignment(Alignment.CENTRE);
//			formatTitle1.setBorder(Border.ALL, BorderLineStyle.THIN);
//			
//			WritableFont ffb = new WritableFont(WritableFont.createFont("宋体"),11, WritableFont.BOLD);
//			WritableCellFormat fb = new WritableCellFormat(ffb);
//			fb.setAlignment(Alignment.CENTRE);
//			fb.setVerticalAlignment(VerticalAlignment.CENTRE);
//			fb.setBorder(Border.ALL, BorderLineStyle.THIN);
//			
//			WritableFont f = new WritableFont(WritableFont.createFont("宋体"),11);
//			WritableCellFormat fc = new WritableCellFormat(f);
//			fc.setAlignment(Alignment.CENTRE);
//			fc.setVerticalAlignment(VerticalAlignment.CENTRE);
//			fc.setBorder(Border.ALL, BorderLineStyle.THIN);
//			
//			WritableFont ffs = new WritableFont(WritableFont.createFont("宋体"),11);
//			WritableCellFormat fs = new WritableCellFormat(ffs);
//			fs.setAlignment(Alignment.CENTRE);
//			fs.setVerticalAlignment(VerticalAlignment.CENTRE);
//			fc.setBorder(Border.ALL, BorderLineStyle.THIN);
//			
//			Calendar cal = new GregorianCalendar();
//			
////			int row=0;
////			int column=0;
//			List<AttShift> list = new ArrayList<AttShift>();
//			List<Employee> emps = new ArrayList<Employee>();
//			if(seachType.equals("employee")){
//				Employee em = employeeService.get(employee);
//				if(em!=null){
//					emps.add(em);
//				}else{
//					emps = employeeService.getNotTurnAwayAndIsAttendance();
//				}
//			}else{
//				if(organization==""||organization==null){
//					emps = employeeService.getEmployeeByOrganizations(null);
//				}else{
//					emps = employeeService.getEmployeeByOrganizations(organization.split(","));
//				}
//			}
//			WritableSheet sheet = book.createSheet("考勤记录查询表", 0);
//			sheet.mergeCells(0, 0,5, 0);
//			sheet.addCell(new Label(0,0,"考勤明细",fs));
//			int row = 2;
//			for(Employee e : emps){
//				if(notWell.equals("false")){
//					list = attShiftService.getDefDao().findByQueryString("select a from AttShift a where a.employee.id = ? and a.attDate >=? and attDate <= ? ", e.getId(),startDate,endDate);
//				}else{
//					list = attShiftService.getDefDao().findByQueryString("select a from AttShift a where a.employee.id = ? and a.attDate >=? and attDate <= ? and a.statusInfo is not ?", e.getId(),startDate,endDate,"正常工作");
//				}
//				sheet.addCell(new Label(0,1,"员工编号"));
//				sheet.addCell(new Label(1,1,"员工姓名"));
//				sheet.addCell(new Label(2,1,"日期"));
//				sheet.addCell(new Label(3,1,"星期"));
//				sheet.addCell(new Label(4,1,"上班时间"));
//				sheet.addCell(new Label(5,1,"下班时间"));
//				sheet.addCell(new Label(6,1,"签入时间"));
//				sheet.addCell(new Label(7,1,"签出时间"));
//				sheet.addCell(new Label(8,1,"考勤状态"));
//				cal.setFirstDayOfWeek(Calendar.MONDAY);
//				cal.setTime(startDate);
//				
//				while(cal.getTime().before(endDate) || cal.getTime().equals(endDate)){
//					sheet.addCell(new Label(0,row,e.getCode()));
//					sheet.addCell(new Label(1,row,e.getName()));
//					sheet.addCell(new Label(2,row,Tools.date2String(cal.getTime()),fs));
//					sheet.addCell(new Label(3,row,Tools.getChineseDay(cal.get(Calendar.DAY_OF_WEEK))));
//					for(AttShift a : list){
//						if(!Tools.date2String(cal.getTime()).equals(Tools.date2String(a.getAttDate()))){
//							continue;
//						}
//						sheet.addCell(new Label(4,row,Tools.date2String(a.getStartTime())));
//						sheet.addCell(new Label(5,row,Tools.date2String(a.getEndTime())));
//						sheet.addCell(new Label(6,row,Tools.date2String(a.getCheckInTime())));
//						sheet.addCell(new Label(7,row,Tools.date2String(a.getCheckOutTime())));
//						sheet.addCell(new Label(8,row,a.getStatusInfo().contains("加班")?"加班":a.getStatusInfo()));
//					}
//					cal.add(Calendar.DATE, 1);
//					row=row+1;
//				}
//			}
//			book.write();   
//	        book.close();
//			
//		}catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//		return tmpFile;
//	}
//	
	public File ExportMeetOrTrainExcel(String fileName,List<MeetOrTrain> list) throws Exception{
		String temporaryFile = System.getProperty("java.io.tmpdir");
		File tmpFile = new File(temporaryFile+fileName+".xls");
		WritableWorkbook book;
		try{
			book = Workbook.createWorkbook(tmpFile);
			//设置样式
			WritableFont fontTitle1 = new WritableFont(WritableFont.createFont("标题一"), 16, WritableFont.BOLD);
			WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle1);
			formatTitle1.setAlignment(Alignment.CENTRE);
			formatTitle1.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffb = new WritableFont(WritableFont.createFont("宋体"),11, WritableFont.BOLD);
			WritableCellFormat fb = new WritableCellFormat(ffb);
			fb.setAlignment(Alignment.CENTRE);
			fb.setVerticalAlignment(VerticalAlignment.CENTRE);
			fb.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont f = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fc = new WritableCellFormat(f);
			fc.setAlignment(Alignment.CENTRE);
			fc.setVerticalAlignment(VerticalAlignment.CENTRE);
			fc.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffs = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fs = new WritableCellFormat(ffs);
			fs.setAlignment(Alignment.CENTRE);
			fs.setVerticalAlignment(VerticalAlignment.CENTRE);
			fs.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableSheet sheet = book.createSheet("sheet_01", 0);
			sheet.mergeCells(0, 0,5, 0);
			sheet.addCell(new Label(0,0,"会议/培训统计报表",formatTitle1));
			
			sheet.addCell(new Label(0,1,"会议名称",fb));
			sheet.addCell(new Label(1,1,"主办部门",fb));
			sheet.addCell(new Label(2,1,"地址",fb));
			sheet.addCell(new Label(3,1,"日期",fb));
			sheet.addCell(new Label(4,1,"主题",fb));
			
			int row = 2;
			for(MeetOrTrain me : list){
				sheet.addCell(new Label(0,row,me.getName(),fs));
				sheet.addCell(new Label(1,row,me.getOrgName()));
				sheet.addCell(new Label(2,row,me.getAddress()));
				String sdate = Tools.date2String(me.getStartDate());
				String edate = Tools.date2String(me.getEndDate());
				if(sdate.equals(edate)){
					sheet.addCell(new Label(3,row,sdate));
				}else{
					sheet.addCell(new Label(3,row,sdate+"~~~"+edate));
				}
				sheet.addCell(new Label(4,row,me.getTheme()));row = row+1;
			}
			sheet.setColumnView(0, 12);
			sheet.setColumnView(1, 14);
			sheet.setColumnView(2, 16);
			sheet.setColumnView(3, 24);
			sheet.setColumnView(4, 20);
			book.write();
			book.close();
		}catch (Exception e) {
		}
		return tmpFile;
	}

	//添加一个导出用户角色信息excel方法
	public File exportUserRoleExcel(String fileName,List<User> list){
		String temporaryFile = System.getProperty("java.io.tmpdir");
		File tmpFile = new File(temporaryFile+fileName+".xls");
		WritableWorkbook book;
		try{
			book = Workbook.createWorkbook(tmpFile);
			//设置样式
			WritableFont fontTitle1 = new WritableFont(WritableFont.createFont("标题一"), 16, WritableFont.BOLD);
			WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle1);
			formatTitle1.setAlignment(Alignment.CENTRE);
			formatTitle1.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffb = new WritableFont(WritableFont.createFont("宋体"),11, WritableFont.BOLD);
			WritableCellFormat fb = new WritableCellFormat(ffb);
			fb.setAlignment(Alignment.CENTRE);
			fb.setVerticalAlignment(VerticalAlignment.CENTRE);
			fb.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont f = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fc = new WritableCellFormat(f);
			fc.setAlignment(Alignment.CENTRE);
			fc.setVerticalAlignment(VerticalAlignment.CENTRE);
			fc.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffs = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fs = new WritableCellFormat(ffs);
			fs.setAlignment(Alignment.CENTRE);
			fs.setVerticalAlignment(VerticalAlignment.CENTRE);
			fs.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			
//			List<User> list = new ArrayList<User>();
			
			WritableSheet sheet = book.createSheet("sheet_01", 0);			
			
			sheet.mergeCells(0, 0,5, 0);
			sheet.addCell(new Label(0,0,"考勤系统用户报表",fs));
			
			sheet.addCell(new Label(0,1,"用户名"));
			sheet.addCell(new Label(1,1,"用户显示名"));
			sheet.addCell(new Label(2,1,"对应员工"));
			sheet.addCell(new Label(3,1,"所属部门"));
			sheet.addCell(new Label(4,1,"锁定"));
			
			int row = 2;
			for(User a : list){				
				sheet.addCell(new Label(0,row,a.getName(),fs));
				sheet.addCell(new Label(1,row,a.getDisplayName()));
				sheet.addCell(new Label(2,row,a.getEmployee().getName()));
				sheet.addCell(new Label(3,row,a.getEmployee().getOrganization().getName()));
				sheet.addCell(new Label(4,row,a.isLocked()==true?"已锁定":""));row=row+1;
			}
			book.write();   
	        book.close();
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		return tmpFile;
	}
	/**
	 * 输出员工请假详细
	 * */
	public File exportHolidayInfoExcel(String fileName,Map<String, String> queryMap){
//		String temporaryFile = System.getProperty("java.io.tmpdir");
//		File tmpFile = new File(temporaryFile+fileName);
//		WritableWorkbook book;
	
//		
		String temporaryFile = System.getProperty("java.io.tmpdir");
		File tmpFile = new File(temporaryFile+fileName+".xls");
		WritableWorkbook book;
		try{
			book = Workbook.createWorkbook(tmpFile);
			//设置样式
			WritableFont fontTitle1 = new WritableFont(WritableFont.createFont("标题一"), 16, WritableFont.BOLD);
			WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle1);
			formatTitle1.setAlignment(Alignment.CENTRE);
			formatTitle1.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffb = new WritableFont(WritableFont.createFont("宋体"),11, WritableFont.BOLD);
			WritableCellFormat fb = new WritableCellFormat(ffb);
			fb.setAlignment(Alignment.CENTRE);
			fb.setVerticalAlignment(VerticalAlignment.CENTRE);
			fb.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont f = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fc = new WritableCellFormat(f);
			fc.setAlignment(Alignment.CENTRE);
			fc.setVerticalAlignment(VerticalAlignment.CENTRE); 
			fc.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffs = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fs = new WritableCellFormat(ffs);
			fs.setAlignment(Alignment.CENTRE);
			fs.setVerticalAlignment(VerticalAlignment.CENTRE);
			fs.setBorder(Border.ALL, BorderLineStyle.THIN);
			Date startDate = null,endDate = null;
			String reportType = queryMap.get("reportType");
			String month;
			if("yearmonth".equals(reportType)){
				String aa = queryMap.get("year")+"-";
				if(Integer.parseInt(queryMap.get("month"))>=10){
					month = queryMap.get("month");
				}else{
					month = "0"+queryMap.get("month");
				}
				String cc = queryMap.get("year")+"-"+(queryMap.get("month"))+"-01";
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				startDate = df.parse(cc);
//				startDate = Tools.string2Date(queryMap.get("year")+"-"+(queryMap.get("month"))+"-01");
				//得到当前月的最后一天
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, Integer.parseInt(queryMap.get("year")));
				c.set(Calendar.MONTH, Integer.parseInt(queryMap.get("month")));
				c.set(Calendar.DAY_OF_MONTH, 1);
				c.add(Calendar.DATE, -1);
				endDate = c.getTime();
			}else if("year".equals(reportType)){
				startDate = Tools.string2Date(queryMap.get("year")+"-01-01");
				endDate = Tools.string2Date(queryMap.get("year")+"-12-31");
			}else{
				startDate = Tools.string2Date(queryMap.get("startDate"));
				endDate = Tools.string2Date(queryMap.get("endDate"));
			}
			String searchType = queryMap.get("searchType");
			
			List<HolidayApply> list = new ArrayList<HolidayApply>();
			if(searchType.equals("employee")&&StringUtils.isNotEmpty(queryMap.get("employee"))){
				Employee emp = employeeService.get(queryMap.get("employee"));
				list = holidayApplyService.getHolidayApplysByEmployee(startDate, endDate, queryMap.get("employee"));
				String organizationName = emp.getOrganization().getName();
				WritableSheet sheet = book.createSheet(emp.getName(), 0);
				sheet.mergeCells(0, 0, 4,0);
				if(reportType.equals("yearmonth")){
					sheet.addCell(new Label(0,0,emp.getName()+queryMap.get("year")+"年"+queryMap.get("month")+"月请假详细报表",formatTitle1));
				}else if(reportType.equals("year")){
					sheet.addCell(new Label(0,0,emp.getName()+queryMap.get("year")+"年请假详细报表",formatTitle1));
				}else{
					sheet.addCell(new Label(0,0,emp.getName()+queryMap.get("startDate")+"到"+queryMap.get("endDate")+"请假详细报表",formatTitle1));
				}
				sheet.addCell(new Label(0,1,"姓名",fs));
				sheet.addCell(new Label(1,1,"部门",fs));
				sheet.addCell(new Label(2,1,"请假类型",fs));
				sheet.addCell(new Label(3,1,"请假时间段",fs));
				sheet.addCell(new Label(4,1,"原因",fs));
				int rowSon = 2;
				if(list.size()>0){
					sheet.mergeCells(0, 2, 0, 2+list.size()-1);
					sheet.addCell(new Label(0,2,emp.getName(),fs));
					for(HolidayApply ha : list){
						sheet.addCell(new Label(1,rowSon,organizationName,fs));
						sheet.addCell(new Label(2,rowSon,ha.getLeaveType().getText(),fs));
						sheet.addCell(new Label(3,rowSon,Tools.DatetoStringByChinese(ha.getStartDate())+"-"+Tools.DatetoStringByChinese(ha.getEndDate()),fs));
						sheet.addCell(new Label(4,rowSon,ha.getApplyReason(),fs));rowSon = rowSon+1;
					}
				}
			}else if(StringUtils.isNotEmpty(queryMap.get("organizations"))){
				List<Organization> orgs = organizationService.getOrganizationsByOrganizations(queryMap.get("organizations"));
				for(Organization o : orgs){
					WritableSheet sheet = book.createSheet(o.getName(), 0);
					sheet.mergeCells(0, 0, 4,0);
					sheet.addCell(new Label(0,0,"请假详细信息",formatTitle1));
					sheet.addCell(new Label(0,1,"姓名",fs));
					sheet.addCell(new Label(1,1,"部门",fs));
					sheet.addCell(new Label(2,1,"请假类型",fs));
					sheet.addCell(new Label(3,1,"请假时间段",fs));
					sheet.addCell(new Label(4,1,"原因",fs));
					int row = 2;
					int rowSon = 2;
					for(Employee e : o.getEmployees()){
						List<HolidayApply> has = holidayApplyService.getHolidayApplysByEmployee(startDate, endDate, e.getId()+"");
						if(has.size()>0){
							sheet.mergeCells(0, row, 0, row+has.size()-1);
							sheet.addCell(new Label(0,row,e.getName(),fs)); row = row+has.size();
							for(HolidayApply ha : has){
								sheet.addCell(new Label(1,rowSon,o.getName(),fs));
								sheet.addCell(new Label(2,rowSon,ha.getLeaveType().getText(),fs));
								sheet.addCell(new Label(3,rowSon,Tools.DatetoStringByChinese(ha.getStartDate())+"-"+Tools.DatetoStringByChinese(ha.getEndDate()),fs));
								sheet.addCell(new Label(4,rowSon,ha.getApplyReason(),fs));rowSon = rowSon+1;
							}
						}
					}
				}
			}
			book.write();
			book.close();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return tmpFile;
	}
	/*
	 *导出全行在岗人员休病假明细表 
	 */
	public File exportHolidayMonthInfoExcel(String fileName,String yearMonth,Map<String,String> queryMap){
		String temporaryFile = System.getProperty("java.io.tmpdir");
		File tmpFile = new File(temporaryFile+fileName);
		WritableWorkbook book;
		try{
			book = Workbook.createWorkbook(tmpFile);
			//设置样式
			WritableFont fontTitle1 = new WritableFont(WritableFont.createFont("标题一"), 16, WritableFont.BOLD);
			WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle1);
			formatTitle1.setAlignment(Alignment.CENTRE);
			formatTitle1.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffb = new WritableFont(WritableFont.createFont("宋体"),11, WritableFont.BOLD);
			WritableCellFormat fb = new WritableCellFormat(ffb);
			fb.setAlignment(Alignment.CENTRE);
			fb.setWrap(true);//自动换行
			Colour co = Colour.GRAY_25;
			fb.setBackground(co);
			fb.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			fb.setVerticalAlignment(VerticalAlignment.CENTRE);
			fb.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont f = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fc = new WritableCellFormat(f);
			fc.setAlignment(Alignment.CENTRE);
			fc.setVerticalAlignment(VerticalAlignment.CENTRE);
			fc.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffs = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fs = new WritableCellFormat(ffs);
			fs.setAlignment(Alignment.CENTRE);
			fs.setVerticalAlignment(VerticalAlignment.CENTRE);
			fs.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			String reportType = queryMap.get("reportType");
			WritableSheet sheet ;
//			= book.createSheet(yearMonth+"全行在岗人员休病假明细表", 0);
			
			if(reportType.equals("defined")){
				sheet = book.createSheet(queryMap.get("startDate")+"到"+queryMap.get("endDate")+"全行在岗人员休病假明细表", 0);
			}
			else if(reportType.equals("year")){
				sheet = book.createSheet(queryMap.get("ayear")+"年全行在岗人员休病假明细表", 0);
			}else{
				sheet = book.createSheet(yearMonth+"全行在岗人员休病假明细表", 0);
			}
			
			
			sheet.mergeCells(0, 0, 6,0);
			if(reportType.equals("defined")){
				sheet.addCell(new Label(0,0,queryMap.get("startDate")+"到"+queryMap.get("endDate")+"全行在岗人员休病假明细表",formatTitle1));
			}
			else if(reportType.equals("year")){
				sheet.addCell(new Label(0,0,queryMap.get("ayear")+"年全行在岗人员休病假明细表",formatTitle1));
			}else{
				sheet.addCell(new Label(0,0,yearMonth+"全行在岗人员休病假明细表",formatTitle1));
			}
//			sheet.addCell(new Label(0,0,yearMonth+"全行在岗人员休病假明细表",formatTitle1));
			sheet.mergeCells(0, 1, 0,2);
			sheet.addCell(new Label(0,1,"一级部门",fb));
			sheet.mergeCells(1, 1, 0, 2);
			sheet.addCell(new Label(1,1,"所在单位",fb));
			sheet.mergeCells(2,1, 5, 0);
			sheet.addCell(new Label(2,1,"病假",fb));
			sheet.addCell(new Label(2,2,"员工休病假小计",fb));
//			sheet.setRowView(2, 2, true);
			sheet.addCell(new Label(3,2,"保胎",fb));
			sheet.addCell(new Label(4,2,"非保胎",fb));
//			sheet.addCell(new Label(5,2,"非保胎（连续请病假1-6月）",fb));
			sheet.addCell(new Label(5,2,"非保胎（连续请病假6月以上）",fb));
//			sheet.mergeCells(0, 3, 1, 0);
//			sheet.addCell(new Label(0,3,"当月全行在岗人员考勤合计",fb));
//			sheet.addCell(new Label(1,3,"",fb));
//			sheet.addCell(new Label(2,3,"",fb));
//			sheet.addCell(new Label(3,3,"",fb));
//			sheet.addCell(new Label(4,3,"",fb));
//			sheet.addCell(new Label(5,3,"",fb));
			List<Organization> orgs = organizationService.getSimpleOrg("1");
			int row = 3;
			int sickholidayTotal=0;
			int sickbao=0,sickfeibao=0,sickfeibaomore=0;
			for(Organization org : orgs){
				List<Organization> childs = organizationService.getSimpleOrg(Long.toString(org.getId()));
				int size = childs.size();
//				sheet.mergeCells(0, row, 0,row+size-1);
				sheet.addCell(new Label(0,row,org.getName(),fc));
				EmployeeTotal totalHoliday = employeeService.getPeopleTotal(org.getId()+"",queryMap);
				
				if(size==0){
					sheet.mergeCells(0, row, 0,row+size-1);
					sheet.addCell(new Label(1,row,org.getName(),fc));
					AttshiftsOrg aa = attShiftService.getAttshiftsOrg(org.getId()+"",queryMap);
					AttshiftsOrg ao = getAttshiftOrgHasHolidays(aa,totalHoliday,org.getId()+"");
					double sickTotal = ao.getSickBaoLeave()+ao.getSickFeiBaoLeave();
					sheet.addCell(new Label(2,row,sickTotal==0?"":Tools.getValuesNotZore(sickTotal),fc));sickholidayTotal+=sickTotal;
					sheet.addCell(new Label(3,row,ao.getSickBaoLeave()==0?"":Tools.getValuesNotZore(ao.getSickBaoLeave()),fc));sickbao+=ao.getSickBaoLeave();
					sheet.addCell(new Label(4,row,ao.getSickFeiBaoLeave()==0?"":Tools.getValuesNotZore(ao.getSickFeiBaoLeave()),fc));sickfeibao+=ao.getSickFeiBaoLeave();
					
					List<SickHoliday> shList = attShiftService.findSickHolidays(org.getId()+"", queryMap);
					int total = this.getSickFeiMoreThan6(shList);
					sheet.addCell(new Label(5,row,total==0?"":total+"",fc));sickfeibaomore+=total;
					
					row++;
				}else{
					sheet.mergeCells(0, row, 0,row+size+1);
					sheet.addCell(new Label(1,row,org.getName(),fc));
					
					AttshiftsOrg ao2 = attShiftService.getAttshiftsOrg(org.getId()+"",queryMap);
					
					AttshiftsOrg ao1 = this.getAttshiftOrgHasHolidays(ao2, totalHoliday,org.getId()+"");
					
					int sickholidayXJ=0,sickholidayTotalXJ=0;
					int sickbaoXJ=0,sickfeibaoXJ=0,sickfeibaomoreXJ=0;
					double sickTotal = ao1.getSickBaoLeave()+ao1.getSickFeiBaoLeave();
					sheet.addCell(new Label(2,row,sickTotal==0?"":Tools.getValuesNotZore(sickTotal),fc));sickholidayTotal+=sickTotal;sickholidayXJ+=sickTotal;
					
					sheet.addCell(new Label(3,row,ao1.getSickBaoLeave()==0?"":Tools.getValuesNotZore(ao1.getSickBaoLeave()),fc));sickbaoXJ+=ao1.getSickBaoLeave();sickbao+=ao1.getSickBaoLeave();
					sheet.addCell(new Label(4,row,ao1.getSickFeiBaoLeave()==0?"":Tools.getValuesNotZore(ao1.getSickFeiBaoLeave()),fc));sickfeibaoXJ+=ao1.getSickFeiBaoLeave();sickfeibao+=ao1.getSickFeiBaoLeave();
					
					List<SickHoliday> shList = attShiftService.findSickHolidays(org.getId()+"", queryMap);
					int total = this.getSickFeiMoreThan6(shList);
					sheet.addCell(new Label(5,row,total==0?"":total+"",fc));sickfeibaomoreXJ+=total;sickfeibaomore+=total;
					row++;
					for(Organization child : childs){
						sheet.addCell(new Label(1,row,child.getName(),fc));
						
						AttshiftsOrg attshiftOrg1 = attShiftService.getAttshiftsOrg(child.getId()+"",queryMap);
						
						AttshiftsOrg attshiftOrg = this.getAttshiftOrgHasHolidays(attshiftOrg1, totalHoliday,child.getId()+"");
						
						double sickTotal1 = attshiftOrg.getSickBaoLeave()+attshiftOrg.getSickFeiBaoLeave();
//						sheet.addCell(new Label(1,row,org.getName(),fc));
//						sheet.addCell(new Label(1,row,child.getName()));
						sheet.addCell(new Label(2,row,sickTotal1==0?"":Tools.getValuesNotZore(sickTotal1),fc));sickholidayXJ+=sickTotal1;sickholidayTotal+=sickTotal1;
						
						sheet.addCell(new Label(3,row,attshiftOrg.getSickBaoLeave()==0?"":Tools.getValuesNotZore(attshiftOrg.getSickBaoLeave()),fc));sickbaoXJ +=attshiftOrg.getSickBaoLeave();sickbao +=attshiftOrg.getSickBaoLeave();
						sheet.addCell(new Label(4,row,attshiftOrg.getSickFeiBaoLeave()==0?"":Tools.getValuesNotZore(attshiftOrg.getSickFeiBaoLeave()),fc));sickfeibaoXJ +=attshiftOrg.getSickFeiBaoLeave();sickfeibao +=attshiftOrg.getSickFeiBaoLeave();
						
						List<SickHoliday> sList = attShiftService.findSickHolidays(org.getId()+"", queryMap);
						int sicktotal = this.getSickFeiMoreThan6(sList);
						sheet.addCell(new Label(5,row,sicktotal==0?"":sicktotal+"",fc));sickfeibaomoreXJ +=sicktotal;sickfeibaomore +=sicktotal;
						
						row++;
					}
					sheet.addCell(new Label(0,row,"",fc));
//					sheet.mergeCells(0, row, 1, 0);
					sheet.addCell(new Label(1,row,"小计",fc));
					sheet.addCell(new Label(2,row,sickholidayXJ==0?"":sickholidayXJ+"",fc));sheet.addCell(new Label(3,row,sickbaoXJ==0?"":sickbaoXJ+"",fc));
					sheet.addCell(new Label(4,row,sickfeibaoXJ==0?"":sickfeibaoXJ+"",fc));sheet.addCell(new Label(5,row,sickfeibaomoreXJ==0?"":sickfeibaomoreXJ+"",fc));
					sickholidayXJ = 0;sickbaoXJ = 0;sickfeibaoXJ = 0;sickfeibaomoreXJ = 0;
				
					row++;
				}
				
			}
			
			sheet.addCell(new Label(0,row,"",fc));
//			sheet.mergeCells(0, row, 1, 0);
			sheet.addCell(new Label(1,row,"总计",fc));
			sheet.addCell(new Label(2,row,sickholidayTotal==0?"":sickholidayTotal+"",fc));sheet.addCell(new Label(3,row,sickbao==0?"":sickbao+"",fc));
			sheet.addCell(new Label(4,row,sickfeibao==0?"":sickfeibao+"",fc));sheet.addCell(new Label(5,row,sickfeibaomore==0?"":sickfeibaomore+"",fc));
			
			
			sheet.setColumnView(0, 20);
			sheet.setColumnView(1, 30);
			sheet.setColumnView(2, 20);
			sheet.setColumnView(3, 10);
			sheet.setColumnView(4, 20);
			sheet.setColumnView(5, 20);
			sheet.setColumnView(6, 20);
			book.write();
			book.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return tmpFile;
	}
	/**
	 * 人员考勤统计统计表
	 * 
	 * */
	public File exportMonthAttendanceExcel(String fileName,String yearMonth,Map<String,String> queryMap){
		String temporaryFile = System.getProperty("java.io.tmpdir");
		File tmpFile = new File(temporaryFile+fileName);
		WritableWorkbook book;
		
		Calendar c = Calendar.getInstance();
		String reportType = queryMap.get("reportType");
		if(reportType.equals("defined")){
			c.set(Calendar.YEAR, Integer.parseInt(queryMap.get("startDate").split("-")[0]));c.set(Calendar.MONTH, Integer.parseInt(queryMap.get("startDate").split("-")[1])-1);
		}
		else if(reportType.equals("year")){
			c.set(Calendar.YEAR, Integer.parseInt(queryMap.get("ayear")));c.set(Calendar.MONTH, Integer.parseInt(queryMap.get("month"))-1);
		}else{
			c.set(Calendar.YEAR, Integer.parseInt(queryMap.get("year")));c.set(Calendar.MONTH, Integer.parseInt(queryMap.get("month"))-1);
		}
		double monthOfDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		try{
			book = Workbook.createWorkbook(tmpFile);
			//设置样式
			WritableFont fontTitle1 = new WritableFont(WritableFont.createFont("标题一"), 16, WritableFont.BOLD);
			WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle1);
			formatTitle1.setAlignment(Alignment.CENTRE);
//			formatTitle1.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffb = new WritableFont(WritableFont.createFont("宋体"),10, WritableFont.BOLD);
			WritableCellFormat fb = new WritableCellFormat(ffb);
			fb.setAlignment(Alignment.CENTRE);
			fb.setWrap(true);//自动换行
			Colour co = Colour.GRAY_25;
			fb.setBackground(co);
			fb.setVerticalAlignment(VerticalAlignment.CENTRE);
			fb.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
//			fb.setBorder(Border.ALL, BorderLineStyle.THIN);
			
//			WritableFont f = new WritableFont(WritableFont.createFont("宋体"),11,WritableFont.BOLD);
			WritableFont f = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fc = new WritableCellFormat(f);
			fc.setAlignment(Alignment.CENTRE);
			fc.setVerticalAlignment(VerticalAlignment.CENTRE);
			fc.setBorder(Border.ALL, BorderLineStyle.THIN,Colour.BLACK);
			
			WritableFont ffs = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fs = new WritableCellFormat(ffs);
			fs.setAlignment(Alignment.CENTRE);
			fs.setVerticalAlignment(VerticalAlignment.CENTRE);
			fs.setBorder(Border.ALL, BorderLineStyle.THIN,Colour.BLACK);
			
			
			WritableSheet sheet ;
//				book.createSheet(yearMonth+"全行在岗人员考勤统计表", 0);
//			String reportType = queryMap.get("reportType");
			if("yearmonth".equals(reportType)){
				sheet = book.createSheet(yearMonth+"全行在岗人员考勤统计表", 0);
			}else if("year".equals(reportType)){
				sheet = book.createSheet(queryMap.get("ayear")+"年全行在岗人员考勤统计表", 0);
			}else {
				sheet = book.createSheet(queryMap.get("startDate")+"到"+queryMap.get("endDate")+"全行在岗人员考勤统计表", 0);
			}
			
			sheet.mergeCells(0, 0, 23,0);
//			sheet.addCell(new Label(0,0,yearMonth+"全行在岗人员考勤统计表",formatTitle1));
			
			if("yearmonth".equals(reportType)){
				sheet.addCell(new Label(0,0,yearMonth+"全行在岗人员考勤统计表",formatTitle1));
			}else if("year".equals(reportType)){
				sheet.addCell(new Label(0,0,queryMap.get("ayear")+"年全行在岗人员考勤统计表",formatTitle1));
			}else {
				sheet.addCell(new Label(0,0,queryMap.get("startDate")+"到"+queryMap.get("endDate")+"全行在岗人员考勤统计表",formatTitle1));
			}
			
			sheet.mergeCells(0, 1, 0,3);
			sheet.addCell(new Label(0,1,"一级部门",fb));
			sheet.mergeCells(1, 1, 0, 3);
			sheet.addCell(new Label(1,1,"所在单位",fb));
			sheet.mergeCells(2,1,0,3);
			sheet.addCell(new Label(2,1,"休假人员总计",fb));
			
			sheet.mergeCells(3,1, 7, 0);
			sheet.addCell(new Label(3,1,"员工类别",fb));
			sheet.mergeCells(3,2,0, 3);
			sheet.addCell(new Label(3,2,"中长期工",fb));
			sheet.mergeCells(4,2,0, 3);
			sheet.addCell(new Label(4,2,"短期工",fb));
			sheet.mergeCells(5,2,0, 3);
			sheet.addCell(new Label(5,2,"劳务工",fb));
			sheet.mergeCells(6,2,0, 3);
			sheet.addCell(new Label(6,2,"定向招聘劳务合同工",fb));
			sheet.mergeCells(7,2,0, 3);
			sheet.addCell(new Label(7,2,"系统外员工",fb));
			
			
			sheet.mergeCells(8, 1, 11, 0);
			sheet.addCell(new Label(8,1,"主要业务岗位休假情况",fb));
			sheet.mergeCells(8,2,0, 3);
			sheet.addCell(new Label(8,2,"管理岗位",fb));
			sheet.mergeCells(9,2,0, 3);
			sheet.addCell(new Label(9,2,"柜员",fb));
			sheet.mergeCells(10,2,0, 3);
			sheet.addCell(new Label(10,2,"客户经理",fb));
			sheet.mergeCells(11,2,0, 3);
			sheet.addCell(new Label(11,2,"其他",fb));
//			sheet.mergeCells(10,2,0, 3);sheet.addCell(new Label(10,2,"个人业务顾问",fb));
//			sheet.mergeCells(11,2,0, 3);sheet.addCell(new Label(11,2,"客户经理-公司业务",fb));
//			sheet.mergeCells(12,2,0, 3);sheet.addCell(new Label(12,2,"客户经理-消费信贷",fb));
			
			
			sheet.mergeCells(12, 1, 23, 0);
			sheet.addCell(new Label(12,1,"休假类别",fb));
			sheet.mergeCells(12, 2, 0, 3);
			sheet.addCell(new Label(12,2,"年休假",fb));
			sheet.mergeCells(13, 2, 0, 3);
			sheet.addCell(new Label(13,2,"探亲假",fb));
			sheet.mergeCells(14,2,0,3);
			sheet.addCell(new Label(14,2,"产假",fb));
			
			sheet.mergeCells(15,2,17,0);
			sheet.addCell(new Label(15,2,"病假",fb));
			sheet.addCell(new Label(15,3,"保胎",fb));
			sheet.addCell(new Label(16,3,"非保胎",fb));
			sheet.addCell(new Label(17,3,"非保胎（连续请病假6月以上）",fb));
			
			sheet.mergeCells(18,2,0,3);
			sheet.addCell(new Label(18,2,"事假",fb));
			
			sheet.mergeCells(19, 2, 0, 3);
			sheet.addCell(new Label(19,2,"其他",fb));
//			sheet.mergeCells(22,2,0,3);sheet.addCell(new Label(22,2,"计划生育假",fb));
			sheet.mergeCells(20,2,0,3);
			sheet.addCell(new Label(20,2,"旷工",fb));
			sheet.mergeCells(21,2,0,3);
			sheet.addCell(new Label(21,2,"迟到",fb));
			sheet.mergeCells(22,2,0,3);
			sheet.addCell(new Label(22,2,"早退",fb));
			sheet.mergeCells(23, 2, 0, 3);
			sheet.addCell(new Label(23,2,"备注",fb));
			
			List<Organization> orgs = organizationService.getSimpleOrg("1");
			int row = 4;
			int holidayPersonTotal=0,holdayTotalAll=0;

			double total1=0,total2=0,total3=0,total4=0,total5=0;
			double postion1=0,postion2=0,postion3=0,postion4=0,holiday1=0,holiday2=0,holiday3=0,holiday4=0,holiday5=0,holiday6=0,holiday7=0,holiday8=0,holiday9=0,holiday10=0,holiday11=0;
			for(Organization org : orgs){
				List<Organization> childs = organizationService.getSimpleOrg(Long.toString(org.getId()));
				int size = childs.size();
//				sheet.mergeCells(0, row, 0,row+size-1);
				sheet.addCell(new Label(0,row,org.getName(),fc));
				
				EmployeeTotal totalHoliday = employeeService.getPeopleTotal(org.getId()+"",queryMap);
				
				if(size==0){
					sheet.mergeCells(0, row, 0,row+size-1);
					AttshiftsOrg aa = attShiftService.getAttshiftsOrg(org.getId()+"",queryMap);
					AttshiftsOrg ao = getAttshiftOrgHasHolidays(aa,totalHoliday,org.getId()+"");
					
					sheet.addCell(new Label(1,row,org.getName(),fc));
					
					
					sheet.addCell(new Label(2,row,ao.getHolidayPersonTotal()==0?"":ao.getHolidayPersonTotal()+"",fc));holidayPersonTotal+=ao.getHolidayPersonTotal();
					
					sheet.addCell(new Label(3,row,ao.getLongOrMiddleTermTotal()==0?"":Tools.getValuesNotZore(ao.getLongOrMiddleTermTotal()),fc));total1+=ao.getLongOrMiddleTermTotal();
					sheet.addCell(new Label(4,row,ao.getShortTermTotal()==0?"":Tools.getValuesNotZore(ao.getShortTermTotal()),fc));total2+=ao.getShortTermTotal();
					sheet.addCell(new Label(5,row,ao.getLaoWuTotal()==0?"":Tools.getValuesNotZore(ao.getLaoWuTotal()),fc));total3+=ao.getLaoWuTotal();
					sheet.addCell(new Label(6,row,ao.getDxTotal()==0?"":Tools.getValuesNotZore(ao.getDxTotal()),fc));total4+=ao.getDxTotal();
					sheet.addCell(new Label(7,row,ao.getOutOfSystemTotal()==0?"":Tools.getValuesNotZore(ao.getOutOfSystemTotal()),fc));total5+=ao.getOutOfSystemTotal();
					
					sheet.addCell(new Label(8,row,ao.getManagerPostLeaves()==0?"":Tools.getValuesNotZore(ao.getManagerPostLeaves()),fc));postion1+=ao.getManagerPostLeaves();
					sheet.addCell(new Label(9,row,ao.getCabinetLeaves()==0?"":Tools.getValuesNotZore(ao.getCabinetLeaves()),fc));postion2+=ao.getCabinetLeaves();
					sheet.addCell(new Label(10,row,ao.getCustomerManageLeaves()==0?"":Tools.getValuesNotZore(ao.getCustomerManageLeaves()),fc));postion3+=ao.getCustomerManageLeaves();
					sheet.addCell(new Label(11,row,ao.getOherLeaves()==0?"":Tools.getValuesNotZore(ao.getOherLeaves()),fc));postion4+=ao.getOherLeaves();
					
					
					sheet.addCell(new Label(12,row,ao.getAnnualLeave()==0?"":Tools.getValuesNotZore(ao.getAnnualLeave()),fc));holiday1+=ao.getAnnualLeave();
					sheet.addCell(new Label(13,row,ao.getFamilyPlanningLeave()==0?"":Tools.getValuesNotZore(ao.getFamilyPlanningLeave()),fc));holiday2+=ao.getFamilyPlanningLeave();
					sheet.addCell(new Label(14,row,ao.getMaternityLeave()==0?"":Tools.getValuesNotZore(ao.getMaternityLeave()),fc));holiday3+=ao.getMaternityLeave();
					sheet.addCell(new Label(15,row,ao.getSickBaoLeave()==0?"":Tools.getValuesNotZore(ao.getSickBaoLeave()),fc));holiday4+=ao.getSickBaoLeave();
					sheet.addCell(new Label(16,row,ao.getSickFeiBaoLeave()==0?"":Tools.getValuesNotZore(ao.getSickFeiBaoLeave()),fc));holiday5+=ao.getSickFeiBaoLeave();
					
					List<SickHoliday> shList = attShiftService.findSickHolidays(org.getId()+"", queryMap);
					int total = this.getSickFeiMoreThan6(shList);
					sheet.addCell(new Label(17,row,total==0?"":total+"",fc));holiday6+=total;
					sheet.addCell(new Label(18,row,ao.getThingsLeave()==0?"":Tools.getValuesNotZore(ao.getThingsLeave()),fc));holiday7+=ao.getThingsLeave();
					sheet.addCell(new Label(19,row,ao.getOtherHoldiayLeave()==0?"":Tools.getValuesNotZore(ao.getOtherHoldiayLeave()),fc));holiday8+=ao.getOtherHoldiayLeave();
					
					sheet.addCell(new Label(20,row,ao.getAbsents()==0?"":Tools.getValuesNotZore(ao.getAbsents()),fc));holiday9+=ao.getAbsents();
					sheet.addCell(new Label(21,row,ao.getLates()==0?"":Tools.getValuesNotZore(ao.getLates()),fc));holiday10+=ao.getLates();
					sheet.addCell(new Label(22,row,ao.getEarlys()==0?"":Tools.getValuesNotZore(ao.getEarlys()),fc));holiday11+=ao.getEarlys();
					
					List<AttshiftMoreThanMidole> listAmm = attShiftService.getXiuJia(org.getId()+"", queryMap);
					int holidaysTotal=0;
					String content = "";
					if(listAmm!=null&&listAmm.size()!=0){
						for(AttshiftMoreThanMidole amm:listAmm){
							if("year".equals(queryMap.get("reportType"))){
								if(amm.getHolidays()>=180){
									holidaysTotal++;
								}
							}else {
								if(amm.getHolidays()>=15){
									holidaysTotal++;
								}
							}
						}
						
						if(holidaysTotal==0){
							content = "";
						}else{
							content = "休假超过半个月总共有"+holidaysTotal+"人";
						}
						sheet.addCell(new Label(23,row,content,fc));
					}else{
						sheet.addCell(new Label(23,row,"",fc));
					}
					
					holdayTotalAll+=holidaysTotal;
					
					
					row++;
				}else{
					sheet.mergeCells(0, row, 0,row+size+1);
					sheet.addCell(new Label(1,row,org.getName(),fc));
					
					AttshiftsOrg ao2 = attShiftService.getAttshiftsOrg(org.getId()+"",queryMap);
					
					AttshiftsOrg ao1 = this.getAttshiftOrgHasHolidays(ao2, totalHoliday,org.getId()+"");
					
					int holidayXJ=0,holidayTotalXJ=0;
					double total1XJ=0,total2XJ=0,total3XJ=0,total4XJ=0,total5XJ=0;
					double postion1XJ=0,postion2XJ=0,postion3XJ=0,postion4XJ=0,holiday1XJ=0,holiday2XJ=0,holiday3XJ=0,holiday4XJ=0,
					holiday5XJ=0,holiday6XJ=0,holiday7XJ=0,holiday8XJ=0,holiday9XJ=0,holiday10XJ=0,holiday11XJ=0;
					
				
					sheet.addCell(new Label(2,row,ao1.getHolidayPersonTotal()==0?"":ao1.getHolidayPersonTotal()+"",fc));holidayPersonTotal+=ao1.getHolidayPersonTotal();holidayXJ+=ao1.getHolidayPersonTotal();	
					sheet.addCell(new Label(3,row,ao1.getLongOrMiddleTermTotal()==0?"":Tools.getValuesNotZore(ao1.getLongOrMiddleTermTotal()),fc));total1+=ao1.getLongOrMiddleTermTotal();total1XJ+=ao1.getLongOrMiddleTermTotal();
					sheet.addCell(new Label(4,row,ao1.getShortTermTotal()==0?"":Tools.getValuesNotZore(ao1.getShortTermTotal()),fc));total2+=ao1.getShortTermTotal();total2XJ+=ao1.getShortTermTotal();
					sheet.addCell(new Label(5,row,ao1.getLaoWuTotal()==0?"":Tools.getValuesNotZore(ao1.getLaoWuTotal()),fc));total3+=ao1.getLaoWuTotal();total3XJ+=ao1.getLaoWuTotal();
					sheet.addCell(new Label(6,row,ao1.getDxTotal()==0?"":Tools.getValuesNotZore(ao1.getDxTotal()),fc));total4+=ao1.getDxTotal();total4XJ+=ao1.getDxTotal();
					sheet.addCell(new Label(7,row,ao1.getOutOfSystemTotal()==0?"":Tools.getValuesNotZore(ao1.getOutOfSystemTotal()),fc));total5+=ao1.getOutOfSystemTotal();total5XJ+=ao1.getOutOfSystemTotal();
					
					sheet.addCell(new Label(8,row,ao1.getManagerPostLeaves()==0?"":Tools.getValuesNotZore(ao1.getManagerPostLeaves()),fs));postion1 +=ao1.getManagerPostLeaves();postion1XJ +=ao1.getManagerPostLeaves();
					sheet.addCell(new Label(9,row,ao1.getCabinetLeaves()==0?"":Tools.getValuesNotZore(ao1.getCabinetLeaves()),fs));postion2 +=ao1.getCabinetLeaves();postion2XJ +=ao1.getCabinetLeaves();
					sheet.addCell(new Label(10,row,ao1.getCustomerManageLeaves()==0?"":Tools.getValuesNotZore(ao1.getCustomerManageLeaves()),fs));postion3 +=ao1.getCustomerManageLeaves();postion3XJ +=ao1.getCustomerManageLeaves();
					sheet.addCell(new Label(11,row,ao1.getOherLeaves()==0?"":Tools.getValuesNotZore(ao1.getOherLeaves()),fs));postion4 +=ao1.getOherLeaves();postion4XJ +=ao1.getOherLeaves();
					
					sheet.addCell(new Label(12,row,ao1.getAnnualLeave()==0?"":Tools.getValuesNotZore(ao1.getAnnualLeave()),fc));holiday1 += ao1.getAnnualLeave();holiday1XJ += ao1.getAnnualLeave();
					sheet.addCell(new Label(13,row,ao1.getFamilyPlanningLeave()==0?"":Tools.getValuesNotZore(ao1.getFamilyPlanningLeave()),fc));holiday2 += ao1.getFamilyPlanningLeave();holiday2XJ += ao1.getFamilyPlanningLeave();
					sheet.addCell(new Label(14,row,ao1.getMaternityLeave()==0?"":Tools.getValuesNotZore(ao1.getMaternityLeave()),fc));holiday3 += ao1.getMaternityLeave();holiday3XJ += ao1.getMaternityLeave();
					sheet.addCell(new Label(15,row,ao1.getSickBaoLeave()==0?"":Tools.getValuesNotZore(ao1.getSickBaoLeave()),fc));holiday4 += ao1.getSickBaoLeave();holiday4XJ += ao1.getSickBaoLeave();
					sheet.addCell(new Label(16,row,ao1.getSickFeiBaoLeave()==0?"":Tools.getValuesNotZore(ao1.getSickFeiBaoLeave()),fc));holiday5 += ao1.getSickFeiBaoLeave();holiday5XJ += ao1.getSickFeiBaoLeave();
					
					List<SickHoliday> shList = attShiftService.findSickHolidays(org.getId()+"", queryMap);
					int total = this.getSickFeiMoreThan6(shList);
					sheet.addCell(new Label(17,row,total==0?"":total+"",fc));holiday6 += total;holiday6XJ += total;
					sheet.addCell(new Label(18,row,ao1.getThingsLeave()==0?"":Tools.getValuesNotZore(ao1.getThingsLeave()),fc));holiday7 += ao1.getThingsLeave();holiday7XJ += ao1.getThingsLeave();
					sheet.addCell(new Label(19,row,ao1.getOtherHoldiayLeave()==0?"":Tools.getValuesNotZore(ao1.getOtherHoldiayLeave()),fc));holiday8 += ao1.getOtherHoldiayLeave();holiday8XJ += ao1.getOtherHoldiayLeave();
					
					sheet.addCell(new Label(20,row,ao1.getAbsents()==0?"":Tools.getValuesNotZore(ao1.getAbsents()),fc));holiday9 += ao1.getAbsents();holiday9XJ += ao1.getAbsents();
					sheet.addCell(new Label(21,row,ao1.getLates()==0?"":Tools.getValuesNotZore(ao1.getLates()),fc));holiday10 += ao1.getLates();holiday10XJ += ao1.getLates();
					sheet.addCell(new Label(22,row,ao1.getEarlys()==0?"":Tools.getValuesNotZore(ao1.getEarlys()),fc));holiday11 += ao1.getEarlys();holiday11XJ += ao1.getEarlys();
//					sheet.addCell(new Label(22,row,"",fc));
					
					List<AttshiftMoreThanMidole> listAmm = attShiftService.getXiuJia(org.getId()+"",queryMap);
					int holidaysTotal=0;
					String content = "";
					if(listAmm!=null&&listAmm.size()!=0){
						for(AttshiftMoreThanMidole amm:listAmm){
							if("year".equals(queryMap.get("reportType"))){
								if(amm.getHolidays()>=180){
									holidaysTotal++;
								}
							}else {
								if(amm.getHolidays()>=15){
									holidaysTotal++;
								}
							}
						}
						
						if(holidaysTotal==0){
							content = "";
						}else{
							content = "休假超过半个月总共有"+holidaysTotal+"人";
						}
						sheet.addCell(new Label(23,row,content,fc));
					}else{
						sheet.addCell(new Label(23,row,"",fc));
					}
					
					holdayTotalAll+=holidaysTotal;
					holidayTotalXJ+=holidaysTotal;
					row++;
					
					for(Organization child : childs){
						sheet.addCell(new Label(1,row,child.getName(),fc));
						
						AttshiftsOrg attshiftOrg1 = attShiftService.getAttshiftsOrg(child.getId()+"",queryMap);
						
						AttshiftsOrg attshiftOrg = this.getAttshiftOrgHasHolidays(attshiftOrg1, totalHoliday,child.getId()+"");
//						sheet.addCell(new Label(1,row,org.getName(),fc));
						sheet.addCell(new Label(2,row,attshiftOrg.getHolidayPersonTotal()==0?"":attshiftOrg.getHolidayPersonTotal()+"",fc));holidayPersonTotal+=attshiftOrg.getHolidayPersonTotal();holidayXJ+=attshiftOrg.getHolidayPersonTotal();
						
						sheet.addCell(new Label(3,row,attshiftOrg.getLongOrMiddleTermTotal()==0?"":Tools.getValuesNotZore(attshiftOrg.getLongOrMiddleTermTotal()),fc));total1 +=attshiftOrg.getLongOrMiddleTermTotal();total1XJ +=attshiftOrg.getLongOrMiddleTermTotal();
						sheet.addCell(new Label(4,row,attshiftOrg.getShortTermTotal()==0?"":Tools.getValuesNotZore(attshiftOrg.getShortTermTotal()),fc));total2 +=attshiftOrg.getShortTermTotal();total2XJ +=attshiftOrg.getShortTermTotal();
						sheet.addCell(new Label(5,row,attshiftOrg.getLaoWuTotal()==0?"":Tools.getValuesNotZore(attshiftOrg.getLaoWuTotal()),fc));total3 +=attshiftOrg.getLaoWuTotal();total3XJ +=attshiftOrg.getLaoWuTotal();
						sheet.addCell(new Label(6,row,attshiftOrg.getDxTotal()==0?"":Tools.getValuesNotZore(attshiftOrg.getDxTotal()),fc));total4 +=attshiftOrg.getDxTotal();total4XJ +=attshiftOrg.getDxTotal();
						sheet.addCell(new Label(7,row,attshiftOrg.getOutOfSystemTotal()==0?"":Tools.getValuesNotZore(attshiftOrg.getOutOfSystemTotal()),fc));total5 +=attshiftOrg.getOutOfSystemTotal();total5XJ +=attshiftOrg.getOutOfSystemTotal();
						
						sheet.addCell(new Label(8,row,attshiftOrg.getManagerPostLeaves()==0?"":Tools.getValuesNotZore(attshiftOrg.getManagerPostLeaves()),fs));postion1+=attshiftOrg.getManagerPostLeaves();postion1XJ+=attshiftOrg.getManagerPostLeaves();
						sheet.addCell(new Label(9,row,attshiftOrg.getCabinetLeaves()==0?"":Tools.getValuesNotZore(attshiftOrg.getCabinetLeaves()),fs));postion2+=attshiftOrg.getCabinetLeaves();postion2XJ+=attshiftOrg.getCabinetLeaves();
						sheet.addCell(new Label(10,row,attshiftOrg.getCustomerManageLeaves()==0?"":Tools.getValuesNotZore(attshiftOrg.getCustomerManageLeaves()),fs));postion3+=attshiftOrg.getCustomerManageLeaves();postion3XJ+=attshiftOrg.getCustomerManageLeaves();
						sheet.addCell(new Label(11,row,attshiftOrg.getOherLeaves()==0?"":Tools.getValuesNotZore(attshiftOrg.getOherLeaves()),fs));postion4+=attshiftOrg.getOherLeaves();postion4XJ+=attshiftOrg.getOherLeaves();
						
						sheet.addCell(new Label(12,row,attshiftOrg.getAnnualLeave()==0?"":Tools.getValuesNotZore(attshiftOrg.getAnnualLeave()),fc));holiday1 +=attshiftOrg.getAnnualLeave();holiday1XJ +=attshiftOrg.getAnnualLeave();
						sheet.addCell(new Label(13,row,attshiftOrg.getFamilyPlanningLeave()==0?"":Tools.getValuesNotZore(attshiftOrg.getFamilyPlanningLeave()),fc));holiday2 +=attshiftOrg.getFamilyPlanningLeave();holiday2XJ +=attshiftOrg.getFamilyPlanningLeave();
						sheet.addCell(new Label(14,row,attshiftOrg.getMaternityLeave()==0?"":Tools.getValuesNotZore(attshiftOrg.getMaternityLeave()),fc));holiday3 +=attshiftOrg.getMaternityLeave();holiday3XJ +=attshiftOrg.getMaternityLeave();
						sheet.addCell(new Label(15,row,attshiftOrg.getSickBaoLeave()==0?"":Tools.getValuesNotZore(attshiftOrg.getSickBaoLeave()),fc));holiday4 +=attshiftOrg.getSickBaoLeave();holiday4XJ +=attshiftOrg.getSickBaoLeave();
						sheet.addCell(new Label(16,row,attshiftOrg.getSickFeiBaoLeave()==0?"":Tools.getValuesNotZore(attshiftOrg.getSickFeiBaoLeave()),fc));holiday5 +=attshiftOrg.getSickFeiBaoLeave();holiday5XJ +=attshiftOrg.getSickFeiBaoLeave();
						
						List<SickHoliday> sList = attShiftService.findSickHolidays(child.getId()+"", queryMap);
						int sickTotal = this.getSickFeiMoreThan6(sList);
						sheet.addCell(new Label(17,row,sickTotal==0?"":sickTotal+"",fc));holiday6 +=sickTotal;holiday6XJ +=sickTotal;
						sheet.addCell(new Label(18,row,attshiftOrg.getThingsLeave()==0?"":Tools.getValuesNotZore(attshiftOrg.getThingsLeave()),fc));holiday7 +=attshiftOrg.getThingsLeave();holiday7XJ +=attshiftOrg.getThingsLeave();
						sheet.addCell(new Label(19,row,attshiftOrg.getOtherHoldiayLeave()==0?"":Tools.getValuesNotZore(attshiftOrg.getOtherHoldiayLeave()),fc));holiday8 +=attshiftOrg.getOtherHoldiayLeave();holiday8XJ +=attshiftOrg.getOtherHoldiayLeave();
						
						sheet.addCell(new Label(20,row,attshiftOrg.getAbsents()==0?"":Tools.getValuesNotZore(attshiftOrg.getAbsents()),fc));holiday9 +=attshiftOrg.getAbsents();holiday9XJ +=attshiftOrg.getAbsents();
						sheet.addCell(new Label(21,row,attshiftOrg.getLates()==0?"":Tools.getValuesNotZore(attshiftOrg.getLates()),fc));holiday10 +=attshiftOrg.getLates();holiday10XJ +=attshiftOrg.getLates();
						sheet.addCell(new Label(22,row,attshiftOrg.getEarlys()==0?"":Tools.getValuesNotZore(attshiftOrg.getEarlys()),fc));holiday11 +=attshiftOrg.getEarlys();holiday11XJ +=attshiftOrg.getEarlys();
//						sheet.addCell(new Label(22,row,"",fc));
						
						List<AttshiftMoreThanMidole> listAmm1 = attShiftService.getXiuJia(org.getId()+"", queryMap);
						int holidaysTotal2=0;
						String content1 = "";
						if(listAmm1!=null&&listAmm1.size()!=0){
							for(AttshiftMoreThanMidole amm:listAmm1){
								
								if("year".equals(queryMap.get("reportType"))){
									if(amm.getHolidays()>=180){
										holidaysTotal++;
									}
								}else {
									if(amm.getHolidays()>=15){
										holidaysTotal++;
									}
								}
							}
							if(holidaysTotal2==0){
								content1 = "";
							}else{
								content1 = "休假超过半个月总共有"+holidaysTotal2+"人";
							}
							sheet.addCell(new Label(23,row,content1,fc));
						}else{
							sheet.addCell(new Label(23,row,"",fc));
						}
						holdayTotalAll+=holidaysTotal2;
						holidayTotalXJ+=holidaysTotal2;
						row++;
					}
					sheet.addCell(new Label(0,row,"",fc));
//					sheet.mergeCells(0, row, 1, 0);
					sheet.addCell(new Label(1,row,"小计",fc));
					sheet.addCell(new Label(2,row,holidayXJ==0?"":holidayXJ+"",fc));sheet.addCell(new Label(3,row,total1XJ==0?"":Tools.getValuesNotZore(total1XJ),fc));
					sheet.addCell(new Label(4,row,total2XJ==0?"":Tools.getValuesNotZore(total2XJ),fc));sheet.addCell(new Label(5,row,total3XJ==0?"":Tools.getValuesNotZore(total3XJ),fc));
					sheet.addCell(new Label(6,row,total4XJ==0?"":Tools.getValuesNotZore(total4XJ),fc));sheet.addCell(new Label(7,row,total5XJ==0?"":Tools.getValuesNotZore(total5XJ),fc));
					sheet.addCell(new Label(8,row,postion1XJ==0?"":Tools.getValuesNotZore(postion1XJ),fc));sheet.addCell(new Label(9,row,postion2XJ==0?"":Tools.getValuesNotZore(postion2XJ),fc));
					sheet.addCell(new Label(10,row,postion3XJ==0?"":Tools.getValuesNotZore(postion3XJ),fc));sheet.addCell(new Label(11,row,postion4XJ==0?"":Tools.getValuesNotZore(postion4XJ),fc));
					sheet.addCell(new Label(12,row,holiday1XJ==0?"":Tools.getValuesNotZore(holiday1XJ),fc));sheet.addCell(new Label(13,row,holiday2XJ==0?"":Tools.getValuesNotZore(holiday2XJ),fc));
					sheet.addCell(new Label(14,row,holiday3XJ==0?"":Tools.getValuesNotZore(holiday3XJ),fc));sheet.addCell(new Label(15,row,holiday4XJ==0?"":Tools.getValuesNotZore(holiday4XJ),fc));
					sheet.addCell(new Label(16,row,holiday5XJ==0?"":Tools.getValuesNotZore(holiday5XJ),fc));sheet.addCell(new Label(17,row,holiday6XJ==0?"":Tools.getValuesNotZore(holiday6XJ),fc));
					sheet.addCell(new Label(18,row,holiday7XJ==0?"":Tools.getValuesNotZore(holiday7XJ),fc));sheet.addCell(new Label(19,row,holiday8XJ==0?"":Tools.getValuesNotZore(holiday8XJ),fc));
					
					sheet.addCell(new Label(20,row,holiday9XJ==0?"":Tools.getValuesNotZore(holiday9XJ),fc));
					sheet.addCell(new Label(21,row,holiday10XJ==0?"":Tools.getValuesNotZore(holiday10XJ),fc));sheet.addCell(new Label(22,row,holiday11XJ==0?"":Tools.getValuesNotZore(holiday11XJ),fc));
					if(holidayTotalXJ==0){
						sheet.addCell(new Label(23,row,"",fc));
					}else{
						sheet.addCell(new Label(23,row,"休假超过半个月总共有"+holidayTotalXJ+"人",fc));
					}
					
					holidayXJ=0;total1XJ=0;total2XJ=0;total3XJ=0;total4XJ=0;total5XJ=0;
					postion1XJ=0;postion2XJ=0;postion3XJ=0;postion4XJ=0;holiday1XJ=0;holiday2XJ=0;holiday3XJ=0;holiday4XJ=0;
					holiday5XJ=0;holiday6XJ=0;holiday7XJ=0;holiday8XJ=0;holiday9XJ=0;holiday10XJ=0;holiday11XJ=0;holidayTotalXJ = 0;
					
					row++;
				}
			}
//			sheet.mergeCells(0, row, 1, 0);
			sheet.addCell(new Label(0,row,"",fc));
			sheet.addCell(new Label(1,row,"总计",fc));
			sheet.addCell(new Label(2,row,holidayPersonTotal+"",fc));sheet.addCell(new Label(3,row,total1==0?"":Tools.getValuesNotZore(total1),fc));
			sheet.addCell(new Label(4,row,total2==0?"":Tools.getValuesNotZore(total2),fc));sheet.addCell(new Label(5,row,total3==0?"":Tools.getValuesNotZore(total3),fc));
			sheet.addCell(new Label(6,row,total4==0?"":Tools.getValuesNotZore(total4),fc));sheet.addCell(new Label(7,row,total5==0?"":Tools.getValuesNotZore(total5),fc));
			sheet.addCell(new Label(8,row,postion1==0?"":Tools.getValuesNotZore(postion1),fc));sheet.addCell(new Label(9,row,postion2==0?"":Tools.getValuesNotZore(postion2),fc));
			sheet.addCell(new Label(10,row,postion3==0?"":Tools.getValuesNotZore(postion3),fc));sheet.addCell(new Label(11,row,postion4==0?"":Tools.getValuesNotZore(postion4),fc));
			sheet.addCell(new Label(12,row,holiday1==0?"":Tools.getValuesNotZore(holiday1),fc));sheet.addCell(new Label(13,row,holiday2==0?"":Tools.getValuesNotZore(holiday2),fc));
			sheet.addCell(new Label(14,row,holiday3==0?"":Tools.getValuesNotZore(holiday3),fc));sheet.addCell(new Label(15,row,holiday4==0?"":Tools.getValuesNotZore(holiday4),fc));
			sheet.addCell(new Label(16,row,holiday5==0?"":Tools.getValuesNotZore(holiday5),fc));sheet.addCell(new Label(17,row,holiday6==0?"":Tools.getValuesNotZore(holiday6),fc));
			sheet.addCell(new Label(18,row,holiday7==0?"":Tools.getValuesNotZore(holiday6),fc));sheet.addCell(new Label(19,row,holiday8==0?"":Tools.getValuesNotZore(holiday8),fc));
			
			sheet.addCell(new Label(20,row,holiday9==0?"":Tools.getValuesNotZore(holiday9),fc));
			sheet.addCell(new Label(21,row,holiday10==0?"":Tools.getValuesNotZore(holiday10),fc));sheet.addCell(new Label(22,row,holiday11==0?"":Tools.getValuesNotZore(holiday11),fc));
			
			if(holdayTotalAll==0){
				sheet.addCell(new Label(23,row,"",fc));
			}else{
				sheet.addCell(new Label(23,row,"全行休假超过半个月总共有"+holdayTotalAll+"人",fc));
			}
			
			
			sheet.setColumnView(0, 20);sheet.setColumnView(20, 7);
			sheet.setColumnView(1, 30);sheet.setColumnView(21, 7);
			sheet.setColumnView(2, 7);sheet.setColumnView(22, 7);sheet.setColumnView(23, 7);
			sheet.setColumnView(3, 7);sheet.setColumnView(10, 7);sheet.setColumnView(13, 7);
			sheet.setColumnView(4, 7);sheet.setColumnView(7, 7);sheet.setColumnView(14, 7);
			sheet.setColumnView(5, 7);sheet.setColumnView(8, 7);
			sheet.setColumnView(6, 7);sheet.setColumnView(9, 7);
			book.write();
			book.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return tmpFile;
	}
	
	
	
	private AttshiftsOrg getAttshiftOrgHasHolidays(AttshiftsOrg ao,EmployeeTotal totalHoliday ,String orgId){
		
		//判断如果某个部门没有员工统计时候就跳过
//		List<Employee> list = employeeService.getEmployeeByOrgId(orgId);
		//判断得一个部门attshift中有请假有迟到早退旷工现象
		
		//这些机构没有员工('福田支行结算业务部','盐田区域中心结算业务部','上步支行结算业务部','田背支行结算业务部','罗湖支行结算业务部','景苑支行结算业务部')
		//(99,116,133,147,167,212)
//		String[] aa = {99,116,133,147,167,212,};
		if(totalHoliday.getPeopleTotal()>=0 && !"224".equals(orgId)&&!"99".equals(orgId)&&!"116".equals(orgId)
				&&!"133".equals(orgId)&&!"147".equals(orgId)&&!"167".equals(orgId)&&!"212".equals(orgId)){
			ao.setHolidayPersonTotal(ao.getHolidayPersonTotal()-1);
			
			ao.setLongOrMiddleTermTotal(ao.getLongOrMiddleTermTotal()-1);
			ao.setShortTermTotal(ao.getShortTermTotal()-1);
			ao.setLaoWuTotal(ao.getLaoWuTotal()-1);
			ao.setDxTotal(ao.getDxTotal()-1);
			ao.setOutOfSystemTotal(ao.getOutOfSystemTotal()-1);
			
			ao.setManagerPostLeaves(ao.getManagerPostLeaves()-1);
			ao.setCabinetLeaves(ao.getCabinetLeaves()-1);
			ao.setCustomerManageLeaves(ao.getCustomerManageLeaves()-1);
			ao.setOherLeaves(ao.getOherLeaves()-1);
			
			ao.setAnnualLeave(ao.getAnnualLeave()-1);
			ao.setFamilyPlanningLeave(ao.getFamilyPlanningLeave()-1);
			ao.setMaternityLeave(ao.getMaternityLeave()-1);
			ao.setSickBaoLeave(ao.getSickBaoLeave()-1);
			ao.setSickFeiBaoLeave(ao.getSickFeiBaoLeave()-1);
			ao.setThingsLeave(ao.getThingsLeave()-1);
			ao.setOtherHoldiayLeave(ao.getOtherHoldiayLeave()-1);
			
			ao.setAbsents(ao.getAbsents()-1);
			ao.setLates(ao.getLates()-1);
			ao.setEarlys(ao.getEarlys()-1);
			
		}
		return ao;
	}
	
private AttshiftsOrg getAttshiftOrgHasHolidays(AttshiftsOrg ao,SickPeopleTotal totalHoliday ,String orgId){
		
		//判断如果某个部门没有员工统计时候就跳过
//		List<Employee> list = employeeService.getEmployeeByOrgId(orgId);
		//判断得一个部门attshift中有请假有迟到早退旷工现象
		if(totalHoliday.getSickPeopleTotal()>=0 && !"224".equals(orgId)){
//			ao.setHolidayPersonTotal(ao.getHolidayPersonTotal()-1);
			
//			ao.setLongOrMiddleTermTotal(ao.getLongOrMiddleTermTotal()-1);
//			ao.setShortTermTotal(ao.getShortTermTotal()-1);
//			ao.setLaoWuTotal(ao.getLaoWuTotal()-1);
//			ao.setDxTotal(ao.getDxTotal()-1);
//			ao.setOutOfSystemTotal(ao.getOutOfSystemTotal()-1);
			
//			ao.setManagerPostLeaves(ao.getManagerPostLeaves()-1);
//			ao.setCabinetLeaves(ao.getCabinetLeaves()-1);
//			ao.setCustomerManageLeaves(ao.getCustomerManageLeaves()-1);
//			ao.setOherLeaves(ao.getOherLeaves()-1);
			
//			ao.setAnnualLeave(ao.getAnnualLeave()-1);
//			ao.setFamilyPlanningLeave(ao.getFamilyPlanningLeave()-1);
//			ao.setMaternityLeave(ao.getMaternityLeave()-1);
			ao.setSickBaoLeave(ao.getSickBaoLeave()-1);
			ao.setSickFeiBaoLeave(ao.getSickFeiBaoLeave()-1);
//			ao.setThingsLeave(ao.getThingsLeave()-1);
//			ao.setOtherHoldiayLeave(ao.getOtherHoldiayLeave()-1);
			
//			ao.setAbsents(ao.getAbsents()-1);
//			ao.setLates(ao.getLates()-1);
//			ao.setEarlys(ao.getEarlys()-1);
			
		}
		return ao;
	}
	/**
	 * 获取非保胎假超过6个月的员工人数
	 * @param list
	 * @return
	 */
	private int getSickFeiMoreThan6(List<SickHoliday> list){
		int total = 0;
		if(list!=null&&list.size()>0){
			for(SickHoliday sh:list){
				if(sh.getSickFeiBaoHolidays()>=180){
					total +=total;
				}
			}
			return total;
		}else {
			return 0;
		}
	}
	
//	private String getLeaveContent(List<AttshiftMoreThanMidole> list){
//		String content = "";
//		if
//	}
	
	/**
	 * 导出部门排班报表
	 * @param fileName
	 * @param orgId
	 * @return
	 */
	public File exportShiftToOrganization(String fileName,String orgId){
		String temporaryFile = System.getProperty("java.io.tmpdir");
		File tmpFile = new File(temporaryFile+fileName);
		WritableWorkbook book;
		try{
			book = Workbook.createWorkbook(tmpFile);
			//设置样式
			WritableFont fontTitle1 = new WritableFont(WritableFont.createFont("标题一"), 16, WritableFont.BOLD);
			WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle1);
			formatTitle1.setAlignment(Alignment.CENTRE);
			formatTitle1.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffb = new WritableFont(WritableFont.createFont("宋体"),10, WritableFont.BOLD);
			WritableCellFormat fb = new WritableCellFormat(ffb);
			fb.setAlignment(Alignment.CENTRE);
			fb.setWrap(true);//自动换行
			Colour co = Colour.GRAY_25;
			fb.setBackground(co);
			fb.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			fb.setVerticalAlignment(VerticalAlignment.CENTRE);
			fb.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont f = new WritableFont(WritableFont.createFont("宋体"),11,WritableFont.BOLD);
			WritableCellFormat fc = new WritableCellFormat(f);
			fc.setAlignment(Alignment.CENTRE);
			fc.setVerticalAlignment(VerticalAlignment.CENTRE);
			fc.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffs = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fs = new WritableCellFormat(ffs);
			fs.setAlignment(Alignment.CENTRE);
			fs.setVerticalAlignment(VerticalAlignment.CENTRE);
			fs.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			
			WritableSheet sheet = book.createSheet("全行在岗人员考勤统计表", 0);
			
			book.write();
			book.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return tmpFile;
	}
	
	public boolean exportOrNot(MonthStatistics m){
		if(m.getSickLeave()>0){
			return false;
		}else if(m.getLeaves()>0){
			return false;
		}else if(m.getHomeLeave()>0){
			return false;
		}else if(m.getMaternityLeave()>0){
			return false;
		}else if(m.getFamilyPlanningLeave()>0){
			return false;
		}else if(m.getAbsents()>0){
			return false;
		}else if(m.getLates()>0){
			return false;
		}else if(m.getEarlys()>0){
			return false;
		}else if(m.getAnnualLeave()>0){
			return false;
		}else if(m.getFeedLeave()>0){
			return false;
		}else if(m.getFuneralLeave()>0){
			return false;
		}else if(m.getInjuryLeave()>0){
			return false;
		}else if(m.getLookAfterLeave()>0){
			return false;
		}else if(m.getMarriageLeave()>0){
			return false;
		}else if(m.getNurseLeave()>0){
			return false;
		}else if(m.getTakeCareLeave()>0){
			return false;
		}else if(m.getHolidays()>0){
			return false;
		}
		return true;
	}
	public boolean exportOrNot1(AttShifts m){
		if(m.getSickLeave()>0){
			return false;
		}else if(m.getLeave()>0){
			return false;
		}else if(m.getHomeLeave()>0){
			return false;
		}else if(m.getMaternityLeave()>0){
			return false;
		}else if(m.getFamilyPlanningLeave()>0){
			return false;
		}else if(m.getAbsents()>0){
			return false;
		}else if(m.getLates()>0){
			return false;
		}else if(m.getEarlys()>0){
			return false;
		}else if(m.getAnnualLeave()>0){
			return false;
		}else if(m.getFeedLeave()>0){
			return false;
		}else if(m.getFuneralLeave()>0){
			return false;
		}else if(m.getInjuryLeave()>0){
			return false;
		}else if(m.getLookAfterLeave()>0){
			return false;
		}else if(m.getMarriageLeave()>0){
			return false;
		}else if(m.getNurseLeave()>0){
			return false;
		}
	///////2012-09-24///////////////////////////////	
		else if(m.getTakeCareLeave()>0){
			return false;
		}else if(m.getHolidays()>0){
			return false;
		}
		return true;
	}
	public File exportExcelAffirmed(String fileName,List<MonthStatistics> list,int year,int month){
		String temporaryFile = System.getProperty("java.io.tmpdir");
		File tmpFile = new File(temporaryFile+fileName);
		WritableWorkbook book;
		double lateOrEarlyDeductStardard = Long.parseLong(configService.get(ConfigKey.LATEOREARLY_DEDUCT_BASIC_SALARY_STANDARD));
		double absentDeductStardard = Long.parseLong(configService.get(ConfigKey.ABSENT_DEDUCT_BASIC_SALARY_STANDARD));
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);c.set(Calendar.MONTH, month-1);
		double monthOfDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		try {
			book = Workbook.createWorkbook(tmpFile);
			WritableSheet sheet = book.createSheet("Sheet_1", 0);
			
			//设置样式
			WritableFont fontTitle1 = new WritableFont(WritableFont.createFont("标题一"), 16, WritableFont.BOLD);
			WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle1);
			formatTitle1.setAlignment(Alignment.CENTRE);
			formatTitle1.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffb = new WritableFont(WritableFont.createFont("宋体"),11, WritableFont.BOLD);
			WritableCellFormat fb = new WritableCellFormat(ffb);
			fb.setAlignment(Alignment.CENTRE);
			
//			Colour co = Colour.GRAY_25;
//			fb.setBackground(co);
			
			fb.setVerticalAlignment(VerticalAlignment.CENTRE);
			fb.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont f = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fc = new WritableCellFormat(f);
			fc.setAlignment(Alignment.CENTRE);
//			fc.setVerticalAlignment(VerticalAlignment.CENTRE);
			fc.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffs = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fs = new WritableCellFormat(ffs);
			fs.setAlignment(Alignment.CENTRE);
			fs.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			sheet.mergeCells(0, 0,14, 0);
			sheet.addCell(new Label(0,0,year+"年"+month+"月考勤情况汇总表",formatTitle1));
//			sheet.mergeCells(0, 1,14, 0);
			Label label01_ = new Label(0, 1, "日期：",fc);
			sheet.addCell(label01_);
			Label label11_ = new Label(1, 1,Tools.date2Date(new Date()),fc); 
			sheet.addCell(label11_);
			Label label0 = new Label(0, 2, "序号",fb); 
			Label label1 = new Label(1, 2, "员工编号",fb); 
			Label label2 = new Label(2, 2, "姓名",fb);
			Label label15 = new Label(3, 2, "一级部门",fb);
			Label label3 = new Label(4 ,2,  "所在部门",fb);
			Label label4 = new Label( 5,2, "病假天数",fb);
			Label label5 = new Label(6, 2, "事假天数",fb);
			Label label6 = new Label(7, 2, "探亲假天数",fb);
			Label label7 = new Label(8, 2, "产假天数",fb);
			Label label8 = new Label(9, 2, "计划生育假天数",fb);
			Label label9 = new Label(10, 2, "旷工天数",fb);
			Label label10 = new Label(11, 2, "迟到早退次数",fb);
			Label label11 = new Label(12, 2, "扣月基本工资",fb);
			Label label12 = new Label(13, 2, "扣区域津贴",fb);
//			Label label13 = new Label(13, 2, month+"月休假情况",fb);
			Label label14 = new Label(14, 2, "备注",fb);
		
			
			sheet.addCell(label0);sheet.addCell(label1);sheet.addCell(label2);sheet.addCell(label3);
			sheet.addCell(label4);sheet.addCell(label5);sheet.addCell(label6);sheet.addCell(label7);
			sheet.addCell(label8);sheet.addCell(label9);sheet.addCell(label10);sheet.addCell(label11);
			sheet.addCell(label12);
//			sheet.addCell(label13);
			sheet.addCell(label14);
			sheet.addCell(label15);
			int i = 3;
			for(MonthStatistics a : list){
				if(exportOrNot(a)){
					continue;
				}
				jxl.write.Number label_0 = new jxl.write.Number(0, i, i-2,fc);
				jxl.write.Label label_1 = new jxl.write.Label(1,i,a.getEmployeeCode(),fs);
				jxl.write.Label label_2 = new jxl.write.Label(2,i,a.getEmployeeName(),fs);
				
				String firstDeptName = organizationService.getFirstOrganization(a.getDeptName());
				jxl.write.Label label_15 = new jxl.write.Label(3,i,firstDeptName,fc);
				
				
				jxl.write.Label label_3 = new jxl.write.Label(4,i,a.getDeptName(),fs);
				jxl.write.Label label_4 = new jxl.write.Label(5,i,a.getSickLeave()==0?"":Tools.getValuesNotZore(a.getSickLeave()),fc);
				jxl.write.Label label_5 = new jxl.write.Label(6,i,a.getLeaves()==0?"":Tools.getValuesNotZore(a.getLeaves()),fc);
				jxl.write.Label label_6 = new jxl.write.Label(7,i,a.getHomeLeave()==0?"":Tools.getValuesNotZore(a.getHomeLeave()),fc);
				jxl.write.Label label_7 = new jxl.write.Label(8,i,a.getMaternityLeave()==0?"":Tools.getValuesNotZore(a.getMaternityLeave()),fc);
				jxl.write.Label label_8 = new jxl.write.Label(9,i,a.getFamilyPlanningLeave()==0?"":Tools.getValuesNotZore(a.getFamilyPlanningLeave()),fc);
				jxl.write.Label label_9 = new jxl.write.Label(10,i,a.getAbsents()==0?"":Tools.getValuesNotZore(a.getAbsents()),fc);
				Double lateAndEarly = a.getLates()+a.getEarlys();
				jxl.write.Label label_10 = new jxl.write.Label(11,i,lateAndEarly==0?"":Tools.getValuesNotZore(lateAndEarly),fc);
				
				double leaveDeductStardard = Math.round(a.getSalary()/monthOfDays);
				double deductMonthBasicSalary = getMonBasicSalaryForAffirmed(a,leaveDeductStardard,lateOrEarlyDeductStardard,absentDeductStardard);
				jxl.write.Label label_11 = new jxl.write.Label(12,i,deductMonthBasicSalary==0?"":Tools.getValuesNotZore(deductMonthBasicSalary),fc);
				double deductAreaAllowanceCoefficient = getDeductAreaAllowanceCoefficient(a,c);
//				jxl.write.Label label_12 = new jxl.write.Label(12,i,deductAreaAllowanceCoefficient==0?"":deductAreaAllowanceCoefficient,fc);
				jxl.write.Label label_12 = new jxl.write.Label(13,i,deductAreaAllowanceCoefficient==0?"":Tools.getValuesNotZore(deductAreaAllowanceCoefficient),fc);
				double leaveDays = a.getLeaves()+a.getSickLeave()+a.getHomeLeave()+a.getMaternityLeave()+a.getFamilyPlanningLeave();
//				jxl.write.Label label_13 = new jxl.write.Label(13,i,"休假"+a.getHolidays()+"天",fc);//月休假情况
				
				
				String content = "";
				if(leaveDays>=15){
					content = "休假超过半个月";
				}
				else if(leaveDays>=monthOfDays){
					content = "休假满一个月";
				}
				jxl.write.Label label_14 = new jxl.write.Label(14,i,content,fs);//备注
//				
				
				sheet.addCell(label_0);sheet.addCell(label_1);sheet.addCell(label_2);sheet.addCell(label_3);
				sheet.addCell(label_4);
				sheet.addCell(label_5);sheet.addCell(label_6);sheet.addCell(label_7);
				sheet.addCell(label_8);sheet.addCell(label_9);sheet.addCell(label_10);sheet.addCell(label_11);
				sheet.addCell(label_12);
//				sheet.addCell(label_13);
				sheet.addCell(label_14);
				sheet.addCell(label_15);
				
				i++;
			}
			book.write();   
	        book.close();
		}catch(Exception e){
			
		}
		return tmpFile;
	}
	
	public File exportExcel(String fileName,List<AttShifts> list,int year,int month){
		String temporaryFile = System.getProperty("java.io.tmpdir");
		File tmpFile = new File(temporaryFile+fileName);
		WritableWorkbook book;
		double lateOrEarlyDeductStardard = Long.parseLong(configService.get(ConfigKey.LATEOREARLY_DEDUCT_BASIC_SALARY_STANDARD));
		double absentDeductStardard = Long.parseLong(configService.get(ConfigKey.ABSENT_DEDUCT_BASIC_SALARY_STANDARD));
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);c.set(Calendar.MONTH, month-1);
		double monthOfDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		try {
			book = Workbook.createWorkbook(tmpFile);
			WritableSheet sheet = book.createSheet("Sheet_1", 0);
			
			//设置样式
			WritableFont fontTitle1 = new WritableFont(WritableFont.createFont("标题一"), 16, WritableFont.BOLD);
			WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle1);
			formatTitle1.setAlignment(Alignment.CENTRE);
			formatTitle1.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffb = new WritableFont(WritableFont.createFont("宋体"),11, WritableFont.BOLD);
			WritableCellFormat fb = new WritableCellFormat(ffb);
			
//			Colour co = Colour.GRAY_25;
//			fb.setBackground(co);
			
			fb.setAlignment(Alignment.CENTRE);
			fb.setVerticalAlignment(VerticalAlignment.CENTRE);
			fb.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont f = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fc = new WritableCellFormat(f);
			fc.setAlignment(Alignment.CENTRE);
			fc.setVerticalAlignment(VerticalAlignment.CENTRE);
			fc.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffs = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fs = new WritableCellFormat(ffs);
//			fs.setAlignment(Alignment.CENTRE);
			fs.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			
			sheet.mergeCells(0, 0,14, 0);
			sheet.addCell(new Label(0,0,year+"年"+month+"月考勤情况汇总表",formatTitle1));
//			sheet.mergeCells(0, 1,14, 0);
			Label label01_ = new Label(0, 1, "日期：",fc);
			sheet.addCell(label01_);
//			sheet.mergeCells(1, 1,4, 0);
			Label label11_ = new Label(1, 1,Tools.date2Date(new Date()),fc); 
			sheet.addCell(label11_);
			Label label0 = new Label(0, 2, "序号",fb); 
			Label label1 = new Label(1, 2, "员工编号",fb); 
			Label label2 = new Label(2, 2, "姓名",fb);
			Label label15 = new Label(3, 2, "一级机构",fb);
			Label label3 = new Label(4, 2,  "所在部门",fb);
			Label label4 = new Label(5, 2, "病假天数",fb);
			Label label5 = new Label(6, 2, "事假天数",fb);
			Label label6 = new Label(7, 2, "探亲假天数",fb);
			Label label7 = new Label(8, 2, "产假天数",fb);
			Label label8 = new Label(9, 2, "计划生育假天数",fb);
			Label label9 = new Label(10, 2, "旷工天数",fb);
			Label label10 = new Label(11, 2, "迟到早退次数",fb);
			Label label11 = new Label(12, 2, "扣月基本工资",fb);
			Label label12 = new Label(13, 2, "扣区域津贴",fb);
//			Label label13 = new Label(13, 2, month+"月休假情况",fb);
			Label label14 = new Label(14, 2, "备注",fb);
			
			sheet.addCell(label0);sheet.addCell(label1);sheet.addCell(label2);sheet.addCell(label3);
			sheet.addCell(label4);sheet.addCell(label5);sheet.addCell(label6);sheet.addCell(label7);
			sheet.addCell(label8);sheet.addCell(label9);sheet.addCell(label10);sheet.addCell(label11);
			sheet.addCell(label12);
//			sheet.addCell(label13);
			sheet.addCell(label14);
			sheet.addCell(label15);
			
			int i = 3;
			for(AttShifts a : list){
				if(exportOrNot1(a)){
					continue;
				}
				jxl.write.Number label_0 = new jxl.write.Number(0, i, i-2,fc);
				jxl.write.Label label_1 = new jxl.write.Label(1,i,a.getEmployeeCode(),fs);
				jxl.write.Label label_2 = new jxl.write.Label(2,i,a.getEmployeeName(),fs);
				
				String firstDeptName = organizationService.getFirstOrganization(a.getDeptName());
				jxl.write.Label label_15 = new jxl.write.Label(3,i,firstDeptName,fc);
				
				jxl.write.Label label_3 = new jxl.write.Label(4,i,a.getDeptName(),fs);
				jxl.write.Label label_4 = new jxl.write.Label(5,i,a.getSickLeave()==0?"":Tools.getValuesNotZore(a.getSickLeave()),fc);
				jxl.write.Label label_5 = new jxl.write.Label(6,i,a.getLeave()==0?"":Tools.getValuesNotZore(a.getLeave()),fc);
				jxl.write.Label label_6 = new jxl.write.Label(7,i,a.getHomeLeave()==0?"":Tools.getValuesNotZore(a.getHomeLeave()),fc);
				jxl.write.Label label_7 = new jxl.write.Label(8,i,a.getMaternityLeave()==0?"":Tools.getValuesNotZore(a.getMaternityLeave()),fc);
				jxl.write.Label label_8 = new jxl.write.Label(9,i,a.getFamilyPlanningLeave()==0?"":Tools.getValuesNotZore(a.getFamilyPlanningLeave()),fc);
				jxl.write.Label label_9 = new jxl.write.Label(10,i,a.getAbsents()==0?"":Tools.getValuesNotZore(a.getAbsents()),fc);
				Double lateAndEarly = a.getLates()+a.getEarlys();
				jxl.write.Label label_10 = new jxl.write.Label(11,i,lateAndEarly==0?"":Tools.getValuesNotZore(lateAndEarly),fc);
				
				double leaveDeductStardard = Math.round(a.getSalary()/monthOfDays);
				double deductMonthBasicSalary = getMonBasicSalary(a,leaveDeductStardard,lateOrEarlyDeductStardard,absentDeductStardard);
				jxl.write.Label label_11 = new jxl.write.Label(12,i,deductMonthBasicSalary==0?"":Tools.getValuesNotZore(deductMonthBasicSalary),fc);
				double deductAreaAllowanceCoefficient = getDeductAreaAllowanceCoefficient(a,c);
				jxl.write.Label label_12 = new jxl.write.Label(13,i,deductAreaAllowanceCoefficient==0?"":Tools.getValuesNotZore(deductAreaAllowanceCoefficient),fc);
				double leaveDays = a.getLeave()+a.getSickLeave()+a.getHomeLeave()+a.getMaternityLeave()+a.getFamilyPlanningLeave();
//				jxl.write.Label label_13 = new jxl.write.Label(13,i,"当月休假"+a.getHolidays()+"天",fc);//月休假情况
				
				String content = "";
				if(leaveDays>=15){
					content = "休假超过半个月";
				}
				else if(leaveDays>=monthOfDays){
					content = "休假满一个月";
				}
				jxl.write.Label label_14 = new jxl.write.Label(14,i,content,fb);//备注
				

				sheet.addCell(label_0);sheet.addCell(label_1);sheet.addCell(label_2);sheet.addCell(label_3);
				sheet.addCell(label_4);
				sheet.addCell(label_5);sheet.addCell(label_6);sheet.addCell(label_7);
				sheet.addCell(label_8);sheet.addCell(label_9);sheet.addCell(label_10);sheet.addCell(label_11);
				sheet.addCell(label_12);
//				sheet.addCell(label_13);
				sheet.addCell(label_14);
				sheet.addCell(label_15);
				i++;
			}
			book.write();   
	        book.close();
		} catch (Exception e) {
		}
		
		return tmpFile;
	}
//	private String getStartStop(List<MonthStatistics> list,String code){
//		String startStop = "";
//		for(MonthStatistics ml : list){
//			if(ml.getEmployee().getCode().equals(code)){
//				startStop = ml.getDateContents();
//				break;
//			}
//		}
//		return startStop;
//	}
//	private MonthStatistics getYearStatistics(List<MonthStatistics> list,String code){
//		MonthStatistics yearStatistics = null;
//		for(MonthStatistics ml : list){
//			if(ml.getEmployee().getCode().equals(code)){
//				yearStatistics = ml;
//				break;
//			}
//		}
//		return yearStatistics;
//	}
	/**
	 * 计算该员工当月扣月基本工资
	 * @param a
	 * @return
	 */
	private double getMonBasicSalary(AttShifts a,double leave,double lateOrEarly,double absent){
		double deductMonBasicSalary = a.getLeave()*leave+lateOrEarly*(1+a.getLates()+a.getEarlys())*(a.getLates()+a.getEarlys())/2+absent*a.getAbsents();
		return deductMonBasicSalary==0?0:-deductMonBasicSalary;
	}
	
	private double getMonBasicSalaryForAffirmed(MonthStatistics a,double leave,double lateOrEarly,double absent){
		double deductMonBasicSalary = a.getLeaves()*leave+lateOrEarly*(1+a.getLates()+a.getEarlys())*(a.getLates()+a.getEarlys())/2+absent*a.getAbsents();
		return deductMonBasicSalary==0?0:-deductMonBasicSalary;
	}
	private double getDeductAreaAllowanceCoefficient(AttShifts a,Calendar c){
//		double monthOfDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		double monthOfDays = 21.75;
		double deductAreaAllowanceCoefficient = (a.getSickLeave()+a.getLeave()+a.getAbsents())/monthOfDays;
		deductAreaAllowanceCoefficient = deductAreaAllowanceCoefficient>1?1:deductAreaAllowanceCoefficient;
		double minusRegionalAllowance = a.getRegionalAllowance()*deductAreaAllowanceCoefficient;
		return minusRegionalAllowance;
	}
	
	private double getDeductAreaAllowanceCoefficient(MonthStatistics a,Calendar c){
//		double monthOfDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		double monthOfDays = 21.75;
		double deductAreaAllowanceCoefficient = (a.getSickLeave()+a.getLeaves()+a.getAbsents())/monthOfDays;
		deductAreaAllowanceCoefficient = deductAreaAllowanceCoefficient>1?1:deductAreaAllowanceCoefficient;
		double minusRegionalAllowance = a.getRegionalAllowance()*deductAreaAllowanceCoefficient;
		return minusRegionalAllowance;
	}
	
	/**
	 * 批量导入考勤信息
	 * @param importExcel
	 * @param employee
	 * @throws Exception
	 */
	public void importAttshift(File excelFile,Employee employee)throws Exception{
		try{
			InputStream is = new FileInputStream(excelFile);
			jxl.Workbook wb = Workbook.getWorkbook(is);
			//获取第一张Sheet表
			Sheet rs = wb.getSheet(0);
			int rows = rs.getRows();
			Map<String, String> codes = new HashMap<String, String>();
			Employee e = null;
			
			DateFormat df = new SimpleDateFormat("HH:mm:ss");
			String code,name,reason,currentDateStr,checkInTimeStr,checkOutTimeStr;
			Date currentDate,checkInTime,checkOutTime;
			
			
			for(int i = 2;i<rows;i++){
				AttShift as;
				AttendanceLog al = new AttendanceLog();
				
				String content = rs.getCell(0, i).getContents().trim();
				if(codes.containsKey(content)){
					e = AdminHelper.toEmployee(codes.get(content));
				}else{
					e = employeeService.getEmployeeEmpty(content);
					codes.put(content, Long.toString(e.getId()));
				}

				code = rs.getCell(0, i).getContents().trim();
				name = rs.getCell(1, i).getContents().trim();
				currentDateStr = rs.getCell(2, i).getContents().trim();
				if(null==currentDateStr||"".equals(currentDateStr)||currentDateStr.indexOf("-")<0){
					throw new DictTypeException("excel表中第"+(i+1)+"行员工"+"日期不存在或者或者日期格式不正确，格式：2012-05-06");
				}
				currentDate = Tools.stringToDate(currentDateStr);
				checkInTimeStr = rs.getCell(6, i).getContents().trim();
				checkOutTimeStr = rs.getCell(7, i).getContents().trim();
				if(null==checkInTimeStr||"".equals(checkInTimeStr)||checkInTimeStr.split(":").length!=3||checkInTimeStr.indexOf(":")<0){
					throw new DictTypeException("excel表中第"+(i+1)+"行员工"+"签入时间不存在或者或者时间格式不正确，格式：08:30:06");
				}
				checkInTime = df.parse(checkInTimeStr);
				if(null==checkOutTimeStr||"".equals(checkOutTimeStr)||checkOutTimeStr.split(":").length!=3||checkOutTimeStr.indexOf(":")<0){
					throw new DictTypeException("excel表中第"+(i+1)+"行员工"+"签入时间不存在或者或者时间格式不正确，格式：08:30:06");
				}
				checkOutTime = df.parse(checkOutTimeStr);
				reason = rs.getCell(9, i).getContents().trim();
				
				as = attShiftService.getAttShiftByDateAndId(Long.toString(e
						.getId()), currentDate);
				
				if(null!=as){
					if(null==as.getStartTime()||"".equals(as.getStartTime())){
						throw new DictTypeException("excel表中第"+(i+1)+"行员工"+"当天没有排版，不能导入");
					}
					if(null==as.getEndTime()||"".equals(as.getEndTime())){
						throw new DictTypeException("excel表中第"+(i+1)+"行员工"+"当天没有排版，不能导入");
					}
					if(checkInTime.compareTo(as.getCheckInTime())==0&&checkOutTime.compareTo(as.getCheckOutTime())==0){
						continue;
					}else if(checkInTime.compareTo(as.getCheckInTime())!=0&&checkOutTime.compareTo(as.getCheckOutTime())!=0){ 
						al.setAttDate(currentDate);
						al.setEmployee(e);
						al.setEmployeeCode(e.getCode());
						al.setPlusCardMan(employee);
						if(null==reason||"".equals(reason)){
							throw new DictTypeException("excel表中第"+(i+1)+"行员工"+"的考勤补赠原因不能为空");
						}else{
							al.setReason(reason);
						}
						al.setCheckInTime(checkInTime);
						attendanceLogService.add(al);
						
						AttendanceLog alog = new AttendanceLog();
						alog.setAttDate(currentDate);
						alog.setEmployee(e);
						alog.setEmployeeCode(e.getCode());
						alog.setPlusCardMan(employee);
						if(null==reason||"".equals(reason)){
							throw new DictTypeException("excel表中第"+(i+1)+"行员工"+"的考勤补赠原因不能为空");
						}else{
							alog.setReason(reason);
						}
						alog.setCheckInTime(checkOutTime);
						attendanceLogService.add(alog);
						attShiftService.delDayAndEmployee(Long.toString(e.getId()),currentDate);
						attShiftService.mendCard(e.getId()+"", currentDate);
					}else if(checkInTime.compareTo(as.getCheckInTime())!=0){
						al.setAttDate(currentDate);
						al.setEmployee(e);
						al.setEmployeeCode(e.getCode());
						al.setPlusCardMan(employee);
						if(null==reason||"".equals(reason)){
							throw new DictTypeException("excel表中第"+(i+1)+"行员工"+"的考勤补赠原因不能为空");
						}else{
							al.setReason(reason);
						}
						al.setCheckInTime(checkInTime);
						attendanceLogService.add(al);
						attShiftService.delDayAndEmployee(Long.toString(e.getId()),currentDate);
						attShiftService.mendCard(e.getId()+"", currentDate);
					}else if(checkOutTime.compareTo(as.getCheckOutTime())!=0){
						al.setAttDate(currentDate);
						al.setEmployee(e);
						al.setEmployeeCode(e.getCode());
						al.setPlusCardMan(employee);
						if(null==reason||"".equals(reason)){
							throw new DictTypeException("excel表中第"+(i+1)+"行员工"+"的考勤补赠原因不能为空");
						}else{
							al.setReason(reason);
						}
						al.setCheckInTime(checkOutTime);
						attendanceLogService.add(al);
						attShiftService.delDayAndEmployee(Long.toString(e.getId()),currentDate);
						attShiftService.mendCard(e.getId()+"", currentDate);
					}
				}else{
					throw new DictTypeException("excel表中第"+(i+1)+"行员工"+"当天没有排版，不能导入");
				}
			}
			is.close();
			wb.close();
		}catch (DictTypeException e) {
			// TODO: handle exception
//			e.printStackTrace();
			throw e;
		}
	}
	
	public List<User> excelToUser(File importExcel) throws Exception{
		List<User> list = new ArrayList<User>();
		try{
			InputStream is = new FileInputStream(importExcel);
			jxl.Workbook wb = Workbook.getWorkbook(is);
			//获取第一张Sheet表
			Sheet rs = wb.getSheet(0);
			int rows = rs.getRows();
			for(int i=1;i<rows;i++){
				String code = rs.getCell(0, i).getContents().trim();
				Employee e = employeeService.getEmployee(code);
				if(e.getUser()==null){
					User user = new User();
					List<Role> roles = new ArrayList<Role>();
					roles.add(AdminHelper.toRole("6"));
					
					String content = rs.getCell(9, i).getContents();
					user.setName(content);
					user.setPassword(Tools.encodePassword("000000"));
					user.setDisplayName(e.getName());
					user.setEmployee(e);
					user.setRoles(roles);
					list.add(user);
				}
			}
			is.close();
			wb.close();
			return list;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 导入员工信息excel表
	 * @param importExcel
	 * @return
	 * @throws Exception
	 */
	public List<Employee> excelToEmployee(File importExcel) throws Exception{
		try{
			//构建Workbook对象, 只读Workbook对象
			///dictService.getDefDao().getHibernateTemplate().setFlushMode(HibernateTemplate.FLUSH_EAGER);
			InputStream is = new FileInputStream(importExcel);
			jxl.Workbook wb = Workbook.getWorkbook(is);
			//获取第一张Sheet表
			Sheet rs = wb.getSheet(0);
			int rows = rs.getRows();
			int columns = rs.getColumns();
			List<Dict> sexs = dictService.getDictsByType("sex");
			
			List<Organization> organizations = organizationService.getListData().getList();
//			List<Area> areas = areaService.getListData().getList();
			List<Employee> es = new ArrayList<Employee>();
			Employee e = null;
			Map<String, Integer> maps = new HashMap<String, Integer>();
			for(int i=1;i<rows;i++){
				List<Dict> positions = dictService.getDictsByType("position");
				List<Dict> peoples = dictService.getDictsByType("peopleType");
				e = new Employee();
				for(int j= 0 ;j<columns;j++){
					String content = rs.getCell(j, i).getContents();
					if(j==0){
						if(null==content||"".equals(content)){
							throw new DictTypeException("Excel表格第"+(i+1)+"行的员工编号不能为空");
						}
						e.setCode(content);
					}
					else if(j==1){
						if(null==content||"".equals(content)){
							throw new DictTypeException("Excel表格第"+(i+1)+"行的员工姓名不能为空");
						}
						e.setName(content);
					}
					else if(j==2){
						if(null==content||"".equals(content))continue;
						String sex_id = null;
						for(Dict sex : sexs){
							if(content.equals(sex.getText())){
								sex_id = sex.getId();
								break;
							}
						}
						if(null==sex_id){
							throw new DictTypeException("Excel表格第"+(i+1)+"行的性别:"+content+"，在系统中不存在，请先添加或修改后再导入");
						}
						e.setSex(AdminHelper.toDict(sex_id));
					}
					else if(j==3){
						if(null!=content||!"".equals(content)){
							try {
								
								e.setBirthday(Tools.stringToDate(content));
							} catch (Exception e2) {
								throw new DictTypeException("Excel表格第"+(i+1)+"行的日期格式（yyyy-mm-dd）:"+content+"错误，请查正后再导入");
							}
						}
					}
					else if(j==4){
						if(null==content||"".equals(content))continue;
						String organization_id = null;
						for(Organization o : organizations){
							if(content.equals(o.getName())){
								organization_id =""+ o.getId();
							}
						}
						if(null!=organization_id){
							e.setOrganization(AdminHelper.toOrganization(organization_id));
						}else{
							throw new DictTypeException("Excel表格第"+(i+1)+"行组织机构名称:"+content+"，在系统中不存在，请先添加或修改后再导入");
						}
					}
					else if(j==5){
						if(null==content||"".equals(content))continue;
						String position_id= null;
						for(Dict p : positions){
							if(content.equals(p.getText())){
								position_id = p.getId();
							}
						}
						if(null!=position_id){
							e.setPosition(AdminHelper.toDict(position_id));
						}else{
							Dict position = new Dict(Tools.uuid(),content);
							position.setParent(dictService.get("position"));
							Dict p = dictService.add(position);
//							dictService.refresh(p);
							e.setPosition(p);
//							throw new DictTypeException("Excel表格第"+(i+1)+"行岗位名称代码:"+content+"，在系统中不存在，请先添加或修改后再导入");
						}
					}
					else if(j==6){
						if(null==content||"".equals(content))continue;
						String people_id= null;
						for(Dict p : peoples){
							if(content.equals(p.getText())){
								people_id = p.getId();
							}
						}
						if(null!=people_id){
							e.setPeopleType(AdminHelper.toDict(people_id));
						}else{
							Dict peopleType = new Dict(Tools.uuid(),content);
							peopleType.setParent(dictService.get("peopleType"));
							Dict p = dictService.add(peopleType);
//							dictService.refresh(p);
							e.setPeopleType(p);
//							throw new DictTypeException("Excel表格第"+(i+1)+"行人员类型名称:"+content+",在系统中不存在，请先添加或修改后再导入");
						}
					}
					else if(j==7){
						if(null!=content&&!"".equals(content)){
							try {
								e.setJoinWorkDate(Tools.stringToDate(content
										+ "-01"));
							} catch (Exception e2) {
								throw new DictTypeException("Excel表格第"+(i+1)+"行的日期格式错误（yyyy-mm）:"+content+"错误，请查正后再导入");
							}
						}
					}
					else if(j==8){
						if(null!=content&&!"".equals(content)){
							try {
								e.setHireDate(Tools.stringToDate(content
										+ "-01"));
							} catch (Exception e2) {
								throw new DictTypeException("Excel表格第"+(i+1)+"行的日期格式错误（yyyy-mm）:"+content+"错误，请查正后再导入");
							}
						}
					}
					else if(j==9){
						if(null==content||"".equals(content)){
							throw new DictTypeException("Excel表格第"+(i+1)+"行的员工卡号不能为空");
						}
						if(content.length()>9){
							throw new DictTypeException("Excel表格第"+(i+1)+"行的员工卡号格式不正确");
						}
						e.setCard(content);
						Integer count = maps.get(content);//首先获取是否有此卡号从在
						maps.put(content, count==null?1:count+1);
						if(maps.get(content)>1){
							throw new DictTypeException("Excel表格第"+(i+1)+"行的员工卡号重复");
						}
					}
				}
				es.add(e);
			}
			is.close();
			wb.close();
			return es;
		}
		catch (DictTypeException e){
			throw e;
		}
	}
	
	public List<Employee> importUserAndCard(File userFile,int x){
		InputStream is;
		try {
			is = new FileInputStream(userFile);
			jxl.Workbook wb = Workbook.getWorkbook(is);
			//获取第一张Sheet表
			Sheet rs = wb.getSheet(0);
			int rows = rs.getRows();
			int columns = rs.getColumns();
			if(x==1){
				for(int i = 1;i<rows;i++){
					String code = rs.getCell(0, i).getContents().trim();
					if(code!=null&&!code.equals("")){
						Employee e = employeeService.getEmployeeEmpty(code);
						User user = new User();
						List<Role> roles = new ArrayList<Role>();
						roles.add(AdminHelper.toRole("6"));
						if(e!=null){
							for(int j= 0 ;j<columns;j++){
								String content = rs.getCell(10, i).getContents();
								user.setName(content);
//								if(j==7&&content!=null&&!content.equals("")){
//									roles.add(AdminHelper.toRole("3"));
//									List<Organization> orgs = new ArrayList<Organization>();
//									orgs.add(e.getOrganization());user.setOrganizations(orgs);
//								}
							}
							user.setPassword(Tools.encodePassword("000000"));
							user.setDisplayName(e.getName());user.setEmployee(e);user.setRoles(roles);
							
							userService.add(user);
						}
					}
				}
			}
			is.close();
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args){
//		exportEmployee();
		File file = new File("d:/areaorg1.xls");
		try {
			Map<String,List<Organization>> hash = new ExcelHelperService().areaOrganizationRel(file);
			Set<String> keys = hash.keySet();
			for(String key:keys){
				List<Organization> orgs = hash.get(key);
				System.out.println(key+":"+orgs.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/*导出全行员工信息
	 * 
	 * @param fileName
	 * @return
	 */
	public File exportEmployee(String fileName){
		String temporaryFile = System.getProperty("java.io.tmpdir");
		File file = new File(temporaryFile+fileName);
		WritableWorkbook book;
		try {
			book = Workbook.createWorkbook(file);
			WritableSheet sheet = book.createSheet("Sheet_1", 0);
			Label label0 = new Label(0, 0, "员工编号代码"); 
			Label label1 = new Label(1, 0, "姓名代码");
			Label label2 = new Label(2, 0, "性别代码");
			Label label3 = new Label(3, 0, "出生日期代码");
			Label label4 = new Label(4, 0, "所在部门代码");
			Label label5 = new Label(5, 0, "岗位名称代码");
			Label label6 = new Label(6, 0, "员工类别代码");
			Label label7 = new Label(7, 0, "参加工作时间代码");
			Label label8 = new Label(8, 0, "来建行时间代码");
			Label label9 = new Label(9, 0, "卡号代码");
//			Label label10 = new Label(10, 0, "考勤区域代码");
	
			sheet.addCell(label0);sheet.addCell(label1);sheet.addCell(label2);sheet.addCell(label3);
			sheet.addCell(label4);sheet.addCell(label5);sheet.addCell(label6);sheet.addCell(label7);
			sheet.addCell(label8);sheet.addCell(label9);
//			sheet.addCell(label10);
			List<Employee> list = employeeService.getNotTurnAwayAndIsAttendance();
			int i = 1;
			for(Employee e: list){
				Organization organization = e.getOrganization();
				Dict postion = e.getPosition();
				Dict peopleType = e.getPeopleType();
				Dict sex = e.getSex();
				jxl.write.Label label_0 = new jxl.write.Label(0, i, e.getCode());
				jxl.write.Label label_1 = new jxl.write.Label(1,i,e.getName());
				jxl.write.Label label_2 = new jxl.write.Label(2,i,sex==null?"":sex.getText());
				jxl.write.Label label_3 = new jxl.write.Label(3,i,e.getBirthday()==null||"".equals(e.getBirthday().toString())?"":Tools.dateToString(e.getBirthday()));
				jxl.write.Label label_4 = new jxl.write.Label(4,i,organization==null?"":organization.getName());
				jxl.write.Label label_5 = new jxl.write.Label(5,i,postion==null?"":postion.getText());
				jxl.write.Label label_6 = new jxl.write.Label(6,i,peopleType.getText());
				jxl.write.Label label_7 = new jxl.write.Label(7,i,e.getJoinWorkDate()!=null?Tools.dateToString(e.getJoinWorkDate()):"");
				jxl.write.Label label_8 = new jxl.write.Label(8,i,e.getHireDate()!=null?Tools.dateToString(e.getHireDate()):"");
				jxl.write.Label label_9 = new jxl.write.Label(9,i,e.getCard());
//				jxl.write.Label label_10 = new jxl.write.Label(10,i,"公司总部");
				sheet.addCell(label_0);sheet.addCell(label_1);sheet.addCell(label_2);sheet.addCell(label_3);
				sheet.addCell(label_4);sheet.addCell(label_5);sheet.addCell(label_6);sheet.addCell(label_7);
				sheet.addCell(label_8);sheet.addCell(label_9);
				i++;
//				sheet.addCell(label_10);
			}
			book.write();   
	        book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * 导出建行门襟系统中全部员工的卡号
	 * @param fileName
	 * @param list
	 * @return
	 */
	public File ExportNewClosureEmployees(String fileName,List<EmployeeClosure> list){
		String temporaryFile = System.getProperty("java.io.tmpdir");
		File file = new File(temporaryFile+fileName);
		WritableWorkbook book;
		try {
			book = Workbook.createWorkbook(file);
			WritableSheet sheet = book.createSheet("Sheet_1", 0);
			Label label0 = new Label(0, 0, "员工工号"); 
			Label label1 = new Label(1, 0, "员工姓名");
			Label label2 = new Label(2, 0, "员工卡号");
	
			sheet.addCell(label0);sheet.addCell(label1);sheet.addCell(label2);
			int i=1;
			for(EmployeeClosure ec: list){
				jxl.write.Label label_0 = new jxl.write.Label(0, i, ec.getCode());
				jxl.write.Label label_1 = new jxl.write.Label(1,i,ec.getName());
				jxl.write.Label label_2 = new jxl.write.Label(2,i,CardHelper.cardNo2CardNo(ec.getCard()));
				sheet.addCell(label_0);sheet.addCell(label_1);sheet.addCell(label_2);
				i++;
			}
			book.write();   
	        book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public File generateReport(String fileName,String columns,List<AttShifts> list) throws Exception{
		String temporaryFile = System.getProperty("java.io.tmpdir");
		File file = new File(temporaryFile+fileName);
		String[] cols = columns.split(",");
		WritableWorkbook book = Workbook.createWorkbook(file);
		
		
		WritableSheet sheet = book.createSheet("Sheet_1", 0);
		WritableFont fsb = new WritableFont(WritableFont.createFont("宋体"),11, WritableFont.BOLD);
		WritableCellFormat fsbc = new WritableCellFormat(fsb);
		fsbc.setAlignment(Alignment.CENTRE);
		fsbc.setVerticalAlignment(VerticalAlignment.CENTRE);
		fsbc.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
//		fsbc.setBorder(Border.LEFT, BorderLineStyle.THIN);
//		fsbc.setBorder(Border.RIGHT, BorderLineStyle.THIN);
//		fsbc.setBorder(Border.TOP, BorderLineStyle.THIN);
//		fsbc.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
		
		WritableFont fs = new WritableFont(WritableFont.createFont("宋体"),11);
		WritableCellFormat fsc = new WritableCellFormat(fs);
		fsc.setAlignment(Alignment.CENTRE);
		fsc.setVerticalAlignment(VerticalAlignment.CENTRE);
		fsc.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
//		fsbc.setBorder(Border.LEFT, BorderLineStyle.THIN);
//		fsbc.setBorder(Border.TOP, BorderLineStyle.THIN);
//		fsbc.setBorder(Border.RIGHT, BorderLineStyle.THIN);
		
		Label label0 = new Label(0, 0, "序号",fsbc); 
		sheet.addCell(label0);
		int head = 1;
		for(String col : cols){
			Label label = new Label(head, 0, AttShiftColumns.valueOf(col).getLabel(),fsbc);
			head++;
			sheet.addCell(label);
		}
		List<String> getters = ExcelHelperService.field2GetMethodName(cols);
		int row = 1;
		for(AttShifts attShifts : list){
			if(exportOrNot1(attShifts)){
				continue;
			}
			sheet.addCell(new Label(0, row,row+"",fsc));
			int col = 1;
			for(String getter : getters){
				if(getter.equals("getInstitution")){
					Object obj = ExcelHelperService.getValue(getter, attShifts);
//					Label labelca = new Label(col, 0,"一级机构",fsbc);
					Label label = new Label(col, row,obj.toString(),fsc);
//					Label labs = new Label(col+1,0,obj.toString(),fsc);
//					Label labet = new Label(col+1,row,obj.toString(),fsc);
					col++;
//					sheet.addCell(labelca);
					sheet.addCell(label);
				}
				else if(getter.equals("getEmployeeCode")||getter.equals("getEmployeeName")||getter.equals("getDeptName")){
					Object obj = ExcelHelperService.getValue(getter, attShifts);
					Label label = new Label(col, row,obj.toString(),fsc);
					col++;
					sheet.addCell(label);
				}else{
					Object obj = ExcelHelperService.getValue(getter, attShifts);
					Label label = new Label(col, row,obj.toString().equals("0.0")?"":Tools.getValuesNotZore(Double.valueOf(obj.toString())),fsc);
					col++;
					sheet.addCell(label);
				}
			}
			row++;
		}
		book.write();   
        book.close();
        return file;
	}
	
	public Map<String,List<Organization>> areaOrganizationRel(File excel) throws Exception {
		InputStream is = new FileInputStream(excel);
		jxl.Workbook wb = Workbook.getWorkbook(is);
		Sheet rs = wb.getSheet(0);	
		int rows = rs.getRows();
		List<Organization> organizations = organizationService.getAll();
		Map<String,List<Organization>> hash = new HashMap<String,List<Organization>>();
		List<Organization> orgs = null;
		for(int row = 1;row<rows;row++){
			String key = rs.getCell(0, row).getContents();
			orgs = hash.get(key);
			if(orgs==null){
				orgs = new ArrayList<Organization>();
			}
			for(Organization org:organizations){
				if(org.getShortName().equals(rs.getCell(1, row).getContents())){
					orgs.add(org);
					break;
				}
			}
			hash.put(key, orgs);
		}
		is.close();
		wb.close();
		return hash;
	}
	
	//添加导入组织机构关系表的逻辑处理方法
	public List<Organization> excelToOrganization(File excel) throws Exception{
		InputStream in=new FileInputStream(excel);
		jxl.Workbook wb=Workbook.getWorkbook(in);
		Sheet rr=wb.getSheet(0);
		int rows=rr.getRows();
		List<Organization> orgsList=new ArrayList<Organization>();
		Organization root = organizationService.get("1");
		Organization orgs=new Organization();
		String con = "";
		Dict OrgW = new Dict("organizationType_01");//网点
		Dict OrgQ = new Dict("organizationType_02");//区域
		Dict OrgB = new Dict("organizationType_03");//部门
		Dict orgD = new Dict("organizationType_04");//单位
		for(int i = 1;i<rows;i++){
			String con1=rr.getCell(0,i).getContents().trim();
			String con2 = rr.getCell(1, i).getContents().trim();
			String con3 = rr.getCell(3, i).getContents().trim();
			Dict dict = null;
			if(con3.equals("部门")){
				dict = OrgB;
			}
			if(con3.equals("网点")){
				dict = OrgW;
			}
			if(con3.equals("支行")){
				dict = OrgQ;
			}
			if(con=="" || !con.equals(con1)){
				con = con1;
				orgs=new Organization(root,con1,con1,dict);
				orgsList.add(orgs);
			}else{
				if(!con1.equals(con2)){
					Organization o = new Organization(orgs,con2,con2,dict);
					orgsList.add(o);
				}
			}
		}
		in.close();
		wb.close();
		return orgsList;
	}
	
	public static Object getValue(String get,AttShifts attShifts) throws Exception{
		Class<?> c = AttShifts.class;
		Method mh = c.getMethod(get);
		Object obj = mh.invoke(attShifts);
		return obj;
	}
	public static List<String> field2GetMethodName(String[] fields){
		List<String> list = new ArrayList<String>();
		for(String field : fields){
			String tmp = "get"+field.substring(0,1).toUpperCase() + field.substring(1);
			list.add(tmp);
		}
		return list;
	}
	
	public void importSalaryToEmployee(File excelFile) throws Exception {
		InputStream is = new FileInputStream(excelFile);
		jxl.Workbook wb = Workbook.getWorkbook(is);
		Sheet rs = wb.getSheet(0);	
		int rows = rs.getRows();
		List<Employee> emps = employeeService.getAll();
		for(int row = 1;row<rows;row++){
			String code = rs.getCell(0, row).getContents();
			for(Employee emp: emps){
				if(emp.getCode().equals(code)){
					String salary = rs.getCell(2, row).getContents();
					emp.setSalary(Double.parseDouble(salary));
					String regionalAllowance = rs.getCell(3, row).getContents();
					if(!regionalAllowance.equals("")&&regionalAllowance!=null){	
						emp.setRegionalAllowance(Double.parseDouble(regionalAllowance));
					}else{
						emp.setRegionalAllowance(0);
					}
					employeeService.update(emp);
					break;
				}
			}
		}
		is.close();
		wb.close();
	}
	
	public void generateReportBranch(long organization, int days, String type,
			OutputStream os, String yearMonth){
		WritableWorkbook book;
		try {
			book = Workbook.createWorkbook(os);
			WritableSheet sheet = book.createSheet("Sheet_1", 0);
			
			//设置样式
			WritableFont fontTitle1 = new WritableFont(WritableFont.createFont("标题一"), 16, WritableFont.BOLD);
			WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle1);
			formatTitle1.setAlignment(Alignment.CENTRE);
			formatTitle1.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffb = new WritableFont(WritableFont.createFont("宋体"),11, WritableFont.BOLD);
			WritableCellFormat fb = new WritableCellFormat(ffb);
			fb.setAlignment(Alignment.CENTRE);
			fb.setVerticalAlignment(VerticalAlignment.CENTRE);
			fb.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
			
			WritableFont f = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fc = new WritableCellFormat(f);
			fc.setAlignment(Alignment.CENTRE);
			fc.setVerticalAlignment(VerticalAlignment.CENTRE);
			fc.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffs = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fs = new WritableCellFormat(ffs);
			List<Organization> organizations = null;
			if(organization==00){
				organizations=organizationService.getOrganizationByType("organizationType_01");
			}else{
				Organization org= organizationService.get(Long.toString(organization));
				organizations=new ArrayList<Organization>();
				if(org.getType().getId().equals("organizationType_02")){
					organizations.addAll(org.getChildren());
				}else{
					organizations.add(organizationService.get(organization+""));
				}
			}
			int row=0;
			for(Organization o : organizations){
				ListData<BranchShiftAnalysis> stats = (ListData<BranchShiftAnalysis>) branchShiftAnalysisService.getBranchShiftAnalysis(
					o.getId(),yearMonth,days,type,0,0);
				List<BranchShiftAnalysis> list=stats.getList();
				sheet.mergeCells(0, row,5, row);
				if(type.equals("OTHER_BRANCH_IN_TOTAL")){
					sheet.addCell(new Label(0,row,yearMonth+"月"+o.getName()+"外行顶班情况汇总表",formatTitle1)); row = row+1;
				}else if(type.equals("MORE_IN_WEEKEND")){
					sheet.addCell(new Label(0,row,yearMonth+"月"+o.getName()+"周末排班超过3天情况汇总表",formatTitle1)); row = row+1;
				}else if(type.equals("EQUAL_IN_WEEKEND")){
					sheet.addCell(new Label(0,row,yearMonth+"月"+o.getName()+"周末排班0天情况汇总表",formatTitle1)); row = row+1;
				}else if(type.equals("LESS_IN_TOTAL")){
					sheet.addCell(new Label(0,row,yearMonth+"月"+o.getName()+"总计排班小于3天情况汇总表",formatTitle1)); row = row+1;
				}else if(type.equals("REST_MORE_THAN")){
					sheet.addCell(new Label(0,row,yearMonth+"月"+o.getName()+"休息日超过8天情况汇总表",formatTitle1));row = row+1;
				}else{
					sheet.addCell(new Label(0,row,yearMonth+"月"+o.getName()+"排班情况汇总表",formatTitle1)); row = row+1;
				}
				
				Label label01_ = new Label(0, row, "日期：",fc);
				sheet.addCell(label01_);
				sheet.mergeCells(1, 1, 6, 0);
				Label label11_ = new Label(1, row,Tools.date2Date(new Date()),fc); row = row+1;
				sheet.addCell(label11_);
				
				Label label02 = new Label(0,row,"区域:",fc);
				sheet.addCell(label02);
				sheet.mergeCells(1, 2, 6, 0);
				Label label021 = new Label(1,row,o.getParent().getName(),fc); row = row +1;
				sheet.addCell(label021);
				Label label0 = new Label(0, row, "序号",fb); 
				Label label1 = new Label(1, row, "所属行",fb);
				Label label2 = new Label(2, row, "姓名",fb); 
				Label label3 = new Label(3, row, "岗位",fb); 
				if(type.equals("REST_MORE_THAN")){
					Label label4 = new Label(4, row, "休息日统计(日)",fb);
					Label label5 = new Label(5, row, "描述",fb);
					sheet.addCell(label4);sheet.addCell(label5);
				}
		////////////2012-10-22//////////////////////////////		
				else if(type.equals("OTHER_BRANCH_IN_TOTAL")){
					Label label4 = new Label(4, row, "派驻网点",fb);
					Label label5 = new Label(5, row, "派驻网点排班统计(小时)",fb);
					Label label6 = new Label(6, row, "描述",fb);
					sheet.addCell(label4);sheet.addCell(label5);
					sheet.addCell(label6);
				}
		//////////////////////////////////////////	
				else{
					Label label4 = new Label(4, row, "总计排班统计(日)",fb);
					Label label5 = new Label(5, row, "周末排班统计(日)",fb);
					Label label6 = new Label(6, row, "总计排班时间(小时)",fb);
					Label label7 = new Label(7, row,  "描述",fb);
					sheet.addCell(label4);sheet.addCell(label5);
					sheet.addCell(label6);sheet.addCell(label7);
				}
				
				
				sheet.addCell(label0);sheet.addCell(label1);sheet.addCell(label2);
				sheet.addCell(label3);
				row=row+1;
				int i = 3;
				for(BranchShiftAnalysis a : list){
					Employee e = employeeService.get(a.getEmployee().getId()+"");
					jxl.write.Number label_0 = new jxl.write.Number(0, row, i-2,fc);
					jxl.write.Label label_1 = new jxl.write.Label(1,row,a.getEmployee().getOrganization().getShortName(),fc);
					jxl.write.Label label_2 = new jxl.write.Label(2,row,a.getEmployee().getName(),fc);
					jxl.write.Label label_3 = new jxl.write.Label(3,row,e.getPosition().getText(),fc);
					if(type.equals("REST_MORE_THAN")){
						jxl.write.Number label_4 = new jxl.write.Number(4,row,a.getRestdays(),fc);
						jxl.write.Label label_5 = new jxl.write.Label(5,row,a.getNotesDesc(),fc);
						sheet.addCell(label_4);sheet.addCell(label_5);
					}
		/////////////////////2012-10-22/////////////////////////////			
					else if(type.equals("OTHER_BRANCH_IN_TOTAL")){
						jxl.write.Label label_4 = new jxl.write.Label(4,row,a.getOrganization().getShortName(),fc);
						jxl.write.Number label_5 = new jxl.write.Number(5,row,a.getBranchShiftTimeTotal(),fc);
						jxl.write.Label label_6 = new jxl.write.Label(6,row,a.getNotesDesc(),fc);
						sheet.addCell(label_4);sheet.addCell(label_5);
						sheet.addCell(label_6);
					}
 //////////////////////////////////////////////////								
					else{
						jxl.write.Number label_4 = new jxl.write.Number(4,row,a.getWorkdays(),fc);
						jxl.write.Number label_5 = new jxl.write.Number(5,row,a.getWeekdays(),fc);
						jxl.write.Number label_6 = new jxl.write.Number(6,row,a.getBranchShiftTimeTotal(),fc);
						jxl.write.Label label_7 = new jxl.write.Label(7,row,a.getNotesDesc(),fc);
						sheet.addCell(label_4);sheet.addCell(label_5);
						sheet.addCell(label_6);sheet.addCell(label_7);
					}
					sheet.addCell(label_0);sheet.addCell(label_1);sheet.addCell(label_2);
					sheet.addCell(label_3);				
					row++;i++;
				}
				row++;
			}
			book.write();   
	        book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 导入服务管理中心员工排班情况
	 * @param excelFile
	 * @throws Exception
	 */
	public List<ShiftConfig> importBrowseExcel(File excelFile) throws Exception {
		List<ShiftConfig> list = new ArrayList<ShiftConfig> ();
		try{
			InputStream is = new FileInputStream(excelFile);
			jxl.Workbook wb = Workbook.getWorkbook(is);
			Sheet rs = wb.getSheet(0);	
			int rows = rs.getRows();
			int columns = rs.getColumns();
			Organization org = organizationService.get("35");
//			User user = AdminHelper.getUser();
//			Organization org = user.getEmployee().getOrganization();
			List<Employee> emps = employeeService.getEmployeeByOrgId(org.getId());
			Map<String,String> codeAndName = new HashMap<String, String>();
			for(Employee emp:emps){
				codeAndName.put(emp.getCode(), emp.getName());
			}
			//分解年月日
//			String firstDate = rs.getCell(2,0).getContents();
//			String[] dd = firstDate.split("/");
//			String dateString = dd[2]+"-"+dd[1]+"-"+dd[0];
//			Date firstMonthOfDate = Tools.stringToDate(dateString);
//			
//			Calendar currentDay = Calendar.getInstance(); 
//			currentDay.setTime(firstMonthOfDate); 
//			
//			Calendar lastDayOfMonth = Calendar.getInstance();
//			lastDayOfMonth.setTime(firstMonthOfDate);
//			lastDayOfMonth.set(Calendar.DAY_OF_MONTH, 1); 
//			lastDayOfMonth.add(Calendar.MONTH, 1); 
//			lastDayOfMonth.add(Calendar.DATE, -1); 
//			Date lastMonthOfDate = cal.getTime();
			ShiftConfigTime sct=null;
			
			for(int row = 2;row<rows;row++){
				String code = rs.getCell(0, row).getContents().trim();
				String name = rs.getCell(1, row).getContents().trim();
				if("".equals(code)||null==code){
					throw new DictTypeException("第"+(row+1)+"行员工工号不能为空");
				}
				if("".equals(name)||null==name){
					throw new DictTypeException("第"+(row+1)+"行员工姓名不能为空");
				}
//				Employee e = employeeService.getEmployee(code);
				Employee e = employeeService.getFWGEmployeeByCode(code);
				if(null==e||"".equals(e)){
					throw new DictTypeException("第"+(row+1)+name+"现在不属于服务管理中心部门的员工，请清除后再导入。");
				} else {
					if (codeAndName.containsKey(code)
							&& codeAndName.get(code).equals(name)) {
						for (int column = 2; column < columns; column++) {
							ShiftConfig sc = new ShiftConfig();
							String shiftConfigBrowse = rs.getCell(column, row)
									.getContents();
							String curentDateStr = rs.getCell(column, 1)
									.getContents();
							String[] str1 = curentDateStr.split("/");
							String strDate = str1[2] + "-" + str1[1] + "-"
									+ str1[0];
							Date curentDate = Tools.stringToDate(strDate);
							if (shiftConfigBrowse == null
									|| "".equals(shiftConfigBrowse)) {
								sc.setCancel(true);
								sc.setDate(curentDate);
								sc.setEmployee(e);
								shiftConfigService.deleteConfig(curentDate, e);
								shiftConfigService.add(sc);
								continue;
							}

							sct = shiftConfigTimeService
									.findShiftConfigTimeByType(shiftConfigBrowse);
							if (sct == null || "".equals(sct)) {
								throw new DictTypeException(shiftConfigBrowse
										+ "班次在系统中不存在，请先在系统中添加后再导入");
							} else {
								sc.setEmployee(e);
								sc.setStartTime(sct.getStartWorkTime());
								sc.setEndTime(sct.getEndWorkTime());
								if (sct.getEndWorkTime().compareTo(
										sct.getStartWorkTime()) < 0) {
									sc.setNextDay(true);
								} else {
									sc.setNextDay(sct.isNextDay());
								}
								sc.setDate(curentDate);
								shiftConfigService.deleteConfig(curentDate, e);
								list.add(sc);
							}
						}
					} else {
						throw new DictTypeException("班次信息不存在或输入有误");
					}
				}
			}
			is.close();
			wb.close();
			return list;
		}catch (DictTypeException e) {
			// TODO: handle exception
			throw e;
//			e.printStackTrace();
		}
	}
	/**
	 * 导入班次信息时间表
	 * @param excelFile
	 * @return
	 * @throws Exception
	 */
	public List<ShiftConfigTime> excelToShiftConfigTime(File excelFile) throws Exception {
		List<ShiftConfigTime> list = new ArrayList<ShiftConfigTime> ();
		try{
			InputStream is = new FileInputStream(excelFile);
			jxl.Workbook wb = Workbook.getWorkbook(is);
			Sheet rs = wb.getSheet(0);	
			int rows = rs.getRows();
			int columns = rs.getColumns();
			for(int row = 2;row<rows;row++){
				ShiftConfigTime sct;
				String content = rs.getCell(1, row).getContents().trim();
				sct = shiftConfigTimeService.findShiftConfigTimeByType(content);
				if(null==sct||"".equals(sct)){
					sct = new ShiftConfigTime();
					for (int column = 0; column < columns; column++) {
						if (column == 0) {
							String treamName = rs.getCell(column, row)
									.getContents().trim();
							if ("".equals(treamName) || treamName == null) {
								throw new DictTypeException("技能组别名称不能为空");
							}
							sct.setTeamName(treamName);
						}
						if (column == 1) {
							String shiftConfigType = rs.getCell(column, row)
									.getContents().trim();
							if ("".equals(shiftConfigType)
									|| shiftConfigType == null) {
								throw new DictTypeException("班次名称不能为空");
							}
							sct.setShiftConfigType(shiftConfigType);
						}
						if (column == 2) {
							String workTimes = rs.getCell(column, row)
									.getContents().trim();
							String[] times = workTimes.split("-");
							if (times[0] == null || times[1] == null) {
								throw new DictTypeException(
										"上班时间段格式不正确，格式为：12:00-14:00");
							}
							Date startTime, endTime;
							try {
								SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
								startTime = df.parse(times[0] + ":00");
								if (times[1].contains("次日")) {
									endTime = df.parse(times[1].substring(2)
											+ ":00");
								} else {
									endTime = df.parse(times[1] + ":00");
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								throw e;
							}
							sct.setStartWorkTime(startTime);
							sct.setEndWorkTime(endTime);
							if (times[1].contains("次日")) {
								sct.setNextDay(true);
							}
						}
					}
				}else{
					for (int column = 0; column < columns; column++) {
						if (column == 0) {
							String treamName = rs.getCell(column, row)
									.getContents().trim();
							if ("".equals(treamName) || treamName == null) {
								throw new DictTypeException("技能组别名称不能为空");
							}
							if(!treamName.equals(sct.getTeamName())){
								sct.setTeamName(treamName);
							}
						}
						if (column == 2) {
							String workTimes = rs.getCell(column, row)
									.getContents().trim();
							String[] times = workTimes.split("-");
							if (times[0] == null || times[1] == null) {
								throw new DictTypeException(
										"上班时间段格式不正确，前后时间段不能为空，格式为：08:00-17:00");
							}
							Date startTime, endTime;
							try {
								SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
								startTime = df.parse(times[0] + ":00");
								if (times[1].contains("次日")) {
									endTime = df.parse(times[1].substring(2)
											+ ":00");
								} else {
									endTime = df.parse(times[1] + ":00");
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								throw e;
							}
							if(sct.getStartWorkTime().compareTo(startTime)!=0){
								sct.setStartWorkTime(startTime);
							}
							if(sct.getEndWorkTime().compareTo(endTime)!=0){
								sct.setEndWorkTime(endTime);
							}
							if(times[1].contains("次日")) {
								sct.setNextDay(true);
							}
						}
					}
					shiftConfigTimeService.update(sct);
				}
				list.add(sct);
			}
			is.close();
			wb.close();
			return list;
		}catch (DictTypeException e) {
			throw e;
		}
	} 
	
///////////////2012-11-27/////////////////////////////////////////
	//添加一个导出excel表格的方法
	public File exportAttShiftExcel(String fileName,String employee,String organization,Date startDate,Date endDate,String seachType,
			String notWell){
		String temporaryFile = System.getProperty("java.io.tmpdir");
		File tmpFile = new File(temporaryFile+fileName+".xls");
		WritableWorkbook book;
		try{
			book = Workbook.createWorkbook(tmpFile);
			//设置样式
			WritableFont fontTitle1 = new WritableFont(WritableFont.createFont("标题一"), 16, WritableFont.BOLD);
			WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle1);
			formatTitle1.setAlignment(Alignment.CENTRE);
			formatTitle1.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffb = new WritableFont(WritableFont.createFont("宋体"),11, WritableFont.BOLD);
			WritableCellFormat fb = new WritableCellFormat(ffb);
			fb.setAlignment(Alignment.CENTRE);
			fb.setVerticalAlignment(VerticalAlignment.CENTRE);
			fb.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont f = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fc = new WritableCellFormat(f);
			fc.setAlignment(Alignment.CENTRE);
			fc.setVerticalAlignment(VerticalAlignment.CENTRE);
			fc.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			WritableFont ffs = new WritableFont(WritableFont.createFont("宋体"),11);
			WritableCellFormat fs = new WritableCellFormat(ffs);
			fs.setAlignment(Alignment.CENTRE);
			fs.setVerticalAlignment(VerticalAlignment.CENTRE);
			fc.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			Calendar cal = new GregorianCalendar();
			
//			int row=0;
//			int column=0;
			List<AttShift> list = new ArrayList<AttShift>();
			List<Employee> emps = new ArrayList<Employee>();
 
			if(seachType.equals("employee")){
				Employee em = employeeService.get(employee);
				if(em!=null){
					emps.add(em);
				}else{
					emps = employeeService.getNotTurnAwayAndIsAttendance();
				}
			}else{
				if(organization==""||organization==null){
					emps = employeeService.getEmployeeByOrganizations(null);
				}else{
					emps = employeeService.getEmployeeByOrganizations(organization.split(","));
				}
			}
			WritableSheet sheet = book.createSheet("考勤记录查询表", 0);
			sheet.mergeCells(0, 0,5, 0);
			sheet.addCell(new Label(0,0,"考勤明细",fs));
			int row = 2;
			for(Employee e : emps){
				if(notWell.equals("false")){
					list = attShiftService.getDefDao().findByQueryString("select a from AttShift a where a.employee.id = ? and a.attDate >=? and attDate <= ? ", e.getId(),startDate,endDate);
				}else{
					list = attShiftService.getDefDao().findByQueryString("select a from AttShift a where a.employee.id = ? and a.attDate >=? and attDate <= ? and a.statusInfo is not ?", e.getId(),startDate,endDate,"正常工作");
				}
				
				Organization org = organizationService.getOrganizationById(Long.toString(e.getOrganization().getId()));
				
				sheet.addCell(new Label(0,1,"员工编号"));
				sheet.addCell(new Label(1,1,"员工姓名"));
				sheet.addCell(new Label(2,1,"部门"));
				sheet.addCell(new Label(3,1,"日期"));
				sheet.addCell(new Label(4,1,"星期"));
				sheet.addCell(new Label(5,1,"上班时间"));
				sheet.addCell(new Label(6,1,"下班时间"));
				sheet.addCell(new Label(7,1,"签入时间"));
				sheet.addCell(new Label(8,1,"签出时间"));
				sheet.addCell(new Label(9,1,"考勤状态"));
				cal.setFirstDayOfWeek(Calendar.MONDAY);
				cal.setTime(startDate);
				
				while(cal.getTime().before(endDate) || cal.getTime().equals(endDate)){
					sheet.addCell(new Label(0,row,e.getCode()));
					sheet.addCell(new Label(1,row,e.getName()));
					sheet.addCell(new Label(2,row,org.getName()));
					sheet.addCell(new Label(3,row,Tools.date2String(cal.getTime()),fs));
					sheet.addCell(new Label(4,row,Tools.getChineseDay(cal.get(Calendar.DAY_OF_WEEK))));
					for(AttShift a : list){
						if(!Tools.date2String(cal.getTime()).equals(Tools.date2String(a.getAttDate()))){
							continue;
						}
						sheet.addCell(new Label(5,row,Tools.date2String(a.getStartTime())));
						sheet.addCell(new Label(6,row,Tools.date2String(a.getEndTime())));
						sheet.addCell(new Label(7,row,Tools.dateTimeString(a.getCheckInTime())));
						sheet.addCell(new Label(8,row,Tools.dateTimeString(a.getCheckOutTime())));
						sheet.addCell(new Label(9,row,a.getStatusInfo().contains("加班")?"加班":a.getStatusInfo()));
					}
					cal.add(Calendar.DATE, 1);
					row=row+1;
				}
			}
			book.write();   
	        book.close();
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return tmpFile;
	}
	
	 
}
