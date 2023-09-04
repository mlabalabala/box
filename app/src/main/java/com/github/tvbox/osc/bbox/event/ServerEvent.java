package com.github.tvbox.osc.bbox.event;

/**
 * @author pj567
 * @date :2021/1/5
 * @description:
 */
public class ServerEvent {
    public static final int SERVER_SUCCESS = 0;
    public static final int SERVER_CONNECTION = 1;
    public static final int SERVER_SEARCH = 2;
    public int type;
    public Object obj;

    public ServerEvent(int type) {
        this.type = type;
    }

    public ServerEvent(int type, Object obj) {
        this.type = type;
        this.obj = obj;
    }
}