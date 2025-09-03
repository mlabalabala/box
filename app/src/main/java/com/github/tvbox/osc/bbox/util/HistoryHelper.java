package com.github.tvbox.osc.bbox.util;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

public class HistoryHelper {
    private static final Integer[] hisNumArray = {30,50,100};

    public static String getHistoryNumName(int index){
        Integer value = getHisNum(index);
        return value + "条";
    }

    public static int getHisNum(int index){
        Integer value = null;
        if(index>=0 && index < hisNumArray.length){
            value = hisNumArray[index];
        }else{
            value = hisNumArray[0];
        }
        return value;
    }

    public static void setSearchHistory(String title){
        // 读取历史记录
        ArrayList<String> history = Hawk.get(HawkConfig.SEARCH_HISTORY, new ArrayList<String>());
        history.remove(title);
        history.add(0, title);
        // 保证最多只保留 15 条，超过的就删除最后一条
        if (history.size() > 15) {
            history.remove(history.size() - 1);
        }
        Hawk.put(HawkConfig.SEARCH_HISTORY, history);
    }

    public static void setLiveApiHistory(String value){
        ArrayList<String> history = Hawk.get(HawkConfig.LIVE_API_HISTORY, new ArrayList<String>());
        if (!history.contains(value)) {
            history.add(0, value);
        }
        if (history.size() > 30) {
            history.remove(30);
        }
        Hawk.put(HawkConfig.LIVE_API_HISTORY, history);
    }

    public static void setApiHistory(String value){
        ArrayList<String> history = Hawk.get(HawkConfig.API_HISTORY, new ArrayList<String>());
        if (!history.contains(value)) {
            history.add(0, value);
        }
        if (history.size() > 30) {
            history.remove(30);
        }
        Hawk.put(HawkConfig.API_HISTORY, history);
    }
}
