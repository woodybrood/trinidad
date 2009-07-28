package com.neuri.trinidad.fitnesserunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import fitnesse.trinidad.FitnesseSuite;
import fitnesse.trinidad.FitnesseSuite.*;

@RunWith(FitnesseSuite.class)
@Name("JavaExamples")
@FitnesseDir("target/test-classes")
//@TestEngine(FitTestEngine.class) //this is optional since it's the default
//@OutputDir("/tmp/fitnesse") //Specify an absolute or relative path
@OutputDir(systemProperty = "java.io.tmpdir", pathExtension = "fitnesse")
public class JUnitExampleFitnesseSuiteTest {

  @Test
  public void dummy(){
  
  }
}
