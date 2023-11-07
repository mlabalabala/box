package com.github.tvbox.osc.bbox.util;

import com.github.tvbox.osc.bbox.constant.URL;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class Checker {

    public interface OnProxyAvailableListener {
        void available(boolean isAvailable);
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

}
