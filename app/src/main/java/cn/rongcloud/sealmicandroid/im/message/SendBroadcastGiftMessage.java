package cn.rongcloud.sealmicandroid.im.message;


import android.os.Parcel;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cn.rongcloud.sealmicandroid.util.log.SLog;
import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

@MessageTag(value = "RCMic:broadcastGift", flag = MessageTag.NONE)
public class SendBroadcastGiftMessage extends MessageContent {

    private static final String TAG = SendBroadcastGiftMessage.class.getSimpleName();
    private String roomName;
    private String tag;

    public static final Creator<SendBroadcastGiftMessage> CREATOR = new Creator<SendBroadcastGiftMessage>() {
        @Override
        public SendBroadcastGiftMessage createFromParcel(Parcel source) {
            return new SendBroadcastGiftMessage(source);
        }

        @Override
        public SendBroadcastGiftMessage[] newArray(int size) {
            return new SendBroadcastGiftMessage[size];
        }
    };

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public static SendBroadcastGiftMessage obtain() {
        return new SendBroadcastGiftMessage();
    }

    public SendBroadcastGiftMessage() {
    }

    /**
     * 创建 CustomMessage(byte[] data) 带有 byte[] 的构造方法用于解析消息内容.
     */
    public SendBroadcastGiftMessage(byte[] data) {
        if (data == null) {
            SLog.e(TAG, "data is null ");
            return;
        }

        String jsonStr = null;
        try {
            jsonStr = new String(data, "UTF-8");
            SLog.e(SLog.TAG_SEAL_MIC, "收到的豪车礼物广播消息json串: "+ jsonStr);
        } catch (UnsupportedEncodingException e) {
            SLog.e(TAG, "UnsupportedEncodingException ", e);
        }

        if (jsonStr == null) {
            SLog.e(TAG, "jsonStr is null ");
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
            if (jsonObj.has("roomName")) {
                setRoomName(jsonObj.getString("roomName"));
                SLog.e(TAG, jsonObj.getString("roomName"));
            }

            if (jsonObj.has("tag")) {
                setTag(jsonObj.getString("tag"));
                SLog.e(TAG, jsonObj.getString("tag"));
            }

        } catch (JSONException e) {
            SLog.e(TAG, "发送超级礼物 JSONException byte[] data " + e.getMessage());
        }
    }

    /**
     * 将本地消息对象序列化为消息数据。
     *
     * @return 消息数据。
     */
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
            //豪华跑车:gift_sportsCar
            if (!TextUtils.isEmpty(roomName)) {
                jsonObj.put("roomName", roomName);

            }
            if (!TextUtils.isEmpty(tag)) {
                jsonObj.put("tag", tag);
            }

        } catch (JSONException e) {
            SLog.e(TAG, "发送超级礼物 JSONException encode " + e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            SLog.e(TAG, "发送超级礼物 UnsupportedEncodingException ", e);
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public SendBroadcastGiftMessage(Parcel in) {
        setRoomName(in.readString());
        setTag(in.readString());
        setUserInfo(ParcelUtils.readFromParcel(in, UserInfo.class));
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getRoomName());
        dest.writeString(getTag());
        ParcelUtils.writeToParcel(dest, getUserInfo());
    }
}