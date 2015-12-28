package com.fortunes.fjdp.admin.action;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.fortunes.core.CachedValue;
import net.fortunes.core.Helper;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.exception.DeleteForeignConstrainException;
import net.fortunes.util.PinYin;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.faas.exception.DictTypeException;
import com.fortunes.faas.model.Area;
import com.fortunes.faas.service.AreaService;
import com.fortunes.faas.service.ExcelHelperService;
import com.fortunes.faas.service.OperateCmdService;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.OrganizationService;
import com.fortunes.fjdp.admin.service.RoleService;
import com.fortunes.fjdp.admin.service.UserService;

@SuppressWarnings("serial")
@Component @Scope("prototype")
public class EmployeeAction extends GenericAction<Employee> {
	
	private static final String PHOTO_DIR = "E:/app/photo/";
	@Resource private EmployeeService employeeService;
	@Resource private ExcelHelperService excelHelperService;
	private File photoFile;
	private File excelFile;
	public static final String PHOTO_URL_PREFIX = "/employee/photo?photoId=";
	@Resource private OrganizationService organizationService;
	@Resource private OperateCmdService operateCmdService;
	@Resource private UserService userService;
	
	@Override
	public String list() throws Exception {
	String roles = (String) getSessionMap().get(Helper.ROLES_STRING);
	String[] rolesArr = roles.split(",");
	queryMap.put("roleString", roles);
	Employee e = authedUser.getEmployee();
	if(rolesArr.length==1&&roles.contains("普通员工")){
	queryMap.put("employee", Long.toString(e.getId()));
	}else{
	queryMap.put("organization", AdminHelper.getOrganizationId()+"");
	}
	String tt = (String) getSessionMap().get(Helper.ROLE_CODE);
	queryMap.put("turnAway", p("turnAway"));
	return super.list();
	}
	 
	protected void setEntity(Employee employee) throws ParseException{
		employee.setCode(p("code"));
		employee.setCard(p("card"));
		employee.setName(p("name"));
		employee.setEmail(p("email"));
		employee.setPhone(p("phone"));
		employee.setMobile(p("mobile"));
		employee.setPhotoId(p("photoId"));
		employee.setSex(AdminHelper.toDict(p("sex")));
		employee.setStatus(AdminHelper.toDict(p("status")));
		employee.setPosition(AdminHelper.toDict(p("position")));
		employee.setPostType(AdminHelper.toDict(p("postType")));
		employee.setEducation(AdminHelper.toDict(p("education")));
		employee.setPeopleType(AdminHelper.toDict(p("peopleType")));
		employee.setHireDate(AdminHelper.toDate(p("hireDate")));
		employee.setBirthday(AdminHelper.toDate(p("birthday")));
		employee.setJoinWorkDate(AdminHelper.toDate(p("joinWorkDate")));
		employee.setTurnAway(p("turnAway").equals("1")?true:false);
		employee.setTurnAwayDate(AdminHelper.toDate(p("turnAwayDate")));
		employee.setOrganization(AdminHelper.toOrganization(p("organization")));
//		employee.getAreas().clear();
//		if(employee.getId()!=1){
//			List<Area> list = areaService.listTree("1");
//			employee.setAreas(list);
//		}
	}
	
	/**
	 * 覆盖父类的create
	 * 在新增员工选择了所在部门时,在"员工_部门表"中插入信息.
	 **/
	public String create() throws Exception{
		Employee e = getEntityClass().newInstance();
		setEntity(e);
		List<String> cards = employeeService.getCards();
		if(!cards.contains(e.getCard())){
			Employee employeeTemp = employeeService.getEmployee(e.getCode());
			if(null!=employeeTemp){
				setJsonMessage(true,"员工编号不能重复");
				return render(jo);
			}
			Employee employee = employeeService.add(e);
			employeeService.updateCache(employee);
			String primaryOrganization = p("primaryOrganization");
			if(primaryOrganization!=null&&primaryOrganization!=""){
				organizationService.addEmployee(primaryOrganization,e.getId()+"");
			}
			//添加命令
			try {
				employeeService.refresh(employee);
				operateCmdService.addOperateCmd(id, employee, "NEW");
			} catch (Exception e1) {
				return renderWarning(e1.getMessage());
			}
			jo.put(ENTITY_KEY, toJsonObject(e));
			setJsonMessage(true, e.toString().equals("")?
					"新增了一条记录!" : "新增了("+e+")的记录");
		}else{
			setJsonMessage(true, "员工卡号重复");
		}
		
		return render(jo);
	}
				
	@Override
	public String del() throws Exception {
		Employee employee = getDefService().get(id);
		//添加命令
//		operateCmdService.addOperateCmd(id, employee, "DEL");
		
		try {
			getDefService().del(employee);
			employeeService.removeCache(employee);
			setJsonMessage(true,"记录成功删除!");
		} catch (DeleteForeignConstrainException e) {
			return renderWarning("该项被其它数据所引用，不能删除！");
		}
		return render(jo);
	}

	/**
	 * 覆盖父类的update
	 * 若选择了部门修改项,先把此员工从原来的部门中移除,再加入到新的部门.
	 **/
	public String update() throws Exception{
		String massage = "";
		Employee entity = getDefService().get(id);
		String card = p("card");
		String organizationId = p("organization");
		String oldCard = entity.getCard();
		List<String> cards = employeeService.getCards();
		if(entity.getOrganization()!=null)
		    organizationService.removeEmployee(entity.getOrganization().getId()+"", id);
		setEntity(entity);
		String primaryOrganization = p("organization");
		
		//添加操作命令道命令表
		try {
			if(!entity.getTurnAway()&&!card.equals(oldCard)){
				if(cards.contains(card)){
					massage = "员工卡号重复";
				}else{
					if(primaryOrganization!=null&&primaryOrganization!="")
					    organizationService.addEmployee(primaryOrganization,entity.getId()+"");
					Employee employee = employeeService.updateEmployee(entity);
					employeeService.updateCache(employee);
					operateCmdService.addOperateCmd(id, employee, "UPDATE");
					massage = entity.toString().equals("")?
							"更新了一条记录!" : "更新了("+entity+")的记录";
				}
			}else if(!entity.getTurnAway()&&card.equals(oldCard)){
				if(primaryOrganization!=null&&primaryOrganization!="")
				    organizationService.addEmployee(primaryOrganization,entity.getId()+"");
				Employee employee = employeeService.updateEmployee(entity);
				employeeService.updateCache(employee);
			}
			else if(entity.getTurnAway()){
				if(primaryOrganization!=null&&primaryOrganization!="")
				    organizationService.addEmployee(primaryOrganization,entity.getId()+"");
				Employee employee = employeeService.updateEmployee(entity);
				employeeService.updateCache(employee);
				operateCmdService.addOperateCmd(id, employee, "DEL");
				massage = entity.toString().equals("")?
						"更新了一条记录!" : "更新了("+entity+")的记录";
			}
		} catch (Exception e) {
			return renderWarning(e.getMessage());
		}
		setJsonMessage(true, massage);
		jo.put(ENTITY_KEY, toJsonObject(entity));
		return render(jo);
	}
	
	protected JSONObject toJsonObject(Employee e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("card",e.getCard());
		record.put("code", e.getCode());		
		record.put("sex", e.getSex());
		record.put("status", e.getStatus());
		record.put("position", e.getPosition());
		record.put("postType", e.getPostType());
		record.put("education", e.getEducation());
		record.put("peopleType", e.getPeopleType());
		record.put("name", e.getName());
		record.put("phone", e.getPhone());
		record.put("mobile", e.getMobile());
		record.put("email", e.getEmail());
		record.put("joinWorkDate", e.getJoinWorkDate());
		record.put("hireDate", e.getHireDate());
		record.put("turnAway", e.getTurnAway());
		record.put("turnAwayDate", e.getTurnAwayDate());
		record.put("birthday", e.getBirthday());
		record.put("photoId",e.getPhotoId());
		record.put("organization", e.getOrganization());
		record.put("annualLeave", e.getAnnualLeave());
//		JSONArray ja = new JSONArray();
//		for(Area area : e.getAreas()){
//			ja.add(new AdminHelper().put("area", area));
//		}
//		record.put("areas", ja);
		return record.getJsonObject();
	}
	
	/**
	 * 用于雇员选择的下拉菜单,可以用于拼音首字母和关键字查询
	 * @return　json
	 * @throws Exception
	 */
	public String getEmployees() throws Exception{
		List<Employee> employeeList = getDefService().getListData().getList();
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
	
	/**
	 * 查询没有被分配用户的雇员
	 * @return json
	 * @throws Exception
	 */
	public String getEmployeesUnAssign() throws Exception{
		List<Employee> employees = employeeService.getEmployeesUnAssign();
		JSONArray ja = new JSONArray();
		for(Employee employee : employees){
			JSONObject record = new JSONObject();
			record.put("id", employee.getId());
			record.put("text", employee.getName());
			record.put("code", employee.getCode());
			ja.add(record);
		}
		jo.put("data", ja);
		return render(jo);
	}
	
	public String getEmployeesByOrganization()throws Exception{
		String[] orgArray = userService.getUserOperateOrganizationToString();
		List<Employee> employeeList = employeeService.getEmployeeByOrganizations(orgArray);
		JSONArray ja = getEmployeeArray(employeeList);
		jo.put("data", ja);
		return render(jo);
	}
	/**
	 * 在考勤记录查询中人员的查询
	 * @return
	 * @throws Exception
	 */
	public String getAttEmployeeByOrganization()throws Exception{
		List<Employee> employeeList = new ArrayList<Employee>();
		String[] organizations = userService.getUserOperateOrganizationToString();
		if(organizations.length==0){
			JSONArray roleArray = JSONArray.fromObject(this.getSessionMap().get(Helper.ROLE_CODE));
			if(roleArray.size() == 1 && roleArray.contains(Role.SYSTEM_GENERAL_STAFF)){
				employeeList.add(authedUser.getEmployee());
			}else{
				employeeList = employeeService.getEmployeeByOrgId(Helper.getOrganizationId());
			}
		}else{
			employeeList = employeeService.getEmployeeByOrganizations(organizations);
		}
		JSONArray ja = getEmployeeArray(employeeList);
		jo.put("data", ja);
		return render(jo);
	}
	
	public JSONArray getEmployeeArray(List<Employee> employeeList)throws Exception{
		JSONArray ja = new JSONArray();
		if(query != null && query != ""){
			for(Employee e : employeeList){
				String namePy = PinYin.toPinYinString(e.getName());
				if(namePy.startsWith(getQuery().toUpperCase()) || e.getName().startsWith(getQuery())){
					ja.add(getEmployeeObject(e));
				}
			}
		}else{
			for(Employee e : employeeList){
				ja.add(getEmployeeObject(e));
			}
		}
		return ja;
	}
	
	public JSONObject getEmployeeObject(Employee e) throws Exception{
		String namePy = PinYin.toPinYinString(e.getName());
		JSONObject record = new JSONObject();
		record.put("id", Long.valueOf(e.getId()));
		record.put("text", e.getName());
		record.put("code", e.getCode());
		record.put("pinyin", namePy);
		record.put("orgName", e.getOrganization().getShortName());
		return record;
	}
	public String getEmployeeByOrganization()throws Exception{
		List<Employee> emps = employeeService.getEmployeeByOrgId(Long.parseLong(p("relativeId")));
		jo.put("data", getEmpJsonArray(emps));
		return render(jo);
	}
	
	public String getEmployeeByCode()throws Exception{
		String employeeCode = p("employeeCode");
		Employee employee = employeeService.getEmployee(employeeCode);
		JSONObject record = new JSONObject();
		record.put("id", employee.getId());
		record.put("text", employee.getName());
		return render(record);
	}
	
	public JSONArray getEmpJsonArray(List<Employee> list){
		JSONArray ja = new JSONArray();
		for(Employee e : list){
			JSONObject record = new JSONObject();
			record.put("id", e.getId());
			record.put("text", e.getName());
			ja.add(record);
		}
		return ja;
	}
	
	public String setupPhoto() throws Exception {
		String uuid = Tools.uuid();
		//final String photoDir = configService.get(ConfigKey.PHOTO_DIR);
		FileUtils.copyFile(photoFile, new File(PHOTO_DIR+uuid+".jpg"));
		jo.put("photoId", uuid);
		setJsonMessage(true,"设置人员相片成功!");
		return render(jo.toString());
	}
	
	public String photo() throws Exception{
		//final String photoDir = configService.get(ConfigKey.PHOTO_DIR);
		return render(FileUtils.readFileToByteArray(new File(PHOTO_DIR+p("photoId")+".jpg")), "image/jpeg");
	}
	
	public String uploadPhoto()throws Exception {
		String uuid = Tools.uuid();
		File file = new File(PHOTO_DIR+uuid+".jpg");
		
		int size = 0;
		int len = 0;
		byte[] tmp = new byte[100000];
		
		response.setContentType("application/octet-stream");
		InputStream is = getRequest().getInputStream();
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
		while ((len = is.read(tmp)) != -1) {
			dos.write(tmp, 0, len);
			size += len;
		}
		dos.flush();
		dos.close();
		jo.put("photoId", uuid);
		setJsonMessage(true,"设置人员相片成功!");
		return render(jo.toString());
	}
	public String search() throws Exception {
		
		Map<String, CachedValue> map = employeeService.search(query, isMatchPinyin());
		
		return render(Helper.toJsonObject(map));
	}
	/**
	 * 请假审批人的查询
	 * @return
	 * @throws Exception
	 */
	public String getHolidayApplyByOrganization()throws Exception{
		String organizationId = p("relativeId");
		if(!organizationId.equals("H-0")){
			List<Employee> list = employeeService.getHolidayApplyByOrganization(organizationId);
			JSONArray ja = new JSONArray();
			for(Employee e : list){
				JSONObject record = new JSONObject();
				record.put("id", e.getId());
				record.put("text", e.getName());
				record.put("code", e.getCode());
				ja.add(record);
			}
			jo.put("data", ja);
		}
		return render(jo);
	}
	
	/**
	 * 导入员工信息之前，备份导出
	 * @return
	 * @throws Exception
	 */
	public String exportEmployee()throws Exception{
		String fileName = "员工信息备份.xls";
		File file = excelHelperService.exportEmployee(fileName);
		byte[] bytes = FileUtils.readFileToByteArray(file);
		return renderFile(bytes, fileName);
	}
	
	/**
	 * 通过Excel导入员工信息
	 * @return
	 * @throws Exception
	 */
	public String exportExcelToEmployee()throws Exception{
		try{
			List<Employee> list = excelHelperService.excelToEmployee(excelFile);
			employeeService.bathImportEmployee(list);
			setJsonMessage(true, "导入成功");
		}catch (DictTypeException e){
			setJsonMessage(true, e.toString());
		}catch (Exception e){
			e.printStackTrace();
			setJsonMessage(true, "导入员工时出错");
		}
		return render(jo.toString());
	}
	public String importSalaryToEmployee() throws Exception {
		try {
			excelHelperService.importSalaryToEmployee(excelFile);
			setJsonMessage(true, "导入成功");
		} catch (Exception e) {
			e.printStackTrace();
			setJsonMessage(true, "导入基本工资失败，请检查工资是否是数字格式");
		}
		return render(jo.toString());
	}
	
	public String annualLeave()throws Exception{
		try{
			String organization = p("organization");
			String employee = p("employee");
			List<Employee> emps = new ArrayList<Employee>();
			if(StringUtils.isNotEmpty(employee)&&StringUtils.isNotEmpty(organization)){
				emps.add(this.getDefService().get(employee));
				 employeeService.yearTaskOnly(emps);
				
			}else if (!StringUtils.isNotEmpty(employee)&&StringUtils.isNotEmpty(organization)){
				emps = employeeService.getEmployreeByOrganization(organization);
				this.employeeService.yearTask(emps);
			}else{
				emps = employeeService.getAll();
				this.employeeService.yearTask(emps);
			}
			
			setJsonMessage(true,"年假计算成功");
		}catch(Exception e){
			e.printStackTrace();
			setJsonMessage(true,"年假计算出错");
		}
		return render(jo.toString());
	}
	//获取行长办公室所有人员
	public String getBoss()throws Exception{
		List<Employee> bosses = employeeService.getBossAffirm();
		jo.put("data", getEmpJsonArray(bosses));
		return render(jo.toString());
	}
	
	public String getLeader()throws Exception{
		List<Employee> leaders = employeeService.getEmployeeByOrgId(Long.parseLong("2"));
		JSONArray ja = new JSONArray();
		JSONObject cancel = new JSONObject();
		cancel.put("id", "H-0");
		cancel.put("text", "取消选择");
		ja.add(cancel);
		for(Employee e : leaders){
			if(!e.getCode().equals("440771993110001")){
				JSONObject record = new JSONObject();
				record.put("id", e.getId());
				record.put("text", e.getName());
				ja.add(record);
			}
		}
		jo.put("data", ja);
		return render(jo.toString());
	}
	
	public String getLeaderB()throws Exception{
		List<Employee> leaders = employeeService.getEmployeeByOrgId(Long.parseLong("2"));
		JSONArray ja = new JSONArray();
		JSONObject cancel = new JSONObject();
		cancel.put("id", "H-0");
		cancel.put("text", "取消选择");
		ja.add(cancel);
		for(Employee e : leaders){
			if(e.getCode().equals("440771993110001")){
				JSONObject record = new JSONObject();
				record.put("id", e.getId());
				record.put("text", e.getName());
				ja.add(record);
			}
		}
		jo.put("data", ja);
		return render(jo.toString());
	}
	
	//================== setter and getter ===================
	
	@Override
	public GenericService<Employee> getDefService() {
		return this.employeeService;
	}

	public void setEmployeeService(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	public EmployeeService getEmployeeService() {
		return employeeService;
	}
	public File getPhotoFile() {
		return photoFile;
	}

	public void setPhotoFile(File photoFile) {
		this.photoFile = photoFile;
	}

	public File getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}

	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	public OrganizationService getOrganizationService() {
		return organizationService;
	}
}
