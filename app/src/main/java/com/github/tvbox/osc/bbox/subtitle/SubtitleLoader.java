package com.github.tvbox.osc.bbox.subtitle;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.github.tvbox.osc.bbox.subtitle.exception.FatalParsingException;
import com.github.tvbox.osc.bbox.subtitle.format.FormatASS;
import com.github.tvbox.osc.bbox.subtitle.format.FormatSRT;
import com.github.tvbox.osc.bbox.subtitle.format.FormatSTL;
import com.github.tvbox.osc.bbox.subtitle.format.TimedTextFileFormat;
import com.github.tvbox.osc.bbox.subtitle.model.TimedTextObject;
import com.github.tvbox.osc.bbox.subtitle.runtime.AppTaskExecutor;
import com.github.tvbox.osc.bbox.util.FileUtils;
import com.github.tvbox.osc.bbox.util.UnicodeReader;
import com.lzy.okgo.OkGo;
import okhttp3.Response;
import org.apache.commons.io.input.ReaderInputStream;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * @author AveryZhong.
 */

public class SubtitleLoader {
    private static final String TAG = SubtitleLoader.class.getSimpleName();

    private SubtitleLoader() {
        throw new AssertionError("No instance for you.");
    }

    public static void loadSubtitle(final String path, final Callback callback) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Log.d(TAG, "loadSubtitle: path = " + path);
        if (path.startsWith("http://")
                || path.startsWith("https://")) {
            loadFromRemoteAsync(path, callback);
        } else {
            loadFromLocalAsync(path, callback);
        }
    }

    private static void loadFromRemoteAsync(final String remoteSubtitlePath,
                                            final Callback callback) {
        AppTaskExecutor.deskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final SubtitleLoadSuccessResult subtitleLoadSuccessResult = loadFromRemote(remoteSubtitlePath);
                    if (callback != null) {
                        AppTaskExecutor.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(subtitleLoadSuccessResult);
                            }
                        });
                    }

                } catch (final Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        AppTaskExecutor.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    }

                }
            }
        });
    }

    private static void loadFromLocalAsync(final String localSubtitlePath,
                                           final Callback callback) {
        AppTaskExecutor.deskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final SubtitleLoadSuccessResult subtitleLoadSuccessResult = loadFromLocal(localSubtitlePath);
                    if (callback != null) {
                        AppTaskExecutor.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(subtitleLoadSuccessResult);
                            }
                        });
                    }

                } catch (final Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        AppTaskExecutor.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    }

                }
            }
        });
    }

    public SubtitleLoadSuccessResult loadSubtitle(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        try {
            if (path.startsWith("http://")
                    || path.startsWith("https://")) {
                return loadFromRemote(path);
            } else {
                return loadFromLocal(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SubtitleLoadSuccessResult loadFromRemote(final String remoteSubtitlePath)
            throws IOException, FatalParsingException, Exception {
        Log.d(TAG, "parseRemote: remoteSubtitlePath = " + remoteSubtitlePath);
        String referer = "";
        if (remoteSubtitlePath.contains("alicloud") || remoteSubtitlePath.contains("aliyundrive")) {
            referer = "https://www.aliyundrive.com/";
        } else if (remoteSubtitlePath.contains("assrt.net")) {
            referer = "https://secure.assrt.net/";
        }
        String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.54 Safari/537.36";
        Response response = OkGo.<String>get(remoteSubtitlePath.split("#")[0])
                .headers("Referer", referer)
                .headers("User-Agent", ua)
                .execute();
        byte[] bytes = response.body().bytes();
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        if (TextUtils.isEmpty(encoding)) encoding = "UTF-8";
        String content = new String(bytes, encoding);
        InputStream is = new ByteArrayInputStream(content.getBytes());
        String filename = "";
        String contentDispostion = response.header("content-disposition", "");
        String[] cd = contentDispostion.split(";");
        if (cd.length > 1) {
            String filenameInfo = cd[1];
            filenameInfo = filenameInfo.trim();
            if (filenameInfo.startsWith("filename=")) {
                filename = filenameInfo.replace("filename=", "");
                filename = filename.replace("\"", "");
            } else if (filenameInfo.startsWith("filename*=")) {
                filename = filenameInfo.substring(filenameInfo.lastIndexOf("''")+2);
            }
            filename = filename.trim();
            filename = URLDecoder.decode(filename);
        }
        String filePath = filename;
        if (filename == null || filename.length() < 1) {
            Uri uri = Uri.parse(remoteSubtitlePath);
            filePath = uri.getPath();
        }
        if (!filePath.contains(".") && remoteSubtitlePath.contains("#")) {
            filePath = remoteSubtitlePath.split("#")[1];
            filePath = URLDecoder.decode(filePath);
        }
        SubtitleLoadSuccessResult subtitleLoadSuccessResult = new SubtitleLoadSuccessResult();
        subtitleLoadSuccessResult.timedTextObject = loadAndParse(is, filePath);
        subtitleLoadSuccessResult.fileName = filePath;
        subtitleLoadSuccessResult.content = content;
        subtitleLoadSuccessResult.subtitlePath = remoteSubtitlePath;
        return subtitleLoadSuccessResult;
    }

    private static SubtitleLoadSuccessResult loadFromLocal(final String localSubtitlePath)
            throws IOException, FatalParsingException {
        Log.d(TAG, "parseLocal: localSubtitlePath = " + localSubtitlePath);
        File file = new File(localSubtitlePath);
        if (!file.exists()) {
            Log.d(TAG, "parseLocal: localSubtitlePath = " + localSubtitlePath + " file not exsits");
            return null;
        }
        byte[] bytes = FileUtils.readSimple(file);
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        String content = new String(bytes, encoding);
        InputStream is = new ByteArrayInputStream(content.getBytes());
        String filePath = file.getPath();
        SubtitleLoadSuccessResult subtitleLoadSuccessResult = new SubtitleLoadSuccessResult();
        subtitleLoadSuccessResult.timedTextObject = loadAndParse(is, filePath);
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        subtitleLoadSuccessResult.fileName = fileName;
        subtitleLoadSuccessResult.subtitlePath = localSubtitlePath;
        return subtitleLoadSuccessResult;
    }

    private static TimedTextObject loadAndParse(final InputStream is, final String filePath)
            throws IOException, FatalParsingException {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        String ext = "";
        if (fileName.lastIndexOf(".") > 0) {
            ext = fileName.substring(fileName.lastIndexOf("."));
        }
        Log.d(TAG, "parse: name = " + fileName + ", ext = " + ext);
        Reader reader = new UnicodeReader(is); //处理有BOM头的utf8
        InputStream newInputStream = new ReaderInputStream(reader, Charset.defaultCharset());
        if (".srt".equalsIgnoreCase(ext)) {
            return new FormatSRT().parseFile(fileName, newInputStream);
        } else if (".ass".equalsIgnoreCase(ext)) {
            return new FormatASS().parseFile(fileName, newInputStream);
        } else if (".stl".equalsIgnoreCase(ext)) {
            return new FormatSTL().parseFile(fileName, newInputStream);
        } else if (".ttml".equalsIgnoreCase(ext)) {
            return new FormatSTL().parseFile(fileName, newInputStream);
        }
        TimedTextFileFormat[] arr = {new FormatSRT(), new FormatASS(), new FormatSTL(), new FormatSTL()};
        for(TimedTextFileFormat oneFormat : arr) {
            try {
                TimedTextObject obj = oneFormat.parseFile(fileName, newInputStream);
                return obj;
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    public interface Callback {
        void onSuccess(SubtitleLoadSuccessResult SubtitleLoadSuccessResult);

        void onError(Exception exception);
    }
}
