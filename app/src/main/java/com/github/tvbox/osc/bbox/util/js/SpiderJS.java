package com.github.tvbox.osc.bbox.util.js;

import android.content.Context;

import com.github.catvod.crawler.Spider;
import com.github.tvbox.quickjs.JSArray;
import com.github.tvbox.quickjs.JSModule;
import com.github.tvbox.quickjs.JSObject;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SpiderJS extends Spider {

    private String key;
    private String js;
    private String ext;
    private JSObject jsObject = null;
    private JSEngine.JSThread jsThread = null;

    public SpiderJS(String key, String js, String ext) {
        this.key = key;
        this.js = js;
        this.ext = ext;
    }

    void checkLoaderJS() {
        if (jsThread == null) {
            jsThread = JSEngine.getInstance().getJSThread();
        }
        if (jsObject == null && jsThread != null) {
            try {
                jsThread.postVoid((ctx, globalThis) -> {
                    String moduleKey = "__" + UUID.randomUUID().toString().replace("-", "") + "__";
                    String jsContent = JSEngine.getInstance().loadModule(js);
                    try {
                        if (js.contains(".js?")) {
                            int spIdx = js.indexOf(".js?");
                            String[] query = js.substring(spIdx + 4).split("&|=");
                            js = js.substring(0, spIdx);
                            for (int i = 0; i < query.length; i += 2) {
                                String key = query[i];
                                String val = query[i + 1];
                                String sub = JSModule.convertModuleName(js, val);
                                String content = JSEngine.getInstance().loadModule(sub);
                                jsContent = jsContent.replace("__" + key.toUpperCase() + "__", content);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(jsContent.contains("export default{")){
                        jsContent = jsContent.replace("export default{", "globalThis." + moduleKey+" ={");
                    }else if(jsContent.contains("export default {")){
                        jsContent = jsContent.replace("export default {", "globalThis." + moduleKey+" ={");
                    }else {
                        jsContent = jsContent.replace("__JS_SPIDER__", "globalThis." + moduleKey);
                    }
                    ctx.evaluateModule(jsContent, js);
                    jsObject = (JSObject) ctx.getProperty(globalThis, moduleKey);
                    jsObject.getJSFunction("init").call(ext);
                    return null;
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    String postFunc(String func, Object... args) {
        checkLoaderJS();
        if (jsObject != null) {
            try {
                return jsThread.post((ctx, globalThis) -> (String) jsObject.getJSFunction(func).call(args));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return "";
    }

    @Override
    public void init(Context context, String extend) {
        super.init(context, extend);
        checkLoaderJS();
    }

    @Override
    public String homeContent(boolean filter) {
        return postFunc("home", filter);
    }

    @Override
    public String homeVideoContent() {
        return postFunc("homeVod");
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        try {
            JSObject obj = jsThread.post((ctx, globalThis) -> {
                JSObject o = ctx.createNewJSObject();
                if (extend != null) {
                    for (String s : extend.keySet()) {
                        o.setProperty(s, extend.get(s));
                    }
                }
                return o;
            });
            return postFunc("category", tid, pg, filter, obj);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return "";

    }

    @Override
    public String detailContent(List<String> ids) {
        return postFunc("detail", ids.get(0));
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            JSArray array = jsThread.post((ctx, globalThis) -> {
                JSArray arr = ctx.createNewJSArray();
                if (vipFlags != null) {
                    for (int i = 0; i < vipFlags.size(); i++) {
                        arr.set(vipFlags.get(i), i);
                    }
                }
                return arr;
            });
            return postFunc("play", flag, id, array);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return "";
    }

    @Override
    public String searchContent(String key, boolean quick) {
        return postFunc("search", key, quick);
    }
}
