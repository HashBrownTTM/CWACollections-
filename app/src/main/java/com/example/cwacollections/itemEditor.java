package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class itemEditor extends AppCompatActivity implements View.OnClickListener{
    Calendar calendar;
    TextView dtObtained, spCollections;
    ImageButton btnBack;
    EditText txtItemName, txtItemDescription;
    Button btnEditItem;

    ImageView itemImage;
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_IMAGE_CAPTURE_PERMISSION = 100;

    String date = "";
    long timestamp = 0;
    int year, month, day;

    //item id from intent started from AdapterItem
    private String itemId;

    String uploadedItemUrl = "";
    private String selectedCollectionId = "", selectedCollectionTitle = "";

    //firebase auth
    private FirebaseAuth firebaseAuth;
    public StorageReference storageReference = FirebaseStorage.getInstance().getReference();


    //progress dialog
    private ProgressDialog progressDialog;

    //url of picked image
    public Uri imageUrl = null;

    private ArrayList<String> collectionTitleArrayList, collectionIdArrayList;

    private static final String TAG = "ITEM_EDIT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_editor);

        //initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        itemImage = findViewById(R.id.imgItem);
        dtObtained = findViewById(R.id.dtObtained);
        btnBack = findViewById(R.id.btnBack);
        spCollections = findViewById(R.id.spCollections);
        btnEditItem = findViewById(R.id.btnEditItem);
        txtItemName = findViewById(R.id.txtItemName);
        txtItemDescription = findViewById(R.id.txtItemDescription);

        itemImage.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        spCollections.setOnClickListener(this);
        btnEditItem.setOnClickListener(this);

        //item id from intent started from AdapterItem
        itemId = getIntent().getStringExtra("itemId");

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //for date selection
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        checkUser();
        loadItemInfo();
    }

    private void checkUser() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            //not logged in, goto main screen
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else{
            //logged in, get user info
            String uid = firebaseUser.getUid();
            loadCollections(uid);
        }
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        try{
            //changing the date format to dd/MM/yyyy, Stack Overflow (2021)
            String selectedDate = new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year).toString();

            SimpleDateFormat dateFormat =  new SimpleDateFormat("dd/MM/yyyy");

            Date objDate = dateFormat.parse(selectedDate);

            SimpleDateFormat dateFormat2 =  new SimpleDateFormat("dd/MM/yyyy");

            String finalDate = dateFormat2.format(objDate);

            dtObtained.setText(finalDate);
        }
        catch(Exception e){}

        date = dtObtained.getText().toString();
    }

    private void loadItemInfo() {
        Log.d(TAG, "loadItemInfo: Loading item info");

        DatabaseReference refItems = FirebaseDatabase.getInstance().getReference("Items");
        refItems.child(itemId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get item info
                        selectedCollectionId = ""+snapshot.child("collectionId").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        String title = ""+snapshot.child("title").getValue();
                        timestamp = Long.parseLong(""+snapshot.child("timestamp").getValue());
                        uploadedItemUrl = ""+snapshot.child("url").getValue();
                        date = ""+snapshot.child("dateObtained").getValue();

                        //set to views
                        Glide.with(itemEditor.this)
                                .load(uploadedItemUrl)
                                .into(itemImage);

                        txtItemName.setText(title);
                        txtItemDescription.setText(description);
                        dtObtained.setText(date);

                        Log.d(TAG, "onDataChange: Loading item collection info");
                        DatabaseReference refItemCollection = FirebaseDatabase.getInstance().getReference("Collections");
                        refItemCollection.child(selectedCollectionId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //get collection
                                        String collection = ""+snapshot.child("collection").getValue();
                                        //set to collection text view
                                        spCollections.setText(collection);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void collectionDialog(){
        //make string array from arraylist of string
        String[] collectionsArray = new String[collectionTitleArrayList.size()];
        for(int i=0; i<collectionTitleArrayList.size(); i++){
            collectionsArray[i] = collectionTitleArrayList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose collection")
                .setItems(collectionsArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedCollectionId = collectionIdArrayList.get(which);
                        selectedCollectionTitle = collectionTitleArrayList.get(which);

                        //set to textview
                        spCollections.setText(selectedCollectionTitle);
                    }
                })
                .show();
    }
    
    private void loadCollections(String uid) {
        Log.d(TAG, "loadCollections: Loading collections...");

        collectionIdArrayList = new ArrayList<>();
        collectionTitleArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Collections");
        ref.orderByChild("uid").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                collectionIdArrayList.clear();
                collectionTitleArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    String id = ""+ds.child("id").getValue();
                    String collection = ""+ds.child("collection").getValue();
                    collectionIdArrayList.add(id);
                    collectionTitleArrayList.add(collection);

                    Log.d(TAG, "onDataChange: ID: "+id);
                    Log.d(TAG, "onDataChange: Collection: "+collection);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String title="", description = "";
    private void validateData(){
        //get data
        title = txtItemName.getText().toString().trim();
        description = txtItemDescription.getText().toString().trim();
        date = dtObtained.getText().toString().trim();

        //validate data
        if(TextUtils.isEmpty(title)){
            Toast.makeText(this, "Enter Item Name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(selectedCollectionId)){
            Toast.makeText(this, "Pick a collection ", Toast.LENGTH_SHORT).show();
        }
        else if(imageUrl==null){
            //if no new image has been selected
            updateItemData();
        }
        else{
            //if a new image has been selected
            updateItemDataWithImage();
        }
    }

    private void updateItemData() {
        Log.d(TAG, "validateData: Starting updating item info to db...");

        //show progress
        progressDialog.setMessage("Updating item info...");
        progressDialog.show();

        //setup data to update to db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("title", ""+title);
        hashMap.put("description", ""+description);
        hashMap.put("collectionId", ""+selectedCollectionId);
        hashMap.put("dateObtained", ""+date);

        //start updating
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Items");
        ref.child(itemId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Book updating...");
                        progressDialog.dismiss();
                        Toast.makeText(itemEditor.this, "Item info updated", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(itemEditor.this, home.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: failed to update due to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(itemEditor.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateItemDataWithImage() {
        Log.d(TAG, "validateData: Starting updating item info to db...");

        //show progress
        progressDialog.setMessage("Updating item info...");
        progressDialog.show();

        //path of image in firebase
        String filePathAndName = "Items/" + timestamp;
        StorageReference storageRef = storageReference.child(filePathAndName);

        //storage reference
        storageRef.putFile(imageUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: Item editing to storage...");
                        Log.d(TAG, "onSuccess: getting image url");

                        //get image url
                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!urlTask.isSuccessful());
                        uploadedItemUrl = "" + urlTask.getResult();


                        //setup data to update to db
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("title", ""+title);
                        hashMap.put("description", ""+description);
                        hashMap.put("collectionId", ""+selectedCollectionId);
                        hashMap.put("url", "" + uploadedItemUrl);
                        hashMap.put("dateObtained", ""+date);

                        //start updating
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Items");
                        ref.child(itemId)
                                .updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: Book updating...");
                                        progressDialog.dismiss();
                                        Toast.makeText(itemEditor.this, "Item info updated", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(itemEditor.this, home.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: failed to update due to "+e.getMessage());
                                        progressDialog.dismiss();
                                        Toast.makeText(itemEditor.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Item editing failed due to " + e.getMessage());
                        Toast.makeText(itemEditor.this,"Item editing failed due to " + e.getMessage(), Toast.LENGTH_SHORT ).show();
                    }
                });


    }

    private void selectPhoto(){
        //to select a photo from the devices gallery
        Log.d(TAG, "takePhoto: starting photo selecting intent");
        //Stack Overflow (2021).
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    private void takePhoto(){
        //to take a picture
        Log.d(TAG, "takePhoto: starting photo taking intent");
        //Stack Overflow (2021).
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check if we are receiving the result from the right request, Stack Overflow (2021)
        //Also check whether the data is null, meaning the user may have cancelled
        if(requestCode == REQUEST_IMAGE_CAPTURE && data != null){
            Log.d(TAG, "onActivityResult: image selected");

            if(data.getData() != null){
                //if selecting a picture from the gallery was chosen
                imageUrl = data.getData();
            }
            else{
                //if taking a picture was chosen
                Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, "val",null);

                imageUrl = Uri.parse(path);
            }

            itemImage.setImageURI(imageUrl);
            Log.d(TAG, "onActivityResult: URL: " + imageUrl);
        }
        else{
            Log.d(TAG, "onActivityResult: cancelled selecting image");
            Toast.makeText(this, "cancelled selecting image", Toast.LENGTH_SHORT).show();
        }
    }

    //onClick method for this activity
    @Override
    public void onClick(View v){
        Intent intent;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (v.getId()){
            case R.id.imgItem:
                if(ActivityCompat.checkSelfPermission(itemEditor.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    final String[] permissions = {Manifest.permission.CAMERA};

                    //Request permission - this is asynchronous
                    ActivityCompat.requestPermissions(itemEditor.this, permissions, REQUEST_IMAGE_CAPTURE_PERMISSION);
                }
                else{
                    //Permission has been granted
                    //options to show in dialog
                    String[] options ={"Choose from Gallery", "Take a Picture"};

                    //alert dialog
                    builder.setTitle("Choose Option")
                            .setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //handles dialog option click
                                    if(which == 0){
                                        //if selecting from the gallery is chosen
                                        selectPhoto();
                                    }
                                    else if(which == 1){
                                        //if taking a picture with the camera is chosen
                                        takePhoto();
                                    }
                                }
                            })
                            .show();
                }
                break;
            case R.id.btnBack:
                //goes to the previous activity
                finish();
                break;
            case R.id.spCollections:
                collectionDialog();
                break;
            case R.id.btnEditItem:
                builder.setTitle("Edit item")
                        .setMessage("Edit this item's data?")
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
Book App Firebase | 07 Edit Delete Books Admin | Android Studio | Java. 2021. YouTube video, added by Atif Pervaiz
[ONLINE]. Available at: https://youtu.be/Vp8PcHuBzys. [Accessed 14 July 2021].

Stack Overflow. 2021. java - Convert string to date format in android - Stack Overflow.
[ONLINE] Available at: https://stackoverflow.com/questions/8878886/convert-string-to-date-format-in-android. [Accessed 14 July 2021].

*/