package com.obenproto.oben;

import android.app.Application;

import com.obenproto.oben.api.ObenAPIClient;
import com.obenproto.oben.utils.CommonUtils;
import com.obenproto.oben.utils.LocalStorage;

public class ObenApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initSingletons();
    }

    private void initSingletons() {
        ObenAPIClient.init();
        LocalStorage.init(this);
        CommonUtils.init(this);
    }
}
