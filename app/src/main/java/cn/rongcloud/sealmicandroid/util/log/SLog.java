package cn.rongcloud.sealmicandroid.util.log;

import android.content.Context;

public class SLog {
    public static final String TAG_SEAL_MIC = "SealMic";

    public static final String TAG_NET = "SealMicNet";

    public static final String TAG_TASK = "SealMicTask";

    public static final String TAG_IM = "SealMicIM";

    public static final String TAG_RTC = "SealMicRTC";

    public static void init(Context context) {
        SLogCreator.LOG_INSTANCE.init(context);
    }

    public static void i(String tag, String msg) {
        SLogCreator.LOG_INSTANCE.i(tag, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        SLogCreator.LOG_INSTANCE.i(tag, msg, tr);
    }

    public static void v(String tag, String msg) {
        SLogCreator.LOG_INSTANCE.v(tag, msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        SLogCreator.LOG_INSTANCE.v(tag, msg, tr);
    }

    public static void d(String tag, String msg) {
        SLogCreator.LOG_INSTANCE.d(tag, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        SLogCreator.LOG_INSTANCE.d(tag, msg, tr);
    }

    public static void w(String tag, String msg) {
        SLogCreator.LOG_INSTANCE.w(tag, msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        SLogCreator.LOG_INSTANCE.w(tag, msg, tr);
    }

    public static void e(String tag, String msg) {
        SLogCreator.LOG_INSTANCE.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        SLogCreator.LOG_INSTANCE.e(tag, msg, tr);
    }

    private static class SLogCreator {
        // 使用其他Log请替换此实现
        public final static ISLog LOG_INSTANCE = new SimpleDebugSLog();
    }
}
