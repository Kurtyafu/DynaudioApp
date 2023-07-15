package com.byd.dynaudio_app;

/**
 * 用于live data bus 数据传输时的tag类
 */
public class LiveDataBusConstants {

    // 跳转到指定fragment
    public static final String to_fragment = "to_fragment";

    // 返回
    public static final String go_back = "go_back";

    // 聚合页类型
    public static final String aggregation_type = "aggregation_type";

    // 全屏播放页 播放类型
    public static final String play_type = "play_type";

    // 全屏播放页 当前歌词高亮行数
    public static final String highlight_line = "highlight_line";

    // 当前专辑id
    public static final String current_album_id = "current_album_id";
    // 详情类型
    public static final String detail_type = "detail_type";


    // 是否正在播放
    public static final String is_playing = "is_playing";
    public static final String PLAY_INDEX = "PLAY_INDEX";// 播放器中正在播放的item的下标
    public static String golden_detail_id = "golden_detail_id";
    public static String station_detail_id = "station_detail_id";

    public static final String station_data = "station_data";
    public static final String sound_settings_tab = "sound_settings_tab";

    public static final String is_in_taixuan = "is_in_taixuan";// 是否处于台宣：台宣情况下，屏蔽界面所有音乐操作按钮
    public static String to_soundSettings_from_fullPlayer = "to_soundSettings_from_fullPlayer"; // 是否从全屏播放器跳转到设置 如果是 返回的时候直接拉起全屏播放器
    public static final String SC_POS = "SC_POS"; // sc里记住位置
    public static String MINI_PLAY_STATUS_CAN_CHANGE = "MINI_PLAY_STATUS_CAN_CHANGE"; // mini 播放器播放状态不能改变


    public static final class Player {
        /**
         * 这里面的属性 都是当前在内存中 需要在界面展示的
         */
        public static final String CURRENT_PLAY = "Player_" + "current_play"; // 播放内容 MusicListBean对象

        public static final String CURRENT_PLAYER = "Player_" + "CURRENT_PLAYER"; // 播放器内容

        public static final String ITEM_PLAY = "Player_" + "item_play"; // 列表item播放内容 MusicListBean对象 用于收藏


        public static final String PLAY_STATUS = "Player_" + "play_status"; // 播放状态：播放，暂停，加载
        public static final String PLAY_MODE = "Player_" + "play_mode";  // 播放模式

        public static final String PlAY_LIST = "Player_" + "play_list"; // 播放列表

        public static final String PLAY_SPEED = "Player_" + "play speed"; // 播放速度
        /**
         * ui控制类型
         */
        public static final String SET_CURRENT = "Player_" + "set_current"; // 通知vp更新

        public static final String SHOW_LIST = "Player_" + "show_list";// 全屏界面是否显示列表

        public static final String SHOW_FULLPLAY = "Player_" + "FullPLAY";// 是否显示全屏播放器

        public static final String IS_MINI_PLAYER_SHOW = "Player_" + "is_mini_show"; // mini 播放器是否显示
        // public static final String CURRENT_PLAY_ID = "CURRENT_PLAY_ID"; // 当前正在播放的碎片id 用于列表展示的高亮操作
    }


    public static final class ViewTag {
        public static final int HAS_LRC = 0x01;
        public static final int IS_VIDEO = 0x02;
    }


    /**
     * 主页就能获取的数据
     */
    public static final class MainData {
        public static final String GOLDEN_SONG = "GOLDEN_SONG"; // 金曲
        public static final String AUDIO_STATION = "AUDIO_STATION"; // 电台
        public static final String BLACK_PLASTIC = "BLACK_PLASTIC"; // 黑胶
        public static final String IMMERSION_SOUND = "IMMERSION_SOUND"; // 3d沉浸声
        public static final String AUDIO_COLUMN = "AUDIO_COLUMN"; // 有声专栏
    }
}
