package cn.rongcloud.sealmicandroid.ui.room.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import cn.rongcloud.sealmicandroid.common.constant.RoomMemberStatus;

/**
 * 房间成员管理viewpager2对应的adapter
 */
public class RoomMemberManagerDialogAdapter extends FragmentStateAdapter {

    private List<String> tabList;

    public RoomMemberManagerDialogAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public RoomMemberManagerDialogAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public RoomMemberManagerDialogAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public void setTabList(List<String> tabList) {
        this.tabList = tabList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (tabList.get(position).equals(RoomMemberStatus.ONLINE.getStatus())) {
            return new OnLineMemberFragment();
        } else if (tabList.get(position).equals(RoomMemberStatus.ENQUEUE_MIC.getStatus())) {
            return new EnqueueFragment();
        } else if (tabList.get(position).equals(RoomMemberStatus.BAN.getStatus())) {
            return new BanMemberFragment();
        } else {
            return new OnLineMemberFragment();
        }
    }

    @Override
    public int getItemCount() {
        return tabList == null ? 0 : tabList.size();
    }
}
