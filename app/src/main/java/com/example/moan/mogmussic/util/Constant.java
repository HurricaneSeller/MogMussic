package com.example.moan.mogmussic.util;

public class Constant {
    public class Where{
        public static final String WHERE = "where";
        public static final String WHERE_CLICK_LOCAL_SONG = "song";
        public static final String WHERE_CLICK_LOCAL_PLAY_ALL = "all";
        public static final String WHERE_CLICK_LIST_SONG = "list_song";
        public static final String WHERE_CLICK_LIST_PLAY_ALL = "list_all";
    }
    public static final String MUSIC_CLICKED = "music_clicked";
    public static final String LIST_CLICKED = "list_clicked";
    public static final String MUSIC_SERVICE = "music_service";

    public class Action{
        public static final String ACTION_REFRESH_LIST = "mog.refresh_list";
        public static final String ACTION_START_MUSIC = "mog.start_music";
    }

    public class Message{
        public static final int REFRESH_DATA = 0;
        public static final int MEDIA_START = 1;
        public static final int MEDIA_PAUSE = 2;
    }
}