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

package com.github.tvbox.osc.bbox.subtitle;

import androidx.annotation.Nullable;
import com.github.tvbox.osc.bbox.subtitle.model.Subtitle;
import xyz.doikki.videoplayer.player.AbstractPlayer;

import java.util.List;

/**
 * @author AveryZhong.
 */

public interface SubtitleEngine {

    /**
     * 设置字幕路径，加载字幕
     *
     * @param path 字幕路径（本地路径或者是远程路径）
     */
    void setSubtitlePath(String path);

    /**
     *  字幕延时
     * @param milliseconds
     */
    void setSubtitleDelay(Integer milliseconds);

    void setPlaySubtitleCacheKey(String cacheKey);

    String getPlaySubtitleCacheKey();

    /**
     * 开启字幕刷新任务
     */
    void start();

    /**
     * 暂停
     */
    void pause();

    /**
     * 恢复
     */
    void resume();

    /**
     * 停止字幕刷新任务
     */
    void stop();

    /**
     * 重置
     */
    void reset();

    /**
     * 销毁字幕
     */
    void destroy();

    /**
     * 绑定AbstractPlayer
     *
     * @param mediaPlayer mediaPlayer
     */
    void bindToMediaPlayer(AbstractPlayer mediaPlayer);

    /**
     * 设置字幕准备完成监接口
     *
     * @param listener OnSubtitlePreparedListener
     */
    void setOnSubtitlePreparedListener(OnSubtitlePreparedListener listener);

    /**
     * 设置字幕改变监听接口
     *
     * @param listener OnSubtitleChangeListener
     */
    void setOnSubtitleChangeListener(OnSubtitleChangeListener listener);

    /**
     * 幕准备完成监接口
     */
    interface OnSubtitlePreparedListener {
        void onSubtitlePrepared(@Nullable List<Subtitle> subtitles);
    }

    /**
     * 字幕改变监听接口
     */
    interface OnSubtitleChangeListener {
        void onSubtitleChanged(@Nullable Subtitle subtitle);
    }

}