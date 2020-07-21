package cn.rongcloud.sealmicandroid.common.factory.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.SealMicApp;
import cn.rongcloud.sealmicandroid.bean.kv.MicBean;
import cn.rongcloud.sealmicandroid.common.Event;
import cn.rongcloud.sealmicandroid.common.MicState;
import cn.rongcloud.sealmicandroid.common.adapter.ButtonBaseDialogAdapter;
import cn.rongcloud.sealmicandroid.common.factory.dialog.base.BottomDialogFactory;
import cn.rongcloud.sealmicandroid.common.listener.OnDialogButtonListClickListener;

/**
 * 麦位设置dialog
 */
public class MicSettingDialogFactory extends BottomDialogFactory {

    private OnDialogButtonListClickListener onDialogButtonListClickListener;
    private ButtonBaseDialogAdapter dialogAdapter;
    private Dialog micSettingDialog;
    private TextView textContent;

    public void setOnDialogButtonListClickListener(OnDialogButtonListClickListener onDialogButtonListClickListener) {
        this.onDialogButtonListClickListener = onDialogButtonListClickListener;
    }

    public Dialog buildDialog(FragmentActivity context, MicBean micBean) {
        EventBus.getDefault().register(this);
        micSettingDialog = super.buildDialog(context);
        ListView root = (ListView) LayoutInflater.from(context).inflate(
                R.layout.view_bottomdialog, null);
        micSettingDialog.setContentView(root);
        Resources res = context.getResources();
        String[] hosteling;
        if (micBean.getState() == MicState.NORMAL.getState()) {
            hosteling = res.getStringArray(R.array.dialog_micmanage);
        } else if (micBean.getState() == MicState.LOCK.getState()) {
            hosteling = res.getStringArray(R.array.dialog_micmanage_unlock);
        } else {
            hosteling = res.getStringArray(R.array.dialog_micmanage);
        }
        dialogAdapter = new ButtonBaseDialogAdapter(context, R.layout.item_dialog_bottom, hosteling, false);
        root.setAdapter(dialogAdapter);
        View headView = LayoutInflater.from(context).inflate(R.layout.item_dialog_textheader, null);
        headView.measure(0, 0);
        textContent = headView.findViewById(R.id.dialog_name);
        // 获取对话框当前的参数值
        Window micSettingWindow = micSettingDialog.getWindow();
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
        dialogAdapter.setOnButtonClickListener(new OnDialogButtonListClickListener() {
            @Override
            public void onClick(String content) {
                onDialogButtonListClickListener.onClick(content);
            }
        });
        micSettingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                EventBus.getDefault().unregister(this);
            }
        });
        return micSettingDialog;
    }

    public void setWheetContent(String content) {
        if (content == null) {
            this.textContent.setVisibility(View.GONE);
            return;
        }
        if (content.isEmpty()) {
            this.textContent.setVisibility(View.GONE);
            return;
        }
        if (this.textContent != null) {
            this.textContent.setText(content);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventLockState(Event.EventLockMic eventLockMic) {
        if (eventLockMic.getState() == MicState.NORMAL.getState()) {
            String[] hosteling = SealMicApp.getApplication().getResources().getStringArray(R.array.dialog_micmanage);
            dialogAdapter.setDatas(hosteling);
        }
        if (eventLockMic.getState() == MicState.LOCK.getState()) {
            String[] hosteling = SealMicApp.getApplication().getResources().getStringArray(R.array.dialog_micmanage_unlock);
            dialogAdapter.setDatas(hosteling);
        }
//        micSettingDialog.cancel();
    }

}
