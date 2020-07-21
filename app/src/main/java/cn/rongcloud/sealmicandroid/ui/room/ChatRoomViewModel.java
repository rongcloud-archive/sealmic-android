package cn.rongcloud.sealmicandroid.ui.room;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.bean.repo.NetResult;
import cn.rongcloud.sealmicandroid.bean.repo.RoomDetailRepo;
import cn.rongcloud.sealmicandroid.bean.repo.RoomListRepo;
import cn.rongcloud.sealmicandroid.bean.repo.RoomMemberRepo;
import cn.rongcloud.sealmicandroid.common.MicState;
import cn.rongcloud.sealmicandroid.common.NetStateLiveData;
import cn.rongcloud.sealmicandroid.common.SealMicResultCallback;
import cn.rongcloud.sealmicandroid.common.constant.UserRoleType;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.manager.NavOptionsRouterManager;
import cn.rongcloud.sealmicandroid.manager.RoomManager;
import cn.rongcloud.sealmicandroid.manager.ThreadManager;
import cn.rongcloud.sealmicandroid.model.MicModel;
import cn.rongcloud.sealmicandroid.model.RoomModel;
import cn.rongcloud.sealmicandroid.model.UserModel;
import cn.rongcloud.sealmicandroid.util.ToastUtil;

/**
 * 聊天室VM层
 */
public class ChatRoomViewModel extends ViewModel {

    private RoomModel roomModel;
    private MicModel micModel;
    private UserModel userModel;
    private MutableLiveData<RoomListRepo> roomListRepoMutableLiveData = new MutableLiveData<>();
    private NetStateLiveData<RoomDetailRepo> roomDetailRepoNetStateLiveData;
    private Observer<RoomDetailRepo> roomDetailRepoObserver;
    private MutableLiveData<RoomDetailRepo> roomDetailRepoMutableLiveData;
    private Observer<Integer> roomSettingObserver;
    private NetStateLiveData<NetResult<Void>> roomSettingLiveData;
    private MutableLiveData<NetResult<List<RoomMemberRepo.MemberBean>>> userinfolistRepoLiveData;
    private Observer<NetResult<List<RoomMemberRepo.MemberBean>>> userinfolistRepoObserver;

    public ChatRoomViewModel(RoomModel roomModel, MicModel micModel, UserModel userModel) {
        this.roomModel = roomModel;
        this.micModel = micModel;
        this.userModel = userModel;
    }

    public MutableLiveData<RoomListRepo> getRoomListRepoMutableLiveData() {
        return roomListRepoMutableLiveData;
    }

    public void setRoomListRepo(RoomListRepo roomListRepo) {
        if (roomListRepoMutableLiveData == null) {
            roomListRepoMutableLiveData = new MutableLiveData<>();
        }
        roomListRepoMutableLiveData.postValue(roomListRepo);
    }

    public void setRoomDetailRepo(RoomDetailRepo roomDetailRepo) {
        if (roomDetailRepoMutableLiveData == null) {
            roomDetailRepoMutableLiveData = new MutableLiveData<>();
        }
        roomDetailRepoMutableLiveData.postValue(roomDetailRepo);
    }

    public MutableLiveData<RoomDetailRepo> getRoomDetailRepoMutableLiveData() {
        if (roomDetailRepoMutableLiveData == null) {
            roomDetailRepoMutableLiveData = new MutableLiveData<>();
        }
        return roomDetailRepoMutableLiveData;
    }

    public void roomSetting(final Context context, String id, boolean allowedAudienceJoinRoom, boolean allowedAudienceFreeJoinMic) {
        roomSettingLiveData = roomModel.roomSetting(id, allowedAudienceJoinRoom, allowedAudienceFreeJoinMic);
        roomSettingObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (roomSettingLiveData.isSuccess()) {
                    ToastUtil.showToast(context.getResources().getString(R.string.room_setting_success));
                } else {
                    ToastUtil.showToast(context.getResources().getString(R.string.room_setting_success));
                }
            }
        };
        roomSettingLiveData.getNetStateMutableLiveData().observeForever(roomSettingObserver);
    }

    public NetStateLiveData<RoomListRepo> roomList(String fromRoomId, int size) {
        return roomModel.roomList(fromRoomId, size);
    }

    public void roomDetail(String roomId) {
        roomDetailRepoNetStateLiveData = roomModel.roomDetail(roomId);
        roomDetailRepoObserver = new Observer<RoomDetailRepo>() {
            @Override
            public void onChanged(RoomDetailRepo roomDetailRepo) {
                setRoomDetailRepo(roomDetailRepo);
            }
        };
        roomDetailRepoNetStateLiveData.observeForever(roomDetailRepoObserver);
    }

    public void gotoChatRoomFragment(View view, String roomId, String roomName, String roomTheme) {
        CacheManager.getInstance().cacheUserRoleType(UserRoleType.AUDIENCE.getValue());
        NavOptionsRouterManager.getInstance().gotoChatRoomFragment(view, roomId, roomName, roomTheme, UserRoleType.AUDIENCE);
    }

    public void saveRoomDetail(RoomDetailRepo roomDetailRepo) {
//        if (!CacheManager.getInstance().getRoomDetailRepo().getRoomId().equals(roomDetailRepo.getRoomId())) {
        CacheManager.getInstance().cacheRoomDetail(roomDetailRepo);
//        }
    }

    /**
     * 上下麦操作，主要涉及到用户角色的转变
     *
     * @param roomId         房间id
     * @param targetRoleType 期望变成的用户角色
     */
    public void switchMic(final String roomId, final int currentRoleType, final int targetRoleType, SealMicResultCallback<Map<String, String>> callback) {
        //以前是连麦者或者是主持人，而现在变成观众，则 需要退出RTC房间，然后重新订阅liveUrl
        //想成为观众
        boolean wantBecameAudience = (targetRoleType == UserRoleType.AUDIENCE.getValue());
        //想成为观众且当前角色不为观众，则切换
        if (wantBecameAudience && (currentRoleType != UserRoleType.AUDIENCE.getValue())) {
            //主持人 和 连麦者 -> 观众  下麦操作
            RoomManager.getInstance().micGoDown(roomId, callback);
        }

        //以前是观众，现在变成了连麦者或者主持人
        //想成为主持人或者连麦者
        boolean wantBecameConnect = (targetRoleType == UserRoleType.CONNECT_MIC.getValue()
                || targetRoleType == UserRoleType.HOST.getValue());
        //想成为主持人或者连麦者且当前角色不为主持人和连麦者
        boolean currentConnect = (currentRoleType != UserRoleType.CONNECT_MIC.getValue() && currentRoleType != UserRoleType.HOST.getValue());
        if (wantBecameConnect && currentConnect) {
            // 观众 -> 连麦者 和 主持人  上麦操作
            RoomManager.getInstance().audienceGoMic(roomId, callback);
        }
    }

    /**
     * 主持人针对麦位的状态设置
     *
     * @param state    状态
     * @param position 麦位
     */
    public NetStateLiveData<NetResult<Void>> micState(int state, int position) {
        String roomId = CacheManager.getInstance().getRoomId();
        return micModel.micState(roomId, state, position);
    }

    /**
     * 主持人让用户下麦
     */
    public NetStateLiveData<NetResult<Void>> micKick(String userId) {
        String roomId = CacheManager.getInstance().getRoomId();
        return micModel.micKick(roomId, userId);
    }

    /**
     * 以主持人的身份转让主持人
     */
    public NetStateLiveData<NetResult<Void>> micTransferHost(String userId) {
        String roomId = CacheManager.getInstance().getRoomId();
        return micModel.micTransferHost(roomId, userId);
    }

    /**
     * 同意主持人方发起的转让请求
     */
    public NetStateLiveData<NetResult<Void>> micTransferHostAccept() {
        String roomId = CacheManager.getInstance().getRoomId();
        return micModel.micTransferHostAccept(roomId);
    }

    /**
     * 拒绝主持人方发起的转让请求
     */
    public NetStateLiveData<NetResult<Void>> micTransferHostReject() {
        String roomId = CacheManager.getInstance().getRoomId();
        return micModel.micTransferHostReject(roomId);
    }

    /**
     * 连麦者下麦
     */
    public NetStateLiveData<NetResult<Void>> micQuit() {
        String roomId = CacheManager.getInstance().getRoomId();
        return micModel.micQuit(roomId);
    }

    /**
     * 连麦者接管主持人
     */
    public NetStateLiveData<NetResult<Void>> micTakeOverHost() {
        String roomId = CacheManager.getInstance().getRoomId();
        return micModel.micTakeOverHost(roomId);
    }

    /**
     * 作为主持人方，拒绝来自连麦者的接管请求
     */
    public NetStateLiveData<NetResult<Void>> micTakeOverHostReject(String userId) {
        String roomId = CacheManager.getInstance().getRoomId();
        return micModel.micTakeOverHostReject(roomId, userId);
    }

    /**
     * 作为主持人方，同意来自连麦者的接管请求
     */
    public NetStateLiveData<NetResult<Void>> micTakeOverHostAccept(String userId) {
        String roomId = CacheManager.getInstance().getRoomId();
        return micModel.micTakeOverHostAccept(roomId, userId);
    }

    /**
     * 批量请求用户数据
     */
    public void userBatch(final List<String> userIds) {
        if (userIds != null && userIds.size() >= 1) {
            userinfolistRepoLiveData = userModel.userbatch(userIds);
            userinfolistRepoObserver = new Observer<NetResult<List<RoomMemberRepo.MemberBean>>>() {
                @Override
                public void onChanged(NetResult<List<RoomMemberRepo.MemberBean>> listNetResult) {

                }


            };
            userinfolistRepoLiveData.observeForever(userinfolistRepoObserver);
        }
    }

    /**
     * 轮询请求房间在线人数，5秒轮询一次
     */
    public void onlineNumber(final String roomId, final SealMicResultCallback<Boolean> sealMicResultCallback) {
        ThreadManager.getInstance().runTimeFixedDelay(new Runnable() {
            @Override
            public void run() {
                sealMicResultCallback.onSuccess(true);
            }
        }, 5);
    }

    /**
     * 设置麦克风是否可用
     */
    public void setLocalMicEnable(
            final boolean enable,
            int position,
            final SealMicResultCallback<Boolean> callback) {
        final NetStateLiveData<NetResult<Void>> result;
        if (enable) {
            //可用，请求接口，麦位正常
            result = micState(MicState.NORMAL.getState(), position);
            result.getNetStateMutableLiveData().observeForever(new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    if (result.isSuccess()) {
                        callback.onSuccess(true);
                    }
                }
            });
        } else {
            //不可用，请求接口，麦位关闭
            result = micState(MicState.CLOSE.getState(), position);
            result.getNetStateMutableLiveData().observeForever(new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    if (result.isSuccess()) {
                        callback.onSuccess(false);
                    }
                }
            });
        }

    }

    public MutableLiveData<NetResult<List<RoomMemberRepo.MemberBean>>> getUserinfolistRepoLiveData() {
        if (userinfolistRepoLiveData == null) {
            userinfolistRepoLiveData = new MutableLiveData<>();
        }
        return userinfolistRepoLiveData;
    }

    public NetStateLiveData<NetResult<Void>> messageBroad(String content) {
        return roomModel.messageBroad("", "RCMic:broadcastGift", content);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (roomDetailRepoNetStateLiveData != null) {
            roomDetailRepoNetStateLiveData.removeObserver(roomDetailRepoObserver);
            roomDetailRepoObserver = null;
        }
        if (roomSettingLiveData != null) {
            if (roomSettingLiveData.getNetStateMutableLiveData() != null) {
                roomSettingLiveData.getNetStateMutableLiveData().removeObserver(roomSettingObserver);
            }
        }
    }
}
