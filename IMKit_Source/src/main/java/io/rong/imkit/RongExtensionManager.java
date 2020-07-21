package io.rong.imkit;

import android.content.Context;
import android.text.TextUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imlib.model.Message;

public class RongExtensionManager {

    private final static String TAG = "RongExtensionManager";

    private static String mAppKey;
    private static List<IExtensionModule> mExtModules = new ArrayList<>();
    private final static String DEFAULT_REDPACKET = "com.jrmf360.rylib.modules.JrmfExtensionModule";
    private final static String DEFAULT_BQMM = "com.melink.bqmmplugin.rc.BQMMExtensionModule";
    private final static String DEFAULT_RC_STICKER = "io.rong.sticker.StickerExtensionModule";
    private static IExtensionProxy extensionProxy;

    private RongExtensionManager() {
        if (mExtModules != null) {
            try {
                Class<?> cls = Class.forName(DEFAULT_REDPACKET);
                Constructor<?> constructor = cls.getConstructor();
                IExtensionModule jrmf = (IExtensionModule) constructor.newInstance();
                RLog.i(TAG, "add module " + jrmf.getClass().getSimpleName());
                mExtModules.add(jrmf);
                jrmf.onInit(mAppKey);
            } catch (Exception e) {
                //do nothing
            }

            checkRCBQ();
        }
    }

    private static class SingletonHolder {
        static RongExtensionManager sInstance = new RongExtensionManager();
    }

    public static RongExtensionManager getInstance() {
        return SingletonHolder.sInstance;
    }

    /**
     * 初始化，SDK 在初始化时已调用此方法，用户不需要再调用。
     *
     * @param context 应用上下文.
     * @param appKey  应用 key.
     */
    public static void init(Context context, String appKey) {
        RLog.d(TAG, "init");

        AndroidEmoji.init(context);
        RongUtils.init(context);
        mAppKey = appKey;
    }

    /**
     * 设置 Extension 代理，在代理方法执行时，进行自定义修改。
     *
     * @param proxy 代理实例
     */
    public static void setExtensionProxy(IExtensionProxy proxy) {
        extensionProxy = proxy;
    }

    static IExtensionProxy getExtensionProxy() {
        return extensionProxy;
    }

    /**
     * 注册自定义的 {@link IExtensionModule},注册后，可以通过 {@link #getExtensionModules()} 获取已注册的 module
     * <pre>
     * 注意：
     * 1. 请在 SDK 初始化后 {@link RongIM#init(Context)}，调用此方法注册自定义 {@link IExtensionModule}
     * 2. 一定要在进入会话界面之前调此方法
     * </pre>
     *
     * @param extensionModule 自定义模块。
     * @throws IllegalArgumentException IExtensionModule 参数非法时，抛出异常
     */
    public void registerExtensionModule(IExtensionModule extensionModule) {
        if (mExtModules == null) {
            RLog.e(TAG, "Not init in the main process.");
            return;
        }
        if (extensionModule == null || mExtModules.contains(extensionModule)) {
            RLog.e(TAG, "Illegal extensionModule.");
            return;
        }
        RLog.i(TAG, "registerExtensionModule " + extensionModule.getClass().getSimpleName());
        //当集成了红包，表情美美或融云表情的时候，需要把EMOJI置于list的最前面；
        if (mExtModules.size() > 0 && (mExtModules.get(0).getClass().getCanonicalName().equals(DEFAULT_REDPACKET)
                || mExtModules.get(0).getClass().getCanonicalName().equals(DEFAULT_BQMM)
                || mExtModules.get(0).getClass().getCanonicalName().equals(DEFAULT_RC_STICKER))) {
            mExtModules.add(0, extensionModule);
        } else {
            mExtModules.add(extensionModule);
        }
        extensionModule.onInit(mAppKey);
    }

    public void registerExtensionModule(int index, IExtensionModule extensionModule) {
        if (mExtModules == null) {
            RLog.e(TAG, "Not init in the main process.");
            return;
        }
        if (extensionModule == null || mExtModules.contains(extensionModule)) {
            RLog.e(TAG, "Illegal extensionModule.");
            return;
        }
        RLog.i(TAG, "registerExtensionModule " + extensionModule.getClass().getSimpleName());
        mExtModules.add(index, extensionModule);
        extensionModule.onInit(mAppKey);
    }

    /**
     * 添加自定义的 {@link IExtensionModule},添加后，可以通过 {@link #getExtensionModules()} 获取已注册的 module
     * <pre>
     * 注意：
     * 1. 此方法只是把自定义IExtensionModule加入到IExtensionModule列表,不会调用{@link IExtensionModule#onInit(String)}
     * 2. 注册请使用{@link #registerExtensionModule(IExtensionModule)}
     * 3. 此方法适用于IExtensionModule的排序
     * </pre>
     *
     * @param extensionModule 自定义模块。
     * @throws IllegalArgumentException IExtensionModule 参数非法时，抛出异常
     */
    public void addExtensionModule(IExtensionModule extensionModule) {
        if (mExtModules == null) {
            RLog.e(TAG, "Not init in the main process.");
            return;
        }
        if (extensionModule == null || mExtModules.contains(extensionModule)) {
            RLog.e(TAG, "Illegal extensionModule.");
            return;
        }
        RLog.i(TAG, "addExtensionModule " + extensionModule.getClass().getSimpleName());
        mExtModules.add(extensionModule);
    }

    /**
     * 注销 {@link IExtensionModule} 模块
     * <pre>
     * 注意：
     * 1. 请在 SDK 初始化后 {@link RongIM#init(Context)}，调用此方法反注册注册 {@link IExtensionModule}
     * 2. 一定要在进入会话界面之前调次方法
     * </pre>
     *
     * @param extensionModule 已注册的 IExtensionModule 模块
     * @throws IllegalArgumentException IExtensionModule 参数非法时，抛出异常
     */
    public void unregisterExtensionModule(IExtensionModule extensionModule) {
        if (mExtModules == null) {
            RLog.e(TAG, "Not init in the main process.");
            return;
        }
        if (extensionModule == null || !mExtModules.contains(extensionModule)) {
            RLog.e(TAG, "Illegal extensionModule.");
            return;
        }
        RLog.i(TAG, "unregisterExtensionModule " + extensionModule.getClass().getSimpleName());
        Iterator<IExtensionModule> iterator = mExtModules.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(extensionModule)) {
                iterator.remove();
            }
        }
    }

    /**
     * 获取已注册的模块。
     *
     * @return 已注册的模块列表
     */
    public List<IExtensionModule> getExtensionModules() {
        return mExtModules;
    }


    /**
     * SDK 连接时，已调用此方法，用户不需要再次调用。
     *
     * @param token 用户身份 id.
     */
    void connect(String token) {
        if (mExtModules == null) {
            return;
        }
        for (IExtensionModule extensionModule : mExtModules) {
            extensionModule.onConnect(token);
        }
    }

    /**
     * SDK 断开连接时，已调用此方法，用户不需要再次调用。
     */
    void disconnect() {
        if (mExtModules == null) {
            return;
        }
        for (IExtensionModule extensionModule : mExtModules) {
            extensionModule.onDisconnect();
        }
    }

    /**
     * SDK 接收到消息时，已调用此方法，用户不需要再次调用。
     * RongExtModuleManage 会将消息路由到各个 {@link IExtensionModule} 模块。
     *
     * @param message 接收到的消息实体。
     */
    void onReceivedMessage(Message message) {
        for (IExtensionModule extensionModule : mExtModules) {
            extensionModule.onReceivedMessage(message);
        }
    }

    /**
     * 检查融云表情是否存在
     */
    private void checkRCBQ() {
        try {
            Class<?> cls = Class.forName(DEFAULT_RC_STICKER);
            Constructor<?> constructor = cls.getConstructor();
            IExtensionModule rcbq = (IExtensionModule) constructor.newInstance();
            RLog.i(TAG, "add module " + rcbq.getClass().getSimpleName());
            mExtModules.add(rcbq);
            rcbq.onInit(mAppKey);
        } catch (Exception e) {
            RLog.i(TAG, "Can't find " + DEFAULT_RC_STICKER);
        }
    }
}
