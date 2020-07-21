package cn.rongcloud.sealmicandroid.bean.req;

/**
 * 转让主持人拒绝(连麦者)请求body
 */
public class MicTransferHostReq {


    /**
     * roomId : xxxxx
     * userId : xxxxx
     */

    private String roomId;
    private String userId;

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

    public MicTransferHostReq(String roomId, String userId) {
        this.roomId = roomId;
        this.userId = userId;
    }
}
