package com.obenproto.oben.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.obenproto.oben.R;
import com.obenproto.oben.activities.base.BaseActivity;
import com.obenproto.oben.api.ObenAPIClient;
import com.obenproto.oben.api.domain.ObenUser;
import com.obenproto.oben.api.response.GetUserAvatarResponse;

import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    TextView userIDTxt, userEmailTxt, avatarIDTxt;
    RelativeLayout progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.profile_activity);

        progressView = (RelativeLayout) findViewById(R.id.layout_progress_view);
        userIDTxt = (TextView) findViewById(R.id.userIDLbl);
        userEmailTxt = (TextView) findViewById(R.id.userEmailLbl);
        avatarIDTxt = (TextView) findViewById(R.id.avatarIDLbl);

        // Map event handlers.
        findViewById(R.id.setUpAvatarLbl).setOnClickListener(this);
        findViewById(R.id.logoutLbl).setOnClickListener(this);

        // Setup user info.
        ObenUser user = ObenUser.getSavedUser();
        if (user != null) {
            userIDTxt.setText(String.valueOf(user.userId));
            userEmailTxt.setText(user.email);
        }

        // Recall getUserAvatar endpoint.
        getUserAvatar();
    }

    private void getUserAvatar() {
        ObenUser user = ObenUser.getSavedUser();
        if (user != null) {
            progressView.setVisibility(View.VISIBLE);
            Call<GetUserAvatarResponse> call = ObenAPIClient.getAPIService().getUserAvatar(user.userId);
            call.enqueue(new Callback<GetUserAvatarResponse>() {
                @Override
                public void onResponse(Response<GetUserAvatarResponse> response, Retrofit retrofit) {
                    progressView.setVisibility(View.GONE);
                    if (response.code() == HttpURLConnection.HTTP_OK) {
                        String avatarID = String.valueOf(response.body().UserAvatar.avatarId);
                        avatarIDTxt.setText(avatarID);
                    } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        helperUtils.showMessage(R.string.unauthorized_toast);
                        showLoginPage();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    progressView.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.setUpAvatarLbl) {
            Intent intent = new Intent(ProfileActivity.this, OptionActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.logoutLbl) {
            requestLogout();
        }
    }

    private void requestLogout() {
        ObenUser.removeSavedUser();
        showLoginPage();
    }

    private void showLoginPage() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}