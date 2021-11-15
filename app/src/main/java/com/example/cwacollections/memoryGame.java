package com.example.cwacollections;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;

public class memoryGame extends AppCompatActivity implements View.OnClickListener{

    TextView lblLevel, lblNumber;
    EditText txtNumber;
    Button btnConfirm, btnPlay;
    ImageButton btnBack;

    Random r;

    int level = 1;
    String generatedNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);

        lblLevel = findViewById(R.id.lblLevel);
        lblNumber = findViewById(R.id.lblNumber);

        txtNumber = findViewById(R.id.txtNumber);

        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(this);
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        r = new Random();

        //hide the input and the button and show the number
        lblLevel.setVisibility(View.GONE);
        lblNumber.setVisibility(View.GONE);
        txtNumber.setVisibility(View.GONE);
        btnConfirm.setVisibility(View.GONE);

    }

    private String generateNumber(int digits){
        String output = "";
        for(int i = 0; i<digits; i++){
            int randomDigit = r.nextInt(10);
            output = output + "" + randomDigit;
        }

        return output;
    }

    //onClick method for this activity
    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.btnConfirm:
                //check if the numbers are the same
                if(generatedNumber.equals(txtNumber.getText().toString())){
                    //hide the input and the button and show the number
                    lblNumber.setVisibility(View.VISIBLE);
                    txtNumber.setVisibility(View.GONE);
                    btnConfirm.setVisibility(View.GONE);

                    //remove text from input
                    txtNumber.setText("");

                    //increase the level
                    level++;

                    //display the current level
                    lblLevel.setText("Level: " + level);

                    //display random number according to the level
                    generatedNumber = generateNumber(level);
                    lblNumber.setText(generatedNumber);

                    //display the elements after two seconds and hide the number
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txtNumber.setVisibility(View.VISIBLE);
                            btnConfirm.setVisibility(View.VISIBLE);
                            lblNumber.setVisibility(View.GONE);

                            txtNumber.requestFocus();
                        }
                    }, 2000);
                }
                else{
                    lblLevel.setText("Game Over! The number was " + generatedNumber);
                    btnConfirm.setVisibility(View.GONE);
                    btnPlay.setVisibility(View.VISIBLE);
                    level = 1;
                }
                break;
            case R.id.btnPlay:
                txtNumber.setText("");

                //hide the input and the button and show the number
                lblLevel.setVisibility(View.VISIBLE);
                lblNumber.setVisibility(View.VISIBLE);
                txtNumber.setVisibility(View.GONE);
                btnConfirm.setVisibility(View.GONE);
                btnPlay.setVisibility(View.GONE);

                //display the current level
                lblLevel.setText("Level: " + level);

                //display random number according to the level
                generatedNumber = generateNumber(level);
                lblNumber.setText(generatedNumber);

                //display the elements after two seconds and hide the number
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txtNumber.setVisibility(View.VISIBLE);
                        btnConfirm.setVisibility(View.VISIBLE);
                        lblNumber.setVisibility(View.GONE);

                        txtNumber.requestFocus();
                    }
                }, 2000);
                break;
            case R.id.btnBack:
                //goes to the previous activity
                finish();
                break;
        }
    }
}

/* References
Develop Numbers Memory Game in Android Studio. 2020. YouTube video, added by Tihomir RAdeff
[ONLINE]. Available at: https://www.youtube.com/watch?v=3yRZIEO_NaY. [Accessed 14 July 2021].

*/