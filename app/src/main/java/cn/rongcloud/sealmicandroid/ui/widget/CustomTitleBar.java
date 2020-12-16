package cn.rongcloud.sealmicandroid.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.rongcloud.sealmicandroid.R;
import cn.rongcloud.sealmicandroid.manager.GlideManager;
import cn.rongcloud.sealmicandroid.util.BitmapUtil;
import cn.rongcloud.sealmicandroid.util.DisplayUtil;

public class CustomTitleBar extends RelativeLayout implements View.OnClickListener, View.OnLongClickListener {


    private String leftTitle;
    private String middleTitle;
    private String rightTitle;

    private int leftTextColor;
    private int middleTextColor;
    private int rightTextColor;

    private float leftTextSize;
    private float rightTextSize;
    private float middleTextSize;

    private TextView tvLeft;
    private TextView tvMiddle;
    private CustomTextView tvRight;

    private int leftImage;
    private int rightImage;
    private int rightImage2;
    private ImageView ivRight;

    private TitleClickListener listener;

    public CustomTitleBar(Context context) {
        this(context, null);
    }

    public CustomTitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomTitleBar, defStyleAttr, 0);
        initView(array);
        array.recycle();
    }

    private void initView(TypedArray array) {

        LayoutInflater.from(getContext()).inflate(R.layout.view_titlebar, this);

        tvLeft = findViewById(R.id.tvLeftTitle);
        tvLeft.setOnClickListener(this);
        tvMiddle = findViewById(R.id.tvMiddleTitle);
        tvMiddle.setOnClickListener(this);
        tvMiddle.setOnLongClickListener(this);
        tvRight = findViewById(R.id.tvRightTitle);
        tvRight.setOnClickListener(this);
        ivRight = findViewById(R.id.ivRight);
        ivRight.setOnClickListener(this);


        leftTitle = array.getString(R.styleable.CustomTitleBar_leftTitle);
        middleTitle = array.getString(R.styleable.CustomTitleBar_middleTitle);
        rightTitle = array.getString(R.styleable.CustomTitleBar_rightTitle);

        leftTextColor = array.getColor(R.styleable.CustomTitleBar_leftTextColor, Color.BLACK);
        middleTextColor = array.getColor(R.styleable.CustomTitleBar_middleTextColor, Color.BLACK);
        rightTextColor = array.getColor(R.styleable.CustomTitleBar_rightTextColor, Color.BLACK);

        leftImage = array.getResourceId(R.styleable.CustomTitleBar_leftImage, 0);
        rightImage = array.getResourceId(R.styleable.CustomTitleBar_rightImage, 0);
        rightImage2 = array.getResourceId(R.styleable.CustomTitleBar_rightImage2, 0);


        leftTextSize = array.getDimension(R.styleable.CustomTitleBar_leftTextSize, DisplayUtil.dp2px(getContext(), 15));
        rightTextSize = array.getDimension(R.styleable.CustomTitleBar_rightTextSize, DisplayUtil.dp2px(getContext(), 17));
        middleTextSize = array.getDimension(R.styleable.CustomTitleBar_middleTextSize, DisplayUtil.dp2px(getContext(), 15));

        if (leftImage > 0) {
            setLeftImage(leftImage);
        } else {
            setLeftTitle(leftTitle);
        }

        if (rightImage > 0) {
            setRightImage(rightImage);
        } else {
            setRightTitle(rightTitle);
        }

        if (rightImage > 0 && rightImage2 > 0) {
            setRightImage(rightImage, rightImage2);
        }

        tvLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize);
        tvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize);
        tvMiddle.setTextSize(TypedValue.COMPLEX_UNIT_PX, middleTextSize);


//        tvRight.setDrawableLeftListener(new CustomTextView.DrawableLeftListener() {
//            @Override
//            public void onDrawableLeftClick(View view) {
//                listener.onRightButton1Click();
//            }
//        });
//        tvRight.setDrawableRightListener(new CustomTextView.DrawableRightListener() {
//            @Override
//            public void onDrawableRightClick(View view) {
//                listener.onRightButton2Click();
//            }
//        });

        setMiddleTitle(middleTitle);
        setLeftTextColor(leftTextColor);
        setMiddleTextColor(middleTextColor);
        setRightTextColor(rightTextColor);
    }

    /**
     * @param size 单位sp
     */
    public void setLeftTextSize(float size) {
        tvLeft.setTextSize(size);
    }

    /**
     * 隐藏创建房间页面左边的Title
     */
    public void setTvLeftIsGong() {
        tvLeft.setVisibility(GONE);
    }

    /**
     * @param size 单位sp
     */
    public void setMiddleTextSize(float size) {
        tvMiddle.setTextSize(size);
    }

    /**
     * 隐藏中间Title
     */
    public void setTvMiddleIsGong() {
        tvMiddle.setVisibility(GONE);
    }

    /**
     * @param size 单位sp
     */
    public void setRightTextSize(float size) {
        tvRight.setTextSize(size);
    }

    public void hideRightText() {
        tvRight.setVisibility(GONE);
    }

    public void showRightText() {
        tvRight.setVisibility(VISIBLE);
    }

    public void setLeftTextColor(int color) {
        tvLeft.setTextColor(color);
    }

    public void setMiddleTextColor(int color) {
        tvMiddle.setTextColor(color);
    }

    public void setRightTextColor(int color) {
        tvRight.setTextColor(color);
    }


    public void setLeftTitle(String title) {
        tvLeft.setText(title);
    }

    public void setRightTitle(String title) {
        tvRight.setText(title);
    }


    public void setMiddleTitle(int titleId) {
        tvMiddle.setText(titleId);
    }

    public void setMiddleTitle(String title) {
        tvMiddle.setText(title);
    }

    public void setLeftImage(int leftImage) {

        setLeftTitle(leftTitle);
        Drawable drawable = getResources().getDrawable(leftImage);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvLeft.setCompoundDrawablePadding(DisplayUtil.dp2px(getContext(), 8));
        tvLeft.setCompoundDrawables(drawable, null, null, null);
    }


    public void setRightImage(int rightImage) {
        setRightTitle(rightTitle);
        Drawable drawable = getResources().getDrawable(rightImage);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvRight.setCompoundDrawablePadding(DisplayUtil.dp2px(getContext(), 8));
        tvRight.setCompoundDrawables(null, null, drawable, null);
    }

    public void setRightImage(int dp, Bitmap bitmap) {
        Drawable drawable = BitmapUtil.bitmapToDrawable(bitmap, getContext());
        tvRight.setCompoundDrawablePadding(DisplayUtil.dp2px(getContext(), dp));
        tvRight.setCompoundDrawables(null, null, drawable, null);
    }

    public void setRightImage(int rightImage1, int rightImage2) {

        setRightTitle(rightTitle);
        Drawable drawable1 = getResources().getDrawable(rightImage1);
        Drawable drawable2 = getResources().getDrawable(rightImage2);
        drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
        drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
        tvRight.setCompoundDrawablePadding(DisplayUtil.dp2px(getContext(), 8));
        tvRight.setCompoundDrawables(drawable1, null, drawable2, null);
    }

    public void setRightUrl(String url) {
        GlideManager.getInstance().setUrlImage(this, url, ivRight);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvLeftTitle:
                if (listener != null) {
                    listener.onLeftClick();
                }
                break;
            case R.id.tvRightTitle:
            case R.id.ivRight:
                if (listener != null) {
                    listener.onRightClick();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (listener != null) {
            listener.onTitleLongClick();
        }
        return true;
    }

    public void setTitleClickListener(TitleClickListener listener) {
        this.listener = listener;
    }

    public interface TitleClickListener {

        /**
         * 点击左边区域
         */
        void onLeftClick();

        /**
         * 点击右边区域
         */
        void onRightClick();

        /**
         * 长按标题区域
         */
        void onTitleLongClick();

//        void onRightButton1Click();
//
//        void onRightButton2Click();
    }

}
