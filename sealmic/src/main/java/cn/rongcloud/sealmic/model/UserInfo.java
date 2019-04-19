package cn.rongcloud.sealmic.model;

import cn.rongcloud.sealmic.utils.ResourceUtils;

public class UserInfo {
    private String userId;
    private long joinDt;
    private String nickName;
    private int avatarResourceId;

    public long getJoinDt() {
        return joinDt;
    }

    public void setJoinDt(long joinDt) {
        this.joinDt = joinDt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        if (nickName == null) {
            nickName = ResourceUtils.getInstance().getUserName(userId);
        }

        return nickName;
    }

    public int getAvatarResourceId() {
        if (avatarResourceId == 0) {
            avatarResourceId = ResourceUtils.getInstance().getUserAvatarResourceId(userId);
        }
        return avatarResourceId;
    }
}
