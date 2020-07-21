package cn.rongcloud.sealmicandroid.common.listener;

import cn.rongcloud.sealmicandroid.im.message.HandOverHostMessage;

/**
 * 转让主持人dialog点击事件
 */
public interface OnHandOverHostDialogClickListener {

    /**
     * 同意
     *
     * @param handOverHostMessage 转让自定义的通知消息
     */
    void onAgree(HandOverHostMessage handOverHostMessage);

    /**
     * 拒绝
     *
     * @param handOverHostMessage 转让自定义的通知消息
     */
    void onRefuse(HandOverHostMessage handOverHostMessage);
}
