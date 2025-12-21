package com.github.tvbox.osc.bbox.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.tvbox.osc.bbox.R;
import com.github.tvbox.osc.bbox.api.ApiConfig;
import com.github.tvbox.osc.bbox.base.BaseActivity;
import com.github.tvbox.osc.bbox.bean.SourceBean;
import com.github.tvbox.osc.bbox.bean.VodInfo;
import com.github.tvbox.osc.bbox.cache.RoomDataManger;
import com.github.tvbox.osc.bbox.event.RefreshEvent;
import com.github.tvbox.osc.bbox.ui.adapter.HistoryAdapter;
import com.github.tvbox.osc.bbox.ui.dialog.ConfirmClearDialog;
import com.github.tvbox.osc.bbox.util.FastClickCheckUtil;
import com.github.tvbox.osc.bbox.util.HawkConfig;
import com.github.tvbox.osc.bbox.util.LOG;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pj567
 * @date :2021/1/7
 * @description:
 */
public class HistoryActivity extends BaseActivity {
    private ImageView tvDel;
    private ImageView tvClear;
    private TextView tvDelTip;
    private TvRecyclerView mGridView;
    public static HistoryAdapter historyAdapter;
    private boolean delMode = false;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_history;
    }

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void toggleDelMode() {
        HawkConfig.hotVodDelete = !HawkConfig.hotVodDelete;
        historyAdapter.notifyDataSetChanged();
        delMode = !delMode;
        tvDelTip.setVisibility(delMode ? View.VISIBLE : View.GONE);
        tvDel.setImageResource(delMode ? R.drawable.icon_delete_select : R.drawable.icon_delete);
    }

    private void initView() {
        EventBus.getDefault().register(this);
        tvDel = findViewById(R.id.tvDel);
        tvClear = findViewById(R.id.tvClear);
        tvDelTip = findViewById(R.id.tvDelTip);
        mGridView = findViewById(R.id.mGridView);
        mGridView.setHasFixedSize(true);
        mGridView.setLayoutManager(new V7GridLayoutManager(this.mContext, isBaseOnWidth() ? 5 : 6));
        historyAdapter = new HistoryAdapter();
        mGridView.setAdapter(historyAdapter);
        mGridView.post(()-> {
            mGridView.scrollToPosition(0);
            RecyclerView.ViewHolder viewHolder = mGridView.findViewHolderForAdapterPosition(0);
            if (viewHolder != null) {
                viewHolder.itemView.requestFocus();
            }
        });
        tvDel.setOnClickListener(v -> toggleDelMode());
        tvClear.setOnClickListener(v -> {
            ConfirmClearDialog dialog = new ConfirmClearDialog(mContext, "History");
            dialog.show();
        });
        mGridView.setOnInBorderKeyEventListener((direction, focused) -> {
            if (direction == View.FOCUS_UP) {
                tvDel.setFocusable(true);
                tvClear.setFocusable(true);
                tvDel.requestFocus();
            }
            return false;
        });
        mGridView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                itemView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                itemView.animate().scaleX(1.05f).scaleY(1.05f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {

            }
        });
        historyAdapter.setOnItemClickListener((adapter, view, position) -> {
            FastClickCheckUtil.check(view);
            if (position == -1) return;
            VodInfo vodInfo = historyAdapter.getData().get(position);

            if (vodInfo != null) {
                if (delMode) {
                    historyAdapter.remove(position);
                    RoomDataManger.deleteVodRecord(vodInfo.sourceKey, vodInfo);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", vodInfo.id);
                    bundle.putString("sourceKey", vodInfo.sourceKey);
                    SourceBean sourceBean = ApiConfig.get().getSource(vodInfo.sourceKey);
                    if(sourceBean!=null){
                        bundle.putString("picture", vodInfo.pic);
                        jumpActivity(DetailActivity.class, bundle);
                    }else {
                        bundle.putString("title", vodInfo.name);
                        if(Hawk.get(HawkConfig.FAST_SEARCH_MODE, false)){
                            jumpActivity(FastSearchActivity.class, bundle);
                        }else {
                            jumpActivity(SearchActivity.class, bundle);
                        }
                    }
                }
            }
        });
        historyAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                tvDel.setFocusable(true);
                toggleDelMode();
                return true;
            }
        });
    }

    private void initData() {
        List<VodInfo> allVodRecord = RoomDataManger.getAllVodRecord(100);
        List<VodInfo> vodInfoList = new ArrayList<>();
        for (VodInfo vodInfo : allVodRecord) {
            if (vodInfo.playNote != null && !vodInfo.playNote.isEmpty())vodInfo.note = "上次看到" + vodInfo.playNote;
            vodInfoList.add(vodInfo);

            // 不保留当前线路源不存在的记录
            // SourceBean sourceBean = ApiConfig.get().getSource(vodInfo.sourceKey);
            // if (sourceBean != null) {
            //     if (vodInfo.playNote != null && !vodInfo.playNote.isEmpty())vodInfo.note = "上次看到" + vodInfo.playNote;
            //     vodInfoList.add(vodInfo);
            // }
        }
        historyAdapter.setNewData(vodInfoList);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        if (event.type == RefreshEvent.TYPE_HISTORY_REFRESH) {
            initData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (delMode) {
            toggleDelMode();
            return;
        }
        super.onBackPressed();
    }
}