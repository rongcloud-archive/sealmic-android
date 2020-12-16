package cn.rongcloud.sealmicandroid.common.constant;

/**
 * 首页数据拉取方式
 */
public enum MainLoadData {

    /**
     * 被动触发，即执行逻辑时触发，进入页面时加载触发
     */
    LOAD_DATA(98),
    /**
     * 被动触发，即执行逻辑时触发，直接定位到我新创建的房间
     */
    LOCATE_CREATE(99),
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
