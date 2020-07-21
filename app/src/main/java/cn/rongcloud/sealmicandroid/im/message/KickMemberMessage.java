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
 * 用户被踢出房间通知
 * <p>
 * ObjectName: SM:chrmSysMsg
 * Content:
 * {
 * "type": 0
 * "operatorId":"xxxxx",
 * "operatorName":"xxxxx",
 * "roomId":"xxxxx"
 * }
 * <p>
 * 解释说明:
 * <p>
 * type: 0 用户被踢出房间
 */
@MessageTag(value = "RCMic:chrmSysMsg", flag = MessageTag.NONE)
public class KickMemberMessage extends MessageContent {

    private static final String TAG = KickMemberMessage.class.getSimpleName();
    private int type;
    private String operatorId;
    private String operatorName;
    private String roomId;

    public static final Parcelable.Creator<KickMemberMessage> CREATOR = new Parcelable.Creator<KickMemberMessage>() {
        @Override
        public KickMemberMessage createFromParcel(Parcel source) {
            return new KickMemberMessage(source);
        }

        @Override
        public KickMemberMessage[] newArray(int size) {
            return new KickMemberMessage[size];
        }
    };

    public KickMemberMessage(byte[] data) {
        if (data == null) {
            SLog.e(TAG, "踢人 data is null ");
            return;
        }

        String jsonStr = null;
        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            SLog.e(TAG, "踢人 UnsupportedEncodingException ", e);
        }

        if (jsonStr == null) {
            SLog.e(TAG, "踢人 jsonStr is null ");
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
            if (jsonObj.has("type")) {
                type = jsonObj.getInt("type");
                SLog.e(TAG, String.valueOf(jsonObj.getInt("type")));
            }

            if (jsonObj.has("operatorId")) {
                operatorId = jsonObj.getString("operatorId");
                SLog.e(TAG, jsonObj.getString("operatorId"));
            }

            if (jsonObj.has("operatorName")) {
                operatorName = jsonObj.getString("operatorName");
                SLog.e(TAG, jsonObj.getString("operatorName"));
            }

            if (jsonObj.has("roomId")) {
                roomId = jsonObj.getString("roomId");
                SLog.e(TAG, jsonObj.getString("roomId"));
            }

        } catch (JSONException e) {
            SLog.e(TAG, "踢人 JSONException byte[] data " + e.getMessage());
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

            // 自定义消息, 定义的字段
            // ...
            //礼物消息 objectname："RCMic:gift"
            //消息体里就一个字段：“content”，是一个字符串，放的就是聊天窗口要提示的文本，具体文本的不同就根据送的不同礼物类型拼接就行
            //消息体里还得再加个字段：“tag”，也是个字符串，用来标记发的是哪个礼物
            //笑脸:gift_smell
            //冰淇凌:gift_ice
            //机票:gift_airTicket
            //爱心车:gift_lovingCar
            //蜂蜜:gift_honey
            //存钱罐:gift_savingPot
            //宝箱:gift_treasureBox
            //豪华跑车:gift_sportsCar
            jsonObj.put("type", type);
            if (!TextUtils.isEmpty(operatorId)) {
                jsonObj.put("operatorId", operatorId);
                SLog.e(TAG, "operatorId " + operatorId);
            }
            if (!TextUtils.isEmpty(operatorName)) {
                jsonObj.put("operatorName", operatorName);
                SLog.e(TAG, "operatorName " + operatorName);
            }
            if (!TextUtils.isEmpty(roomId)) {
                jsonObj.put("roomId", roomId);
                SLog.e(TAG, "roomId " + roomId);
            }

        } catch (JSONException e) {
            SLog.e(TAG, "踢人 JSONException encode " + e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            SLog.e(TAG, "踢人 UnsupportedEncodingException ", e);
        }
        return null;
    }

    public int getType() {
        return type;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getType());
        dest.writeString(getOperatorId());
        dest.writeString(getOperatorName());
        dest.writeString(getRoomId());
    }

    public KickMemberMessage(Parcel in) {
        setType(in.readInt());
        setOperatorId(in.readString());
        setOperatorName(in.readString());
        setRoomId(in.readString());
    }
}
