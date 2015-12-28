package com.fortunes.faas.action;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.fortunes.core.action.BaseAction;
import net.fortunes.util.Tools;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.faas.util.ClosureService;
import com.fortunes.faas.vo.EmployeeClosure;

/**
 * 
 * @author Leo
 * @version 2011-4-27
 */
@Component @Scope("prototype")
public class ClosureAction extends BaseAction{
	
	@Resource private ClosureService closureService;
	
	public String syncClosureEmployeesCard() throws Exception {
		closureService.syncCards();
		setJsonMessage(true, "更新成功");
		return render(jo);
	}
	
	public String syncLogs() throws Exception {
		String fdate = p("date");
		Date date = Tools.string2Date(fdate);
		try {
			closureService.syncLogsOfDate(date);
			setJsonMessage(true, "同步完成并成功");
		} catch (Exception e) {
			setJsonMessage(true, "同步失败");
		}
		return render(jo);
	}
}
