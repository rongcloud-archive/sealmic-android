package cn.rongcloud.sealmicandroid.ui.room.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.SealMicApp;
import cn.rongcloud.sealmicandroid.databinding.ItemChatlistChatBinding;
import cn.rongcloud.sealmicandroid.im.message.SendGiftMessage;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

/**
 * 聊天室界面聊天列表adapter
 */
public class RoomChatListAdapter extends RecyclerView.Adapter<RoomChatListAdapter.RoomChatListViewHolder> {
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_CHAT_MESSAGE = 0;
    private static final int VIEW_TYPE_USER_CHANGED_INFO = 1;
    private List<Message> messageList;
    private OnClick onClick;

    @Override
    public int getItemCount() {
        return messageList == null ? 0 : messageList.size();
    }

    @NonNull
    @Override
    public RoomChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RoomChatListViewHolder roomChatListViewHolder;
        ItemChatlistChatBinding itemChatlistChatBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_chatlist_chat,
                parent,
                false);
        roomChatListViewHolder = new RoomChatListViewHolder(itemChatlistChatBinding);
        return roomChatListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RoomChatListViewHolder holder, final int position) {
        String name = "";
        String contentTv = "";
        MessageContent content = messageList.get(position).getContent();
        String objectName = messageList.get(position).getObjectName();
        if (content instanceof SendGiftMessage) {
            SendGiftMessage sendGiftMessage = (SendGiftMessage) content;
            name = sendGiftMessage.getUserInfo().getName();
            contentTv = sendGiftMessage.getContent();
        }
        if (content instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) content;
            name = textMessage.getUserInfo().getName();
            contentTv = textMessage.getContent();
        }

        if (TextUtils.isEmpty(name)) {
            holder.itemChatlistChatBinding.chatroomItemChatlitTvMessage.setVisibility(View.GONE);
            holder.itemChatlistChatBinding.chatroomItemChatlistTvNickname.setVisibility(View.GONE);
        } else {
            holder.itemChatlistChatBinding.chatroomItemChatlistTvNickname.setText(name + ": ");
            holder.itemChatlistChatBinding.chatroomItemChatlitTvMessage.setText(contentTv);
            if (objectName != null) {
                if (objectName.equals(SealMicApp.getApplication().getResources().getString(R.string.object_name))) {
                    holder.itemChatlistChatBinding.chatroomItemChatlistTvNickname.setText(name);
                    holder.itemChatlistChatBinding.chatroomItemChatlitTvMessage.setTextColor(Color.parseColor("#F8E71C"));
                    holder.itemChatlistChatBinding.chatroomItemChatlistTvNickname.setTextColor(Color.parseColor("#F8E71C"));
                }
            } else {
                holder.itemChatlistChatBinding.chatroomItemChatlitTvMessage.setTextColor(Color.parseColor("#FFFFFF"));
                holder.itemChatlistChatBinding.chatroomItemChatlistTvNickname.setTextColor(Color.parseColor("#CFCFCF"));
            }
        }

        holder.itemChatlistChatBinding.executePendingBindings();
        holder.itemChatlistChatBinding.chatroomItemChatlitTvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onClick(position, messageList.get(position));
            }
        });
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

    static class RoomChatListViewHolder extends RecyclerView.ViewHolder {

        private ItemChatlistChatBinding itemChatlistChatBinding;

        public RoomChatListViewHolder(@NonNull ItemChatlistChatBinding itemChatlistChatBinding) {
            super(itemChatlistChatBinding.getRoot());
            this.itemChatlistChatBinding = itemChatlistChatBinding;
        }
    }

    public interface OnClick {
        /**
         * 点击消息的回调
         *
         * @param position
         */
        void onClick(int position, Message message);
    }

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }
}