package com.fortunes.faas.action;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.faas.exception.DictTypeException;
import com.fortunes.faas.model.ShiftConfig;
import com.fortunes.faas.model.ShiftConfigTime;
import com.fortunes.faas.service.ExcelHelperService;
import com.fortunes.faas.service.ShiftConfigService;
import com.fortunes.faas.service.ShiftConfigTimeService;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Config;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Config.ConfigKey;
import com.fortunes.fjdp.admin.service.ConfigService;
import com.fortunes.fjdp.admin.service.EmployeeService;

import net.fortunes.core.ListData;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class ShiftConfigTimeAction extends GenericAction<ShiftConfigTime>{
	
	private static final String WORK = "WORK";
	@Resource private ShiftConfigTimeService shiftConfigTimeService;
	@Resource private ExcelHelperService excelHelperService;
	@Resource private ShiftConfigService shiftConfigService;
	@Resource private EmployeeService employeeService;
	@Resource private ConfigService configService;
	private File excelFile;
	
	@Override
	public String list() throws Exception {
		// TODO Auto-generated method stub
//		return super.list();
		ListData<ShiftConfigTime> listData = getDefService().getListData(query, queryMap, start, limit);
		JSONArray ja = new JSONArray();
		for(ShiftConfigTime entity:listData.getList()){
			ja.add(toJsonObject(entity));
		}
		jo.put(DATA_KEY, ja);
		jo.put(TOTAL_COUNT_KEY, listData.getTotal());
		return render(jo);  
	}
	
	@Override
	public String create() throws Exception {
		ShiftConfigTime sct = getEntityClass().newInstance();
		setEntity(sct);
		ShiftConfigTime shiftConfigTime = shiftConfigTimeService.findShiftConfigTimeByType(sct.getShiftConfigType());
		if(shiftConfigTime!=null){
			setJsonMessage(true,"班次名称不能重复");
			return render(jo);
		}
		if(sct.getStartWorkTime().equals("00:00:00")||sct.getEndWorkTime().equals("00:00:00")){
			setJsonMessage(true,"上下班时间不能为空");
			return render(jo);
		}
//		return super.create();
		getDefService().add(sct);
		jo.put(ENTITY_KEY, toJsonObject(sct));
		setJsonMessage(true, sct.toString().equals("")?
				"新增了一条记录!" : "新增了("+sct+")的记录");
		return render(jo);
	}
	@Override
	protected void setEntity(ShiftConfigTime sct) throws Exception {
		sct.setTeamName(p("teamName"));
		sct.setShiftConfigType(p("shiftConfigType"));
		DateFormat df = new SimpleDateFormat("HH:mm");
		sct.setStartWorkTime(df.parse(p("startWorkTime")));
		sct.setEndWorkTime(df.parse(p("endWorkTime")));
		sct.setNextDay(p("nextDay").equals("on")? true:false);
	}

	@Override
	protected JSONObject toJsonObject(ShiftConfigTime sct) throws Exception {
		// TODO Auto-generated method stub
		AdminHelper record = new AdminHelper();
		record.put("id", sct.getId());
		record.put("teamName", sct.getTeamName());
		record.put("shiftConfigType", sct.getShiftConfigType());
		record.put("startWorkTime", sct.getStartWorkTime());
//		record.put("endWorkTime", sct.getEndWorkTime());
		record.put("endWorkTime", getNextDayTime(sct));
		record.put("nextDay", sct.isNextDay());
		return record.getJsonObject();
	}
	
	public String getNextDayTime(ShiftConfigTime sct){
		Date endWorkTime = sct.getEndWorkTime();
		String endWorkTimeStr = null;
		try {
			endWorkTimeStr = Tools.Time2String(endWorkTime);
			if(sct.getEndWorkTime().compareTo(sct.getStartWorkTime())<=0){
				endWorkTimeStr = "次日"+endWorkTimeStr;
			}else{
				endWorkTimeStr = endWorkTimeStr;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return endWorkTimeStr;
	}
	
	/**
	 * 导入班次时间表信息
	 * @return
	 * @throws Exception
	 */
	public String exportExcel() throws Exception{
		try{
			List<ShiftConfigTime> list = excelHelperService.excelToShiftConfigTime(excelFile);
			shiftConfigTimeService.addBatch(list);
			setJsonMessage(true, "导入成功");
		}catch (DictTypeException e){
			setJsonMessage(true, e.toString());
		}catch (Exception e){
			e.printStackTrace();
			setJsonMessage(true, "班次时间导入时出错");
			setJsonMessage(true,e.toString());
		}
		return render(jo.toString());
	}
	/**
	 * 排班班次导入
	 * @return
	 * @throws Exception
	 */
	public String importBrowseExcel() throws Exception{
		try {
			List<ShiftConfig> list = excelHelperService.importBrowseExcel(excelFile);
			shiftConfigService.addBatch(list);
			setJsonMessage(true,"排班班次表导入成功");
		}catch (DictTypeException e){
			setJsonMessage(true, e.toString());
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setJsonMessage(true,"导入失败");
		}
		return render(jo.toString());
	}
	
	public String getShiftConfigInfos()throws Exception{
		List<ShiftConfig> list = shiftConfigService.queryShiftConfigs(
				p("employeeId"),AppHelper.toDate(p("startDate")),AppHelper.toDate(p("endDate")));
		JSONArray ja = new JSONArray();
		Calendar calendar = Calendar.getInstance();
//		Date sDate = Tools.string2Date(sf.format(calendar.getTime())+"-01");
		Date sDate = Tools.string2Date(p("startDate"));
		Date eDate = Tools.string2Date(p("endDate"));
		calendar.setTime(sDate);
//		Date eDate = Tools.string2Date(sf.format(calendar.getTime())+"-"+calendar.getActualMaximum(calendar.DAY_OF_MONTH));
		Employee e = employeeService.get(p("employeeId"));
		SimpleDateFormat sfd = new SimpleDateFormat("HH:mm:ss");
		Config start = configService.getConfigBykey(ConfigKey.START_WORK_IN_MORNING_TIME);
		Config end = configService.getConfigBykey(ConfigKey.OVER_WORK_IN_AFTERNOON_TIME);
		Date startTime = sfd.parse(start.getConfigValue()+":00");
		Date endTime = sfd.parse(end.getConfigValue()+":00");
		while (eDate.compareTo(calendar.getTime())>=0) {
			boolean isExit = false;
			for(ShiftConfig info:list){
				if(info.getDate().equals(calendar.getTime())){
					isExit = true;
				}
				if(isExit){
					if(!info.isCancel()){
						ja.add(toJsonObject(info));
					}
				}
			}
			calendar.add(calendar.DATE, 1);
		}
		
		jo.put(DATA_KEY, ja);
		return render(jo);
	}
	
	protected JSONObject toJsonObject(ShiftConfig e) throws ParseException{
		DateFormat fmt = new SimpleDateFormat("HH:mm");
		String timeRange = fmt.format(e.getStartTime());
		if(e.isNextDay()){
			timeRange += "-次日"+fmt.format(e.getEndTime());
		}else{
			timeRange += "-"+fmt.format(e.getEndTime());
		}
		
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("date", Tools.date2String(e.getDate()));
		record.put("timeRange", timeRange);
		record.put("type", WORK);
		return record.getJsonObject();
	}
	
	@Override
	public GenericService<ShiftConfigTime> getDefService() {
		// TODO Auto-generated method stub
		return this.shiftConfigTimeService;
	}

	public File getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}
}
