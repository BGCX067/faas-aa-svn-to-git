package com.fortunes.faas.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fortunes.faas.model.ShiftConfig;
import com.fortunes.faas.model.ShiftConfigBundle;
import com.fortunes.faas.model.ShiftConfigTemplate;

import net.fortunes.core.service.GenericService;

@Component
public class ShiftConfigTemplateService extends GenericService<ShiftConfigTemplate> {
	public List<ShiftConfigTemplate> getShiftConfigTemplates(int currentDayInWeek,long bundleId,long organizationId){
		
		List<ShiftConfigTemplate> list = this.getDefDao().findByQueryString(
				"from ShiftConfigTemplate sct where sct.shiftConfigBundle.id = ?",
				bundleId);
		return list;
	}
	
	public void delAllShiftConfigTemplate(long bundleId){
		String HQL = "delete from ShiftConfigTemplate sct where sct.shiftConfigBundle.id = ?";
		this.getDefDao().bulkUpdate(HQL, bundleId);
	}
}
