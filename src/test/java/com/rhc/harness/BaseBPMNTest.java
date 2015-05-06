/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.rhc.harness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Before;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class constructs a ksession for the given test resources (bpmn2 classpath resources) for use
 * in each BPMN test case.
 */
public class BaseBPMNTest extends JbpmJUnitBaseTestCase {

  private static final Logger logger = LoggerFactory.getLogger(BaseBPMNTest.class);

  protected static final String TESTSUITE_ACTOR = "test";
  private static final String HUMAN_TASK = "Human Task";
  private static final String REST_TASK = "Rest";

  /**
   * This field can be used by all tests which extend this class to instantiate processes, complete
   * tasks, etc.
   * 
   */
  protected KieSession ksession;

  /**
   * This field is used for reference in each test case to query process variables, query tasks,
   * etc.
   * 
   */
  protected ProcessInstance processInstance;

  /**
   * This list contains all of the bpmn2 classpath resources used in an individual test case, and
   * creates the runtime engine for each test.
   * 
   * e.g. ["myFirstProcess.bpmn2", "mySecondProcess.bpmn2"]
   * 
   */
  protected static List<String> testResources = new ArrayList<String>();

  /**
   * This map contains the process variables within each particular process
   * 
   */
  protected Map<String, Object> processVars = new HashMap<String, Object>();

  /**
   * This method is run before each individual BPMN test to set a new knowledge session with the
   * resources required for the test to run successfully.
   * 
   */
  @Before
  public void createKnowledgeSession() {
    logger.info("Setting up knowledge session for resources: " + testResources.toString());
    createRuntimeManager(testResources.toArray(new String[testResources.size()]));
    ksession = getRuntimeEngine(null).getKieSession();
    ksession.getWorkItemManager().registerWorkItemHandler(HUMAN_TASK, getTestWorkItemHandler());
    ksession.getWorkItemManager().registerWorkItemHandler(REST_TASK, getTestWorkItemHandler());
    /*
     * TODO: Register additional Work Item Handlers needed for the test suite here
     */
    processVars.clear();
    logger.info("Knowledge session created successfully.");
  }

  /**
   * This method loops through the ksession for outstanding work items with the given name and
   * completes the work item with the given data outputs.
   * 
   * @param itemName
   * @param itemOutput
   * 
   */
  public void completeWorkItem(String itemName, Map<String, Object> itemOutput) {
    boolean itemExists = false;

    for (WorkItem item : getTestWorkItemHandler().getWorkItems()) {
      if (((String) item.getParameter("NodeName")).equalsIgnoreCase(itemName)) {
        itemExists = true;
        ksession.getWorkItemManager().completeWorkItem(item.getId(), itemOutput);
      }
    }
    if (!itemExists) {
      fail("The following Work Item was not found or could not be completed: " + itemName);
    }
  }

  /**
   * This method sets this base class with the resource names to be injected in the ksession for
   * each individual test.
   * 
   * @param resources
   */
  public static void setTestResources(String... resources) {
    testResources.clear();
    testResources.addAll(Arrays.asList(resources));
  }

}
