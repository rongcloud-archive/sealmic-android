package cn.rongcloud.sealmicandroid.common.factory.dialog.base;

import android.app.Dialog;
import android.view.Gravity;
import android.view.Window;

import androidx.fragment.app.FragmentActivity;

import cn.rongcloud.sealmicandroid.R;

/**
 * 占据全屏的dialog
 */
public class FullDialogFactory implements SealMicDialogFactory {

    @Override
    public Dialog buildDialog(FragmentActivity context) {
        Dialog fullDialog = new Dialog(context, R.style.BottomDialog);
        Window dialogWindow = fullDialog.getWindow();
        dialogWindow.setGravity(Gravity.FILL_VERTICAL);
        return fullDialog;
    }
}
