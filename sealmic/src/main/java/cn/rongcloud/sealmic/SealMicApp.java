package cn.rongcloud.sealmic;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import java.util.Iterator;
import java.util.List;

import androidx.multidex.MultiDexApplication;
import cn.rongcloud.sealmic.im.IMClient;
import cn.rongcloud.sealmic.net.HttpClient;
import cn.rongcloud.sealmic.task.ThreadManager;
import cn.rongcloud.sealmic.utils.ResourceUtils;
import cn.rongcloud.sealmic.utils.log.SLog;

public class SealMicApp extends MultiDexApplication {
    private static SealMicApp instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // 初始化日志打印
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
        // 初始化网络请求
        HttpClient.getInstance().init(this);
        // 初始化线程管理
        ThreadManager.getInstance().init(this);
        // 初始化 IM
        IMClient.getInstance().init(this);
        // 初始化资源管理
        ResourceUtils.getInstance().init(this);
    }


    /**
     * 获取当前进程的名称
     *
     * @param context
     * @return
     */
    public String getCurProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
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

    /**
     * 获取当前 Application 实例
     *
     * @return
     */
    public static SealMicApp getApplication() {
        return instance;
    }
}
