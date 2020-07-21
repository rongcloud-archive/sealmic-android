package cn.rongcloud.sealmicandroid.bean.kv;

import java.io.Serializable;

public class MicBean implements Serializable {
    private String userId;
    private int state;
    private int position;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "MicBean{" +
                "userId='" + userId + '\'' +
                ", state=" + state +
                ", position=" + position +
                '}';
    }
}
