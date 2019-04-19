package cn.rongcloud.sealmic.rtc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.rongcloud.rtc.RTCErrorCode;
import cn.rongcloud.rtc.RongRTCEngine;
import cn.rongcloud.rtc.callback.JoinRoomUICallBack;
import cn.rongcloud.rtc.callback.RongRTCResultUICallBack;
import cn.rongcloud.rtc.events.RongRTCEventsListener;
import cn.rongcloud.rtc.room.RongRTCRoom;
import cn.rongcloud.rtc.stream.MediaType;
import cn.rongcloud.rtc.stream.ResourceState;
import cn.rongcloud.rtc.stream.local.RongRTCAVOutputStream;
import cn.rongcloud.rtc.stream.local.RongRTCCapture;
import cn.rongcloud.rtc.stream.remote.RongRTCAVInputStream;
import cn.rongcloud.rtc.user.RongRTCLocalUser;
import cn.rongcloud.rtc.user.RongRTCRemoteUser;
import cn.rongcloud.sealmic.constant.ErrorCode;
import cn.rongcloud.sealmic.task.ResultCallback;
import cn.rongcloud.sealmic.task.ThreadManager;
import cn.rongcloud.sealmic.utils.log.SLog;

/**
 * Rong RTC 语音业务封装
 */
public class RtcClient {
    private static RtcClient instance;

    private RtcClient() {
    }

    public static RtcClient getInstance() {
        if (instance == null) {
            synchronized (RtcClient.class) {
                if (instance == null) {
                    instance = new RtcClient();
                }
            }
        }

        return instance;
    }


    /**
     * 加入语音聊天房间
     *
     * @param roomId
     * @param callBack
     */
    public void joinRtcRoom(final String roomId, final RongRTCEventsListener rtcEventsListener, final ResultCallback<RongRTCRoom> callBack) {
        RongRTCEngine.getInstance().joinRoom(roomId, new JoinRoomUICallBack() {
            @Override
            protected void onUiSuccess(RongRTCRoom rtcRoom) {
                rtcRoom.registerEventsListener(rtcEventsListener);
                if (callBack != null) {
                    callBack.onSuccess(rtcRoom);
                }
            }

            @Override
            protected void onUiFailed(RTCErrorCode rtcErrorCode) {
                SLog.e(SLog.TAG_RTC, "joinRtcRoom failed - " + rtcErrorCode.gerReason());
                if (callBack != null) {
                    callBack.onFail(ErrorCode.RTC_ERROR.getCode());
                }
            }
        });
    }

    /**
     * 离开语音聊天室
     *
     * @param roomId
     * @param callBack
     */
    public void quitRtcRoom(final String roomId, final ResultCallback<Boolean> callBack) {
        RongRTCEngine.getInstance().quitRoom(roomId, new RongRTCResultUICallBack() {
            @Override
            public void onUiSuccess() {
                if (callBack != null) {
                    callBack.onSuccess(true);
                }
            }

            @Override
            public void onUiFailed(RTCErrorCode rtcErrorCode) {
                SLog.e(SLog.TAG_RTC, "quitRtcRoom error - " + rtcErrorCode.gerReason());
                if (callBack != null) {
                    callBack.onFail(ErrorCode.RTC_ERROR.getCode());
                }
            }
        });
    }

    /**
     * 开启语音聊天
     *
     * @param rtcRoom
     * @param callBack
     */
    public void startVoiceChat(RongRTCRoom rtcRoom, final ResultCallback<Boolean> callBack) {
        RongRTCLocalUser localUser = rtcRoom.getLocalUser();
        List<RongRTCAVOutputStream> localAvStreams = localUser.getLocalAvStreams();

        if (localAvStreams != null) {
            for (RongRTCAVOutputStream outputStream : localAvStreams) {
                MediaType mediaType = outputStream.getMediaType();
                // 禁用视频
                if (mediaType == MediaType.VIDEO) {
                    outputStream.setResourceState(ResourceState.DISABLED);
                }
            }
        }
        localUser.publishDefaultAVStream(new RongRTCResultUICallBack() {
            @Override
            public void onUiSuccess() {
                if (callBack != null) {
                    callBack.onSuccess(true);
                }
            }

            @Override
            public void onUiFailed(RTCErrorCode rtcErrorCode) {
                SLog.e(SLog.TAG_RTC, "startVoiceChat error - " + rtcErrorCode.gerReason());
                if (callBack != null) {
                    callBack.onFail(ErrorCode.RTC_ERROR.getCode());
                }
            }
        });
    }

    /**
     * 停止语音聊天
     *
     * @param rtcRoom
     * @param callBack
     */
    public void stopVoiceChat(RongRTCRoom rtcRoom, final ResultCallback<Boolean> callBack) {
        RongRTCLocalUser localUser = rtcRoom.getLocalUser();
        localUser.unPublishDefaultAVStream(new RongRTCResultUICallBack() {
            @Override
            public void onUiSuccess() {
                if (callBack != null) {
                    callBack.onSuccess(true);
                }
            }

            @Override
            public void onUiFailed(RTCErrorCode rtcErrorCode) {
                SLog.e(SLog.TAG_RTC, "stopVoiceChat error - " + rtcErrorCode.gerReason());
                if (callBack != null) {
                    callBack.onFail(ErrorCode.RTC_ERROR.getCode());
                }
            }
        });
    }

    /**
     * 设置麦克风是否可用
     *
     * @param enable
     */
    public void setLocalMicEnable(boolean enable) {
        RongRTCCapture.getInstance().muteMicrophone(!enable);
    }

    /**
     * 接受房间语音
     *
     * @param rtcRoom
     * @param userList 要接受语音的用户列表
     * @param callBack
     */
    public void receiveRoomVoice(RongRTCRoom rtcRoom, List<String> userList, final ResultCallback<Boolean> callBack) {
        Map<String, RongRTCRemoteUser> remoteUsers = rtcRoom.getRemoteUsers();
        List<RongRTCAVInputStream> receiveVoiceSteamList = new ArrayList<>();
        for (String userId : userList) {
            RongRTCRemoteUser rongRTCRemoteUser = remoteUsers.get(userId);
            if (rongRTCRemoteUser != null) {
                List<RongRTCAVInputStream> remoteAVStreams = rongRTCRemoteUser.getRemoteAVStreams();
                if (remoteAVStreams != null && remoteAVStreams.size() > 0) {
                    receiveVoiceSteamList.addAll(remoteAVStreams);
                }
            }
        }
        if (receiveVoiceSteamList.size() > 0) {
            rtcRoom.subscribeAvStream(receiveVoiceSteamList, new RongRTCResultUICallBack() {
                @Override
                public void onUiSuccess() {
                    if (callBack != null) {
                        callBack.onSuccess(true);
                    }
                }

                @Override
                public void onUiFailed(RTCErrorCode rtcErrorCode) {
                    SLog.e(SLog.TAG_RTC, "receiveRoomVoice error - " + rtcErrorCode.gerReason());
                    if (callBack != null) {
                        callBack.onFail(ErrorCode.RTC_ERROR.getCode());
                    }
                }
            });
        }else{
            ThreadManager.getInstance().runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (callBack != null) {
                        callBack.onSuccess(true);
                    }
                }
            });
        }
    }

    /**
     * 房间静音
     *
     * @param rtcRoom
     * @param callBack
     */
    public void muteRoomVoice(RongRTCRoom rtcRoom, final ResultCallback<Boolean> callBack) {
        Map<String, RongRTCRemoteUser> remoteUserMap = rtcRoom.getRemoteUsers();
        List<RongRTCAVInputStream> receiveVoiceSteamList = new ArrayList<>();
        Collection<RongRTCRemoteUser> remoteUsers = remoteUserMap.values();
        for (RongRTCRemoteUser remoteUser : remoteUsers) {
            receiveVoiceSteamList.addAll(remoteUser.getRemoteAVStreams());
        }

        if (receiveVoiceSteamList.size() > 0) {
            rtcRoom.unSubscribeAVStream(receiveVoiceSteamList, new RongRTCResultUICallBack() {
                @Override
                public void onUiSuccess() {
                    if (callBack != null) {
                        callBack.onSuccess(true);
                    }
                }

                @Override
                public void onUiFailed(RTCErrorCode rtcErrorCode) {
                    SLog.e(SLog.TAG_RTC, "muteRoomVoice error - " + rtcErrorCode.gerReason());
                    if (callBack != null) {
                        callBack.onFail(ErrorCode.RTC_ERROR.getCode());
                    }
                }
            });
        }else{
            ThreadManager.getInstance().runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (callBack != null) {
                        callBack.onSuccess(true);
                    }
                }
            });
        }
    }
}
