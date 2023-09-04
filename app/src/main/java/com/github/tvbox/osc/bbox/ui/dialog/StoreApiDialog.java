package com.github.tvbox.osc.bbox.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.bbox.R;
import com.github.tvbox.osc.bbox.api.StoreApiConfig;
import com.github.tvbox.osc.bbox.event.RefreshEvent;
import com.github.tvbox.osc.bbox.server.ControlManager;
import com.github.tvbox.osc.bbox.ui.adapter.ApiHistoryDialogAdapter;
import com.github.tvbox.osc.bbox.ui.tv.QRCodeGen;
import com.github.tvbox.osc.bbox.util.HawkConfig;
import com.github.tvbox.osc.bbox.util.LOG;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * 描述
 *
 * @author pj567
 * @since 2020/12/27
 */
public class StoreApiDialog extends BaseDialog {
    private final ImageView ivQRCode;
    private final TextView tvAddress;
    // private EditText inputStoreApiName;
    private EditText inputStoreApiUrl;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        if (event.type == RefreshEvent.TYPE_STORE_CONFIG_CHANGE) {
            inputStoreApiUrl.setText((String) event.obj);
        }
    }

    public StoreApiDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_store_api);
        setCanceledOnTouchOutside(false);
        ivQRCode = findViewById(R.id.ivQRCode);
        tvAddress = findViewById(R.id.tvAddress);
        // inputStoreApiName = findViewById(R.id.inputStoreApiName);
        inputStoreApiUrl = findViewById(R.id.inputStoreApiUrl);

        String storeApiName = Hawk.get(HawkConfig.STORE_API_NAME, "");
        HashMap<String, String> map = Hawk.get(HawkConfig.STORE_API_MAP, new HashMap<>());
        if (map.containsKey(storeApiName)){
            // inputStoreApiName.setText(storeApiName);
            inputStoreApiUrl.setText(map.get(storeApiName));
        }

        findViewById(R.id.inputSubmit).setOnClickListener(v -> {
            // String name = inputStoreApiName.getText().toString().trim();
            String url = inputStoreApiUrl.getText().toString().trim();

            if (!url.isEmpty()) {
                if (!map.containsValue(url))
                    map.put(url, url);

                listener.onchange(url);

                Hawk.put(HawkConfig.STORE_API_MAP, map);
                Hawk.put(HawkConfig.STORE_API_NAME, url);
            }
            try {
                StoreApiConfig.get().Subscribe(this.getContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            dismiss();
        });

        refreshQRCode();
    }

    private void refreshQRCode() {
        String address = ControlManager.get().getAddress(false);
        tvAddress.setText(String.format("手机/电脑扫描上方二维码或者直接浏览器访问地址\n%s", address));
        ivQRCode.setImageBitmap(QRCodeGen.generateBitmap(address, AutoSizeUtils.mm2px(getContext(), 300), AutoSizeUtils.mm2px(getContext(), 300)));
    }

    public void setOnListener(OnListener listener) {
        this.listener = listener;
    }

    OnListener listener = null;

    public interface OnListener {
        void onchange(String data);
    }
}
