package cn.rongcloud.sealmic.model;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmic.utils.log.SLog;
import io.rong.imlib.model.Message;

/**
 * 房间详情信息请求结果
 */
public class DetailRoomInfo extends BaseRoomInfo {
    private List<UserInfo> audiences = new ArrayList<>();
    private List<RoomMicPositionInfo> micPositions = new ArrayList<>();
    private List<Message> messageList = new ArrayList<>();

    public List<RoomMicPositionInfo> getMicPositions() {
        return micPositions;
    }

    public void setMicPositions(List<RoomMicPositionInfo> micPositions) {
        if (micPositions != null) {
            this.micPositions = micPositions;
        } else {
            clearMicPositions();
        }
    }

    public void clearMicPositions() {
        this.micPositions.clear();
    }

    public List<UserInfo> getAudiences() {
        return audiences;
    }

    public void setAudiences(List<UserInfo> audiences) {
        this.audiences = audiences;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }
}
