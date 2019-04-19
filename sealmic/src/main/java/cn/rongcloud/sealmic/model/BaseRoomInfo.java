package cn.rongcloud.sealmic.model;

import cn.rongcloud.sealmic.utils.ResourceUtils;

/**
 * 获取房间列表请求中房间信息
 */
public class BaseRoomInfo {
    private long createDt;
    private String creatorUserId;
    private int memCount;
    private String roomId;
    private int roomType;
    private String subject;
    private int bgId;

    public long getCreateDt() {
        return createDt;
    }

    public void setCreateDt(long createDt) {
        this.createDt = createDt;
    }

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public int getMemCount() {
        return memCount;
    }

    public void setMemCount(int memCount) {
        this.memCount = memCount;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getBgId() {
        return bgId;
    }

    public void setBgId(int bgId) {
        this.bgId = bgId;
    }

    public int getRoomCoverImageId() {
        return ResourceUtils.getInstance().getRoomCoverImageId(roomId);
    }

}
