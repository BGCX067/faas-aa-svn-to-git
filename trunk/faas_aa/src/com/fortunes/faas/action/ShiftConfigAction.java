package com.fortunes.faas.action;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.fortunes.faas.model.ShiftConfig;
import com.fortunes.faas.service.ExcelHelperService;
import com.fortunes.faas.service.ShiftConfigService;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Config;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.Config.ConfigKey;
import com.fortunes.fjdp.admin.service.ConfigService;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.OrganizationService;
import com.fortunes.fjdp.admin.service.UserService;

@Component @Scope("prototype")
public class ShiftConfigAction extends GenericAction<ShiftConfig> {
	
	private static final String WORK = "WORK";
	private ShiftConfigService shiftConfigService;
	private OrganizationService organizationService;
	private EmployeeService employeeService;
	@Resource ExcelHelperService excelHelperService;
	@Resource private UserService userService;
	@Resource private ConfigService configService;
	public String [] employeeIds;
	
	private static final String SPLIT = "-";
	private static final String EMPLOYEE = "e";
	private static final String ORGANIZATION = "o";
	private static final String FIRST = "f";
	
	public String listEmployees() throws Exception{
		String type = node.split(SPLIT)[0];
		int id = Integer.parseInt(node.split(SPLIT)[1]);
		if(type.startsWith(FIRST)){
			Organization selectedOrganization;
			JSONArray ja = new JSONArray();
			Long [] orgArray = userService.getUserOperateOrganization();
			int len = orgArray.length;
			if(len==1){
				selectedOrganization = organizationService.get(Long.toString(orgArray[0]));
				for(Employee e : employeeService.getEmployeeByOrgId(selectedOrganization.getId())){
					ja.add(toJsonObject(e));
				}
			}else{
				for(int i =0;i<len;i++){
					selectedOrganization = organizationService.get(Long.toString(orgArray[i]));
					ja.add(toJsonObject(selectedOrganization));
				}
			}
			return render(ja);
		}else if(type.startsWith(ORGANIZATION)){
			JSONArray ja = new JSONArray();
			Organization selectedOrganization = organizationService.get(id+"");
			for(Employee e : employeeService.getEmployeeByOrgId(selectedOrganization.getId())){
				ja.add(toJsonObject(e));
			}
			return render(ja);
		}else{
			throw new IllegalArgumentException();
		}
	}
	
	//列出服务管理中心的员工
	public String listEmployeeByOrg() throws Exception{
		String type = node.split(SPLIT)[0];
		int id = Integer.parseInt(node.split(SPLIT)[1]);
		if(type.startsWith(FIRST)){
			Organization selectedOrganization =organizationService.get("35");
			JSONArray ja = new JSONArray();
			for(Employee e : employeeService.getEmployeeByOrgId(selectedOrganization.getId())){
				ja.add(toJsonObject(e));
			}
			
			return render(ja);
		}else{
			throw new IllegalArgumentException();
		}
	}

	
	public String createShiftConfig() throws Exception{
		String[] dates = p("dates").split(",");
		String[] employeeIds = p("employeeIds").split(",");
		shiftConfigService.batchCreate(dates,employeeIds,
				p("startTime"),p("endTime"),p("nextDay").equals("on")?true:false,p("cancelShiftConfig").equals("on")?true:false);
		setJsonMessage(true, "成功设置员工排班");
		return render(jo);
	}
	
	public String createNextMonthShiftConfig() throws Exception{
		List<Long> list = new ArrayList<Long>();
//		if(employeeIds.length==1&&employeeIds[0]==""){
//			 list = employeeService.getEmployeeIdByOrg(AdminHelper.getOrganizationId());
//		}else{
//			for(String id : employeeIds){
//				list.add(Long.parseLong(id.split("-")[1]));
//			}
//		}
		shiftConfigService.createNextMonth(list);
//		setJsonMessage(true, "成功设置员工排班");
		return render(jo);
	}
	public String delOrAdd()throws Exception{
		ShiftConfig shiftConfig = shiftConfigService.get(id);
		if(shiftConfig!=null){
			shiftConfigService.del(shiftConfig);
		}
		shiftConfig = new ShiftConfig();
		shiftConfig.setCancel(true);//取消当日排版
		shiftConfig.setDate(Tools.string2Date(p("date")));
		shiftConfig.setEmployee(AdminHelper.toEmployee(p("employeeId")));
		shiftConfigService.add(shiftConfig);
		setJsonMessage(true,"记录成功删除!");
		return render(jo);
	}
	
	public String getInfos() throws Exception{
		List<ShiftConfig> list = shiftConfigService.queryShiftConfigs(
				p("employeeId"),AppHelper.toDate(p("startDate")),AppHelper.toDate(p("endDate")));
		JSONArray ja = new JSONArray();
		Calendar calendar = Calendar.getInstance();
//		Date sDate = Tools.string2Date(sf.format(calendar.getTime())+"-01");
		Date sDate = Tools.string2Date(p("startDate"));
		Date eDate = Tools.string2Date(p("endDate"));
		calendar.setTime(sDate);
//		Date eDate = Tools.string2Date(sf.format(calendar.getTime())+"-"+calendar.getActualMaximum(calendar.DAY_OF_MONTH));
		Employee e = employeeService.get(p("employeeId"));
		SimpleDateFormat sfd = new SimpleDateFormat("HH:mm:ss");
		Config start = configService.getConfigBykey(ConfigKey.START_WORK_IN_MORNING_TIME);
		Config end = configService.getConfigBykey(ConfigKey.OVER_WORK_IN_AFTERNOON_TIME);
		Date startTime = sfd.parse(start.getConfigValue()+":00");
		Date endTime = sfd.parse(end.getConfigValue()+":00");
		while (eDate.compareTo(calendar.getTime())>=0) {
			boolean isExit = false;
			for(ShiftConfig info:list){
				if(info.getDate().equals(calendar.getTime())){
					isExit = true;
				}
				if(isExit){
					if(!info.isCancel()){
						ja.add(toJsonObject(info));
					}
				}
			}
			if(!isExit){
				Date d = calendar.getTime();
				if(d.getDay() != 0 && d.getDay() != 6){
					ShiftConfig info = new ShiftConfig();
					info.setWeekEnd((d.getDay() == 0 || d.getDay() == 6) ? true : false);
					info.setDate(calendar.getTime());info.setStartTime(startTime);info.setEndTime(endTime);
					info.setEmployee(e);info.setNextDay(false);
					ja.add(toJsonObject(info));
				}
			}
			calendar.add(calendar.DATE, 1);
		}
		
		jo.put(DATA_KEY, ja);
		return render(jo);
	}
	
	public String loadConfig() throws Exception{
		ShiftConfig config = shiftConfigService.queryShiftConfigs(
				p("employeeId"),AppHelper.toDate(p("date")));
		Employee e = employeeService.get(p("employeeId"));
		JSONObject json = new JSONObject();
		Config start = configService.getConfigBykey(ConfigKey.START_WORK_IN_MORNING_TIME);
		Config end = configService.getConfigBykey(ConfigKey.OVER_WORK_IN_AFTERNOON_TIME);
		DateFormat df = new SimpleDateFormat("HH:mm");
		if(config ==null){
			json.put("employeeId", p("employeeId"));
			json.put("employeeName", e.getName());
			json.put("date", p("date"));
			json.put("startTime",start.getConfigValue());
			json.put("endTime",  end.getConfigValue());
			json.put("nextDay", "no");
		}else{
			json.put("id", config.getId());
			json.put("employeeId", config.getEmployee().getId());
			json.put("employeeName", config.getEmployee().getName());
			json.put("date", Tools.date2String(config.getDate()));
			json.put("startTime", df.format(config.getStartTime()));
			json.put("endTime",  df.format(config.getEndTime()));
			json.put("nextDay", config.isNextDay()?"on":"");
		}
		setJsonMessage(true, json);
		return render(jo);
	}
	
	public String updateConfig() throws Exception{
		ShiftConfig config = shiftConfigService.get(id);
		DateFormat df = new SimpleDateFormat("HH:mm");
		if(config==null){
			config = new ShiftConfig();
			config.setStartTime(df.parse(p("startTime")));
			config.setEmployee(AdminHelper.toEmployee(p("employeeId")));
			config.setEndTime(df.parse(p("endTime")));
			config.setDate(Tools.string2Date(p("date")));
			config.setNextDay(p("nextDay").equals("on")?true:false);
		}else{
			config.setStartTime(df.parse(p("startTime")));
			config.setEndTime(df.parse(p("endTime")));
			config.setNextDay(p("nextDay").equals("on")?true:false);
		}
		shiftConfigService.addOrUpdate(config);
		setJsonMessage(true,"成功更新班次设置!");
		return render(jo);
	}
	
	public String exportShift()throws Exception{
		String orgId = p("organizationId");
		Organization org = organizationService.get(orgId);
		String fileName = org.getName()+"排班情况";
		File file = excelHelperService.exportShiftToOrganization(fileName,orgId);
		byte[] bytes = FileUtils.readFileToByteArray(file);
		return renderFile(bytes, fileName);
	}
	public String getShiftConfigInfos()throws Exception{
		List<ShiftConfig> list = shiftConfigService.queryShiftConfigs(
				p("employeeId"),AppHelper.toDate(p("startDate")),AppHelper.toDate(p("endDate")));
		JSONArray ja = new JSONArray();
		Calendar calendar = Calendar.getInstance();
		Date sDate = Tools.string2Date(p("startDate"));
		Date eDate = Tools.string2Date(p("endDate"));
		calendar.setTime(sDate);
		Employee e = employeeService.get(p("employeeId"));
		SimpleDateFormat sfd = new SimpleDateFormat("HH:mm:ss");
		Config start = configService.getConfigBykey(ConfigKey.START_WORK_IN_MORNING_TIME);
		Config end = configService.getConfigBykey(ConfigKey.OVER_WORK_IN_AFTERNOON_TIME);
		Date startTime = sfd.parse(start.getConfigValue()+":00");
		Date endTime = sfd.parse(end.getConfigValue()+":00");
		while (eDate.compareTo(calendar.getTime())>=0) {
			boolean isExit = false;
			for(ShiftConfig info:list){
				if(info.getDate().equals(calendar.getTime())){
					isExit = true;
				}
				if(isExit){
					if(!info.isCancel()){
						ja.add(toJsonObject(info));
					}
				}
			}
			calendar.add(calendar.DATE, 1);
		}
		
		jo.put(DATA_KEY, ja);
		return render(jo);
	}

	
	protected void setEntity(ShiftConfig e) throws ParseException{
	}
	
	protected JSONObject toJsonObject(ShiftConfig e) throws ParseException{
		DateFormat fmt = new SimpleDateFormat("HH:mm");
		String timeRange = fmt.format(e.getStartTime());
		if(e.isNextDay()){
			timeRange += "-次日"+fmt.format(e.getEndTime());
		}else{
			timeRange += "-"+fmt.format(e.getEndTime());
		}
		
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("date", Tools.date2String(e.getDate()));
		record.put("timeRange", timeRange);
		record.put("type", WORK);
		return record.getJsonObject();
	}
	
	protected JSONObject toJsonObject(Employee e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", EMPLOYEE+SPLIT+e.getId());
		record.put("text", e.getName());
		record.put("iconCls","employee");
		record.put("type","employee");
		record.put("leaf",true);
		if(p("checkBox") != null && !p("checkBox").equals("false")){
			record.put("checked",false);
		}
		return record.getJsonObject();
	}
	
	protected JSONObject toJsonObject(Organization e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", ORGANIZATION+SPLIT+e.getId());
		record.put("text", e.getShortName());
		record.put("iconCls", "organization");
		record.put("type", "organization");
		record.put("leaf",false);
		if(p("checkBox") != null && !p("checkBox").equals("false")){
			record.put("checked",false);
		}
		return record.getJsonObject();
	}
	
	/**
	 * 删除网点在班次批量设置中的错误排班
	 */
	public String deleteMistake()throws Exception{
		shiftConfigService.deleteMistakeShiftConfig();
		return render(jo);
	}
	
	
	/*=============== setter and getter =================*/
	
	@Override
	public GenericService<ShiftConfig> getDefService() {
		return shiftConfigService;
	}
	
	public void setShiftConfigService(ShiftConfigService shiftConfigService) {
		this.shiftConfigService = shiftConfigService;
	}

	public ShiftConfigService getShiftConfigService() {
		return shiftConfigService;
	}

	public void setEmployeeService(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	public EmployeeService getEmployeeService() {
		return employeeService;
	}

	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	public OrganizationService getOrganizationService() {
		return organizationService;
	}

}
