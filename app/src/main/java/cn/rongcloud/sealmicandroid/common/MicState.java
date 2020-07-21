package cn.rongcloud.sealmicandroid.common;

/**
 * 麦位状态
 */
public enum MicState {

    /**
     * 正常
     */
    NORMAL(0),
    /**
     * 麦位锁定
     */
    LOCK(1),
    /**
     * 闭麦
     */
    CLOSE(2);

    private int state;

    MicState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
