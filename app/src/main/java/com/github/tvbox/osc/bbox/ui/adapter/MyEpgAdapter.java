package com.github.tvbox.osc.bbox.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.tvbox.osc.bbox.R;
import com.github.tvbox.osc.bbox.bean.Epginfo;
import com.github.tvbox.osc.bbox.ui.tv.widget.AudioWaveView;

import java.util.List;

public class MyEpgAdapter extends BaseAdapter {

    private List<Epginfo> data;
    private Context context;
    public static float fontSize=20;
    private int defaultSelection = 0;
    private int defaultShiyiSelection = 0;

    public MyEpgAdapter(List<Epginfo> data, Context context, int i ) {
        this.data = data;
        this.context = context;
        this.defaultSelection = i;
    }


    public void setSelection(int i) {
        this.defaultSelection = i;
        notifyDataSetChanged();
    }

    public void setShiyiSelection(int i) {
        this.defaultShiyiSelection = i;
        notifyDataSetChanged();
    }

    public void setFontSize(float f) {
        fontSize = f;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view ==null){
            view =  LayoutInflater.from(context).inflate(R.layout.epglist_item,viewGroup,false);
        }
        TextView textview = (TextView)view.findViewById(R.id.tv_epg_name);
        TextView timeview = (TextView)view.findViewById(R.id.tv_epg_time);
        AudioWaveView wqddg_AudioWaveView = (AudioWaveView)view.findViewById(R.id.wqddg_AudioWaveView);
        wqddg_AudioWaveView.setVisibility(View.GONE);
        if(i < data.size()){

            textview.setText(data.get(i).title);
            timeview.setText(data.get(i).start + "--" + data.get(i).end);
            textview.setTextColor(Color.WHITE) ;
            timeview.setTextColor(Color.WHITE) ;
            Log.e("roinlong", "getView: "+  i);
               if(i == this.defaultSelection){
                    wqddg_AudioWaveView.setVisibility(View.VISIBLE);
                    textview.setTextColor(Color.rgb(0, 153, 255)) ;
                    timeview.setTextColor(Color.rgb(0, 153, 255)) ;
                    textview.setFreezesText(true);
                    timeview.setFreezesText(true);
                }else {
                    wqddg_AudioWaveView.setVisibility(View.GONE);
                }

        }
        return view;
    }
}


