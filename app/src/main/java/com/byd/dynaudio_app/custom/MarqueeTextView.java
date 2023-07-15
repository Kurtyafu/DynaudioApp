package com.byd.dynaudio_app.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.widget.TextView;

import com.byd.dynaudio_app.utils.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by lum on 2019/1/15.
 */

@SuppressLint("AppCompatCustomView")
public class MarqueeTextView extends TextView {

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setMarqueeRepeatLimit(1);
        setFocusable(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setFocusableInTouchMode(true);
        setHorizontallyScrolling(true);

        setMarqueeListener(new OnMarqueeListener() {
            @Override
            public void onMarqueeRepeatChanged(int repeatLimit) {
                LogUtils.d("222222222222222 : " + repeatLimit);

                if (repeatLimit == 1){
                    setEllipsize(TextUtils.TruncateAt.END);
                    postInvalidate();
                }
            }
        });
    }

    public void setContent(String text) {
        if (!TextUtils.equals(text, getText().toString())) {
            setText(text);
            startMarqueeOnce();
        }
    }

    private void startMarqueeOnce() {
        setEllipsize(TextUtils.TruncateAt.MARQUEE);


    }

    @Override
    public boolean isFocused() {
        return true;
    }

    private static final byte MARQUEE_STOPPED = 0x0;
    private static final byte MARQUEE_STARTING = 0x1;
    private static final byte MARQUEE_RUNNING = 0x2;

    private OnMarqueeListener onMarqueeListener;

    public void setMarqueeListener(OnMarqueeListener onMarqueeListener) {
        this.onMarqueeListener = onMarqueeListener;
        try {
            Field marquee = this.getClass().getSuperclass().getDeclaredField("mMarquee");
            if(marquee != null) {
                marquee.setAccessible(true);
                Object obj = marquee.get(this);
                if(obj != null) {
                    Class cls = obj.getClass();
                    Field field = cls.getDeclaredField("mStatus");
                    if(field != null) {
                        field.setAccessible(true);
                    }
                    Field field1 = cls.getDeclaredField("mRestartCallback");
                    if(field1 != null) {
                        field1.setAccessible(true);
                        field1.set(obj, mRestartCallback);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Choreographer.FrameCallback mRestartCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {

            try {
                Field marquee = MarqueeTextView.this.getClass().getSuperclass().getDeclaredField("mMarquee");
                if(marquee != null) {
                    marquee.setAccessible(true);
                    Object obj = marquee.get(MarqueeTextView.this);
                    if(obj != null) {
                        Class cls = obj.getClass();
                        Field field = cls.getDeclaredField("mStatus");
                        if(field != null) {
                            field.setAccessible(true);
                            byte mStatus = ((Byte)field.get(obj)).byteValue();
                            if(mStatus == MARQUEE_RUNNING) {
                                Field field1 = cls.getDeclaredField("mRepeatLimit");
                                field1.setAccessible(true);
                                int mRepeatLimit = ((Integer)field1.get(obj)).intValue();;
                                if(mRepeatLimit >= 0) {
                                    mRepeatLimit--;
                                }
                                if(onMarqueeListener != null) {
                                    onMarqueeListener.onMarqueeRepeatChanged(mRepeatLimit);
                                }
                                Method method = cls.getDeclaredMethod("start", Integer.TYPE);
                                method.setAccessible(true);
                                method.invoke(obj, mRepeatLimit);
                            }
                        }
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    public interface OnMarqueeListener {
        void onMarqueeRepeatChanged(int repeatLimit);
    }

}


