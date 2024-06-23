package com.github.tvbox.osc.bbox.ui.adapter;

import android.graphics.Color;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.bbox.R;
import com.github.tvbox.osc.bbox.bean.LiveChannelItem;

import java.util.ArrayList;

/**
 * @author pj567
 * @date :2021/1/12
 * @description:
 */
public class LiveChannelItemAdapter extends BaseQuickAdapter<LiveChannelItem, BaseViewHolder> {
    private int selectedChannelIndex = -1;
    private int focusedChannelIndex = -1;

    public LiveChannelItemAdapter() {
        super(R.layout.item_live_channel, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder holder, LiveChannelItem item) {
        TextView tvChannelNum = holder.getView(R.id.tvChannelNum);
        TextView tvChannel = holder.getView(R.id.tvChannelName);
        TextView tvChannelEpgInfo = holder.getView(R.id.tvChannelEpgInfo);
        tvChannelNum.setText(String.format("%s", item.getChannelNum()));
        tvChannel.setText(item.getChannelName());
        tvChannelEpgInfo.setText(item.getChannelEpgInfo());
        int channelIndex = item.getChannelIndex();
        if (channelIndex == selectedChannelIndex) {
            tvChannelNum.setTextColor(ContextCompat.getColor(mContext, R.color.color_1890FF));
            tvChannel.setTextColor(ContextCompat.getColor(mContext, R.color.color_1890FF));
            tvChannelEpgInfo.setTextColor(ContextCompat.getColor(mContext, R.color.color_BD0CADE2));
        } else if (channelIndex == focusedChannelIndex) {
            tvChannelNum.setTextColor(ContextCompat.getColor(mContext, R.color.color_00FF0A));
            tvChannel.setTextColor(ContextCompat.getColor(mContext, R.color.color_00FF0A));
        } else{
            tvChannelNum.setTextColor(Color.WHITE);
            tvChannel.setTextColor(Color.WHITE);
            tvChannelEpgInfo.setTextColor(ContextCompat.getColor(mContext, R.color.color_FFFFFF_50));
        }
    }

    public void setSelectedChannelIndex(int selectedChannelIndex) {
        if (selectedChannelIndex == this.selectedChannelIndex) return;
        int preSelectedChannelIndex = this.selectedChannelIndex;
        this.selectedChannelIndex = selectedChannelIndex;
        if (preSelectedChannelIndex != -1)
            notifyItemChanged(preSelectedChannelIndex);
        if (this.selectedChannelIndex != -1)
            notifyItemChanged(this.selectedChannelIndex);
    }

    public void setFocusedChannelIndex(int focusedChannelIndex) {
        int preFocusedChannelIndex = this.focusedChannelIndex;
        this.focusedChannelIndex = focusedChannelIndex;
        if (preFocusedChannelIndex != -1)
            notifyItemChanged(preFocusedChannelIndex);
        if (this.focusedChannelIndex != -1)
            notifyItemChanged(this.focusedChannelIndex);
        else if (this.selectedChannelIndex != -1)
            notifyItemChanged(this.selectedChannelIndex);
    }
}