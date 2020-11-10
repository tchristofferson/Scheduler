package com.tchristofferson.scheduler;

public interface ITask {

    int getTaskId();

    long getScheduledTime();

    long getDelay();

    boolean isCanceled();

    boolean isSync();

    boolean isRunning();

    boolean isFinished();

    Thread getThread();

    Runnable getTask();

}
