package com.tchristofferson.scheduler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class BasicTask implements ITask {

    private final int taskId;
    private final long scheduled;
    private final long delay;
    private final boolean isSync;
    private final AtomicBoolean isCanceled;
    private final AtomicBoolean isRunning;
    private final AtomicBoolean isFinished;
    private final AtomicReference<Thread> thread;
    private final Runnable task;

    public BasicTask(int taskId, boolean isSync, long delay, Runnable task) {
        this.taskId = taskId;
        this.scheduled = System.currentTimeMillis();
        this.delay = delay;
        this.isSync = isSync;
        this.isCanceled = new AtomicBoolean(false);
        this.isRunning = new AtomicBoolean(false);
        this.isFinished = new AtomicBoolean(false);
        this.thread = new AtomicReference<>(null);
        this.task = task;
    }

    @Override
    public int getTaskId() {
        return taskId;
    }

    @Override
    public long getScheduledTime() {
        return scheduled;
    }

    @Override
    public long getDelay() {
        return delay;
    }

    @Override
    public boolean isCanceled() {
        synchronized (isCanceled) {
            return isCanceled.get();
        }
    }

    @Override
    public boolean isSync() {
        return isSync;
    }

    @Override
    public boolean isRunning() {
        synchronized (isRunning) {
            return isRunning.get();
        }
    }

    @Override
    public boolean isFinished() {
        synchronized (isFinished) {
            return isFinished.get();
        }
    }

    @Override
    public Thread getThread() {
        synchronized (thread) {
            return thread.get();
        }
    }

    public synchronized void run() {
        setThread(Thread.currentThread());

        synchronized (isRunning) {
            isRunning.set(true);
        }

        task.run();

        synchronized (isRunning) {
            isRunning.set(false);
        }

        synchronized (isFinished) {
            isFinished.set(true);
        }
    }

    public void setCanceled() {
        synchronized (isCanceled) {
            isCanceled.set(true);
        }
    }

    void setThread(Thread thread) {
        synchronized (this.thread) {
            this.thread.set(thread);
        }
    }

    public Runnable getTask() {
        return task;
    }
}
