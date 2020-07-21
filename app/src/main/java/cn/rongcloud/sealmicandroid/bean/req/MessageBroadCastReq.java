package cn.rongcloud.sealmicandroid.bean.req;

public class MessageBroadCastReq {
    private String fromUserId;
    private String objectName;
    private String content;

    public MessageBroadCastReq(String fromUserId, String objectName, String content) {
        this.fromUserId = fromUserId;
        this.objectName = objectName;
        this.content = content;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
