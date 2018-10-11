package com.medialab.civiclink;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Transportation extends AppCompatActivity {
    RadioGroup car_availability;
    RadioButton yescar, nocar;

    TextView fit, need;
    EditText numfit, numneed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transportation);

        yescar = (RadioButton)findViewById(R.id.yescar);
        nocar = (RadioButton)findViewById(R.id.nocar);

        fit = (TextView)findViewById(R.id.fit);
        numfit = (EditText) findViewById(R.id.numfit);

        need = (TextView)findViewById(R.id.need);
        numneed = (EditText)findViewById(R.id.numneed);
        yescar.setChecked(true);
        fit.setVisibility(View.VISIBLE);
        numfit.setVisibility(View.VISIBLE);
        need.setVisibility(View.INVISIBLE);
        numneed.setVisibility(View.INVISIBLE);
        nocar.setChecked(false);

        yescar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fit.setVisibility(View.VISIBLE);
                numfit.setVisibility(View.VISIBLE);
                need.setVisibility(View.INVISIBLE);
                numneed.setVisibility(View.INVISIBLE);
                nocar.setChecked(false);
            }
        });
        nocar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fit.setVisibility(View.INVISIBLE);
                numfit.setVisibility(View.INVISIBLE);
                need.setVisibility(View.VISIBLE);
                numneed.setVisibility(View.VISIBLE);
                yescar.setChecked(false);
            }
        });
    }
}
