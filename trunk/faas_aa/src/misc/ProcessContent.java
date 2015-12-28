package misc;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessDefinitionQuery;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.ProcessInstanceQuery;
import org.jbpm.api.RepositoryService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class ProcessContent {

	private SessionFactory sessionFactory;	
	private Session session;
	
	private ProcessEngine processEngine;
	private RepositoryService repositoryService;
	private ExecutionService executionService;
	
	public List<ProcessDefinition> getProcessDefinitions() {
		ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
		List<ProcessDefinition> list = query.list();
		/*for(ProcessDefinition element: list) {
			System.out.println("流程定义名称: " + element.getName() + 
					"; 流程定义key: " + element.getKey() +
					"; 流程版本: " + element.getVersion() + 
					"; 流程导入时间: ");
		}*/
		return list;
	}
	
	public void getProcessInstances() {
		ProcessInstanceQuery query = executionService.createProcessInstanceQuery();
		List<ProcessInstance> list = query.list();
		for(ProcessInstance element: list) {
			System.out.println("发起时间: " + 
					"; 目前状态: " + element.getState());
		}
	}
	
	public void execute() {
		this.getProcessInstances();
	}
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-*.xml");
		ProcessContent processContent = (ProcessContent)context.getBean("processContent");
		processContent.setUp();
		processContent.execute();
		processContent.tearDown();
	}
	
	public void setUp() {
		session = SessionFactoryUtils.getSession(sessionFactory, true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
	}
	
	public void tearDown() {
		TransactionSynchronizationManager.unbindResource(sessionFactory);
		SessionFactoryUtils.releaseSession(session, sessionFactory);
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

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}	
}
