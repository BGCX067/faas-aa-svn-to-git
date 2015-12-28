package com.fortunes.faas.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.MeetOrTrain;
import com.fortunes.faas.service.ExcelHelperService;
import com.fortunes.faas.service.MeetOrTrainService;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.OrganizationService;

import net.fortunes.core.Helper;
import net.fortunes.core.ListData;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component @Scope("prototype")
public class MeetOrTrainAction extends GenericAction<MeetOrTrain>{
	@Resource private MeetOrTrainService meetOrTrainService;
	@Resource private ExcelHelperService excelHelperService;
	@Override
	protected void setEntity(MeetOrTrain entity) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String list() throws Exception {
		Long employee = authedUser.getEmployee().getId();
		queryMap.put("employee", Long.toString(employee));
		String employeeId = p("organization");
		queryMap.put("organization", employeeId!=null&&!employeeId.equals("")?employeeId:null);
		String sdate = p("sdate");
		queryMap.put("sdate", sdate!=null&&!sdate.equals("")?sdate:null);
		String edate = p("ddate");
		queryMap.put("edate", edate!=null&&!edate.equals("")?edate:null);
		return super.list();
//		User user = (User)sessionMap.get(Helper.AUTHED_USER);
//		Employee e = user.getEmployee();
//		List<MeetOrTrain> list = meetOrTrainService.getMeetByEmployee(e);
//		JSONArray ja = new JSONArray();
//		for(MeetOrTrain me : list){
//			ja.add(toJsonObject(me));
//		}
//		jo.put(DATA_KEY, ja);
//		jo.put(TOTAL_COUNT_KEY, list.size());
//		return render(jo);  
	}

	@Override
	protected JSONObject toJsonObject(MeetOrTrain e) throws Exception {
		AdminHelper record=new AdminHelper();
		record.put("id", e.getId());
		record.put("name", e.getName());
		record.put("address", e.getAddress());
		record.put("organization", e.getOrganizationCode());
		record.put("theme", e.getTheme());
		record.put("isVerify", e.isVerify());
		record.put("verifyOr", e.getCurrentVerify()==null?null:e.getCurrentVerify().getName());
		return record.getJsonObject();
	}
	
	@Override
	public String update() throws Exception {
//		long orId = Helper.getOrganizationId();
//		Organization o = organizationService.get(Long.toString(orId));
		String areaId=p("areaId");
		MeetOrTrain me = meetOrTrainService.get(areaId);
		if(me.getCurrentVerify().equals(me.getPersonnelVerify())){
			me.setVerify(true);
			me.setCurrentVerify(null);
		}else{
			me.setCurrentVerify(me.getPersonnelVerify());
			me.setVerify(false);
		}
		getDefService().update(me);
		jo.put(ENTITY_KEY, toJsonObject(me));
		setJsonMessage(true, "会议/培训("+me.getName()+")"+(me.isVerify()?"已确认":"确认失败"));
		return render(jo);
	}
	
	public String exportExcel()throws Exception{
		String employeeId = Long.toString(authedUser.getEmployee().getId());
		queryMap.put("employee", employeeId);
		String organizationId = p("organization");
		queryMap.put("organization", organizationId!=null&&!organizationId.equals("")?organizationId:null);
		String sdate = p("sdate");
		queryMap.put("sdate", sdate!=null&&!sdate.equals("")?sdate:null);
		String edate = p("edate");
		queryMap.put("edate", edate!=null&&!edate.equals("")?edate:null);
		
		String fileName = "会议培训报表";
		List<MeetOrTrain> list = this.getDefService().getListData(query, queryMap, 0, 0).getList();
		File file = excelHelperService.ExportMeetOrTrainExcel(fileName,list);
		byte[] bytes = FileUtils.readFileToByteArray(file);
		return renderFile(bytes,fileName+".xls");
	}
	
	@Override
	public GenericService<MeetOrTrain> getDefService(){
		return meetOrTrainService;
	}

}
