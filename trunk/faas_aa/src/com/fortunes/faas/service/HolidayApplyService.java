package com.fortunes.faas.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import net.fortunes.core.ListData;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Component;
import com.fortunes.faas.model.HolidayApply;
import com.fortunes.faas.model.HolidayApply.ApplyStatus;
import com.fortunes.faas.util.LeaveHelper;
import com.fortunes.faas.vo.HolidayCountVO;
import com.fortunes.fjdp.Constants;
import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.RoleService;
import com.fortunes.fjdp.admin.service.UserService;
@Component
public class HolidayApplyService extends GenericService<HolidayApply> {
	
	@Resource private RoleService roleService;
	@Resource private AttShiftService attShiftService;
	@Resource private UserService userService;
	@Resource private AttendancePlusService attendancePlusService;
	@Resource private EmployeeService employeeService;
		
	@Override
	protected DetachedCriteria getConditions(String query,
			Map<String, String> queryMap) {
		DetachedCriteria critria = DetachedCriteria.forClass(HolidayApply.class);
		// TODO Auto-generated method stub
		critria.addOrder(Order.desc("id"));
		return critria;
	}
	
	/**
	 * 我的任务列表
	 * @param user 登陆用户
	 * @param applyStatus 审批状态
	 * @param start
	 * @param limit
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ListData<HolidayApply> myTaskList(User user,ApplyStatus applyStatus,String employeeId,String sSdate,String eSdate,int start,int limit,String applyType,String leaveType){
//		String sSdate = Tools.date2String(sDate);
//		String eSdate = Tools.date2String(eDate);
		StringBuffer selectHQL = new StringBuffer(" from HolidayApply ha where ha.applyStatus = '").append(applyStatus.name()).append("'")
		.append(" and ha.currentApprove.id = '").append(user.getEmployee().getId()).append("'");
		if(employeeId!=null&&!employeeId.equals("")){
			selectHQL.append(" and ha.leaveApply.id = '").append(employeeId).append("'");
		}
		if(applyType.equals("holiday")){
			returnStringB(selectHQL, sSdate, eSdate);
			selectHQL.append(" and ha.applyType='holiday'");
			if(StringUtils.isNotEmpty(leaveType)){
				selectHQL.append(" and ha.leaveType.id='").append(leaveType).append("'");
			}
		}else{
			returnStringB(selectHQL, sSdate, eSdate);
		}
		List list = getDefDao().findByQueryString("select count(*) "+selectHQL);
		int total = Integer.parseInt(list.get(0).toString());
		List<HolidayApply> has = getDefDao().findByQueryString("select ha "+selectHQL.toString(), start,limit);
		return new ListData(has,total);
	}
	/**
	 * 获取登陆用户的相关状态的请假单
	 * @param employee 登陆用户关联的员工对象
	 * @param applyStatus
	 * @param start
	 * @param limit
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ListData<HolidayApply> approveList(User user,ApplyStatus applyStatus,String employeeId,String sSdate,String eSdate,int start,int limit,String applyType,String leaveType){
		user = userService.getUserByAuthedUserId(user.getId());
		Employee employee = user.getEmployee();
		Role role = roleService.getRoleByCode(Role.SYSTEM_MANAGER);
		List<Role> roles = userService.getRolesByAuthedUserId(user.getId());
		if(roles.contains(role)){
//			List<Organization> organizations = user.getOrganizations();
			String[] organizations = userService.getUserOperateOrganizationToString();
			String orgStr = ""; int i =0;
			for(String o : organizations){
				if(i!=0&&i!=organizations.length){
					orgStr = orgStr+",";
				}
				orgStr = orgStr + o; i++;
			}
			StringBuffer hql = new StringBuffer(" where ha.applyStatus ='").append(applyStatus).append("'"); 
			hql.append(" and ha.leaveApply.organization.id in (").append(orgStr).append(")");
			if(employeeId!=""&&employeeId!=null){
				hql.append(" and ha.leaveApply.id = '").append(employeeId).append("'");
			}
			if(applyType.equals("holiday")){
				hql = returnStringB(hql, sSdate, eSdate);
				hql.append(" and ha.applyType='holiday'");
				if(StringUtils.isNotEmpty(leaveType)){
					hql.append(" and ha.leaveType.id='").append(leaveType).append("'");
				}
			}else{
				hql = returnStringB(hql, sSdate, eSdate);
				hql.append(" and ha.applyType='overTime'");
			}
			List list = getDefDao().findByQueryString("select count(*) from HolidayApply ha"+ hql.toString());
			int total = Integer.parseInt(list.get(0).toString());
			String hqlString ="select ha from HolidayApply ha "+hql.toString();
			List<HolidayApply> has = getDefDao().findByQueryString(hqlString,start,limit);
			return new ListData(has,total);
		}
		
		StringBuffer HQL = new StringBuffer(" where ha.applyStatus = ? and (ha.leaveApply=? or ha.operater =? or ha.id in (select hp.holidayApply.id from ApproveProcess hp where hp.leaveApprove=?))");
		if(employeeId!=""&&employeeId!=null){
			HQL.append(" and ha.leaveApply.id = '").append(employeeId).append("'");
		}
		if(applyType.equals("holiday")){
			HQL = returnStringB(HQL, sSdate, eSdate);
			HQL.append(" and ha.applyType='holiday'");
			if(StringUtils.isNotEmpty(leaveType)){
				HQL.append(" and ha.leaveType.id='").append(leaveType).append("'");
			}
		}else{
			HQL = returnStringB(HQL, sSdate, eSdate);
			HQL.append(" and ha.applyType='overTime')");
		}
//		 HQL.append(" or ((ha.applyDate between '").append(eSdate).append("' and '").append(sSdate).append("') and ha.applyType='overTime')");
		List list = getDefDao().findByQueryString("select count(*) from HolidayApply ha"+HQL.toString(),applyStatus,employee,employee,employee);
		int total = Integer.parseInt(list.get(0).toString());
		String hqlString ="select ha from HolidayApply ha "+HQL.toString();
		List<HolidayApply> has = getDefDao().findByQueryString(hqlString,start,limit,applyStatus,employee,employee,employee);
		return new ListData(has,total);
	}
	
	public StringBuffer returnStringB(StringBuffer HQL ,String sSdate,String eSdate){
		if(StringUtils.isNotEmpty(sSdate) && StringUtils.isNotEmpty(eSdate)){
			 HQL.append(" and ((ha.applyDate between '").append(eSdate).append("' and '").append(sSdate).append("')");
		}
		if(StringUtils.isNotEmpty(sSdate)&&eSdate==null){
			HQL.append(" and ((ha.applyDate >= '").append(sSdate).append("')");
		}
		if(sSdate == null&&StringUtils.isNotEmpty(eSdate)){
			HQL.append(" and ((ha.applyDate <= '").append(eSdate).append("')");
		}
		return HQL;
	}
	/**
	 * 按照流程审批请假单
	 * @param ha 请假单对象
	 * @throws Exception
	 */
	public void holidayApprove(HolidayApply ha) throws Exception {
		Employee nextApprove = null;
		int node = ha.getNode();
		node++;
		if(node==2){
			nextApprove = ha.getLeaveApprove2();
		}
		else if(node==3){
			nextApprove = ha.getLeaveApprove3();
		}
		else if(node==4){
			nextApprove = ha.getLeaveApprove4();
		}
		if(null==nextApprove){
			if(ha.getCertigier()!=null && node<=5){
				node = 5;
				nextApprove = ha.getCertigier();
			}else if(ha.getBossAffirm()!=null && node<=6){
				node = 6;
				nextApprove = ha.getBossAffirm();
			}else{
				ha.setApplyStatus(ApplyStatus.FINISHED);
			}
		}
		ha.setNode(node);
		ha.setCurrentApprove(nextApprove);
		update(ha);
		if(nextApprove==null){
			if(ha.getApplyType().equals("overTime")){
				analyseWorkOverTime(ha);
			}else{
				Calendar start = Calendar.getInstance();
				start.setTime(ha.getStartDate());
				Calendar end = Calendar.getInstance();
				end.setTime(ha.getEndDate());
				attendancePlusService.createPlusInfo(start, end, Long.toString(ha.getLeaveApply().getId()), "ALL_DAY", "LEAVE", ha.getLeaveType().getId(), null, null);
				analyseHoliday(ha);
				if(ha.getApplyStatus().name().equals("FINISHED")&&ha.getLeaveType().getId().equals("leaveType_03_1")){
					Employee e = employeeService.get(Long.toString(ha.getLeaveApply().getId()));
					e.setAnnualLeave(e.getAnnualLeave()-ha.getLeaveDays());
					employeeService.update(e);
				}
			}
		}
	}
	
	public void analyseWorkOverTime(HolidayApply ha) throws Exception{
		Calendar start = Calendar.getInstance();
		start.setTime(ha.getWorkOverTime());
		Calendar end = Calendar.getInstance();
		end.setTime(ha.getEWorkOverTimeDate());
		Date now = new Date();
		while(end.compareTo(start)>=0){
			if(now.compareTo(start.getTime())>0){
				String employeeId = Long.toString(ha.getLeaveApply().getId());
				attShiftService.delDayAndEmployee(employeeId,start.getTime());
				attShiftService.mendCard(employeeId, start.getTime());
			}
			start.add(Calendar.DATE, 1);
		}
	}
	
	public void analyseHoliday(HolidayApply ha) throws Exception{
		Calendar start = Calendar.getInstance();
		start.setTime(ha.getStartDate());
		Calendar end = Calendar.getInstance();
		end.setTime(ha.getEndDate());
		String employeeId = Long.toString(ha.getLeaveApply().getId());
		while (end.compareTo(start)>0) {
			Date sDate = start.getTime();
			Date now = new Date();
			if(now.compareTo(sDate)>0){
				attShiftService.delDayAndEmployee(employeeId,start.getTime());
				attShiftService.mendCard(employeeId, start.getTime());
			}
			start.add(Calendar.DATE, 1);
		}
	}
	
	
	public void unPassApprove(HolidayApply ha) throws Exception {
		ha.setApplyStatus(ApplyStatus.FAILED);
		update(ha);
	}
	
	/**
	 * 返回这个月内所有请假记录。(1) start<3.1 and 3.31>end>3.1; (2) start>3.1 and end<3.31; (3) 3.1<start<3.31 and end >3.31
	 * @param date
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<HolidayApply> getHolidayApplys(Date monthFirst,Date monthLast){
		String querySQL = "select ha from HolidayApply ha where ha.applyStatus = 'FINISHED' and ((ha.startDate<= ? and ?<=ha.endDate and ha.endDate<= ?) or (ha.startDate<=? and ha.endDate>=?)"
			+" or (ha.startDate>=? and ha.endDate<=?) or (?<=ha.startDate and ha.startDate<=? and ha.endDate>=?))";
		List<HolidayApply> list = getDefDao().findByQueryString(querySQL, monthFirst,monthFirst,monthLast,monthFirst,monthLast,monthFirst,monthLast,monthFirst,monthLast,monthLast);
		return list;
	}

	
	public List<HolidayApply> getYearHolidayApplys(Date startDate,Date EndDate,Map<String, String> queryMap){
		StringBuffer querySQL = new StringBuffer("select ha.leaveApply,ha.leaveType,ha.startDate,ha.endDate,ha.applyReason from HolidayApply ha where ha.startDate between ");
			querySQL.append(startDate).append(" and ").append(EndDate).append(" or ha.endDate between ").append(startDate).append(" and ").append(EndDate);
			String searchType = queryMap.get("searchType");
			if(searchType.equals("employee")&&StringUtils.isNotEmpty(queryMap.get("employee"))){
				querySQL.append(" and ha.leaveApply.id = ").append(Long.parseLong(queryMap.get("employee")));
			}
			else if(StringUtils.isNotEmpty(queryMap.get("organizations"))){
				querySQL.append(" and ha.leaveApply.organization IN (").append(queryMap.get("organizations")).append(")");
			}
			querySQL.append(" GROUP BY ha.leaveApply.id");
			List<HolidayApply> list = getDefDao().findByQueryString(querySQL.toString());
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<HolidayApply> getHolidayApplysByEmployee(Date startDate,Date endDate,String employee){
		StringBuffer querySQL = new StringBuffer("select new HolidayApply(ha.id,ha.leaveType.id,ha.leaveType.text,ha.startDate,ha.endDate,ha.applyReason) from HolidayApply ha where (ha.startDate between ?");
		querySQL.append(" and ?").append(" or ha.endDate between ?").append(" and ?)")
		.append(" and ha.leaveApply.id = ?");
		List<HolidayApply> list = getDefDao().findByQueryString(querySQL.toString(),startDate,endDate,startDate,endDate, Long.parseLong(employee));
		return list;
	}
	
	public List<HolidayCountVO> getLeaveTotal (String startDate,String endDate){
		StringBuffer hql = new StringBuffer("select new com.fortunes.faas.vo.HolidayCountVO(ha.leaveApply.id,ha.leaveApply.code,sum(ha.leaveDays)) from HolidayApply ha where ((ha.startDate between '").append(startDate);
		hql.append("' and '").append(endDate).append("') or (ha.endDate between '").append(startDate).append("' and '").append(endDate).append("')) and ha.applyStatus='FINISHED' group by ha.leaveApply.id,ha.leaveApply.code having sum(ha.leaveDays)>0");
		List<HolidayCountVO> count = getDefDao().findByQueryString(hql.toString());
		return count;
	}
	
	
	public List<HolidayApply> getHolidayApplyCount(Date startDate,Date endDate){
		String hql = "select ha from HolidayApply ha where ha.applyStatus='FINISHED' and ha.leaveType.id = 'leaveType_03_1' and (ha.startDate between ? and ? or ha.endDate between ? and ?)";
		List<HolidayApply> count = getDefDao().findByQueryString(hql.toString(),startDate,endDate,startDate,endDate);
		return count;
	}
	/**
	 * 
	 * @param list
	 * @param employeeId
	 * @param beforeYear去年年末的那一天
	 * @param lastYear今年年末的那一天
	 * @return
	 * @throws Exception
	 */
	public HolidayCountVO analyseYearHoliday(List<HolidayApply> list ,long employeeId,Date beforeYear,Date lastYear) throws Exception{
//		Date before = Tools.stringToDate(beforeYear+"-12-31");
//		Date last = Tools.stringToDate(lastYear+"01-01");
//		List<HolidayCountVO> count = new ArrayList<HolidayCountVO>();
		HolidayCountVO hcv = new HolidayCountVO();
		hcv.setEmployeeId(employeeId);
		double day = 0;
		for(HolidayApply ha : list){
			if(ha.getLeaveApply().getId()==employeeId){
				if(beforeYear.compareTo(ha.getStartDate())>0&&ha.getEndDate().compareTo(beforeYear)>0){
					day = day+(int) ((beforeYear.getTime()-ha.getStartDate().getTime())/1000/60/60/24);
				}else if(ha.getEndDate().compareTo(lastYear)>0&&lastYear.compareTo(ha.getStartDate())>0){
					day = day+(int) ((ha.getEndDate().getTime()-lastYear.getTime())/1000/60/60/24);
				}else{
					day = day+ha.getCancelDays();
				}
			}
		}
		hcv.setCount(day);
		return hcv;
	}
	
	
	public static void main(String[] args) throws ParseException {
		Date s = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Date e = sf.parse("2012-04-24");
		long st = s.getTime();
		long et = e.getTime();
		long lon = st-et;
		System.out.println(lon/1000/60/60/24);
	}
	
	public HolidayApply getHolidayApplyByDateAndEmployee(Date date,Long employeeId){
		String hql = "select new HolidayApply(ha.id,ha.leaveType.id,ha.leaveType.text,ha.leaveDays,ha.startDate,ha.endDate,ha.cancelDate,ha.leaveApply.id,ha.applyType) from HolidayApply ha where ha.leaveApply.id=? and ha.applyStatus='FINISHED' and (? between ha.startDate and ha.endDate)";
		List<HolidayApply> list= new ArrayList<HolidayApply>();
		try{
			list = getDefDao().findByQueryString(hql,employeeId,date);
			if(list.size()==0){
				String HQL = "select new HolidayApply(ha.id,ha.workOverTime,ha.overTimeStart,ha.overTimeEnd,ha.applyType) from HolidayApply ha where ha.leaveApply.id = ? and ha.applyStatus = 'FINISHED' and ? between ha.workOverTime and ha.eWorkOverTimeDate";
				list = getDefDao().findByQueryString(HQL,employeeId,date);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list.size()>0?list.get(0):null;
	}
}
