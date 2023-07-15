package com.byd.dynaudio_app.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.response.SingerBean;
import com.byd.dynaudio_app.user.UserController;
import com.byd.dynaudio_app.utils.SPUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint({"Range", "Recycle"})
public class DBController {
    public static void insertMusicCollect(Context context, MusicListBean bean) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        String id = String.valueOf(bean.getSpecialId());
        Cursor cursor = db.query(DBApi.TABLE_NAME_MUSIC_COLLECT, new String[]{"specialId"}, "specialId = ?", new String[]{id}, null, null, null);
        // 数据已存在，删除在尾部添加，更新位置
        if (cursor.getCount() != 0) {
            db.delete(DBApi.TABLE_NAME_MUSIC_COLLECT, "specialId = ?", new String[]{id});
        }
        ContentValues values = createContentValues(bean);
        db.insert(DBApi.TABLE_NAME_MUSIC_COLLECT, null, values);
        values.clear();
        db.close();
    }

    public static void deleteMusicCollect(Context context, MusicListBean bean) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        String id = String.valueOf(bean.getSpecialId());
        db.delete(DBApi.TABLE_NAME_MUSIC_COLLECT, "specialId = ?", new String[]{id});
        db.close();
    }

    public static List<MusicListBean> queryMusicCollect(Context context) {
        List<MusicListBean> list = new ArrayList<>();
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.query(DBApi.TABLE_NAME_MUSIC_COLLECT, null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                MusicListBean bean = getField(cursor);
                list.add(bean);
            }
        }
        cursor.close();
        db.close();
        Collections.reverse(list);
        return list;
    }

    /**
     * 返回一个对象 collectFlag
     */
    public static boolean queryMusicCollect(@NonNull Context context, MusicListBean bean) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.query(DBApi.TABLE_NAME_MUSIC_COLLECT, new String[]{"collectFlag"}, "specialId=?", new String[]{String.valueOf(bean.getSpecialId())}, null, null, null);
        boolean collectFlag = false;
        if (cursor.moveToFirst()) {
            collectFlag = cursor.getInt(cursor.getColumnIndex("collectFlag")) == 1;
        }
        cursor.close();
        db.close();
        return collectFlag;
    }

    /**
     * 返回一个对象 collectFlag
     */
    public static boolean queryAudioCollect(@NonNull Context context, MusicListBean bean) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.query(DBApi.TABLE_NAME_AUDIO_COLLECT, new String[]{"collectFlag"}, "specialId=?", new String[]{String.valueOf(bean.getSpecialId())}, null, null, null);
        boolean collectFlag = false;
        if (cursor.moveToFirst()) {
            collectFlag = cursor.getInt(cursor.getColumnIndex("collectFlag")) == 1;
        }
        cursor.close();
        db.close();
        return collectFlag;
    }

    public static void insertAudioCollect(Context context, MusicListBean bean) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        String id = String.valueOf(bean.getSpecialId());
        Cursor cursor = db.query(DBApi.TABLE_NAME_AUDIO_COLLECT, new String[]{"specialId"}, "specialId = ?", new String[]{id}, null, null, null);
        // 数据已存在，删除在尾部添加，更新位置
        if (cursor.getCount() != 0) {
            db.delete(DBApi.TABLE_NAME_AUDIO_COLLECT, "specialId = ?", new String[]{id});
        }
        ContentValues values = createContentValues(bean);
        db.insert(DBApi.TABLE_NAME_AUDIO_COLLECT, null, values);
        values.clear();
        db.close();
    }

    public static void deleteAudioCollect(Context context, MusicListBean bean) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        String id = String.valueOf(bean.getSpecialId());
        db.delete(DBApi.TABLE_NAME_AUDIO_COLLECT, "specialId = ?", new String[]{id});
        db.close();
    }

    public static List<MusicListBean> queryAudioCollect(Context context) {
        List<MusicListBean> list = new ArrayList<>();
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.query(DBApi.TABLE_NAME_AUDIO_COLLECT, null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                MusicListBean bean = getField(cursor);
                list.add(bean);
            }
        }
        cursor.close();
        db.close();
        Collections.reverse(list);
        return list;
    }

    public static void insertMusicRecord(@NonNull Context context, MusicListBean bean) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        String id = String.valueOf(bean.getSpecialId());
        Cursor cursor = db.query(DBApi.TABLE_NAME_MUSIC_RECORD, new String[]{"specialId"}, "specialId = ?", new String[]{id}, null, null, null);
        // 数据已存在，删除在尾部添加，更新位置
        if (cursor.getCount() != 0) {
            db.delete(DBApi.TABLE_NAME_MUSIC_RECORD, "specialId = ?", new String[]{id});
        }
        ContentValues values = createContentValues(bean);
        db.insert(DBApi.TABLE_NAME_MUSIC_RECORD, null, values);
        values.clear();
        db.close();
    }

    public static void deleteMusicRecord(Context context, MusicListBean bean) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        String id = String.valueOf(bean.getSpecialId());
        db.delete(DBApi.TABLE_NAME_MUSIC_RECORD, "specialId = ?", new String[]{id});
        db.close();
    }

    public static List<MusicListBean> queryMusicRecord(Context context) {
        List<MusicListBean> list = new ArrayList<>();
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.query(DBApi.TABLE_NAME_MUSIC_RECORD, null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                MusicListBean bean = getField(cursor);
                boolean collect = queryMusicCollect(context, bean);
                bean.setCollectFlag(collect);
                list.add(bean);
            }
        }
        cursor.close();
        db.close();
        Collections.reverse(list);
        return list;
    }

    public static void insertAudioRecord(@NonNull Context context, MusicListBean bean) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        String id = String.valueOf(bean.getSpecialId());
        Cursor cursor = db.query(DBApi.TABLE_NAME_AUDIO_RECORD, new String[]{"specialId"}, "specialId = ?", new String[]{id}, null, null, null);
        // 数据已存在，删除在尾部添加，更新位置
        if (cursor.getCount() != 0) {
            db.delete(DBApi.TABLE_NAME_AUDIO_RECORD, "specialId = ?", new String[]{id});
        }
        ContentValues values = createContentValues(bean);
        db.insert(DBApi.TABLE_NAME_AUDIO_RECORD, null, values);
        values.clear();
        db.close();
    }

    public static void deleteAudioRecord(Context context, MusicListBean bean) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        String id = String.valueOf(bean.getSpecialId());
        db.delete(DBApi.TABLE_NAME_AUDIO_RECORD, "specialId = ?", new String[]{id});
        db.close();
    }

    public static List<MusicListBean> queryAudioRecord(Context context) {
        List<MusicListBean> list = new ArrayList<>();
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.query(DBApi.TABLE_NAME_AUDIO_RECORD, null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                MusicListBean bean = getField(cursor);
                boolean collect = queryAudioCollect(context, bean);
                bean.setCollectFlag(collect);
                list.add(bean);
            }
        }
        cursor.close();
        db.close();
        Collections.reverse(list);
        return list;
    }

    /**
     * 添加当前播放专辑，应用退出前调用，下次打开后播放此专辑
     */
    public static void insertLastAlbum(@NonNull Context context, List<MusicListBean> list) {
        if (list == null) {
            return;
        }
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        // 清空当前表
        db.delete(DBApi.TABLE_NAME_LAST_ALBUM, null, null);
        for (MusicListBean bean : list) {
            ContentValues values = createContentValues(bean);
            db.insert(DBApi.TABLE_NAME_LAST_ALBUM, null, values);
            values.clear();
        }
        db.close();
    }

    public static List<MusicListBean> queryLastAlbum(Context context) {
        List<MusicListBean> list = new ArrayList<>();
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.query(DBApi.TABLE_NAME_LAST_ALBUM, null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                MusicListBean bean = getField(cursor);
                boolean collect = queryAudioCollect(context, bean);
                bean.setCollectFlag(collect);
                list.add(bean);
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    private static ContentValues createContentValues(MusicListBean bean) {
        ContentValues values = new ContentValues();
        values.put("name", bean.getName());
        // 歌手这块TM巨坑，后台不拆分，服辣
        List<SingerBean> singerList = bean.getSingerList();
        if (singerList != null && singerList.size() > 0) {
            bean.setSinger(SPUtils.formatAuther(singerList));
        }
        values.put("singer", bean.getSinger());
        values.put("imageUrl", bean.getImageUrl());
        values.put("audioUrl", bean.getAudioUrl());
        values.put("videoUrl", bean.getVideoUrl());
        values.put("wordUrl", bean.getWordUrl());
        values.put("size", bean.getSize());
        values.put("duration", bean.getDuration());
        values.put("beginTime", bean.getBeginTime());
        values.put("endTime", bean.getEndTime());
        values.put("libraryId", bean.getLibraryId());
        values.put("libraryType", bean.getLibraryType());
        values.put("userId", bean.getUserId());
        values.put("quality", bean.getQuality());
        values.put("musicQuality", bean.getMusicQuality());
        values.put("qualityUrl", bean.getQualityUrl());
        values.put("collectFlag", bean.isCollectFlag() ? 1 : 0);
        values.put("specialId", bean.getSpecialId());
        return values;
    }

    private static MusicListBean getField(Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex("name"));
        String singer = cursor.getString(cursor.getColumnIndex("singer"));
        String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
        String audioUrl = cursor.getString(cursor.getColumnIndex("audioUrl"));
        String videoUrl = cursor.getString(cursor.getColumnIndex("videoUrl"));
        String wordUrl = cursor.getString(cursor.getColumnIndex("wordUrl"));
        Long size = cursor.getLong(cursor.getColumnIndex("size"));
        Long duration = cursor.getLong(cursor.getColumnIndex("duration"));
        String beginTime = cursor.getString(cursor.getColumnIndex("beginTime"));
        String endTime = cursor.getString(cursor.getColumnIndex("endTime"));
        int libraryId = cursor.getInt(cursor.getColumnIndex("libraryId"));
        String libraryType = cursor.getString(cursor.getColumnIndex("libraryType"));
        String userId = cursor.getString(cursor.getColumnIndex("userId"));
        String quality = cursor.getString(cursor.getColumnIndex("quality"));
        String musicQuality = cursor.getString(cursor.getColumnIndex("musicQuality"));
        String qualityUrl = cursor.getString(cursor.getColumnIndex("qualityUrl"));
        int collectFlag = cursor.getInt(cursor.getColumnIndex("collectFlag"));
        int specialId = cursor.getInt(cursor.getColumnIndex("specialId"));


        MusicListBean bean = new MusicListBean();
        bean.setName(name);
        bean.setSinger(singer);
        bean.setImageUrl(imageUrl);
        bean.setAudioUrl(audioUrl);
        bean.setVideoUrl(videoUrl);
        bean.setWordUrl(wordUrl);
        bean.setSize(size);
        bean.setDuration(duration);
        bean.setBeginTime(beginTime);
        bean.setEndTime(endTime);
        bean.setLibraryId(libraryId);
        bean.setLibraryType(libraryType);
        bean.setUserId(userId);
        bean.setQuality(quality);
        bean.setMusicQuality(musicQuality);
        bean.setQualityUrl(qualityUrl);
        bean.setCollectFlag(collectFlag != 0);
        bean.setSpecialId(specialId);
        return bean;
    }

    public static void clearAll(Context context) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        db.delete(DBApi.TABLE_NAME_MUSIC_COLLECT, null, null);
        db.delete(DBApi.TABLE_NAME_AUDIO_COLLECT, null, null);
        db.delete(DBApi.TABLE_NAME_MUSIC_RECORD, null, null);
        db.delete(DBApi.TABLE_NAME_AUDIO_RECORD, null, null);
        db.delete(DBApi.TABLE_NAME_LAST_ALBUM, null, null);
        db.delete(DBApi.TABLE_NAME_TIME_REMAINING_LOGIN, null, null);
        db.delete(DBApi.TABLE_NAME_TIME_REMAINING_NOT_LOGIN, null, null);
        db.close();
    }

    /**
     * 有声节目的播放/剩余时间
     */
    public static void insertCurrentTime(@NonNull Context context, MusicListBean bean) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", bean.getUserId());
        values.put("currentTime", bean.getCurrentTime());
        values.put("specialId", bean.getSpecialId());

        String id = String.valueOf(bean.getSpecialId());
        if (UserController.getInstance().isLoginStates()) {
            Cursor cursor = db.query(DBApi.TABLE_NAME_TIME_REMAINING_LOGIN, new String[]{"specialId"}, "specialId = ?", new String[]{id}, null, null, null);
            // 数据已存在，删除再添加
            if (cursor.getCount() != 0) {
                db.delete(DBApi.TABLE_NAME_TIME_REMAINING_LOGIN, "specialId = ?", new String[]{id});
            }
            db.insert(DBApi.TABLE_NAME_TIME_REMAINING_LOGIN, null, values);
        } else {
            Cursor cursor = db.query(DBApi.TABLE_NAME_TIME_REMAINING_NOT_LOGIN, new String[]{"specialId"}, "specialId = ?", new String[]{id}, null, null, null);
            // 数据已存在，删除再添加
            if (cursor.getCount() != 0) {
                db.delete(DBApi.TABLE_NAME_TIME_REMAINING_NOT_LOGIN, "specialId = ?", new String[]{id});
            }
            db.insert(DBApi.TABLE_NAME_TIME_REMAINING_NOT_LOGIN, null, values);
        }

        values.clear();
        db.close();
    }

    public static long queryCurrentTime(Context context, int specialId) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        Cursor cursor;
        if (UserController.getInstance().isLoginStates()) {
            cursor = db.query(DBApi.TABLE_NAME_TIME_REMAINING_LOGIN, new String[]{"currentTime"}, "specialId=?", new String[]{String.valueOf(specialId)}, null, null, null);
        } else {
            cursor = db.query(DBApi.TABLE_NAME_TIME_REMAINING_NOT_LOGIN, new String[]{"currentTime"}, "specialId=?", new String[]{String.valueOf(specialId)}, null, null, null);
        }
        long currentTime = 0;
        if (cursor.moveToFirst()) {
            currentTime = cursor.getLong(cursor.getColumnIndex("currentTime"));
        }
        cursor.close();
        db.close();
        return currentTime;
    }

    public static void clearTimeRemaining(Context context) {
        SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        db.delete(DBApi.TABLE_NAME_TIME_REMAINING_LOGIN, null, null);
        db.close();
    }
}
