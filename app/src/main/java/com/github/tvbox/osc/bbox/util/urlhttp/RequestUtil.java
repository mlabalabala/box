package com.github.tvbox.osc.bbox.util.urlhttp;

import android.text.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by fighting on 2017/4/24.
 */

class RequestUtil {
    private Thread mThread;

    /**
     * 一般的get请求或post请求
     */
    RequestUtil(String method, String url, Map<String, String> paramsMap, Map<String, String> headerMap, CallBackUtil callBack) {
        switch (method) {
            case "GET":
                urlHttpGet(url, paramsMap, headerMap, callBack);
                break;
            case "POST":
                urlHttpPost(url, paramsMap, null, headerMap, callBack);
                break;
        }
    }

    /**
     * post请求，传递json格式数据。
     */
    RequestUtil(String url, String jsonStr, Map<String, String> headerMap, CallBackUtil callBack) {
        urlHttpPost(url, null, jsonStr, headerMap, callBack);
    }

    /**
     * 上传文件
     */
    RequestUtil(String url, File file, List<File> fileList, Map<String, File> fileMap, String fileKey, String fileType, Map<String, String> paramsMap, Map<String, String> headerMap, CallBackUtil callBack) {
        urlHttpUploadFile(url, file, fileList, fileMap, fileKey, fileType, paramsMap, headerMap, callBack);
    }

    /**
     * get请求
     */
    private void urlHttpGet(final String url, final Map<String, String> paramsMap, final Map<String, String> headerMap, final CallBackUtil callBack) {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                RealResponse response = new RealRequest().getData(getUrl(url, paramsMap), headerMap);
                if (response.code == HttpURLConnection.HTTP_OK) {
                    callBack.onSeccess(response);
                } else {
                    callBack.onError(response);
                }
            }

        });
    }

    /**
     * post请求
     */
    private void urlHttpPost(final String url, final Map<String, String> paramsMap, final String jsonStr, final Map<String, String> headerMap, final CallBackUtil callBack) {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                RealResponse response = new RealRequest().postData(url, getPostBody(paramsMap, jsonStr), getPostBodyType(paramsMap, jsonStr), headerMap);
                if (response.code == HttpURLConnection.HTTP_OK) {
                    callBack.onSeccess(response);
                } else {
                    callBack.onError(response);
                }

            }

        });

    }

    /**
     * 上传文件
     */
    private void urlHttpUploadFile(final String url, final File file, final List<File> fileList, final Map<String, File> fileMap, final String fileKey, final String fileType, final Map<String, String> paramsMap, final Map<String, String> headerMap, final CallBackUtil callBack) {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                RealResponse response = null;
                response = new RealRequest().uploadFile(url, file, fileList, fileMap, fileKey, fileType, paramsMap, headerMap, callBack);
                if (response.code == HttpURLConnection.HTTP_OK) {
                    callBack.onSeccess(response);
                } else {
                    callBack.onError(response);
                }
            }

        });
    }


    /**
     * get请求，将键值对凭接到url上
     */
    private String getUrl(String path, Map<String, String> paramsMap) {
        if (paramsMap != null) {
            path = path + "?";
            for (String key : paramsMap.keySet()) {
                path = path + key + "=" + paramsMap.get(key) + "&";
            }
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * 得到post请求的body
     */
    private String getPostBody(Map<String, String> params, String jsonStr) {//throws UnsupportedEncodingException {
        if (params != null) {
            return getPostBodyFormParameMap(params);
        } else if (!TextUtils.isEmpty(jsonStr)) {
            return jsonStr;
        }
        return null;
    }


    /**
     * 根据键值对参数得到body
     */
    private String getPostBodyFormParameMap(Map<String, String> params) {//throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return result.toString();
        } catch (UnsupportedEncodingException e) {
            return null;
        }

    }

    /**
     * 得到bodyType
     */
    private String getPostBodyType(Map<String, String> paramsMap, String jsonStr) {
        if (paramsMap != null) {
            //return "text/plain";不知为什么这儿总是报错。目前暂不设置(20170424)
            return null;
        } else if (!TextUtils.isEmpty(jsonStr)) {
            return "application/json;charset=utf-8";
        }
        return null;
    }


    /**
     * 开启子线程，调用run方法
     */
    void execute() {
        if (mThread != null) {
            mThread.start();
        }
    }





}
