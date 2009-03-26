package com.neuri.trinidad.fitnesserunner;
import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.neuri.trinidad.JUnitHelper;
import com.neuri.trinidad.TestRunner;

public class JUnitExampleFitLibraryTest {
	JUnitHelper helper;
	@Before
	public void initHelper() throws Exception{
		helper=new JUnitHelper(new TestRunner(
				new FitNesseRepository("target/test-classes"),
				new FitLibraryTestEngine(),
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
