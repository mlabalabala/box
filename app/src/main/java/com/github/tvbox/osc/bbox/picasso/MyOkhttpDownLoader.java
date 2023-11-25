/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tvbox.osc.bbox.picasso;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import com.github.tvbox.osc.bbox.util.UA;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Downloader;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A {@link Downloader} which uses OkHttp to download images.
 */
public final class MyOkhttpDownLoader implements Downloader {
    @VisibleForTesting
    final Call.Factory client;
    private final Cache cache;
    private boolean sharedClient = true;

    /**
     * Create a new downloader that uses the specified OkHttp instance. A response cache will not be
     * automatically configured.
     */
    public MyOkhttpDownLoader(OkHttpClient client) {
        this.client = client;
        this.cache = client.cache();
    }

    /**
     * Create a new downloader that uses the specified {@link Call.Factory} instance.
     */
    public MyOkhttpDownLoader(Call.Factory client) {
        this.client = client;
        this.cache = null;
    }

    @NonNull
    @Override
    public Response load(@NonNull Request request) throws IOException {
        String url = request.url().toString();
        String header = null;
        String cookie = null;
        String ua = null;
        String referer = null;

        //检查链接里面是否有自定义header
        if (url.contains("@Headers=")){
            header =url.split("@Headers=")[1].split("@")[0];
            header =URLDecoder.decode(header,"UTF-8");
        }
        if (url.contains("@Cookie=")) cookie= url.split("@Cookie=")[1].split("@")[0];
        if (url.contains("@User-Agent=")) ua =url.split("@User-Agent=")[1].split("@")[0];
        if (url.contains("@Referer=")) referer= url.split("@Referer=")[1].split("@")[0];

        url = url.split("@")[0];
        Request.Builder mRequestBuilder = request.newBuilder().url(url);
        if(!TextUtils.isEmpty(header)) {
            JsonObject jsonInfo = new Gson().fromJson(header, JsonObject.class);
            for (String key : jsonInfo.keySet()) {
                String val = jsonInfo.get(key).getAsString();
                mRequestBuilder.addHeader(key.toUpperCase(), removeDuplicateSlashes(val));
            }
        }else {
            if(!TextUtils.isEmpty(cookie)) {
                assert cookie != null;
                mRequestBuilder.addHeader("Cookie", cookie);
            }
            if(!TextUtils.isEmpty(ua)){
                assert ua != null;
                mRequestBuilder.addHeader("User-Agent", ua);
            }else {
                String mobile_UA = "Dalvik/2.1.0 (Linux; U; Android 13; M2102J2SC Build/TKQ1.220829.002)";
                mRequestBuilder.addHeader("User-Agent", mobile_UA);
            }
            if(!TextUtils.isEmpty(referer)){
                assert referer != null;
                mRequestBuilder.addHeader("Referer", referer);
            }
        }

        return client.newCall(mRequestBuilder.build()).execute();
    }

    private static String removeDuplicateSlashes(String paramValue) {
        return paramValue.replaceAll("//", "/");
    }
    @Override
    public void shutdown() {
        if (!sharedClient && cache != null) {
            try {
                cache.close();
            } catch (IOException ignored) {
            }
        }
    }
}
