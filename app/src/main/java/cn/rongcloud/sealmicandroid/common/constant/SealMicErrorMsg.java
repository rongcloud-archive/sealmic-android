package cn.rongcloud.sealmicandroid.common.constant;

import cn.rongcloud.sealmicandroid.util.ToastUtil;

public enum SealMicErrorMsg {

    /**
     * 系统内部错误
     */
    HTTP_SERVER_ERROR(10001, "系统内部错误"),
    HTTP_REQUEST_PARAM_ERROR(10002, "请求参数缺失或无效"),
    HTTP_INVALID_AUTH(10003, "认证信息无效或已过期"),
    HTTP_ACCESS_DENIED(10004, "无权限操作"),
    HTTP_REQUEST_INVALID(10005, "错误的请求"),
    HTTP_GET_TOKEN_ERROR(20000, "获取 IM Token 失败"),
    HTTP_SEND_CODE_OVER_FREQUENCY(20001, "发送短信请求过于频繁"),
    HTTP_SEND_CODE_FAILED(20002, "短信发送失败"),
    HTTP_SEND_CODE_INVALID_PHONE_NUMBER(20003, "手机号无效"),
    HTTP_CODE_NOT_SEND(20004, "短信验证码尚未发送"),
    HTTP_CODE_INVALID(20005, "短信验证码无效"),
    HTTP_CODE_EMPTY(20006, "验证码不能为空"),
    HTTP_CREATE_ROOM_FAILED(30000, "房间创建失败"),
    HTTP_ROOM_DESTROY(30001, "房间不存在"),
    HTTP_USER_ID_SIZE_EXCEED(30002, "用户id个数不能超过 20"),
    HTTP_ADD_BLOCK_FAILED(30003, "封禁用户失败"),
    HTTP_USER_NOT_IN_ROOM(30004, "用户不在房间"),
    HTTP_USER_ALREADY_IN_MIC(30005, "用户已在麦位"),
    HTTP_USER_HAS_APPLIED(30006, "用户已在排麦列表"),
    HTTP_USER_NOT_APPLIED(30007, "用户没有申请排麦"),
    HTTP_USER_NOT_IN_MIC(30008, "用户不在麦位"),
    HTTP_MIC_NULL(30009, "没有可用麦位"),
    HTTP_ALREADY_HOST(30010, "您已是主持人"),
    HTTP_TRANSFER_EXPIRED(30011, "主持人转让信息已失效"),
    HTTP_GAG_USER_FAILED(30012, "禁言用户失败"),
    HTTP_TAKE_OVER_EXPIRED(30013, "接管主持人信息已失效"),
    HTTP_SET_MIC_LOCK(30014, "麦位已有用户，不允许进行麦位锁定操作"),
    HTTP_SET_MIC_CLOSE(30015, "麦位上无用户，不允许进行闭麦操作"),
    VERSION_EXIST(40000, "版本已存在"),
    VERSION_NO_EXIST(40001, "版本不存在"),
    NO_NEW_VERSION(40002, "没有新版本");

    private int code;
    private String message;

    SealMicErrorMsg(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static void fromCode(int code) {
        if (code == HTTP_SERVER_ERROR.code) {
            ToastUtil.showToast(HTTP_SERVER_ERROR.message);
        } else if (code == HTTP_REQUEST_PARAM_ERROR.code) {
            ToastUtil.showToast(HTTP_REQUEST_PARAM_ERROR.message);
        } else if (code == HTTP_INVALID_AUTH.code) {
            ToastUtil.showToast(HTTP_INVALID_AUTH.message);
        } else if (code == HTTP_ACCESS_DENIED.code) {
            ToastUtil.showToast(HTTP_ACCESS_DENIED.message);
        } else if (code == HTTP_REQUEST_INVALID.code) {
            ToastUtil.showToast(HTTP_REQUEST_INVALID.message);
        } else if (code == HTTP_GET_TOKEN_ERROR.code) {
            ToastUtil.showToast(HTTP_GET_TOKEN_ERROR.message);
        } else if (code == HTTP_SEND_CODE_OVER_FREQUENCY.code) {
            ToastUtil.showToast(HTTP_SEND_CODE_OVER_FREQUENCY.message);
        } else if (code == HTTP_SEND_CODE_FAILED.code) {
            ToastUtil.showToast(HTTP_SEND_CODE_FAILED.message);
        } else if (code == HTTP_SEND_CODE_INVALID_PHONE_NUMBER.code) {
            ToastUtil.showToast(HTTP_SEND_CODE_INVALID_PHONE_NUMBER.message);
        } else if (code == HTTP_CODE_NOT_SEND.code) {
            ToastUtil.showToast(HTTP_CODE_NOT_SEND.message);
        } else if (code == HTTP_CODE_INVALID.code) {
            ToastUtil.showToast(HTTP_CODE_INVALID.message);
        } else if (code == HTTP_CODE_EMPTY.code) {
            ToastUtil.showToast(HTTP_CODE_EMPTY.message);
        } else if (code == HTTP_CREATE_ROOM_FAILED.code) {
            ToastUtil.showToast(HTTP_CREATE_ROOM_FAILED.message);
        } else if (code == HTTP_ROOM_DESTROY.code) {
            ToastUtil.showToast(HTTP_ROOM_DESTROY.message);
        } else if (code == HTTP_USER_ID_SIZE_EXCEED.code) {
            ToastUtil.showToast(HTTP_USER_ID_SIZE_EXCEED.message);
        } else if (code == HTTP_ADD_BLOCK_FAILED.code) {
            ToastUtil.showToast(HTTP_ADD_BLOCK_FAILED.message);
        } else if (code == HTTP_USER_NOT_IN_ROOM.code) {
            ToastUtil.showToast(HTTP_USER_NOT_IN_ROOM.message);
        } else if (code == HTTP_USER_HAS_APPLIED.code) {
            ToastUtil.showToast(HTTP_USER_HAS_APPLIED.message);
        } else if (code == HTTP_USER_ALREADY_IN_MIC.code) {
            ToastUtil.showToast(HTTP_USER_ALREADY_IN_MIC.message);
        } else if (code == HTTP_MIC_NULL.code) {
            ToastUtil.showToast(HTTP_MIC_NULL.message);
        } else if (code == HTTP_ALREADY_HOST.code) {
            ToastUtil.showToast(HTTP_ALREADY_HOST.message);
        } else if (code == HTTP_TRANSFER_EXPIRED.code) {
            ToastUtil.showToast(HTTP_TRANSFER_EXPIRED.message);
        } else if (code == HTTP_GAG_USER_FAILED.code) {
            ToastUtil.showToast(HTTP_GAG_USER_FAILED.message);
        } else if (code == HTTP_TAKE_OVER_EXPIRED.code) {
            ToastUtil.showToast(HTTP_TAKE_OVER_EXPIRED.message);
        } else if (code == HTTP_SET_MIC_LOCK.code) {
            ToastUtil.showToast(HTTP_SET_MIC_LOCK.message);
        } else if (code == HTTP_SET_MIC_CLOSE.code) {
            ToastUtil.showToast(HTTP_SET_MIC_CLOSE.message);
        } else if (code == VERSION_EXIST.code) {
            ToastUtil.showToast(VERSION_EXIST.message);
        } else if (code == VERSION_NO_EXIST.code) {
            ToastUtil.showToast(VERSION_NO_EXIST.message);
        }
//        else if (code == NO_NEW_VERSION.code) {
//            ToastUtil.showToast(NO_NEW_VERSION.message);
//        }
    }
}
