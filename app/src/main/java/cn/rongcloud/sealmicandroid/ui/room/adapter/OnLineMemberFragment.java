package cn.rongcloud.sealmicandroid.ui.room.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.bean.repo.NetResult;
import cn.rongcloud.sealmicandroid.bean.repo.RoomMemberRepo;
import cn.rongcloud.sealmicandroid.common.Event;
import cn.rongcloud.sealmicandroid.common.NetStateLiveData;
import cn.rongcloud.sealmicandroid.common.factory.CommonViewModelFactory;
import cn.rongcloud.sealmicandroid.databinding.FragmentOnlineMemberBinding;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.ui.room.member.RoomMemberViewModel;
import cn.rongcloud.sealmicandroid.util.ToastUtil;
import cn.rongcloud.sealmicandroid.util.log.SLog;

/**
 * 在线列表
 */
public class OnLineMemberFragment extends Fragment {

    private RoomMemberViewModel roomMemberViewModel;
    private FragmentOnlineMemberBinding fragmentOnlineMemberBinding;
    private ArrayList<RoomMemberRepo.MemberBean> memberBeanList;
    private OnlineRoomMemberAdapter onlineRoomMemberAdapter;
    private boolean dataRequestIsSuccess = false;
    private boolean bankDataRequestIsSuccess = false;
    private ArrayList<RoomMemberRepo.MemberBean> bankBeanArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        roomMemberViewModel = new ViewModelProvider(this, new CommonViewModelFactory()).get(RoomMemberViewModel.class);
        fragmentOnlineMemberBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_online_member, container, false);
        fragmentOnlineMemberBinding.setLifecycleOwner(this);
        fragmentOnlineMemberBinding.setRoomMemberViewModel(roomMemberViewModel);
        //注册EventBus
        EventBus.getDefault().register(this);
        return fragmentOnlineMemberBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 接收禁言列表发送的事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getUnBankUserInfo(Event.EventUserLineStatusChange.UnBankBean unBankBean) {
        //判断是否已经存在
        boolean alreadyAtList = onlineRoomMemberAdapter.isAlreadyAtList(unBankBean.getMemberBean());
        if (alreadyAtList) {
            return;
        }
        //通知本地列表添加被解禁的用户
        onlineRoomMemberAdapter.addRoomMember(unBankBean.getMemberBean());
        //把被解禁的用户添加至本地集合
        memberBeanList.add(unBankBean.getMemberBean());
    }

    /**
     * 接收禁言列表发送的被禁言用户数据集合
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBankUserList(Event.EventUserLineStatusChange.MicBankUserStatus micBankUserStatus) {
        bankDataRequestIsSuccess = true;
        bankBeanArrayList.clear();
        bankBeanArrayList.addAll(micBankUserStatus.getMemberBeanList());
        //过滤
        filterBankUserFromOnLine();
    }

    /**
     * 接收聊天室发送过来已经过滤掉在麦位上的用户的数据集合
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getFilterAfter(Event.EventUserLineStatusChange.MicUserFilterBankAndMic micUserFilterBankAndMic) {
        //重新设置一遍过滤后的数据
        onlineRoomMemberAdapter.setRoomMemberList(micUserFilterBankAndMic.getMemberBeanList());
    }

    /**
     * 从在线列表中过滤已经被禁言的用户
     */
    private void filterBankUserFromOnLine() {
//        SLog.i("asdf", dataRequestIsSuccess + "--" + bankDataRequestIsSuccess);
        //判断数据是否请求成功
        if (bankDataRequestIsSuccess && dataRequestIsSuccess) {
            //判断禁言列表是否有数据
            if (bankBeanArrayList.size() > 0) {
                //在线列表是否有数据
                if (memberBeanList.size() > 0) {
                    Iterator<RoomMemberRepo.MemberBean> memberBeanIterator = memberBeanList.iterator();
                    Iterator<RoomMemberRepo.MemberBean> bankIterator = bankBeanArrayList.iterator();
                    while (memberBeanIterator.hasNext()) {
                        RoomMemberRepo.MemberBean memberBean = memberBeanIterator.next();
                        while (bankIterator.hasNext()) {
                            RoomMemberRepo.MemberBean bankBean = bankIterator.next();
                            //每取出一个在线列表的人去禁言列表里查询，如果有则代表被禁言，则从在线列表里删除
                            if (memberBean.getUserId().equals(bankBean.getUserId())) {
                                memberBeanIterator.remove();
                            }
                        }
                    }
                }
            }
            //重新设置一遍过滤后的数据
            onlineRoomMemberAdapter.setRoomMemberList(memberBeanList);
//            SLog.i("asdf", memberBeanList.size() + "");
            //发送过滤后的数据到ChatRoomFragment
            EventBus.getDefault().post(new Event.EventUserLineStatusChange.MicUserStatus(memberBeanList));
        }
    }

    private void initData() {

        memberBeanList = new ArrayList<>();
        onlineRoomMemberAdapter = new OnlineRoomMemberAdapter();
        fragmentOnlineMemberBinding.rvRoomMember.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false));
        fragmentOnlineMemberBinding.rvRoomMember.setAdapter(onlineRoomMemberAdapter);
        roomMemberViewModel.roomMembers();
        roomMemberViewModel.getMemberBeanListLiveData().observe(getViewLifecycleOwner(), new Observer<List<RoomMemberRepo.MemberBean>>() {
            @Override
            public void onChanged(final List<RoomMemberRepo.MemberBean> memberBeans) {
                dataRequestIsSuccess = true;
                for (int i = 0; i < memberBeans.size(); i++) {
                    //第一行不显示自己，过滤掉自己
                    if (memberBeans.get(i).getUserId().equals(CacheManager.getInstance().getUserId())) {
                        continue;
                    }
                    //显示除主持人、主播、禁言用户、当前用户外的成员
                    memberBeanList.add(memberBeans.get(i));
                }
                onlineRoomMemberAdapter.setRoomMemberList(memberBeanList);
                //过滤
                filterBankUserFromOnLine();
            }
        });
        onlineRoomMemberAdapter.setOnlineRoomMemberClickListener(new OnlineRoomMemberAdapter.OnlineRoomMemberClickListener() {
            @Override
            public void onClickConnect(final int position, final RoomMemberRepo.MemberBean memberBean) {
                final String userId = memberBean.getUserId();
                //邀请用户连麦
                final NetStateLiveData<NetResult<Void>> result = roomMemberViewModel.micInvite(userId);
                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        if (result.isSuccess()) {
                            SLog.e(SLog.TAG_SEAL_MIC, "邀请用户连麦成功");
                            //连麦成功后过滤
                            filterBanMicUser(userId, false);
                            //连麦之后通知dialog将dialog收起
                            EventBus.getDefault().post(new Event.EventUserLineStatusChange.ConnectMicBean());
                        } else {
                            ToastUtil.showToast("上麦失败！！");
                        }
                    }
                });
            }

            @Override
            public void onClickBan(int position, RoomMemberRepo.MemberBean memberBean) {
                final String userId = memberBean.getUserId();
                List<String> userIdList = new ArrayList<>();
                userIdList.add(userId);
                //禁言
                final NetStateLiveData<NetResult<Void>> result = roomMemberViewModel.banMember("add", userIdList);
                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        if (result.isSuccess()) {
                            SLog.e(SLog.TAG_SEAL_MIC, "禁言成功");
                            //禁言成功发送事件
                            filterBanMicUser(userId, true);

                            //禁言成功后刷新禁言列表
//                                roomMemberViewModel.gagMembers();
                        }
                    }
                });
            }

            @Override
            public void onClickKick(int position, RoomMemberRepo.MemberBean memberBean) {
                //踢人
                //请求demo server踢人的接口
                final String userId = memberBean.getUserId();
                List<String> userIdList = new ArrayList<>();
                userIdList.add(userId);
                final NetStateLiveData<NetResult<Void>> result = roomMemberViewModel.kickMember(userIdList);
                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        if (result.isSuccess()) {
                            SLog.e(SLog.TAG_SEAL_MIC, "踢人成功");
                            //踢人成功后过滤掉被踢出的人
                            filterBanMicUser(userId, false);
                        }
                    }
                });
                //麦位变动之后会下发KV，完成下麦操作
                //踢人demo server请求成功之后会下发踢人的自定义消息
            }
        });

    }

    /**
     * 过滤用户
     *
     * @param userId
     */
    private void filterBanMicUser(String userId, boolean isSendEvent) {

        Iterator<RoomMemberRepo.MemberBean> iterator = memberBeanList.iterator();
        while (iterator.hasNext()) {
            RoomMemberRepo.MemberBean memberBean = iterator.next();
            if (memberBean.getUserId().equals(userId)) {
                if (isSendEvent) {
                    //发送事件，禁言列表接收通知
                    EventBus.getDefault().post(new Event.EventUserLineStatusChange.BankMicBean(memberBean));
                }
                iterator.remove();
                break;
            }
        }
        //刷新本地列表
        onlineRoomMemberAdapter.setRoomMemberList(memberBeanList);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //解绑EventBus
        EventBus.getDefault().unregister(this);
    }
}
