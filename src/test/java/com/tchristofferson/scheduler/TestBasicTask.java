package com.tchristofferson.scheduler;

import java.util.concurrent.CountDownLatch;

public class TestBasicTask extends BasicTask {

    private CountDownLatch countDownLatch;

    public TestBasicTask(BasicTask basicTask) {
        super(basicTask.getTaskId(), basicTask.isSync(), basicTask.getDelay(), basicTask.getTask());
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void setCanceled() {
        super.setCanceled();
        countDown();
    }

    @Override
    void setThread(Thread thread) {
        super.setThread(thread);
        countDown();
    }

    private void countDown() {
        if (countDownLatch != null)
            countDownLatch.countDown();
    }
}
