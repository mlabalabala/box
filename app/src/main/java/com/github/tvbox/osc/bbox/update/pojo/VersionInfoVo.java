package com.github.tvbox.osc.bbox.update.pojo;

public class VersionInfoVo {
    private String desc;
    private int versionCode;
    private String versionName;
    private String downloadUrl;
    private boolean forceUpgrade;

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void setForceUpgrade(boolean forceUpgrade) {
        this.forceUpgrade = forceUpgrade;
    }


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
