package com.fortunes.faas.util;

import com.fortunes.fjdp.Constants;

public class LeaveHelper {
	
	
	
	public static String getLeaveType(String leaveType){
		if(Constants.WELFARES_LEAVE.contains(leaveType)){
			return Constants.LEAVE_TYPE_WELFARE;
		}
		else if(Constants.MATERNITY_LEAVE.contains(leaveType)){
			return Constants.LEAVE_TYPE_MATERNITY;
		}
		else 
			return leaveType;
	}

}
