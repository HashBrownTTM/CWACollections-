package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPassword extends AppCompatActivity implements View.OnClickListener{
    ImageButton btnBack;
    EditText txtEmail;
    Button btnResetPassword;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        auth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.txtEmail);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnResetPassword.setOnClickListener(this);
    }

    //onClick method for this activity
    @Override
    public void onClick(View v){
        Intent intent;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (v.getId()){
            case R.id.btnBack:
                //goes to the previous activity
                finish();
                break;
            case R.id.btnResetPassword:
                String email = txtEmail.getText().toString();
                if(email.isEmpty()){
                    txtEmail.setError("Email cannot be empty");
                    txtEmail.requestFocus();
                }
                else{
                    progressDialog.setMessage("Verifying User...");
                    progressDialog.show();

                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(forgotPassword.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(forgotPassword.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(forgotPassword.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                }
                break;
        }
    }
}

/* References
Firebase. 2021. Manage Users in Firebase.
[ONLINE] Available at: https://firebase.google.com/docs/auth/android/manage-users#send_a_password_reset_email. [Accessed 17 July 2021].

*/