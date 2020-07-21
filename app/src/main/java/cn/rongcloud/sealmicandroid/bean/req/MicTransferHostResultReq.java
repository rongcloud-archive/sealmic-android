package cn.rongcloud.sealmicandroid.bean.req;

/**
 * 转让主持人同意/拒绝的请求body
 */
public class MicTransferHostResultReq {


    /**
     * roomId : xxxxx
     */

    private String roomId;

    public MicTransferHostResultReq(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
