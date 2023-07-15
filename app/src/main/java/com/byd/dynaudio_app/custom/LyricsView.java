package com.byd.dynaudio_app.custom;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseRecyclerViewAdapter;
import com.byd.dynaudio_app.base.BaseView;
import com.byd.dynaudio_app.custom.lrc.LrcEntry;
import com.byd.dynaudio_app.custom.lrc.LrcUtils;
import com.byd.dynaudio_app.databinding.LayoutItemLyricsBinding;
import com.byd.dynaudio_app.databinding.LayoutViewLyricsBinding;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.TestUtils;
import com.hw.lrcviewlib.DefaultLrcRowsParser;
import com.hw.lrcviewlib.LrcDataBuilder;
import com.hw.lrcviewlib.LrcRow;
import com.hw.lrcviewlib.LrcView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint("StaticFieldLeak")
public class LyricsView extends BaseView<LayoutViewLyricsBinding> {
    private static final long AFTER_ENDING_SCROLL_DURATION = 1000;
    private LyricsAdapter lyricsAdapter;
    // 是否正在通过点击歌词来切换歌词
    private boolean isClickMoving = false;
    private int lyricTopMargin;
    private int lyricBottomMargin;

    private int highlightPosition;

    private LinearLayoutManager layoutManager;

    // 当前的滑动总量
    private int totalScroll;
    private long endScrollTime;
    private int position;
    private long time;

    public LyricsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {
        layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        mDataBinding.recycler.setLayoutManager(layoutManager);
        lyricsAdapter = new LyricsAdapter(mContext, new ArrayList<>());
        mDataBinding.recycler.setAdapter(lyricsAdapter);

        // 设置第一个前方的边距 及最后一个后方的边距
        mDataBinding.recycler.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                if (position == 0) {
                    lyricTopMargin = DensityUtils.dp2Px(mContext, 40);
                    outRect.top = lyricTopMargin;
                } else if (position == parent.getAdapter().getItemCount() - 1) {
                    lyricBottomMargin = DensityUtils.dp2Px(mContext, 320);
                    outRect.bottom = lyricBottomMargin;
                }
            }
        });

        mDataBinding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean shouldRecord = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case SCROLL_STATE_DRAGGING:
                        shouldRecord = true;
                        break;
                    case SCROLL_STATE_IDLE:
                        if (shouldRecord) {
                            // 滚动停止的时候，记录当前时间
                            endScrollTime = System.currentTimeMillis();
                            shouldRecord = false;
                        }
                        break;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalScroll += dy;
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_view_lyrics;
    }


    /**
     * 给定一个时间 跳转到对应的歌词
     *
     * @param currentTime
     */
    public void toTime(long currentTime) {
        if (lyricsAdapter.getData() != null && lyricsAdapter.getData().size() > 0) {
            if (currentTime == time) {
                return;
            }

            LogUtils.d("show line start...");
            int showLine = findShowLine(currentTime);
            this.time = currentTime;
            LogUtils.d("show line : " + showLine + " time : " + currentTime);
            if (showLine == -1) {
                return;
            }
            if (mDataBinding.recycler.getScrollState() != SCROLL_STATE_DRAGGING) {
                smoothScrollToPosition(showLine);
            }
            setHighlightPosition(showLine);
        }
    }

    private long lastTime;

    /**
     * 返回列表里 i<=time<i+1
     */
    private synchronized int findShowLine(long time) {
        List<LrcEntry> data = new ArrayList<>(lyricsAdapter.getData());
        int index = 0;
        for (int i = 0; i < data.size() - 1; i++) {
            LrcEntry lrcEntry = data.get(i);
            if (time >= lrcEntry.getTime()) {
                index++;
            }
        }
        return index;
    }


    /**
     * 将position高亮并显示在屏幕中的第二个位置
     */
    private void smoothScrollToPosition(int position) {
        if (this.position == position) return;
        // LogUtils.d("position : " + position);
        this.position = position;
        if (mDataBinding.recycler.getScrollState() == SCROLL_STATE_DRAGGING
            /*|| System.currentTimeMillis() - endScrollTime < AFTER_ENDING_SCROLL_DURATION*/)
            return;

        // LogUtils.d("item height : " + lyricsAdapter.getNormalItemHeight());

        int toY = lyricTopMargin + (position - 1) * getItemHeight();  // 相对于初始状态时，目标偏移量
        int dY = toY - totalScroll;  // 相对于初始位置 当前偏移量
        // LogUtils.d("to y : " + toY + " totalScroll : " + totalScroll + " dy : " + dY);
        mDataBinding.recycler.smoothScrollBy(0, dY);
    }

    public int getHighlightPosition() {
        return highlightPosition;
    }

    public void setHighlightPosition(int highlightPosition) {
        if (highlightPosition < 0 || highlightPosition > lyricsAdapter.getItemCount() - 1) return;

        // smoothScrollToPosition(highlightPosition);

        this.highlightPosition = highlightPosition;

        LiveDataBus.get().with(LiveDataBusConstants.highlight_line).postValue(highlightPosition);
    }

    /**
     * 获取一个item的高度
     */
    private int getItemHeight() {
        return DensityUtils.dp2Px(mContext, 40);
    }

    /**
     * 加载歌词文件
     *
     * @param lrcFile 歌词文件
     */
    public void loadLrc(File lrcFile) {
        loadLrc(lrcFile, null);
    }

    /**
     * 加载双语歌词文件，两种语言的歌词时间戳需要一致
     *
     * @param mainLrcFile   第一种语言歌词文件
     * @param secondLrcFile 第二种语言歌词文件
     */
    public void loadLrc(File mainLrcFile, File secondLrcFile) {
        mDataBinding.getRoot().post(() -> {
            reset();

            StringBuilder sb = new StringBuilder("file://");
            sb.append(mainLrcFile.getPath());
            if (secondLrcFile != null) {
                sb.append("#").append(secondLrcFile.getPath());
            }
            String flag = sb.toString();
            setFlag(flag);
            new AsyncTask<File, Integer, List<LrcEntry>>() {
                @Override
                protected List<LrcEntry> doInBackground(File... params) {
                    return LrcUtils.parseLrc(params);
                }

                @Override
                protected void onPostExecute(List<LrcEntry> lrcEntries) {
                    if (getFlag() == flag) {
                        lyricsAdapter.setData(lrcEntries);
                        setFlag(null);
                    }
                }
            }.execute(mainLrcFile, secondLrcFile);
        });
    }

    /**
     * 加载歌词文本
     *
     * @param lrcText 歌词文本
     */
    public void loadLrc(String lrcText) {
        loadLrc(lrcText, null);
    }

    /**
     * 加载双语歌词文本，两种语言的歌词时间戳需要一致
     *
     * @param mainLrcText   第一种语言歌词文本
     * @param secondLrcText 第二种语言歌词文本
     */
    public void loadLrc(String mainLrcText, String secondLrcText) {
        mDataBinding.getRoot().post(() -> {
            reset();

            StringBuilder sb = new StringBuilder("file://");
            sb.append(mainLrcText);
            if (secondLrcText != null) {
                sb.append("#").append(secondLrcText);
            }
            String flag = sb.toString();
            setFlag(flag);
            new AsyncTask<String, Integer, List<LrcEntry>>() {
                @Override
                protected List<LrcEntry> doInBackground(String... params) {
                    return LrcUtils.parseLrc(params);
                }

                @Override
                protected void onPostExecute(List<LrcEntry> lrcEntries) {
                    if (getFlag() == flag) {
                        for (LrcEntry lrcEntry : lrcEntries) {
                            LogUtils.d(" time 22 : " + lrcEntry.getTime()
                                    + " text : " + lrcEntry.getShowText());
                        }


                        lyricsAdapter.setData(lrcEntries);
                        setFlag(null);
                    }
                }
            }.execute(mainLrcText, secondLrcText);
        });
    }

    /**
     * 加载在线歌词，默认使用 utf-8 编码
     *
     * @param lrcUrl 歌词文件的网络地址
     */
    public void loadLrcByUrl(String lrcUrl) {
        loadLrcByUrl(lrcUrl, "utf-8");
    }

    /**
     * 加载在线歌词
     *
     * @param lrcUrl  歌词文件的网络地址
     * @param charset 编码格式
     */
    public void loadLrcByUrl(String lrcUrl, String charset) {
        String flag = "url://" + lrcUrl;
        setFlag(flag);
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                return LrcUtils.getContentFromNetwork(params[0], params[1]);
            }

            @Override
            protected void onPostExecute(String lrcText) {
                if (getFlag() == flag) {
                    loadLrc(lrcText);
                }
            }
        }.execute(lrcUrl, charset);
    }

    private void reset() {
//        endAnimation();
//        mScroller.forceFinished(true);
//        isShowTimeline = false;
//        isTouching = false;
//        isFling = false;
//        removeCallbacks(hideTimelineRunnable);
//        mLrcEntryList.clear();
//        mOffset = 0;
//        mCurrentLine = 0;
        invalidate();
    }

    private Object mFlag;

    private Object getFlag() {
        return mFlag;
    }

    private void setFlag(Object flag) {
        this.mFlag = flag;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {

        void onClick(int position);
    }

    public class LyricsAdapter extends BaseRecyclerViewAdapter<LrcEntry, LayoutItemLyricsBinding> {

        private int normalItemHeight = 0;

        private long lastClickTime;

        public LyricsAdapter(Context mContext, List<LrcEntry> mData) {
            super(mContext, mData);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.layout_item_lyrics;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        protected void bindItem(LayoutItemLyricsBinding dataBinding, LrcEntry itemBean, int position) {
            dataBinding.tvLyrics.setText(itemBean.getText());
            // todo 这里平板减少一些最大长度
//            LogUtils.d("time record : " + itemBean.getTime());

            highlight(dataBinding, highlightPosition, position);

            LiveDataBus.get().with(LiveDataBusConstants.highlight_line, Integer.class)
                    .observe((LifecycleOwner) mContext, integer -> {
                        if (integer != null) {
                            highlight(dataBinding, integer, position);
                        }
                    });

            dataBinding.getRoot().setOnClickListener(v -> {
                mDataBinding.recycler.smoothScrollToPosition(position);
                /*setHighlightPosition(position);

                if (System.currentTimeMillis() - lastClickTime > 1000) {
                    MusicPlayManager.getInstance().setTime(itemBean.getTime());
                    lastClickTime = System.currentTimeMillis();
                }*/

                if (listener != null) listener.onClick(position);
            });

            if (normalItemHeight == 0 && position > 0 && position < getItemCount()) {
                normalItemHeight = dataBinding.getRoot().getHeight();
            }
        }

        /**
         * 不处理滑动 仅修改高亮的item
         */
        private void highlight(LayoutItemLyricsBinding dataBinding, int highlightPos, int position) {
            dataBinding.tvLyrics.setTextColor(highlightPos == position ? Color.parseColor("#FFFFFFFF") : Color.parseColor("#73FFFFFF"));
            dataBinding.tvLyrics.setTag(highlightPos == position);
            highlightPosition = highlightPos;
        }

        public int getNormalItemHeight() {
            return normalItemHeight;
        }
    }
}
