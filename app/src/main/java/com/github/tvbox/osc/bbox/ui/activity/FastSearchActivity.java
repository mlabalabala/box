package com.github.tvbox.osc.bbox.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.catvod.crawler.JsLoader;
import com.github.tvbox.osc.bbox.R;
import com.github.tvbox.osc.bbox.api.ApiConfig;
import com.github.tvbox.osc.bbox.base.BaseActivity;
import com.github.tvbox.osc.bbox.bean.AbsXml;
import com.github.tvbox.osc.bbox.bean.Movie;
import com.github.tvbox.osc.bbox.bean.SourceBean;
import com.github.tvbox.osc.bbox.event.RefreshEvent;
import com.github.tvbox.osc.bbox.event.ServerEvent;
import com.github.tvbox.osc.bbox.ui.adapter.FastListAdapter;
import com.github.tvbox.osc.bbox.ui.adapter.FastSearchAdapter;
import com.github.tvbox.osc.bbox.ui.adapter.SearchWordAdapter;
import com.github.tvbox.osc.bbox.util.FastClickCheckUtil;
import com.github.tvbox.osc.bbox.util.HistoryHelper;
import com.github.tvbox.osc.bbox.util.SearchHelper;
import com.github.tvbox.osc.bbox.viewmodel.SourceViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class FastSearchActivity extends BaseActivity {
    private LinearLayout llLayout;
    private TextView mSearchTitle;
    private TvRecyclerView mGridView;
    private TvRecyclerView mGridViewFilter;
    private TvRecyclerView mGridViewWord;
    private TvRecyclerView mGridViewWordFenci;
    SourceViewModel sourceViewModel;

    private SearchWordAdapter searchWordAdapter;
    private FastSearchAdapter searchAdapter;
    private FastSearchAdapter searchAdapterFilter;
    private FastListAdapter spListAdapter;
    private String searchTitle = "";
    private HashMap<String, String> spNames;
    private boolean isFilterMode = false;
    private String searchFilterKey = "";    // 过滤的key
    private HashMap<String, ArrayList<Movie.Video>> resultVods; // 搜索结果
    private final List<String> quickSearchWord = new ArrayList<>();
    private HashMap<String, String> mCheckSources = null;

    private final View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View itemView, boolean hasFocus) {
            try {
                if (!hasFocus) {
                    spListAdapter.onLostFocus(itemView);
                } else {
                    int ret = spListAdapter.onSetFocus(itemView);
                    if (ret < 0) return;
                    TextView v = (TextView) itemView;
                    String sb = v.getText().toString();
                    filterResult(sb);
                }
            } catch (Exception e) {
                Toast.makeText(FastSearchActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_fast_search;
    }

    @Override
    protected void init() {
        spNames = new HashMap<String, String>();
        resultVods = new HashMap<String, ArrayList<Movie.Video>>();
        initView();
        initViewModel();
        initData();
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
    }

    private void initView() {
        EventBus.getDefault().register(this);
        llLayout = findViewById(R.id.llLayout);
        mSearchTitle = findViewById(R.id.mSearchTitle);
        mGridView = findViewById(R.id.mGridView);
        mGridViewWord = findViewById(R.id.mGridViewWord);
        mGridViewFilter = findViewById(R.id.mGridViewFilter);

        mGridViewWord.setHasFixedSize(true);
        mGridViewWord.setLayoutManager(new V7LinearLayoutManager(this.mContext, 1, false));
        spListAdapter = new FastListAdapter();
        mGridViewWord.setAdapter(spListAdapter);

        mGridViewWord.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View child) {
                child.setFocusable(true);
                child.setOnFocusChangeListener(focusChangeListener);
                TextView t = (TextView) child;
                if (t.getText() == "全部") {
                    t.requestFocus();
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                view.setOnFocusChangeListener(null);
            }
        });

        spListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String spName = spListAdapter.getItem(position);
                filterResult(spName);
            }
        });

        mGridView.setHasFixedSize(true);
        mGridView.setLayoutManager(new V7GridLayoutManager(this.mContext, 5));

        searchAdapter = new FastSearchAdapter();
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
                    Bundle bundle = new Bundle();
                    bundle.putString("id", video.id);
                    bundle.putString("sourceKey", video.sourceKey);
                    jumpActivity(DetailActivity.class, bundle);
                }
            }
        });


        mGridViewFilter.setLayoutManager(new V7GridLayoutManager(this.mContext, 5));
        searchAdapterFilter = new FastSearchAdapter();
        mGridViewFilter.setAdapter(searchAdapterFilter);
        searchAdapterFilter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                Movie.Video video = searchAdapterFilter.getData().get(position);
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
                    Bundle bundle = new Bundle();
                    bundle.putString("id", video.id);
                    bundle.putString("sourceKey", video.sourceKey);
                    jumpActivity(DetailActivity.class, bundle);
                }
            }
        });

        setLoadSir(llLayout);

        // 分词
        searchWordAdapter = new SearchWordAdapter();
        mGridViewWordFenci = findViewById(R.id.mGridViewWordFenci);
        mGridViewWordFenci.setAdapter(searchWordAdapter);
        mGridViewWordFenci.setLayoutManager(new V7LinearLayoutManager(this.mContext, 0, false));
        searchWordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String str = searchWordAdapter.getData().get(position);
                search(str);
            }
        });
        searchWordAdapter.setNewData(new ArrayList<>());
    }

    private void initViewModel() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
    }

    private void filterResult(String spName) {
        if (spName.equals("全部")) {
            mGridView.setVisibility(View.VISIBLE);
            mGridViewFilter.setVisibility(View.GONE);
            return;
        }
        mGridView.setVisibility(View.GONE);
        mGridViewFilter.setVisibility(View.VISIBLE);
        String key = spNames.get(spName);
        if (key.isEmpty()) return;

        if (searchFilterKey == key) return;
        searchFilterKey = key;

        List<Movie.Video> list = resultVods.get(key);
        searchAdapterFilter.setNewData(list);
    }

    private void fenci() {
        if (!quickSearchWord.isEmpty()) return; // 如果经有分词了，不再进行二次分词
        quickSearchWord.addAll(SearchHelper.splitWords(searchTitle));
        List<String> words = new ArrayList<>(new HashSet<>(quickSearchWord));
        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH_WORD, words));
        // 分词
//        OkGo.<String>get("http://api.pullword.com/get.php?source=" + URLEncoder.encode(searchTitle) + "&param1=0&param2=0&json=1")
//                .tag("fenci")
//                .execute(new AbsCallback<String>() {
//                    @Override
//                    public String convertResponse(okhttp3.Response response) throws Throwable {
//                        if (response.body() != null) {
//                            return response.body().string();
//                        } else {
//                            throw new IllegalStateException("网络请求错误");
//                        }
//                    }
//
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        String json = response.body();
//                        quickSearchWord.clear();
//                        try {
//                            for (JsonElement je : new Gson().fromJson(json, JsonArray.class)) {
//                                quickSearchWord.add(je.getAsJsonObject().get("t").getAsString());
//                            }
//                        } catch (Throwable th) {
//                            th.printStackTrace();
//                        }
//                        quickSearchWord.addAll(SearchHelper.splitWords(searchTitle));
//                        List<String> words = new ArrayList<>(new HashSet<>(quickSearchWord));
//                        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH_WORD, words));
//                    }
//
//                    @Override
//                    public void onError(Response<String> response) {
//                        super.onError(response);
//                    }
//                });
    }

    private void initData() {
        initCheckedSourcesForSearch();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("title")) {
            String title = intent.getStringExtra("title");
            showLoading();
            search(title);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void server(ServerEvent event) {
        if (event.type == ServerEvent.SERVER_SEARCH) {
            String title = (String) event.obj;
            showLoading();
            search(title);
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
        } else if (event.type == RefreshEvent.TYPE_QUICK_SEARCH_WORD) {
            if (event.obj != null) {
                List<String> data = (List<String>) event.obj;
                searchWordAdapter.setNewData(data);
            }
        }
        if (mSearchTitle != null) {
//            mSearchTitle.setText(String.format("搜索(%d/%d)", resultVods.size(), startSearchNum));
            mSearchTitle.setText(String.format("已搜索( %d )", resultVods.size()));
        }
    }

    private void initCheckedSourcesForSearch() {
        mCheckSources = SearchHelper.getSourcesForSearch();
    }

    private void search(String title) {
        cancel();
        showLoading();
        this.searchTitle = title;
        fenci();
        mGridView.setVisibility(View.INVISIBLE);
        mGridViewFilter.setVisibility(View.GONE);
        searchAdapter.setNewData(new ArrayList<>());
        searchAdapterFilter.setNewData(new ArrayList<>());

        spListAdapter.reset();
        resultVods.clear();
        searchFilterKey = "";
        isFilterMode = false;
        spNames.clear();

        //写入历史记录
        HistoryHelper.setSearchHistory(title);

        searchResult();
    }

    private ExecutorService searchExecutorService = null;
    private final AtomicInteger allRunCount = new AtomicInteger(0);

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
            searchAdapterFilter.setNewData(new ArrayList<>());
            allRunCount.set(0);
        }
        searchExecutorService = Executors.newFixedThreadPool(5);
        List<SourceBean> searchRequestList = new ArrayList<>();
        searchRequestList.addAll(ApiConfig.get().getSourceBeanList());
        SourceBean home = ApiConfig.get().getHomeSourceBean();
        searchRequestList.remove(home);
        searchRequestList.add(0, home);


        ArrayList<String> siteKey = new ArrayList<>();
        ArrayList<String> hots = new ArrayList<>();

        spListAdapter.setNewData(hots);
        spListAdapter.addData("全部");
        for (SourceBean bean : searchRequestList) {
            if (!bean.isSearchable()) {
                continue;
            }
            if (mCheckSources != null && !mCheckSources.containsKey(bean.getKey())) {
                continue;
            }
            siteKey.add(bean.getKey());
            this.spNames.put(bean.getName(), bean.getKey());
            allRunCount.incrementAndGet();
        }

        for (String key : siteKey) {
            searchExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        sourceViewModel.getSearch(key, searchTitle);
                    } catch (Exception e) {

                    }
                }
            });
        }
    }

    // 向过滤栏添加有结果的spname
    private String addWordAdapterIfNeed(String key) {
        try {
            String name = "";
            for (String n : spNames.keySet()) {
                if (spNames.get(n) == key) {
                    name = n;
                }
            }
            if (name == "") return key;

            List<String> names = spListAdapter.getData();
            for (int i = 0; i < names.size(); ++i) {
                if (name == names.get(i)) {
                    return key;
                }
            }

            spListAdapter.addData(name);
            return key;
        } catch (Exception e) {
            return key;
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
        return matchNum == arr.length;
    }

    private void searchData(AbsXml absXml) {
        String lastSourceKey = "";

        if (absXml != null && absXml.movie != null && absXml.movie.videoList != null && absXml.movie.videoList.size() > 0) {
            List<Movie.Video> data = new ArrayList<>();
            for (Movie.Video video : absXml.movie.videoList) {
                if (!matchSearchResult(video.name, searchTitle)) continue;
                data.add(video);
                if (!resultVods.containsKey(video.sourceKey)) {
                    resultVods.put(video.sourceKey, new ArrayList<Movie.Video>());
                }
                resultVods.get(video.sourceKey).add(video);
                if (video.sourceKey != lastSourceKey) {
                    lastSourceKey = this.addWordAdapterIfNeed(video.sourceKey);
                }
            }

            if (searchAdapter.getData().size() > 0) {
                searchAdapter.addData(data);
            } else {
                showSuccess();
                if (!isFilterMode)
                    mGridView.setVisibility(View.VISIBLE);
                searchAdapter.setNewData(data);
            }
        }

        int count = allRunCount.decrementAndGet();
        if (count <= 0) {
            if (searchAdapter.getData().size() == 0) {
                showEmpty();
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
}