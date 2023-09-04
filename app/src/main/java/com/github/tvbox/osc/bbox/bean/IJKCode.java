package com.github.tvbox.osc.bbox.bean;

import com.github.tvbox.osc.bbox.util.HawkConfig;
import com.orhanobut.hawk.Hawk;

import java.util.LinkedHashMap;

/**
 * @author pj567
 * @date :2021/3/8
 * @description:
 */
public class IJKCode {
    private String name;
    private LinkedHashMap<String, String> option;
    private boolean selected;

    public void selected(boolean selected) {
        this.selected = selected;
        if (selected) {
            Hawk.put(HawkConfig.IJK_CODEC, name);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashMap<String, String> getOption() {
        return option;
    }

    public void setOption(LinkedHashMap<String, String> option) {
        this.option = option;
    }
}