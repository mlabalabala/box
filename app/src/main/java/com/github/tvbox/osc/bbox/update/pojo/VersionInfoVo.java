package com.github.tvbox.osc.bbox.update.pojo;

public class VersionInfoVo {
    private String desc;
    private int versionCode;
    private String versionName;
    private boolean forceUpgrade;

    public String getDesc() {
        return desc;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public boolean isForceUpgrade() {
        return forceUpgrade;
    }
}
