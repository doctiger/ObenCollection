package com.obenproto.oben.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.obenproto.oben.R;
import com.obenproto.oben.adapters.RegularListViewAdapter;
import com.obenproto.oben.api.ObenAPIClient;
import com.obenproto.oben.api.ObenAPIService;
import com.obenproto.oben.response.ObenApiResponse;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class RegularActivity extends Activity {
    public static Context context;
    public static ArrayList<HashMap<String, String>> list;
    public static RegularListViewAdapter adapter;
    public static ListView listView;
    public static ProgressBar progressBar;
    public static int recordcount = 0;
    public static List<ObenApiResponse> phraseList;
    public static Activity activity = null;
    public static Map recordMap;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.regular_activity);
        activity = this;

        context = this.getBaseContext();
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        listView = (ListView)findViewById(R.id.listView);
        list = new ArrayList<>();

        // Get the list contents.
        onGetPhrases();

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
        AlertDialog.Builder builder = new AlertDialog.Builder(RegularActivity.this);
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

    public static void populateList(int index) {
        HashMap<String, String> temp = new HashMap<>();
        temp.put(String.valueOf(0), phraseList.get(index).Phrase.getSentence());
        list.add(temp);
        for (int i = 1; i <= index; i++) {
            temp.put(String.valueOf(i), phraseList.get((index-i)%34).Phrase.getSentence());
            list.add(temp);
        }

        adapter = new RegularListViewAdapter(context, list);
        listView.setAdapter(adapter);

    }/**/

    public static void refreshListView() {
        activity.finish();
        activity.startActivity(activity.getIntent());
    }

    //// Get the all avatar data for regular.
    public void onAvatarData() {
        ObenAPIService client = ObenAPIClient.newInstance(ObenAPIService.class);
        Call<ObenApiResponse> call = client.getAvatarData(pref.getInt("RegularAvatarID", 0));

        call.enqueue(new Callback<ObenApiResponse>() {

            @Override
            public void onResponse(Response<ObenApiResponse> response, Retrofit retrofit) {
                if (response.code() == HttpURLConnection.HTTP_OK) { // success
                    ObenApiResponse response_result = response.body();
                    recordMap = (Map) response_result.Avatar;
                    Log.d("debug avatar List", String.valueOf(recordMap.get("record" + 5)));


                    progressBar.setVisibility(View.GONE);
                    if (recordMap.get("status") == null) {
                        String str = recordMap.get("recordCount").toString();
                        recordcount = Float.valueOf(str).intValue();
                        Log.d("debug record count", String.valueOf(recordcount));

                        listView = (ListView) findViewById(R.id.listView);
                        list = new ArrayList<>();
                        populateList(recordcount);

                    } else {

                        populateList(0);
                        Toast.makeText(getApplicationContext(), "Avatar with id 4 not found", Toast.LENGTH_LONG).show();
                    }


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

    public void onGetPhrases() {
        ObenAPIService client = ObenAPIClient.newInstance(ObenAPIService.class);
        Call<List<ObenApiResponse>> call = client.getPhraseData();

        call.enqueue(new Callback<List<ObenApiResponse>>() {
            @Override
            public void onResponse(Response<List<ObenApiResponse>> response, Retrofit retrofit) {
                if (response.code() == HttpURLConnection.HTTP_OK) { // success
                    phraseList = response.body();

                    onAvatarData();

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
                Log.d("Status", "failure");
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
            }
        });
    }
}