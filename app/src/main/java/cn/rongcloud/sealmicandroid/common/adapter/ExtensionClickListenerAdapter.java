package cn.rongcloud.sealmicandroid.common.adapter;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import io.rong.imkit.IExtensionClickListener;

/**
 * 输入框adapter，适配器模式
 */
public class ExtensionClickListenerAdapter implements IExtensionClickListener {
    @Override
    public void onSendToggleClick(View v, String text) {

    }

    @Override
    public void onSwitchToggleClick(View v, ViewGroup extensionBar) {

    }

    @Override
    public void onVoiceInputToggleTouch(View v, MotionEvent event) {

    }

    @Override
    public void onEmoticonToggleClick(View v, ViewGroup extensionBoard) {

    }

    @Override
    public void onPluginToggleClick(View v, ViewGroup extensionBoard) {

    }

    @Override
    public void onEditTextClick(EditText editText) {

    }

    @Override
    public boolean onKey(View editText, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onExtensionCollapsed() {

    }

    @Override
    public void onExtensionExpanded(int h) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
