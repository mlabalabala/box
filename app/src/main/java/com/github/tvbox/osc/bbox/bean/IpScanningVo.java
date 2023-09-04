package com.github.tvbox.osc.bbox.bean;

public class IpScanningVo {

    private String hostName;
    private String ip;

    public IpScanningVo(String hostName, String ip) {
        this.hostName = hostName;
        this.ip = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
