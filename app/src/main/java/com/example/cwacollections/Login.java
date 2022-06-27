package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cwacollections.models.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnShowPassword;
    EditText txtPassword, txtUsername;
    TextView lblSignUp, lblForgotPassword;
    Button btnLogin;

    FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    public String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);

        btnShowPassword = findViewById(R.id.btnShowPassword);
        lblSignUp = findViewById(R.id.lblSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        lblForgotPassword = findViewById(R.id.lblForgotPassword);

        btnShowPassword.setOnClickListener(this);
        lblSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        lblForgotPassword.setOnClickListener(this);

        //fetching instance of firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();

        //progressDialog function
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait a moment...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void UserLogin() {
        String user = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

        if(user.isEmpty()){
            txtUsername.setError("Email cannot be empty");
            txtUsername.requestFocus();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()){
            txtUsername.setError("Invalid email address");
            txtUsername.requestFocus();
        }
        else if (password.isEmpty()){
            txtPassword.setError("Password cannot be empty");
            txtPassword.requestFocus();
        }
        else if(user.isEmpty() && password.isEmpty()){
            Toast.makeText(Login.this, "Please enter the requested information", Toast.LENGTH_LONG).show();
        }
        else if(!(user.isEmpty() && password.isEmpty())){
            progressDialog.setMessage("Checking for user...");
            progressDialog.show();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean userFound = false;

                    for(DataSnapshot ds: snapshot.getChildren()){
                        Users users = ds.getValue(Users.class);

                        assert users != null;
                        if(users.getEmail().equals(user)){
                            userFound = true;
                            break;
                        }
                        else{
                            userFound = false;
                        }
                    }

                    if(userFound){
                        progressDialog.setMessage("Logging in...");
                        firebaseAuth.signInWithEmailAndPassword(user,password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        startActivity(new Intent(Login.this, home.class));
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //dialog for a failure
                                        progressDialog.dismiss();
                                        Toast.makeText(Login.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "User not found: \nPossible incorrect or non-existent user details entered...", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btnLogin:
                UserLogin();
                break;

            case R.id.btnShowPassword:
                Drawable visibility;

                if(txtPassword.getTransformationMethod().getClass().getSimpleName().equals("PasswordTransformationMethod")){
                    txtPassword.setTransformationMethod(new SingleLineTransformationMethod());
                    visibility = ContextCompat.getDrawable(this, R.drawable.ic_baseline_visibility_off_24);
                }
                else {
                    txtPassword.setTransformationMethod(new PasswordTransformationMethod());
                    visibility = ContextCompat.getDrawable(this, R.drawable.ic_baseline_remove_red_eye_24);
                }

                btnShowPassword.setImageDrawable(visibility);

                txtPassword.setSelection(txtPassword.getText().length());
                break;

            case R.id.lblSignUp: //opens Registration screen
                intent = new Intent(Login.this, registration.class);
                startActivity(intent);
                finish();
                break;

            case R.id.lblForgotPassword:
                //implement the forgot password class at a later date
                intent = new Intent(Login.this, ResetPassword.class);
                startActivity(intent);
                break;
        }
    }
}