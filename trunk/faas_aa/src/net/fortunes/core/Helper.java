package net.fortunes.core;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.text.DecimalFormat;

import javax.servlet.http.HttpSession;

import jxl.Sheet;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.xwork.StringUtils;

import com.fortunes.fjdp.admin.model.User;


import net.fortunes.core.action.BaseAction;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 助手类,提供有关json和date的快捷操作
 * @author Neo.Liao
 *
 */
public class Helper {
	
	protected JSONObject jsonObject;
	
	public static boolean DEBUG = true;
	public static final String LOGIN_LISTENER = "loginListener";
	public static final String AUTHED_USER = "authedUser";//User
	public static final String PRIVILEGES = "privileges";//(String)Object []
	public static final String PRIVILEGES_STRING = "privilegesString";//"['role'],['role_view']"
	public static final String ROLES_STRING = "rolesString";//String "['系统管理员'],['会计']"
	public static final String WIDGET_URLS = "widgets";//String "['/admin/menu.js'],['/admin/role.js']"
	public static final String ORGANIZATION_ID = "organizationId";//String 1
	public static final String ORGANIZATION_NAME = "organizationName";//String 车公庙营业厅
	public static final String ORGANIZATION_CODE = "organizationCode";//部门代码HR
	public static final String ROLE_CODE = "roleCode";
	public static final String ANNUALlEAVE_NUM="annualLeave";
	
	DecimalFormat df = new DecimalFormat("#,##0.00");
	DecimalFormat dft = new DecimalFormat("#,##0.000");
	
	private static final ThreadLocal<HttpSession> HTTP_SESSION_IN_THREAD = new ThreadLocal<HttpSession>();
	
	public static void setHttpSessionInThread(HttpSession httpSession) {
		Helper.HTTP_SESSION_IN_THREAD.set(httpSession);
	}

	public static HttpSession getHttpSessionInThread(){
		return Helper.HTTP_SESSION_IN_THREAD.get();
	}
	
	public static void removeHttpSessionInThread(){
		Helper.HTTP_SESSION_IN_THREAD.remove();
	}
	
	public static User getUser(){
		if(Helper.HTTP_SESSION_IN_THREAD.get()!=null){
			return (User)Helper.HTTP_SESSION_IN_THREAD.get().getAttribute(AUTHED_USER);
		}else
			return null;
	}
	
	public static long getOrganizationId(){
		if(Helper.HTTP_SESSION_IN_THREAD.get()!=null && Helper.HTTP_SESSION_IN_THREAD.get().getAttribute(ORGANIZATION_ID)!=null &&  Helper.HTTP_SESSION_IN_THREAD.get().getAttribute(ORGANIZATION_ID)!=""){
			return (Long) Helper.HTTP_SESSION_IN_THREAD.get().getAttribute(ORGANIZATION_ID);
		}else
			return 0;
	}
	
	public static boolean userHasPrivilege(String privilegeCode){
		Object[] privileges;
		if(Helper.HTTP_SESSION_IN_THREAD.get()!=null){
			privileges = (Object[])Helper.HTTP_SESSION_IN_THREAD.get().getAttribute(PRIVILEGES);
		}else
			return false;
		if(privileges == null)
			return false;
		for(Object p : privileges){
			if(((String)p).equals(privilegeCode)) 
				return true;
		}
		return false;		
	}
	
	public static JSONArray toJsonArray(Collection<Model> collection,String idProperty,String nameProperty){
		JSONArray ja = new JSONArray();
		for(Model e : collection){
			String id = null;
			String text = null;
			try {
				id = BeanUtils.getSimpleProperty(e, idProperty);
				text = BeanUtils.getSimpleProperty(e, nameProperty);
			} catch( Exception ex) {
				ex.printStackTrace();
			} 
			JSONObject temp = new JSONObject();
			temp.put("id", id);
			temp.put("text", text);
			ja.add(temp);
		}
		return ja;
	}
	
	public static JSONArray toJsonArray(Map<String,CachedValue> map){
		JSONArray ja = new JSONArray();
		for(Entry<String, CachedValue> e : map.entrySet()){
			JSONObject temp = new JSONObject();
			temp.put("id", e.getKey());
			temp.put("text", e.getValue().getName());
			temp.put("pinyin", e.getValue().getPinyin());
			temp.put("code", e.getValue().getCode());
			temp.put("orgName", e.getValue().getOrgName());
			if(StringUtils.isNotEmpty(e.getValue().getRelativeId())){
				JSONObject relative = new JSONObject();
				relative.put("id", e.getValue().getRelativeId());
				relative.put("text", e.getValue().getRelativeName());
				temp.put("relative", relative);
			}
			
			ja.add(temp);
		}
		return ja;
	}
	
	public static JSONObject toJsonObject(Map<String,CachedValue> map){
		JSONObject jo = new JSONObject();
		jo.put(BaseAction.DATA_KEY, toJsonArray(map));
		return jo;
	}
	
	public static JSONArray toJsonArray(Collection<Model> collection){
		return toJsonArray(collection, "id", "name");
	}
	
	public static JSONObject toJsonObject(Collection<Model> collection){
		JSONObject jo = new JSONObject();
		jo.put(BaseAction.DATA_KEY, toJsonArray(collection));
		return jo;
	}
	
	public static JSONObject toJsonObject(Collection<Model> collection,String idProperty,String nameProperty){
		JSONObject jo = new JSONObject();
		jo.put(BaseAction.DATA_KEY, toJsonArray(collection,idProperty,nameProperty));
		return jo;
	}
	
	
	public Helper(){
		jsonObject = new JSONObject();
	}
	
	public Object put(String key,JSONArray o){	
		return jsonObject.put(key, o);
	}
	
	public Object put(String key,String o){	
		return jsonObject.put(key, o);
	}
	
	public Object put(String key,int o){	
		return jsonObject.put(key, o);
	}
	
	public Object put(String key,Integer o){	
		return jsonObject.put(key, o);
	}
	
	/*
	public Object put(String key,double o){
		return jsonObject.put(key, df.format(o));
	}*/
	public Object put(String key,double value){
		return jsonObject.put(key, value);
	}
	
	/*
	public Object put(String key,double o,int digital){
		return jsonObject.put(key, dft.format(o));
	}*/
	public Object put(String key,double o,int digital){
		return jsonObject.put(key, o);
	}
	
	/*
	public Object put(String key,Double o){
		return jsonObject.put(key, o != null ? df.format(o) : "");
	}*/
	public Object put(String key, Double value) {
		return jsonObject.put(key, value);
	}
	
	/*
	public Object put(String key,Double o,int digital){
		return jsonObject.put(key, o != null ? dft.format(o) : "");
	}*/
	public Object put(String key,Double o,int digital){
		return jsonObject.put(key, o);
	}
	
	public Object put(String key,long o){	
		return jsonObject.put(key, o);
	}
	
	public Object put(String key,Long o){	
		return jsonObject.put(key, o);
	}
	
	public Object put(String key,boolean o){	
		return jsonObject.put(key, o);
	}
	
	public Object put(String key,Boolean o){	
		return jsonObject.put(key, o);
	}
	
	public Object put(String key,JSONObject o){	
		return jsonObject.put(key, o);
	}
	
	public Object put(String key,Date o){	
		return jsonObject.put(key, Tools.date2String(o));
	}
	
	public static Date toDate(String dateString) throws ParseException{
		return Tools.string2Date(dateString);		
	}
	
	public static String toDateString(Date date) throws ParseException{
		return Tools.date2String(date);
	}
	
	public static String getCell(Sheet sheet,int column ,int row){
		return sheet.getCell(column, row).getContents().trim();
	}
	
	
	public String toString(int indentFactor){
		return jsonObject.toString(indentFactor);
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

}
