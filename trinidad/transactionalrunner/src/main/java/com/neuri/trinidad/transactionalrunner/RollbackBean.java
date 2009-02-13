package com.neuri.trinidad.transactionalrunner;
import org.springframework.transaction.annotation.Transactional;

	 
public class RollbackBean implements RollbackIntf {
	@Transactional(rollbackFor=Throwable.class)
	public void process(Runnable r) {
		r.run();
		throw new RollbackNow();
	}
}
