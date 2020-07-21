package cn.rongcloud.sealmicandroid.bean.req;

/**
 * 麦位状态请求body
 */
public class MicStateReq {


    /**
     * roomId : xxxxx
     * state : 0
     * position : 0
     */

    private String roomId;
    private int state;
    private int position;

    public MicStateReq(String roomId, int state, int position) {
        this.roomId = roomId;
        this.state = state;
        this.position = position;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
