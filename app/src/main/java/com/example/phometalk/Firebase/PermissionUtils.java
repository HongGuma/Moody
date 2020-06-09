package com.example.phometalk.Firebase;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {
    public static final String TITLE = "Permission";
    public static final String PERMISSION_SD  = Manifest.permission.WRITE_EXTERNAL_STORAGE;



    public interface OnPermissionListener {


        void onPermissionGranted();


        void onPermissionDenied(String... permission);

        void alwaysDenied(String... permission);
    }

    private static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean checkPermission(Context context, String permission) {
        if(isOverMarshmallow()){
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }else {
                return false;
            }
        }
        return  true;
    }




    public static void requestPermission(Context context, String permission, int requestCode) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
    }






    public static boolean canAskAgainPermission(Context context, String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
            return true;
        } else {
            return false;
        }
    }





    public static void checkPermission(Context context, String permission, OnPermissionListener callBack) {
        if (checkPermission(context, permission)) {
            callBack.onPermissionGranted();
        } else {
            if (canAskAgainPermission(context, permission)) {
                callBack.onPermissionDenied(permission);
            } else {
                callBack.alwaysDenied(permission);
            }
        }
    }







    public static boolean isPermissionRequestSuccess(int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    public static void onRequestPermissionResult(Context context, String permission, int[] grantResults, OnPermissionListener callback) {
        if (PermissionUtils.isPermissionRequestSuccess(grantResults)) {
            callback.onPermissionGranted();
        } else {
            if (PermissionUtils.canAskAgainPermission(context, permission)) {
                callback.onPermissionDenied(permission);
            } else {
                callback.alwaysDenied(permission);
            }
        }
    }





    public static void goToAppSetting(final Context context,String name) {

        new AlertDialog.Builder(context)
                .setTitle(PermissionUtils.TITLE)
                .setMessage("tips")
                .setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null).show();

    }
}
