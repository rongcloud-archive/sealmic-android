package cn.rongcloud.sealmic.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.rongcloud.sealmic.R;
import cn.rongcloud.sealmic.model.MicState;
import cn.rongcloud.sealmic.model.RoomMicPositionInfo;
import cn.rongcloud.sealmic.utils.ResourceUtils;

/**
 * 聊天室麦位视图
 */
public class MicSeatView extends FrameLayout {
    private ImageView micSeatIv;
    private ImageView micMuteIv;
    private MicSeatRippleView micSeatRippleView;
    private RoomMicPositionInfo micInfo;
    private OnImageClickListener mOnImageClickLitener;

    public MicSeatView(@NonNull Context context) {
        super(context);
        initView();
    }

    public MicSeatView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        View contentView = inflate(this.getContext(), R.layout.chatroom_item_mic_seat, this);
        micSeatIv = contentView.findViewById(R.id.chatroom_item_iv_mic_seat);
        micMuteIv = contentView.findViewById(R.id.chatroom_item_iv_mic_mute);
        micSeatRippleView = contentView.findViewById(R.id.chatroom_item_rp_mic_ripple);
        micSeatIv.setBackground(null);
        micSeatIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnImageClickLitener != null) {
                    mOnImageClickLitener.onImageClick(view, micInfo.getPosition());
                }
            }
        });
    }

    public void updateMicState(RoomMicPositionInfo micInfo) {
        this.micInfo = micInfo;
        int state = micInfo.getState();
        String micUserId = micInfo.getUserId();

        // 麦位是否为空状态
        if (TextUtils.isEmpty(micUserId) && !MicState.isState(state, MicState.Locked)) {
            setMicSeatEmpty();

            // 麦位是否被锁定
        } else if (MicState.isState(state, MicState.Locked)) {
            lockMicSeat();

            // 麦位有用户
        } else if (MicState.isState(state, MicState.Hold)) {
            setMicSeatAvatar(ResourceUtils.getInstance().getUserAvatarResourceId(micInfo.getUserId()));
        }

        // 麦位是否被禁麦
        if (MicState.isState(state, MicState.Forbidden)) {
            setMicMuteState(true);
        } else {
            setMicMuteState(false);
        }
    }

    /**
     * 设置麦位上的头像
     *
     * @param resourceId
     */
    private void setMicSeatAvatar(int resourceId) {
        micSeatIv.setBackgroundResource(R.drawable.chatroom_bg_room_linker_avatar);
        micSeatIv.setImageDrawable(getResources().getDrawable(resourceId));
    }

    /**
     * 设置麦位静音
     */
    private void setMicMuteState(boolean isMute) {
        if (isMute) {
            micMuteIv.setVisibility(VISIBLE);
        } else {
            micMuteIv.setVisibility(GONE);
        }
    }

    /**
     * 锁定麦位
     */
    private void lockMicSeat() {
        micSeatIv.setImageDrawable(getResources().getDrawable(R.drawable.chatroom_bg_mic_seat_lock));
        micSeatIv.setBackground(null);
    }

    /**
     * 设置麦位为空
     */
    private void setMicSeatEmpty() {
        micSeatIv.setImageDrawable(getResources().getDrawable(R.drawable.chatroom_bg_mic_seat_empty));
        micSeatIv.setBackground(null);
    }

    /**
     * 初始化麦位位置
     *
     * @param position
     */
    public void init(int position) {
        micInfo = new RoomMicPositionInfo();
        micInfo.setState(MicState.Idle.getValue());
        micInfo.setPosition(position);
        setMicSeatEmpty();
    }

    /**
     * 获取视图当前所在麦位
     *
     * @return
     */
    public int getPosition() {
        if (micInfo != null) {
            return micInfo.getPosition();
        }
        return -1;
    }

    /**
     * 获取麦位信息
     *
     * @return
     */
    public RoomMicPositionInfo getMicInfo() {
        return micInfo;
    }

    /**
     * 开始波纹动画
     */
    public void startRipple() {
        micSeatRippleView.enableRipple(true);
    }

    /**
     * 关闭波纹动画
     */
    public void stopRipple() {
        micSeatRippleView.enableRipple(false);
    }

    public interface OnImageClickListener {
        void onImageClick(View view, int position);
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        mOnImageClickLitener = onImageClickListener;
    }

}
