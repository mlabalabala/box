package com.github.tvbox.osc.bbox.ui.fragment;

import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import com.github.tvbox.osc.bbox.R;
import com.github.tvbox.osc.bbox.api.ApiConfig;
import com.github.tvbox.osc.bbox.api.StoreApiConfig;
import com.github.tvbox.osc.bbox.base.BaseActivity;
import com.github.tvbox.osc.bbox.base.BaseLazyFragment;
import com.github.tvbox.osc.bbox.bean.IJKCode;
import com.github.tvbox.osc.bbox.constant.URL;
import com.github.tvbox.osc.bbox.event.RefreshEvent;
import com.github.tvbox.osc.bbox.player.thirdparty.RemoteTVBox;
import com.github.tvbox.osc.bbox.ui.activity.SettingActivity;
import com.github.tvbox.osc.bbox.ui.adapter.ApiHistoryDialogAdapter;
import com.github.tvbox.osc.bbox.ui.adapter.SelectDialogAdapter;
import com.github.tvbox.osc.bbox.ui.dialog.*;
import com.github.tvbox.osc.bbox.update.component.CustomUpdateChecker;
import com.github.tvbox.osc.bbox.update.component.CustomUpdateParser;
import com.github.tvbox.osc.bbox.update.component.CustomUpdatePrompter;
import com.github.tvbox.osc.bbox.update.pojo.VersionInfoVo;
import com.github.tvbox.osc.bbox.update.service.OkGoUpdateHttpService;
import com.github.tvbox.osc.bbox.util.*;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.orhanobut.hawk.Hawk;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.utils.UpdateUtils;
import okhttp3.HttpUrl;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class ModelSettingFragment extends BaseLazyFragment {
    private TextView tvDebugOpen;
    private TextView tvMediaCodec;
    private TextView tvParseWebView;
    private TextView tvPlay;
    private TextView tvRender;
    private TextView tvScale;
    private TextView tvApi;
    private TextView tvHomeApi;
    private TextView tvDns;
    private TextView tvHomeRec;
    private TextView tvHistoryNum;
    private TextView tvSearchView;
    private TextView tvShowPreviewText;
    private TextView tvFastSearchText;
    private TextView tvRecStyleText;
    private TextView tvIjkCachePlay;
    private View notificationPoint;

    public static ModelSettingFragment newInstance() {
        return new ModelSettingFragment().setArguments();
    }

    public ModelSettingFragment setArguments() {
        return this;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_model;
    }

    @Override
    protected void init() {
        notificationPoint = findViewById(R.id.notification_point);
        tvFastSearchText = findViewById(R.id.showFastSearchText);
        tvFastSearchText.setText(Hawk.get(HawkConfig.FAST_SEARCH_MODE, false) ? "已开启" : "已关闭");
        tvRecStyleText = findViewById(R.id.showRecStyleText);
        tvRecStyleText.setText(Hawk.get(HawkConfig.HOME_REC_STYLE, false) ? "是" : "否");
        tvShowPreviewText = findViewById(R.id.showPreviewText);
        tvShowPreviewText.setText(Hawk.get(HawkConfig.SHOW_PREVIEW, true) ? "开启" : "关闭");
        tvDebugOpen = findViewById(R.id.tvDebugOpen);
        tvParseWebView = findViewById(R.id.tvParseWebView);
        tvMediaCodec = findViewById(R.id.tvMediaCodec);
        tvPlay = findViewById(R.id.tvPlay);
        tvRender = findViewById(R.id.tvRenderType);
        tvScale = findViewById(R.id.tvScaleType);
        tvApi = findViewById(R.id.tvApi);
        tvHomeApi = findViewById(R.id.tvHomeApi);
        tvDns = findViewById(R.id.tvDns);
        tvHomeRec = findViewById(R.id.tvHomeRec);
        tvHistoryNum = findViewById(R.id.tvHistoryNum);
        tvSearchView = findViewById(R.id.tvSearchView);
        tvIjkCachePlay = findViewById(R.id.tvIjkCachePlay);
        tvMediaCodec.setText(Hawk.get(HawkConfig.IJK_CODEC, ""));
        tvDebugOpen.setText(Hawk.get(HawkConfig.DEBUG_OPEN, false) ? "已打开" : "已关闭");
        tvParseWebView.setText(Hawk.get(HawkConfig.PARSE_WEBVIEW, true) ? "系统自带" : "XWalkView");
//        tvApi.setText(Hawk.get(HawkConfig.API_URL, ""));

        tvDns.setText(OkGoHelper.dnsHttpsList.get(Hawk.get(HawkConfig.DOH_URL, 0)));
        tvHomeRec.setText(getHomeRecName(Hawk.get(HawkConfig.HOME_REC, 0)));
        tvHistoryNum.setText(HistoryHelper.getHistoryNumName(Hawk.get(HawkConfig.HISTORY_NUM, 0)));
        tvSearchView.setText(getSearchView(Hawk.get(HawkConfig.SEARCH_VIEW, 0)));
        // tvHomeApi.setText(ApiConfig.get().getHomeSourceBean().getName());
        tvHomeApi.setText(Hawk.get(HawkConfig.API_NAME, ""));
        tvScale.setText(PlayerHelper.getScaleName(Hawk.get(HawkConfig.PLAY_SCALE, 0)));
        tvPlay.setText(PlayerHelper.getPlayerName(Hawk.get(HawkConfig.PLAY_TYPE, 0)));
        tvRender.setText(PlayerHelper.getRenderName(Hawk.get(HawkConfig.PLAY_RENDER, 0)));
        tvIjkCachePlay.setText(Hawk.get(HawkConfig.IJK_CACHE_PLAY, false) ? "开启" : "关闭");
        checkHasUpdate();
        findViewById(R.id.llCheckUpdate).setOnClickListener( v -> {
            Toast.makeText(mActivity, "检查更新中...", Toast.LENGTH_SHORT).show();
            checkUpdate();
            notificationPoint.setVisibility(View.GONE);
        });
        findViewById(R.id.llDebug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.DEBUG_OPEN, !Hawk.get(HawkConfig.DEBUG_OPEN, false));
                tvDebugOpen.setText(Hawk.get(HawkConfig.DEBUG_OPEN, false) ? "已打开" : "已关闭");
            }
        });
        findViewById(R.id.llParseWebVew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                boolean useSystem = !Hawk.get(HawkConfig.PARSE_WEBVIEW, true);
                Hawk.put(HawkConfig.PARSE_WEBVIEW, useSystem);
                tvParseWebView.setText(Hawk.get(HawkConfig.PARSE_WEBVIEW, true) ? "系统自带" : "XWalkView");
                if (!useSystem) {
                    Toast.makeText(mContext, "注意: XWalkView只适用于部分低Android版本，Android5.0以上推荐使用系统自带", Toast.LENGTH_LONG).show();
                    XWalkInitDialog dialog = new XWalkInitDialog(mContext);
                    dialog.setOnListener(new XWalkInitDialog.OnListener() {
                        @Override
                        public void onchange() {
                        }
                    });
                    dialog.show();
                }
            }
        });
        findViewById(R.id.llBackup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                BackupDialog dialog = new BackupDialog(mActivity);
                dialog.show();
            }
        });
        findViewById(R.id.llAbout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                AboutDialog dialog = new AboutDialog(mActivity);
                dialog.show();
            }
        });
        findViewById(R.id.llWp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                if (!ApiConfig.get().wallpaper.isEmpty())
                    OkGo.<File>get(ApiConfig.get().wallpaper).execute(new FileCallback(requireActivity().getFilesDir().getAbsolutePath(), "wp") {
                        @Override
                        public void onSuccess(Response<File> response) {
                            ((BaseActivity) requireActivity()).changeWallpaper(true);
                        }

                        @Override
                        public void onError(Response<File> response) {
                            super.onError(response);
                        }

                        @Override
                        public void downloadProgress(Progress progress) {
                            super.downloadProgress(progress);
                        }
                    });
            }
        });
        findViewById(R.id.llWpRecovery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                File wp = new File(requireActivity().getFilesDir().getAbsolutePath() + "/wp");
                if (wp.exists())
                    wp.delete();
                ((BaseActivity) requireActivity()).changeWallpaper(true);
            }
        });
        findViewById(R.id.llHomeApi).setOnClickListener( v -> {
            ArrayList<String> history = Hawk.get(HawkConfig.API_NAME_HISTORY, new ArrayList<>());
            HashMap<String, String> map = Hawk.get(HawkConfig.API_MAP, new HashMap<>());

            if (history.isEmpty())
                return;
            String current = Hawk.get(HawkConfig.API_NAME, "");
            int idx = 0;
            if (history.contains(current))
                idx = history.indexOf(current);
            ApiHistoryDialog dialog = new ApiHistoryDialog(getContext());
            dialog.setTip("历史配置列表");
            dialog.setAdapter(new ApiHistoryDialogAdapter.SelectDialogInterface() {
                @Override
                public void click(String value) {
                    Hawk.put(HawkConfig.API_NAME, value);
                    if (map.containsKey(value))
                        Hawk.put(HawkConfig.API_URL, map.get(value));
                    else
                        Hawk.put(HawkConfig.API_URL, value);

                    tvHomeApi.setText(value);

                    dialog.dismiss();
                }


                @Override
                public void del(String value, ArrayList<String> data) {
                    Hawk.put(HawkConfig.API_NAME_HISTORY, data);
                }
            }, history, idx);
            dialog.show();
        });
        findViewById(R.id.llDns).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int dohUrl = Hawk.get(HawkConfig.DOH_URL, 0);

                SelectDialog<String> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择安全DNS");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<String>() {
                    @Override
                    public void click(String value, int pos) {
                        tvDns.setText(OkGoHelper.dnsHttpsList.get(pos));
                        Hawk.put(HawkConfig.DOH_URL, pos);
                        String url = OkGoHelper.getDohUrl(pos);
                        OkGoHelper.dnsOverHttps.setUrl(url.isEmpty() ? null : HttpUrl.get(url));
                        IjkMediaPlayer.toggleDotPort(pos > 0);
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
                }, OkGoHelper.dnsHttpsList, dohUrl);
                dialog.show();
            }
        });
        findViewById(R.id.llApi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                ApiDialog dialog = new ApiDialog(mActivity);
                EventBus.getDefault().register(dialog);
                dialog.setOnListener(url -> {
                    if (-1 != url.indexOf("api-")) {
                        url = url.replaceAll("api-", "");
                        tvHomeApi.setText(url);
                    }
//                        tvApi.setText(api);
                });
                dialog.setOnDismissListener(dialog1 -> {
                    ((BaseActivity) mActivity).hideSysBar();
                    EventBus.getDefault().unregister(dialog1);
                });
                dialog.show();
            }
        });

        findViewById(R.id.llStoreApi).setOnClickListener( v -> {
            FastClickCheckUtil.check(v);
            StoreApiDialog storeApiDialog = new StoreApiDialog(mActivity);
            EventBus.getDefault().register(storeApiDialog);
            storeApiDialog.setOnListener(new StoreApiDialog.OnListener() {
                @Override
                public void onchange(String name) {
                    Hawk.put(HawkConfig.STORE_API_NAME, name);
                }
            });
            storeApiDialog.setOnDismissListener(dialog -> {
                ((BaseActivity) mActivity).hideSysBar();
                EventBus.getDefault().unregister(dialog);
            });
            storeApiDialog.show();
        });

         findViewById(R.id.llApiHistory).setOnClickListener( v -> {
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
            dialog.setTip("历史配置列表");
            dialog.setAdapter(new ApiHistoryDialogAdapter.SelectDialogInterface() {
                @Override
                public void click(String value) {
                    Hawk.put(HawkConfig.API_NAME, value);
                    if (map.containsKey(value))
                        Hawk.put(HawkConfig.API_URL, map.get(value));
                    else
                        Hawk.put(HawkConfig.API_URL, value);

                    tvHomeApi.setText(value);

                    dialog.dismiss();
                }


                @Override
                public void del(String value, ArrayList<String> data) {
                    Hawk.put(HawkConfig.API_NAME_HISTORY, data);
                }
            }, history, idx);
            dialog.show();
        });

        findViewById(R.id.llStoreApiHistory).setOnClickListener( v -> {
            ArrayList<String> history = Hawk.get(HawkConfig.STORE_API_NAME_HISTORY, new ArrayList<>());
            if (history.isEmpty())
                return;

            String storeApiName = Hawk.get(HawkConfig.STORE_API_NAME, "");

            int idx = 0;
            if (history.contains(storeApiName))
                idx = history.indexOf(storeApiName);

            ApiHistoryDialog dialog = new ApiHistoryDialog(getContext());
            dialog.setTip("多源历史配置列表");
            dialog.setAdapter(new ApiHistoryDialogAdapter.SelectDialogInterface() {
                @Override
                public void click(String value) {
                    Hawk.put(HawkConfig.STORE_API_NAME, value);
                    try {
                        StoreApiConfig.get().Subscribe(getContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }

                @Override
                public void del(String value, ArrayList<String> data) {
                    HashMap<String, String> map = Hawk.get(HawkConfig.STORE_API_MAP, new HashMap<>());
                    map.remove(value);
                    Hawk.put(HawkConfig.STORE_API_MAP, map);
                    Hawk.put(HawkConfig.STORE_API_NAME_HISTORY, data);
                }
            }, history, idx);
            dialog.show();
        });


        findViewById(R.id.llMediaCodec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<IJKCode> ijkCodes = ApiConfig.get().getIjkCodes();
                if (ijkCodes == null || ijkCodes.size() == 0)
                    return;
                FastClickCheckUtil.check(v);

                int defaultPos = 0;
                String ijkSel = Hawk.get(HawkConfig.IJK_CODEC, "");
                for (int j = 0; j < ijkCodes.size(); j++) {
                    if (ijkSel.equals(ijkCodes.get(j).getName())) {
                        defaultPos = j;
                        break;
                    }
                }

                SelectDialog<IJKCode> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择IJK解码");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<IJKCode>() {
                    @Override
                    public void click(IJKCode value, int pos) {
                        value.selected(true);
                        tvMediaCodec.setText(value.getName());
                    }

                    @Override
                    public String getDisplay(IJKCode val) {
                        return val.getName();
                    }
                }, new DiffUtil.ItemCallback<IJKCode>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull IJKCode oldItem, @NonNull @NotNull IJKCode newItem) {
                        return oldItem == newItem;
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull IJKCode oldItem, @NonNull @NotNull IJKCode newItem) {
                        return oldItem.getName().equals(newItem.getName());
                    }
                }, ijkCodes, defaultPos);
                dialog.show();
            }
        });
        findViewById(R.id.llScale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.PLAY_SCALE, 0);
                ArrayList<Integer> players = new ArrayList<>();
                players.add(0);
                players.add(1);
                players.add(2);
                players.add(3);
                players.add(4);
                players.add(5);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择默认画面缩放");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.PLAY_SCALE, value);
                        tvScale.setText(PlayerHelper.getScaleName(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return PlayerHelper.getScaleName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, players, defaultPos);
                dialog.show();
            }
        });
        findViewById(R.id.llPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int playerType = Hawk.get(HawkConfig.PLAY_TYPE, 0);
                int defaultPos = 0;
                ArrayList<Integer> players = PlayerHelper.getExistPlayerTypes();
                ArrayList<Integer> renders = new ArrayList<>();
                for(int p = 0; p<players.size(); p++) {
                    renders.add(p);
                    if (players.get(p) == playerType) {
                        defaultPos = p;
                    }
                }
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择默认播放器");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Integer thisPlayerType = players.get(pos);
                        Hawk.put(HawkConfig.PLAY_TYPE, thisPlayerType);
                        tvPlay.setText(PlayerHelper.getPlayerName(thisPlayerType));
                        PlayerHelper.init();
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        Integer playerType = players.get(val);
                        return PlayerHelper.getPlayerName(playerType);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, renders, defaultPos);
                dialog.show();
            }
        });
        findViewById(R.id.llRender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.PLAY_RENDER, 0);
                ArrayList<Integer> renders = new ArrayList<>();
                renders.add(0);
                renders.add(1);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择默认渲染方式");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.PLAY_RENDER, value);
                        tvRender.setText(PlayerHelper.getRenderName(value));
                        PlayerHelper.init();
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return PlayerHelper.getRenderName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, renders, defaultPos);
                dialog.show();
            }
        });
        findViewById(R.id.llHomeRec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.HOME_REC, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                types.add(2);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择首页列表数据");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.HOME_REC, value);
                        tvHomeRec.setText(getHomeRecName(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return getHomeRecName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.show();
            }
        });
        findViewById(R.id.llSearchView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.SEARCH_VIEW, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择搜索视图");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.SEARCH_VIEW, value);
                        tvSearchView.setText(getSearchView(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return getSearchView(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.show();
            }
        });
        SettingActivity.callback = new SettingActivity.DevModeCallback() {
            @Override
            public void onChange() {
                findViewById(R.id.llDebug).setVisibility(View.VISIBLE);
            }
        };

        findViewById(R.id.showPreview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.SHOW_PREVIEW, !Hawk.get(HawkConfig.SHOW_PREVIEW, true));
                tvShowPreviewText.setText(Hawk.get(HawkConfig.SHOW_PREVIEW, true) ? "开启" : "关闭");
            }
        });
        findViewById(R.id.llHistoryNum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.HISTORY_NUM, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                types.add(2);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("保留历史记录数量");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.HISTORY_NUM, value);
                        tvHistoryNum.setText(HistoryHelper.getHistoryNumName(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return HistoryHelper.getHistoryNumName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.show();
            }
        });
        findViewById(R.id.showFastSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.FAST_SEARCH_MODE, !Hawk.get(HawkConfig.FAST_SEARCH_MODE, false));
                tvFastSearchText.setText(Hawk.get(HawkConfig.FAST_SEARCH_MODE, false) ? "已开启" : "已关闭");
            }
        });
        findViewById(R.id.llHomeRecStyle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.HOME_REC_STYLE, !Hawk.get(HawkConfig.HOME_REC_STYLE, false));
                tvRecStyleText.setText(Hawk.get(HawkConfig.HOME_REC_STYLE, false) ? "是" : "否");
            }
        });

        findViewById(R.id.llSearchTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FastClickCheckUtil.check(view);
                loadingSearchRemoteTvDialog = new SearchRemoteTvDialog(mActivity);
                EventBus.getDefault().register(loadingSearchRemoteTvDialog);
                loadingSearchRemoteTvDialog.setTip("搜索附近TVBox");
                loadingSearchRemoteTvDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        EventBus.getDefault().unregister(loadingSearchRemoteTvDialog);
                    }
                });
                loadingSearchRemoteTvDialog.show();

                RemoteTVBox tv = new RemoteTVBox();
                remoteTvHostList = new ArrayList<>();
                foundRemoteTv = false;
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                RemoteTVBox.searchAvalible(tv.new Callback() {
                                    @Override
                                    public void found(String viewHost, boolean end) {
                                        remoteTvHostList.add(viewHost);
                                        if (end) {
                                            foundRemoteTv = true;
                                            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_SETTING_SEARCH_TV));
                                        }
                                    }

                                    @Override
                                    public void fail(boolean all, boolean end) {
                                        if (end) {
                                            if (all) {
                                                foundRemoteTv = false;
                                            } else {
                                                foundRemoteTv = true;
                                            }
                                            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_SETTING_SEARCH_TV));
                                        }
                                    }
                                });
                            }
                        }).start();

                    }
                }, 500);


            }
        });

        findViewById(R.id.llIjkCachePlay).setOnClickListener((view -> onClickIjkCachePlay(view)));
        findViewById(R.id.llClearCache).setOnClickListener((view -> onClickClearCache(view)));
    }

    private void checkHasUpdate () {
        if (Hawk.get(HawkConfig.IS_IGNORE_VERSION, false)) {LOG.i("已忽略更新");return;}
        // LOG.i("checkHasUpdate");
        String checkUrl = URL.GITHUB_VERSION_PATH;
        StoreApiConfig.get().doGet(checkUrl, new StoreApiConfig.StoreApiConfigCallback() {
            @Override
            public void success(String json) {
                try {
                    LOG.i(json);
                    VersionInfoVo versionInfoVo = JsonUtil.fromJson(json, VersionInfoVo.class);
                    if (versionInfoVo != null && versionInfoVo.getVersionCode() > UpdateUtils.getVersionCode(getContext())) {
                        LOG.i(versionInfoVo.toString());
                        // 有新版本
                        Hawk.put(HawkConfig.VERSION_INFO_STR, json);
                        notificationPoint.setVisibility(View.VISIBLE);

                    } else {
                        // 已是最新版本
                        Hawk.put(HawkConfig.VERSION_INFO_STR, json);
                        notificationPoint.setVisibility(View.GONE);
                    }
                }
                catch (Exception e) {
                    LOG.i(e.getMessage());
                }
            }

            @Override
            public void error(String msg) {
                Toast.makeText(mContext, "请求：" + checkUrl + "失败！", Toast.LENGTH_SHORT);
                Hawk.put(HawkConfig.VERSION_INFO_STR, "");
            }
        });
    }

    private void checkUpdate () {
        Hawk.put(HawkConfig.IS_IGNORE_VERSION, false);
        String checkUrl = URL.GITHUB_VERSION_PATH;
        String apkUrl = URL.APK_PATH;
        XUpdate.newBuild(mContext)
                .updateUrl(checkUrl)
                .updateChecker(new CustomUpdateChecker(getActivity()))
                .updateParser(new CustomUpdateParser(mContext, apkUrl))
                .updatePrompter(new CustomUpdatePrompter())
                // .updateDownLoader(new CustomUpdateDownloader())
                .updateHttpService(new OkGoUpdateHttpService())
                .update();
    }

    private void onClickIjkCachePlay(View v) {
        FastClickCheckUtil.check(v);
        Hawk.put(HawkConfig.IJK_CACHE_PLAY, !Hawk.get(HawkConfig.IJK_CACHE_PLAY, false));
        tvIjkCachePlay.setText(Hawk.get(HawkConfig.IJK_CACHE_PLAY, false) ? "开启" : "关闭");
    }

    private void onClickClearCache(View v) {
        FastClickCheckUtil.check(v);
        String cachePath = FileUtils.getCachePath();
        LOG.i("cachePath: " + cachePath);
        File cacheDir = new File(cachePath);
        if (!cacheDir.exists()) return;
        new Thread(() -> {
            try {
                FileUtils.cleanDirectory(cacheDir);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Toast.makeText(getContext(), "缓存已清空", Toast.LENGTH_LONG).show();
        return;
    }


    public static SearchRemoteTvDialog loadingSearchRemoteTvDialog;
    public static List<String> remoteTvHostList;
    public static boolean foundRemoteTv;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SettingActivity.callback = null;
    }

    String getHomeRecName(int type) {
        if (type == 1) {
            return "站点推荐";
        } else if (type == 2) {
            return "观看历史";
        } else {
            return "豆瓣热播";
        }
    }

    String getSearchView(int type) {
        if (type == 0) {
            return "文字列表";
        } else {
            return "缩略图";
        }
    }
}