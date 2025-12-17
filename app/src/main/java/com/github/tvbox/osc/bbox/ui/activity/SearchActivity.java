package com.github.tvbox.osc.bbox.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.LinearLayoutManager;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.catvod.crawler.JsLoader;
import com.github.tvbox.osc.bbox.R;
import com.github.tvbox.osc.bbox.api.ApiConfig;
import com.github.tvbox.osc.bbox.base.BaseActivity;
import com.github.tvbox.osc.bbox.ui.activity.DetailActivity;
import com.github.tvbox.osc.bbox.bean.AbsXml;
import com.github.tvbox.osc.bbox.bean.Movie;
import com.github.tvbox.osc.bbox.bean.SourceBean;
import com.github.tvbox.osc.bbox.event.RefreshEvent;
import com.github.tvbox.osc.bbox.event.ServerEvent;
import com.github.tvbox.osc.bbox.server.ControlManager;
import com.github.tvbox.osc.bbox.ui.adapter.PinyinAdapter;
import com.github.tvbox.osc.bbox.ui.adapter.SearchAdapter;
import com.github.tvbox.osc.bbox.ui.dialog.RemoteDialog;
import com.github.tvbox.osc.bbox.ui.dialog.SearchCheckboxDialog;
import com.github.tvbox.osc.bbox.ui.tv.QRCodeGen;
import com.github.tvbox.osc.bbox.ui.tv.widget.SearchKeyboard;
import com.github.tvbox.osc.bbox.util.*;
import com.github.tvbox.osc.bbox.viewmodel.SourceViewModel;
import com.google.gson.*;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.yang.flowlayoutlibrary.FlowLayout;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class SearchActivity extends BaseActivity {
    private LinearLayout llLayout;
    private LinearLayout llHotWord;
    private TvRecyclerView mGridView;
    private TvRecyclerView mGridViewWord;
    private FlowLayout mGridViewHotWord;
    private FlowLayout mGridViewMovieHotWord;
    private FlowLayout mGridViewTvHotWord;
    private FlowLayout mGridViewCiliHotWord;
    SourceViewModel sourceViewModel;
    private RemoteDialog remoteDialog;
    private EditText etSearch;
    private TextView tvSearch;
    private TextView tvClear;
    private TextView tvBackspace;
    private SearchKeyboard keyboard;
    private SearchAdapter searchAdapter;
    private PinyinAdapter wordAdapter;
    private String searchTitle = "";
    private TextView tvSearchCheckboxBtn;
    private TextView tvClearSearchHistory;
    private TextView tvRemoteSearch;

    private static HashMap<String, String> mCheckSources = null;
    private SearchCheckboxDialog mSearchCheckboxDialog = null;

    private TextView wordsSwitch;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_search;
    }


    private static Boolean hasKeyBoard;
    private static Boolean isSearchBack;
    @Override
    protected void init() {
        initView();
        initViewModel();
        initData();
        hasKeyBoard = true;
        isSearchBack = false;
    }

    private List<Runnable> pauseRunnable = null;

    @Override
    protected void onResume() {
        super.onResume();
        if (pauseRunnable != null && pauseRunnable.size() > 0) {
            searchExecutorService = Executors.newFixedThreadPool(5);
            allRunCount.set(pauseRunnable.size());
            for (Runnable runnable : pauseRunnable) {
                searchExecutorService.execute(runnable);
            }
            pauseRunnable.clear();
            pauseRunnable = null;
        }
        if (hasKeyBoard) {
            tvSearch.requestFocus();
            tvSearch.requestFocusFromTouch();
        }else {
            if(!isSearchBack){
                etSearch.requestFocus();
                etSearch.requestFocusFromTouch();
            }
        }
    }

    private void initView() {
        EventBus.getDefault().register(this);
        llLayout = findViewById(R.id.llLayout);
        llHotWord = findViewById(R.id.llHotWord);
        etSearch = findViewById(R.id.etSearch);
        tvSearch = findViewById(R.id.tvSearch);
        tvSearchCheckboxBtn = findViewById(R.id.tvSearchCheckboxBtn);
        tvRemoteSearch = findViewById(R.id.tvRemoteSearch);
        tvClearSearchHistory = findViewById(R.id.tvClearSearchHistory);
        tvClear = findViewById(R.id.tvClear);
        mGridView = findViewById(R.id.mGridView);
        keyboard = findViewById(R.id.keyBoardRoot);
        tvBackspace = findViewById(R.id.tvBackSpace);
        mGridViewWord = findViewById(R.id.mGridViewWord);
        mGridViewWord.setHasFixedSize(true);
        mGridViewWord.setLayoutManager(new V7LinearLayoutManager(this.mContext, 1, false));
        wordAdapter = new PinyinAdapter();
        mGridViewWord.setAdapter(wordAdapter);
        wordsSwitch = findViewById(R.id.wordSwitch);

        mGridViewHotWord = findViewById(R.id.mGridViewHotWord);
        mGridViewMovieHotWord = findViewById(R.id.mGridViewMovieHotWord);
        mGridViewTvHotWord = findViewById(R.id.mGridViewTvHotWord);
        mGridViewCiliHotWord = findViewById(R.id.mGridViewCiliHotWord);

        wordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(Hawk.get(HawkConfig.FAST_SEARCH_MODE, false)){
                    Bundle bundle = new Bundle();
                    bundle.putString("title", wordAdapter.getItem(position));
                    jumpActivity(FastSearchActivity.class, bundle);
                }else {
                    search(wordAdapter.getItem(position));
                }
            }
        });
        mGridView.setHasFixedSize(true);
        // lite
        if (Hawk.get(HawkConfig.SEARCH_VIEW, 0) == 0)
            mGridView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 1, false));
            // with preview
        else
            mGridView.setLayoutManager(new V7GridLayoutManager(this.mContext, 3));
        searchAdapter = new SearchAdapter();
        mGridView.setAdapter(searchAdapter);
        searchAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                Movie.Video video = searchAdapter.getData().get(position);
                if (video != null) {
                    try {
                        if (searchExecutorService != null) {
                            pauseRunnable = searchExecutorService.shutdownNow();
                            searchExecutorService = null;
                            JsLoader.stopAll();
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                    hasKeyBoard = false;
                    isSearchBack = true;
                    Bundle bundle = new Bundle();
                    bundle.putString("id", video.id);
                    bundle.putString("sourceKey", video.sourceKey);
                    jumpActivity(DetailActivity.class, bundle);
                }
            }
        });
        /*
        wordsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                String wd = wordsSwitch.getText().toString().trim();
                if(wd.equals("热门搜索")){
                    ArrayList<String> hisWord= Hawk.get(HawkConfig.SEARCH_HISTORY, new ArrayList<>());
                    if (hisWord.isEmpty()){
                        Toast.makeText(mContext, "暂无历史搜索", Toast.LENGTH_SHORT).show();
                    }else {
                        wordsSwitch.setText("搜索历史");
                        wordAdapter.setNewData(hisWord);
                    }
                }
                if(wd.equals("搜索历史")){
                    wordsSwitch.setText("热门搜索");
                    if(hots!=null && !hots.isEmpty()){
                        wordAdapter.setNewData(hots);
                    }
                }
            }
        });
         */
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                hasKeyBoard = true;
                String wd = etSearch.getText().toString().trim();
                if (!TextUtils.isEmpty(wd)) {
                    if(Hawk.get(HawkConfig.FAST_SEARCH_MODE, false)){
                        Bundle bundle = new Bundle();
                        bundle.putString("title", wd);
                        jumpActivity(FastSearchActivity.class, bundle);
                    }else {
                        search(wd);
                    }
                } else {
                    Toast.makeText(mContext, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                initData();
                etSearch.setText("");
            }
        });
        tvBackspace.setOnClickListener(v -> {
            String text = etSearch.getText().toString().trim();
            if (text.length() > 0) {
                text = text.substring(0, text.length() - 1);
                etSearch.setText(text);
            }
            if (text.length() > 0) {
                loadRec(text);
            }
        });
        tvRemoteSearch.setOnClickListener(v -> {
            remoteDialog = new RemoteDialog(mContext);
            remoteDialog.show();
        });
        tvClearSearchHistory.setOnClickListener(v -> {
            LOG.d("clear search history");
            Hawk.put(HawkConfig.SEARCH_HISTORY, new ArrayList<String>());
            wordsSwitch.setText("历史搜索");
            // if(hots!=null && !hots.isEmpty()){
            //     wordAdapter.setNewData(hots);
            // }
            wordAdapter.setNewData(Hawk.get(HawkConfig.SEARCH_HISTORY, new ArrayList<>()));
        });


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString().trim())) {
                    wordsSwitch.setText("历史搜索");
                    ArrayList<String> hisWord= Hawk.get(HawkConfig.SEARCH_HISTORY, new ArrayList<>());
                    wordAdapter.setNewData(hisWord);
                    cancel();
                    llLayout.setVisibility(View.VISIBLE);
                    llHotWord.setVisibility(View.VISIBLE);
                    mGridView.setVisibility(View.GONE);
                }
            }
        });
        //软键盘
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String wd = etSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(wd)) {
                        if (Hawk.get(HawkConfig.FAST_SEARCH_MODE, false)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("title", wd);
                            jumpActivity(FastSearchActivity.class, bundle);
                        } else {
                            hiddenImm();
                            search(wd);
                        }
                    } else {
                        Toast.makeText(mContext, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        // 监听遥控器
        etSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String wd = etSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(wd)) {
                        if (Hawk.get(HawkConfig.FAST_SEARCH_MODE, false)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("title", wd);
                            jumpActivity(FastSearchActivity.class, bundle);
                        } else {
                            hiddenImm();
                            search(wd);
                        }
                    } else {
                        Toast.makeText(mContext, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });
        keyboard.setOnSearchKeyListener((pos, key) -> {
            String text = etSearch.getText().toString().trim();
            text += key;
            etSearch.setText(text);
            if (text.length() > 0) {
                loadRec(text);
            }
        });
        setLoadSir(llLayout);
        tvSearchCheckboxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<SourceBean> searchAbleSource = ApiConfig.get().getSearchSourceBeanList();
                if (mSearchCheckboxDialog == null) {
                    mSearchCheckboxDialog = new SearchCheckboxDialog(SearchActivity.this, searchAbleSource, mCheckSources);
                }else {
                    if(searchAbleSource.size()!=mSearchCheckboxDialog.mSourceList.size()){
                        mSearchCheckboxDialog.setMSourceList(searchAbleSource);
                    }
                }
                mSearchCheckboxDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });
                mSearchCheckboxDialog.show();
            }
        });
    }

    private void initViewModel() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
    }

    /**
     * 拼音联想
     */
    private void loadRec(String key) {
        OkGo.get("https://tv.aiseet.atianqi.com/i-tvbin/qtv_video/search/get_search_smart_box")
                .params("format", "json")
                .params("page_num", 0)
                .params("page_size", 20)
                .params("key", key)
                .execute(new AbsCallback() {
                    @Override
                    public void onSuccess(Response response) {
                        try {
                            ArrayList hots = new ArrayList<>();
                            String result = (String) response.body();
                            Gson gson = new Gson();
                            JsonElement json = gson.fromJson(result, JsonElement.class);
                            JsonArray groupDataArr = json.getAsJsonObject()
                                    .get("data").getAsJsonObject()
                                    .get("search_data").getAsJsonObject()
                                    .get("vecGroupData").getAsJsonArray()
                                    .get(0).getAsJsonObject()
                                    .get("group_data").getAsJsonArray();
                            for (JsonElement groupDataElement : groupDataArr) {
                                JsonObject groupData = groupDataElement.getAsJsonObject();
                                String keywordTxt = groupData.getAsJsonObject("dtReportInfo")
                                        .getAsJsonObject("reportData")
                                        .get("keyword_txt").getAsString();
                                hots.add(keywordTxt.trim());
                            }
                            wordsSwitch.setText("猜你想搜");
                            wordAdapter.setNewData(hots);
                            mGridViewWord.smoothScrollToPosition(0);
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                    }

                    @Override
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        return response.body().string();
                    }
                });
    }

    private void initData() {
        initCheckedSourcesForSearch();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("title")) {
            String title = intent.getStringExtra("title");
            showLoading();
            if(Hawk.get(HawkConfig.FAST_SEARCH_MODE, false)){
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                jumpActivity(FastSearchActivity.class, bundle);
            }else {
                search(title);
            }
        }
        wordsSwitch.setText("历史搜索");
        ArrayList<String> hisWord= Hawk.get(HawkConfig.SEARCH_HISTORY, new ArrayList<>());
        wordAdapter.setNewData(hisWord);
        // if(hots!=null && !hots.isEmpty()){
        //     wordAdapter.setNewData(hots);
        //     return;
        // }
        // 加载热词
        OkGo.<String>get("https://node.video.qq.com/x/api/hot_search")
//        OkGo.<String>get("https://api.web.360kan.com/v1/rank")
//            .params("cat", "1")
            .params("channdlId", "0")
            .params("_", System.currentTimeMillis())
            .execute(new AbsCallback<String>() {
                @Override
                public void onSuccess(Response<String> response) {
                    try {
                        JsonObject respBody = JsonParser.parseString(response.body()).getAsJsonObject();
                        handleRespBody(respBody);
                        llLayout.setVisibility(View.VISIBLE);
                        llHotWord.setVisibility(View.VISIBLE);
                        mGridView.setVisibility(View.GONE);
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }

                @Override
                public String convertResponse(okhttp3.Response response) throws Throwable {
                    return response.body().string();
                }
            });
    }

    private void handleRespBody(JsonObject respBody) {
        ArrayList<String> hots = new ArrayList<>();
        ArrayList<String> movieHots = new ArrayList<>();
        ArrayList<String> tvHots = new ArrayList<>();
        ArrayList<String> ciliHots = new ArrayList<>();
        JsonObject hotsMapResult = respBody.get("data").getAsJsonObject().get("mapResult").getAsJsonObject();
        // 热搜
        JsonArray itemList = hotsMapResult.get("0").getAsJsonObject().get("listInfo").getAsJsonArray();
        // 电影
        JsonArray moviesItemList = hotsMapResult.get("1").getAsJsonObject().get("listInfo").getAsJsonArray();
        // 电视剧
        JsonArray tvItemList = hotsMapResult.get("2").getAsJsonObject().get("listInfo").getAsJsonArray();
        // 动漫
        JsonArray ciliItemList = hotsMapResult.get("3").getAsJsonObject().get("listInfo").getAsJsonArray();
        for (JsonElement ele : itemList) hots.add(getTitle(((JsonObject) ele).get("title").getAsString()));
        for (JsonElement ele : moviesItemList) movieHots.add(getTitle(((JsonObject) ele).get("title").getAsString()));
        for (JsonElement ele : tvItemList) tvHots.add(getTitle(((JsonObject) ele).get("title").getAsString()));
        for (JsonElement ele : ciliItemList) ciliHots.add(getTitle(((JsonObject) ele).get("title").getAsString()));

        mGridViewHotWord.setViews(hots, st -> search(st));
        mGridViewMovieHotWord.setViews(movieHots, st -> search(st));
        mGridViewTvHotWord.setViews(tvHots, st -> search(st));
        mGridViewCiliHotWord.setViews(ciliHots, st -> search(st));
    }
    private static String getTitle(String st) {
        return st.trim().replaceAll("<|>|《|》|-", "").split(" ")[0];
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void server(ServerEvent event) {
        if (event.type == ServerEvent.SERVER_SEARCH) {
            String title = (String) event.obj;
            showLoading();
            if(Hawk.get(HawkConfig.FAST_SEARCH_MODE, false)){
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                jumpActivity(FastSearchActivity.class, bundle);
            }else{
                search(title);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        if (event.type == RefreshEvent.TYPE_SEARCH_RESULT) {
            try {
                searchData(event.obj == null ? null : (AbsXml) event.obj);
            } catch (Exception e) {
                searchData(null);
            }
        }
    }

    private void initCheckedSourcesForSearch() {
        mCheckSources = SearchHelper.getSourcesForSearch();
    }

    public static void setCheckedSourcesForSearch(HashMap<String,String> checkedSources) {
        mCheckSources = checkedSources;
    }

    private void search(String title) {
        cancel();
        if (remoteDialog != null) {
            remoteDialog.dismiss();
            remoteDialog = null;
        }
        showLoading();
        etSearch.setText(title);

        //写入历史记录
        HistoryHelper.setSearchHistory(title);
        ArrayList<String> hisWord= Hawk.get(HawkConfig.SEARCH_HISTORY, new ArrayList<>());
        // wordAdapter.setNewData(hisWord);


        this.searchTitle = title;
        // llLayout.setVisibility(View.VISIBLE);
        // llHotWord.setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.GONE);
        searchAdapter.setNewData(new ArrayList<>());
        searchResult();
    }

    private ExecutorService searchExecutorService = null;
    private AtomicInteger allRunCount = new AtomicInteger(0);

    private void searchResult() {
        try {
            if (searchExecutorService != null) {
                searchExecutorService.shutdownNow();
                searchExecutorService = null;
                JsLoader.stopAll();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        } finally {
            searchAdapter.setNewData(new ArrayList<>());
            allRunCount.set(0);
        }
        searchExecutorService = Executors.newFixedThreadPool(5);
        List<SourceBean> searchRequestList = new ArrayList<>();
        searchRequestList.addAll(ApiConfig.get().getSourceBeanList());
        SourceBean home = ApiConfig.get().getHomeSourceBean();
        searchRequestList.remove(home);
        searchRequestList.add(0, home);

        ArrayList<String> siteKey = new ArrayList<>();
        for (SourceBean bean : searchRequestList) {
            if (!bean.isSearchable()) {
                continue;
            }
            if (mCheckSources != null && !mCheckSources.containsKey(bean.getKey())) {
                continue;
            }
            siteKey.add(bean.getKey());
            allRunCount.incrementAndGet();
        }
        if (siteKey.size() <= 0) {
            Toast.makeText(mContext, "没有指定搜索源", Toast.LENGTH_SHORT).show();
            // showEmpty();
            showSuccess();
            return;
        }
        for (String key : siteKey) {
            searchExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    sourceViewModel.getSearch(key, searchTitle);
                }
            });
        }
    }

    private boolean matchSearchResult(String name, String searchTitle) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(searchTitle)) return false;
        searchTitle = searchTitle.trim();
        String[] arr = searchTitle.split("\\s+");
        int matchNum = 0;
        for(String one : arr) {
            if (name.contains(one)) matchNum++;
        }
        return matchNum == arr.length ? true : false;
    }

    private void searchData(AbsXml absXml) {
        if (absXml != null && absXml.movie != null && absXml.movie.videoList != null && absXml.movie.videoList.size() > 0) {
            List<Movie.Video> data = new ArrayList<>();
            for (Movie.Video video : absXml.movie.videoList) {
                if (matchSearchResult(video.name, searchTitle)) data.add(video);
            }
            if (searchAdapter.getData().size() > 0) {
                searchAdapter.addData(data);
            } else {
                showSuccess();
                llLayout.setVisibility(View.VISIBLE);
                llHotWord.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);
                searchAdapter.setNewData(data);
            }
        }

        int count = allRunCount.decrementAndGet();
        if (count <= 0) {
            if (searchAdapter.getData().size() <= 0) {
                // showEmpty();
                showSuccess();
                llLayout.setVisibility(View.VISIBLE);
                llHotWord.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, "搜索结果为空", Toast.LENGTH_SHORT).show();
            }
            cancel();
        }
    }


    private void cancel() {
        OkGo.getInstance().cancelTag("search");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancel();
        try {
            if (searchExecutorService != null) {
                searchExecutorService.shutdownNow();
                searchExecutorService = null;
                JsLoader.stopAll();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        EventBus.getDefault().unregister(this);
    }

    private void hiddenImm()
    {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Hawk.put(HawkConfig.ACTIVITY_ID, 2);
    }
}