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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.bean.repo.NetResult;
import cn.rongcloud.sealmicandroid.bean.repo.RoomMemberRepo;
import cn.rongcloud.sealmicandroid.common.NetStateLiveData;
import cn.rongcloud.sealmicandroid.common.factory.CommonViewModelFactory;
import cn.rongcloud.sealmicandroid.databinding.FragmentOnlineMemberBinding;
import cn.rongcloud.sealmicandroid.ui.room.member.RoomMemberViewModel;
import cn.rongcloud.sealmicandroid.util.ToastUtil;
import cn.rongcloud.sealmicandroid.util.log.SLog;

/**
 * 排麦列表页
 */
public class EnqueueFragment extends Fragment {
    private RoomMemberViewModel roomMemberViewModel;
    private FragmentOnlineMemberBinding fragmentEnqueueMemberBinding;
    private ArrayList<RoomMemberRepo.MemberBean> memberBeanList;
    private EnqueueMicAdapter enqueueMicAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        roomMemberViewModel = new ViewModelProvider(this, new CommonViewModelFactory()).get(RoomMemberViewModel.class);
        fragmentEnqueueMemberBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_online_member, container, false);
        fragmentEnqueueMemberBinding.setLifecycleOwner(this);
        fragmentEnqueueMemberBinding.setRoomMemberViewModel(roomMemberViewModel);
        return fragmentEnqueueMemberBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {

        memberBeanList = new ArrayList<>();
        enqueueMicAdapter = new EnqueueMicAdapter();
        fragmentEnqueueMemberBinding.rvRoomMember.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false));
        fragmentEnqueueMemberBinding.rvRoomMember.setAdapter(enqueueMicAdapter);
        roomMemberViewModel.micMembers();
        roomMemberViewModel.getMicMemberBeanListLiveData().observe(getViewLifecycleOwner(), new Observer<List<RoomMemberRepo.MemberBean>>() {
            @Override
            public void onChanged(List<RoomMemberRepo.MemberBean> memberBeans) {
                enqueueMicAdapter.setRoomMemberList(memberBeans);
                memberBeanList.addAll(memberBeans);
            }
        });
        enqueueMicAdapter.setMicAcceptOnClickListener(new EnqueueMicAdapter.OnMicAcceptClickListener() {
            @Override
            public void onMicAcceptClick(int position, RoomMemberRepo.MemberBean memberBean) {
                final String userId = memberBean.getUserId();
                //同意用户上麦
                final NetStateLiveData<NetResult<Void>> result = roomMemberViewModel.micAccept(userId);
                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        if (result.isSuccess()) {
                            SLog.e(SLog.TAG_SEAL_MIC, "同意上麦");
                            //同意上麦成功后从列表中过滤
                            filterEnqueueUser(userId);
                        } else {
                            ToastUtil.showToast("同意上麦失败！！");
                        }
                    }
                });
            }

            @Override
            public void onMicRejectClick(int position, RoomMemberRepo.MemberBean memberBean) {
                final String userId = memberBean.getUserId();
                //拒绝用户上麦
                final NetStateLiveData<NetResult<Void>> result = roomMemberViewModel.micReject(userId);
                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        if (result.isSuccess()) {
                            ToastUtil.showToast("拒绝用户上麦");
                            filterEnqueueUser(userId);
                        } else {
                            ToastUtil.showToast("操作失败");
                        }
                    }
                });
            }
        });
    }

    /**
     * 过滤掉被允许上麦或者被拒绝上麦的用户
     *
     * @param userId
     */
    private void filterEnqueueUser(String userId) {

        Iterator<RoomMemberRepo.MemberBean> memberBeanIterator = memberBeanList.iterator();
        while (memberBeanIterator.hasNext()) {
            RoomMemberRepo.MemberBean memberBean = memberBeanIterator.next();
            if (memberBean.getUserId().equals(userId)) {
                //从列表中移除
                memberBeanIterator.remove();
                break;
            }
        }
        //刷新列表
        enqueueMicAdapter.setRoomMemberList(memberBeanList);
    }
}
