package com.github.tvbox.osc.bbox.update.callback;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.xuexiang.xupdate.proxy.IUpdateHttpService;

public class CustomStringCallback extends StringCallback {

    private final IUpdateHttpService.Callback mCallBack;

    public CustomStringCallback(IUpdateHttpService.Callback callBack) {
        mCallBack = callBack;
    }

    @Override
    public void onError(Response<String> response) {
        mCallBack.onError(new RuntimeException("请求失败，没有获取到数据！！"));
    }

    @Override
    public void onSuccess(Response<String> response) {
        mCallBack.onSuccess(response.body());
    }

    @Override
    public String convertResponse(okhttp3.Response response) throws Throwable {
        assert response.body() != null;
        return response.body().string();
    }
}