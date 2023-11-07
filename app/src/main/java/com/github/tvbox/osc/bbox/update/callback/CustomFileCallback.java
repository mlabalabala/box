package com.github.tvbox.osc.bbox.update.callback;

import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.xuexiang.xupdate.proxy.IUpdateHttpService.DownloadCallback;

import java.io.File;

public class CustomFileCallback extends FileCallback {

    private final DownloadCallback mCallback;

    public CustomFileCallback(DownloadCallback callback, String fileDir, String fileName) {
        super(fileDir, fileName);
        mCallback = callback;
    }

    @Override
    public void onStart(com.lzy.okgo.request.base.Request<File, ? extends com.lzy.okgo.request.base.Request> request) {
        super.onStart(request);
        mCallback.onStart();
    }

    @Override
    public void onError(Response<File> response) {
        super.onError(response);
        mCallback.onError(new RuntimeException("下载失败！"));
    }

    @Override
    public void downloadProgress(Progress progress) {
        mCallback.onProgress(progress.currentSize*1.0f/progress.totalSize, progress.totalSize);
    }

    @Override
    public void onSuccess(Response<File> response) {
        mCallback.onSuccess(response.body());
    }
}