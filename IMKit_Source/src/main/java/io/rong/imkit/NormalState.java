package io.rong.imkit;

import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import io.rong.imkit.emoticon.EmoticonTabAdapter;

import static android.view.View.GONE;

/*
 * Created by Android Studio.
 * User: lvhongzhen
 * Date: 2019-08-15
 * Time: 11:22
 */
public class NormalState implements IRongExtensionState {

    NormalState() {
    }

    @Override
    public void changeView(RongExtension pExtension) {
        ImageView voiceToggle = pExtension.getVoiceToggle();
        if (voiceToggle != null)
            voiceToggle.setImageResource(R.drawable.rc_voice_toggle_selector);
        ImageView pluginToggle = pExtension.getPluginToggle();
        if (pluginToggle != null)
            pluginToggle.setImageResource(R.drawable.rc_plugin_toggle_selector);
        ImageView emoticonToggle = pExtension.getEmoticonToggle();
        if (emoticonToggle != null)
            emoticonToggle.setImageResource(R.drawable.rc_emotion_toggle_selector);
        EditText editText = pExtension.getEditText();
        if (editText != null) {
            editText.setBackgroundResource(R.drawable.rc_edit_text_background_selector);
        }
        Button voiceInputToggle = pExtension.getVoiceInputToggle();
        if (voiceInputToggle != null) {
            voiceInputToggle.setTextColor(pExtension.getContext().getResources().getColor(R.color.rc_text_voice));
        }
    }

    @Override
    public void onClick(final RongExtension pExtension, View v) {
        int id = v.getId();
        if (id == R.id.rc_plugin_toggle) {
            if (pExtension.getExtensionClickListener() != null) {
                pExtension.getExtensionClickListener().onPluginToggleClick(v, pExtension);
            }
        } else if (id == R.id.rc_emoticon_toggle) {
            if (pExtension.getExtensionClickListener() != null) {
                pExtension.getExtensionClickListener().onEmoticonToggleClick(v, pExtension);
            }
            if (pExtension.isKeyBoardActive()) {
                pExtension.hideInputKeyBoard();
                pExtension.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pExtension.setEmoticonBoard();
                    }
                }, 200);
            } else {
                pExtension.setEmoticonBoard();
            }
        } else if (id == R.id.rc_voice_toggle) {
            pExtension.clickVoice(pExtension.isRobotFirst(), pExtension, v, R.drawable.rc_emotion_toggle_selector);
        }

    }

    @Override
    public boolean onEditTextTouch(RongExtension pExtension, View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            EditText editText = pExtension.getEditText();
            if (pExtension.getExtensionClickListener() != null)
                pExtension.getExtensionClickListener().onEditTextClick(editText);
            if (Build.BRAND.toLowerCase().contains("meizu")) {
                editText.requestFocus();
                pExtension.getEmoticonToggle().setSelected(false);
                pExtension.setKeyBoardActive(true);
            } else {
                pExtension.showInputKeyBoard();
            }
            pExtension.getContainerLayout().setSelected(true);
            pExtension.hideEmoticonBoard();
        }
        return false;
    }

    @Override
    public void hideEmoticonBoard(ImageView pEmoticonToggle, EmoticonTabAdapter pEmotionTabAdapter) {
        pEmotionTabAdapter.setVisibility(GONE);
        pEmoticonToggle.setImageResource(R.drawable.rc_emotion_toggle_selector);
    }


}
