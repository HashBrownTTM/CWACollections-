package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ItemDescription extends AppCompatActivity implements View.OnClickListener{
    ImageButton btnBack, btnEdit, btnMore;
    ImageView picView;
    TextView lblItemHeading, txtItemDescription, lblDateObtained;
    PopupMenu dropDownMenu;
    Menu menu;

    String itemId, title, dateObtained, description, itemUrl;

    private static final String TAG = "ITEM_DESC_TAG";

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_description);

        picView = findViewById(R.id.picView);
        picView.setOnClickListener(this);
        lblItemHeading = findViewById(R.id.lblItemHeading);
        txtItemDescription = findViewById(R.id.txtItemDescription);
        lblDateObtained = findViewById(R.id.lblDateObtained);

        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);
        btnMore = findViewById(R.id.btnMore);
        btnBack.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnMore.setOnClickListener(this);

        //initialise progress dialog
        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        dropDownMenu = new PopupMenu(this, btnMore);

        menu = dropDownMenu.getMenu();
        dropDownMenu.getMenuInflater().inflate(R.menu.item_description_menu, menu);

        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.itemShare:
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        String body = "Item name: " + title + "\n\n" +
                                "Description:\n" + description + "\n\n" +
                                "Date Obtained: " + dateObtained + "\n\n" +
                                itemUrl;

                        intent.putExtra(Intent.EXTRA_TEXT, body);
                        startActivity(Intent.createChooser(intent, "Share using..."));
                        return true;

                    case R.id.itemDelete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(ItemDescription.this);
                        builder.setTitle("Delete")
                                .setMessage("Are you sure you want to delete " + title + "?")
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(ItemDescription.this, "Deleting...", Toast.LENGTH_SHORT).show();
                                        deleteItem();
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
                        return true;
                    default:
                        return false;
                }
            }
        });
        Intent intent = getIntent();
        itemId = intent.getStringExtra("itemId");

        loadItemInfo();
    }

    private void deleteItem() {
        Log.d(TAG, "deleteItem: Deleting...");
        progressDialog.setMessage("Deleting " + title + "...");
        progressDialog.show();

        Log.d(TAG, "deleteItem: Deleting from storage...");
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(itemUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Deleted from storage");

                        Log.d(TAG, "onSuccess: Now deleting information from database");
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Items");
                        reference.child(itemId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: Deleted from db too");
                                        progressDialog.dismiss();
                                        Toast.makeText(ItemDescription.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Failed to delete from db due to "+e.getMessage());
                                        progressDialog.dismiss();
                                        Toast.makeText(ItemDescription.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to delete from storage due to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(ItemDescription.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadItemInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Items");
        reference.child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get data
                title = ""+snapshot.child("title").getValue();
                dateObtained = ""+snapshot.child("dateObtained").getValue();
                description = ""+snapshot.child("description").getValue();
                itemUrl = ""+snapshot.child("url").getValue();

                //set data
                lblItemHeading.setText(title);
                txtItemDescription.setText(description);
                lblDateObtained.setText(dateObtained);

                Glide.with(ItemDescription.this).load(itemUrl).centerCrop().into(picView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btnBack:
                //goes to the previous activity
                finish();
                break;
            case R.id.btnEdit:
                intent = new Intent(this, itemEditor.class);
                intent.putExtra("itemId", itemId);
                startActivity(intent);
                finish();
                break;
            case R.id.picView:
                intent = new Intent(this, FullScreenImage.class);
                intent.putExtra("itemUrl", itemUrl);
                startActivity(intent);
                break;
            case R.id.btnMore:
                dropDownMenu.show();
                break;
        }
    }

}