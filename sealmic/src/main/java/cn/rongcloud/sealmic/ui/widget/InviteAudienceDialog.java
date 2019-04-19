package cn.rongcloud.sealmic.ui.widget;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.rongcloud.sealmic.R;
import cn.rongcloud.sealmic.model.UserInfo;
import cn.rongcloud.sealmic.task.ResultCallback;
import cn.rongcloud.sealmic.task.RoomManager;
import cn.rongcloud.sealmic.ui.adapter.ChatRoomAudienceListAdapter;
import cn.rongcloud.sealmic.utils.ToastUtils;

/**
 * 邀请观众抱麦
 */
public class InviteAudienceDialog extends BaseFullScreenDialog implements AdapterView.OnItemClickListener {

    /**
     * 观众邀请的点击回掉接口
     */
    public interface OnAudienceItemClickListener extends Serializable {
        /**
         * 点击方法
         *
         * @param info
         */
        void onItemClick(UserInfo info);
    }

    private static final String BUNDLE_KEY_HAS_CANCEL = "has_cancel";
    private static final String BUNDLE_KEY_ITEM_LISTENER = "item_listener";
    private static final String BUNDLE_KEY_ROOM_ID = "room_id";

    public static final int PAGE_SIZE = 30;

    // 模拟页数
    private int pageIndex = 0;

    private ListView lvList;
    private LinearLayout llListEmpty;
    private ChatRoomAudienceListAdapter adapter;
    private OnAudienceItemClickListener mListener;
    private InviteHandler mHandler = new InviteHandler();
    private List<UserInfo> mUserInfos = new ArrayList<>();
    private String roomId;

    class InviteHandler extends Handler {
        private static final int WHAT_REFRESH = 0;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_REFRESH:
                    if (adapter != null) {
                        if (msg.obj != null) {
                            List<UserInfo> infos = (ArrayList) msg.obj;
                            // 如果当前的页的数据不满足一页的大小，则不显示加载更多
                            adapter.setExpandable(PAGE_SIZE <= infos.size());
                            if (mUserInfos != null && PAGE_SIZE > infos.size()) {
                                Toast.makeText(getContext(), getString(R.string.chatroom_list_load_completed), Toast.LENGTH_SHORT).show();
                            }
                            mUserInfos.addAll(infos);
                        } else {
                            if (mUserInfos != null) {
                                Toast.makeText(getContext(), getString(R.string.chatroom_list_load_completed), Toast.LENGTH_SHORT).show();
                            }
                            adapter.setExpandable(false);
                        }

                        // 防止对原有数据多次操作的时候改变原先对象
                        List<UserInfo> infoList = new ArrayList<>(mUserInfos);
                        adapter.setDatas(infoList);
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_invite_audience, null);
        initView(view);
        return view;
    }


    private void initView(View view) {
        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        lvList = (ListView) view.findViewById(R.id.lv_list);
        llListEmpty = (LinearLayout) view.findViewById(R.id.ll_list_empty);
        lvList.setEmptyView(llListEmpty);
        adapter = new ChatRoomAudienceListAdapter(null);
        adapter.setExpandable(false);
        lvList.setAdapter(adapter);
        lvList.setOnItemClickListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Serializable serializable = bundle.getSerializable(BUNDLE_KEY_ITEM_LISTENER);
            if (serializable != null && serializable instanceof OnAudienceItemClickListener) {
                mListener = (OnAudienceItemClickListener) bundle.getSerializable(BUNDLE_KEY_ITEM_LISTENER);
            }

            boolean cancel = bundle.getBoolean(BUNDLE_KEY_HAS_CANCEL);
            roomId = bundle.getString(BUNDLE_KEY_ROOM_ID);
            setCancelable(cancel);
        }
        loadUserInfo();
    }

    private void loadUserInfo() {
        RoomManager.getInstance().getRoomUserList(roomId, new ResultCallback<List<UserInfo>>() {
            @Override
            public void onSuccess(List<UserInfo> userInfoList) {
                List<String> onMicPositionUserList = RoomManager.getInstance().getOnMicPositionUserList();
                List<UserInfo> removeList = new ArrayList<>();
                if (userInfoList != null) {
                    for (UserInfo userInfo : userInfoList) {
                        if (onMicPositionUserList.contains(userInfo.getUserId())) {
                            removeList.add(userInfo);
                        }
                    }
                    userInfoList.removeAll(removeList);
                }
                adapter.setDatas(userInfoList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(int errorCode) {
                ToastUtils.showErrorToast(errorCode);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int realPosition = position - lvList.getHeaderViewsCount();
        if (realPosition >= 0 && realPosition < adapter.getCount()) {
            Object item = adapter.getItem(realPosition);
            if (item != null && item instanceof UserInfo) {
                UserInfo info = (UserInfo) item;
                if (mListener != null) {
                    mListener.onItemClick(info);
                }
            }
        }
        dismiss();
    }

    /**
     * 通过此类可以创建对话框
     */
    public static class Builder {
        private boolean hasCancel = true;
        private String roomId;
        private OnAudienceItemClickListener onItemClickListener;

        public Builder setRoomId(String roomId) {
            this.roomId = roomId;
            return this;
        }

        /**
         * 设置是否有取消按钮
         *
         * @param hasCancel
         */
        public Builder setCancelable(boolean hasCancel) {
            this.hasCancel = hasCancel;
            return this;
        }

        /**
         * 设置列表项点击事件
         *
         * @param listener
         */
        public Builder setOnAudienceItemClickListener(OnAudienceItemClickListener listener) {
            this.onItemClickListener = listener;
            return this;
        }

        public InviteAudienceDialog build() {
            InviteAudienceDialog dialog = new InviteAudienceDialog();
            Bundle bundle = new Bundle();
            bundle.putBoolean(BUNDLE_KEY_HAS_CANCEL, hasCancel);
            bundle.putSerializable(BUNDLE_KEY_ITEM_LISTENER, onItemClickListener);
            bundle.putString(BUNDLE_KEY_ROOM_ID, roomId);
            dialog.setArguments(bundle);
            return dialog;
        }
    }
}
