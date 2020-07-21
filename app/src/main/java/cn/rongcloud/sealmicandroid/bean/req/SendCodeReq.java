package cn.rongcloud.sealmicandroid.bean.req;

/**
 * 发送短信验证码请求body
 */
public class SendCodeReq {

    /**
     * mobile : 13333333333
     */

    private String mobile;

    public SendCodeReq(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
