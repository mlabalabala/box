package com.github.tvbox.osc.bbox.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.bbox.R;
import com.github.tvbox.osc.bbox.bean.VodInfo;

import java.util.ArrayList;

/**
 * @author pj567
 * @date :2020/12/22
 * @description:
 */
public class SeriesFlagAdapter extends BaseQuickAdapter<VodInfo.VodSeriesFlag, BaseViewHolder> {
    public SeriesFlagAdapter() {
        super(R.layout.item_series_flag, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, VodInfo.VodSeriesFlag item) {
        TextView tvSeries = helper.getView(R.id.tvSeriesFlag);
        View select = helper.getView(R.id.tvSeriesFlagSelect);
        if (item.selected) {
            select.setVisibility(View.VISIBLE);
        } else {
            select.setVisibility(View.GONE);
        }
        helper.setText(R.id.tvSeriesFlag, item.name);
        View mSeriesGroupTv = ((Activity) helper.itemView.getContext()).findViewById(R.id.mSeriesGroupTv);
        if (mSeriesGroupTv != null && mSeriesGroupTv.getVisibility() == View.VISIBLE) {
            helper.itemView.setNextFocusDownId(R.id.mSeriesSortTv);
        }else {
            helper.itemView.setNextFocusDownId(R.id.mGridView);
        }
        if (helper.getLayoutPosition() == getData().size() - 1) {
            helper.itemView.setId(ViewCompat.generateViewId());
            helper.itemView.setNextFocusRightId(helper.itemView.getId());
        }else {
            helper.itemView.setNextFocusRightId(View.NO_ID);
        }
    }
}