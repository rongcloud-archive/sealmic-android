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
import cn.rongcloud.sealmicandroid.common.listener.OnHandOverHostDialogClickListener;
import cn.rongcloud.sealmicandroid.common.listener.OnTakeOverHostDialogClickListener;
import cn.rongcloud.sealmicandroid.im.message.TakeOverHostMessage;
import cn.rongcloud.sealmicandroid.util.DisplayUtil;

/**
 * 接管主持人弹窗
 */
public class TakeOverHostDialogFactory extends CenterDialogFactory {

    private OnTakeOverHostDialogClickListener onTakeOverHostClickListener;

    public void setOnTakeOverHostClickListener(OnTakeOverHostDialogClickListener onTakeOverHostClickListener) {
        this.onTakeOverHostClickListener = onTakeOverHostClickListener;
    }

    public Dialog buildDialog(FragmentActivity context, final TakeOverHostMessage takeOverHostMessage) {
        final Dialog takeOverSpeakerDialog = super.buildDialog(context);
        View root = LayoutInflater.from(context).inflate(R.layout.dialog_take_over_speaker, null);
        takeOverSpeakerDialog.setContentView(root);
        TextView agreeTextView = root.findViewById(R.id.tv_agree);
        agreeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeOverSpeakerDialog.cancel();
                if (onTakeOverHostClickListener != null) {
                    onTakeOverHostClickListener.onAgree(takeOverHostMessage);
                }
            }
        });
        TextView refuseTextView = root.findViewById(R.id.tv_refuse);
        refuseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeOverSpeakerDialog.cancel();
                if (onTakeOverHostClickListener != null) {
                    onTakeOverHostClickListener.onRefuse(takeOverHostMessage);
                }
            }
        });
        TextView titleTextView = root.findViewById(R.id.tv_take_over_title);
        titleTextView.setText(String.format(context.getResources().getString(R.string.tv_take_over_title), takeOverHostMessage.getOperatorName()));
        Window takeOverSpeakerWindow = takeOverSpeakerDialog.getWindow();
        WindowManager.LayoutParams layoutParams = takeOverSpeakerWindow.getAttributes();
        layoutParams.height = DisplayUtil.dp2px(context, 158);
        return takeOverSpeakerDialog;
    }
}
