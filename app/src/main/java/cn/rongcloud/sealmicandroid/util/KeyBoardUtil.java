package cn.rongcloud.sealmicandroid.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imlib.model.Message;

/**
 * 键盘工具类
 */
public class KeyBoardUtil {

    private static final String KEY_SOFT_KEYBOARD_HEIGHT = "SoftKeyboardHeight";
    private static final int SOFT_KEYBOARD_HEIGHT_DEFAULT = 654;

    public static String shortMD5(List<String> args) {
        String time = System.currentTimeMillis() + "";
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(time);
            for (String arg : args) {
                builder.append(arg);
            }

            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(builder.toString().getBytes());
            byte[] mds = mdInst.digest();
            mds = Base64.encode(mds, Base64.NO_WRAP);
            String result = new String(mds);
            result = result.replace("=", "").replace("+", "-").replace("/", "_").replace("\n", "");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static boolean checkIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> apps = packageManager.queryIntentActivities(intent, 0);
        return apps.size() > 0;
    }

    private static Map<String, Long> mapLastClickTime = new HashMap<>();

    /**
     * double click
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        return isFastDoubleClick("Default");
    }

    public static boolean isFastDoubleClick(String eventType) {
        Long lastClickTime = mapLastClickTime.get(eventType);
        if (lastClickTime == null) {
            lastClickTime = 0L;
        }
        long curTime = System.currentTimeMillis();
        long timeD = curTime - lastClickTime;
        if (timeD > 0 && timeD < 800) {
            return true;
        }
        mapLastClickTime.put(eventType, curTime);
        return false;
    }

    /**
     * 关闭软键盘
     *
     * @param activity
     * @param view
     */
    public static void closeKeyBoard(Activity activity, View view) {
        IBinder token;
        if (view == null || view.getWindowToken() == null) {
            if (null == activity) {
                return;
            }
            Window window = activity.getWindow();
            if (window == null) {
                return;
            }
            View v = window.peekDecorView();
            if (v == null) {
                return;
            }
            token = v.getWindowToken();
        } else {
            token = view.getWindowToken();
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token, 0);
    }

    public static void showKeyBoard(Activity activity, EditText editText) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null) {
            return info.isConnected();
        }
        return false;
    }

//    public static void showErrorMessageToast(Context context, String tip) {
//        View toastLayout = LayoutInflater.from(context).inflate(R.layout.rce_toast_tip, null);
//        TextView contentView = (TextView) toastLayout.findViewById(R.id.toast_content);
//        Toast toast = new Toast(context);
//        toast.setGravity(Gravity.BOTTOM, 0, 120);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.setView(toastLayout);
//        contentView.setText(tip);
//        toast.show();
//    }

//    public static int getVersionCode(Context context) {
//        PackageManager packageManager = context.getPackageManager();
//        int versionCode = 0;
//        try {
//            versionCode = packageManager.getPackageInfo(context.getPackageName(), 0).versionCode;
//            return versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return BuildConfig.VERSION_CODE;
//    }

    public static String getApplicationName(Context context) {
        PackageManager packageManager;
        String applicationName = null;
        try {
            packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return applicationName;
    }

    /**
     * 当前是不是需要在展示缩略图，暂无法判断，此功能在评定中。
     *
     * @param message
     * @return
     */
    public static boolean isShowImageThumb(Message message) {
        return false;
    }


    /**
     * 解决 toast 长文字不居中的问题
     */
    public static void showToastCenter(Context context, String toastStr) {
        Toast toast = Toast.makeText(context.getApplicationContext(), toastStr, Toast.LENGTH_SHORT);
        int tvToastId = Resources.getSystem().getIdentifier("message", "id", "android");
        TextView tvToast = ((TextView) toast.getView().findViewById(tvToastId));
        if (tvToast != null) {
            tvToast.setGravity(Gravity.CENTER);
        }
        toast.show();
    }

    /**
     * 获取键盘的高度
     */
    public static int getSoftKeyboardHeight(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //屏幕当前可见高度，不包括状态栏
        int displayHeight = rect.bottom - rect.top;
        //屏幕可用高度
        int availableHeight = DisplayUtil.getAvailableScreenHeight(activity);
        //用于计算键盘高度
        int softInputHeight = availableHeight - displayHeight - DisplayUtil.getStatusBarHeight(activity);
        Log.e("TAG-di", displayHeight + "");
        Log.e("TAG-av", availableHeight + "");
        Log.e("TAG-so", softInputHeight + "");
        if (softInputHeight != 0) {
            // 因为考虑到用户可能会主动调整键盘高度，所以只能是每次获取到键盘高度时都将其存储起来
            SPUtil.put(activity, KEY_SOFT_KEYBOARD_HEIGHT, softInputHeight);
        }
        return softInputHeight;
    }

    /**
     * 获取本地存储的键盘高度值或者是返回默认值
     */
    private Object getSoftKeyboardHeightLocalValue(Activity activity) {
        return SPUtil.get(activity, KEY_SOFT_KEYBOARD_HEIGHT, SOFT_KEYBOARD_HEIGHT_DEFAULT);
    }
}
