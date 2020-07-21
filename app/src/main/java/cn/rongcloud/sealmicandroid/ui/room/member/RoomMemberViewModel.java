package cn.rongcloud.sealmicandroid.ui.room.member;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.List;

import cn.rongcloud.sealmicandroid.bean.repo.NetResult;
import cn.rongcloud.sealmicandroid.bean.repo.RoomMemberRepo;
import cn.rongcloud.sealmicandroid.common.NetStateLiveData;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.model.MicModel;
import cn.rongcloud.sealmicandroid.model.RoomModel;

/**
 * 房间成员管理VM
 */
public class RoomMemberViewModel extends ViewModel {

    private RoomModel roomModel;
    private MicModel micModel;

    private NetStateLiveData<NetResult<List<RoomMemberRepo.MemberBean>>> membersNetStateLiveData;
    private MutableLiveData<List<RoomMemberRepo.MemberBean>> memberBeanListLiveData;
    private Observer<NetResult<List<RoomMemberRepo.MemberBean>>> membersObserver;

    private NetStateLiveData<NetResult<List<RoomMemberRepo.MemberBean>>> micMemberNetStateLiveData;
    private MutableLiveData<List<RoomMemberRepo.MemberBean>> micMemberBeanListLiveData;
    private Observer<NetResult<List<RoomMemberRepo.MemberBean>>> micMembersObserver;

    private NetStateLiveData<NetResult<List<RoomMemberRepo.MemberBean>>> gagMemberNetStateLiveData;
    private MutableLiveData<List<RoomMemberRepo.MemberBean>> gagMemberBeanListLiveData;
    private Observer<NetResult<List<RoomMemberRepo.MemberBean>>> gagMembersObserver;

    public RoomMemberViewModel(RoomModel roomModel, MicModel micModel) {
        this.roomModel = roomModel;
        this.micModel = micModel;
    }

    public MutableLiveData<List<RoomMemberRepo.MemberBean>> getMemberBeanListLiveData() {
        if (memberBeanListLiveData == null) {
            memberBeanListLiveData = new MutableLiveData<>();
        }
        return memberBeanListLiveData;
    }

    public MutableLiveData<List<RoomMemberRepo.MemberBean>> getMicMemberBeanListLiveData() {
        if (micMemberBeanListLiveData == null) {
            micMemberBeanListLiveData = new MutableLiveData<>();
        }
        return micMemberBeanListLiveData;
    }

    public MutableLiveData<List<RoomMemberRepo.MemberBean>> getGagMemberBeanListLiveData() {
        if (gagMemberBeanListLiveData == null) {
            gagMemberBeanListLiveData = new MutableLiveData<>();
        }
        return gagMemberBeanListLiveData;
    }

    /**
     * 响应体需要修改成 userinfo 类
     */
    public void roomMembers() {
        String roomId = CacheManager.getInstance().getRoomId();
        membersNetStateLiveData = roomModel.roomMembers(roomId);
        membersObserver = new Observer<NetResult<List<RoomMemberRepo.MemberBean>>>() {
            @Override
            public void onChanged(NetResult<List<RoomMemberRepo.MemberBean>> listNetResult) {
                List<RoomMemberRepo.MemberBean> memberBeans = listNetResult.getData();
                memberBeanListLiveData.postValue(memberBeans);
            }
        };
        membersNetStateLiveData.observeForever(membersObserver);
    }

    public void micMembers() {
        String roomId = CacheManager.getInstance().getRoomId();
        micMemberNetStateLiveData = roomModel.micMembers(roomId);
        micMembersObserver = new Observer<NetResult<List<RoomMemberRepo.MemberBean>>>() {
            @Override
            public void onChanged(NetResult<List<RoomMemberRepo.MemberBean>> listNetResult) {
                List<RoomMemberRepo.MemberBean> micMemberBeans = listNetResult.getData();
                micMemberBeanListLiveData.postValue(micMemberBeans);
            }
        };
        micMemberNetStateLiveData.observeForever(micMembersObserver);
    }

    public void gagMembers() {
        String roomId = CacheManager.getInstance().getRoomId();
        gagMemberNetStateLiveData = roomModel.gagMembers(roomId);
        gagMembersObserver = new Observer<NetResult<List<RoomMemberRepo.MemberBean>>>() {
            @Override
            public void onChanged(NetResult<List<RoomMemberRepo.MemberBean>> listNetResult) {
                List<RoomMemberRepo.MemberBean> gagMemberBeans = listNetResult.getData();
                gagMemberBeanListLiveData.postValue(gagMemberBeans);
            }
        };
        gagMemberNetStateLiveData.observeForever(gagMembersObserver);
    }

    /**
     * 用户禁言和解除禁言
     */
    public NetStateLiveData<NetResult<Void>> banMember(String operation, List<String> userIds) {
        String roomId = CacheManager.getInstance().getRoomId();
        return roomModel.banMember(roomId, operation, userIds);
    }

    /**
     * 主持人将人踢出房间
     */
    public NetStateLiveData<NetResult<Void>> kickMember(List<String> userIds) {
        String roomId = CacheManager.getInstance().getRoomId();
        return roomModel.kickMember(roomId, userIds);
    }

    /**
     * 同意用户上麦
     */
    public NetStateLiveData<NetResult<Void>> micAccept(String userId) {
        String roomId = CacheManager.getInstance().getRoomId();
        return micModel.micAccept(roomId, userId);
    }

    /**
     * 拒绝用户上麦
     */
    public NetStateLiveData<NetResult<Void>> micReject(String userId) {
        String roomId = CacheManager.getInstance().getRoomId();
        return micModel.micReject(roomId, userId);
    }

    /**
     * 邀请用户连麦
     */
    public NetStateLiveData<NetResult<Void>> micInvite(String userId) {
        String roomId = CacheManager.getInstance().getRoomId();
        return micModel.micInvite(roomId, userId);
    }

    /**
     * 申请排麦
     */
    public NetStateLiveData<NetResult<Void>> micApply() {
        String roomId = CacheManager.getInstance().getRoomId();
        return micModel.micApply(roomId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (membersNetStateLiveData != null) {
            membersNetStateLiveData.removeObserver(membersObserver);
        }
        if (micMemberNetStateLiveData != null) {
            micMemberNetStateLiveData.removeObserver(micMembersObserver);
        }
        if (gagMemberNetStateLiveData != null) {
            gagMemberNetStateLiveData.removeObserver(gagMembersObserver);
        }
    }
}
