package com.obenproto.oben.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.obenproto.oben.R;
import com.obenproto.oben.activities.base.BaseActivity;
import com.obenproto.oben.api.ObenAPIClient;
import com.obenproto.oben.api.ObenAPIService;
import com.obenproto.oben.response.ObenApiResponse;

import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity extends BaseActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    EditText emailText, passwordText;
    String userLogin;
    int userID;
    String email, email_pattern, password;
    String errorMsg = "ERROR";
    String successMsg = "SUCCESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();

        // Get the shareedpreference.
        if (!pref.getString("userEmail", "").equals("")) {
            Log.d("Already registered", "*****");

            editor.putString("InitialLogin", "no");
            editor.apply();

            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        }

        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        TextView textView = (TextView) findViewById(R.id.loginLbl);

        email_pattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z]+";

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailText.getText().toString().trim();
                password = passwordText.getText().toString().trim();
                userLogin = null;

                // Compare user login info.
                if (TextUtils.isEmpty(email)) {
                    emailText.setError("Email cannot be empty");
                    emailText.focusSearch(View.FOCUS_DOWN);
                } else if (TextUtils.isEmpty(password)) {
                    passwordText.setError("Password must not be empty");
                    passwordText.focusSearch(View.FOCUS_DOWN);
                } else if (email.matches(email_pattern)) {
                    // Send the request with email, password, name.
                    requestLogin(email, password);
                } else {
                    emailText.setError("email must be in format:abc@abc.com");
                    emailText.focusSearch(View.FOCUS_DOWN);
                }
            }
        });
    }

    private void requestLogin(final String email, String password) {
        showProgress();
        ObenAPIService client = ObenAPIClient.newInstance(ObenAPIService.class);
        Call<ObenApiResponse> call = client.userLogin(email, password, "Oben User");
        call.enqueue(new Callback<ObenApiResponse>() {
            @Override
            public void onResponse(Response<ObenApiResponse> response, Retrofit retrofit) {
                dismissProgress();
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    ObenApiResponse response_result = response.body();
                    userLogin = response_result.User.getLogin();
                    userID = response_result.User.getUserId();

                    if (userLogin.equals(successMsg)) {
                        // Save the login info to Local Storage.
                        editor.putString("InitialLogin", "yes");
                        editor.putString("userEmail", email);
                        editor.putInt("userID", userID);
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (userLogin.equals(errorMsg)) {
                        passwordText.setError("no valid password");
                        passwordText.focusSearch(View.FOCUS_DOWN);

                    }
                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Toast.makeText(getApplicationContext(), getString(R.string.unauthorized_toast),
                            Toast.LENGTH_LONG).show();
                } else {
                    userLogin = "Connection Failure";
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dismissProgress();
            }
        });
    }
}