package cn.rongcloud.sealmicandroid.ui.room.member;

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
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.bean.kv.MicBean;
import cn.rongcloud.sealmicandroid.bean.repo.NetResult;
import cn.rongcloud.sealmicandroid.bean.repo.RoomMemberRepo;
import cn.rongcloud.sealmicandroid.common.Event;
import cn.rongcloud.sealmicandroid.common.NetStateLiveData;
import cn.rongcloud.sealmicandroid.common.constant.RoomMemberStatus;
import cn.rongcloud.sealmicandroid.common.factory.CommonViewModelFactory;
import cn.rongcloud.sealmicandroid.databinding.FragmentOnlineMemberBinding;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.manager.ThreadManager;
import cn.rongcloud.sealmicandroid.ui.room.adapter.BanMicAdapter;
import cn.rongcloud.sealmicandroid.ui.room.adapter.EnqueueMicAdapter;
import cn.rongcloud.sealmicandroid.ui.room.adapter.OnlineRoomMemberAdapter;
import cn.rongcloud.sealmicandroid.util.ToastUtil;
import cn.rongcloud.sealmicandroid.util.log.SLog;

/**
 * 房间观众成员管理
 */
public class RoomMemberFragment extends Fragment {

    private static final String TAB = "tab_name";
    private RoomMemberViewModel roomMemberViewModel;
    private FragmentOnlineMemberBinding fragmentOnlineMemberBinding;
    private String tabName;
    private OnlineRoomMemberAdapter onlineRoomMemberAdapter;
    private EnqueueMicAdapter enqueueMicAdapter;
    private BanMicAdapter banMicAdapter;
    private boolean onLineIsSuccess = false;
    private boolean banIsSuccess = false;

    /**
     * 在线成员列表
     */
    private List<RoomMemberRepo.MemberBean> onlineMemberBeanList;
    /**
     * 禁言成员列表
     */
    private List<RoomMemberRepo.MemberBean> banMemberBeanList;
    private RecyclerView recyclerView;
//    private RecyclerView onLineRecyclerView;
//    private RecyclerView banMicRecyclerView;
//    private RecyclerView enqueueRecyclerView;

    public static RoomMemberFragment newInstance(String tab) {
        RoomMemberFragment roomMemberFragment = new RoomMemberFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TAB, tab);
        roomMemberFragment.setArguments(bundle);
        return roomMemberFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tabName = getArguments().getString(TAB);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        roomMemberViewModel = new ViewModelProvider(this, new CommonViewModelFactory()).get(RoomMemberViewModel.class);
        fragmentOnlineMemberBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_online_member, container, false);
        fragmentOnlineMemberBinding.setLifecycleOwner(this);
        fragmentOnlineMemberBinding.setRoomMemberViewModel(roomMemberViewModel);
        return fragmentOnlineMemberBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        recyclerView = fragmentOnlineMemberBinding.rvRoomMember;
//        if (RoomMemberStatus.ONLINE.getStatus().equals(tabName)) {
//            onLineRecyclerView = fragmentOnlineMemberBinding.rvRoomMember;
//        } else if (RoomMemberStatus.ENQUEUE_MIC.getStatus().equals(tabName)) {
//            enqueueRecyclerView = fragmentOnlineMemberBinding.rvRoomMember;
//        } else if (RoomMemberStatus.BAN.getStatus().equals(tabName)) {
//            banMicRecyclerView = fragmentOnlineMemberBinding.rvRoomMember;
//        }
        //在线
        onlineMemberBeanList = new ArrayList<>();
        onlineRoomMemberAdapter = new OnlineRoomMemberAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false));

        //禁言
        banMemberBeanList = new ArrayList<>();
        banMicAdapter = new BanMicAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false));
        if (RoomMemberStatus.ONLINE.getStatus().equals(tabName)) {
            roomMemberViewModel.roomMembers();
            roomMemberViewModel.getMemberBeanListLiveData().observe(getViewLifecycleOwner(), new Observer<List<RoomMemberRepo.MemberBean>>() {
                @Override
                public void onChanged(List<RoomMemberRepo.MemberBean> memberBeans) {
                    for (RoomMemberRepo.MemberBean memberBean : memberBeans) {
                        //第一行不显示自己，过滤掉自己
                        if (memberBean.getUserId().equals(CacheManager.getInstance().getUserId())) {
                            continue;
                        }
                        //显示除主持人、主播、禁言用户、当前用户外的成员
                        onlineMemberBeanList.add(memberBean);
                    }

                    ThreadManager.getInstance().runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            SLog.i("asdd", onlineMemberBeanList.size() + "--" + onlineRoomMemberAdapter);
                            onlineRoomMemberAdapter.setRoomMemberList(onlineMemberBeanList);
                            recyclerView.setAdapter(onlineRoomMemberAdapter);
                            onLineIsSuccess = true;
                            setData();
                        }
                    });
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
                                filterOnlineRoomMembers(userId);
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
                                filterOnlineRoomMembers(userId);

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
                                filterOnlineRoomMembers(userId);
                            }
                        }
                    });
                    //麦位变动之后会下发KV，完成下麦操作
                    //踢人demo server请求成功之后会下发踢人的自定义消息
                }
            });
        } else if (RoomMemberStatus.ENQUEUE_MIC.getStatus().equals(tabName)) {
            //排麦
            final List<RoomMemberRepo.MemberBean> enqueueMemberBeans = new ArrayList<>();
            enqueueMicAdapter = new EnqueueMicAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                    LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(enqueueMicAdapter);
            roomMemberViewModel.micMembers();
            roomMemberViewModel.getMicMemberBeanListLiveData().observe(getViewLifecycleOwner(), new Observer<List<RoomMemberRepo.MemberBean>>() {
                @Override
                public void onChanged(List<RoomMemberRepo.MemberBean> memberBeans) {
                    enqueueMicAdapter.setRoomMemberList(memberBeans);
                    enqueueMemberBeans.addAll(memberBeans);
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
                                //同意用户上麦之后，将该用户直接从排麦列表当中移除并刷新列表
                                Iterator<RoomMemberRepo.MemberBean> memberBeanIterator = enqueueMemberBeans.iterator();
                                while (memberBeanIterator.hasNext()) {
                                    RoomMemberRepo.MemberBean member = memberBeanIterator.next();
                                    if (member.getUserId().equals(userId)) {
                                        memberBeanIterator.remove();
                                    }
                                }
                                enqueueMicAdapter.setRoomMemberList(enqueueMemberBeans);
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
                    result.observe(getViewLifecycleOwner(), new Observer<NetResult<Void>>() {
                        @Override
                        public void onChanged(NetResult<Void> voidNetResult) {
                            if (result.isSuccess()) {
                                ToastUtil.showToast("拒绝用户上麦");
                                Iterator<RoomMemberRepo.MemberBean> memberBeanIterator = enqueueMemberBeans.iterator();
                                while (memberBeanIterator.hasNext()) {
                                    RoomMemberRepo.MemberBean member = memberBeanIterator.next();
                                    if (member.getUserId().equals(userId)) {
                                        memberBeanIterator.remove();
                                    }
                                }
                                enqueueMicAdapter.setRoomMemberList(enqueueMemberBeans);
                            }
                        }
                    });
                }
            });
        } else if (RoomMemberStatus.BAN.getStatus().equals(tabName)) {
            //禁言
            roomMemberViewModel.gagMembers();
            roomMemberViewModel.getGagMemberBeanListLiveData().observe(getViewLifecycleOwner(), new Observer<List<RoomMemberRepo.MemberBean>>() {
                @Override
                public void onChanged(List<RoomMemberRepo.MemberBean> memberBeans) {
                    banMicAdapter.setRoomMemberList(memberBeans);
                    recyclerView.setAdapter(banMicAdapter);
                    banMemberBeanList.addAll(memberBeans);
                    banIsSuccess = true;
                    setData();
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
                                filterBanRoomMembers(userId);
                                //解除禁言成功后刷新在线列表
//                                roomMemberViewModel.roomMembers();
                            }
                        }
                    });
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMicBean(Event.EventMicBean eventMicBean) {
        MicBean micBean = eventMicBean.getMicBean();
        //过滤掉麦位上的人(包括主持人)
        filterOnlineRoomMembers(micBean.getUserId());
        SLog.e(SLog.TAG_SEAL_MIC, "onEventMicBean过滤麦位上的人");
    }

    public void setData() {

        SLog.i("asdda", onLineIsSuccess + "" + banIsSuccess);
        if (onLineIsSuccess && banIsSuccess) {
            SLog.i("asdda", onlineMemberBeanList.size() + "");
            //使用禁言列表的adapter对比在线列表的adapter，排除掉在线列表中已经禁言的用户
            //判断禁言列表是否有人，判断在线列表是否有人
            if (banMemberBeanList.size() > 0 && onlineMemberBeanList.size() > 0) {
                for (int i = 0; i < banMemberBeanList.size(); i++) {
                    for (int j = 0; j < onlineMemberBeanList.size(); j++) {
                        //从禁言列表中取出一个就去在线列表中的所有数据对比一次，如果相同则从在线列表中删除对应的用户
                        if (banMemberBeanList.get(i).getUserId().
                                equals(onlineMemberBeanList.get(j).getUserId())) {
                            onlineMemberBeanList.remove(i);
                        }
                    }

                }
            }
            onlineRoomMemberAdapter.setRoomMemberList(onlineMemberBeanList);
            recyclerView.setAdapter(onlineRoomMemberAdapter);

        }
    }

    /**
     * 在线成员列表中过滤掉想过滤的成员
     * 目前过滤掉的有: 在线：显示除主持人、主播、禁言用户、当前用户外的成员
     *
     * @param userId 被过滤的用户的id
     */
    public void filterOnlineRoomMembers(String userId) {
        RoomMemberRepo.MemberBean memberBean = null;
        Iterator<RoomMemberRepo.MemberBean> micBeanIterator = onlineMemberBeanList.iterator();
        while (micBeanIterator.hasNext()) {
            memberBean = micBeanIterator.next();
            if (memberBean.getUserId().equals(userId)) {
                micBeanIterator.remove();
                //把禁言掉的成员挪到禁言列表去
                //刷新禁言列表
                if (banMicAdapter != null) {
                    banMicAdapter.addRoomMember(memberBean);
                    recyclerView.setAdapter(banMicAdapter);
                } else {
                    ToastUtil.showToast("null");
                }
                break;
            }
        }
        //刷新列表界面
        if (onlineRoomMemberAdapter != null) {
            onlineRoomMemberAdapter.setRoomMemberList(onlineMemberBeanList);
            recyclerView.setAdapter(onlineRoomMemberAdapter);
        }

    }

    /**
     * 禁言成员列表中过滤掉想过滤的成员
     *
     * @param userId 被过滤的用户
     */
    public void filterBanRoomMembers(String userId) {
        Iterator<RoomMemberRepo.MemberBean> memberBeanIterator = banMemberBeanList.iterator();
        while (memberBeanIterator.hasNext()) {
            RoomMemberRepo.MemberBean member = memberBeanIterator.next();
            if (member.getUserId().equals(userId)) {
                memberBeanIterator.remove();
                //把解禁的成员放到在线列表去
                if (onlineRoomMemberAdapter != null) {
                    onlineRoomMemberAdapter.addRoomMember(member);
                    recyclerView.setAdapter(onlineRoomMemberAdapter);
                }
                break;
            }
        }
        if (banMicAdapter != null) {
            banMicAdapter.setRoomMemberList(banMemberBeanList);
            recyclerView.setAdapter(banMicAdapter);
        }
    }
}
