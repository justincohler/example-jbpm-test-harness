Feature: Review monthly spending on command and ensure we have not exceeded the given budget

  Scenario: Monthly spending is over budget
    Given a project "project1"
    And the following processes:
      | "budgetProcess.bpmn2" |
    When starting the process "budgetProcess" with the following variables:
      | "spending" | 11000.00 |
    Then "Determine Budget Remaining" task should have triggered
    And "Budget Exceeded" task should be active

  Scenario: Monthly spending is over budget - complete review
    Given a project "project1"
    And the following processes:
      | "budgetProcess.bpmn2" |
    When starting the process "budgetProcess" with the following variables:
      | "spending" | 11000.00 |
    And completing the task "Budget Exceeded" with the following output:
      | "t_testString"  | "A"   |
      | "t_testInteger" | 12345 |
    Then the following tasks should have triggered:
      | "Determine Budget Remaining" |
      | "Budget Exceeded"            |
    And the process should have completed
