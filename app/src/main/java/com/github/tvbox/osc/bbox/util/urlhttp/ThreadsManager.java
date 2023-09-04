package com.github.tvbox.osc.bbox.util.urlhttp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadsManager {
    private static ExecutorService mExecutorService;
    private static Map<Integer, Future> mTaskMap = new HashMap();

    public ThreadsManager() {
    }

    static {
    }

    public static void init() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newCachedThreadPool();
            clear();
        }
    }

    public static void clear() {
        Collection<Future> values = mTaskMap.values();
        if (values.size() > 0) {
            for (Future hashCode : values) {
                stop(Integer.valueOf(hashCode.hashCode()));
            }
        }
    }

    public static Integer post(Runnable runnable) {
        if (mExecutorService == null) {
            init();
        }
        Future<?> submit = mExecutorService.submit(runnable);
        Integer valueOf = Integer.valueOf(submit.hashCode());
        mTaskMap.put(Integer.valueOf(submit.hashCode()), submit);
        return valueOf;
    }

    public static void stop(Integer num) {
        Future future = mTaskMap.get(num);
        if (future != null) {
            mTaskMap.remove(num);
            if (!future.isDone() && !future.isCancelled() && mExecutorService != null) {
                future.cancel(true);
            }
        }
    }
}
