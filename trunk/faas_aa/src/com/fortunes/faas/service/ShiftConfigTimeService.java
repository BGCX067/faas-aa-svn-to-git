package com.fortunes.faas.service;

import java.util.List;

import org.springframework.stereotype.Component;

import net.fortunes.core.service.GenericService;

import com.fortunes.faas.model.ShiftConfigTime;
@Component
public class ShiftConfigTimeService extends GenericService<ShiftConfigTime>{
	public ShiftConfigTime findShiftConfigTimeByType(String shiftConfigType){
		String sql = "select new ShiftConfigTime(s.id,s.teamName,s.shiftConfigType,s.startWorkTime,s.endWorkTime,s.isNextDay) from ShiftConfigTime s where s.shiftConfigType=?";
		List<ShiftConfigTime> list = this.getDefDao().findByQueryString(sql,shiftConfigType);
		return list.size()>0 ? list.get(0):null; 
	}
	
	public void addBatch(List<ShiftConfigTime> list)throws Exception{
		for(ShiftConfigTime sct:list){
			this.add(sct);
		}
	}
}
