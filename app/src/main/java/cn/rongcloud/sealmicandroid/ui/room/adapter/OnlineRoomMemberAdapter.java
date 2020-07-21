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
import cn.rongcloud.sealmicandroid.databinding.ItemDialogRoomManagerBinding;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.manager.GlideManager;

/**
 * 房间成员管理adapter
 */
public class OnlineRoomMemberAdapter extends RecyclerView.Adapter<OnlineRoomMemberAdapter.RoomMemberViewHolder> {

    private List<RoomMemberRepo.MemberBean> roomMemberList;

    public OnlineRoomMemberAdapter() {
        roomMemberList = new ArrayList<>();
    }

    public interface OnlineRoomMemberClickListener {
        /**
         * 点击连麦
         *
         * @param position   第几个
         * @param memberBean 被点击项
         */
        void onClickConnect(int position, RoomMemberRepo.MemberBean memberBean);

        /**
         * 点击禁言
         *
         * @param position   第几个
         * @param memberBean 被点击项
         */
        void onClickBan(int position, RoomMemberRepo.MemberBean memberBean);

        /**
         * 点击踢人
         *
         * @param position   第几个
         * @param memberBean 被点击项
         */
        void onClickKick(int position, RoomMemberRepo.MemberBean memberBean);
    }

    private OnlineRoomMemberClickListener onlineRoomMemberClickListener;

    public void setOnlineRoomMemberClickListener(OnlineRoomMemberClickListener onlineRoomMemberClickListener) {
        this.onlineRoomMemberClickListener = onlineRoomMemberClickListener;
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
        if (roomMemberList == null) {
            roomMemberList = new ArrayList<>();
        }
        roomMemberList.add(memberBean);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDialogRoomManagerBinding itemDialogRoomManagerBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_dialog_room_manager,
                parent,
                false);
        return new RoomMemberViewHolder(itemDialogRoomManagerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomMemberViewHolder holder, int position) {
        holder.bind(position);
        holder.itemDialogRoomManagerBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return roomMemberList == null ? 0 : roomMemberList.size();
    }

    class RoomMemberViewHolder extends RecyclerView.ViewHolder {

        private ItemDialogRoomManagerBinding itemDialogRoomManagerBinding;
        private RoomMemberRepo.MemberBean memberBean;
        private int position;

        public RoomMemberViewHolder(@NonNull ItemDialogRoomManagerBinding binding) {
            super(binding.getRoot());
            this.itemDialogRoomManagerBinding = binding;
            this.itemDialogRoomManagerBinding.tvConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onlineRoomMemberClickListener == null) {
                        return;
                    }
                    onlineRoomMemberClickListener.onClickConnect(position, memberBean);
                }
            });
            this.itemDialogRoomManagerBinding.tvBan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onlineRoomMemberClickListener == null) {
                        return;
                    }
                    onlineRoomMemberClickListener.onClickBan(position, memberBean);
                }
            });
            this.itemDialogRoomManagerBinding.tvKick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onlineRoomMemberClickListener == null) {
                        return;
                    }
                    onlineRoomMemberClickListener.onClickKick(position, memberBean);
                }
            });
        }

        void bind(int position) {
            this.position = position;
            this.memberBean = roomMemberList.get(position);
            itemDialogRoomManagerBinding.tvOnlineName.setText(memberBean.getUserName());
            GlideManager.getInstance().setUrlImage(
                    itemDialogRoomManagerBinding.getRoot(),
                    memberBean.getPortrait(),
                    itemDialogRoomManagerBinding.imgOnlineHead);
            if (UserRoleType.HOST.isHost(CacheManager.getInstance().getUserRoleType())) {
                //如果是主持人，显示按钮，其他角色，隐藏按钮
                itemDialogRoomManagerBinding.tvConnect.setVisibility(View.VISIBLE);
                itemDialogRoomManagerBinding.tvBan.setVisibility(View.VISIBLE);
                itemDialogRoomManagerBinding.tvKick.setVisibility(View.VISIBLE);
            } else {
                itemDialogRoomManagerBinding.tvConnect.setVisibility(View.GONE);
                itemDialogRoomManagerBinding.tvBan.setVisibility(View.GONE);
                itemDialogRoomManagerBinding.tvKick.setVisibility(View.GONE);
            }
        }
    }
}
