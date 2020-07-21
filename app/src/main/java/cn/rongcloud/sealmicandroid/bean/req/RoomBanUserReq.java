package cn.rongcloud.sealmicandroid.bean.req;

import java.util.List;

/**
 * 用户禁言设置(主持人)请求头
 */
public class RoomBanUserReq {


    /**
     * roomId : xxxxxxx
     * userIds : ["xxxxxxx","yyyyyyy"]
     * operation : add
     */

    private String roomId;
    private String operation;
    private List<String> userIds;

    public RoomBanUserReq(String roomId, String operation, List<String> userIds) {
        this.roomId = roomId;
        this.operation = operation;
        this.userIds = userIds;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}
