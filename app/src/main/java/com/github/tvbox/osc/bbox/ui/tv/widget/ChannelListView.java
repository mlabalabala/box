package com.github.tvbox.osc.bbox.ui.tv.widget;



import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ListView;

import com.github.tvbox.osc.bbox.ui.activity.LivePlayActivity;

public class ChannelListView extends ListView {
    DataChangedListener dataChangedListener;
    public int pos= LivePlayActivity.currentChannelGroupIndex;
    private int y;

    public ChannelListView(Context context) {
        super(context);
    }
    public ChannelListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ChannelListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setSelect(int position,int y) {
        super.setSelection(position);
        pos=position;
        this.y=y;
    }
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            setSelectionFromTop(pos, y);
        }
    }


    @Override
    protected void handleDataChanged() {
        super.handleDataChanged();
        if(dataChangedListener!=null)dataChangedListener.onSuccess();
    }
    public void setDataChangedListener(DataChangedListener dataChangedListener) {
        this.dataChangedListener = dataChangedListener;
    }
    public interface DataChangedListener{
        public void onSuccess();
    }

}
