package cn.rongcloud.sealmic.im.message;

import android.os.Parcel;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 房间一定时间自动销毁消息
 */
@MessageTag(value = "SM:RDNtfyMsg", flag = MessageTag.NONE)
public class RoomDestroyNotifyMessage extends MessageContent {
    public RoomDestroyNotifyMessage(byte[] data) {
    }

    public RoomDestroyNotifyMessage(Parcel source) {
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

    }

    public static final Creator<RoomDestroyNotifyMessage> CREATOR = new Creator<RoomDestroyNotifyMessage>() {
        @Override
        public RoomDestroyNotifyMessage createFromParcel(Parcel source) {
            return new RoomDestroyNotifyMessage(source);
        }

        @Override
        public RoomDestroyNotifyMessage[] newArray(int size) {
            return new RoomDestroyNotifyMessage[size];
        }
    };
}
