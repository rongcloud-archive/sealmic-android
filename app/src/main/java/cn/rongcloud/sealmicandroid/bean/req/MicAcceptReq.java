package cn.rongcloud.sealmicandroid.bean.req;

/**
 * 同意用户上麦(主持人)
 */
public class MicAcceptReq {


    /**
     * roomId : xxxxx
     * userId : xxxxx
     */

    private String roomId;
    private String userId;

    public MicAcceptReq(String roomId, String userId) {
        this.roomId = roomId;
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
