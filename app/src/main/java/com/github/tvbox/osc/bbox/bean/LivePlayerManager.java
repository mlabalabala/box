package com.github.tvbox.osc.bbox.bean;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.bbox.util.HawkConfig;
import com.github.tvbox.osc.bbox.util.PlayerHelper;
import com.orhanobut.hawk.Hawk;

import org.json.JSONException;
import org.json.JSONObject;

import xyz.doikki.videoplayer.player.VideoView;

public class LivePlayerManager {
    JSONObject defaultPlayerConfig = new JSONObject();
    JSONObject currentPlayerConfig;
    private String currentApi="";


    public void init(VideoView videoView) {
        try {
            currentApi=Hawk.get(HawkConfig.LIVE_URL,"");
            defaultPlayerConfig.put("pl", Hawk.get(HawkConfig.LIVE_PLAY_TYPE, Hawk.get(HawkConfig.PLAY_TYPE, 0)));
            defaultPlayerConfig.put("ijk", Hawk.get(HawkConfig.IJK_CODEC, "硬解码"));
            defaultPlayerConfig.put("pr", Hawk.get(HawkConfig.PLAY_RENDER, 0));
            defaultPlayerConfig.put("sc", Hawk.get(HawkConfig.PLAY_SCALE, 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getDefaultLiveChannelPlayer(videoView);
    }

    public void getDefaultLiveChannelPlayer(VideoView videoView) {
        PlayerHelper.updateCfg(videoView, defaultPlayerConfig);
        try {
            currentPlayerConfig = new JSONObject(defaultPlayerConfig.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getLiveChannelPlayer(VideoView videoView, String channelName) {
        channelName=currentCfgKey(channelName);
        JSONObject playerConfig = Hawk.get(channelName, null);
        if (playerConfig == null) {
            if (!currentPlayerConfig.toString().equals(defaultPlayerConfig.toString()))
                getDefaultLiveChannelPlayer(videoView);
            return;
        }
        if (playerConfig.toString().equals(currentPlayerConfig.toString()))
            return;

        try {
            if (playerConfig.getInt("pl") == currentPlayerConfig.getInt("pl")
                    && playerConfig.getInt("pr") == currentPlayerConfig.getInt("pr")
                    && playerConfig.getString("ijk").equals(currentPlayerConfig.getString("ijk"))) {
                videoView.setScreenScaleType(Hawk.get(HawkConfig.IS_GLOBAL_SCALE, false)?Hawk.get(HawkConfig.PLAY_SCALE,0):playerConfig.getInt("sc"));
            } else {
                PlayerHelper.updateCfg(videoView, playerConfig);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        currentPlayerConfig = playerConfig;
    }

    public int getLivePlayerType() {
        int playerTypeIndex = 0;
        try {
            int playerType = currentPlayerConfig.getInt("pl");
            String ijkCodec = currentPlayerConfig.getString("ijk");
            switch (playerType) {
                case 0:
                    playerTypeIndex = 0;
                    break;
                case 1:
                    if (ijkCodec.equals("硬解码"))
                        playerTypeIndex = 1;
                    else
                        playerTypeIndex = 2;
                    break;
                case 2:
                    playerTypeIndex = 3;
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return playerTypeIndex;
    }

    public int getLivePlayerScale() {
        try {
            return Hawk.get(HawkConfig.IS_GLOBAL_SCALE, false)?Hawk.get(HawkConfig.PLAY_SCALE,0):currentPlayerConfig.getInt("sc");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void changeLivePlayerType(VideoView videoView, int playerType, String channelName) {
        channelName=currentCfgKey(channelName);
        JSONObject playerConfig = currentPlayerConfig;
        try {
            switch (playerType) {
                case 0:
                    playerConfig.put("pl", 0);
                    playerConfig.put("ijk", "软解码");
                    break;
                case 1:
                    playerConfig.put("pl", 1);
                    playerConfig.put("ijk", "硬解码");
                    break;
                case 2:
                    playerConfig.put("pl", 1);
                    playerConfig.put("ijk", "软解码");
                    break;
                case 3:
                    playerConfig.put("pl", 2);
                    playerConfig.put("ijk", "软解码");
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PlayerHelper.updateCfg(videoView, playerConfig);

        if (playerConfig.toString().equals(defaultPlayerConfig.toString()))
            Hawk.delete(channelName);
        else
            Hawk.put(channelName, playerConfig);

        currentPlayerConfig = playerConfig;
    }

    public void changeLivePlayerScale(@NonNull VideoView videoView, int playerScale, String channelName){
        channelName=currentCfgKey(channelName);
        videoView.setScreenScaleType(playerScale);

        JSONObject playerConfig = currentPlayerConfig;
        try {
            int scale = Hawk.get(HawkConfig.IS_GLOBAL_SCALE, false)?Hawk.get(HawkConfig.PLAY_SCALE,0):playerScale;
            playerConfig.put("sc", scale);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (playerConfig.toString().equals(defaultPlayerConfig.toString()))
            Hawk.delete(channelName);
        else
            Hawk.put(channelName, playerConfig);

        currentPlayerConfig = playerConfig;
    }

    private String currentCfgKey(String channelName)
    {
        return currentApi+"_"+channelName;
    }
}