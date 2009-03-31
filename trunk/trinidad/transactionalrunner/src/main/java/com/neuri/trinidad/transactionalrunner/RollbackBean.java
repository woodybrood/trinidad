package com.neuri.trinidad.transactionalrunner;

import java.util.concurrent.Executor;

import org.springframework.transaction.annotation.Transactional;

public class RollbackBean implements RollbackIntf {
    @Transactional(rollbackFor = Throwable.class)
    public void process(Runnable r) {
        if (System.getProperty("trinidad.test.wrapper") != null) {
            Executor testWrapper = (Executor) FitnesseSpringContext.getInstance().getBean(
                    System.getProperty("trinidad.test,wrapper"));
            testWrapper.execute(r);
        } else {
            r.run();
        }
        throw new RollbackNow();
    }
}
