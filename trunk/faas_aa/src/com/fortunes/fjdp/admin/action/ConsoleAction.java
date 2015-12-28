package com.fortunes.fjdp.admin.action;

import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import misc.InitDb;
import net.fortunes.core.Helper;
import net.fortunes.core.action.BaseAction;
import net.fortunes.util.Tools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.Area;
import com.fortunes.faas.model.AttendanceLog;
import com.fortunes.faas.model.ShiftConfig;
import com.fortunes.faas.model.WorkingArea;
import com.fortunes.faas.service.AreaService;
import com.fortunes.faas.service.AttShiftService;
import com.fortunes.faas.service.AttendanceLogService;
import com.fortunes.faas.service.ExcelHelperService;
import com.fortunes.faas.service.OperateCmdService;
import com.fortunes.faas.service.ShiftConfigService;
import com.fortunes.faas.service.WorkingAreaService;
import com.fortunes.faas.util.ClosureService;
import com.fortunes.faas.vo.AttshiftsOrg;
import com.fortunes.faas.vo.EmployeeClosure;
import com.fortunes.fjdp.Constants;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.Privilege;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.model.Config.ConfigKey;
import com.fortunes.fjdp.admin.service.ConfigService;
import com.fortunes.fjdp.admin.service.DictService;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.MenuService;
import com.fortunes.fjdp.admin.service.OrganizationService;
import com.fortunes.fjdp.admin.service.PrivilegeService;
import com.fortunes.fjdp.admin.service.RoleService;
import com.fortunes.fjdp.admin.service.UserService;

import freemarker.template.utility.StringUtil;

@Component @Scope("prototype")
public class ConsoleAction extends BaseAction implements ApplicationContextAware{
	
	public static final String REBUILD_DB = "rebuildDb";
	public static final String DICT_XML_PATH = "/dict.xml";
	public static final String FUNC_XML_PATH = "/function.xml";
	
	private ApplicationContext applicationContext;
	
	@Resource public PrivilegeService privilegeService;
	@Resource public EmployeeService employeeService;
	@Resource public OrganizationService organizationService;
	@Resource public RoleService roleService;
	@Resource public UserService userService;
	@Resource public DictService dictService;
	@Resource public MenuService menuService;
	@Resource public ConfigService configService;
	@Resource public AreaService areaService;
	@Resource private AttShiftService attShiftService;
	@Resource private ClosureService closureService;
	@Resource private ExcelHelperService excelHelperService;
	@Resource private WorkingAreaService workingAreaService;
	@Resource private ShiftConfigService shiftConfigService;
	@Resource private OperateCmdService operateCmdService;
		
	public String initDb() throws Exception{
		AnnotationSessionFactoryBean annotationSessionFactoryBean = 
			(AnnotationSessionFactoryBean)applicationContext.getBean("&sessionFactory");
		annotationSessionFactoryBean.dropDatabaseSchema();
		annotationSessionFactoryBean.createDatabaseSchema();
		
		
		menuService.initToDb(new InputStreamReader(
				InitDb.class.getResourceAsStream(FUNC_XML_PATH),"utf-8"));
		dictService.initToDb(new InputStreamReader(
				InitDb.class.getResourceAsStream(DICT_XML_PATH),"utf-8"));
		
		//初始化系统参数
		Map<ConfigKey, String> maps = new EnumMap<ConfigKey, String>(ConfigKey.class);
		maps.put(ConfigKey.APP_ROOT_DIR, "/home/weblogic");
		maps.put(ConfigKey.ADMIN_EMAIL, "admin@sz.pbc.org.cn");
		maps.put(ConfigKey.TEMP_UPLOAD_DIR, "/upload");
		maps.put(ConfigKey.PHOTO_DIR, "E:/app/upload");
		maps.put(ConfigKey.LATE_GREATER_THAN_TIME, "60");
		maps.put(ConfigKey.LATE_GREATER_THAN_TIME_FOR_PARAM, "0.5");
		maps.put(ConfigKey.EARLY_GREATER_THAN_TIME, "60");
		maps.put(ConfigKey.EARLY_GREATER_THAN_TIME_FOR_PARAM, "0.5");
		maps.put(ConfigKey.ATTSHIFT_PUSH_CARD_ONLYONE, "0.5");
		maps.put(ConfigKey.LATEOREARLY_DEDUCT_BASIC_SALARY_STANDARD, "20");
		maps.put(ConfigKey.ABSENT_DEDUCT_BASIC_SALARY_STANDARD, "300");
		maps.put(ConfigKey.WORK_TO_MORROW_EARLYEST_AHEAD, "1");
		maps.put(ConfigKey.WORK_TO_MORROW_LATEST_REMIT, "1");
		maps.put(ConfigKey.START_WORK_IN_MORNING_TIME, "08:30");
		maps.put(ConfigKey.OVER_WORK_IN_AFTERNOON_TIME, "17:30");
		configService.initConfigs(maps);
		
		//新建权限
		List<Privilege> pList = privilegeService.getListData().getList();	
		
		//新建角色
		Role admin = new Role("系统管理员",Role.SYSTEM_ROLE,Role.SYSTEM_OPERATETYPE_GENERAL,null);
		Role typeMan = new Role("业务人员",Role.SYSTEM_ROLE,Role.SYSTEM_OPERATETYPE_GENERAL,null);
		Role systemMan = new Role("考勤管理员",Role.SYSTEM_ROLE,Role.SYSTEM_OPERATETYPE_GENERAL,Role.SYSTEM_MANAGER);
		Role leaveApprove = new Role("请假审批人",Role.SYSTEM_ROLE,Role.SYSTEM_OPERATETYPE_LEAVE,null);
		Role feedReader = new Role("业务审批人员",Role.SYSTEM_ROLE,Role.SYSTEM_OPERATETYPE_GENERAL,null);
		Role employeeUser = new Role("普通员工",Role.SYSTEM_ROLE,Role.SYSTEM_OPERATETYPE_GENERAL,Role.SYSTEM_GENERAL_STAFF);
		Role branchMan = new Role("网点管理员",Role.SYSTEM_ROLE,Role.SYSTEM_OPERATETYPE_GENERAL,"BM");
		Role areaMan = new Role("区域管理员",Role.SYSTEM_ROLE,Role.SYSTEM_OPERATETYPE_GENERAL,"AM");
		Role headOfficeMan = new Role("总行管理员",Role.SYSTEM_ROLE,Role.SYSTEM_OPERATETYPE_GENERAL,"ALLM");
		roleService.add(admin);
		roleService.add(typeMan);
		roleService.add(systemMan);
		roleService.add(leaveApprove);
		roleService.add(feedReader);
		roleService.add(employeeUser);
		roleService.add(branchMan);
		roleService.add(areaMan);
		roleService.add(headOfficeMan);
		//关联角色和权限
		admin.setPrivileges(pList);
	
		//新建员工
		Employee adminEmployee = new Employee("00","超级管理员");
		employeeService.add(adminEmployee);
		
		//新建用户 
		User adminUser = new User("admin",Tools.encodePassword("admin"),adminEmployee.getName());
		
		//关联用户和角色,用户和员工
		adminUser.setEmployee(adminEmployee);
		
		List<Role> roles = new ArrayList<Role>();
		roles.add(admin);
		adminUser.setRoles(roles);
		
		userService.add(adminUser);
		
		WorkingArea rootArea = new WorkingArea(null, "支行");
		workingAreaService.add(rootArea);
		WorkingArea work1 = new WorkingArea(rootArea,"大堂经理","MANAGER");
		WorkingArea work2 = new WorkingArea(rootArea,"大堂副理","ASS-MANAGER");
		workingAreaService.add(work1);
		workingAreaService.add(work2);
		
		//新建部门 
		Organization root = new Organization(null,"组织root","");
		organizationService.add(root);
//		Area areaRoot = new Area(null,"root");
//		areaService.add(areaRoot);
//		
//		Area area3 = new Area(areaRoot,"公司总部");
//		areaService.add(area3);
//		
//		
		return render(jo);
	}
	
	public String rebuildDb() throws Exception{
		menuService.initToDb(new InputStreamReader(
				InitDb.class.getResourceAsStream(FUNC_XML_PATH),"utf-8"));
		dictService.initToDb(new InputStreamReader(
				InitDb.class.getResourceAsStream(DICT_XML_PATH),"utf-8"));
		return render(jo);
	}
	
	public String executeHql() throws Exception{
		employeeService.getDefDao().getHibernateTemplate().find(p("hql"));
		return render(jo);
	}
	public String attShiftDayCompute() throws Exception {
		String fdate = p("date");
		Date date = Tools.string2Date(fdate);
		try {
			attShiftService.delDayAttShift(date);
			attShiftService.dayTask(date);
			setJsonMessage(true, "计算完成并成功");
		} catch (Exception e) {
			setJsonMessage(true, "计算出错");
		}
		return render(jo);
	}
	
	public String attShiftDayComputeByNode() throws Exception{
		try{
			String fdate = p("date");
			String organizationId = p("organization");
			String employeeId = p("employee");
			Date date = Tools.stringToDate(fdate);
			if(!StringUtils.isNotEmpty(organizationId)){
				attShiftService.analyAttshiftForADay(date);
			}else if(!StringUtils.isNotEmpty(employeeId)&&StringUtils.isNotEmpty(organizationId)){
				List<Employee> employees = employeeService.getEmployeeByOrgId(Long.parseLong(organizationId));
				attShiftService.analyAttshiftForDayAndEmployees(date, employees);
			}else if(StringUtils.isNotEmpty(employeeId)){
				attShiftService.analyAttshiftForEmployee(date,employeeId);
			}
		}catch (Exception e) {
			// TODO: handle exception
			setJsonMessage(true,"计算失败");
		}
		setJsonMessage(true, "计算成功");
		return render(jo);
	}
	
	public String clearEmployeeoperatedOrganization() throws Exception {
		String organizationId = p("organization");
		String employeeId = p("employee");
		if(StringUtils.isNotEmpty(employeeId)){
			Employee emp = employeeService.get(employeeId);
			User user = emp.getUser();
			userService.clearEmployeeOperated(user.getId());
			setJsonMessage(true, "清除员工可操作部门成功");
		}else{
			setJsonMessage(true,"员工不能没有，请选择指定员工");
		}
		return render(jo);
	}
	
	public String setCard()throws Exception{
		employeeService.setCard();
		return render(jo);
	}
	public String deviceClear()throws Exception{
		operateCmdService.updateAllStatus();
		return render(jo);
	}
	
//	public String attShiftMonthCompute() throws Exception {
//		Calendar c = Calendar.getInstance();
//		c.set(Calendar.YEAR, Integer.parseInt(p("year")));
//		c.set(Calendar.MONTH, Integer.parseInt(p("month")));
//		Date date = c.getTime();
//		try {
//			attShiftService.monthTask(date);
//			setJsonMessage(true, "统计成功");
//		} catch (Exception e) {
//			setJsonMessage(true, "统计出错");
//		}
//		return render(jo);
//	}
	
	public String clearBranchTemplete() throws Exception{
		String year = p("year");
		String month = p("month");
		String organization = p("organization");
		shiftConfigService.deleteConfigByOrg(year,month, organization);
		setJsonMessage(true, "清除成功");
		return render(jo);
	}
	
	public String syncClosureEmployeesCard() throws Exception {
		closureService.syncCards();
		setJsonMessage(true, "更新成功");
		return render(jo);
	}
	
	public String getClosureEmployeesCard() throws Exception{
		String fileName = "全行员工卡号.xls";
		//新员工卡号导出
//		List<EmployeeClosure> empcs = closureService.getNewClosureEmployees();
		
		//获得门禁系统中全部员工卡号
		List<EmployeeClosure> empcs = closureService.getClosureEmployees();
		File file = excelHelperService.ExportNewClosureEmployees(fileName,empcs);
		byte[] bytes = FileUtils.readFileToByteArray(file);
		return renderFile(bytes,fileName);
	}
	
	public String syncLogs() throws Exception {
		String fdate = p("date");
		Date date = Tools.stringToDate(fdate);
		try {
			closureService.syncLogsOfDate(date);
			setJsonMessage(true, "同步完成并成功");
		} catch (Exception e) {
			e.printStackTrace();
			setJsonMessage(true, "同步失败");
		}
		return render(jo);
	}
	
	public String area2Orgs() throws Exception {
		Map<String,List<Organization>> hash = excelHelperService.areaOrganizationRel(area2OrgsFile);
//		areaService.area2Orgs(hash);
//		jo.put(SUCCESS_KEY, true);
		return render(jo.toString());
	}
	
	//添加一个导入组织机构的方法
	public String exportExcelToOrganization() throws Exception{
		try{
			
			List<Organization> organizationList=excelHelperService.excelToOrganization(organizationFile);
			organizationService.bathImportOrganization(organizationList);
		}catch(Exception e){
			
		}
		setJsonMessage(true, "倒入组织机构成功");
		return render(jo.toString());
	}
	
	public String importUserAndCard()throws Exception{
		try{
			excelHelperService.importUserAndCard(userFile,1);
		}catch (Exception e) {
			// TODO: handle exception
		}
		setJsonMessage(true, "员工生成成功");
		return render(jo);
	}
	
	public String importUserAndCard1()throws Exception{
		try{
			excelHelperService.importUserAndCard(userFile,2000);
		}catch (Exception e) {
			// TODO: handle exception
		}
		setJsonMessage(true, "员工生成成功");
		return render(jo);
	}
	public String importUserAndCard2()throws Exception{
		try{
			excelHelperService.importUserAndCard(userFile,1);
		}catch (Exception e) {
			// TODO: handle exception
		}
		setJsonMessage(true, "员工生成成功");
		return render(jo);
	}
	public String importUserAndCard3()throws Exception{
		try{
			excelHelperService.importUserAndCard(userFile,2);
		}catch (Exception e) {
			// TODO: handle exception
		}
		setJsonMessage(true, "员工生成成功");
		return render(jo);
	}
	public String importUserAndCard4()throws Exception{
		try{
			excelHelperService.importUserAndCard(userFile,3);
		}catch (Exception e) {
			// TODO: handle exception
		}
		setJsonMessage(true, "员工生成成功");
		return render(jo);
	}
	
	public String batchCreateAuto() throws Exception{
		try{
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
			SimpleDateFormat sdf = new SimpleDateFormat("HH:ss:mm");
			List<Long> list = employeeService.wipeBranchEmployee();
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR,Integer.parseInt(p("year")));
			c.set(Calendar.MONTH,Integer.parseInt(p("month")));
			Date sDate = Tools.string2Date(sf.format(c.getTime())+"-01");
			Date eDate = Tools.string2Date(sf.format(c.getTime())+"-"+c.getActualMaximum(c.DAY_OF_MONTH));
			c.setTime(sDate);
			Date sTime = sdf.parse("08:30:00");
			Date eTime = sdf.parse("17:30:00");
			while (c.getTime().before(eDate) || c.getTime().compareTo(eDate)==0) {
				Date d = c.getTime();
				if(d.getDay() !=0 && d.getDay()!=6){
					for(Long e : list){
						ShiftConfig config = shiftConfigService.findOrCreate(d,AdminHelper.toEmployee(e+""));
						config.setEmployee(AdminHelper.toEmployee(e+""));
						config.setDate(d);
						config.setWeekEnd((d.getDay() == 0 || d.getDay() == 6) ? true : false);
						config.setStartTime(sTime);
						config.setEndTime(eTime);
						config.setNextDay(false);
						shiftConfigService.add(config);
					}
				}
				c.add(c.DATE, 1);
				
			}
			setJsonMessage(true,"排班成功");
		}catch(Exception e){
			setJsonMessage(true,"排班失败");
		}
		return render(jo);
	}
	
	public String creatUser()throws Exception{
		List<Employee> list = employeeService.getEmployeesUnAssign();
		List<Role> roles = new ArrayList<Role>();
		Role role = roleService.getRoleByCode("GENERAL_STAFF");
		roles.add(role);
		for(Employee e : list){
			User user = new User();
			user.setDisplayName(e.getName());
			user.setName(e.getCode());
			user.setPassword(Tools.encodePassword(e.getCode()));
			user.setEmployee(e);
			user.setRoles(roles);
			userService.add(user);
		}
		return render(jo);
	}
	public String yearTask()throws Exception{
		List<Employee> emps = employeeService.getAll();
		employeeService.yearTask(emps);
		return render(jo);
	}
	
	/**
	 * 清除员工修改排班
	 * @return
	 * @throws Exception
	 */
	public String clearOldBranchTemplete() throws Exception {
		String year = p("year");
		String month = p("month");
		String organization = p("organization");
		String employee = p("employee");
		if(StringUtils.isEmpty(organization)){
			setJsonMessage(true,"请选择部门，部门不能为空，否则无法选择员工");
		}else if(StringUtils.isEmpty(employee)){
			setJsonMessage(true,"员工不能为空，请选择员工");
		}else{
			shiftConfigService.deleteOldScheduleByOrg(year, month,employee);
			setJsonMessage(true, "清除成功");
		}
		
		return render(jo);
	}

	
	//用于测试统计数据
	public String getTotal() throws Exception{
		List<Organization> orgs = organizationService.getSimpleOrg("1");
		for(Organization org : orgs){
			List<Organization> childs = organizationService.getSimpleOrg(Long.toString(org.getId()));
			int size = childs.size();
			if(size ==0){
				
//				AttshiftsOrg att = attShiftService.getAttshiftsOrg(org.getId()+"","2012","8");
//				System.out.println(org.getId()+"=="+org.getName()+"==========="+att.getHolidayPersonTotal()+"======"+att.getAnnualLeave());
			}else{
//				AttshiftsOrg att = attShiftService.getAttshiftsOrg(org.getId()+"","2012","8");
//				System.out.println(org.getId()+"=="+org.getName()+"==========="+att.getHolidayPersonTotal()+"======"+att.getAnnualLeave());
				for(Organization oo:org.getChildren()){
//					AttshiftsOrg atts = attShiftService.getAttshiftsOrg(oo.getId()+"","2012","8");
//					System.out.println(oo.getId()+"=="+oo.getName()+"==========="+att.getHolidayPersonTotal()+"======"+att.getAnnualLeave());
				}
			}
		}
		return render(jo);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
		
	}	
	
	private File area2OrgsFile;
	//添加属性
	private File organizationFile;
	
	private File userFile;

	public File getArea2OrgsFile() {
		return area2OrgsFile;
	}

	public void setArea2OrgsFile(File area2OrgsFile) {
		this.area2OrgsFile = area2OrgsFile;
	}
	
	//给属性添加相应的get和set方法
	public File getOrganizationFile(){
		return organizationFile;
	}
	
	public void setOrganizationFile(File organizationFile){
		this.organizationFile=organizationFile;
	}

	public File getUserFile() {
		return userFile;
	}

	public void setUserFile(File userFile) {
		this.userFile = userFile;
	}
	
	
}
