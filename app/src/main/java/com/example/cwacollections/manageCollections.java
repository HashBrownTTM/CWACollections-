package com.example.cwacollections;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class manageCollections extends AppCompatActivity implements View.OnClickListener{
    ImageButton btnBack;
    TextView txtCollection, lblGoalDiff, lblStats;
    ProgressBar pbPieChart;

    private int goalAmount = 0;
    private int itemAmount = 0;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_collections);



        Intent intent = getIntent();
        goalAmount = Integer.parseInt(intent.getStringExtra("goal"));
        itemAmount = Integer.parseInt(intent.getStringExtra("item"));
        title = intent.getStringExtra("title");

        //initialising the form components
        lblGoalDiff = findViewById(R.id.lblGoalDiff);
        lblStats = findViewById(R.id.lblStats);
        pbPieChart = findViewById(R.id.pbPieChart);

        txtCollection = findViewById(R.id.txtCollection);
        txtCollection.setText(title);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        loadChart();
    }

    private void loadChart() {
        double d = (double) itemAmount / (double) goalAmount;
        int progress = (int) (d * 100);
        pbPieChart.setProgress(progress);

        lblGoalDiff.setText("" + progress + "%");

        lblStats.setText("Collection: " + title +
                "\nCollection item goal amount:\n" + goalAmount +
                "\nCurrent number of items in this collection:\n" + itemAmount +
                "\nNumber of items left till completion:\n" + (goalAmount - itemAmount));
    }

    //onClick methods
    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.btnBack:
                //goes to the previous activity
                finish();
                break;
        }
    }
}

/* References
Medium. 2018. Android Tutorial for Beginners: Create a Pie Chart With XML | by Ivanna Kacewica | Medium. [
ONLINE] Available at: https://medium.com/@evanca/android-tutorial-for-beginners-create-a-pie-chart-with-xml-36e67dabe67f.
[Accessed 14 July 2021].

*/