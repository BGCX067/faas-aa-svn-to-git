package com.fortunes.faas.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.ApproveProcess;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;

import net.fortunes.core.service.GenericService;

/**
 * 
 * @author Leo
 * @version 2011-3-28
 */
@Component
public class ApproveProcessService extends GenericService<ApproveProcess>{
	public List<ApproveProcess> getProcessesByHolidayId(String holidayId){
		String hql = "select new ApproveProcess(a.id,a.holidayApply.id,a.leaveApprove.id,a.leaveApprove.name,a.leaveApprove.organization.id,a.leaveApprove.organization.name,a.certigier,a.approveDate,a.approveStatus,a.suggestion) from ApproveProcess a where a.holidayApply.id = ?";
		List<ApproveProcess> list = this.getDefDao().findByQueryString(hql, Long.parseLong(holidayId));
		return list;
	}
	public void delWrongProdess(String holidayId,Employee e){
		if(e!=null&&e.getId()!=0){
			String employeeId = AdminHelper.longToString(e.getId());
			JdbcTemplate jdbcTemplate = this.getDefDao().getJdbcTemplate();
			String sql = "delete from ApproveProcess where holidayApply_id = '"+holidayId+"' and leaveApprove_id = '"+employeeId+"'";
			jdbcTemplate.execute(sql);
		}
	}
}
