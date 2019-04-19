package cn.rongcloud.sealmic.task.role;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import cn.rongcloud.sealmic.model.MicBehaviorType;
import cn.rongcloud.sealmic.model.MicState;
import cn.rongcloud.sealmic.model.RoomMicPositionInfo;
import cn.rongcloud.sealmic.task.ResultCallback;
import cn.rongcloud.sealmic.task.RoomManager;

public class Listener extends Role {

    public Listener() {
    }

    @Override
    public List<MicBehaviorType> getBehaviorList(int micPosition) {
        RoomMicPositionInfo micPositionInfo = getMicInfoByPosition(micPosition);
        List<MicBehaviorType> behaviorList = new ArrayList<>();
        if (micPositionInfo != null) {
            String micUserId = micPositionInfo.getUserId();
            int micState = micPositionInfo.getState();

            // 麦位上没有人且非锁定状态时
            if(TextUtils.isEmpty(micUserId) && !MicState.isState(micState, MicState.Locked)){
                behaviorList.add(MicBehaviorType.JumpOnMic);//上麦
            }
        }
        return behaviorList;
    }

    @Override
    public void perform(MicBehaviorType micBehaviorType, int targetPosition, String targetUserId, ResultCallback<Boolean> callback) {
        RoomManager roomManager = RoomManager.getInstance();
        switch (micBehaviorType) {
            case JumpOnMic:
                roomManager.joinMic(targetPosition, callback);
                break;
        }
    }

    @Override
    public boolean hasVoiceChatPermission() {
        return false;
    }

    @Override
    public boolean hasRoomSettingPermission() {
        return false;
    }

    @Override
    public boolean isSameRole(Role role) {
        return role instanceof Listener;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Listener) {
            return true;
        }
        return super.equals(obj);
    }
}
