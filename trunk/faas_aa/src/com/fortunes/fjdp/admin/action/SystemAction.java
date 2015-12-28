package com.fortunes.fjdp.admin.action;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.Constants;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Menu;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.Privilege;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.model.Menu.MenuType;
import com.fortunes.fjdp.admin.service.MenuService;
import com.fortunes.fjdp.admin.service.PrivilegeService;
import com.fortunes.fjdp.admin.service.UserService;

import net.fortunes.core.Helper;
import net.fortunes.core.LoginSessionBindingListener;
import net.fortunes.core.action.BaseAction;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 系统action,提供登陆等系统服务
 * @author Neo.Liao
 *
 */
@Component @Scope("prototype")
public class SystemAction extends BaseAction {
	
	private static final boolean NOT_ALLOW_REPEAT_LOGIN = false;
	
	@Resource private MenuService menuService;
	@Resource private UserService userService;
	
	private String initMsg;
	public String login() throws Exception {
		User loginUser = new User();
		loginUser.setName(p("userName"));
		loginUser.setPassword(Tools.encodePassword(p("password")));
		User authedUser = userService.authUser(loginUser);
		if(authedUser == null){
			setJsonMessage(false,"登陆错误,请检查你的用户名和密码是否正确以及用户是否被锁定!");
			return render(jo);
		}else{
			if(NOT_ALLOW_REPEAT_LOGIN){
				Integer b = UserService.getUserLoginStatus().get(authedUser.getName());
				
				if(b!=null&&Constants.USER_STATUS_LOGIN==b.intValue()){
					setJsonMessage(false, "该用户已经登陆,不可重复登陆");
					return render(jo);
				}
			}
			getSessionMap().clear();
			getSessionMap().put(Helper.LOGIN_LISTENER, new LoginSessionBindingListener(authedUser));
			getSessionMap().put(Helper.AUTHED_USER, authedUser);
			getSessionMap().put(Helper.ANNUALlEAVE_NUM, authedUser.getEmployee().getAnnualLeave());
			getSessionMap().put(Helper.PRIVILEGES, getPrivilegesArray(authedUser).toArray());
			getSessionMap().put(Helper.PRIVILEGES_STRING, getPrivilegesArray(authedUser).toString());
			getSessionMap().put(Helper.WIDGET_URLS,getWidgetUrlsList());
			getSessionMap().put(Helper.ROLES_STRING, getRolesArray(authedUser).toString());
			getSessionMap().put(Helper.ROLE_CODE, getRoleCodeArray(authedUser).toString());
			Organization o = authedUser.getEmployee().getOrganization();
			getSessionMap().put(Helper.ORGANIZATION_ID, o != null ? o.getId():"");
			getSessionMap().put(Helper.ORGANIZATION_NAME, o != null ? o.getName():"");
			getSessionMap().put(Helper.ORGANIZATION_CODE, o!=null ? o.getCode():"");
			jo.put(SUCCESS_KEY, true);
			
			return render(jo);
		}
		
	}
	
	public String logout() throws Exception {
		getSessionMap().clear();
		return LOGIN;
	}
	
	public String viewport(){
		return VIEWPORT;
	}
	
	
	public String getMenuTree() throws Exception{
		Menu rootMenu = menuService.getRoot();
		JSONObject jo = walkMenuTree(rootMenu);
		return render((JSONArray)jo.get("children"));
	}
	
	public String personalConfig() throws Exception{
		authedUser.setPassword(Tools.encodePassword(p("password")));
		if (!authedUser.isPasswordChanged()) {
			authedUser.setPasswordChanged(true);
		}
		userService.update(authedUser);
		jo.put("success", true);
		jo.put("msg", "个人设置成功!");
		return render(jo);
	}
	
	public String downloadManual() throws  Exception{
		String fileType = p("fileType");
		String rootPath = request.getSession().getServletContext().getRealPath("/");
		File file = new File(rootPath+Constants.MANUAL_DOC_PATH_NAME + "." + fileType);
		return renderFile(FileUtils.readFileToByteArray(file), 
				Constants.PROJECT_CNAME+"-操作手册."+fileType);
	}
	
	//====================== private method ============================
	private List<String> getWidgetUrlsList() throws Exception{
		List<String> list = new ArrayList<String>();
		Menu rootMenu = menuService.getRoot();
		walkMenu(rootMenu, list);
		return list;
	}
	
	private void walkMenu(Menu menu,List<String> list)throws Exception{
		if(menu.isLeaf()){
			String privilegeCode = menu.getName()+"_view";
			if(AdminHelper.userHasPrivilege(privilegeCode)){
				list.add(menu.getUrl());
			}
		}else{					
			List<Menu> subMenus = menu.getChildren();
			for(Menu submenu : subMenus){
				walkMenu(submenu,list);
			}
		}
	}
	
	private JSONArray getPrivilegesArray(User user){
		List<Privilege> privileges = userService.getPrivileges(user);
		JSONArray ja = new JSONArray();
		if(privileges != null){
			for(Privilege p:privileges){
				ja.add(p.getCode());
			}
		}
		return ja;
	}
	
	private JSONArray getRolesArray(User user){
		List<Role> roleList=user.getRoles();
		JSONArray ja = new JSONArray();
		for(Role role : roleList){
			if(role != null){
				ja.add(role.getName());
			}
		}
		return ja;
	}
	private JSONArray getRoleCodeArray(User user){
		List<Role> roleList = user.getRoles();
		JSONArray ja = new JSONArray();
		for(Role role : roleList){
			if(role.getCode() != "" && role.getCode() !=null){
				ja.add(role.getCode());
			}else{
				ja.add("");
			}
		}
		return ja;
	}
	
	private JSONObject walkMenuTree(Menu menu)throws Exception{
		
		JSONObject jo = new JSONObject();
		
		jo.put("id", menu.getName());
		jo.put("text", menu.getText());
		
		//level 0
		if(menu.getType().equals(MenuType.ROOT)){
			jo.put("expanded", true);
		}
		//level 1
		else if(menu.getType().equals(MenuType.CATEGORY)){
			jo.put("cls", "parent-menu");
			jo.put("iconCls", menu.getIcon());
			jo.put("expanded", true);
			jo.put("singleClickExpand", true);
		}
		//level 2
		else if(menu.getType().equals(MenuType.NODE)){
			jo.put("cls", "n-menu");
			jo.put("menuIcon", menu.getParent().getIcon());
		}
		if(menu.isLeaf()){
			jo.put("leaf", true);
		}else{					
			List<Menu> subMenus = menu.getChildren();
			JSONArray ja = new JSONArray();
			for(Menu submenu:subMenus){
				//如果登陆用户拥有该菜单的权限,则显示该菜单
				String privilegeCode = submenu.getName();
				if(AdminHelper.userHasPrivilege(privilegeCode)){
					ja.add(walkMenuTree(submenu));
				}				
				jo.put("children", ja);
			}
		}
		return jo;
	}
	
	//======================== setter and getter ====================
	

	
}
