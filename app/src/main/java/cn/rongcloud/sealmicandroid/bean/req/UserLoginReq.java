package cn.rongcloud.sealmicandroid.bean.req;

/**
 * 用户登录请求body
 */
public class UserLoginReq {


    /**
     * mobile : 13300000000
     * userId : xxxxx
     * verifyCode : 1234
     */

    private String mobile;
    private String verifyCode;
    /**
     * userName : 秦时明月
     * portrait : http://xxx:xxx/portrait.png
     * deviceId : xxxxxxxx
     */

    private String userName;
    private String portrait;
    private String deviceId;

    public UserLoginReq(String mobile, String verifyCode, String userName, String portrait, String deviceId) {
        this.mobile = mobile;
        this.verifyCode = verifyCode;
        this.userName = userName;
        this.portrait = portrait;
        this.deviceId = deviceId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
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
