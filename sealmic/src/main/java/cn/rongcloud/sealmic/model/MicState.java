package cn.rongcloud.sealmic.model;

/**
 * 麦位状态
 * 麦位状态可复合存在，即存在空闲且被禁麦或麦位被锁且被禁等情况
 * 具体状态可以参照{ @link MicState#value} 的位上值
 * 当第一位为0时为{@link MicState#Idle} 麦位空闲,当为1时为{@link MicState#Locked} 麦位被锁
 * <p>
 * 例如：
 * 麦位状态值为3时，2进制数为11,代表当前是两种复合状态
 * 1.被禁{@link MicState#Forbidden}（二进制为10）
 * 2.麦位被锁{@link MicState#Locked}（二进制为1）
 */
public enum MicState {
    Idle(0x0),     //空闲
    Locked(0x1),   //麦位被锁
    Forbidden(0x2), //麦位被禁
    Hold(0x4);      //麦位有人且处于正常状态

    private int value;

    MicState(int value) {
        this.value = value;
    }

    public static MicState valueOf(int value) {
        for (MicState state : MicState.values()) {
            if (state.ordinal() == value) {
                return state;
            }
        }
        return MicState.Idle;
    }

    public static boolean isState(int micStateValue, MicState targetState) {
        return (micStateValue & targetState.value) == targetState.value;
    }

    public int getValue() {
        return this.value;
    }
}
