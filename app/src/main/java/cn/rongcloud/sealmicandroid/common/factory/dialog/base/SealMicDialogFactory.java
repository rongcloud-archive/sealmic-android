package cn.rongcloud.sealmicandroid.common.factory.dialog.base;

import android.app.Dialog;

import androidx.fragment.app.FragmentActivity;

/**
 * dialog生成工厂，采用工厂方法模式
 */
interface SealMicDialogFactory {

    /**
     * 显示特定dialog
     *
     * @param context 传递上下文
     * @return 工厂造出的dialog
     */
    Dialog buildDialog(FragmentActivity context);
}
