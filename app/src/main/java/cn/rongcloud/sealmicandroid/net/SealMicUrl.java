package cn.rongcloud.sealmicandroid.net;

import cn.rongcloud.sealmicandroid.BuildConfig;

/**
 * seal mic 请求接口地址集合
 *
 * @author yangyi
 */
public class SealMicUrl {
    public static final String DOMAIN = BuildConfig.App_server;

    //用户模块
    /**
     * 发送短信验证码
     */
    public static final String SEND_CODE = "user/sendCode";

    /**
     * 游客登录
     */
    public static final String VISITOR_LOGIN = "user/visitorLogin";

    /**
     * 刷新token
     */
    public static final String REFRESH_TOKEN = "user/refreshToken";

    /**
     * 用户登录
     */
    public static final String USER_LOGIN = "user/login";

    /**
     * 批量获取用户信息
     */
    public static final String USER_BATCH ="user/batch";

    //房间模块
    /**
     * 创建房间
     */
    public static final String ROOM_CREATE = "room/create";

    /**
     * 获取房间列表
     */
    public static final String ROOM_LIST = "room/list";

    /**
     * 获取房间详情
     */
    public static final String ROOM_DETAIL = "room/{roomId}";

    /**
     * 房间设置(主持人)
     */
    public static final String ROOM_SETTING = "room/setting";

    /**
     * 获取房间成员列表
     */
    public static final String ROOM_MEMBERS = "room/{roomId}/members";

    /**
     * 获取排麦成员(主持人)
     */
    public static final String ROOM_APPLY_MIC_MEMBERS = "room/{roomId}/mic/apply/members";

    /**
     * 将用户移除房间(主持人)
     */
    public static final String ROOM_USER_KICK = "room/kick";

    /**
     * 用户禁言设置(主持人)
     */
    public static final String ROOM_USER_GAG = "room/gag";

    /**
     * 获取禁言用户列表(主持人)
     */
    public static final String ROOM_GAG_MEMBERS = "room/{roomId}/gag/members";

    //麦位模块
    /**
     * 同意用户上麦(主持人)
     */
    public static final String ROOM_MIC_ACCEPT = "room/mic/apply/accept";

    /**
     * 拒绝用户上麦(主持人)
     */
    public static final String ROOM_MIC_REJECT = "room/mic/apply/reject";

    /**
     * 邀请用户连麦(主持人)
     */
    public static final String ROOM_MIC_INVITE = "room/mic/invite";

    /**
     * 踢用户下麦(主持人)
     */
    public static final String ROOM_MIC_KICK = "room/mic/kick";

    /**
     * 麦位状态设置(主持人)
     */
    public static final String ROOM_MIC_STATE = "room/mic/state";

    /**
     * 转让主持人(主持人)
     */
    public static final String ROOM_MIC_TRANSFER_HOST = "room/mic/transferHost";

    /**
     * 转让主持人拒绝(连麦者)
     */
    public static final String ROOM_MIC_TRANSFER_HOST_REJECT = "room/mic/transferHost/reject";

    /**
     * 转让主持人同意(连麦者)
     */
    public static final String ROOM_MIC_TRANSFER_HOST_ACCEPT = "room/mic/transferHost/accept";

    /**
     * 接管主持人(连麦者)
     */
    public static final String ROOM_MIC_TAKE_OVER_HOST = "room/mic/takeOverHost";

    /**
     * 接管主持人拒绝(主持人)
     */
    public static final String ROOM_MIC_TAKE_OVER_HOST_REJECT = "room/mic/takeOverHost/reject";

    /**
     * 接管主持人同意(主持人)
     */
    public static final String ROOM_MIC_TAKE_OVER_HOST_ACCEPT = "room/mic/takeOverHost/accept";

    /**
     * 主播下麦
     */
    public static final String ROOM_MIC_QUIT = "room/mic/quit";

    /**
     * 申请排麦(观众)
     */
    public static final String ROOM_MIC_APPLY = "room/mic/apply";

    /**
     * 消息模块 - 发送聊天室广播消息
     */
    public static final String ROOM_MESSAGE_BROADCAST = "room/message/broadcast";

    //App版本管理
    /**
     * 获取APP最新版本
     */
    public static final String APP_VERSION_LATEST = "appversion/latest";
}
