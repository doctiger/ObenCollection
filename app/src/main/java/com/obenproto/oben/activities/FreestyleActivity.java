package com.obenproto.oben.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.obenproto.oben.R;
import com.obenproto.oben.api.ObenAPIClient;
import com.obenproto.oben.api.ObenAPIService;
import com.obenproto.oben.response.ObenApiResponse;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FreestyleActivity extends Activity {

    RelativeLayout start;
    RelativeLayout stop;
    ProgressBar progressBar;
    SharedPreferences pref;
    int userId = 0;
    private static String filePath;
    private static MediaRecorder mediaRecorder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freestyle_activity);

        start = (RelativeLayout) findViewById(R.id.start_recording_layout);
        stop = (RelativeLayout) findViewById(R.id.stop_recording_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        stop.setVisibility(View.GONE);

        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/iPhoneRecVoice.wav";

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        userId = pref.getInt("userID", 0);
        Log.d("userID", String.valueOf(pref.getInt("userID", 0)));

        ImageButton start_rec = (ImageButton) findViewById(R.id.btnStart);
        start_rec.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // pressed
                        try {
                            startRecording(v);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        // released
                        stopRecording(v);
                        return true;
                }
                return false;
            }
        });

        ImageButton stop_rec = (ImageButton) findViewById(R.id.btnStop);
        stop_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording(v);
            }
        });

        TextView cancelTxt = (TextView) findViewById(R.id.cancelBtn);
        cancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });

    }

    @Override
    public void onBackPressed() {
        showAlert();
    }

    public void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FreestyleActivity.this);
        builder.setTitle("Cancel Avatar");
        builder.setMessage("You may return here and complete your avatar at any time.");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes, Cancel",
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

    public void startRecording(View view) throws IOException {
        start.setVisibility(View.GONE);
        stop.setVisibility(View.VISIBLE);
        Log.d("Recorder", "Start recording");

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioChannels(1);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setAudioSamplingRate(48000);
            mediaRecorder.prepare();
            mediaRecorder.setOutputFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    public void stopRecording(View view) {
        start.setVisibility(View.VISIBLE);
        stop.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Log.d("Recorder", "Stop recording");

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        String str = "/storage/emulated/0/iPhoneRecVoice1.wav";
        Log.d("audio file path : ", filePath);
        File audioFileName = new File(filePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/wav"), audioFileName);
        onSaveUserAvatarRequest(userId, 1, requestBody);
    }

    // Recall of save user avatar
    public void onSaveUserAvatarRequest(int userId, int recordId, RequestBody audioFile) {
        // save user avatar
        ObenAPIService client = ObenAPIClient.newInstance(ObenAPIService.class);
        Call<ObenApiResponse> call = client.saveUserAvatar(userId, recordId, audioFile);

        call.enqueue(new Callback<ObenApiResponse>() {
            @Override
            public void onResponse(Response<ObenApiResponse> response, Retrofit retrofit) {
                progressBar.setVisibility(View.GONE);
                if (response.code() == HttpURLConnection.HTTP_OK) { // success
                    Log.v("Upload", "Success");
                    Toast.makeText(getApplicationContext(), "Upload Success", Toast.LENGTH_LONG).show();

                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Log.d("Status", "Authorization Error");
                    Toast.makeText(getApplicationContext(), "Http Unauthorized", Toast.LENGTH_LONG).show();

                } else {
                    Log.d("Status", "failure");
                    Toast.makeText(getApplicationContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("Upload", t.getMessage());
            }
        });
    }
}