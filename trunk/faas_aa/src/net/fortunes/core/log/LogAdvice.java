package net.fortunes.core.log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import net.fortunes.core.Helper;
import net.fortunes.core.Model;
import net.fortunes.core.dao.GenericDao;
import net.fortunes.core.log.annotation.LoggerClass;
import net.fortunes.core.log.annotation.LoggerMethod;
import net.fortunes.util.Tools;

import org.springframework.aop.AfterReturningAdvice;

import com.fortunes.fjdp.admin.model.Log;
import com.fortunes.fjdp.admin.model.User;

/**
 * 日志切面通知,给特定的Service类中的方法记录日志
 * Service类和方法及方法参数要满足于一些给定，详细内容见方法注释
 * @author Neo.Liao
 *
 */
public class LogAdvice implements AfterReturningAdvice {
	
	private GenericDao<Log> genericDao; 
	
	public void afterReturning(Object returnValue, Method method, Object[] methodArgs,
			Object target) throws Throwable {
		if(logable(target,method)){
			log(target,method,methodArgs[0]);
		}
	}
	
	/**
	 * 判断一个Service Class中的方法是否要记录日志
	 * @param target 拦截的Service Class
	 * @param method Service　Class中的方法
	 * @return　这个方法的执行是否需要被log
	 */
	private boolean logable(Object target,Method method){
		return target.getClass().isAnnotationPresent(LoggerClass.class)
				&& method.isAnnotationPresent(LoggerMethod.class);
	}
	

	/**
	 * 记录操作日志，约定执行操作的这个方法第一个参数为业务Model，如 add(User)
	 * @param target 拦截的Service Class,如 UserService
	 * @param method Service　Class中的方法, 如UserService.add(User)这个方法
	 * @param model 业务Model，如一个User类
	 */
	private void log(Object target,Method method,Object model) 
		throws SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ParseException{
		
		Date createTime = new Date();
		User opUser = Helper.getUser();
		String opName = method.getAnnotation(LoggerMethod.class).operateName();
		
		doLog(createTime,opUser,opName,(Model)model);
	}
	
	private void doLog(Date createTime,User opUser,String opName,Model model) throws ParseException{
		String opUserName = opUser == null ? "系统管理员" : opUser.getDisplayName();
		String contents = getContents(createTime,opUserName,opName,model);
		printLogMsg(contents);
		saveToDb(createTime,opUserName,opName,contents);
	}
	
	private void saveToDb(Date createTime,String opUserName,String opName,String contents){
		Log newLog = new Log();
		newLog.setCreateTime(createTime);
		newLog.setOpUser(opUserName);
		newLog.setOpType(opName);
		newLog.setContents(contents);
		genericDao.add(newLog);
		//genericDao.getHibernateTemplate().flush();
		
	}
	
	private String getContents(Date createTime,String opUserName,
			String opName,Model model) throws ParseException{
		return Helper.toDateString(createTime)+" : "
			+"用户"+"<"+opUserName+">"
			+opName
			+"了一条["
			+model
			+"]记录!";
	}
	
	//打印到控制台
	private void printLogMsg(String contents){
		Tools.println(contents);
	}

	public void setGenericDao(GenericDao<Log> genericDao) {
		this.genericDao = genericDao;
	}

	public GenericDao<Log> getGenericDao() {
		return genericDao;
	}
	
}
