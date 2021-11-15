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
import android.widget.ImageButton;
import android.widget.NumberPicker;
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

public class collectionAdder extends AppCompatActivity implements View.OnClickListener{
    EditText txtCollectionName, numGoal;
    Button btnCreateCollection;
    ImageButton btnBack;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    String goalStr = "", collectionName = "";
    int goal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_adder);

        //initialising firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        txtCollectionName = findViewById(R.id.txtCollectionName);
        numGoal = findViewById(R.id.numGoal);
        btnCreateCollection = findViewById(R.id.btnCreateCollection);
        btnBack = findViewById(R.id.btnBack);

        btnCreateCollection.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void createNewCollection() {
        goal = Integer.parseInt(goalStr);
        //checks if the goal amount is less than or equal to zero
        if(goal <= 0) {
            Toast.makeText(collectionAdder.this, "Goal amount cannot be smaller or equal to zero (0).", Toast.LENGTH_SHORT).show();
        }
        else{
            //show progress bar
            progressDialog.setMessage("Adding collection...");
            progressDialog.show();

            long timestamp = System.currentTimeMillis();

            //setup info to add in firebase db
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("id", ""+timestamp);
            hashMap.put("collection", collectionName);
            hashMap.put("timestamp", timestamp);
            hashMap.put("Goal", goal);
            hashMap.put("uid", ""+firebaseAuth.getUid());

            //add to firebase database---------Database Root> Collections> collectionId> category info
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Collections");
            ref.child(""+timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //category add successful
                            progressDialog.dismiss();
                            Toast.makeText(collectionAdder.this, "Collection added successfully...", Toast.LENGTH_SHORT).show();

                            txtCollectionName.setText("");
                            numGoal.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            //category add failed
                            progressDialog.dismiss();
                            Toast.makeText(collectionAdder.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    //onClick methods
    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.btnCreateCollection:
                goalStr = numGoal.getText().toString();
                collectionName = txtCollectionName.getText().toString();

                //checking if none of the data is empty
                if(!collectionName.isEmpty() && !goalStr.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(collectionAdder.this);
                    builder.setTitle("Create new collection")
                            .setMessage("Add new collection: " + txtCollectionName.getText() + "?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    createNewCollection();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //cancel creating new collection
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
                else{
                    //message for alerting the user if something is missing
                    Toast.makeText(collectionAdder.this, "Please complete all the required fields", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnBack:
                //goes to the previous activity
                finish();
                break;
        }
    }
}

/* References
TutorialsPoint. 2021. How to Go back to previous activity in android.
[ONLINE] Available at: https://www.tutorialspoint.com/how-to-go-back-to-previous-activity-in-android. [Accessed 02 June 2021].

Android Tutorials. 2021. Android Tutorials.
[ONLINE] Available at: https://devofandroid.blogspot.com/. [Accessed 02 June 2021].

*/