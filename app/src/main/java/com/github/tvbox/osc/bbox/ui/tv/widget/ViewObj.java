package com.github.tvbox.osc.bbox.ui.tv.widget;

import android.view.View;
import android.view.ViewGroup;

/**
 * 描述
 *
 * @author pj567
 * @since 2020/7/28
 */
public class ViewObj {
    private final View view;
    private final ViewGroup.MarginLayoutParams params;

    public ViewObj(View view, ViewGroup.MarginLayoutParams params) {
        this.view = view;
        this.params = params;
    }

    public void setMarginLeft(int left) {
        params.leftMargin = left;
        view.setLayoutParams(params);
    }

    public void setMarginTop(int top) {
        params.topMargin = top;
        view.setLayoutParams(params);
    }

    public void setMarginRight(int right) {
        params.rightMargin = right;
        view.setLayoutParams(params);
    }

    public void setMarginBottom(int bottom) {
        params.bottomMargin = bottom;
        view.setLayoutParams(params);
    }

    public void setWidth(int width) {
        params.width = width;
        view.setLayoutParams(params);
    }

    public void setHeight(int height) {
        params.height = height;
        view.setLayoutParams(params);
    }
}
