package com.github.tvbox.osc.bbox.update.component;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.github.tvbox.osc.bbox.util.HawkConfig;
import com.orhanobut.hawk.Hawk;
import com.xuexiang.xupdate._XUpdate;
import com.xuexiang.xupdate.entity.UpdateEntity;
import com.xuexiang.xupdate.proxy.IUpdateChecker;
import com.xuexiang.xupdate.proxy.IUpdateProxy;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CustomUpdateChecker implements IUpdateChecker {
    private final Activity mActivity;

    public CustomUpdateChecker(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onBeforeCheck() {
        // CProgressDialogUtils.showProgressDialog(mActivity, "检查更新中...");
    }

    @Override
    public void checkVersion(boolean isGet, @NonNull @NotNull String url, @NonNull @NotNull Map<String, Object> params, @NonNull @NotNull IUpdateProxy updateProxy) {
        // 从缓存获取信息
        String result = Hawk.get(HawkConfig.VERSION_INFO_STR, "");
        if (!TextUtils.isEmpty(result)) {
            this.processCheckResult(result, updateProxy);
        } else {
            _XUpdate.onUpdateError(2005);
        }
    }

    @Override
    public void onAfterCheck() {
        // CProgressDialogUtils.cancelProgressDialog(mActivity);
    }

    @Override
    public void processCheckResult(@NonNull @NotNull String result, @NonNull @NotNull IUpdateProxy updateProxy) {
        try {
            if (updateProxy.isAsyncParser()) {
                updateProxy.parseJson(result, updateEntity -> {
                    try {
                        processUpdateEntity(updateEntity, result, updateProxy);
                    } catch (Exception var3) {
                        var3.printStackTrace();
                        _XUpdate.onUpdateError(2006, var3.getMessage());
                    }

                });
            } else {
                processUpdateEntity(updateProxy.parseJson(result), result, updateProxy);
            }
        } catch (Exception var4) {
            var4.printStackTrace();
            _XUpdate.onUpdateError(2006, var4.getMessage());
        }
    }

    private void processUpdateEntity(UpdateEntity updateEntity, @NonNull String result, @NonNull IUpdateProxy updateProxy) {
        if (updateEntity != null) {
            if (updateEntity.isHasUpdate()) {
                if (updateEntity.isIgnorable() && Hawk.get(HawkConfig.IS_IGNORE_VERSION, false)) {
                    _XUpdate.onUpdateError(2007);
                } else if (TextUtils.isEmpty(updateEntity.getApkCacheDir())) {
                    _XUpdate.onUpdateError(2008);
                } else {
                    updateProxy.findNewVersion(updateEntity, updateProxy);
                }
            } else {
                updateProxy.noNewVersion(null);
            }
        } else {
            _XUpdate.onUpdateError(2006, "json:" + result);
        }

    }

    @Override
    public void noNewVersion(Throwable throwable) {
        _XUpdate.onUpdateError(2004, throwable != null ? throwable.getMessage() : null);
        Toast.makeText(mActivity, "暂无更新信息...", Toast.LENGTH_SHORT).show();
    }
}
