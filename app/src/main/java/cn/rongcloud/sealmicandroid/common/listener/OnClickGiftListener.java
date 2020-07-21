package cn.rongcloud.sealmicandroid.common.listener;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * 点击礼物对应的监听器
 */
public interface OnClickGiftListener {

    /**
     * 点击礼物图标
     *
     * @param view     点击的视图本身
     * @param drawable 礼物对应的图片
     * @param position 点击的是第几个
     */
    void onClickGift(View view, Drawable drawable, int position);
}
