package com.byd.dynaudio_app.database;

import static com.byd.dynaudio_app.database.DBApi.DATABASE_NAME;
import static com.byd.dynaudio_app.database.DBApi.DATABASE_VERSION;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.byd.dynaudio_app.utils.LogUtils;

public class DBHelper extends SQLiteOpenHelper {
    private static volatile DBHelper mInstance;

    public static DBHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBHelper.class) {
                if (mInstance == null) {
                    mInstance = new DBHelper(context);
                }
            }
        }
        return mInstance;
    }

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        LogUtils.d();
        sqLiteDatabase.execSQL(DBApi.TABLE_MUSIC_COLLECT);
        sqLiteDatabase.execSQL(DBApi.TABLE_AUDIO_COLLECT);
        sqLiteDatabase.execSQL(DBApi.TABLE_MUSIC_RECORD);
        sqLiteDatabase.execSQL(DBApi.TABLE_AUDIO_RECORD);
        sqLiteDatabase.execSQL(DBApi.TABLE_LAST_ALBUM);
        sqLiteDatabase.execSQL(DBApi.TABLE_TIME_REMAINING_NOT_LOGIN);
        sqLiteDatabase.execSQL(DBApi.TABLE_TIME_REMAINING_LOGIN);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        LogUtils.d("old : " + oldVersion + " new : " + newVersion);

//        if (newVersion > oldVersion) {
//            sqLiteDatabase.execSQL("ALTER TABLE DBApi.TABLE_MUSIC_COLLECT ADD COLUMN specialId INTEGER");
//            sqLiteDatabase.execSQL("ALTER TABLE DBApi.TABLE_AUDIO_COLLECT ADD COLUMN specialId INTEGER");
//            sqLiteDatabase.execSQL("ALTER TABLE DBApi.TABLE_MUSIC_RECORD ADD COLUMN specialId INTEGER");
//            sqLiteDatabase.execSQL("ALTER TABLE DBApi.TABLE_AUDIO_RECORD ADD COLUMN specialId INTEGER");
//
//        }
    }
}
