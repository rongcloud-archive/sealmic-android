package cn.rongcloud.sealmic.utils;

import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.rongcloud.sealmic.utils.log.SLog;

/**
 * 有线耳机和蓝牙耳机插入和拔出广播监听
 */
public class HeadsetPlugReceiver extends BroadcastReceiver {
    private static final String TAG = "HeadsetPlugReceiver";

    public boolean FIRST_HEADSET_PLUG_RECEIVER = false;
    private static OnHeadsetPlugListener headsetPlugListener = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        HeadsetInfo headsetInfo = null;

        if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
            int state = -1;
            if (FIRST_HEADSET_PLUG_RECEIVER) {
                if (intent.hasExtra("state")) {
                    state = intent.getIntExtra("state", -1);
                }
                if (state == 1) {
                    headsetInfo = new HeadsetInfo(true, 1);
                } else if (state == 0) {
                    headsetInfo = new HeadsetInfo(false, 1);
                }
            } else {
                FIRST_HEADSET_PLUG_RECEIVER = true;
            }
        } else if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
            switch (state) {
                case BluetoothProfile.STATE_DISCONNECTED:
                    headsetInfo = new HeadsetInfo(false, 0);
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    headsetInfo = new HeadsetInfo(true, 0);
                    break;
            }
        }

        if (null != headsetInfo && headsetPlugListener != null) {
            SLog.d(TAG, "isConnected:" + headsetInfo.isConnected() + ",type:" + headsetInfo.getType());
            headsetPlugListener.onNotifyHeadsetState(headsetInfo.isConnected(), headsetInfo.getType());
        }
    }

    public static void setOnHeadsetPlugListener(OnHeadsetPlugListener onHeadsetPlugListener) {
        headsetPlugListener = onHeadsetPlugListener;
    }

    public interface OnHeadsetPlugListener {
        /**
         * 耳机连接状态监听
         *
         * @param connected 是否连接
         * @param type      连接类型 ，0：蓝牙耳机；1：有线耳机
         */
        void onNotifyHeadsetState(boolean connected, int type);
    }

    private class HeadsetInfo {
        boolean isConnected;
        int type;

        HeadsetInfo(boolean isInsert, int type) {
            this.isConnected = isInsert;
            this.type = type;
        }

        boolean isConnected() {
            return isConnected;
        }

        int getType() {
            return type;
        }

        void setType(int type) {
            this.type = type;
        }
    }
}
