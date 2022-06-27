package com.example.cwacollections;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginOrRegister extends AppCompatActivity implements View.OnClickListener {
    Button btnLogin, btnCreateAccount; 
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);
        
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnLogin.setOnClickListener(this);
        btnCreateAccount.setOnClickListener(this);
    }

    @Override
    public void onBackPressed(){
        this.finishAffinity();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btnLogin:
                intent = new Intent(LoginOrRegister.this, Login.class);
                startActivity(intent);
                break;

            case R.id.btnCreateAccount:
                intent = new Intent(LoginOrRegister.this, registration.class);
                startActivity(intent);
                break;
        }
    }
}