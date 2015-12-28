package com.fortunes.faas.action;

import java.util.Calendar;
import java.util.Map;

import javax.annotation.Resource;

import net.fortunes.core.ListData;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.MonthStatistics;
import com.fortunes.faas.service.AttShiftService;
import com.fortunes.faas.service.MonthStatisticsService;
import com.fortunes.faas.vo.AttShifts;
import com.fortunes.fjdp.admin.AdminHelper;

/**
 * 
 * @author Leo
 * @version 2011-4-19
 */
@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class MonthStatisticsAction extends GenericAction<MonthStatistics>{
	
	@Resource private MonthStatisticsService monthStatisticsService;
	@Resource private AttShiftService attShiftService;
	
	@SuppressWarnings("unchecked")
	public String attShiftStatistics() throws Exception {
		Map queryMap = request.getParameterMap();		
		ListData<AttShifts> listData = null;
		try {
			listData = attShiftService.attShiftStatistics(queryMap, start, limit);
		} catch (Exception e) {
			e.printStackTrace();
			setJsonMessage(true, "统计出错");
		}
		JSONArray ja = new JSONArray();
		for(AttShifts a:listData.getList()){
			ja.add(toJsonObject(a));
		}
		jo.put(DATA_KEY, ja);
		jo.put(TOTAL_COUNT_KEY, listData.getTotal());
		return render(jo);
	}
	protected JSONObject toJsonObject(AttShifts a) throws Exception {
		AdminHelper record = new AdminHelper();
		record.put("employeeCode", a.getEmployeeCode());
		record.put("employeeName", a.getEmployeeName());
		record.put("deptName", a.getDeptName());
		record.put("lates", a.getLates());
		record.put("earlys", a.getEarlys());
		record.put("absents", a.getAbsents());
		record.put("lateTimes", a.getLateTimes());
		record.put("earlyTimes", a.getEarlyTimes());
		record.put("weekday", a.getWeekday());
		record.put("restday", a.getRestday());
		double leaveTimes = a.getAnnualLeave()+a.getLeave()+a.getSickLeave();
		record.put("leaveTimes", leaveTimes);
		record.put("leave", a.getLeave());
		record.put("sickLeave", a.getSickLeave());
		record.put("annualLeave", a.getAnnualLeave());
		record.put("homeLeave", a.getHomeLeave());
		record.put("maternityLeave", a.getMaternityLeave());
		record.put("familyPlanningLeave", a.getFamilyPlanningLeave());
		record.put("travel", a.getTravel());
		record.put("overTime", a.getOverTime());
		
		return record.getJsonObject();
	}
	@Override
	protected void setEntity(MonthStatistics entity) throws Exception {
		
	}

	@Override
	protected JSONObject toJsonObject(MonthStatistics entity) throws Exception {
		return null;
	}
	@Override
	public GenericService<MonthStatistics> getDefService() {
		return monthStatisticsService;
	}
}
