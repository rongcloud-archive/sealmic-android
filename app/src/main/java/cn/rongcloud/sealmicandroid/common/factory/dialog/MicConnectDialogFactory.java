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
 * 当连麦者点击自己时弹出的dialog
 */
public class MicConnectDialogFactory extends BottomDialogFactory {

    private ButtonBaseDialogAdapter dialogAdapter;
    private OnDialogButtonListClickListener onDialogButtonListClickListener;
    private ImageView portrait;
    private TextView userName;
    private TextView micPosition;
    private ListView root;
    private RelativeLayout imgRel;
    private RelativeLayout titleRel;
    private boolean userType = false;

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

        //设置适配器
        Resources res = context.getResources();
        String[] hosteling = res.getStringArray(R.array.dialog_mic_out);
        dialogAdapter = new ButtonBaseDialogAdapter(context, R.layout.item_dialog_bottom, hosteling, false);
        root.setAdapter(dialogAdapter);

        //设置头布局
        View headView = LayoutInflater.from(context).inflate(R.layout.item_dialog_userheader, null);
        headView.measure(0, 0);
        imgRel = headView.findViewById(R.id.item_dialog_imgrel);
        titleRel = headView.findViewById(R.id.item_dialog_titlerel);

        userName = headView.findViewById(R.id.item_dialog_name);
        micPosition = headView.findViewById(R.id.item_dialog_mic);
        portrait = headView.findViewById(R.id.item_dialog_img);

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
//        lp.height = root.getMeasuredHeight() * hosteling.length + headView.getMeasuredHeight();
        // 透明度
        lp.alpha = 9f;
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
        GlideManager.getInstance().setUrlImage(root, url, portrait);
    }

    /**
     * 当前是否是连麦者
     */
    public void setCurrentUser(boolean userType) {
        this.userType = userType;
        if (userType) {
            //主持人,不显示头像
            imgRel.setVisibility(View.GONE);
            titleRel.setBackgroundResource(R.mipmap.item_dialog_headtitle);
        } else {
            //连麦者，显示头像
            imgRel.setVisibility(View.VISIBLE);
            titleRel.setBackgroundResource(R.mipmap.item_dialog_title);
        }
    }

//    public void setUserImgIsGong() {
//        this.imgRel.setVisibility(View.GONE);
//    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }

    public void setMicPosition(String micPosition) {
        this.micPosition.setText(micPosition);
    }
}
