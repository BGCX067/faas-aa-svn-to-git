package com.fortunes.fjdp.admin.service;

import java.io.Reader;
import java.util.List;

import javax.annotation.Resource;

import net.fortunes.core.service.GenericService;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.model.Menu;
import com.fortunes.fjdp.admin.model.Privilege;

@Component
public class MenuService extends GenericService<Menu>{
	
	private static final String NODE = "node";
	private static final String MY_HOME = "myhome";
	public static final String FUNC_ELEMENT = "func";
	public static final String PRIVILEGE_ELEMENT = "privilege";
	public static final String WIDGET_JS_ROOT = "/widget";
	
	@Resource
	private PrivilegeService privilegeService;
	
	@Override
	public Menu add(Menu entity) throws Exception {
		super.add(entity);
		if(entity.getParent() != null)
			entity.getParent().setLeaf(false);
		return entity;
	}
	
	@Override
	public void del(Menu entity) throws Exception {
		Menu parent = entity.getParent();
		super.del(entity);
		if(parent != null && parent.getChildren().size() <= 0)
			parent.setLeaf(true);
	}
	
	public void initToDb(Reader reader) throws Exception{
		SAXReader xmlReader = new SAXReader(); 
		Document doc = xmlReader.read(reader);
		Element root = doc.getRootElement();
		
		walkMenuTree(root.element(FUNC_ELEMENT),new Menu());
		
		walkPrivilegeTree(root.element(FUNC_ELEMENT),new Privilege());			
	}
	
	private void walkMenuTree(Element menuElement,Menu menu) throws Exception {
		String funcType = menuElement.attributeValue("type") == null ? NODE : menuElement.attributeValue("type");
		if(!funcType.equals(MY_HOME)){
			createMenu(menuElement,menu,funcType);
		}
		
		if (menuElement.hasContent()) {
			//遍历子菜单
			List<Element> funcElements = menuElement.elements(FUNC_ELEMENT);
			for(int i = 0;i < funcElements.size();i++){
				if(!funcType.equals(MY_HOME)){
					Menu subMenu = new Menu();
					subMenu.setOrderPlace(i+1);
					subMenu.setParent(menu);
					walkMenuTree(funcElements.get(i),subMenu);
				}
			}
		}
	}
	
	private void walkPrivilegeTree(Element menuElement,Privilege privilege) throws Exception {
		createPrivilegeForFunc(menuElement,privilege);
		
		if (menuElement.hasContent()) {
			//遍历子菜单
			List<Element> funcElements = menuElement.elements(FUNC_ELEMENT);
			for (Element funcElement : funcElements) {
				Privilege subPrivilege = new Privilege();
				subPrivilege.setParent(privilege);
				walkPrivilegeTree(funcElement,subPrivilege);
			}
			//遍历菜单包含的权限
			List<Element> privilegeElements = menuElement.elements(PRIVILEGE_ELEMENT);
			for(int i = 0;i < privilegeElements.size();i++){
				Privilege subPrivilege = new Privilege();
				subPrivilege.setParent(privilege);
				subPrivilege.setOrderPlace(i+1);
				createPrivilege(menuElement, privilegeElements.get(i), subPrivilege);
			}
		}
	}
	
	private void createMenu(Element menuElement,Menu menu,String funcType) throws Exception{
		if(funcType.equals(NODE)){
			menu.setUrl(getUrl(menuElement));
		}
		menu.setType(funcType);
		setMenu(menuElement, menu);
		this.addOrUpdate(menu);
		
		Menu parent = menu.getParent();
		if(parent != null){
			parent.setLeaf(false);
			this.update(menu.getParent());
		}
		
	}
	
	private void setMenu(Element menuElement,Menu menu){
		menu.setName(menuElement.attributeValue("name"));
		menu.setText(menuElement.attributeValue("text"));
		menu.setIcon(menuElement.attributeValue("icon"));
		if(menuElement.attributeValue("display") != null && menuElement.attributeValue("display").equals("false"))
			menu.setDisplay(false);
		else
			menu.setDisplay(true);
	}
	
	private String getUrl(Element menuElement){
		return WIDGET_JS_ROOT+"/"+
			StringUtils.uncapitalize(menuElement.getParent().attributeValue("name"))+"/"+
			StringUtils.uncapitalize(menuElement.attributeValue("name"))+".js";
	}
	
	private void createPrivilegeForFunc(Element funcElement,Privilege p) throws Exception{
		p.setCode(funcElement.attributeValue("name"));
		p.setText(funcElement.attributeValue("text"));
		privilegeService.addOrUpdate(p);
	}
	
	private void createPrivilege(Element funcElement,Element privilegeElement,Privilege p) throws Exception{
		p.setCode(funcElement.attributeValue("name")+"_"+privilegeElement.attributeValue("name"));
		p.setText(privilegeElement.attributeValue("text"));
		p.setDescription(privilegeElement.attributeValue("text")+funcElement.attributeValue("text"));
		p.setLeaf(true);
		privilegeService.addOrUpdate(p);
	}

}
