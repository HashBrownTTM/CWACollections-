package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class collectionEditor extends AppCompatActivity implements View.OnClickListener{
    EditText txtCollectionName, numGoal;
    Button btnEditCollection;
    ImageButton btnBack;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    //collection id from intent
    public String collectionId;

    String goalStr = "", collectionName = "";
    int goal;

    private static final String TAG = "COLLECTION_EDIT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_editor);

        txtCollectionName = findViewById(R.id.txtCollectionName);
        numGoal = findViewById(R.id.numGoal);

        btnEditCollection = findViewById(R.id.btnEditCollection);
        btnEditCollection.setOnClickListener(this);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        collectionId = getIntent().getStringExtra("collectionId");

        loadCollectionInfo();
    }

    private void loadCollectionInfo() {
        Log.d(TAG, "loadCollectionInfo: Loading collection info");

        DatabaseReference refCollections = FirebaseDatabase.getInstance().getReference("Collections");
        refCollections.child(collectionId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get collection info
                        String collectionName = ""+snapshot.child("collection").getValue();
                        String goalStr = ""+snapshot.child("Goal").getValue();

                        //set to views
                        txtCollectionName.setText(collectionName);
                        numGoal.setText(goalStr);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void validateData() {
        goal = Integer.parseInt(goalStr);
        //checks if the goal amount is less than or equal to zero
        if(goal <= 0) {
            Toast.makeText(collectionEditor.this, "Goal amount cannot be smaller or equal to zero (0).", Toast.LENGTH_SHORT).show();
        }
        else{
            //show progress bar
            progressDialog.setMessage("Editing collection...");
            progressDialog.show();

            //setup data to update to db
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("collection", collectionName);
            hashMap.put("Goal", goal);

            //start updating
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Collections");
            ref.child(collectionId)
                    .updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "onSuccess: Collection updating...");
                            progressDialog.dismiss();
                            Toast.makeText(collectionEditor.this, "Collection info updated", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: failed to update due to "+e.getMessage());
                            progressDialog.dismiss();
                            Toast.makeText(collectionEditor.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //onClick methods
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnEditCollection:
                goalStr = numGoal.getText().toString();
                collectionName = txtCollectionName.getText().toString();

                //checking if none of the data is empty
                if(!collectionName.isEmpty() && !goalStr.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(collectionEditor.this);
                    builder.setTitle("Edit collection")
                            .setMessage("Edit this collection's data?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    validateData();
                                    Intent intent = new Intent(collectionEditor.this, home.class);
                                    startActivity(intent);
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
                    //message for alerting the user if something is missing
                    Toast.makeText(collectionEditor.this, "Please complete all the required fields", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnBack:
                //goes to the previous activity
                finish();
                break;
        }
    }
}