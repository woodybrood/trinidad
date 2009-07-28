package com.neuri.trinidad.fitnesserunner;

import java.util.List;

import junit.framework.Assert;

import fitnesse.trinidad.*;


public class SuiteSetUpExecutionTest {
	
	@org.junit.Test
	public void suiteSetUpIsExecutedOnSuite() throws Exception{
		FitNesseRepository frp=new FitNesseRepository("target/test-classes");
		List<Test> tests=frp.getSuite("SlimTest");
		Assert.assertEquals("SlimTest.SuiteSetUp", tests.get(0).getName());
	}
	@org.junit.Test
	public void suiteSetUpIsExecutedOnASingleTest() throws Exception{
		FitNesseRepository frp=new FitNesseRepository("target/test-classes");
		Test t=frp.getTest("SlimTest.PlayerList");
		
		Assert.assertTrue(t.getContent().contains("this is in suite set up"));		
	}
}
