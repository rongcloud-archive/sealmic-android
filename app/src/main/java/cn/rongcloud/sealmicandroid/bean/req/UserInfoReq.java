package cn.rongcloud.sealmicandroid.bean.req;

import java.util.List;

public class UserInfoReq {

    private List<String> userIds;

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public UserInfoReq(List<String> userIds) {
        this.userIds = userIds;
    }
}
