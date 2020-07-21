package cn.rongcloud.sealmicandroid.bean.req;

import java.util.List;

/**
 * 将用户踢出房间的请求体
 */
public class RoomKickUserReq {


    /**
     * roomId : xxxxxx
     * userIds : ["xxxxxxx","yyyyyyy"]
     */

    private String roomId;
    private List<String> userIds;

    public RoomKickUserReq(String roomId, List<String> userIds) {
        this.roomId = roomId;
        this.userIds = userIds;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}
