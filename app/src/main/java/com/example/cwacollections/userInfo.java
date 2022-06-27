package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class userInfo extends AppCompatActivity implements View.OnClickListener{
    ImageButton btnBack, btnMore;
    private ProgressDialog progressDialog;
    TextView lblEmail;
    FirebaseUser user;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        btnMore = findViewById(R.id.btnMore);
        btnMore.setOnClickListener(this);
        lblEmail = findViewById(R.id.lblEmail);

        user = FirebaseAuth.getInstance().getCurrentUser();

        lblEmail.setText("" + user.getEmail());
        uid = user.getUid();

        //progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    //onClick method for this activity
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btnBack:
                //goes to the previous activity
                finish();
                break;
            case R.id.btnMore:
                //options to show in dialog
                String[] options ={"Change Password", "Delete Account"};

                //alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(userInfo.this);
                builder.setTitle("Choose Option")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    //change password option
                                    Intent intent = new Intent(userInfo.this,  ResetPassword.class);
                                    startActivity(intent);
                                }
                                else if(which == 1){
                                    //delete account option
                                    AlertDialog.Builder builder = new AlertDialog.Builder(userInfo.this);
                                    builder.setTitle("Delete Account")
                                            .setMessage("Are you sure you want to delete your account?")
                                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    progressDialog.setMessage("Verifying User...");
                                                    progressDialog.show();

                                                    deleteUserAccount();
                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //cancel deleting
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();
                                }
                            }
                        })
                        .show();
                break;
        }
    }

    private void deleteUserAccount() {
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull  Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(userInfo.this, "User account deleted successfully", Toast.LENGTH_SHORT).show();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.child(uid)
                                    .removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(userInfo.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(userInfo.this, "Failed to delete user account from db due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });

                        }
                        else {
                            Toast.makeText(userInfo.this, "Failed to delete user account", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}

/* References
Firebase. 2021. Manage Users in Firebase.
[ONLINE] Available at: https://firebase.google.com/docs/auth/android/manage-users#send_a_password_reset_email. [Accessed 17 July 2021].

*/