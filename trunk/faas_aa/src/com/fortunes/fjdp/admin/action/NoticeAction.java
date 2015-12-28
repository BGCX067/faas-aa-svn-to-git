package com.fortunes.fjdp.admin.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.sf.json.JSONObject;

import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Notice;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.NoticeService;
import com.fortunes.fjdp.admin.service.OrganizationService;


@Component @Scope("prototype")
public class NoticeAction extends GenericAction<Notice> {
	
	@Resource private NoticeService noticeService;
	@Resource private OrganizationService organizationService;
	
	private String[] organizations;
	
	@Override
	public String create() throws Exception {
		Notice notice = new Notice();
		setEntity(notice);
		List<User> users = new ArrayList<User>();
		for(String id : organizations){
			Organization d = organizationService.get(id);
			for(Employee e : d.getEmployees()){
				if(e.getUser() != null){
					users.add(e.getUser());
				}
			}
		}
		noticeService.publishNotice(notice,users);
		jo.put("success", true);
		return render(jo);
	}
	
	@Override
	public String update() throws Exception {
		Notice notice = noticeService.get(id);
		setEntity(notice);
		noticeService.update(notice);
		jo.put("success", true);
		return render(jo);
	}

	@Override
	protected void setEntity(Notice e) throws ParseException {
		e.setCreateDateTime(new Date());
		e.setCreator(authedUser);
		e.setContents(p("contents"));
		e.setLevel(AppHelper.toDict(p("level")));
	}

	@Override
	protected JSONObject toJsonObject(Notice e) throws ParseException {
		AppHelper record = new AppHelper();
		record.put("id", e.getId());
		record.put("contents", e.getContents());
		record.put("createUser", e.getCreator());
		record.put("createDateTime", e.getCreateDateTime());
		record.put("level", e.getLevel());
		return record.getJsonObject();
	}
	
	//================== setter and getter ===================
	
	@Override
	public GenericService<Notice> getDefService() {
		return noticeService;
	}

	public void setOrganizations(String[] organizations) {
		this.organizations = organizations;
	}

	public String[] getOrganizations() {
		return organizations;
	}


}
