package com.github.tvbox.osc.bbox.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.bbox.R;
import com.github.tvbox.osc.bbox.event.RefreshEvent;
import com.github.tvbox.osc.bbox.server.ControlManager;
import com.github.tvbox.osc.bbox.ui.adapter.ApiHistoryDialogAdapter;
import com.github.tvbox.osc.bbox.ui.tv.QRCodeGen;
import com.github.tvbox.osc.bbox.util.HawkConfig;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * 描述
 *
 * @author pj567
 * @since 2020/12/27
 */
public class ApiDialog extends BaseDialog {
    private ImageView ivQRCode;
    private TextView tvAddress;
    private EditText inputApi;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        if (event.type == RefreshEvent.TYPE_API_URL_CHANGE) {
            inputApi.setText((String) event.obj);
        }
    }

    public ApiDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_api);
        setCanceledOnTouchOutside(false);
        ivQRCode = findViewById(R.id.ivQRCode);
        tvAddress = findViewById(R.id.tvAddress);
        inputApi = findViewById(R.id.input);
        //内置网络接口在此处添加
        inputApi.setText(Hawk.get(HawkConfig.API_URL, ""));

        findViewById(R.id.inputSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newApi = inputApi.getText().toString().trim();
                if (!newApi.isEmpty()) {
                    // ArrayList<String> history = Hawk.get(HawkConfig.API_HISTORY, new ArrayList<String>());
                    // if (!history.contains(newApi))
                    //     history.add(0, newApi);
                    // if (history.size() > 30)
                    //     history.remove(30);
                    //
                    // listener.onchange(newApi);
                    //
                    // Hawk.put(HawkConfig.API_HISTORY, history);



                    ArrayList<String> nameHistory = Hawk.get(HawkConfig.API_NAME_HISTORY, new ArrayList<>());
                    HashMap<String, String> map = Hawk.get(HawkConfig.API_MAP, new HashMap<>());
                    if(!map.containsValue(newApi)){
                        Hawk.put(HawkConfig.API_URL, newApi);
                        Hawk.put(HawkConfig.API_NAME, newApi);
                        nameHistory.add(0,newApi);
                        map.put(newApi, newApi);

                        listener.onchange(newApi);
                    }
                    if(map.size()>30){
                        map.remove(nameHistory.get(30));
                        nameHistory.remove(30);
                    }
                    Hawk.put(HawkConfig.API_NAME_HISTORY, nameHistory);
                    Hawk.put(HawkConfig.API_MAP, map);
                    dismiss();
                }
            }
        });

//        findViewById(R.id.apiHistory).setOnClickListener(v -> {
//            ArrayList<String> history = Hawk.get(HawkConfig.API_NAME_HISTORY, new ArrayList<String>());
//            if (history.isEmpty())
//                return;
//            String name = Hawk.get(HawkConfig.API_NAME, "");
//
//            int idx = 0;
//            if (history.contains(name))
//                idx = history.indexOf(name);
//
//            ApiHistoryDialog dialog = new ApiHistoryDialog(getContext());
//            dialog.setTip("历史配置列表");
//            dialog.setAdapter(new ApiHistoryDialogAdapter.SelectDialogInterface() {
//                @Override
//                public void click(String value) {
//                    inputApi.setText(map.get(value));
//                    listener.onchange(value);
//                    dialog.dismiss();
//                }
//
//                @Override
//                public void del(String value, ArrayList<String> data) {
//                    Hawk.put(HawkConfig.API_NAME_HISTORY, data);
//                }
//            }, history, idx);
//            dialog.show();
//        });

        findViewById(R.id.storagePermission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (XXPermissions.isGranted(getContext(), Permission.Group.STORAGE)) {
                    Toast.makeText(getContext(), "已获得存储权限", Toast.LENGTH_SHORT).show();
                } else {
                    XXPermissions.with(getContext())
                            .permission(Permission.Group.STORAGE)
                            .request(new OnPermissionCallback() {
                                @Override
                                public void onGranted(List<String> permissions, boolean all) {
                                    if (all) {
                                        Toast.makeText(getContext(), "已获得存储权限", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onDenied(List<String> permissions, boolean never) {
                                    if (never) {
                                        Toast.makeText(getContext(), "获取存储权限失败,请在系统设置中开启", Toast.LENGTH_SHORT).show();
                                        XXPermissions.startPermissionActivity((Activity) getContext(), permissions);
                                    } else {
                                        Toast.makeText(getContext(), "获取存储权限失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
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
        void onchange(String api);
    }
}
