package com.github.tvbox.osc.bbox.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import com.github.tvbox.osc.bbox.base.App;
import com.github.tvbox.osc.bbox.server.ControlManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.orhanobut.hawk.Hawk;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

    public static File open(String str) {
        return new File(App.getInstance()
            .getExternalCacheDir()
            .getAbsolutePath() + "/qjscache_" + str + ".js");
    }
    
    public static String genUUID() {
        return UUID.randomUUID()
            .toString()
            .replaceAll("-", "");
    }
    
    public static String getCache(String name) {
        try {
            String code = "";
            File file = open(name);
            if (file.exists()) {
                code = new String(readSimple(file));
            }
            if (TextUtils.isEmpty(code)) {
                return "";
            }
            JsonObject asJsonObject = (new Gson()
                .fromJson(code, JsonObject.class))
                .getAsJsonObject();
            if (((long) asJsonObject.get("expires")
                .getAsInt()) > System.currentTimeMillis() / 1000) {
                return new String(Base64.decode(asJsonObject.get("data")
                    .getAsString(), Base64.URL_SAFE));
            }
            recursiveDelete(open(name));
            return "";
        } catch (Exception e4) {
            return "";
        }
    }
    
    public static byte[] getCacheByte(String name) {
        try {
            File file = open("B_" + name);
            if (file.exists()) {
                return readSimple(file);
            }
            return null;
        } catch (Exception e4) {
            return null;
        }
    }

    public static void setCache(int time, String name, String data) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("expires", (int)(time + (System.currentTimeMillis() / 1000)));
            jSONObject.put("data", Base64.encodeToString(data.getBytes(), Base64.URL_SAFE));    
            writeSimple(jSONObject.toString().getBytes(), open(name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setCacheByte(String name, byte[] data) {
        try {
            writeSimple(byteMerger("//DRPY".getBytes(),Base64.encode(data, Base64.URL_SAFE)), open("B_" + name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }
    
    public static String get(String str) {
        return get(str, null);
    }
    
    public static String get(String str, Map<String, String> headerMap) {
        try {
            HttpHeaders h = new HttpHeaders();
            if (headerMap != null) {
                for (String key : headerMap.keySet()) {
                    h.put(key, headerMap.get(key));
                }
                return OkGo.<String>get(str).headers(h).execute().body().string();
            } else {
                return OkGo.<String>get(str).headers("User-Agent", str.startsWith("https://gitcode.net/") ? UA.randomOne() : "okhttp/3.15").execute().body().string();
            }

        } catch (IOException e) {
            return "";
        }
    }

    private static final Pattern URLJOIN = Pattern.compile("^http.*\\.(js|txt|json|m3u)$", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    public static String loadModule(String name) {
        try {
        	if (name.endsWith("ali.js")) {
                name = "ali.js";
            } else if (name.endsWith("ali_api.js")) {
                name = "ali_api.js";    
            } else if (name.contains("similarity.js")) {
                name = "similarity.js";
            } else if (name.contains("gbk.js")) {
                name = "gbk.js";
            } else if (name.contains("模板.js")) {
                name = "模板.js";
            } else if (name.contains("cat.js")) {
                name = "cat.js";
            }
            Matcher m = URLJOIN.matcher(name);
            if (m.find()) {
                if (!Hawk.get(HawkConfig.DEBUG_OPEN, false)) {
                    String cache = getCache(MD5.encode(name));
                    if (StringUtils.isEmpty(cache)) {
                        String netStr = get(name);
                        if (!TextUtils.isEmpty(netStr)) {
                            setCache(604800, MD5.encode(name), netStr);
                        }
                        return netStr;
                    }
                    return cache;
                } else {
                    return get(name);
                }
            } else if (name.startsWith("assets://")) {
                return getAsOpen(name.substring(9));
            } else if (isAsFile(name, "js/lib")) {
                return getAsOpen("js/lib/" + name);
            } else if (name.startsWith("file://")) {
                return get(ControlManager.get()
                    .getAddress(true) + "file/" + name.replace("file:///", "")
                    .replace("file://", ""));
            } else if (name.startsWith("clan://localhost/")) {
                return get(ControlManager.get()
                    .getAddress(true) + "file/" + name.replace("clan://localhost/", ""));
            } else if (name.startsWith("clan://")) {
                String substring = name.substring(7);
                int indexOf = substring.indexOf(47);
                return get("http://" + substring.substring(0, indexOf) + "/file/" + substring.substring(indexOf + 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return name;
        }
        return name;
    }

    public static boolean isAsFile(String name, String path) {
        try {
            for (String fname: App.getInstance()
                .getAssets()
                .list(path)) {
                if (fname.equals(name.trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getAsOpen(String name) {
        try {
            InputStream is = App.getInstance()
                .getAssets()
                .open(name);
            byte[] data = new byte[is.available()];
            is.read(data);
            return new String(data, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean writeSimple(byte[] data, File dst) {
        try {
            if (dst.exists()) dst.delete();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dst));
            bos.write(data);
            bos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] readSimple(File src) {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
            int len = bis.available();
            byte[] data = new byte[len];
            bis.read(data);
            bis.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String readFileToString(String path, String charsetName) {
        // 定义返回结果
        StringBuilder jsonString = new StringBuilder();

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(path), charsetName));// 读取文件
            String thisLine;
            while ((thisLine = in.readLine()) != null) {
                jsonString.append(thisLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException el) {
                }
            }
        }
        // 返回拼接好的JSON String
        return jsonString.toString();
    }
    
    public static void copyFile(File source, File dest) throws IOException {
        try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    public static String getRootPath() {
        return Environment.getExternalStorageDirectory()
            .getAbsolutePath();
    }

    public static File getLocal(String path) {
        return new File(path.replace("file:/", getRootPath()));
    }

    public static File getCacheDir() {
        return App.getInstance()
            .getCacheDir();
    }
    public static File getExternalCacheDir() {
        return App.getInstance()
            .getExternalCacheDir();
    }
    public static String getExternalCachePath() {
        return getExternalCacheDir()
            .getAbsolutePath();
    }
    public static String getCachePath() {
        return getCacheDir()
            .getAbsolutePath();
    }
    public static void recursiveDelete(File file) {
        if (!file.exists()) return;
        if (file.isDirectory()) {
            for (File f: file.listFiles()) {
                recursiveDelete(f);
            }
        }
        file.delete();
    }

    /**
     * 获取缓存大小
     * @param context
     * @return
     * @throws Exception
     */
    public static String getTotalCacheSize(Context context) {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState()
            .equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }

    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File value: fileList) {
                // 如果下面还有文件
                if (value.isDirectory()) {
                    size = size + getFolderSize(value);
                } else {
                    size = size + value.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            //            return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP)
            .toPlainString() + "TB";
    }
    /***
     * 清理所有缓存
     */
    public static void clearAllCache() {
        deleteDir(getCacheDir());
        if (Environment.getExternalStorageState()
            .equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child: children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    //启动app清除
    public static void cleanPlayerCache() {
        String ijkCachePath = getCachePath() + "/ijkcaches/";
        String thunderCachePath = getCachePath() + "/thunder/";
        String jpaliCachePath = getCachePath() + "/jpali/Downloads/";
        File ijkCacheDir = new File(ijkCachePath);
        File thunderCacheDir = new File(thunderCachePath);
        File jpaliCacheDir = new File(jpaliCachePath);
        try {
            if (ijkCacheDir.exists()) deleteDir(ijkCacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (thunderCacheDir.exists()) deleteDir(thunderCacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (jpaliCacheDir.exists()) deleteDir(jpaliCacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //0805同步q版
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) return "";
        String fileName = filePath;
        int p = fileName.lastIndexOf(File.separatorChar);
        if (p != -1) {
            fileName = fileName.substring(p + 1);
        }
        return fileName;
    }

    public static String getFileNameWithoutExt(String filePath) {
        if (TextUtils.isEmpty(filePath)) return "";
        String fileName = filePath;
        int p = fileName.lastIndexOf(File.separatorChar);
        if (p != -1) {
            fileName = fileName.substring(p + 1);
        }
        p = fileName.indexOf('.');
        if (p != -1) {
            fileName = fileName.substring(0, p);
        }
        return fileName;
    }

    public static String getFileExt(String fileName) {
        if (TextUtils.isEmpty(fileName)) return "";
        int p = fileName.lastIndexOf('.');
        if (p != -1) {
            return fileName.substring(p)
                .toLowerCase();
        }
        return "";
    }

    public static boolean hasExtension(String path) {
        int lastDotIndex = path.lastIndexOf(".");
        int lastSlashIndex = Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\"));
        // 如果路径中有点号，并且点号在最后一个斜杠之后，认为有后缀
        return lastDotIndex > lastSlashIndex && lastDotIndex < path.length() - 1;
    }

    public static void cleanDirectory(File dir) {
        if (!dir.exists()) return;
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return;
        for(File one : files) {
            try {
                deleteFile(one);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteFile(File file) {
        if (!file.exists()) return;
        if (file.isFile()) {
            if (file.canWrite()) file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                if (file.canWrite()) file.delete();
                return;
            }
            for(File one : files) {
                deleteFile(one);
            }
        }
        return;
    }

    public static String readRawFile(int id) {
        StringBuilder tv = new StringBuilder();
        try {
            InputStream is = App.getInstance().getResources().openRawResource(id);
            InputStreamReader isr = new InputStreamReader(is,"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String str = "";
            while((str = br.readLine()) != null){
                tv.append(str).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tv.toString();
    }
}
