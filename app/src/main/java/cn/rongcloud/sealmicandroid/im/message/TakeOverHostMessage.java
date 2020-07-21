package cn.rongcloud.sealmicandroid.im.message;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cn.rongcloud.sealmicandroid.util.log.SLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 主持人接管通知
 * ObjectName: SM:TakeOverHostMsg
 * Content:
 * {
 * "cmd": 0,
 * "operatorId": "xxxx",
 * "operatorName": "xxx",
 * "targetUserId": "xxxx",
 * "targetUserName": "xxxxx"
 * }
 * cmd: 0: 接管 1: 拒绝 2: 同意
 */
@MessageTag(value = "RCMic:takeOverHostMsg", flag = MessageTag.NONE)
public class TakeOverHostMessage extends MessageContent {

    private static final String TAG = TakeOverHostMessage.class.getSimpleName();

    /**
     * cmd : 0
     * operatorId : xxxx
     * operatorName : xxx
     * targetUserId : xxxx
     * targetUserName : xxxxx
     */

    private int cmd;
    private String operatorId;
    private String operatorName;
    private String targetUserId;
    private String targetUserName;

    public static final Parcelable.Creator<TakeOverHostMessage> CREATOR = new Parcelable.Creator<TakeOverHostMessage>() {
        @Override
        public TakeOverHostMessage createFromParcel(Parcel source) {
            return new TakeOverHostMessage(source);
        }

        @Override
        public TakeOverHostMessage[] newArray(int size) {
            return new TakeOverHostMessage[size];
        }
    };

    public TakeOverHostMessage(byte[] data) {
        if (data == null) {
            SLog.e(TAG, "接管主持人 data is null ");
            return;
        }

        String jsonStr = null;
        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            SLog.e(TAG, "接管主持人 UnsupportedEncodingException ", e);
        }

        if (jsonStr == null) {
            SLog.e(TAG, "接管主持人 jsonStr is null ");
            return;
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            // 消息携带用户信息时, 自定义消息需添加下面代码
            if (jsonObj.has("user")) {
                setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
            }

            // 用于群组聊天, 消息携带 @ 人信息时, 自定义消息需添加下面代码
            if (jsonObj.has("mentionedInfo")) {
                setMentionedInfo(parseJsonToMentionInfo(jsonObj.getJSONObject("mentionedInfo")));
            }

            // ...
            // 自定义消息, 定义的字段
            // ...
            if (jsonObj.has("cmd")) {
                cmd = jsonObj.getInt("cmd");
            }

            if (jsonObj.has("operatorId")) {
                operatorId = jsonObj.getString("operatorId");
            }

            if (jsonObj.has("operatorName")) {
                operatorName = jsonObj.getString("operatorName");
            }

            if (jsonObj.has("targetUserId")) {
                targetUserId = jsonObj.getString("targetUserId");
            }

            if (jsonObj.has("targetUserName")) {
                targetUserName = jsonObj.getString("targetUserName");
            }

        } catch (JSONException e) {
            SLog.e(TAG, "接管主持人 JSONException " + e.getMessage());
        }
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {

            // 消息携带用户信息时, 自定义消息需添加下面代码
            if (getJSONUserInfo() != null) {
                jsonObj.putOpt("user", getJSONUserInfo());
            }

            // 用于群组聊天, 消息携带 @ 人信息时, 自定义消息需添加下面代码
            if (getJsonMentionInfo() != null) {
                jsonObj.putOpt("mentionedInfo", getJsonMentionInfo());
            }

            // ...
            // 自定义消息, 定义的字段.
            // ...

            jsonObj.put("cmd", cmd);
            if (!TextUtils.isEmpty(operatorId)) {
                jsonObj.put("operatorId", operatorId);
            }
            if (!TextUtils.isEmpty(operatorName)) {
                jsonObj.put("operatorName", operatorName);
            }
            if (!TextUtils.isEmpty(targetUserId)) {
                jsonObj.put("targetUserId", targetUserId);
            }
            if (!TextUtils.isEmpty(targetUserName)) {
                jsonObj.put("targetUserName", targetUserName);
            }

        } catch (JSONException e) {
            SLog.e(TAG, "接管主持人 JSONException " + e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            SLog.e(TAG, "接管主持人 UnsupportedEncodingException ", e);
        }
        return null;
    }

    public TakeOverHostMessage(Parcel in) {
        setCmd(in.readInt());
        setOperatorId(in.readString());
        setOperatorName(in.readString());
        setTargetUserId(in.readString());
        setTargetUserName(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getCmd());
        dest.writeString(getOperatorId());
        dest.writeString(getOperatorName());
        dest.writeString(getTargetUserId());
        dest.writeString(getTargetUserName());
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }
}
