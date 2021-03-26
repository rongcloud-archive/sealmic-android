package cn.rongcloud.sealmicandroid.manager;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.rongcloud.rtc.api.callback.IRCRTCResultCallback;
import cn.rongcloud.rtc.base.RTCErrorCode;
import cn.rongcloud.sealmicandroid.bean.kv.MicBean;
import cn.rongcloud.sealmicandroid.common.SealMicResultCallback;
import cn.rongcloud.sealmicandroid.common.adapter.SendMessageAdapter;
import cn.rongcloud.sealmicandroid.common.constant.SealMicConstant;
import cn.rongcloud.sealmicandroid.im.IMClient;
import cn.rongcloud.sealmicandroid.rtc.RTCClient;
import cn.rongcloud.sealmicandroid.util.log.SLog;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;

/**
 * 有关房间的操作均在此
 */
public class RoomManager {

    private static final String RTC_PREFIX = "";

    public static RoomManager getInstance() {
        return RoomManagerHelper.INSTANCE;
    }

    private static class RoomManagerHelper {
        private static final RoomManager INSTANCE = new RoomManager();
    }

    private RoomManager() {
    }

    /**
     * 主播加入房间
     */
    public void micJoinRoom(final String roomId, IRongCoreCallback.ResultCallback<String> imCallBack) {
        //加入IM
        IMClient.getInstance().joinChatRoom(roomId, imCallBack);
        //初始化RTC
        RTCClient.getInstance().init();
        //加入RTC
        RTCClient.getInstance().micJoinRoom(RTC_PREFIX + roomId);
    }

    /**
     * 主播退出房间
     */
    public void micQuitRoom(String roomId, IRongCoreCallback.ResultCallback<String> imCallBack) {
        //退出IM
        IMClient.getInstance().quitChatRoom(roomId, imCallBack);
        //退出RTC
        RTCClient.getInstance().micQuitRoom(RTC_PREFIX + roomId, null);
        //回收RTC
        RTCClient.getInstance().unInit();
    }

    /**
     * 观众加入聊天室
     */
    public void audienceJoinRoom(final String roomId, final SealMicResultCallback<Boolean> callback) {
        RTCClient.getInstance().init();
        //加入IM聊天室
        IMClient.getInstance().joinChatRoom(roomId, new IRongCoreCallback.ResultCallback<String>() {
            @Override
            public void onSuccess(final String roomId) {
                //加入RTC聊天室
                RTCClient.getInstance().audienceJoinRoom(RTC_PREFIX + roomId, new IRCRTCResultCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess(true);
                        SLog.e(SLog.TAG_SEAL_MIC, "观众加入RTC房间成功");
                    }

                    @Override
                    public void onFailed(RTCErrorCode rtcErrorCode) {
                        callback.onFail(rtcErrorCode.getValue());
                        SLog.e(SLog.TAG_SEAL_MIC, "观众加入RTC房间失败，错误码为: " + rtcErrorCode.toString());
                    }
                });
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                //TODO 未成功加入房间
                SLog.e(SLog.TAG_SEAL_MIC, "观众加入聊天室失败: " + coreErrorCode.toString());
            }

        });

    }

    /**
     * 观众退出房间
     */
    public void audienceQuitRoom(final String roomId, final IRongCoreCallback.ResultCallback<String> imCallBack) {
        RTCClient.getInstance().audienceQuitRoom(RTC_PREFIX + roomId, new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {
                RTCClient.getInstance().unInit();
                //退出IM聊天室
                IMClient.getInstance().quitChatRoom(roomId, imCallBack);
            }

            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {
                RTCClient.getInstance().unInit();
                SLog.e(SLog.TAG_SEAL_MIC, "观众退出RTC房间失败: " + rtcErrorCode.toString());
            }
        });
    }

    /**
     * 观众端上麦
     */
    public void audienceGoMic(final String roomId, final SealMicResultCallback<String> callback) {
        RTCClient.getInstance().init();
        //所谓的观众端上麦就是观众变成主播，目前的版本下，需要观众先退RTC房，然后再以主播身份重新加RTC房
        RTCClient.getInstance().audienceQuitRoom(RTC_PREFIX + roomId, new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {
                RTCClient.getInstance().micJoinRoom(RTC_PREFIX + roomId);
                callback.onSuccess(roomId);
                SLog.e(SLog.TAG_SEAL_MIC, "观众离开房间，准备上麦，即以主播的身份重新进入房间");
            }

            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {
                SLog.e(SLog.TAG_SEAL_MIC, "观众端退出RTC房间失败: " + rtcErrorCode.toString());
            }
        });
//        IMClient.getInstance().getChatRoomEntry(roomId, LIVE_URL, new IRongCoreCallback.ResultCallback<Map<String, String>>() {
//            @Override
//            public void onSuccess(final Map<String, String> stringStringMap) {
//                String liveUrl = stringStringMap.get(LIVE_URL);
//                SLog.e(SLog.TAG_SEAL_MIC, "观众上麦之后下发的KV: " + stringStringMap.toString());
//                //1. 应先调用 RongRTCEngine#unsubscribeLiveAVStream 接口取消观看直播。
//                if (!TextUtils.isEmpty(liveUrl)) {
//                    SLog.e(SLog.TAG_SEAL_MIC, "观众上麦之后的liveUrl: " + liveUrl);
//                    RTCClient.getInstance().unsubscribeLiveAVStream(liveUrl, new IRCRTCResultCallback() {
//                        @Override
//                        public void onSuccess() {
//                            //2. 取消观看直播后，调用 RongRTCEngine#joinRoom 接口加入到直播房间升级为主播。
//                            //3. 升级为主播后，调用 RongRTCRoom#publishDefaultLiveAVStream 接口来订阅观看。
//                            RTCClient.getInstance().micJoinRoom(roomId);
//                            SLog.e(SLog.TAG_SEAL_MIC, "观众上麦成功！");
//                            callback.onSuccess(stringStringMap);
//                            //上麦工程后角色变为连麦者
////                            CacheManager.getInstance().cacheUserRoleType(UserRoleType.CONNECT_MIC.getValue());
//                        }
//
//                        @Override
//                        public void onFailed(RTCErrorCode rtcErrorCode) {
//                            if (rtcErrorCode == RTCErrorCode.ILLEGALSTATE) {
//                                RTCClient.getInstance().micJoinRoom(roomId);
//                                callback.onSuccess(stringStringMap);
//                            }
//                            SLog.e(SLog.TAG_SEAL_MIC, "观众上麦失败: " + rtcErrorCode.getValue());
//                        }
//                    });
//                } else {
//                    RTCClient.getInstance().micJoinRoom(roomId);
//                    callback.onSuccess(stringStringMap);
//                }
//            }
//
//            @Override
//            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
//                if (coreErrorCode.getValue() == 23427) {
//                    RTCClient.getInstance().micJoinRoom(roomId);
//                    //代表成功
//                    callback.onSuccess(null);
//                } else {
//                    callback.onFail(coreErrorCode.getValue());
//                }
//                SLog.e(SLog.TAG_SEAL_MIC, "获取聊天室liveUrl失败: " + coreErrorCode.getValue());
//
//            }
//        });

    }

    /**
     * 主播下麦
     */
    public void micGoDown(final String roomId, final SealMicResultCallback<String> callback) {
        RTCClient.getInstance().init();
        //所谓的主播下麦就是主播变成观众，在当前版本下，主播退出房RTC房间，以观众身份重新加入RTC房间
        RTCClient.getInstance().micQuitRoom(RTC_PREFIX + roomId, new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {
                SLog.e(SLog.TAG_SEAL_MIC, "主播退出RTC成功");
                RTCClient.getInstance().audienceJoinRoom(RTC_PREFIX + roomId, new IRCRTCResultCallback() {
                    @Override
                    public void onSuccess() {
                        SLog.e(SLog.TAG_SEAL_MIC, "主播下麦成功");
                        callback.onSuccess(roomId);
                    }

                    @Override
                    public void onFailed(RTCErrorCode rtcErrorCode) {
                        SLog.e(SLog.TAG_SEAL_MIC, "主播下麦失败: " + rtcErrorCode.toString());
                    }
                });
//                IMClient.getInstance().getChatRoomEntry(roomId, LIVE_URL, new IRongCoreCallback.ResultCallback<Map<String, String>>() {
//                    @Override
//                    public void onSuccess(Map<String, String> stringStringMap) {
//                        String liveUrl = stringStringMap.get(LIVE_URL);
//                        SLog.e(SLog.TAG_SEAL_MIC, "主播下麦后下发的KV map: " + stringStringMap.toString());
//                        if (!TextUtils.isEmpty(liveUrl)) {
//                            SLog.e(SLog.TAG_SEAL_MIC, "主播下麦后获取的liveUrl: " + liveUrl);
//                            //如果取消连麦后，用户还需要继续观看直播，则可以调用 RongRTCEngine#subscribeLiveAVStream 接口开始观看直播。
//                            RTCClient.getInstance().subscribeLiveAVStream(liveUrl);
//                            //主播下麦之后角色变为观众
////                    CacheManager.getInstance().cacheUserRoleType(UserRoleType.AUDIENCE.getValue());
//                            callback.onSuccess(stringStringMap);
//                        }
//                    }
//
//                    @Override
//                    public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
//                        SLog.e(SLog.TAG_SEAL_MIC, "主播下麦失败: " + coreErrorCode.getValue());
//                    }
//                });
            }

            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {
                SLog.e(SLog.TAG_SEAL_MIC, "主播退出RTC失败: " + rtcErrorCode.toString());
            }
        });

    }

    public void getAllChatRoomMic(final String roomId, final IRongCoreCallback.ResultCallback<Map<String, String>> callback) {
//        for (int i = 0; i < 2; i++) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                IMClient.getInstance().getAllChatRoomEntries(roomId, new IRongCoreCallback.ResultCallback<Map<String, String>>() {
                    @Override
                    public void onSuccess(Map<String, String> stringStringMap) {
                        SLog.e(SLog.TAG_SEAL_MIC, "从IM SDK中获取并返回的KV: " + stringStringMap.toString());
                        Map<String, String> micmap = new HashMap<>();
                        for (String key : stringStringMap.keySet()) {
                            if (key.contains(SealMicConstant.KV_POSITION)) {
                                micmap.put(key, stringStringMap.get(key));
                            }
                        }
                        callback.onSuccess(micmap);
                    }

                    @Override
                    public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                        callback.onError(coreErrorCode);
                    }
                });
            }
        }, SealMicConstant.DELAY_KV);
//        }
    }

    public void getAllChatRoomSpeaking(final String roomId, final IRongCoreCallback.ResultCallback<Map<String, String>> callback) {
        for (int i = 0; i < 2; i++) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    IMClient.getInstance().getAllChatRoomEntries(roomId, new IRongCoreCallback.ResultCallback<Map<String, String>>() {
                        @Override
                        public void onSuccess(Map<String, String> stringStringMap) {
                            Map<String, String> micmap = new HashMap<>();
                            for (String key : stringStringMap.keySet()) {
                                Log.i(SLog.TAG_SEAL_MIC + " 获取全部的的讲话状态", micmap.toString());
                                if (key.contains(SealMicConstant.KV_SPEAK)) {
                                    micmap.put(key, stringStringMap.get(key));
                                }
                            }
                            callback.onSuccess(micmap);
                        }

                        @Override
                        public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                            callback.onError(coreErrorCode);
                        }
                    });
                }
            }, SealMicConstant.DELAY_KV);
        }
    }

    /**
     * 将所有的kv对象转为MicBean对象
     *
     * @param stringStringMap       从KV中取得的map
     * @param sealMicResultCallback 结果回调
     */
    public void transMicBean(Map<String, String> stringStringMap, @NonNull final SealMicResultCallback<MicBean> sealMicResultCallback) {
        for (String key : stringStringMap.keySet()) {
            final MicBean micBean = new Gson().fromJson(stringStringMap.get(key), MicBean.class);
//            if (!TextUtils.isEmpty(micBean.getUserId())) {
            ThreadManager.getInstance().runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    //当前包括主持人在内共9个麦位，当有1个以上的麦位上有人时，该回调会调用多次
                    sealMicResultCallback.onSuccess(micBean);
                }
            });
//            }
        }
    }

    public void sendMessage(String roomId, String messageContent, SendMessageAdapter sendMessageAdapter) {
        IMClient.getInstance().sendMessage(IMClient.getInstance().getTextMessage(roomId,
                messageContent),
                sendMessageAdapter);
    }

    public void getHistoryMessage(String roomId, IRongCoreCallback.IChatRoomHistoryMessageCallback callback) {
        IMClient.getInstance().getHistoryMessage(roomId, callback);
    }

}
