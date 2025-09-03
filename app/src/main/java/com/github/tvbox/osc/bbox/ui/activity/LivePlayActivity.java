package com.github.tvbox.osc.bbox.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.catvod.crawler.Spider;
import com.github.tvbox.osc.bbox.R;
import com.github.tvbox.osc.bbox.api.ApiConfig;
import com.github.tvbox.osc.bbox.base.App;
import com.github.tvbox.osc.bbox.base.BaseActivity;
import com.github.tvbox.osc.bbox.bean.*;
import com.github.tvbox.osc.bbox.player.controller.LiveController;
import com.github.tvbox.osc.bbox.server.ControlManager;
import com.github.tvbox.osc.bbox.ui.adapter.*;
import com.github.tvbox.osc.bbox.ui.dialog.ApiDialog;
import com.github.tvbox.osc.bbox.ui.dialog.ApiHistoryDialog;
import com.github.tvbox.osc.bbox.ui.dialog.LivePasswordDialog;
import com.github.tvbox.osc.bbox.ui.tv.widget.ViewObj;
import com.github.tvbox.osc.bbox.util.*;
import com.github.tvbox.osc.bbox.util.live.TxtSubscribe;
import com.github.tvbox.osc.bbox.util.urlhttp.CallBackUtil;
import com.github.tvbox.osc.bbox.util.urlhttp.UrlHttpUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;
import com.squareup.picasso.Picasso;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import xyz.doikki.videoplayer.player.VideoView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.tvbox.osc.bbox.util.RegexUtils.getPattern;

/**
 * @author pj567
 * @date :2021/1/12
 * @description:
 */
public class LivePlayActivity extends BaseActivity {
    public static Context context;
    private VideoView<xyz.doikki.videoplayer.player.AbstractPlayer> mVideoView;
    private TextView tvChannelInfo;
    private TextView tvTime;
    private TextView tvNetSpeed;
    private LinearLayout tvLeftChannelListLayout;
    private TvRecyclerView mChannelGroupView;
    private TvRecyclerView mLiveChannelView;
    private LiveChannelGroupAdapter liveChannelGroupAdapter;
    private LiveChannelItemAdapter liveChannelItemAdapter;

    private LinearLayout tvRightSettingLayout;
    private TvRecyclerView mSettingGroupView;
    private TvRecyclerView mSettingItemView;
    private LiveSettingGroupAdapter liveSettingGroupAdapter;
    private LiveSettingItemAdapter liveSettingItemAdapter;
    private List<LiveSettingGroup> liveSettingGroupList = new ArrayList<>();

    public static  int currentChannelGroupIndex = 0;
    private Handler mHandler = new Handler();

    private List<LiveChannelGroup> liveChannelGroupList = new ArrayList<>();
    private Map<String, List<Epginfo>> liveChannelEpgInfoList = new HashMap<>();
    private int currentLiveChannelIndex = -1;
    private int currentLiveLookBackIndex = -1;
    private int currentLiveChangeSourceTimes = 0;
    private LiveChannelItem currentLiveChannelItem = null;
    private LivePlayerManager livePlayerManager = new LivePlayerManager();
    private ArrayList<Integer> channelGroupPasswordConfirmed = new ArrayList<>();

    //EPG   by 龍
    private static LiveChannelItem  channel_Name = null;
    private static Hashtable<String, ArrayList<Epginfo>> hsEpg = new Hashtable<>();
    private CountDownTimer countDownTimer;
    // private CountDownTimer countDownTimerRightTop;
    private View ll_right_top_loading;
    private View ll_right_top_huikan;
    private View divLoadEpg;
    private View divLoadEpgleft;
    private LinearLayout divEpg;
    RelativeLayout ll_epg;
    TextView tv_channelnum;
    TextView tip_chname;
    TextView tip_epg1;
    TextView  tip_epg2;
    TextView tv_srcinfo;
    TextView tv_curepg_left;
    TextView tv_nextepg_left;
    private MyEpgAdapter myAdapter;
    private TextView tv_right_top_tipnetspeed;
    private TextView tv_right_top_channel_name;
    private TextView tv_right_top_epg_name;
    private TextView tv_right_top_type;
    private ImageView iv_circle_bg;
    private TextView tv_shownum ;
    private TextView txtNoEpg ;
    private ImageView iv_back_bg;

    private ObjectAnimator objectAnimator;
    public String epgStringAddress ="";

    private TvRecyclerView mEpgDateGridView;
    private TvRecyclerView mRightEpgList;
    private LiveEpgDateAdapter liveEpgDateAdapter;
    private LiveEpgAdapter epgListAdapter;

    private List<LiveDayListGroup> liveDayList = new ArrayList<>();


    //laodao 7day replay
    public static SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat formatDate1 = new SimpleDateFormat("MM-dd");
    public static String day = formatDate.format(new Date());
    public static Date nowday = new Date();

    private boolean isSHIYI = false;
    private boolean isBack = false;
    private static String shiyi_time;//时移时间
    private static int shiyi_time_c;//时移时间差值
    public static String playUrl;
    //kenson
    private ImageView imgLiveIcon;
    private FrameLayout liveIconNullBg;
    private TextView liveIconNullText;
    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
    private View backcontroller;
    private CountDownTimer countDownTimer3;
    private final int videoWidth = 1920;
    private final int videoHeight = 1080;
    private TextView tv_currentpos;
    private TextView tv_duration;
    private SeekBar sBar;
    private View iv_playpause;
    private View iv_play;
    private  boolean show = false;
    private static final int postTimeout = 6000;

    // 遥控器数字键输入的要切换的频道号码
    private int selectedChannelNumber = 0;
    private TextView tvSelectedChannel;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_live_play;
    }

    @Override
    protected void init() {
        context = this;
        epgStringAddress = Hawk.get(HawkConfig.EPG_URL,"");
        if(epgStringAddress == null || epgStringAddress.length()<5)
            epgStringAddress = "http://epg.51zmt.top:8000/api/diyp/";

        setLoadSir(findViewById(R.id.live_root));
        mVideoView = findViewById(R.id.mVideoView);

        tvLeftChannelListLayout = findViewById(R.id.tvLeftChannnelListLayout);
        mChannelGroupView = findViewById(R.id.mGroupGridView);
        mLiveChannelView = findViewById(R.id.mChannelGridView);
        tvRightSettingLayout = findViewById(R.id.tvRightSettingLayout);
        mSettingGroupView = findViewById(R.id.mSettingGroupView);
        mSettingItemView = findViewById(R.id.mSettingItemView);
        tvChannelInfo = findViewById(R.id.tvChannel);
        tvTime = findViewById(R.id.tvTime);
        tvNetSpeed = findViewById(R.id.tvNetSpeed);

        //EPG  findViewById  by 龍
        tip_chname = (TextView)  findViewById(R.id.tv_channel_bar_name);//底部名称
        tv_channelnum = (TextView) findViewById(R.id.tv_channel_bottom_number); //底部数字
        tip_epg1 = (TextView) findViewById(R.id.tv_current_program_time);//底部EPG当前节目信息
        tip_epg2 = (TextView) findViewById(R.id.tv_next_program_time);//底部EPG当下个节目信息
        tv_srcinfo = (TextView) findViewById(R.id.tv_source);//线路状态
        tv_curepg_left = (TextView) findViewById(R.id.tv_current_program);//当前节目
        tv_nextepg_left= (TextView) findViewById(R.id.tv_next_program);//下一节目
        ll_epg = (RelativeLayout) findViewById(R.id.ll_epg);
//        tv_right_top_tipnetspeed = (TextView)findViewById(R.id.tv_right_top_tipnetspeed);
        tv_right_top_channel_name = (TextView)findViewById(R.id.tv_right_top_channel_name);
        tv_right_top_epg_name = (TextView)findViewById(R.id.tv_right_top_epg_name);
//        tv_right_top_type = (TextView)findViewById(R.id.tv_right_top_type);
//         iv_circle_bg = (ImageView) findViewById(R.id.iv_circle_bg);
        iv_back_bg = (ImageView) findViewById(R.id.iv_back_bg);
        tv_shownum = (TextView) findViewById(R.id.tv_shownum);
        txtNoEpg = (TextView) findViewById(R.id.txtNoEpg);
        ll_right_top_loading = findViewById(R.id.ll_right_top_loading);
        ll_right_top_huikan = findViewById(R.id.ll_right_top_huikan);
        divLoadEpg = (View) findViewById(R.id.divLoadEpg);
        divLoadEpgleft = (View) findViewById(R.id.divLoadEpgleft);
        divEpg = (LinearLayout) findViewById(R.id.divEPG);
        //右上角图片旋转
        // objectAnimator = ObjectAnimator.ofFloat(iv_circle_bg,"rotation", 360.0f);
        // objectAnimator.setDuration(5000);
        // objectAnimator.setRepeatCount(-1);
        // objectAnimator.start();

        //laodao 7day replay
        mEpgDateGridView = findViewById(R.id.mEpgDateGridView);
        Hawk.put(HawkConfig.NOW_DATE, formatDate.format(new Date()));
        day=formatDate.format(new Date());
        nowday=new Date();

        mRightEpgList = (TvRecyclerView) findViewById(R.id.lv_epg);
        //EPG频道名称
        imgLiveIcon = findViewById(R.id.img_live_icon);
        liveIconNullBg = findViewById(R.id.live_icon_null_bg);
        liveIconNullText = findViewById(R.id.live_icon_null_text);
        imgLiveIcon.setVisibility(View.INVISIBLE);
        liveIconNullText.setVisibility(View.INVISIBLE);
        liveIconNullBg.setVisibility(View.INVISIBLE);

        sBar = (SeekBar) findViewById(R.id.pb_progressbar);
        tv_currentpos = (TextView) findViewById(R.id.tv_currentpos);
        backcontroller = (View) findViewById(R.id.backcontroller);
        tv_duration = (TextView) findViewById(R.id.tv_duration);
        iv_playpause = findViewById(R.id.iv_playpause);
        iv_play = findViewById(R.id.iv_play);

        numberTextView = findViewById(R.id.numberTextView);

        if(show){
            backcontroller.setVisibility(View.VISIBLE);
            ll_epg.setVisibility(View.GONE);
        }else{
            backcontroller.setVisibility(View.GONE);
            ll_epg.setVisibility(View.VISIBLE);
        }


        iv_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mVideoView.start();
                iv_play.setVisibility(View.INVISIBLE);
                countDownTimer.start();
                iv_playpause.setBackground(ContextCompat.getDrawable(LivePlayActivity.context, R.drawable.vod_pause));
            }
        });

        iv_playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(mVideoView.isPlaying()){
                    mVideoView.pause();
                    countDownTimer.cancel();
                    iv_play.setVisibility(View.VISIBLE);
                    iv_playpause.setBackground(ContextCompat.getDrawable(LivePlayActivity.context, R.drawable.icon_play));
                }else{
                    mVideoView.start();
                    iv_play.setVisibility(View.INVISIBLE);
                    countDownTimer.start();
                    iv_playpause.setBackground(ContextCompat.getDrawable(LivePlayActivity.context, R.drawable.vod_pause));
                }
            }
        });
        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onStopTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromuser) {
                if (!fromuser) {
                    return;
                }
                if(countDownTimer!=null){
                    mVideoView.seekTo(progress);
                    countDownTimer.cancel();
                    countDownTimer.start();
                }
            }


        });
        sBar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int keycode, KeyEvent event) {
                if(event.getAction()==KeyEvent.ACTION_DOWN){
                    if(keycode==KeyEvent.KEYCODE_DPAD_CENTER||keycode==KeyEvent.KEYCODE_ENTER){
                        if(mVideoView.isPlaying()){
                            mVideoView.pause();
                            countDownTimer.cancel();
                            iv_play.setVisibility(View.VISIBLE);
                            iv_playpause.setBackground(ContextCompat.getDrawable(LivePlayActivity.context, R.drawable.icon_play));
                        }else{
                            mVideoView.start();
                            iv_play.setVisibility(View.INVISIBLE);
                            countDownTimer.start();
                            iv_playpause.setBackground(ContextCompat.getDrawable(LivePlayActivity.context, R.drawable.vod_pause));
                        }
                    }
                }
                return false;
            }
        });
        // 作为直播播放器需要使用
        //ApiConfig.get().onlyLoadBaseConfig();
        //ControlManager.get().startServer();
        initEpgDateView();
        initEpgListView();
        initDayList();
        initVideoView();
        initChannelGroupView();
        initLiveChannelView();
        initSettingGroupView();
        initSettingItemView();
        initLiveChannelList();
        // initChannelEpgInfoList();
        initLiveSettingGroupList();
        Hawk.put(HawkConfig.PLAYER_IS_LIVE,false);
    }

    //获取EPG并存储 // 百川epg  DIYP epg   51zmt epg ------- 自建EPG格式输出格式请参考 51zmt
    private List<Epginfo> epgdata = new ArrayList<>();

    private void showEpg(Date date, ArrayList<Epginfo> arrayList) {
        if (arrayList != null && arrayList.size() > 0) {
            epgdata = arrayList;
            epgListAdapter.CanBack(currentLiveChannelItem.getinclude_back());
            epgListAdapter.setNewData(epgdata);

            int i = -1;
            int size = epgdata.size() - 1;
            while (size >= 0) {
                if (new Date().compareTo(((Epginfo) epgdata.get(size)).startdateTime) >= 0) {
                    break;
                }
                size--;
            }
            i = size;
            if (i >= 0 && new Date().compareTo(epgdata.get(i).enddateTime) <= 0) {
                mRightEpgList.setSelectedPosition(i);
                mRightEpgList.setSelection(i);
                epgListAdapter.setSelectedEpgIndex(i);
                int finalI = i;
                mRightEpgList.post(new Runnable() {
                    @Override
                    public void run() {
                        mRightEpgList.smoothScrollToPosition(finalI);
                    }
                });
            }
        }
    }
    private String getFirstPartBeforeSpace(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        int spaceIndex = str.indexOf(' ');
        if (spaceIndex == -1) {
            return str;
        } else {
            return str.substring(0, spaceIndex);
        }
    }

    public void getEpg(Date date) {
        String channelName = channel_Name.getChannelName();
        String channelNameReal = getFirstPartBeforeSpace(channelName);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String epgTagName = channelNameReal;
        if (logoUrl==null || logoUrl.isEmpty()){
            String[] epgInfo = EpgUtil.getEpgInfo(channelNameReal);
            if (epgInfo != null && !epgInfo[1].isEmpty()) {
                epgTagName = epgInfo[1];
            }
            updateChannelIcon(channelName, epgInfo == null ? null : epgInfo[0]);
        }else if(logoUrl.equals("false")){
            updateChannelIcon(channelName, null);
        }else {
            String logo= logoUrl.replace("{name}",epgTagName);
            updateChannelIcon(channelName, logo);
        }
        epgListAdapter.CanBack(currentLiveChannelItem.getinclude_back());
        String url;
        if(epgStringAddress.contains("{name}") && epgStringAddress.contains("{date}")){
            url= epgStringAddress.replace("{name}", URLEncoder.encode(epgTagName)).replace("{date}",timeFormat.format(date));
        }else {
            url= epgStringAddress + "?ch="+ URLEncoder.encode(epgTagName) + "&date=" + timeFormat.format(date);
        }

        String savedEpgKey = channelName + "_" + Objects.requireNonNull(liveEpgDateAdapter.getItem(liveEpgDateAdapter.getSelectedIndex())).getDatePresented();
        if (hsEpg.containsKey(savedEpgKey)){
            showEpg(date, hsEpg.get(savedEpgKey));
            showBottomEpg();
            return;
        }
        UrlHttpUtil.get(url, new CallBackUtil.CallBackString() {
            public void onFailure(int i, String str) {
//                showEpg(date, new ArrayList<>());
//                showBottomEpg();
            }

            public void onResponse(String paramString) {
                LOG.i("echo-epgTagName:"+channelNameReal);
                ArrayList<Epginfo> arrayList = new ArrayList<Epginfo>();
                try {
                    if (paramString.contains("epg_data")) {
                        final JSONArray jSONArray = new JSONObject(paramString).optJSONArray("epg_data");
                        if (jSONArray != null)
                            for (int b = 0; b < jSONArray.length(); b++) {
                                JSONObject jSONObject = jSONArray.getJSONObject(b);
                                Epginfo epgbcinfo = new Epginfo(date,jSONObject.optString("title"), date, jSONObject.optString("start"), jSONObject.optString("end"),b);
                                arrayList.add(epgbcinfo);
                            }
                    }

                } catch (JSONException jSONException) {
                    jSONException.printStackTrace();
                }
                hsEpg.put(savedEpgKey, arrayList);
                showEpg(date, arrayList);
                showBottomEpg();
            }
        });
    }

    //显示底部EPG
    @SuppressLint("SetTextI18n")
    private void showBottomEpg() {
        if (isSHIYI){
            return;
        }
        if (channel_Name.getChannelName() != null) {
            tip_chname.setText(channel_Name.getChannelName());
            tv_channelnum.setText("" + channel_Name.getChannelNum());
            TextView tv_current_program_name = findViewById(R.id.tv_current_program_name);
            TextView tv_next_program_name = findViewById(R.id.tv_next_program_name);
            tip_epg1.setText("暂无信息");
            tv_current_program_name.setText("");
            tip_epg2.setText("暂无信息");
            tv_next_program_name.setText("");
            String savedEpgKey = channel_Name.getChannelName() + "_" + Objects.requireNonNull(liveEpgDateAdapter.getItem(liveEpgDateAdapter.getSelectedIndex())).getDatePresented();

            if (hsEpg.containsKey(savedEpgKey)) {
                ArrayList<Epginfo> arrayList = hsEpg.get(savedEpgKey);
                if (arrayList != null && arrayList.size() > 0) {
                    Date date = new Date();
                    int size = arrayList.size() - 1;
                    boolean hasInfo = false;
                    while (size >= 0) {
                        if (date.after((arrayList.get(size)).startdateTime) & date.before((arrayList.get(size)).enddateTime)) {
                            tip_epg1.setText((arrayList.get(size)).start + "-" + (arrayList.get(size)).end);
                            tv_current_program_name.setText((arrayList.get(size)).title);
                            if (size != arrayList.size() - 1) {
                                tip_epg2.setText((arrayList.get(size + 1)).start + "-" + (arrayList.get(size + 1)).end);
                                tv_next_program_name.setText((arrayList.get(size + 1)).title);
                            } else {
                                tip_epg2.setText((arrayList.get(size)).end+"-23:59");
                                tv_next_program_name.setText("精彩节目-暂无节目预告信息");
                            }
                            hasInfo=true;
                            break;
                        } else {
                            size--;
                        }
                    }
                    if(!hasInfo){
                        tip_epg1.setText("00:00-"+(arrayList.get(0)).start);
                        tv_current_program_name.setText("精彩节目-暂无节目预告信息");
                        tip_epg2.setText((arrayList.get(0)).start + "-" + (arrayList.get(0)).end);
                        tv_next_program_name.setText((arrayList.get(0)).title);
                    }
                }
                epgListAdapter.CanBack(currentLiveChannelItem.getinclude_back());
                epgListAdapter.setNewData(arrayList);
            } else {
                int selectedIndex = liveEpgDateAdapter.getSelectedIndex();
                if (selectedIndex < 0)
                    getEpg(new Date());
            }

            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            if(!tip_epg1.getText().equals("暂无信息")){
                ll_right_top_loading.setVisibility(View.VISIBLE);
                ll_epg.setVisibility(View.VISIBLE);
                countDownTimer = new CountDownTimer(postTimeout, 1000) {//底部epg隐藏时间设定
                    public void onTick(long j) {
                    }
                    public void onFinish() {
                        ll_right_top_loading.setVisibility(View.GONE);
                        ll_right_top_huikan.setVisibility(View.GONE);
                        ll_epg.setVisibility(View.GONE);
                    }
                };
                countDownTimer.start();
            }else {
                ll_right_top_loading.setVisibility(View.GONE);
                ll_right_top_huikan.setVisibility(View.GONE);
                ll_epg.setVisibility(View.GONE);
            }
            if (channel_Name == null || channel_Name.getSourceNum() <= 0) {
                ((TextView) findViewById(R.id.tv_source)).setText("1/1");
            } else {
                ((TextView) findViewById(R.id.tv_source)).setText("[线路" + (channel_Name.getSourceIndex() + 1) + "/" + channel_Name.getSourceNum() + "]");
            }
            tv_right_top_channel_name.setText(channel_Name.getChannelName());
            tv_right_top_epg_name.setText(channel_Name.getChannelName());
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateChannelIcon(String channelName, String logoUrl) {
        if (StringUtils.isEmpty(logoUrl)) {
            liveIconNullBg.setVisibility(View.VISIBLE);
            liveIconNullText.setVisibility(View.VISIBLE);
            imgLiveIcon.setVisibility(View.INVISIBLE);
            liveIconNullText.setText("" + channel_Name.getChannelNum());
        } else {
            imgLiveIcon.setVisibility(View.VISIBLE);
            Picasso.get().load(logoUrl).into(imgLiveIcon);
            liveIconNullBg.setVisibility(View.INVISIBLE);
            liveIconNullText.setVisibility(View.INVISIBLE);
        }
    }


    //频道列表
    @SuppressLint("NotifyDataSetChanged")
    public  void divLoadEpgRight(View view) {
        mHandler.removeCallbacks(mHideChannelListRun);
        mHandler.postDelayed(mHideChannelListRun, postTimeout);
        mChannelGroupView.setVisibility(View.GONE);
        divEpg.setVisibility(View.VISIBLE);
        divLoadEpgleft.setVisibility(View.VISIBLE);
        divLoadEpg.setVisibility(View.GONE);
        mRightEpgList.setSelectedPosition(epgListAdapter.getSelectedIndex());
        epgListAdapter.notifyDataSetChanged();
    }
    //频道列表
    public  void divLoadEpgLeft(View view) {
        mHandler.removeCallbacks(mHideChannelListRun);
        mHandler.postDelayed(mHideChannelListRun, postTimeout);
        mChannelGroupView.setVisibility(View.VISIBLE);
        divEpg.setVisibility(View.GONE);
        divLoadEpgleft.setVisibility(View.GONE);
        divLoadEpg.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        if (tvLeftChannelListLayout.getVisibility() == View.VISIBLE) {
            mHandler.removeCallbacks(mHideChannelListRun);
            mHandler.post(mHideChannelListRun);
        } else if (tvRightSettingLayout.getVisibility() == View.VISIBLE) {
            mHandler.removeCallbacks(mHideSettingLayoutRun);
            mHandler.post(mHideSettingLayoutRun);
        } else if( backcontroller.getVisibility() == View.VISIBLE){ //
            backcontroller.setVisibility(View.GONE);
        }else if(isBack){
            isBack= false;
            playPreSource();
        }else {
            mHandler.removeCallbacks(mConnectTimeoutChangeSourceRun);
            mHandler.removeCallbacks(mUpdateNetSpeedRun);
            super.onBackPressed();
            // 返回主页
            //Intent intent = new Intent(mContext, HomeActivity.class);
            //LivePlayActivity.this.startActivity(intent);
        }
    }

    private TextView numberTextView;
    private String enteredNumber = "";


    private void changeChannel(String channelNumber) {
        numberTextView.setVisibility(View.GONE);
        enteredNumber = "";
        if (StringUtils.isBlank(channelNumber)) return;
        int cn = Integer.parseInt(channelNumber);
        // 频道数量
        int totalChannels = 0;
        for (LiveChannelGroup lcg : liveChannelGroupList) totalChannels += lcg.getSize();
        LOG.i("cn：" + cn + " totalChannels：" + totalChannels);
        int sizeDiff = totalChannels - cn;
        if (0 <= sizeDiff) {
            int[] indexes = getChannelGroupIndex(cn, liveChannelGroupList);
            // 要切换的频道号cn所在分组index
            int channelGroupIndex = indexes[0];
            // 要切换的频道在分组中的index
            int channelIndex = indexes[1];
            // 当前分组和要切换的分组是否一致，判断是否需要切换分组
            boolean notChangeSource = channelIndex == currentLiveChannelIndex;
            LOG.i("channelGroupIndex:" + channelGroupIndex + " channelIndex:" + channelIndex + " currentLiveChannelIndex:" + currentLiveChannelIndex + " notChangeSource:" + notChangeSource);
            playChannel(channelGroupIndex, channelIndex, notChangeSource);
        }
        else {
            LOG.e("sizeDiff：" + sizeDiff);
        }
    }

    private int[] getChannelGroupIndex(int cn, List<LiveChannelGroup> liveChannelGroupList) {
        int channelGroupIndex = 0;
        int channelIndex = 0;
        int tmpDiffSize = cn;
        for (int i=0; i<liveChannelGroupList.size(); i++) {
            int  currentChannelGroupSize = liveChannelGroupList.get(i).getSize();
            tmpDiffSize -= currentChannelGroupSize;
            if (0 >= tmpDiffSize) {
                channelGroupIndex = i;
                channelIndex = currentChannelGroupSize + tmpDiffSize - 1;
                LOG.i("channelGroupIndex: " + channelGroupIndex + " channelIndex: " + channelIndex);
                break;
            }
        }
        int[] res = new int[2];
        res[0] = channelGroupIndex;
        res[1] = channelIndex;
        return res;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_INFO || keyCode == KeyEvent.KEYCODE_HELP) {
                showSettingGroup();
            }
            /*
            else if (KeyEvent.KEYCODE_SETTINGS == keyCode) {
                ApiDialog dlg = new ApiDialog(LivePlayActivity.this);
                EventBus.getDefault().register(dlg);
                dlg.setOnListener(url -> {
                    if (url.contains("live-")) {
                        try {
                            url = url.replaceAll("live-", "");
                            url = Base64.encodeToString(url.getBytes("UTF-8"), Base64.DEFAULT | Base64.URL_SAFE | Base64.NO_WRAP);
                            url = "http://127.0.0.1:9978/proxy?do=live&type=txt&ext=" + url;
                            loadProxyLives(url);
                            // reload();
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                dlg.setOnDismissListener(dialog1 -> {
                    this.hideSysBar();
                    EventBus.getDefault().unregister(dialog1);
                });
                dlg.show();
            }
            */
            else if (!isListOrSettingLayoutVisible()) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                        if (Hawk.get(HawkConfig.LIVE_CHANNEL_REVERSE, false))
                            playNext();
                        else
                            playPrevious();
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        if (Hawk.get(HawkConfig.LIVE_CHANNEL_REVERSE, false))
                            playPrevious();
                        else
                            playNext();
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        if(isBack){
                            showProgressBars(true);
                        }else{
                            playPreSource();
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        if(isBack){
                            showProgressBars(true);
                        }else{
                            playNextSource();
                        }
                        break;
                    case KeyEvent.KEYCODE_0:
                    case KeyEvent.KEYCODE_1:
                    case KeyEvent.KEYCODE_2:
                    case KeyEvent.KEYCODE_3:
                    case KeyEvent.KEYCODE_4:
                    case KeyEvent.KEYCODE_5:
                    case KeyEvent.KEYCODE_6:
                    case KeyEvent.KEYCODE_7:
                    case KeyEvent.KEYCODE_8:
                    case KeyEvent.KEYCODE_9:
                        // 拼接数字并更新TextView
                        if (enteredNumber.length() < 4) {
                            numberTextView.setVisibility(View.VISIBLE);
                            enteredNumber += (keyCode - KeyEvent.KEYCODE_0);
                            numberTextView.setText(enteredNumber);
                        }

                        mHandler.removeCallbacksAndMessages(null); // 移除之前的延迟任务
                        if (enteredNumber.length() == 4) {
                            mHandler.postDelayed(()-> changeChannel(enteredNumber), 100);
                        }
                        mHandler.postDelayed(()-> changeChannel(enteredNumber), 1000 * 3);
                        break;
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        if (View.VISIBLE == numberTextView.getVisibility())
                        {
                            mHandler.removeCallbacksAndMessages(null); // 移除之前的延迟任务
                            changeChannel(enteredNumber);
                        }
                        else {
                            showChannelList();
                        }
                        break;
                }
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {

        }
        return super.dispatchKeyEvent(event);
    }


    private final Handler mmHandler = new Handler();
    private Runnable mLongPressRunnable;
    private static final long LONG_PRESS_DELAY = 800;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) && event.getRepeatCount() == 0) {
            mLongPressRunnable = new Runnable() {
                @Override
                public void run() {
                    showSettingGroup(); //实现长按调出菜单
                }
            };
            mmHandler.postDelayed(mLongPressRunnable, LONG_PRESS_DELAY);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            if (mLongPressRunnable != null) {
                mmHandler.removeCallbacks(mLongPressRunnable);
                mLongPressRunnable = null;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.release();
            mVideoView = null;
        }
    }

    private void showChannelList() {
        if(liveChannelGroupList.isEmpty()) return;
        if (tvRightSettingLayout.getVisibility() == View.VISIBLE) {
            mHandler.removeCallbacks(mHideSettingLayoutRun);
            mHandler.post(mHideSettingLayoutRun);
            return;
        }
        if (tvLeftChannelListLayout.getVisibility() == View.INVISIBLE) {
            if(currentLiveLookBackIndex>-1){
                mRightEpgList.setSelectedPosition(currentLiveLookBackIndex);
                mRightEpgList.post(new Runnable() {
                    @Override
                    public void run() {
                        mRightEpgList.smoothScrollToPosition(currentLiveLookBackIndex);
                    }
                });
            }
            refreshChannelList(currentChannelGroupIndex);

            mHandler.postDelayed(mFocusCurrentChannelAndShowChannelList, 200);
        }
        else {
            mHandler.removeCallbacks(mHideChannelListRun);
            mHandler.post(mHideChannelListRun);
        }
    }

    private int mLastChannelGroupIndex = -1;
    private List<LiveChannelItem> mLastChannelList = new ArrayList<>();

    private void refreshChannelList(int currentChannelGroupIndex) {
        List<LiveChannelItem> newChannels = getLiveChannels(currentChannelGroupIndex);
        // 2. 判断数据是否变化
        if (currentChannelGroupIndex == mLastChannelGroupIndex
                && isSameData(newChannels, mLastChannelList)) {
            return; // 数据未变化，跳过刷新 解决部分直播频道过多时卡顿
        }
        if (currentLiveChannelIndex > -1){
            mLiveChannelView.scrollToPosition(currentLiveChannelIndex);
            mLiveChannelView.setSelection(currentLiveChannelIndex);
        }
        mChannelGroupView.scrollToPosition(currentChannelGroupIndex);
        mChannelGroupView.setSelection(currentChannelGroupIndex);
        mLastChannelGroupIndex = currentChannelGroupIndex;
        mLastChannelList = new ArrayList<>(newChannels);
        liveChannelItemAdapter.setNewData(newChannels);
    }

    // 对比两个列表内容是否相同
    private boolean isSameData(List<LiveChannelItem> list1, List<LiveChannelItem> list2) {
//        return list1.size() == list2.size();
        if (list1 == list2) return true;
        if (list1 == null || list2 == null || list1.size() != list2.size()) return false;
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    private Runnable mFocusCurrentChannelAndShowChannelList = new Runnable() {
        @Override
        public void run() {
            if (mChannelGroupView.isScrolling() || mLiveChannelView.isScrolling() || mChannelGroupView.isComputingLayout() || mLiveChannelView.isComputingLayout()) {
                mHandler.postDelayed(this, 100);
            } else {
                liveChannelGroupAdapter.setSelectedGroupIndex(currentChannelGroupIndex);
                liveChannelItemAdapter.setSelectedChannelIndex(currentLiveChannelIndex);
                RecyclerView.ViewHolder holder = mLiveChannelView.findViewHolderForAdapterPosition(currentLiveChannelIndex);
                if (holder != null)
                    holder.itemView.requestFocus();
                tvLeftChannelListLayout.setVisibility(View.VISIBLE);
                ViewObj viewObj = new ViewObj(tvLeftChannelListLayout, (ViewGroup.MarginLayoutParams) tvLeftChannelListLayout.getLayoutParams());
                ObjectAnimator animator = ObjectAnimator.ofObject(viewObj, "marginLeft", new IntEvaluator(), -tvLeftChannelListLayout.getLayoutParams().width, 0);
                animator.setDuration(200);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mHandler.removeCallbacks(mHideChannelListRun);
                        mHandler.postDelayed(mHideChannelListRun, postTimeout);
                    }
                });
                animator.start();
            }
        }
    };

    private Runnable mHideChannelListRun = new Runnable() {
        @Override
        public void run() {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tvLeftChannelListLayout.getLayoutParams();
            if (tvLeftChannelListLayout.getVisibility() == View.VISIBLE) {
                ViewObj viewObj = new ViewObj(tvLeftChannelListLayout, params);
                ObjectAnimator animator = ObjectAnimator.ofObject(viewObj, "marginLeft", new IntEvaluator(), 0, -tvLeftChannelListLayout.getLayoutParams().width);
                animator.setDuration(200);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        tvLeftChannelListLayout.setVisibility(View.INVISIBLE);
                    }
                });
                animator.start();
            }
        }
    };

    private void showChannelInfo() {
        tvChannelInfo.setText(String.format(Locale.getDefault(), "%d %s %s(%d/%d)", currentLiveChannelItem.getChannelNum(),
                currentLiveChannelItem.getChannelName(), currentLiveChannelItem.getSourceName(),
                currentLiveChannelItem.getSourceIndex() + 1, currentLiveChannelItem.getSourceNum()));

        FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (tvRightSettingLayout.getVisibility() == View.VISIBLE) {
            lParams.gravity = Gravity.LEFT;
            lParams.leftMargin = 60;
            lParams.topMargin = 30;
        } else {
            lParams.gravity = Gravity.RIGHT;
            lParams.rightMargin = 60;
            lParams.topMargin = 30;
        }
        tvChannelInfo.setLayoutParams(lParams);

        tvChannelInfo.setVisibility(View.VISIBLE);
        mHandler.removeCallbacks(mHideChannelInfoRun);
        mHandler.postDelayed(mHideChannelInfoRun, 3000);
    }

    private Runnable mHideChannelInfoRun = new Runnable() {
        @Override
        public void run() {
            tvChannelInfo.setVisibility(View.INVISIBLE);
        }
    };

    private JsonObject catchup=null;
    private Boolean hasCatchup=false;
    private String logoUrl=null;
    private void initLiveObj(){
        int position=Hawk.get(HawkConfig.LIVE_GROUP_INDEX, 0);
        JsonArray live_groups=Hawk.get(HawkConfig.LIVE_GROUP_LIST,new JsonArray());
        JsonObject livesOBJ = live_groups.get(position).getAsJsonObject();
        String type = livesOBJ.has("type")?livesOBJ.get("type").getAsString():"0";

        if(livesOBJ.has("catchup")){
            catchup = livesOBJ.getAsJsonObject("catchup");
            LOG.i("echo-catchup :"+ catchup.toString());
            hasCatchup=true;
        }
        if(livesOBJ.has("logo")){
            logoUrl = livesOBJ.get("logo").getAsString();
        }
        if(type.equals("3")){
            String py_jar="";
            if(livesOBJ.has("jar")){
                py_jar=livesOBJ.has("jar")?livesOBJ.get("jar").getAsString():"";

            }else if(livesOBJ.has("api")){
                py_jar=livesOBJ.has("api")?livesOBJ.get("api").getAsString():"";
//                String ext = livesOBJ.has("ext")?livesOBJ.get("ext").getAsJsonObject().toString():"";
                String ext="";
                if(livesOBJ.has("ext") && (livesOBJ.get("ext").isJsonObject() || livesOBJ.get("ext").isJsonArray())){
                    ext=livesOBJ.get("ext").toString();
                }else {
                    ext= DefaultConfig.safeJsonString(livesOBJ, "ext", "");
                }
                LOG.i("echo-ext:"+ext);
                if(!ext.isEmpty())py_jar=py_jar+"?extend="+ext;
            }
            ApiConfig.get().setLiveJar(py_jar);
        }
    }

    private HashMap<String,String> liveWebHeader()
    {
        return Hawk.get(HawkConfig.LIVE_WEB_HEADER);
    }
    private boolean playChannel(int channelGroupIndex, int liveChannelIndex, boolean changeSource) {
        if ((channelGroupIndex == currentChannelGroupIndex && liveChannelIndex == currentLiveChannelIndex && !changeSource)
                || (changeSource && currentLiveChannelItem.getSourceNum() == 1)) {
            // showChannelInfo();
            return true;
        }
        if(mVideoView!=null)mVideoView.release();
        if (!changeSource) {
            currentChannelGroupIndex = channelGroupIndex;
            currentLiveChannelIndex = liveChannelIndex;
            currentLiveChannelItem = getLiveChannels(currentChannelGroupIndex).get(currentLiveChannelIndex);
            Hawk.put(HawkConfig.LIVE_CHANNEL, currentLiveChannelItem.getChannelName());
            livePlayerManager.getLiveChannelPlayer(mVideoView, currentLiveChannelItem.getChannelName());
        }

        channel_Name = currentLiveChannelItem;
        currentLiveLookBackIndex=-1;
        epgListAdapter.setSelectedEpgIndex(-1);
        isSHIYI=false;
        isBack = false;
        if(hasCatchup || currentLiveChannelItem.getUrl().contains("PLTV/") || currentLiveChannelItem.getUrl().contains("TVOD/")){
            currentLiveChannelItem.setinclude_back(true);
        }else {
            currentLiveChannelItem.setinclude_back(false);
        }
        showBottomEpg();
        getEpg(new Date());
        backcontroller.setVisibility(View.GONE);
        ll_right_top_huikan.setVisibility(View.GONE);
        if(mVideoView!=null){
            if(liveWebHeader()!=null)LOG.i("echo-"+liveWebHeader().toString());
            mVideoView.setUrl(currentLiveChannelItem.getUrl(),liveWebHeader());
            mVideoView.start();
        }
        return true;
    }

    private void playNext() {
        if (!isCurrentLiveChannelValid()) return;
        Integer[] groupChannelIndex = getNextChannel(1);
        playChannel(groupChannelIndex[0], groupChannelIndex[1], false);
    }

    private void playPrevious() {
        if (!isCurrentLiveChannelValid()) return;
        Integer[] groupChannelIndex = getNextChannel(-1);
        playChannel(groupChannelIndex[0], groupChannelIndex[1], false);
    }

    public void playPreSource() {
        if (!isCurrentLiveChannelValid()) return;
        currentLiveChannelItem.preSource();
        playChannel(currentChannelGroupIndex, currentLiveChannelIndex, true);
    }

    public void playNextSource() {
        if (!isCurrentLiveChannelValid()) return;
        currentLiveChannelItem.nextSource();
        playChannel(currentChannelGroupIndex, currentLiveChannelIndex, true);
    }

    //显示设置列表
    private void showSettingGroup() {
        if (tvLeftChannelListLayout.getVisibility() == View.VISIBLE) {
            mHandler.removeCallbacks(mHideChannelListRun);
            mHandler.post(mHideChannelListRun);
        }
        if (tvRightSettingLayout.getVisibility() == View.INVISIBLE) {
            if (!isCurrentLiveChannelValid()) return;
            //重新载入默认状态
            loadCurrentSourceList();
            liveSettingGroupAdapter.setNewData(liveSettingGroupList);
            selectSettingGroup(0, false);
            mSettingGroupView.scrollToPosition(0);
            mSettingItemView.scrollToPosition(currentLiveChannelItem.getSourceIndex());
            mHandler.postDelayed(mFocusAndShowSettingGroup, 200);
        } else {
            mHandler.removeCallbacks(mHideSettingLayoutRun);
            mHandler.post(mHideSettingLayoutRun);
        }
    }

    private Runnable mFocusAndShowSettingGroup = new Runnable() {
        @Override
        public void run() {
            if (mSettingGroupView.isScrolling() || mSettingItemView.isScrolling() || mSettingGroupView.isComputingLayout() || mSettingItemView.isComputingLayout()) {
                mHandler.postDelayed(this, 100);
            } else {
                RecyclerView.ViewHolder holder = mSettingGroupView.findViewHolderForAdapterPosition(0);
                if (holder != null)
                    holder.itemView.requestFocus();
                tvRightSettingLayout.setVisibility(View.VISIBLE);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tvRightSettingLayout.getLayoutParams();
                if (tvRightSettingLayout.getVisibility() == View.VISIBLE) {
                    ViewObj viewObj = new ViewObj(tvRightSettingLayout, params);
                    ObjectAnimator animator = ObjectAnimator.ofObject(viewObj, "marginRight", new IntEvaluator(), -tvRightSettingLayout.getLayoutParams().width, 0);
                    animator.setDuration(200);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mHandler.postDelayed(mHideSettingLayoutRun, postTimeout);
                        }
                    });
                    animator.start();
                }
            }
        }
    };

    private Runnable mHideSettingLayoutRun = new Runnable() {
        @Override
        public void run() {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tvRightSettingLayout.getLayoutParams();
            if (tvRightSettingLayout.getVisibility() == View.VISIBLE) {
                ViewObj viewObj = new ViewObj(tvRightSettingLayout, params);
                ObjectAnimator animator = ObjectAnimator.ofObject(viewObj, "marginRight", new IntEvaluator(), 0, -tvRightSettingLayout.getLayoutParams().width);
                animator.setDuration(200);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        tvRightSettingLayout.setVisibility(View.INVISIBLE);
                        liveSettingGroupAdapter.setSelectedGroupIndex(-1);
                    }
                });
                animator.start();
            }
        }
    };

    //laodao 7天Epg数据绑定和展示
    private void initEpgListView() {
        mRightEpgList.setHasFixedSize(true);
        mRightEpgList.setLayoutManager(new V7LinearLayoutManager(this.mContext, 1, false));
        epgListAdapter = new LiveEpgAdapter();
        mRightEpgList.setAdapter(epgListAdapter);

        mRightEpgList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mHandler.removeCallbacks(mHideChannelListRun);
                mHandler.postDelayed(mHideChannelListRun, postTimeout);
            }
        });
        //电视
        mRightEpgList.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                epgListAdapter.setFocusedEpgIndex(-1);
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                mHandler.removeCallbacks(mHideChannelListRun);
                mHandler.postDelayed(mHideChannelListRun, postTimeout);
                epgListAdapter.setFocusedEpgIndex(position);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                if(position==currentLiveLookBackIndex)return;
                currentLiveLookBackIndex=position;
                Date date = liveEpgDateAdapter.getSelectedIndex() < 0 ? new Date() :
                        liveEpgDateAdapter.getData().get(liveEpgDateAdapter.getSelectedIndex()).getDateParamVal();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                Epginfo selectedData = epgListAdapter.getItem(position);
                String targetDate = dateFormat.format(date);
                assert selectedData != null;
                String shiyiStartdate = targetDate + selectedData.originStart.replace(":", "") + "30";
                String shiyiEnddate = targetDate + selectedData.originEnd.replace(":", "") + "30";
                Date now = new Date();
                if(new Date().compareTo(selectedData.startdateTime) < 0){
                    return;
                }
                epgListAdapter.setSelectedEpgIndex(position);
                if (now.compareTo(selectedData.startdateTime) >= 0 && now.compareTo(selectedData.enddateTime) <= 0) {
                    mVideoView.release();
                    isSHIYI = false;
                    mVideoView.setUrl(currentLiveChannelItem.getUrl(),liveWebHeader());
                    mVideoView.start();
                    epgListAdapter.setShiyiSelection(-1, false,timeFormat.format(date));
                    epgListAdapter.notifyDataSetChanged();
                    showProgressBars(false);
                    return;
                }
                String shiyiUrl = currentLiveChannelItem.getUrl();
                if (now.compareTo(selectedData.startdateTime) < 0) {

                } else if(hasCatchup || shiyiUrl.contains("PLTV/") || shiyiUrl.contains("TVOD/")){
                    shiyiUrl=shiyiUrl.replaceAll("/PLTV/", "/TVOD/");
                    mHandler.removeCallbacks(mHideChannelListRun);
                    mHandler.postDelayed(mHideChannelListRun, 100);
                    mVideoView.release();
                    shiyi_time = shiyiStartdate + "-" + shiyiEnddate;
                    isSHIYI = true;
                    //mCanSeek=true;
                    if(hasCatchup){
                        String replace=catchup.get("replace").getAsString();
                        String source=catchup.get("source").getAsString();
                        String[] parts = replace.split(",");
                        String left = parts.length > 0 ? parts[0].trim() : "";
                        String right = parts.length > 1 ? parts[1].trim() : "";
                        shiyiUrl = shiyiUrl.replaceAll(left, right);
                        // 已知参数
                        String startHHmm = selectedData.originStart.replace(":", "");
                        String endHHmm = selectedData.originEnd.replace(":", "");
                        // 正则表达式：匹配 ${(b)...} 或 ${(e)...}
                        Pattern pattern = getPattern("\\$\\{\\((b|e)\\)(.*?)\\}");
                        Matcher matcher = pattern.matcher(source);
                        Map<String, String> valueMap = new HashMap<>();
                        valueMap.put("b", targetDate + "T" + startHHmm);
                        valueMap.put("e", targetDate + "T" + endHHmm);
                        StringBuffer result = new StringBuffer();
                        while (matcher.find()) {
                            String type = matcher.group(1); // 捕获 b 或 e
                            String patternPart = matcher.group(2);
                            // 生成替换值（如 "20231023T1500"）
                            String replacement = valueMap.get(type);
                            // 将 ${(b)yyyyMMdd'T'HHmm} 替换为 "20231023T1500"
                            assert replacement != null;
                            matcher.appendReplacement(result, replacement);
                        }
                        matcher.appendTail(result);
                        LOG.i("echo-shiyiurl:"+shiyiUrl);
                        if(shiyiUrl.endsWith("&"))shiyiUrl=shiyiUrl.substring(0, shiyiUrl.length() - 1);
                        shiyiUrl += result.toString();
                    }else {
                        if (shiyiUrl.indexOf("?") <= 0) {
                            shiyiUrl += "?playseek=" + shiyi_time;
                        } else if (shiyiUrl.indexOf("playseek") > 0) {
                            shiyiUrl = shiyiUrl.replaceAll("playseek=(.*)", "playseek=" + shiyi_time);
                        } else {
                            shiyiUrl += "&playseek=" + shiyi_time;
                        }
                    }
                    LOG.i("echo-回看地址playUrl :"+ shiyiUrl);
                    playUrl = shiyiUrl;

                    mVideoView.setUrl(playUrl,liveWebHeader());
                    mVideoView.start();
                    epgListAdapter.setShiyiSelection(position, true, timeFormat.format(date));
                    epgListAdapter.notifyDataSetChanged();
                    mRightEpgList.setSelectedPosition(position);
                    mRightEpgList.post(new Runnable() {
                        @Override
                        public void run() {
                            mRightEpgList.smoothScrollToPosition(position);
                        }
                    });
                    shiyi_time_c = (int)getTime(formatDate.format(nowday) +" " + selectedData.start + ":" +"30", formatDate.format(nowday) +" " + selectedData.end + ":" +"30");
                    ViewGroup.LayoutParams lp =  iv_play.getLayoutParams();
                    lp.width=videoHeight/7;
                    lp.height=videoHeight/7;
                    sBar = (SeekBar) findViewById(R.id.pb_progressbar);
                    sBar.setMax(shiyi_time_c*1000);
                    sBar.setProgress((int)  mVideoView.getCurrentPosition());
                    tv_currentpos.setText(durationToString((int)mVideoView.getCurrentPosition()));
                    tv_duration.setText(durationToString(shiyi_time_c*1000));
                    showProgressBars(true);
                    isBack = true;
                }
            }
        });

        //手机/模拟器
        epgListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(position==currentLiveLookBackIndex)return;
                currentLiveLookBackIndex=position;
                Date date = liveEpgDateAdapter.getSelectedIndex() < 0 ? new Date() :
                        liveEpgDateAdapter.getData().get(liveEpgDateAdapter.getSelectedIndex()).getDateParamVal();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                Epginfo selectedData = epgListAdapter.getItem(position);
                String targetDate = dateFormat.format(date);
                assert selectedData != null;
                LOG.i("echo-targetDate"+targetDate);
                LOG.i("echo-targethm"+selectedData.originStart.replace(":", ""));
                String shiyiStartdate = targetDate + selectedData.originStart.replace(":", "") + "00";
                String shiyiEnddate = targetDate + selectedData.originEnd.replace(":", "") + "00";
                Date now = new Date();
                if(new Date().compareTo(selectedData.startdateTime) < 0){
                    return;
                }
                epgListAdapter.setSelectedEpgIndex(position);
                if (now.compareTo(selectedData.startdateTime) >= 0 && now.compareTo(selectedData.enddateTime) <= 0) {
                    mVideoView.release();
                    isSHIYI = false;
                    mVideoView.setUrl(currentLiveChannelItem.getUrl(),liveWebHeader());
                    mVideoView.start();
                    epgListAdapter.setShiyiSelection(-1, false,timeFormat.format(date));
                    epgListAdapter.notifyDataSetChanged();
                    showProgressBars(false);
                    return;
                }
                String shiyiUrl = currentLiveChannelItem.getUrl();
                if (now.compareTo(selectedData.startdateTime) < 0) {

                } else if(hasCatchup || shiyiUrl.contains("PLTV/") || shiyiUrl.contains("TVOD/")){
                    shiyiUrl = shiyiUrl.replaceAll("/PLTV/", "/TVOD/");
                    mHandler.removeCallbacks(mHideChannelListRun);
                    mHandler.postDelayed(mHideChannelListRun, 100);

                    mVideoView.release();
                    shiyi_time = shiyiStartdate + "-" + shiyiEnddate;
                    isSHIYI = true;
                    //mCanSeek=true;
                    if(hasCatchup){
                        String replace=catchup.get("replace").getAsString();
                        String source=catchup.get("source").getAsString();
                        String[] parts = replace.split(",");
                        String left = parts.length > 0 ? parts[0].trim() : "";
                        String right = parts.length > 1 ? parts[1].trim() : "";
                        shiyiUrl = shiyiUrl.replaceAll(left, right);
                        String startHHmm = selectedData.originStart.replace(":", "");
                        String endHHmm = selectedData.originEnd.replace(":", "");
                        // 正则表达式：匹配 ${(b)...} 或 ${(e)...}
                        Pattern pattern = getPattern("\\$\\{\\((b|e)\\)(.*?)\\}");
                        Matcher matcher = pattern.matcher(source);
                        Map<String, String> valueMap = new HashMap<>();
                        valueMap.put("b", targetDate + "T" + startHHmm);
                        valueMap.put("e", targetDate + "T" + endHHmm);
                        StringBuffer result = new StringBuffer();
                        while (matcher.find()) {
                            String type = matcher.group(1); // 捕获 b 或 e
                            String patternPart = matcher.group(2);
                            // 生成替换值（如 "20231023T1500"）
                            String replacement = valueMap.get(type);
                            // 将 ${(b)yyyyMMdd'T'HHmm} 替换为 "20231023T1500"
                            assert replacement != null;
                            matcher.appendReplacement(result, replacement);
                        }
                        matcher.appendTail(result);
                        LOG.i("echo-shiyiurl:"+shiyiUrl);
                        if(shiyiUrl.endsWith("&"))shiyiUrl=shiyiUrl.substring(0, shiyiUrl.length() - 1);
                        shiyiUrl += result.toString();
                    }else {
                        if (shiyiUrl.indexOf("?") <= 0) {
                            shiyiUrl += "?playseek=" + shiyi_time;
                        } else if (shiyiUrl.indexOf("playseek") > 0) {
                            shiyiUrl = shiyiUrl.replaceAll("playseek=(.*)", "playseek=" + shiyi_time);
                        } else {
                            shiyiUrl += "&playseek=" + shiyi_time;
                        }
                        Log.d("PLTV播放地址", "playUrl   " + shiyiUrl);
                    }

                    LOG.i("echo-回看地址playUrl :"+ shiyiUrl);
                    playUrl = shiyiUrl;
                    if(liveWebHeader()!=null)LOG.i("echo-liveWebHeader :"+ liveWebHeader().toString());
                    mVideoView.setUrl(playUrl,liveWebHeader());
                    mVideoView.start();
                    epgListAdapter.setShiyiSelection(position, true,timeFormat.format(date));
                    epgListAdapter.notifyDataSetChanged();
                    mRightEpgList.setSelectedPosition(position);
                    mRightEpgList.post(new Runnable() {
                        @Override
                        public void run() {
                            mRightEpgList.smoothScrollToPosition(position);
                        }
                    });
                    shiyi_time_c = (int)getTime(formatDate.format(nowday) +" " + selectedData.start + ":" +"00", formatDate.format(nowday) +" " + selectedData.end + ":" +"00");
                    ViewGroup.LayoutParams lp =  iv_play.getLayoutParams();
                    lp.width=videoHeight/7;
                    lp.height=videoHeight/7;
                    sBar = (SeekBar) findViewById(R.id.pb_progressbar);
                    sBar.setMax(shiyi_time_c*1000);
                    sBar.setProgress((int)  mVideoView.getCurrentPosition());
                    // long dd = mVideoView.getDuration();
                    tv_currentpos.setText(durationToString((int)mVideoView.getCurrentPosition()));
                    tv_duration.setText(durationToString(shiyi_time_c*1000));
                    showProgressBars(true);
                    isBack = true;
                }
            }
        });
    }
    //laoda 生成7天回放日期列表数据
    private void initDayList() {
        liveDayList.clear();
//        Date firstday = new Date(nowday.getTime() - 2 * 24 * 60 * 60 * 1000);
//        for (int i = 0; i < 1; i++) {
//            LiveDayListGroup daylist = new LiveDayListGroup();
//            Date newday= new Date(firstday.getTime() + i * 24 * 60 * 60 * 1000);
//            String day = formatDate1.format(newday);
//            LOG.i("echo-date"+day);
//            daylist.setGroupIndex(i);
//            daylist.setGroupName(day);
//            liveDayList.add(daylist);
//        }

        LiveDayListGroup daylist = new LiveDayListGroup();
        Date newday= new Date((nowday.getTime()));
        String day = formatDate1.format(newday);
        LOG.i("echo-date"+day);
        daylist.setGroupIndex(0);
        daylist.setGroupName(day);
        liveDayList.add(daylist);
    }
    //kens 7天回放数据绑定和展示
    private void initEpgDateView() {
        mEpgDateGridView.setHasFixedSize(true);
        mEpgDateGridView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 1, false));
        liveEpgDateAdapter = new LiveEpgDateAdapter();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat datePresentFormat = new SimpleDateFormat("MM-dd");
        calendar.add(Calendar.DAY_OF_MONTH, 0);
        for (int i = 0; i < 1; i++) {
            Date dateIns = calendar.getTime();
            LiveEpgDate epgDate = new LiveEpgDate();
            epgDate.setIndex(i);
            epgDate.setDatePresented(datePresentFormat.format(dateIns));
            epgDate.setDateParamVal(dateIns);
            liveEpgDateAdapter.addData(epgDate);
//            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        mEpgDateGridView.setAdapter(liveEpgDateAdapter);
        mEpgDateGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mHandler.removeCallbacks(mHideChannelListRun);
                mHandler.postDelayed(mHideChannelListRun, postTimeout);
            }
        });

//        //电视
//        mEpgDateGridView.setOnItemListener(new TvRecyclerView.OnItemListener() {
//            @Override
//            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
//                liveEpgDateAdapter.setFocusedIndex(-1);
//            }
//
//            @Override
//            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
//                mHandler.removeCallbacks(mHideChannelListRun);
//                mHandler.postDelayed(mHideChannelListRun, postTimeout);
//                liveEpgDateAdapter.setFocusedIndex(position);
//            }
//
//            @Override
//            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
//                mHandler.removeCallbacks(mHideChannelListRun);
//                mHandler.postDelayed(mHideChannelListRun, postTimeout);
//                liveEpgDateAdapter.setSelectedIndex(position);
//                getEpg(liveEpgDateAdapter.getData().get(position).getDateParamVal());
//            }
//        });
//
//        //手机/模拟器
//        liveEpgDateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                FastClickCheckUtil.check(view);
//                mHandler.removeCallbacks(mHideChannelListRun);
//                mHandler.postDelayed(mHideChannelListRun, postTimeout);
//                liveEpgDateAdapter.setSelectedIndex(position);
//                getEpg(liveEpgDateAdapter.getData().get(position).getDateParamVal());
//            }
//        });
        liveEpgDateAdapter.setSelectedIndex(0);
        mEpgDateGridView.setVisibility(View.GONE);
    }



    private void initVideoView() {
        LiveController controller = new LiveController(this);
        controller.setListener(new LiveController.LiveControlListener() {
            @Override
            public boolean singleTap() {
                showChannelList();
                return true;
            }

            @Override
            public void longPress() {
                if(isBack){  //手机换源和显示时移控制栏
                    showProgressBars(true);
                }else{
                    showSettingGroup();
                }
            }

            @Override
            public void playStateChanged(int playState) {
                mHandler.removeCallbacks(mConnectTimeoutChangeSourceRun);
                switch (playState) {
                    case VideoView.STATE_IDLE:
                        // 空闲状态：播放器处于空闲，尚未开始播放。一般不需要自动换源。
                    case VideoView.STATE_PAUSED:
                        // 暂停状态：播放被暂停，通常是用户操作，不触发自动换源
                        break;
                    case VideoView.STATE_PREPARED:
                        // 准备就绪：播放器已经加载好媒体数据，但尚未开始播放。
                    case VideoView.STATE_BUFFERED:
                    case VideoView.STATE_PLAYING:
                        // 播放状态：当播放器缓冲完成或正在正常播放时，表明当前源是可用的，
                        currentLiveChangeSourceTimes = 0;
                        break;
                    case VideoView.STATE_ERROR:
                    case VideoView.STATE_PLAYBACK_COMPLETED:
                        // 错误或播放结束状态：播放器遇到错误或播放完毕时，
                        // 启动自动换源任务，等待3秒后尝试切换至备选源
                        mHandler.postDelayed(mConnectTimeoutChangeSourceRun, 3500);
                        break;
                    case VideoView.STATE_PREPARING:
                    case VideoView.STATE_BUFFERING:
                        // 正在准备或缓冲状态：表示当前源正在加载中
                        mHandler.postDelayed(mConnectTimeoutChangeSourceRun, (Hawk.get(HawkConfig.LIVE_CONNECT_TIMEOUT, 1) + 1) * 5000L);
                        break;
                    default:
                        LOG.i("echo-Unexpected live_play state: " + playState);
                        break;
                }
            }

            @Override
            public void changeSource(int direction) {
                if (direction > 0)
                    if(isBack){  //手机换源和显示时移控制栏
                        showProgressBars(true);
                    }else{
                        playNextSource();
                    }
                else
                    playPreSource();
            }
        });
        controller.setCanChangePosition(false);
        controller.setEnableInNormal(true);
        controller.setGestureEnabled(true);
        controller.setDoubleTapTogglePlayEnabled(false);
        mVideoView.setVideoController(controller);
        mVideoView.setProgressManager(null);
    }

    private Runnable mConnectTimeoutChangeSourceRun = new Runnable() {
        @Override
        public void run() {
            currentLiveChangeSourceTimes++;
            if (currentLiveChannelItem.getSourceNum() == currentLiveChangeSourceTimes) {
                currentLiveChangeSourceTimes = 0;
                Integer[] groupChannelIndex = getNextChannel(Hawk.get(HawkConfig.LIVE_CHANNEL_REVERSE, false) ? -1 : 1);
                playChannel(groupChannelIndex[0], groupChannelIndex[1], false);
            } else {
                playNextSource();
            }
        }
    };

    private void initChannelGroupView() {
        mChannelGroupView.setHasFixedSize(true);
        mChannelGroupView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 1, false));

        liveChannelGroupAdapter = new LiveChannelGroupAdapter();
        mChannelGroupView.setAdapter(liveChannelGroupAdapter);
        mChannelGroupView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mHandler.removeCallbacks(mHideChannelListRun);
                mHandler.postDelayed(mHideChannelListRun, postTimeout);
            }
        });

        //电视
        mChannelGroupView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                selectChannelGroup(position, true, -1);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                if (isNeedInputPassword(position)) {
                    showPasswordDialog(position, -1);
                }
            }
        });

        //手机/模拟器
        liveChannelGroupAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                selectChannelGroup(position, false, -1);
            }
        });
    }

    private void selectChannelGroup(int groupIndex, boolean focus, int liveChannelIndex) {
        mLastChannelGroupIndex=groupIndex;
        if (focus) {
            liveChannelGroupAdapter.setFocusedGroupIndex(groupIndex);
            liveChannelItemAdapter.setFocusedChannelIndex(-1);
        }
        if ((groupIndex > -1 && groupIndex != liveChannelGroupAdapter.getSelectedGroupIndex()) || isNeedInputPassword(groupIndex)) {
            liveChannelGroupAdapter.setSelectedGroupIndex(groupIndex);
            if (isNeedInputPassword(groupIndex)) {
                showPasswordDialog(groupIndex, liveChannelIndex);
                return;
            }
            loadChannelGroupDataAndPlay(groupIndex, liveChannelIndex);
        }
        if (tvLeftChannelListLayout.getVisibility() == View.VISIBLE) {
            mHandler.removeCallbacks(mHideChannelListRun);
            mHandler.postDelayed(mHideChannelListRun, postTimeout);
        }
    }

    private void initLiveChannelView() {
        mLiveChannelView.setHasFixedSize(true);
        mLiveChannelView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 1, false));

        liveChannelItemAdapter = new LiveChannelItemAdapter();
        mLiveChannelView.setAdapter(liveChannelItemAdapter);
        mLiveChannelView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mHandler.removeCallbacks(mHideChannelListRun);
                mHandler.postDelayed(mHideChannelListRun, postTimeout);
            }
        });

        //电视
        mLiveChannelView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                if (position < 0) return;
                liveChannelGroupAdapter.setFocusedGroupIndex(-1);
                liveChannelItemAdapter.setFocusedChannelIndex(position);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                clickLiveChannel(position);
            }
        });

        //手机/模拟器
        liveChannelItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                liveChannelItemAdapter.setSelectedChannelIndex(position);
                clickLiveChannel(position);
            }
        });
    }

    private void clickLiveChannel(int position) {
        if (tvLeftChannelListLayout.getVisibility() == View.VISIBLE) {
            mHandler.removeCallbacks(mHideChannelListRun);
            mHandler.postDelayed(mHideChannelListRun, postTimeout);
        }
        playChannel(liveChannelGroupAdapter.getSelectedGroupIndex(), position, false);
    }

    private void initSettingGroupView() {
        mSettingGroupView.setHasFixedSize(true);
        mSettingGroupView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 1, false));

        liveSettingGroupAdapter = new LiveSettingGroupAdapter();
        mSettingGroupView.setAdapter(liveSettingGroupAdapter);
        mSettingGroupView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mHandler.removeCallbacks(mHideSettingLayoutRun);
                mHandler.postDelayed(mHideSettingLayoutRun, postTimeout);
            }
        });

        //电视
        mSettingGroupView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                selectSettingGroup(position, true);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
            }
        });

        //手机/模拟器
        liveSettingGroupAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                selectSettingGroup(position, false);
            }
        });
    }

    private void selectSettingGroup(int position, boolean focus) {
        if (!isCurrentLiveChannelValid()) return;
        if (focus) {
            liveSettingGroupAdapter.setFocusedGroupIndex(position);
            liveSettingItemAdapter.setFocusedItemIndex(-1);
        }
        if (position == liveSettingGroupAdapter.getSelectedGroupIndex() || position < -1)
            return;

        liveSettingGroupAdapter.setSelectedGroupIndex(position);
        liveSettingItemAdapter.setNewData(liveSettingGroupList.get(position).getLiveSettingItems());

        switch (position) {
            case 0:
                liveSettingItemAdapter.selectItem(currentLiveChannelItem.getSourceIndex(), true, false);
                break;
            case 1:
                liveSettingItemAdapter.selectItem(livePlayerManager.getLivePlayerScale(), true, true);
                break;
            case 2:
                liveSettingItemAdapter.selectItem(livePlayerManager.getLivePlayerType(), true, true);
                break;
        }
        int scrollToPosition = liveSettingItemAdapter.getSelectedItemIndex();
        if (scrollToPosition < 0) scrollToPosition = 0;
        mSettingItemView.scrollToPosition(scrollToPosition);
        mHandler.removeCallbacks(mHideSettingLayoutRun);
        mHandler.postDelayed(mHideSettingLayoutRun, postTimeout);
    }

    private void initSettingItemView() {
        mSettingItemView.setHasFixedSize(true);
        mSettingItemView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 1, false));

        liveSettingItemAdapter = new LiveSettingItemAdapter();
        mSettingItemView.setAdapter(liveSettingItemAdapter);
        mSettingItemView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mHandler.removeCallbacks(mHideSettingLayoutRun);
                mHandler.postDelayed(mHideSettingLayoutRun, postTimeout);
            }
        });

        //电视
        mSettingItemView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                if (position < 0) return;
                liveSettingGroupAdapter.setFocusedGroupIndex(-1);
                liveSettingItemAdapter.setFocusedItemIndex(position);
                mHandler.removeCallbacks(mHideSettingLayoutRun);
                mHandler.postDelayed(mHideSettingLayoutRun, postTimeout);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                clickSettingItem(position);
            }
        });

        //手机/模拟器
        liveSettingItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                clickSettingItem(position);
            }
        });
    }

    private void clickSettingItem(int position) {
        int settingGroupIndex = liveSettingGroupAdapter.getSelectedGroupIndex();
        if (settingGroupIndex < 4) {
            if (position == liveSettingItemAdapter.getSelectedItemIndex())
                return;
            liveSettingItemAdapter.selectItem(position, true, true);
        }
        switch (settingGroupIndex) {
            case 0://线路切换
                currentLiveChannelItem.setSourceIndex(position);
                playChannel(currentChannelGroupIndex, currentLiveChannelIndex,true);
                break;
            case 1://画面比例
                Hawk.put(HawkConfig.PLAY_SCALE, position); // 调整画面比例后全部节目生效
                Hawk.put(HawkConfig.GLOBAL_PLAY_SCALE, position);
                livePlayerManager.changeLivePlayerScale(mVideoView, position, currentLiveChannelItem.getChannelName());
                break;
            case 2://播放解码
                mVideoView.release();
                livePlayerManager.changeLivePlayerType(mVideoView, position, currentLiveChannelItem.getChannelName());
                mVideoView.setUrl(currentLiveChannelItem.getUrl(),liveWebHeader());
                mVideoView.start();
                break;
            case 3://超时换源
                Hawk.put(HawkConfig.LIVE_CONNECT_TIMEOUT, position);
                break;
            case 4://偏好设置
                boolean select = false;
                switch (position) {
                    case 0:
                        select = !Hawk.get(HawkConfig.LIVE_SHOW_TIME, false);
                        Hawk.put(HawkConfig.LIVE_SHOW_TIME, select);
                        showTime();
                        break;
                    case 1:
                        select = !Hawk.get(HawkConfig.LIVE_SHOW_NET_SPEED, false);
                        Hawk.put(HawkConfig.LIVE_SHOW_NET_SPEED, select);
                        showNetSpeed();
                        break;
                    case 2:
                        select = !Hawk.get(HawkConfig.LIVE_CHANNEL_REVERSE, false);
                        Hawk.put(HawkConfig.LIVE_CHANNEL_REVERSE, select);
                        break;
                    case 3:
                        select = !Hawk.get(HawkConfig.LIVE_CROSS_GROUP, false);
                        Hawk.put(HawkConfig.LIVE_CROSS_GROUP, select);
                        break;
                    case 4:
                        select = !Hawk.get(HawkConfig.IS_GLOBAL_SCALE, false);
                        Hawk.put(HawkConfig.IS_GLOBAL_SCALE, select);
                        break;
                }
                liveSettingItemAdapter.selectItem(position, select, false);
                break;
            /*
            case 5:// 直播地址
                switch (position) {
                    case 0:

                        ApiDialog dlg = new ApiDialog(LivePlayActivity.this);
                        EventBus.getDefault().register(dlg);
                        dlg.setOnListener(url -> {
                            if (url.contains("live-")) {
                                try {
                                    url = url.replaceAll("live-", "");
                                    url = Base64.encodeToString(url.getBytes("UTF-8"), Base64.DEFAULT | Base64.URL_SAFE | Base64.NO_WRAP);
                                    url = "http://127.0.0.1:9978/proxy?do=live&type=txt&ext=" + url;
                                    loadProxyLives(url);
                                    // reload();
                                } catch (UnsupportedEncodingException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                        dlg.setOnDismissListener(dialog1 -> {
                            this.hideSysBar();
                            EventBus.getDefault().unregister(dialog1);
                        });
                        dlg.show();
                        break;
                    case 1:

                        // takagen99 : Added Live History list selection - 直播列表
                        ArrayList<String> liveHistory = Hawk.get(HawkConfig.LIVE_HISTORY, new ArrayList<String>());
                        if (liveHistory.isEmpty())
                            return;
                        String current = Hawk.get(HawkConfig.LIVE_URL, "");
                        int idx = 0;
                        if (liveHistory.contains(current))
                            idx = liveHistory.indexOf(current);
                        ApiHistoryDialog dialog = new ApiHistoryDialog(LivePlayActivity.this);
                        dialog.setTip(getString(R.string.dia_history_live));
                        dialog.setAdapter(new ApiHistoryDialogAdapter.SelectDialogInterface() {
                            @Override
                            public void click(String liveURL) {
                                Hawk.put(HawkConfig.LIVE_URL, liveURL);
                                try {
                                    liveURL = Base64.encodeToString(liveURL.getBytes("UTF-8"), Base64.DEFAULT | Base64.URL_SAFE | Base64.NO_WRAP);
                                    liveURL = "http://127.0.0.1:9978/proxy?do=live&type=txt&ext=" + liveURL;
                                    loadProxyLives(liveURL);
                                    // reload();
                                } catch (Throwable th) {
                                    th.printStackTrace();
                                }
                                dialog.dismiss();
                            }

                            @Override
                            public void del(String value, ArrayList<String> data) {
                                Hawk.put(HawkConfig.LIVE_HISTORY, data);
                            }
                        }, liveHistory, idx);
                        dialog.show();
                        break;
                }
                break;
                */
            case 5://多源切换
                //TODO
                if (mVideoView != null) {
                    mVideoView.release();
                    mVideoView=null;
                }
                if(position==Hawk.get(HawkConfig.LIVE_GROUP_INDEX, 0))break;
                JsonArray live_groups=Hawk.get(HawkConfig.LIVE_GROUP_LIST,new JsonArray());
                JsonObject livesOBJ = live_groups.get(position).getAsJsonObject();
                liveSettingItemAdapter.selectItem(position, true, true);
                Hawk.put(HawkConfig.LIVE_GROUP_INDEX, position);
                ApiConfig.get().loadLiveApi(livesOBJ);
                recreate();
                return;
            case 6:// 退出直播 takagen99 : Added Exit Option
                switch (position) {
                    case 0:
                        finish();
                        break;
                }
                break;
        }
        mHandler.removeCallbacks(mHideSettingLayoutRun);
        mHandler.postDelayed(mHideSettingLayoutRun, postTimeout);
    }

    private void initLiveChannelList() {
        List<LiveChannelGroup> list = ApiConfig.get().getChannelGroupList();
        if (list.isEmpty()) {
            setDefaultLiveChannelList();
            return;
        }
        initLiveObj();
        if (list.size() == 1 && list.get(0).getGroupName().startsWith("http://127.0.0.1")) {
            loadProxyLives(list.get(0).getGroupName());
        } else {
            liveChannelGroupList.clear();
            liveChannelGroupList.addAll(list);
            showSuccess();
            initLiveState();
        }
    }
    public void loadProxyLives(String url) {
        try {
            Uri parsedUrl = Uri.parse(url);
            url = new String(Base64.decode(parsedUrl.getQueryParameter("ext"), Base64.DEFAULT | Base64.URL_SAFE | Base64.NO_WRAP), "UTF-8");
        } catch (Throwable th) {
            if (!url.startsWith("http://127.0.0.1")) {
                setDefaultLiveChannelList();
                return;
            }
        }
        showLoading();

        LOG.i("echo-live-url:"+url);

        if(url.contains(".py")){
            Toast.makeText(this.mContext, "不支持！", Toast.LENGTH_SHORT).show();
            /*
            if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // 权限不足时，直接设置默认播放列表
                Toast.makeText(App.getInstance(), "该源需要存储权限", Toast.LENGTH_SHORT).show();
                setDefaultLiveChannelList();
                return;
            }
            String finalUrl = url;
            Runnable waitResponse = new Runnable() {
                @Override
                public void run() {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Future<String> future = executor.submit(new Callable<String>() {
                        @Override
                        public String call() {
                            LOG.i("echo--loadProxyLives-json--");
                            Spider sp = ApiConfig.get().getPyCSP(finalUrl);
                            String json=sp.liveContent(finalUrl);
                            LOG.i("echo--loadProxyLives-json--"+json);
                            return json;
                        }
                    });
                    String sortJson = null;
                    try {
                        sortJson = future.get(10, TimeUnit.SECONDS);
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        future.cancel(true);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    } finally {
                        if (sortJson==null || sortJson.isEmpty()) {
                            // 频道列表为空时，使用默认播放列表
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    setDefaultLiveChannelList();
                                }
                            });
                            return;
                        }
                        LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> linkedHashMap = new LinkedHashMap<>();
                        TxtSubscribe.parse(linkedHashMap, sortJson);
                        JsonArray livesArray = TxtSubscribe.live2JsonArray(linkedHashMap);

                        ApiConfig.get().loadLives(livesArray);
                        List<LiveChannelGroup> list = ApiConfig.get().getChannelGroupList();
                        if (list.isEmpty()) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    setDefaultLiveChannelList();
                                }
                            });
                            return;
                        }
                        liveChannelGroupList.clear();
                        liveChannelGroupList.addAll(list);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                LivePlayActivity.this.showSuccess();
                                initLiveState();
                            }
                        });
                        try {
                            executor.shutdown();
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                    }
                }
            };
            Executors.newSingleThreadExecutor().execute(waitResponse);
            */
        }
        else {
            OkGo.<String>get(url).execute(new AbsCallback<String>() {

                @Override
                public String convertResponse(okhttp3.Response response) throws Throwable {
                    assert response.body() != null;
                    return response.body().string();
                }

                @Override
                public void onSuccess(Response<String> response) {
                    LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> linkedHashMap = new LinkedHashMap<>();
                    TxtSubscribe.parse(linkedHashMap, response.body());
                    JsonArray livesArray = TxtSubscribe.live2JsonArray(linkedHashMap);

                    ApiConfig.get().loadLives(livesArray);
                    List<LiveChannelGroup> list = ApiConfig.get().getChannelGroupList();
                    if (list.isEmpty()) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                setDefaultLiveChannelList();
                            }
                        });
                        return;
                    }
                    liveChannelGroupList.clear();
                    liveChannelGroupList.addAll(list);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LivePlayActivity.this.showSuccess();
                            initLiveState();
                        }
                    });
                }

                @Override
                public void onError(Response<String> response) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setDefaultLiveChannelList();
                        }
                    });
                }
            });
        }
    }

    private void initLiveState() {
        String lastChannelName = Hawk.get(HawkConfig.LIVE_CHANNEL, "");

        int lastChannelGroupIndex = -1;
        int lastLiveChannelIndex = -1;
        for (LiveChannelGroup liveChannelGroup : liveChannelGroupList) {
            for (LiveChannelItem liveChannelItem : liveChannelGroup.getLiveChannels()) {
                if (liveChannelItem.getChannelName().equals(lastChannelName)) {
                    lastChannelGroupIndex = liveChannelGroup.getGroupIndex();
                    lastLiveChannelIndex = liveChannelItem.getChannelIndex();
                    break;
                }
            }
            if (lastChannelGroupIndex != -1) break;
        }
        if (lastChannelGroupIndex == -1) {
            lastChannelGroupIndex = getFirstNoPasswordChannelGroup();
            if (lastChannelGroupIndex == -1)
                lastChannelGroupIndex = 0;
            lastLiveChannelIndex = 0;
        }

        livePlayerManager.init(mVideoView);
        showTime();
        showNetSpeed();
        tvLeftChannelListLayout.setVisibility(View.INVISIBLE);
        tvRightSettingLayout.setVisibility(View.INVISIBLE);

        liveChannelGroupAdapter.setNewData(liveChannelGroupList);
        selectChannelGroup(lastChannelGroupIndex, false, lastLiveChannelIndex);
    }

    private boolean isListOrSettingLayoutVisible() {
        return tvLeftChannelListLayout.getVisibility() == View.VISIBLE || tvRightSettingLayout.getVisibility() == View.VISIBLE;
    }

    private void initLiveSettingGroupList() {
        liveSettingGroupList=ApiConfig.get().getLiveSettingGroupList();
        try {
            liveSettingGroupList.get(3).getLiveSettingItems().get(Hawk.get(HawkConfig.LIVE_CONNECT_TIMEOUT, 1)).setItemSelected(true);
            liveSettingGroupList.get(4).getLiveSettingItems().get(0).setItemSelected(Hawk.get(HawkConfig.LIVE_SHOW_TIME, false));
            liveSettingGroupList.get(4).getLiveSettingItems().get(1).setItemSelected(Hawk.get(HawkConfig.LIVE_SHOW_NET_SPEED, false));
            liveSettingGroupList.get(4).getLiveSettingItems().get(2).setItemSelected(Hawk.get(HawkConfig.LIVE_CHANNEL_REVERSE, false));
            liveSettingGroupList.get(4).getLiveSettingItems().get(3).setItemSelected(Hawk.get(HawkConfig.LIVE_CROSS_GROUP, false));
            liveSettingGroupList.get(4).getLiveSettingItems().get(4).setItemSelected(Hawk.get(HawkConfig.IS_GLOBAL_SCALE, false));
            liveSettingGroupList.get(5).getLiveSettingItems().get(Hawk.get(HawkConfig.LIVE_GROUP_INDEX, 0)).setItemSelected(true);
        }
        catch (IndexOutOfBoundsException ignored) {
        }
    }

    private void loadCurrentSourceList() {
        ArrayList<String> currentSourceNames = currentLiveChannelItem.getChannelSourceNames();
        ArrayList<LiveSettingItem> liveSettingItemList = new ArrayList<>();
        for (int j = 0; j < currentSourceNames.size(); j++) {
            LiveSettingItem liveSettingItem = new LiveSettingItem();
            liveSettingItem.setItemIndex(j);
            liveSettingItem.setItemName(currentSourceNames.get(j));
            liveSettingItemList.add(liveSettingItem);
        }
        liveSettingGroupList.get(0).setLiveSettingItems(liveSettingItemList);
    }

    void showTime() {
        if (Hawk.get(HawkConfig.LIVE_SHOW_TIME, false)) {
            mHandler.post(mUpdateTimeRun);
            tvTime.setVisibility(View.VISIBLE);
        } else {
            mHandler.removeCallbacks(mUpdateTimeRun);
            tvTime.setVisibility(View.GONE);
        }
    }

    private Runnable mUpdateTimeRun = new Runnable() {
        @Override
        public void run() {
            Date day=new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
            tvTime.setText(df.format(day));
            mHandler.postDelayed(this, 1000);
        }
    };

    private void showNetSpeed() {
//        tv_right_top_tipnetspeed.setVisibility(View.VISIBLE);
        if (Hawk.get(HawkConfig.LIVE_SHOW_NET_SPEED, false)) {
            mHandler.post(mUpdateNetSpeedRun);
            tvNetSpeed.setVisibility(View.VISIBLE);
        } else {
            mHandler.removeCallbacks(mUpdateNetSpeedRun);
            tvNetSpeed.setVisibility(View.GONE);
        }
    }

    private Runnable mUpdateNetSpeedRun = new Runnable() {
        @Override
        public void run() {
            if (mVideoView == null) return;
            String speed = PlayerHelper.getDisplaySpeed(mVideoView.getTcpSpeed(),true);
            tvNetSpeed.setText(speed);
//            tv_right_top_tipnetspeed.setText(speed);
            mHandler.postDelayed(this, 1000);
        }
    };

    private void showPasswordDialog(int groupIndex, int liveChannelIndex) {
        if (tvLeftChannelListLayout.getVisibility() == View.VISIBLE)
            mHandler.removeCallbacks(mHideChannelListRun);

        LivePasswordDialog dialog = new LivePasswordDialog(this);
        dialog.setOnListener(new LivePasswordDialog.OnListener() {
            @Override
            public void onChange(String password) {
                if (password.equals(liveChannelGroupList.get(groupIndex).getGroupPassword())) {
                    channelGroupPasswordConfirmed.add(groupIndex);
                    loadChannelGroupDataAndPlay(groupIndex, liveChannelIndex);
                } else {
                    Toast.makeText(App.getInstance(), "密码错误", Toast.LENGTH_SHORT).show();
                }

                if (tvLeftChannelListLayout.getVisibility() == View.VISIBLE)
                    mHandler.postDelayed(mHideChannelListRun, postTimeout);
            }

            @Override
            public void onCancel() {
                if (tvLeftChannelListLayout.getVisibility() == View.VISIBLE) {
                    int groupIndex = liveChannelGroupAdapter.getSelectedGroupIndex();
                    liveChannelItemAdapter.setNewData(getLiveChannels(groupIndex));
                }
            }
        });
        dialog.show();
    }

    private void loadChannelGroupDataAndPlay(int groupIndex, int liveChannelIndex) {
        liveChannelItemAdapter.setNewData(getLiveChannels(groupIndex));
        if (groupIndex == currentChannelGroupIndex) {
            if (currentLiveChannelIndex > -1)
                mLiveChannelView.scrollToPosition(currentLiveChannelIndex);
            liveChannelItemAdapter.setSelectedChannelIndex(currentLiveChannelIndex);
        }
        else {
            mLiveChannelView.scrollToPosition(0);
            liveChannelItemAdapter.setSelectedChannelIndex(-1);
        }

        if (liveChannelIndex > -1) {
            clickLiveChannel(liveChannelIndex);
            mChannelGroupView.scrollToPosition(groupIndex);
            mLiveChannelView.scrollToPosition(liveChannelIndex);
            playChannel(groupIndex, liveChannelIndex, false);
        }
    }

    private boolean isNeedInputPassword(int groupIndex) {
        return !liveChannelGroupList.get(groupIndex).getGroupPassword().isEmpty()
                && !isPasswordConfirmed(groupIndex);
    }

    private boolean isPasswordConfirmed(int groupIndex) {
        for (Integer confirmedNum : channelGroupPasswordConfirmed) {
            if (confirmedNum == groupIndex)
                return true;
        }
        return false;
    }

    private ArrayList<LiveChannelItem> getLiveChannels(int groupIndex) {
        if (!isNeedInputPassword(groupIndex)) {
            return liveChannelGroupList.get(groupIndex).getLiveChannels();
        } else {
            return new ArrayList<>();
        }
    }

    private Integer[] getNextChannel(int direction) {
        int channelGroupIndex = currentChannelGroupIndex;
        int liveChannelIndex = currentLiveChannelIndex;

        //跨选分组模式下跳过加密频道分组（遥控器上下键换台/超时换源）
        if (direction > 0) {
            liveChannelIndex++;
            if (liveChannelIndex >= getLiveChannels(channelGroupIndex).size()) {
                liveChannelIndex = 0;
                if (Hawk.get(HawkConfig.LIVE_CROSS_GROUP, false)) {
                    do {
                        channelGroupIndex++;
                        if (channelGroupIndex >= liveChannelGroupList.size())
                            channelGroupIndex = 0;
                    } while (!liveChannelGroupList.get(channelGroupIndex).getGroupPassword().isEmpty() || channelGroupIndex == currentChannelGroupIndex);
                }
            }
        } else {
            liveChannelIndex--;
            if (liveChannelIndex < 0) {
                if (Hawk.get(HawkConfig.LIVE_CROSS_GROUP, false)) {
                    do {
                        channelGroupIndex--;
                        if (channelGroupIndex < 0)
                            channelGroupIndex = liveChannelGroupList.size() - 1;
                    } while (!liveChannelGroupList.get(channelGroupIndex).getGroupPassword().isEmpty() || channelGroupIndex == currentChannelGroupIndex);
                }
                liveChannelIndex = getLiveChannels(channelGroupIndex).size() - 1;
            }
        }

        Integer[] groupChannelIndex = new Integer[2];
        groupChannelIndex[0] = channelGroupIndex;
        groupChannelIndex[1] = liveChannelIndex;

        return groupChannelIndex;
    }

    private int getFirstNoPasswordChannelGroup() {
        for (LiveChannelGroup liveChannelGroup : liveChannelGroupList) {
            if (liveChannelGroup.getGroupPassword().isEmpty())
                return liveChannelGroup.getGroupIndex();
        }
        return -1;
    }

    private boolean isCurrentLiveChannelValid() {
        if (currentLiveChannelItem == null) {
            Toast.makeText(App.getInstance(), "请先选择频道", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //计算两个时间相差的秒数
    public static long getTime(String startTime, String endTime)  {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long eTime = 0;
        try {
            eTime = df.parse(endTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long sTime = 0;
        try {
            sTime = df.parse(startTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = (eTime - sTime) / 1000;
        return diff;
    }
    private  String durationToString(int duration) {
        String result = "";
        int dur = duration / 1000;
        int hour=dur/3600;
        int min = (dur / 60) % 60;
        int sec = dur % 60;
        if(hour>0){
            if (min > 9) {
                if (sec > 9) {
                    result =hour+":"+ min + ":" + sec;
                } else {
                    result =hour+":"+ min + ":0" + sec;
                }
            } else {
                if (sec > 9) {
                    result =hour+":"+ "0" + min + ":" + sec;
                } else {
                    result = hour+":"+"0" + min + ":0" + sec;
                }
            }
        }else{
            if (min > 9) {
                if (sec > 9) {
                    result = min + ":" + sec;
                } else {
                    result = min + ":0" + sec;
                }
            } else {
                if (sec > 9) {
                    result ="0" + min + ":" + sec;
                } else {
                    result = "0" + min + ":0" + sec;
                }
            }
        }
        return result;
    }
    public void showProgressBars( boolean show){

        sBar.requestFocus();
        if(show){
            ll_right_top_huikan.setVisibility(View.VISIBLE);
            backcontroller.setVisibility(View.VISIBLE);
            ll_epg.setVisibility(View.GONE);
        }else{
            backcontroller.setVisibility(View.GONE);
            ll_right_top_huikan.setVisibility(View.GONE);
            if(!tip_epg1.getText().equals("暂无信息")){
                ll_epg.setVisibility(View.VISIBLE);
            }
        }



        iv_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mVideoView.start();
                iv_play.setVisibility(View.INVISIBLE);
                countDownTimer.start();
                iv_playpause.setBackground(ContextCompat.getDrawable(LivePlayActivity.context, R.drawable.vod_pause));
            }
        });

        iv_playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(mVideoView.isPlaying()){
                    mVideoView.pause();
                    countDownTimer.cancel();
                    iv_play.setVisibility(View.VISIBLE);
                    iv_playpause.setBackground(ContextCompat.getDrawable(LivePlayActivity.context, R.drawable.icon_play));
                }else{
                    mVideoView.start();
                    iv_play.setVisibility(View.INVISIBLE);
                    countDownTimer.start();
                    iv_playpause.setBackground(ContextCompat.getDrawable(LivePlayActivity.context, R.drawable.vod_pause));
                }
            }
        });
        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onStopTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromuser) {
                if(fromuser){
                    if(countDownTimer!=null){
                        mVideoView.seekTo(progress);
                        countDownTimer.cancel();
                        countDownTimer.start();
                    }
                }
            }
        });
        sBar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int keycode, KeyEvent event) {
                if(event.getAction()==KeyEvent.ACTION_DOWN){
                    if(keycode==KeyEvent.KEYCODE_DPAD_CENTER||keycode==KeyEvent.KEYCODE_ENTER){
                        if(mVideoView.isPlaying()){
                            mVideoView.pause();
                            countDownTimer.cancel();
                            iv_play.setVisibility(View.VISIBLE);
                            iv_playpause.setBackground(ContextCompat.getDrawable(LivePlayActivity.context, R.drawable.icon_play));
                        }else{
                            mVideoView.start();
                            iv_play.setVisibility(View.INVISIBLE);
                            countDownTimer.start();
                            iv_playpause.setBackground(ContextCompat.getDrawable(LivePlayActivity.context, R.drawable.vod_pause));
                        }
                    }
                }
                return false;
            }
        });
        if(mVideoView.isPlaying()){
            iv_play.setVisibility(View.INVISIBLE);
            iv_playpause.setBackground(ContextCompat.getDrawable(LivePlayActivity.context, R.drawable.vod_pause));
        }else{
            iv_play.setVisibility(View.VISIBLE);
            iv_playpause.setBackground(ContextCompat.getDrawable(LivePlayActivity.context, R.drawable.icon_play));
        }
        if(countDownTimer3==null){
            countDownTimer3 = new CountDownTimer(postTimeout, 1000) {

                @Override
                public void onTick(long arg0) {

                    if(mVideoView != null){
                        sBar.setProgress((int) mVideoView.getCurrentPosition());
                        tv_currentpos.setText(durationToString((int) mVideoView.getCurrentPosition()));
                    }

                }

                @Override
                public void onFinish() {
                    if(backcontroller.getVisibility() == View.VISIBLE){
                        backcontroller.setVisibility(View.GONE);
                    }
                }
            };
        }else{
            countDownTimer3.cancel();
        }
        countDownTimer3.start();
    }

    /**
     * 当播放列表为空或加载失败时，设置一个默认的播放列表，保证播放界面不会崩溃
     */
    private void setDefaultLiveChannelList() {
        liveChannelGroupList.clear();
        // 创建默认直播分组
        LiveChannelGroup defaultGroup = new LiveChannelGroup();
        defaultGroup.setGroupIndex(0);
        defaultGroup.setGroupName("default group");
        defaultGroup.setGroupPassword("");
        LiveChannelItem defaultChannel = new LiveChannelItem();
        defaultChannel.setChannelName("default channel");
        defaultChannel.setChannelIndex(0);
        defaultChannel.setChannelNum(1);
        ArrayList<String> defaultSourceNames = new ArrayList<>();
        ArrayList<String> defaultSourceUrls = new ArrayList<>();
        defaultSourceNames.add("default source");
        defaultSourceUrls.add("http://default.play.url/stream");
        defaultChannel.setChannelSourceNames(defaultSourceNames);
        defaultChannel.setChannelUrls(defaultSourceUrls);
        // 将默认频道添加到分组内
        ArrayList<LiveChannelItem> channels = new ArrayList<>();
        channels.add(defaultChannel);
        defaultGroup.setLiveChannels(channels);
        // 添加分组到全局列表
        liveChannelGroupList.add(defaultGroup);
        showSuccess();
        initLiveState();
    }

}