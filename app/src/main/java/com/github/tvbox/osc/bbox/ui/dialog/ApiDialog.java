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
import com.github.tvbox.osc.bbox.constant.URL;
import com.github.tvbox.osc.bbox.event.RefreshEvent;
import com.github.tvbox.osc.bbox.server.ControlManager;
import com.github.tvbox.osc.bbox.ui.activity.HomeActivity;
import com.github.tvbox.osc.bbox.ui.adapter.ApiHistoryDialogAdapter;
import com.github.tvbox.osc.bbox.ui.tv.QRCodeGen;
import com.github.tvbox.osc.bbox.util.HawkConfig;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.orhanobut.hawk.Hawk;
import me.jessyan.autosize.utils.AutoSizeUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private EditText liveApi;
    private EditText epgApi;
    private EditText proxyUrl;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        if (event.type == RefreshEvent.TYPE_API_URL_CHANGE) {
            inputApi.setText((String) event.obj);
        }
        else if (event.type == RefreshEvent.TYPE_API_LIVE_URL){
            liveApi.setText((String) event.obj);
        }
        else if (event.type == RefreshEvent.TYPE_API_EPG_URL){
            epgApi.setText((String) event.obj);
        }
        else if (event.type == RefreshEvent.TYPE_PROXY_URL){
            proxyUrl.setText((String) event.obj);
        }
    }

    public ApiDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_api);
        setCanceledOnTouchOutside(false);
        ivQRCode = findViewById(R.id.ivQRCode);
        tvAddress = findViewById(R.id.tvAddress);
        inputApi = findViewById(R.id.input);
        liveApi = findViewById(R.id.liveUlrInput);
        epgApi = findViewById(R.id.epgInput);
        proxyUrl = findViewById(R.id.proxyInput);
        //内置网络接口在此处添加
        inputApi.setText(Hawk.get(HawkConfig.API_URL, ""));
        liveApi.setText(Hawk.get(HawkConfig.LIVE_URL, ""));
        epgApi.setText(Hawk.get(HawkConfig.EPG_URL, ""));
        proxyUrl.setText(Hawk.get(HawkConfig.PROXY_URL, ""));

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


                String newLive = liveApi.getText().toString().trim();
                // Capture Live input into Settings & Live History (max 20)
                Hawk.put(HawkConfig.LIVE_URL, newLive);
                if (!newLive.isEmpty()) {
                    ArrayList<String> liveHistory = Hawk.get(HawkConfig.LIVE_HISTORY, new ArrayList<String>());
                    if (!liveHistory.contains(newLive))
                        liveHistory.add(0, newLive);
                    if (liveHistory.size() > 20)
                        liveHistory.remove(20);
                    Hawk.put(HawkConfig.LIVE_HISTORY, liveHistory);
                    dismiss();
                }

                String newEPG = epgApi.getText().toString().trim();
                // Capture EPG input into Settings
                Hawk.put(HawkConfig.EPG_URL, newEPG);
                if (!newEPG.isEmpty()) {
                    ArrayList<String> EPGHistory = Hawk.get(HawkConfig.EPG_HISTORY, new ArrayList<String>());
                    if (!EPGHistory.contains(newEPG))
                        EPGHistory.add(0, newEPG);
                    if (EPGHistory.size() > 20)
                        EPGHistory.remove(20);
                    Hawk.put(HawkConfig.EPG_HISTORY, EPGHistory);
                    dismiss();
                }

                String newProxyUrl = proxyUrl.getText().toString().trim();
                // Capture proxy input into Settings
                Hawk.put(HawkConfig.PROXY_URL, newProxyUrl);
                if (!newProxyUrl.isEmpty()) {
                    putDefaultApis(newProxyUrl);
                    ArrayList<String> proxyHistory = Hawk.get(HawkConfig.PROXY_URL_HISTORY, new ArrayList<String>());
                    if (!proxyHistory.contains(newProxyUrl))
                        proxyHistory.add(0, newProxyUrl);
                    if (proxyHistory.size() > 20)
                        proxyHistory.remove(20);
                    Hawk.put(HawkConfig.PROXY_URL_HISTORY, proxyHistory);
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
        findViewById(R.id.apiHistory).setOnClickListener( v -> {
            // ArrayList<String> apiHistory = Hawk.get(HawkConfig.API_HISTORY, new ArrayList<>());
            // ArrayList<String> nameHistory = Hawk.get(HawkConfig.API_NAME_HISTORY, new ArrayList<>());
            ArrayList<String> history = Hawk.get(HawkConfig.API_NAME_HISTORY, new ArrayList<>());
            HashMap<String, String> map = Hawk.get(HawkConfig.API_MAP, new HashMap<>());

            // apiHistory.addAll(nameHistory);
            //
            //
            // Set<String> set = new HashSet<>();
            // List<String> history = new ArrayList<>();
            //
            // for (String cd : apiHistory) {
            //     if (set.add(cd)) {
            //         history.add(cd);
            //     }
            // }

            if (history.isEmpty())
                return;
            String current = Hawk.get(HawkConfig.API_NAME, "");
            int idx = 0;
            if (history.contains(current))
                idx = history.indexOf(current);
            ApiHistoryDialog dialog = new ApiHistoryDialog(getContext());
            dialog.setTip(HomeActivity.getRes().getString(R.string.dia_history_list));
            dialog.setAdapter(new ApiHistoryDialogAdapter.SelectDialogInterface() {
                @Override
                public void click(String value) {
                    Hawk.put(HawkConfig.API_NAME, value);
                    if (map.containsKey(value))
                        Hawk.put(HawkConfig.API_URL, map.get(value));
                    else
                        Hawk.put(HawkConfig.API_URL, value);

                    inputApi.setText(Hawk.get(HawkConfig.API_URL, ""));
                    listener.onchange(value);

                    dialog.dismiss();
                }


                @Override
                public void del(String value, ArrayList<String> data) {
                    Hawk.put(HawkConfig.API_NAME_HISTORY, data);
                }
            }, history, idx);
            dialog.show();
        });
        findViewById(R.id.liveHistory).setOnClickListener(v -> {
            ArrayList<String> liveHistory = Hawk.get(HawkConfig.LIVE_HISTORY, new ArrayList<String>());
            if (liveHistory.isEmpty())
                return;
            String current = Hawk.get(HawkConfig.LIVE_URL, "");
            int idx = 0;
            if (liveHistory.contains(current))
                idx = liveHistory.indexOf(current);
            ApiHistoryDialog dialog = new ApiHistoryDialog(getContext());
            dialog.setTip(HomeActivity.getRes().getString(R.string.dia_history_live));
            dialog.setAdapter(new ApiHistoryDialogAdapter.SelectDialogInterface() {
                @Override
                public void click(String liveURL) {
                    liveApi.setText(liveURL);
                    Hawk.put(HawkConfig.LIVE_URL, liveURL);
                    dialog.dismiss();
                }

                @Override
                public void del(String value, ArrayList<String> data) {
                    Hawk.put(HawkConfig.LIVE_HISTORY, data);
                }
            }, liveHistory, idx);
            dialog.show();
        });
        findViewById(R.id.EPGHistory).setOnClickListener(v -> {
            ArrayList<String> EPGHistory = Hawk.get(HawkConfig.EPG_HISTORY, new ArrayList<String>());
            if (EPGHistory.isEmpty())
                return;
            String current = Hawk.get(HawkConfig.EPG_URL, "");
            int idx = 0;
            if (EPGHistory.contains(current))
                idx = EPGHistory.indexOf(current);
            ApiHistoryDialog dialog = new ApiHistoryDialog(getContext());
            dialog.setTip(HomeActivity.getRes().getString(R.string.dia_history_epg));
            dialog.setAdapter(new ApiHistoryDialogAdapter.SelectDialogInterface() {
                @Override
                public void click(String epgURL) {
                    epgApi.setText(epgURL);
                    Hawk.put(HawkConfig.EPG_URL, epgURL);
                    dialog.dismiss();
                }

                @Override
                public void del(String value, ArrayList<String> data) {
                    Hawk.put(HawkConfig.EPG_HISTORY, data);
                }
            }, EPGHistory, idx);
            dialog.show();
        });
        findViewById(R.id.proxyHistory).setOnClickListener(v -> {
            ArrayList<String> proxyHistory = Hawk.get(HawkConfig.PROXY_URL_HISTORY, new ArrayList<String>());
            if (proxyHistory.isEmpty())
                return;
            String current = Hawk.get(HawkConfig.PROXY_URL, "");
            int idx = 0;
            if (proxyHistory.contains(current))
                idx = proxyHistory.indexOf(current);
            ApiHistoryDialog dialog = new ApiHistoryDialog(getContext());
            dialog.setTip(HomeActivity.getRes().getString(R.string.dia_proxy_epg));
            dialog.setAdapter(new ApiHistoryDialogAdapter.SelectDialogInterface() {
                @Override
                public void click(String proxyURL) {
                    proxyUrl.setText(proxyURL);
                    Hawk.put(HawkConfig.PROXY_URL, proxyURL);
                    dialog.dismiss();
                }

                @Override
                public void del(String value, ArrayList<String> data) {
                    Hawk.put(HawkConfig.PROXY_URL_HISTORY, data);
                }
            }, proxyHistory, idx);
            dialog.show();
        });

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

    private void putDefaultApis(String url) {
        URL.DOMAIN_NAME_PROXY = url;
        // 默认线路地址
        String defaultApiName = "自备份线路";
        String defaultApi = url + URL.DEFAULT_API_URL;
        // 默认仓库地址
        String defaultStoreApi = url + URL.DEFAULT_STORE_API_URL;

        Map<String, String> defaultApiMap = Hawk.get(HawkConfig.API_MAP, new HashMap<>());
        defaultApiMap.put(defaultApiName, defaultApi);

        List<String> defaultApiHistory = Hawk.get(HawkConfig.API_NAME_HISTORY, new ArrayList<>());
        defaultApiHistory.add(defaultApiName);

        // 不添加默认线路
        // putDefault(HawkConfig.API_URL, defaultApi);
        // putDefault(HawkConfig.API_NAME, defaultApiName);
        // putDefault(HawkConfig.API_NAME_HISTORY, defaultApiHistory);
        // putDefault(HawkConfig.API_MAP, defaultApiMap);

        Hawk.put(HawkConfig.DEFAULT_STORE_API, defaultStoreApi);
    }

    private void refreshQRCode() {
        String address = ControlManager.get().getAddress(false);
        tvAddress.setText(address);
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
