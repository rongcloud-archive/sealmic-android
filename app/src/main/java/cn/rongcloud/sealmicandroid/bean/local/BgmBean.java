package cn.rongcloud.sealmicandroid.bean.local;

public class BgmBean {

    private String roomId;
    private String bgmContent;

    public BgmBean(String roomId, String bgmContent) {
        this.roomId = roomId;
        this.bgmContent = bgmContent;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getBgmContent() {
        return bgmContent;
    }

    public void setBgmContent(String bgmContent) {
        this.bgmContent = bgmContent;
    }
}
