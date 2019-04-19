package cn.rongcloud.sealmic.im.message;

import android.os.Parcel;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 房主发送的房间还在活动中的消息
 * 用于保证 IM 聊天室 不会因长时间没有人发消息而销毁
 */
@MessageTag(value = "SM:RMACTMsg", flag = MessageTag.NONE)
public class RoomIsActiveMessage extends MessageContent {
    public RoomIsActiveMessage(){
    }

    public RoomIsActiveMessage(byte[] data) {
    }

    public RoomIsActiveMessage(Parcel source) {
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

    public static final Creator<RoomIsActiveMessage> CREATOR = new Creator<RoomIsActiveMessage>() {
        @Override
        public RoomIsActiveMessage createFromParcel(Parcel source) {
            return new RoomIsActiveMessage(source);
        }

        @Override
        public RoomIsActiveMessage[] newArray(int size) {
            return new RoomIsActiveMessage[size];
        }
    };
}
