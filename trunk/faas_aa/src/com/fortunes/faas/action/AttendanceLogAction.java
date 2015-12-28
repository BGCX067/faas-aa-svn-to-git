package com.fortunes.faas.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.fortunes.core.Helper;
import net.fortunes.core.ListData;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.compass.core.json.JsonObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.AttShift;
import com.fortunes.faas.model.AttendanceLog;
import com.fortunes.faas.service.AttShiftService;
import com.fortunes.faas.service.AttendanceLogService;
import com.fortunes.faas.service.AttendancePlusService;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.OrganizationService;
import com.fortunes.fjdp.admin.service.UserService;

@Component @Scope("prototype")
public class AttendanceLogAction extends GenericAction<AttendanceLog> {
	
	private AttendanceLogService attendanceLogService;
	private EmployeeService employeeService;
	@Resource private AttShiftService attShiftService;
	@Resource private UserService userService;
	private String organizationCode = "";
	@Override
	public String create() throws Exception {
		Date checkInDate = AppHelper.toDate(p("checkInDate"));
		Date endCheckInDate = AppHelper.toDate(p("endCheckInDate"));
		if(endCheckInDate.compareTo(checkInDate)<0){
			setJsonMessage(true, "请设置正确的补刷时间!");
			return render(jo);
		}
		Calendar start = Calendar.getInstance();
		start.setTime(checkInDate);
		
		Calendar end = Calendar.getInstance();
		end.setTime(endCheckInDate);
		String employeeId = p("employee");
		Employee e = employeeService.get(employeeId);
		while (end.compareTo(start)>=0) {
			Date startDate = start.getTime();
			AttShift attshift = attShiftService.getAttShiftByDateAndId(employeeId,startDate);
			if(!attshift.isAffirm()){
				AttendanceLog log = new AttendanceLog();
				log.setEmployee(AppHelper.toEmployee(p("employee")));
				log.setAttDate(startDate);
				log.setCheckInTime(AppHelper.toDate(AdminHelper.toDateString(startDate)+" "+p("checkInTime")+":00"));
				Employee loginedEmployee = AppHelper.getUser().getEmployee();
				log.setPlusCardMan(loginedEmployee);
				log.setReason(p("reason"));
				attendanceLogService.add(log);
//				super.create();
				if(getBeforeOrLater(start)){
					AttShift attshift1 = attShiftService.getAttShiftByDateAndId(employeeId,startDate);
					if(attshift1.getId()==0){
						setJsonMessage(true, "成功补签");
					}else{
						List<AttShift> list = new ArrayList<AttShift>();
						list.add(attshift1);
						attShiftService.delDayAttShift(list, startDate);
						attShiftService.mendCard(employeeId, startDate);
						setJsonMessage(true, "成功补签!");
					}
				}
			}else{
				setJsonMessage(true,e.toString()+startDate+"的考勤信息以确认，不可更改");
			}
			start.add(Calendar.DATE, 1);
		}
		
		return render(jo);
	}
	public boolean getBeforeOrLater(Calendar CDate) throws ParseException{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String as = df.format(new Date());
		Date now = df.parse(as);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		if(CDate.compareTo(calendar)<=0){
			return true;//如果是当天或当天以前则返回true
		}else{
			return false;
		}
	}
	@Override
	public String list() throws Exception {
		organizationCode = (String) this.getSessionMap().get(Helper.ORGANIZATION_CODE);
		this.getSessionMap().get(Helper.ORGANIZATION_ID);
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
//		queryMap.put("isVer", p("isVer"));
//		queryMap.put("notWell", p("notWell"));
		return super.list();
	};
	
	protected void setEntity(AttendanceLog e) throws ParseException{
		e.setEmployee(AppHelper.toEmployee(p("employee")));
		e.setAttDate(AppHelper.toDate(p("checkInDate")));
		e.setCheckInTime(AppHelper.toDate(p("checkInDate")+" "+p("checkInTime")+":00"));
		Employee loginedEmployee = AppHelper.getUser().getEmployee();
		e.setPlusCardMan(loginedEmployee);
		e.setReason(p("reason"));
	}
	
	protected JSONObject toJsonObject(AttendanceLog e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("cardNo", e.getEmployee().getCard());
		record.put("employeeName", e.getEmployee().getName());
		record.put("employeeCode", e.getEmployee().getCode());
		record.put("attDate", e.getAttDate());
		record.put("checkInTime", Tools.date2String(e.getCheckInTime()));
		record.put("plusCardMan", e.getPlusCardMan());
		record.put("reason", e.getReason());
		if(StringUtils.isNotEmpty(organizationCode) && organizationCode.equals("HR")){
			record.put("pushSite", e.getPushSite());
		}
		return record.getJsonObject();
	}
	
	
	/*=============== setter and getter =================*/
	
	@Override
	public GenericService<AttendanceLog> getDefService() {
		return attendanceLogService;
	}
	
	public void setAttendanceLogService(AttendanceLogService attendanceLogService) {
		this.attendanceLogService = attendanceLogService;
	}

	public AttendanceLogService getAttendanceLogService() {
		return attendanceLogService;
	}

	public void setEmployeeService(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	public EmployeeService getEmployeeService() {
		return employeeService;
	}

}
