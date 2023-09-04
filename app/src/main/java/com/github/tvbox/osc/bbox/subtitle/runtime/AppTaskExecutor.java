/*
 *                       Copyright (C) of Avery
 *
 *                              _ooOoo_
 *                             o8888888o
 *                             88" . "88
 *                             (| -_- |)
 *                             O\  =  /O
 *                          ____/`- -'\____
 *                        .'  \\|     |//  `.
 *                       /  \\|||  :  |||//  \
 *                      /  _||||| -:- |||||-  \
 *                      |   | \\\  -  /// |   |
 *                      | \_|  ''\- -/''  |   |
 *                      \  .-\__  `-`  ___/-. /
 *                    ___`. .' /- -.- -\  `. . __
 *                 ."" '<  `.___\_<|>_/___.'  >'"".
 *                | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *                \  \ `-.   \_ __\ /__ _/   .-` /  /
 *           ======`-.____`-.___\_____/___.-`____.-'======
 *                              `=- -='
 *           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *              Buddha bless, there will never be bug!!!
 */

package com.github.tvbox.osc.bbox.subtitle.runtime;


import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * @author AveryZhong.
 */

public class AppTaskExecutor extends TaskExecutor {

    private TaskExecutor mDelegate;
    private TaskExecutor mDefaultTaskExecutor;

    private static AppTaskExecutor sInstance;

    @NonNull
    public static TaskExecutor getInstance() {
        if (sInstance == null) {
            synchronized (AppTaskExecutor.class) {
                sInstance = new AppTaskExecutor();
            }
        }
        return sInstance;
    }

    private AppTaskExecutor() {
        mDefaultTaskExecutor = new DefaultTaskExecutor();
        mDelegate = mDefaultTaskExecutor;
    }

    public void setDelegate(final TaskExecutor taskExecutor) {
        mDelegate = taskExecutor == null ? mDefaultTaskExecutor : taskExecutor;
    }

    @Override
    public void executeOnDeskIO(final Runnable task) {
        mDelegate.executeOnDeskIO(task);
    }

    @Override
    public void executeOnMainThread(final Runnable task) {
        mDelegate.executeOnMainThread(task);
    }

    @Override
    public void postToMainThread(final Runnable task) {
        mDelegate.postToMainThread(task);
    }

    @Override
    public boolean isMainThread() {
        return mDelegate.isMainThread();
    }

    private static Executor sDeskIO = new Executor() {
        @Override
        public void execute(@NonNull final Runnable command) {
            getInstance().executeOnDeskIO(command);
        }
    };

    private static Executor sMainThread = new Executor() {
        @Override
        public void execute(@NonNull final Runnable command) {
            getInstance().executeOnMainThread(command);
        }
    };

    public static Executor deskIO() {
        return sDeskIO;
    }

    public static Executor mainThread() {
        return sMainThread;
    }
}
