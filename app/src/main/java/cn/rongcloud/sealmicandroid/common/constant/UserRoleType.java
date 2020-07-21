package cn.rongcloud.sealmicandroid.common.constant;

/**
 * 用户角色枚举
 */
public enum UserRoleType {

    /**
     * 主持人 (创建房间者默认为主持人，其余情况默认为观众)
     */
    HOST(10),
    /**
     * 参与者
     */
    CONNECT_MIC(11),
    /**
     * 观众
     */
    AUDIENCE(12);

    private int value;

    UserRoleType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isHost(int value) {
        return HOST.value == value;
    }

    public boolean isAudience(int value) {
        return AUDIENCE.value == value;
    }

    public boolean isConnectMic(int value) {
        return CONNECT_MIC.value == value;
    }

}
