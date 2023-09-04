package com.github.tvbox.osc.bbox.player;

import java.util.ArrayList;
import java.util.List;

public class TrackInfo {
    private List<TrackInfoBean> audio;
    private List<TrackInfoBean> subtitle;

    public TrackInfo() {
        audio = new ArrayList<>();
        subtitle = new ArrayList<>();
    }

    public List<TrackInfoBean> getAudio() {
        return audio;
    }

    public int getAudioSelected(boolean track) {
        return getSelected(audio, track);
    }

    public int getSubtitleSelected(boolean track) {
        return getSelected(subtitle, track);
    }

    public int getSelected(List<TrackInfoBean> list, boolean track) {
        int i = 0;
        for (TrackInfoBean trackInfoBean : list) {
            if (trackInfoBean.selected) return track ? trackInfoBean.index : i;
            i++;
        }
        return 99999;
    }

    public void addAudio(TrackInfoBean audio) {
        this.audio.add(audio);
    }

    public List<TrackInfoBean> getSubtitle() {
        return subtitle;
    }

    public void addSubtitle(TrackInfoBean subtitle) {
        this.subtitle.add(subtitle);
    }
}
