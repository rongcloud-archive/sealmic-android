package cn.rongcloud.sealmicandroid.common.factory.dialog;

import android.app.Dialog;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.common.adapter.SwitchBaseDialogAdapter;
import cn.rongcloud.sealmicandroid.common.constant.UserRoleType;
import cn.rongcloud.sealmicandroid.common.factory.dialog.base.BottomDialogFactory;
import cn.rongcloud.sealmicandroid.manager.CacheManager;

/**
 * 主持人设置弹窗
 */
public class RoomSettingDialogFactory extends BottomDialogFactory {

    private OnRoomSettingDialogAction onRoomSettingDialogAction;
    private SwitchBaseDialogAdapter dialogAdapter;

    public void setOnRoomSettingDialogAction(OnRoomSettingDialogAction onRoomSettingDialogAction) {
        this.onRoomSettingDialogAction = onRoomSettingDialogAction;
    }

    public interface OnRoomSettingDialogAction {
        void audienceJoin(boolean isChecked);

        void audienceFreeMic(boolean isChecked);

        void useTelephoneReceiver(boolean isChecked);

        void openDebug(boolean isChecked);
    }

    @Override
    public Dialog buildDialog(FragmentActivity context) {
        Dialog roomSetting = super.buildDialog(context);
        Resources res = context.getResources();
        final boolean isHost = UserRoleType.HOST.isHost(CacheManager.getInstance().getUserRoleType());
        String[] hosteling = isHost
                ? res.getStringArray(R.array.dialog_room_setting_host)
                : res.getStringArray(R.array.dialog_room_setting_audience);
        dialogAdapter = new SwitchBaseDialogAdapter(context, R.layout.item_setting_room, hosteling);
        dialogAdapter.setOnSwitchButtonChangeListener(new SwitchBaseDialogAdapter.OnSwitchButtonChangeListener() {
            @Override
            public void onCheckedChanged(int position, CompoundButton buttonView, boolean isChecked) {
                dialogAction(position, isHost, isChecked);
            }
        });
        View headView = LayoutInflater.from(context).inflate(R.layout.item_dialog_textheader, null);
        headView.measure(0, 0);
        ListView root = (ListView) LayoutInflater.from(context).inflate(
                R.layout.view_bottomdialog, null);
        root.setAdapter(dialogAdapter);
        roomSetting.setContentView(root);
        // 获取对话框当前的参数值
        Window micSettingWindow = roomSetting.getWindow();
        WindowManager.LayoutParams lp = micSettingWindow.getAttributes();
        // 新位置X坐标
        lp.x = 0;
        // 新位置Y坐标
        lp.y = 0;
        // 宽度
        lp.width = context.getResources().getDisplayMetrics().widthPixels;
        root.measure(0, 0);
//        lp.height = root.getMeasuredHeight() * hosteling.length + headView.getMeasuredHeight();
        // 透明度
        lp.alpha = 9f;
        micSettingWindow.setAttributes(lp);
        root.addHeaderView(headView);
        TextView dialogTitleText = headView.findViewById(R.id.dialog_name);
        dialogTitleText.setText(R.string.setting);
        return roomSetting;
    }

    public SwitchBaseDialogAdapter getAdapter() {
        return dialogAdapter;
    }

    public void dialogAction(int position, boolean isHost, boolean isChecked) {
        if (isHost) {
            if (position == 0) {
                //是否允许观众加入
                onRoomSettingDialogAction.audienceJoin(isChecked);
            } else if (position == 1) {
                //是否允许观众自由上麦
                onRoomSettingDialogAction.audienceFreeMic(isChecked);
            } else if (position == 2) {
                //是否允许使用听筒播放
                onRoomSettingDialogAction.useTelephoneReceiver(isChecked);
            } else if (position == 3) {
                //是否开启debug模式
                onRoomSettingDialogAction.openDebug(isChecked);
            }
        } else {
            if (position == 0) {
                //是否允许使用听筒播放
                onRoomSettingDialogAction.useTelephoneReceiver(isChecked);
            } else if (position == 1) {
                //是否开启debug模式
                onRoomSettingDialogAction.openDebug(isChecked);
            }
        }
    }
}
