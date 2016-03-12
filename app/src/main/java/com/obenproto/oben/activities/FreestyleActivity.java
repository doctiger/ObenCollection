package com.obenproto.oben.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.obenproto.oben.R;
import com.obenproto.oben.activities.base.BaseActivity;
import com.obenproto.oben.api.APIClient;
import com.obenproto.oben.api.domain.ObenUser;
import com.obenproto.oben.api.domain.ObenUserAvatar;
import com.obenproto.oben.api.response.SaveUserAvatarResponse;
import com.obenproto.oben.recorder.ExtAudioRecorder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FreestyleActivity extends BaseActivity implements View.OnClickListener {

    RelativeLayout progressView;
    ImageView btnRecord;
    TextView tvRecordNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_freestyle);

        // Map view elements to class members.
        progressView = (RelativeLayout) findViewById(R.id.layout_progress_view);
        btnRecord = (ImageView) findViewById(R.id.btn_record);
        tvRecordNote = (TextView) findViewById(R.id.tv_record_note);

        // Map event handlers.
        findViewById(R.id.cancelBtn).setOnClickListener(this);
        btnRecord.setOnClickListener(this);
    }

    @Override
    protected void showProgress() {
        progressView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void dismissProgress() {
        progressView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancelBtn) {
            showAlert();
        } else if (v.getId() == btnRecord.getId()) {
            if (isRecording) {
                isRecording = false;
                finishRecording();
            } else {
                isRecording = true;
                startRecording();
            }
        }
    }

    private void showAlert() {
        stopRecording();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save & Exit");
        builder.setMessage(R.string.exit_message_str);
        builder.setCancelable(true);
        builder.setPositiveButton("Save & Exit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton("Keep Recording",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    boolean isRecording = false;
    final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/oben_audio.wav";
    ExtAudioRecorder extAudioRecorder;

    private void startRecording() {
        btnRecord.setImageResource(R.drawable.background_image_mic_red);
        tvRecordNote.setText(R.string.stopRecording);

        // Uncompressed recording (WAV) : IF true - AMR
        extAudioRecorder = ExtAudioRecorder.getInstanse(false);
        extAudioRecorder.setOutputFile(PATH);
        extAudioRecorder.prepare();
        extAudioRecorder.start();
    }

    private void stopRecording() {
        if (extAudioRecorder != null) {
            extAudioRecorder.stop();
            extAudioRecorder.release();
        }
    }

    private void finishRecording() {
        stopRecording();

        btnRecord.setImageResource(R.drawable.background_image_mic_blue);
        tvRecordNote.setText(R.string.startRecording);

        // Upload user recording.
        File audioFileName = new File(PATH);
        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/wav"), audioFileName);
        saveAvatar(requestBody);
    }

    private void saveAvatar(RequestBody requestBody) {
        ObenUser user = ObenUser.getSavedUser();
        if (user != null) {
            showProgress();
            Call<SaveUserAvatarResponse> call = APIClient.getAPIService().saveUserAvatar(
                    FREESTYLE_MODE, user.userId, 1, requestBody, null);
            call.enqueue(new Callback<SaveUserAvatarResponse>() {
                @Override
                public void onResponse(Response<SaveUserAvatarResponse> response, Retrofit retrofit) {
                    dismissProgress();
                    if (response.code() == HttpURLConnection.HTTP_OK) {
                        ObenUserAvatar savedAvatar = response.body().UserAvatar;
                        helperUtils.showMessage(savedAvatar.status);
                    } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        helperUtils.showMessage(R.string.unauthorized_toast);
                        requestLogout();
                    } else {
                        helperUtils.showMessage(R.string.Network_Error);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    dismissProgress();
                    helperUtils.showMessage(t.getLocalizedMessage());
                }
            });
        }
    }

    private void requestLogout() {
        showLoginPage();
    }

    private void showLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}