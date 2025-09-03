package com.github.tvbox.osc.bbox.util.urlhttp;

import com.github.tvbox.osc.bbox.util.OkGoHelper;
import com.github.tvbox.osc.bbox.util.UA;
import com.lzy.okgo.OkGo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class OkHttpUtil {

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    public static String string(OkHttpClient client, String url, String tag, Map<String, String> paramsMap, Map<String, String> headerMap, Map<String, List<String>> respHeaderMap) {
        OKCallBack<String> stringCallback = new OKCallBack<String>() {
            @Override
            public String onParseResponse(Call call, Response response) {
                try {
                    if (respHeaderMap != null) {
                        respHeaderMap.clear();
                        respHeaderMap.putAll(response.headers().toMultimap());
                    }
                    return response.body().string();
                } catch (IOException e) {
                    return "";
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {
                setResult("");
            }

            @Override
            public void onResponse(String response) {
            }
        };
        OKRequest req = new OKRequest(METHOD_GET, url, paramsMap, headerMap, stringCallback);
        req.setTag(tag);
        req.execute(client);
        return stringCallback.getResult();
    }

    public static String stringNoRedirect(String url, Map<String, String> headerMap, Map<String, List<String>> respHeaderMap) {
        return string(OkGoHelper.getNoRedirectClient(), url, null, null, headerMap, respHeaderMap);
    }

    public static String string(String url, Map<String, String> headerMap, Map<String, List<String>> respHeaderMap) {
        return string(OkGoHelper.getDefaultClient(), url, null, null, headerMap, respHeaderMap);
    }

    public static String string(String url, Map<String, String> headerMap) {
        return string(OkGoHelper.getDefaultClient(), url, null, null, headerMap, null);
    }

    public static String string(String url, String tag, Map<String, String> headerMap) {
        return string(OkGoHelper.getDefaultClient(), url, tag, null, headerMap, null);
    }

    public static void get(OkHttpClient client, String url, OKCallBack callBack) {
        get(client, url, null, null, callBack);
    }

    public static void get(OkHttpClient client, String url, Map<String, String> paramsMap, OKCallBack callBack) {
        get(client, url, paramsMap, null, callBack);
    }

    public static void get(OkHttpClient client, String url, Map<String, String> paramsMap, Map<String, String> headerMap, OKCallBack callBack) {
        new OKRequest(METHOD_GET, url, paramsMap, headerMap, callBack).execute(client);
    }

    public static void post(OkHttpClient client, String url, OKCallBack callBack) {
        post(client, url, null, callBack);
    }

    public static void post(OkHttpClient client, String url, Map<String, String> paramsMap, OKCallBack callBack) {
        post(client, url, paramsMap, null, callBack);
    }

    public static void post(OkHttpClient client, String url, Map<String, String> paramsMap, Map<String, String> headerMap, OKCallBack callBack) {
        new OKRequest(METHOD_POST, url, paramsMap, headerMap, callBack).execute(client);
    }

    public static void post(OkHttpClient client, String url, String tag, Map<String, String> paramsMap, Map<String, String> headerMap, OKCallBack callBack) {
        OKRequest req = new OKRequest(METHOD_POST, url, paramsMap, headerMap, callBack);
        req.setTag(tag);
        req.execute(client);
    }

    public static void postJson(OkHttpClient client, String url, String jsonStr, OKCallBack callBack) {
        postJson(client, url, jsonStr, null, callBack);
    }

    public static void postJson(OkHttpClient client, String url, String jsonStr, Map<String, String> headerMap, OKCallBack callBack) {
        new OKRequest(METHOD_POST, url, jsonStr, headerMap, callBack).execute(client);
    }

    public static String get(String str) {
        try {
            return OkGo.<String>get(str).headers("User-Agent", UA.random()).execute().body().string();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * 根据Tag取消请求
     */
    public static void cancel(OkHttpClient client, Object tag) {
        if (client == null || tag == null) return;
        for (Call call : client.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    public static void cancel(Object tag) {
        cancel(OkGoHelper.getDefaultClient(), tag);
    }
    
    public static void cancelAll() {
        cancelAll(OkGoHelper.getDefaultClient());
    }

    /**
     * 取消所有请求请求
     */
    public static void cancelAll(OkHttpClient client) {
        if (client == null) return;
        for (Call call : client.dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : client.dispatcher().runningCalls()) {
            call.cancel();
        }
    }

    /**
     * 获取重定向地址
     *
     * @param headers
     * @return
     */
    public static String getRedirectLocation(Map<String, List<String>> headers) {
        if (headers == null)
            return null;
        if (headers.containsKey("location"))
            return headers.get("location").get(0);
        if (headers.containsKey("Location"))
            return headers.get("Location").get(0);
        return null;
    }
}
