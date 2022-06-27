package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cwacollections.customUI.RelativeRadioGroup;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class collectionEditor extends AppCompatActivity implements View.OnClickListener{
    EditText txtCollectionName, numGoal;
    ImageButton btnBack, btnEditCollection, btnSelectColour;
    RelativeRadioGroup rgColors;
    View selectedColour;

    private ProgressDialog progressDialog;

    //collection id from intent
    public String collectionId;

    String goalStr = "", collectionName = "";
    String selectedColorHex = "";
    int goal, selectedColorId = 0;

    private static final String TAG = "COLLECTION_EDIT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_editor);

        txtCollectionName = findViewById(R.id.txtCollectionName);
        numGoal = findViewById(R.id.numGoal);
        selectedColour = findViewById(R.id.selectedColour);

        btnEditCollection = findViewById(R.id.btnEditCollection);
        btnBack = findViewById(R.id.btnBack);
        btnSelectColour = findViewById(R.id.btnSelectColour);
        btnEditCollection.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnSelectColour.setOnClickListener(this);

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
                        String colColourHex = ""+snapshot.child("colColour").getValue();

                        //set to views
                        txtCollectionName.setText(collectionName);
                        numGoal.setText(goalStr);
                        selectedColorHex = colColourHex;
                        selectedColour.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(selectedColorHex)));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void colourPickerDialog(){
        Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_colour_picker);

        rgColors = dialog.findViewById(R.id.rgColors);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        TextView lblCancel = dialog.findViewById(R.id.lblCancel);

        /*RadioButton rbtn1 = dialog.findViewById(R.id.rbtn1),
                rbtn2 = dialog.findViewById(R.id.rbtn2),
                rbtn3 = dialog.findViewById(R.id.rbtn3),
                rbtn4 = dialog.findViewById(R.id.rbtn4),
                rbtn5 = dialog.findViewById(R.id.rbtn5),
                rbtn6 = dialog.findViewById(R.id.rbtn6),
                rbtn7 = dialog.findViewById(R.id.rbtn7),
                rbtn8 = dialog.findViewById(R.id.rbtn8),
                rbtn9 = dialog.findViewById(R.id.rbtn9),
                rbtn10 = dialog.findViewById(R.id.rbtn10),
                rbtn11 = dialog.findViewById(R.id.rbtn11),
                rbtn12 = dialog.findViewById(R.id.rbtn12);*/

        rgColors.setOnCheckedChangeListener(new RelativeRadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RelativeRadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rbtn1:
                        selectedColorId = R.color.cwa_picker_blue;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionEditor.this, selectedColorId));
                        //rbtn1.setChecked(true);
                        break;

                    case R.id.rbtn2:
                        selectedColorId = R.color.cwa_picker_turquoise;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionEditor.this, selectedColorId));
                        //rbtn2.setChecked(true);
                        break;

                    case R.id.rbtn3:
                        selectedColorId = R.color.cwa_picker_green;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionEditor.this, selectedColorId));
                        //rbtn3.setChecked(true);
                        break;

                    case R.id.rbtn4:
                        selectedColorId = R.color.cwa_picker_yellow;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionEditor.this, selectedColorId));
                        //rbtn4.setChecked(true);
                        break;

                    case R.id.rbtn5:
                        selectedColorId = R.color.cwa_picker_orange;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionEditor.this, selectedColorId));
                        //rbtn5.setChecked(true);
                        break;

                    case R.id.rbtn6:
                        selectedColorId = R.color.cwa_picker_red;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionEditor.this, selectedColorId));
                        //rbtn6.setChecked(true);
                        break;

                    case R.id.rbtn7:
                        selectedColorId = R.color.cwa_picker_purple;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionEditor.this, selectedColorId));
                        //rbtn7.setChecked(true);
                        break;

                    case R.id.rbtn8:
                        selectedColorId = R.color.cwa_picker_grey1;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionEditor.this, selectedColorId));
                        //rbtn8.setChecked(true);
                        break;

                    case R.id.rbtn9:
                        selectedColorId = R.color.cwa_picker_green2;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionEditor.this, selectedColorId));
                        //rbtn9.setChecked(true);
                        break;

                    case R.id.rbtn10:
                        selectedColorId = R.color.cwa_picker_brown;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionEditor.this, selectedColorId));
                        //rbtn10.setChecked(true);
                        break;

                    case R.id.rbtn11:
                        selectedColorId = R.color.cwa_picker_purple2;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionEditor.this, selectedColorId));
                        //rbtn11.setChecked(true);
                        break;

                    case R.id.rbtn12:
                        selectedColorId = R.color.cwa_picker_maroon;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionEditor.this, selectedColorId));
                        //rbtn12.setChecked(true);
                        break;
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColour.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(selectedColorHex)));
                dialog.dismiss();
            }
        });

        lblCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
            hashMap.put("colColour", selectedColorHex);

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
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnEditCollection:
                goalStr = numGoal.getText().toString();
                collectionName = txtCollectionName.getText().toString();

                //checking if none of the data is empty
                if(!collectionName.isEmpty() && !goalStr.isEmpty() && !selectedColorHex.isEmpty()){
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

            case R.id.btnSelectColour:
                colourPickerDialog();
                break;

            case R.id.btnBack:
                //goes to the previous activity
                finish();
                break;
        }
    }
}