package io.rong.imkit.emoticon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;


public interface IEmoticonTab {

    /**
     * 构造 tab 的小图标，用于显示在 tab bar中。
     *
     * @param context 应用上下文。
     * @return 图标的 drawable，不能为 null。
     */
    Drawable obtainTabDrawable(Context context);

    /**
     * 构造 table 页面。
     *
     * @param context 应用上下文。
     * @return 构造后的 table view，不能为 null。
     */
    View obtainTabPager(Context context);

    /**
     * 表情面板左右滑动时，回调此方法。
     *
     * @param position 当前 table 的位置。
     */
    void onTableSelected(int position);
}
