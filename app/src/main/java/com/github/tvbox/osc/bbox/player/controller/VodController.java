package com.github.tvbox.osc.bbox.player.controller;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.tvbox.osc.bbox.R;
import com.github.tvbox.osc.bbox.api.ApiConfig;
import com.github.tvbox.osc.bbox.bean.IJKCode;
import com.github.tvbox.osc.bbox.bean.ParseBean;
import com.github.tvbox.osc.bbox.subtitle.widget.SimpleSubtitleView;
import com.github.tvbox.osc.bbox.ui.activity.DetailActivity;
import com.github.tvbox.osc.bbox.ui.adapter.ParseAdapter;
import com.github.tvbox.osc.bbox.ui.adapter.SelectDialogAdapter;
import com.github.tvbox.osc.bbox.ui.dialog.SelectDialog;
import com.github.tvbox.osc.bbox.util.*;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.PlayerUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static xyz.doikki.videoplayer.util.PlayerUtils.stringForTime;

public class VodController extends BaseController {
    public VodController(@NonNull @NotNull Context context) {
        super(context);
        mHandlerCallback = new HandlerCallback() {
            @Override
            public void callback(Message msg) {
                switch (msg.what) {
                    case 1000: { // seek 刷新
                        mProgressRoot.setVisibility(VISIBLE);
                        break;
                    }
                    case 1001: { // seek 关闭
                        mProgressRoot.setVisibility(GONE);
                        break;
                    }
                    case 1002: { // 显示底部菜单
                        mBottomRoot.setVisibility(VISIBLE);
                        mTopRoot1.setVisibility(VISIBLE);
                        mTopRoot2.setVisibility(VISIBLE);
                        mPlayTitle.setVisibility(GONE);
                        mNextBtn.requestFocus();
                        break;
                    }
                    case 1003: { // 隐藏底部菜单
                        mBottomRoot.setVisibility(GONE);
                        mTopRoot1.setVisibility(GONE);
                        mTopRoot2.setVisibility(GONE);
                        break;
                    }
                    case 1004: { // 设置速度
                        if (isInPlaybackState()) {
                            try {
                                float speed = (float) mPlayerConfig.getDouble("sp");
                                mControlWrapper.setSpeed(speed);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else
                            mHandler.sendEmptyMessageDelayed(1004, 100);
                        break;
                    }
                }
            }
        };
    }
    TextView mTvSpeedTip;
    LinearLayout mLlSpeed;
    SeekBar mSeekBar;
    TextView mCurrentTime;
    TextView mTotalTime;
    boolean mIsDragging;
    LinearLayout mProgressRoot;
    TextView mProgressText;
    ImageView mProgressIcon;
    LinearLayout mBottomRoot;
    LinearLayout mTopRoot1;
    LinearLayout mTopRoot2;
    LinearLayout mParseRoot;
    TvRecyclerView mGridView;
    TextView mPlayTitle;
    TextView mPlayPlayerText;
    TextView mPlayerSpeedText;
    TextView mPlayerScaleText;
    TextView mPlayTitle1;
    TextView mPlayLoadNetSpeedRightTop;
    LinearLayout mNextBtn;
    LinearLayout mPreBtn;
    LinearLayout mPlayerScaleBtn;
    public LinearLayout mPlayerSpeedBtn;
    LinearLayout mPlayerBtn;
    TextView mPlayerIJKBtn;
    LinearLayout mPlayerRetry;
    LinearLayout mPlayrefresh;
    public TextView mPlayerTimeStartEndText;
    public TextView mPlayerTimeStartBtn;
    public TextView mPlayerTimeSkipBtn;
    public TextView mPlayerTimeResetBtn;
    TextView mPlayPauseTime;
    TextView mPlayLoadNetSpeed;
    TextView mVideoSize;
    public SimpleSubtitleView mSubtitleView;
    LinearLayout mZimuBtn;
    LinearLayout mAudioTrackBtn;
    public LinearLayout mLandscapePortraitBtn;

    Handler myHandle;
    Runnable myRunnable;
    int myHandleSeconds = 10000;//闲置多少毫秒秒关闭底栏  默认6秒

    int videoPlayState = 0;

    private Runnable myRunnable2 = new Runnable() {
        @Override
        public void run() {
            Date date = new Date();
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            mPlayPauseTime.setText(timeFormat.format(date));
            String speed = PlayerHelper.getDisplaySpeed(mControlWrapper.getTcpSpeed());
            mPlayLoadNetSpeedRightTop.setText(speed);
            mPlayLoadNetSpeed.setText(speed);
            String width = Integer.toString(mControlWrapper.getVideoSize()[0]);
            String height = Integer.toString(mControlWrapper.getVideoSize()[1]);
            mVideoSize.setText("[ " + width + " X " + height +" ]");

            mHandler.postDelayed(this, 1000);
        }
    };





    @Override
    protected void initView() {
        super.initView();
        mPlayPlayerText = findViewById(R.id.play_player_txt);
        mPlayerSpeedText = findViewById(R.id.play_speed_text);
        mPlayerScaleText = findViewById(R.id.play_scale_text);
        mLlSpeed = findViewById(R.id.ll_speed);
        mTvSpeedTip = findViewById(R.id.tv_speed);
        mCurrentTime = findViewById(R.id.curr_time);
        mTotalTime = findViewById(R.id.total_time);
        mPlayTitle = findViewById(R.id.tv_info_name);
        mPlayTitle1 = findViewById(R.id.tv_info_name1);
        mPlayLoadNetSpeedRightTop = findViewById(R.id.tv_play_load_net_speed_right_top);
        mSeekBar = findViewById(R.id.seekBar);
        mProgressRoot = findViewById(R.id.tv_progress_container);
        mProgressIcon = findViewById(R.id.tv_progress_icon);
        mProgressText = findViewById(R.id.tv_progress_text);
        mBottomRoot = findViewById(R.id.bottom_container);
        mTopRoot1 = findViewById(R.id.tv_top_l_container);
        mTopRoot2 = findViewById(R.id.tv_top_r_container);
        mParseRoot = findViewById(R.id.parse_root);
        mGridView = findViewById(R.id.mGridView);
        mPlayerRetry = findViewById(R.id.play_retry);
        mPlayrefresh = findViewById(R.id.play_refresh);
        mNextBtn = findViewById(R.id.play_next);
        mPreBtn = findViewById(R.id.play_pre);
        mPlayerScaleBtn = findViewById(R.id.play_scale);
        mPlayerSpeedBtn = findViewById(R.id.play_speed);
        mPlayerBtn = findViewById(R.id.play_player);
        mPlayerIJKBtn = findViewById(R.id.play_ijk);
        mPlayerTimeStartEndText = findViewById(R.id.play_time_start_end_text);
        mPlayerTimeStartBtn = findViewById(R.id.play_time_start);
        mPlayerTimeSkipBtn = findViewById(R.id.play_time_end);
        mPlayerTimeResetBtn = findViewById(R.id.play_time_reset);
        mPlayPauseTime = findViewById(R.id.tv_sys_time);
        mPlayLoadNetSpeed = findViewById(R.id.tv_play_load_net_speed);
        mVideoSize = findViewById(R.id.tv_videosize);
        mSubtitleView = findViewById(R.id.subtitle_view);
        mZimuBtn = findViewById(R.id.zimu_select);
        mAudioTrackBtn = findViewById(R.id.audio_track_select);
        mLandscapePortraitBtn = findViewById(R.id.landscape_portrait);

        initSubtitleInfo();

        mTopRoot1.setOnClickListener(view -> {
            boolean showPreview = Hawk.get(HawkConfig.SHOW_PREVIEW, true);
            assert mActivity != null;
            if (showPreview) {
                mTopRoot1.setVisibility(GONE);
                mTopRoot2.setVisibility(GONE);
                mBottomRoot.setVisibility(GONE);
                mHandler.removeCallbacks(myRunnable);
                ((DetailActivity) mActivity).toggleFullPreview();
            } else {
                mActivity.finish();
            }
        });

        myHandle = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                hideBottom();
            }
        };

        mPlayPauseTime.post(new Runnable() {
            @Override
            public void run() {
                mHandler.post(myRunnable2);
            }
        });

        mGridView.setLayoutManager(new V7LinearLayoutManager(getContext(), 0, false));
        ParseAdapter parseAdapter = new ParseAdapter();
        parseAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ParseBean parseBean = parseAdapter.getItem(position);
                // 当前默认解析需要刷新
                int currentDefault = parseAdapter.getData().indexOf(ApiConfig.get().getDefaultParse());
                parseAdapter.notifyItemChanged(currentDefault);
                ApiConfig.get().setDefaultParse(parseBean);
                parseAdapter.notifyItemChanged(position);
                listener.changeParse(parseBean);
                hideBottom();
            }
        });
        mGridView.setAdapter(parseAdapter);
        parseAdapter.setNewData(ApiConfig.get().getParseBeanList());

        mParseRoot.setVisibility(VISIBLE);


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                long duration = mControlWrapper.getDuration();
                long newPosition = (duration * progress) / seekBar.getMax();
                if (mCurrentTime != null)
                    mCurrentTime.setText(stringForTime((int) newPosition));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsDragging = true;
                mControlWrapper.stopProgress();
                mControlWrapper.stopFadeOut();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myHandle.removeCallbacks(myRunnable);
                myHandle.postDelayed(myRunnable, myHandleSeconds);
                long duration = mControlWrapper.getDuration();
                long newPosition = (duration * seekBar.getProgress()) / seekBar.getMax();
                mControlWrapper.seekTo((int) newPosition);
                mIsDragging = false;
                mControlWrapper.startProgress();
                mControlWrapper.startFadeOut();
            }
        });
        mPlayerRetry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.replay(true);
                hideBottom();
            }
        });
        mPlayrefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.replay(false);
                hideBottom();
            }
        });
        mNextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.playNext(false);
                hideBottom();
            }
        });
        mPreBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.playPre();
                hideBottom();
            }
        });
        mPlayerScaleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                myHandle.removeCallbacks(myRunnable);
                myHandle.postDelayed(myRunnable, myHandleSeconds);
                try {
                    int scaleType = mPlayerConfig.getInt("sc");
                    scaleType++;
                    if (scaleType > 5)
                        scaleType = 0;
                    mPlayerConfig.put("sc", scaleType);
                    updatePlayerCfgView();
                    listener.updatePlayerCfg();
                    mControlWrapper.setScreenScaleType(scaleType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mPlayerSpeedBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                myHandle.removeCallbacks(myRunnable);
                myHandle.postDelayed(myRunnable, myHandleSeconds);
                try {
                    float speed = (float) mPlayerConfig.getDouble("sp");
                    speed += 0.25f;
                    if (speed > 3)
                        speed = 0.5f;
                    mPlayerConfig.put("sp", speed);
                    updatePlayerCfgView();
                    listener.updatePlayerCfg();
                    speed_old = speed;
                    mControlWrapper.setSpeed(speed);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mPlayerSpeedBtn.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    mPlayerConfig.put("sp", 1.0f);
                    updatePlayerCfgView();
                    listener.updatePlayerCfg();
                    speed_old = 1.0f;
                    mControlWrapper.setSpeed(1.0f);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        /* mPlayerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                myHandle.removeCallbacks(myRunnable);
                myHandle.postDelayed(myRunnable, myHandleSeconds);
                try {
                    int playerType = mPlayerConfig.getInt("pl");
                    ArrayList<Integer> exsitPlayerTypes = PlayerHelper.getExistPlayerTypes();
                    int playerTypeIdx = 0;
                    int playerTypeSize = exsitPlayerTypes.size();
                    for(int i = 0; i<playerTypeSize; i++) {
                        if (playerType == exsitPlayerTypes.get(i)) {
                            if (i == playerTypeSize - 1) {
                                playerTypeIdx = 0;
                            } else {
                                playerTypeIdx = i + 1;
                            }
                        }
                    }
                    playerType = exsitPlayerTypes.get(playerTypeIdx);
                    mPlayerConfig.put("pl", playerType);
                    updatePlayerCfgView();
                    listener.updatePlayerCfg();
                    listener.replay(false);
                    hideBottom();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mPlayerBtn.requestFocus();
                mPlayerBtn.requestFocusFromTouch();
            }
        }); */

        mPlayerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switchPlayer(view);
            }
        });
        mPlayerBtn.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                switchPlayer(view);
                return true;
            }
        });
        mPlayerIJKBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                myHandle.removeCallbacks(myRunnable);
                myHandle.postDelayed(myRunnable, myHandleSeconds);
                try {
                    String ijk = mPlayerConfig.getString("ijk");
                    List<IJKCode> codecs = ApiConfig.get().getIjkCodes();
                    for (int i = 0; i < codecs.size(); i++) {
                        if (ijk.equals(codecs.get(i).getName())) {
                            if (i >= codecs.size() - 1)
                                ijk = codecs.get(0).getName();
                            else {
                                ijk = codecs.get(i + 1).getName();
                            }
                            break;
                        }
                    }
                    mPlayerConfig.put("ijk", ijk);
                    updatePlayerCfgView();
                    listener.updatePlayerCfg();
                    listener.replay(false);
                    hideBottom();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mPlayerIJKBtn.requestFocus();
                mPlayerIJKBtn.requestFocusFromTouch();
            }
        });
//        增加播放页面片头片尾时间重置
        mPlayerTimeResetBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                myHandle.removeCallbacks(myRunnable);
                myHandle.postDelayed(myRunnable, myHandleSeconds);
                try {
                    mPlayerConfig.put("et", 0);
                    mPlayerConfig.put("st", 0);
                    updatePlayerCfgView();
                    listener.updatePlayerCfg();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mPlayerTimeStartBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                myHandle.removeCallbacks(myRunnable);
                myHandle.postDelayed(myRunnable, myHandleSeconds);
                try {
                    int current = (int) mControlWrapper.getCurrentPosition();
                    int duration = (int) mControlWrapper.getDuration();
                    if (current > duration / 2) return;
                    mPlayerConfig.put("st",current/1000);
                    updatePlayerCfgView();
                    listener.updatePlayerCfg();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mPlayerTimeStartBtn.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    mPlayerConfig.put("st", 0);
                    updatePlayerCfgView();
                    listener.updatePlayerCfg();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        mPlayerTimeSkipBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                myHandle.removeCallbacks(myRunnable);
                myHandle.postDelayed(myRunnable, myHandleSeconds);
                try {
                    int current = (int) mControlWrapper.getCurrentPosition();
                    int duration = (int) mControlWrapper.getDuration();
                    if (current < duration / 2) return;
                    mPlayerConfig.put("et", (duration - current)/1000);
                    updatePlayerCfgView();
                    listener.updatePlayerCfg();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mPlayerTimeSkipBtn.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    mPlayerConfig.put("et", 0);
                    updatePlayerCfgView();
                    listener.updatePlayerCfg();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        mZimuBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FastClickCheckUtil.check(view);
                listener.selectSubtitle();
                hideBottom();
            }
        });
        mZimuBtn.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mSubtitleView.setVisibility(View.GONE);
                mSubtitleView.destroy();
                mSubtitleView.clearSubtitleCache();
                mSubtitleView.isInternal = false;
                hideBottom();
                Toast.makeText(getContext(), "字幕已关闭", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mAudioTrackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FastClickCheckUtil.check(view);
                listener.selectAudioTrack();
                hideBottom();
            }
        });
        mLandscapePortraitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FastClickCheckUtil.check(view);
                setLandscapePortrait();
                hideBottom();
            }
        });
        // mNextBtn.setNextFocusLeftId(R.id.play_time_start);
    }

    private void switchPlayer(View view) {
        myHandle.removeCallbacks(myRunnable);
        myHandle.postDelayed(myRunnable, myHandleSeconds);
        FastClickCheckUtil.check(view);
        try {
            int playerType = mPlayerConfig.getInt("pl");
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
            dialog.setTip("请选择播放器");
            dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                @Override
                public void click(Integer value, int pos) {
                    try {
                        dialog.cancel();
                        int thisPlayType = players.get(pos);
                        if (thisPlayType != playerType) {
                            mPlayerConfig.put("pl", thisPlayType);
                            updatePlayerCfgView();
                            listener.updatePlayerCfg();
                            listener.replay(false);
                            hideBottom();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPlayerBtn.requestFocus();
                    mPlayerBtn.requestFocusFromTouch();
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void hideLiveAboutBtn() {
        if (mControlWrapper != null && mControlWrapper.getDuration() == 0) {
            mPlayerSpeedBtn.setVisibility(GONE);
            mPlayerTimeStartEndText.setVisibility(GONE);
            mPlayerTimeStartBtn.setVisibility(GONE);
            mPlayerTimeSkipBtn.setVisibility(GONE);
            // mPlayerTimeResetBtn.setVisibility(GONE);
            // mNextBtn.setNextFocusLeftId(R.id.zimu_select);
        } else {
            mPlayerSpeedBtn.setVisibility(View.VISIBLE);
            mPlayerTimeStartEndText.setVisibility(View.VISIBLE);
            mPlayerTimeStartBtn.setVisibility(View.VISIBLE);
            mPlayerTimeSkipBtn.setVisibility(View.VISIBLE);
            // mPlayerTimeResetBtn.setVisibility(View.VISIBLE);
            // mNextBtn.setNextFocusLeftId(R.id.play_time_start);
        }
    }

    public void initLandscapePortraitBtnInfo() {
        if(mControlWrapper!=null && mActivity!=null){
            UiModeManager uiModeManager = (UiModeManager) mActivity.getSystemService(Context.UI_MODE_SERVICE);
            int width = mControlWrapper.getVideoSize()[0];
            int height = mControlWrapper.getVideoSize()[1];
            double screenSqrt = ScreenUtils.getSqrt(mActivity);
            LOG.i("screenSqrt: "+screenSqrt+", width: " + width + ", height: " + height);
            if (null != uiModeManager && Configuration.UI_MODE_TYPE_TELEVISION != uiModeManager.getCurrentModeType() && screenSqrt < 10.0 && width < height) {
                mLandscapePortraitBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    void setLandscapePortrait() {
        int requestedOrientation = mActivity.getRequestedOrientation();
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE || requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT || requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    void initSubtitleInfo() {
        int subtitleTextSize = SubtitleHelper.getTextSize(mActivity);
        mSubtitleView.setTextSize(subtitleTextSize);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.player_vod_control_view;
    }

    public void showParse(boolean userJxList) {
        mParseRoot.setVisibility(userJxList ? VISIBLE : GONE);
    }

    private JSONObject mPlayerConfig = null;

    public void setPlayerConfig(JSONObject playerCfg) {
        this.mPlayerConfig = playerCfg;
        updatePlayerCfgView();
    }

    void updatePlayerCfgView() {
        try {
            int playerType = mPlayerConfig.getInt("pl");
            // mPlayPlayerText.setText(PlayerHelper.getPlayerName(playerType));
            mPlayerScaleText.setText(PlayerHelper.getScaleName(mPlayerConfig.getInt("sc")));
            mPlayerIJKBtn.setText(mPlayerConfig.getString("ijk"));
            mPlayerIJKBtn.setVisibility(playerType == 1 ? VISIBLE : GONE);
            mPlayerScaleText.setText(PlayerHelper.getScaleName(mPlayerConfig.getInt("sc")));
            mPlayerSpeedText.setText("x" + mPlayerConfig.getDouble("sp"));
            mPlayerTimeStartBtn.setText(PlayerUtils.stringForTime(mPlayerConfig.getInt("st") * 1000));
            mPlayerTimeSkipBtn.setText(PlayerUtils.stringForTime(mPlayerConfig.getInt("et") * 1000));
            mAudioTrackBtn.setVisibility((playerType == 1) ? VISIBLE : GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setTitle(String playTitleInfo) {
        mPlayTitle.setText(playTitleInfo);
        mPlayTitle1.setText(playTitleInfo);
    }

    public void setUrlTitle(String playTitleInfo) {
        mPlayTitle.setText(playTitleInfo);
    }

    public void resetSpeed() {
        skipEnd = true;
        mHandler.removeMessages(1004);
        mHandler.sendEmptyMessageDelayed(1004, 100);
    }

    public interface VodControlListener {
        void playNext(boolean rmProgress);

        void playPre();

        void prepared();

        void changeParse(ParseBean pb);

        void updatePlayerCfg();

        void replay(boolean replay);

        void errReplay();

        void selectSubtitle();

        void selectAudioTrack();
    }

    public void setListener(VodControlListener listener) {
        this.listener = listener;
    }

    private VodControlListener listener;

    private boolean skipEnd = true;

    @Override
    protected void setProgress(int duration, int position) {

        if (mIsDragging) {
            return;
        }
        super.setProgress(duration, position);
        if (skipEnd && position != 0 && duration != 0) {
            int et = 0;
            try {
                et = mPlayerConfig.getInt("et");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (et > 0 && position + (et * 1000) >= duration) {
                skipEnd = false;
                listener.playNext(true);
            }
        }
        mCurrentTime.setText(PlayerUtils.stringForTime(position));
        mTotalTime.setText(PlayerUtils.stringForTime(duration));
        if (duration > 0) {
            mSeekBar.setEnabled(true);
            int pos = (int) (position * 1.0 / duration * mSeekBar.getMax());
            mSeekBar.setProgress(pos);
        } else {
            mSeekBar.setEnabled(false);
        }
        int percent = mControlWrapper.getBufferedPercentage();
        if (percent >= 95) {
            mSeekBar.setSecondaryProgress(mSeekBar.getMax());
        } else {
            mSeekBar.setSecondaryProgress(percent * 10);
        }
    }

    private boolean simSlideStart = false;
    private int simSeekPosition = 0;
    private long simSlideOffset = 0;

    public void tvSlideStop() {
        if (!simSlideStart)
            return;
        mControlWrapper.seekTo(simSeekPosition);
        if (!mControlWrapper.isPlaying())
            mControlWrapper.start();
        simSlideStart = false;
        simSeekPosition = 0;
        simSlideOffset = 0;
    }

    public void tvSlideStart(int dir) {
        int duration = (int) mControlWrapper.getDuration();
        if (duration <= 0)
            return;
        if (!simSlideStart) {
            simSlideStart = true;
        }
        // 每次10秒
        simSlideOffset += (10000.0f * dir);
        int currentPosition = (int) mControlWrapper.getCurrentPosition();
        int position = (int) (simSlideOffset + currentPosition);
        if (position > duration) position = duration;
        if (position < 0) position = 0;
        updateSeekUI(currentPosition, position, duration);
        simSeekPosition = position;
    }

    @Override
    protected void updateSeekUI(int curr, int seekTo, int duration) {
        super.updateSeekUI(curr, seekTo, duration);
        if (seekTo > curr) {
            mProgressIcon.setImageResource(R.drawable.icon_pre);
        } else {
            mProgressIcon.setImageResource(R.drawable.icon_back);
        }
        mProgressText.setText(PlayerUtils.stringForTime(seekTo) + " / " + PlayerUtils.stringForTime(duration));
        mHandler.sendEmptyMessage(1000);
        mHandler.removeMessages(1001);
        mHandler.sendEmptyMessageDelayed(1001, 1000);
    }

    @Override
    protected void onPlayStateChanged(int playState) {
        super.onPlayStateChanged(playState);
        videoPlayState = playState;
        switch (playState) {
            case VideoView.STATE_IDLE:
                break;
            case VideoView.STATE_PLAYING:
                initLandscapePortraitBtnInfo();
                startProgress();
                break;
            case VideoView.STATE_PAUSED:
                mTopRoot1.setVisibility(GONE);
                mTopRoot2.setVisibility(GONE);
                mPlayTitle.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_ERROR:
                listener.errReplay();
                break;
            case VideoView.STATE_PREPARED:
                mPlayLoadNetSpeed.setVisibility(GONE);
                hideLiveAboutBtn();
                listener.prepared();
                break;
            case VideoView.STATE_BUFFERED:
                mPlayLoadNetSpeed.setVisibility(GONE);
                break;
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_BUFFERING:
                if(mProgressRoot.getVisibility()==GONE)mPlayLoadNetSpeed.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                listener.playNext(true);
                break;
        }
    }

    boolean isBottomVisible() {
        return mBottomRoot.getVisibility() == VISIBLE;
    }

    void showBottom() {
        mHandler.removeMessages(1003);
        mHandler.sendEmptyMessage(1002);
        mHandler.postDelayed(myRunnable, 8000);
    }

    void hideBottom() {
        mHandler.removeMessages(1002);
        mHandler.sendEmptyMessage(1003);
        mHandler.removeCallbacks(myRunnable);
    }

    @Override
    public boolean onKeyEvent(KeyEvent event) {
        myHandle.removeCallbacks(myRunnable);
        if (super.onKeyEvent(event)) {
            return true;
        }
        int keyCode = event.getKeyCode();
        int action = event.getAction();
        if (isBottomVisible()) {
            myHandle.removeCallbacks(myRunnable);
            myHandle.postDelayed(myRunnable, myHandleSeconds);
            return super.dispatchKeyEvent(event);
        }
        boolean isInPlayback = isInPlaybackState();
        if (action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (isInPlayback) {
                    tvSlideStart(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 1 : -1);
                    return true;
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (isInPlayback) {
                    togglePlay();
                    return true;
                }
//            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {  return true;// 闲置开启计时关闭透明底栏
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode== KeyEvent.KEYCODE_MENU) {
                if (!isBottomVisible()) {
                    showBottom();
                    myHandle.postDelayed(myRunnable, myHandleSeconds);
                    return true;
                }
            }
        } else if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (isInPlayback) {
                    tvSlideStop();
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }


    private boolean fromLongPress;
    private float speed_old = 1.0f;
    @Override
    public void onLongPress(MotionEvent e) {
        if (videoPlayState!=VideoView.STATE_PAUSED) {
            fromLongPress = true;
            try {
                speed_old = (float) mPlayerConfig.getDouble("sp");
                float speed = 3.0f;
                mPlayerConfig.put("sp", speed);
                updatePlayerCfgView();
                listener.updatePlayerCfg();
                mControlWrapper.setSpeed(speed);
                mLlSpeed.setVisibility(VISIBLE);
                mTvSpeedTip.setText(speed + "x >>>");
            } catch (JSONException f) {
                f.printStackTrace();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            if (fromLongPress) {
                fromLongPress =false;
                mLlSpeed.setVisibility(GONE);
                try {
                    float speed = speed_old;
                    mPlayerConfig.put("sp", speed);
                    updatePlayerCfgView();
                    listener.updatePlayerCfg();
                    mControlWrapper.setSpeed(speed);
                } catch (JSONException f) {
                    f.printStackTrace();
                }
            }
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        myHandle.removeCallbacks(myRunnable);
        if (!isBottomVisible()) {
            showBottom();
            // 闲置计时关闭
            myHandle.postDelayed(myRunnable, myHandleSeconds);
        } else {
            hideBottom();
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        if (super.onBackPressed()) {
            return true;
        }
        if (isBottomVisible()) {
            hideBottom();
            return true;
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacks(myRunnable2);
    }
}
