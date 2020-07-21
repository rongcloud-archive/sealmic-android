package cn.rongcloud.sealmicandroid.common.constant;

/**
 * 房间中的成员(观众)状态
 */
public enum RoomMemberStatus {

    /**
     * 在线
     */
    ONLINE("在线"),
    /**
     * 排麦
     */
    ENQUEUE_MIC("排麦"),
    /**
     * 禁言
     */
    BAN("禁言");

    private String status;

    RoomMemberStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
