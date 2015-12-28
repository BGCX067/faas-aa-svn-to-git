package com.fortunes.fjdp.admin.action;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.Resource;

import net.fortunes.core.Helper;
import net.fortunes.core.ListData;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.faas.service.ExcelHelperService;
import com.fortunes.faas.vo.AttShifts;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.OrganizationService;
import com.fortunes.fjdp.admin.service.RoleService;
import com.fortunes.fjdp.admin.service.UserService;

@Component @Scope("prototype")
public class UserAction extends GenericAction<User> {
	
	@Resource private UserService userService;
	@Resource private ExcelHelperService excelHelperService;
	@Resource private RoleService roleService;
	@Resource private OrganizationService organizationService;
	@Resource private EmployeeService employeeService;
	
	
	private String[] checkedId;
	
	private File excelFile;
	
	public File getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}

	public String[] getCheckedId() {
		return checkedId;
	}

	public void setCheckedId(String[] checkedId) {
		this.checkedId = checkedId;
	}

	@Override
	public String list() throws Exception {
		queryMap.put("roleId", p("type"));
		return super.list();
	}
	
	protected void setEntity(User user){
		user.setName(p("userName"));
		user.setDisplayName(p("userDisplayName"));
		user.setEmployee(AdminHelper.toEmployee(p("employee")));
		//修改时不改变密码
		if(StringUtils.isNotEmpty(p("password"))){
			user.setPassword(Tools.encodePassword(p("password")));
		}
	}
	
	protected JSONObject toJsonObject(User user) throws Exception{
		AdminHelper record = new AdminHelper();
		record.put("id", user.getId());
		record.put("userName", user.getName());
		record.put("userDisplayName", user.getDisplayName());
		record.put("password", user.getPassword());
		record.put("password2", user.getPassword());
		record.put("employee", user.getEmployee());
		record.put("locked", user.isLocked());
		record.put("lastLoginTime", user.getLoginSession().getLastLoginTime());
		record.put("logined", user.getLoginSession().isLogined());
		record.put("organization", user.getEmployee().getOrganization());
		record.put("role", toRoleArray(user.getRoles()));
		return record.getJsonObject();
	}
	
	public JSONArray toRoleArray(List<Role> list)throws Exception{
		JSONArray ja=new JSONArray();
		for(Role role : list){
			JSONObject record=new JSONObject();
			record.put("id", role.getDbId());
			record.put("text", role.getName());
			ja.add(record);
		}
		return ja;
	}
	
	public String importUser()throws Exception{
		List<User> list = excelHelperService.excelToUser(excelFile);
		for(User u : list){
			userService.add(u);
		}
		setJsonMessage(true, "员工导入成功");
		return render(jo.toString());
	}
	
	
	public String getUsers() throws Exception{
		List<User> userList = getDefService().getListData().getList();
		JSONArray ja = new JSONArray();
		for(User user:userList){
			JSONObject record = new JSONObject();
			record.put("id", user.getId());
			record.put("text", user.getEmployee().getName());
			ja.add(record);
		}
		jo.put("data", ja);
		return render(jo); 
	}
	
	public String getUserIsSysManager()throws Exception{
		Role role = roleService.getRoleByCode(Role.SYSTEM_GENERAL_STAFF);
		queryMap.put("roleId", role.getDbId()+"");
		queryMap.put("type", "sysOperate");
		List<User> list = userService.getListData(query,queryMap,0,0).getList();
//		List<User> list = userService.getSomeUser();
		JSONArray ja = new JSONArray();
		for(User user : list){
			JSONObject record = new JSONObject();
//			Employee e = user.getEmployee();
			record.put("id", user.getId());
			record.put("name", user.getDisplayName());
			record.put("userName", user.getName());
			ja.add(record);
		}
		jo.put("data", ja);
		return render(jo);
	}
	
	
	public String lockUser() throws Exception{
		boolean flag = userService.lockOrUnlockUser(getId());
		jo.put("success", flag);
		return render(jo);
	}
	
	public String resetPassword() throws Exception{
		boolean flag = userService.resetPassword(getId(),Tools.encodePassword(p("password")));
		jo.put("success", flag);
		return render(jo);
	}
	
	//添加一个导出用户信息excel表格的方法
	public String exportUserRole()throws Exception{
		String fileName = "考勤系统用户报表";
	    query = p("queryName");
	    queryMap.put("roleId", p("roleType"));
	    ListData<User> listData = userService.getListData(query, queryMap, 0, 0);
		File file = excelHelperService.exportUserRoleExcel(fileName, listData.getList());
		byte[] bytes = FileUtils.readFileToByteArray(file);
		return renderFile(bytes, fileName+".xls");
	}
	
	@Override
	public String create() throws Exception {
		String userName = p("userName");
		boolean isExist = userService.checkUserName(userName, null);
		if (isExist){
			User user = (User)getEntityClass().newInstance();
			setEntity(user);
			String StrRoleArr = p("role");
			String roleArr[] = StrRoleArr.split(",");
			List<Role> roleList = new ArrayList<Role>();
			String as[];
			int j = (as = roleArr).length;
			for (int i = 0; i < j; i++){
				String id = as[i];
				Role role = AdminHelper.toRole(id);
				roleList.add(role);
			}

			user.setRoles(roleList);
			Employee e = employeeService.get(p("employee"));
			List<Organization> organizations = new ArrayList<Organization>();
			organizations.add(e.getOrganization());
			user.setOrganizations(organizations);
			userService.add(user);
			jo.put("entity", toJsonObject(user));
			setJsonMessage(true, user.toString().equals("") ? "新增了一条记录!" : (new StringBuilder("新增了(")).append(user).append(")的记录").toString());
			return render(jo);
		}else{
			setJsonMessage(false, (new StringBuilder(String.valueOf(userName))).append("已经存在").toString());
			return render(jo);
		}
	}
	
	@Override
	public String update() throws Exception {
		String userName = p("userName");
		boolean isExits = userService.checkUserName(userName, this.id);
		if (isExits){
			User user = (User)getDefService().get(this.id);
			setEntity(user);
			String StrRoleArr = p("role");
			String roleArr[] = StrRoleArr.split(",");
			List<Role> list = new ArrayList<Role>();
			if (StrRoleArr != null && StrRoleArr != ""){
				String as[];
				int j = (as = roleArr).length;
				for (int i = 0; i < j; i++){
					String id = as[i];
					Role role = AdminHelper.toRole(id);
					list.add(role);
				}

				user.setRoles(list);
			}else{
				user.setRoles(list);
			}
			userService.update(user);
			jo.put("entity", toJsonObject(user));
			setJsonMessage(true, user.toString().equals("") ? "更新了一条记录!" : (new StringBuilder("更新了(")).append(user).append(")的记录").toString());
			return render(jo);
		}else{
			setJsonMessage(false, (new StringBuilder("修改失败,用户")).append(userName).append("已经存在").toString());
			return render(jo);
		}
	}
	
	public String updateOrganizations()throws Exception{
		User user = userService.get(id);
		List<Organization> list = new ArrayList<Organization>();
		for(String code : getCheckedId()){
			list.add(AdminHelper.toOrganization(code));
		}
		user.setOrganizations(list);
		userService.update(user);
		setJsonMessage(true, user.toString().equals("")?"更新了一条记录":(new StringBuffer("更新了(")).append(user).append(")的记录").toString());
		return render(jo);
	}
	
	public String getLeaveEmp()throws Exception{
		queryMap.put("roleId", p("relativeId"));
		List<User> list = userService.getListData(query, queryMap, 0, 0).getList();
		JSONArray ja = new JSONArray();
		for(User user : list){
			JSONObject record = new JSONObject();
			Employee e = user.getEmployee();
			record.put("id", e.getId());
			record.put("text", e.getName());
			ja.add(record);
		}
		jo.put("data", ja);
		return render(jo);
	}
	//================== setter and getter ===================
	
	@Override
	public GenericService<User> getDefService() {
		return userService;
	}

	public ExcelHelperService getExcelHelperService() {
		return excelHelperService;
	}

	public void setExcelHelperService(ExcelHelperService excelHelperService) {
		this.excelHelperService = excelHelperService;
	}
	
	
}
