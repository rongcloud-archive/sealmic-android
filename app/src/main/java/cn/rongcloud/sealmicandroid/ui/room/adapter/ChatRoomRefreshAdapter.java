package cn.rongcloud.sealmicandroid.ui.room.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.bean.repo.RoomListRepo;
import cn.rongcloud.sealmicandroid.common.listener.RoomListItemOnClickListener;
import cn.rongcloud.sealmicandroid.databinding.ItemMainChatRoomBinding;

public class ChatRoomRefreshAdapter extends RecyclerView.Adapter<ChatRoomRefreshAdapter.ChatRoomRefreshHolder> {

    private List<RoomListRepo.RoomsBean> rooms;
    private RoomListItemOnClickListener itemOnClickListener;

    public ChatRoomRefreshAdapter() {
        rooms = new ArrayList<>();
    }

    public void setData(List<RoomListRepo.RoomsBean> rooms) {
        this.rooms.clear();
        this.rooms = rooms;
        notifyDataSetChanged();
    }

    public void addData(List<RoomListRepo.RoomsBean> rooms) {
        this.rooms.addAll(rooms);
        notifyDataSetChanged();
    }

    public void setItemOnClickListener(RoomListItemOnClickListener itemOnClickListener) {
        if (itemOnClickListener == null) {
            return;
        }
        this.itemOnClickListener = itemOnClickListener;
    }

    @NonNull
    @Override
    public ChatRoomRefreshHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ItemMainChatRoomBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.getContext()),
                R.layout.item_main_chat_room,
                viewGroup,
                false);
        return new ChatRoomRefreshHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomRefreshHolder holder, final int i) {
        if (i == rooms.size()) {
            holder.getBinding().chatroomlistImgHot.setVisibility(View.GONE);
            holder.getBinding().chatroomlistTvHotnum.setVisibility(View.GONE);
            holder.getBinding().chatroomlistTvName.setVisibility(View.GONE);
            holder.getBinding().chatroomImgLock.setVisibility(View.GONE);
            holder.getBinding().chatroomlistIvPre.setVisibility(View.GONE);
            Glide.with(holder.getBinding().getRoot().getContext()).load(R.mipmap.ic_chatroon_create).into(holder.getBinding().chatroomlistIvPicture);
            holder.getBinding().chatroomlistIvPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemOnClickListener.onClickCreateRoom();
                }
            });
            return;
        } else {
            holder.getBinding().chatroomlistImgHot.setVisibility(View.VISIBLE);
            holder.getBinding().chatroomlistTvHotnum.setVisibility(View.VISIBLE);
            holder.getBinding().chatroomlistTvName.setVisibility(View.VISIBLE);
            holder.getBinding().chatroomlistIvPre.setVisibility(View.VISIBLE);
        }
        holder.getBinding().setItem(rooms.get(i));
        //是否可以加入房间
        if (rooms.get(i).isAllowedJoinRoom()) {
            holder.getBinding().chatroomImgLock.setVisibility(View.GONE);
        } else {
            holder.getBinding().chatroomImgLock.setVisibility(View.VISIBLE);
        }
        Glide.with(holder.getBinding().getRoot().getContext())
                .load(rooms.get(i).getThemePictureUrl()).apply(RequestOptions.bitmapTransform(new RoundedCorners(35)))
                .into(holder.getBinding().chatroomlistIvPicture);
        Glide.with(holder.getBinding().getRoot().getContext())
                .load(R.mipmap.main_item_pre).apply(RequestOptions.bitmapTransform(new RoundedCorners(35)))
                .into(holder.getBinding().chatroomlistIvPre);

        holder.getBinding().chatroomlistIvPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemOnClickListener.onClick(view, rooms.get(i), i);
            }
        });
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return rooms == null ? 0 : rooms.size() + 1;
    }

    static class ChatRoomRefreshHolder extends RecyclerView.ViewHolder {
        private ItemMainChatRoomBinding binding;

        ChatRoomRefreshHolder(@NonNull ItemMainChatRoomBinding itemMainChatRoomBinding) {
            super(itemMainChatRoomBinding.getRoot());
            this.binding = itemMainChatRoomBinding;
        }

        ItemMainChatRoomBinding getBinding() {
            return binding;
        }
    }

}
