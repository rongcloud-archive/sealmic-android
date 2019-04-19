package cn.rongcloud.sealmic.task.role;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import cn.rongcloud.sealmic.model.MicBehaviorType;
import cn.rongcloud.sealmic.model.MicState;
import cn.rongcloud.sealmic.model.RoomMicPositionInfo;
import cn.rongcloud.sealmic.task.AuthManager;
import cn.rongcloud.sealmic.task.ResultCallback;
import cn.rongcloud.sealmic.task.RoomManager;

public class Linker extends Role {
    private RoomMicPositionInfo currentMicInfo;

    public Linker() {
    }

    @Override
    public List<MicBehaviorType> getBehaviorList(int micPosition) {
        RoomMicPositionInfo micPositionInfo = getMicInfoByPosition(micPosition);
        List<MicBehaviorType> behaviorList = new ArrayList<>();
        String currentUserId = AuthManager.getInstance().getCurrentUserId();
        if (micPositionInfo != null && currentUserId != null) {
            int micState = micPositionInfo.getState();
            String micUserId = micPositionInfo.getUserId();

            // 当麦位上没有用户且非锁麦下课进行跳麦
            if (TextUtils.isEmpty(micUserId) && !MicState.isState(micState, MicState.Locked)) {
                behaviorList.add(MicBehaviorType.JumpToMic);//跳麦
            } else if (currentMicInfo.getPosition() == micPosition) {
                behaviorList.add(MicBehaviorType.JumpDownMic);
            }
        }
        return behaviorList;
    }

    @Override
    public void perform(MicBehaviorType micBehaviorType, int targetPosition, String targetUserId, ResultCallback<Boolean> callback) {
        RoomManager roomManager = RoomManager.getInstance();
        switch (micBehaviorType) {
            case JumpToMic:
                if (currentMicInfo != null) {
                    int fromPosition = currentMicInfo.getPosition();
                    roomManager.changeMicPosition(fromPosition, targetPosition, callback);
                    break;
                }
            case JumpDownMic:
                roomManager.leaveMic(targetPosition, callback);
                break;
        }

    }

    @Override
    public boolean hasVoiceChatPermission() {
        if (currentMicInfo != null) {
            int state = currentMicInfo.getState();
            if (!MicState.isState(state, MicState.Forbidden)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasRoomSettingPermission() {
        return false;
    }

    @Override
    public boolean isSameRole(Role role) {
        if (role instanceof Linker) {
            Linker linker = (Linker) role;
            // 当同样为连麦者且可发言状态一致时认为是同一角色
            if (linker.hasVoiceChatPermission() == hasVoiceChatPermission()) {
                return true;
            }
        }
        return false;
    }

    public RoomMicPositionInfo getMicPositionInfo() {
        return currentMicInfo;
    }

    public void setMicPositionInfo(RoomMicPositionInfo micPositionInfo) {
        currentMicInfo = micPositionInfo;
    }

    @Override
    public boolean equals(@Nullable Object target) {
        if (target instanceof Linker) {
            Linker targetLinker = (Linker) target;
            if (currentMicInfo != null && targetLinker.getMicPositionInfo() != null) {
                RoomMicPositionInfo targetPosition = targetLinker.getMicPositionInfo();
                return currentMicInfo.getPosition() == targetPosition.getPosition()
                        && currentMicInfo.getUserId() != null && currentMicInfo.getUserId().equals(targetPosition.getUserId())
                        && currentMicInfo.getState() == targetPosition.getState();
            }
        }
        return super.equals(target);
    }
}
