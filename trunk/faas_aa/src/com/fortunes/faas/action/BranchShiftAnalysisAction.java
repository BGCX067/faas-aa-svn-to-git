package com.fortunes.faas.action;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import net.fortunes.core.Helper;
import net.fortunes.core.ListData;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.BranchShiftAnalysis;
import com.fortunes.faas.model.BranchShiftAnalysis.AnalysisType;
import com.fortunes.faas.service.BranchShiftAnalysisService;
import com.fortunes.faas.service.ExcelHelperService;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.OrganizationService;

@SuppressWarnings("serial")
@Component @Scope("prototype")
public class BranchShiftAnalysisAction extends
		GenericAction<BranchShiftAnalysis> {
	
	private BranchShiftAnalysisService branchShiftAnalysisService;
	@Resource ExcelHelperService excelHelperService;
	@Resource OrganizationService organizationService;
	@Resource EmployeeService employeeService;
	
	@Override
	public String list() throws Exception{
		return this.processBranchShiftAnalysis(false);
	}
	
	public String reCalculate() throws Exception{
		return this.processBranchShiftAnalysis(true);
	}
	
	private String processBranchShiftAnalysis(boolean force) throws Exception{
		//compute first date and last date in a mouth
		String yearMonth = p("yearMonth");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String organizationId = p("organizationId");
		long organization = 0;
		if(organizationId == ""){
			String roleCode = (String)getSessionMap().get(Helper.ROLE_CODE);
			if(roleCode.contains("ALLM")){
				organization=00;
			}else if(roleCode.contains("AM")){
				Organization o= organizationService.get(Long.toString((Long)getSessionMap().get(Helper.ORGANIZATION_ID)));
				if(o != null && o.getType().getId().equals("organizationType_01")){
					o = o.getParent();
				}
				organization = o.getId();
			}else if(roleCode.contains("BM")){
				organization = Long.parseLong(Long.toString((Long)getSessionMap().get(Helper.ORGANIZATION_ID)));
			}
		}
		if(StringUtils.isNotEmpty(organizationId) && organizationId!="00"){
			organization = Long.parseLong(organizationId);
		}
		if(organizationId=="00"){
			organization=00;
		}
		Date firstDate = df.parse(yearMonth+"-01");
		Calendar cal = new GregorianCalendar();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(firstDate);
		int lastDateInt = cal.getActualMaximum(Calendar.DATE);
		Date lastDate = df.parse(yearMonth+"-"+lastDateInt);
		
		ListData<BranchShiftAnalysis> stats = getBranchShiftAnalysisService().getBranchShiftAnalysis(
				organization, yearMonth, Integer.parseInt(p("days")), p("analysisType"),start,limit);
		
		if((force || (stats == null || stats.getList().size() <= 0))&&organization>0){
			List<Object[]> statObjects = null;
			if(p("analysisType").equals(AnalysisType.MORE_IN_WEEKEND.name())){
				statObjects = getBranchShiftAnalysisService().searchMoreInWeekend(organization,
						Long.parseLong(p("days")),firstDate,lastDate);
			}else if(p("analysisType").equals(AnalysisType.LESS_IN_TOTAL.name())){
				statObjects = getBranchShiftAnalysisService().searchLessInTotal(organization,
						Long.parseLong(p("days")),firstDate,lastDate);
			}else if(p("analysisType").equals(AnalysisType.EQUAL_IN_WEEKEND.name())){
				statObjects = getBranchShiftAnalysisService().searchEqualForDaysInWeekend(organization,
						Long.parseLong(p("days")),firstDate,lastDate);
			}else if(p("analysisType").equals(AnalysisType.BRANCH_IN_TOTAL.name())){
				statObjects = getBranchShiftAnalysisService().searchBranchInTotal(organization,
						Long.parseLong(p("days")),firstDate,lastDate);
			}else if(p("analysisType").equals(AnalysisType.OTHER_BRANCH_IN_TOTAL.name())){
				statObjects = getBranchShiftAnalysisService().searchOtherBranchInTotal(organization,
						Long.parseLong(p("days")),firstDate,lastDate);
			}else if(p("analysisType").equals(AnalysisType.REST_MORE_THAN.name())){
				statObjects = getBranchShiftAnalysisService().searchRestdayMoreThanTotal(organization,
						Long.parseLong(p("days")) , firstDate, lastDate);
			}
			 getBranchShiftAnalysisService().batchCreateOrUpdate(organization, yearMonth, 
					 Integer.parseInt(p("days")),p("analysisType"),statObjects);
			 stats = getBranchShiftAnalysisService().getBranchShiftAnalysis(
						organization, yearMonth, Integer.parseInt(p("days")), p("analysisType"),start,limit);
		}
		
		JSONArray ja = new JSONArray();
		for(BranchShiftAnalysis stat : stats.getList()){
			JSONObject json =toJsonObject(stat);
			ja.add(json);
		}
		jo.put(DATA_KEY, ja);
		jo.put(TOTAL_COUNT_KEY, stats.getTotal());
		return render(jo);
	}
	
	public String generateReport() throws Exception {
		String yearMonth = p("yearMonth");
		String organizationId = p("organizationId");
		String type = p("type");
		long organization = 0;
		if(StringUtils.isNotEmpty(organizationId)){
			organization = Long.parseLong(organizationId);
		}
		OutputStream os = response.getOutputStream();
		response.reset();  
	    response.setHeader("Content-Disposition","attachment;filename="+yearMonth+".xls");//指定下载的文件名  
	    response.setContentType("application/vnd.ms-excel");  
	    response.setHeader("Pragma", "no-cache");  
	    response.setHeader("Cache-Control", "no-cache");  
	    response.setDateHeader("Expires", 0);
	    
		ListData<BranchShiftAnalysis> stats = getBranchShiftAnalysisService().getBranchShiftAnalysis(
				organization, yearMonth, Integer.parseInt(p("days")), p("analysisType"),0,0);
		excelHelperService.generateReportBranch(organization, Integer.parseInt(p("days")), p("analysisType"), os, yearMonth);
		setJsonMessage(true, "成功导出");
		return render(jo);
	}
	
/*	public String noteReason() throws Exception{
		BranchShiftAnalysis anlysis = branchShiftAnalysisService.get(id);
		anlysis.setNotesDesc(p("notesDesc"));
		branchShiftAnalysisService.update(anlysis);
		setJsonMessage(true, "原因更新成功");
		return render(jo);
	}*/

	@Override
	protected void setEntity(BranchShiftAnalysis anlysis) throws Exception {
		anlysis.setNotesDesc(p("notesDesc"));
	}

	@Override
	protected JSONObject toJsonObject(BranchShiftAnalysis stat) throws Exception {
		AdminHelper json = new AdminHelper();
		json.put("id", stat.getId());
		if(((String)getSessionMap().get(Helper.ROLE_CODE)).contains("AM") || ((String)getSessionMap().get(Helper.ROLE_CODE)).contains("ALLM")){
			json.put("name", stat.getEmployee().getName()+"(<span style='color:blue'>"+stat.getEmployee().getOrganization().getShortName()+"</span>)");
		}else{
			json.put("name",stat.getEmployee().getName());
		}
		
//		if(stat.getAnalysisType().equals(AnalysisType.REST_MORE_THAN.name())){
			json.put("restdays", stat.getRestdays());
//		}
		json.put("orgName", stat.getEmployee().getOrganization().getShortName());
		Employee  e = employeeService.get(stat.getEmployee().getId()+"");
		json.put("postionText", e.getPosition().getText());
		json.put("workdays",stat.getWorkdays());
		json.put("weekdays", stat.getWeekdays());
		json.put("notesDesc", stat.getNotesDesc());
		json.put("branchShiftTimeTotal", stat.getBranchShiftTimeTotal());
		////////////////////////2012-10-23//////////////////
		json.put("branchShiftState", stat.getOrganization().getShortName());
		
		return json.getJsonObject();
	}
	
	@Override
		public GenericService<BranchShiftAnalysis> getDefService() {
			return getBranchShiftAnalysisService();
		}

	public void setBranchShiftAnalysisService(BranchShiftAnalysisService branchShiftAnalysisService) {
		this.branchShiftAnalysisService = branchShiftAnalysisService;
	}

	public BranchShiftAnalysisService getBranchShiftAnalysisService() {
		return branchShiftAnalysisService;
	}

}
