package com.github.tvbox.osc.bbox.util.urlhttp.internal;

import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import org.brotli.dec.BrotliInputStream;

import java.io.IOException;

public final class BrotliSource {
  public static Source create(BufferedSource source) throws IOException {
    BrotliInputStream brotliInputStream = new BrotliInputStream(source.inputStream());
    return Okio.source(brotliInputStream);
  }
}
