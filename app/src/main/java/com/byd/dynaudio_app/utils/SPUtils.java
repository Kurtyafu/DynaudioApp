package com.byd.dynaudio_app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.byd.dynaudio_app.bean.response.SingerBean;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SPUtils {

    public static final String SP_KEY_USER_ID = "SP_KEY_USER_ID";
    public static final String SP_KEY_TOKEN = "SP_KEY_TOKEN";
    public static final String SP_KEY_DEMO_MODE = "SP_KEY_DEMO_MODE";
    public static final String SP_KEY_FIRST_RUN = "SP_KEY_FIRST_RUN";

    private final static String spName = "DynAudio";

    public static final String PLAYMODEKEY = "PLAYMODE";

    public static final Integer LISTLOOP = 1;
    public static final Integer SIGLECYCLE = 2;
    public static final Integer RANDOMCYCLE = 3;


    public static void putValue(Context context, String key, Object value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            edit.putFloat(key, (Float) value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            edit.putLong(key, (Long) value);
        } else if (value instanceof String) {
            edit.putString(key, (String) value);
        }
        edit.apply();
    }

    public static Object getValue(Context context, String key, Object defValue) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        if (defValue instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defValue);
        } else if (defValue instanceof Float) {
            return sp.getFloat(key, (Float) defValue);
        } else if (defValue instanceof Integer) {
            return sp.getInt(key, (Integer) defValue);
        } else if (defValue instanceof Long) {
            return sp.getLong(key, (Long) defValue);
        } else if (defValue instanceof String) {
            return sp.getString(key, (String) defValue);
        }
        return null;
    }

    public static void clearSP(Context context) {
        context.getSharedPreferences(spName, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }

    public static void removeSP(Context context, String Key) {
        context.getSharedPreferences(spName, Context.MODE_PRIVATE)
                .edit()
                .remove(Key)
                .apply();
    }

    public static Map<String, ?> getAllSP(Context context) {
        return context.getSharedPreferences(spName, Context.MODE_PRIVATE).getAll();
    }

    public static String formatTime(long timeMs) {
        int seconds = (int) (timeMs / 1000) % 60;
        int minutes = (int) ((timeMs / (1000 * 60)) % 60);
        int hours = (int) ((timeMs / (1000 * 60 * 60)) % 24);

        // Format the time string as "hh:mm:ss" or "mm:ss"
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public static String formatTime2(long timeMs) {
        int seconds = (int) (timeMs / 1000) % 60;
        int minutes = (int) ((timeMs / (1000 * 60)) % 60);
        int hours = (int) ((timeMs / (1000 * 60 * 60)) % 24);

        // Format the time string as "hh:mm:ss" or "mm:ss"
        if (hours > 0) {
            return String.format("%02d'%02d'%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d'%02d", minutes, seconds);
        }
    }

    public static String formatTime3(long timeMs) {
        int seconds = (int) (timeMs / 1000) % 60;
        int minutes = (int) ((timeMs / (1000 * 60)) % 60);
        int hours = (int) ((timeMs / (1000 * 60 * 60)) % 24);

        // Format the time string as "hh:mm:ss" or "mm:ss"
        if (hours > 0) {
            return String.format("%02d小时%02d分钟", hours, minutes);
        } else {
            return String.format("%02d分钟", minutes);
        }
    }

    private static final String[] SIZE_SUFFIXES = {"B", "KB", "MB", "GB", "TB"};

    public static String formatSize(long size) {
        // 方法是针对b的 后台实际上是kb
//        size *= 1024;

        if (size <= 0) {
            return "0B";
        }
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.2f%s", size / Math.pow(1024, digitGroups), SIZE_SUFFIXES[digitGroups]);
    }

    /**
     * 获取安卓机型 判断是不是 "CMR-W19"
     */
    public static boolean isPad() {
        String model = Build.MODEL;
        LogUtils.d("model : " + model);
        return model != null && (model.equals("CMR-W19")
                || model.contains("81AC")
                || model.equals("VOG-AL00"));
    }

    public static boolean isTest() {
        return true;
    }

    public static void setBlurredBackground(ImageView imageView, Context context) {
        LogUtils.d();
        // 创建圆形形状drawable对象
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.getPaint().setColor(Color.parseColor("#4DFFFFFF"));

        // 创建蒙层drawable对象
        Drawable[] layers = new Drawable[2];
        layers[0] = imageView.getBackground(); // 播放按钮背景
        layers[1] = shapeDrawable; // 圆形蒙层
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        // 设置圆形蒙层的位置和大小
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        layerDrawable.setLayerSize(1, imageView.getWidth(), imageView.getHeight());

        // 将蒙层drawable对象设置为播放按钮的背景
        imageView.setBackground(layerDrawable);


//        // 获取ImageView的背景Drawable
//        Drawable backgroundDrawable = imageView.getDrawable();
//
//        // 创建蒙层drawable对象
//        Drawable[] layers = new Drawable[2];
//        layers[0] = backgroundDrawable; // ImageView的背景Drawable
//        layers[1] = new ColorDrawable(Color.parseColor("#1A000000")); // 半透明黑色蒙层
//        LayerDrawable layerDrawable = new LayerDrawable(layers);
//
//        // 创建背景模糊效果
//        BitmapDrawable bitmapDrawable = (BitmapDrawable) backgroundDrawable;
//        Bitmap blurredBitmap = blurBitmap(bitmapDrawable.getBitmap(), DensityUtils.dp2Px(context,33)/2,context); // 模糊半径为25
//        BitmapDrawable blurredDrawable = new BitmapDrawable(context.getResources(), blurredBitmap);
//        blurredDrawable.setAlpha(10); // 降低模糊图片的不透明度
//
//        // 将模糊图片添加为LayerDrawable的第三个层
//        // layerDrawable.addLayer(blurredDrawable);
//
//        // 将LayerDrawable设置为ImageView的背景
//        imageView.setBackground(layerDrawable);
    }

    // 使用高斯模糊算法模糊指定的Bitmap
    private static Bitmap blurBitmap(Bitmap bitmap, int radius, Context context) {
        RenderScript renderScript = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation output = Allocation.createTyped(renderScript, input.getType());

        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        script.setInput(input);
        script.setRadius(radius);
        script.forEach(output);

        output.copyTo(bitmap);

        input.destroy();
        output.destroy();
        script.destroy();
        renderScript.destroy();

        return bitmap;
    }

    public static String formatAuther(List<SingerBean> autherList) {
        String res = "";
        if (autherList != null && autherList.size() > 0) {
            for (int i = 0; i < autherList.size(); i++) {
                SingerBean singerBean = autherList.get(i);
                if (i != 0) res += ",";
                res += singerBean.getAutherName();
            }
        }
        return res;
    }

    public static String formatContent(String jsonString) {
        jsonString = "[" + jsonString + "]";

        String res = "";
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonString);

            JSONObject jsonObject = jsonArray.getJSONObject(0);

            Map<String, String> map = new HashMap<>();
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                JSONArray valueArray = jsonObject.getJSONArray(key);
                String value = valueArray.getString(0);
                map.put(key, value);
            }

            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                res += key + " : " + value + "  ";
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public static void setViewWidth(@NonNull View view, int width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = width;
            view.setLayoutParams(layoutParams);
        }
    }

    public static void setViewHeight(@NonNull View view, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);
        }
    }

    public static void setViewMarginStart(@NonNull View view, int marginStart) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.setMarginStart(marginStart);
            view.setLayoutParams(layoutParams);
        }
    }

    public static void setViewMarginEnd(@NonNull View view, int marginEnd) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.setMarginEnd(marginEnd);
            view.setLayoutParams(layoutParams);
        }
    }

    public static String getUpdateStr(String updateTime) {
        try {
            long timestamp = Long.parseLong(updateTime) * 1000L; // 转换为毫秒级时间戳
            Date currentDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);

            if (isSameDay(calendar, currentDate)) {
                return "今日更新";
            } else if (isYesterday(calendar, currentDate)) {
                return "昨天";
            } else if (isSameYear(calendar, currentDate)) {
                SimpleDateFormat format = new SimpleDateFormat("M月d日", Locale.getDefault());
                return format.format(calendar.getTime());
            } else {
                SimpleDateFormat format = new SimpleDateFormat("yyyy年M月d日", Locale.getDefault());
                return format.format(calendar.getTime());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "今日更新";
        }
    }

    private static boolean isSameDay(Calendar cal1, Date date2) {
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private static boolean isYesterday(Calendar cal1, Date date2) {
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        cal2.add(Calendar.DAY_OF_MONTH, -1); // 将日期减去1天
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private static boolean isSameYear(Calendar cal1, Date date2) {
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }


    /**
     * 使用SharedPreferences保存List
     * 支持类型：List<String>，List<JavaBean>
     *
     * @param context  上下文
     * @param key      储存的key
     * @param dataList 存储数据
     * @param <T>      泛型
     */
    public static <T> void setDataList(Context context, String key, List<T> dataList) {
        if (null == dataList || dataList.size() < 0) {
            return;
        }

        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(dataList);
        editor.putString(key, strJson);
        editor.apply();
    }


    /**
     * 获取SharedPreferences保存的List
     *
     * @param context 上下文
     * @param key     储存的key
     * @param <T>     泛型
     * @return 存储List<T>数据
     */
    public static <T> List<T> getDataList(Context context, String key, Class<T> cls) {

        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        List<T> dataList = new ArrayList<T>();
        String strJson = sp.getString(key, null);
        if (null == strJson) {
            return dataList;
        }
        Gson gson = new Gson();
        //使用泛型解析数据会出错，返回的数据类型是LinkedTreeMap
        //        dataList = gson.fromJson(strJson, new TypeToken<List<T>>() {
        //        }.getType());

        //这样写，太死
        //        dataList = gson.fromJson(strJson, new TypeToken<List<UserModel>>() {
        //        }.getType());

        JsonArray arry = new JsonParser().parse(strJson).getAsJsonArray();
        for (JsonElement jsonElement : arry) {
            dataList.add(gson.fromJson(jsonElement, cls));
        }
        return dataList;
    }

    public static void startMarqueeIfNeeded(@NonNull TextView textView) {
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setSingleLine(true);
        textView.requestFocus(0);
        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setSelected(false);
            }
        }, calculateDuration(textView));
    }

    private static long calculateDuration(TextView textView) {
        int textWidth = (int) textView.getPaint().measureText(textView.getText().toString());
        int viewWidth = textView.getWidth();
        float speed = 0.3f; // 轮播速度，可根据需要调整

        if (textWidth > viewWidth) {
            return (long) (textWidth / speed);
        } else {
            return 0;
        }
    }
}