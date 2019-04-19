package cn.rongcloud.sealmic.task.role;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import cn.rongcloud.sealmic.constant.ErrorCode;
import cn.rongcloud.sealmic.model.DetailRoomInfo;
import cn.rongcloud.sealmic.model.MicBehaviorType;
import cn.rongcloud.sealmic.model.MicState;
import cn.rongcloud.sealmic.model.RoomMicPositionInfo;
import cn.rongcloud.sealmic.task.ResultCallback;
import cn.rongcloud.sealmic.task.RoomManager;

public class Owner extends Role {
    public Owner() {
    }

    @Override
    public List<MicBehaviorType> getBehaviorList(int micPosition) {
        RoomMicPositionInfo micPositionInfo = getMicInfoByPosition(micPosition);
        List<MicBehaviorType> behaviorList = new ArrayList<>();
        String userId = micPositionInfo.getUserId();

        if (micPositionInfo != null) {
            // 根据麦位状态添加可操作的行为
            int micState = micPositionInfo.getState();

            // 判断麦位是否有人来进行抱麦和下麦操作
            if (TextUtils.isEmpty(userId) && !MicState.isState(micState, MicState.Locked)) {
                behaviorList.add(MicBehaviorType.PickupMic);    //抱麦
            } else if (!TextUtils.isEmpty(userId)) {
                behaviorList.add(MicBehaviorType.KickOffMic);   //踢麦
                behaviorList.add(MicBehaviorType.JumpDownMic);  //下麦
            }

            // 判断锁麦状态
            if (MicState.isState(micState, MicState.Locked)) {
                behaviorList.add(MicBehaviorType.UnlockMic);        //解锁麦
            } else {
                behaviorList.add(MicBehaviorType.LockMic);          //锁麦
            }

            // 判断麦位禁麦状态
            if (MicState.isState(micState, MicState.Forbidden)) {
                behaviorList.add(MicBehaviorType.UnForbidMic);      //解禁麦
            } else {
                behaviorList.add(MicBehaviorType.ForbidMic);        //禁麦
            }
        }

        return behaviorList;
    }

    @Override
    public void perform(MicBehaviorType micBehaviorType, int targetPosition, String targetUserId, ResultCallback<Boolean> callback) {
        RoomManager.getInstance().controlMicPosition(targetPosition, targetUserId, micBehaviorType.ordinal(), callback);
    }


    @Override
    public void leaveRoom(final ResultCallback<Boolean> callBack) {
        final RoomManager roomManager = RoomManager.getInstance();
        DetailRoomInfo currentRoomInfo = roomManager.getCurrentRoomInfo();
        if (currentRoomInfo == null || TextUtils.isEmpty(currentRoomInfo.getRoomId())) {
            if (callBack != null) {
                callBack.onFail(ErrorCode.ROOM_NOT_JOIN_TO_ROOM.getCode());
            }
            return;
        }

        final String roomId = currentRoomInfo.getRoomId();
        roomManager.leaveRoom(new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                roomManager.destroyRoom(roomId, callBack);
            }

            @Override
            public void onFail(int errorCode) {
                if (callBack != null) {
                    callBack.onFail(errorCode);
                }
            }
        });
    }

    @Override
    public boolean hasVoiceChatPermission() {
        return true;
    }

    @Override
    public boolean hasRoomSettingPermission() {
        return true;
    }

    @Override
    public boolean isSameRole(Role role) {
        return role instanceof Owner;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Owner) {
            return true;
        }
        return super.equals(obj);
    }
}
