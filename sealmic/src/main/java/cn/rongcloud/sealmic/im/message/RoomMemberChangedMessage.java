package cn.rongcloud.sealmic.im.message;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cn.rongcloud.sealmic.utils.log.SLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 房间成员变动消息
 */
@MessageTag(value = "SM:RMChangeMsg", flag = MessageTag.NONE)
public class RoomMemberChangedMessage extends MessageContent {
    private final String TAG = RoomMemberChangedMessage.class.getSimpleName();
    private int cmd; //1 join, 2 leave, 3 kick,
    private String targetUserId;
    private int targetPosition = -1; //-1 无效，>=0 有效的麦位

    public RoomMemberChangedMessage(byte[] data) {
        String jsonStr = null;
        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            SLog.e(TAG, "UnsupportedEncodingException ", e);
        }
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            setCmd(jsonObj.optInt("cmd"));
            setTargetUserId(jsonObj.optString("targetUserId"));
            setTargetPosition(jsonObj.optInt("targetPosition"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public RoomMemberAction getRoomMemberAction() {
        return RoomMemberAction.valueOf(cmd);
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public int getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(int targetPosition) {
        this.targetPosition = targetPosition;
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
        dest.writeInt(this.cmd);
        dest.writeString(this.targetUserId);
        dest.writeInt(this.targetPosition);
    }

    public RoomMemberChangedMessage() {
    }

    protected RoomMemberChangedMessage(Parcel in) {
        this.cmd = in.readInt();
        this.targetUserId = in.readString();
        this.targetPosition = in.readInt();
    }

    public static final Creator<RoomMemberChangedMessage> CREATOR = new Creator<RoomMemberChangedMessage>() {
        @Override
        public RoomMemberChangedMessage createFromParcel(Parcel source) {
            return new RoomMemberChangedMessage(source);
        }

        @Override
        public RoomMemberChangedMessage[] newArray(int size) {
            return new RoomMemberChangedMessage[size];
        }
    };

    public enum RoomMemberAction {
        UNKNOWN,
        JOIN,
        LEAVE,
        KICK;

        public static RoomMemberAction valueOf(int value) {
            for (RoomMemberAction action : RoomMemberAction.values()) {
                if (action.ordinal() == value) {
                    return action;
                }
            }
            return UNKNOWN;
        }
    }
}
