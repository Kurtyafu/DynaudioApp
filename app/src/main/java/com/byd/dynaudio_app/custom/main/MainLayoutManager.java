package com.byd.dynaudio_app.custom.main;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainLayoutManager extends LinearLayoutManager {
    public MainLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);

        int curLineWidth = 0, curLineTop = 0, lastLineMaxHeight = 0;
        for (int i = 0; i < getItemCount(); i++) {
            View view = recycler.getViewForPosition(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            addView(view);
            measureChildWithMargins(view, 0, 0);
            int width = getDecoratedMeasuredWidth(view) + layoutParams.leftMargin + layoutParams.rightMargin;
            int height = getDecoratedMeasuredHeight(view) + layoutParams.topMargin + layoutParams.bottomMargin;
            curLineWidth += width;

            if (curLineWidth <= getWidth()) {
                layoutDecorated(view,
                        curLineWidth - width + layoutParams.leftMargin,
                        curLineTop + layoutParams.topMargin,
                        curLineWidth - layoutParams.rightMargin,
                        curLineTop + height - layoutParams.bottomMargin);

                lastLineMaxHeight = Math.max(lastLineMaxHeight, height);
            } else {
                curLineWidth = width;
                if (lastLineMaxHeight == 0) {
                    lastLineMaxHeight = height;
                }

                curLineTop += lastLineMaxHeight;
                layoutDecorated(view,
                        layoutParams.leftMargin,
                        curLineTop + layoutParams.topMargin,
                        width - layoutParams.rightMargin,
                        curLineTop + height - layoutParams.bottomMargin);

                lastLineMaxHeight = height;
            }
        }
    }
}
