package com.example.cwacollections;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class AboutUs extends AppCompatActivity implements View.OnClickListener{
    ImageButton btnBack, btnAppInfo;
    TextView lblAppVersion;

    String versionName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        btnBack = findViewById(R.id.btnBack);
        btnAppInfo = findViewById(R.id.btnAppInfo);
        btnBack.setOnClickListener(this);
        btnAppInfo.setOnClickListener(this);

        versionName = "Version " + BuildConfig.VERSION_NAME;

        lblAppVersion = findViewById(R.id.lblAppVersion);
        lblAppVersion.setText(versionName);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btnBack:
                finish();
                break;

            case R.id.btnAppInfo:
                try{
                    intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                    startActivity(intent);
                }
                catch (ActivityNotFoundException e){
                    intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    startActivity(intent);
                }
                break;
        }
    }
}