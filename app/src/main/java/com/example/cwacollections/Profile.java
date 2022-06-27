package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cwacollections.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity implements View.OnClickListener{
    EditText txtPName, txtPEmail;
    TextView lblChangePassword, lblDeleteAccount;
    Button btnSaveChanges, btnCancel;
    ImageButton btnBack;
    LinearLayout llButtons;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;

    public String uid, fullName = "", email = "", password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtPName = findViewById(R.id.txtPName);
        txtPEmail = findViewById(R.id.txtPEmail);
        llButtons = findViewById(R.id.llButtons);
        lblChangePassword = findViewById(R.id.lblChangePassword);
        lblDeleteAccount = findViewById(R.id.lblDeleteAccount);
        lblChangePassword.setOnClickListener(this);
        lblDeleteAccount.setOnClickListener(this);

        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);
        btnSaveChanges.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        //setup for progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        //checks who the current user is, and fetches their data
        CheckUser();

        //Checks if the EditText boxes are being edited
        CheckTextChange();
    }

    private void CheckUser() {
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){//user logged in
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users users = snapshot.getValue(Users.class);

                            assert users != null;
                            fullName = ""+users.getFullName();
                            email = ""+users.getEmail();

                            txtPName.setText(fullName);
                            txtPEmail.setText(email);
                            uid = users.getUid();

                            llButtons.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void CheckTextChange() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                llButtons.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        //adds a TextWatcher to the EditText, which checks if the EditText's data was changed
        txtPName.addTextChangedListener(textWatcher);
    }

    private void reauthenticateUser() {
        Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_CONTEXT_MENU);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_reauthenticate);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText txtAuthPassword = dialog.findViewById(R.id.txtAuthPassword);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        ImageButton btnShowPassword = dialog.findViewById(R.id.btnShowPassword);
        TextView lblCancel = dialog.findViewById(R.id.lblCancel);

        lblCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable visibility;

                if(txtAuthPassword.getTransformationMethod().getClass().getSimpleName().equals("PasswordTransformationMethod")){
                    txtAuthPassword.setTransformationMethod(new SingleLineTransformationMethod());
                    visibility = ContextCompat.getDrawable(Profile.this, R.drawable.ic_baseline_visibility_off_24);
                }
                else {
                    txtAuthPassword.setTransformationMethod(new PasswordTransformationMethod());
                    visibility = ContextCompat.getDrawable(Profile.this, R.drawable.ic_baseline_remove_red_eye_24);
                }

                btnShowPassword.setImageDrawable(visibility);
                txtAuthPassword.setSelection(txtAuthPassword.getText().length());
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = txtAuthPassword.getText().toString();
                if(password.isEmpty()){
                    txtAuthPassword.setError("Password cannot be empty");
                    txtAuthPassword.requestFocus();
                }
                else{
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                deleteAccount();
                                dialog.dismiss();
                            }
                            else{
                                Toast.makeText(Profile.this, "Incorrect password entered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        dialog.show();
    }

    private void deleteAccount() {
        firebaseUser.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Profile.this, "User account deleted successfully", Toast.LENGTH_SHORT).show();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.child(uid)
                                    .removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(Profile.this, LoginOrRegister.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Profile.this, "Failed to delete user account from db due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });

                        }
                        else {
                            Toast.makeText(Profile.this, "Failed to delete user account", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btnBack:
                finish();
                break;

            case R.id.btnCancel:
                CheckUser();

                llButtons.setVisibility(View.INVISIBLE);

                txtPName.setFocusable(false);
                txtPName.setFocusableInTouchMode(true);
                break;

            case R.id.lblChangePassword:
                intent = new Intent(Profile.this, ResetPassword.class);
                startActivity(intent);
                break;

            case R.id.lblDeleteAccount:
                reauthenticateUser();
                break;

        }
    }
}