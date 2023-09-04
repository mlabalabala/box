package com.github.tvbox.osc.bbox.bean;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import com.github.tvbox.osc.bbox.base.App;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppInfo {

    public String name;
    public Drawable icon;
    public String pack;

    public AppInfo(String name, String pack, Drawable icon) {
        this.name = name;
        this.pack = pack;
        this.icon = icon;
    }

    public static AppInfo get(ApplicationInfo info) {
        Drawable icon = info.loadIcon(App.getInstance().getPackageManager());
        String name = info.loadLabel(App.getInstance().getPackageManager()).toString();
        String pack = info.packageName;
        return new AppInfo(name, pack, icon);
    }

    public String getName() {
        return name;
    }

    public String getPack() {
        return pack;
    }

    public Drawable getIcon() {
        return icon;
    }

    public static class Sorter implements Comparator<AppInfo> {

        public static void sort(List<AppInfo> items) {
            Collections.sort(items, new Sorter());
        }

        @Override
        public int compare(AppInfo info1, AppInfo info12) {
            return info1.getName().compareToIgnoreCase(info12.getName());
        }
    }
}