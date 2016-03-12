package com.obenproto.oben.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.obenproto.oben.R;
import com.obenproto.oben.activities.base.BaseActivity;
import com.obenproto.oben.api.APIClient;
import com.obenproto.oben.api.domain.AvatarInfo;
import com.obenproto.oben.api.domain.ObenUser;
import com.obenproto.oben.api.response.GetAllUserAvatarsResponse;

import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    TextView tvUserID, tvEmail, tvRegular, tvCommercial, tvFreestyle;
    RelativeLayout progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);

        progressView = (RelativeLayout) findViewById(R.id.layout_progress_view);
        tvUserID = (TextView) findViewById(R.id.tv_user_id);
        tvEmail = (TextView) findViewById(R.id.tv_user_email);
        tvRegular = (TextView) findViewById(R.id.tv_regular_avatar);
        tvCommercial = (TextView) findViewById(R.id.tv_commercial_avatar);
        tvFreestyle = (TextView) findViewById(R.id.tv_freestyle_avatar);

        // Map event handlers.
        findViewById(R.id.setUpAvatarLbl).setOnClickListener(this);
        findViewById(R.id.logoutLbl).setOnClickListener(this);

        // Setup user info.
        ObenUser user = ObenUser.getSavedUser();
        if (user != null) {
            tvUserID.setText(String.valueOf(user.userId));
            tvEmail.setText(user.email);
        }

        // Recall getUserAvatar endpoint.
        getAllUserAvatars();
    }

    private void getAllUserAvatars() {
        ObenUser user = ObenUser.getSavedUser();
        if (user != null) {
            helperUtils.avatarLoaded = false;
            progressView.setVisibility(View.VISIBLE);
            Call<GetAllUserAvatarsResponse> call = APIClient.getAPIService().getAllUserAvatars(user.userId);
            call.enqueue(new Callback<GetAllUserAvatarsResponse>() {
                @Override
                public void onResponse(Response<GetAllUserAvatarsResponse> response, Retrofit retrofit) {
                    progressView.setVisibility(View.GONE);
                    if (response.code() == HttpURLConnection.HTTP_OK) {
                        if (response.body() != null) {
                            helperUtils.avatarLoaded = true;
                            showAvatarInfo(response.body());
                        }
                    } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        helperUtils.showMessage(R.string.unauthorized_toast);
                        showLoginPage();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    progressView.setVisibility(View.GONE);
                    helperUtils.showMessage(t.getLocalizedMessage());
                }
            });
        }
    }

    private void showAvatarInfo(GetAllUserAvatarsResponse response) {
        String notExist = "n/a";
        AvatarInfo regular = response.getAvatar(REGULAR_MODE);
        AvatarInfo commercial = response.getAvatar(COMMERCIAL_MODE);
        AvatarInfo freestyle = response.getAvatar(FREESTYLE_MODE);
        if (regular != null) {
            tvRegular.setText(String.valueOf(regular.Avatar.avatarId));
        } else {
            tvRegular.setText(notExist);
        }
        if (commercial != null) {
            tvCommercial.setText(String.valueOf(commercial.Avatar.avatarId));
        } else {
            tvCommercial.setText(notExist);
        }
        if (freestyle != null) {
            tvFreestyle.setText(String.valueOf(freestyle.Avatar.avatarId));
        } else {
            tvFreestyle.setText(notExist);
        }

        // Save loaded avatar info.
        helperUtils.regular = regular;
        helperUtils.commercial = commercial;
        helperUtils.freestyle = freestyle;
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