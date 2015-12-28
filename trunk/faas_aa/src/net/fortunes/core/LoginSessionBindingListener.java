package net.fortunes.core;

import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import net.fortunes.util.Tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.UserService;

public class LoginSessionBindingListener implements HttpSessionBindingListener {
	
	final Logger logger = LoggerFactory.getLogger("ROOT");
	
	private static final String USER_SERVICE_NAME = "userService";
	
	private User user;

	public LoginSessionBindingListener(User authedUser) {
		this.setUser(authedUser);
	}

	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		User userInDb = loginOrLogOut(event, 1);
		logger.info("{} 用户<{}>登陆;",userInDb.getDisplayName(),Tools.date2String(new Date()));
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		User userInDb = loginOrLogOut(event, 0);
		logger.info("{} 用户<{}>注销;",userInDb.getDisplayName(),Tools.date2String(new Date()));
	}
	
	private User loginOrLogOut(HttpSessionBindingEvent event,int flag){
		HttpSession session = event.getSession();
		UserService userService = lookupService(session);
		User userInDb = userService.get(String.valueOf(user.getId()));
		userService.updateLoginSession(userInDb, flag);
		return userInDb;
	}
	
	private UserService lookupService(HttpSession session){
		WebApplicationContext wac =
			WebApplicationContextUtils.getRequiredWebApplicationContext(session.getServletContext());
		return (UserService) wac.getBean(USER_SERVICE_NAME, UserService.class);
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

}
