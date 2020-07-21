package cn.rongcloud.sealmicandroid.bean.req;

/**
 * 房间设置请求体
 */
public class RoomSettingReq {

    /**
     * roomId : xxxxxxx
     * allowedJoinRoom : 0
     * allowedFreeJoinMic : 0
     */

    private String roomId;
    private boolean allowedJoinRoom;
    private boolean allowedFreeJoinMic;

    public RoomSettingReq(String roomId, boolean allowedJoinRoom, boolean allowedFreeJoinMic) {
        this.roomId = roomId;
        this.allowedJoinRoom = allowedJoinRoom;
        this.allowedFreeJoinMic = allowedFreeJoinMic;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public boolean getAllowedJoinRoom() {
        return allowedJoinRoom;
    }

    public void setAllowedJoinRoom(boolean allowedJoinRoom) {
        this.allowedJoinRoom = allowedJoinRoom;
    }

    public boolean getAllowedFreeJoinMic() {
        return allowedFreeJoinMic;
    }

    public void setAllowedFreeJoinMic(boolean allowedFreeJoinMic) {
        this.allowedFreeJoinMic = allowedFreeJoinMic;
    }
}
