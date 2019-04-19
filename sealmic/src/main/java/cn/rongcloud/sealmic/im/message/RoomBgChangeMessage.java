package cn.rongcloud.sealmic.im.message;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 房间背景变动消息
 */
@MessageTag(value = "SM:RBgNtfyMsg", flag = MessageTag.NONE)
public class RoomBgChangeMessage extends MessageContent {
    private final static String TAG = RoomBgChangeMessage.class.getSimpleName();
    private int bgId;

    public RoomBgChangeMessage(byte[] data) {
        String jsonStr = null;
        try {
            jsonStr = new String(data, "UTF-8");
            JSONObject jsonObj = new JSONObject(jsonStr);
            setBgId(jsonObj.optInt("bgId"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public int getBgId() {
        return bgId;
    }

    public void setBgId(int bgId) {
        this.bgId = bgId;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.bgId);
    }

    protected RoomBgChangeMessage(Parcel in) {
        this.bgId = in.readInt();
    }

    public static final Creator<RoomBgChangeMessage> CREATOR = new Creator<RoomBgChangeMessage>() {
        @Override
        public RoomBgChangeMessage createFromParcel(Parcel source) {
            return new RoomBgChangeMessage(source);
        }

        @Override
        public RoomBgChangeMessage[] newArray(int size) {
            return new RoomBgChangeMessage[0];
        }
    };
}
