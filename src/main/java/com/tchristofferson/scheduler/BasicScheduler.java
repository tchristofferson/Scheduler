package com.tchristofferson.scheduler;

import com.tchristofferson.scheduler.util.Validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BasicScheduler implements IScheduler {

    private final AtomicBoolean isShutdown = new AtomicBoolean(false);
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final List<BasicTask> pendingTasks = new ArrayList<>();
    //Initial value is -1
    //getNextId method will increment first task so it will have id of 0
    private final AtomicInteger taskIdCounter = new AtomicInteger(-1);

    @Override
    public void cancelTasks() {
        synchronized (pendingTasks) {
            Iterator<BasicTask> iterator = pendingTasks.iterator();

            while (iterator.hasNext()) {
                BasicTask task = iterator.next();

                synchronized (task) {
                    if (!task.isRunning() && !task.isFinished()) {
                        task.setCanceled();
                        iterator.remove();
                    }
                }
            }
        }
    }

    @Override
    public List<ITask> getPendingTasks() {
        synchronized (pendingTasks) {
            return new ArrayList<>(pendingTasks);
        }
    }

    @Override
    public ITask getTask(int taskId) {
        synchronized (pendingTasks) {
            for (BasicTask task : pendingTasks) {
                if (task.getTaskId() == taskId)
                    return task;
            }
        }

        return null;
    }

    @Override
    public ITask runTask(Runnable task, long delay) {
        Validator.validateDelay(delay);
        return addTask(true, delay, task);
    }

    @Override
    public ITask runTaskAsynchronously(Runnable task, long delay) {
        Validator.validateDelay(delay);
        return addTask(false, delay, task);
    }

    @Override
    public void shutdown() {
        setIsShutDown();
        executorService.shutdown();

        synchronized (pendingTasks) {
            pendingTasks.clear();
        }
    }

    @Override
    public void shutdownAndRunTasks(boolean runTasksSynchronously) {
        setIsShutDown();
        executorService.shutdown();

        if (runTasksSynchronously) {
            synchronized (pendingTasks) {
                for (BasicTask task : pendingTasks) {
                    task.run();
                }
            }
        } else {
            run(true);
        }

        synchronized (pendingTasks) {
            pendingTasks.clear();
        }
    }

    //Should run frequently
    //A game loop is an example where this would be ran each iteration
    @Override
    public void run() {
        run(false);
    }

    private void run(boolean isShuttingDown) {
        if (isShuttingDown) {
            synchronized (isShutdown) {
                if (!isShutdown.get())
                    throw new IllegalStateException("Scheduler isn't shutting down");
            }
        } else {
            synchronized (isShutdown) {
                if (isShutdown.get())
                    throw new IllegalStateException("Scheduler has been shutdown");
            }
        }

        final long time = System.currentTimeMillis();
        List<BasicTask> syncTasks = new ArrayList<>();

        synchronized (pendingTasks) {
            Iterator<BasicTask> iterator = pendingTasks.iterator();

            while (iterator.hasNext()) {
                BasicTask task = iterator.next();

                if (!isShuttingDown && task.getScheduledTime() + task.getDelay() > time)
                    continue;

                if (task.isSync())
                    syncTasks.add(task);
                else
                    executorService.execute(task::run);

                iterator.remove();
            }

            for (BasicTask task : syncTasks) {
                task.run();
            }
        }
    }

    private int getNextId() {
        synchronized (taskIdCounter) {
            return taskIdCounter.incrementAndGet();
        }
    }

    private BasicTask addTask(boolean isSync, long delay, Runnable task) {
        BasicTask basicTask = new BasicTask(getNextId(), isSync, delay, task);

        synchronized (pendingTasks) {
            pendingTasks.add(basicTask);
        }

        return basicTask;
    }

    private void setIsShutDown() {
        synchronized (isShutdown) {
            if (isShutdown.get())
                throw new IllegalStateException("Scheduler was already shutdown");

            isShutdown.set(true);
        }
    }
}
