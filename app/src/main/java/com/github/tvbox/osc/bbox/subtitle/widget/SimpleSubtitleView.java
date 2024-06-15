/*
 *                       Copyright (C) of Avery
 *
 *                              _ooOoo_
 *                             o8888888o
 *                             88" . "88
 *                             (| -_- |)
 *                             O\  =  /O
 *                          ____/`- -'\____
 *                        .'  \\|     |//  `.
 *                       /  \\|||  :  |||//  \
 *                      /  _||||| -:- |||||-  \
 *                      |   | \\\  -  /// |   |
 *                      | \_|  ''\- -/''  |   |
 *                      \  .-\__  `-`  ___/-. /
 *                    ___`. .' /- -.- -\  `. . __
 *                 ."" '<  `.___\_<|>_/___.'  >'"".
 *                | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *                \  \ `-.   \_ __\ /__ _/   .-` /  /
 *           ======`-.____`-.___\_____/___.-`____.-'======
 *                              `=- -='
 *           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *              Buddha bless, there will never be bug!!!
 */

package com.github.tvbox.osc.bbox.subtitle.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.github.tvbox.osc.bbox.cache.CacheManager;
import com.github.tvbox.osc.bbox.subtitle.DefaultSubtitleEngine;
import com.github.tvbox.osc.bbox.subtitle.SubtitleEngine;
import com.github.tvbox.osc.bbox.subtitle.model.Subtitle;
import com.github.tvbox.osc.bbox.util.MD5;
import xyz.doikki.videoplayer.player.AbstractPlayer;

import java.util.List;

/**
 * @author AveryZhong.
 */

@SuppressLint("AppCompatCustomView")
public class SimpleSubtitleView extends TextView
        implements SubtitleEngine, SubtitleEngine.OnSubtitleChangeListener,
        SubtitleEngine.OnSubtitlePreparedListener {

    private static final String EMPTY_TEXT = "";

    private SubtitleEngine mSubtitleEngine;

    public boolean isInternal = false;

    public boolean hasInternal = false;

    private TextView backGroundText = null;//用于描边的TextView

    public SimpleSubtitleView(final Context context) {
        super(context);
        backGroundText = new TextView(context);
        init();
    }

    public SimpleSubtitleView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        backGroundText = new TextView(context, attrs);
        init();
    }

    public SimpleSubtitleView(final Context context, final AttributeSet attrs,
                              final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        backGroundText = new TextView(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSubtitleEngine = new DefaultSubtitleEngine();
        mSubtitleEngine.setOnSubtitlePreparedListener(this);
        mSubtitleEngine.setOnSubtitleChangeListener(this);
    }

    @Override
    public void onSubtitlePrepared(@Nullable final List<Subtitle> subtitles) {
        start();
    }

    @Override
    public void onSubtitleChanged(@Nullable final Subtitle subtitle) {
        if (subtitle == null) {
            setText(EMPTY_TEXT);
            return;
        }
        String text = subtitle.content;
        text = text.replaceAll("(?:\\r\\n)", "<br />");
        text = text.replaceAll("(?:\\r)", "<br />");
        text = text.replaceAll("(?:\\n)", "<br />");
        text = text.replaceAll("\\\\N", "<br />");
        text = text.replaceAll("\\{[\\s\\S]*?\\}", "");
        text = text.replaceAll("^.*?,.*?,.*?,.*?,.*?,.*?,.*?,.*?,.*?,", "");
        setText(Html.fromHtml(text));
    }

    @Override
    public void setSubtitlePath(final String path) {
        isInternal = false;
        mSubtitleEngine.setSubtitlePath(path);
    }

    @Override
    public void setSubtitleDelay(Integer mseconds) {
        mSubtitleEngine.setSubtitleDelay(mseconds);
    }

    public void setPlaySubtitleCacheKey(String cacheKey) {
        mSubtitleEngine.setPlaySubtitleCacheKey(cacheKey);
    }

    public String getPlaySubtitleCacheKey() {
        return mSubtitleEngine.getPlaySubtitleCacheKey();
    }

    public void clearSubtitleCache() {
        String subtitleCacheKey = getPlaySubtitleCacheKey();
        if (subtitleCacheKey != null && subtitleCacheKey.length() > 0) {
            CacheManager.delete(MD5.string2MD5(subtitleCacheKey), "");
        }
    }

    @Override
    public void reset() {
        mSubtitleEngine.reset();
    }

    @Override
    public void start() {
        mSubtitleEngine.start();
    }

    @Override
    public void pause() {
        mSubtitleEngine.pause();
    }

    @Override
    public void resume() {
        mSubtitleEngine.resume();
    }

    @Override
    public void stop() {
        mSubtitleEngine.stop();
    }

    @Override
    public void destroy() {
        mSubtitleEngine.destroy();
    }

    @Override
    public void bindToMediaPlayer(AbstractPlayer mediaPlayer) {
        mSubtitleEngine.bindToMediaPlayer(mediaPlayer);
    }

    @Override
    public void setOnSubtitlePreparedListener(final OnSubtitlePreparedListener listener) {
        mSubtitleEngine.setOnSubtitlePreparedListener(listener);
    }

    @Override
    public void setOnSubtitleChangeListener(final OnSubtitleChangeListener listener) {
        mSubtitleEngine.setOnSubtitleChangeListener(listener);
    }

    @Override
    protected void onDetachedFromWindow() {
        destroy();
        super.onDetachedFromWindow();
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        //同步布局参数
        backGroundText.setLayoutParams(params);
        super.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence tt = backGroundText.getText();
        //两个TextView上的文字必须一致
        if (TextUtils.isEmpty(tt) || !tt.equals(this.getText())) {
            backGroundText.setText(getText());
            this.postInvalidate();
        }
        backGroundText.measure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        backGroundText.setTextSize(size);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (backGroundText != null) {
            backGroundText.setText(text);
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        backGroundText.layout(left, top, right, bottom);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //其他地方，backGroundText和super的先后顺序影响不会很大，但是此处必须要先绘制backGroundText，
        drawBackGroundText();
        backGroundText.draw(canvas);
        super.onDraw(canvas);
    }

    private void drawBackGroundText() {
        TextPaint tp = backGroundText.getPaint();
        //设置描边宽度
        tp.setStrokeWidth(3);
        //背景描边并填充全部
        tp.setStyle(Paint.Style.FILL_AND_STROKE);
        //设置描边颜色
        backGroundText.setTextColor(Color.BLACK);
        //将背景的文字对齐方式做同步
        backGroundText.setGravity(getGravity());
    }
}
