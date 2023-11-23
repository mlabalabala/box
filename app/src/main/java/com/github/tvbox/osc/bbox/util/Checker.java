package com.github.tvbox.osc.bbox.util;

import com.github.tvbox.osc.bbox.constant.URL;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import okhttp3.OkHttpClient;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Checker {

    public interface OnProxyAvailableListener {
        void available(boolean isAvailable);
    }

    public interface OnCheckProxyUrlsListener {
        void available(String url);
    }

    private OkHttpClient mClient;

    private Checker() {

    }

    private static volatile Checker instance = null;

    public static Checker getInstance() {
        if (instance == null) {
            // 加锁
            synchronized (Checker.class) {
                // 这一次判断也是必须的，不然会有并发问题
                if (instance == null) {
                    instance = new Checker();
                }
            }
        }
        return instance;
    }

    public void checkProxy(OnProxyAvailableListener onProxyAvailableListener) {

        if (mClient == null) {
            mClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .build();
        }

        OkGo.<String>get(URL.DOMAIN_NAME_PROXY)
                .client(mClient)
                .execute(new StringCallback() {

                    boolean isAvailable;

                    @Override
                    public void onSuccess(Response<String> response) {
                        isAvailable = true;
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        isAvailable = false;
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (onProxyAvailableListener != null) {
                            onProxyAvailableListener.available(isAvailable);
                        }
                    }
                });
    }

    public void checkProxy(OnProxyAvailableListener onProxyAvailableListener, String url) {

        if (mClient == null) {
            mClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .build();
        }

        OkGo.<String>get(url)
                .client(mClient)
                .execute(new StringCallback() {

                    boolean isAvailable;

                    @Override
                    public void onSuccess(Response<String> response) {
                        isAvailable = true;
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        isAvailable = false;
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (onProxyAvailableListener != null) {
                            onProxyAvailableListener.available(isAvailable);
                        }
                    }
                });
    }

    public void getAllProxyUrls(OnCheckProxyUrlsListener listener) {

        LOG.i("从sd卡的urls.txt获取带分隔符的链接字符串");

        if (mClient == null) {
            mClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .build();
        }

        OkGo.<String>get("http://127.0.0.1:9978/file/urls.txt")
                .client(mClient)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        String urlsWithDelimiter = response.body();
                        String[] urls = urlsWithDelimiter.split("###");
                        LOG.i("代理链接：" + Arrays.toString(urls));
                        for (String url : urls) {
                            checkProxy(isAvailable -> {
                                if (isAvailable) {
                                    LOG.i("可用链接: " + url);
                                    listener.available(url);
                                }
                            }, url);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        // LOG.i("urls.txt不存在使用默认代理链接: " + URL.DOMAIN_NAME_PROXY);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

}
