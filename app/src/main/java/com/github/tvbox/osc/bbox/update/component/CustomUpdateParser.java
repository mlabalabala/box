package com.github.tvbox.osc.bbox.update.component;

import android.content.Context;
import android.os.Environment;
import androidx.annotation.NonNull;
import com.github.tvbox.osc.bbox.update.pojo.VersionInfoVo;
import com.github.tvbox.osc.bbox.util.JsonUtil;
import com.xuexiang.xupdate.entity.UpdateEntity;
import com.xuexiang.xupdate.listener.IUpdateParseCallback;
import com.xuexiang.xupdate.proxy.IUpdateParser;
import com.xuexiang.xupdate.utils.UpdateUtils;

public class CustomUpdateParser implements IUpdateParser {
    private final Context mContext;

    public CustomUpdateParser(Context context) {
        mContext = context;
    }

    @Override
    public UpdateEntity parseJson(String json) {
        return getParseResult(json);
    }

    private UpdateEntity getParseResult(String json) {
        VersionInfoVo versionInfo = JsonUtil.fromJson(json, VersionInfoVo.class);
        if (versionInfo != null) {
            return new UpdateEntity()
                .setApkCacheDir(String.valueOf(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)))
                .setHasUpdate(versionInfo.getVersionCode() > UpdateUtils.getVersionCode(mContext))
                .setDownloadUrl(versionInfo.getDownloadUrl())
                .setForce(versionInfo.isForceUpgrade())
                .setIsIgnorable(!versionInfo.isForceUpgrade())
                .setVersionCode(versionInfo.getVersionCode())
                .setVersionName(versionInfo.getVersionName())
                .setUpdateContent(versionInfo.getDesc());
        }
        return null;
    }

    @Override
    public void parseJson(String json, @NonNull IUpdateParseCallback callback) throws Exception {
        //当isAsyncParser为 true时调用该方法, 所以当isAsyncParser为false可以不实现
        callback.onParseResult(getParseResult(json));
    }


    @Override
    public boolean isAsyncParser() {
        return false;
    }
}
