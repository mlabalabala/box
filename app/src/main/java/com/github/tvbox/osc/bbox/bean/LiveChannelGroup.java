package com.github.tvbox.osc.bbox.bean;

import java.util.ArrayList;

public class LiveChannelGroup {
    /**
     * groupIndex : 分组索引号
     * groupName : 分组名称
     * password : 分组密码
     */
    private int groupIndex;
    private String groupName;
    private String groupPassword;
    private int size;

    @Override
    public String toString() {
        return "LiveChannelGroup{" +
                "groupIndex=" + groupIndex +
                ", groupName='" + groupName + '\'' +
                ", groupPassword='" + groupPassword + '\'' +
                ", liveChannelItems=" + liveChannelItems +
                '}';
    }

    private ArrayList<LiveChannelItem> liveChannelItems;

    public int getSize() {
        return liveChannelItems.size();
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<LiveChannelItem> getLiveChannels() {
        return liveChannelItems;
    }

    public void setLiveChannels(ArrayList<LiveChannelItem> liveChannelItems) {
        this.liveChannelItems = liveChannelItems;
    }

    public String getGroupPassword() {
        return groupPassword;
    }

    public void setGroupPassword(String groupPassword) {
        this.groupPassword = groupPassword;
    }
}
