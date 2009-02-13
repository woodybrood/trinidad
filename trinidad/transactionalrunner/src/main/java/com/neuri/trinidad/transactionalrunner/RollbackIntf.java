package com.neuri.trinidad.transactionalrunner;

public interface RollbackIntf {
	public void process(Runnable r);
}
