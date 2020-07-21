package cn.rongcloud.sealmicandroid.ui.room;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.rongcloud.rtc.api.report.StatusBean;
import cn.rongcloud.rtc.api.report.StatusReport;
import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.SealMicApp;
import cn.rongcloud.sealmicandroid.bean.SendSuperGiftBean;
import cn.rongcloud.sealmicandroid.bean.kv.AppliedMicListBean;
import cn.rongcloud.sealmicandroid.bean.kv.KvExtraBean;
import cn.rongcloud.sealmicandroid.bean.kv.MicBean;
import cn.rongcloud.sealmicandroid.bean.kv.SpeakBean;
import cn.rongcloud.sealmicandroid.bean.repo.NetResult;
import cn.rongcloud.sealmicandroid.bean.repo.RoomDetailRepo;
import cn.rongcloud.sealmicandroid.bean.repo.RoomMemberRepo;
import cn.rongcloud.sealmicandroid.common.Event;
import cn.rongcloud.sealmicandroid.common.MicState;
import cn.rongcloud.sealmicandroid.common.NetStateLiveData;
import cn.rongcloud.sealmicandroid.common.SealMicResultCallback;
import cn.rongcloud.sealmicandroid.common.adapter.ExtensionClickListenerAdapter;
import cn.rongcloud.sealmicandroid.common.adapter.SendMessageAdapter;
import cn.rongcloud.sealmicandroid.common.constant.ErrorCode;
import cn.rongcloud.sealmicandroid.common.constant.RoomMemberStatus;
import cn.rongcloud.sealmicandroid.common.constant.SealMicConstant;
import cn.rongcloud.sealmicandroid.common.constant.UserRoleType;
import cn.rongcloud.sealmicandroid.common.factory.CommonViewModelFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.BgBaseAudioDialogFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.ChangeBaseAudioDialogFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.ClickMessageDialogFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.GiftDialogFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.HandOverHostDialogFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.MicAudienceFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.MicConnectDialogFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.MicConnectTakeOverDialogFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.MicDialogFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.MicEnqueueDialogFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.MicSettingDialogFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.RoomMemberManagerDialogFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.RoomNoticeDialogFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.RoomSettingDialogFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.SelectedGiftDialogFactory;
import cn.rongcloud.sealmicandroid.common.factory.dialog.TakeOverHostDialogFactory;
import cn.rongcloud.sealmicandroid.common.lifecycle.RoomObserver;
import cn.rongcloud.sealmicandroid.common.listener.OnChatRoomTopBarClickListener;
import cn.rongcloud.sealmicandroid.common.listener.OnDialogButtonListClickListener;
import cn.rongcloud.sealmicandroid.common.listener.OnHandOverHostDialogClickListener;
import cn.rongcloud.sealmicandroid.common.listener.OnTakeOverHostDialogClickListener;
import cn.rongcloud.sealmicandroid.databinding.FragmentChatRoomBinding;
import cn.rongcloud.sealmicandroid.im.IMClient;
import cn.rongcloud.sealmicandroid.im.message.HandOverHostMessage;
import cn.rongcloud.sealmicandroid.im.message.KickMemberMessage;
import cn.rongcloud.sealmicandroid.im.message.SendGiftTag;
import cn.rongcloud.sealmicandroid.im.message.TakeOverHostMessage;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.manager.GlideManager;
import cn.rongcloud.sealmicandroid.manager.NavOptionsRouterManager;
import cn.rongcloud.sealmicandroid.manager.RoomManager;
import cn.rongcloud.sealmicandroid.manager.ThreadManager;
import cn.rongcloud.sealmicandroid.rtc.DebugInfoAdapter;
import cn.rongcloud.sealmicandroid.rtc.RTCClient;
import cn.rongcloud.sealmicandroid.ui.login.LoginViewModel;
import cn.rongcloud.sealmicandroid.ui.room.adapter.RoomChatMessageListAdapter;
import cn.rongcloud.sealmicandroid.ui.room.member.RoomMemberViewModel;
import cn.rongcloud.sealmicandroid.ui.widget.CustomDynamicAvatar;
import cn.rongcloud.sealmicandroid.ui.widget.MicTextLayout;
import cn.rongcloud.sealmicandroid.util.ButtonDelayUtil;
import cn.rongcloud.sealmicandroid.util.KeyBoardUtil;
import cn.rongcloud.sealmicandroid.util.SystemUtil;
import cn.rongcloud.sealmicandroid.util.ToastUtil;
import cn.rongcloud.sealmicandroid.util.log.SLog;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.ChatRoomInfo;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ChatRoomKVNotiMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;

/**
 * 聊天室
 */
public class ChatRoomFragment extends Fragment {

    public static final String TAG = ChatRoomFragment.class.getSimpleName();
    private static boolean isSpeak = false;
    /**
     * 软键盘是否显示
     */
    private boolean isShowKey = false;

    private FragmentChatRoomBinding fragmentChatRoomBinding;
    private ChatRoomViewModel chatRoomViewModel;
    private RoomMemberViewModel roomMemberViewModel;
    private LoginViewModel loginViewModel;
    private String roomId;
    private String roomName;
    private String roomTheme;
    private UserRoleType userRoleType;
    private ClickProxy clickProxy;
    private Gson gson;
    private List<CustomDynamicAvatar> dynamicAvatarViewList;
    private List<MicTextLayout> micTextLayoutList;
    private List<String> userIdList;
    boolean isAudienceJoin = false;
    boolean isAudienceFreeMic = false;
    String name;
    /**
     * 请求房间详情是否弹出设置dialog
     */
    private boolean isAlertSettingDialog = false;

    /**
     * 点击目标的名字
     */
    private String micUserName;
    private RoomChatMessageListAdapter roomChatMessageListAdapter;

    /**
     * 本地维护一个kv列表
     */
    private Map<Integer, MicBean> localMicBeanMap = new HashMap<>();

    /**
     * debug模式下RTC数据列表
     */
    private DebugInfoAdapter debugInfoAdapter;

    public ChatRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getLifecycle().addObserver(new RoomObserver());
        gson = new Gson();
        if (getArguments() != null) {
            roomId = getArguments().getString(SealMicConstant.ROOM_ID);
            CacheManager.getInstance().cacheRoomId(roomId);
            roomName = getArguments().getString(SealMicConstant.ROOM_NAME);
            roomTheme = getArguments().getString(SealMicConstant.ROOM_THEME);
            userRoleType = (UserRoleType) getArguments().getSerializable(SealMicConstant.ROOM_USER_ROLE);
            SLog.d(TAG, TextUtils.isEmpty(roomId) ? "" : roomId);
            SLog.d(TAG, TextUtils.isEmpty(roomName) ? "" : roomName);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentChatRoomBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_chat_room, container, false);
        chatRoomViewModel = new ViewModelProvider(this, new CommonViewModelFactory()).get(ChatRoomViewModel.class);
        roomMemberViewModel = new ViewModelProvider(this, new CommonViewModelFactory()).get(RoomMemberViewModel.class);
        loginViewModel = new ViewModelProvider(this, new CommonViewModelFactory()).get(LoginViewModel.class);
        fragmentChatRoomBinding.setLifecycleOwner(this);
        fragmentChatRoomBinding.setChatRoomViewModel(chatRoomViewModel);
        clickProxy = new ClickProxy();
        fragmentChatRoomBinding.setClick(clickProxy);
        dynamicAvatarViewList = new ArrayList<>();
        micTextLayoutList = new ArrayList<>();
        EventBus.getDefault().register(this);
        return fragmentChatRoomBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRoom();
        initView();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        if (CacheManager.getInstance().getUserRoleType() == UserRoleType.HOST.getValue()
                || CacheManager.getInstance().getUserRoleType() == UserRoleType.CONNECT_MIC.getValue()) {
            NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micQuit();
            result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    SLog.e(SLog.TAG_SEAL_MIC, "连麦者退出房间");
                }
            });
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUserGoOutBean(Event.UserGoOutBean userGoOutBean) {
        ToastUtil.showToast("当前账号在其他端登录");
        SLog.e(SLog.TAG_SEAL_MIC, "在聊天室页面被踢");
        loginViewModel.visitorLogin();
        NavOptionsRouterManager.getInstance().gotoLoginFragmentFromChatRoom(getView());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventSendSuperGift(Event.EventSendSuperGift eventSendSuperGift) {
        String userId = CacheManager.getInstance().getUserId();
        String userName = CacheManager.getInstance().getUserName();
        String userPortrait = CacheManager.getInstance().getUserPortrait();
        SendSuperGiftBean.UserBean userInfo = new SendSuperGiftBean.UserBean(userName, userPortrait, userId);
        SendSuperGiftBean sendSuperGiftBean = IMClient.getInstance().getSendSuperGiftBean(roomName, "RCMic:broadcastGift", userInfo);
        String json = new Gson().toJson(sendSuperGiftBean);
        chatRoomViewModel.messageBroad(json);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBroadcastGiftMessage(Event.EventBroadcastGiftMessage eventBroadcastGiftMessage) {
        TextView textView = fragmentChatRoomBinding.chatroomBroadcastGiftmessage;
        String strMsg = "<font color=\"#F8E71C\">"
                + eventBroadcastGiftMessage.getEventBroadcastGiftMessage().getUserInfo().getName()
                + "</font> 在 " + "<font color=\"#F8E71C\">"
                + eventBroadcastGiftMessage.getEventBroadcastGiftMessage().getRoomName()
                + "</font> 送出豪华跑车!!!";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(strMsg, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(strMsg));
        }
        ObjectAnimator animator;
        textView.setTranslationX(SystemUtil.dp2px(requireActivity(), -1));
        animator = ObjectAnimator.ofFloat(textView, "translationX",
                -2000);
        animator.setDuration(8000);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //当图片发生点击时可以通过下面代码将图片复位到原来位置
                //否则响应点击事件的图片可能会显示不全，不响应点击的忽略
                //image.setTranslationX(dp2px(-1));
            }
        });
        animator.start();
    }

    /**
     * 接收右上角小手在线列表过滤禁言列表后的数据，在这里过滤掉在麦位上的用户
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventFilterBankUser(Event.EventUserLineStatusChange.MicUserStatus micUserStatus) {
        List<RoomMemberRepo.MemberBean> memberBeanList = micUserStatus.getMemberBeanList();
        if (memberBeanList.size() <= 0) {
            return;
        }
        Iterator<RoomMemberRepo.MemberBean> beanIterator = memberBeanList.iterator();
        while (beanIterator.hasNext()) {
            RoomMemberRepo.MemberBean memberBean = beanIterator.next();
            for (int i = 0; i < localMicBeanMap.size(); i++) {
                if (localMicBeanMap.get(i).getUserId() != null
                        && !localMicBeanMap.get(i).getUserId().isEmpty()) {
                    if (memberBean.getUserId()
                            .equals(localMicBeanMap.get(i).getUserId())) {
                        beanIterator.remove();
                    }
                }
            }
        }
        //把处理完之后的数据在发送到在线列表中
        EventBus.getDefault().post(new Event.EventUserLineStatusChange.MicUserFilterBankAndMic(memberBeanList));
    }

    /**
     * 主持人的正在说话
     *
     * @param eventAudioInputLevel
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventAudioInputLevel(Event.EventAudioInputLevel eventAudioInputLevel) {
        int position = eventAudioInputLevel.getPosition();
        int inputLevel = eventAudioInputLevel.getInputLevel();
        // > 0表示正在讲话
        if (inputLevel > 0) {
            dynamicAvatarViewList.get(position).startSpeak();
        } else {
            dynamicAvatarViewList.get(position).stopSpeak();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventKvList(Event.EventKvMessage eventKvMessage) {
        String key = eventKvMessage.getChatRoomKVNotiMessage().getKey();
        int po = Integer.parseInt(key.substring(key.lastIndexOf("_") + 1));
        final SpeakBean speakBean = gson.fromJson(eventKvMessage.getChatRoomKVNotiMessage().getValue(), SpeakBean.class);
        Log.e(SLog.TAG_SEAL_MIC, speakBean.getPosition() + "speak" + speakBean.getSpeaking());
        if (speakBean.getSpeaking() > 0) {
            if (isSpeak) {
                return;
            }
            isSpeak = true;
            dynamicAvatarViewList.get(po).startSpeak();
        } else {
            if (!isSpeak) {
                return;
            }
            isSpeak = false;
            dynamicAvatarViewList.get(po).stopSpeak();
        }
    }

    /**
     * 收到房间内送的礼物
     *
     * @param eventGiftMessage
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event.EventGiftMessage eventGiftMessage) {

        //将消息添加到消息列表
        if (eventGiftMessage.getMessage() != null) {
            roomChatMessageListAdapter.addMessages(eventGiftMessage.getMessage());
            //设置定位到最后一行
            fragmentChatRoomBinding.chatroomListChat.smoothScrollToPosition(roomChatMessageListAdapter.getCount());
            fragmentChatRoomBinding.chatroomListChat.setSelection(roomChatMessageListAdapter.getCount());
        }
        //显示礼物弹窗
        SelectedGiftDialogFactory selectedGiftDialogFactory = new SelectedGiftDialogFactory();
        final Dialog giftDialog = selectedGiftDialogFactory.setSelectedGift(ContextCompat.getDrawable(SealMicApp.getApplication(),
                SendGiftTag.getGiftType(eventGiftMessage.getTag())))
                .buildDialog(requireActivity());
        giftDialog.show();
        selectedGiftDialogFactory.setGiftContent(eventGiftMessage.getContent());
        selectedGiftDialogFactory.setGiftTitle(eventGiftMessage.getMessage().getContent().getUserInfo().getName());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event.EventMemberChangeMessage eventMemberChangeMessage) {
        KickMemberMessage roomMemberChangeMessage = eventMemberChangeMessage.getRoomMemberChangeMessage();
        if (roomMemberChangeMessage.getType() == 0) {
            ToastUtil.showToast("被踢出了房间");
            NavOptionsRouterManager.getInstance().backUp(getView());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event.EventBroadcastRecallMessage recallMessage) {
        Message recallNtfMessage = recallMessage.getEventBroadRecallMessage();
        roomChatMessageListAdapter.removeMessage(recallNtfMessage.getMessageId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMicKVMessage(Event.EventMicKVMessage eventMicKVMessage) {
        ChatRoomKVNotiMessage chatRoomKVNotiMessage = eventMicKVMessage.getChatRoomKVNotiMessage();
        String json = chatRoomKVNotiMessage.getValue();
        String key = chatRoomKVNotiMessage.getKey();
        String extra = chatRoomKVNotiMessage.getExtra();
        //KV中携带的相关类型说明
        KvExtraBean kvExtraBean = new Gson().fromJson(extra, KvExtraBean.class);

        //当前房间id
        String roomId = CacheManager.getInstance().getRoomId();
        //当前用户id
        String userId = CacheManager.getInstance().getUserId();
        //当前登录用户本地保存的麦位信息（如果有则说明当前用户在麦位上）
        MicBean currentMicBean = CacheManager.getInstance().getMicBean();
        //当前用户的角色
        int currentUserType = CacheManager.getInstance().getUserRoleType();

        SpeakBean newSpeakBean = null;
        AppliedMicListBean appliedMicListBean = null;

        final MicBean newMicBean;
        //下发的KV通知共4类KV，分情况解析
        if (key.contains(SealMicConstant.KV_MIC_POSITION_PREFIX)) {
            //此次 KV 消息所携带的最新的麦位信息
            newMicBean = new Gson().fromJson(json, MicBean.class);
            //更新kv时本地更新麦位map
            localMicBeanMap.put(newMicBean.getPosition(), newMicBean);
            if (newMicBean != null) {
                //1. 根据新返回来的KV更新UI
                if (newMicBean.getState() == MicState.NORMAL.getState()) {
                    //用户下麦
                    dynamicAvatarViewList.get(newMicBean.getPosition()).micDelUser();
                    //主持人
                    if (UserRoleType.HOST.isHost(CacheManager.getInstance().getUserRoleType())) {
                        if (newMicBean.getUserId().equals(CacheManager.getInstance().getUserId())) {
                            micTextLayoutList.get(newMicBean.getPosition()).HasMic("号麦位");
                        } else {
                            micTextLayoutList.get(newMicBean.getPosition()).NullMic("号麦位");
                        }
                    }
                    //连麦者
                    if (UserRoleType.CONNECT_MIC.isConnectMic(CacheManager.getInstance().getUserRoleType()) ||
                            UserRoleType.AUDIENCE.isAudience(CacheManager.getInstance().getUserRoleType())) {
                        micTextLayoutList.get(newMicBean.getPosition()).NullMic("号麦位");
                    }
                    List<String> ids = new ArrayList<>();
                    ids.add(newMicBean.getUserId());
                    chatRoomViewModel.userBatch(ids);
                    final MicBean finalNewMicBean = newMicBean;
                    chatRoomViewModel.getUserinfolistRepoLiveData().observe(getViewLifecycleOwner(), new Observer<NetResult<List<RoomMemberRepo.MemberBean>>>() {
                        @Override
                        public void onChanged(NetResult<List<RoomMemberRepo.MemberBean>> listNetResult) {
                            List<RoomMemberRepo.MemberBean> memberBeanList = listNetResult.getData();
                            if (memberBeanList != null && memberBeanList.size() != 0) {
                                RoomMemberRepo.MemberBean memberBean = memberBeanList.get(0);
                                if ("".equals(memberBean.getUserName())) {
                                    micTextLayoutList.get(finalNewMicBean.getPosition()).HasMic(finalNewMicBean.getPosition() + "号麦");
                                } else {
                                    micTextLayoutList.get(finalNewMicBean.getPosition()).HasMic(memberBean.getUserName());
                                }
                                GlideManager.getInstance().setUrlImage(getView(),
                                        memberBean.getPortrait(),
                                        dynamicAvatarViewList.get(finalNewMicBean.getPosition()).getUserImg());

                            }
                        }
                    });
                } else if (newMicBean.getState() == MicState.LOCK.getState()) {
                    //麦位锁定
                    dynamicAvatarViewList.get(newMicBean.getPosition()).lockMic();
                } else if (newMicBean.getState() == MicState.CLOSE.getState()) {
                    //闭麦
                    dynamicAvatarViewList.get(newMicBean.getPosition()).bankMic();
                }

                //2. 被点的人是自己
                if (newMicBean.getUserId().equals(userId)) {
                    if (UserRoleType.AUDIENCE.isAudience(currentUserType)) {
                        //观众上麦
                        chatRoomViewModel.switchMic(roomId, CacheManager.getInstance().getUserRoleType(),
                                newMicBean.getPosition() == 0
                                        ? UserRoleType.HOST.getValue()
                                        : UserRoleType.CONNECT_MIC.getValue(),
                                new SealMicResultCallback<Map<String, String>>() {
                                    @Override
                                    public void onSuccess(Map<String, String> stringStringMap) {
                                        //切换角色上麦成功之后，更新当前用户角色
                                        ThreadManager.getInstance().runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SLog.e(SLog.TAG_SEAL_MIC, "观众上麦成功");
                                                //上麦成功后用户角色变为主持人或者连麦者，同时V层响应变化
                                                EventBus.getDefault().post(
                                                        newMicBean.getPosition() == 0
                                                                ? new Event.EventUserRoleType(UserRoleType.HOST, true)
                                                                : new Event.EventUserRoleType(UserRoleType.CONNECT_MIC, true));
                                                //上麦成功之后默认麦克风可用
                                                fragmentChatRoomBinding.chatroomVoiceIn.setSelected(false);
                                                RTCClient.getInstance().setLocalMicEnable(true);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFail(int errorCode) {

                                    }
                                });
                    }
                    //当前用户不在麦位或者当前用户所在麦位的状态和新的麦位状态不匹配
                    if (currentMicBean == null || currentMicBean.getState() != newMicBean.getState()) {
                        //根据新麦位的状态更新本地的麦克风状态（禁用或启用）
                        //底部栏图标，控制是否使用麦克风
                        //正常: 启用 打开方法  闭麦: 禁用 关闭方法
                        if (newMicBean.getState() == MicState.NORMAL.getState()) {
                            fragmentChatRoomBinding.chatroomVoiceIn.setSelected(false);
                            RTCClient.getInstance().setLocalMicEnable(true);
                        } else if (newMicBean.getState() == MicState.CLOSE.getState()) {
                            fragmentChatRoomBinding.chatroomVoiceIn.setSelected(true);
                            RTCClient.getInstance().setLocalMicEnable(false);
                        }
                    }

                    //在本地保存的麦位信息改变前将所在麦位的发言状态手动设置为 0 一次，设置到KV里
                    SpeakBean speakBean = new SpeakBean(0, currentMicBean != null ? currentMicBean.getPosition() : 0);
                    String speakingValue = new Gson().toJson(speakBean);
                    IMClient.getInstance().setChatRoomSpeakEntry(
                            roomId,
                            SealMicConstant.KV_SPEAK_POSITION_PREFIX + (currentMicBean != null ? currentMicBean.getPosition() : 0),
                            speakingValue);

                    //将本地保存的当前用户的麦位信息更新为新麦位的信息
                    CacheManager.getInstance().cacheMicBean(newMicBean);
                    //上麦情况下刷新本地map
                    localMicBeanMap.put(newMicBean.getPosition(), newMicBean);

                } else {

                    //如果 changeType 不是 （4，5，6）中的一种并且当前用户的麦位信息存在（也就是当前用户在麦位上）并且新麦位的序号等于当前用户时
                    int changeType = kvExtraBean.getChangeType();
                    //456 3种情况为不用下麦的情况
                    if (changeType != 4
                            && changeType != 5
                            && changeType != 6
                            && currentMicBean != null
                            && currentMicBean.getPosition() == newMicBean.getPosition()) {
                        //主播下麦
                        chatRoomViewModel.switchMic(roomId,
                                CacheManager.getInstance().getUserRoleType(),
                                UserRoleType.AUDIENCE.getValue(),
                                new SealMicResultCallback<Map<String, String>>() {
                                    @Override
                                    public void onSuccess(Map<String, String> stringStringMap) {
                                        //切换角色下麦成功之后，更新当前用户角色
                                        ThreadManager.getInstance().runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SLog.e(SLog.TAG_SEAL_MIC, "主播下麦成功");
                                                //下麦成功后用户角色变为观众，同时V层响应变化
                                                EventBus.getDefault().post(new Event.EventUserRoleType(UserRoleType.AUDIENCE, true));
                                                boolean outSelected = fragmentChatRoomBinding.chatroomVoiceOut.isSelected();
                                                RTCClient.getInstance().setSpeakerEnable(!outSelected);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFail(int errorCode) {

                                    }
                                });

                        //在本地保存的麦位信息改变前将所在麦位的发言状态手动设置为 0 一次
                        //设置到KV里
                        SpeakBean speakBean = new SpeakBean(0, currentMicBean.getPosition());
                        String speakingValue = new Gson().toJson(speakBean);
                        IMClient.getInstance().setChatRoomSpeakEntry(
                                roomId,
                                SealMicConstant.KV_SPEAK_POSITION_PREFIX + currentMicBean.getPosition(),
                                speakingValue);

                        //更新本地kv列表
                        currentMicBean.setUserId("");
                        //下麦后刷新本地麦位map
                        localMicBeanMap.put(currentMicBean.getPosition(), currentMicBean);
                        //将本地保存的当前用户的麦位信息更新为新麦位的信息
                        CacheManager.getInstance().cacheMicBean(null);
                    }
                }
            }
        }
        if (key.contains(SealMicConstant.KV_SPEAK_POSITION_PREFIX)) {
            //解析下发的正在讲话信息
            //远端用户靠下发的KV设置说话状态
            newSpeakBean = new Gson().fromJson(json, SpeakBean.class);
            CustomDynamicAvatar customDynamicAvatar = dynamicAvatarViewList.get(newSpeakBean.getPosition());
            //1: 正在说话  0: 没有说话
            if (newSpeakBean.getSpeaking() == 1) {
                customDynamicAvatar.startSpeak();
            } else {
                customDynamicAvatar.stopSpeak();
            }

            //本地用户靠音量是否大于1来判断是否显示动画


        }
        if (key.contains(SealMicConstant.KV_APPLIED_MIC_PREFIX)) {
            //解析下发的是否有人排麦信息
            //0 表示没人在排麦，1 表示有人在排麦
            if ("0".equals(json)) {
                fragmentChatRoomBinding.chatRoomTopBar.hideRedDot();
            } else if ("1".equals(json)) {
                fragmentChatRoomBinding.chatRoomTopBar.showRedDot();
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event.EventHandOverHostMessage eventHandOverHostMessage) {
        HandOverHostMessage handOverHostMessage = eventHandOverHostMessage.getHandOverHostMessage();
        String currentUserId = CacheManager.getInstance().getUserId();
        //当前用户的id等于通知下发下来的目标id，且用户角色不为主持人，弹窗
        boolean isShowDialog = currentUserId.equals(handOverHostMessage.getTargetUserId())
                && CacheManager.getInstance().getUserRoleType() != UserRoleType.HOST.getValue();
        if (isShowDialog) {
            if (handOverHostMessage.getCmd() == 0) {
                //主播端弹出转让主持人的弹出框
                HandOverHostDialogFactory handOverHostDialogFactory = new HandOverHostDialogFactory();
                Dialog dialog = handOverHostDialogFactory.buildDialog(requireActivity(), handOverHostMessage);
                handOverHostDialogFactory.setOnHandOverHostClickListener(new OnHandOverHostDialogClickListener() {
                    @Override
                    public void onAgree(HandOverHostMessage takeOverHostMessage) {
                        final NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micTransferHostAccept();
                        result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer integer) {
                                if (result.isSuccess()) {
                                    //同意转让，接口已通知服务器，服务器会更新麦位信息并下发麦位更新的KV通知
                                    ThreadManager.getInstance().runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            SLog.e(SLog.TAG_SEAL_MIC, "同意转让");

                                        }
                                    });
                                }
                            }
                        });
                    }

                    @Override
                    public void onRefuse(HandOverHostMessage takeOverHostMessage) {
                        final NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micTransferHostReject();
                        result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer integer) {
                                if (result.isSuccess()) {
                                    //拒绝转让，接口已通知服务器，服务器会更新麦位信息并下发麦位更新的KV通知
                                    ThreadManager.getInstance().runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            SLog.e(SLog.TAG_SEAL_MIC, "拒绝转让");
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
            if (handOverHostMessage.getCmd() == 1) {
                //最终拒绝了主持人的转让
                //主持人端弹出主播端是否接受了转让的最终结果
                ToastUtil.showToast(getResources().getString(R.string.hand_over_refuse));
            }
            if (handOverHostMessage.getCmd() == 2) {
                //最终同意了主持人的转让
                //主持人端弹出主播端是否接受了转让的最终结果
                ToastUtil.showToast(getResources().getString(R.string.hand_over_agree));
            }
        }
    }

    /**
     * 接管主持，通知事件
     *
     * @param eventTakeOverHostMessage
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event.EventTakeOverHostMessage eventTakeOverHostMessage) {
        TakeOverHostMessage takeOverHostMessage = eventTakeOverHostMessage.getTakeOverHostMessage();
        String currentUserId = CacheManager.getInstance().getUserId();
        //当前用户的id等于通知下发下来的目标id，且用户角色就是主持人，弹窗
        boolean isShowDialog = currentUserId.equals(takeOverHostMessage.getTargetUserId())
                && UserRoleType.HOST.getValue() == UserRoleType.HOST.getValue();
        if (isShowDialog) {
            if (takeOverHostMessage.getCmd() == 0) {
                //接管
                TakeOverHostDialogFactory takeOverHostDialogFactory = new TakeOverHostDialogFactory();
                Dialog dialog = takeOverHostDialogFactory.buildDialog(requireActivity(), takeOverHostMessage);
                takeOverHostDialogFactory.setOnTakeOverHostClickListener(new OnTakeOverHostDialogClickListener() {
                    @Override
                    public void onAgree(TakeOverHostMessage takeOverHostMessage) {
                        final NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micTakeOverHostAccept(takeOverHostMessage.getOperatorId());
                        result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer integer) {
                                if (result.isSuccess()) {
                                    //同意接管，接口已通知服务器，服务器会更新麦位信息并下发麦位更新的KV通知
                                    ThreadManager.getInstance().runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.showToast("同意接管");
                                        }
                                    });
                                }
                            }
                        });
                    }

                    @Override
                    public void onRefuse(TakeOverHostMessage takeOverHostMessage) {
                        final NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micTakeOverHostReject(takeOverHostMessage.getOperatorId());
                        result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer integer) {
                                if (result.isSuccess()) {
                                    //拒绝接管，接口已通知服务器，服务器会更新麦位信息并下发麦位更新的KV通知
                                    ThreadManager.getInstance().runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.showToast("拒绝接管");
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
            if (takeOverHostMessage.getCmd() == 1) {
                //最终拒绝了连麦者的接管
                ToastUtil.showToast(getResources().getString(R.string.take_over_refuse));
            }
            if (takeOverHostMessage.getCmd() == 2) {
                //最终同意了连麦者的接管
                ToastUtil.showToast(getResources().getString(R.string.take_over_agree));
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUserRoleType(Event.EventUserRoleType eventUserRoleType) {
        if (UserRoleType.AUDIENCE.isAudience(eventUserRoleType.getUserRoleType().getValue())) {
            fragmentChatRoomBinding.chatroomVoiceIn.setVisibility(View.GONE);
            fragmentChatRoomBinding.chatroomVoice.setVisibility(View.GONE);
        }
        if (UserRoleType.HOST.isHost(eventUserRoleType.getUserRoleType().getValue())
                || UserRoleType.CONNECT_MIC.isConnectMic(eventUserRoleType.getUserRoleType().getValue())) {
            fragmentChatRoomBinding.chatroomVoiceIn.setVisibility(View.VISIBLE);
            fragmentChatRoomBinding.chatroomVoice.setVisibility(View.VISIBLE);
        }
//        fragmentChatRoomBinding.chatroomVoiceChanger.setSelected(!eventUserRoleType.isMicOpen());
    }

    /**
     * 接收文本消息
     *
     * @param eventImList
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventImList(Event.EventImList eventImList) {
        Message message = eventImList.getMessage();
        if (message != null) {
            roomChatMessageListAdapter.addMessages(message);
            fragmentChatRoomBinding.chatroomListChat.smoothScrollToPosition(roomChatMessageListAdapter.getCount());
            fragmentChatRoomBinding.chatroomListChat.setSelection(roomChatMessageListAdapter.getCount());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        debugInfoAdapter = new DebugInfoAdapter(requireContext());
        fragmentChatRoomBinding.debugLayout.debugInfoList.setAdapter(debugInfoAdapter);

        //设置扬声器播放的图标，每次进入默认扬声器打开
        fragmentChatRoomBinding.chatroomVoiceOut.setSelected(false);
        RTCClient.getInstance().setSpeakerEnable(true);
        //扬声器是否播放，true为是
        boolean isSpeakerphoneOn = RTCClient.getInstance().isSpeakerphoneOn(requireContext());
        SLog.e(SLog.TAG_SEAL_MIC, "扬声器是否打开: " + isSpeakerphoneOn);

        //设置麦克风使用的图标，每次进入默认麦克风可用
        fragmentChatRoomBinding.chatroomVoiceIn.setSelected(false);
        RTCClient.getInstance().setLocalMicEnable(true);

        //更改roommanager的名字大小
        final MicTextLayout chatroomRoomManagerTv = fragmentChatRoomBinding.chatroomRoomManagerTv;
        if (chatroomRoomManagerTv != null) {
            chatroomRoomManagerTv.getTextView().setTextSize(14);
            chatroomRoomManagerTv.getImageView().setImageResource(R.drawable.bg_item_user_target);
        }

        //发送按钮的回调
        fragmentChatRoomBinding.rcExtension.setConversation(Conversation.ConversationType.CHATROOM, roomId);
        fragmentChatRoomBinding.rcExtension.setExtensionClickListener(new ExtensionClickListenerAdapter() {
            @Override
            public void onSendToggleClick(View v, String text) {
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                RoomManager.getInstance().sendMessage(roomId, text, new SendMessageAdapter() {
                    @Override
                    public void onSuccess(Message message) {
                        if (message.getContent() instanceof TextMessage) {
                            roomChatMessageListAdapter.addMessages(message);
                            fragmentChatRoomBinding.chatroomListChat.smoothScrollToPosition(roomChatMessageListAdapter.getCount());
                            fragmentChatRoomBinding.chatroomListChat.setSelection(roomChatMessageListAdapter.getCount());
                            super.onSuccess(message);
                        }

                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        super.onError(message, errorCode);
                        //已经被禁言，发送消息失败
                        if (errorCode.getValue() == ErrorCode.FORBIDDEN_IN_CHATROOM.getCode()) {
                            ToastUtil.showToast(getResources().getString(R.string.cant_speak));
                        }
                    }
                });
                fragmentChatRoomBinding.rcExtension.setVisibility(View.GONE);
                KeyBoardUtil.closeKeyBoard(requireActivity(), requireActivity().getCurrentFocus());
                fragmentChatRoomBinding.chatroomFunction.setVisibility(View.VISIBLE);
            }
        });
        chatRoomViewModel.roomDetail(roomId);
        chatRoomViewModel.getRoomDetailRepoMutableLiveData().observe(getViewLifecycleOwner(), new Observer<RoomDetailRepo>() {
            @Override
            public void onChanged(RoomDetailRepo roomDetailRepo) {
                isAudienceJoin = roomDetailRepo.isAllowedJoinRoom();
                isAudienceFreeMic = roomDetailRepo.isAllowedFreeJoinMic();
                fragmentChatRoomBinding.chatRoomTopBar.getRoomName().setText(roomDetailRepo.getRoomName());
                GlideManager.getInstance().setUrlImage(fragmentChatRoomBinding.getRoot(),
                        roomTheme,
                        fragmentChatRoomBinding.chatRoomTopBar.getRoomPortrait());
                //缓存整个房间信息
                chatRoomViewModel.saveRoomDetail(roomDetailRepo);
                //缓存房间后查看是否弹出设置dialog
                if (isAlertSettingDialog) {
                    //弹出设置的dialog框
                    clickProxy.alertDialog();
                }
            }
        });
        fragmentChatRoomBinding.chatRoomTopBar.setOnChatRoomTopBarClickListener(new OnChatRoomTopBarClickListener() {
            @Override
            public void back(View view) {
                NavOptionsRouterManager.getInstance().backUp(view);
            }

            @Override
            public void noticeDialog() {
                clickProxy.showRoomNoticeDialog();
            }

            @Override
            public void lineUpDialog() {
                clickProxy.showRoomMemberManagerDialog();
            }

            @Override
            public void settingRoomDialog() {
                clickProxy.showRoomSettingDialog();
            }
        });
        roomChatMessageListAdapter = new RoomChatMessageListAdapter(SealMicApp.getApplication());
        List<Message> messageList = new ArrayList<>();

        fragmentChatRoomBinding.chatroomListChat.setAdapter(roomChatMessageListAdapter);
        roomChatMessageListAdapter.setMessages(messageList);
        //加入房间后给集合里面添加一条默认消息
        TextMessage currentUserTextMessage = new TextMessage(getResources().getString(R.string.welcome_join_room));
        currentUserTextMessage.setUserInfo(new UserInfo(CacheManager.getInstance().getUserId(),
                CacheManager.getInstance().getUserName(),
                Uri.parse(CacheManager.getInstance().getUserPortrait())));

        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.CHATROOM, roomId, currentUserTextMessage, null, null, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onSuccess(Message message) {
                roomChatMessageListAdapter.addMessages(message);
                //设置定位到最后一行
                fragmentChatRoomBinding.chatroomListChat.setSelection(roomChatMessageListAdapter.getCount());
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {

            }
        });
        if (roomChatMessageListAdapter.getCount() > 0) {
            //设置定位到最后一行
            fragmentChatRoomBinding.chatroomListChat.setSelection(roomChatMessageListAdapter.getCount());
        }

        dynamicAvatarViewList.add(fragmentChatRoomBinding.chatroomRoomManager);
        micTextLayoutList.add(fragmentChatRoomBinding.chatroomRoomManagerTv);
        for (int i = 0; i < fragmentChatRoomBinding.chatroomMiclist.getChildCount(); i++) {
            View v = fragmentChatRoomBinding.chatroomMiclist.getChildAt(i);
            if (v instanceof CustomDynamicAvatar) {
                CustomDynamicAvatar d = (CustomDynamicAvatar) v;
                dynamicAvatarViewList.add(d);
            }
            if (v instanceof MicTextLayout) {
                MicTextLayout m = (MicTextLayout) v;
                micTextLayoutList.add(m);
            }
        }

        //点击消息的OnClick回调
        roomChatMessageListAdapter.setCallClick(new RoomChatMessageListAdapter.CallClick() {
            @Override
            public void onClick(int position, final Message message) {
                final String currentUserId = CacheManager.getInstance().getUserId();
                int userRoleType = CacheManager.getInstance().getUserRoleType();
                if (UserRoleType.HOST.isHost(userRoleType)) {
                    //主持人点击消息列表条目
                    ThreadManager.getInstance().runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            String userId = message.getContent().getUserInfo().getUserId();
                            //主持人点的消息是谁发的
                            ClickMessageDialogFactory clickMessageDialogFactory = new ClickMessageDialogFactory();
                            if (currentUserId.equals(userId)) {
                                clickMessageDialogFactory.setClickType(true);
                            } else {
                                clickMessageDialogFactory.setClickType(false);
                            }
                            final Dialog dialog = clickMessageDialogFactory.buildDialog(requireActivity());
                            if (currentUserId.equals(userId)) {
                                name = CacheManager.getInstance().getUserName();
                                clickMessageDialogFactory.setPortrait(CacheManager.getInstance().getUserPortrait());
                                clickMessageDialogFactory.setUserName(CacheManager.getInstance().getUserName());
                                clickMessageDialogFactory.setMicPosition("主持人");
                            } else {
                                //循环麦位map，查看当前发消息的人是否存在于麦位上
                                for (int i = 0; i < localMicBeanMap.size(); i++) {
                                    MicBean micBean = localMicBeanMap.get(i);
                                    if (micBean.getUserId() != null && !micBean.getUserId().isEmpty()) {
                                        if (localMicBeanMap.get(i).getUserId().equals(userId)) {
                                            //说明发消息的人在麦位上,隐藏发消息的角色
                                            clickMessageDialogFactory.setMicPositionIsGong();
                                            break;
                                        }
                                    }
                                }
                                name = message.getContent().getUserInfo().getName();
                                clickMessageDialogFactory.setPortrait(message.getContent().getUserInfo().getPortraitUri().toString());
                                clickMessageDialogFactory.setUserName(message.getContent().getUserInfo().getName());
                                clickMessageDialogFactory.setMicPosition("观众");
                            }

                            clickMessageDialogFactory.setOnDialogButtonListClickListener(new OnDialogButtonListClickListener() {
                                @Override
                                public void onClick(String content) {

                                    //邀请连麦
                                    if (getResources().getString(R.string.connect_speak).equals(content)) {
                                        final NetStateLiveData<NetResult<Void>> result = roomMemberViewModel.micInvite(message.getContent().getUserInfo().getUserId());
                                        result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                            @Override
                                            public void onChanged(Integer integer) {
                                                if (result.isSuccess()) {
                                                    SLog.e(SLog.TAG_SEAL_MIC, "邀请用户连麦成功");
                                                    dialog.cancel();
                                                }
                                            }
                                        });
                                    }

                                    //点击发送消息，此处弹出键盘发消息
                                    if (getResources().getString(R.string.send_message).equals(content)) {
                                        sendMessage(dialog);
                                    }

                                    //禁言
                                    if (getResources().getString(R.string.lock_wheet_speak).equals(content)) {
                                        List<String> userIds = new ArrayList<>();
                                        userIds.add(message.getContent().getUserInfo().getUserId());
                                        final NetStateLiveData<NetResult<Void>> result = roomMemberViewModel.banMember("add", userIds);
                                        result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                            @Override
                                            public void onChanged(Integer integer) {
                                                if (result.isSuccess()) {
                                                    ToastUtil.showToast("禁止发言");
                                                    dialog.cancel();
                                                }
                                            }
                                        });
                                    }

                                    //点击移除房间
                                    if (getResources().getString(R.string.go_out_room).equals(content)) {
                                        List<String> userIds = new ArrayList<>();
                                        userIds.add(message.getContent().getUserInfo().getUserId());
                                        final NetStateLiveData<NetResult<Void>> result = roomMemberViewModel.kickMember(userIds);
                                        result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                            @Override
                                            public void onChanged(Integer integer) {
                                                if (result.isSuccess()) {
                                                    ToastUtil.showToast("移出房间");
                                                    dialog.cancel();
                                                }
                                            }
                                        });
                                    }

                                    //删除消息
                                    if (getResources().getString(R.string.delete_message).equals(content)) {
                                        recallMessage(message, "");
                                        dialog.cancel();
                                    }


                                }
                            });
                            dialog.show();
                        }
                    });
                }
            }
        });

        fragmentChatRoomBinding.chatroomVoiceOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //给当前用户设置外放
                fragmentChatRoomBinding.chatroomVoiceOut.setSelected(!fragmentChatRoomBinding.chatroomVoiceOut.isSelected());
                RTCClient.getInstance().setSpeakerEnable(!fragmentChatRoomBinding.chatroomVoiceOut.isSelected());
            }
        });
        fragmentChatRoomBinding.chatroomVoiceIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ButtonDelayUtil.isNormalClick()) {
                    //给当前用户设置麦克风
                    MicBean micBean = CacheManager.getInstance().getMicBean();
                    boolean isSelectedChatRoomVoiceIn = fragmentChatRoomBinding.chatroomVoiceIn.isSelected();
                    if (isSelectedChatRoomVoiceIn) {
                        //如果没选中，麦位正常
                        chatRoomViewModel.setLocalMicEnable(
                                true,
                                micBean.getPosition(),
                                new SealMicResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        fragmentChatRoomBinding.chatroomVoiceIn.setSelected(!aBoolean);
                                    }

                                    @Override
                                    public void onFail(int errorCode) {

                                    }
                                });
                    } else {
                        //如果选中了，麦位关闭
                        chatRoomViewModel.setLocalMicEnable(
                                false,
                                micBean.getPosition(),
                                new SealMicResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        fragmentChatRoomBinding.chatroomVoiceIn.setSelected(!aBoolean);
                                    }

                                    @Override
                                    public void onFail(int errorCode) {

                                    }
                                }
                        );
                    }
                }
            }
        });

        //设置recyclerview监听
        fragmentChatRoomBinding.chatroomListChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //判断软键盘是否显示
                if (isShowKey) {
                    clickProxy.hide();
                    fragmentChatRoomBinding.chatroomListChat.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event.EventMicStatusReport eventMicStatusReport) {
        StatusReport statusReport = eventMicStatusReport.getStatusReport();
        String delayMs = String.format(getString(R.string.delay_ms), String.valueOf(statusReport.rtt));
        fragmentChatRoomBinding.chatRoomTopBar.getRtt().setText(delayMs);
        updateDebugInfo(statusReport);
    }

    private void updateDebugInfo(StatusReport statusReport) {
        fragmentChatRoomBinding.debugLayout.debugInfoBitrateSend.setText(String.valueOf(statusReport.bitRateSend));
        fragmentChatRoomBinding.debugLayout.debugInfoBitrateRcv.setText(String.valueOf(statusReport.bitRateRcv));
        fragmentChatRoomBinding.debugLayout.debugInfoRttSend.setText(String.valueOf(statusReport.rtt));
        List<StatusBean> statusBeans = RTCClient.getInstance().parseToDebugInfoList(statusReport);
        debugInfoAdapter.setStatusBeanList(statusBeans);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event.EventAudioReceivedLevel eventAudioReceivedLevel) {
        HashMap<String, String> audioLevel = eventAudioReceivedLevel.getAudioLevel();

    }

    public void initData() {
        chatRoomViewModel.onlineNumber(roomId, new SealMicResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean) {
                    IMClient.getInstance().getChatRoomInfo(roomId, new RongIMClient.ResultCallback<ChatRoomInfo>() {
                        @Override
                        public void onSuccess(ChatRoomInfo chatRoomInfo) {
                            int onlineNumber = chatRoomInfo.getTotalMemberCount();
                            String onlineNumberString = SealMicApp.getApplication().getResources().getString(R.string.online_number);
                            fragmentChatRoomBinding.chatRoomTopBar.getOnlineNumber().setText(String.format(onlineNumberString, onlineNumber + ""));
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }
            }

            @Override
            public void onFail(int errorCode) {

            }
        });
    }

    public void initRoom() {
        //根据不同的用户角色定义不同的逻辑，例如不同用户角色对应不同的操作权限和UI展示，
        if (UserRoleType.AUDIENCE.isAudience(userRoleType.getValue())) {
            fragmentChatRoomBinding.chatroomVoiceIn.setVisibility(View.GONE);
            fragmentChatRoomBinding.chatroomVoice.setVisibility(View.GONE);
            RoomManager.getInstance().audienceJoinRoom(roomId, new SealMicResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean b) {
                    SLog.e(SLog.TAG_SEAL_MIC, "以观众的身份加入房间");
                    initMic();
                    initSpeak();
                    //房间加入成功，初始化音频
                    //设置扬声器播放的图标
                    //获取手机系统数据，是否为扬声器播放
                    boolean speakerphoneOn = RTCClient.getInstance().isSpeakerphoneOn(SealMicApp.getApplication());
                    fragmentChatRoomBinding.chatroomVoiceOut.setSelected(!speakerphoneOn);
                    RTCClient.getInstance().setSpeakerEnable(speakerphoneOn);
                }

                @Override
                public void onFail(int errorCode) {
                    SLog.e(SLog.TAG_SEAL_MIC, "以观众的身份加入房间: " + errorCode);
                }
            });
        }
        if (UserRoleType.HOST.isHost(userRoleType.getValue())
                || UserRoleType.CONNECT_MIC.isConnectMic(userRoleType.getValue())) {
            fragmentChatRoomBinding.chatroomVoiceIn.setVisibility(View.VISIBLE);
            fragmentChatRoomBinding.chatroomVoice.setVisibility(View.VISIBLE);
            RoomManager.getInstance().micJoinRoom(roomId, new RongIMClient.ResultCallback<String>() {
                @Override
                public void onSuccess(String roomId) {
                    SLog.e(SLog.TAG_SEAL_MIC, "以主持人的身份加入房间");
                    //创建房间之后，保存用户角色，关键盘，跳走
                    CacheManager.getInstance().cacheUserRoleType(UserRoleType.HOST.getValue());
                    KeyBoardUtil.closeKeyBoard(requireActivity(), getView());
                    initMic();
                    initSpeak();
                    //房间加入成功，初始化音频
                    //设置扬声器播放的图标
                    boolean speakerphoneOn = RTCClient.getInstance().isSpeakerphoneOn(SealMicApp.getApplication());
                    fragmentChatRoomBinding.chatroomVoiceOut.setSelected(!speakerphoneOn);
                    RTCClient.getInstance().setSpeakerEnable(speakerphoneOn);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    SLog.e(SLog.TAG_SEAL_MIC, "以主持人的身份加入房间" + errorCode);
                }
            });
        }
        initData();
    }

    /**
     * 撤回发送的消息
     */
    public void recallMessage(final Message message, String pushMessage) {
//        if (message.getMessageId())
        SLog.i("asdff", message.getMessageId() + "");
        IMClient.getInstance().recallMessage(message, pushMessage, new RongIMClient.ResultCallback<RecallNotificationMessage>() {
            /**
             * 删除消息成功的回调
             * @param recallNotificationMessage
             */
            @Override
            public void onSuccess(RecallNotificationMessage recallNotificationMessage) {
                SLog.e(SLog.TAG_SEAL_MIC, "消息删除成功");
                roomChatMessageListAdapter.removeMessage(message.getMessageId());
            }

            /**
             * 删除消息失败的回调
             * @param errorCode
             */
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                SLog.e(SLog.TAG_SEAL_MIC, "消息删除失败: " + errorCode.getValue());
            }
        });
    }

    public void initSpeak() {
        RoomManager.getInstance().getAllChatRoomSpeaking(roomId, new RongIMClient.ResultCallback<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> stringStringMap) {
                for (String key : stringStringMap.keySet()) {
                    final SpeakBean speakBean = gson.fromJson(stringStringMap.get(key), SpeakBean.class);
                    ThreadManager.getInstance().runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (1 == speakBean.getSpeaking()) {
                                dynamicAvatarViewList.get(speakBean.getPosition()).startSpeak();
                            } else {
                                dynamicAvatarViewList.get(speakBean.getPosition()).stopSpeak();
                            }
                        }
                    });

                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                SLog.e(SLog.TAG_SEAL_MIC, "获取正在讲话的KV信息失败，错误码为: " + errorCode);
                ToastUtil.showToast("获取正在讲话的KV信息失败，错误码为: " + errorCode);
                NavOptionsRouterManager.getInstance().backUp(getView());
            }
        });
    }

    private void initMic() {
        //获取全部麦位的KV
        RoomManager.getInstance().getAllChatRoomMic(roomId, new RongIMClient.ResultCallback<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> stringStringMap) {
                //根据KV判断，如果对应的麦位上有人，则用对应的信息填充麦位
                RoomManager.getInstance().transMicBean(stringStringMap, new SealMicResultCallback<MicBean>() {
                    @Override
                    public void onSuccess(final MicBean micBean) {
                        //初始化麦位时本地保存一份麦位map
                        localMicBeanMap.put(micBean.getPosition(), micBean);
                        userIdList = new ArrayList<>();
                        userIdList.add(micBean.getUserId());
                        chatRoomViewModel.userBatch(userIdList);
                        //请求完后更新V层
                        //Can't access the Fragment View's LifecycleOwner when getView() is null i.e., before onCreateView() or after onDestroyView()
                        if (getView() != null) {
                            chatRoomViewModel.getUserinfolistRepoLiveData().observe(getViewLifecycleOwner(), new Observer<NetResult<List<RoomMemberRepo.MemberBean>>>() {
                                @Override
                                public void onChanged(NetResult<List<RoomMemberRepo.MemberBean>> listNetResult) {
                                    if (listNetResult != null && listNetResult.getData().size() != 0) {
                                        dynamicAvatarViewList.get(micBean.getPosition()).stopSpeak();
                                        GlideManager.getInstance().setUrlImage(fragmentChatRoomBinding.getRoot(),
                                                listNetResult.getData().get(0).getPortrait(),
                                                dynamicAvatarViewList.get(micBean.getPosition()).getUserImg());
                                        micTextLayoutList.get(micBean.getPosition()).HasMic(listNetResult.getData().get(0).getUserName());
                                    }
                                    if (micBean.getState() == MicState.NORMAL.getState()) {
                                        dynamicAvatarViewList.get(micBean.getPosition()).unBankMic();
                                    } else if (micBean.getState() == MicState.CLOSE.getState()) {
                                        dynamicAvatarViewList.get(micBean.getPosition()).bankMic();
                                    } else if (micBean.getState() == MicState.LOCK.getState()) {
                                        dynamicAvatarViewList.get(micBean.getPosition()).lockMic();
                                    }
                                }
                            });
                        }
                        EventBus.getDefault().postSticky(new Event.EventMicBean(micBean));
                    }

                    @Override
                    public void onFail(int errorCode) {
                        SLog.e(SLog.TAG_SEAL_MIC, "获取初始化麦位信息失败: " + errorCode);
                    }
                });
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                SLog.e(SLog.TAG_SEAL_MIC, "获取全部麦位的KV信息失败，错误码为: " + errorCode);
                ToastUtil.showToast("获取全部麦位的KV信息失败，错误码为: " + errorCode);
                NavOptionsRouterManager.getInstance().backUp(getView());
            }
        });
    }


    public void clickMic(final int position) {
        //获取点击的麦位信息
        MicBean clickMicBean = localMicBeanMap.get(position);
        if (clickMicBean != null) {
            if (TextUtils.isEmpty(clickMicBean.getUserId())) {
                //空麦位
                if (UserRoleType.HOST.getValue() == CacheManager.getInstance().getUserRoleType()) {
                    //主持人
                    micAbsentHost(clickMicBean);
                } else if (UserRoleType.CONNECT_MIC.getValue() == CacheManager.getInstance().getUserRoleType()) {
                    //连麦者
                    micAbsentConnect(clickMicBean);
                } else if (UserRoleType.AUDIENCE.getValue() == CacheManager.getInstance().getUserRoleType()) {
                    //观众
                    micAbsentAudience(clickMicBean);
                }
            } else {
                //麦位上有人
                if (UserRoleType.HOST.getValue() == CacheManager.getInstance().getUserRoleType()) {
                    //主持人
                    micPresentHost(clickMicBean);
                } else if (UserRoleType.CONNECT_MIC.getValue() == CacheManager.getInstance().getUserRoleType()) {
                    //连麦者
                    micPresentConnect(clickMicBean);
                } else if (UserRoleType.AUDIENCE.getValue() == CacheManager.getInstance().getUserRoleType()) {
                    //观众
                    micPresentAudience(clickMicBean);
                }
            }
        }
    }

    /**
     * 用户角色为主持人时，空麦位时对应的操作
     */
    public void micAbsentHost(final MicBean micBean) {
        //麦位上没人
        ThreadManager.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                MicSettingDialogFactory micSettingDialogFactory = new MicSettingDialogFactory();
                final Dialog dialog = micSettingDialogFactory.buildDialog(requireActivity(), micBean);
                micSettingDialogFactory.setOnDialogButtonListClickListener(new OnDialogButtonListClickListener() {
                    @Override
                    public void onClick(String content) {
                        if (getResources().getString(R.string.invite_mic).equals(content)) {
                            //判断状态是否为锁定状态
                            if (micBean.getState() == MicState.LOCK.getState()) {
                                ToastUtil.showToast(getResources().getString(R.string.current_mic_lock));
                                dialog.cancel();
                                return;
                            }
                            //点击邀请连麦
                            new RoomMemberManagerDialogFactory().buildDialog(requireActivity(), RoomMemberStatus.ONLINE.getStatus()).show();
                            dialog.cancel();
                        }
                        if (getResources().getString(R.string.lock_mic).equals(content)) {
                            //点击麦位状态操作
                            if (micBean.getState() == MicState.NORMAL.getState()) {
                                //如果麦位为正常，则锁定
                                final NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micState(MicState.LOCK.getState(), micBean.getPosition());
                                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer integer) {
                                        if (result.isSuccess()) {
                                            ToastUtil.showToast("锁定麦位");
                                            dialog.cancel();
                                        }
                                    }
                                });
                            }
                        }


                        if (getResources().getString(R.string.unlock_all_mic).equals(content)) {
                            if (micBean.getState() == MicState.LOCK.getState()) {
                                //如果麦位为锁定，则解锁
                                final NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micState(MicState.NORMAL.getState(), micBean.getPosition());
                                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer integer) {
                                        if (result.isSuccess()) {
                                            ToastUtil.showToast("解锁麦位");
                                            dialog.cancel();
                                        }
                                    }
                                });
                            }
                        }
//                        }
                    }
                });
                dialog.show();
                micSettingDialogFactory.setWheetContent("麦位管理-" + micBean.getPosition() + "号麦");
            }
        });
    }

    /**
     * 用户角色为主持人时，麦位上有人对应的操作
     */
    public void micPresentHost(final MicBean micBean) {

        //麦位上有人
        //主持人自己点自己
        if (micBean.getPosition() == 0 && CacheManager.getInstance().getUserId().equals(micBean.getUserId())) {
            ThreadManager.getInstance().runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    final MicConnectDialogFactory micConnectFactory = new MicConnectDialogFactory();
                    final Dialog dialog = micConnectFactory.buildDialog(requireActivity());
                    micConnectFactory.setCurrentUser(true);
                    micConnectFactory.setOnDialogButtonListClickListener(new OnDialogButtonListClickListener() {
                        @Override
                        public void onClick(String content) {
                            NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micQuit();
                            result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                @Override
                                public void onChanged(Integer integer) {
                                    SLog.e(SLog.TAG_SEAL_MIC, "连麦者下麦");
                                    dialog.cancel();
                                }
                            });
                        }
                    });
                    List<String> ids = new ArrayList<>();
                    ids.add(micBean.getUserId());
                    chatRoomViewModel.userBatch(ids);
                    chatRoomViewModel.getUserinfolistRepoLiveData().observe(getViewLifecycleOwner(), new Observer<NetResult<List<RoomMemberRepo.MemberBean>>>() {
                        @Override
                        public void onChanged(NetResult<List<RoomMemberRepo.MemberBean>> listNetResult) {
                            List<RoomMemberRepo.MemberBean> result = listNetResult.getData();
                            if (result == null || result.size() == 0) {
                                return;
                            }
                            RoomMemberRepo.MemberBean memberBean = result.get(0);
                            micConnectFactory.setUserName(memberBean.getUserName());
                        }
                    });
                    micConnectFactory.setMicPosition("主持人");
                    dialog.show();
                }
            });
        }

        //主持人点击别的主播
        if (micBean.getPosition() != 0 && !"".equals(micBean.getUserId())) {
            micUserName = "";
            ThreadManager.getInstance().runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    final MicDialogFactory micDialogFactory = new MicDialogFactory();
                    final Dialog micDialog = micDialogFactory.buildDialog(requireActivity(), micBean);
                    List<String> ids = new ArrayList<>();
                    ids.add(micBean.getUserId());
                    chatRoomViewModel.userBatch(ids);
                    chatRoomViewModel.getUserinfolistRepoLiveData().observe(getViewLifecycleOwner(), new Observer<NetResult<List<RoomMemberRepo.MemberBean>>>() {
                        @Override
                        public void onChanged(NetResult<List<RoomMemberRepo.MemberBean>> listNetResult) {
                            RoomMemberRepo.MemberBean memberBean = listNetResult.getData().get(0);
                            name = memberBean.getUserName();
                            //获取点击目标的name
                            micUserName = memberBean.getUserName();
                            micDialogFactory.setUserName(memberBean.getUserName());
                            micDialogFactory.setPortrait(memberBean.getPortrait());
                        }
                    });
                    micDialogFactory.setMicPosition(String.valueOf(micBean.getPosition()) + "号麦");
                    micDialogFactory.setOnDialogButtonListClickListener(new OnDialogButtonListClickListener() {
                        @Override
                        public void onClick(String content) {
                            if (getResources().getString(R.string.close_mic).equals(content)) {
                                //点击闭麦
                                final NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micState(MicState.CLOSE.getState(), micBean.getPosition());
                                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer integer) {
                                        if (result.isSuccess()) {
                                            ToastUtil.showToast("关闭麦位");
                                            micDialog.cancel();
                                        }
                                    }
                                });
                            }
                            if (getResources().getString(R.string.open_mic).equals(content)) {
                                //点击开麦
                                final NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micState(MicState.NORMAL.getState(), micBean.getPosition());
                                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer integer) {
                                        if (result.isSuccess()) {
                                            ToastUtil.showToast("开启麦位");
                                            micDialog.cancel();
                                        }
                                    }
                                });
                            }
                            if (getResources().getString(R.string.down_mic).equals(content)) {
                                //点击下麦
                                final NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micKick(micBean.getUserId());
                                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer integer) {
                                        if (result.isSuccess()) {
                                            ToastUtil.showToast("踢人下麦");
                                            micDialog.cancel();
                                        }
                                    }
                                });
                            }
                            if (getResources().getString(R.string.hand_over_host).equals(content)) {
                                //点击转让主持人
                                final NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micTransferHost(micBean.getUserId());
                                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer integer) {
                                        if (result.isSuccess()) {
                                            ToastUtil.showToast("转让主持人");
                                            micDialog.cancel();
                                        }
                                    }
                                });
                            }
                            if (getResources().getString(R.string.send_message).equals(content)) {
                                //点击发送消息，此处弹出键盘发消息
                                sendMessage(micDialog);
                            }
                            if (getResources().getString(R.string.send_gift_item).equals(content)) {
                                //点击送礼，此处弹出送礼的弹窗
                                ThreadManager.getInstance().runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        GiftDialogFactory giftDialogFactory = new GiftDialogFactory();
                                        giftDialogFactory.buildDialog(requireActivity(), micUserName).show();
                                        micDialog.cancel();
                                        giftDialogFactory.setCallSendGiftMessage(new GiftDialogFactory.CallSendGiftMessage() {
                                            @Override
                                            public void callMessage(Message message) {
                                                //把消息添加进集合展示
                                                roomChatMessageListAdapter.addMessages(message);
                                                fragmentChatRoomBinding.chatroomListChat.smoothScrollToPosition(roomChatMessageListAdapter.getCount());
                                                fragmentChatRoomBinding.chatroomListChat.setSelection(roomChatMessageListAdapter.getCount());
                                            }
                                        });
                                    }
                                });
                            }
                            if (getResources().getString(R.string.go_out_room).equals(content)) {
                                //点击移除房间
                                String userId = micBean.getUserId();
                                List<String> userIds = new ArrayList<>();
                                userIds.add(userId);
                                final NetStateLiveData<NetResult<Void>> result = roomMemberViewModel.kickMember(userIds);
                                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer integer) {
                                        if (result.isSuccess()) {
                                            ToastUtil.showToast("移除房间");
                                            micDialog.cancel();
                                        }
                                    }
                                });
                            }
                        }
                    });
                    micDialog.show();
                }
            });
        }

    }

    /**
     * 用户角色为观众时，空麦位时对应的操作
     */
    public void micAbsentAudience(final MicBean micBean) {
        if (micBean.getPosition() == 0) {
            //作为观众，面对主持人空麦位的情况下，弹出接管主持人的弹窗
            ThreadManager.getInstance().runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    final MicConnectTakeOverDialogFactory micConnectTakeOverDialogFactory = new MicConnectTakeOverDialogFactory();
                    micConnectTakeOverDialogFactory.setShowMessageButton(false);
                    final Dialog dialog = micConnectTakeOverDialogFactory.buildDialog(requireActivity());
                    micConnectTakeOverDialogFactory.setCurrentType(false);
                    micConnectTakeOverDialogFactory.setUserName(getResources().getString(R.string.host_location));
                    micConnectTakeOverDialogFactory.setOnDialogButtonListClickListener(new OnDialogButtonListClickListener() {
                        @Override
                        public void onClick(String content) {
                            if (getResources().getString(R.string.send_message).equals(content)) {
                                //点击发消息
                                //此处弹窗不显示发送消息按钮，自然不用处理
                            }
                            if (getResources().getString(R.string.take_over_host).equals(content)) {
                                //接管主持
                                NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micTakeOverHost();
                                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer integer) {
                                        ToastUtil.showToast("接管主持");
                                        dialog.cancel();
                                    }
                                });
                            }
                        }
                    });
                    dialog.show();
                }
            });
        } else {
            //作为观众，面对其他主播空麦位的情况下，弹出排麦的弹窗
            ThreadManager.getInstance().runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    MicEnqueueDialogFactory micEnqueueDialogFactory = new MicEnqueueDialogFactory();
                    final Dialog dialog = micEnqueueDialogFactory.buildDialog(requireActivity(), micBean);
                    micEnqueueDialogFactory.setCallClick(new MicEnqueueDialogFactory.CallClick() {
                        @Override
                        public void onClick(String content) {
                            if (ButtonDelayUtil.isNormalClick()) {
//                                SLog.i("asdff", content);
                                if (getResources().getString(R.string.enqueue_mic).equals(content)) {
                                    //麦位状态如果为正常，请求排麦
                                    if (micBean.getState() == MicState.NORMAL.getState() || micBean.getState() == MicState.CLOSE.getState()) {
                                        //判断当前是否在排麦列表
                                        for (int i = 0; i < localMicBeanMap.size(); i++) {
                                            if (localMicBeanMap.get(i).getUserId().equals(CacheManager.getInstance().getUserId())) {
//                                                ToastUtil.showToast(getResources().getString(R.string.already_at_miclilst));
                                                dialog.cancel();
                                                return;
                                            }
                                        }
                                        //调用请求排麦接口
                                        final NetStateLiveData<NetResult<Void>> result = roomMemberViewModel.micApply();
                                        result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                            @Override
                                            public void onChanged(Integer integer) {
                                                if (result.isSuccess()) {
//                                                    ToastUtil.showToast("观众申请排麦");
                                                    dialog.cancel();
                                                }
                                            }
                                        });
                                    } else {
                                        ToastUtil.showToast(getResources().getString(R.string.already_lock_mic));
                                        dialog.cancel();
                                    }
                                }
                            }
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                }
            });
        }
    }

    /**
     * 用户角色为观众时，麦位上有人对应的操作
     */
    public void micPresentAudience(final MicBean micBean) {
        ThreadManager.getInstance().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                final MicAudienceFactory micAudienceFactory = new MicAudienceFactory();
                final Dialog dialog = micAudienceFactory.buildDialog(requireActivity());
                if (micBean.getPosition() == 0) {
                    micAudienceFactory.setMicPosition("主持人");
                } else {
                    micAudienceFactory.setMicPosition(micBean.getPosition() + "号麦");
                }
                List<String> ids = new ArrayList<>();
                ids.add(micBean.getUserId());
                chatRoomViewModel.userBatch(ids);
                chatRoomViewModel.getUserinfolistRepoLiveData().observe(getViewLifecycleOwner(), new Observer<NetResult<List<RoomMemberRepo.MemberBean>>>() {
                    @Override
                    public void onChanged(NetResult<List<RoomMemberRepo.MemberBean>> listNetResult) {
                        if (listNetResult.getData() == null || listNetResult.getData().size() <= 0) {
                            return;
                        }
                        RoomMemberRepo.MemberBean memberBean = listNetResult.getData().get(0);
                        name = memberBean.getUserName();
                        micUserName = memberBean.getUserName();
                        micAudienceFactory.setUserName(memberBean.getUserName());
                        micAudienceFactory.setPortrait(memberBean.getPortrait());

                    }
                });
                micAudienceFactory.setOnDialogButtonListClickListener(new OnDialogButtonListClickListener() {
                    @Override
                    public void onClick(String content) {
                        if (getResources().getString(R.string.send_message).equals(content)) {
                            //发消息
                            sendMessage(dialog);
                        }

                        if (getResources().getString(R.string.send_gift_item).equals(content)) {
                            //送礼
                            ThreadManager.getInstance().runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    GiftDialogFactory giftDialogFactory = new GiftDialogFactory();
                                    giftDialogFactory.buildDialog(requireActivity(), micUserName).show();
                                    dialog.cancel();
                                    giftDialogFactory.setCallSendGiftMessage(new GiftDialogFactory.CallSendGiftMessage() {
                                        @Override
                                        public void callMessage(Message message) {
                                            roomChatMessageListAdapter.addMessages(message);
                                            fragmentChatRoomBinding.chatroomListChat.smoothScrollToPosition(roomChatMessageListAdapter.getCount());
                                            fragmentChatRoomBinding.chatroomListChat.setSelection(roomChatMessageListAdapter.getCount());
                                        }
                                    });
                                }
                            });
                        }
//                        if (getResources().getString(R.string.mic_apply).equals(content)) {
//                            //申请排麦
//                            if (micBean.getState() == MicState.NORMAL.getState() || micBean.getState() == MicState.LOCK.getState()) {
//                                final NetStateLiveData<NetResult<Void>> result = roomMemberViewModel.micApply();
//                                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
//                                    @Override
//                                    public void onChanged(Integer integer) {
//                                        if (result.isSuccess()) {
//                                            ToastUtil.showToast("观众申请排麦");
//                                            dialog.cancel();
//                                        }
//                                    }
//                                });
//                            }
//                        }
                    }
                });
                dialog.show();
            }
        });
    }

    private void sendMessage(Dialog dialog) {
        dialog.cancel();
        clickProxy.popupEditText();
        String str = "@" + name + " ";
        fragmentChatRoomBinding.rcExtension.getInputEditText().setText(str);
        fragmentChatRoomBinding.rcExtension.getInputEditText().setSelection(str.length());
        fragmentChatRoomBinding.rcExtension.showSoftInput();
    }

    /**
     * 用户角色为连麦者时，麦位上有人对应的操作
     */
    public void micPresentConnect(final MicBean micBean) {
        if (micBean.getPosition() == 0 && !"".equals(micBean.getUserId())) {
            //当点击的是主持人时，弹出接管主持人dialog
            ThreadManager.getInstance().runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    final MicConnectTakeOverDialogFactory micConnectTakeOverDialogFactory = new MicConnectTakeOverDialogFactory();
                    final Dialog dialog = micConnectTakeOverDialogFactory.buildDialog(requireActivity());
                    micConnectTakeOverDialogFactory.setCurrentType(true);
                    micConnectTakeOverDialogFactory.setOnDialogButtonListClickListener(new OnDialogButtonListClickListener() {
                        @Override
                        public void onClick(String content) {
                            if (getResources().getString(R.string.send_message).equals(content)) {
                                //点击发消息
                                sendMessage(dialog);
                            }
                            if (getResources().getString(R.string.take_over_host).equals(content)) {
                                //接管主持
                                NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micTakeOverHost();
                                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer integer) {
                                        ToastUtil.showToast("接管主持");
                                        dialog.cancel();
                                    }
                                });
                            }
                        }
                    });
                    List<String> ids = new ArrayList<>();
                    ids.add(micBean.getUserId());
                    chatRoomViewModel.userBatch(ids);
                    chatRoomViewModel.getUserinfolistRepoLiveData().observe(getViewLifecycleOwner(), new Observer<NetResult<List<RoomMemberRepo.MemberBean>>>() {
                        @Override
                        public void onChanged(NetResult<List<RoomMemberRepo.MemberBean>> listNetResult) {
                            RoomMemberRepo.MemberBean memberBean = listNetResult.getData().get(0);
                            name = memberBean.getUserName();
                            micConnectTakeOverDialogFactory.setPortrait(memberBean.getPortrait());
                        }
                    });
                    micConnectTakeOverDialogFactory.setUserName("主持人麦位");
                    dialog.show();

                }
            });
        }
        if (micBean.getPosition() != 0 && micBean.getUserId().equals(CacheManager.getInstance().getUserId())) {
            //当连麦者点击的是自己时，弹出下麦的dialog
            ThreadManager.getInstance().runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    final MicConnectDialogFactory micConnectFactory = new MicConnectDialogFactory();
                    final Dialog dialog = micConnectFactory.buildDialog(requireActivity());
                    micConnectFactory.setCurrentUser(false);
                    micConnectFactory.setMicPosition(micBean.getPosition() + "号麦");
                    micConnectFactory.setOnDialogButtonListClickListener(new OnDialogButtonListClickListener() {
                        @Override
                        public void onClick(String content) {
                            NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micQuit();
                            result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                @Override
                                public void onChanged(Integer integer) {
                                    ToastUtil.showToast("连麦者下麦");
                                    dialog.cancel();
                                }
                            });
                        }
                    });
                    List<String> ids = new ArrayList<>();
                    ids.add(micBean.getUserId());
                    chatRoomViewModel.userBatch(ids);
                    chatRoomViewModel.getUserinfolistRepoLiveData().observe(getViewLifecycleOwner(), new Observer<NetResult<List<RoomMemberRepo.MemberBean>>>() {
                        @Override
                        public void onChanged(NetResult<List<RoomMemberRepo.MemberBean>> listNetResult) {
                            RoomMemberRepo.MemberBean memberBean = listNetResult.getData().get(0);
                            micConnectFactory.setUserName(memberBean.getUserName());
                            micConnectFactory.setPortrait(memberBean.getPortrait());
                        }
                    });
                    dialog.show();
                }
            });
        }
        if (micBean.getPosition() != 0 && !micBean.getUserId().equals(CacheManager.getInstance().getUserId())) {
            //当点击的不是自己而是别人时，弹出对应的人的用户资料卡
            micPresentAudience(micBean);
        }
    }

    public void micAbsentConnect(final MicBean micBean) {
        //当其他连麦者面对主持人麦位为空的情况下，弹出接管主持的弹窗
        if (micBean.getPosition() == 0) {
            ThreadManager.getInstance().runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    final MicConnectTakeOverDialogFactory micConnectTakeOverDialogFactory = new MicConnectTakeOverDialogFactory();
                    micConnectTakeOverDialogFactory.setShowMessageButton(false);
                    final Dialog dialog = micConnectTakeOverDialogFactory.buildDialog(requireActivity());
                    micConnectTakeOverDialogFactory.setCurrentType(false);
                    micConnectTakeOverDialogFactory.setOnDialogButtonListClickListener(new OnDialogButtonListClickListener() {
                        @Override
                        public void onClick(String content) {
                            if (getResources().getString(R.string.send_message).equals(content)) {
                                //点击发消息
                                //此处弹窗不显示发送消息按钮，自然不用处理
                            }
                            if (getResources().getString(R.string.take_over_host).equals(content)) {
                                //接管主持
                                NetStateLiveData<NetResult<Void>> result = chatRoomViewModel.micTakeOverHost();
                                result.getNetStateMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer integer) {
                                        ToastUtil.showToast("接管主持");
                                        dialog.cancel();
                                    }
                                });
                            }
                        }
                    });
                    micConnectTakeOverDialogFactory.setUserName("主持人麦位");
                    dialog.show();
                }
            });
        }
    }

    public class ClickProxy {

        public void micManager() {
            clickMic(0);
        }

        public void mic1() {
            clickMic(1);
        }

        public void mic2() {
            clickMic(2);
        }

        public void mic3() {
            clickMic(3);
        }

        public void mic4() {
            clickMic(4);
        }

        public void mic5() {
            clickMic(5);
        }

        public void mic6() {
            clickMic(6);
        }

        public void mic7() {
            clickMic(7);
        }

        public void mic8() {
            clickMic(8);
        }


        public void popupEditText() {
            isShowKey = true;
            fragmentChatRoomBinding.rcExtension.setVisibility(View.VISIBLE);
            fragmentChatRoomBinding.rcExtension.showSoftInput();
            fragmentChatRoomBinding.chatroomFunction.setVisibility(View.GONE);
        }

        public void hide() {
            isShowKey = false;
            fragmentChatRoomBinding.rcExtension.setVisibility(View.GONE);
            fragmentChatRoomBinding.rcExtension.collapseExtension();
            fragmentChatRoomBinding.chatroomFunction.setVisibility(View.VISIBLE);
        }

        public void showRoomMemberManagerDialog() {
            if (ButtonDelayUtil.isNormalClick()) {
                new RoomMemberManagerDialogFactory().buildDialog(requireActivity(), RoomMemberStatus.ENQUEUE_MIC.getStatus()).show();
            }
        }

        public void showRoomSettingDialog() {
            //点击设置按钮时设置为true然后去请求房间详情接口
            isAlertSettingDialog = true;
            chatRoomViewModel.roomDetail(roomId);
        }

        private void alertDialog() {
            //弹出后设置为false
            isAlertSettingDialog = false;
            final RoomSettingDialogFactory roomSettingDialogFactory = new RoomSettingDialogFactory();
            roomSettingDialogFactory.setOnRoomSettingDialogAction(new RoomSettingDialogFactory.OnRoomSettingDialogAction() {
                @Override
                public void audienceJoin(boolean isChecked) {
                    isAudienceJoin = isChecked;
                    //只有主持人才有权力设置是否允许加入和是否允许自由上麦
                    if (UserRoleType.HOST.isHost(CacheManager.getInstance().getUserRoleType())) {
                        chatRoomViewModel.roomSetting(SealMicApp.getApplication(), roomId, isChecked, isAudienceFreeMic);
                    } else {
                        ToastUtil.showToast(getResources().getString(R.string.no_permission_update));
                    }

                }

                @Override
                public void audienceFreeMic(boolean isChecked) {
                    isAudienceFreeMic = isChecked;
                    //只有主持人才有权力设置是否允许加入和是否允许自由上麦
                    if (UserRoleType.HOST.isHost(CacheManager.getInstance().getUserRoleType())) {
                        chatRoomViewModel.roomSetting(SealMicApp.getApplication(), roomId, isAudienceJoin, isChecked);
                    } else {
                        ToastUtil.showToast(getResources().getString(R.string.no_permission_update));
                    }
                }

                @Override
                public void useTelephoneReceiver(boolean isChecked) {
                    ToastUtil.showToast(getResources().getString(R.string.room_setting_success));
                    //设置扬声器播放状态
                    RTCClient.getInstance().setSpeakerEnable(!isChecked);
                    //改变图标
                    fragmentChatRoomBinding.chatroomVoiceOut.setSelected(isChecked);
                }

                @Override
                public void openDebug(boolean isChecked) {
                    if (isChecked) {
                        fragmentChatRoomBinding.debugLayout.debugInfo.setVisibility(View.VISIBLE);
                    } else {
                        fragmentChatRoomBinding.debugLayout.debugInfo.setVisibility(View.GONE);
                    }
                    CacheManager.getInstance().cacheIsOpenDebug(isChecked);
                }
            });
            Dialog dialog = roomSettingDialogFactory.buildDialog(requireActivity());
            dialog.show();
        }

        public void showChangeAudioDialog() {
            new ChangeBaseAudioDialogFactory().buildDialog(requireActivity()).show();
        }

        public void showBgAudioDialog() {
            new BgBaseAudioDialogFactory().buildDialog(requireActivity()).show();
        }

        public void showRoomNoticeDialog() {
            new RoomNoticeDialogFactory().buildDialog(requireActivity()).show();
        }

        public void showGiftDialog() {
            GiftDialogFactory giftDialogFactory = new GiftDialogFactory();
            giftDialogFactory.buildDialog(requireActivity(), "")
                    .show();
            giftDialogFactory.setCallSendGiftMessage(new GiftDialogFactory.CallSendGiftMessage() {
                @Override
                public void callMessage(Message message) {
                    roomChatMessageListAdapter.addMessages(message);
                    fragmentChatRoomBinding.chatroomListChat.smoothScrollToPosition(roomChatMessageListAdapter.getCount());
                    fragmentChatRoomBinding.chatroomListChat.setSelection(roomChatMessageListAdapter.getCount());

                }
            });
        }

    }
}
