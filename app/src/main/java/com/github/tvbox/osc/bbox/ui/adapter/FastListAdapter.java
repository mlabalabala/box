package com.github.tvbox.osc.bbox.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.bbox.R;

import java.util.ArrayList;

public class FastListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public FastListAdapter() {
        super(R.layout.item_search_word_hot, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tvSearchWord, item);
    }


    // 记录失去焦点的控件
    public void onLostFocus(View child) {
        if(lostFocusTimestamp==0) {
            lostFocusTimestamp = System.currentTimeMillis();
        }
    }

    // 记录获得焦点的控件
    public int onSetFocus(View child){
        // 可以调整间隔还判断到底是不是整个view失去了焦点
        if(System.currentTimeMillis() - lostFocusTimestamp > 200) {
            setp = 0;
        }
        int index=((ViewGroup)child.getParent()).indexOfChild(child);
        if(focusView != null){
            int index2=((ViewGroup)focusView.getParent()).indexOfChild(focusView);
            if(Math.abs(index-index2) > setp) { // 跳了控件，将焦点恢复到之前的控件上去
                setp = 0;
                int offset = (index > index2? -1 :1);
                ViewGroup parent =  ((ViewGroup)focusView.getParent());
                parent.getChildAt(index+offset).requestFocus();
                return  -1;
            }
        }
        lostFocusTimestamp =0;
        focusView = child;
        setp =1;
        return 1;
    }

    public void  reset(){
        lostFocusTimestamp =0;
        setp =0;
        focusView = null;
    }

    public long lostFocusTimestamp=0;   // 控件失去焦点的时间
    public int setp =0; //步长
    View focusView;     // 当前获得焦点的控件
}