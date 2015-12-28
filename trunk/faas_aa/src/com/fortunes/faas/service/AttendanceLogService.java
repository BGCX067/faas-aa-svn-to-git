package com.fortunes.faas.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fortunes.faas.model.AttendanceLog;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.service.UserService;

import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.tool.hbm2x.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class AttendanceLogService extends GenericService<AttendanceLog> {
	@Resource UserService userService;
	
	public boolean addAttendanceLogs(List<AttendanceLog> list) throws Exception{
		for(AttendanceLog a:list){
			try {
				add(a);
			} catch (Exception e) {
				throw e;
			}
		}
		return true;
	}

	@Override
	protected DetachedCriteria getConditions(String query,
			Map<String, String> queryMap) {
		DetachedCriteria criteria = super.getConditions(query, queryMap);
		
		if(queryMap != null && StringUtils.isNotEmpty(queryMap.get("employee"))){
			criteria.add(Restrictions.eq("employee.id", Long.parseLong(queryMap.get("employee"))));
		}
		if(queryMap != null && StringUtils.isNotEmpty(queryMap.get("organizations"))){
			criteria.createAlias("employee", "e");
			criteria.createAlias("e.organization", "o");
			String[] organizations = queryMap.get("organizations").split(",");
			List<Long> ids = new ArrayList<Long>();
			for(String o:organizations){
				ids.add(Long.parseLong(o));
			}
			criteria.add(Restrictions.in("o.id", ids));
		}
		if(queryMap != null && StringUtils.isNotEmpty(queryMap.get("beginDate"))){
			criteria.add(Restrictions.ge("attDate", Tools.string2Date(queryMap.get("beginDate"))));
		}
		if(queryMap != null && StringUtils.isNotEmpty(queryMap.get("endDate"))){
			criteria.add(Restrictions.le("attDate", Tools.string2Date(queryMap.get("endDate"))));
		}
		Order.desc("attDate");
		return criteria;
	}

		
	/**
	 * 查询某一天的全部考勤记录
	 * @param date yyyy-mm-dd
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AttendanceLog> getAttendanceLogs(Date date){
		JdbcTemplate jdbcTemplate = getDefDao().getJdbcTemplate();
		StringBuffer SQL= new StringBuffer("SELECT a.employee_id AS employeeCode,min(a.checkInTime) AS checkInTime,max(a.checkInTime) AS checkOutTime");
		SQL.append(" FROM attendanceLog AS a WHERE a.attDate=? GROUP BY a.employee_id");
		List<AttendanceLog> list = jdbcTemplate.query(SQL.toString(), new Object[]{date}, new AttendanceLogMapper());
		return list;
	}
	
	
	private static final class AttendanceLogMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			AttendanceLog att = new AttendanceLog();
			att.setEmployeeCode(rs.getString("employeeCode"));
	        att.setCheckInTime(Tools.string2Date(rs.getString("checkInTime")));
	        att.setCheckOutTime(Tools.string2Date(rs.getString("checkOutTime")));
	        return att;
	    }
	}
	
	@SuppressWarnings("unchecked")
	public AttendanceLog getAttendanceLogs(String employeeId,Date date){
		JdbcTemplate jdbcTemplate = getDefDao().getJdbcTemplate();
		String HQL = "SELECT a.employee_id AS employeeCode,min(A.checkInTime) AS checkInTime,max(A.checkInTime) AS checkOutTime" +
				" FROM attendanceLog AS a WHERE a.attDate = ? AND a.employee_id = ? GROUP BY a.employee_id";
		List<AttendanceLog> list = jdbcTemplate.query(HQL, new Object[]{date,employeeId}, new AttendanceLogMapper());
		return null!=list&&list.size()>0?list.get(0):null;
	}

	@SuppressWarnings("unchecked")
	public AttendanceLog getAttendanceLog(String employeeId,Date startTime,Date endTime){
		JdbcTemplate jdbcTemplate = getDefDao().getJdbcTemplate();
		String HQL = "SELECT a.employee_id AS employeeCode,min(A.checkInTime) AS checkInTime,max(A.checkInTime) AS checkOutTime" +
				" FROM attendanceLog AS a WHERE a.employee_id = ? and a.checkInTime between ? AND ? GROUP BY a.employee_id";
		List<AttendanceLog> list = jdbcTemplate.query(HQL, new Object[]{employeeId,startTime,endTime}, new AttendanceLogMapper());
		return null!=list&&list.size()>0?list.get(0):null;
	}
	
}
