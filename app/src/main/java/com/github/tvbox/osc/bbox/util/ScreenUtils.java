package com.github.tvbox.osc.bbox.util;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenUtils {

    public static double getSqrt(Activity activity) {
        WindowManager wm = activity.getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);// 屏幕尺寸
        return screenInches;
    }

    private static boolean checkScreenLayoutIsTv(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) > Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private static boolean checkIsPhone(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    public static boolean isTv(Context context) {
        // 1. 系统特性检测（主要方法）
        PackageManager pm = context.getPackageManager();
        boolean isTvByFeature = pm.hasSystemFeature(PackageManager.FEATURE_TELEVISION);

        // 2. 对于Android 5.0+的设备，额外检查Leanback特性
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isTvByFeature = isTvByFeature || pm.hasSystemFeature(PackageManager.FEATURE_LEANBACK);
        }

        // 3. 你的原有备用检测逻辑
        return isTvByFeature || (checkScreenLayoutIsTv(context) && !checkIsPhone(context));
    }


}
