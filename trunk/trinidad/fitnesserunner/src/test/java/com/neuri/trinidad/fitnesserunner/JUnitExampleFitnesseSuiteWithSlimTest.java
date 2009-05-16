package com.neuri.trinidad.fitnesserunner;

import org.junit.runner.RunWith;

import com.neuri.trinidad.FitnesseSuite;
import com.neuri.trinidad.FitnesseSuite.*;

@RunWith(FitnesseSuite.class)
@Name("SlimTest")
@Engine(SlimTestEngine.class)
@FitnesseDir("target/test-classes")
//@OutputDir("/tmp/fitnesse") //Specify an absolute or relative path
@OutputDir(systemProperty = "java.io.tmpdir", pathExtension = "fitnesse")
public class JUnitExampleFitnesseSuiteWithSlimTest {

}
