package cn.rongcloud.sealmic.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.rongcloud.sealmic.R;
import cn.rongcloud.sealmic.im.message.RoomMemberChangedMessage;
import cn.rongcloud.sealmic.utils.ResourceUtils;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

public class RoomChatListAdapter extends BaseAdapter {
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_CHAT_MESSAGE = 0;
    private static final int VIEW_TYPE_USER_CHANGED_INFO = 1;

    private ResourceUtils resourceUtils;
    private Context context;
    private List<Message> messageList;

    public RoomChatListAdapter(Context context) {
        this.context = context;
        resourceUtils = ResourceUtils.getInstance();
    }

    @Override
    public int getCount() {
        return messageList != null ? messageList.size() : 0;
    }

    @Override
    public Message getItem(int position) {
        return messageList != null ? messageList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
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
        updateView(viewType, message, viewHolder);
        return convertView;
    }

    private View createView(int viewType, ViewGroup parent) {
        View contentView = null;
        if (viewType == VIEW_TYPE_CHAT_MESSAGE) {
            LayoutInflater inflater = LayoutInflater.from(context);
            contentView = inflater.inflate(R.layout.chatroom_item_chatlist_chat, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.avatarIv = contentView.findViewById(R.id.chatroom_item_chatlist_iv_avatar);
            viewHolder.nickNameTv = contentView.findViewById(R.id.chatroom_item_chatlist_tv_nickname);
            viewHolder.messageTv = contentView.findViewById(R.id.chatroom_item_chatlit_tv_message);
            contentView.setTag(viewHolder);
        } else if (viewType == VIEW_TYPE_USER_CHANGED_INFO) {
            LayoutInflater inflater = LayoutInflater.from(context);
            contentView = inflater.inflate(R.layout.chatroom_item_chatlist_info, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.avatarIv = contentView.findViewById(R.id.chatroom_item_chatlist_iv_avatar);
            viewHolder.nickNameTv = contentView.findViewById(R.id.chatroom_item_chatlist_tv_nickname);
            viewHolder.messageTv = contentView.findViewById(R.id.chatroom_item_chatlit_tv_message);
            contentView.setTag(viewHolder);
        }

        return contentView;
    }

    private void updateView(int viewType, Message message, ViewHolder viewHolder) {
        // 文本消息
        if (viewType == VIEW_TYPE_CHAT_MESSAGE) {
            TextMessage textMessage = (TextMessage) message.getContent();
            viewHolder.nickNameTv.setText(resourceUtils.getUserName(message.getSenderUserId()));
            viewHolder.messageTv.setText(textMessage.getContent());
            viewHolder.avatarIv.setImageDrawable(context.getResources().getDrawable(resourceUtils.getUserAvatarResourceId(message.getSenderUserId())));

            // 房间人员变动消息
        } else if (viewType == VIEW_TYPE_USER_CHANGED_INFO) {
            RoomMemberChangedMessage memberMessage = (RoomMemberChangedMessage) message.getContent();
            String targetUserId = memberMessage.getTargetUserId();
            viewHolder.nickNameTv.setText(resourceUtils.getUserName(targetUserId));
            viewHolder.avatarIv.setImageDrawable(context.getResources().getDrawable(resourceUtils.getUserAvatarResourceId(targetUserId)));
            RoomMemberChangedMessage.RoomMemberAction roomMemberAction = memberMessage.getRoomMemberAction();
            if (roomMemberAction == RoomMemberChangedMessage.RoomMemberAction.JOIN) {
                viewHolder.messageTv.setText(R.string.chatroom_user_enter);
            } else if (roomMemberAction == RoomMemberChangedMessage.RoomMemberAction.LEAVE) {
                viewHolder.messageTv.setText(R.string.chatroom_user_quit);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = getItem(position);
        if (message.getContent() instanceof TextMessage) {
            return VIEW_TYPE_CHAT_MESSAGE;
        } else if (message.getContent() instanceof RoomMemberChangedMessage) {
            return VIEW_TYPE_USER_CHANGED_INFO;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    public void setMessages(List<Message> messages) {
        messageList = messages;
    }

    private class ViewHolder {
        ImageView avatarIv;
        TextView nickNameTv;
        TextView messageTv;
    }
}
