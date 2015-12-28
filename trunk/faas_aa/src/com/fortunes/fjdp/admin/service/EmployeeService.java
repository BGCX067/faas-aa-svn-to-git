package com.fortunes.fjdp.admin.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import net.fortunes.core.CachedValue;
import net.fortunes.core.ListData;
import net.fortunes.core.log.annotation.LoggerClass;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.PinYin;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hsqldb.lib.StringUtil;
import org.quartz.impl.jdbcjobstore.CloudscapeDelegate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.fortunes.faas.exception.DictTypeException;
import com.fortunes.faas.model.Device;
import com.fortunes.faas.model.HolidayApply;
import com.fortunes.faas.service.AttShiftService;
import com.fortunes.faas.service.HolidayApplyService;
import com.fortunes.faas.service.OperateCmdService;
import com.fortunes.faas.vo.AttshiftsOrg;
import com.fortunes.faas.vo.EmployeeClosure;
import com.fortunes.faas.vo.EmployeeTotal;
import com.fortunes.faas.vo.HolidayCountVO;
import com.fortunes.faas.vo.SickPeopleTotal;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.User;

@Component
@LoggerClass
public class EmployeeService extends GenericService<Employee> {
	public static String ALL_QUERY_KEY = "[allQuery]";
	protected Map<String, CachedValue> cache;
	@Resource
	UserService userService;
	@Resource
	AttShiftService attShiftService;

	// 添加两个方法支持excel表格导出
	public List<Employee> getNotTurnAwayAndIsAttendance() {
		String hql = "select e from Employee e where e.turnAway=false ";
		List<Employee> list = this.getDefDao().findByQueryString(hql);
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Employee> getEmployeeByOrganizations(String[] organizations) {
		StringBuffer sb = new StringBuffer(
				"select new Employee(e.id,e.code,e.name,org.id,org.shortName) from Employee e ,Organization org where e.organization.id = org.id ");
		if (organizations != null && organizations.length > 0) {
			sb.append("and e.organization.id in (");
			for (String organization : organizations) {
				sb.append(organization).append(",");
			}
			sb.append(organizations[0]).append(")");
		}
		sb.append(" and e.turnAway= false");

		List<Employee> list = this.getDefDao().findByQueryString(sb.toString());
		return list;
	}

	public Employee addEmployee(Employee entity) throws Exception {
		Employee employee = super.add(entity);
		// getDefDao().getHibernateTemplate().refresh(employee);
		return employee;
	}

	public Employee updateEmployee(Employee entity) {
		Employee employee = super.update(entity);
		getDefDao().getHibernateTemplate().refresh(employee);
		return employee;
	}

	public void updateEmployees(List<Employee> emplyees) {
		for (Employee e : emplyees) {
			this.update(e);
		}
	}

	/**
	 * 员工缓存 2011-03-10
	 * 
	 * @return Map<String, CachedValue>
	 */
	public Map<String, CachedValue> getCache() {

		if (cache == null) {
			cache = new HashMap<String, CachedValue>();

			List<Employee> employees = this.getEmployees();

			for (Employee element : employees) {
				addCache(element);
			}
			return cache;
		} else {
			return cache;
		}
	}

	/**
	 * 增加缓存 2011-03-10
	 * 
	 * @param EmployeeClosure
	 */
	protected void addCache(Employee employee) {

		String employeeId = null;
		String name = null;
		String pinyin = null;
		String code = null;
		String orgName = null;
		try {
			employeeId = BeanUtils.getSimpleProperty(employee, "id");
			name = BeanUtils.getSimpleProperty(employee, "name");
			code = BeanUtils.getSimpleProperty(employee, "code");
			Organization organization = employee.getOrganization();
			orgName = organization == null ? "" : organization.getShortName();
			if (name == null) {
				pinyin = "";
			} else {
				pinyin = PinYin.toPinYinString(name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		getCache()
				.put(employeeId, new CachedValue(pinyin, name, code, orgName));
	}

	/**
	 * 删除缓存 2011-03-10
	 * 
	 * @param employee
	 */
	public void removeCache(Employee employee) {

		String employeeId = null;

		try {
			employeeId = BeanUtils.getSimpleProperty(employee, "id");
		} catch (Exception e) {
			e.printStackTrace();
		}

		getCache().remove(employeeId);
	}

	/**
	 * 更新缓存 2011-03-10
	 * 
	 * @param employee
	 */
	public void updateCache(Employee employee) {
		addCache(employee);
	}

	// @SuppressWarnings("unchecked")
	// protected List<Employee> getEmployees(){
	// JdbcTemplate jdbcTemplate = getDefDao().getJdbcTemplate();
	// StringBuffer SQL= new StringBuffer("select e.id,e.code,e.name");
	// SQL.append(" FROM Employee as e");
	// List<Employee> list = jdbcTemplate.query(SQL.toString(),new
	// EmployeeMapper());
	// return list;
	// }

	protected List<Employee> getEmployees() {
		StringBuffer SQL = new StringBuffer(
				"select new Employee(e.id,e.code,e.name,org.id,org.shortName) from Employee e,Organization org where e.organization.id = org.id");
		List<Employee> list = this.getDefDao()
				.findByQueryString(SQL.toString());
		return list;
	}

	/**
	 * 获取员工实体，并且符合拼音 2011-03-10
	 * 
	 * @param keyword
	 * @param matchPinyin
	 * @return Map<String, CachedValue>
	 */
	public Map<String, CachedValue> search(String keyword, boolean matchPinyin) {

		if ((StringUtil.isEmpty(keyword) || keyword.equals(ALL_QUERY_KEY))) {
			return getCache();
		}

		Map<String, CachedValue> map = new HashMap<String, CachedValue>();

		for (Entry<String, CachedValue> entry : getCache().entrySet()) {
			if (entry.getValue().match(keyword, matchPinyin)) {
				map.put(entry.getKey(), entry.getValue());
			}
		}
		return map;
	}

	@Override
	protected DetachedCriteria getConditions(String query,Map<String, String> queryMap) {
	DetachedCriteria criteria = super.getConditions(query, queryMap);
	Long [] organizations = userService.getUserOperateOrganization();
	if(organizations.length!=0){
	if(StringUtils.isNotEmpty(queryMap.get("employee"))){
	String id = queryMap.get("employee");

	criteria.add(Restrictions.eq("id", Long.parseLong(id)));
	}else{
	criteria.createCriteria("organization", "o");
	criteria.add(Restrictions.in("o.id", organizations));
	}

	if(query !=  null){
	criteria.add(Restrictions.or(
	Restrictions.ilike("name", query, MatchMode.ANYWHERE), 
	Restrictions.ilike("code", query, MatchMode.START)
	));
	}
	if(queryMap != null && StringUtils.isNotEmpty(queryMap.get("turnAway"))){
	criteria.add(Restrictions.eq("turnAway",queryMap.get("turnAway").equals("1")?true:false));
	}
	}else{
	String id = queryMap.get("employee");
	if(null!=id){
	criteria.add(Restrictions.eq("id", Long.parseLong(id)));
	}
	if(queryMap.get("organization")!=null){
	long orgId =Long.parseLong(queryMap.get("organization"));
	criteria.createCriteria("organization", "o");
	criteria.add(Restrictions.eq("o.id", orgId));
	}
	} 
	return criteria;
	}
	
	 
	@SuppressWarnings("unchecked")
	public List<Employee> getEmployeesUnAssign() {
		return getDefDao()
				.findByQueryString(
						"select new Employee(e.id,e.code,e.name) from Employee as e left join  e.user as u where u.id is null");
	}

	private static final class EmployeeMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			Employee employee = new Employee();
			employee.setId(rs.getLong("id"));
			employee.setCode(rs.getString("code"));
			employee.setName(rs.getString("name"));
			return employee;
		}
	}

	@SuppressWarnings("unchecked")
	public Employee getEmployee(String code) {
		List<Employee> list = getDefDao().findByQueryString(
				"select e from Employee as e where e.code = '" + code
						+ "' and e.turnAway = false");
		return list.size() > 0 ? list.get(0) : null;
	}

	public Employee getEmployeeEmpty(String code) {
		List<Employee> list = getDefDao().findByQueryString(
				"select new Employee(e.id,e.name) from Employee as e where e.code = '"
						+ code + "' and e.turnAway = false");
		return list.size() > 0 ? list.get(0) : null;
	}

	public List<Employee> getIsAttendanceAndNotTurnAway(long organizationID,
			boolean isAdministratorManager) {
		String HQL = "select e from Employee e where e.organization.id=? and e.turnAway=false ";
		List<Employee> list = null;
		if (isAdministratorManager) {
			HQL = "select e from Employee e where e.turnAway=false ";
			list = getDefDao().findByQueryString(HQL);
			return list;
		} else {
			list = getDefDao().findByQueryString(HQL,
					new Object[] { organizationID });
			return list;
		}
	}

	/**
	 * 查询未分配区域的所有员工
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ListData<Employee> getEmployeesUnAssignArea(String query,
			Map<String, String> queryMap, int start, int limit) {
		List l = getDefDao()
				.findByQueryString(
						"select count(*) from Employee as e left join e.areas as a where a.id is null");
		int total = l != null ? Integer.parseInt(l.get(0).toString()) : 0;
		List<Employee> list = getDefDao()
				.findByQueryString(
						"select e from Employee as e left join e.areas as a where a.id is null",
						start, limit);
		return new ListData<Employee>(list, total);
	}

	/**
	 * 根据卡号查询员工
	 * 
	 * @param card
	 * @return
	 */
	public Employee getEmployeeByCard(String card) {
		List<Employee> list = getDefDao().findByQueryString(
				"select e from Employee as e where e.card = '" + card + "'");
		return list.size() > 0 ? list.get(0) : null;
	}

	/**
	 * 通过区域编号查询所在区域员工
	 * 
	 * @param areaId
	 * @param query
	 * @param queryMap
	 * @param start
	 * @param limit
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ListData<Employee> getEmployeesByAreaId(String areaId, String query,
			Map<String, String> queryMap, int start, int limit) {
		DetachedCriteria criteria = getConditions(query, queryMap);
		criteria.createAlias("areas", "a").add(
				Restrictions.eq("a.id", Long.parseLong(areaId)));
		int total = (Integer) getDefDao().getHibernateTemplate()
				.findByCriteria(criteria.setProjection(Projections.rowCount()))
				.iterator().next();
		List<Employee> list = getDefDao().findByQueryString(
				"select e from Employee as e left join e.areas as a where a.id="
						+ Long.parseLong(areaId), start, limit);
		return new ListData<Employee>(list, total);
	}

	public List<Employee> getAllNotTurnAway() {
		return getDefDao().findByQueryString(
				"select e from Employee e where e.turnAway= false");
	}

	public void bathImportEmployee(List<Employee> list) throws Exception {
		List<Employee> oldList = getAllNotTurnAway();
		List<Employee> delList = new ArrayList<Employee>();// 要删除的员工列表
		List<Employee> newList = new ArrayList<Employee>();// 新添加的员工列表
		// Map<String, Integer> codeAndCard = new HashMap<String, Integer>();
		// for(Employee em : list){
		// int as = codeAndCard.get(em.getCard());
		// codeAndCard.put(em.getCard(), as!=0?as++:1);
		// }
		for (Employee e : oldList) {
			if (!list.contains(e)) {
				e.setTurnAwayDate(new Date());
				delList.add(e);
			}
		}
		for (Employee e : list) {
			if (!oldList.contains(e)) {
				newList.add(e);
			}
		}
		updateEmployeeNewToOld(list, oldList);
		bathAddEmployee(newList);
		bathTurnawayEmployee(delList);
	}

	public void bathAddEmployee(List<Employee> list) throws Exception {
		for (Employee e : list) {
			add(e);
			operateCmdService.addOperateCmd(e.getCode(), e, "NEW");
		}
	}

	public void updateEmployeeNewToOld(List<Employee> newList,
			List<Employee> oldList) throws Exception {
		for (Employee e : oldList) {
			int i = newList.indexOf(e);
			Employee ee = i >= 0 ? newList.get(i) : null;
			if (ee != null) {
				e.setName(ee.getName());
				e.setCard(ee.getCard());
				e.setSex(ee.getSex());
				e.setBirthday(ee.getBirthday());
				e.setOrganization(ee.getOrganization());
				e.setPosition(ee.getPosition());
				e.setPeopleType(ee.getPeopleType());
				e.setJoinWorkDate(ee.getJoinWorkDate());
				e.setHireDate(ee.getHireDate());
				// e.setAreas(ee.getAreas());
				if (StringUtils.isNotEmpty(ee.getCard())
						&& !e.getCard().equals(ee.getCard())) {
					operateCmdService.addOperateCmd(ee.getCode(), ee, "UPDATE");
				}
			}
			update(e);
		}
	}

	public void bathTurnawayEmployee(List<Employee> employees) throws Exception {
		for (Employee employee : employees) {
			turnawayEmployee(employee);
		}
	}

	public void turnawayEmployee(Employee employee) throws Exception {
		Employee delEmployee = getEmployee(employee.getCode());
		delEmployee.setTurnAway(true);
		delEmployee.setTurnAwayDate(new Date());
		this.update(delEmployee);
	}

	@SuppressWarnings("unchecked")
	public Employee getLeaveApprove(String organizationName,
			String leaveApproveType) {
		String HQL = "select e from Employee e where e.organization.name = ? and e.user.role.name = ?";
		List<Employee> list = this.getDefDao().findByQueryString(HQL,
				organizationName, leaveApproveType);
		return null != list && list.size() > 0 ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public Employee getLeaveApprove(String leaveApproveType) {
		String HQL = "select e from Employee e where e.user.role.name = ?";
		List<Employee> list = this.getDefDao().findByQueryString(HQL,
				leaveApproveType);
		return null != list && list.size() > 0 ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public List<Employee> getUnUploadEmployees() {
		String selectHQL = "select new Employee(e.id,e.name,e.code,e.card) from Employee e";
		List<Employee> list = this.getDefDao().findByQueryString(selectHQL);
		return list;
	}

	public void refresh(Employee employee) {
		getDefDao().getHibernateTemplate().refresh(employee);
	}

	public List<Long> wipeBranchEmployee() {
		String sql = "select e.id from Employee e where e.organization.id not in (select o.id from Organization o where o.type.id='organizationType_01')";
		List<Long> list = this.getDefDao().findByQueryString(sql);
		return list;
	}

	public List<Long> getEmployeeIdByOrg(Long organizationId) {
		String sql = "select e.id from Employee e where e.organization.id=?";
		List<Long> list = this.getDefDao().findByQueryString(sql,
				organizationId);
		return list;
	}

	public List<Employee> getEmployreeByOrganization(String organizationId) {
		String hql = "select e from Employee e where e.organization.id = '"
				+ organizationId + "'";
		List<Employee> list = this.getDefDao().findByQueryString(hql);
		return list;
	}

	public List<Employee> getEmployeeByOrgId(Long organizationId) {
		String sql = "select new Employee(e.id,e.code,e.name,e.sex,e.organization.id,e.organization.shortName) from Employee e where e.organization.id = ? and e.turnAway = false";
		List<Employee> list = this.getDefDao().findByQueryString(sql,
				organizationId);
		return list;
	}

	public List<Employee> getEmployeeToAttshit() {
		String sql = "select new Employee(e.id,e.code,e.name,e.organization.id,e.organization.type.id,e.turnAwayDate,e.turnAway) from Employee e where e.organization is not null";
		List<Employee> list = this.getDefDao().findByQueryString(sql);
		return list;
	}

	public Employee getEmployeeToPushCard(String employeeId) {
		String sql = "select new Employee(e.id,e.code,e.name,e.organization.id,e.organization.name,e.organization.type.id) from Employee e where e.id = ?";
		List<Employee> list = this.getDefDao().findByQueryString(sql,
				Long.parseLong(employeeId));
		return list.size() > 0 ? list.get(0) : null;
	}

	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(1);
		System.out.println(year - 1);
		System.out.println(year);
	}

	/**
	 * 年假计算
	 * 
	 * @throws Exception
	 */
	public void yearTaskOnly(List<Employee> emps) throws Exception {
 
		List<HolidayCountVO> list = holidayOfLastYear(new Date());
		List<String> codes = new ArrayList<String>();
		for (HolidayCountVO h : list) {
			codes.add(h.getCode());
			}
		Calendar calendar = Calendar.getInstance();
		for (Employee e : emps) {
			Date date = e.getJoinWorkDate();
			Date hireDate = e.getHireDate();
			double chacha = calculateTDoA(hireDate, new Date());
				if (date != null) {
						if (chacha < 1) {
							e.setAnnualLeave(0);
						} else {
								double cha = calculateTDoA(date, new Date());
									if (cha < 1) {
										e.setAnnualLeave(0);
									} else if (1 <= cha && cha < 10) {
										e.setAnnualLeave(5);
									} else if (cha >= 10 && cha < 20) {
										e.setAnnualLeave(10);
									} else if (cha >= 20) {
										e.setAnnualLeave(15);
									}
								}
						}
				HolidayCountVO hcv = attShiftService.analyseYearHoliday(calendar.get(1), e.getId());
				if (hcv != null) {
					e.setAnnualLeave(e.getAnnualLeave() - hcv.getCount());
				}
				this.update(e);
			}
	}
	 
	public void yearTask(List<Employee> emps) throws Exception {
		// List<Employee> emps = this.getAll();
		List<HolidayCountVO> list = holidayOfLastYear(new Date());
		List<String> codes = new ArrayList<String>();
		for (HolidayCountVO h : list) {
			codes.add(h.getCode());
		}
		Calendar calendar = Calendar.getInstance();
		for (Employee e : emps) {
			Date date = e.getJoinWorkDate();
			Date hireDate = e.getHireDate();
			double chacha = calculateTDoA(hireDate, new Date());
			if (list.size() == 0) {
				if (date != null) {
					if (chacha < 1) {
						e.setAnnualLeave(0);
					} else {
						double cha = calculateTDoA(date, new Date());
						if (cha < 1) {
							e.setAnnualLeave(0);
						} else if (1 <= cha && cha < 10) {
							e.setAnnualLeave(5);
						} else if (cha >= 10 && cha < 20) {
							e.setAnnualLeave(10);
						} else if (cha >= 20) {
							e.setAnnualLeave(15);
						}
					}
				}
			} else {
				for (HolidayCountVO h : list) {
					if (codes.contains(e.getCode())) {
						if (chacha < 1) {
							e.setAnnualLeave(0);
						} else {
							double cha = calculateTDoA(date, new Date());
							if (date != null) {
								if (1 <= cha && cha < 10 && h.getCount() > 60) {
									e.setAnnualLeave(0);
								} else if (cha >= 10 && cha < 20 && h.getCount() > 90) {
									e.setAnnualLeave(0);
								} else if (cha >= 20 && h.getCount() > 120) {
									e.setAnnualLeave(0);
								} else {
									if (1 <= cha && cha < 10) {
										e.setAnnualLeave(5);
									} else if (cha >= 10 && cha < 20) {
										e.setAnnualLeave(10);
									} else if (cha >= 20) {
										e.setAnnualLeave(15);
									}
								}
							}
						}
					} else {
						if (date != null) {
							if (chacha < 1) {
								e.setAnnualLeave(0);
							} else {
								double cha = calculateTDoA(date, new Date());
								if (cha < 1) {
									e.setAnnualLeave(0);
								} else if (1 <= cha && cha < 10) {
									e.setAnnualLeave(5);
								} else if (cha >= 10 && cha < 20) {
									e.setAnnualLeave(10);
								} else if (cha >= 20) {
									e.setAnnualLeave(15);
								}
							}
						}
					}
				}
			}
			HolidayCountVO hcv = attShiftService.analyseYearHoliday(calendar.get(1), e.getId());
			if (hcv != null) {
				e.setAnnualLeave(e.getAnnualLeave() - hcv.getCount());
			}
			this.update(e);
		} 

	}

	/**
	 * 计算时间差
	 * 
	 * @param startDate起始时间
	 * @param endDate结束时间
	 * @return
	 */
	public double calculateTDoA(Date startDate, Date endDate) {
		double cha = (endDate.getTime() - startDate.getTime()) / 1000 / 60 / 60
				/ 24 / 365;
		return cha;
	}

	public List<HolidayCountVO> holidayOfLastYear(Date date) {
		String dateStr = Tools.date2String(date);
		String year = dateStr.substring(0, 4);
		// Date startDate =
		// Tools.string2Date(Integer.parseInt(year)-1+"-01-01");
		// Date endDate = Tools.string2Date(year+"-01-01");
		String startDate = Integer.parseInt(year) - 1 + "-01-01";
		String endDate = year + "-01-01";
		List<HolidayCountVO> list = holidayApplyService.getLeaveTotal(
				startDate, endDate);
		return list;
	}

	public List<Employee> getHolidayApplyByOrganization(String organizationId) {
		String sql = "SELECT * FROM employee WHERE id IN (SELECT employee_id FROM tuser WHERE id IN(SELECT users_id FROM tuser_role WHERE users_id IN "
				+ "(SELECT id FROM tuser WHERE employee_id IN (SELECT id FROM employee WHERE employee.organization_id="
				+ organizationId + ")) AND roles_dbId=4))";
		JdbcTemplate jdbcTemplate = this.getDefDao().getJdbcTemplate();

		List<Employee> list = jdbcTemplate.query(sql, new EmployeeMapper());
		return list;
	}

	public List<Employee> getBossAffirm() {
		String sql = "select * from Employee where id in (select employee_id from tuser where id in (select users_id from tuser_role where roles_dbId = (select r.dbid from Role as r where r.code = 'BOSSAFFIRM')))";
		JdbcTemplate jdbcTemplate = this.getDefDao().getJdbcTemplate();
		List<Employee> list = jdbcTemplate.query(sql, new EmployeeMapper());
		return list;
	}

	public void setCard() {
		String sql = "select e from Employee e where e.card=''";
		List<Employee> list = this.getDefDao().findByQueryString(sql);
		int card = 111111;
		for (Employee e : list) {
			e.setCard(Integer.toString(card));
			card++;
			this.update(e);
		}
	}

	public List<String> getCards() {
		String hql = "select e.card from Employee as e where e.turnAway=false";
		List<Object> list = this.getDefDao().findByQueryString(hql);
		List<String> cards = new ArrayList<String>();
		for (Object card : list) {
			cards.add(card.toString());
		}
		return cards;
	}

	public Employee getFWGEmployeeByCode(String code) {
		String HQL = "select e from Employee e where e.organization.id = 35 and e.code = ? ";
		List<Employee> list = this.getDefDao().findByQueryString(HQL, code);
		return null != list && list.size() > 0 ? list.get(0) : null;
	}

	public EmployeeTotal getPeopleTotal(String orgId,
			Map<String, String> queryMap) {
		JdbcTemplate jdbcTemplate = this.getDefDao().getJdbcTemplate();
		StringBuffer HQL = new StringBuffer();
		HQL.append("SELECT COUNT(DISTINCT a.statusinfo) as peopleTotal FROM employee e LEFT JOIN attshift a ON e.id = a.employee_id ");
		HQL.append(" WHERE e.organization_id = ? AND a.holiday >0 AND  ");

		String reportType = queryMap.get("reportType");
		if (reportType.equals("defined")) {
			HQL.append(" a.attDate BETWEEN '")
					.append(queryMap.get("startDate")).append("' and '")
					.append(queryMap.get("endDate")).append("'");
		} else if (reportType.equals("year")) {
			HQL.append(" a.year = ").append(
					Integer.parseInt(queryMap.get("ayear")));
		} else {
			HQL.append(" a.year = ").append(
					Integer.parseInt(queryMap.get("year"))).append(
					" and  a.month = ").append(
					Integer.parseInt(queryMap.get("month")));
		}

		String sql = HQL.toString();
		List<EmployeeTotal> list = jdbcTemplate.query(HQL.toString(),
				new Object[] { orgId }, new EmployeeTotalMapper());
		EmployeeTotal ss = list.get(0);
		return null != list && list.size() > 0 ? list.get(0) : null;
	}

	private static final class EmployeeTotalMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			EmployeeTotal et = new EmployeeTotal();
			et.setPeopleTotal(Integer.parseInt(rs.getString("peopleTotal")));
			return et;
		}
	}

	public SickPeopleTotal getSickHolidayTotal(String orgId,
			Map<String, String> queryMap) {
		JdbcTemplate jdbcTemplate = this.getDefDao().getJdbcTemplate();
		StringBuffer HQL = new StringBuffer();
		HQL
				.append("SELECT COUNT(DISTINCT e.id) as sickPeopleTotal FROM employee e LEFT JOIN attshift a ON e.id = a.employee_id ");
		HQL
				.append(" WHERE e.organization_id = ? AND a.holiday >0 AND a.holidayType IN('b45ed16b-f9ec-4e87-8a87-1cb4fb2d859e','bce240b5-1294-4e8f-a1bb-74152f0e0ff4','leaveType_02')  AND  ");

		String reportType = queryMap.get("reportType");
		if (reportType.equals("defined")) {
			HQL.append(" a.attDate BETWEEN '")
					.append(queryMap.get("startDate")).append("' and '")
					.append(queryMap.get("endDate")).append("'");
		} else if (reportType.equals("year")) {
			HQL.append(" a.year = ").append(
					Integer.parseInt(queryMap.get("ayear")));
		} else {
			HQL.append(" a.year = ").append(
					Integer.parseInt(queryMap.get("year"))).append(
					" and  a.month = ").append(
					Integer.parseInt(queryMap.get("month")));
		}

		String sql = HQL.toString();
		List<SickPeopleTotal> list = jdbcTemplate.query(HQL.toString(),
				new Object[] { orgId }, new SickPeopleTotalMapper());
		// EmployeeTotal ss= list.get(0);
		return null != list && list.size() > 0 ? list.get(0) : null;
	}

	private static final class SickPeopleTotalMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			SickPeopleTotal sp = new SickPeopleTotal();
			sp.setSickPeopleTotal(Integer.parseInt(rs
					.getString("sickPeopleTotal")));
			return sp;
		}
	}

	public List<Employee> getEmployeeByOrgId(String orgId) {
		String HQL = "select e from Employee e where e.organization.id =  ? ";
		List<Employee> list = this.getDefDao().findByQueryString(HQL, orgId);
		return list;
	}

	@Resource
	OperateCmdService operateCmdService;
	@Resource
	HolidayApplyService holidayApplyService;
	@Resource
	JdbcTemplate closureJdbcTemplate;
}
