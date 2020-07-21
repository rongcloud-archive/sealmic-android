package cn.rongcloud.sealmicandroid.common.adapter;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

/**
 * 消息送达回调对应的适配器
 */
public class SendMessageAdapter implements IRongCallback.ISendMessageCallback {

    /**
     * 消息发送前回调, 回调时消息已存储数据库
     *
     * @param message 已存库的消息体
     */
    @Override
    public void onAttached(Message message) {

    }


    /**
     * 消息发送成功。
     *
     * @param message 发送成功后的消息体
     */
    @Override
    public void onSuccess(Message message) {

    }


    /**
     * 消息发送失败
     *
     * @param message   发送失败的消息体
     * @param errorCode 具体的错误
     */
    @Override
    public void onError(Message message, RongIMClient.ErrorCode errorCode) {

    }
}
