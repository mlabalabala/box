package com.github.tvbox.osc.bbox.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.github.tvbox.osc.bbox.api.ApiConfig;
import com.github.tvbox.osc.bbox.base.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * 图片工具
 * @version 1.0.0 <br/>
 */
public class ImgUtil {
    private static final Map<String, Drawable> drawableCache = new HashMap<>();
    public static boolean isBase64Image(String picUrl) {
        return picUrl.startsWith("data:image");
    }
    public static int defaultWidth = 244;
    public static int defaultHeight = 320;

    /**
     * style 数据结构：ratio 指定宽高比（宽 / 高），type 表示风格（例如 rect、list）
     */
    public static class Style {
        public float ratio;
        public String type;

        public Style(float ratio, String type) {
            this.ratio = ratio;
            this.type = type;
        }
    }

    public static Style initStyle()
    {
        String bStyle = ApiConfig.get().getHomeSourceBean().getStyle();
        if(!bStyle.isEmpty()){
            try {
                JSONObject jsonObject = new JSONObject(bStyle);
                float ratio = (float) jsonObject.getDouble("ratio");
                String type = jsonObject.getString("type");
                return new Style(ratio, type);
            }catch (JSONException e){

            }
        }
        return null;
    }

    public static int spanCountByStyle(Style style,int defaultCount){
        int spanCount=defaultCount;
        if ("rect".equals(style.type)) {
            if (style.ratio >= 1.7) {
                spanCount = 3; // 横图
            } else if (style.ratio >= 1.3) {
                spanCount = 4; // 4:3
            }
        } else if ("list".equals(style.type)) {
            spanCount = 1;
        }
        return spanCount;
    }

    public static int getStyleDefaultWidth(Style style){
        int styleDefaultWidth = 280;
        if(style.ratio<1)styleDefaultWidth=214;
        if(style.ratio>1.7)styleDefaultWidth=380;
        return styleDefaultWidth;
    }

    public static Bitmap decodeBase64ToBitmap(String base64Str) {
        // 去掉 Base64 数据的头部前缀，例如 "data:image/png;base64,"
        String base64Data = base64Str.substring(base64Str.indexOf(",") + 1);
        byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static Drawable createTextDrawable(String text) {
        if(text.isEmpty())text="TVBox";
        text=text.substring(0, 1);
        // 如果缓存中已存在，直接返回
        if (drawableCache.containsKey(text)) {
            return drawableCache.get(text);
        }
        int width = 180, height = 240; // 设定图片大小
        int randomColor = getRandomColor();
        float cornerRadius = AutoSizeUtils.mm2px(App.getInstance(), 5); // 圆角半径

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // 画圆角背景
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(randomColor);
        paint.setStyle(Paint.Style.FILL);
        RectF rectF = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
        paint.setColor(Color.WHITE); // 文字颜色
        paint.setTextSize(50); // 文字大小
        paint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float x = width / 2f;
        float y = (height - fontMetrics.bottom - fontMetrics.top) / 2f;

        canvas.drawText(text, x, y, paint);
        Drawable drawable = new BitmapDrawable(bitmap);
        drawableCache.put(text, drawable);
        return drawable;

    }
    public static int getRandomColor() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static void clearCache() {
        drawableCache.clear();
    }
}
