package com.fortunes.fjdp.admin.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import net.fortunes.core.ListData;
import net.fortunes.core.action.BaseAction;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessInstance;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.service.ProcessManagerService;

@Component @Scope("prototype")
public class ProcessManagerAction extends BaseAction  {
	
	@Resource private ProcessManagerService processManagerService;
	
	private File importFile;
	private String importFileFileName;
	
	public String getDefinitions() throws Exception {
		
		List<ProcessDefinition> list = processManagerService.getProcessDefinitions();
		
		JSONArray ja = new JSONArray();
		for(ProcessDefinition element: list) {
			ja.add(defitionToJSONObject(element));
		}
		jo.put("data", ja);
		return render(jo);  
	}
	
	public String getInstances() throws Exception {		
		
		ListData<ProcessInstance> listData = processManagerService.getProcessInstance(id, start, limit);
		
		JSONArray ja = new JSONArray();
		for(ProcessInstance element: listData.getList()) {
			String instanceId = element.getId();
			int dotPosition = instanceId.indexOf(".");
			String batchId = instanceId.substring(dotPosition + 1);
			
			ja.add(instanceToJSONObject(element, batchId));
		}
		jo.put("data", ja);
		jo.put("totalCount", listData.getTotal());
		return render(jo);
	}
	
	public String importProcessDefinition() throws Exception{
		InputStream inputStream = new FileInputStream(importFile);
		processManagerService.importProcessDefinition(importFileFileName, inputStream);		
		return render("{success:true}");
	}
	
	private JSONObject defitionToJSONObject(ProcessDefinition definition) {
		AppHelper record = new AppHelper();
		record.put("id", definition.getId());
		record.put("name", definition.getName());
		record.put("key", definition.getKey());
		record.put("version", definition.getVersion());
		return record.getJsonObject();
	}
	
	private JSONObject instanceToJSONObject(ProcessInstance instance, String batchId) {
		AppHelper record = new AppHelper();
		
		record.put("batchId", batchId);
		
		Set<String> set = instance.findActiveActivityNames();
		Iterator<String> iterator = set.iterator();
		String activityName = iterator.next();
		
		record.put("activityName", activityName);
		
		return record.getJsonObject();
	}
	
	public ProcessManagerService getProcessManagerService() {
		return processManagerService;
	}

	public void setProcessManagerService(ProcessManagerService processManagerService) {
		this.processManagerService = processManagerService;
	}

	public File getImportFile() {
		return importFile;
	}

	public void setImportFile(File importFile) {
		this.importFile = importFile;
	}

	public String getImportFileFileName() {
		return importFileFileName;
	}

	public void setImportFileFileName(String importFileFileName) {
		this.importFileFileName = importFileFileName;
	}	
}
