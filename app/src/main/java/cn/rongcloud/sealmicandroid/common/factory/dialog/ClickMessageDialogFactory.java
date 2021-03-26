package cn.rongcloud.sealmicandroid.common.factory.dialog;

import android.app.Dialog;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.common.adapter.ButtonBaseDialogAdapter;
import cn.rongcloud.sealmicandroid.common.factory.dialog.base.BottomDialogFactory;
import cn.rongcloud.sealmicandroid.common.listener.OnDialogButtonListClickListener;
import cn.rongcloud.sealmicandroid.manager.GlideManager;

/**
 * 点击消息列表弹出的 dialog 工厂
 */
public class ClickMessageDialogFactory extends BottomDialogFactory {

    private OnDialogButtonListClickListener onDialogButtonListClickListener;
    private ImageView portrait;
    private TextView userName;
    private TextView micPosition;
    private View headView;
    private boolean clickMessageIsHost = false;

    public void setOnDialogButtonListClickListener(OnDialogButtonListClickListener onDialogButtonListClickListener) {
        this.onDialogButtonListClickListener = onDialogButtonListClickListener;
    }

    @Override
    public Dialog buildDialog(final FragmentActivity context) {
        //设置mic的布局
        Dialog micDialog = super.buildDialog(context);
        ListView root = (ListView) LayoutInflater.from(context).inflate(
                R.layout.view_bottomdialog, null);
        micDialog.setContentView(root);

        //设置适配器
        Resources res = context.getResources();
        String[] hosteling;
        if (clickMessageIsHost) {
            hosteling = res.getStringArray(R.array.dialog_click_message_del);
        } else {
            hosteling = res.getStringArray(R.array.dialog_click_message);
        }
        ButtonBaseDialogAdapter dialogAdapter = new ButtonBaseDialogAdapter(context, R.layout.item_dialog_bottom, hosteling, true);
        root.setAdapter(dialogAdapter);

        //设置头布局
        headView = LayoutInflater.from(context).inflate(R.layout.item_dialog_userheader, null);
        headView.measure(0, 0);
//        final RelativeLayout imgRel = headView.findViewById(R.id.item_dialog_imgrel);

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
        return micDialog;
    }

    public void setPortrait(String url) {
        GlideManager.getInstance().setUrlImage(headView, url, portrait);
    }

    public void setClickType(boolean type) {
        clickMessageIsHost = type;
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }

    public void setMicPosition(String micPosition) {
        this.micPosition.setText(micPosition);
    }

    public void setMicPositionIsGong() {
        this.micPosition.setVisibility(View.GONE);
    }

}
