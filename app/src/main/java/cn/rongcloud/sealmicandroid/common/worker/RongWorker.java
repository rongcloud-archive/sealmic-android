package cn.rongcloud.sealmicandroid.common.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import cn.rongcloud.sealmicandroid.BuildConfig;
import cn.rongcloud.sealmicandroid.util.log.SLog;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.RongExtensionManager;

/**
 * WorkManager初始化类
 */
public class RongWorker extends Worker {

    private static final String RONG_WORKER = "RongWorker";

    public RongWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //表情插件控件初始化
        RongExtensionManager.init(getApplicationContext(), BuildConfig.Rong_key);
        RongExtensionManager.getInstance().registerExtensionModule(
                new DefaultExtensionModule(getApplicationContext()));
        SLog.d(RONG_WORKER, "init success");
        return Result.success();
    }
}
