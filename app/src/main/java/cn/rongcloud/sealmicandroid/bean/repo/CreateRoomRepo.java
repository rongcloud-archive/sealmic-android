package cn.rongcloud.sealmicandroid.bean.repo;

/**
 * 创建房间响应体
 */
public class CreateRoomRepo extends NetResult<CreateRoomRepo> {


    /**
     * roomId : ytZ0cAqxTT4mTgcDHhQ3ik
     * roomName : 123
     * themePictureUrl : pic
     * type : 1
     * createDt : 1555406087939
     */

    private String roomId;
    private String roomName;
    private String themePictureUrl;
    private int type;
    private long createDt;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCreateDt() {
        return createDt;
    }

    public void setCreateDt(long createDt) {
        this.createDt = createDt;
    }
}
