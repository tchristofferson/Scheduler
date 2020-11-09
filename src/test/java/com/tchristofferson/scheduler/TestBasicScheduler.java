package com.tchristofferson.scheduler;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TestBasicScheduler extends BasicScheduler {

    private CountDownLatch countDownLatch;

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void cancelTasks() {
        super.cancelTasks();
        countDownLatch.countDown();
    }

    @Override
    public List<ITask> getPendingTasks() {
        List<ITask> tasks = super.getPendingTasks();
        countDown();
        return tasks;
    }

    @Override
    public ITask getTask(int taskId) {
        ITask task = super.getTask(taskId);
        countDown();
        return task;
    }

    @Override
    TestBasicTask addTask(BasicTask basicTask) {
        TestBasicTask testBasicTask = new TestBasicTask(basicTask);

        executorService.submit(() -> {
            synchronized (pendingTasks) {
                pendingTasks.add(testBasicTask);
            }

            countDown();
        });

        return testBasicTask;
    }

    @Override
    public void run() {
        super.run();
        countDown();
    }

    private void countDown() {
        if (countDownLatch != null)
            countDownLatch.countDown();
    }
}
