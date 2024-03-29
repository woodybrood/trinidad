package com.neuri.trinidad.fitnesserunner;
import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fitnesse.trinidad.*;

public class JUnitExampleSlimTest {
	JUnitHelper helper;
	@Before
	public void initHelper() throws Exception{

		helper=new JUnitHelper(new TestRunner(
				new FitNesseRepository("target/test-classes"),
				new SlimTestEngine(),
				new File(System.getProperty("java.io.tmpdir"),"fitnesse").getAbsolutePath()));
	}
//	@Test
//	public void runSuite() throws Exception{
//		helper.assertSuitePasses("SlimTest");
//	}
	@Test
	public void runSingleTest() throws Exception{
		helper.assertTestPasses("SlimTest.ConcatenatingStrings");
	}
}
