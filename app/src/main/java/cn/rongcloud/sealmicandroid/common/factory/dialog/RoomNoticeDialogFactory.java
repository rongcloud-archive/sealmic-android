package cn.rongcloud.sealmicandroid.common.factory.dialog;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.common.factory.dialog.base.CenterDialogFactory;

/**
 * 房间公告dialog 工厂
 */
public class RoomNoticeDialogFactory extends CenterDialogFactory {

    @Override
    public Dialog buildDialog(FragmentActivity context) {
        final Dialog roomNoticeDialog = super.buildDialog(context);
        View root = LayoutInflater.from(context).inflate(R.layout.dialog_room_notice, null);
        roomNoticeDialog.setContentView(root);
        Window micSettingWindow = roomNoticeDialog.getWindow();
        WindowManager.LayoutParams layoutParams = micSettingWindow.getAttributes();
//        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 3 * 2;
//        layoutParams.height = context.getResources().getDisplayMetrics().heightPixels / 5 * 2;
        root.measure(0, 0);
        TextView haveDoneTextView = roomNoticeDialog.findViewById(R.id.tv_have_done);
        TextView noticeContentTextView = roomNoticeDialog.findViewById(R.id.tv_notice_content);
        haveDoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomNoticeDialog.cancel();
            }
        });
//        noticeContentTextView.getBackground().setAlpha(128);
        return roomNoticeDialog;
    }
}
