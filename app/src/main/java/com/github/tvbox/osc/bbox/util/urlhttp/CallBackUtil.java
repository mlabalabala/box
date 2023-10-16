package com.github.tvbox.osc.bbox.util.urlhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.*;

/**
 * Created by fighting on 2017/4/7.
 */

public abstract class CallBackUtil<T> {
    static Handler mMainHandler = new Handler(Looper.getMainLooper());


    public  void onProgress(float progress, long total ){}

    void onError(final RealResponse response){

        final String errorMessage;
        if(response.inputStream != null){
            errorMessage = getRetString(response.inputStream);
        }else if(response.errorStream != null) {
            errorMessage = getRetString(response.errorStream);
        }else if(response.exception != null) {
            errorMessage = response.exception.getMessage();
        }else {
            errorMessage = "";
        }
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                onFailure(response.code,errorMessage);
            }
        });
    }
    void onSeccess(RealResponse response){
        final T obj = onParseResponse(response);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                onResponse(obj);
            }
        });
    }


    /**
     * 解析response，执行在子线程
     */
    public abstract T onParseResponse(RealResponse response);

    /**
     * 访问网络失败后被调用，执行在UI线程
     */
    public abstract void onFailure(int code,String errorMessage);

    /**
     *
     * 访问网络成功后被调用，执行在UI线程
     */
    public abstract void onResponse(T response);



    public static abstract class CallBackDefault extends CallBackUtil<RealResponse> {
        @Override
        public RealResponse onParseResponse(RealResponse response) {
            return response;
        }
    }

    public static abstract class CallBackString extends CallBackUtil<String> {
        @Override
        public String onParseResponse(RealResponse response) {
            try {
                return getRetString(response.inputStream);
            } catch (Exception e) {
                throw new RuntimeException("failure");
            }
        }
    }


    public static abstract class CallBackBitmap extends CallBackUtil<Bitmap> {
        private int mTargetWidth;
        private int mTargetHeight;

        public CallBackBitmap(){};
        public CallBackBitmap(int targetWidth,int targetHeight){
            mTargetWidth = targetWidth;
            mTargetHeight = targetHeight;
        };
        public CallBackBitmap(ImageView imageView){
            int width = imageView.getWidth();
            int height = imageView.getHeight();
            if(width <=0 || height <=0){
                throw new RuntimeException("无法获取ImageView的width或height");
            }
            mTargetWidth = width;
            mTargetHeight = height;
        };
        @Override
        public Bitmap onParseResponse(RealResponse response) {
            if(mTargetWidth ==0 || mTargetHeight == 0){
                return BitmapFactory.decodeStream(response.inputStream);
            }else {
                return getZoomBitmap( response.inputStream);
            }
        }

        /**
         * 压缩图片，避免OOM异常
         */
        private Bitmap getZoomBitmap(InputStream inputStream) {
            byte[] data = null;
            try {
                data = input2byte(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeByteArray(data,0,data.length,options);
            int picWidth = options.outWidth;
            int picHeight = options.outHeight;
            int sampleSize = 1;
            int heightRatio = (int) Math.floor((float) picWidth / (float) mTargetWidth);
            int widthRatio = (int) Math.floor((float) picHeight / (float) mTargetHeight);
            if (heightRatio > 1 || widthRatio > 1){
                sampleSize = Math.max(heightRatio,widthRatio);
            }
            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length,options);

            if(bitmap == null){
                throw new RuntimeException("Failed to decode stream.");
            }
            return bitmap;
        }
    }

    public static final byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    /**
     * 下载文件时的回调类
     */
    public static abstract class CallBackFile extends CallBackUtil<File> {

        private final String mDestFileDir;
        private final String mdestFileName;

        /**
         *
         * @param destFileDir:文件目录
         * @param destFileName：文件名
         */
        public CallBackFile(String destFileDir, String destFileName){
            mDestFileDir = destFileDir;
            mdestFileName = destFileName;
        }
        @Override
        public File onParseResponse(RealResponse response) {

            InputStream is = null;
            byte[] buf = new byte[1024*8];
            int len = 0;
            FileOutputStream fos = null;
            try{
                is = response.inputStream;
                final long total = response.contentLength;

                long sum = 0;

                File dir = new File(mDestFileDir);
                if (!dir.exists()){
                    dir.mkdirs();
                }
                File file = new File(dir, mdestFileName);
                fos = new FileOutputStream(file);
                while ((len = is.read(buf)) != -1){
                    sum += len;
                    fos.write(buf, 0, len);
                    final long finalSum = sum;
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onProgress(finalSum * 100.0f / total,total);
                        }
                    });
                }
                fos.flush();

                return file;

            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                try{
                    fos.close();
                    if (is != null) is.close();
                } catch (IOException e){
                }
                try{
                    if (fos != null) fos.close();
                } catch (IOException e){
                }

            }
            return null;
        }
    }


    private static String getRetString(InputStream is) {
        String buf;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            buf = sb.toString();
            return buf;

        } catch (Exception e) {
            return null;
        }
    }

}
