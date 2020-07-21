package cn.rongcloud.sealmicandroid.common.constant;

/**
 * 首页数据拉取方式
 */
public enum MainLoadData {

    /**
     * 下拉刷新
     */
    PULL_REFRESH(100),
    /**
     * 上拉加载
     */
    LOAD_MORE(101);

    private int value;

    MainLoadData(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
