package cn.rongcloud.sealmicandroid.bean.repo;

/**
 * 游客登录响应体
 */
public class VisitorLoginRepo extends NetResult<VisitorLoginRepo> {


    /**
     * userId : 2akJS6N5QOYsCKf5LhpgqY
     * userName : 秦时明月
     * portrait : http://xxx:xxx/portrait.png
     * imToken : xxxxxx
     * authorization : xxxxxxx
     * type : 0
     */

    private String userId;
    private String userName;
    private String portrait;
    private String imToken;
    private String authorization;
    private int type;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getImToken() {
        return imToken;
    }

    public void setImToken(String imToken) {
        this.imToken = imToken;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
