package com.practice.venturesitychallenge.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.practice.venturesitychallenge.R;
import com.practice.venturesitychallenge.constants.Constants;

/**
 * Created by akshayaggarwal08 on 8/1/16.
 */
public class SubmitResponseActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_submitresponse);

        TextView tv = (TextView)findViewById(R.id.tvEchoResponse);
        Intent startedActivity = getIntent();
        String ServerResponse = startedActivity.getStringExtra(Constants.ServerResponse);
        tv.setText(ServerResponse);

    }
}
