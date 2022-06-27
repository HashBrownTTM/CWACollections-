package com.example.cwacollections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class registration extends AppCompatActivity implements View.OnClickListener{
    TextView lblLogin;
    Button btnSignup;
    ImageButton btnShowPassword1, btnShowPassword2;
    EditText txtName, txtSEmail, txtSPassword, txtConfirm;

    FirebaseAuth mFirebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mFirebaseAuth = FirebaseAuth.getInstance();

        //technical buttons
        lblLogin = findViewById(R.id.lblLogin);
        btnSignup = findViewById(R.id.btnSignup);
        btnShowPassword1 = findViewById(R.id.btnShowPassword1);
        btnShowPassword2 = findViewById(R.id.btnShowPassword2);

        //EditTexts
        txtName = findViewById(R.id.txtName);
        txtSEmail = findViewById(R.id.txtSEmail);
        txtSPassword = findViewById(R.id.txtSPassword);
        txtConfirm = findViewById(R.id.txtConfirm);

        lblLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        btnShowPassword1.setOnClickListener(this);
        btnShowPassword2.setOnClickListener(this);

        //progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void validateData() {
        String name = txtName.getText().toString(),
                email = txtSEmail.getText().toString(),
                password = txtSPassword.getText().toString(),
                confirmPass = txtConfirm.getText().toString();

        if(email.isEmpty()){
            txtSEmail.setError("Email cannot be empty");
            txtSEmail.requestFocus();
        }
        else if(name.isEmpty()){
            txtName.setError("Name cannot be empty");
            txtName.requestFocus();
        }
        else if (password.isEmpty()){
            txtSPassword.setError("Password cannot be empty");
            txtSPassword.requestFocus();
        }
        else if(email.isEmpty() && name.isEmpty() && password.isEmpty() && confirmPass.isEmpty()){
            Toast.makeText(registration.this, "Please enter the required information", Toast.LENGTH_LONG).show();
        }
        else if(!password.equals(confirmPass)){
            Toast.makeText(registration.this, "Error: Passwords do not match", Toast.LENGTH_LONG).show();
        }
        else if(!(email.isEmpty() && name.isEmpty() &&  password.isEmpty() && confirmPass.isEmpty())){
            AlertDialog.Builder builder = new AlertDialog.Builder(registration.this);
            builder.setTitle("Register?")
                    .setMessage("Are you sure you would like to register?")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CreateAccount(name, email, password);
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
        }
        else{
            progressDialog.dismiss();
            Toast.makeText(registration.this, "Something went wrong, please try again later", Toast.LENGTH_LONG).show();
        }
    }

    private void CreateAccount(String name, String email, String password) {
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
                        hashMap.put("fullName", name);
                        hashMap.put("email", email);

                        //set data to database
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        assert uid != null;
                        ref.child(uid)
                                .setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        //data added in db
                                        progressDialog.dismiss();
                                        Toast.makeText(registration.this, "Account created successfully", Toast.LENGTH_SHORT).show();

                                        //goes to MainActivity
                                        startActivity(new Intent(registration.this, home.class));
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
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(registration.this, LoginOrRegister.class));
        finish();
    }

    //onClick methods
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v){
        Drawable visibility;
        Intent intent;

        switch (v.getId()){
            case R.id.lblLogin:
                intent = new Intent(registration.this, Login.class);
                startActivity(intent);
                finish();
                break;

            case R.id.btnSignup:
                validateData();
                break;

            case R.id.btnShowPassword1: // password visibility, Stack Overflow (2021)
                if (txtSPassword.getTransformationMethod().getClass().getSimpleName() .equals("PasswordTransformationMethod")) {
                    txtSPassword.setTransformationMethod(new SingleLineTransformationMethod());
                    visibility = ContextCompat.getDrawable(this, R.drawable.ic_baseline_visibility_off_24);
                }
                else {
                    txtSPassword.setTransformationMethod(new PasswordTransformationMethod());
                    visibility = ContextCompat.getDrawable(this, R.drawable.ic_baseline_remove_red_eye_24);
                }

                btnShowPassword1.setImageDrawable(visibility);

                txtSPassword.setSelection(txtSPassword.getText().length());
                break;

            case R.id.btnShowPassword2: // password visibility, Stack Overflow (2021)
                if (txtConfirm.getTransformationMethod().getClass().getSimpleName() .equals("PasswordTransformationMethod")) {
                    txtConfirm.setTransformationMethod(new SingleLineTransformationMethod());
                    visibility = ContextCompat.getDrawable(this, R.drawable.ic_baseline_visibility_off_24);
                }
                else {
                    txtConfirm.setTransformationMethod(new PasswordTransformationMethod());
                    visibility = ContextCompat.getDrawable(this, R.drawable.ic_baseline_remove_red_eye_24);
                }

                btnShowPassword2.setImageDrawable(visibility);

                txtConfirm.setSelection(txtConfirm.getText().length());
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