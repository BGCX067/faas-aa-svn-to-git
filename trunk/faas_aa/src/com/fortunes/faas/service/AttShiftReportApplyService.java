package com.fortunes.faas.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fortunes.faas.model.AttShiftReportApply;

import net.fortunes.core.service.GenericService;
@Component
public class AttShiftReportApplyService extends GenericService<AttShiftReportApply>{
	
	public AttShiftReportApply getAttshiftReportApply(String organizationIds,String yearMonth){
		String hql = "select a from AttShiftReportApply a where a.organization.id in ("+organizationIds+") and a.yearMonth=?";
		List<AttShiftReportApply> list = this.getDefDao().findByQueryString(hql,yearMonth);
		return list.size()>0?list.get(0):null;
	}
}
