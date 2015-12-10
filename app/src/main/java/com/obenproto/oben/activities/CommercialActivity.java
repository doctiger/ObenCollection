package com.obenproto.oben.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.obenproto.oben.adapters.CommercialListViewAdapter;
import com.obenproto.oben.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.obenproto.oben.adapters.Constants.DESCRIPTION_COLUMN;

public class CommercialActivity extends Activity {
    public static Context context;
    public static ArrayList<HashMap<String, String>> list;
    public static CommercialListViewAdapter adapter;
    public static ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.commercial_activity);

        context = this.getBaseContext();

        final TextView headerDesTxt = (TextView)findViewById(R.id.descriptionTxt);
        headerDesTxt.setVisibility(View.GONE);

        listView = (ListView)findViewById(R.id.listView);
        list = new ArrayList<>();
        populateList(0);

        TextView cancelTxt = (TextView)findViewById(R.id.cancelBtn);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(CommercialActivity.this);
        builder.setTitle("All phrases will be deleted unless you record at least 3 phrases and save");
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
        Display display = getWindowManager().getDefaultDisplay();
        int width = (int) (display.getWidth() * 0.5);
        int height = (int) (display.getHeight() * 0.5);
        alertDialog.getWindow().setLayout(width, height);
        Log.d(String.valueOf(width), String.valueOf(height));
        alertDialog.show();
    }

    public static void populateList(int index) {
        HashMap<String, String> temp = new HashMap<>();
        temp.put(DESCRIPTION_COLUMN, "You should fetch azure, Mike."+String.valueOf(index));
        list.add(temp);

        adapter = new CommercialListViewAdapter(context, list);
        listView.setAdapter(adapter);
    }
}