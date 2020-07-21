package cn.rongcloud.sealmicandroid.bean.req;

/**
 * 申请排麦请求body
 */
public class MicApplyReq {


    /**
     * roomId : xxxxx
     */

    private String roomId;

    public MicApplyReq(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
