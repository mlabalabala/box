package com.github.tvbox.osc.bbox.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author pj567
 * @date :2020/12/18
 * @description:
 */
@XStreamAlias("class")
public class MovieSort implements Serializable {
    @XStreamImplicit(itemFieldName = "ty")
    public List<SortData> sortList;

    @XStreamAlias("ty")
    @XStreamConverter(value = ToAttributedValueConverter.class, strings = {"name"})
    public static class SortData implements Serializable, Comparable<SortData> {
        @XStreamAsAttribute
        public String id;
        public String name;
        public int sort = -1;
        public boolean select = false;
        public ArrayList<SortFilter> filters = new ArrayList<>();
        public HashMap<String, String> filterSelect = new HashMap<>();
        public String flag; // 类型

        public SortData() {
        }

        public SortData(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public int filterSelectCount() {
            if (filterSelect == null) {
                return 0;
            }
            int count = 0;
            for (String filter : filterSelect.values()) {
                if (filter != null && !filter.isEmpty()) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public int compareTo(SortData o) {
            return this.sort - o.sort;
        }

        @Override
        public String toString() {
            return "SortData{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", sort=" + sort +
                    ", select=" + select +
                    ", filters=" + filters +
                    ", filterSelect=" + filterSelect +
                    ", flag='" + flag + '\'' +
                    '}';
        }
    }

    public static class SortFilter {
        public String key;
        public String name;
        public LinkedHashMap<String, String> values;

        @Override
        public String toString() {
            return "SortFilter{" +
                    "key='" + key + '\'' +
                    ", name='" + name + '\'' +
                    ", values=" + values +
                    '}';
        }
    }

}