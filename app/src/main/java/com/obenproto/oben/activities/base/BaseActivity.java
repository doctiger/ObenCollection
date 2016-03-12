package com.obenproto.oben.activities.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.obenproto.oben.R;
import com.obenproto.oben.utils.CommonUtils;
import com.obenproto.oben.utils.LocalStorage;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends Activity {

    private KProgressHUD progressHUD;
    protected LocalStorage localStorage;
    protected CommonUtils helperUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localStorage = LocalStorage.getInstance();
        helperUtils = CommonUtils.getInstance();
    }

    protected void showProgress() {
        progressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.6f)
                .show();
    }

    protected void dismissProgress() {
        if (progressHUD != null) {
            progressHUD.dismiss();
        }
    }

    /**
     * Convenience method to check whether storage permission granted or not.
     *
     * @return true if granted and otherwise false.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected boolean hasGrantedAppPermissions() {
        int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int microphonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        return readStoragePermission == PackageManager.PERMISSION_GRANTED
                && writeStoragePermission == PackageManager.PERMISSION_GRANTED
                && microphonePermission == PackageManager.PERMISSION_GRANTED;
    }

    private static final int MY_PERMISSIONS_REQUEST = 12001;

    /**
     * Convenience method to ask user to grant permissions.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void requestPermissions() {
        boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO);
        if (shouldShowRationale) {
            CommonUtils.getInstance().showMessage(R.string.Grant_necessary_permissions);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            Map<String, Integer> perms = new HashMap<>();
            // Initial
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
            // Fill with results
            for (int i = 0; i < permissions.length; i++) {
                perms.put(permissions[i], grantResults[i]);
            }
            if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                CommonUtils.getInstance().showMessage("All permissions granted.");
                onPermissionsGranted();
            } else {
                CommonUtils.getInstance().showMessage("Some permissions not granted.");
                onPermissionsDenied();
            }
        }
    }

    protected void onPermissionsGranted() {
    }

    protected void onPermissionsDenied() {
    }
}
