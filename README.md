# Example JBPM Test Harness
This project is intended to provide a simple example showing off the full test functionality available within the jbpm-test project provided in JBPM and BPM Suite installations.

## Extending BaseBPMNTest
This class is the core of the test harness. It acts as a simple parent that initializes a knowledge session for each test and simplifies some of the more verbose implementations of core test functionality.

Every test should extend BaseBPMNTest, which itself extends the JbpmJUnitBaseTestCase, the out-of-the-box test framework.

## Test Harness Organization Options
This test harness can be afixed to a BPMS project (kjar) in the following ways:
* As a sibling module of the BPMS project under a common parent (preferred)
* As a standalone project that imports the BPMS project as a dependency
* As a module under the BPMS project (i.e. a parent-child relationship)
* Within the BPMS project itself
** Note: BPMS current Maven functionality does not recognize <scope> tags, so including "provided" or "test" scope to this dependency will not prevent the test harness from being included on the build path of the project.
