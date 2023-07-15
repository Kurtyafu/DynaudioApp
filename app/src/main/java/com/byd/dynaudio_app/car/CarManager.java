package com.byd.dynaudio_app.car;

import android.content.Context;
import android.hardware.bydauto.audio.AbsBYDAutoAudioListener;
import android.hardware.bydauto.audio.BYDAutoAudioDevice;
import android.hardware.bydauto.gearbox.AbsBYDAutoGearboxListener;
import android.hardware.bydauto.gearbox.BYDAutoGearboxDevice;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.byd.dynaudio_app.BuildConfig;
import com.byd.dynaudio_app.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class CarManager {
    private static CarManager instance;
    private Context mContext;

    private CarManager() {
    }

    public static CarManager getInstance() {
        if (instance == null) {
            instance = new CarManager();
        }
        return instance;
    }

    public CarManager init(@NonNull Context context) {
        mContext = context;

        try {
            BYDAutoAudioDevice.getInstance(mContext)
                    .registerListener(new AbsBYDAutoAudioListener() {
                        @Override
                        public void onDynaSoundFeatureStatus(int area, int status) {
                            super.onDynaSoundFeatureStatus(area, status);
                            for (CarAudioListener listener : listeners) {
                                listener.onDynaSoundFeatureStatus(area, status);
                            }
                        }

                        @Override
                        public void onDyna3DSoundEffect(int status) {
                            super.onDyna3DSoundEffect(status);
                            for (CarAudioListener listener : listeners) {
                                listener.onDyna3DSoundEffect(status);
                            }
                        }

                        @Override
                        public void onDynaHeadrestSoundEffect(int status) {
                            super.onDynaHeadrestSoundEffect(status);
                            for (CarAudioListener listener : listeners) {
                                listener.onDynaHeadrestSoundEffect(status);
                            }
                        }

                        @Override
                        public void onDynaHeadrestSpeakerConfig(int status) {
                            super.onDynaHeadrestSpeakerConfig(status);
                            for (CarAudioListener listener : listeners) {
                                listener.onDynaHeadrestSpeakerConfig(status);
                            }
                        }

                        @Override
                        public void onDyna3DSoundSpeakerConfig(int status) {
                            super.onDyna3DSoundSpeakerConfig(status);
                            for (CarAudioListener listener : listeners) {
                                listener.onDyna3DSoundSpeakerConfig(status);
                            }
                        }

                        @Override
                        public void onDynaSoundFeatures(int status) {
                            super.onDynaSoundFeatures(status);
                            for (CarAudioListener listener : listeners) {
                                listener.onDynaSoundFeatures(status);
                            }
                        }

                        @Override
                        public void onDynaGamut(int area, int status) {
                            super.onDynaGamut(area, status);
                            for (CarAudioListener listener : listeners) {
                                listener.onDynaGamut(area, status);
                            }
                        }

                        @Override
                        public void onDynaSoundField(int area, int status) {
                            super.onDynaSoundField(area, status);
                            for (CarAudioListener listener : listeners) {
                                listener.onDynaSoundField(area, status);
                            }
                        }

                        @Override
                        public void onDynaSoundFieldFocus(int status) {
                            super.onDynaSoundFieldFocus(status);
                            for (CarAudioListener listener : listeners) {
                                listener.onDynaSoundFieldFocus(status);
                            }
                        }
                    });

            BYDAutoGearboxDevice.getInstance(mContext).registerListener(new AbsBYDAutoGearboxListener() {
                @Override
                public void onCurrentGearChanged(int gear) {
                    super.onCurrentGearChanged(gear);

                    dispatchPMode();
                }
            });
        } catch (RuntimeException e) {
            handleException(e);
        }
        return this;
    }

    private static Handler handler = new Handler(Looper.getMainLooper());

    public static void showToast(final Context context, final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<CarAudioListener> listeners = new ArrayList<>();

    private void handleException(RuntimeException e) {
        LogUtils.d("车机lib包错误 ： " + e.toString());
    }

    /**
     * 注册监听 所有的回调都在这里面 选择需要的实现即可
     */
    public void registerListener(CarAudioListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * 获取声场聚焦 默认返回1
     * 0：无效
     * 0x1:All 全车
     * 0x2:Driver 主驾驶
     * 0x3:Passenger 副驾驶
     * 0x4:Rear 后排
     * 0x5:Surround 环绕
     * 0x6:FaderBlance 自定义
     * 0x7：预留
     */
    public int getDynaSoundFieldFocus() {
        try {
            int value = BYDAutoAudioDevice.getInstance(mContext)
                    .getDynaSoundFieldFocus();
            LogUtils.d("value : " + value);
            return value;
        } catch (RuntimeException e) {
            handleException(e);
        }
        return 2;
    }

    /**
     * 设置声场聚焦
     * 0：无效
     * 0x1:All 全车
     * 0x2:Driver 主驾驶
     * 0x3:Passenger 副驾驶
     * 0x4:Rear 后排
     * 0x5:Surround 环绕
     * 0x6:FaderBlance 自定义
     * 0x7：预留
     */
    public void setDynaSoundFieldFocus(int value) {
        LogUtils.d("context : " + mContext);
        try {
            LogUtils.d("value : " + value);
            BYDAutoAudioDevice.getInstance(mContext)
                    .setDynaSoundFieldFocus(value);
        } catch (RuntimeException e) {
            handleException(e);
        }
    }

    /**
     * 声场聚焦 自定义重置
     * 0x0：无效
     * 0x1：重置
     * 默认值:0x0
     */
    public void setResetDynaSoundFieldFoucs(int value) {
        try {
            LogUtils.d("value : " + value);
            BYDAutoAudioDevice.getInstance(mContext)
                    .setResetDynaSoundFieldFoucs(value);
        } catch (RuntimeException e) {
            handleException(e);
        }
    }

    /**
     * 获取声音特色
     * 0x0:无效
     * 0x1:原声
     * 0x2:动感
     * 0x3:柔和
     * 0x4:演说
     * 0x5:自定义
     * 0x6:声音特色
     * 0x7~0xF:预留
     * 默认值：0x1
     */
    public int getDynaSoundFeatures() {
        try {
            int value = BYDAutoAudioDevice.getInstance(mContext)
                    .getDynaSoundFeatures();
            LogUtils.d("value : " + value);
            return value;
        } catch (RuntimeException e) {
            handleException(e);
        }
        return 0;
    }

    /**
     * 设置声音特色
     * 0x0:无效
     * 0x1:原声
     * 0x2:动感
     * 0x3:柔和
     * 0x4:演说
     * 0x5:自定义
     * 0x6:声音特色
     * 0x7~0xF:预留
     * 默认值：0x1
     */
    public void setDynaSoundFeatures(int value) {
        try {
            LogUtils.d("value : " + value);
            BYDAutoAudioDevice.getInstance(mContext)
                    .setDynaSoundFeatures(value);
        } catch (RuntimeException e) {
            handleException(e);
        }
    }

    /**
     * 设置左右、前后声音特色
     * <p>
     * area:
     * DYNA_FRONT_REAR = 1;
     * DYNA_LEFT_RIGHT = 2;
     * value:
     * DYNA_FRONT_SOUND_FILED_THITREEN = 3;
     * DYNA_FRONT_SOUND_FILED_TWELFTH  = 4;
     * DYNA_FRONT_SOUND_FILED_ELEVENTH = 5;
     * DYNA_FRONT_SOUND_FILED_TENTH    = 6;
     * DYNA_FRONT_SOUND_FILED_NINTH    = 7;
     * DYNA_FRONT_SOUND_FILED_EIGHTH   = 8;
     * DYNA_FRONT_SOUND_FILED_SEVENTH  = 9;
     * DYNA_FRONT_SOUND_FILED_SIXTH    = 0xA;
     * DYNA_FRONT_SOUND_FILED_FIFTH    = 0xB;
     * DYNA_FRONT_SOUND_FILED_FOURTH   = 0xC;
     * DYNA_FRONT_SOUND_FILED_THIRD    = 0xD;
     * DYNA_FRONT_SOUND_FILED_SECOND   = 0xE;
     * DYNA_FRONT_SOUND_FILED_FIRST    = 0xF;
     * DYNA_MIDDLE                     = 0x10;
     * DYNA_REAR_SOUND_FILED_FIRST     = 0x11;
     * DYNA_REAR_SOUND_FILED_SECOND    = 0x12;
     * DYNA_REAR_SOUND_FILED_THIRD     = 0x13;
     * DYNA_REAR_SOUND_FILED_FOURTH    = 0x14;
     * DYNA_REAR_SOUND_FILED_FIFTH     = 0x15;
     * DYNA_REAR_SOUND_FILED_SIXTH     = 0x16;
     * DYNA_REAR_SOUND_FILED_SEVENTH   = 0x17;
     * DYNA_REAR_SOUND_FILED_EIGHTH    = 0x18;
     * DYNA_REAR_SOUND_FILED_NINTH     = 0x19;
     * DYNA_REAR_SOUND_FILED_TENTH     = 0x1A;
     * DYNA_REAR_SOUND_FILED_ELEVENTH  = 0x1B;
     * DYNA_REAR_SOUND_FILED_TWELFTH   = 0x1C;
     * DYNA_REAR_SOUND_FILED_THITREEN  = 0x1D;
     */
    public void setDynaSoundFeatureStatus(int area, int value) {
        try {
            LogUtils.d("area : " + area + " value : " + value);

            // x 最大值范围是+-15 y是+-13
            if (area == 2) {
                if (value < -15) {
                    value = -15;
                }
                if (value > 15) {
                    value = 15;
                }
            } else if (area == 1) {
                if (value < -13) {
                    value = -13;
                }
                if (value > 13) {
                    value = 13;
                }
            }
//            value += 16;
//            LogUtils.d("after area : " + area + " value : " + Integer.toHexString(value));
            BYDAutoAudioDevice.getInstance(mContext)
                    .setDynaSoundFeatureStatus(area, value);
        } catch (RuntimeException e) {
            handleException(e);
        }
    }

    public int getDynaSoundFeatureStatus(int area) {
        try {
            int value = BYDAutoAudioDevice.getInstance(mContext)
                    .getDynaSoundFeatureStatus(area);
            LogUtils.d("area : " + area + "value :" + Integer.toHexString(value));
//            value -= 16;
//            LogUtils.d("after area : " + area + "value :" + value);
            return value;
        } catch (RuntimeException e) {
            handleException(e);
        }
        return 0;
    }

    /**
     * 声音特色重置
     * 0x00：无效
     * 0x01：重置
     */
    public void setResetDynaSoundFeature(int value) {
        try {
            LogUtils.d("value : " + value);
            BYDAutoAudioDevice.getInstance(mContext)
                    .setResetDynaSoundFeature(value);
        } catch (RuntimeException e) {
            handleException(e);
        }
    }

    /**
     * 获取重低音、低音、中音、高音
     * 0x1~0x1F：1~31
     * 0x0：无效
     * 有效值：
     * 0x7-0xF：-9到-1
     * 0x10:0
     * 0x11-0x19：1-9
     * 预留：0x1-0x6、0x1A-0x1F
     * 默认值：0x10
     */
    public int getDynaGamut(int area) { // 输入值-9到+9即可
        try {
            int value = BYDAutoAudioDevice.getInstance(mContext)
                    .getDynaGamut(area) - 16;
            LogUtils.d("area : " + area + " value : " + (value + 16));
            return value >= -9 && value <= 9 ? value : 0;
        } catch (RuntimeException e) {
            handleException(e);
        }
        return 0;
    }

    /**
     * 设置重低音1、低音2、中音3、高音4
     * 0x1~0x1F：1~31
     * 0x0：无效
     * 有效值：
     * 0x7-0xF：-9到-1
     * 0x10:0
     * 0x11-0x19：1-9
     * 预留：0x1-0x6、0x1A-0x1F
     * 默认值：0x10
     */
    public void setDynaGamut(int area, int value) {
        try {
            // 直接输入-9 到 9 解析成它要的
            value += 16;
            LogUtils.d("area : " + area + "value : " + value);
            BYDAutoAudioDevice.getInstance(mContext)
                    .setDynaGamut(area, value);
        } catch (RuntimeException e) {
            handleException(e);
        }
    }

    /**
     * 获取3d沉浸音效
     * 0x0:无效
     * 0x1:关闭
     * 0x2:Level1
     * 0x3:Level2
     * 0x4:Level3
     * 0x5:Level4
     * 0x6:Level5
     * 0x7:预留
     * 默认值：0x1
     */
    public int getDyna3DSoundEffect() {
        try {
            int value = BYDAutoAudioDevice.getInstance(mContext)
                    .getDyna3DSoundEffect();
            LogUtils.d(" value " + value);
            return value;
        } catch (RuntimeException e) {
            handleException(e);
        }
        return 0;
    }

    /**
     * 设置3d沉浸音效
     * 0x0:无效
     * 0x1:关闭
     * 0x2:Level1
     * 0x3:Level2
     * 0x4:Level3
     * 0x5:Level4
     * 0x6:Level5
     * 0x7:预留
     * 默认值：0x1
     */
    public void setDyna3DSoundEffect(int value) {
        try {
            LogUtils.d(" value " + (value));
            BYDAutoAudioDevice.getInstance(mContext)
                    .setDyna3DSoundEffect(value);
        } catch (RuntimeException e) {
            handleException(e);
        }
    }

    /**
     * 设置声场设置 ---声场聚焦调节
     *
     * @param area  1表示前后 2表示左右
     * @param value 0x7 到 0x19          0x7
     */
    public void setDynaSoundField(int area, int value) {
        try {
            LogUtils.d("area : " + area + " value " + (value + 16));
            BYDAutoAudioDevice.getInstance(mContext)
                    .setDynaSoundField(area, value + 16);
        } catch (RuntimeException e) {
            handleException(e);
        }
    }

    /**
     * 获取声场设置  --- 声场聚焦调节
     *
     * @param area 1表示前后 2表示左右
     * @return -9 到 9
     */
    public int getDynaSoundField(int area) {
        try {
            int value = BYDAutoAudioDevice.getInstance(mContext)
                    .getDynaSoundField(area) - 16;
            LogUtils.d("area : " + area + " value " + (BYDAutoAudioDevice.getInstance(mContext)
                    .getDynaSoundField(area)));
            if (value < -9) value = -9;
            if (value > 9) value = 9;
            return value;
        } catch (RuntimeException e) {
            handleException(e);
        }
        return 0;
    }

    private boolean flag = true;

    /**
     * 返回是否p挡 非p挡不让播视频
     */
    public boolean isPMode() {
        try {
            return BYDAutoGearboxDevice.getInstance(mContext).getCurrentGear() == BYDAutoGearboxDevice.GEAR_P;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.equals(BuildConfig.HOST, "prod")) {
            // 非生产 并且车机信息报错，就返回假的数据用于测试
            return flag;
        }
        return true;
    }

    private List<PModeListener> pModeListeners;

    public void addPModeListener(PModeListener pModeListener) {
        if (pModeListeners == null) {
            pModeListeners = new ArrayList<>();
        }
        pModeListeners.add(pModeListener);
    }

    public void dispatchPMode() {
        if (pModeListeners != null) {
            for (PModeListener listener : pModeListeners) {
                if (listener != null) {
                    listener.onPModeChange(isPMode());
                }
            }
        }
    }

    public void trySendP(boolean flag) {
        this.flag = flag;
        dispatchPMode();
        showToast(mContext, "设置了p裆为: " + flag);
    }
}

