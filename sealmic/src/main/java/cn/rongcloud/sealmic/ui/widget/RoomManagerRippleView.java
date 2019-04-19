package cn.rongcloud.sealmic.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmic.R;

public class RoomManagerRippleView extends View {
    private final static long DEFAULT_AUTO_STOP_RIPPLE_INTERVAL_TIME_MILLIS = 3000;
    private Context context;

    // 圆圈集合
    private List<Circle> circleList;

    // 圆圈初始半径
    private int startRadius;

    // 圆圈最大半径
    private int maxRadius;

    // 第一个圆圈的颜色
    private int firstColor;

    // 第二个圆圈的颜色
    private int secondColor;

    // View宽
    private float mWidth;

    // View高
    private float mHeight;

    private long autoStopTimeIntervalMillis = DEFAULT_AUTO_STOP_RIPPLE_INTERVAL_TIME_MILLIS;

    private long startRippleTimeMillis;


    private AnimatorSet animatorSet;

    private boolean startAnimator = true;

    private long duration;

    public RoomManagerRippleView(Context context) {
        this(context, null);
    }

    public RoomManagerRippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoomManagerRippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray tya = context.obtainStyledAttributes(attrs, R.styleable.RoomManagerRippleView);
        firstColor = tya.getColor(R.styleable.RoomManagerRippleView_firstColor, Color.BLUE);
        secondColor = tya.getColor(R.styleable.RoomManagerRippleView_secondColor, Color.argb(60, 0, 0, 255));
        duration = (long) tya.getInt(R.styleable.RoomManagerRippleView_duration, 1000);
        startRadius = (int) tya.getDimension(R.styleable.RoomManagerRippleView_startRadius, 0);
        maxRadius = (int) tya.getDimension(R.styleable.RoomManagerRippleView_maxRadius, 0);
        tya.recycle();
        init();
    }

    private void init() {
        context = getContext();
        circleList = new ArrayList<>();

        // 第两个圆的半径
        int firstCircleMaxRadius = (maxRadius - startRadius) / 2 + startRadius;

        // 添加第一个圆圈
        Paint paint = new Paint();
        paint.setColor(firstColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        Circle firstCircle = new Circle(startRadius, firstCircleMaxRadius, paint);

        circleList.add(firstCircle);

        // 添加第二个圆圈
        paint = new Paint();
        paint.setColor(secondColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        Circle secondCircle = new Circle(startRadius, maxRadius, paint);

        circleList.add(secondCircle);

        // 设置透明背景
        setBackgroundColor(Color.TRANSPARENT);

        // 动画
        ObjectAnimator firstAnimator = ObjectAnimator.ofInt(firstCircle, "radius", firstCircle.startRadius, firstCircle.maxRadius);
        firstAnimator.setInterpolator(new DecelerateInterpolator(6));
        firstAnimator.setDuration((int) (duration * 0.75));
        firstAnimator.setRepeatCount(1);
        firstAnimator.setRepeatMode(ValueAnimator.REVERSE);

        ObjectAnimator secondAnimator = ObjectAnimator.ofInt(secondCircle, "radius", secondCircle.startRadius, secondCircle.maxRadius);
        secondAnimator.setInterpolator(new DecelerateInterpolator(1));
        secondAnimator.setDuration(duration);
        secondAnimator.setRepeatCount(1);
        secondAnimator.setRepeatMode(ValueAnimator.REVERSE);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(firstAnimator, secondAnimator);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                long currentTimeMillis = System.currentTimeMillis();
                if (autoStopTimeIntervalMillis > 0 && currentTimeMillis - startRippleTimeMillis > autoStopTimeIntervalMillis) {
                    startAnimator = false;
                    return;
                }

                if (startAnimator) {
                    animatorSet.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCircle(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 设置该view的宽高
        setMeasuredDimension((int) mWidth, (int) mHeight);
    }

    /**
     * 圆到宽度
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        canvas.save();

        // 处理每个圆的宽度和透明度
        for (int i = 0; i < circleList.size(); i++) {
            Circle c = circleList.get(i);
            canvas.drawCircle(mWidth / 2, mHeight / 2, c.radius, c.paint);
        }

        invalidate();
        canvas.restore();
    }

    class Circle {
        int radius;
        int startRadius;
        int maxRadius;
        Paint paint;

        Circle(int startRadius, int maxRadius, Paint paint) {
            this.startRadius = startRadius;
            this.maxRadius = maxRadius;
            this.radius = startRadius;
            this.paint = paint;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }
    }

    public void enableRipple(boolean enable) {
        startAnimator = enable;

        if (autoStopTimeIntervalMillis > 0) {
            startRippleTimeMillis = System.currentTimeMillis();
        }

        if (enable && !animatorSet.isRunning()) {
            animatorSet.start();
        }
    }
}
