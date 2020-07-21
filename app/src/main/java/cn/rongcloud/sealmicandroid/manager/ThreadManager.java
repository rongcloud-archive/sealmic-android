package cn.rongcloud.sealmicandroid.manager;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 线程管理类
 */
public class ThreadManager {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));

    private static ExecutorService workThreadExecutor = Executors.newFixedThreadPool(CORE_POOL_SIZE);
    private static Handler mainThreadHandler;
    private static ScheduledExecutorService timerThreadExecutor = Executors.newScheduledThreadPool(10);

    private ThreadManager() {
    }

    public void init() {
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    private static class ThreadManagerHelper {
        private static final ThreadManager INSTANCE = new ThreadManager();
    }

    public static ThreadManager getInstance() {
        return ThreadManagerHelper.INSTANCE;
    }

    public void runOnWorkThread(Runnable runnable) {
        workThreadExecutor.execute(runnable);
    }

    public void runOnUIThread(Runnable runnable) {
        mainThreadHandler.post(runnable);
    }

    /**
     * @param delay 每隔多长时间执行一次，单位秒
     */
    public void runTimeFixedDelay(Runnable runnable, int delay) {
        timerThreadExecutor.scheduleWithFixedDelay(runnable, 0, delay, TimeUnit.SECONDS);
    }
}
