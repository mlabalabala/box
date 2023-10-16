package com.github.tvbox.osc.bbox.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 作者：By hdy
 * 日期：On 2018/12/6
 * 时间：At 21:27
 */
public class HeavyTaskUtil {
    //这里的代码是拿的AsyncTask的源码，作用是创建合理可用的线程池容量
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 3)) + 2;
    private static LinkedBlockingDeque<Runnable> taskQueue = new LinkedBlockingDeque<>(8192);
    private static ExecutorService executorService = new ThreadPoolExecutor(CORE_POOL_SIZE, 6,
            10L, TimeUnit.SECONDS, taskQueue);

    public static void executeNewTask(Runnable command) {
//        Log.d(TAG, "executeNewTask: CPU_COUNT=" + CPU_COUNT + ", CORE_POOL_SIZE=" + CORE_POOL_SIZE);
        executorService.execute(command);
    }

    public static void executeBigTask(Runnable command) {
        executorService.execute(command);
    }

    public static ExecutorService getBigTaskExecutorService() {
        return executorService;
    }

    public static LinkedBlockingDeque<Runnable> getBigTaskQueue() {
        return taskQueue;
    }


}
