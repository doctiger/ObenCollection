package com.obenproto.oben.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.obenproto.oben.api.ObenAPIClient;
import com.obenproto.oben.api.ObenAPIService;
import com.obenproto.oben.response.ObenApiResponse;

import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ObenUserLogin extends Activity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public static EditText emailText, passwordText;
    String userLogin, userDisplayName;
    int userID;
    String email, email_pattern, password;
    public static ProgressBar progressBar;
    String errorMsg = "ERROR";
    String successMsg = "SUCCESS";

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();

        // Get the shareedpreference.
        if (!pref.getString("userEmail", "").equals("")) {
            Log.d("Already registered", "*****");
            Intent intent = new Intent(ObenUserLogin.this, ProfileActivity.class);
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
                    // Send the request with email, password, display, name.
                    onUserLogin(email, password, "Petro Rington");

                    progressBar.setVisibility(View.VISIBLE);

                } else {
                    emailText.setError("email must be in format:abc@abc.com");
                    emailText.focusSearch(View.FOCUS_DOWN);

                }
            }
        });
    }

    public void onUserLogin(final String email, String password, String displayName) {
        // Email login.
        ObenAPIService client = ObenAPIClient.newInstance(ObenAPIService.class);
        Call<ObenApiResponse> call = client.userLogin(email, password, displayName);

        call.enqueue(new Callback<ObenApiResponse>() {
            @Override
            public void onResponse(Response<ObenApiResponse> response, Retrofit retrofit) {
                if (response.code() == HttpURLConnection.HTTP_OK) { // success
                    ObenApiResponse response_result = response.body();
                    userLogin = response_result.User.getLogin();
                    userID = response_result.User.getUserId();
                    userDisplayName = response_result.User.getUserDisplayName();

                    Log.d("User Login Status :", String.valueOf(userLogin));

                    if (userLogin.equals(successMsg)) {
                        // Save the login infomation to sharedpreference.
                        editor.putString("userEmail", email);
                        editor.putInt("userID", userID);
                        editor.putString("userDisplayName", userDisplayName);
                        editor.apply();

                        passwordText.setError("Login Success");
                        Intent intent = new Intent(ObenUserLogin.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (userLogin.equals(errorMsg)) {
                        progressBar.setVisibility(View.GONE);
                        passwordText.setError("no valid password");
                        passwordText.focusSearch(View.FOCUS_DOWN);

                    }

                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Log.d("User Login Status :", "Authorization Error");
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Http Unauthorized", Toast.LENGTH_LONG).show();

                } else {
                    Log.d("User Login Status", "failure");
                    userLogin = "Connection Failure";
                    Toast.makeText(getApplicationContext(), "Server Connection Failure", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}