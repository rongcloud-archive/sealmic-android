package cn.rongcloud.sealmicandroid.common.constant;

import cn.rongcloud.sealmicandroid.manager.RoomManager;

/**
 * 全局常量
 */
public class SealMicConstant {
    public final static int CONNECT_TIME_OUT = 10;
    public final static int READ_TIME_OUT = 10;
    public final static int WRITE_TIME_OUT = 10;

    public final static String SP_NAME_NET = "net";
    public final static String SP_KEY_NET_COOKIE_SET = "cookie_set";
    public final static String SP_KEY_NET_HEADER_AUTH = "header_auth";

    public static final String ROOM_ID = "room_id";
    public static final String ROOM_NAME = "room_name";
    public static final String ROOM_THEME = "room_theme";

    /**
     * KV取出之前的延迟时间，单位为毫秒，延迟取出避免KV取出失败
     */
    public static final long DELAY_KV = 1000;
    public static final String TAG = RoomManager.class.getSimpleName();
    public static final String ROOM_USER_ROLE = "room_user_role";
    public static final String INIT_KEY = "init_key";
    public static final String KV_SPEAK = "speaking";
    public static final String KV_POSITION = "position";

    /**
     * kv中的麦位信息前缀
     */
    public static final String KV_MIC_POSITION_PREFIX = "sealmic_position_";

    /**
     * kv中的正在讲话信息前缀
     */
    public static final String KV_SPEAK_POSITION_PREFIX = "speaking_";

    /**
     * kv中是否有人排麦信息前缀
     */
    public static final String KV_APPLIED_MIC_PREFIX = "applied_mic_list_empty";

}
