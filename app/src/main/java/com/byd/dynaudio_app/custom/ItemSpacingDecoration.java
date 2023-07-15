package com.byd.dynaudio_app.custom;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class ItemSpacingDecoration extends RecyclerView.ItemDecoration {

    private int spacing;

    public ItemSpacingDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
//        outRect.right = spacing;
//        outRect.top = spacing;
//
//        if (parent.getPaddingLeft() != spacing) parent.setPadding(spacing, 0, 0, 0);

        outRect.left = spacing;
        outRect.top = spacing;
        if (parent.getPaddingRight() != spacing) parent.setPadding(0, 0, spacing, 0);
    }
}
