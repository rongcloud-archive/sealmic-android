package cn.rongcloud.sealmic.task;

import java.util.List;

import cn.rongcloud.sealmic.model.RoomMicPositionInfo;
import cn.rongcloud.sealmic.task.role.Role;
import io.rong.imlib.model.Message;

/**
 * 聊天房间事件监听
 */
public interface RoomEventListener {
    /**
     * 房间人数发生变化
     * @param memberCount
     */
    void onRoomMemberChange(int memberCount);

    /**
     * 麦位信息更新
     *
     * @param micPositionInfoList
     */
    void onMicUpdate(List<RoomMicPositionInfo> micPositionInfoList);

    /**
     * 当有房间内有人说话时，回调所有当前说话人的用户id
     *
     * @param speakUserIdList
     */
    void onRoomMicSpeak(List<String> speakUserIdList);

    /**
     * 当有新的聊天消息
     *
     * @param message
     */
    void onMessageEvent(Message message);

    /**
     * 发送消息失败
     *
     * @param message
     * @param errorCode
     */
    void onSendMessageError(Message message, int errorCode);

    /**
     * 房间背景更新
     *
     * @param bgId
     */
    void onRoomBgChanged(int bgId);

    /**
     * 当前角色发生改变
     *
     * @param role
     */
    void onRoleChanged(Role role);

    /**
     * 被提出房间
     */
    void onKickOffRoom();


    /**
     * 房间被销毁
     */
    void onRoomDestroy();

    /**
     * 长时间网络异常时退出房间
     */
    void onErrorLeaveRoom();

    /**
     * 当房间超出了最长存在时间
     */
    void onRoomExistOverTimeLimit();
}
