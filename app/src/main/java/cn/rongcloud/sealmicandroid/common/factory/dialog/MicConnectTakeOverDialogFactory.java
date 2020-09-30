package cn.rongcloud.sealmicandroid.common.factory.dialog;

import android.app.Dialog;
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

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.common.adapter.ButtonBaseDialogAdapter;
import cn.rongcloud.sealmicandroid.common.factory.dialog.base.BottomDialogFactory;
import cn.rongcloud.sealmicandroid.common.listener.OnDialogButtonListClickListener;
import cn.rongcloud.sealmicandroid.manager.GlideManager;

/**
 * 连麦者点击主持人麦位，且主持人麦位有人时所对应弹出的dialog
 */
public class MicConnectTakeOverDialogFactory extends BottomDialogFactory {

    private ButtonBaseDialogAdapter dialogAdapter;
    private OnDialogButtonListClickListener onDialogButtonListClickListener;
    private ImageView portrait;
    private TextView userName;
    private TextView micPosition;
    private ListView root;

    /**
     * 默认显示"发消息"按钮
     */
    private boolean isShowMessageButton = true;
    private RelativeLayout imgRel;
    private RelativeLayout titleRel;

    public void setOnDialogButtonListClickListener(OnDialogButtonListClickListener onDialogButtonListClickListener) {
        this.onDialogButtonListClickListener = onDialogButtonListClickListener;
    }

    @Override
    public Dialog buildDialog(FragmentActivity context) {
        //设置mic的布局
        Dialog micDialog = super.buildDialog(context);
        root = (ListView) LayoutInflater.from(context).inflate(
                R.layout.view_bottomdialog, null);
        micDialog.setContentView(root);

        String[] hosteling;
        //设置适配器
        Resources res = context.getResources();
        if (isShowMessageButton) {
            hosteling = res.getStringArray(R.array.dialog_mic_take_over);
        } else {
            hosteling = res.getStringArray(R.array.dialog_mic_take_over_without_message);
        }
        dialogAdapter = new ButtonBaseDialogAdapter(context, R.layout.item_dialog_bottom, hosteling, false);
        root.setAdapter(dialogAdapter);

        View headView = LayoutInflater.from(context).inflate(R.layout.item_dialog_userheader, null);
        imgRel = headView.findViewById(R.id.item_dialog_imgrel);
        titleRel = headView.findViewById(R.id.item_dialog_titlerel);
        //重新计算高度，获取对话框当前的参数值
        Window dialogWindow = micDialog.getWindow();
        WindowManager.LayoutParams lp = micDialog.getWindow().getAttributes();
        // 新位置X坐标
        lp.x = 0;
        // 新位置Y坐标
        lp.y = 0;
        // 宽度
        lp.width = context.getResources().getDisplayMetrics().widthPixels;
        headView.measure(0, 0);
        userName = headView.findViewById(R.id.item_dialog_name);
        micPosition = headView.findViewById(R.id.item_dialog_mic);
        portrait = headView.findViewById(R.id.item_dialog_img);
        root.measure(0, 0);
        //显示发消息按钮的时候再设置头布局
//        if (isShowMessageButton) {
//            lp.height = root.getMeasuredHeight() * hosteling.length + headView.getMeasuredHeight();
//        }
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
        return micDialog;
    }

    public void setPortrait(String url) {
        if (this.portrait != null) {
            GlideManager.getInstance().setUrlImage(root, url, this.portrait);
        }
    }

    public void setUserName(String userName) {
        if (this.userName != null) {
            this.userName.setText(userName);
        }
    }

    /**
     * 点击的麦位是否有人
     */
    public void setCurrentType(boolean userType) {
        this.micPosition.setVisibility(View.GONE);
        if (userType) {
            //有人，展示头像
            imgRel.setVisibility(View.VISIBLE);
            titleRel.setBackgroundResource(R.mipmap.item_dialog_title);
        } else {
            imgRel.setVisibility(View.GONE);
            titleRel.setBackgroundResource(R.mipmap.item_dialog_headtitle);
        }
    }

    public void setMicPosition(String micPosition) {
        if (this.micPosition != null) {
            this.micPosition.setText(micPosition);
        }
    }

    public void setShowMessageButton(boolean showMessageButton) {
        isShowMessageButton = showMessageButton;
    }
}
