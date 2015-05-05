# Example JBPM Test Harness
This project is intended to provide a simple example showing off the full test functionality available within the jbpm-test project provided in JBPM and BPM Suite installations.

## Extending BaseBPMNTest
This class is the core of the test harness. It acts as a simple parent that initializes a knowledge session for each test and simplifies some of the more verbose implementations of core test functionality.

Every test should extend BaseBPMNTest, which itself extends the JbpmJUnitBaseTestCase, the out-of-the-box test framework.
