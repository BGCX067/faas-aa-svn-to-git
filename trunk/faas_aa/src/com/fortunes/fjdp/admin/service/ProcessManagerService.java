package com.fortunes.fjdp.admin.service;

import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;

import net.fortunes.core.ListData;

import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessDefinitionQuery;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.ProcessInstanceQuery;
import org.jbpm.api.RepositoryService;
import org.springframework.stereotype.Component;

//@Component
public class ProcessManagerService {

	@Resource private ProcessEngine processEngine;
	@Resource private RepositoryService repositoryService;
	@Resource private ExecutionService executionService;
	
	public List<ProcessDefinition> getProcessDefinitions() {		
		ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
		List<ProcessDefinition> list = query.list();
		
		return list;
	}
	
	public ListData<ProcessInstance> getProcessInstance(String id, int start, int limit) {		
		ProcessInstanceQuery query = executionService.createProcessInstanceQuery().processDefinitionId(id);
		int total = query.list().size();
		
		query = executionService.createProcessInstanceQuery().processDefinitionId(id).page(start, limit);
		List<ProcessInstance> list = query.list();	
		ListData<ProcessInstance> listData = new ListData<ProcessInstance>(list, total);
		
		return listData;
	}
	
	public void importProcessDefinition(String strFileName, InputStream inputStream) {
		repositoryService.createDeployment().addResourceFromInputStream(strFileName, inputStream).deploy();
	}
	
	public ProcessEngine getProcessEngine() {
		return processEngine;
	}
	
	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}
	
	public RepositoryService getRepositoryService() {
		return repositoryService;
	}
	
	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}
	
	public ExecutionService getExecutionService() {
		return executionService;
	}
	
	public void setExecutionService(ExecutionService executionService) {
		this.executionService = executionService;
	}	
}
