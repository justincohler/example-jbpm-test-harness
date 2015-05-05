package com.rhc.tests;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rhc.harness.BaseBPMNTest;

/**
 * This is an example BPMN test class for a Budget Process that includes 
 * gateways, timers, and user tasks.
 * 
 * @author jcohler
 *
 */
public class BudgetProcessTest extends BaseBPMNTest {
	
	private static final Logger logger = LoggerFactory.getLogger(BudgetProcessTest.class);
	private static final String P_BUDGET = "budgeting.budgetProcess";
	
	@Test
	public void should_escalate_exceeded_budget() {
		setTestResources("budgetProcess.bpmn2");
		processVars.put("user1", TESTSUITE_ACTOR);
		processVars.put("spending", 10001);
		
		processInstance = ksession.startProcess(P_BUDGET, processVars);
		assertProcessInstanceActive(processInstance.getId(), ksession);
	

		WorkflowProcessInstanceImpl workflowInstance = (WorkflowProcessInstanceImpl) processInstance;
		assertEquals(12345, workflowInstance.getVariable("spending"));
		
		assertNodeTriggered(processInstance.getId(), "Determine Budget Remaining");
		assertNodeActive(processInstance.getId(), ksession, "Budget Exceeded");
		
		completeWorkItem("BudgetExceeded", null);
		assertProcessInstanceCompleted(processInstance.getId());
	}
	
	@Test
	public void should_not_escalate_exceeded_budget() throws InterruptedException {
		setTestResources("budgetProcess.bpmn2");
		processVars.put("user1", TESTSUITE_ACTOR);
		processVars.put("spending", 9999);
		
		processInstance = ksession.startProcess(P_BUDGET, processVars);
		assertProcessInstanceActive(processInstance.getId(), ksession);
	

		WorkflowProcessInstanceImpl workflowInstance = (WorkflowProcessInstanceImpl) processInstance;
		assertEquals(12345, workflowInstance.getVariable("spending"));
		
		assertNodeTriggered(processInstance.getId(), "Determine Budget Remaining");
		assertNodeActive(processInstance.getId(), ksession, "Review Budget Remaining");
		
		Map<String, Object> itemOutput = new HashMap<String, Object>();
		itemOutput.put("timerDuration", "2s");
		completeWorkItem("Review Budget Remaining", itemOutput);
		
		// sleep just a bit longer than the timer to be safe
		logger.info("Waiting 2 seconds for timer event to fire...");
		Thread.sleep(2200);
		
		assertProcessInstanceCompleted(processInstance.getId());
	}
}
