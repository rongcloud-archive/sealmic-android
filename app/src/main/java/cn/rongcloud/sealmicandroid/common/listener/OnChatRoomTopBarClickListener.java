package cn.rongcloud.sealmicandroid.common.listener;

import android.view.View;

/**
 * 聊天室顶部条点击事件
 */
public interface OnChatRoomTopBarClickListener {

    /**
     * 返回
     * @param v
     */
    void back(View v);

    /**
     * 公告
     */
    void noticeDialog();

    /**
     * 成员
     */
    void lineUpDialog();

    /**
     * 房间设置
     */
    void settingRoomDialog();
}
