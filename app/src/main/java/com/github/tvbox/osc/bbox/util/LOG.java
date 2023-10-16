package com.github.tvbox.osc.bbox.util;

import android.util.Log;
import com.github.tvbox.osc.bbox.event.LogEvent;
import org.greenrobot.eventbus.EventBus;

/**
 * @author pj567
 * @date :2020/12/18
 * @description:
 */
public class LOG {
    private static String TAG = "BBox";

    public static void e(Throwable t) {
        Log.e(TAG, t.getMessage(), t);
        EventBus.getDefault().post(new LogEvent(String.format("E/%s ==> ", TAG) + Log.getStackTraceString(t)));
    }

    public static void e(String tag, Throwable t) {
        Log.e(tag, t.getMessage(), t);
        EventBus.getDefault().post(new LogEvent(String.format("E/%s ==> ", tag) + Log.getStackTraceString(t)));
    }

    public static void e(String msg) {
        Log.e(TAG, "" + msg);
        EventBus.getDefault().post(new LogEvent(String.format("E/%s ==> ", TAG) + msg));
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
        EventBus.getDefault().post(new LogEvent(String.format("E/%s ==> ", tag) + msg));
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
        EventBus.getDefault().post(new LogEvent(String.format("I/%s ==> ", TAG) + msg));
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
        EventBus.getDefault().post(new LogEvent(String.format("I/%s ==> ", tag) + msg));
    }
}