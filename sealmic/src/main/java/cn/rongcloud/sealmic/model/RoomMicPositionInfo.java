package cn.rongcloud.sealmic.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class RoomMicPositionInfo implements Parcelable {
    @SerializedName(value = "userId", alternate = {"uid"})
    private String userId;  // 当前麦位上的人员 id
    @SerializedName("rid")
    private String roomId;
    private int state;
    private int position;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.roomId);
        dest.writeInt(this.state);
        dest.writeInt(this.position);
    }

    public RoomMicPositionInfo() {
    }

    public static RoomMicPositionInfo parseJsonToMicPositionInfo(JSONObject jsonObject) {
        RoomMicPositionInfo micPositionInfo = new RoomMicPositionInfo();
        micPositionInfo.setUserId(jsonObject.optString("uid"));
        micPositionInfo.setRoomId(jsonObject.optString("rid"));
        micPositionInfo.setState(jsonObject.optInt("state"));
        micPositionInfo.setPosition(jsonObject.optInt("position"));
        return micPositionInfo;
    }

    protected RoomMicPositionInfo(Parcel in) {
        this.userId = in.readString();
        this.roomId = in.readString();
        this.state = in.readInt();
        this.position = in.readInt();
    }

    public static final Creator<RoomMicPositionInfo> CREATOR = new Creator<RoomMicPositionInfo>() {
        @Override
        public RoomMicPositionInfo createFromParcel(Parcel source) {
            return new RoomMicPositionInfo(source);
        }

        @Override
        public RoomMicPositionInfo[] newArray(int size) {
            return new RoomMicPositionInfo[size];
        }
    };
}
