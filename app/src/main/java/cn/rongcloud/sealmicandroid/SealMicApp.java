package cn.rongcloud.sealmicandroid;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;

import java.util.Iterator;
import java.util.List;

import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.sealmicandroid.common.Event;
import cn.rongcloud.sealmicandroid.common.service.RTCNotificationService;
import cn.rongcloud.sealmicandroid.common.worker.RongWorker;
import cn.rongcloud.sealmicandroid.im.IMClient;
import cn.rongcloud.sealmicandroid.manager.CacheManager;
import cn.rongcloud.sealmicandroid.manager.ThreadManager;
import cn.rongcloud.sealmicandroid.net.client.HttpClient;
import cn.rongcloud.sealmicandroid.rtc.RTCClient;
import cn.rongcloud.sealmicandroid.util.log.SLog;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;

/**
 * 项目application
 */
public class SealMicApp extends MultiDexApplication {

    private static SealMicApp sealMicAppInstance;
    private int activeCount = 0;
    private int aliveCount = 0;
    private boolean isActive;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sealMicAppInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //init日志
        SLog.init(this);

        /*
         * 以上部分在所有进程中会执行
         */
        if (!getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            return;
        }
        /*
         * 以下部分仅在主进程中进行执行
         */

        //init rong im rtc
        WorkManager workManager = WorkManager.getInstance(this);
        Constraints imConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest rongWorkRequest = new OneTimeWorkRequest.Builder(RongWorker.class)
                .setConstraints(imConstraints)
                .build();
        workManager.enqueue(rongWorkRequest);

        //初始化IM
        IMClient.getInstance().init(getApplicationContext());
        //初始化MediaServer
        RTCClient.getInstance().initMediaServer();
        //初始化网络请求
        HttpClient.getInstance().init(getApplicationContext());
        //初始化后清除掉请求认证缓存，保证每次登录都使用不用的用户
        HttpClient.getInstance().clearRequestCache();
        //初始化线程管理
        ThreadManager.getInstance().init();
        //初始化initBugly
        initBugly();

        registerLifecycleCallbacks();

        connectIM();
    }

    /**
     * 获取当前 Application 实例
     */
    public static SealMicApp getApplication() {
        return sealMicAppInstance;
    }

    private void initBugly() {
        //bugly
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getCurProcessName(getApplication());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        CrashReport.initCrashReport(context, BuildConfig.Bugly_app_id, false, strategy);
    }

    /**
     * 获取当前进程的名称
     */
    public String getCurProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (mActivityManager == null) {
            return "";
        }
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = mActivityManager.getRunningAppProcesses();
        if (runningAppProcessInfoList == null) {
            return null;
        } else {
            Iterator processInfoIterator = runningAppProcessInfoList.iterator();

            ActivityManager.RunningAppProcessInfo appProcess;
            do {
                if (!processInfoIterator.hasNext()) {
                    return null;
                }

                appProcess = (ActivityManager.RunningAppProcessInfo) processInfoIterator.next();
            } while (appProcess.pid != pid);

            return appProcess.processName;
        }
    }

    private void registerLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                aliveCount++;
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                activeCount++;
                notifyChange();
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                activeCount--;
                notifyChange();
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                aliveCount--;
                if (aliveCount == 0) {
                    stopNotificationService();
                }
            }
        });
    }

    private void notifyChange() {
        if (activeCount > 0) {
            if (!isActive) {
                isActive = true;
                // AppForeground
                stopNotificationService();
            }
        } else {
            if (isActive) {
                isActive = false;
                // AppBackground
                if (RCRTCEngine.getInstance().getRoom() != null) {
                    startService(new Intent(this, RTCNotificationService.class));
                }
            }
        }
    }

    private void stopNotificationService() {
        if (RCRTCEngine.getInstance().getRoom() != null) {
            stopService(new Intent(SealMicApp.this, RTCNotificationService.class));
        }
    }

    private void connectIM() {
        String token = CacheManager.getInstance().getToken();
        if (token.isEmpty()) {
            EventBus.getDefault().postSticky(new Event.UserTokenLose());
            return;
        }
        SLog.e(SLog.TAG_SEAL_MIC, "token: " + token);
        IMClient.getInstance().connect(token, new IRongCoreCallback.ConnectCallback() {
            @Override
            public void onSuccess(String s) {
                SLog.e(SLog.TAG_SEAL_MIC, "IM连接成功");
            }

            @Override
            public void onError(IRongCoreEnum.ConnectionErrorCode connectionErrorCode) {
                SLog.e(SLog.TAG_SEAL_MIC, "IM连接失败，错误码为: " + connectionErrorCode.toString());
                if (connectionErrorCode.equals(IRongCoreEnum.ConnectionErrorCode.RC_CONN_TOKEN_INCORRECT)) {
                    //从 获取新 token，并重连,发送Event
                    EventBus.getDefault().postSticky(new Event.UserTokenLose());
                }
            }

            @Override
            public void onDatabaseOpened(IRongCoreEnum.DatabaseOpenStatus databaseOpenStatus) {

            }
        });
    }

}
