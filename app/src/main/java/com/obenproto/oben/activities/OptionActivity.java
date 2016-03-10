package com.obenproto.oben.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.obenproto.oben.R;
import com.obenproto.oben.activities.base.BaseActivity;
import com.obenproto.oben.api.domain.ObenUser;

public class OptionActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.option_activity);

        // Map event handlers.
        findViewById(R.id.regularLbl).setOnClickListener(this);
        findViewById(R.id.commercialLbl).setOnClickListener(this);
        findViewById(R.id.freestyleLbl).setOnClickListener(this);
        findViewById(R.id.cancelBtn).setOnClickListener(this);
        findViewById(R.id.logoutBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.regularLbl) {
            startActivity(new Intent(OptionActivity.this, RegularActivity.class));
        } else if (v.getId() == R.id.commercialLbl) {
            startActivity(new Intent(OptionActivity.this, CommercialActivity.class));
        } else if (v.getId() == R.id.freestyleLbl) {
            startActivity(new Intent(OptionActivity.this, FreestyleActivity.class));
        } else if (v.getId() == R.id.cancelBtn) {
            finish();
        } else if (v.getId() == R.id.logoutBtn) {
            requestLogout();
        }
    }

    private void requestLogout() {
        ObenUser.removeSavedUser();
        showLoginPage();
    }

    private void showLoginPage() {
        Intent intent = new Intent(OptionActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}