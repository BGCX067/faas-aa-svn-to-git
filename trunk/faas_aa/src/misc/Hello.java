package misc;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class Hello {

	private SessionFactory sessionFactory;	
	private Session session;
	
	private ProcessEngine processEngine;
	private RepositoryService repositoryService;
	private ExecutionService executionService;
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-*.xml");
		Hello hello = (Hello)context.getBean("hello");
		hello.setUp();
		hello.execute();
		hello.tearDown();
	}
	
	public void execute() {
//		repositoryService.createDeployment().addResourceFromClasspath("process/hello.jpdl.xml").deploy();
//		executionService.startProcessInstanceByKey("hello");
		
		
		List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
		for(ProcessDefinition element: list) {
			System.out.println("---------------------------------------------------------------------");
			System.out.println("Id: " + element.getId()
					+ "; Name: " + element.getName()
					+ "; Version: " + element.getVersion());
			
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			List<ProcessInstance> listInstance = executionService.createProcessInstanceQuery()
			.processDefinitionId(element.getId()).list();
			for(ProcessInstance e: listInstance) {
				System.out.println("Id: " + e.getId() + "; Key: " + e.getKey() 
						+ "; Name: " + e.getName());
			}
		}
		System.out.println("---------------------------------------------------------------------");
		
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
