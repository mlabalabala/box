/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.tvbox.osc.bbox.update.service;

import androidx.annotation.NonNull;
import com.github.tvbox.osc.bbox.util.JsonUtil;
import com.github.tvbox.osc.bbox.update.callback.CustomFileCallback;
import com.github.tvbox.osc.bbox.update.callback.CustomStringCallback;
import com.lzy.okgo.OkGo;
import com.xuexiang.xupdate.proxy.IUpdateHttpService;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

/**
 * 使用okhttp
 *
 * @author xuexiang
 * @since 2018/7/10 下午4:04
 */
public class OkGoUpdateHttpService implements IUpdateHttpService {

    private boolean mIsPostJson;

    public OkGoUpdateHttpService() {
        this(false);
    }

    public OkGoUpdateHttpService(boolean isPostJson) {
        mIsPostJson = isPostJson;
    }


    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, Object> params, final @NonNull Callback callBack) {
        OkGo.<String>get(url)
            .headers("User-Agent", "okhttp/3.15")
            .headers("Accept", "text/html," + "application/xhtml+xml,application/xml;q=0.9,image/avif," + "image/webp,image/apng," + "*/*;q=0.8,application/signed-exchange;v=b3;" + "q=0.9")
            .execute(new CustomStringCallback(callBack));
    }

    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, Object> params, final @NonNull Callback callBack) {
        // 这里默认post的是Form格式，使用json格式的请修改 post -> postString
        if (mIsPostJson) {
            OkGo.<String>post(url).upJson(JsonUtil.toJson(params))
                // .upString(JsonUtil.toJson(params), MediaType.parse("application/json; charset=utf-8"))
                .execute(new CustomStringCallback(callBack));
        } else {
            OkGo.<String>post(url)
                .params(transform(params))
                // .upString(JsonUtil.toJson(params), MediaType.parse("application/json; charset=utf-8"))
                .execute(new CustomStringCallback(callBack));
        }
    }

    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, final @NonNull DownloadCallback callback) {
        OkGo.<File>get(url)
            .tag(url)
            .execute(new CustomFileCallback(callback, path, fileName));
    }

    @Override
    public void cancelDownload(@NonNull String url) {
        OkGo.getInstance().cancelTag(url);
    }

    private Map<String, String> transform(Map<String, Object> params) {
        Map<String, String> map = new TreeMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            map.put(entry.getKey(), entry.getValue().toString());
        }
        return map;
    }

}