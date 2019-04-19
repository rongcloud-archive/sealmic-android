package cn.rongcloud.sealmic.ui;

import android.bluetooth.BluetoothHeadset;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmic.R;
import cn.rongcloud.sealmic.SealMicApp;
import cn.rongcloud.sealmic.constant.ErrorCode;
import cn.rongcloud.sealmic.constant.IntentExtra;
import cn.rongcloud.sealmic.im.IMClient;
import cn.rongcloud.sealmic.model.DetailRoomInfo;
import cn.rongcloud.sealmic.model.MicBehaviorType;
import cn.rongcloud.sealmic.model.RoomMicPositionInfo;
import cn.rongcloud.sealmic.model.UserInfo;
import cn.rongcloud.sealmic.task.AuthManager;
import cn.rongcloud.sealmic.task.ResultCallback;
import cn.rongcloud.sealmic.task.RoomEventListener;
import cn.rongcloud.sealmic.task.RoomManager;
import cn.rongcloud.sealmic.task.role.Role;
import cn.rongcloud.sealmic.ui.adapter.RoomChatListAdapter;
import cn.rongcloud.sealmic.ui.widget.BottomSelectDialog;
import cn.rongcloud.sealmic.ui.widget.InviteAudienceDialog;
import cn.rongcloud.sealmic.ui.widget.MicSeatView;
import cn.rongcloud.sealmic.ui.widget.RoomManagerRippleView;
import cn.rongcloud.sealmic.utils.DisplayUtils;
import cn.rongcloud.sealmic.utils.HeadsetPlugReceiver;
import cn.rongcloud.sealmic.utils.HeadsetUtils;
import cn.rongcloud.sealmic.utils.ResourceUtils;
import cn.rongcloud.sealmic.utils.ToastUtils;
import io.rong.imlib.model.Message;

public class ChatRoomActivity extends BaseActivity implements RoomEventListener, View.OnClickListener {
    private static final String TAG = "ChatRoomActivity";
    private RelativeLayout roomLayout;
    private TextView titleTv;
    private ImageView backIv;
    private ImageView settingIv;

    private ImageView roomManagerAvatarIv;
    private TextView roomManagerNickNameTv;

    private List<MicSeatView> micSeatViewList;

    private ListView chatLv;
    private RoomChatListAdapter chatListAdapter;

    private LinearLayout inputContainer;
    private EditText inputMessageEt;
    private ImageView micControlIv;
    private ImageView soundControlIv;

    private int defaultMarginBottom;
    private DetailRoomInfo detailRoomInfo;
    private RoomManager roomManager;
    private RoomManagerRippleView roomManagerRippleView;
    private List<Message> chatMessageList = new ArrayList<>();
    private boolean isMuteMic = false;         //麦克风是否静音
    private InviteAudienceDialog inviteAudienceDialog;
    private AudioManager audioManager;
    private TelephonyManager telephonyManager;
    private RoomPhoneStateListener roomPhoneStateListener;
    private HeadsetPlugReceiver headsetPlugReceiver;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        handler = new Handler();

        initView();
        initRoom();
        initAudioOutputMode();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        //房间整体布局
        roomLayout = findViewById(R.id.chatroom_layout);
        // 标题栏
        backIv = findViewById(R.id.chatroom_title_view_back);
        titleTv = findViewById(R.id.chatroom_title_tv_title);
        settingIv = findViewById(R.id.chatroom_title_view_setting);

        // 房主
        roomManagerNickNameTv = findViewById(R.id.chatroom_tv_room_manager_nickname);
        roomManagerAvatarIv = findViewById(R.id.chatroom_iv_room_manager_avatar);
        roomManagerRippleView = findViewById(R.id.chatroom_rv_room_manager_ripple);

        // 聊天列表
        chatLv = findViewById(R.id.chatroom_list_chat);

        // 输入栏
        inputContainer = findViewById(R.id.chatroom_ll_input_container);
        inputMessageEt = findViewById(R.id.chatroom_et_chat_input);
        micControlIv = findViewById(R.id.chatroom_iv_mic_control);
        soundControlIv = findViewById(R.id.chatroom_iv_sound_control);

        //聊天列表
        chatListAdapter = new RoomChatListAdapter(this);
        chatLv.setAdapter(chatListAdapter);

        //麦位
        setMicSeaViewList();

        backIv.setOnClickListener(this);
        settingIv.setOnClickListener(this);
        micControlIv.setOnClickListener(this);
        soundControlIv.setOnClickListener(this);

        // 默认麦克不可用
        enableUseMic(false);

        //监听软件发送按钮，点击发送时发送消息
        inputMessageEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND && event != null ? event.getAction() == KeyEvent.ACTION_DOWN : true) {
                    String msg = inputMessageEt.getText().toString();
                    if (!TextUtils.isEmpty(msg)) {
                        roomManager.sendChatRoomMessage(msg);
                    }
                    hideInputKeyboard();
                    inputMessageEt.setText("");
                    return true;
                }
                return false;
            }
        });

        //保存底部边距，用于软件收起后位置还原
        defaultMarginBottom = DisplayUtils.dp2px(this, 8);

        // 沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarTransparent();

            View viewById = findViewById(R.id.chatroom_title_rl_titlebar);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewById.getLayoutParams();
            layoutParams.topMargin = DisplayUtils.getStatusBarHeight(this);
        }

        //启用软件弹出监听
        enableKeyboardStateListener(true);
    }

    private void setMicSeaViewList() {
        micSeatViewList = new ArrayList<>();
        MicSeatView micSeatView;
        int[] micIds = {R.id.chatroom_mp_mic_1, R.id.chatroom_mp_mic_2, R.id.chatroom_mp_mic_3, R.id.chatroom_mp_mic_4,
                R.id.chatroom_mp_mic_5, R.id.chatroom_mp_mic_6, R.id.chatroom_mp_mic_7, R.id.chatroom_mp_mic_8};

        for (int i = 0; i < micIds.length; i++) {
            micSeatView = findViewById(micIds[i]);
            micSeatViewList.add(micSeatView);
        }

        int size = micSeatViewList.size();
        for (int i = 0; i < size; i++) {
            micSeatView = micSeatViewList.get(i);
            micSeatView.init(i);
            micSeatView.setOnImageClickListener(new MicSeatView.OnImageClickListener() {
                @Override
                public void onImageClick(View view, int position) {
                    doActionToMicSeat(((micSeatViewList.get(position)).getMicInfo()));
                }
            });
        }
    }

    /**
     * 初始化聊天室信息
     */
    private void initRoom() {
        roomManager = RoomManager.getInstance();
        detailRoomInfo = roomManager.getCurrentRoomInfo();
        if (detailRoomInfo == null) {
            finish();
            return;
        }
        roomManager.setCurrentRoomEventListener(this);

        //设置房间背景
        updateRoomBg(detailRoomInfo.getBgId());

        //获取进入房间前的消息
        List<Message> messageList = detailRoomInfo.getMessageList();

        //加入一条自己进入房间的消息
        Message localEnterMsg = IMClient.getInstance().createLocalEnterRoomMessage(AuthManager.getInstance().getCurrentUserId(), detailRoomInfo.getRoomId());
        messageList.add(localEnterMsg);

        //设置聊天信息
        chatMessageList.addAll(messageList);
        chatListAdapter.setMessages(chatMessageList);
        chatListAdapter.notifyDataSetChanged();
        chatLv.setSelection(chatListAdapter.getCount());

        //设置标题
        updateRoomTitle(detailRoomInfo.getSubject(), detailRoomInfo.getMemCount());

        //设置房主信息
        roomManagerNickNameTv.setText(ResourceUtils.getInstance().getUserName(detailRoomInfo.getCreatorUserId()));
        roomManagerAvatarIv.setImageDrawable(getResources().getDrawable(ResourceUtils.getInstance().getUserAvatarResourceId(detailRoomInfo.getCreatorUserId())));

        //更新麦位状态
        updateMicSeatState(detailRoomInfo.getMicPositions());

        final Role role = roomManager.getCurrentRole();

        // 判断是否可以设置房间
        if (role.hasRoomSettingPermission()) {
            settingIv.setVisibility(View.VISIBLE);
        } else {
            settingIv.setVisibility(View.GONE);
        }

        // 初始化语音
        roomManager.initRoomVoice(new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result && role.hasVoiceChatPermission()) {
                    // 当房主时直接开启语音聊天
                    enableVoiceChat(true);
                }

                // 开启房间语音声音
                enableRoomChatVoice(true);

                // 监听来电话状态
                roomPhoneStateListener = new RoomPhoneStateListener();
                telephonyManager.listen(roomPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }

            @Override
            public void onFail(int errorCode) {
                ToastUtils.showErrorToast(errorCode);
            }
        });
    }

    /**
     * 初始化音频播放模式
     */
    private void initAudioOutputMode() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        // 监听有线耳机连接和断开广播监听
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        headsetPlugReceiver = new HeadsetPlugReceiver();
        HeadsetPlugReceiver.setOnHeadsetPlugListener(new HeadsetPlugReceiver.OnHeadsetPlugListener() {
            @Override
            public void onNotifyHeadsetState(boolean connected, int type) {
                if (connected) {
                    // 蓝牙耳机连接时,且有线耳机没有连接时启用蓝牙耳机通讯
                    if (type == 0
                            && !HeadsetUtils.isWiredHeadsetOn(ChatRoomActivity.this)) { //蓝牙耳机连接时
                        // 部分蓝牙设备连接后需要一定延迟后才能保证连接
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                audioManager.setBluetoothScoOn(true);
                                audioManager.startBluetoothSco();
                                audioManager.setSpeakerphoneOn(false);
                            }
                        }, 2000);

                    } else if (type == 1) {
                        // 有线耳机连接时关闭蓝牙耳机语音通讯
                        audioManager.setBluetoothScoOn(false);
                        audioManager.stopBluetoothSco();
                        audioManager.setSpeakerphoneOn(false);
                    }
                } else {
                    if (type == 0) { //蓝牙耳机断开时
                        // 关闭蓝牙耳机语音通讯
                        audioManager.setBluetoothScoOn(false);
                        audioManager.stopBluetoothSco();

                    } else if (type == 1 && HeadsetUtils.hasBluetoothHeadSetConnected()) {
                        // 当有线耳机拔出，蓝牙耳机还在连接时，开启蓝牙耳机通信
                        audioManager.setBluetoothScoOn(true);
                        audioManager.startBluetoothSco();
                    }

                    // 判断是否当有线耳机拔出或蓝牙耳机断开时，另一个耳机存在，如果存在则不改变输出模式
                    if (HeadsetUtils.isWiredHeadsetOn(ChatRoomActivity.this)
                            || HeadsetUtils.hasBluetoothHeadSetConnected()) {
                        audioManager.setSpeakerphoneOn(false);
                    } else {
                        // 当没有耳机连接时，使用外放模式
                        audioManager.setSpeakerphoneOn(true);
                    }
                }
            }
        });
        registerReceiver(headsetPlugReceiver, intentFilter);
    }

    /**
     * 改变音频输出模式
     */
    private void changeAudioOutputMode() {
        if (audioManager == null) return;

        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        //检测是否有有线耳机或蓝牙耳机连接
        if (HeadsetUtils.hasBluetoothHeadSetConnected()) {
            audioManager.startBluetoothSco();
            audioManager.setBluetoothScoOn(true);
            audioManager.setSpeakerphoneOn(false);
        } else if (HeadsetUtils.isWiredHeadsetOn(this)) {
            audioManager.setSpeakerphoneOn(false);
        } else {
            // 设置为外放模式
            audioManager.setSpeakerphoneOn(true);
        }
    }

    /**
     * 更新房间信息
     */
    private void updateRoomInfo() {
        roomManager.getRoomDetailInfo(detailRoomInfo.getRoomId(), new ResultCallback<DetailRoomInfo>() {
            @Override
            public void onSuccess(DetailRoomInfo roomDetailInfo) {
                if (roomDetailInfo != null) {
                    detailRoomInfo.setMemCount(roomDetailInfo.getMemCount());
                    updateMicSeatState(roomDetailInfo.getMicPositions());
                    updateRoomTitle(roomDetailInfo.getSubject(), roomDetailInfo.getMemCount());
                }
            }

            @Override
            public void onFail(int errorCode) {
                ToastUtils.showErrorToast(errorCode);
            }
        });
    }

    /**
     * 更新房间标题
     *
     * @param subject
     * @param memberCount
     */
    private void updateRoomTitle(String subject, int memberCount) {
        titleTv.setText(getString(R.string.room_title_format, subject, memberCount));
    }

    /**
     * 更新房间背景
     *
     * @param bgId
     */
    private void updateRoomBg(int bgId) {
        roomLayout.setBackground(getResources().getDrawable(ResourceUtils.getInstance().getRoomBackgroundImageId(bgId)));
    }

    /**
     * 设置启动房间聊天声音
     *
     * @param enable
     */
    private void enableRoomChatVoice(boolean enable) {
        roomManager.enableRoomChatVoice(enable, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onFail(int errorCode) {
                ToastUtils.showErrorToast(errorCode);
            }
        });
    }

    /**
     * 设置是否开启语音聊天
     *
     * @param enable
     */
    private void enableVoiceChat(boolean enable) {
        if (enable) {
            roomManager.startVoiceChat(new ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    enableUseMic(true);
                }

                @Override
                public void onFail(int errorCode) {
                    ToastUtils.showErrorToast(errorCode);
                }
            });
        } else {
            roomManager.stopVoiceChat(new ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    enableUseMic(false);
                }

                @Override
                public void onFail(int errorCode) {
                    ToastUtils.showErrorToast(errorCode);
                }
            });
        }
    }

    /**
     * 切换房间声音
     */
    private void enableRoomSound(boolean enable) {
        if (enable) {
            //启用房间声音
            roomManager.enableRoomChatVoice(true, new ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    // 每次启用声音时做音频输出切换
                    changeAudioOutputMode();
                    soundControlIv.setImageDrawable(getResources().getDrawable(R.drawable.chatroom_ic_volume));
                }

                @Override
                public void onFail(int errorCode) {
                    ToastUtils.showErrorToast(errorCode);
                }
            });
        } else {
            // 关闭房间声音
            roomManager.enableRoomChatVoice(false, new ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    soundControlIv.setImageDrawable(getResources().getDrawable(R.drawable.chatroom_ic_volume_mute));
                }

                @Override
                public void onFail(int errorCode) {
                    ToastUtils.showErrorToast(errorCode);
                }
            });
        }
    }

    /**
     * 切换当前的麦克状态
     */
    private void setMuteMic(boolean isMute) {
        if (isMute) {
            // 关闭麦克
            RoomManager.getInstance().enableMic(false);
            micControlIv.setImageDrawable(getResources().getDrawable(R.drawable.chatroom_ic_mic_mute));
            isMuteMic = true;
        } else {
            // 启用麦克
            RoomManager.getInstance().enableMic(true);
            micControlIv.setImageDrawable(getResources().getDrawable(R.drawable.chatroom_ic_mic_enable));
            isMuteMic = false;
        }
    }

    /**
     * 是否可使用麦克
     */
    private void enableUseMic(boolean isMicEnable) {
        micControlIv.setEnabled(isMicEnable);
        if (isMicEnable) {
            setMuteMic(isMuteMic);
        } else {
            micControlIv.setImageDrawable(getResources().getDrawable(R.drawable.chatroom_ic_mic_disable));
        }
    }

    /**
     * 退出房间
     */
    private void quiteRoom() {
        Role currentRole = roomManager.getCurrentRole();
        if (currentRole != null) {
            roomManager.getCurrentRole().leaveRoom(null);
        }
        finish();
    }

    /**
     * 销毁房间
     */
    private void destroyRoom() {
        final String roomId = detailRoomInfo.getRoomId();
        roomManager.leaveRoom(new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                roomManager.destroyRoom(roomId, null);
            }

            @Override
            public void onFail(int errorCode) {
            }
        });

        finish();
    }

    /**
     * 显示房间设置
     */
    private void showSetting() {
        Intent intent = new Intent(this, ChatRoomSettingWindow.class);
        intent.putExtra(IntentExtra.ROOM_ID, detailRoomInfo.getRoomId());
        startActivityForResult(intent, 0);
    }


    /**
     * 显示抱麦对话框，选择听众上麦
     */
    private void showPickListenerToMicDialog(final int targetMicPosition) {
        inviteAudienceDialog = new InviteAudienceDialog.Builder()
                .setCancelable(true)
                .setRoomId(detailRoomInfo.getRoomId())
                .setOnAudienceItemClickListener(new InviteAudienceDialog.OnAudienceItemClickListener() {
                    @Override
                    public void onItemClick(UserInfo info) {
                        RoomManager.getInstance().controlMicPosition(targetMicPosition
                                , info.getUserId()
                                , MicBehaviorType.PickupMic.ordinal()
                                , new ResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        if (inviteAudienceDialog != null) {
                                            inviteAudienceDialog.dismiss();
                                        }
                                        updateRoomInfo();
                                    }

                                    @Override
                                    public void onFail(int errorCode) {
                                        ToastUtils.showErrorToast(errorCode);
                                    }
                                });
                    }
                }).build();
        inviteAudienceDialog.show(getSupportFragmentManager(), "InviteAudienceDialog");
    }

    /**
     * 更新麦位状态
     *
     * @param micInfoList
     */
    private void updateMicSeatState(List<RoomMicPositionInfo> micInfoList) {
        if (micInfoList == null) return;

        clearMicState();

        int seatSize = micSeatViewList.size();
        for (RoomMicPositionInfo micInfo : micInfoList) {
            int position = micInfo.getPosition();
            if (position < seatSize) {
                MicSeatView micSeatView = micSeatViewList.get(position);
                micSeatView.updateMicState(micInfo);
            }
        }
    }

    /**
     * 更新麦位说话状态
     *
     * @param speakUserList 当前正在发言的用户 id 列表
     */
    private void updateMicSpeakState(List<String> speakUserList) {
        if (speakUserList == null || speakUserList.size() == 0) return;

        // 更新房主的说话状态
        String creatorUserId = detailRoomInfo.getCreatorUserId();
        if (speakUserList.contains(creatorUserId)) {
            roomManagerRippleView.enableRipple(true);
        }

        // 更新麦位上的说话状态
        for (MicSeatView micSeatView : micSeatViewList) {
            String userId = micSeatView.getMicInfo().getUserId();
            if (userId != null && speakUserList.contains(userId)) {
                micSeatView.startRipple();
            }
        }
    }

    /**
     * 清除麦位状态
     */
    private void clearMicState() {
        int size = micSeatViewList.size();
        for (int i = 0; i < size; i++) {
            MicSeatView micSeatView = micSeatViewList.get(i);
            micSeatView.init(i);
        }
    }

    /**
     * 操作麦位
     * 当为房主时，会根据当前麦位状态显示可操作指令的对话框，选择指令后会执行该项操作，并触发操作回调
     * 当为听众时，会根据当前自己是否在麦位上可进行上麦，下麦和跳麦的操作，
     *
     * @param targetMicPositionInfo 需要操作的麦位信息
     */
    public void doActionToMicSeat(RoomMicPositionInfo targetMicPositionInfo) {
        if (detailRoomInfo == null) {
            //未加入到房间提示
            ToastUtils.showErrorToast(ErrorCode.ROOM_NOT_JOIN_TO_ROOM);
            return;
        }

        final int micPosition = targetMicPositionInfo.getPosition();    //操作的麦位位置
        final String userId = targetMicPositionInfo.getUserId();        //操作的麦位上的用户id
        final List<MicBehaviorType> behaviorList = new ArrayList<>();   //可执行的麦位操作列表
        List<String> behaviorNameList = new ArrayList<>();              //可执行的麦位操作的名称列表

        final Role currentRole = RoomManager.getInstance().getCurrentRole();
        behaviorList.addAll(currentRole.getBehaviorList(micPosition));

        if (behaviorList.size() == 0) {
            return;

            // 当仅有一个操作时判断是否直接进行操作
        } else if (behaviorList.size() == 1) {
            MicBehaviorType behaviorType = behaviorList.get(0);
            if (behaviorType == MicBehaviorType.JumpOnMic
                    || behaviorType == MicBehaviorType.JumpToMic) {
                currentRole.perform(behaviorType, micPosition, userId, new ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        updateRoomInfo();
                    }

                    @Override
                    public void onFail(int errorCode) {
                        updateRoomInfo();
                        ToastUtils.showErrorToast(errorCode);
                    }
                });
                return;
            }
        }

        // 显示根据当前可操作行为显示对话框进行操作选择
        for (MicBehaviorType behaviorType : behaviorList) {
            behaviorNameList.add(behaviorType.getName(SealMicApp.getApplication()));
        }
        BottomSelectDialog cmdSelectDialog = new BottomSelectDialog.Builder(behaviorNameList)
                .hasCancel(true)
                .setOnItemClickListener(new BottomSelectDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int index) {
                        //根据选择的操作进行请求
                        MicBehaviorType behaviorType = behaviorList.get(index);
                        if (behaviorType == MicBehaviorType.PickupMic) {
                            showPickListenerToMicDialog(micPosition);

                            // 其他麦位控制
                        } else {
                            currentRole.perform(behaviorType, micPosition, userId, new ResultCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    updateRoomInfo();
                                }

                                @Override
                                public void onFail(int errorCode) {
                                    updateRoomInfo();
                                    ToastUtils.showErrorToast(errorCode);
                                }
                            });
                        }
                    }
                }).build();
        cmdSelectDialog.show(getSupportFragmentManager(), "bottomSelectDialog");
    }

    /**
     * 监听来电状态进行房间的静音和禁麦操作
     */
    private class RoomPhoneStateListener extends PhoneStateListener {
        private boolean preMuteMicState = true;
        private boolean preRoomVoiceState = true;
        private boolean isInitCallState = true; //用与判断注册监听后的立即回调

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            //注册监听时会立即回调状态，所以需要做判断，并不进行处理
            if (isInitCallState) {
                isInitCallState = false;
                return;
            }

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING: // 响铃
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: // 接听
                    // 记录接听电话前静音和房间声音的状态
                    preMuteMicState = isMuteMic;
                    preRoomVoiceState = roomManager.isCurrentRoomVoiceEnable();
                    // 静音并关闭房间内的声音
                    setMuteMic(true);
                    enableRoomSound(false);
                    break;
                case TelephonyManager.CALL_STATE_IDLE: // 挂断
                    // 还原回接听电话前静音及房间声音的状态
                    setMuteMic(preMuteMicState);
                    enableRoomSound(preRoomVoiceState);
                    break;
            }
        }
    }

    @Override
    public void onRoomMemberChange(int memberCount) {
        updateRoomTitle(detailRoomInfo.getSubject(), memberCount);
    }

    @Override
    public void onMicUpdate(List<RoomMicPositionInfo> micPositionInfoList) {
        updateMicSeatState(micPositionInfoList);
    }

    @Override
    public void onRoomMicSpeak(List<String> speakUserIdList) {
        updateMicSpeakState(speakUserIdList);
    }

    @Override
    public void onMessageEvent(Message message) {
        if (chatListAdapter != null) {
            chatMessageList.add(message);
            chatListAdapter.notifyDataSetChanged();
            chatLv.smoothScrollToPosition(chatListAdapter.getCount());
        }
    }

    @Override
    public void onSendMessageError(Message message, int errorCode) {
        ToastUtils.showErrorToast(errorCode);
    }

    @Override
    public void onRoomBgChanged(int bgId) {
        updateRoomBg(bgId);
    }

    @Override
    public void onKickOffRoom() {
        ToastUtils.showToast(R.string.toast_chatroom_kick_off_from_room);
        quiteRoom();
    }

    @Override
    public void onRoomDestroy() {
        ToastUtils.showToast(R.string.toast_chatroom_room_manager_exit_room);
        quiteRoom();
    }

    @Override
    public void onErrorLeaveRoom() {
        ToastUtils.showToast(R.string.toast_error_leave_room_because_error);
        // 提示后延迟退出房间
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                quiteRoom();
            }
        }, 3000);
    }

    @Override
    public void onRoomExistOverTimeLimit() {
        ToastUtils.showToast(R.string.toast_room_exist_over_time_limit);
        // 提示后延迟退出房间
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                quiteRoom();
            }
        }, 3000);
    }

    @Override
    public void onRoleChanged(Role role) {
        if (role != null) {
            enableVoiceChat(role.hasVoiceChatPermission());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatroom_title_view_back:
                quiteRoom();
                break;
            case R.id.chatroom_title_view_setting:
                showSetting();
                break;
            case R.id.chatroom_iv_mic_control:
                setMuteMic(!isMuteMic);
                break;
            case R.id.chatroom_iv_sound_control:
                enableRoomSound(!roomManager.isCurrentRoomVoiceEnable());
                break;
        }
    }

    @Override
    public void onKeyboardStateChanged(boolean isShown, int height) {
        /*
         * 监听软键盘弹出，当有软键盘时使输入框加入软键盘等高的间距
         */
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) inputContainer.getLayoutParams();

        if (isShown) {
            layoutParams.bottomMargin = height + defaultMarginBottom;
        } else {
            layoutParams.bottomMargin = defaultMarginBottom;
        }
        inputContainer.setLayoutParams(layoutParams);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * 当用户切出应用时可能会有其他应用更改音频的输出模式
         * 所以当应用切回前台时切换音频输出模式
         */
        changeAudioOutputMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(headsetPlugReceiver);

        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
        }
        if (telephonyManager != null && roomPhoneStateListener != null) {
            telephonyManager.listen(roomPhoneStateListener, TelephonyManager.PHONE_TYPE_NONE);
        }
    }

    @Override
    public void onBackPressed() {
        quiteRoom();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0) {
            detailRoomInfo = roomManager.getCurrentRoomInfo();
            if (detailRoomInfo != null) {
                updateRoomBg(detailRoomInfo.getBgId());
            }

            if (data != null) {
                boolean isExitRoom = data.getBooleanExtra(IntentExtra.EXIT_ROOM, false);
                if (isExitRoom) {
                    destroyRoom();
                }
            }
        }
    }
}

