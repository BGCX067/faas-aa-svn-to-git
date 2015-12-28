package com.fortunes.fjdp;

import com.fortunes.faas.model.Area;
import com.fortunes.faas.model.ShiftConfigBundle;
import com.fortunes.faas.model.WorkingArea;
import com.fortunes.fjdp.admin.AdminHelper;

public class AppHelper extends AdminHelper {
	
	public static Area toArea(String id){
		return (id == null || id.equals("")) ? null : new Area(Long.parseLong(id));
	}
	
	public static WorkingArea toWorkingArea(String id){
		return (id == null || id.equals("")) ? null : new WorkingArea(Long.parseLong(id));
	}
	
	public static ShiftConfigBundle toShiftConfigBundle(String id){
		return (id == null || id.equals("")) ? null : new ShiftConfigBundle(Long.parseLong(id));
	}

}
