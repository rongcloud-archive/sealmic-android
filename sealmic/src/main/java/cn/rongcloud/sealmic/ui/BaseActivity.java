package cn.rongcloud.sealmic.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {
    private boolean mEnableListenKeyboardState = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        /*
         * 修复 Android 8.0 手机在TargetSDK 大于 26 时，指定 Activity 方向时崩溃的问题
         */
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            fixOrientation();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mEnableListenKeyboardState) {
            addKeyboardStateListener();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeKeyBoardStateListener();
    }

    /**
     * 隐藏键盘
     */
    public void hideInputKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    /**
     * 设置沉浸式状态栏
     */
    public void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 启动键盘状态监听
     *
     * @param enable
     */
    public void enableKeyboardStateListener(boolean enable) {
        mEnableListenKeyboardState = enable;
    }

    /**
     * 添加键盘显示监听
     */
    private void addKeyboardStateListener() {
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(onKeyboardStateChangedListener);
    }

    /**
     * 移除键盘显示监听
     */
    private void removeKeyBoardStateListener() {
        getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(onKeyboardStateChangedListener);
    }

    /**
     * 监听键盘显示状态
     */
    private ViewTreeObserver.OnGlobalLayoutListener onKeyboardStateChangedListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        int mScreenHeight = 0;

        private int getScreenHeight() {
            if (mScreenHeight > 0) {
                return mScreenHeight;
            }
            Point point = new Point();
            ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
            mScreenHeight = point.y;
            return mScreenHeight;
        }

        @Override
        public void onGlobalLayout() {
            Rect rect = new Rect();
            // 获取当前窗口显示范围
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            int screenHeight = getScreenHeight();
            int keyboardHeight = screenHeight - rect.bottom; // 输入法的高度
            boolean isActive = false;
            if (Math.abs(keyboardHeight) > screenHeight / 4) {
                isActive = true; // 超过屏幕1/4则表示弹出了输入法
            }

            onKeyboardStateChanged(isActive, keyboardHeight);
        }
    };

    /**
     * 当软键盘显示时回调
     * 此回调在调用{@link BaseActivity#enableKeyboardStateListener(boolean)}启用监听键盘显示
     *
     * @param isShown
     * @param height
     */
    public void onKeyboardStateChanged(boolean isShown, int height) {

    }

    /**
     * 判断当前主题是否是透明悬浮
     *
     * @return
     */
    private boolean isTranslucentOrFloating() {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    /**
     * 改变当前的 Activity 的显示方向
     * 解决当前Android 8.0 系统在透明主题时设定显示方向时崩溃的问题
     *
     * @return
     */
    private boolean fixOrientation() {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        /*
         * 修复 Android 8.0 手机在TargetSDK 大于 26 时，指定 Activity 方向时崩溃的问题
         */
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            return;
        }
        super.setRequestedOrientation(requestedOrientation);
    }

}
