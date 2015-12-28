package com.fortunes.faas.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.fortunes.util.Tools;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.AttendanceLog;
import com.fortunes.faas.service.AttendanceLogService;
import com.fortunes.faas.vo.EmployeeClosure;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Log;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.LogService;
import com.opensymphony.xwork2.util.logging.Logger;

/**
 * 
 * @author Leo
 * @version 2011-4-27
 */
@Component
public class ClosureService {
	
	@Resource private JdbcTemplate closureJdbcTemplate;
	@Resource private JdbcTemplate jdbcTemplate;
	@Resource private EmployeeService employeeService;
	@Resource private AttendanceLogService attendanceLogService;
	@Resource private LogService logService;
	
	/**
	 *获取门襟系统中全部员工卡号
	 * @return
	 */
	
	@SuppressWarnings("unchecked")
	public List<EmployeeClosure> getClosureEmployees(){
		String SQL= "select u.id as id,u.first_name as code,u.last_name as name,u.ag_name as organizationName,c.code as card " +
				"from crdhld u,card c where u.id=c.owner";
		List<EmployeeClosure> list = closureJdbcTemplate.query(SQL.toString(),new EmployeeClosureMapper());
		return list;
	}
	
	/**
	 * 获取门襟系统数据库中在2012-01-01之后入职员工的卡号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<EmployeeClosure> getNewClosureEmployees(){
		String SQL= "select u.id as id,u.first_name as code,u.last_name as name,u.ag_name as organizationName,c.code as card " +
				"from crdhld u,card c where u.id=c.owner and u.id>=5580";
		List<EmployeeClosure> list = closureJdbcTemplate.query(SQL.toString(),new EmployeeClosureMapper());
		return list;
	}
	
	
	private static final class EmployeeClosureMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			EmployeeClosure employee = new EmployeeClosure();
			employee.setId(rs.getLong("id"));
			employee.setCode(rs.getString("code"));
			employee.setCard(rs.getString("card"));
			employee.setName(rs.getString("name"));
			employee.setOrganizationName(rs.getString("organizationName"));
	        return employee;
	    }
	}
	
	public int[] syncCard() throws Exception {
		final List<EmployeeClosure> empcs = getClosureEmployees();
		String updateSQL = "update Employee set card = ? where code = ?";
		int[] updateCounts = 
			jdbcTemplate.batchUpdate(
				updateSQL,
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, (empcs.get(i)).getCard());
                        ps.setString(2, (empcs.get(i)).getCode());
//                        ps.setString(3, (empcs.get(i)).getName());
                    }
                    public int getBatchSize() {
                        return empcs.size();
                    }
				}
			);
		return updateCounts;
	}
	
	public void syncCards() throws Exception {
		List<EmployeeClosure> empcs = getClosureEmployees();
		List<Employee> emps = employeeService.getAll();
		for(EmployeeClosure empc : empcs){
			for(Employee emp : emps){
				if(empc.getCode()!=null&&empc.getCode().equals(emp.getCode())){
					emp.setCard(CardHelper.cardNo2CardNo(empc.getCard()));
					employeeService.update(emp);
					break;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<AttendanceLog> getLogsOfDateMin(Date date) throws Exception {
		String fdate = Tools.date2Date(date);
		String selectSQL = "select Desc3 as employeeCode, date as checkInTime,from_Name as location"+
							" from log" +
							" where convert(varchar(10),date,121) = '"+fdate+"'";
		List<AttendanceLog> list = closureJdbcTemplate.query(selectSQL, new AttendanceLogMapper());
		return list;
	}
	public void syncLogsOfDate(Date date) throws Exception{
		List<AttendanceLog> attLogsMin = getLogsOfDateMin(date);
		List<Employee> emps = employeeService.getUnUploadEmployees();
		for(AttendanceLog attLog : attLogsMin){
			String code = attLog.getEmployeeCode().trim();
			String empCode = "";
			AttendanceLog att1 = null;
			for(Employee emp : emps){
				empCode = emp.getCode();
				if(empCode.equals(code)){
					att1 = new AttendanceLog();
					att1.setAttDate(date);
					att1.setEmployee(emp);
					att1.setCheckInTime(attLog.getCheckInTime());
					att1.setPushSite(attLog.getPushSite());
					attendanceLogService.add(att1);
				}
			}
		}
	}
	private static final class AttendanceLogMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			AttendanceLog attendanceLog = new AttendanceLog();
			String code = rs.getString("employeeCode");
			if(StringUtils.isNotEmpty(code)){
				String [] codeAndName = code.split(" ");
				int lenth = codeAndName.length;
				if(lenth>1){
					code = codeAndName[lenth-1];
				}
				
			}
			attendanceLog.setEmployeeCode(code);
			attendanceLog.setCheckInTime(Tools.string2Date(rs.getString("checkInTime")));
			attendanceLog.setPushSite(rs.getString("location"));
//			attendanceLog.setCheckOutTime(Tools.string2Date(rs.getString("checkOutTime")));
	        return attendanceLog;
	    }
	}

}
