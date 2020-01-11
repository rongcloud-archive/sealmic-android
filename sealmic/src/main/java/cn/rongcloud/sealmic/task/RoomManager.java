package cn.rongcloud.sealmic.task;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cn.rongcloud.rtc.RTCErrorCode;
import cn.rongcloud.rtc.callback.RongRTCResultUICallBack;
import cn.rongcloud.rtc.engine.report.StatusReport;
import cn.rongcloud.rtc.events.RongRTCEventsListener;
import cn.rongcloud.rtc.events.RongRTCStatusReportListener;
import cn.rongcloud.rtc.room.RongRTCRoom;
import cn.rongcloud.rtc.stream.remote.RongRTCAVInputStream;
import cn.rongcloud.rtc.user.RongRTCRemoteUser;
import cn.rongcloud.sealmic.constant.ErrorCode;
import cn.rongcloud.sealmic.im.IMClient;
import cn.rongcloud.sealmic.im.message.MicPositionChangeMessage;
import cn.rongcloud.sealmic.im.message.MicPositionControlMessage;
import cn.rongcloud.sealmic.im.message.RoomBgChangeMessage;
import cn.rongcloud.sealmic.im.message.RoomDestroyNotifyMessage;
import cn.rongcloud.sealmic.im.message.RoomIsActiveMessage;
import cn.rongcloud.sealmic.im.message.RoomMemberChangedMessage;
import cn.rongcloud.sealmic.model.BaseRoomInfo;
import cn.rongcloud.sealmic.model.DetailRoomInfo;
import cn.rongcloud.sealmic.model.MicBehaviorType;
import cn.rongcloud.sealmic.model.MicState;
import cn.rongcloud.sealmic.model.RoomMicPositionInfo;
import cn.rongcloud.sealmic.model.UserInfo;
import cn.rongcloud.sealmic.net.HttpClient;
import cn.rongcloud.sealmic.net.SealMicRequest;
import cn.rongcloud.sealmic.net.model.CreateRoomResult;
import cn.rongcloud.sealmic.rtc.RtcClient;
import cn.rongcloud.sealmic.task.callback.HandleRequestWrapper;
import cn.rongcloud.sealmic.task.callback.RequestWrapper;
import cn.rongcloud.sealmic.task.role.Linker;
import cn.rongcloud.sealmic.task.role.Listener;
import cn.rongcloud.sealmic.task.role.Owner;
import cn.rongcloud.sealmic.task.role.Role;
import cn.rongcloud.sealmic.utils.log.SLog;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

public class RoomManager {
    /**
     * 房主发送房间保活消息间隔时间
     */
    private static final long ROOM_SEND_ACTIVE_INTERVAL_TIME_MILLIS = 10 * 60 * 1000;
    private static RoomManager sInstance;
    private ThreadManager threadManager;
    private SealMicRequest request;
    private IMClient imClient;
    private RtcClient rtcClient;

    private RoomMessageListener messageListener;
    private RoomRtcEventListener rtcListener;
    private final Object roomLock = new Object();
    // 用户所在当前房间的详细信息
    private DetailRoomInfo currentRoom;
    // 用户所在当前房间的音频房间实体
    private RongRTCRoom currentRTCRoom;
    // 用户所在当前房间的角色
    private Role currentRole;
    // 用户所在当前房间声音是否开启
    private boolean currentRoomAudioEnable;
    // 用户所在当前房间的事件回调
    private RoomEventListener currentRoomEventListener;
    // 房主用于发送房间正在活动的消息线程
    private RoomActiveMsgSenderThread currentRoomActiveThread;


    public static RoomManager getInstance() {
        if (sInstance == null) {
            synchronized (RoomManager.class) {
                if (sInstance == null) {
                    sInstance = new RoomManager();
                }
            }
        }

        return sInstance;
    }

    private RoomManager() {
        threadManager = ThreadManager.getInstance();
        request = HttpClient.getInstance().getRequest();
        rtcClient = RtcClient.getInstance();
        imClient = IMClient.getInstance();
        messageListener = new RoomMessageListener();
        imClient.addMessageReceiveListener(messageListener);
    }

    /**
     * 获取聊天室房间列表
     */
    public void getChatRoomList(ResultCallback<List<BaseRoomInfo>> callBack) {
        request.getChatRoomList(new RequestWrapper<>(callBack));
    }

    /**
     * 创建房间
     *
     * @param subject  主题内容
     * @param type     类型
     * @param callBack
     */
    public void createRoom(String subject, int type, ResultCallback<CreateRoomResult> callBack) {
        request.createRoom(subject, type, new RequestWrapper<>(callBack));
    }

    /**
     * 销毁房间，此操作仅房主可使用
     *
     * @param callBack
     */
    public void destroyRoom(final String roomId, ResultCallback<Boolean> callBack) {
        request.destroyRoom(roomId, new HandleRequestWrapper<Boolean, Boolean>(callBack) {
            @Override
            public Boolean handleRequestResult(Boolean requestResult) {
                if (requestResult) {
                    clearRoomInfo(roomId);
                }
                return requestResult;
            }
        });
        imClient.quitChatRoom(roomId, null);
        rtcClient.quitRtcRoom(roomId, null);
    }

    /**
     * 加入聊天室
     *
     * @param roomId
     * @param callBack
     */
    public void joinRoom(final String roomId, final ResultCallback<DetailRoomInfo> callBack) {
        /*
         * 加入房间前进行初始化房间保证可以监听到对应房间的消息，再加入 IM 的聊天室，最后进行API服务器服加入聊天室请求
         * 防止API服务器请求延迟丢失对应聊天室的消息
         */
        initRoomInfo(roomId);

        imClient.joinChatRoom(roomId, new ResultCallback<String>() {
            @Override
            public void onSuccess(final String roomId) {
                request.joinRoom(roomId, new HandleRequestWrapper<DetailRoomInfo, DetailRoomInfo>(callBack) {
                    @Override
                    public DetailRoomInfo handleRequestResult(DetailRoomInfo dataResult) {
                        if (dataResult != null) {
                            synchronized (roomLock) {
                                //补偿在加入聊天室时收到的消息
                                if (currentRoom != null) {
                                    dataResult.setMessageList(currentRoom.getMessageList());

                                    //如果加入房间前收到了麦位变动消息则以消息为准，防止请求延迟造成麦位改变状态没有及时更新
                                    if (currentRoom.getMicPositions().size() > 0) {
                                        dataResult.setMicPositions(currentRoom.getMicPositions());
                                    }
                                }
                                currentRoom = dataResult;
                                currentRole = initCurrentRole();

                                // 当为房主时，每隔一定时间发送房间正在活动消息以保证 IM 聊天室 不会因长时间没有人发消息而销毁
                                if (currentRole instanceof Owner) {
                                    currentRoomActiveThread = new RoomActiveMsgSenderThread(roomId);
                                    currentRoomActiveThread.start();
                                }

                                return currentRoom;
                            }
                        }
                        return null;
                    }

                    @Override
                    public void onFail(int errorCode) {
                        super.onFail(errorCode);
                        /*
                         * 请求服务器加入聊天室失败时退出聊天室
                         */
                        imClient.quitChatRoom(roomId, null);
                        clearRoomInfo(roomId);
                    }
                });
            }

            @Override
            public void onFail(int errorCode) {
                callBack.onFail(errorCode);
                clearRoomInfo(roomId);
            }
        });
    }

    /**
     * 获得当前聊天室
     *
     * @return
     */
    public DetailRoomInfo getCurrentRoomInfo() {
        return currentRoom;
    }

    /**
     * 退出聊天室
     *
     * @param callBack
     */
    public void leaveRoom(final ResultCallback<Boolean> callBack) {
        synchronized (roomLock) {
            if (currentRoom != null) {
                final String roomId = currentRoom.getRoomId();
                request.leaveRoom(currentRoom.getRoomId(), new HandleRequestWrapper<Boolean, Boolean>(callBack) {
                    @Override
                    public Boolean handleRequestResult(Boolean requestResult) {
                        if (requestResult != null) {
                            clearRoomInfo(roomId);
                        }
                        return requestResult;
                    }
                });
                imClient.quitChatRoom(roomId, null);
                rtcClient.quitRtcRoom(roomId, null);
            } else {
                notInJoinToRoomCallBack(callBack);
            }
        }
    }

    /**
     * 获取房间详细
     *
     * @param roomId
     * @param callBack
     */
    public void getRoomDetailInfo(final String roomId, final ResultCallback<DetailRoomInfo> callBack) {
        request.getChatRoomDetail(roomId,
                new HandleRequestWrapper<DetailRoomInfo, DetailRoomInfo>(callBack) {
                    @Override
                    public DetailRoomInfo handleRequestResult(DetailRoomInfo dataResult) {
                        synchronized (roomLock) {
                            /*
                             * 当现在已加入房间且与获取的房间 id 相同时更新房间人数和房间人员
                             */
                            if (currentRoom != null && dataResult != null) {
                                if (currentRoom.getRoomId().equals(dataResult.getRoomId())) {
                                    currentRoom.setMemCount(dataResult.getMemCount());
                                    currentRoom.setAudiences(dataResult.getAudiences());
                                    currentRoom.setMicPositions(dataResult.getMicPositions());

                                    //检测角色是否发生了改变
                                    final Role newRole = initCurrentRole();
                                    if (newRole != null && !newRole.isSameRole(currentRole)) { // 角色发生改变时回调角色改变监听
                                        currentRole = newRole;
                                        final RoomEventListener roomEventlistener = currentRoomEventListener;
                                        if (roomEventlistener != null) {
                                            threadManager.runOnUIThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    roomEventlistener.onRoleChanged(newRole);
                                                }
                                            });
                                        }
                                    } else if (!newRole.equals(currentRole)) { // 角色没有改版但是角色信息改变时仅更新信息
                                        currentRole = newRole;
                                    }
                                }
                            }
                            return dataResult;
                        }
                    }
                });
    }

    /**
     * 获取房间用户列表
     *
     * @param roomId
     * @param callBack
     */
    public void getRoomUserList(final String roomId, final ResultCallback<List<UserInfo>> callBack) {
        request.getChatRoomUserList(roomId, new RequestWrapper<>(callBack));
    }

    /**
     * 加入麦位
     *
     * @param position
     * @param callBack
     */
    public void joinMic(final int position, final ResultCallback<Boolean> callBack) {
        synchronized (roomLock) {
            if (currentRoom != null) {
                final String roomId = currentRoom.getRoomId();
                request.joinMic(roomId, position, new RequestWrapper<>(callBack));
            } else {
                notInJoinToRoomCallBack(callBack);
            }
        }
    }

    /**
     * 离开麦位
     *
     * @param position
     * @param callBack
     */
    public void leaveMic(final int position, final ResultCallback<Boolean> callBack) {
        synchronized (roomLock) {
            if (currentRoom != null) {
                final String roomId = currentRoom.getRoomId();
                request.leaveMic(roomId, position, new RequestWrapper<>(callBack));
            } else {
                notInJoinToRoomCallBack(callBack);
            }
        }
    }

    /**
     * 跳转麦位
     */
    public void changeMicPosition(final int fromPosition, final int targetPosition, final ResultCallback<Boolean> callBack) {
        synchronized (roomLock) {
            if (currentRoom != null) {
                final String roomId = currentRoom.getRoomId();
                request.changMicPosition(roomId, fromPosition, targetPosition, new RequestWrapper<>(callBack));
            } else {
                notInJoinToRoomCallBack(callBack);
            }
        }
    }

    /**
     * 控制麦位状态
     *
     * @param position
     * @param targetUserId
     * @param cmd
     * @param callBack
     */
    public void controlMicPosition(final int position, String targetUserId, int cmd, final ResultCallback<Boolean> callBack) {
        synchronized (roomLock) {
            if (currentRoom != null) {
                final String roomId = currentRoom.getRoomId();
                request.controlMicPosition(roomId, position, targetUserId, cmd, new RequestWrapper<>(callBack));
            } else {
                notInJoinToRoomCallBack(callBack);
            }
        }
    }

    /**
     * 在当前所在聊天室发送消息
     *
     * @param message
     */
    public void sendChatRoomMessage(String message) {
        synchronized (roomLock) {
            if (currentRoom == null) {
                return;
            }

            TextMessage textMessage = TextMessage.obtain(message);
            RongIMClient.getInstance().sendMessage(Conversation.ConversationType.CHATROOM, currentRoom.getRoomId(), textMessage, null, null, new IRongCallback.ISendMessageCallback() {
                @Override
                public void onAttached(Message message) {

                }

                @Override
                public void onSuccess(Message message) {
                    synchronized (roomLock) {
                        currentRoom.getMessageList().add(message);
                        if (currentRoomEventListener != null) {
                            currentRoomEventListener.onMessageEvent(message);
                        }
                    }
                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    synchronized (roomLock) {
                        if (currentRoomEventListener != null) {
                            // 当不在聊天室时回调与房间断开连接
                            if (errorCode == RongIMClient.ErrorCode.NOT_IN_CHATROOM) {
                                currentRoomEventListener.onErrorLeaveRoom();
                            } else {
                                currentRoomEventListener.onSendMessageError(message, ErrorCode.ROOM_SEND_MSG_ERROR.getCode());
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * 设置当前房间的事件监听
     *
     * @param currentRoomEventListener
     */
    public void setCurrentRoomEventListener(RoomEventListener currentRoomEventListener) {
        this.currentRoomEventListener = currentRoomEventListener;
    }

    /**
     * 开始加入接受音频
     *
     * @param callBack
     */
    public void initRoomVoice(final ResultCallback<Boolean> callBack) {
        synchronized (roomLock) {
            if (currentRoom == null) {
                notInJoinToRoomCallBack(callBack);
                return;
            }
            final String roomId = currentRoom.getRoomId();
            rtcListener = new RoomRtcEventListener(roomId);

            // 加入rtc房间
            rtcClient.joinRtcRoom(roomId, rtcListener, new ResultCallback<RongRTCRoom>() {
                @Override
                public void onSuccess(RongRTCRoom rongRTCRoom) {
                    synchronized (roomLock) {
                        String rtcRoomId = rongRTCRoom.getRoomId();
                        // 若当前已不再房间则不进行处理
                        if (currentRoom == null || !currentRoom.getRoomId().equals(rtcRoomId)) {
                            rtcClient.quitRtcRoom(rtcRoomId, null);
                            callBack.onFail(ErrorCode.RTC_JOIN_ROOM_ERROR.getCode());
                            return;
                        }

                        currentRTCRoom = rongRTCRoom;

                        // 加入当前房间发言监听
                        currentRTCRoom.registerStatusReportListener(rtcListener);
                        callBack.onSuccess(true);
                    }
                }

                @Override
                public void onFail(int errorCode) {
                    callBack.onFail(errorCode);
                }
            });
        }
    }

    /**
     * 开始语音聊天
     *
     * @param callBack
     */
    public void startVoiceChat(ResultCallback<Boolean> callBack) {
        synchronized (roomLock) {
            if (currentRTCRoom != null) {
                rtcClient.startVoiceChat(currentRTCRoom, callBack);
            } else {
                notInJoinToRoomCallBack(callBack);
            }
        }
    }

    /**
     * 关闭语音聊天
     *
     * @param callBack
     */
    public void stopVoiceChat(ResultCallback<Boolean> callBack) {
        synchronized (roomLock) {
            if (currentRTCRoom != null) {
                rtcClient.stopVoiceChat(currentRTCRoom, callBack);
            } else {
                notInJoinToRoomCallBack(callBack);
            }
        }
    }

    /**
     * 设置是否开启房间聊天声音
     *
     * @param callBack
     */
    public void enableRoomChatVoice(final boolean enable, final ResultCallback<Boolean> callBack) {
        synchronized (roomLock) {
            if (currentRTCRoom != null) {
                if (enable) {
                    List<String> enableVoiceChatUserList = getEnableVoiceChatUserList();
                    rtcClient.receiveRoomVoice(currentRTCRoom, enableVoiceChatUserList, new ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            currentRoomAudioEnable = true;
                            callBack.onSuccess(aBoolean);
                        }

                        @Override
                        public void onFail(int errorCode) {
                            callBack.onFail(errorCode);
                        }
                    });
                } else {
                    rtcClient.muteRoomVoice(currentRTCRoom, new ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            currentRoomAudioEnable = false;
                            callBack.onSuccess(aBoolean);
                        }

                        @Override
                        public void onFail(int errorCode) {
                            callBack.onFail(errorCode);
                        }
                    });
                }
            } else {
                notInJoinToRoomCallBack(callBack);
            }
        }
    }

    /**
     * 开启或关闭自己的麦克风
     */
    public void enableMic(boolean enableMic) {
        rtcClient.setLocalMicEnable(enableMic);
    }

    /**
     * 返回当前可进行语音聊天的用户
     * 可进行语音聊天的用户包括房主及在麦位上未被禁麦的用户
     */
    public List<String> getEnableVoiceChatUserList() {
        synchronized (roomLock) {
            List<String> userList = new ArrayList<>();
            if (currentRoom == null) return userList;

            // 加入房主
            userList.add(currentRoom.getCreatorUserId());
            List<RoomMicPositionInfo> micPositions = currentRoom.getMicPositions();
            if (micPositions != null) {
                // 加入麦位上可发言的用户
                for (RoomMicPositionInfo micInfo : micPositions) {
                    int state = micInfo.getState();
                    if (MicState.isState(state, MicState.Hold)
                            && !MicState.isState(state, MicState.Forbidden)) {
                        userList.add(micInfo.getUserId());
                    }
                }
            }

            return userList;
        }
    }

    /**
     * 返回当前在麦位上的用户
     */
    public List<String> getOnMicPositionUserList() {
        synchronized (roomLock) {
            List<String> userList = new ArrayList<>();
            if (currentRoom == null) return userList;

            // 加入房主
            userList.add(currentRoom.getCreatorUserId());
            List<RoomMicPositionInfo> micPositions = currentRoom.getMicPositions();
            if (micPositions != null) {
                // 加入麦位上的用户
                for (RoomMicPositionInfo micInfo : micPositions) {
                    int state = micInfo.getState();
                    if (MicState.isState(state, MicState.Hold)) {
                        userList.add(micInfo.getUserId());
                    }
                }
            }

            return userList;
        }
    }

    /**
     * 获取当前角色
     *
     * @return
     */
    public Role getCurrentRole() {
        return currentRole;
    }

    /**
     * 根据当前房间信息初始化当前角色
     *
     * @return
     */
    private Role initCurrentRole() {
        String currentUserId = AuthManager.getInstance().getCurrentUserId();
        if (TextUtils.isEmpty(currentUserId) || currentRoom == null) {
            return null;
        }

        String creatorUserId = currentRoom.getCreatorUserId();

        // 在调用进入 IM 房间后，在调用 API服务器 进入房间成功前创建房间用户 id 为空，此时无法判断角色
        if (creatorUserId == null) {
            return null;
        }

        // 本人为创建房间用户时，自己即为房主
        if (currentUserId.equals(creatorUserId)) {
            return new Owner();
        }

        // 判断当前用户是否在麦位上，如果在麦位上则为连麦者
        List<RoomMicPositionInfo> micPositions = currentRoom.getMicPositions();
        if (micPositions != null) {
            for (RoomMicPositionInfo micInfo : micPositions) {
                String micUser = micInfo.getUserId();
                if (currentUserId.equals(micUser)) {
                    Linker linker = new Linker();
                    linker.setMicPositionInfo(micInfo);
                    return linker;
                }
            }
        }

        // 房间存在则当前用户为听众
        return new Listener();
    }

    /**
     * 获取当前房间声音是否开启
     *
     * @return
     */
    public boolean isCurrentRoomVoiceEnable() {
        return currentRoomAudioEnable;
    }

    /**
     * 当没有加入房间时回调错误
     *
     * @param callBack
     */
    private void notInJoinToRoomCallBack(final ResultCallback callBack) {
        threadManager.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onFail(ErrorCode.ROOM_NOT_JOIN_TO_ROOM.getCode());
                }
            }
        });
    }

    /**
     * 初始化房间信息
     *
     * @param roomId
     */
    private void initRoomInfo(String roomId) {
        synchronized (roomLock) {
            currentRoom = new DetailRoomInfo();
            currentRoom.setRoomId(roomId);
            currentRoomAudioEnable = true;
        }
    }

    /**
     * 清除房间信息
     */
    private void clearRoomInfo(String roomId) {
        synchronized (roomLock) {
            if (currentRoom != null && currentRoom.getRoomId().equals(roomId)) {
                currentRoom = null;
                if (currentRTCRoom != null) {
                    currentRTCRoom.unRegisterEventsListener(rtcListener);
                    currentRTCRoom.unRegisterStatusReportListener(rtcListener);
                    currentRTCRoom.release();
                    currentRTCRoom = null;
                }
                currentRoomEventListener = null;
                currentRole = null;
                currentRoomAudioEnable = false;
                if (currentRoomActiveThread != null) {
                    // 如果线程正在睡眠则打断睡眠，加速线程退出
                    currentRoomActiveThread.interrupt();
                    currentRoomActiveThread = null;
                }
            }
        }
    }

    /**
     * 设置房间背景
     *
     * @param backgroundIndex
     * @param callBack
     */
    public void setRoomBackground(final int backgroundIndex, final ResultCallback<Boolean> callBack) {
        synchronized (roomLock) {
            if (currentRoom == null) {
                notInJoinToRoomCallBack(callBack);
                return;
            }

            final String roomId = currentRoom.getRoomId();
            request.setRoomBackground(roomId, backgroundIndex, new HandleRequestWrapper<Boolean, Boolean>(callBack) {
                @Override
                public Boolean handleRequestResult(Boolean aBoolean) {
                    synchronized (roomLock) {
                        if (currentRoom != null
                                && currentRoom.getRoomId().equals(roomId)
                                && aBoolean) {
                            currentRoom.setBgId(backgroundIndex);
                        }
                    }
                    return aBoolean;
                }
            });
        }
    }

    /**
     * 聊天室相关信息拦截处理
     */
    private class RoomMessageListener implements RongIMClient.OnReceiveMessageListener {

        @Override
        public boolean onReceived(final Message message, int i) {
            synchronized (roomLock) {
                Conversation.ConversationType conversationType = message.getConversationType();
                if (conversationType == Conversation.ConversationType.CHATROOM) {
                    if (currentRoom == null) return false;

                    //判断chatRoomID
                    String targetId = message.getTargetId();
                    if (!currentRoom.getRoomId().equals(targetId)) return false;
                    final RoomEventListener roomEventlistener = currentRoomEventListener;

                    // 当为文本消息
                    if (message.getContent() instanceof TextMessage) {
                        //文本消息显示在聊天列表中
                        currentRoom.getMessageList().add(message);
                        if (roomEventlistener != null) {
                            threadManager.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    roomEventlistener.onMessageEvent(message);
                                }
                            });
                        }

                        //房间成员变动消息
                    } else if (message.getContent() instanceof RoomMemberChangedMessage) {
                        RoomMemberChangedMessage memberChangedMessage = (RoomMemberChangedMessage) message.getContent();
                        //房间成员变动消息显示在聊天列表中
                        currentRoom.getMessageList().add(message);
                        if (memberChangedMessage.getRoomMemberAction() == RoomMemberChangedMessage.RoomMemberAction.JOIN) {
                            currentRoom.setMemCount(currentRoom.getMemCount() + 1);
                        } else if (memberChangedMessage.getRoomMemberAction() == RoomMemberChangedMessage.RoomMemberAction.LEAVE
                                || memberChangedMessage.getRoomMemberAction() == RoomMemberChangedMessage.RoomMemberAction.KICK) {
                            currentRoom.setMemCount(currentRoom.getMemCount() - 1);
                        }
                        final int memCount = currentRoom.getMemCount();
                        if (roomEventlistener != null) {
                            threadManager.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    roomEventlistener.onMessageEvent(message);
                                    roomEventlistener.onRoomMemberChange(memCount);
                                }
                            });
                        }

                        String currentUserId = AuthManager.getInstance().getCurrentUserId();
                        String targetUserId = memberChangedMessage.getTargetUserId();
                        String creatorUserId = currentRoom.getCreatorUserId();

                        //房主退出房间时通知所有人退出,当自己为房主时不做重复通知
                        if (creatorUserId != null
                                && memberChangedMessage.getRoomMemberAction() == RoomMemberChangedMessage.RoomMemberAction.LEAVE
                                && creatorUserId.equals(targetUserId)
                                && !currentUserId.equals(creatorUserId)) {
                            if (roomEventlistener != null) {
                                threadManager.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        roomEventlistener.onRoomDestroy();
                                    }
                                });
                            }
                        }

                        //麦位变动消息更新麦位信息
                    } else if (message.getContent() instanceof MicPositionChangeMessage) {
                        MicPositionChangeMessage positionChangeMessage = (MicPositionChangeMessage) message.getContent();
                        final List<RoomMicPositionInfo> micPositions = positionChangeMessage.getMicPositions();
                        if (micPositions != null) {
                            currentRoom.setMicPositions(micPositions);
                        } else {
                            currentRoom.clearMicPositions();
                        }

                        if (roomEventlistener != null) {
                            threadManager.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    roomEventlistener.onMicUpdate(micPositions);
                                }
                            });
                        }

                        //麦位控制消息更新麦位信息
                    } else if (message.getContent() instanceof MicPositionControlMessage) {
                        MicPositionControlMessage positionChangeMessage = (MicPositionControlMessage) message.getContent();
                        final List<RoomMicPositionInfo> micPositions = positionChangeMessage.getMicPositions();
                        if (micPositions != null) {
                            currentRoom.setMicPositions(micPositions);
                        } else {
                            currentRoom.clearMicPositions();
                        }

                        if (roomEventlistener != null) {
                            threadManager.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    synchronized (roomLock) {
                                        roomEventlistener.onMicUpdate(micPositions);
                                    }
                                }
                            });
                        }

                        // 当前如果麦位控制信息针对当前用户，则进行麦克风开启和关闭事件
                        String currentUserId = AuthManager.getInstance().getCurrentUserId();
                        String targetUserId = positionChangeMessage.getTargetUserId();
                        if (currentUserId.equals(targetUserId)) {
                            final Role newRole = initCurrentRole();
                            currentRole = newRole;
                            MicBehaviorType behaviorType = positionChangeMessage.getBehaviorType();
                            switch (behaviorType) {
                                case PickupMic:
                                case JumpOnMic:
                                case UnForbidMic:
                                    if (roomEventlistener != null && newRole != null) {
                                        threadManager.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                roomEventlistener.onRoleChanged(newRole);
                                            }
                                        });
                                    }
                                    break;
                                case JumpDownMic:
                                case ForbidMic:
                                    if (roomEventlistener != null && newRole != null) {
                                        threadManager.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                roomEventlistener.onRoleChanged(newRole);
                                            }
                                        });
                                    }
                                    break;
                                case KickOffMic:
                                    currentRole = new Linker();
                                    if (roomEventlistener != null) {
                                        threadManager.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                roomEventlistener.onKickOffRoom();
                                            }
                                        });
                                    }
                                    break;
                            }
                        }

                        // 房间背景更新消息
                    } else if (message.getContent() instanceof RoomBgChangeMessage) {
                        final RoomBgChangeMessage bgChangeMessage = (RoomBgChangeMessage) message.getContent();
                        if (roomEventlistener != null) {
                            threadManager.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    roomEventlistener.onRoomBgChanged(bgChangeMessage.getBgId());
                                }
                            });
                        }
                    } else if (message.getContent() instanceof RoomDestroyNotifyMessage) {
                        if (roomEventlistener != null) {
                            threadManager.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    roomEventlistener.onRoomExistOverTimeLimit();
                                }
                            });
                        }
                    }
                    return true;
                }
                return false;
            }
        }
    }

    private class RoomRtcEventListener implements RongRTCEventsListener, RongRTCStatusReportListener {
        private String listenRoomId;

        RoomRtcEventListener(String roomId) {
            listenRoomId = roomId;
        }

        /**
         * 判断是否在房间内
         *
         * @return
         */
        private boolean isInRoom() {
            synchronized (roomLock) {
                if (currentRTCRoom != null) {
                    return currentRTCRoom.getRoomId().equals(listenRoomId);
                }
            }

            return false;
        }

        /**
         * 当有用户发布音视频流时回调，通过此回调可以订阅其他用户发布的的
         *
         * @param rongRTCRemoteUser
         * @param list
         */
        @Override
        public void onRemoteUserPublishResource(RongRTCRemoteUser rongRTCRemoteUser, List<RongRTCAVInputStream> list) {
            /*
             * 当前不在房间内或当前不开启房间内音频时不进行订阅音频
             */
            if (!isInRoom() || !currentRoomAudioEnable) return;

            /*
             * 判断当前用户是否已在可进行语音聊天用户列表中，
             * 仅接受在该列表中的用户音频声音
             */
            List<String> enableVoiceChatUserList = getEnableVoiceChatUserList();
            if (enableVoiceChatUserList.contains(rongRTCRemoteUser.getUserId())) {
                rongRTCRemoteUser.subscribeAvStream(rongRTCRemoteUser.getRemoteAVStreams(), new RongRTCResultUICallBack() {
                    @Override
                    public void onUiSuccess() {
                        SLog.d(SLog.TAG_RTC, "subscribeAvStream - success");
                    }

                    @Override
                    public void onUiFailed(RTCErrorCode rtcErrorCode) {
                        SLog.e(SLog.TAG_RTC, "subscribeAvStream - failed:" + rtcErrorCode.gerReason());
                    }
                });
            }
        }

        @Override
        public void onRemoteUserAudioStreamMute(RongRTCRemoteUser rongRTCRemoteUser, RongRTCAVInputStream rongRTCAVInputStream, boolean b) {

        }

        @Override
        public void onRemoteUserVideoStreamEnabled(RongRTCRemoteUser rongRTCRemoteUser, RongRTCAVInputStream rongRTCAVInputStream, boolean b) {

        }

        @Override
        public void onRemoteUserUnpublishResource(RongRTCRemoteUser rongRTCRemoteUser, List<RongRTCAVInputStream> list) {

        }


        @Override
        public void onUserJoined(RongRTCRemoteUser rongRTCRemoteUser) {
        }

        @Override
        public void onUserLeft(RongRTCRemoteUser rongRTCRemoteUser) {
        }

        @Override
        public void onUserOffline(RongRTCRemoteUser rongRTCRemoteUser) {
        }

        @Override
        public void onVideoTrackAdd(String s, String s1) {

        }

        @Override
        public void onFirstFrameDraw(String s, String s1) {
        }

        /**
         * 由于异常退出房间
         */
        @Override
        public void onLeaveRoom() {
            SLog.e(SLog.TAG_RTC, "Room id:" + (currentRoom != null ? currentRoom.getRoomId() : "") + ",onErrorLeaveRoom");
            final RoomEventListener roomEventlistener = currentRoomEventListener;
            if (roomEventlistener != null) {
                threadManager.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        roomEventlistener.onErrorLeaveRoom();
                    }
                });
            }
        }

        @Override
        public void onReceiveMessage(Message message) {

        }

        /**
         * 接受音频音频回调，通过该回调接口可以知道当前哪个用户在发言
         *
         * @param hashMap 用户id 与 代表当前音频声音大小的等级，注意此处的用户id会有后缀tag
         *                如果非用户自定义的音频流则以 _RongCloudRTC 为后缀
         */
        @Override
        public void onAudioReceivedLevel(HashMap<String, String> hashMap) {
            if (hashMap == null || hashMap.size() == 0) return;
            final List<String> enableVoiceChatUserList = getEnableVoiceChatUserList();
            final List<String> speakUserList = new ArrayList<>();
            Set<String> userAudioSet = hashMap.keySet();
            for (String userAudioId : userAudioSet) {
                /*
                 * 此处的userId包含固定的后缀_RongCloudRTC，需要截取
                 */
                String userId = userAudioId;
                if (userId != null && userId.endsWith("_RongCloudRTC")) {
                    userId = userAudioId.substring(0, userAudioId.lastIndexOf("_RongCloudRTC"));
                }

                // 判断该音频是否在可发言用户列表中
                if (enableVoiceChatUserList.contains(userId)) {
                    // 获取音频声音等级，大于0则代表正在发言
                    String audioLevel = hashMap.get(userAudioId);
                    int levelValue = 0;
                    try {
                        levelValue = Integer.parseInt(audioLevel);
                    } catch (Exception e) {
                    }

                    if (levelValue > 0) {
                        speakUserList.add(userId);
                    }
                }
            }

            final RoomEventListener eventListener = currentRoomEventListener;
            if (eventListener != null) {
                threadManager.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        eventListener.onRoomMicSpeak(speakUserList);
                    }
                });
            }
        }

        /**
         * 当前发言者音频回调，通过该回调接口可以知道当前自己是否在发言
         *
         * @param audioLevel 声音大小等级，0代表没有声音
         */
        @Override
        public void onAudioInputLevel(String audioLevel) {
            final List<String> enableVoiceChatUserList = getEnableVoiceChatUserList();
            String currentUserId = AuthManager.getInstance().getCurrentUserId();
            if (enableVoiceChatUserList.contains(currentUserId)) {
                final List<String> speakUserList = new ArrayList<>();
                // 获取音频声音等级，大于0则代表正在发言
                int levelValue = 0;
                try {
                    levelValue = Integer.parseInt(audioLevel);
                } catch (Exception e) {
                }

                if (levelValue > 0) {
                    speakUserList.add(currentUserId);
                }

                final RoomEventListener eventListener = currentRoomEventListener;
                if (eventListener != null) {
                    threadManager.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            eventListener.onRoomMicSpeak(speakUserList);
                        }
                    });
                }
            }
        }

        @Override
        public void onConnectionStats(StatusReport statusReport) {

        }
    }

    /**
     * 用于房主定时发送房间正在活动消息的线程
     * 保证聊天室不会因长时间没有人发消息而销毁
     */
    private class RoomActiveMsgSenderThread extends Thread {
        private String roomId;

        public RoomActiveMsgSenderThread(String roomId) {
            this.roomId = roomId;
        }

        /**
         * 判断当前所在房间是否为启动该线程时的房间
         *
         * @return
         */
        private boolean isInRoom() {
            synchronized (roomLock) {
                if (currentRoom != null) {
                    String currentRoomRoomId = currentRoom.getRoomId();
                    if (this.roomId != null && this.roomId.equals(currentRoomRoomId)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void run() {
            // 当房主在房间时，每隔一定时间就发送房间正在活动消息
            while (isInRoom()) {
                RoomIsActiveMessage roomIsActiveMessage = new RoomIsActiveMessage();
                Message message = Message.obtain(roomId, Conversation.ConversationType.CHATROOM, roomIsActiveMessage);
                RongIMClient.getInstance().sendMessage(message, null, null, new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                    }

                    @Override
                    public void onSuccess(Message message) {
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    }
                });
                try {
                    Thread.sleep(ROOM_SEND_ACTIVE_INTERVAL_TIME_MILLIS);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}