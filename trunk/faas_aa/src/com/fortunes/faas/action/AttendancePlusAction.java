package com.fortunes.faas.action;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.swing.GroupLayout.Alignment;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.AttShift;
import com.fortunes.faas.model.AttendancePlus;
import com.fortunes.faas.model.AttendancePlus.AttendancePlusType;
import com.fortunes.faas.model.AttendancePlus.TimeRange;
import com.fortunes.faas.service.AttShiftService;
import com.fortunes.faas.service.AttendancePlusService;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.OrganizationService;
import com.fortunes.fjdp.admin.service.UserService;

import net.fortunes.core.Helper;
import net.fortunes.core.action.BaseAction;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component @Scope("prototype")
public class AttendancePlusAction extends GenericAction<AttendancePlus> {
	
	
	private static final String NONE = "NONE";
	private static final String OVERTIME = "OVERTIME";
	private static final String OTHER = "OTHER";
	private static final String SPLIT = "-";
	private static final String EMPLOYEE = "e";
	private static final String ORGANIZATION = "o";
	private static final String FIRST = "f";
	
	private AttendancePlusService attendancePlusService;
	private OrganizationService organizationService;
	private EmployeeService employeeService;
	@Resource private UserService userService;
	
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
		}else if (type.startsWith(ORGANIZATION)){
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
	
	public String createPlusInfo() throws Exception{
		Date startDate = AppHelper.toDate(p("startDate"));
		Date endDate = AppHelper.toDate(p("endDate"));
		
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		
		String bo = "";
		
		String type = p("attendancePlusType");
		if(type.equals(NONE)){
			attendancePlusService.clearPlusInfo(start,p("employeeId"));
		}else{
			if(type.equals(OVERTIME)){
				String overTimeStart = p("overTimeStart");
				String overTimeEnd = p("overTimeEnd");
				if(overTimeStart==null||overTimeStart == ""||overTimeEnd == null || overTimeEnd == ""){
					setJsonMessage(false, "请选择正确的加班时间");
					return render(jo);
				}else{
					bo = attendancePlusService.createPlusInfo(start,end,
							p("employeeId"),p("timeRange"),p("attendancePlusType"),null,overTimeStart,overTimeEnd);
				}
			}else{
				if(p("attendancePlusType").equals("LEAVE")){
					if(StringUtils.isNotEmpty(p("leaveType"))){
						bo = attendancePlusService.createPlusInfo(start,end,
								p("employeeId"),p("timeRange"),p("attendancePlusType"),p("leaveType"),null,null);
					}else{
						setJsonMessage(false, "请选择请假类型");
						return render(jo);
					}
					
				}else{
					bo = attendancePlusService.createPlusInfo(start,end,
							p("employeeId"),p("timeRange"),p("attendancePlusType"),p("reason"),null,null);
				}
			}
		}
		setJsonMessage(true,bo==""?"成功添加增补信息":bo.substring(0, bo.length()-1)+"日考勤信息已确认,不能更改");
		return render(jo);
	}
	
	public String getInfos() throws Exception{
		List<AttendancePlus> list = attendancePlusService.getPlusInfosByEmployee(
				p("employeeId"),AppHelper.toDate(p("startDate")),AppHelper.toDate(p("endDate")));
		
		JSONArray ja = new JSONArray();
		for(AttendancePlus info:list){
			ja.add(toJsonObject(info));
		}
		jo.put(DATA_KEY, ja);
		return render(jo);
	}
	public String createPlusInfos()throws Exception{
		Date startDate = AppHelper.toDate(p("startDate"));
		Date endDate = AppHelper.toDate(p("endDate"));
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		String[] employeeIds = p("employeeIds").split(",");	
		String type = p("attendancePlusType");
		for(String employeeId : employeeIds){
			Calendar start = Calendar.getInstance();
			start.setTime(startDate);
			if(type.equals(NONE)){
				attendancePlusService.clearPlusInfo(start,employeeId);
			}else{
				if(type.equals(OVERTIME)){
					String overTimeStart = p("overTimeStart");
					String overTimeEnd = p("overTimeEnd");
					if(overTimeStart==null||overTimeStart == ""||overTimeEnd == null || overTimeEnd == ""){
						setJsonMessage(false, "请选择正确的加班时间");
						return render(jo);
					}else{
						attendancePlusService.createPlusInfo(start,end,
								employeeId,p("timeRange"),p("attendancePlusType"),null,overTimeStart,overTimeEnd);
					}
				}else{
					if(p("attendancePlusType").equals("LEAVE")){
						if(StringUtils.isNotEmpty(p("leaveType"))){
							attendancePlusService.createPlusInfo(start,end,
									employeeId,p("timeRange"),p("attendancePlusType"),p("leaveType"),null,null);
						}else{
							setJsonMessage(false, "请选择请假类型");
							return render(jo);
						}
						
					}else{
						attendancePlusService.createPlusInfo(start,end,
								employeeId,p("timeRange"),p("attendancePlusType"),p("reason"),null,null);
					}
				}
			}
		}
		setJsonMessage(true,"成功添加增补信息");
		return render(jo);
	}
	
	protected JSONObject toJsonObject(Employee e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", EMPLOYEE+SPLIT+e.getId());
		record.put("text", e.getName()+"<span style='color:blue'>("+e.getOrganization().getShortName()+")</span>");
		record.put("iconCls","employee");
		record.put("type","employee");
		record.put("leaf",true);
		//record.put("checked",false);
		return record.getJsonObject();
	}
	
	protected JSONObject toJsonObject(Organization e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", ORGANIZATION+SPLIT+e.getId());
		record.put("text", e.getShortName());
		record.put("iconCls", "organization");
		record.put("type", "organization");
		record.put("leaf",false);
		//record.put("checked",false);
		return record.getJsonObject();
	}
	protected void setEntity(AttendancePlus e) throws ParseException{
		e.setDate(AppHelper.toDate(p("date")));
		e.setType(AttendancePlusType.valueOf(p("type")));
	}
	
	protected JSONObject toJsonObject(AttendancePlus e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("date", e.getDate());
		//record.put("timeRange", e.getTimeRange().name());
		if(e.getType().equals(OTHER)){
			record.put("type", e.getReason());
		}else{
			record.put("type", e.getType().name());
		}
		return record.getJsonObject();
	}
	
	
	/*=============== setter and getter =================*/
	
	@Override
	public GenericService<AttendancePlus> getDefService() {
		return attendancePlusService;
	}
	
	public void setAttendancePlusService(AttendancePlusService attendancePlusService) {
		this.attendancePlusService = attendancePlusService;
	}

	public AttendancePlusService getAttendancePlusService() {
		return attendancePlusService;
	}

	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	public OrganizationService getOrganizationService() {
		return organizationService;
	}

	public void setEmployeeService(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	public EmployeeService getEmployeeService() {
		return employeeService;
	}

}
