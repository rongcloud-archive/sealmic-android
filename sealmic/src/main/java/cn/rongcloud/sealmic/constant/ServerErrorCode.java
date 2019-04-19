package cn.rongcloud.sealmic.constant;

/**
 * 服务器返回错误码
 */
public class ServerErrorCode {
    /**
     * 当前麦位已有用户
     */
    public static final int MIC_POSITION_HAS_BEEN_HOLD = 24;

    /**
     * 当前房间数量已达到上限
     */
    public static final int ROOM_CREATE_ROOM_OVER_LIMIT = 26;

    /**
     * 当前房间内人数已达到上限
     */
    public static final int ROOM_JOIN_MEMBER_OVER_LIMIT = 27;

    /**
     * 用户已在麦位上
     */
    public static final int USER_ALREADY_ON_MIC_POSITION = 28;
}
