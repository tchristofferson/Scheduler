package com.tchristofferson.scheduler;

import java.util.List;

public interface IScheduler extends Runnable {

    /**
     * Cancel all pending tasks
     */
    void cancelTasks();

    /**
     * Get all pending tasks
     * @return A List of pending tasks
     */
    List<ITask> getPendingTasks();

    /**
     * Get a pending task
     * @param taskId The id of the task
     * @return The task with the specified taskId
     */
    ITask getTask(int taskId);

    /**
     * Schedules a task to be ran at a later time
     * @param task The runnable to be ran later
     * @param delay The delay in milliseconds when you want the task to be ran
     * @return The task
     */
    ITask runTask(Runnable task, long delay);

    /**
     * Schedules a task to be ran at a later time asynchronously
     * @param task The runnable to be ran later
     * @param delay The delay in milliseconds when you want the task to be ran
     * @return The task
     */
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

    /**
     * Check if the scheduler is shutdown
     * @return {@code true} if the scheduler is shutdown, {@code false} otherwise
     */
    boolean isShutdown();

}
