package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cwacollections.models.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity{
    TextView lblCheckUser;
    FirebaseAuth firebaseAuth;

    public String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblCheckUser = findViewById(R.id.lblCheckUser);
        firebaseAuth = FirebaseAuth.getInstance();

        CheckLoggedInUser();

    }

    private void CheckLoggedInUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){ //user Logged in
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users users = snapshot.getValue(Users.class);
                            lblCheckUser.setText(users.getFullName() + "\nis logged in.");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(MainActivity.this, home.class));
                                    finish();
                                }
                            }, 1500);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        else{ //no users logged in
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, LoginOrRegister.class));
                    finish();
                }
            }, 1500);
        }
    }
}

/* References
Android Tutorials. 2021. Android Tutorials.
[ONLINE] Available at: https://devofandroid.blogspot.com/. [Accessed 02 June 2021].

Book App Firebase | 02 Login SignUp | Android Studio | Java. 2021. YouTube video, added by Atif Pervaiz
[ONLINE]. Available at: https://youtu.be/SbUtFAu9O7k. [Accessed 02 June 2021].







    TextView lblSignup, lblForgotPassword;
    Button btnLogin;
    EditText txtlEmail, txtlPassword;

    FirebaseAuth mFirebaseAuth;
    private ProgressDialog progressDialog;

    public String uid;

    //to check if a user is already logged in
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();

        //progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        lblSignup = findViewById(R.id.lblSignup);
        lblForgotPassword = findViewById(R.id.lblForgotPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtlEmail = findViewById(R.id.txtlEmail);
        txtlPassword = findViewById(R.id.txtlPassword);

        //create an object if Firebase StateListener
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if(mFirebaseUser != null){
                    Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, home.class));

                }
            }
        };

        //lblSignup goes to registration page
        lblSignup.setOnClickListener(this);
        lblForgotPassword.setOnClickListener(this);
        //btnLogin goes to home page
        btnLogin.setOnClickListener(this);
    }

    //onClick methods
    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.btnLogin:
                String email = txtlEmail.getText().toString(),
                       password = txtlPassword.getText().toString();

                if(email.isEmpty()){
                    txtlEmail.setError("Email cannot be empty");
                    txtlEmail.requestFocus();
                }
                else if (password.isEmpty()){
                    txtlPassword.setError("Password cannot be empty");
                    txtlPassword.requestFocus();
                }
                else if(email.isEmpty() && password.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter the required information", Toast.LENGTH_LONG).show();
                }
                else if(!(email.isEmpty() && password.isEmpty())){
                    progressDialog.setMessage("Loggin in...");
                    progressDialog.show();

                    //loginuser
                    mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            progressDialog.setMessage("Checking User...");
                            //login success, check in database
                            uid = mFirebaseAuth.getUid();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(MainActivity.this, home.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            //login failed
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else{
                    Toast.makeText(MainActivity.this, "Something went wrong, please try again later", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.lblSignup:
                intent = new Intent(MainActivity.this, registration.class);
                startActivity(intent);
                break;
            case R.id.lblForgotPassword:
                intent = new Intent(MainActivity.this, ResetPassword.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        //new stuff
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            //user logged in
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(mFirebaseAuth.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    startActivity(new Intent(MainActivity.this, home.class));
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }

        //mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
*/