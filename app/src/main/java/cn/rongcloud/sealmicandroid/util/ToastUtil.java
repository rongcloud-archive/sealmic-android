package cn.rongcloud.sealmicandroid.util;

import android.widget.Toast;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.SealMicApp;
import cn.rongcloud.sealmicandroid.common.constant.ErrorCode;
import cn.rongcloud.sealmicandroid.util.log.SLog;

/**
 * Toast 工具类
 */
public class ToastUtil {
    private static Toast lastToast;

    public static void showErrorToast(ErrorCode errorCode) {
        //根据错误码进行对应错误提示
        int errorMsgResourceId = R.string.toast_error_unkown;
        switch (errorCode) {
            case NETWORK_ERROR:
                errorMsgResourceId = R.string.toast_error_network;
                break;
            case RESULT_ERROR:
            case RESULT_FAILED:
                errorMsgResourceId = R.string.toast_error_server_request;
                break;
            case IM_ERROR:
                errorMsgResourceId = R.string.toast_error_im_request;
                break;
            case RTC_ERROR:
                errorMsgResourceId = R.string.toast_error_rtc_request;
                break;
            case ROOM_SEND_MSG_ERROR:
                errorMsgResourceId = R.string.toast_error_im_send_message;
                break;
            case ROOM_NOT_JOIN_TO_ROOM:
                SLog.d(SLog.TAG_TASK, "currentRoom or currentRTCRoom is null");
                // 该错误暂不做提示，仅 log 输出
                errorMsgResourceId = -1;
                break;
            case MIC_POSITION_HAS_BEEN_HOLD:
                errorMsgResourceId = R.string.toast_error_mic_position_has_been_hold;
                break;
            case ROOM_CREATE_ROOM_OVER_LIMIT:
                errorMsgResourceId = R.string.toast_error_create_room_over_limit;
                break;
            case ROOM_JOIN_MEMBER_OVER_LIMIT:
                errorMsgResourceId = R.string.toast_error_room_member_over_limit;
                break;
            case MIC_USER_ALREADY_ON_OTHER_POSITION:
                errorMsgResourceId = R.string.toast_error_user_already_on_other_position;
                break;
            default:
                break;
        }

        // 特殊错误码不做提示
        if (errorMsgResourceId == -1) {
            return;
        }

        String message = SealMicApp.getApplication().getResources().getString(errorMsgResourceId);
        if (lastToast != null) {
            lastToast.setText(message);
        } else {
            lastToast = Toast.makeText(SealMicApp.getApplication(), message, Toast.LENGTH_SHORT);
        }
        lastToast.show();
    }

    public static void showErrorToast(int errorCode) {
        showErrorToast(ErrorCode.fromCode(errorCode));
    }

    public static void showToast(int resourceId) {
        showToast(SealMicApp.getApplication().getResources().getString(resourceId));
    }

    public static void showToast(String message) {
        if (lastToast != null && lastToast.getView() != null && lastToast.getView().getParent() != null) {
            lastToast.setText(message);
        } else {
            lastToast = Toast.makeText(SealMicApp.getApplication(), message, Toast.LENGTH_SHORT);
        }
        lastToast.show();
    }
}
