package com.fortunes.faas.action;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.MoveAttInfo;
import com.fortunes.faas.service.MoveAttInfoService;
import com.fortunes.fjdp.admin.AdminHelper;

import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.sf.json.JSONObject;

@Component @Scope("prototype")
public class MoveAttInfoAction extends GenericAction<MoveAttInfo>{
	
	@Resource MoveAttInfoService moveAttInfoService;
	
	@Override
	protected void setEntity(MoveAttInfo entity) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected JSONObject toJsonObject(MoveAttInfo e) throws Exception {
		AdminHelper record=new AdminHelper();
		record.put("id", e.getId());
		record.put("code", e.getCode());
		record.put("name", e.getName());
		record.put("attDate", e.getAttDate());
		record.put("organization", e.getOrganization());
		record.put("plusInfo", e.getPlusInfo());
		record.put("status", e.getStatus());
		record.put("attPlus", e.getAttPlus());
		return record.getJsonObject();
	}
	
	@Override
	public String list() throws Exception {
		queryMap.put("meetId", p("meetId"));
		return super.list();
	}
	
	@Override
	public GenericService<MoveAttInfo> getDefService() {
		// TODO Auto-generated method stub
		return moveAttInfoService;
	}

}
