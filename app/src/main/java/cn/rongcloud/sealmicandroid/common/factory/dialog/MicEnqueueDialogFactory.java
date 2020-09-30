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

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.SealMicApp;
import cn.rongcloud.sealmicandroid.bean.kv.MicBean;
import cn.rongcloud.sealmicandroid.common.MicState;
import cn.rongcloud.sealmicandroid.common.adapter.ButtonBaseDialogAdapter;
import cn.rongcloud.sealmicandroid.common.factory.dialog.base.BottomDialogFactory;
import cn.rongcloud.sealmicandroid.common.listener.OnDialogButtonListClickListener;

/**
 * 排麦dialog
 */
public class MicEnqueueDialogFactory extends BottomDialogFactory {

//    private OnDialogButtonListClickListener onDialogButtonListClickListener;
    private ButtonBaseDialogAdapter dialogAdapter;
    private Dialog micEnqueueDialog;
    private CallClick callClick;
//
//    public void setOnDialogButtonListClickListener(OnDialogButtonListClickListener onDialogButtonListClickListener) {
//        this.onDialogButtonListClickListener = onDialogButtonListClickListener;
//    }

    public Dialog buildDialog(FragmentActivity context, MicBean micBean) {
        micEnqueueDialog = super.buildDialog(context);
        Resources res = context.getResources();
        String[] hosteling;
        hosteling = res.getStringArray(R.array.dialog_mic_enqueue);
        dialogAdapter = new ButtonBaseDialogAdapter(context, R.layout.item_dialog_bottom, hosteling, false);
        View headView = LayoutInflater.from(context).inflate(R.layout.item_dialog_textheader, null);
        TextView itemName = headView.findViewById(R.id.dialog_name);
        if (micBean.getState() == MicState.NORMAL.getState() || micBean.getState() == MicState.CLOSE.getState()) {
            String format = SealMicApp.getApplication().getText(R.string.enqueue_mic_item_name_normal).toString();
            itemName.setText(String.format(format, micBean.getPosition()));
        }
        if (micBean.getState() == MicState.LOCK.getState()) {
            String format = SealMicApp.getApplication().getText(R.string.enqueue_mic_item_name_lock).toString();
            itemName.setText(String.format(format, micBean.getPosition()));
        }
        headView.measure(0, 0);
        ListView root = (ListView) LayoutInflater.from(context).inflate(
                R.layout.view_bottomdialog, null);
        root.setAdapter(dialogAdapter);
        micEnqueueDialog.setContentView(root);
        // 获取对话框当前的参数值
        Window micSettingWindow = micEnqueueDialog.getWindow();
        WindowManager.LayoutParams lp = micSettingWindow.getAttributes();
        // 新位置X坐标
        lp.x = 0;
        // 新位置Y坐标
        lp.y = 0;
        // 宽度
        lp.width = context.getResources().getDisplayMetrics().widthPixels;
        root.measure(0, 0);
        micSettingWindow.setAttributes(lp);
        root.addHeaderView(headView);
//        if (onDialogButtonListClickListener != null) {
//            dialogAdapter.setOnButtonClickListener(onDialogButtonListClickListener);
//        }
        dialogAdapter.setOnButtonClickListener(new OnDialogButtonListClickListener() {
            @Override
            public void onClick(String content) {
                callClick.onClick(content);
            }
        });
        micEnqueueDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
//                EventBus.getDefault().unregister(this);
            }
        });
        return micEnqueueDialog;
    }

    public interface CallClick {
        void onClick(String content);
    }

    public void setCallClick(CallClick callClick) {
        this.callClick = callClick;
    }
}
