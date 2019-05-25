package org.polkadot.common;

import java.util.concurrent.*;

public class ExecutorsManager {

    private static final int CORE_COUNT = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService main = Executors.newSingleThreadExecutor(new NamedThreadFactory("Main-Executor"));
    private static final ExecutorService commonExecutor = Executors.newFixedThreadPool(CORE_COUNT + 1, new NamedThreadFactory("General-Executor"));
    private static final ScheduledExecutorService commonScheduleExecutor = Executors.newScheduledThreadPool(CORE_COUNT, new NamedThreadFactory("Schedule-Executor"));
    public static final ExecutorService battle = Executors.newFixedThreadPool(CORE_COUNT << 1, new NamedThreadFactory("Battle-Executor"));

    /**
     * Submit a task to the thread pool
     *
     * @param r task object
     * @return future object
     */
    public static Future<?> execute(Runnable r) {
        return commonExecutor.submit(r);
    }

    public static <T> Future<T> execute(Callable<T> r) {
        return commonExecutor.submit(r);
    }

    /**
     * Execute a task after some time
     *
     * @param r     task object
     * @param delay delay time
     * @param tu    time unit
     * @return future object
     */
    public static ScheduledFuture<?> schedule(Runnable r, long delay, TimeUnit tu) {
        return commonScheduleExecutor.schedule(r, delay, tu);
    }

    public static ScheduledFuture<?> schedule(ExecutorService es, Runnable r, long delay, TimeUnit tu) {
        return commonScheduleExecutor.schedule(() -> es.submit(r), delay, tu);
    }

}
