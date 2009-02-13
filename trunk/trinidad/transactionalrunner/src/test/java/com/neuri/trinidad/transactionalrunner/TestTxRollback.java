package com.neuri.trinidad.transactionalrunner;

import junit.framework.Assert;

import org.junit.Test;

import com.neuri.trinidad.transactionalrunner.FitnesseSpringContext;
import com.neuri.trinidad.transactionalrunner.RollbackIntf;
import com.neuri.trinidad.transactionalrunner.RollbackNow;

public class TestTxRollback {
	@Test
	public void testRollsBack(){
		System.setProperty("spring.context", "classpath:spring.xml");
		RollbackIntf bn= FitnesseSpringContext.getRollbackBean();
		final TestRepository tr=(TestRepository) FitnesseSpringContext.getInstance().getBean("testRepository");
		try {
			bn.process(new Runnable(){
				public void run() {
					tr.put("test123");
					Assert.assertEquals(1,tr.check("test123"));					
				}
			});
			Assert.fail("exception expected");
		}
		catch  (RollbackNow rn){
			//
		}
		Assert.assertEquals(0,tr.check("test123"));		
	}
}
