package cn.rongcloud.sealmicandroid.ui.room.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.SealMicApp;
import cn.rongcloud.sealmicandroid.common.constant.SealMicErrorMsg;
import cn.rongcloud.sealmicandroid.im.message.SendGiftMessage;
import cn.rongcloud.sealmicandroid.util.ToastUtil;
import cn.rongcloud.sealmicandroid.util.log.SLog;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

/**
 * 聊天室消息列表adapter，Listview
 */
public class RoomChatMessageListAdapter extends BaseAdapter {
//    private static final int VIEW_TYPE_COUNT = 2;
//    private static final int VIEW_TYPE_CHAT_MESSAGE = 0;
//    private static final int VIEW_TYPE_USER_CHANGED_INFO = 1;

    private Context context;
    private List<Message> messageList;
    private CallClick callClick;

    public RoomChatMessageListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return messageList != null ? messageList.size() : 0;
    }

    @Override
    public Message getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = messageList.get(position);
        int viewType = getItemViewType(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = createView(viewType, parent);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        updateView(viewType, position, message, viewHolder);
        return convertView;
    }

    private View createView(int viewType, ViewGroup parent) {
        View contentView = null;

        LayoutInflater inflater = LayoutInflater.from(context);
        contentView = inflater.inflate(R.layout.item_chatlist_chat, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.avatarIv = contentView.findViewById(R.id.chatroom_item_chatlist_iv_avatar);
        viewHolder.nickNameTv = contentView.findViewById(R.id.chatroom_item_chatlist_tv_nickname);
        viewHolder.messageTv = contentView.findViewById(R.id.chatroom_item_chatlit_tv_message);
        viewHolder.constraintLayout = contentView.findViewById(R.id.chatroom_item_constranint);
        contentView.setTag(viewHolder);

        return contentView;
    }

    private void updateView(int viewType, final int position, final Message message, ViewHolder viewHolder) {
        try {
            String name = "";
            String contentTv = "";
            //是否显示背景
            boolean isShowBackground = false;
            MessageContent content = message.getContent();
            String objectName = message.getObjectName();
            if (content instanceof SendGiftMessage) {
                SendGiftMessage sendGiftMessage = (SendGiftMessage) content;
                name = sendGiftMessage.getUserInfo().getName();
                contentTv = sendGiftMessage.getContent();
                isShowBackground = false;
            }
            if (content instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) content;
                name = textMessage.getUserInfo().getName();
                contentTv = textMessage.getContent();
                if (contentTv.contains(context.getResources().getString(R.string.join_room_success))) {
                    isShowBackground = false;
                } else {
                    isShowBackground = true;
                }
            }

            if (TextUtils.isEmpty(name)) {
                viewHolder.messageTv.setVisibility(View.GONE);
                viewHolder.nickNameTv.setVisibility(View.GONE);
            } else {
                //是否展示背景
                if (isShowBackground) {
                    viewHolder.constraintLayout.setBackgroundResource(R.drawable.bg_room_message_item);
                } else {
                    viewHolder.constraintLayout.setBackgroundResource(R.drawable.bg_room_message_item_gong);
                }
                viewHolder.nickNameTv.setText(name + ": ");
                viewHolder.messageTv.setText(contentTv);
                if (objectName != null) {
                    if (objectName.equals(SealMicApp.getApplication().getResources().getString(R.string.object_name))) {
                        viewHolder.nickNameTv.setText(name);
                        viewHolder.messageTv.setTextColor(Color.parseColor("#F8E71C"));
                        viewHolder.nickNameTv.setTextColor(Color.parseColor("#F8E71C"));
                    } else {
                        viewHolder.messageTv.setTextColor(Color.parseColor("#FFFFFF"));
                        viewHolder.nickNameTv.setTextColor(Color.parseColor("#CFCFCF"));
                    }
                } else {
                    viewHolder.messageTv.setTextColor(Color.parseColor("#FFFFFF"));
                    viewHolder.nickNameTv.setTextColor(Color.parseColor("#CFCFCF"));
                }
            }

            viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callClick.onClick(position, message);
                }
            });
        } catch (Exception e) {
            SLog.e(SLog.TAG_SEAL_MIC, "Error: " + e.getMessage());
        }

    }


    public void setMessages(List<Message> messages) {
        messageList = messages;
        notifyDataSetChanged();
    }

    public void addMessages(Message message) {
        if (messageList == null) {
            messageList = new ArrayList<>();
        }
        messageList.add(message);
        notifyDataSetChanged();
    }

    public void removeMessage(int messageId) {
        if (messageList == null) {
            messageList = new ArrayList<>();
        }
        for (int i = 0; i < messageList.size(); i++) {
            Message msg = messageList.get(i);
            if (msg.getMessageId() == messageId) {
                messageList.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

    private class ViewHolder {
        ImageView avatarIv;
        TextView nickNameTv;
        TextView messageTv;
        ConstraintLayout constraintLayout;
    }

    public void setCallClick(CallClick callClick) {
        this.callClick = callClick;
    }

    public interface CallClick {
        /**
         * 点击消息回调的方法
         *
         * @param position
         * @param message
         */
        void onClick(int position, Message message);
    }
}
