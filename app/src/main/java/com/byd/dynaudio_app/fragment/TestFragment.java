package com.byd.dynaudio_app.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.custom.lrc.LrcEntry;
import com.byd.dynaudio_app.custom.lrc.LrcView;
import com.byd.dynaudio_app.databinding.LayoutFragmentTestBinding;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LogUtils;
import com.hw.lrcviewlib.LrcDataBuilder;
import com.hw.lrcviewlib.LrcRow;
import com.zlm.hp.lyrics.LyricsReader;

import java.util.List;

public class TestFragment extends BaseFragment<LayoutFragmentTestBinding, BaseViewModel> {

    private List<LrcRow> lrcRows;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private String testPath = "https://stream7.iqilu.com/10339/article/202002/17/c292033ef110de9f42d7d539fe0423cf.mp4";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
//        String lrcPath = "https://dynaudio.oss-cn-hangzhou.aliyuncs.com/resources/VinylRecord/Adele_25/249.lrc";
//        mDataBinding.lrc.loadLrcByUrl(lrcPath);









//        mDataBinding.lrc.loadLrcByUrl(lrcPath);
//        mDataBinding.lrc.setOnPlayClickListener(new LrcView.OnPlayClickListener() {
//            @Override
//            public boolean onPlayClick(LrcView view, long time) {
//                LogUtils.d("click time : " + time);
//                return false;
//            }
//        });
//
////        mDataBinding.getRoot().postDelayed(() -> mDataBinding.lrc.setTranslationY(
////                -mDataBinding.lrc.getHeight() / 2.f + DensityUtils.dp2Px(mContext, 50)), 100);
//        mDataBinding.lrc.setShowTimeLine(false);
//        mDataBinding.lrc.setOnTapListener(new LrcView.OnTapListener() {
//            @Override
//            public void onTap(LrcView view, float x, float y, LrcEntry time) {
//                if (time != null) {
//                    LogUtils.d("dy : " + y + " time : " + time.getTime()
//                            + " text : " + time.getText());
//                } else {
//                    LogUtils.d(" time :  null...");
//                }
//            }
//        });


//        startTest();
//
//
//        List<LrcRow> lrcRows = new LrcDataBuilder().BuiltFromAssets(mContext, "test_lrc.lrc");
        //ro  List<LrcRow> lrcRows = new LrcDataBuilder().Build(file);
        //mLrcView.setTextSizeAutomaticMode(true);//是否自动适配文字大小

//        //init the lrcView
//        mDataBinding.lrc.getLrcSetting().setTimeTextSize(40)//时间字体大小
//                .setSelectLineColor(Color.parseColor("#ffffff"))//选中线颜色
//                .setSelectLineTextSize(25)//选中线大小
//                .setHeightRowColor(Color.parseColor("#aaffffff"))//高亮字体颜色
//                .setNormalRowTextSize(DensityUtils.dp2Px(mContext, 17))//正常行字体大小
//                .setHeightLightRowTextSize(DensityUtils.dp2Px(mContext, 17))//高亮行字体大小
//                .setTrySelectRowTextSize(DensityUtils.dp2Px(mContext, 17))//尝试选中行字体大小
//                .setTimeTextColor(Color.parseColor("#ffffff"))//时间字体颜色
//                .setTrySelectRowColor(Color.parseColor("#55ffffff"));//尝试选中字体颜色
//
//        mDataBinding.lrc.commitLrcSettings();
//        mDataBinding.lrc.setLrcData(lrcRows);


//        TestUtils.setRecyclerFakeAdapter(mContext, mDataBinding.recycler, 30);


/*        mDataBinding.pzv.setOnRefreshListener(new SwipeLoadLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {

            }

            @Override
            public void onPullingDown(float dy, int pullOutDistance, float viewHeight) {
                LogUtils.d("dy11: " + dy);
            }
        });*/


//        mDataBinding.pzv.setIsZoomEnable(false);
//        mDataBinding.pzv.setIsParallax(true);
//        mDataBinding.pzv.setSensitive(1.5f);
//        mDataBinding.pzv.setZoomTime(500);


//        Glide.with(mContext)
//                .load("http://dynaudio.oss-cn-hangzhou.aliyuncs.com/banner_music.png")
//                .into(mDataBinding.imgHeader);
//        Glide.with(mContext)
//                .load("http://dynaudio.oss-cn-hangzhou.aliyuncs.com/banner_music.png")
//                .into(mDataBinding.imgTest);

//        mDataBinding.imgTest.setPivotY(mDataBinding.imgTest.getHeight());
//        mDataBinding.imgTest.setPivotX(mDataBinding.imgTest.getWidth() / 2);

//        mDataBinding.pzv.setOnPullZoomListener(new PullZoomView2.OnPullZoomListener() {
//
//            private int current;
//
//            @Override
//            public void onPullZoom(int originHeight, int currentHeight) {
//                super.onPullZoom(originHeight, currentHeight);
//                if (originHeight == 0) return;
//
//                if (mDataBinding.recycler.canScrollVertically(1)){
//                    float factor = currentHeight * 1.f / originHeight;
//
//                    mDataBinding.imgTest.setScaleY(factor);
//                    mDataBinding.imgTest.setScaleX(factor);
//
//                    mDataBinding.recycler.setTranslationY(currentHeight - originHeight);
//
//                    current = originHeight;
//                }
//
//                LogUtils.d("current : " + currentHeight + " origin : " + originHeight);
//            }
//
//            @Override
//            public void onZoomFinish() {
//                super.onZoomFinish();
//
//                onPullZoom(current, current);
//            }
//        });
//
//        mDataBinding.pzv.setOnScrollListener(new PullZoomView2.OnScrollListener() {
//            @Override
//            public void onScroll(int l, int t, int oldl, int oldt) {
//                super.onScroll(l, t, oldl, oldt);
//                LogUtils.d("ssss t : " + t
//                        + " old t :" + oldt);
//            }
//
//            @Override
//            public void onHeaderScroll(int currentY, int maxY) {
//                super.onHeaderScroll(currentY, maxY);
//                LogUtils.d("ssss currentY : " + currentY);
//            }
//
//            @Override
//            public void onContentScroll(int l, int t, int oldl, int oldt) {
//                super.onContentScroll(l, t, oldl, oldt);
//                LogUtils.d("ssss t : " + t);
//            }
//        });

//        mDataBinding.recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            private int lastY;
//
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                LogUtils.d("dy : " + dy);
//                // 随着recycler view下拉 将img view的高度逐渐放到到原来的1.5倍
//                // 随着recycler view往回拉 将img view的高度逐渐放到原来的1倍
//
//                if (dy != lastY) {
//                    int deltaY = dy - lastY;
//
//
//                    lastY = dy;
//                }
//            }
//        });
//
//        mDataBinding.recycler.setOnTouchListener(new View.OnTouchListener() {
//
//            private float rawY;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // 判断是否处于下拉 并且recycler已经不能下拉 并且获取此时的下拉的偏移量
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        rawY = event.getRawY();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        float dy = event.getRawY() - rawY; // 大于0下拉 小于0上拉
//                        LogUtils.d("dyyy : " + dy
//                                + " can move : " + mDataBinding.recycler.canScrollVertically(-1)
//                                + " current scale Y " + mDataBinding.imgTest.getScaleY());// 是否可下拉列表
//                        if (dy > 0) {
//                            mDataBinding.recycler.setTranslationY(dy);
//                            setImgHeight((1.f + dy / 500.f));
//                        } else {
//                            float scaleY = mDataBinding.imgTest.getScaleY();
//                            if (scaleY > 1) {
//                                // 缩小图
//                                setImgHeight((1.f + dy / 500.f));
//                            } else if (scaleY == 1) {
//                                // 只上拉
//                            }
//                        }
//
//
////                        }
//
//
//                        break;
//                }
//                return false;
//            }
//        });

//        mDataBinding.refreshLayout.setOnRefreshListener(new OnRefreshListener() { //下拉刷新
//            @Override
//            public void onRefresh(RefreshLayout refreshlayout) {
//                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
//            }
//        });
//
//        mDataBinding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() { //上拉加载更多
//            @Override
//            public void onLoadMore(RefreshLayout refreshlayout) {
//                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
//            }
//        });
//
//        mDataBinding.refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
//            @Override
//            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
//                super.onHeaderMoving(header, isDragging, percent, offset, headerHeight, maxDragHeight);
//                LogUtils.d(" is dragging : " + isDragging + " percent : " + percent + " offset : " + offset + " header height : " + headerHeight + " max drag height : " + maxDragHeight);
//                float factor = (offset + mDataBinding.imgTest.getHeight()) * 1.f / (mDataBinding.imgTest.getHeight() * 1.f);
//                mDataBinding.imgTest.setPivotX(mDataBinding.imgTest.getWidth() / 2);
//                mDataBinding.imgTest.setPivotY(mDataBinding.imgTest.getHeight());
//                mDataBinding.imgTest.setScaleY(factor);
//                mDataBinding.imgTest.setScaleX(factor);
//            }
//
//            @Override
//            public void onHeaderReleased(RefreshHeader header, int headerHeight, int maxDragHeight) {
//                super.onHeaderReleased(header, headerHeight, maxDragHeight);
//                LogUtils.d();
//                mDataBinding.refreshLayout.finishRefresh();
//            }
//        });
//
//        mDataBinding.refreshLayout.setRefreshHeader(new RefreshHeader() {
//            @NonNull
//            @Override
//            public View getView() {
//                View view = new View(mContext);
//                view.setBackgroundColor(Color.GRAY);
//                return view;
//            }
//
//            @NonNull
//            @Override
//            public SpinnerStyle getSpinnerStyle() {
//                return SpinnerStyle.Scale;
//            }
//
//            @SuppressLint("RestrictedApi")
//
//            @Override
//            public void setPrimaryColors(int... colors) {
//
//            }
//
//            @SuppressLint("RestrictedApi")
//            @Override
//            public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
//
//            }
//
//            @SuppressLint("RestrictedApi")
//            @Override
//            public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
//
//            }
//
//            @SuppressLint("RestrictedApi")
//            @Override
//            public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
//
//            }
//
//            @SuppressLint("RestrictedApi")
//
//            @Override
//            public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
//
//            }
//
//            @SuppressLint("RestrictedApi")
//
//            @Override
//            public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
//                return 0;
//            }
//
//            @SuppressLint("RestrictedApi")
//
//            @Override
//            public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
//
//            }
//
//            @Override
//            public boolean isSupportHorizontalDrag() {
//                return false;
//            }
//
//            @SuppressLint("RestrictedApi")
//
//            @Override
//            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
//
//            }
//        });


//        mDataBinding.playView.setDataSource(testPath);
//        mDataBinding.playView.start();

//        mDataBinding.jv.setUp(testPath,"我是视频标题");
//        mDataBinding.jv.startPreloading();


//        lrcRows = new LrcDataBuilder().BuiltFromAssets(mContext, "test_lrc.lrc");
//        for (LrcRow lrcRow : lrcRows) {
//            LogUtils.d("dddd ： " + lrcRow.getRowData());
//        }
//        //ro  List<LrcRow> lrcRows = new LrcDataBuilder().Build(file);
//        //mLrcView.setTextSizeAutomaticMode(true);//是否自动适配文字大小
//
//        //init the lrcView
//        mDataBinding.auLrcView.getLrcSetting().setTimeTextSize(40)//时间字体大小
//                .setSelectLineColor(Color.parseColor("#ffffff"))//选中线颜色
//                .setSelectLineTextSize(25)//选中线大小
//                .setHeightRowColor(Color.parseColor("#333333"))//高亮字体颜色
//                .setNormalRowTextSize(DensityUtils.dp2Px(mContext, 17))//正常行字体大小
//                .setHeightLightRowTextSize(DensityUtils.dp2Px(mContext, 17))//高亮行字体大小
//                .setTrySelectRowTextSize(DensityUtils.dp2Px(mContext, 17))//尝试选中行字体大小
//                .setTimeTextColor(Color.parseColor("#ffffff"))//时间字体颜色
//                .setTrySelectRowColor(Color.parseColor("#55ffffff"));//尝试选中字体颜色
//
//        mDataBinding.auLrcView.commitLrcSettings();
//        mDataBinding.auLrcView.setLrcData(lrcRows);
//        test();

//        TestUtils.setRecyclerFakeAdapter(mContext, mDataBinding.recycler, 30);

        doTopGradualEffect();


    }

    private long time;

    private void startTest() {
//        mDataBinding.getRoot().postDelayed(() -> {
//            mDataBinding.lrc.updateTime(time);
//            time += 100;
//            startTest();
//        }, 100);
    }

    private void setImgHeight(float factor) {
//        float translationY = (mDataBinding.imgTest.getScaleY() - factor) * DensityUtils.dp2Px(mContext, 300);
//        LogUtils.d("translation Y " + translationY);
//        scrollRecycler(translationY);
//
//        mDataBinding.imgTest.setScaleX(factor);
//        mDataBinding.imgTest.setScaleY(factor);
////        ViewGroup.LayoutParams layoutParams = mDataBinding.imgTest.getLayoutParams();
////        if (layoutParams != null) {
////            layoutParams.height = (int) height * DensityUtils.dp2Px(mContext, 300);
////            mDataBinding.imgTest.setLayoutParams(layoutParams);
////        }
    }

    private void scrollRecycler(float translationY) {
//        if ((translationY < 0 && mDataBinding.recycler.canScrollVertically(-1))
//                || (translationY > 0 && mDataBinding.recycler.canScrollVertically(1))) {
//            mDataBinding.recycler.scrollBy(0, (int) translationY);
//        } else {
//            mDataBinding.recycler.setTranslationY(translationY);
//        }
    }


    int layerId;

    public void doTopGradualEffect() {

        final Paint mPaint = new Paint();
        // 融合器
        final Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mPaint.setXfermode(xfermode);
        // 创造一个颜色渐变，作为聊天区顶部效果
        final LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 100.0f, new int[]{Color.parseColor("#00000000"), Color.parseColor("#FF000000")}, null, Shader.TileMode.CLAMP);

//        mDataBinding.recycler.addItemDecoration(new RecyclerView.ItemDecoration() {
//            // 滑动RecyclerView，渲染之后每次都会回调这个方法，就在这里进行融合
//            @Override
//            public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//                super.onDrawOver(canvas, parent, state);
//
//                mPaint.setXfermode(xfermode);
//                mPaint.setShader(linearGradient);
//                canvas.drawRect(0.0f, 0.0f, parent.getRight(), 200.0f, mPaint);
//                mPaint.setXfermode(null);
//                canvas.restoreToCount(layerId);
//            }
//
//            @Override
//            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//                super.onDraw(c, parent, state);
//                layerId = c.saveLayer(0.0f, 0.0f, (float) parent.getWidth(),
//                        (float) parent.getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
//            }
//
//            @Override
//            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
//                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//                super.getItemOffsets(outRect, view, parent, state);
//            }
//        });
    }

    private int current = 0;

    private void test() {
//        long lastRowTime = lrcRows.get(current).getCurrentRowTime();
//        current++;
//        long currentRowTime = lrcRows.get(current).getCurrentRowTime();
//
//        LogUtils.d("current : " + currentRowTime + " last : " + lastRowTime);
//        mDataBinding.auLrcView.smoothScrollToTime(currentRowTime);
//
//        long delta = currentRowTime - lastRowTime;
//        mDataBinding.getRoot().postDelayed(() -> {
//            test();
//        }, delta);
    }


    @Override
    protected void initListener() {

    }

    @Override
    protected void initObserver() {

    }

    @Override
    protected BaseViewModel getViewModel() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_test;
    }
}
