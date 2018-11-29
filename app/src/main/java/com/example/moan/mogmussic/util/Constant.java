package com.example.moan.mogmussic.util;

public class Constant {
    public class Where {
        public static final String WHERE = "where";
        public static final String WHERE_CLICK_LOCAL_SONG = "song";
        public static final String WHERE_CLICK_LOCAL_PLAY_ALL = "all";
        public static final String WHERE_CLICK_LIST_SONG = "list_song";
        public static final String WHERE_CLICK_LIST_PLAY_ALL = "list_all";
    }

    public static final String MUSIC_CLICKED = "music_clicked";
    public static final String LIST_CLICKED = "list_clicked";
    public static final String MUSIC_SERVICE = "music_service";

    public class Action {
        public static final String ACTION_REFRESH_LIST = "mog.music.refresh_list";
        public static final String ACTION_START_MUSIC = "mog.music.start_music";
        public static final String ACTION_UPDATE_TIME = "mog.music.update_time";
        public static final String ACTION_SONG_FINISHED = "mog.music.song_finished";
        public static final String ACTION_BINDER_INIT = "mog.music.binder_init";
        public static final String ACTION_RESET_FINISHED = "mog.music.reset_finished";
    }

    public class Type {
        public static final int LOOPING = 0;
        public static final int ORDER = 1;
        public static final int RANDOM = 2;
        public static final String T_LOOPING = "单曲循环";
        public static final String T_ORDER = "列表播放";
        public static final String T_RANDOM = "随机播放";
    }
}