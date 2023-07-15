package com.byd.dynaudio_app.custom.lrc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseView;
import com.byd.dynaudio_app.databinding.LayoutItemLyricsBinding;
import com.byd.dynaudio_app.databinding.LayoutViewMyLrcBinding;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("StaticFieldLeak")

public class MyLrc extends BaseView<LayoutViewMyLrcBinding> {
    private List<LrcEntry> mLrcEntryList = new ArrayList<>();

    private int mCurrentLine = -1;
    private BaseMultiItemQuickAdapter<LrcEntry, BaseDataBindingHolder> adapter;

    private Map<Integer, Integer> heightMap = new HashMap<>();// 存储每个位置的item的高度
    private LinearLayoutManager linearLayoutManager;
    private boolean isDragging;// 是否在拖拽歌词

    private long lastItemClickTime = -1; // 防止跳转歌词被短时间重复点击
    private long AUTO_BACK_DURATION = 2000; // 松手到自动回正的时间差

    private int loadingStatus = 1;// 0表示有歌词 1-3表示：歌词加载中+n个.
    private long duration = 400;
    private Runnable lrcLoadingRunnable = new Runnable() {
        @Override
        public void run() {
            mDataBinding.tvLoading.setText("歌词加载中" + ".".repeat(loadingStatus));

            loadingStatus++;

            if (loadingStatus > 3) {
                loadingStatus = 0;
            }

            postDelayed(lrcLoadingRunnable,duration);
        }
    };

    public MyLrc(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_view_my_lrc;
    }

    /**
     * 跳转到对应的 time
     */
    public void updateTime(long time) {
        runOnUi(() -> {
            if (!hasLrc()) {
                return;
            }

            int line = findShowLine(time);
            if (line != mCurrentLine) {
                LiveDataBus.get().with("my_lrc_highlight").postValue(line);
                LogUtils.d("current line : " + mCurrentLine + " to line : " + line);
                smoothMoveToPosition(mDataBinding.recycler, line);
                mCurrentLine = line;

            }
        });
    }

    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;

    /**
     * 使指定的项平滑到中间
     *
     * @param mRecyclerView
     * @param position      待指定的项
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        LogUtils.d("smooth move to position : " + position);

        if (isDragging) {
            LogUtils.d("but is dragging...");
            return;
        }

        int firstItemPosition = -1;
        int lastItemPosition = -1;

        // 判断是当前layoutManager是否为LinearLayoutManager
        // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            //获取第一个可见view的位置
            firstItemPosition = linearManager.findFirstVisibleItemPosition();
            //获取最后一个可见view的位置
            lastItemPosition = linearManager.findLastVisibleItemPosition();
        }

//        LogUtils.d("smoothMoveToPosition: firstItemPosition::" + firstItemPosition + " lastItemPosition::" + lastItemPosition + "\n");

        if (position < firstItemPosition) {
            // 第一种可能:跳转位置在第一个可见位置之前
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        } else if (position <= lastItemPosition) {
            // 第二种可能:跳转位置在第一个可见位置之后,在最后一个可见项之前
            int movePosition = position - firstItemPosition;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                Rect rect = new Rect();
                mRecyclerView.getGlobalVisibleRect(rect);
                int viewHeight = 0;
                View childAt = mDataBinding.recycler.getChildAt(position);
                if (childAt != null) {
                    viewHeight = childAt.getHeight();
                }

                int reHeight = rect.bottom - rect.top - viewHeight / 2;
                mRecyclerView.smoothScrollBy(0, top - reHeight / 2);//dx>0===>向左  dy>0====>向上
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }


    private int findShowLine(long time) {
        int left = 0;
        int right = mLrcEntryList.size();
        while (left <= right) {
            int middle = (left + right) / 2;
            long middleTime = mLrcEntryList.get(middle).getTime();

            if (time < middleTime) {
                right = middle - 1;
            } else {
                if (middle + 1 >= mLrcEntryList.size() || time < mLrcEntryList.get(middle + 1).getTime()) {
                    return middle;
                }

                left = middle + 1;
            }
        }

        return 0;
    }

    /**
     * 演示后开始回正
     */
    private void startBack() {
        removeCallbacks(backRunnable);
        postDelayed(backRunnable, AUTO_BACK_DURATION);
    }

    private Runnable backRunnable = () -> smoothMoveToPosition(mDataBinding.recycler, mCurrentLine);

    @SuppressLint("ClickableViewAccessibility")
    private void onLrcLoaded(List<LrcEntry> entryList) {
        // LogUtils.d("on loaded : " + entryList.size());
        mDataBinding.tvLoading.setVisibility(INVISIBLE);
        mDataBinding.recycler.setVisibility(VISIBLE);
        removeCallbacks(lrcLoadingRunnable);

        if (entryList != null && !entryList.isEmpty()) {
            mLrcEntryList.addAll(entryList);
        }

        heightMap.clear();
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mDataBinding.recycler.setLayoutManager(linearLayoutManager);
        adapter = new MyLrcAdapter(mLrcEntryList);

        mDataBinding.recycler.setAdapter(adapter);
        mDataBinding.recycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                int position = parent.getChildAdapterPosition(view);
                if (position == 0) {
//                    outRect.top = DensityUtils.dp2Px(mContext, 150);
                } else if (position == adapter.getData().size() - 1) {
                    outRect.bottom = DensityUtils.dp2Px(mContext, 150);
                }
            }
        });
        mDataBinding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {//
                    mShouldScroll = false;
                    smoothMoveToPosition(mDataBinding.recycler, mToPosition);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mDataBinding.recycler.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    isDragging = false;
                    startBack();
                    break;
                case MotionEvent.ACTION_DOWN:
                    isDragging = true;
                    break;
            }
            return false;
        });

        Collections.sort(mLrcEntryList);
        updateTime(0);

        initEntryList();
        invalidate();
    }

    private void initEntryList() {
        if (!hasLrc() || getWidth() == 0) {
            return;
        }
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
        runOnUi(() -> {
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
                        onLrcLoaded(lrcEntries);
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

        mDataBinding.recycler.setVisibility(INVISIBLE);
        mDataBinding.tvLoading.setVisibility(VISIBLE);
        post(lrcLoadingRunnable);
    }

    /**
     * 加载在线歌词
     *
     * @param lrcUrl  歌词文件的网络地址
     * @param charset 编码格式
     */
    @SuppressLint("StaticFieldLeak")
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

    /**
     * 歌词是否有效
     *
     * @return true，如果歌词有效，否则false
     */
    public boolean hasLrc() {
        return !mLrcEntryList.isEmpty();
    }

    /**
     * 在主线程中运行
     */
    private void runOnUi(Runnable r) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            r.run();
        } else {
            post(r);
        }
    }

    private Object mFlag;

    private Object getFlag() {
        return mFlag;
    }

    private void setFlag(Object flag) {
        this.mFlag = flag;
    }

    private void reset() {
//        endAnimation();
//        mScroller.forceFinished(true);
//        isShowTimeline = false;
//        isTouching = false;
//        isFling = false;
//        removeCallbacks(hideTimelineRunnable);
        mLrcEntryList.clear();
//        mOffset = 0;
        mCurrentLine = -1;
        invalidate();
    }

    public void setLabel(String string) {

    }

    private OnTapListener tapListener;

    public void setOnTapListener(OnTapListener tapListener) {
        this.tapListener = tapListener;
    }

    /**
     * 歌词控件点击监听器
     */
    public interface OnTapListener {
        /**
         * 歌词控件被点击
         *
         * @param view     歌词控件
         * @param showText
         * @param time
         */
        void onTap(MyLrc view, String showText, long time);
    }

    /**
     * 播放按钮点击监听器，点击后应该跳转到指定播放位置
     */
    public interface OnPlayClickListener {
        /**
         * 播放按钮被点击，应该跳转到指定播放位置
         *
         * @param view 歌词控件
         * @param time 选中播放进度
         * @return 是否成功消费该事件，如果成功消费，则会更新UI
         */
        boolean onPlayClick(LrcView view, long time);
    }

    /**
     * 设置歌词是否允许拖动
     *
     * @param draggable           是否允许拖动
     * @param onPlayClickListener 设置歌词拖动后播放按钮点击监听器，如果允许拖动，则不能为 null
     */
    public void setDraggable(boolean draggable, LrcView.OnPlayClickListener onPlayClickListener) {
    }

    class MyLrcAdapter extends BaseMultiItemQuickAdapter<LrcEntry, BaseDataBindingHolder> {

        public MyLrcAdapter(@Nullable List<LrcEntry> data) {
            super(data);
            addItemType(0, R.layout.layout_item_lyrics);
        }

        @Override
        protected void convert(@NonNull BaseDataBindingHolder baseDataBindingHolder, LrcEntry lrcEntry) {
            LayoutItemLyricsBinding dataBinding = (LayoutItemLyricsBinding) baseDataBindingHolder.getDataBinding();
            int position = adapter.getItemPosition(lrcEntry);

            dataBinding.tvLyrics.setText(lrcEntry.getText());

            LiveDataBus.get().with("my_lrc_highlight", Integer.class).observe((LifecycleOwner) mContext, integer -> {
                if (integer != null) {
                    boolean isHighLight = integer == position;
                    dataBinding.tvLyrics.setTextColor(Color.parseColor(isHighLight ? "#CCFFFFFF" : "#73FFFFFF"));
                    dataBinding.tvLyrics.setTypeface(null, isHighLight ? Typeface.BOLD : Typeface.NORMAL);
                }
            });

            dataBinding.getRoot().setOnClickListener(v -> {
                if (tapListener != null && System.currentTimeMillis() - lastItemClickTime > 800
                        && position != mCurrentLine) {
                    tapListener.onTap(MyLrc.this, lrcEntry.getText(), lrcEntry.getTime());
                    lastItemClickTime = System.currentTimeMillis();
                }
            });
        }
    }

}
