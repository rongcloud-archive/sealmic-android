package io.rong.imkit;

import android.content.Context;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.rong.common.RLog;
import io.rong.imkit.emoticon.EmojiTab;
import io.rong.imkit.emoticon.IEmojiItemClickListener;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

public class DefaultExtensionModule implements IExtensionModule {
    private final static String TAG = DefaultExtensionModule.class.getSimpleName();
    private EditText mEditText;
    private Stack<EditText> stack;
    private String[] types = null;

    public DefaultExtensionModule(Context context) {
        Resources resources = context.getResources();
        try {
            types = resources.getStringArray(resources.getIdentifier("rc_realtime_support_conversation_types", "array", context.getPackageName()));
        } catch (Resources.NotFoundException e) {
            RLog.i(TAG, "not config rc_realtime_support_conversation_types in rc_config.xml");
        }
    }

    public DefaultExtensionModule() {

    }

    @Override
    public void onInit(String appKey) {
        stack = new Stack<>();
    }

    @Override
    public void onConnect(String token) {

    }

    @Override
    public void onAttachedToExtension(RongExtension extension) {
        mEditText = extension.getInputEditText();
        RLog.i(TAG, "attach " + stack.size());
        stack.push(mEditText);
    }

    @Override
    public void onDetachedFromExtension() {
        RLog.i(TAG, "detach " + stack.size());
        if (stack.size() > 0) {
            stack.pop();
            mEditText = stack.size() > 0 ? stack.peek() : null;
        }
    }

    @Override
    public void onReceivedMessage(Message message) {

    }

    @Override
    public List<IEmoticonTab> getEmoticonTabs() {
        EmojiTab emojiTab = new EmojiTab();
        emojiTab.setOnItemClickListener(new IEmojiItemClickListener() {
            @Override
            public void onEmojiClick(String emoji) {
                EditText editText = DefaultExtensionModule.this.mEditText;
                if (editText != null) {
                    int start = editText.getSelectionStart();
                    editText.getText().insert(start, emoji);
                }
            }

            @Override
            public void onDeleteClick() {
                EditText editText = DefaultExtensionModule.this.mEditText;
                if (editText != null) {
                    editText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                }
            }
        });
        List<IEmoticonTab> list = new ArrayList<>();
        list.add(emojiTab);
        return list;
    }

    @Override
    public void onDisconnect() {

    }
}