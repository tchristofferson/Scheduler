package com.tchristofferson.scheduler;

import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class BasicTests {

    private static final TestBasicScheduler scheduler = new TestBasicScheduler();

    @AfterAll
    public static void shutdownScheduler() {
        scheduler.shutdown();
        assertTrue(scheduler.isShutdown());
    }

    @AfterEach
    public void cancelTasks() {
        scheduler.cancelTasks();
    }

    @Test
    @Order(1)
    @Timeout(1)
    public void testRunTaskWithinTask() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        scheduler.setCountDownLatch(latch);

        scheduler.runTask(() -> {
            System.out.println("Test runTask within another task");
            scheduler.runTask(() -> System.out.println("Task scheduled in another task ran successfully"), 0);
        }, 0).getTaskId();

        latch.await();
        scheduler.run();
    }

    @Test
    @Order(2)
    @Timeout(1)
    public void testGetTask() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        scheduler.setCountDownLatch(latch);
        ITask task = scheduler.runTask(() -> System.out.println("Test getTask"), 10000);
        latch.await();
        assertEquals(scheduler.getTask(task.getTaskId()), task);
    }

    @Test
    @Order(3)
    @Timeout(1)
    public void testRunAsyncTaskIsRanAsync() throws InterruptedException {
        CountDownLatch schedulerLatch = new CountDownLatch(1);
        scheduler.setCountDownLatch(schedulerLatch);
        ITask task = scheduler.runTaskAsynchronously(() -> System.out.println(Thread.currentThread().getId()), 0);
        schedulerLatch.await();
        CountDownLatch taskLatch = new CountDownLatch(1);
        ((TestBasicTask) task).setCountDownLatch(taskLatch);
        scheduler.run();
        taskLatch.await();
        Thread thread = task.getThread();
        assertNotEquals(Thread.currentThread().getId(), thread.getId());
    }

}
