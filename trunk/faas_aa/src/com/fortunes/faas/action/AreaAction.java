package com.fortunes.faas.action;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.Area;
import com.fortunes.faas.service.AreaService;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.OrganizationService;

import net.fortunes.core.ListData;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

//@Component @Scope("prototype")
public class AreaAction extends GenericAction<Area> {

	
	private static final String SPLIT = "-";
	private static final String EMPLOYEE = "e";
	private static final String ORGANIZATION = "o";
	private boolean isChecked = false;
	
	@Resource private OrganizationService organizationService;

	public String removeAreaEmployees() throws Exception {
//		areaService.removeAreaEmployees(checked,p("areaId"));
		setJsonMessage(true, "成功将"+checked.length+"条移出区域");
		return render(jo);
	}

	public String getAreaEmployeesList() throws Exception {
		ListData<Employee> listData =null;
		if(null!=p("areaId")&&"2".equals(p("areaId"))){
			listData = employeeService.getListData(query, queryMap, start, limit);			
		}
		else if(null!=p("areaId")&&"3".equals(p("areaId"))){
			listData = employeeService.getEmployeesUnAssignArea(query, queryMap,start,limit);
		}
		else{
			listData = employeeService.getEmployeesByAreaId(p("areaId"),query,queryMap,start,limit);
		}
		JSONArray ja = new JSONArray();
		for(Employee e:listData.getList()){
			JSONObject record = new JSONObject();
			record.put("id", e.getId());
			record.put("name", e.getName());
			record.put("code",e.getCode());
			Organization org = e.getOrganization();
			record.put("organization", org!=null?org.getName():"");
			ja.add(record);
		}
		jo.put(DATA_KEY, ja);
		jo.put(TOTAL_COUNT_KEY, listData.getTotal());
		return render(jo);  
	}

	public String getAreasByEmployee() throws Exception {
		Employee employee = employeeService.get(getId());
		List<Area> areaList = areaService.getListData().getList();
		JSONArray ja = new JSONArray();
		for(Area area:areaList){
			if("所有人员".equals(area.getName())||"未分区域".equals(area.getName())
					||"root".equals(area.getName())){
				continue;
			}
			JSONObject record = new JSONObject();
			record.put("id", area.getId());
			record.put("text", area.getName());
//			record.put("checked",employee == null?false : employee.getAreas().contains(area));
			ja.add(record);
		}
		jo.put("data", ja);
		return render(jo);
	}

	public String getAreas() throws Exception {
		List<Area> areaList = areaService.getListData().getList();
		JSONArray ja = new JSONArray();
		JSONObject record = null;
		for(Area area:areaList){
			if("所有人员".equals(area.getName())||"未分区域".equals(area.getName())
					||"root".equals(area.getName())){
				continue;
			}
			if(area.getParent() != null ){
				record = new JSONObject();
				record.put("id", area.getId());
				record.put("text", area.getName());
				ja.add(record);
			}
		}
		jo.put("data", ja);
		return render(jo);
	}

	public String areaTree() throws Exception {
		List<Area> areas = areaService.listTree("1");
		JSONArray jsonArray = new JSONArray();
		for(Area area : areas){
			if("所有人员".equals(area.getName())||"未分区域".equals(area.getName())
					||"root".equals(area.getName())){
				continue;
			}
			jsonArray.add(walkAreaTree(area));
		}
		return render(jsonArray);
	}
	public String multiAreaTree() throws Exception {
		isChecked = true;
		List<Area> areas = areaService.listTree("1");
		JSONArray jsonArray = new JSONArray();
		for(Area area : areas){
			if("所有人员".equals(area.getName())||"未分区域".equals(area.getName())
					||"root".equals(area.getName())){
				continue;
			}
			jsonArray.add(walkAreaTree(area));
		}
		return render(jsonArray);
	}

	private JSONObject walkAreaTree(Area area){
		JSONObject jo = toJsonObjectArea(area);
		List<Area> areas = area.getChildren();
		if(null!=areas&&areas.size()>0){
			JSONArray ja = new JSONArray();
			List<Area> childs = areaService.listTree(area.getId()+"");
			for(Area a :childs){
				ja.add(walkAreaTree(a));
			}
			jo.put("children", ja);
		}
		return jo;
	}
	private JSONObject toJsonObjectArea(Area area){
		AdminHelper record = new AdminHelper();
		record.put("id", area.getId());
		record.put("text", area.getName());
		if(isChecked){
			record.put("checked", false);
		}
		List<Area> areas = area.getChildren();
		if(areas!=null&&areas.size()>0){
			record.put("leaf", false);
		}else
			record.put("leaf", true);
		return record.getJsonObject();
	}
	
	@Override
	protected void setEntity(Area a) throws Exception {
		a.setName(p("name"));
		a.setAddress(p("address"));
		if(a.getId() == 0){
			a.setParent(parentId.equals("0") ? 
					areaService.getRoot() : areaService.get(parentId));
		}
	}

	@Override
	protected JSONObject toJsonObject(Area a) throws Exception {
		AdminHelper record = new AdminHelper();
		record.put("id", a.getId());
		record.put("name", a.getName());
		record.put("text", a.getName());
		return record.getJsonObject();
	}

	public JSONArray walkTreeInConfig(Area area) throws Exception{
		JSONArray ja = new JSONArray();
		if(area != null){
			List<Area> ds = area.getChildren();		
			for(Area d : ds){
				JSONObject jo = toJsonObject(d);
				if(d.isLeaf()){				
					jo.put("leaf", true);
				}else{
					//异步load
					//jo.put("children", walkTree(d));
				}
				ja.add(jo);
			}
		}
		return ja;
	}

	@Override
	protected JSONArray walkTree(Area area) throws Exception {
		JSONArray ja = new JSONArray();
		if(area != null){
			List<Area> ds = area.getChildren();		
			for(Area d : ds){
				JSONObject jo = toJsonObject(d);
				if(d.isLeaf()){				
					jo.put("leaf", true);
				}else{
					//异步load
					//jo.put("children", walkTree(d));
				}
				ja.add(jo);
			}
		}
		return ja;
	}

	public String listEmployees() throws Exception {
		String type = node.split(SPLIT)[0];
		int id = Integer.parseInt(node.split(SPLIT)[1]);
		if(type.startsWith(ORGANIZATION)){
			Organization selectedOrganization;
			if(id == 0){
				selectedOrganization = organizationService.get(1+"");
			}else{
				selectedOrganization = organizationService.get(id+"");
			}
				
			List<Organization> children = selectedOrganization.getChildren();
			List<Employee> employees = selectedOrganization.getEmployees();
			
			JSONArray ja = new JSONArray();
			for(Organization subOrganization:children){
				ja.add(toJsonObject(subOrganization));
			}
			
			for(Employee e:employees){
				ja.add(toJsonObject(e));
			}
			return render(ja);
			
		}else{
			throw new IllegalArgumentException();
		}
	}
	public String assignArea() throws Exception {
		long areaId = Long.parseLong(p("areaId"));
//		areaService.batchMoveEmployeesToAreas(checked, areaId);
		setJsonMessage(true, "成功分配");
		return render(jo);
	}
	protected JSONObject toJsonObject(Employee e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", EMPLOYEE+SPLIT+e.getId());
		record.put("text", e.getName());
		record.put("iconCls","employee");
		record.put("type","employee");
		record.put("leaf",true);
		if(p("checkBox") != null && !p("checkBox").equals("false")){
			record.put("checked",false);
		}
		return record.getJsonObject();
	}

	protected JSONObject toJsonObject(Organization e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", ORGANIZATION+SPLIT+e.getId());
		record.put("text", e.getShortName());
		record.put("iconCls", "organization");
		record.put("type", "organization");
		record.put("leaf",false);
		if(p("checkBox") != null && !p("checkBox").equals("false")){
			record.put("checked",false);
		}
		return record.getJsonObject();
	}
	private long[] checked;

	@Resource private AreaService areaService;
	@Resource private EmployeeService employeeService;
	
	public long[] getChecked() {
		return checked;
	}

	public void setChecked(long[] checked) {
		this.checked = checked;
	}

	@Override
	public GenericService<Area> getDefService() {
		return areaService;
	}
}
