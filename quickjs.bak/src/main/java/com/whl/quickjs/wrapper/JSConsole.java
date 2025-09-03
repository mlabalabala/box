package com.whl.quickjs.wrapper;

import java.util.HashMap;
import java.util.Map;

public class JSConsole {
    private final Map<String, Long> timer = new HashMap<>();
    private final QuickJSContext.Console console;
    private int count;

    JSConsole(QuickJSContext.Console console) {
        this.console = console;
    }

    @Function
    public final void log(String msg) {
        count++;
        this.console.log(msg);
    }

    @Function
    public final void info(String msg) {
        count++;
        this.console.log(msg);
    }

    @Function
    public final void debug(String msg) {
        count++;
        this.console.log(msg);
    }

    @Function
    public final void error(String msg) {
        count++;
        this.console.log(msg);
    }

    @Function
    public final void warn(String msg) {
        count++;
        this.console.log(msg);
    }

    @Function
    public final int count() {
        return count;
    }

    @Function
    public final void table(JSObject obj) {
        if (obj instanceof JSArray) {
            log(((JSArray) obj).toJsonArray().toString());
        } else if (obj != null) {
            log(obj.toJsonObject().toString());
        }
    }

    @Function
    public final void time(String name) {
        if (timer.containsKey(name)) {
            warn(String.format("Timer '%s' already exists", name));
            return;
        }
        timer.put(name, System.currentTimeMillis());
    }

    @Function
    public final void timeEnd(String name) {
        Long startTime = timer.get(name);
        if (startTime != null) {
            float ms = (System.currentTimeMillis() - startTime);
            log(String.format("%s: %s ms", name, ms));
        }
        timer.remove(name);
    }

    @Function
    public void trace() {
        log("This 'console.trace' function is not supported");
    }

    @Function
    public void clear() {
        log("This 'console.clear' function is not supported");
    }

    @Function
    public void group(String name) {
        log("This 'console.group' function is not supported");
    }

    @Function
    public void groupCollapsed(String name) {
        log("This 'console.groupCollapsed' function is not supported");
    }

    @Function
    public void groupEnd(String name) {
        log("This 'console.groupEnd' function is not supported");
    }

}