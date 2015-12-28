package com.fortunes.faas.action;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import net.fortunes.core.Helper;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.faas.exception.NoBundleToApplyedException;
import com.fortunes.faas.model.BranchShiftAnalysis;
import com.fortunes.faas.model.BranchShiftAnalysis.AnalysisType;
import com.fortunes.faas.model.DefaultTime;
import com.fortunes.faas.model.ShiftConfig;
import com.fortunes.faas.model.ShiftConfigTemplate;
import com.fortunes.faas.model.WorkingArea;
import com.fortunes.faas.service.BranchShiftAnalysisService;
import com.fortunes.faas.service.DefaultTimeService;
import com.fortunes.faas.service.ShiftConfigBundleService;
import com.fortunes.faas.service.ShiftConfigService;
import com.fortunes.faas.service.ShiftConfigTemplateService;
import com.fortunes.faas.service.WorkingAreaService;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.OrganizationService;

@Component @Scope("prototype")
public class WorkingAreaAction extends GenericAction<WorkingArea> {
	
	private static final String CONFIG = "config";
	private static final String SPILT = "-";
	private static final String AREA = "area";
	@Resource private OrganizationService organizationService;
	@Resource private ShiftConfigTemplateService shiftConfigTemplateService;
	@Resource private DefaultTimeService defaultTimeService;
	@Resource private EmployeeService employeeService;
	private WorkingAreaService workingAreaService; 
	private ShiftConfigService shiftConfigService;
	private ShiftConfigBundleService shiftConfigBundleService;
	private BranchShiftAnalysisService branchShiftAnalysisService;
	
	@Override
	public String listTree() throws Exception {
		WorkingArea root = getNode().equals("0") ? 
				getDefService().getRoot():getDefService().get(getNode());
		JSONArray ja = walkTree(root);								
		return render(ja);
	}
	
	@Override
	protected JSONArray walkTree(WorkingArea workingArea) throws Exception {
		JSONArray ja = new JSONArray();
		List<WorkingArea> ds = workingArea.getChildren();
		for (WorkingArea d : ds) {
			if(null==d.getOrg()||d.getOrg().getId()!=AdminHelper.getOrganizationId()){
				continue;
			}
//			else if(d.getOrg().getId() ==4||d.getOrg().getId()==AdminHelper.getOrganizationId()){
				JSONObject jo = new JSONObject();
				jo.put("id", d.getId());
				jo.put("text", d.getName());
				jo.put("iconCls", "workingArea");
				if (d.getChildren().size() > 0) {
					jo.put("children", walkTree(d));
				} else {
					jo.put("leaf", true);
				}
				ja.add(jo);
			}
//		}
		return ja;
	}
	public String setDefaultTime()throws Exception{
		String startTime = p("startTime");
		String endTime = p("endTime");
		double bunchBreakTime = Double.parseDouble("".equals(p("bunchBreakTime"))||p("bunchBreakTime")==null?"0":p("bunchBreakTime"));
		Long orgId = Helper.getOrganizationId();
		DefaultTime dt = defaultTimeService.getDefaultTimeByOrg(orgId);
		if(dt==null){
			dt = new DefaultTime();
		}
		DateFormat df = new SimpleDateFormat("HH:mm");
		dt.setStartTime(df.parse(startTime));
		dt.setEndTime(df.parse(endTime));
		dt.setOrganization(new Organization(orgId));
		dt.setBunchBreakTime(bunchBreakTime);
		defaultTimeService.addOrUpdate(dt);
		setJsonMessage(true, "成功设置默认排班时间");
		return render(jo.toString());
	}
	
	public String getDefaultTime()throws Exception{
		Long orgId = Helper.getOrganizationId();
		DefaultTime dt = defaultTimeService.getDefaultTimeByOrg(orgId);
		DateFormat df = new SimpleDateFormat("HH:mm");
		AdminHelper record = new AdminHelper();
		String startTime = dt==null?"08:30":df.format(dt.getStartTime());
		String endTime = dt==null?"17:30":df.format(dt.getEndTime());
		String bunchBreakTime = dt==null?"1":dt.getBunchBreakTime()+"";
		record.put("startTime", startTime);
		record.put("endTime", endTime);
		record.put("bunchBreakTime", bunchBreakTime);
		return render(record.getJsonObject());
	}
	
	//============ for Branch Shift Config ==============
	public String getbranchShiftConfig() throws Exception{
		WorkingArea selectedArea;
		if(node.equals("0")){
			selectedArea = workingAreaService.getRoot();
		}else{
			selectedArea = workingAreaService.get(node.split(SPILT)[1]);
		}
		String orgId = p("organizationId");
		long currentOrgId = AdminHelper.getOrganizationId();
//		List<WorkingArea> list = workingAreaService.getAllWoringArea();
		List<WorkingArea> list = workingAreaService.getAllWorkingAreaByOrg("".equals(orgId)?Helper.getOrganizationId():Long.parseLong(orgId));
		
		Date selectedDate = Helper.toDate(p("selectedDate"));
		String roleCode = (String)getSessionMap().get(Helper.ROLE_CODE);
		long organizationId ;
		if(roleCode.contains("AM") && orgId!=null && orgId!=""){
			organizationId = Long.parseLong(orgId);
		}else{
			organizationId = Helper.getOrganizationId();
		}
		List<ShiftConfig> configs = shiftConfigService.getConfigs(selectedDate,organizationId);
		JSONArray ja = walkTreeInConfig(selectedArea,list,configs);
		return render(ja);
	}
	
	private JSONArray walkTreeInConfig(WorkingArea workingArea,List<WorkingArea> areas,List<ShiftConfig> configs) throws Exception{
		JSONArray ja = new JSONArray();
		if(workingArea != null){
			List<WorkingArea> list = workingAreaService.getChildres(workingArea, areas);	
			for(WorkingArea a : list){
				JSONObject areaJson = toJsonObjectForTree(a);
				JSONArray childrenJa = walkTreeInConfig(a,areas,configs);
//				if(configs.size() > 0){	
					for(ShiftConfig config : configs){
						if(config.getWorkingArea().getId()==a.getId()){
							JSONObject joConfig = toJsonObject(config);
							childrenJa.add(joConfig);
						}
					}
					areaJson.put("children", childrenJa);
					areaJson.put("expanded", true);
//				}
				ja.add(areaJson);
			}
		}
		return ja;
	}
	
	public String pasteShiftConfigTemplateToCurrentDay() throws Exception{
		String currentDayInWeekStr = p("currentDayInWeek");
		int currentDayInWeek = Integer.parseInt(currentDayInWeekStr);
		String dayInWeekStr = p("dayInWeek");
		int dayInWeek = Integer.parseInt(dayInWeekStr);
//		long bundleId = Long.parseLong(p("bundleId"));
		long organizationId = Helper.getOrganizationId();
		
		shiftConfigBundleService.pasteShiftConfigTemplateToCurrentDay(organizationId,currentDayInWeek,dayInWeek);
		return render(jo);
	}
	public String availableEmployees() throws Exception{
		Date selectedDate = Helper.toDate(p("selectedDate"));
		String roleCode = (String)getSessionMap().get(Helper.ROLE_CODE);
		String orgId = p("organizationId");
		long organizationId;
		if(roleCode.contains("AM") && orgId!=null && orgId!=""){
			organizationId = Long.parseLong(p("organizationId"));
		}else{
			organizationId = Helper.getOrganizationId(); 
		}
		List<Employee> list = workingAreaService.getAvailableEmployees(selectedDate,organizationId);
		JSONArray ja = new JSONArray();
		for(Employee e : list){
			JSONObject jo = toJsonObject(e);
			ja.add(jo);
		}
		return render(ja);
	}
	
	public String appendEmployee() throws Exception{
		Date selectedDate = Helper.toDate(p("selectedDate"));
		String areaId =  p("areaId").split(SPILT)[1];
		String employeeId = p("employeeId");
		if(employeeId.startsWith("area-")){
			setJsonMessage(true, "不能将窗口作为员工进行排班");
		}else{
			ShiftConfig config = shiftConfigService.findOrCreate(selectedDate, AppHelper.toEmployee(employeeId));
			Long orgId = AdminHelper.getOrganizationId();
			DefaultTime dt = defaultTimeService.getDefaultTimeByOrg(orgId);
			DateFormat df = new SimpleDateFormat("HH:mm");
			if(dt!=null){
				config.setStartTime(dt.getStartTime());
				config.setEndTime(dt.getEndTime());
				config.setBunchBreakTime(dt.getBunchBreakTime());
			}else{
				config.setStartTime(df.parse("08:30"));
				config.setEndTime(df.parse("17:30"));
				config.setBunchBreakTime(1);
			}
			config.setNextDay(false);
			config.setEmployee(AppHelper.toEmployee(employeeId));
			if(StringUtils.isNotEmpty(p("orgId"))){
				if(StringUtils.isNotEmpty(p("organizationId"))){
					if(!p("organizationId").equals(p("orgId"))){
						config.setConfigOrganization(AppHelper.toOrganization(p("orgId")));
					}
				}
			}else{
				if(StringUtils.isNotEmpty(p("organizationId")) && !p("organizationId").equals(Long.toString((Long)getSessionMap().get(Helper.ORGANIZATION_ID)))){
					config.setConfigOrganization(AppHelper.toOrganization(Long.toString((Long)getSessionMap().get(Helper.ORGANIZATION_ID))));
				}
			}
			
			config.setWorkingArea(AppHelper.toWorkingArea(areaId));
			config.setDate(selectedDate);
			config.setWeekEnd((selectedDate.getDay() == 0 || selectedDate.getDay() == 6) ? true : false);
			workingAreaService.createOrUpateBranchShiftConfig(config);
		}
		jo.put(SUCCESS_KEY, true);
		return render(jo);
	}
	
	public String removeEmployee() throws Exception{
		Date selectedDate = Helper.toDate(p("selectedDate"));
		String employeeId = p("employeeId");
		shiftConfigService.deleteConfig(selectedDate, AppHelper.toEmployee(employeeId));
		jo.put(SUCCESS_KEY, true);
		return render(jo);
	}
	
	public String removeAllEmployee()throws Exception{
		Date selectedDate = Helper.toDate(p("selectedDate"));
		String orgId = p("organizationId");
		List<ShiftConfig> list = shiftConfigService.getConfigs(selectedDate, Long.parseLong(orgId));
		if(list.size()==0|null==list){
			setJsonMessage(true,"该天该部门员工没有排班，无法删除");
		}else{
			if(StringUtils.isNotEmpty(orgId)){
				shiftConfigService.delAllShiftConfig(selectedDate,orgId);
				setJsonMessage(true,"该天排班清除成功！");
			}
		}
		return render(jo);
	}
	
	public String pasteShiftConfigToDay() throws Exception{
		Date selectDate = Helper.toDate(p("selectedDate"));
		String orgId = p("organizationId");
		if(null==orgId||"".equals(orgId)){
			orgId = Long.toString(Helper.getOrganizationId());
		}
		Organization org = organizationService.get(orgId);
		Date currentDate = Helper.toDate(p("currentDate"));
		try {
			shiftConfigService.pasteShiftConfigToCurrentday(selectDate,currentDate,orgId);
		} catch (NullPointerException e) {
			setJsonMessage(true,selectDate+"天没有排班，请重新选择！");
			e.printStackTrace();
		}
		setJsonMessage(true,"成功的把"+p("selectDate")+"天"+org.getName()+"的所有排班复制到"+p("currentDate")+"天");
		return render(jo);
	}
	
	public String exportConfigStat() throws Exception{
		String temporaryFile = System.getProperty("java.io.tmpdir");
		File tmpFile = new File(temporaryFile+Tools.uuid()+".xls");
		WritableWorkbook workbook = Workbook.createWorkbook(tmpFile);
		WritableSheet sheet = workbook.createSheet("Sheet1", 0);
		
		//设置列宽
		//sheet.setColumnView(0, 5);
		
		//设置样式
		WritableFont fontTitle1 = new WritableFont(WritableFont.createFont("标题一"), 18, WritableFont.BOLD);
		WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle1);
		formatTitle1.setAlignment(Alignment.CENTRE);
		
		WritableFont f = new WritableFont(WritableFont.createFont("内容"),8);
		WritableCellFormat ff = new WritableCellFormat(f);
		ff.setAlignment(Alignment.CENTRE);
		ff.setVerticalAlignment(VerticalAlignment.CENTRE);
		
		WritableFont f2 = new WritableFont(WritableFont.createFont("内容"),8);
		WritableCellFormat df = new WritableCellFormat(f2);
		df.setAlignment(Alignment.CENTRE);
		df.setVerticalAlignment(VerticalAlignment.CENTRE);
		
		WritableFont f3 = new WritableFont(WritableFont.createFont("内容"),8);
		WritableCellFormat wk = new WritableCellFormat(f3);
		wk.setAlignment(Alignment.CENTRE);
		wk.setVerticalAlignment(VerticalAlignment.CENTRE);
		wk.setBackground(Colour.AQUA);
		
		WritableFont f4 = new WritableFont(WritableFont.createFont("内容"),8);
		WritableCellFormat wd = new WritableCellFormat(f4);
		wd.setAlignment(Alignment.CENTRE);
		wd.setVerticalAlignment(VerticalAlignment.CENTRE);
		wd.setBackground(Colour.ICE_BLUE);
		
		
		sheet.mergeCells(0, 1, 1, 2);
		sheet.addCell(new Label(0,1,"日期/星期",ff));
		sheet.addCell(new Label(2,1,"区域",ff));
		sheet.addCell(new Label(2,2,"窗口号",ff));
//		sheet.addCell(new Label(2,3,"岗位",ff));
		
		List<Long> areaIds = new ArrayList<Long>();
		int colStart = 3;
		WorkingArea rootArea = workingAreaService.getRoot();
		for(WorkingArea firstLevelArea : rootArea.getChildren()){
			if(null==firstLevelArea.getOrg()||firstLevelArea.getOrg().getId()!=Helper.getOrganizationId()){
				continue;
			}else{
				List<WorkingArea> secondLevelAreas = firstLevelArea.getChildren();
				int size = secondLevelAreas.size();
				if(size > 0){
					sheet.mergeCells(colStart, 1,colStart+size-1, 1);
					sheet.addCell(new Label(colStart,1,firstLevelArea.getName(),ff));
				
					for(int i = 0;i < size;i++){
						sheet.addCell(new Label(colStart+i,2,secondLevelAreas.get(i).getName(),ff));
						areaIds.add(secondLevelAreas.get(i).getId());
					}
					colStart = colStart+size;
				}else{
					sheet.mergeCells(colStart, 1,colStart+size-1, 2);
					sheet.addCell(new Label(colStart,1,firstLevelArea.getName(),ff));
					areaIds.add(firstLevelArea.getId());
					colStart = colStart+1;
				}
			}
		}
		Organization org = organizationService.get(Long.toString(Helper.getOrganizationId()));
		//set the title
		sheet.mergeCells(0, 0,colStart-1, 0);
		sheet.addCell(new Label(0,0,org.getName()+p("yearMonth")+"弹性排班表",formatTitle1));
		//compute first date and last date in a mouth
		String yearMonth = p("yearMonth");
		Date firstDate = AppHelper.toDate(yearMonth+"-01");
		Calendar cal = new GregorianCalendar();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(firstDate);
		int lastDateInt = cal.getActualMaximum(Calendar.DATE);
		Date lastDate = AppHelper.toDate(yearMonth+"-"+lastDateInt);
		
		List<ShiftConfig> allConfigInMonth = shiftConfigService.getConfigs(firstDate,lastDate,Helper.getOrganizationId());
		
		int rowStart = 3;
		while(cal.getTime().before(lastDate) || cal.getTime().equals(lastDate)){
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			sheet.addCell(new Label(0,rowStart,Tools.date2String(cal.getTime()),df));
			sheet.addCell(new Label(1,rowStart,Tools.getChineseDay(dayOfWeek),df));
			int configColStart = 3;
			for(Long aredId : areaIds){
				String c = "";
				for(ShiftConfig config :  allConfigInMonth){
					if(config.getWorkingArea()==null){
						continue;
					}
					if(config.getDate().equals(cal.getTime()) && config.getWorkingArea().getId() == aredId){
						c += config.getEmployee().getName();
						if(config.getConfigOrganization()!=null){
							c += "("+config.getEmployee().getOrganization()==null?"":config.getEmployee().getOrganization().getShortName()+")";
						}
						c +="/";
					}
				}
				if(c.endsWith("/")){
					c = c.substring(0,c.length()-1);
				}
				
				if(dayOfWeek==1||dayOfWeek==7){
					sheet.addCell(new Label(configColStart,rowStart,c,wk));
				}else{
					sheet.addCell(new Label(configColStart,rowStart,c,wd));
				}
				configColStart++;
			}
			cal.add(Calendar.DATE, 1);
			rowStart++;
		}
		workbook.write();
		workbook.close();
		byte[] bytes = FileUtils.readFileToByteArray(tmpFile);
		return renderFile(bytes, p("yearMonth")+"排班表.xls");
	}
	public String exportConfigAllStat() throws Exception{
		String temporaryFile = System.getProperty("java.io.tmpdir");
		File tmpFile = new File(temporaryFile+Tools.uuid()+".xls");
		WritableWorkbook workbook = Workbook.createWorkbook(tmpFile);
		
		
		WritableFont fontTitle1 = new WritableFont(WritableFont.createFont("标题一"), 18, WritableFont.BOLD);
		WritableCellFormat formatTitle1 = new WritableCellFormat(fontTitle1);
		formatTitle1.setAlignment(Alignment.CENTRE);
		
		WritableFont f = new WritableFont(WritableFont.createFont("内容"),8);
		WritableCellFormat ff = new WritableCellFormat(f);
		ff.setAlignment(Alignment.CENTRE);
		ff.setVerticalAlignment(VerticalAlignment.CENTRE);
		
		WritableFont f2 = new WritableFont(WritableFont.createFont("内容"),8);
		WritableCellFormat df = new WritableCellFormat(f2);
		df.setVerticalAlignment(VerticalAlignment.CENTRE);
		
		WritableFont f3 = new WritableFont(WritableFont.createFont("内容"),8);
		WritableCellFormat wk = new WritableCellFormat(f3);
		wk.setAlignment(Alignment.CENTRE);
		wk.setVerticalAlignment(VerticalAlignment.CENTRE);
		wk.setBackground(Colour.AQUA);
		
		WritableFont f4 = new WritableFont(WritableFont.createFont("内容"),8);
		WritableCellFormat wd = new WritableCellFormat(f4);
		wd.setAlignment(Alignment.CENTRE);
		wd.setVerticalAlignment(VerticalAlignment.CENTRE);
		wd.setBackground(Colour.ICE_BLUE);
		
//		Organization organization = organizationService.get(Long.toString(AdminHelper.getOrganizationId()));
		List<Organization> organizations = organizationService.getOrganizationByType("organizationType_01");
		for(Organization o : organizations){
			int column=0;
			int row=0;
			WritableSheet sheet = workbook.createSheet(o.getName(), 0);
			sheet.mergeCells(0, 1, 1, 2);
			sheet.addCell(new Label(0,1,"日期/星期",ff));
			sheet.addCell(new Label(2,1,"区域",ff));
			sheet.addCell(new Label(2,2,"窗口号",ff));
//			sheet.addCell(new Label(2,3,"岗位",ff));
			
			List<Long> areaIds = new ArrayList<Long>();
			
			int colStart = 3;
			WorkingArea rootArea = workingAreaService.getRoot();
			for(WorkingArea firstLevelArea : rootArea.getChildren()){
				List<WorkingArea> secondLevelAreas = firstLevelArea.getChildren();
				int size = secondLevelAreas.size();
				if(size > 0){
					sheet.mergeCells(colStart, row+1,colStart+size-1, row+1);
					sheet.addCell(new Label(colStart,row+1,firstLevelArea.getName(),ff));
					
					for(int i = 0;i < size;i++){
						sheet.addCell(new Label(colStart+i,row+2,secondLevelAreas.get(i).getName(),ff));
						areaIds.add(secondLevelAreas.get(i).getId());
//						row=row+1;
					}
					colStart = colStart+size;
				}else{
					sheet.mergeCells(colStart, row+1,colStart+size-1, row+2);
					sheet.addCell(new Label(colStart,1,firstLevelArea.getName(),ff));
					areaIds.add(firstLevelArea.getId());
					colStart = colStart+1;
//					row=row+1;
				}
			}
			sheet.mergeCells(column, row,colStart-1, row);
			sheet.addCell(new Label(0,row,p("yearMonth")+o.getName()+"排班表",formatTitle1));
			row=row+3;
			//compute first date and last date in a mouth
			String yearMonth = p("yearMonth");
			Date firstDate = AppHelper.toDate(yearMonth+"-01");
			Calendar cal = new GregorianCalendar();
			cal.setFirstDayOfWeek(Calendar.MONDAY);
			cal.setTime(firstDate);
			int lastDateInt = cal.getActualMaximum(Calendar.DATE);
			Date lastDate = AppHelper.toDate(yearMonth+"-"+lastDateInt);
			
			List<ShiftConfig> allConfigInMonth = shiftConfigService.getConfigs(firstDate,lastDate,o.getId());
			int rowStart = 3;
			while(cal.getTime().before(lastDate) || cal.getTime().equals(lastDate)){
				int weekDay = cal.get(Calendar.DAY_OF_WEEK);
				sheet.addCell(new Label(0,row,Tools.date2String(cal.getTime()),df));
				sheet.addCell(new Label(1,row,Tools.getChineseDay(weekDay),df));
				int configColStart = 3;
				for(Long aredId : areaIds){
					String c = "";
					for(ShiftConfig config :  allConfigInMonth){
						if(config.getWorkingArea()==null){
							continue;
						}
						if(config.getDate().equals(cal.getTime()) && config.getWorkingArea().getId() == aredId){
							c += config.getEmployee().getName();
							if(config.getConfigOrganization()!=null){
								c += "("+config.getEmployee().getOrganization()!=null?config.getEmployee().getOrganization().getShortName():""+")";
							}
							c +="/";
						}
					}
					if(c.endsWith("/")){
						c = c.substring(0,c.length()-1);
					}
					if(weekDay==1||weekDay==7){
						sheet.addCell(new Label(configColStart,row,c,wk));
					}else{
						sheet.addCell(new Label(configColStart,row,c,wd));
					}
					
					configColStart++;
					colStart=configColStart+colStart;
				}
				cal.add(Calendar.DATE, 1);
				rowStart++;
				row=row+1;
			}
		}
		workbook.write();
		workbook.close();
		
		byte[] bytes = FileUtils.readFileToByteArray(tmpFile);
		return renderFile(bytes, p("yearMonth")+"排班表.xls");
	}
	
	//============ for Conifg Template ==============
	
	public String getConifgTemplates() throws Exception{
		//p("bundleId")
		WorkingArea selectedArea;
		if(node.equals("0")){
			selectedArea = workingAreaService.getRoot();
		}else{
			selectedArea = workingAreaService.get(node.split(SPILT)[1]);
		}
//		List<WorkingArea> list = workingAreaService.getAllWoringArea();
		List<WorkingArea> list = workingAreaService.getAllWorkingAreaByOrg(AdminHelper.getOrganizationId());
		List<ShiftConfigTemplate> configs = getShiftConfigBundleService().getTemplate(
				Long.parseLong(p("bundleId")));
		JSONArray ja = walkTreeInTemplateConfig(selectedArea,list,configs);
		return render(ja);
	}
	
	private JSONArray walkTreeInTemplateConfig(WorkingArea workingArea,List<WorkingArea> areas,List<ShiftConfigTemplate> configs) throws Exception {
		JSONArray ja = new JSONArray();
		if(workingArea != null){
			List<WorkingArea> list = workingAreaService.getChildres(workingArea, areas);	
			for(WorkingArea a : list){
				JSONObject areaJson = toJsonObjectForTree(a);
				JSONArray childrenJa = walkTreeInTemplateConfig(a,areas,configs);
//				if(configs.size() > 0){	
					for(ShiftConfigTemplate config : configs){
						if(config.getWorkingArea().getId()==a.getId()){
							JSONObject joConfig = toJsonObject(config);
							childrenJa.add(joConfig);
						}
					}
					areaJson.put("children", childrenJa);
					areaJson.put("expanded", true);
//				}
				ja.add(areaJson);
			}
		}
		return ja;
	}

	public String availableEmployeesInTemplate() throws Exception{
		long organizationId = Helper.getOrganizationId();
		List<Employee> list = getShiftConfigBundleService().getAvailableEmployees(
				Long.parseLong(p("bundleId")),organizationId);
		JSONArray ja = new JSONArray();
		for(Employee e : list){
			JSONObject jo = toJsonObject(e);
			ja.add(jo);
		}
		return render(ja);
	}
	
	public String appendEmployeeTemplate() throws Exception{
		long bundleId = Long.parseLong(p("bundleId"));
		String areaId =  p("areaId").split(SPILT)[1];
		String employeeId = p("employeeId");
		
		ShiftConfigTemplate config = getShiftConfigBundleService().findOrCreate(bundleId, AppHelper.toEmployee(employeeId));
		DateFormat df = new SimpleDateFormat("HH:mm");
		Long orgId = AdminHelper.getOrganizationId();
		DefaultTime dt = defaultTimeService.getDefaultTimeByOrg(orgId);
		if(dt!=null){
			config.setStartTime(dt.getStartTime());
			config.setEndTime(dt.getEndTime());
			config.setBunchBreakTime(dt.getBunchBreakTime());
		}else{
			config.setStartTime(df.parse("08:30"));
			config.setEndTime(df.parse("17:30"));
			config.setBunchBreakTime(1);
		}
		config.setEmployee(AppHelper.toEmployee(employeeId));
		config.setWorkingArea(AppHelper.toWorkingArea(areaId));
		config.setShiftConfigBundle(AppHelper.toShiftConfigBundle(bundleId+""));
		
		getShiftConfigBundleService().createOrUpateBranchShiftConfig(config);
		jo.put(SUCCESS_KEY, true);
		
		return render(jo);
	}
	
	public String changeTemplateTime()throws Exception{
		long bundleId = Long.parseLong(p("bundleId"));
		String employeeId = p("employeeId");
		double bunchBreakTime = Double.parseDouble("".equals(p("bunchBreakTime"))||p("bunchBreakTime")==null?"0":p("bunchBreakTime"));
		ShiftConfigTemplate config = getShiftConfigBundleService().findOrCreate(bundleId, AppHelper.toEmployee(employeeId));
		DateFormat df = new SimpleDateFormat("HH:mm");
		config.setStartTime(df.parse(p("startTime")));
		config.setEndTime(df.parse(p("endTime")));
		config.setBunchBreakTime(bunchBreakTime);
		getShiftConfigBundleService().createOrUpateBranchShiftConfig(config);
		jo.put(SUCCESS_KEY, true);
		return render(jo);
	}
	
	public String getTemplateTime() throws Exception{
		long bundleId = Long.parseLong(p("bundleId"));
		String employeeId = p("employeeId");
		ShiftConfigTemplate config = getShiftConfigBundleService().findOrCreate(bundleId, AppHelper.toEmployee(employeeId));
		DateFormat df = new SimpleDateFormat("HH:mm");
		
		AdminHelper record = new AdminHelper();
		String startTime = config==null?"08:30":df.format(config.getStartTime());
		String endTime = config==null?"17:30":df.format(config.getEndTime());
		String bunchBreakTime = config==null?"1":config.getBunchBreakTime()+"";
		record.put("startTime", startTime);
		record.put("endTime", endTime);
		record.put("bunchBreakTime", bunchBreakTime);
		return render(record.getJsonObject());
		
	}
	
	public String removeEmployeeTemplate() throws Exception{
		long bundleId = Long.parseLong(p("bundleId"));
		
		getShiftConfigBundleService().deleteConfig(bundleId, AppHelper.toEmployee(p("employeeId")));
		jo.put(SUCCESS_KEY, true);
		return render(jo);
	}
	
	//============ check and apply template ==================
	
	public String checkTemplateApplyStatus() throws Exception{
		long organizationId = Helper.getOrganizationId();
		boolean hasApplyed = getShiftConfigBundleService().checkApplyed(organizationId,p("yearMonth"));
		jo.put(SUCCESS_KEY, true);
		jo.put("applyed", hasApplyed);
		return render(jo);
	}
	
	public String applyTemplate() throws Exception{
		final long organizationId = Helper.getOrganizationId();
		try {
//			Thread t = new Thread(new Runnable(){
//				@Override
//				public void run() {
//					try {
						getShiftConfigBundleService().applyTemplate(organizationId,p("yearMonth"));
//					} catch (Exception e) {
//						e.printStackTrace();
//					}			
//				}
//			});
//			t.start();
//			setJsonMessage(true, "开始应用排班模版，此操作可能耗时较长，请稍后在查看");
//			jo.put(SUCCESS_KEY, true);
		}catch(NoBundleToApplyedException e){
			setJsonMessage(false, "请初始化排班模板");
			e.printStackTrace();
		}finally{
			return render(jo);
		}
	}
	
	public String setEmployeeTime() throws Exception {
		String employeeId = p("employeeId");
		Date date = Tools.string2Date(p("date"));
		String startTime = p("startTime");
		String endTime = p("endTime");
		double bunchBreakTime = Double.parseDouble("".equals(p("bunchBreakTime"))||p("bunchBreakTime")==null?"0":p("bunchBreakTime"));
//		long orgId = employeeService.get(employeeId).getOrganization().getId();
//		DefaultTime dt = defaultTimeService.getDefaultTimeByOrg(orgId);
//		dt.setBunchBreakTime(bunchBreakTime);
//		defaultTimeService.update(dt);
		shiftConfigService.setEmployeeTime(employeeId, date, startTime, endTime,bunchBreakTime);
		jo.put(SUCCESS_KEY, true);
		return render(jo);
	}
	
	public String getEmployeeTime()throws Exception{
		String employeeId = p("employeeId");
		Date date = Tools.string2Date(p("date"));
		ShiftConfig sc = shiftConfigService.queryShiftConfigs(employeeId,date);
		DateFormat df = new SimpleDateFormat("HH:mm");
		AdminHelper record = new AdminHelper();
		String startTime = sc==null?"08:30":df.format(sc.getStartTime());
		String endTime = sc==null?"17:30":df.format(sc.getEndTime());
		String bunchBreakTime = sc==null?"1":sc.getBunchBreakTime()+"";
		record.put("startTime", startTime);
		record.put("endTime", endTime);
		record.put("bunchBreakTime", bunchBreakTime);
		return render(record.getJsonObject());
	}
	
	public String listUnShiftEmployees() throws Exception {
		long organizationId = Long.parseLong(p("organizationId"));
		String type = p("selectType");
		Organization organization = organizationService.get(Long.toString(organizationId));
		Date date = Tools.string2Date(p("date"));
		Calendar cal=new GregorianCalendar();
		String yearMonth=p("date").substring(0,7);
		Date firstDate = Tools.string2Date(yearMonth+"-01");
		cal.setTime(firstDate);
		int lastDate = cal.getActualMaximum(Calendar.DATE);
		Date endDate = Tools.string2Date(yearMonth+"-"+lastDate);
		List<Organization> organizations = null;
		if(type.equals("ALL_BANK") || type.equals("LOW_THREE_DAYS")){
			String roleCode = (String)getSessionMap().get(Helper.ROLE_CODE);
			if(roleCode.contains("ALLM")){
				organizations = organizationService.getOrganizationByType("organizationType_01");
			}else if(roleCode.contains("AM")){
				Organization o = organizationService.get(Long.toString((Long)getSessionMap().get(Helper.ORGANIZATION_ID)));
				if(o.getType().getId().equals("organizationType_01")){
					o = o.getParent();
				}
				organizations = o.getChildren();
			}else if(roleCode.contains("BM")){
				
				organizations = new ArrayList<Organization>();
				organizations.add(organization);
			}
			
		}else{
//			organizations = new ArrayList<Organization>();
			if(organizationId == 00){
				organizations = organizationService.getOrganizationByType("organizationType_01");
			}else{
				organizations = new ArrayList<Organization>();
				organizations.add(organization);
			}
			
			
		}
		JSONArray ja = new JSONArray();
		for(Organization o : organizations){
			List<Employee> employees=null;
			if(type.equals("ALL_BANK")||type.equals("BRANCH")){
				employees = workingAreaService.getAvailableEmployees(date,o.getId());
			}else if(type.equals("LOW_THREE_DAYS")){
				employees = workingAreaService.getAttLowThreeDay(o.getId(),3,firstDate,endDate);
			}
			for(Employee e : employees){
				JSONObject record = new JSONObject();
				record.put("id", e.getId());
				String text = e.getName();
				if(e.getBirthday()!=null){
					text = text + "  "+ Tools.getAge(e.getBirthday());
				}
				if(e.getPosition()!=null){
					text = text + "  " + e.getPosition().getText();
				}
				text = text + " "+ o.getShortName();
				record.put("text",text);
				record.put("iconCls", 
						(e.getSex()!=null && e.getSex().getText().equals("女"))? "employee_female" : "userman");
				record.put("leaf", true);
				ja.add(record);
			}
		}
		return render(ja);  
	}
	
	//============ private methods ==================
	
	private JSONObject toJsonObject(ShiftConfig config) {
		AdminHelper record = new AdminHelper();
		Employee e = config.getEmployee();
		ShiftConfig sc = shiftConfigService.get(config.getId()+"");
		Employee ee = employeeService.get(e.getId()+"");
		record.put("id", e.getId());
		String text = e.getName()+"<span style='color:blue'>["+ee.getOrganization().getName()+"]</span>";
		if(e.getSex()!=null){
			record.put("iconCls", e.getSex().getText().equals("女")?"employee_female":"userman");
		}
		if(e.getBirthday()!=null){
			text = text + "  "+ Tools.getAge(e.getBirthday());
		}
		if(e.getPosition()!=null&&e.getPosition().getText()!=null){
			text = text + "  " + e.getPosition().getText();
		}
		if(config.getConfigOrganization()!=null){
			record.put("qtip", "部门："+e.getOrganization().getShortName());
			record.put("organization", e.getOrganization().getShortName());
		}
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		if(sc.getStartTime()!=null && sc.getEndTime()!=null && sc.getBunchBreakTime()!=0){
			text = text + "<span style='color:blue'>("+df.format(sc.getStartTime())+"~~"+df.format(sc.getEndTime())+")</span>"+"<span style='color:blue'>[午休 "+Tools.getValuesNotZore(sc.getBunchBreakTime())+" 小时]</span>";
		}
		record.put("text", text);
		record.put("leaf", true);
		
		return record.getJsonObject();
	}
	private JSONObject toJsonObject(ShiftConfigTemplate configTemplate) {
		AdminHelper record = new AdminHelper();
		Employee e = configTemplate.getEmployee();
		
		ShiftConfigTemplate sct = shiftConfigTemplateService.get(configTemplate.getId()+"");
		
		record.put("id", e.getId());
		String text = e.getName();
		if(e.getSex()!=null){
			record.put("iconCls", e.getSex().getText().equals("女")?"employee_female":"userman");
		}
		text = text + "  "+ Tools.getAge(e.getBirthday());
		if(e.getPosition()!=null){
			text = text + "  " + e.getPosition().getText();
		}
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		text = text+"<span style='color:blue'>("+df.format(sct.getStartTime())+"~"+df.format(sct.getEndTime())+")</span>"+"<span style='color:blue'>[午休 "+Tools.getValuesNotZore(sct.getBunchBreakTime())+" 小时]</span>";
		record.put("text", text);
		record.put("leaf", true);
		return record.getJsonObject();
	}
	
	private JSONObject toJsonObject(Employee e) {
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		String text = e.getName();
		Dict position = e.getPosition();
		if(e.getSex()!=null){
			record.put("iconCls", e.getSex().getText().equals("女")?"employee_female":"userman");
		}
		if(e.getBirthday()!=null){
			text = text + "  "+ Tools.getAge(e.getBirthday());
		}
		if(position!=null){
			String t = position.getText()==null?" ":position.getText();
			text = text + "  " + t;
		}
		record.put("text", text);
		record.put("leaf", true);
		return record.getJsonObject();
	}
	
	protected JSONObject toJsonObjectForTree(WorkingArea e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", AREA+SPILT+e.getId());
		record.put("text", e.getName());
		record.put("cls", "parent-menu");
		record.put("iconCls", "workingArea");
		return record.getJsonObject();
	}
	
	protected JSONObject toJsonObject(WorkingArea e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id",e.getId());
		record.put("text", e.getName());
		record.put("iconCls", "workingArea");
		return record.getJsonObject();
	}
	
	@Override
	protected void setEntity(WorkingArea e) throws Exception {
		WorkingArea parent = parentId.equals("0")? 
				workingAreaService.getRoot() : AppHelper.toWorkingArea(parentId);
		e.setParent(parent);
		e.setName(p("text"));
		//关联部门
		e.setOrg(AppHelper.toOrganization(AdminHelper.getOrganizationId()+""));
		
	}
	
	@Override
	public GenericService<WorkingArea> getDefService() {
		return workingAreaService;
	}
	
	

	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	public OrganizationService getOrganizationService() {
		return organizationService;
	}

	public void setWorkingAreaService(WorkingAreaService workingAreaService) {
		this.workingAreaService = workingAreaService;
	}

	public WorkingAreaService getWorkingAreaService() {
		return workingAreaService;
	}


	public void setShiftConfigService(ShiftConfigService shiftConfigService) {
		this.shiftConfigService = shiftConfigService;
	}

	public ShiftConfigService getShiftConfigService() {
		return shiftConfigService;
	}

	public void setShiftConfigBundleService(ShiftConfigBundleService shiftConfigBundleService) {
		this.shiftConfigBundleService = shiftConfigBundleService;
	}

	public ShiftConfigBundleService getShiftConfigBundleService() {
		return shiftConfigBundleService;
	}

	public String removeAllTemplateEmployee()throws Exception{
		String currentDayInWeekStr = p("currentDayInWeek");
		int currentDayInWeek = Integer.parseInt(currentDayInWeekStr);
		long bundleId = Long.parseLong(p("bundleId"));
		long organizationId = Helper.getOrganizationId();
		List<ShiftConfigTemplate> list = shiftConfigTemplateService.getShiftConfigTemplates(currentDayInWeek, bundleId, organizationId);
		if(list.size()==0|null==list){
			setJsonMessage(true,"模板中每周的这一天没有排班，无法删除");
		}else{
			shiftConfigTemplateService.delAllShiftConfigTemplate(bundleId);
			setJsonMessage(true,"该模板每周本天排班清除成功！");
		}
		
		return render(jo);
	}


	public void setBranchShiftAnalysisService(BranchShiftAnalysisService branchShiftAnalysisService) {
		this.branchShiftAnalysisService = branchShiftAnalysisService;
	}

	public BranchShiftAnalysisService getBranchShiftAnalysisService() {
		return branchShiftAnalysisService;
	}
}
