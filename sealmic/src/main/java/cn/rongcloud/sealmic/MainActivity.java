package cn.rongcloud.sealmic;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.rongcloud.sealmic.model.BaseRoomInfo;
import cn.rongcloud.sealmic.model.DetailRoomInfo;
import cn.rongcloud.sealmic.model.LoginInfo;
import cn.rongcloud.sealmic.net.model.CreateRoomResult;
import cn.rongcloud.sealmic.task.AuthManager;
import cn.rongcloud.sealmic.task.ResultCallback;
import cn.rongcloud.sealmic.task.RoomManager;
import cn.rongcloud.sealmic.ui.BaseActivity;
import cn.rongcloud.sealmic.ui.ChatRoomActivity;
import cn.rongcloud.sealmic.ui.adapter.ChatRoomRefreshAdapter;
import cn.rongcloud.sealmic.utils.ResourceUtils;
import cn.rongcloud.sealmic.utils.ToastUtils;
import cn.rongcloud.sealmic.utils.log.SLog;
import io.rong.imlib.RongIMClient;

import static cn.rongcloud.rtc.core.voiceengine.BuildInfo.MANDATORY_PERMISSIONS;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";
    /**
     * 退出应用时两次点击后退键的时间间隔
     */
    private static final long DOUBLE_BACK_KEY_TO_EXIT_INTERVAL_MILLIS = 2000;
    private ImageView imageViewCreate;
    private Dialog bottomDialog;
    private int selectedId = -1;
    private ChatRoomRefreshAdapter chatRoomRefreshAdapter;
    private RecyclerView recyclerView = null;
    private EditText editText;
    private SwipeRefreshLayout mRefreshLayout;
    private Handler handler;
    private volatile boolean isJoiningRoom = false; // 是否正有加入房间操作
    private long lastPressBackKeyMillis; // 最近一次点击后退键的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_mic_main);

        handler = new Handler();

        loginToServer();
        initView();
        checkPermissions();
    }

    /**
     * 加载聊天室数据
     */
    private void loadData() {
        RoomManager.getInstance().getChatRoomList(new ResultCallback<List<BaseRoomInfo>>() {

            public void onSuccess(List<BaseRoomInfo> baseRoomInfos) {
                chatRoomRefreshAdapter.setLoadDataList(baseRoomInfos);
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFail(int errorCode) {
                ToastUtils.showErrorToast(errorCode);
            }
        });
    }

    /**
     * 初始化view
     */
    private void initView() {
        imageViewCreate = findViewById(R.id.rc_seal_mic_create_iv);
        imageViewCreate.setOnClickListener(this);

        chatRoomRefreshAdapter = new ChatRoomRefreshAdapter(this);
        setAdapterItemClickListener();
        recyclerView = findViewById(R.id.rc_seal_mic_rv);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(chatRoomRefreshAdapter);

        imageViewCreate = findViewById(R.id.rc_seal_mic_create_iv);
        imageViewCreate.setOnClickListener(this);


        mRefreshLayout = findViewById(R.id.srl_refresh);

        // 初始画面，使用 SwipeRefreshLayout 做 Loading
        mRefreshLayout.setRefreshing(true);

        // 自定义 SwipeRefreshLayout 颜色
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_purple
        );

        // 设定下拉圆圈的背景色
        mRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);

        // 下拉刷新
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //防止第一次登录失败，加入补充登录的时机
                loginToServer();

                loadData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * 加入延时能改善刚退出房间时，房间没有完全销毁导致刷新的列表中存在即将销毁的房间
         * 点击时因房间已不存在而报错
         */
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }, 1000);

        /*
         * 当成功加入房间后，再次回到主界面时才可以再加入其他房间
         * 防止多次加入房间问题
         */
        isJoiningRoom = false;
    }

    private void loginToServer() {
        if (!TextUtils.isEmpty(AuthManager.getInstance().getCurrentUserId())) return;

        // 生成唯一随机id
        String deviceId = String.valueOf(new Random().nextLong());
        // 请求服务器获取登录 IM 所使用的 token
        AuthManager.getInstance().login(deviceId, new ResultCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                if (loginInfo != null) {
                    loginToIM(loginInfo.getImToken());
                }
            }

            @Override
            public void onFail(int errorCode) {
                ToastUtils.showToast(R.string.toast_error_login_failed);
            }
        });
    }

    private void loginToIM(String token) {
        RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                SLog.e(TAG, "RongIMClient onTokenIncorrect");
            }

            @Override
            public void onSuccess(String s) {
                SLog.d(TAG, "RongIMClient connect success");
                //ToastUtils.showToast(R.string.toast_login_success);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                SLog.e(TAG, "RongIMClient connect onError:" + errorCode.getValue() + "-" + errorCode.getMessage());
            }
        });
    }

    private void joinChatRoom(final String roomId, final boolean isCreate) {
        if (!isCreate) {
            //ToastUtils.showToast(R.string.toast_joining_room);
        }

        RoomManager.getInstance().joinRoom(roomId, new ResultCallback<DetailRoomInfo>() {
            @Override
            public void onSuccess(DetailRoomInfo detailRoomInfo) {
                if (isCreate) {
                    //ToastUtils.showToast(R.string.toast_create_room_success);
                } else {
                    //ToastUtils.showToast(R.string.toast_join_room_success);
                }
                Intent intent = new Intent(MainActivity.this, ChatRoomActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFail(int errorCode) {
                isJoiningRoom = false;

                ToastUtils.showErrorToast(errorCode);
                // 可能存在加入的房间已不存在而导致保存，此时刷新下房间列表
                loadData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rc_seal_mic_create_iv:
                if (isJoiningRoom) return;

                showBottomDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 弹出底部对话框
     */
    private void showBottomDialog() {
        bottomDialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.rc_seal_mic_dialog, null);

        editText = contentView.findViewById(R.id.room_edit_text);
        // 默认选择一条随机话题
        String randomRoomTopic = ResourceUtils.getInstance().getRandomRoomTopic();
        editText.setText(randomRoomTopic);
        editText.setSelection(randomRoomTopic.length());

        bottomDialog.setContentView(contentView);

        bottomDialogCloseClick(contentView);

        // 初始化点击事件
        int[] arrayClicker = {R.id.random, R.id.topics_1, R.id.topics_2, R.id.topics_3, R.id.topics_4, R.id.topics_5, R.id.room_create};
        for (int clickId :
                arrayClicker) {
            itemClick(contentView, clickId);
        }
        //默认选中第一项类型
        TextView topic1Tv = contentView.findViewById(R.id.topics_1);
        topic1Tv.setSelected(true);
        selectedId = R.id.topics_1;

        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = (int) getResources().getDimension(R.dimen.bottom_dialog_height);
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.show();
    }

    /**
     * 点击底部弹出框中聊天室类型Textiew处理
     *
     * @param contentView
     * @param clickId
     */
    private void itemClick(final View contentView, final int clickId) {
        final TextView clickItem = contentView.findViewById(clickId);
        clickItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.random:
                        // 设置随机房间名称
                        String randomRoomTopic = ResourceUtils.getInstance().getRandomRoomTopic();
                        editText.setText(randomRoomTopic);
                        editText.setSelection(randomRoomTopic.length());
                        break;
                    case R.id.topics_1:
                    case R.id.topics_2:
                    case R.id.topics_3:
                    case R.id.topics_4:
                    case R.id.topics_5:
                        // 刷新点击状态
                        TextView selectedItem = contentView.findViewById(selectedId);
                        if (selectedItem != null && !selectedItem.equals(clickItem)) {
                            selectedItem.setSelected(false);
                        }
                        clickItem.setSelected(true);
                        selectedId = v.getId();
                        break;
                    case R.id.room_create:
                        if (TextUtils.isEmpty(editText.getText())) {
                            ToastUtils.showToast(R.string.toast_error_invalid_room_title);
                            return;
                        }
                        String subjectStr = editText.getText().toString();
                        int type = formatType(selectedId);

                        // 判断是否有手机运行权限进行语音聊天
                        if (!checkPermissions()) return;

                        // 标记正在进入房间
                        isJoiningRoom = true;

                        //ToastUtils.showToast(R.string.toast_creating_room);
                        RoomManager.getInstance().createRoom(subjectStr, type, new ResultCallback<CreateRoomResult>() {
                            @Override
                            public void onSuccess(CreateRoomResult createRoomResult) {
                                bottomDialog.dismiss();
                                joinChatRoom(createRoomResult.getRoomId(), true);
                            }

                            @Override
                            public void onFail(int errorCode) {
                                isJoiningRoom = false;
                                ToastUtils.showErrorToast(errorCode);
                                loadData();
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 将选中的id转成对应的type
     *
     * @param selectedId
     * @return
     */
    private int formatType(int selectedId) {
        switch (selectedId) {
            case R.id.topics_1:
                return 1;
            case R.id.topics_2:
                return 2;
            case R.id.topics_3:
                return 3;
            case R.id.topics_4:
                return 4;
            case R.id.topics_5:
                return 5;
            default:
                return 0;
        }
    }

    /**
     * 点击关闭底部弹出框
     *
     * @param contentView
     */
    private void bottomDialogCloseClick(View contentView) {
        ImageView bottomDialogClose = contentView.findViewById(R.id.bottom_dialog_close);
        bottomDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomDialog != null && bottomDialog.isShowing()) {
                    bottomDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (bottomDialog != null && bottomDialog.isShowing()) {
            bottomDialog.dismiss();
            bottomDialog = null;
        }
        super.onDestroy();
    }


    private void setAdapterItemClickListener() {
        chatRoomRefreshAdapter.setOnItemClickListener(new ChatRoomRefreshAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!checkPermissions()) return;

                BaseRoomInfo roomInfo = chatRoomRefreshAdapter.getItem(position);
                if (roomInfo != null && !TextUtils.isEmpty(roomInfo.getRoomId())) {
                    // 判断是否正在进入房间，房子多次进入房间
                    if (isJoiningRoom) return;
                    isJoiningRoom = true;

                    joinChatRoom(roomInfo.getRoomId(), false);
                } else {
                    ToastUtils.showToast(R.string.chatroom_info_error);
                }
            }
        });
    }

    private boolean checkPermissions() {
        List<String> unGrantedPermissions = new ArrayList();
        for (String permission : MANDATORY_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                unGrantedPermissions.add(permission);
            }
        }
        if (unGrantedPermissions.size() == 0) {//已经获得了所有权限
            return true;
        } else {//部分权限未获得，重新请求获取权限
            String[] array = new String[unGrantedPermissions.size()];
            ActivityCompat.requestPermissions(this, unGrantedPermissions.toArray(array), 0);
            ToastUtils.showToast(R.string.toast_error_need_app_permission);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onBackPressed() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastPressBackKeyMillis < DOUBLE_BACK_KEY_TO_EXIT_INTERVAL_MILLIS) {
            finish();
        } else {
            lastPressBackKeyMillis = currentTimeMillis;
            ToastUtils.showToast(R.string.toast_press_back_one_more_to_exit);
        }
    }
}
