package com.neuri.trinidad.fitnesserunner;
import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fitnesse.trinidad.*;

public class JUnitExampleFitTest {
	JUnitHelper helper;
	@Before
	public void initHelper() throws Exception{
		helper=new JUnitHelper(new TestRunner(
				new FitNesseRepository("target/test-classes"),
				new FitTestEngine(),
				new File(System.getProperty("java.io.tmpdir"),"fitnesse").getAbsolutePath()));
	}
	@Test
	public void runSuite() throws Exception{
		helper.assertSuitePasses("JavaExamples");
	}
	@Test
	public void runSingleTest() throws Exception{
		helper.assertTestPasses("JavaExamples.CommonExamples.SetUpFixture");
	}
}
