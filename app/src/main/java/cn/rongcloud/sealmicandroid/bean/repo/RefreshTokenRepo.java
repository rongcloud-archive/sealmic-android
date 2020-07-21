package cn.rongcloud.sealmicandroid.bean.repo;

/**
 * 刷新token接口响应体
 */
public class RefreshTokenRepo extends NetResult<RefreshTokenRepo> {


    /**
     * imToken : xxxxxx
     */

    private String imToken;

    public String getImToken() {
        return imToken;
    }

    public void setImToken(String imToken) {
        this.imToken = imToken;
    }
}
