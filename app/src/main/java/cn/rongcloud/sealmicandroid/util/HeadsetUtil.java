package cn.rongcloud.sealmicandroid.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.media.AudioManager;

import static android.content.Context.AUDIO_SERVICE;

/**
 * 耳机相关工具类
 */
public class HeadsetUtil {
    /**
     * 是否连接了音频播放蓝牙耳机
     *
     * @return
     */
    public static boolean hasBluetoothA2dpConnected() {
        boolean bool = false;
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mAdapter.isEnabled()) {
            int a2dp = mAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
            if (a2dp == BluetoothProfile.STATE_CONNECTED) {
                bool = true;
            }
        }
        return bool;
    }

    /**
     * 是否连接了通信蓝牙耳机
     *
     * @return
     */
    public static boolean hasBluetoothHeadSetConnected() {
        boolean bool = false;
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mAdapter.isEnabled()) {
            int a2dp = mAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
            if (a2dp == BluetoothProfile.STATE_CONNECTED) {
                bool = true;
            }
        }
        return bool;
    }

    /**
     * 是否插入了有线耳机
     *
     * @param context
     * @return
     */
    public static boolean isWiredHeadsetOn(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        return audioManager.isWiredHeadsetOn();
    }
}
