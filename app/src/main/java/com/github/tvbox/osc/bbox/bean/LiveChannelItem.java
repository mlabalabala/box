package com.github.tvbox.osc.bbox.bean;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author pj567
 * @date :2021/1/12
 * @description:
 */
public class LiveChannelItem {
    /**
     * channelIndex : 频道索引号
     * channelNum : 频道名称
     * channelSourceNames : 频道源名称
     * channelUrls : 频道源地址
     * sourceIndex : 频道源索引
     * sourceNum : 频道源总数
     */
    private int channelIndex;
    private int channelNum;
    private String channelName;

    private String channelEpgInfo;
    private ArrayList<String> channelSourceNames;
    private ArrayList<String> channelUrls;
    public int sourceIndex = 0;
    public int sourceNum = 0;
    public boolean include_back = false;

    public String getChannelEpgInfo() {
        return channelEpgInfo;
    }

    public void setChannelEpgInfo(String channelEpgInfo) {
        this.channelEpgInfo = channelEpgInfo;
    }

    public void setinclude_back(boolean include_back) {
        this.include_back = include_back;
    }

    public boolean getinclude_back() {
        return include_back;
    }

    public void setChannelIndex(int channelIndex) {
        this.channelIndex = channelIndex;
    }

    public int getChannelIndex() {
        return channelIndex;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

    public int getChannelNum() {
        return channelNum;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelName() {
        return channelName;
    }

    public ArrayList<String> getChannelUrls() {
        return channelUrls;
    }

    public void setChannelUrls(ArrayList<String> channelUrls) {
        this.channelUrls = channelUrls;
        sourceNum = channelUrls.size();
    }
    public void preSource() {
        sourceIndex--;
        if (sourceIndex < 0) sourceIndex = sourceNum - 1;
    }
    public void nextSource() {
        sourceIndex++;
        if (sourceIndex == sourceNum) sourceIndex = 0;
    }

    public void setSourceIndex(int sourceIndex) {
        this.sourceIndex = sourceIndex;
    }

    public int getSourceIndex() {
        return sourceIndex;
    }

    public String getUrl() {
        return channelUrls.get(sourceIndex);
    }

    public int getSourceNum() {
        return sourceNum;
    }

    public ArrayList<String> getChannelSourceNames() {
        return channelSourceNames;
    }

    public void setChannelSourceNames(ArrayList<String> channelSourceNames) {
        this.channelSourceNames = channelSourceNames;
    }

    public String getSourceName() {
        return channelSourceNames.get(sourceIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiveChannelItem that = (LiveChannelItem) o;

        // 替换 Objects.equals() 为 null 安全的比较
        return equals(channelName, that.channelName)
                && equals(channelUrls.get(sourceIndex), that.getUrl());
    }

    @Override
    public int hashCode() {
        // 替换 Objects.hash() 为 Arrays.hashCode() 或手动计算
        return hashCode(channelName, channelUrls.get(sourceIndex));
    }

    /**
     * 自定义的 null 安全的对象比较方法（替代 Objects.equals()）
     */
    private static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    /**
     * 自定义的哈希值计算方法（替代 Objects.hash()）
     */
    private static int hashCode(Object... values) {
        if (values == null) {
            return 0;
        }

        int result = 1;
        for (Object element : values) {
            result = 31 * result + (element == null ? 0 : element.hashCode());
        }
        return result;
    }

    @Override
    public String toString() {
        return "LiveChannelItem{" +
                "channelIndex=" + channelIndex +
                ", channelNum=" + channelNum +
                ", channelName='" + channelName + '\'' +
                ", channelEpgInfo='" + channelEpgInfo + '\'' +
                ", channelSourceNames=" + channelSourceNames +
                ", channelUrls=" + channelUrls +
                ", sourceIndex=" + sourceIndex +
                ", sourceNum=" + sourceNum +
                ", include_back=" + include_back +
                '}';
    }
}