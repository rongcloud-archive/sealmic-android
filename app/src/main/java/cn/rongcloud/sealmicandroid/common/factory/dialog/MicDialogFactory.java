package cn.rongcloud.sealmicandroid.common.factory.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import cn.rongcloud.sealmicandroid.manager.GlideManager;

/**
 * 弹出麦位的 dialog 工厂
 */
public class MicDialogFactory extends BottomDialogFactory {

    private ButtonBaseDialogAdapter dialogAdapter;
    private OnDialogButtonListClickListener onDialogButtonListClickListener;
    private ImageView portrait;
    private TextView userName;
    private TextView micPosition;
    private ListView root;

    public void setOnDialogButtonListClickListener(OnDialogButtonListClickListener onDialogButtonListClickListener) {
        this.onDialogButtonListClickListener = onDialogButtonListClickListener;
    }

    public Dialog buildDialog(FragmentActivity context, MicBean micBean) {
        EventBus.getDefault().register(this);
        //设置mic的布局
        Dialog micDialog = super.buildDialog(context);
        root = (ListView) LayoutInflater.from(context).inflate(
                R.layout.view_bottomdialog, null);
        micDialog.setContentView(root);

        //设置适配器
        Resources res = context.getResources();
        String[] hosteling;
        if (MicState.NORMAL.getState() == micBean.getState()) {
            hosteling = res.getStringArray(R.array.dialog_hostsetting);
        } else if (MicState.CLOSE.getState() == micBean.getState()) {
            hosteling = res.getStringArray(R.array.dialog_hostsetting_no);
        } else {
            hosteling = res.getStringArray(R.array.dialog_hostsetting);
        }
        dialogAdapter = new ButtonBaseDialogAdapter(context, R.layout.item_dialog_bottom, hosteling, false);
        root.setAdapter(dialogAdapter);

        //设置头布局
        View headView = LayoutInflater.from(context).inflate(R.layout.item_dialog_userheader, null);
        headView.measure(0, 0);
        RelativeLayout imgRel = headView.findViewById(R.id.item_dialog_imgrel);
        portrait = headView.findViewById(R.id.item_dialog_img);
        userName = headView.findViewById(R.id.item_dialog_name);
        micPosition = headView.findViewById(R.id.item_dialog_mic);

        //重新计算高度，获取对话框当前的参数值
        Window dialogWindow = micDialog.getWindow();
        WindowManager.LayoutParams lp = micDialog.getWindow().getAttributes();
        // 新位置X坐标
        lp.x = 0;
        // 新位置Y坐标
        lp.y = 0;
        // 宽度
        lp.width = context.getResources().getDisplayMetrics().widthPixels;
        root.measure(0, 0);
        dialogWindow.setAttributes(lp);
        root.addHeaderView(headView);

        dialogAdapter.setOnButtonClickListener(new OnDialogButtonListClickListener() {
            @Override
            public void onClick(String content) {
                if (onDialogButtonListClickListener != null) {
                    onDialogButtonListClickListener.onClick(content);
                }
            }
        });
        micDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                EventBus.getDefault().unregister(this);
            }
        });

        return micDialog;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventLockState(Event.EventLockMic eventLockMic) {
        if (!EventBus.getDefault().isRegistered(this)) {
            return;
        }
        if (eventLockMic.getState() == MicState.NORMAL.getState()) {
            String[] hosteling = SealMicApp.getApplication().getResources().getStringArray(R.array.dialog_hostsetting);
            dialogAdapter.setDatas(hosteling);
        }
        if (eventLockMic.getState() == MicState.CLOSE.getState()) {
            String[] hosteling = SealMicApp.getApplication().getResources().getStringArray(R.array.dialog_hostsetting_no);
            dialogAdapter.setDatas(hosteling);
        }
    }

    public void setPortrait(String url) {
        GlideManager.getInstance().setUrlImage(root, url, portrait);
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }

    public void setMicPosition(String micPosition) {
        this.micPosition.setText(micPosition);
    }
}
