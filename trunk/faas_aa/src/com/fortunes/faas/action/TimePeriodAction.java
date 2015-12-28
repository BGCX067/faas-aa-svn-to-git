package com.fortunes.faas.action;

import java.text.ParseException;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.fortunes.faas.model.TimePeriod;
import com.fortunes.faas.service.TimePeriodService;

@Component @Scope("prototype")
public class TimePeriodAction extends GenericAction<TimePeriod> {
	
	private TimePeriodService timePeriodService;
	
	protected void setEntity(TimePeriod e) throws ParseException{
		e.setStartTime(AppHelper.toDate(p("startTime")));
		e.setEndTime(AppHelper.toDate(p("endTime")));
		e.setToleranceStartMinute(Integer.parseInt(p("toleranceStartMinute")));
		e.setToleranceEndMinute(Integer.parseInt(p("toleranceEndMinute")));
	}
	
	protected JSONObject toJsonObject(TimePeriod e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("startTime", e.getStartTime());
		record.put("endTime", e.getEndTime());
		record.put("toleranceStartMinute", e.getToleranceStartMinute());
		record.put("toleranceEndMinute", e.getToleranceEndMinute());
		return record.getJsonObject();
	}
	
	/*=============== setter and getter =================*/
	
	@Override
	public GenericService<TimePeriod> getDefService() {
		return timePeriodService;
	}
	
	public void setTimePeriodService(TimePeriodService timePeriodService) {
		this.timePeriodService = timePeriodService;
	}

	public TimePeriodService getTimePeriodService() {
		return timePeriodService;
	}

}
