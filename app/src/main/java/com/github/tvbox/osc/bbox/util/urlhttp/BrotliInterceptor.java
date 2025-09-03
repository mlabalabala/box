package com.github.tvbox.osc.bbox.util.urlhttp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.github.tvbox.osc.bbox.util.urlhttp.internal.BrotliSource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import okio.GzipSource;
import okio.Okio;
import okio.Source;

public class BrotliInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request userRequest = chain.request();
        if (chain.request().header("Accept-Encoding") == null) {
            userRequest = chain.request().newBuilder()
                    .header("Accept-Encoding", "br,gzip")
                    .build();
          return uncompress(chain.proceed(userRequest));
        }
        return chain.proceed(userRequest);
    }
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(@Nullable CharSequence str) {
        return !isEmpty(str);
    }
    @NotNull
    public final Response uncompress(@NotNull Response response) throws IOException {
        ResponseBody body = response.body();
        if (body != null) {
            String encoding = response.header("Content-Encoding");
            if (isNotEmpty(encoding)) {
                Source brotliSource;
                if (encoding.equals("br")) {
                    brotliSource = BrotliSource.create(body.source());
                } else if (encoding.equals("gzip")) {
                    brotliSource = new GzipSource(body.source());
                } else {
                    return response;
                }
                return response.newBuilder()
                        .removeHeader("Content-Encoding")
                        .removeHeader("Content-Length")
                        .body(RealResponseBody.create(body.contentType(), -1L, Okio.buffer(brotliSource)))
                        .build();
            } else {
                return response;
            }
        } else {
            return response;
        }
    }
}


