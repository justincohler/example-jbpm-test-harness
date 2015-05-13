/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.rhc.example;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rhc.harness.BaseBPMNTest;

/**
 * This is an example BPMN test class for a Budget Process that includes gateways, timers, and user
 * tasks.
 */
public class BudgetProcessTest extends BaseBPMNTest {

  private static final Logger logger = LoggerFactory.getLogger(BudgetProcessTest.class);
  private static final String P_BUDGET = "project1.budgetProcess";

  @BeforeClass
  public static void setTestResources() {
    setProcesses("budgetProcess.bpmn2");
  }

  @Test
  public void should_escalate_exceeded_budget() {
    processVars.put("spending", 10001);

    processInstance = ksession.startProcess(P_BUDGET, processVars);
    assertProcessInstanceActive(processInstance.getId(), ksession);

    WorkflowProcessInstanceImpl workflowInstance = (WorkflowProcessInstanceImpl) processInstance;
    assertEquals(10001, workflowInstance.getVariable("spending"));

    assertNodeTriggered(processInstance.getId(), "Determine Budget Remaining");
    assertNodeActive(processInstance.getId(), ksession, "Budget Exceeded");
    
    completeWorkItem("Budget Exceeded", null);

    assertProcessInstanceNotActive(processInstance.getId(), ksession);
  }

  @Test
  public void should_not_escalate_exceeded_budget() throws InterruptedException {
    processVars.put("spending", 9999);

    processInstance = ksession.startProcess(P_BUDGET, processVars);
    assertProcessInstanceActive(processInstance.getId(), ksession);

    WorkflowProcessInstanceImpl workflowInstance = (WorkflowProcessInstanceImpl) processInstance;
    assertEquals(9999, workflowInstance.getVariable("spending"));
    assertNodeTriggered(processInstance.getId(), "Determine Budget Remaining");
    assertNodeActive(processInstance.getId(), ksession, "Review Budget Remaining");

    Map<String, Object> itemOutput = new HashMap<String, Object>();
    itemOutput.put("timerDuration", "2s");
    
    completeWorkItem("Review Budget Remaining", itemOutput);

    assertProcessInstanceActive(processInstance.getId(), ksession);
    // sleep just a bit longer than the timer to be safe
    logger.info("Waiting 2 seconds for timer event to fire...");
    Thread.sleep(2200);

    assertProcessInstanceNotActive(processInstance.getId(), ksession);
  }
}
