package com.neuri.trinidad.transactionalrunner;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.Before;

import com.neuri.trinidad.transactionalrunner.FitnesseSpringContext;
import com.neuri.trinidad.transactionalrunner.RollbackIntf;
import com.neuri.trinidad.transactionalrunner.RollbackNow;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class TestTxRollback {

	@Before
	public void createTestTable() throws Exception {
		System.setProperty("spring.context", "classpath:spring.xml");
		DataSource dataSource = (DataSource) FitnesseSpringContext.getInstance().getBean("dataSource");
		Connection conn = dataSource.getConnection();
		PreparedStatement ps = null;
		try {
			try {
			ps = conn.prepareStatement("DROP TABLE test");
			ps.executeUpdate();
			ps.close();
			} catch (Exception e) {}
			ps = conn.prepareStatement("CREATE TABLE test (name varchar(100))");
			ps.executeUpdate();
			ps.close();
		} finally {
			conn.close();
		}
	}

	@Test
	public void testRollsBack(){
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
