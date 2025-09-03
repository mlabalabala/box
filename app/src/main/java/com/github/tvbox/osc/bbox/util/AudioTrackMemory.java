package com.github.tvbox.osc.bbox.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

/**
 * 音轨记忆
 */
public class AudioTrackMemory {
    private static AudioTrackMemory instance;
    private final SharedPreferences prefs;
    private static final String PREFS_NAME = "audio_track_prefs";
    private static final String KEY_GROUP_SUFFIX = "_group";
    private static final String KEY_TRACK_SUFFIX = "_track";

    private AudioTrackMemory(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    public static synchronized AudioTrackMemory getInstance(Context context) {
        if (instance == null) {
            instance = new AudioTrackMemory(context);
        }
        return instance;
    }
    public void save(String playKey, int groupIndex, int trackIndex) {
        LOG.i("echo-AudioTrackMemory save playKey:"+playKey);
        playKey=playKey + "_exo";
        prefs.edit().putInt(playKey + KEY_GROUP_SUFFIX, groupIndex).putInt(playKey + KEY_TRACK_SUFFIX, trackIndex).apply();
    }
    public void save(String playKey, int trackIndex) {
        LOG.i("echo-AudioTrackMemory save playKey:"+playKey);
        prefs.edit().putInt(playKey +"_ijk" + KEY_TRACK_SUFFIX, trackIndex).apply();
    }
    public Pair<Integer,Integer> exoLoad(String playKey) {
        playKey=playKey + "_exo";
        Pair<Integer,Integer> p;
        int group = prefs.getInt(playKey + KEY_GROUP_SUFFIX, -1);
        int track = prefs.getInt(playKey + KEY_TRACK_SUFFIX, -1);
        if (group >= 0 && track >= 0) {
            p = Pair.create(group, track);
            return p;
        }
        return null;
    }
    public Integer ijkLoad(String playKey) {
        playKey=playKey + "_ijk";
        return prefs.getInt(playKey + KEY_TRACK_SUFFIX, -1);
    }
}