package cn.rongcloud.sealmicandroid.bean.req;

/**
 * 主播下麦请求body
 */
public class MicQuitReq {


    /**
     * roomId : xxxxx
     */

    private String roomId;

    public MicQuitReq(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
