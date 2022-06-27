package com.example.cwacollections;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


public class collectionProgress extends AppCompatActivity implements View.OnClickListener{
    ImageButton btnBack;
    TextView txtCollection, lblGoalDiff, lblStats;
    ProgressBar pbPieChart;

    private int goalAmount = 0;
    private int itemAmount = 0;
    String title, colColour = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_progress);

        Intent intent = getIntent();
        goalAmount = Integer.parseInt(intent.getStringExtra("goal"));
        itemAmount = Integer.parseInt(intent.getStringExtra("itemCount"));
        title = intent.getStringExtra("title");
        colColour = intent.getStringExtra("colColour");

        //initialising the form components
        lblGoalDiff = findViewById(R.id.lblGoalDiff);
        lblStats = findViewById(R.id.lblStats);
        pbPieChart = findViewById(R.id.pbPieChart);

        pbPieChart.setProgressTintList(ColorStateList.valueOf(Color.parseColor(""+colColour)));

        txtCollection = findViewById(R.id.txtCollection);
        txtCollection.setText(title);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        loadChart();
    }

    @SuppressLint("SetTextI18n")
    private void loadChart() {
        double d = (double) itemAmount / (double) goalAmount;
        double progress = (d * 100);
        //pbPieChart.setProgress(progress);

        ObjectAnimator progressbarAnim = ObjectAnimator.ofInt(pbPieChart, "progress", 0, (int) progress);
        progressbarAnim.setDuration(500);
        progressbarAnim.setInterpolator(new AccelerateInterpolator());
        progressbarAnim.start();

        progress = progress * 100;
        progress = Math.round(progress);
        progress = progress / 100;


        lblGoalDiff.setText("" + progress + "%");

        lblStats.setText("Goal:\t" + goalAmount +
                "\nCurrent number of items:\t" + itemAmount +
                "\nNumber of items left:\t" + (goalAmount - itemAmount));
    }

    //onClick methods
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v){
        if (v.getId() == R.id.btnBack) {
            //goes to the previous activity
            finish();
        }
    }
}

/* References
Medium. 2018. Android Tutorial for Beginners: Create a Pie Chart With XML | by Ivanna Kacewica | Medium. [
ONLINE] Available at: https://medium.com/@evanca/android-tutorial-for-beginners-create-a-pie-chart-with-xml-36e67dabe67f.
[Accessed 14 July 2021].

*/