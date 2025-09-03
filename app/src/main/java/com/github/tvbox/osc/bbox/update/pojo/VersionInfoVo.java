package com.github.tvbox.osc.bbox.update.pojo;

public class VersionInfoVo {
    private String desc;
    private int versionCode;
    private String versionName;
    private String downloadUrl;
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
    public String getDownloadUrl() {
        return downloadUrl;
    }

    public boolean isForceUpgrade() {
        return forceUpgrade;
    }
}
