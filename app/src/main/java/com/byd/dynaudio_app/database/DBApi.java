package com.byd.dynaudio_app.database;

public interface DBApi {

    int DATABASE_VERSION = 1;
    String DATABASE_NAME = "dyn_audio_music.db";

    String TABLE_NAME_MUSIC_COLLECT = "music_collect";
    String TABLE_NAME_AUDIO_COLLECT = "audio_collect";
    String TABLE_NAME_MUSIC_RECORD = "music_record";
    String TABLE_NAME_AUDIO_RECORD = "audio_record";
    String TABLE_NAME_LAST_ALBUM = "last_album";
    String TABLE_NAME_TIME_REMAINING_NOT_LOGIN = "time_remaining_not_login";
    String TABLE_NAME_TIME_REMAINING_LOGIN = "time_remaining_login";

    String TABLE_MUSIC_COLLECT = "create table if not exists "
            + TABLE_NAME_MUSIC_COLLECT
            + " (id integer primary key autoincrement,"
            + "name text,"
            + "singer text,"
            + "imageUrl text,"
            + "audioUrl text,"
            + "videoUrl text,"
            + "wordUrl text,"
            + "size real,"
            + "duration real,"
            + "beginTime text,"
            + "endTime text,"
            + "libraryId integer,"
            + "libraryType text,"
            + "userId text,"
            + "quality text,"
            + "musicQuality text,"
            + "qualityUrl text,"
            + "collectFlag integer,"
            + "specialId integer)";


    String TABLE_AUDIO_COLLECT = "create table if not exists "
            + TABLE_NAME_AUDIO_COLLECT
            + " (id integer primary key autoincrement,"
            + "name text,"
            + "singer text,"
            + "imageUrl text,"
            + "audioUrl text,"
            + "videoUrl text,"
            + "wordUrl text,"
            + "size real,"
            + "duration real,"
            + "beginTime text,"
            + "endTime text,"
            + "libraryId integer,"
            + "libraryType text,"
            + "userId text,"
            + "quality text,"
            + "musicQuality text,"
            + "qualityUrl text,"
            + "collectFlag integer,"
            + "specialId integer)";

    String TABLE_MUSIC_RECORD = "create table if not exists "
            + TABLE_NAME_MUSIC_RECORD
            + " (id integer primary key autoincrement,"
            + "name text,"
            + "singer text,"
            + "imageUrl text,"
            + "audioUrl text,"
            + "videoUrl text,"
            + "wordUrl text,"
            + "size real,"
            + "duration real,"
            + "beginTime text,"
            + "endTime text,"
            + "libraryId integer,"
            + "libraryType text,"
            + "userId text,"
            + "quality text,"
            + "musicQuality text,"
            + "qualityUrl text,"
            + "collectFlag integer,"
            + "specialId integer)";

    String TABLE_AUDIO_RECORD = "create table if not exists "
            + TABLE_NAME_AUDIO_RECORD
            + " (id integer primary key autoincrement,"
            + "name text,"
            + "singer text,"
            + "imageUrl text,"
            + "audioUrl text,"
            + "videoUrl text,"
            + "wordUrl text,"
            + "size real,"
            + "duration real,"
            + "beginTime text,"
            + "endTime text,"
            + "libraryId integer,"
            + "libraryType text,"
            + "userId text,"
            + "quality text,"
            + "musicQuality text,"
            + "qualityUrl text,"
            + "collectFlag integer,"
            + "specialId integer)";

    String TABLE_LAST_ALBUM = "create table if not exists "
            + TABLE_NAME_LAST_ALBUM
            + " (id integer primary key autoincrement,"
            + "name text,"
            + "singer text,"
            + "imageUrl text,"
            + "audioUrl text,"
            + "videoUrl text,"
            + "wordUrl text,"
            + "size real,"
            + "duration real,"
            + "beginTime text,"
            + "endTime text,"
            + "libraryId integer,"
            + "libraryType text,"
            + "userId text,"
            + "quality text,"
            + "musicQuality text,"
            + "qualityUrl text,"
            + "collectFlag integer,"
            + "specialId integer)";

    String TABLE_TIME_REMAINING_NOT_LOGIN = "create table if not exists "
            + TABLE_NAME_TIME_REMAINING_NOT_LOGIN
            + " (id integer primary key autoincrement,"
            + "userId text,"
            + "currentTime real,"
            + "specialId integer)";

    String TABLE_TIME_REMAINING_LOGIN = "create table if not exists "
            + TABLE_NAME_TIME_REMAINING_LOGIN
            + " (id integer primary key autoincrement,"
            + "userId text,"
            + "currentTime real,"
            + "specialId integer)";
}
