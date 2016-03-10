package com.obenproto.oben.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.obenproto.oben.R;
import com.obenproto.oben.activities.base.BaseActivity;
import com.obenproto.oben.api.ObenAPIClient;
import com.obenproto.oben.api.ObenAPIService;
import com.obenproto.oben.api.domain.ObenUser;
import com.obenproto.oben.api.response.LoginResponse;

import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    EditText emailText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);

        findViewById(R.id.btn_login).setOnClickListener(this);

        ObenUser user = ObenUser.getSavedUser();
        if (user != null) {
            String defaultPassword = "ObenSesame";
            emailText.setText(user.email);
            passwordText.setText(defaultPassword);
            requestLogin(user.email, defaultPassword);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            checkAndRequest();
        }
    }

    private void checkAndRequest() {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        // Compare user login info.
        if (TextUtils.isEmpty(email)) {
            emailText.setError("Email cannot be empty");
            emailText.focusSearch(View.FOCUS_DOWN);
        } else if (TextUtils.isEmpty(password)) {
            passwordText.setError("Password can not be empty");
            passwordText.focusSearch(View.FOCUS_DOWN);
        } else if (!helperUtils.validateEmail(email)) {
            emailText.setError("Invalid email");
            emailText.focusSearch(View.FOCUS_DOWN);
        } else {
            requestLogin(email, password);
        }
    }

    private void requestLogin(final String email, String password) {
        showProgress();
        ObenAPIService client = ObenAPIClient.newInstance(ObenAPIService.class);
        Call<LoginResponse> call = client.userLogin(email, password, "Oben User");
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Response<LoginResponse> response, Retrofit retrofit) {
                dismissProgress();
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    ObenUser user = response.body().User;
                    if (user.login.equalsIgnoreCase("SUCCESS")) {
                        user.saveToStorage();
                        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        helperUtils.showMessage(user.message);
                    }
                } else {
                    helperUtils.showMessage("Login failed");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dismissProgress();
            }
        });
    }
}