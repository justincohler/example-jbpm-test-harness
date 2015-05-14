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

import java.util.List;
import java.util.Map;

import org.springframework.test.context.ContextConfiguration;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * 
 * This class holds the common Cucumber functionality used by all step classes pertaining to BPMN
 * Specification 2.0 functionality.
 * 
 */
@ContextConfiguration("classpath:cucumber.xml")
public class BaseBPMNCukesTest extends BaseBPMNTest {

  /**
   * This field holds the project name for a given test, usually the first clause in the cucumber
   * scenario.
   * 
   * Note: ProcessDefinitionId => <projectName>.<processName>
   * 
   */
  private String projectName;

  /**
   * This field holds the process definition id for a given test, comprised of the project name
   * (usually the first clause in the cucumber scenario) and the processName of the process being
   * run.
   * 
   * Note: ProcessDefinitionId => <projectName>.<processName>
   * 
   */
  private String processDefinitionId;

  // ****************************************************
  // ********************** GIVENS **********************
  // ****************************************************

  @Given("^a project \"(.*?)\"$")
  public void a_project(String projectName) throws Throwable {
    this.projectName = projectName;
  }

  @Given("^the following processes:$")
  public void the_following_processes(List<String> processNames) throws Throwable {
    setProcesses(processNames.toArray(new String[processes.size()]));
  }


  // ****************************************************
  // ********************** WHENS ***********************
  // ****************************************************

  @When("^starting the process \"(.*?)\"$")
  public void starting_the_process(String processName) throws Throwable {
    createKnowledgeSession();
    if (projectName == null) {
      fail("Project name must be specified in the cucumber test.");
    }
    processDefinitionId = projectName.concat(".").concat(processName);
    processInstance = ksession.startProcess(processDefinitionId, null);

  }

  @When("^starting the process \"(.*?)\" with the following variables:$")
  public void starting_the_process_with_the_following_variables(String processName,
      Map<String, Object> processVars) throws Throwable {
    createKnowledgeSession();
    if (projectName == null) {
      fail("Project name must be specified in the cucumber test.");
    }
    processDefinitionId = projectName.concat(".").concat(processName);
    processInstance = ksession.startProcess(processDefinitionId, processVars);
  }

  @When("^completing the task \"(.*?)\"$")
  public void completing_the_task(String taskName) throws Throwable {
    completeWorkItem(taskName, null);
  }

  @When("^completing the task \"(.*?)\" with the following output:$")
  public void completing_the_task_with_the_following_output(String taskName,
      Map<String, Object> taskOutput) throws Throwable {
    completeWorkItem(taskName, taskOutput);
  }

  // ****************************************************
  // ********************** THENS ***********************
  // ****************************************************

  @Then("^\"(.*?)\" task should have triggered$")
  public void task_should_have_triggered(String taskName) throws Throwable {
    assertNodeTriggered(processInstance.getId(), taskName);
  }

  @Then("^the following tasks should have triggered:$")
  public void the_following_tasks_should_have_triggered(String... taskNames) throws Throwable {
    assertNodeTriggered(processInstance.getId(), taskNames);
  }

  @Then("^\"(.*?)\" task should be active$")
  public void task_should_be_active(String taskName) throws Throwable {
    assertNodeActive(processInstance.getId(), ksession, taskName);
  }

  @Then("^the following tasks should be active:$")
  public void the_following_tasks_should_be_active(String... taskNames) throws Throwable {
    assertNodeActive(processInstance.getId(), ksession, taskNames);
  }

  @Then("^the process should have completed$")
  public void the_process_should_have_completed() throws Throwable {
    assertProcessInstanceNotActive(processInstance.getId(), ksession);
  }

}
