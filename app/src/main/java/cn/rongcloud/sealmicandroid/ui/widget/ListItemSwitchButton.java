package cn.rongcloud.sealmicandroid.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.rongcloud.sealmicandroid.R;

public class ListItemSwitchButton extends LinearLayout {
    private SwitchButton switchButton;
    private TextView listItemTitle;

    public ListItemSwitchButton(Context context) {
        super(context);
        initView(null);
    }

    public ListItemSwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_switch_button_list_item, this);
        switchButton = findViewById(R.id.switch_button);
        listItemTitle = findViewById(R.id.tv_title);
        if (attrs != null) {
            TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.swb_list_item);
            CharSequence title = attributes.getText(R.styleable.swb_list_item_swb_list_item_title);
            Drawable drawable = attributes.getDrawable(R.styleable.swb_list_item_swb_list_item_button_drawable);
            listItemTitle.setText(title);
            if (drawable != null) {
                switchButton.setBackDrawable(drawable);
            }
            attributes.recycle();
        }
    }

    public void setSwitchButtonChangedListener(CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        switchButton.setOnCheckedChangeListener(checkedChangeListener);
    }

    public void setChecked(boolean checked) {
        switchButton.setChecked(checked);
    }

    public boolean isChecked() {
        return switchButton.isChecked();
    }

    public void setCheckedImmediately(boolean checked) {
        switchButton.setCheckedImmediately(checked);
    }

    public void setSwitchButtonClickListener(OnClickListener clickListener) {
        switchButton.setOnClickListener(clickListener);
    }

    public void setContent(String content) {
        listItemTitle.setText(content);
    }

    public TextView getContent(){
        return listItemTitle;
    }

    public void setContentColor(int color) {
        listItemTitle.setTextColor(color);
    }

    public void setListItemSwitchButtonAlpha(float alpha) {
        setAlpha(alpha);
    }
}
