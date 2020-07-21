package cn.rongcloud.sealmicandroid.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.common.listener.OnChatRoomTopBarClickListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 聊天室顶部条
 */
public class ChatRoomTopBar extends RelativeLayout {

    private CircleImageView roomPortrait;
    private TextView roomName;
    private TextView rtt;
    private TextView onlineNumber;
    private ImageView lineUp;
    private OnChatRoomTopBarClickListener onChatRoomTopBarClickListener;
    private ImageView netState;
    private ImageView lineUpRedDot;

    public void setOnChatRoomTopBarClickListener(OnChatRoomTopBarClickListener onChatRoomTopBarClickListener) {
        this.onChatRoomTopBarClickListener = onChatRoomTopBarClickListener;
    }

    public ChatRoomTopBar(Context context) {
        super(context);
        init();
    }

    public ChatRoomTopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChatRoomTopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.widget_chat_room_top_bar, this, true);
        ImageView back = root.findViewById(R.id.img_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChatRoomTopBarClickListener != null) {
                    onChatRoomTopBarClickListener.back(v);
                }
            }
        });
        roomPortrait = root.findViewById(R.id.img_chat_room_portrait);
        roomName = root.findViewById(R.id.tv_room_name);
        rtt = root.findViewById(R.id.tv_rtt);
        onlineNumber = root.findViewById(R.id.tv_online_number);
        netState = root.findViewById(R.id.img_chat_room_netstate);
        ImageView noticeChatRoom = root.findViewById(R.id.img_notice_chat_room);
        noticeChatRoom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChatRoomTopBarClickListener != null) {
                    onChatRoomTopBarClickListener.noticeDialog();
                }
            }
        });
        lineUp = root.findViewById(R.id.img_line_up);
        lineUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChatRoomTopBarClickListener != null) {
                    onChatRoomTopBarClickListener.lineUpDialog();
                }
            }
        });
        ImageView settingChatRoom = root.findViewById(R.id.img_setting_chat_room);
        settingChatRoom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChatRoomTopBarClickListener != null) {
                    onChatRoomTopBarClickListener.settingRoomDialog();
                }
            }
        });

        lineUpRedDot = root.findViewById(R.id.img_line_up_red_dot);
    }

    public CircleImageView getRoomPortrait() {
        return roomPortrait;
    }

    public TextView getRoomName() {
        return roomName;
    }

    public TextView getRtt() {
        return rtt;
    }

    public ImageView getNetState() {
        return netState;
    }

    public TextView getOnlineNumber() {
        return onlineNumber;
    }

    public ImageView getLineUp() {
        return lineUp;
    }

    public void showRedDot() {
        lineUpRedDot.setVisibility(VISIBLE);
    }

    public void hideRedDot() {
        lineUpRedDot.setVisibility(GONE);
    }
}
