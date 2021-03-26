package cn.rongcloud.sealmicandroid.rtc;

import android.content.Context;
import android.media.AudioManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.rtc.api.RCRTCAudioMixer;
import cn.rongcloud.rtc.api.RCRTCConfig;
import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.RCRTCRemoteUser;
import cn.rongcloud.rtc.api.RCRTCRoom;
import cn.rongcloud.rtc.api.RCRTCRoomConfig;
import cn.rongcloud.rtc.api.callback.IRCRTCResultCallback;
import cn.rongcloud.rtc.api.callback.IRCRTCResultDataCallback;
import cn.rongcloud.rtc.api.callback.IRCRTCStatusReportListener;
import cn.rongcloud.rtc.api.report.StatusBean;
import cn.rongcloud.rtc.api.report.StatusReport;
import cn.rongcloud.rtc.api.stream.RCRTCInputStream;
import cn.rongcloud.rtc.api.stream.RCRTCLiveInfo;
import cn.rongcloud.rtc.api.stream.RCRTCMicOutputStream;
import cn.rongcloud.rtc.base.RCRTCLiveRole;
import cn.rongcloud.rtc.base.RCRTCParamsType;
import cn.rongcloud.rtc.base.RCRTCRoomType;
import cn.rongcloud.rtc.base.RTCErrorCode;
import cn.rongcloud.sealmicandroid.BuildConfig;
import cn.rongcloud.sealmicandroid.SealMicApp;
import cn.rongcloud.sealmicandroid.common.Event;
import cn.rongcloud.sealmicandroid.common.adapter.RTCEventsListenerAdapter;
import cn.rongcloud.sealmicandroid.im.IMClient;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.util.log.SLog;

/**
 * Rong RTC 语音业务封装
 *
 * @author yangyi
 */
public class RTCClient {

    private static final String SPEAKING = "speaking_";
    /**
     * 上次是否在讲话
     */
    private int lastSpeakingLevel;

    private RTCClient() {

    }

    private static class RTCClientHelper {
        private static final RTCClient INSTANCE = new RTCClient();
    }

    public static RTCClient getInstance() {
        return RTCClientHelper.INSTANCE;
    }

    public void initMediaServer() {
        if (BuildConfig.Media_server != null && !BuildConfig.Media_server.isEmpty()) {
            RCRTCEngine.getInstance().setMediaServerUrl(BuildConfig.Media_server);
        }
    }

    public void init() {
        RCRTCConfig config = RCRTCConfig.Builder.create()
                //是否硬解码
                .enableHardwareDecoder(true)
                //是否使用硬编码
                .enableHardwareEncoder(true)
                .build();
        //使用默认配置，直接传null
        RCRTCEngine.getInstance().init(SealMicApp.getApplication(), config);
        RCRTCMicOutputStream rcrtcMicOutputStream = RCRTCEngine.getInstance().getDefaultAudioStream();
        if (rcrtcMicOutputStream != null) {
            rcrtcMicOutputStream.setAudioQuality(RCRTCParamsType.AudioQuality.MUSIC_HIGH, RCRTCParamsType.AudioScenario.MUSIC_CHATROOM);
        }
    }

    public void unInit() {
        //回收RTC
        RCRTCEngine.getInstance().unInit();
    }

    /**
     * 主播加入语音聊天房间
     * 1. 主播端首先调用 RongRTCEngine#joinRoom 接口创建一个直播类型房间
     * 2. 创建直播类型房间成功后，调用 RongRTCLocalUser#publishDefaultLiveAVStream 接口发布音视频资源
     * 3. 音视频资源发布成功后会返回一个 RongRTCLiveInfo 对象，对象中包含了 RoomId、直播地址( liveUrl )等信息，将以上信息上传到自己的 APPServer，至此主播已创建好一个直播间等待观众端的加入
     */
    public void micJoinRoom(final String roomId) {
        //主播端首先调用 RCRTCEngine#joinRoom 接口创建一个直播类型房间
        RCRTCRoomConfig roomConfig = RCRTCRoomConfig.Builder.create()
                //以主播的形式进入房间
                .setLiveRole(RCRTCLiveRole.BROADCASTER)
                //根据实际场景，选择音视频直播：LIVE_AUDIO_VIDEO 或音频直播：LIVE_AUDIO
                .setRoomType(RCRTCRoomType.LIVE_AUDIO)
                .build();
        RCRTCEngine.getInstance().joinRoom(roomId, roomConfig, new IRCRTCResultDataCallback<RCRTCRoom>() {
            @Override
            public void onSuccess(final RCRTCRoom rcrtcRoom) {
                //第一步:
                //RTCEventsListenerAdapter中的onRemoteUserPublishResource监听后加入房间的主播所发布的资源
                rcrtcRoom.registerRoomListener(new RTCEventsListenerAdapter() {
                    @Override
                    public void onRemoteUserPublishResource(RCRTCRemoteUser rcrtcRemoteUser, List<RCRTCInputStream> list) {
                        super.onRemoteUserPublishResource(rcrtcRemoteUser, list);
                        //订阅远端其他人发布出来的资源
                        if (rcrtcRoom.getLocalUser() == null) {
                            return;
                        }
                        rcrtcRoom.getLocalUser().subscribeStreams(list, new IRCRTCResultCallback() {
                            @Override
                            public void onSuccess() {
                                SLog.e(SLog.TAG_SEAL_MIC, "主播订阅远端用户成功");
                            }

                            @Override
                            public void onFailed(RTCErrorCode rtcErrorCode) {
                                SLog.e(SLog.TAG_SEAL_MIC, "主播订阅远端用户失败: " + rtcErrorCode.toString());
                            }
                        });
                    }
                });

                //第二步:
                //监听质量数据，诸如主播的延迟之类的
                RCRTCEngine.getInstance().registerStatusReportListener(new IRCRTCStatusReportListener() {
                    /**
                     * 状态信息的输出，每秒输出一次。
                     *
                     * @param statusReport {@link StatusReport}
                     */
                    @Override
                    public void onConnectionStats(StatusReport statusReport) {
                        super.onConnectionStats(statusReport);
                        EventBus.getDefault().postSticky(new Event.EventMicStatusReport(statusReport));
                    }

                    /**
                     * 以 HashMap 形式返回参与者的 userID 和 audiolevel ，每秒钟刷新一次。当 AudioLevel 大于 0 时，即认为该用户正在讲话
                     * 本端接收对端说话状态
                     */
                    @Override
                    public void onAudioReceivedLevel(HashMap<String, String> audioLevel) {
                        super.onAudioReceivedLevel(audioLevel);
                        EventBus.getDefault().post(new Event.EventRemoteAudioChange(audioLevel));
                    }

                    /**
                     * 本端说话状态
                     * @param audioLevel 声音值
                     */
                    @Override
                    public void onAudioInputLevel(String audioLevel) {
                        super.onAudioInputLevel(audioLevel);

                        EventBus.getDefault().post(new Event.EventLocalAudioChange(audioLevel));

                        String roomId = CacheManager.getInstance().getRoomId();

                        // 获取音频声音等级，大于0则代表正在发言
                        int levelValue = 0;
                        try {
                            levelValue = Integer.parseInt(audioLevel);
                        } catch (Exception e) {

                        }

                        String value;
                        int position = CacheManager.getInstance().getMicBean().getPosition();

                        //如果正在说话
                        if (levelValue > 0) {
                            //上次也正在说话
                            if (lastSpeakingLevel > 0) {
                                return;
                            } else {
                                //如果正在说话但是上次并没有话说，说明讲话状态改变
                                value =
                                        "{" + '"' + "speaking" + '"' + ":1" +
                                                ", " + '"' + "position" + '"' + ":" + position +
                                                '}';
                            }
                        } else {
                            //如果没在说话,
                            //上次也没在说话
                            if (lastSpeakingLevel <= 0) {
                                return;
                            } else {
                                //如果没在说话但是这次说话了，说明讲话状态改变了
                                value =
                                        "{" + '"' + "speaking" + '"' + ":0" +
                                                ", " + '"' + "position" + '"' + ":" + position +
                                                '}';
                            }
                        }
                        lastSpeakingLevel = levelValue;

                        IMClient.getInstance().setChatRoomSpeakEntry(roomId, SPEAKING + position, value);
//                        EventBus.getDefault().post(new Event.EventAudioInputLevel(position, levelValue));
                    }
                });

                //第三步:
                //创建直播类型房间成功后，调用 RCRTCRoom#publishLiveStream 接口发布音视频资源
                //只发布音频资源
                rcrtcRoom.getLocalUser().publishLiveStream(rcrtcRoom.getLocalUser().getDefaultAudioStream(), new IRCRTCResultDataCallback<RCRTCLiveInfo>() {
                    @Override
                    public void onSuccess(final RCRTCLiveInfo rongRTCLiveInfo) {
                        //音视频资源发布成功后会返回一个 RCRTCLiveInfo 对象，
                        //对象中包含了 RoomId、直播地址( liveUrl )等信息，
                        //将以上信息上传到自己的 APPServer，至此主播已创建好一个直播间等待观众端的加入
//                        new Timer().schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                SLog.e(SLog.TAG_SEAL_MIC, "主播加入房间后发布音频流资源成功");
//                                IMClient.getInstance().setChatRoomEntry(rongRTCLiveInfo.getRoomId(),
//                                        LIVE_URL,
//                                        rongRTCLiveInfo.getLiveUrl());
//                            }
//                        }, SealMicConstant.DELAY_KV);
                        SLog.e(SLog.TAG_SEAL_MIC, "主播加入房间后发布音频流资源成功");
                    }

                    @Override
                    public void onFailed(RTCErrorCode rtcErrorCode) {
                        SLog.e(SLog.TAG_SEAL_MIC, "主播加入RTC房间之后发布流失败: " + rtcErrorCode.toString());
                    }
                });

                //第四步:
                //订阅当下的其他主播发出的流
                //发布和订阅并发执行
                List<RCRTCRemoteUser> rcrtcRemoteUserList = rcrtcRoom.getRemoteUsers();
                for (RCRTCRemoteUser rcrtcRemoteUser : rcrtcRemoteUserList) {
                    rcrtcRoom.getLocalUser().subscribeStreams(rcrtcRemoteUser.getStreams(), new IRCRTCResultCallback() {
                        @Override
                        public void onSuccess() {
                            SLog.e(SLog.TAG_SEAL_MIC, "订阅当下其他主播的流成功");
                        }

                        @Override
                        public void onFailed(RTCErrorCode rtcErrorCode) {
                            SLog.e(SLog.TAG_SEAL_MIC, "订阅当下其他主播的流失败");
                        }
                    });
                }
            }

            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {
                SLog.e(SLog.TAG_SEAL_MIC, "主播加入RTC房间失败: " + rtcErrorCode.toString());
            }
        });
    }

    /**
     * 主播退出语音聊天房间
     */
    public void micQuitRoom(String roomId, IRCRTCResultCallback ircrtcResultCallback) {
        RCRTCEngine.getInstance().leaveRoom(ircrtcResultCallback);
    }

    /**
     * 观众端加入房间，从5.1.0开始，观众端加入房间也走RTC的接口，RTC SDK在内部实现从IM KV工具当中读取liveUrl
     * 不再直接从IM SDK中获取liveUrl，而是通过RTC SDK中的API获取
     */
    public void audienceJoinRoom(final String roomId, final IRCRTCResultCallback ircrtcResultCallback) {
        RCRTCRoomConfig roomConfig = RCRTCRoomConfig.Builder.create()
                //以观众的形式进入房间
                .setLiveRole(RCRTCLiveRole.AUDIENCE)
                //根据实际场景，选择音视频直播：LIVE_AUDIO_VIDEO 或音频直播：LIVE_AUDIO
                .setRoomType(RCRTCRoomType.LIVE_AUDIO)
                .build();
        RCRTCEngine.getInstance().joinRoom(roomId, roomConfig, new IRCRTCResultDataCallback<RCRTCRoom>() {
            @Override
            public void onSuccess(final RCRTCRoom rcrtcRoom) {
                rcrtcRoom.registerRoomListener(new RTCEventsListenerAdapter() {

                    /**
                     * 房间发布流之后的回调
                     * @param streams 流
                     */
                    @Override
                    public void onPublishLiveStreams(List<RCRTCInputStream> streams) {
                        subscribeLiveAVStream(rcrtcRoom, streams);
                        SLog.e(SLog.TAG_SEAL_MIC, "onPublishLiveStreams: 收到房间发出的合流并订阅");
                    }

                    /**
                     * 房间取消发布流之后的回调
                     * @param streams 流
                     */
                    @Override
                    public void onUnpublishLiveStreams(List<RCRTCInputStream> streams) {
                        unsubscribeLiveAVStream(rcrtcRoom, streams, ircrtcResultCallback);
                        SLog.e(SLog.TAG_SEAL_MIC, "onUnpublishLiveStreams: 收到房间发出的合流并取消订阅");
                    }

                });

                //当房间已经有人时，可以从回调的rcrtcRoom对象中拿到合流，这种情况下直接进行订阅
                List<RCRTCInputStream> rcrtcInputStreams = rcrtcRoom.getLiveStreams();
                if (rcrtcInputStreams != null && !rcrtcInputStreams.isEmpty()) {
                    subscribeLiveAVStream(rcrtcRoom, rcrtcInputStreams);
                }
                ircrtcResultCallback.onSuccess();
            }

            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {
                ircrtcResultCallback.onFailed(rtcErrorCode);
                SLog.e(SLog.TAG_SEAL_MIC, "观众加入RTC房间失败: " + rtcErrorCode.toString());
            }
        });
    }

    /**
     * 观众退出语音聊天房间
     */
    public void audienceQuitRoom(String roomId, IRCRTCResultCallback ircrtcResultCallback) {
        RCRTCEngine.getInstance().leaveRoom(ircrtcResultCallback);
    }

    /**
     * 仅直播模式可用， 作为观众，直接观看主播的直播，无需加入房间，通过传入主播的 url，仅观众端可用
     *
     * @param rcrtcRoom   当前RTC房间
     * @param liveStreams 直播URL的流 final SealMicResultCallback<RongRTCVideoView> callback
     */
    private void subscribeLiveAVStream(final RCRTCRoom rcrtcRoom, List<RCRTCInputStream> liveStreams) {
        if (rcrtcRoom == null || liveStreams.isEmpty()) {
            return;
        }
        rcrtcRoom.getLocalUser().subscribeStreams(liveStreams, new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {
                SLog.e(SLog.TAG_SEAL_MIC, "观众订阅合流成功");
            }

            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {
                SLog.e(SLog.TAG_SEAL_MIC, "观众订阅合流失败: " + rtcErrorCode.toString());
            }
        });
    }

    /**
     * 取消订阅
     *
     * @param rcrtcRoom            当前RTC房间
     * @param liveStreams          当前订阅的live的流
     * @param ircrtcResultCallback 离开直播间结果回调
     */
    private void unsubscribeLiveAVStream(final RCRTCRoom rcrtcRoom, List<RCRTCInputStream> liveStreams, final IRCRTCResultCallback ircrtcResultCallback) {
        if (rcrtcRoom == null || liveStreams.isEmpty()) {
            return;
        }
        rcrtcRoom.getLocalUser().unsubscribeStreams(liveStreams, new IRCRTCResultCallback() {
            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {
                ircrtcResultCallback.onFailed(rtcErrorCode);
                SLog.e(SLog.TAG_SEAL_MIC, "观众取消订阅合流失败: " + rtcErrorCode.toString());
            }

            @Override
            public void onSuccess() {
                ircrtcResultCallback.onSuccess();
            }
        });
    }

    /**
     * 设置麦克风是否可用
     *
     * @param enable
     */
    public void setLocalMicEnable(boolean enable) {
        RCRTCEngine.getInstance().getDefaultAudioStream().setMicrophoneDisable(!enable);
    }

    /**
     * 设置扬声器是否可用
     *
     * @param enable
     */
    public void setSpeakerEnable(boolean enable) {
        RCRTCEngine.getInstance().enableSpeaker(enable);
    }

    /**
     * 停止混音
     */
    public void stopMix() {
        RCRTCAudioMixer.getInstance().stop();
    }

    /**
     * 开始混音
     */
    public void startMix(int position) {
        String mp3Path = "";
        switch (position) {
            case 0:
                // 停止混音
                RCRTCAudioMixer.getInstance().stop();
                return;
            case 1:
                //机场
                mp3Path = "file:///android_asset/airport_gate1.mp3";
                break;
            case 2:
                //火车站
                mp3Path = "file:///android_asset/metro_entrance.mp3";
                break;
            case 3:
                //自然
                mp3Path = "file:///android_asset/rain_thunder1.mp3";
                break;
            default:
                break;
        }
        RCRTCAudioMixer.getInstance().startMix(mp3Path, RCRTCAudioMixer.Mode.MIX, true, -1);
        RCRTCAudioMixer.getInstance().setMixingVolume(100);
    }

    /**
     * 检查公放是否开启。若开启，返回true
     */
    public boolean isSpeakerphoneOn(Context context) {
        boolean isSpeakerphoneOn = false;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            isSpeakerphoneOn = am.isSpeakerphoneOn();
        }
        return isSpeakerphoneOn;
    }

    /**
     * 转为debug模式下显示的RTC列表
     */
    public List<StatusBean> parseToDebugInfoList(StatusReport statusReport) {
        List<StatusBean> statusBeanList = new ArrayList<>();
        for (Map.Entry<String, StatusBean> entry : statusReport.statusVideoRcvs.entrySet()) {
            statusBeanList.add(entry.getValue());
        }
        for (Map.Entry<String, StatusBean> entry : statusReport.statusVideoSends.entrySet()) {
            statusBeanList.add(entry.getValue());
        }

        for (Map.Entry<String, StatusBean> entry : statusReport.statusAudioSends.entrySet()) {
            statusBeanList.add(entry.getValue());
        }

        for (Map.Entry<String, StatusBean> entry : statusReport.statusAudioRcvs.entrySet()) {
            statusBeanList.add(entry.getValue());
        }
        return statusBeanList;
    }

}
