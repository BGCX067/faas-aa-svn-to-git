package com.fortunes.fjdp.admin.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.fortunes.faas.vo.AttShifts;
import com.fortunes.fjdp.Constants;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.Privilege;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;

import net.fortunes.core.Helper;
import net.fortunes.core.ListData;
import net.fortunes.core.log.annotation.LoggerClass;
import net.fortunes.core.service.GenericService;

@Component
@LoggerClass
public class UserService extends GenericService<User>{
	private static Map<String,Integer> userLoginStatus = new HashMap<String,Integer>();
	
	protected DetachedCriteria getConditions(String query,Map<String, String> queryMap) {
		DetachedCriteria criteria = super.getConditions(query, queryMap);
		criteria.createAlias("roles", "rs");
		if(query !=  null){
			criteria.add(Restrictions.or(
					Restrictions.ilike("name", query, MatchMode.ANYWHERE), 
					Restrictions.ilike("displayName", query, MatchMode.ANYWHERE)
			));
		}
		if(queryMap != null && StringUtils.isNotEmpty(queryMap.get("roleId"))){
			if(StringUtils.isNotEmpty(queryMap.get("type"))){
				criteria.add(Restrictions.ne("rs.id", Long.parseLong(queryMap.get("roleId"))));
			}else{
				criteria.add(Restrictions.eq("rs.id", Long.parseLong(queryMap.get("roleId"))));
			}
		}
		return criteria;
	}
	
	public boolean lockOrUnlockUser(String userId){
		User user = get(userId);
		user.setLocked(user.isLocked()? false : true);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getOnlineUsers(){
		Set<String> names = userLoginStatus.keySet();
		int i = 0 ;
		int len = names.size();
		StringBuffer HQL = new StringBuffer("from User as u where u.name in (");
		if(0==len){
			return null;
		}
		for(String name : names){
			i++;
			if(1 == len){
				HQL.append("'").append(name).append("'");
			}
			else{
				HQL.append("'").append(name).append("'");
			}
			if (i != len) {
				HQL.append(",");
			}
		}	
		HQL.append(")");
		return getDefDao().findByQueryString(HQL.toString());
		//return getDefDao().findByQueryString("from User as u where u.loginSession.logined = true");
	}
	
	public boolean resetPassword(String userId,String password){
		User user = get(userId);
		user.setPassword(password);
		return true;
	}
	
	public void updateLoginSession(User user, int logined){
		user.getLoginSession().setLastLoginTime(new Date());
		update(user);
		if(Constants.USER_STATUS_LOGOUT == logined){
			userLoginStatus.remove(user.getName());
			return;
		}
		userLoginStatus.put(user.getName(), logined);
	}
	
	public void addUserList(List<User> list){
		for(User user : list){
			this.getDefDao().add(user);
		}
	}
	
	/** 验证用户
	 * @param user
	 * @return
	 */
	
	@SuppressWarnings("unchecked")
	public User authUser(User user){
		List<User> userList = getDefDao().findByQueryString(
				"from User as u where u.name = ? and u.password = ? and locked = ?", 
				user.getName(),user.getPassword(),false);
		if(userList.size() == 1){
			return userList.get(0);
		}else{
			return null;
		}
	}
	
	public Long[] getUserOperateOrganization(){
		User user = Helper.getUser();
		String hql = "select o.id from User u right join u.organizations as o where u.id=?";
		List<Object> users = this.getDefDao().findByQueryString(hql,Long.parseLong(user.getId())); 
//		List<Organization> list = users.get(0).getOrganizations();
		Long [] orgArray = new Long[users.size()];
		for (int j = 0; j < orgArray.length; j++) {
			orgArray[j] = Long.parseLong(users.get(j).toString());
		}
		return orgArray;
	}
	
	public String[] getUserOperateOrganizationToString(){
		User user = Helper.getUser();
		String hql = "select o.id from User u right join u.organizations as o where u.id=?";
		List<Object> users = this.getDefDao().findByQueryString(hql,Long.parseLong(user.getId())); 
//		this.getDefDao().refresh(user);
//		List<Organization> list = user.getOrganizations();
		String [] orgArray = new String[users.size()];
		for (int j = 0; j < orgArray.length; j++) {
			orgArray[j] = users.get(j).toString();
		}
		return orgArray;
	}
	public User getUserByAuthedUserId(String userId){
		String hql = "select new User(u.id,u.name,u.employee.id) from User u where u.id=?";
		List<User> users = this.getDefDao().findByQueryString(hql, Long.parseLong(userId));
		return users.get(0);
	}
	public List<Role> getRolesByAuthedUserId(String usersId){
		String hql = "select r.id,r.code from User u right join u.roles as r where u.id = ?";
		List<Object> rs = this.getDefDao().findByQueryString(hql, Long.parseLong(usersId));
		List<Role> roles = new ArrayList<Role>();
		for(int i= 0 ;i<rs.size();i++){
			Object [] obj = (Object[]) rs.get(i);
			Role role = new Role(Long.parseLong(obj[0].toString()),obj[1]==null?null:obj[1].toString());
			roles.add(role);
		}
		return roles;
	}
	
	
	public List<Privilege> getPrivileges(User authedUser){
		List<Privilege> userPrivileges = new ArrayList<Privilege>();
		List<Role> roles=authedUser.getRoles();
		for(Role role : roles){
			for(Privilege p:role.getPrivileges()){
				userPrivileges.add(p);
			}
		}
		return userPrivileges;
	}
	
	public boolean checkUserName(String userName, String userId){
		String hql = "select t from User t where t.name=?";
		User user = (User)get(userId);
		List<User> userList = getDefDao().findByQueryString(hql, new Object[] {userName});
		if (userList.size() > 0){
			if (userId != null)
				return ((User)userList.get(0)).getId().equals(user.getId());
			else
				return false;
		}else{
			return true;
		}
	}
	
	//添加显示用户信息的方法（为导出用户信息做准备的）
	public ListData<User> userStatistics(Map<String,String> queryMap) throws Exception{
		StringBuffer HQL = new StringBuffer();
		StringBuffer SELETHQL = new StringBuffer();
		SELETHQL.append("SELECT new com.fortunes.fjdp.admin.model.User(u.name AS name,u.displayName AS displayName,u.employee AS employeeName")
				.append(",a.employee.salary as salary,(CASE WHEN a.employee.organization IS NULL THEN '' ELSE a.employee.organization.name END) AS deptName")
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
				.append(",sum(a.holiday) AS holidays")
				.append(",sum(CASE WHEN a.exception = 'TRAVEL' THEN exceptionTime ELSE 0 END) AS travel")
				.append(",sum(CASE WHEN a.exception = 'OVERTIME' THEN exceptionTime ELSE 0 END) AS overTime)");
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
		String GROUPBY = " GROUP BY a.employee.id";
		int total = 0;
//		if(limit>0){
			List list = getDefDao().findByQueryString(SELECTCOUNT.toString()+HQL.toString());
			total = Integer.parseInt(list.get(0).toString());
//		}
		List<User> users = getDefDao().findByQueryString(SELETHQL.append(HQL).append(GROUPBY).toString());
		return new ListData<User>(users,total);
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getUsersByPrivilegeCode(String privilegeCode){
		return getDefDao().findByQueryString(
				"select u from User as u join u.roles as r join r.privileges as p" +
				" where p.code = '"+privilegeCode+"'");
	}

	public static Map<String, Integer> getUserLoginStatus(){
		return userLoginStatus;
	}

	public static void setUserLoginStatus(HashMap<String, Integer> userLoginStatus) {
		UserService.userLoginStatus = userLoginStatus;
	}
	
	public void clearEmployeeOperated(String tuserId){
		JdbcTemplate jdbcTemplate = this.getDefDao().getJdbcTemplate();
		String sql = "delete from tuser_organization where Tuser_id ="+Long.parseLong(tuserId);
		jdbcTemplate.execute(sql);
	}
}
