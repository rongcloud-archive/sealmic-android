package cn.rongcloud.sealmic.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.sealmic.R;
import cn.rongcloud.sealmic.utils.DisplayUtils;

public class MicSeatRippleView extends View {
    private final static long DEFAULT_AUTO_STOP_RIPPLE_INTERVAL_TIME_MILLIS = 3000;
    private Context mContext;

    // 画笔
    private Paint paint;

    // 控件宽
    private float width;

    // 控件高
    private float height;

    private float maxRadius;

    // 声波的圆圈集合
    private List<Circle> rippleList;

    // 圆圈扩散的速度
    private float speed;

    // 圆圈初始距离
    private int startOffset;

    // 圆圈之间的间距
    private int density;

    // 圆圈的颜色
    private int color;

    // 圆圈是否为填充模式
    private boolean isFill;

    // 圆圈是否为渐隐模式
    private boolean isAlpha;

    // 是否启动动画
    private boolean startRipple = false;

    // 自动取消动画时间
    private long autoStopTimeIntervalMillis = DEFAULT_AUTO_STOP_RIPPLE_INTERVAL_TIME_MILLIS;

    // 开始动画的时间
    private long startRippleTimeMillis;

    public MicSeatRippleView(Context context) {
        this(context, null);
    }

    public MicSeatRippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MicSeatRippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 获取用户配置属性
        TypedArray tya = context.obtainStyledAttributes(attrs, R.styleable.MicSeatRippleView);
        color = tya.getColor(R.styleable.MicSeatRippleView_color, Color.BLUE);
        speed = tya.getFloat(R.styleable.MicSeatRippleView_speed, 1);
        density = tya.getInt(R.styleable.MicSeatRippleView_density, 10);
        isFill = tya.getBoolean(R.styleable.MicSeatRippleView_isFill, false);
        isAlpha = tya.getBoolean(R.styleable.MicSeatRippleView_isAlpha, false);
        startOffset = (int) tya.getDimension(R.styleable.MicSeatRippleView_startOffset, 0);
        tya.recycle();
        init();
    }

    private void init() {
        mContext = getContext();

        // 设置画笔样式
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(DisplayUtils.dp2px(mContext, 1));
        if (isFill) {
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);

        // 添加第一个圆圈
        rippleList = new ArrayList<>();

        density = DisplayUtils.dp2px(mContext, density);

        // 设置View的圆为半透明
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawInCircle(canvas);
    }

    /**
     * 圆到宽度
     *
     * @param canvas
     */
    private void drawInCircle(Canvas canvas) {
        canvas.save();

        // 处理每个圆的宽度和透明度
        for (int i = 0; i < rippleList.size(); i++) {
            Circle c = rippleList.get(i);

            // 当半径超出View的宽度后移除
            if (c.radius > maxRadius) {
                rippleList.remove(i);
            } else {
                paint.setAlpha(c.alpha);
                canvas.drawCircle(width / 2, height / 2, c.radius - paint.getStrokeWidth(), paint);

                // 计算不透明的数值
                if (isAlpha) {
                    double alpha = 255 - (c.radius - startOffset) / (maxRadius - startOffset) * 255;
                    c.alpha = (int) alpha;
                }
                // 修改这个值控制速度
                c.radius += speed;
            }
        }


        // 里面添加圆
        if (startRipple) {
            addRipples();
        }

        invalidate();

        canvas.restore();
    }


    private void addRipples() {
        long currentTimeMillis = System.currentTimeMillis();
        if (autoStopTimeIntervalMillis > 0 && currentTimeMillis - startRippleTimeMillis > autoStopTimeIntervalMillis) {
            startRipple = false;
            return;
        }

        if (rippleList.size() > 0) {
            // 控制第二个圆出来的间距
            if (rippleList.get(rippleList.size() - 1).radius - startOffset == density) {
                rippleList.add(new Circle(startOffset, 255));
            }
        } else {
            rippleList.add(new Circle(startOffset, 255));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        maxRadius = Math.min(width, height) / 2;

        // 设置该view的宽高
        setMeasuredDimension((int) width, (int) height);
    }


    class Circle {
        Circle(int radius, int alpha) {
            this.radius = radius;
            this.alpha = alpha;
        }

        float radius;
        int alpha;
    }

    public void enableRipple(boolean enable) {
        startRipple = enable;

        if (autoStopTimeIntervalMillis > 0) {
            startRippleTimeMillis = System.currentTimeMillis();
        }

        if (enable && rippleList.size() == 0) {
            rippleList.add(new Circle(startOffset, 255));
        }
    }
}
