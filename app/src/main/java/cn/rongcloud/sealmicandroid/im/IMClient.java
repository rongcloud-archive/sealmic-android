package cn.rongcloud.sealmicandroid.im;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.rongcloud.sealmicandroid.BuildConfig;
import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.SealMicApp;
import cn.rongcloud.sealmicandroid.bean.SendSuperGiftBean;
import cn.rongcloud.sealmicandroid.common.Event;
import cn.rongcloud.sealmicandroid.common.adapter.SendMessageAdapter;
import cn.rongcloud.sealmicandroid.common.constant.UserRoleType;
import cn.rongcloud.sealmicandroid.im.message.HandOverHostMessage;
import cn.rongcloud.sealmicandroid.im.message.KickMemberMessage;
import cn.rongcloud.sealmicandroid.im.message.RoomMemberChangedMessage;
import cn.rongcloud.sealmicandroid.im.message.SendBroadcastGiftMessage;
import cn.rongcloud.sealmicandroid.im.message.SendGiftMessage;
import cn.rongcloud.sealmicandroid.im.message.TakeOverHostMessage;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.manager.NavOptionsRouterManager;
import cn.rongcloud.sealmicandroid.manager.RoomManager;
import cn.rongcloud.sealmicandroid.util.ToastUtil;
import cn.rongcloud.sealmicandroid.util.log.SLog;
import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.ChatRoomInfo;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;

import static cn.rongcloud.sealmicandroid.common.constant.SealMicConstant.TAG;
import static io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT;

/**
 * Rong IM 业务相关封装
 */
public class IMClient {


    /**
     * -1代表不拉取历史消息
     */
    private static final int DEFAULT_MESSAGE_COUNT = -1;
    private static final int DEF_MEMBER_COUNT = 20;

    private static final String LIVE_URL_KEY = "liveUrl";
    private static final String MIC_POSITION = "sealmic_position_";

    /**
     * 接收消息的监听器列表
     */
    private final List<RongIMClient.OnReceiveMessageListener> onReceiveMessageListeners = new ArrayList<>();
    /**
     * 消息撤回的监听器列表
     */
    private final List<RongIMClient.OnRecallMessageListener> onRecallMessageListeners = new ArrayList<>();

    public static IMClient getInstance() {
        return IMClientHelper.INSTANCE;
    }

    private static class IMClientHelper {
        private static final IMClient INSTANCE = new IMClient();
    }

    private IMClient() {
    }

    /**
     * 初始化，需要在使用前初始化一次
     * 接收实时或者离线消息。
     * 注意:
     * 1. 针对接收离线消息时，服务端会将 200 条消息打成一个包发到客户端，客户端对这包数据进行解析。
     * 2. hasPackage 标识是否还有剩余的消息包，left 标识这包消息解析完逐条抛送给 App 层后，剩余多少条。
     * 如何判断离线消息收完：
     * 1. hasPackage 和 left 都为 0；
     * 2. hasPackage 为 0 标识当前正在接收最后一包（200条）消息，left 为 0 标识最后一包的最后一条消息也已接收完毕。
     * <p>
     * //     * @param message    接收到的消息对象
     * //     * @param left       每个数据包数据逐条上抛后，还剩余的条数
     * //     * @param hasPackage 是否在服务端还存在未下发的消息包
     * //     * @param offline    消息是否离线消息
     *
     * @return 是否处理消息。 如果 App 处理了此消息，返回 true; 否则返回 false 由 SDK 处理。
     */
    public void init(Context context) {
        /*
         * 初始化 SDK，在整个应用程序全局，只需要调用一次。建议在 Application 继承类中调用。
         */
        // 可在初始 SDK 时直接带入融云 IM 申请的APP KEY
        RongIMClient.init(context, BuildConfig.Rong_key, false);

        try {
            RongIMClient.registerMessageType(RoomMemberChangedMessage.class);
            RongIMClient.registerMessageType(SendGiftMessage.class);
            RongIMClient.registerMessageType(SendBroadcastGiftMessage.class);
            RongIMClient.registerMessageType(KickMemberMessage.class);
            RongIMClient.registerMessageType(HandOverHostMessage.class);
            RongIMClient.registerMessageType(TakeOverHostMessage.class);
        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
        }

        //管理消息监听，由于同一时间只能有一个消息监听加入 融云 的消息监听，所以做一个消息管理来做消息路由
        RongIMClient.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int left) {
                SLog.e(TAG, "onReceived message. tag:" + message.getObjectName());
                synchronized (onReceiveMessageListeners) {
                    if (onReceiveMessageListeners.size() > 0) {
                        for (RongIMClient.OnReceiveMessageListener listener : onReceiveMessageListeners) {
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

        //消息撤回，用来删除消息
        RongIMClient.setOnRecallMessageListener(new RongIMClient.OnRecallMessageListener() {
            @Override
            public boolean onMessageRecalled(Message message, RecallNotificationMessage recallNotificationMessage) {
                SLog.e(TAG, "onRecall message. tag: " + message.getObjectName());
                synchronized (onRecallMessageListeners) {
                    if (onRecallMessageListeners.size() > 0) {
                        for (RongIMClient.OnRecallMessageListener onRecallMessageListener : onRecallMessageListeners) {
                            boolean result = onRecallMessageListener.onMessageRecalled(message, recallNotificationMessage);
                            if (result) {
                                break;
                            }
                        }
                    }
                }
                return false;
            }
        });
        //IM连接状态监听
        RongIMClient.setConnectionStatusListener(new RongIMClient.ConnectionStatusListener() {
            @Override
            public void onChanged(ConnectionStatus connectionStatus) {
                if (connectionStatus == KICKED_OFFLINE_BY_OTHER_CLIENT) {
                    //多设备登录时被其他端顶下线
                    //1. 断开IM
                    disconnect();
                    //2. 清空本地用户id，清空是否登录状态
                    CacheManager.getInstance().cacheUserId("");
                    CacheManager.getInstance().cacheIsLogin(false);
                    //3. 调用游客登录接口重新登录
                    EventBus.getDefault().post(new Event.UserGoOutBean());
                }
            }
        });
    }

    public void connect(String token, final RongIMClient.ConnectCallback callback) {
        RongIMClient.connect(token, callback);
    }

    public void disconnect() {
        RongIMClient.getInstance().disconnect();
    }

    /**
     * 加入 IM 聊天室
     */
    public void joinChatRoom(final String roomId, final RongIMClient.ResultCallback<String> callBack) {
        RongIMClient.getInstance().joinChatRoom(roomId, DEFAULT_MESSAGE_COUNT, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                if (callBack != null) {
                    getChatRoomInfo(roomId, new RongIMClient.ResultCallback<ChatRoomInfo>() {
                        @Override
                        public void onSuccess(ChatRoomInfo chatRoomInfo) {
                            callBack.onSuccess(roomId);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                SLog.e(SLog.TAG_IM, "join chat room error, error msg:" + errorCode.getMessage() + " , error code" + errorCode.getValue());
                if (callBack != null) {
                    callBack.onError(errorCode);
                }
            }
        });
    }

    /**
     * 获取房间信息
     */
    public void getChatRoomInfo(String roomId, RongIMClient.ResultCallback<ChatRoomInfo> callback) {
        RongIMClient.getInstance().getChatRoomInfo(
                roomId,
                DEF_MEMBER_COUNT,
                ChatRoomInfo.ChatRoomMemberOrder.RC_CHAT_ROOM_MEMBER_DESC,
                callback);
    }


    /**
     * 离开 IM 聊天室
     */
    public void quitChatRoom(final String roomId, final RongIMClient.ResultCallback<String> callBack) {
        RongIMClient.getInstance().quitChatRoom(roomId, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
//                SLog.i(SLog.TAG_IM, "quitRoomSuccess");
                if (callBack != null) {
                    callBack.onSuccess(roomId);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                SLog.e(SLog.TAG_IM, "quit chat room error, error msg:" + errorCode.getMessage() + " , error code" + errorCode.getValue());
                if (callBack != null) {
                    callBack.onError(errorCode);
                }
            }
        });
    }

    /**
     * 加入消息接收监听
     *
     * @param listener 监听回调
     */
    public void addMessageReceiveListener(RongIMClient.OnReceiveMessageListener listener) {
        synchronized (onReceiveMessageListeners) {
            onReceiveMessageListeners.add(listener);
        }
    }

    /**
     * 移除消息接收监听
     *
     * @param listener 监听回调
     */
    public void removeMessageReceiveListener(RongIMClient.OnReceiveMessageListener listener) {
        synchronized (onReceiveMessageListeners) {
            onReceiveMessageListeners.remove(listener);
        }
    }

    /**
     * 添加消息撤回监听
     */
    public void addMessageRecallListener(RongIMClient.OnRecallMessageListener listener) {
        synchronized (onRecallMessageListeners) {
            onRecallMessageListeners.add(listener);
        }
    }

    /**
     * 移除消息撤回监听
     */
    public void removeMessageRecallListener(RongIMClient.OnRecallMessageListener listener) {
        synchronized (onRecallMessageListeners) {
            onRecallMessageListeners.remove(listener);
        }
    }

    /**
     * 创建本地显示用户房间消息
     *
     * @param userId 进入房间的用户
     * @param roomId 房间id
     * @param cmd    1 join, 2 leave, 3 kick
     * @return im信息
     */
    public Message createLocalRoomMessage(String userId, String roomId, int cmd) {
        RoomMemberChangedMessage memberChangedMessage = new RoomMemberChangedMessage();
        memberChangedMessage.setTargetUserId(userId);
        memberChangedMessage.setCmd(cmd);
        return Message.obtain(roomId, Conversation.ConversationType.CHATROOM, memberChangedMessage);
    }

    /**
     * 发送消息
     *
     * @param message            发送消息
     * @param sendMessageAdapter 发送之后的回调
     */
    public void sendMessage(Message message, SendMessageAdapter sendMessageAdapter) {
        RongIMClient.getInstance().sendMessage(message, null, null, sendMessageAdapter);
    }

    /**
     * 撤回发送的消息
     *
     * @param message
     * @param callback
     */
    public void recallMessage(Message message, String pushMessage, RongIMClient.ResultCallback<RecallNotificationMessage> callback) {
        RongIMClient.getInstance().recallMessage(message, pushMessage, callback);
    }

    public void getChatRoomEntry(String chatRoomId, String key, @NonNull final RongIMClient.ResultCallback<Map<String, String>> callback) {
        RongIMClient.getInstance().getChatRoomEntry(chatRoomId, key, callback);
    }

    public void setChatRoomEntry(String roomId, String key, final String value) {
        RongIMClient.getInstance().setChatRoomEntry(roomId, key, value, true, false, "", new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "存value" + value);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.i(TAG, "存value" + errorCode);
            }
        });
    }

    public void getAllChatRoomEntries(String chatRoomId, @NonNull final RongIMClient.ResultCallback<Map<String, String>> callback) {
        RongIMClient.getInstance().getAllChatRoomEntries(chatRoomId, callback);
    }

    public void setChatRoomSpeakEntry(String roomId, String key, final String value) {
        RongIMClient.getInstance().setChatRoomEntry(roomId, key, value, true, false, "", new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                Log.i("TAG", "存speak" + value);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.i("TAG", "存speak" + errorCode);
            }
        });
    }

    /**
     * 作为进入房间的一员，都要发送一条消息出去，以便让该房间内的其他成员知道自己进入了房间
     *
     * @param roomId   房间ID
     * @param userName 进来的人的名字
     */
    public Message getWelcomeMessage(String roomId, String userId, String userName, String portrait) {
        boolean isHost = UserRoleType.HOST.isHost(CacheManager.getInstance().getUserRoleType());
        String welcome = SealMicApp.getApplication().getResources().getString(R.string.welcome_join_room);
        final TextMessage textMessage = TextMessage.obtain(welcome);
        UserInfo userInfo = new UserInfo(userId, userName, Uri.parse(portrait));
        textMessage.setUserInfo(userInfo);
        Message message = Message.obtain(roomId, Conversation.ConversationType.CHATROOM, textMessage);
        SLog.d(RoomManager.class.getSimpleName(), "发送欢迎消息成功:" + (isHost ? "主持人" : "观众") + textMessage.getContent());
        message.setContent(textMessage);
        return message;
    }

    /**
     * 作为进入房间的一员，都要发送一条消息出去，以便让该房间内的其他成员知道自己进入了房间
     *
     * @param roomId         房间ID
     * @param messageContent 发送的内容
     */
    public Message getTextMessage(String roomId, String messageContent) {
        TextMessage textMessage = TextMessage.obtain(messageContent);
        textMessage.setUserInfo(new UserInfo(CacheManager.getInstance().getUserId(),
                CacheManager.getInstance().getUserName(),
                Uri.parse(CacheManager.getInstance().getUserPortrait())));
        return Message.obtain(roomId, Conversation.ConversationType.CHATROOM, textMessage);
    }

    /**
     * 发送礼物的自定义消息
     *
     * @param roomId  房间昵称
     * @param content 聊天窗口要提示的文本，具体文本的不同就根据送的不同礼物类型拼接就行
     * @param tag     标记发的是哪个礼物
     * @return 发送的消息体
     */
    public Message getSendGiftMessage(String roomId, String content, String tag) {
        SendGiftMessage sendGiftMessage = SendGiftMessage.obtain();
        sendGiftMessage.setContent(content);
        sendGiftMessage.setUserInfo(new UserInfo(CacheManager.getInstance().getUserId(), CacheManager.getInstance().getUserName(), Uri.parse(CacheManager.getInstance().getUserPortrait())));
        sendGiftMessage.setTag(tag);
        return Message.obtain(roomId, Conversation.ConversationType.CHATROOM, sendGiftMessage);
    }

    /**
     * 发送超级礼物的消息
     *
     * @param roomName 房间名
     * @param tag      自定义消息头
     * @param userBean 用户实体
     * @return
     */
    public SendSuperGiftBean getSendSuperGiftBean(String roomName, String tag, SendSuperGiftBean.UserBean userBean) {
        SendSuperGiftBean sendSuperGiftBean = new SendSuperGiftBean();
        sendSuperGiftBean.setRoomName(roomName);
        sendSuperGiftBean.setTag(tag);
        sendSuperGiftBean.setUser(userBean);
        return sendSuperGiftBean;
    }

    public void getHistoryMessage(String roomId, IRongCallback.IChatRoomHistoryMessageCallback callback) {
        RongIMClient.getInstance().getChatroomHistoryMessages(roomId, 0, 20, RongIMClient.TimestampOrder.RC_TIMESTAMP_ASC, callback);
    }

}