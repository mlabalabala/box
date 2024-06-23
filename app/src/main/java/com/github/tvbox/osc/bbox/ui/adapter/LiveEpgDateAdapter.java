package com.github.tvbox.osc.bbox.ui.adapter;

import android.graphics.Color;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.bbox.R;
import com.github.tvbox.osc.bbox.bean.LiveEpgDate;

import java.util.ArrayList;


public class LiveEpgDateAdapter extends BaseQuickAdapter<LiveEpgDate, BaseViewHolder> {

    private int selectedIndex = -1;
    private int focusedIndex = -1;

    public LiveEpgDateAdapter() {
        super(R.layout.item_live_channel_group, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder holder, LiveEpgDate item) {
        TextView tvGroupName = holder.getView(R.id.tvChannelGroupName);
        tvGroupName.setText(item.getDatePresented());
        tvGroupName.setBackgroundColor(Color.TRANSPARENT);
        if (item.getIndex() == selectedIndex) {
            tvGroupName.setTextColor(mContext.getResources().getColor(R.color.color_1890FF));
        }else {
            tvGroupName.setTextColor(mContext.getResources().getColor(R.color.color_CCFFFFFF));
        }
    }

    public void setSelectedIndex(int selectedIndex) {
        if (selectedIndex == this.selectedIndex) return;
        int preSelectedIndex = this.selectedIndex;
        this.selectedIndex = selectedIndex;
        if (preSelectedIndex != -1)
            notifyItemChanged(preSelectedIndex);
        if (this.selectedIndex != -1)
            notifyItemChanged(this.selectedIndex);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setFocusedIndex(int focusedIndex) {
        int preSelectedIndex = this.selectedIndex;
        this.focusedIndex = focusedIndex;
        if(preSelectedIndex != -1)
            notifyItemChanged(preSelectedIndex);
        if (this.focusedIndex != -1)
            notifyItemChanged(this.focusedIndex);
    }
}