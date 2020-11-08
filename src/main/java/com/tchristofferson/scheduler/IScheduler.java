package com.tchristofferson.scheduler;

import java.util.List;

public interface IScheduler extends Runnable {

    void cancelTasks();

    List<ITask> getPendingTasks();

    ITask getTask(int taskId);

    ITask runTask(Runnable task, long delay);

    ITask runTaskAsynchronously(Runnable task, long delay);

    /**
     * Shutdown the scheduler. All pending tasks will be discarded
     */
    void shutdown();

    /**
     * Shutdown the scheduler. All tasks will be ran.
     * @param runTasksSynchronously If you want to run all tasks as synchronous tasks even if they were scheduled for asynchronous execution.<br>
     *                              If {@code false} synchronous tasks will be ran synchronous and asynchronous tasks will be ran asynchronous
     */
    void shutdownAndRunTasks(boolean runTasksSynchronously);

}
