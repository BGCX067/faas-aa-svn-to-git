package com.fortunes.fjdp.admin.action;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.model.UserOperation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.OrganizationService;
import com.fortunes.fjdp.admin.service.UserService;

import net.fortunes.core.Helper;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.PinYin;
import net.sf.json.JSONArray;
import net.sf.json.JSONFunction;
import net.sf.json.JSONObject;

@Component @Scope("prototype")
public class OrganizationAction extends GenericAction<Organization> {
	
	@Resource private EmployeeService employeeService;
	@Resource private OrganizationService organizationService;
	@Resource private UserService userService;

	private int[] checkedId;


	protected void setEntity(Organization o) throws ParseException{
		o.setName(p("name")); 
		o.setCode(p("code"));
		o.setShortName(p("shortName"));
		o.setAddress(p("address"));
		o.setTel(p("tel"));
		o.setType(AdminHelper.toDict(p("type")));
		if(StringUtils.isNotEmpty(p("branchLeader"))){
			o.setBranchLeader(AdminHelper.toEmployee(p("branchLeader")));
		}
		if(o.getId() == 0){
			o.setParent(parentId.equals("0") ? 
					organizationService.getRoot() : organizationService.get(parentId));
		}
		String organizationP = p("parentOrganization");
		if(StringUtils.isNotEmpty(organizationP)){
			if(organizationP.equals("o-0")){
				organizationP="1";
			}
			if(o.getChildren()!=null&&o.getChildren().size()>0){
				o.setLeaf(false);
			}
			Organization org = organizationService.get(organizationP);
			org.setLeaf(false);
			organizationService.update(org);
			o.setParent(org);
		}
	}
	
	@Override
	public String del() throws Exception {
		organizationService.delOrganizationFirstStep(p("id"));
		return super.del();
	}
	
	protected JSONObject toJsonObject(Organization e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("name", e.getName());
		record.put("code",e.getCode());
		record.put("shortName", e.getShortName());
		record.put("text", e.getShortName());
		record.put("fullName", e.getFullName());
		record.put("address", e.getAddress());
		record.put("tel", e.getTel());
		record.put("type", e.getType());
		record.put("branchLeader", e.getBranchLeader());
		record.put("iconCls", "organization");
		JSONObject root = new JSONObject();
		root.put("id", "o-0");
		root.put("text", "一级部门");
		root.put("code", "0");
		root.put("leaf", true);
		record.put("parentOrganization", e.getParent().getId()==1?root:tojs(e.getParent()));
		return record.getJsonObject();
	}
	
	public JSONObject tojs(Organization o){
		JSONObject jos = new JSONObject();
		jos.put("id", o.getId());
		jos.put("text", o.getName());
		jos.put("code", o.getCode());
		jos.put("leaf", true);
		return jos;
	}

	public JSONArray walkTree(Organization organization) throws Exception{
		JSONArray ja = new JSONArray();
		if(organization != null){
			List<Organization> ds = organization.getChildren();		
			for(Organization d : ds){
				JSONObject jo = toJsonObject(d);
				if(d.getLeaf()){				
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
	
	//所有部门（用于下拉菜单）
	public String getOrganizations() throws Exception{  
		List<Organization> organizationList = organizationService.getListData().getList();
		JSONArray ja = new JSONArray();
		JSONObject record = null;
		for(Organization organization:organizationList){
			if(organization.getParent() != null ){
				record = new JSONObject();
				record.put("id", organization.getId());
				record.put("text", organization.getName());
				record.put("code", organization.getCode());
				ja.add(record);
			}
		}
		jo.put("data", ja);
		
		return render(jo);  
	}

	
	//列出某个部门所拥有的所有员工
	public String ListEmployees() throws Exception{
		List<Employee> employees = employeeService.getEmployeeByOrgId(Long.parseLong(p("organizationId")));
		JSONArray ja = new JSONArray();
		for(Employee e : employees){
			JSONObject record = new JSONObject();
			record.put("id", e.getId());
			record.put("text", e.getName());
			record.put("qtip", "工号："+e.getCode());
			record.put("iconCls", 
					(e.getSex()!=null && e.getSex().getText().equals("女"))? "employee_female" : "employee");
			record.put("leaf", true);
			ja.add(record);
		}
		return render(ja);  
	}
	
	//列出某个部门未拥有的员工(根据关键字过滤)
	public String ListEmployeesUnassign() throws Exception{
		List<Employee> employeeList;
		if(OrganizationService.SINGLE_ORGANIZATION){
			employeeList = organizationService.getUnassignEmployees();
		}else{
			employeeList = organizationService.getUnassignEmployeesByOrganizationId(p("organizationId"));
		}
		JSONArray ja = new JSONArray();
		for(Employee employee:employeeList){
			String namePy = PinYin.toPinYinString(employee.getName());
			if(namePy.startsWith(getQuery().toUpperCase())
					|| employee.getName().startsWith(getQuery())){
				JSONObject record = new JSONObject();
				record.put("id", employee.getId());
				record.put("text", employee.getName());
				record.put("code", employee.getCode());
				record.put("pinyin", namePy);
				ja.add(record);
			}	
		}
		jo.put("data", ja);
		return render(jo);  
	}
	
	//加入一个员工到一个部门
	public String addEmployee() throws Exception{
		organizationService.addEmployee(p("organizationId"), p("employeeId"));
		
		//修改员工表里面的primaryOrganization属性
	    Employee entity=employeeService.get(p("employeeId"));
	    entity.setOrganization(AdminHelper.toOrganization(p("organizationId")));
		employeeService.update(entity);
		
		return render(jo);
	}
	
	//从一个部门移除某一个员工
	public String removeEmployee() throws Exception{
		organizationService.removeEmployee(p("organizationId"), p("employeeId"));
		
		//修改员工表里面的primaryOrganization属性设置为null
		Employee entity=employeeService.get(p("employeeId"));
	    entity.setOrganization(AdminHelper.toOrganization(null));
	    employeeService.update(entity);
		return render(jo);
	}
	public String organizationTree() throws Exception {
		List<Organization> organizations = organizationService.listTree("1");
		JSONArray jsonArray = new JSONArray();
		for(Organization organization : organizations){
			jsonArray.add(walkOrganizationTree(organization,true));
		}
		return render(jsonArray);
	}
	/**
	 * 用于考勤记录查询，考勤记录统计中的organization列表
	 * 查出当前登录用户所在部门，及所在部门子部门
	 * @return
	 * @throws Exception
	 */
//	public String AttOrganizationTree()throws Exception{
//		String checked = p("checked");
//		JSONArray jsonArray = new JSONArray();
//		User user = userService.getDefDao().refresh(authedUser);
//		List<Organization> userOrganization = user.getOrganizations();
//		for(Organization parent : userOrganization){
//			JSONObject paRecord = new JSONObject();
//			if(parent.getParent().getId()==1){
//				paRecord.put("id", parent.getId());
//				paRecord.put("text", parent.getName());
//				if(!checked.equals("false")){
//					paRecord.put("checked",false);
//				}
//				paRecord.put("leaf", true);
//				paRecord.put("expanded", true);
//				JSONArray childs = walkOrganizationTree(parent,userOrganization); 
//				if(childs.size()>0){
//					paRecord.put("children", childs);
//					paRecord.put("leaf", false);
//				}
//			}
//			if(!paRecord.isEmpty()){
//				jsonArray.add(paRecord);
//			}
//		}
//		return render(jsonArray);
//	}
	/**
	 * 递归上一方法
	 * @param org
	 * @param userOrganization
	 * @return
	 */
//	private JSONArray walkOrganizationTree(Organization org,List<Organization> userOrganization){
//		String checked = p("checked");
//		JSONArray ja = new JSONArray();
//		for(Organization o : userOrganization){
//			if(org.getId()==o.getParent().getId()){
//				JSONObject record = new JSONObject();
//				record.put("id", o.getId());
//				record.put("text", o.getName());
//				if(!checked.equals("false")){
//					record.put("checked",false);
//				}
//				record.put("leaf", true);
//				record.put("expanded", true);
//				JSONArray jas = walkOrganizationTree(o,userOrganization);
//				if(!jas.isEmpty()){
//					record.put("children",jas);
//					record.put("leaf", false);
//				}
//				ja.add(record);
//			}
//		}
//		return ja;
//	}
	
	public String AttOrganizationTree()throws Exception{
		String checked = p("checked");
		JSONArray jsonArray = new JSONArray();
		User user = userService.getDefDao().refresh(authedUser);
		Organization root = this.getDefService().get("1");
		List<Organization> userOrganization = user.getOrganizations();
		List<Organization> parentOrganization = new ArrayList<Organization>();
		if(userOrganization.size()>0){
			for(Organization org : userOrganization){
				if(org.getParent().getId()==root.getId()){
					parentOrganization.add(org);
				}
			}
			if(parentOrganization.size()>0){
				for(Organization pO : parentOrganization){
					JSONObject record = new JSONObject();
					record.put("id", pO.getId());
					record.put("text", pO.getName());
					if(!checked.equals("false"))
					record.put("checked",false);
					record.put("leaf", true);
					JSONArray ja = walkOrganizationTree(pO,userOrganization);
					if(ja.size()>0){
						record.put("leaf", false);
						record.put("children", ja);
					}
					jsonArray.add(record);
				}
			}
			userOrganization.removeAll(parentOrganization);
			for(Organization cO : userOrganization){
				if(!parentOrganization.contains(cO.getParent())){
					JSONObject record = new JSONObject();
					record.put("id", cO.getId());
					record.put("text", cO.getName());
					if(!checked.equals("false"))
					record.put("checked",false);
					record.put("leaf", true);
					record.put("expanded", true);
					jsonArray.add(record);
				}
			}
		}else{
			JSONArray roleArray = JSONArray.fromObject(this.getSessionMap().get(Helper.ROLE_CODE));
			if(roleArray.size() == 1 && roleArray.contains(Role.SYSTEM_GENERAL_STAFF)){
				
			}else{
				Organization oO = organizationService.getOrganizationById(Helper.getOrganizationId()+"");
				if(oO!=null){
					JSONObject record = new JSONObject();
					record.put("id", oO.getId());
					record.put("text", oO.getName());
					if(!checked.equals("false"))
					record.put("checked",false);
					record.put("leaf", true);
					record.put("expanded", true);
					jsonArray.add(record);
				}
			}
		}
		return render(jsonArray);
	}
	
	private JSONArray walkOrganizationTree(Organization org,List<Organization> userOrganization){
		String checked = p("checked");
		List<Organization> childOrganization = org.getChildren();
		JSONArray ja = new JSONArray();
		for(Organization o : childOrganization){
			if(userOrganization.contains(o)){
				JSONObject record = new JSONObject();
				record.put("id", o.getId());
				record.put("text", o.getName());
				if(!checked.equals("false"))
				record.put("checked",false);
				record.put("leaf", true);
				record.put("expanded", true);
				ja.add(record);
			}
		}
		return ja;
	}

	
	
	private JSONObject walkOrganizationTree(Organization organization,boolean isChecked){
		JSONObject jo = toJsonObjectOrganization(organization,isChecked);
			JSONArray ja = new JSONArray();
			List<Organization> orgs = organizationService.listTree(organization.getId()+"");
			for(Organization org :orgs){
				ja.add(walkOrganizationTree(org,isChecked));
			}
			jo.put("children", ja);
		return jo;
	}
	private JSONObject toJsonObjectOrganization(Organization organization,boolean isChecked){
		AdminHelper record = new AdminHelper();
		record.put("id", organization.getId());
		record.put("text", organization.getName());
		if(isChecked){
			record.put("checked", false);
		}
		record.put("leaf", organization.getLeaf());
		return record.getJsonObject();
	}
	
	
//	private JSONArray walkOrganizationTree(){
//		JSONArray ja = new JSONArray();
//		User user = userService.getDefDao().refresh(authedUser);
//		List<Organization> userOrgs = user.getOrganizations();
//		List<Organization> orgs = organizationService.getAll();
//		for(Organization org : orgs){
//			if(userOrgs.contains(org)){
//				JSONObject record = new JSONObject();
//				record.put("id", org.getId());
//				record.put("text", org.getName());
//				record.put("checked",false);
//				if(org.getLeaf()){
//					record.put("leaf", true);
//				}else{
//					record.put("expanded", true);
//					record.put("children",walkOrganizationTree(org,user));
//				}ja.add(record);
//			}
//		}
//		return ja;
//	}
	
	
	private JSONArray walkOrganizationTree(Organization organization,User user){
		JSONArray ja = new JSONArray();
		List<Organization> orgs = organizationService.getSimpleOrg(organization.getId()+"");
		List<Organization> userList = user.getOrganizations();
		for(Organization org : orgs){
			JSONObject record = new JSONObject();
			record.put("id", org.getId());
			record.put("text", org.getName());
			record.put("checked",userList.contains(org) );
			if(org.getLeaf()){
				record.put("leaf", true);
			}else{
				record.put("expanded", true);
				record.put("children",walkOrganizationTree(org,user));
			}ja.add(record);
		}
		return ja;
	}
	
	public String listOrganizations() throws Exception {
		Organization organization = organizationService.getRoot();
		User user = userService.get(id);
		JSONArray ja = walkOrganizationTree(organization,user);
		return render(ja);
	}
	
	public String getOrganizationTree() throws Exception {
		List<Organization> organizations = organizationService.listTree("1");
		JSONArray jsonArray = new JSONArray();
		JSONObject record = new JSONObject();
		record.put("id", "H-0");
		record.put("text", "取消选择");
		record.put("leaf", true);
		jsonArray.add(record);
		for(Organization organization : organizations){
			jsonArray.add(walkOrganizationTree(organization,false));
		}
		return render(jsonArray);
	}
	
	public String getOrganizationAndRoot() throws Exception{
		List<Organization> organizations = organizationService.listTree("1");
		JSONObject record = new JSONObject();
		record.put("id", "o-0");
		record.put("text", "一级部门");
		record.put("code", "0");
		record.put("leaf", true);
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(record);
		for(Organization organization : organizations){
			jsonArray.add(walkOrganizationTree(organization,false));
		}
		return render(jsonArray);
	}
	public String getOrganizationAndAllTree() throws Exception{
		List<Organization> organizations = organizationService.listTree("1");
		JSONArray jsonArray = new JSONArray();
		JSONObject record = new JSONObject();
		record.put("id", "00");
		record.put("text", "全行");
		record.put("code", "all");
		record.put("leaf", true);
		jsonArray.add(record);
		for(Organization organization : organizations){
			jsonArray.add(walkOrganizationTree(organization,false));
		}
		return render(jsonArray);
	}
	
	public String getOrganizationByRole() throws Exception{
		JSONArray ja = new JSONArray();
		if(getNode().equals("0")||getNode().equals(" ")){
			String roleCode = (String) getSessionMap().get(Helper.ROLE_CODE);
			String organizationId = Long.toString((Long)getSessionMap().get(Helper.ORGANIZATION_ID));
			if(roleCode.contains("ALLM")){  //总行管理员
//				ja = walkTree(organizationService.getRoot());
//				JSONObject record = new JSONObject();
//				record.put("id", "00");
//				record.put("text", "全行");
//				record.put("code", "all");
//				record.put("leaf", true);
//				ja.add(record);
				List<Organization> list = organizationService.getOrganizationByTypeForBranch("organizationType_02");
				for(Organization o : list){
					JSONObject jsonO = toJsonObjectOrganization(o, false);
					ja.add(jsonO);
				}
			}else if(roleCode.contains("AM")){//区域管理员
				Organization organization = organizationService.get(organizationId);
				if(organization.getParent().getType().getId().equals("organizationType_02")){
					organization = organization.getParent();
				}
				JSONObject jsonO = toJsonObjectOrganization(organization, false);
				ja.add(jsonO);
			}else if(roleCode.contains("BM")){//网点管理员
				Organization organization = organizationService.get(organizationId);
				JSONObject jsonO = new JSONObject();
				jsonO.put("id", organization.getId());
				jsonO.put("text", organization.getName());
				jsonO.put("leaf", true);
				jsonO.put("value", organization.getId());
//				JSONObject jsonO = toJsonObjectOrganization(organization,false);
				ja.add(jsonO);
			}
		}else{
			ja = walkTree(organizationService.get(getNode()));
		}
		return render(ja);
	}
	
	public String updateStructure() throws Exception{
		String nodeId = p("nodeId");
		String parentNodeId = p("parentNode");
		Organization organization = organizationService.get(nodeId);
		Organization organizationP;
		if(parentNodeId.equals("0")){
			organizationP = organizationService.get("1");
		}else{
			organizationP = organizationService.get(parentNodeId);
		}
		organization.setParent(organizationP);
		organizationService.addOrUpdate(organization);
		
		return null;
	}
	//================== setter and getter ===================
	
	@Override
	public GenericService<Organization> getDefService() {
		return organizationService;
	}
	
	public int[] getCheckedId() {
		return checkedId;
	}

	public void setCheckedId(int[] checkedId) {
		this.checkedId = checkedId;
	}
}