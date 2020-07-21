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
import cn.rongcloud.sealmicandroid.ui.room.member.RoomMemberViewModel;
import cn.rongcloud.sealmicandroid.util.log.SLog;

/**
 * 禁言列表页
 */
public class BanMemberFragment extends Fragment {
    private RoomMemberViewModel roomMemberViewModel;
    private FragmentOnlineMemberBinding fragmentBanMicBinding;
    private ArrayList<RoomMemberRepo.MemberBean> memberBeanList;
    private BanMicAdapter banMicAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        roomMemberViewModel = new ViewModelProvider(this, new CommonViewModelFactory()).get(RoomMemberViewModel.class);
        fragmentBanMicBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_online_member, container, false);
        fragmentBanMicBinding.setLifecycleOwner(this);
        fragmentBanMicBinding.setRoomMemberViewModel(roomMemberViewModel);
        //注册EventBus
        EventBus.getDefault().register(this);
        return fragmentBanMicBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 接收被禁言用户的事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBanMicUserInfo(Event.EventUserLineStatusChange.BankMicBean micBean) {
        //判断是否已经存在
        boolean alreadyAtList = banMicAdapter.isAlreadyAtList(micBean.getMemberBean());
        if (alreadyAtList) {
            return;
        }
        //把被禁言的用户添加至禁言列表
        banMicAdapter.addRoomMember(micBean.getMemberBean());
        //添加至本地集合
        memberBeanList.add(micBean.getMemberBean());
    }

    private void initData() {

        memberBeanList = new ArrayList<>();
        banMicAdapter = new BanMicAdapter();
        fragmentBanMicBinding.rvRoomMember.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false));
        fragmentBanMicBinding.rvRoomMember.setAdapter(banMicAdapter);
        //禁言
        roomMemberViewModel.gagMembers();
        roomMemberViewModel.getGagMemberBeanListLiveData().observe(getViewLifecycleOwner(), new Observer<List<RoomMemberRepo.MemberBean>>() {
            @Override
            public void onChanged(List<RoomMemberRepo.MemberBean> memberBeans) {
                banMicAdapter.setRoomMemberList(memberBeans);
                memberBeanList.addAll(memberBeans);
                //判断禁言列表是否有人，有人则把数据发送事件发出去
                EventBus.getDefault().post(new Event.EventUserLineStatusChange.MicBankUserStatus(memberBeans));
            }
        });
        banMicAdapter.setBanRoomMemberOnClickListener(new BanMicAdapter.BanRoomMemberOnClickListener() {
            @Override
            public void onClickBan(int position, RoomMemberRepo.MemberBean memberBean) {
                final String userId = memberBean.getUserId();
                List<String> userIdList = new ArrayList<>();
                userIdList.add(userId);
                //解除禁言
                final NetStateLiveData<NetResult<Void>> result = roomMemberViewModel.banMember("remove", userIdList);
                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        if (result.isSuccess()) {
                            SLog.e(SLog.TAG_SEAL_MIC, "解除禁言成功");
                            //过滤掉被解禁的用户
                            filterUnBankUser(userId);
                        }
                    }
                });
            }
        });

    }

    /**
     * 过滤被解禁的用户
     *
     * @param userId
     */
    private void filterUnBankUser(String userId) {
        Iterator<RoomMemberRepo.MemberBean> memberBeanIterator = memberBeanList.iterator();
        while (memberBeanIterator.hasNext()) {
            RoomMemberRepo.MemberBean memberBean = memberBeanIterator.next();
            if (memberBean.getUserId().equals(userId)) {
                //发送通知事件被解禁的用户
                EventBus.getDefault().post(new Event.EventUserLineStatusChange.UnBankBean(memberBean));
                //从本地列表删除被解禁的用户
                memberBeanIterator.remove();
                break;
            }
        }
        //刷新本地列表
        banMicAdapter.setRoomMemberList(memberBeanList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //解绑EventBus
        EventBus.getDefault().unregister(this);
    }
}
