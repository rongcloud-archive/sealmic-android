package cn.rongcloud.sealmic.ui.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.rongcloud.sealmic.R;
import cn.rongcloud.sealmic.utils.DisplayUtils;

/**
 * 底部弹出选项对话框
 * 可以自定义选项内容，通过 {@link BottomSelectDialog.Builder} 可以创建实例
 */
public class BottomSelectDialog extends BaseFullScreenDialog implements View.OnClickListener {
    private static final String BUNDLE_KEY_ITEM_LIST = "item_list";
    private static final String BUNDLE_KEY_HAS_CANCEL = "has_cancel";
    private static final String BUNDLE_KEY_ITEM_LISTENER = "item_listener";

    private View contentView;
    private OnItemClickListener onItemClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //创建父容器
        Context context = inflater.getContext();
        LinearLayout parentView = new LinearLayout(context);
        parentView.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        parentView.setLayoutParams(layoutParams);
        parentView.setBackgroundColor(context.getResources().getColor(R.color.white));

        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList<String> itemList = bundle.getStringArrayList(BUNDLE_KEY_ITEM_LIST);
            if (itemList != null && itemList.size() > 0) {
                int itemTextColor = context.getResources().getColor(R.color.text_black);//列表选项字体颜色
                int separateHeight = DisplayUtils.dp2px(context, 1);//分割线高度
                int separateColor = context.getResources().getColor(R.color.separate_line_gray);//分割线颜色
                View itemView;
                View separateView;
                int size = itemList.size();
                for (int i = 0; i < size; i++) {
                    String content = itemList.get(i);
                    // 生成列表选项
                    itemView = createSelectItem(inflater, parentView, content, itemTextColor);
                    itemView.setOnClickListener(this);
                    itemView.setTag(R.id.common_bottom_select_item_tag, i);
                    parentView.addView(itemView);

                    // 生成分割线
                    separateView = createSeparateView(context, parentView, separateHeight, separateColor);
                    parentView.addView(separateView);
                }
            }

            // 是否需要有取消按钮
            boolean hasCancel = bundle.getBoolean(BUNDLE_KEY_HAS_CANCEL);
            if (hasCancel) {
                int cancelTextColor = context.getResources().getColor(R.color.text_gray);
                View cancelView = createSelectItem(inflater, parentView, getString(R.string.cancel), cancelTextColor);
                //将-1作为取消按钮的下标
                cancelView.setOnClickListener(this);
                cancelView.setTag(R.id.common_bottom_select_item_tag, -1);
                parentView.addView(cancelView);
            } else {
                int childCount = parentView.getChildCount();
                //去除最后一条分割项
                if (childCount > 1) {
                    parentView.removeViewAt(childCount - 1);
                }
            }

            onItemClickListener = (OnItemClickListener) bundle.getSerializable(BUNDLE_KEY_ITEM_LISTENER);
        }

        // 设置在底部显示对话框
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        contentView = parentView;
        return parentView;
    }

    // 生成列表项
    private View createSelectItem(LayoutInflater inflater, ViewGroup parentView, String content, int textColor) {
        View selectItemView = inflater.inflate(R.layout.common_item_bottom_select, parentView, false);
        TextView itemContentTv = selectItemView.findViewById(R.id.common_item_tv_content);
        itemContentTv.setText(content);
        itemContentTv.setTextColor(textColor);
        return selectItemView;
    }

    // 生成分割项
    private View createSeparateView(Context context, ViewGroup parentView, int height, int separateColor) {
        View separateV = new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        separateV.setLayoutParams(params);
        separateV.setBackgroundColor(separateColor);
        return separateV;
    }


    @Override
    public void onResume() {
        super.onResume();

        // 设置底部飞入动画
        contentView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_in));
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            Object tag = v.getTag(R.id.common_bottom_select_item_tag);
            if (tag != null) {
                int index = (int) tag;
                if (index != -1) {
                    onItemClickListener.onItemClick(index);
                }
            }
        }
        dismiss();
    }

    /**
     * 通过此类可以创建对话框
     */
    public static class Builder {
        private ArrayList<String> items = new ArrayList<>();
        private boolean hasCancel = true;
        private OnItemClickListener onItemClickListener;

        /**
         * 创建选项列表
         *
         * @param itemList 选项列表
         */
        public Builder(List<String> itemList) {
            items.addAll(itemList);
        }

        /**
         * 设置是否有取消按钮
         *
         * @param hasCancel
         */
        public Builder hasCancel(boolean hasCancel) {
            this.hasCancel = hasCancel;
            return this;
        }

        /**
         * 设置列表项点击事件
         *
         * @param listener
         */
        public Builder setOnItemClickListener(OnItemClickListener listener) {
            this.onItemClickListener = listener;
            return this;
        }

        public BottomSelectDialog build() {
            BottomSelectDialog dialog = new BottomSelectDialog();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(BUNDLE_KEY_ITEM_LIST, items);
            bundle.putBoolean(BUNDLE_KEY_HAS_CANCEL, hasCancel);
            bundle.putSerializable(BUNDLE_KEY_ITEM_LISTENER, onItemClickListener);
            dialog.setArguments(bundle);
            return dialog;
        }
    }

    /**
     * 列表项点击监听
     */
    public interface OnItemClickListener extends Serializable {
        /**
         * 当列表项点击时回调
         *
         * @param index 列表项中的下标
         */
        void onItemClick(int index);
    }
}
