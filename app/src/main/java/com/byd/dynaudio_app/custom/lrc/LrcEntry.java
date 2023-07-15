/*
 * Copyright (C) 2017 wangchenyan
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.byd.dynaudio_app.custom.lrc;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * 一行歌词实体
 */
public class LrcEntry implements Comparable<LrcEntry>, MultiItemEntity {
    private long time;
    private String text;
    private String secondText;
    private StaticLayout staticLayout;

    private int height;
    private int marginTop, marginBottom;

    private float top;
    /**
     * 歌词距离视图顶部的距离
     */
    private float offset = Float.MIN_VALUE;
    public static final int GRAVITY_CENTER = 0;
    public static final int GRAVITY_LEFT = 1;
    public static final int GRAVITY_RIGHT = 2;

    LrcEntry(long time, String text) {
        this.time = time;
        this.text = text;
    }

    LrcEntry(long time, String text, String secondText) {
        this.time = time;
        this.text = text;
        this.secondText = secondText;
    }

    void init(TextPaint paint, int width, int gravity) {
        Layout.Alignment align;
        switch (gravity) {
            case GRAVITY_LEFT:
                align = Layout.Alignment.ALIGN_NORMAL;
                break;

            default:
            case GRAVITY_CENTER:
                align = Layout.Alignment.ALIGN_CENTER;
                break;

            case GRAVITY_RIGHT:
                align = Layout.Alignment.ALIGN_OPPOSITE;
                break;
        }
        staticLayout = new StaticLayout(getShowText(), paint, width, align, 1f, 0f, false);

        offset = Float.MIN_VALUE;
    }

    public long getTime() {
        return time;
    }

    public StaticLayout getStaticLayout() {
        return staticLayout;
    }

    public int getHeight() {
        if (staticLayout == null) {
            return 0;
        }
        return staticLayout.getHeight();
    }

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public String getText() {
        return text;
    }


    public void setSecondText(String secondText) {
        this.secondText = secondText;
    }

    public String getShowText() {
        if (!TextUtils.isEmpty(secondText)) {
            return text + "\n" + secondText;
        } else {
            return text;
        }
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    @Override
    public int compareTo(LrcEntry entry) {
        if (entry == null) {
            return -1;
        }
        return (int) (time - entry.getTime());
    }

    @Override
    public int getItemType() {
        return 0;
    }
}