# automaticTestGenerationBenchmark
A simple benchmark comparing two popular automatic test generation tools

* To generate all tests, use "gradle generateTests"
* To run all generated tests, use "gradle runAllTests"
* To create coverage reports for the generated tests, use "gradle allCoverages" 
* To create mutation reports for the generated tests, use "gradle allMutations"
* To create a csv file with coverage information for the tests, use "gradle exportCSV"
* To clean up generated files and tests, use "gradle cleanUp"

**Please note that some of these tasks depend on a previous task.**
	*i.e*: generateTests must be called before runAllTests    
        
Several convenience methods are available and can be listed by using "gradle tasks"

**Available parameters:**

* Time spent creating tests: -PbudgetTime=X  
* Amount of test suites to generate for each class: -PtestSuites=X  

## Examples:

Generate a csv with information for 10 tests suites, using 2 minutes as a budget

    gradle generateTests runAllTests allCoverages allMutations exportCSV -PbudgetTime=120 -PtestSuites=10

                
Generate and measure only randoop tests

    gradle randoop runRandoopTests randoopCoverage randoopMutation exportCSV

## Csv output format
> *class name, tool name, line coverage, branch coverage, mutation score*
