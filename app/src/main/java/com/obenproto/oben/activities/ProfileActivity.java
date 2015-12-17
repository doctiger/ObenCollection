package com.obenproto.oben.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

public class ProfileActivity extends Activity {

    TextView userIDTxt, avatarIDTxt, userEmailTxt;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.profile_activity);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        userIDTxt = (TextView)findViewById(R.id.userIDLbl);
        avatarIDTxt = (TextView)findViewById(R.id.avatarIDLbl);
        userEmailTxt = (TextView)findViewById(R.id.userEmailLbl);

        // User login
        onUserLogin(pref.getString("userEmail", ""), "ObenSesame");
    }

    // Recall of user avatar.
    public void onGetUserAvatar(int userId) {
        // Email login.
        ObenAPIService client = ObenAPIClient.newInstance(ObenAPIService.class);
        Call<ObenApiResponse> call = client.getUserAvatar(userId);

        call.enqueue(new Callback<ObenApiResponse>() {
            @Override
            public void onResponse(Response<ObenApiResponse> response, Retrofit retrofit) {
                if (response.code() == HttpURLConnection.HTTP_OK) { // success
                    ObenApiResponse response_result = response.body();
                    int avatarId = response_result.UserAvatar.getAvatarId();
                    Log.d("avatar ID ", String.valueOf(avatarId));

                    // Save the avatar ID to shared preference.
                    editor.putInt("avatarID", avatarId);
                    editor.commit();

                    ///////////////////////////////////////////////////////////
                    progressBar.setVisibility(View.GONE);

                    userIDTxt.setText(String.valueOf(pref.getInt("userID", 0)));
                    avatarIDTxt.setText(String.valueOf(avatarId));
                    userEmailTxt.setText(pref.getString("userEmail", ""));

                    TextView setupAvatar = (TextView)findViewById(R.id.setUpAvatarLbl);
                    setupAvatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, OptionActivity.class);
                            startActivity(intent);
                        }
                    });

                    TextView logoutTxt = (TextView)findViewById(R.id.logoutLbl);
                    logoutTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onUserLogout();
                        }
                    });


                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Log.d("Status", "Authorization Error");
                    Toast.makeText(getApplicationContext(), "Http Unauthorized", Toast.LENGTH_LONG).show();

                } else {
                    Log.d("Status", "failure");
                    Toast.makeText(getApplicationContext(), "Server Connection Failure", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    // Recall of user logout
    public void onUserLogout() {
        // Email login.
        ObenAPIService client = ObenAPIClient.newInstance(ObenAPIService.class);
        Call<ObenApiResponse> call = client.userLogout();

        call.enqueue(new Callback<ObenApiResponse>() {
            @Override
            public void onResponse(Response<ObenApiResponse> response, Retrofit retrofit) {
                if (response.code() == HttpURLConnection.HTTP_OK) { // success
                    ObenApiResponse response_result = response.body();
                    String message = response_result.User.getMessage();
                    Log.d("Logout Sucess:", message);

                    // Save the avatar ID to shared preference.
                    editor.putString("userEmail", "");
                    editor.putInt("userID", 0);
                    editor.putInt("avatarID", 0);
                    editor.commit();

                    // Go to the Login page.
                    startActivity(new Intent(ProfileActivity.this, ObenUserLogin.class));
                    finish();

                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Log.d("Status", "Authorization Error");
                    Toast.makeText(getApplicationContext(), "Http Unauthorized", Toast.LENGTH_LONG).show();

                } else {
                    Log.d("Status", "failure");
                    Toast.makeText(getApplicationContext(), "Server Connection Failure", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void onUserLogin(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);

        ObenAPIService client = ObenAPIClient.newInstance(ObenAPIService.class);
        Call<ObenApiResponse> call = client.userLogin(email, password);

        call.enqueue(new Callback<ObenApiResponse>() {
            @Override
            public void onResponse(Response<ObenApiResponse> response, Retrofit retrofit) {
                if (response.code() == HttpURLConnection.HTTP_OK) { // success
                    ObenApiResponse response_result = response.body();

                    // Get the user avatar ID.
                    onGetUserAvatar(response_result.User.getUserId());

                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Log.d("User login Status", "Authorization Error");
                    Toast.makeText(getApplicationContext(), "Http Unauthorized", Toast.LENGTH_LONG).show();

                } else {
                    Log.d("User login Status", "failure");
                    Toast.makeText(getApplicationContext(), "Server Connection Failure", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}