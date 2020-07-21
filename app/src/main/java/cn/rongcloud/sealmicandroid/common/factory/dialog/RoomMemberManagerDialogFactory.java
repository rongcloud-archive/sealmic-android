package cn.rongcloud.sealmicandroid.common.factory.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.common.Event;
import cn.rongcloud.sealmicandroid.common.constant.RoomMemberStatus;
import cn.rongcloud.sealmicandroid.common.factory.dialog.base.BottomDialogFactory;
import cn.rongcloud.sealmicandroid.ui.room.adapter.RoomMemberManagerDialogAdapter;

/**
 * 房间成员管理弹窗
 */
public class RoomMemberManagerDialogFactory extends BottomDialogFactory {

    private Dialog roomMemberManagerDialog;

    public Dialog buildDialog(FragmentActivity context, String currentPosition) {
        EventBus.getDefault().register(this);
        List<String> tabList = new ArrayList<>();
        tabList.add(RoomMemberStatus.ONLINE.getStatus());
        tabList.add(RoomMemberStatus.ENQUEUE_MIC.getStatus());
        tabList.add(RoomMemberStatus.BAN.getStatus());
        roomMemberManagerDialog = super.buildDialog(context);
        View root = LayoutInflater.from(context).inflate(R.layout.dialog_room_member_manager, null);
        roomMemberManagerDialog.setContentView(root);
        ViewPager2 roomMemberManagerViewPager2 = root.findViewById(R.id.room_member_manager_viewpager);
        RoomMemberManagerDialogAdapter roomMemberManagerDialogAdapter = new RoomMemberManagerDialogAdapter(context);
        roomMemberManagerDialogAdapter.setTabList(tabList);
        roomMemberManagerViewPager2.setAdapter(roomMemberManagerDialogAdapter);
        roomMemberManagerViewPager2.setOffscreenPageLimit(tabList.size());
        //设置默认展示为排麦页
        for (int i = 0; i < tabList.size(); i++) {
            if (tabList.get(i).equals(currentPosition)) {
                roomMemberManagerViewPager2.setCurrentItem(i);
                break;
            }
        }
        TabLayout roomMemberManagerTab = root.findViewById(R.id.room_member_manager_tab);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(roomMemberManagerTab,
                roomMemberManagerViewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setText(RoomMemberStatus.ONLINE.getStatus());
                                break;
                            case 1:
                                tab.setText(RoomMemberStatus.ENQUEUE_MIC.getStatus());
                                break;
                            case 2:
                                tab.setText(RoomMemberStatus.BAN.getStatus());
                                break;
                            default:
                                break;
                        }
                    }
                });
        tabLayoutMediator.attach();
        Window window = roomMemberManagerDialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = context.getResources().getDisplayMetrics().heightPixels / 3 * 2;
        roomMemberManagerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                EventBus.getDefault().unregister(this);
            }
        });
        return roomMemberManagerDialog;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventConnectMicBean(Event.EventUserLineStatusChange.ConnectMicBean connectMicBean) {
        if (roomMemberManagerDialog != null) {
            roomMemberManagerDialog.cancel();
        }
    }
}
