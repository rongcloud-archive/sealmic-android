package io.rong.imkit;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import io.rong.imkit.emoticon.EmoticonTabAdapter;

/**
 * Created by Android Studio.
 * User: lvhongzhen
 * Date: 2019-08-15
 * Time: 11:27
 */
public interface IRongExtensionState {
    //改变的view录音按钮，emijo按钮，+号
    void changeView(RongExtension pExtension);

    void onClick(RongExtension pExtension, View v);

    boolean onEditTextTouch(RongExtension pExtension, View v, MotionEvent event);
    void hideEmoticonBoard(ImageView pEmoticonToggle, EmoticonTabAdapter pEmotionTabAdapter);
}
