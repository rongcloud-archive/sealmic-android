package io.rong.imkit;

import android.net.Uri;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.LinkedHashMap;


public interface IExtensionClickListener extends TextWatcher {
    /**
     * 点击 “发送”
     *
     * @param v    “发送” view 实例
     * @param text 输入框内容
     */
    void onSendToggleClick(View v, String text);
    /**
     * 点击左侧按钮（例如：左侧语音切换按钮，客服时的转人工按钮）
     * 切换后，回调中携带 ViewGroup，用户可以添加自己的布局
     *
     * @param v            ”切换“ 实例
     * @param extensionBar 切换后的输入面板 ViewGroup（例如：语音输入面板，公众号时的菜单面板）
     */
    void onSwitchToggleClick(View v, ViewGroup extensionBar);

    /**
     * 点击“按住 说话”，用户可以在此回调方法中实现录音功能。
     *
     * @param v     语音输入面板 view 实例
     * @param event 点击事件
     */
    void onVoiceInputToggleTouch(View v, MotionEvent event);

    /**
     * 点击“表情” 回调.
     *
     * @param v              表情 view 实例
     * @param extensionBoard 用于展示表情的 ViewGroup
     */
    void onEmoticonToggleClick(View v, ViewGroup extensionBoard);

    /**
     * 点击 “+” 号区域, 回调中携带 ViewGroup
     *
     * @param v              “+” 号 view 实例
     * @param extensionBoard 用于展示 plugin 的 ViewGroup
     */
    void onPluginToggleClick(View v, ViewGroup extensionBoard);


    /**
     * 点击 “输入框” 时回调。
     *
     * @param editText “输入框” 实例
     */
    void onEditTextClick(EditText editText);

    /**
     * Called when a hardware key is dispatched to EditText.
     *
     * @param editText The view the key has been dispatched to.
     * @param keyCode  The code for the physical key that was pressed
     * @param event    The KeyEvent object containing full information about
     *                 the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    boolean onKey(View editText, int keyCode, KeyEvent event);

    /**
     * Extension 收起。
     */
    void onExtensionCollapsed();

    /**
     * Extension 已展开。
     *
     * @param h Extension 展开后的高度。
     */
    void onExtensionExpanded(int h);

}
