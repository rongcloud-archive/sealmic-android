package cn.rongcloud.sealmic.im;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmic.constant.ErrorCode;
import cn.rongcloud.sealmic.im.message.MicPositionChangeMessage;
import cn.rongcloud.sealmic.im.message.MicPositionControlMessage;
import cn.rongcloud.sealmic.im.message.RoomBgChangeMessage;
import cn.rongcloud.sealmic.im.message.RoomDestroyNotifyMessage;
import cn.rongcloud.sealmic.im.message.RoomIsActiveMessage;
import cn.rongcloud.sealmic.im.message.RoomMemberChangedMessage;
import cn.rongcloud.sealmic.task.ResultCallback;
import cn.rongcloud.sealmic.utils.log.SLog;
import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * Rong IM 业务相关封装
 */
public class IMClient {
    private static final String TAG = IMClient.class.getSimpleName();
    private static IMClient sInstance;
    private final int DEFAULT_MESSAGE_COUNT = -1;//-1代表不拉取历史消息
    private List<RongIMClient.OnReceiveMessageListener> listenerList = new ArrayList<>();

    public static IMClient getInstance() {
        if (sInstance == null) {
            synchronized (IMClient.class) {
                if (sInstance == null) {
                    sInstance = new IMClient();
                }
            }
        }
        return sInstance;
    }

    private IMClient() {
    }

    /**
     * 初始化，需要在使用前初始化一次
     *
     * @param context
     */
    public void init(Context context) {
        /*
         * 初始化 SDK，在整个应用程序全局，只需要调用一次。建议在 Application 继承类中调用。
         */
        // 可在初始 SDK 时直接带入融云 IM 申请的APP KEY
        RongIMClient.init(context, 这里请替换为您的融云 AppKey, false);

        try {
            RongIMClient.registerMessageType(MicPositionChangeMessage.class);
            RongIMClient.registerMessageType(MicPositionControlMessage.class);
            RongIMClient.registerMessageType(RoomMemberChangedMessage.class);
            RongIMClient.registerMessageType(RoomBgChangeMessage.class);
            RongIMClient.registerMessageType(RoomDestroyNotifyMessage.class);
            RongIMClient.registerMessageType(RoomIsActiveMessage.class);
        } catch (AnnotationNotFoundException e) {
            SLog.e(TAG, "Failed to register messages!!");
            e.printStackTrace();
        }

        /*
         * 管理消息监听，由于同一时间只能有一个消息监听加入 融云 的消息监听，所以做一个消息管理来做消息路由
         */
        RongIMClient.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int left) {
                SLog.d(TAG, "onReceived message. tag:" + message.getObjectName());
                synchronized (listenerList) {
                    if (listenerList.size() > 0) {
                        for (RongIMClient.OnReceiveMessageListener listener : listenerList) {
                            boolean result = listener.onReceived(message, left);
                            if (result) {
                                break;
                            }
                        }
                    }
                }
                return true;
            }
        });
    }

    /**
     * 加入 IM 聊天室
     *
     * @param roomId
     * @param callBack
     */
    public void joinChatRoom(final String roomId, final ResultCallback<String> callBack) {
        RongIMClient.getInstance().joinChatRoom(roomId, DEFAULT_MESSAGE_COUNT, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                if (callBack != null) {
                    callBack.onSuccess(roomId);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                SLog.e(SLog.TAG_IM, "join chat room error, error msg:" + errorCode.getMessage() + " , error code" + errorCode.getValue());
                if (callBack != null) {
                    callBack.onFail(ErrorCode.IM_ERROR.getCode());
                }
            }
        });
    }

    /**
     * 离开 IM 聊天室
     *
     * @param roomId
     * @param callBack
     */
    public void quitChatRoom(final String roomId, final ResultCallback<String> callBack) {
        RongIMClient.getInstance().quitChatRoom(roomId, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                if (callBack != null) {
                    callBack.onSuccess(roomId);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                SLog.e(SLog.TAG_IM, "quit chat room error, error msg:" + errorCode.getMessage() + " , error code" + errorCode.getValue());
                if (callBack != null) {
                    callBack.onFail(ErrorCode.IM_ERROR.getCode());
                }
            }
        });
    }

    /**
     * 加入消息监听
     *
     * @param listener
     */
    public void addMessageReceiveListener(RongIMClient.OnReceiveMessageListener listener) {
        synchronized (listenerList) {
            listenerList.add(listener);
        }
    }

    /**
     * 移除消息监听
     *
     * @param listener
     */
    public void removeMessageReceiveListener(RongIMClient.OnReceiveMessageListener listener) {
        synchronized (listenerList) {
            listenerList.remove(listener);
        }
    }

    /**
     * 创建本地显示用户进入房间消息
     *
     * @param userId 进入房间的用户
     * @param roomId 房间id
     * @return
     */
    public Message createLocalEnterRoomMessage(String userId, String roomId) {
        RoomMemberChangedMessage memberChangedMessage = new RoomMemberChangedMessage();
        memberChangedMessage.setTargetUserId(userId);
        memberChangedMessage.setCmd(1);
        return Message.obtain(roomId, Conversation.ConversationType.CHATROOM, memberChangedMessage);
    }

}
