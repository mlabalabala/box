package com.github.tvbox.osc.bbox.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;

/**
 * @author pj567
 * @date :2020/12/18
 * @description:
 */
@XStreamAlias("rss")
public class AbsXml implements Serializable {
    @XStreamAlias("list")
    public Movie movie;

    @XStreamAlias("msg")
    public String msg;
}