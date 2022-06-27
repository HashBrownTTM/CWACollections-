package com.example.cwacollections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class collectionAdder extends AppCompatActivity implements View.OnClickListener{
    EditText txtCollectionName, numGoal;
    ImageButton btnBack, btnCreateCollection, btnSelectColour;
    RelativeRadioGroup rgColors;
    View selectedColour;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    String goalStr = "", collectionName = "";
    String selectedColorHex = "";
    int goal = 0, selectedColorId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_adder);

        //initialising firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        txtCollectionName = findViewById(R.id.txtCollectionName);
        numGoal = findViewById(R.id.numGoal);
        selectedColour = findViewById(R.id.selectedColour);

        btnCreateCollection = findViewById(R.id.btnCreateCollection);
        btnBack = findViewById(R.id.btnBack);
        btnSelectColour = findViewById(R.id.btnSelectColour);
        btnCreateCollection.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnSelectColour.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
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
                        /*selectedColorHex = String.format("#%06X", (0xFFFFFF & R.color.cwa_picker_blue));*/
                        selectedColorId = R.color.cwa_picker_blue;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionAdder.this, selectedColorId));
                        //rbtn1.setChecked(true);
                        break;

                    case R.id.rbtn2:
                        selectedColorId = R.color.cwa_picker_turquoise;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionAdder.this, selectedColorId));
                        //rbtn2.setChecked(true);
                        break;

                    case R.id.rbtn3:
                        selectedColorId = R.color.cwa_picker_green;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionAdder.this, selectedColorId));
                        //rbtn3.setChecked(true);
                        break;

                    case R.id.rbtn4:
                        selectedColorId = R.color.cwa_picker_yellow;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionAdder.this, selectedColorId));
                        //rbtn4.setChecked(true);
                        break;

                    case R.id.rbtn5:
                        selectedColorId = R.color.cwa_picker_orange;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionAdder.this, selectedColorId));
                        //rbtn5.setChecked(true);
                        break;

                    case R.id.rbtn6:
                        selectedColorId = R.color.cwa_picker_red;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionAdder.this, selectedColorId));
                        //rbtn6.setChecked(true);
                        break;

                    case R.id.rbtn7:
                        selectedColorId = R.color.cwa_picker_purple;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionAdder.this, selectedColorId));
                        //rbtn7.setChecked(true);
                        break;

                    case R.id.rbtn8:
                        selectedColorId = R.color.cwa_picker_grey1;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionAdder.this, selectedColorId));
                        //rbtn8.setChecked(true);
                        break;

                    case R.id.rbtn9:
                        selectedColorId = R.color.cwa_picker_green2;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionAdder.this, selectedColorId));
                        //rbtn9.setChecked(true);
                        break;

                    case R.id.rbtn10:
                        selectedColorId = R.color.cwa_picker_brown;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionAdder.this, selectedColorId));
                        //rbtn10.setChecked(true);
                        break;

                    case R.id.rbtn11:
                        selectedColorId = R.color.cwa_picker_purple2;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionAdder.this, selectedColorId));
                        //rbtn11.setChecked(true);
                        break;

                    case R.id.rbtn12:
                        selectedColorId = R.color.cwa_picker_maroon;
                        selectedColorHex = "#" + Integer.toHexString(ContextCompat.getColor(collectionAdder.this, selectedColorId));
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
            hashMap.put("colColour", selectedColorHex);
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
                            selectedColorHex = "";
                            selectedColour.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(collectionAdder.this, R.color.white)));
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
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnCreateCollection:
                goalStr = numGoal.getText().toString();
                collectionName = txtCollectionName.getText().toString();

                //checking if none of the data is empty
                if(!collectionName.isEmpty() && !goalStr.isEmpty() && !selectedColorHex.isEmpty()){
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

/* References
TutorialsPoint. 2021. How to Go back to previous activity in android.
[ONLINE] Available at: https://www.tutorialspoint.com/how-to-go-back-to-previous-activity-in-android. [Accessed 02 June 2021].

Android Tutorials. 2021. Android Tutorials.
[ONLINE] Available at: https://devofandroid.blogspot.com/. [Accessed 02 June 2021].

*/