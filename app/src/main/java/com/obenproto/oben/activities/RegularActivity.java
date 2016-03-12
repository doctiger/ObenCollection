package com.obenproto.oben.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.obenproto.oben.R;
import com.obenproto.oben.activities.base.BaseActivity;
import com.obenproto.oben.api.APIClient;
import com.obenproto.oben.api.domain.ObenPhrase;
import com.obenproto.oben.api.domain.ObenUser;
import com.obenproto.oben.api.response.GetAvatarResponse;
import com.obenproto.oben.api.response.GetPhrasesResponse;

import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class RegularActivity extends BaseActivity implements View.OnClickListener {

    private static final int LIMIT_COUNT = 36;
    private static final int REGULAR_MODE = 1;

    RelativeLayout progressView;
    ListView listView;
    LayoutInflater inflater;

    Integer avatarID = null;
    GetPhrasesResponse phrasesData;
    GetAvatarResponse avatarData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.regular_activity);

        // Map view elements to class members.
        progressView = (RelativeLayout) findViewById(R.id.layout_progress_view);
        listView = (ListView) findViewById(R.id.listView);
        inflater = LayoutInflater.from(this);

        // Map event handlers.
        findViewById(R.id.cancelBtn).setOnClickListener(this);

        // Recall get all phrases endpoint to fetch all phrases for regular mode.
        getAllPhrases();
    }

    @Override
    protected void showProgress() {
        progressView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void dismissProgress() {
        progressView.setVisibility(View.GONE);
    }

    private void getAllPhrases() {
        showProgress();
        Call<GetPhrasesResponse> call = APIClient.getAPIService().getPhrases(REGULAR_MODE);
        call.enqueue(new Callback<GetPhrasesResponse>() {
            @Override
            public void onResponse(Response<GetPhrasesResponse> response, Retrofit retrofit) {
                dismissProgress();
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    phrasesData = response.body();
                    getAvatar();
                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    helperUtils.showMessage(R.string.unauthorized_toast);
                    requestLogout();
                } else {
                    helperUtils.showMessage("Network error");
                    finish();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dismissProgress();
                helperUtils.showMessage(t.getLocalizedMessage());
                finish();
            }
        });
    }

    private void getAvatar() {
        if (helperUtils.avatarLoaded) {
            if (helperUtils.regular != null) {
                avatarID = helperUtils.regular.Avatar.avatarId;
                getRecordedSentences();
            } else {
                populateListView();
            }
        } else {
            finish();
        }
    }

    private void getRecordedSentences() {
        showProgress();
        Call<GetAvatarResponse> call = APIClient.getAPIService().getAvatar(avatarID);
        call.enqueue(new Callback<GetAvatarResponse>() {
            @Override
            public void onResponse(Response<GetAvatarResponse> response, Retrofit retrofit) {
                dismissProgress();
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    avatarData = response.body();
                    populateListView();
                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    helperUtils.showMessage(R.string.unauthorized_toast);
                    requestLogout();
                } else {
                    helperUtils.showMessage("Network error");
                    finish();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dismissProgress();
                helperUtils.showMessage(t.getLocalizedMessage());
                finish();
            }
        });
    }

    private void requestLogout() {
        ObenUser.removeSavedUser();
        showLoginPage();
    }

    private void showLoginPage() {
        Intent intent = new Intent(RegularActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void populateListView() {
        listView.setAdapter(new RegularAdapter());
    }

    @Override
    public void onBackPressed() {
        showAlert();
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegularActivity.this);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancelBtn) {
            showAlert();
        }
    }

    private class RegularAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            int recordCount = avatarData.getRecordCount();
            return recordCount < LIMIT_COUNT ? recordCount + 1 : LIMIT_COUNT;
        }

        @Override
        public Integer getItem(int position) {
            return getCount() - position; // Return record ID.
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.record_item, parent, false);
            }

            TextView tvSentence = (TextView) convertView.findViewById(R.id.descriptionTxt);
            Button btnHearSample = (Button) convertView.findViewById(R.id.hearSampleBtn);
            Button btnListen = (Button) convertView.findViewById(R.id.listenBtn);
            Button btnRec = (Button) convertView.findViewById(R.id.recBtn);

            final Integer recordId = getItem(position);
            final ObenPhrase.PhraseObj phrase = phrasesData.getPhraseByRecordID(recordId);
            tvSentence.setText(phrase.sentence);

            // Setup function for Hear Sample button.
            btnHearSample.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenFrom(phrase.example);
                }
            });

            // Setup function for LISTEN button.
            if (avatarData.getSentence(recordId) == null) {
                btnListen.setAlpha(0.1f);
                btnListen.setEnabled(false);
            } else {
                btnListen.setAlpha(1.0f);
                btnListen.setEnabled(true);
            }
            btnListen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenFrom(avatarData.getSentence(recordId));
                }
            });

            // Setup function for REC button.
            btnRec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopPlaying();
                }
            });

            return convertView;
        }

        private void listenFrom(String sentence) {
            new PlayTask().execute(sentence);
        }
    }

    private MediaPlayer mediaPlayer;

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private class PlayTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
            stopPlaying();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setVolume(1.0f, 1.0f);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    dismissProgress();
                }
            });
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                mediaPlayer.setDataSource(params[0]);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}