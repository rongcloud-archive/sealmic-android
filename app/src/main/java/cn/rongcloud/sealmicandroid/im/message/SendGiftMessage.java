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

/**
 * 送礼物的消息
 */

@MessageTag(value = "RCMic:gift", flag = MessageTag.ISCOUNTED)
public class SendGiftMessage extends MessageContent {

    private static final String TAG = SendGiftMessage.class.getSimpleName();
    private String content;
    private String tag;

    public static final Creator<SendGiftMessage> CREATOR = new Creator<SendGiftMessage>() {
        @Override
        public SendGiftMessage createFromParcel(Parcel source) {
            return new SendGiftMessage(source);
        }

        @Override
        public SendGiftMessage[] newArray(int size) {
            return new SendGiftMessage[size];
        }
    };

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getTag() {
        return tag;
    }

    public static SendGiftMessage obtain() {
        return new SendGiftMessage();
    }

    public SendGiftMessage() {
    }

    /**
     * 创建 CustomMessage(byte[] data) 带有 byte[] 的构造方法用于解析消息内容.
     */
    public SendGiftMessage(byte[] data) {
        if (data == null) {
            SLog.e(TAG, "发送普通礼物 data is null ");
            return;
        }

        String jsonStr = null;
        try {
            jsonStr = new String(data, "UTF-8");
            SLog.i("ad", jsonStr);
        } catch (UnsupportedEncodingException e) {
            SLog.e(TAG, "发送普通礼物 UnsupportedEncodingException ", e);
        }

        if (jsonStr == null) {
            SLog.e(TAG, "发送普通礼物 jsonStr is null ");
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
            if (jsonObj.has("content")) {
                setContent(jsonObj.getString("content"));
                SLog.e(TAG, jsonObj.getString("content"));
            }

            if (jsonObj.has("tag")) {
                setTag(jsonObj.getString("tag"));
                SLog.e(TAG, jsonObj.getString("tag"));
            }

        } catch (JSONException e) {
            SLog.e(TAG, "发送普通礼物 JSONException byte[] data " + e.getMessage());
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
            if (!TextUtils.isEmpty(content)) {
                jsonObj.put("content", content);
                SLog.e(TAG, "content " + content);
            }
            if (!TextUtils.isEmpty(tag)) {
                jsonObj.put("tag", tag);
                SLog.e(TAG, "content " + content);
            }

        } catch (JSONException e) {
            SLog.e(TAG, "发送普通礼物 JSONException encode " + e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            SLog.e(TAG, "发送普通礼物 UnsupportedEncodingException ", e);
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public SendGiftMessage(Parcel in) {
        setContent(in.readString());
        setTag(in.readString());
        setUserInfo(ParcelUtils.readFromParcel(in, UserInfo.class));
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getContent());
        dest.writeString(getTag());
        ParcelUtils.writeToParcel(dest, getUserInfo());
    }
}
