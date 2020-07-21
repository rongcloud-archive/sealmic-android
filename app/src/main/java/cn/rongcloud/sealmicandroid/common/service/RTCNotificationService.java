package cn.rongcloud.sealmicandroid.common.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import cn.rongcloud.sealmicandroid.MainActivity;
import cn.rongcloud.sealmicandroid.R;

/**
 * 以解决通话过程中切入后台麦克风不工作
 */
public class RTCNotificationService extends Service {

    private NotificationManager mNotificationManager;
    private static final String CHANNEL_ID = "RTCNotificationService";
    private int notifyId = 20200202;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Notification.Builder builder =
                    new Notification.Builder(this)
                            .setSmallIcon(R.mipmap.ic_seal_mic_logo)
                            .setTicker(getString(R.string.app_name))
                            .setContentTitle(getString(R.string.TapToContiueAsVideoCallIsOn))
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setCategory(Notification.CATEGORY_CALL);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel notificationChannel =
                        new NotificationChannel(CHANNEL_ID, "test", importance);
                notificationChannel.enableLights(false);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(false);
                notificationChannel.setSound(null, null);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            mNotificationManager.notify(notifyId, builder.build());
            startForeground(notifyId, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNotificationManager.cancel(notifyId);
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
