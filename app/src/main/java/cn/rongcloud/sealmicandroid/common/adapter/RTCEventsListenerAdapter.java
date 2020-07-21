package cn.rongcloud.sealmicandroid.common.adapter;

import java.util.List;

import cn.rongcloud.rtc.api.RCRTCRemoteUser;
import cn.rongcloud.rtc.api.callback.IRCRTCRoomEventsListener;
import cn.rongcloud.rtc.api.stream.RCRTCInputStream;
import io.rong.imlib.model.Message;

/**
 * RTC加入房间对应的回调适配器
 */
public class RTCEventsListenerAdapter extends IRCRTCRoomEventsListener {

    @Override
    public void onRemoteUserPublishResource(RCRTCRemoteUser rcrtcRemoteUser, List<RCRTCInputStream> list) {
        //比我后加房间的人发布的资源
    }

    @Override
    public void onRemoteUserMuteAudio(RCRTCRemoteUser rcrtcRemoteUser, RCRTCInputStream rcrtcInputStream, boolean b) {

    }

    @Override
    public void onRemoteUserMuteVideo(RCRTCRemoteUser rcrtcRemoteUser, RCRTCInputStream rcrtcInputStream, boolean b) {

    }

    @Override
    public void onRemoteUserUnpublishResource(RCRTCRemoteUser rcrtcRemoteUser, List<RCRTCInputStream> list) {

    }

    @Override
    public void onUserJoined(RCRTCRemoteUser rcrtcRemoteUser) {

    }

    @Override
    public void onUserLeft(RCRTCRemoteUser rcrtcRemoteUser) {

    }

    @Override
    public void onUserOffline(RCRTCRemoteUser rcrtcRemoteUser) {

    }

    @Override
    public void onVideoTrackAdd(String s, String s1) {

    }

    @Override
    public void onLeaveRoom(int i) {

    }

    @Override
    public void onReceiveMessage(Message message) {

    }

    @Override
    public void onKickedByServer() {

    }
}
