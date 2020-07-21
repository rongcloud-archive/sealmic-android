package cn.rongcloud.sealmicandroid.bean.repo;

/**
 * 房间详情的响应体
 */
public class RoomDetailRepo extends NetResult<RoomDetailRepo> {


    /**
     * roomId : 3saDkSLFMdnsseOksdakJS6
     * roomName : 名称2
     * themePictureUrl : xxxxxx
     * creatorId : xxxxxx
     * allowedAudienceFreeJoinMic : 1
     * createDt : 1555406087939
     */

    private String roomId;
    private String roomName;
    private String themePictureUrl;
    private String creatorId;
    private int allowedAudienceFreeJoinMic;
    private boolean allowedJoinRoom;
    private boolean allowedFreeJoinMic;
    private long createDt;

    public boolean isAllowedJoinRoom() {
        return allowedJoinRoom;
    }

    public void setAllowedJoinRoom(boolean allowedJoinRoom) {
        this.allowedJoinRoom = allowedJoinRoom;
    }

    public boolean isAllowedFreeJoinMic() {
        return allowedFreeJoinMic;
    }

    public void setAllowedFreeJoinMic(boolean allowedFreeJoinMic) {
        this.allowedFreeJoinMic = allowedFreeJoinMic;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getThemePictureUrl() {
        return themePictureUrl;
    }

    public void setThemePictureUrl(String themePictureUrl) {
        this.themePictureUrl = themePictureUrl;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public int getAllowedAudienceFreeJoinMic() {
        return allowedAudienceFreeJoinMic;
    }

    public void setAllowedAudienceFreeJoinMic(int allowedAudienceFreeJoinMic) {
        this.allowedAudienceFreeJoinMic = allowedAudienceFreeJoinMic;
    }

    public long getCreateDt() {
        return createDt;
    }

    public void setCreateDt(long createDt) {
        this.createDt = createDt;
    }
}
