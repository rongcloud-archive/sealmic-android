package cn.rongcloud.sealmic.task.role;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmic.model.DetailRoomInfo;
import cn.rongcloud.sealmic.model.MicBehaviorType;
import cn.rongcloud.sealmic.model.MicState;
import cn.rongcloud.sealmic.model.RoomMicPositionInfo;
import cn.rongcloud.sealmic.task.ResultCallback;
import cn.rongcloud.sealmic.task.RoomManager;

public abstract class Role {
    final String TAG = this.getClass().getSimpleName();
    ArrayList<MicBehaviorType> behaviorTypes;

    public abstract List<MicBehaviorType> getBehaviorList(int micPosition);

    RoomMicPositionInfo getMicInfoByPosition(int position) {
        DetailRoomInfo currentRoomInfo = RoomManager.getInstance().getCurrentRoomInfo();
        if (currentRoomInfo != null && currentRoomInfo.getMicPositions() != null) {
            List<RoomMicPositionInfo> micPositions = currentRoomInfo.getMicPositions();

            if (micPositions != null) {
                for (RoomMicPositionInfo info : micPositions) {
                    if (info.getPosition() == position) {
                        return info;
                    }
                }

                // 若当前麦位信息列表中没有该位置上的信息，则该位置为空
                RoomMicPositionInfo emptyInfo = new RoomMicPositionInfo();
                emptyInfo.setRoomId(currentRoomInfo.getRoomId());
                emptyInfo.setPosition(position);
                emptyInfo.setState(MicState.Idle.getValue());
                return emptyInfo;
            }
        }

        return null;
    }

    public abstract void perform(MicBehaviorType micBehaviorType, int targetPosition, String targetUserId, final ResultCallback<Boolean> callback);

    public void leaveRoom(ResultCallback<Boolean> callBack) {
        RoomManager.getInstance().leaveRoom(callBack);
    }

    public abstract boolean hasVoiceChatPermission();

    public abstract boolean hasRoomSettingPermission();

    public abstract boolean isSameRole(Role role);

}
