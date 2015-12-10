package com.obenproto.oben.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.obenproto.oben.R;

public class OptionActivity extends Activity {

    TextView regularLbl, commercialLbl, freestyleLbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.option_activity);

        regularLbl = (TextView)findViewById(R.id.regularLbl);
        regularLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionActivity.this, RegularActivity.class));
            }
        });

        commercialLbl = (TextView)findViewById(R.id.commercialLbl);
        commercialLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionActivity.this, CommercialActivity.class));
            }
        });

        freestyleLbl = (TextView)findViewById(R.id.freestyleLbl);
        freestyleLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionActivity.this, FreestyleActivity.class));
            }
        });
    }
}