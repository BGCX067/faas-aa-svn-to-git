package misc;

import java.util.Calendar;
import java.util.Date;

import net.fortunes.util.Tools;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.fortunes.fjdp.admin.service.DictService;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.UserService;

public class Test{
	
	public SessionFactory sessionFactory;
	public Session session; 
	
	private EmployeeService employeeService;
	private DictService dictService;
	private UserService userService;
	
	public void execute() throws Exception {
	}
	
	
	public static void main(String[] args) throws Exception {
//		System.out.println(Tools.encodePassword("lqp"));
//		Date d = new Date();
//		Calendar c = Calendar.getInstance();
//		c.setTime(d);
//		c.set(Calendar.DAY_OF_MONTH, -1);
//		Date  dd= c.getTime();
//		System.out.println(d);
//		System.out.println(dd);
		
		Date startDate = Tools.string2Date("2012-09-01");
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2012);
		c.set(Calendar.MONTH, 10);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.DATE, -1);
		Date  endDate = c.getTime();
		System.out.println(startDate);
		System.out.println(endDate);
	}
	
	protected void setUp(){
		session = SessionFactoryUtils.getSession(sessionFactory, true);
		TransactionSynchronizationManager.bindResource(sessionFactory,new SessionHolder(session));
	}
	
	protected void tearDown(){
		TransactionSynchronizationManager.unbindResource(sessionFactory);  
		SessionFactoryUtils.releaseSession(session, sessionFactory);  
	}

	public void setEmployeeService(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	public EmployeeService getEmployeeService() {
		return employeeService;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public void setDictService(DictService dictService) {
		this.dictService = dictService;
	}

	public DictService getDictService() {
		return dictService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserService getUserService() {
		return userService;
	}
}


