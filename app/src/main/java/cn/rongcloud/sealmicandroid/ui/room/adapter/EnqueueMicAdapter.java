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
import cn.rongcloud.sealmicandroid.databinding.ItemDialogEnqueueMicBinding;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.manager.GlideManager;

/**
 * 排麦Adapter
 */
public class EnqueueMicAdapter extends RecyclerView.Adapter<EnqueueMicAdapter.EnqueueMicViewHolder> {

    private List<RoomMemberRepo.MemberBean> roomMemberList;

    public EnqueueMicAdapter() {
        this.roomMemberList = new ArrayList<>();
    }

    public interface OnMicAcceptClickListener {
        /**
         * 点击同意用户上麦
         *
         * @param position   第几个
         * @param memberBean 被选中项
         */
        void onMicAcceptClick(int position, RoomMemberRepo.MemberBean memberBean);

        /**
         * 点击拒绝用户上麦
         *
         * @param position   第几个
         * @param memberBean 被选中项
         */
        void onMicRejectClick(int position, RoomMemberRepo.MemberBean memberBean);
    }

    private OnMicAcceptClickListener onMicAcceptClickListener;

    public void setMicAcceptOnClickListener(OnMicAcceptClickListener onMicAcceptClickListener) {
        this.onMicAcceptClickListener = onMicAcceptClickListener;
    }

    public void setRoomMemberList(List<RoomMemberRepo.MemberBean> roomMemberList) {
        this.roomMemberList.clear();
        this.roomMemberList.addAll(roomMemberList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EnqueueMicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDialogEnqueueMicBinding itemDialogEnqueueMicBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_dialog_enqueue_mic,
                parent,
                false);
        return new EnqueueMicViewHolder(itemDialogEnqueueMicBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EnqueueMicViewHolder holder, int position) {
        holder.bind(position);
        holder.itemDialogEnqueueMicBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return roomMemberList == null ? 0 : roomMemberList.size();
    }

    class EnqueueMicViewHolder extends RecyclerView.ViewHolder {

        private ItemDialogEnqueueMicBinding itemDialogEnqueueMicBinding;
        private int position;
        private RoomMemberRepo.MemberBean memberBean;

        public EnqueueMicViewHolder(@NonNull ItemDialogEnqueueMicBinding itemDialogEnqueueMicBinding) {
            super(itemDialogEnqueueMicBinding.getRoot());
            this.itemDialogEnqueueMicBinding = itemDialogEnqueueMicBinding;
            this.itemDialogEnqueueMicBinding.tvGoMic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMicAcceptClickListener == null) {
                        return;
                    }
                    onMicAcceptClickListener.onMicAcceptClick(position, memberBean);
                }
            });
            this.itemDialogEnqueueMicBinding.tvRefuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMicAcceptClickListener == null) {
                        return;
                    }
                    onMicAcceptClickListener.onMicRejectClick(position, memberBean);
                }
            });
        }

        void bind(int position) {
            this.position = position;
            this.memberBean = roomMemberList.get(position);
            itemDialogEnqueueMicBinding.tvOnlineName.setText(memberBean.getUserName());
            GlideManager.getInstance().setUrlImage(itemDialogEnqueueMicBinding.getRoot(), memberBean.getPortrait(), itemDialogEnqueueMicBinding.imgOnlineHead);
            if (UserRoleType.HOST.isHost(CacheManager.getInstance().getUserRoleType())) {
                //如果是主持人显示按钮，其他角色不显示
                itemDialogEnqueueMicBinding.tvGoMic.setVisibility(View.VISIBLE);
                itemDialogEnqueueMicBinding.tvRefuse.setVisibility(View.VISIBLE);
            } else {
                itemDialogEnqueueMicBinding.tvGoMic.setVisibility(View.GONE);
                itemDialogEnqueueMicBinding.tvRefuse.setVisibility(View.GONE);
            }
        }

    }
}
