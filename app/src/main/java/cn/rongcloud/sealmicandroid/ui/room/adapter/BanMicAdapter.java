package cn.rongcloud.sealmicandroid.ui.room.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.bean.repo.RoomMemberRepo;
import cn.rongcloud.sealmicandroid.common.constant.UserRoleType;
import cn.rongcloud.sealmicandroid.databinding.ItemDialogUnlockMicBinding;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.manager.GlideManager;

/**
 * 禁言Adapter
 */
public class BanMicAdapter extends RecyclerView.Adapter<BanMicAdapter.BanMicViewHolder> {

    private List<RoomMemberRepo.MemberBean> roomMemberList;

    public BanMicAdapter() {
        this.roomMemberList = new ArrayList<>();
    }

    public interface BanRoomMemberOnClickListener {
        /**
         * 点击解禁
         *
         * @param position   第几个
         * @param memberBean 点击项
         */
        void onClickBan(int position, RoomMemberRepo.MemberBean memberBean);
    }

    private BanRoomMemberOnClickListener banRoomMemberOnClickListener;

    public void setBanRoomMemberOnClickListener(BanRoomMemberOnClickListener banRoomMemberOnClickListener) {
        this.banRoomMemberOnClickListener = banRoomMemberOnClickListener;
    }

    public void setRoomMemberList(List<RoomMemberRepo.MemberBean> roomMemberList) {
        this.roomMemberList.clear();
        this.roomMemberList.addAll(roomMemberList);
        notifyDataSetChanged();
    }

    /**
     * 是否已经存在于禁言列表中
     *
     * @param memberBean
     * @return
     */
    public boolean isAlreadyAtList(RoomMemberRepo.MemberBean memberBean) {
        for (int i = 0; i < roomMemberList.size(); i++) {
            if (roomMemberList.get(i).getUserId().equals(memberBean.getUserId())) {
                //相同则代表已经存在
                return true;
            }
        }
        return false;
    }

    public void addRoomMember(RoomMemberRepo.MemberBean memberBean) {
        if (this.roomMemberList == null) {
            this.roomMemberList = new ArrayList<>();
        }
        roomMemberList.add(memberBean);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BanMicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDialogUnlockMicBinding itemDialogUnlockMicBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_dialog_unlock_mic,
                        parent,
                        false);
        return new BanMicViewHolder(itemDialogUnlockMicBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BanMicViewHolder holder, int position) {
        holder.bind(position);
        holder.itemDialogUnlockMicBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return roomMemberList == null ? 0 : roomMemberList.size();
    }

    class BanMicViewHolder extends RecyclerView.ViewHolder {

        private ItemDialogUnlockMicBinding itemDialogUnlockMicBinding;
        private int position;
        private RoomMemberRepo.MemberBean memberBean;

        public BanMicViewHolder(@NonNull ItemDialogUnlockMicBinding itemDialogUnlockMicBinding) {
            super(itemDialogUnlockMicBinding.getRoot());
            this.itemDialogUnlockMicBinding = itemDialogUnlockMicBinding;
            this.itemDialogUnlockMicBinding.tvUnlockMic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (banRoomMemberOnClickListener == null) {
                        return;
                    }
                    banRoomMemberOnClickListener.onClickBan(position, memberBean);
                }
            });
        }

        public void bind(int position) {
            this.position = position;
            this.memberBean = roomMemberList.get(position);
            itemDialogUnlockMicBinding.tvOnlineName.setText(memberBean.getUserName());
            GlideManager.getInstance().setUrlImage(itemDialogUnlockMicBinding.getRoot(), memberBean.getPortrait(), itemDialogUnlockMicBinding.imgOnlineHead);
            if (UserRoleType.HOST.isHost(CacheManager.getInstance().getUserRoleType())) {
                itemDialogUnlockMicBinding.tvUnlockMic.setVisibility(View.VISIBLE);
            } else {
                itemDialogUnlockMicBinding.tvUnlockMic.setVisibility(View.GONE);
            }
        }
    }
}
