package cn.rongcloud.sealmicandroid.common.listener;

import cn.rongcloud.sealmicandroid.im.message.TakeOverHostMessage;


/**
 * 接管主持人dialog点击事件
 */
public interface OnTakeOverHostDialogClickListener {

    /**
     * 同意
     *
     * @param takeOverHostMessage 接管自定义的通知消息
     */
    void onAgree(TakeOverHostMessage takeOverHostMessage);

    /**
     * 拒绝
     *
     * @param takeOverHostMessage 接管自定义的通知消息
     */
    void onRefuse(TakeOverHostMessage takeOverHostMessage);
}
