package com.github.tvbox.osc.bbox.ui.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.bbox.R;
import com.github.tvbox.osc.bbox.bean.ParseBean;

import java.util.ArrayList;

public class ParseAdapter extends BaseQuickAdapter<ParseBean, BaseViewHolder> {
    public ParseAdapter() {
        super(R.layout.item_play_parse, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, ParseBean item) {
        TextView tvParse = helper.getView(R.id.tvParse);
        tvParse.setVisibility(View.VISIBLE);
        if (item.isDefault()) {
            tvParse.setTextColor(mContext.getResources().getColor(R.color.color_02F8E1));
        } else {
            tvParse.setTextColor(Color.WHITE);
        }
        tvParse.setText(item.getName());
        // if (helper.getLayoutPosition() == 0) {
        //     helper.itemView.setNextFocusLeftId(R.id.screen_display);
        // }
    }
}