package com.whl.quickjs.android;

public final class QuickJSLoader {
    public static void init() {
        init(Boolean.FALSE);
    }

    public static void init(Boolean bool) {
        try {
            System.loadLibrary("quickjs-android-wrapper");
            if (bool) {
                startRedirectingStdoutStderr("QuJs ==> ");
            }
        } catch (Throwable throwable) {

        }
    }

    public static native void startRedirectingStdoutStderr(String str);
}
