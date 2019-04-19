package cn.rongcloud.sealmic.im.message;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmic.model.MicBehaviorType;
import cn.rongcloud.sealmic.model.RoomMicPositionInfo;
import cn.rongcloud.sealmic.utils.log.SLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 麦位更新消息
 */
@MessageTag(value = "SM:MPChangeMsg", flag = MessageTag.NONE)
public class MicPositionChangeMessage extends MessageContent {
    private static final String TAG = MicPositionChangeMessage.class.getSimpleName();
    private int cmd;
    private String targetUserId;
    private int fromPosition;
    private int toPosition;
    private List<RoomMicPositionInfo> micPositions;


    public MicPositionChangeMessage(byte[] data) {
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
            setFromPosition(jsonObj.optInt("fromPosition"));
            setToPosition(jsonObj.optInt("toPosition"));
            JSONArray positions = jsonObj.optJSONArray("micPositions");
            List<RoomMicPositionInfo> infoList = new ArrayList<>();
            if (positions != null && positions.length() > 0) {
                for (int i = 0; i < positions.length(); i++) {
                    infoList.add(RoomMicPositionInfo.parseJsonToMicPositionInfo(positions.getJSONObject(i)));
                }
            }
            setMicPositions(infoList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public MicBehaviorType getBehaviorType() {
        return MicBehaviorType.valueOf(cmd);
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

    public int getFromPosition() {
        return fromPosition;
    }

    public void setFromPosition(int fromPosition) {
        this.fromPosition = fromPosition;
    }

    public int getToPosition() {
        return toPosition;
    }

    public void setToPosition(int toPosition) {
        this.toPosition = toPosition;
    }

    public List<RoomMicPositionInfo> getMicPositions() {
        return micPositions;
    }

    public void setMicPositions(List<RoomMicPositionInfo> micPositions) {
        this.micPositions = micPositions;
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
        dest.writeInt(this.fromPosition);
        dest.writeInt(this.toPosition);
        dest.writeTypedList(this.micPositions);
    }

    public MicPositionChangeMessage() {
    }

    protected MicPositionChangeMessage(Parcel in) {
        this.cmd = in.readInt();
        this.targetUserId = in.readString();
        this.fromPosition = in.readInt();
        this.toPosition = in.readInt();
        this.micPositions = in.createTypedArrayList(RoomMicPositionInfo.CREATOR);
    }

    public static final Creator<MicPositionChangeMessage> CREATOR = new Creator<MicPositionChangeMessage>() {
        @Override
        public MicPositionChangeMessage createFromParcel(Parcel source) {
            return new MicPositionChangeMessage(source);
        }

        @Override
        public MicPositionChangeMessage[] newArray(int size) {
            return new MicPositionChangeMessage[size];
        }
    };
}
