package cn.rongcloud.sealmicandroid.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import cn.rongcloud.sealmicandroid.R;

/**
 * 自定义麦位显示用户名称的组合控件
 */
public class MicTextLayout extends ConstraintLayout {

    private String position;
    private int backgourd;
    private TextView positionTv;
    private TextView nameTv;
    private ImageView imageView;

    public MicTextLayout(Context context) {
        this(context, null);
    }

    public MicTextLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MicTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.mic_position, defStyleAttr, 0);
        initView(context, array);
    }

    private void initView(Context context, TypedArray array) {
        LayoutInflater.from(context).inflate(R.layout.view_text_layout, this);
        positionTv = findViewById(R.id.chatroom_item_text_mic);
        nameTv = findViewById(R.id.chatroom_item_text_name);
        imageView = findViewById(R.id.chatroom_item_text_icon);
        position = array.getString(R.styleable.mic_position_position);
        backgourd = array.getInt(R.styleable.mic_position_backgroudcolor, R.mipmap.chatroom_mic_name_grey);
        positionTv.setText(position);
    }

    public void HasMic(String name) {
        Drawable drawable = getResources().getDrawable(R.mipmap.chatroom_mic_name);
        imageView.setBackground(drawable);
        nameTv.setText(name);
    }

    public void NullMic(String name) {
        Drawable drawable = getResources().getDrawable(R.mipmap.chatroom_mic_name_grey);
        imageView.setBackground(drawable);
        nameTv.setText(name);
    }

    public TextView getTextView() {
        return nameTv;
    }

    public ImageView getImageView() {
        return imageView;
    }

}