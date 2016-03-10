package com.obenproto.oben.activities.base;

import android.app.Activity;
import android.os.Bundle;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.obenproto.oben.utils.CommonUtils;
import com.obenproto.oben.utils.LocalStorage;

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
}
