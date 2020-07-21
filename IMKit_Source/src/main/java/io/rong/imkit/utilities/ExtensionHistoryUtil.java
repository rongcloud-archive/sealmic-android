package io.rong.imkit.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.Conversation;

public class ExtensionHistoryUtil {
    private static boolean enableHistory;
    private static final String EMOJI_POS = "EMOJI_POS";
    private static final String EXTENSION_BAR_STATE = "EXTENSION_BAR_STATE";
    private static List<Conversation.ConversationType> sExceptConversationTypes = new ArrayList<>();

    /**
     * 设置是否开启记忆功能。
     *
     * @param enable 是否开启
     */
    public static void setEnableHistory(boolean enable) {
        enableHistory = enable;
    }

    /**
     * 设置某类会话不支持记忆功能。例如：客服
     *
     * @param conversationType 会话类型
     */
    public static void addExceptConversationType(Conversation.ConversationType conversationType) {
        sExceptConversationTypes.add(conversationType);
    }

    public static void setEmojiPosition(Context context, String id, int position) {
        if (enableHistory) {
            SharedPreferences sp = context.getSharedPreferences(KitCommonDefine.RONG_KIT_SP_CONFIG, Context.MODE_PRIVATE);
            sp.edit().putInt(id + EMOJI_POS, position).commit();
        }
    }

    public static int getEmojiPosition(Context context, String id) {
        if (!enableHistory) return 0;

        SharedPreferences sp = context.getSharedPreferences(KitCommonDefine.RONG_KIT_SP_CONFIG, Context.MODE_PRIVATE);
        return sp.getInt(id + EMOJI_POS, 0);
    }

    public static void setExtensionBarState(Context context, String id, Conversation.ConversationType conversationType, ExtensionBarState state) {
        if (enableHistory && !sExceptConversationTypes.contains(conversationType)) {
            SharedPreferences sp = context.getSharedPreferences(KitCommonDefine.RONG_KIT_SP_CONFIG, Context.MODE_PRIVATE);
            sp.edit().putString(id + EXTENSION_BAR_STATE, state.toString()).commit();
        }
    }

    public static ExtensionBarState getExtensionBarState(Context context, String id, Conversation.ConversationType conversationType) {
        if (!enableHistory || sExceptConversationTypes.contains(conversationType))
            return ExtensionBarState.NORMAL;

        SharedPreferences sp = context.getSharedPreferences(KitCommonDefine.RONG_KIT_SP_CONFIG, Context.MODE_PRIVATE);
        String v = sp.getString(id + EXTENSION_BAR_STATE, ExtensionBarState.NORMAL.toString());
        return ExtensionBarState.valueOf(v);
    }

    public enum ExtensionBarState {
        NORMAL, VOICE
    }
}
