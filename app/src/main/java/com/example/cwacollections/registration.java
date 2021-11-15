package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class registration extends AppCompatActivity implements View.OnClickListener{
    TextView lblLogin;
    Button btnSignup;
    EditText txtSEmail, txtSPassword, txtConfirm;

    FirebaseAuth mFirebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mFirebaseAuth = FirebaseAuth.getInstance();

        //progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //technical buttons
        lblLogin = findViewById(R.id.lblLogin);
        btnSignup = findViewById(R.id.btnSignup);

        //EditTexts
        txtSEmail = findViewById(R.id.txtSEmail);
        txtSPassword = findViewById(R.id.txtSPassword);
        txtConfirm = findViewById(R.id.txtConfirm);

        //lblLogin goes to MainActivity page
        lblLogin.setOnClickListener(this);
        //btnSignup to register
        btnSignup.setOnClickListener(this);
    }

    private void validateData() {
        String email = txtSEmail.getText().toString(),
                password = txtSPassword.getText().toString(),
                confirmPass = txtConfirm.getText().toString();

        if(email.isEmpty()){
            txtSEmail.setError("Email cannot be empty");
            txtSEmail.requestFocus();
        }
        else if (password.isEmpty()){
            txtSPassword.setError("Password cannot be empty");
            txtSPassword.requestFocus();
        }
        else if(email.isEmpty() && password.isEmpty()){
            Toast.makeText(registration.this, "Please enter the required information", Toast.LENGTH_LONG).show();
        }
        else if(!password.equals(confirmPass)){
            Toast.makeText(registration.this, "Error: Passwords do not match", Toast.LENGTH_LONG).show();
        }
        else if(!(email.isEmpty() && password.isEmpty())){
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();

            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        public void onSuccess(AuthResult authResult) {

                            //getting user uid
                            progressDialog.setMessage("Saving user info");
                            String uid = mFirebaseAuth.getUid();

                            //setup data to add in database
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid", uid);
                            hashMap.put("email", email);

                            //set data to database
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.child(uid)
                                    .setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            //data added in db
                                            progressDialog.dismiss();
                                            Toast.makeText(registration.this, "Account created successfully", Toast.LENGTH_SHORT).show();

                                            //goes to MainActivity
                                            startActivity(new Intent(registration.this, MainActivity.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            //if registration failed
                                            progressDialog.dismiss();
                                            Toast.makeText(registration.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            //if registration failed
                            progressDialog.dismiss();
                            Toast.makeText(registration.this, "" +e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });;
        }
        else{
            progressDialog.dismiss();
            Toast.makeText(registration.this, "Something went wrong, please try again later", Toast.LENGTH_LONG).show();
        }
    }

    //onClick methods
    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.lblLogin:
                intent = new Intent(registration.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btnSignup:
                AlertDialog.Builder builder = new AlertDialog.Builder(registration.this);
                builder.setTitle("Register?")
                        .setMessage("Are you sure you would like to register?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                validateData();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //cancel editing
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
        }
    }
}

/* References
Android Tutorials. 2021. Android Tutorials.
[ONLINE] Available at: https://devofandroid.blogspot.com/. [Accessed 02 June 2021].

Book App Firebase | 02 Login SignUp | Android Studio | Java. 2021. YouTube video, added by Atif Pervaiz
[ONLINE]. Available at: https://youtu.be/SbUtFAu9O7k. [Accessed 02 June 2021].

*/