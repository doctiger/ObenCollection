package com.obenproto.oben.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.obenproto.oben.activities.CommercialActivity;
import com.obenproto.oben.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.obenproto.oben.adapters.Constants.DESCRIPTION_COLUMN;

/**
 * Created by Petro RingTon on 12/9/2015.
 */
public class CommercialListViewAdapter extends BaseAdapter {

    public ArrayList<HashMap<String, String>> list;
    public Context cont_;
    public LayoutInflater mInflater;

    public CommercialListViewAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        super();
        this.cont_ = context;
        this.list = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        Button hearSampleBtn = (Button) convertView.findViewById(R.id.hearSampleBtn);
        Button listenBtn = (Button) convertView.findViewById(R.id.listenBtn);
        Button recBtn = (Button) convertView.findViewById(R.id.recBtn);

        if (position == 0) {
            listenBtn.setEnabled(false);
            listenBtn.setAlpha(0.5f);
        }

        HashMap<String, String> map = list.get(position);
        descriptionTxt.setText(map.get(DESCRIPTION_COLUMN));

        hearSampleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Hear Sample", String.valueOf(position));
            }
        });

        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Listen", String.valueOf(position));
            }
        });

        recBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0 && list.size() < 8) {
                    CommercialActivity.populateList(list.size());
                }
                Log.d("Record", String.valueOf(position));
            }
        });

        return convertView;
    }
}
