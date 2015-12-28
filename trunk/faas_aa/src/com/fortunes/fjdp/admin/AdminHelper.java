package com.fortunes.fjdp.admin;

import com.fortunes.faas.model.Area;
import com.fortunes.faas.model.HolidayApply;
import com.fortunes.faas.model.WorkingArea;
import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;

import net.fortunes.core.Helper;
import net.sf.json.JSONObject;

public class AdminHelper extends Helper{
	
	/**
	 * @param key
	 * @param dict
	 * @return
	 */
	public JSONObject put(String key,Dict dict){
		JSONObject jo = new JSONObject();
		if(dict != null){
			jo.put("id", dict.getId());
			jo.put("text", dict.getText());
			jsonObject.put(key, jo);
		}
		
		return jo;
	}
	
	public JSONObject put(String key,Employee employee){	
		JSONObject jo = new JSONObject();
		if(employee != null){
			jo.put("id", employee.getId());
			jo.put("text", employee.getName());
			jsonObject.put(key, jo);
		}
		return jo;
	}
	
	public JSONObject put(String key,Organization o){	
		JSONObject jo = new JSONObject();
		if(o != null){
			jo.put("id", o.getId());
			jo.put("text", o.getName());
			jsonObject.put(key, jo);
		}
		return jo;
	}

	
	public JSONObject put(String key,User user){
		JSONObject jo = new JSONObject();
		if(user != null){
			jo.put("id", user.getId());
			jo.put("text", user.getDisplayName());
			jsonObject.put(key, jo);
		}
		
		return jo;
	}
	
	public JSONObject put(String key,Role role){
		JSONObject jo = new JSONObject();
		if(role != null){
			jo.put("id", role.getDbId());
			jo.put("text", role.getName());
			jsonObject.put(key, jo);
		}
		return jo;
	}
	
	public JSONObject put(String key,Area area){
		JSONObject jo = new JSONObject();
		if(area != null){
			jo.put("id", area.getId());
			jo.put("text", area.getName());
			jsonObject.put(key, jo);
		}
		return jo;
	}
	
	public static String longToString(Long id){
		return Long.toString(id);
	}
	
	public static Long stringToLong(String id){
		return Long.parseLong(id);
	}
	
	public static Employee toEmployee(String id){
		return (id == null || id.equals("")) ? null : new Employee(Long.parseLong(id));
	}
	
	public static HolidayApply toHolidayApply(String id){
		return (id==null || id.equals(""))? null:new HolidayApply(Long.parseLong(id));
	}
	
	public static Organization toOrganization(String id){
		return (id == null || id.equals("")) ? null : new Organization(Long.parseLong(id));
	}
	
	public static User toUser(String id){
		return (id == null || id.equals("")) ? null : new User(Long.parseLong(id));
	}
	
	public static Role toRole(String id){
		return (id == null || id.equals("")) ? null : new Role(Long.parseLong(id));
	}
	
	public static Dict toDict(String id){
		return (id == null || id.equals("")) ? null : new Dict(id);
	}
	public static WorkingArea toWorkingArea(String id){
		return(id==null || id.equals(""))? null:new WorkingArea(Long.parseLong(id));
	}
	
}
