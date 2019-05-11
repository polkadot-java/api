package org.polkadot.common;

import java.util.concurrent.*;

public class ExecutorsManager {

    private static final int CORE_COUNT = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService main = Executors.newSingleThreadExecutor(new NamedThreadFactory("Main-Executor"));
    private static final ExecutorService commonExecutor = Executors.newFixedThreadPool(CORE_COUNT + 1, new NamedThreadFactory("General-Executor"));
    private static final ScheduledExecutorService commonScheduleExecutor = Executors.newScheduledThreadPool(CORE_COUNT, new NamedThreadFactory("Schedule-Executor"));
    public static final ExecutorService battle = Executors.newFixedThreadPool(CORE_COUNT << 1, new NamedThreadFactory("Battle-Executor"));

    /**
     * 提交一个任务给线程池
     *
     * @param r 任务对象
     * @return future对象
     */
    public static Future<?> execute(Runnable r) {
        return commonExecutor.submit(r);
    }

    public static <T> Future<T> execute(Callable<T> r) {
        return commonExecutor.submit(r);
    }

    /**
     * 一段时间后执行一个任务
     *
     * @param r     任务对象
     * @param delay 延迟时间
     * @param tu    时间单位
     * @return future对象
     */
    public static ScheduledFuture<?> schedule(Runnable r, long delay, TimeUnit tu) {
        return commonScheduleExecutor.schedule(r, delay, tu);
    }

    public static ScheduledFuture<?> schedule(ExecutorService es, Runnable r, long delay, TimeUnit tu) {
        return commonScheduleExecutor.schedule(() -> es.submit(r), delay, tu);
    }

}
