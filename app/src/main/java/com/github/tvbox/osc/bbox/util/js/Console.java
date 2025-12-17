package com.github.tvbox.osc.bbox.util.js;

import android.util.Log;
import com.whl.quickjs.wrapper.QuickJSContext;

public class Console implements QuickJSContext.Console {

    private static final String TAG = "quickjs";

    @Override
    public void log(String info) {
        Log.d(TAG, info);
    }

    @Override
    public void info(String info) {
        Log.i(TAG, info);
    }

    @Override
    public void warn(String info) {
        Log.w(TAG, info);
    }

    @Override
    public void error(String info) {
        Log.e(TAG, info);
    }
}