package com.github.tvbox.osc.bbox.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.github.tvbox.osc.bbox.R;
import com.github.tvbox.osc.bbox.callback.EmptyCallback;
import com.github.tvbox.osc.bbox.callback.LoadingCallback;
import com.github.tvbox.osc.bbox.event.RefreshEvent;
import com.github.tvbox.osc.bbox.player.thirdparty.RemoteTVBox;
import com.github.tvbox.osc.bbox.ui.adapter.SelectDialogAdapter;
import com.github.tvbox.osc.bbox.ui.fragment.ModelSettingFragment;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;


public class SearchRemoteTvDialog extends BaseDialog{


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        if (event.type == RefreshEvent.TYPE_SETTING_SEARCH_TV) {
            showRemoteTvDialog(ModelSettingFragment.foundRemoteTv);
        }
    }

    public SearchRemoteTvDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_search_remotetv);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setTip(String tip) {
        ((TextView) findViewById(R.id.title)).setText(tip);
        setLoadSir(findViewById(R.id.list));
        showLoading();
    }

    private void showRemoteTvDialog(boolean found) {
        if (!found) {
            if (ModelSettingFragment.loadingSearchRemoteTvDialog != null) {
                ModelSettingFragment.loadingSearchRemoteTvDialog.showEmpty();
            }
            Toast.makeText(getContext(), "未找到附近TVBox", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ModelSettingFragment.loadingSearchRemoteTvDialog != null) {
            ModelSettingFragment.loadingSearchRemoteTvDialog.dismiss();
        }
        if (ModelSettingFragment.remoteTvHostList == null) {
            return;
        }
        RemoteTVBox.setAvalible(ModelSettingFragment.remoteTvHostList.get(0));
        SelectDialog<String> dialog = new SelectDialog<>(getContext());
        dialog.setTip("附近TVBox");
        int defaultPos = 0;
        dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<String>() {
            @Override
            public void click(String value, int pos) {
                RemoteTVBox.setAvalible(value);
                Toast.makeText(getContext(), "设置成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public String getDisplay(String val) {
                return val;
            }
        }, new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
                return oldItem.equals(newItem);
            }
        }, ModelSettingFragment.remoteTvHostList, defaultPos);
        dialog.show();
    }



    private LoadService mLoadService;

    protected void setLoadSir(View view) {
        if (mLoadService == null) {
            mLoadService = LoadSir.getDefault().register(view, new Callback.OnReloadListener() {
                @Override
                public void onReload(View v) {
                }
            });
        }
    }

    public void showLoading() {
        if (mLoadService != null) {
            mLoadService.showCallback(LoadingCallback.class);
        }
    }

    public void showEmpty() {
        if (null != mLoadService) {
            mLoadService.showCallback(EmptyCallback.class);
        }
    }

    public void showSuccess() {
        if (null != mLoadService) {
            mLoadService.showSuccess();
        }
    }

}
