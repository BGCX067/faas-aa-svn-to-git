package com.fortunes.faas.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.fortunes.core.Helper;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fortunes.faas.model.ShiftConfigBundle;
import com.fortunes.faas.model.ShiftConfigTemplate;
import com.fortunes.faas.service.ShiftConfigBundleService;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;


@Component @Scope("prototype")
public class ShiftConfigBundleAction extends GenericAction<ShiftConfigBundle> {
	
	private ShiftConfigBundleService shiftConfigBundleService;
	
	@Override
	public String list() throws Exception{
		int day = Integer.parseInt(p("selectedDay"));
		long orgnizationId = Helper.getOrganizationId();
		
		List<ShiftConfigBundle> list;
		list = getShiftConfigBundleService().getBundles(orgnizationId,day);
		if(list ==null || list.size() <= 0){
			list = new ArrayList<ShiftConfigBundle>();
			list.add(initDefault(orgnizationId, day));
		}
		
		JSONArray ja = new JSONArray();
		for (ShiftConfigBundle b : list) {
			JSONObject json = new JSONObject();
			json.put("id", b.getId());
			json.put("name", b.getName());
			ja.add(json);
		}
		jo.put(DATA_KEY, ja);
		jo.put(SUCCESS_KEY, true);
		
		return render(jo);
	}
	
	@Override
	public String del() throws Exception {
		shiftConfigBundleService.delTemplete(Long.parseLong(id));
		return super.del();
	}

	@Override
	protected void setEntity(ShiftConfigBundle e) throws Exception {
		e.setDay(Integer.parseInt(p("selectedDay")));
		e.setName(p("name"));
		e.setOrganization(AppHelper.toOrganization(Helper.getOrganizationId()+""));

	}

	@Override
	protected JSONObject toJsonObject(ShiftConfigBundle e)
			throws Exception {
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("name", e.getName());
		return record.getJsonObject();
	}
	
	private ShiftConfigBundle initDefault(long orgnizationId,int day) throws Exception{
		ShiftConfigBundle defaults = new ShiftConfigBundle();
		defaults.setDay(day);
		defaults.setName("缺省模板");
		defaults.setNextBundle(true);
		defaults.setOrganization(AppHelper.toOrganization(orgnizationId+""));
		this.getDefService().add(defaults);
		return defaults;
	}
	
	@Override
	public GenericService<ShiftConfigBundle> getDefService() {
		return shiftConfigBundleService;
	}

	public void setShiftConfigBundleService(ShiftConfigBundleService shiftConfigBundleService) {
		this.shiftConfigBundleService = shiftConfigBundleService;
	}

	public ShiftConfigBundleService getShiftConfigBundleService() {
		return shiftConfigBundleService;
	}

}
