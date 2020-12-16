package cn.rongcloud.sealmicandroid.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheckPermissionUtil {

    public static boolean requestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (permissions.length == 0) {
            return true;
        }
        if (lacksPermissions(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
            return false;
        }
        return true;
    }

    public static boolean allPermissionGranted(int... grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void showPermissionAlert(Context context, String content, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setMessage(content)
                .setPositiveButton("确定", listener)
                .setNegativeButton("取消", listener)
                .setCancelable(false)
                .create()
                .show();
    }

    public static String getNotGrantedPermissionMsg(Context context, List<String> permissions) {
        Set<String> permissionsValue = new HashSet<>();
        String permissionValue;
        for (String permission : permissions) {
            permissionValue = context.getApplicationContext().getString(context.getResources().getIdentifier("rc_" + permission, "string", context.getPackageName()), 0);
            permissionsValue.add(permissionValue);
        }

        String result = "(";
        for (String value : permissionsValue) {
            result += (value + " ");
        }
        result = result.trim() + ")";
        return result;
    }

    private static boolean lacksPermissions(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) {
                return true;
            }
        }
        return false;
    }

}
