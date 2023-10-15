package com.github.tvbox.osc.bbox.util;

import android.os.Environment;

import android.text.TextUtils;
import com.github.tvbox.osc.bbox.base.App;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FileUtils {

    public static boolean writeSimple(byte[] data, File dst) {
        try {
            if (dst.exists())
                dst.delete();
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

    public static void copyFile(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public static void recursiveDelete(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                recursiveDelete(f);
            }
        }
        file.delete();
    }

    public static String readFileToString(String path, String charsetName) {
        // 定义返回结果
        String jsonString = "";

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), charsetName));// 读取文件
            String thisLine = null;
            while ((thisLine = in.readLine()) != null) {
                jsonString += thisLine;
            }
            in.close();
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
        return jsonString;
    }

    public static String getAssetFile(String assetName) throws IOException {
        InputStream is = App.getInstance().getAssets().open(assetName);
        byte[] data = new byte[is.available()];
        is.read(data);
        return new String(data, "UTF-8");
    }

    public static boolean isAssetFile(String name, String path) {
        try {
            for(String one : App.getInstance().getAssets().list(path)) {
                if (one.equals(name)) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getRootPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static File getLocal(String path) {
        return new File(path.replace("file:/", getRootPath()));
    }

    public static File getCacheDir() {
        return App.getInstance().getCacheDir();
    }

    public static String getCachePath() {
        return getCacheDir().getAbsolutePath();
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

    public static void cleanPlayerCache() {
        String ijkCachePath = getCachePath() + "/ijkcaches/";
        String thunderCachePath = getCachePath() + "/thunder/";
        File ijkCacheDir = new File(ijkCachePath);
        File thunderCacheDir = new File(thunderCachePath);
        try {
            if (ijkCacheDir.exists()) cleanDirectory(ijkCacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (thunderCacheDir.exists()) cleanDirectory(thunderCacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String read(String path) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(getLocal(path))));
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) sb.append(text).append("\n");
            br.close();
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getFileName(String filePath){
        if(TextUtils.isEmpty(filePath)) return "";
        String fileName = filePath;
        int p = fileName.lastIndexOf(File.separatorChar);
        if(p != -1){
            fileName = fileName.substring(p + 1);
        }
        return fileName;
    }

    public static String getFileNameWithoutExt(String filePath){
        if(TextUtils.isEmpty(filePath)) return "";
        String fileName = filePath;
        int p = fileName.lastIndexOf(File.separatorChar);
        if(p != -1){
            fileName = fileName.substring(p + 1);
        }
        p = fileName.indexOf('.');
        if(p != -1){
            fileName = fileName.substring(0, p);
        }
        return fileName;
    }

    public static String getFileExt(String fileName){
        if(TextUtils.isEmpty(fileName)) return "";
        int p = fileName.lastIndexOf('.');
        if(p != -1) {
            return fileName.substring(p).toLowerCase();
        }
        return "";
    }
}
