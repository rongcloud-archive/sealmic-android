package cn.rongcloud.sealmicandroid.bean.req;

/**
 * 创建房间请求体
 */
public class CreateRoomReq {


    /**
     * name : test
     * themePictureUrl : xxxx
     */

    private String name;
    private String themePictureUrl;

    public CreateRoomReq(String name, String themePictureUrl) {
        this.name = name;
        this.themePictureUrl = themePictureUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThemePictureUrl() {
        return themePictureUrl;
    }

    public void setThemePictureUrl(String themePictureUrl) {
        this.themePictureUrl = themePictureUrl;
    }
}
