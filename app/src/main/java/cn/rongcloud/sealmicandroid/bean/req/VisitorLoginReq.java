package cn.rongcloud.sealmicandroid.bean.req;

/**
 * 游客登录请求body
 */
public class VisitorLoginReq {


    /**
     * userName : 秦时明月
     * portrait : http://xxx:xxx/portrait.png
     * deviceId : xxxxxxxx
     */

    private String userName;
    private String portrait;
    private String deviceId;

    public VisitorLoginReq(String userName, String portrait, String deviceId) {
        this.userName = userName;
        this.portrait = portrait;
        this.deviceId = deviceId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
