package com.neuri.trinidad.transactionalrunner;

import java.util.concurrent.Executor;

import org.springframework.transaction.annotation.Transactional;

public class RollbackBean implements RollbackIntf {
    private static final String TEST_WRAPPER_BEAN_NAME = "trinidad.test.wrapper";

    @Transactional(rollbackFor = Throwable.class)
    public void process(Runnable r) {
        if (System.getProperty(TEST_WRAPPER_BEAN_NAME) != null) {
            Executor testWrapper = (Executor) FitnesseSpringContext.getInstance().getBean(
                    System.getProperty(TEST_WRAPPER_BEAN_NAME));
            testWrapper.execute(r);
        } else {
            r.run();
        }
        throw new RollbackNow();
    }
}
