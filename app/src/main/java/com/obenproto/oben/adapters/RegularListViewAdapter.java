package com.obenproto.oben.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.obenproto.oben.R;
import com.obenproto.oben.activities.RegularActivity;
import com.obenproto.oben.api.ObenAPIClient;
import com.obenproto.oben.api.ObenAPIService;
import com.obenproto.oben.response.ObenApiResponse;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class RegularListViewAdapter extends BaseAdapter {

    public ArrayList<HashMap<String, String>> list;
    public Context cont_;
    public LayoutInflater mInflater;
    boolean isRecording = false;
    boolean isUploading = false;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    MediaRecorder mediaRecorder;
    String filePath, recordedFilePath, sampleFilePath;
    String listenAudioUrl, sampleAdudioUrl;
    int record_index = 0;

    public static final int DIALOG_DOWNLOAD_PROGRESS = 1;
    private ProgressDialog progressDialog;

    public RegularListViewAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        super();
        this.cont_ = context;
        this.list = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        pref = PreferenceManager.getDefaultSharedPreferences(this.cont_);
        editor = pref.edit();

        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ObenRegularRecordVoice.wav";
        recordedFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ObenRegularListenAudio.wav";
        sampleFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ObenRegularSampleAudio.wav";
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.record_item, null);

        TextView descriptionTxt = (TextView) convertView.findViewById(R.id.descriptionTxt);
        final Button hearSampleBtn = (Button) convertView.findViewById(R.id.hearSampleBtn);
        final Button listenBtn = (Button) convertView.findViewById(R.id.listenBtn);
        final Button recBtn = (Button) convertView.findViewById(R.id.recBtn);

        if (position == 0) {
            listenBtn.setEnabled(false);
            listenBtn.setAlpha(0.5f);
        }

        HashMap<String, String> map = list.get(position);
        descriptionTxt.setText(map.get(String.valueOf(position)));
        Log.d("debug description text", map.get(String.valueOf(position)));

        hearSampleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUploading) return;        // set the button enable to false for uploading.
                if (isRecording) return;        // recognize recording button.

                sampleAdudioUrl = String.valueOf(RegularActivity.phraseList.get(position-1).Phrase.getExample());
                Log.d("debug sample record url", sampleAdudioUrl);
                Log.i("Debug Hear Sample", String.valueOf(position));
            }
        });

        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUploading) return;        // set the button enable to false for uploading.
                if (isRecording) return;        // recognize recording button.

                Log.d("debug record url ", String.valueOf(RegularActivity.recordMap.get("record" + position)));
                Log.i("Debug Listen", String.valueOf(position));
            }
        });

        recBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUploading) return;                                    // set the button enable to false for uploading.

                if (isRecording && position != record_index) return;        // recognize recording button.
                Log.d("Debug Recording Flag", String.valueOf(isRecording));
                Log.d("Debug Recording position", String.valueOf(position) + "-" + String.valueOf(record_index));

                recBtn.setText(isRecording ? "REC" : "STOP");
                isRecording = !isRecording;

                if (isRecording) {
                    record_index = position;

                    try {
                        startRecording();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    int btnIndex = position;
                    if (position == 0)
                        btnIndex = RegularActivity.recordcount + 1;

                    stopRecording(btnIndex);
                }

            }
        });

        return convertView;
    }

//    // Download the audio file and save to device.
//    public void downloadAudioFile(String audioUrl, String fileName) throws IOException {
//        URL url = new URL(audioUrl);
//        URLConnection ucon = url.openConnection();
//        ucon.connect();
//
//        int lengthOfFile = ucon.getContentLength();
//        Log.d("Length of file ", String.valueOf(lengthOfFile));
//
//        InputStream is = new BufferedInputStream(url.openStream());
//        OutputStream os = new FileOutputStream(fileName);
//
//        byte data[] = new byte[1024];
//
//        long total = 0, count = 0;
//
//        while ((count = is.read(data)) != -1) {
//            total += count;
//            downloadAudioFile();
//        }
//
//
//    }
//
//    @Override
//    @Deprecated
//    protected Dialog onCreateDialog(int id) {
//        switch (id) {
//            case DIALOG_DOWNLOAD_PROGRESS:
//                progressDialog = new ProgressDialog(RegularActivity.activity);
//                progressDialog.setMessage("Preparing file ...");
//                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                progressDialog.setCancelable(false);
//                progressDialog.show();
//
//                return progressDialog;
//            default:
//                return null;
//        }
//    }

    // Upload the recorded audio file.
    public void onSaveRegularAvatar(int userId, final int recordId, RequestBody audioFile, final int avatarId) {
        ObenAPIService client = ObenAPIClient.newInstance(ObenAPIService.class);

        Call<ObenApiResponse> call;
        if (avatarId == 0) {
            call = client.saveUserAvatar(userId, recordId, audioFile);
        } else {
            call = client.saveRegularAvatar(userId, recordId, audioFile, avatarId);
        }

        call.enqueue(new Callback<ObenApiResponse>() {
            @Override
            public void onResponse(Response<ObenApiResponse> response, Retrofit retrofit) {
                RegularActivity.progressBar.setVisibility(View.GONE);
                isUploading = false;
                if (response.code() == HttpURLConnection.HTTP_OK) { // success
                    Log.v("Upload", "Success");
                    ObenApiResponse response_result = response.body();

                    // save the avatarID to shared preference.
                    if (avatarId == 0) {
                        editor.putInt("RegularAvatarID", response_result.UserAvatar.getAvatarId());
                        editor.commit();
                    }
                    Log.d("debug RegularAvatarID ", String.valueOf(response_result.UserAvatar.getAvatarId()));

                    if (record_index == 0) {
                        // Refresh the listview.
                        RegularActivity.refreshListView();
                        Toast.makeText(cont_, "Upload Success", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(cont_, "Change Success", Toast.LENGTH_LONG).show();
                    }
                    Log.d("debug record ID ", String.valueOf(recordId));


                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Log.d("Status", "Authorization Error");
                    Toast.makeText(cont_, "Http Unauthorized", Toast.LENGTH_LONG).show();

                } else {
                    Log.d("Status", "failure");
                    Toast.makeText(cont_, "Server Connection Failure", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Upload", t.getMessage());
            }
        });
    }

    public void startRecording() throws IOException {
        Log.d("Recorder", "Start recording");

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioChannels(1);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setOutputFile(filePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setAudioSamplingRate(48000);
        mediaRecorder.prepare();
//        mediaRecorder.start();
    }

    public void stopRecording(int btnIndex) {
        RegularActivity.progressBar.setVisibility(View.VISIBLE);
        isUploading = true;
        Log.d("Recorder", "Stop recording");

//        mediaRecorder.stop();
//        mediaRecorder.release();
//        mediaRecorder = null;

        String str = "/storage/emulated/0/iPhoneRecVoice1.wav";
        Log.d("audio file path : ", filePath);
        File audioFileName = new File(str);
        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/wav"), audioFileName);
        onSaveRegularAvatar(pref.getInt("userID", 0),
                btnIndex,
                requestBody,
                pref.getInt("RegularAvatarID", 0));

    }
}
