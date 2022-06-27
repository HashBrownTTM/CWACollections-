package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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

import com.example.cwacollections.adapters.AdapterItem;
import com.example.cwacollections.models.ModelItem;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class itemAdder extends AppCompatActivity  implements View.OnClickListener{
    Calendar calendar;
    TextView dtObtained, spCollections;
    ImageButton btnBack, btnDate, btnAddItem;
    EditText txtItemName, txtItemDescription;

    String date = "";
    int year, month, day;

    int goalLimit, itemCount;

    private ImageView itemImage;
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_IMAGE_CAPTURE_PERMISSION = 100;

    //tag for debugging
    private static final String TAG = "ADD_IMAGE_TAG";

    //progressDialog
    private ProgressDialog progressDialog;

    //url of picked image
    public Uri imageUrl = null;

    //arrayList to hold list of data of type ModelItem
    private ArrayList<ModelItem> itemArrayList;
    //adapter
    public AdapterItem adapterItem;

    //firebase auth
    private FirebaseAuth firebaseAuth;
    public StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Items");

    //array to hold items
    private ArrayList<String> collectionTitleArrayList, collectionIdArrayList, collectionGoalArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_adder);

        //initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();

        //initialise list before adding data
        itemArrayList = new ArrayList<>();

        itemImage = findViewById(R.id.imgItem);
        dtObtained = findViewById(R.id.dtObtained);
        btnBack = findViewById(R.id.btnBack);
        spCollections = findViewById(R.id.spCollections);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnDate = findViewById(R.id.btnDate);
        txtItemName = findViewById(R.id.txtItemName);
        txtItemDescription = findViewById(R.id.txtItemDescription);

        //for date selection
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        itemImage.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        spCollections.setOnClickListener(this);
        btnAddItem.setOnClickListener(this);
        btnDate.setOnClickListener(this);

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
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
            loadCollectionItems(uid);
        }
    }
    private void loadCollectionItems(String uid) {
        Log.d(TAG, "loadCollectionItems: Loading collections...");
        collectionTitleArrayList = new ArrayList<>();
        collectionIdArrayList = new ArrayList<>();
        collectionGoalArrayList = new ArrayList<>();

        //db reference to load collections... db > Collections
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Collections");
        ref.orderByChild("uid").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                collectionTitleArrayList.clear();
                collectionIdArrayList.clear();
                collectionGoalArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    //get id and title of collection
                    String collectionId = ""+ds.child("id").getValue();
                    String collectionTitle = ""+ds.child("collection").getValue();
                    String collectionGoal = ""+ds.child("Goal").getValue();

                    //add to respective arraylists
                    collectionTitleArrayList.add(collectionTitle);
                    collectionIdArrayList.add(collectionId);
                    collectionGoalArrayList.add(collectionGoal);
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
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

    private void addNewItem() {
        //all data is valid, can upload now
        //Upload to firebase storage
        Log.d(TAG, "uploadItemToStorage: uploading to storage...");

        //show progress
        progressDialog.setMessage("Uploading item...");
        progressDialog.show();

        //timestamp
        long timestamp = System.currentTimeMillis();

        //path of image in firebase
        String filePathAndName = "Items/" + timestamp;
        StorageReference storageRef = storageReference.child(filePathAndName);

        //storage reference
        storageRef.putFile(imageUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: Item uploaded to storage...");
                        Log.d(TAG, "onSuccess: getting image url");

                        //get image url
                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!urlTask.isSuccessful());
                        String uploadedItemUrl = "" + urlTask.getResult();

                        //upload to firebase db
                        uploadItemInfoToDb(uploadedItemUrl, timestamp);

                        spCollections.setText("");
                        txtItemName.setText("");
                        txtItemDescription.setText("");
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Item upload failed due to " + e.getMessage());
                        Toast.makeText(itemAdder.this,"Item upload failed due to " + e.getMessage(), Toast.LENGTH_SHORT ).show();
                    }
                });

    }

    private void uploadItemInfoToDb(String uploadedItemUrl, long timestamp) {
        //upload to firebase db
        Log.d(TAG, "uploadItemToStorage: uploading item info to firebase db...");

        progressDialog.setMessage("Uploading item info...");

        String uid = firebaseAuth.getUid();

        //setup data to upload
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + uid);
        hashMap.put("id", "" + timestamp);
        hashMap.put("title", "" + title);
        hashMap.put("description", "" + description);
        hashMap.put("collectionId", "" + selectedCollectionId);
        hashMap.put("url", "" + uploadedItemUrl);
        hashMap.put("dateObtained","" + date);
        hashMap.put("timestamp", timestamp);

        //db reference: DB > Items
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Items");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: uploaded successfully ");
                        Toast.makeText(itemAdder.this, "uploaded successfully", Toast.LENGTH_SHORT).show();

                        //reset image view tp
                        itemImage.setImageResource(R.drawable.add_image);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure:Failed to upload due to" +e.getMessage());
                        Toast.makeText(itemAdder.this, "Failed to upload due to" +e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String title = "", description = "";
    private String selectedCollectionId, selectedCollectionTitle;

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
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

    private void getNumberOfItems(String selectedCollectionId, DatabaseReference ref) {
        //get all items from firebase > Items
        ref.orderByChild("collectionId").equalTo(selectedCollectionId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemArrayList.clear();

                        for(DataSnapshot ds: snapshot.getChildren()){
                            //get data
                            ModelItem model = ds.getValue(ModelItem.class);
                            //add to list
                            itemArrayList.add(model);
                        }
                        //setup adapter
                        adapterItem = new AdapterItem(itemAdder.this, itemArrayList);
                        //get item count from adapter
                        itemCount = adapterItem.getItemCount();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(itemAdder.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //onClick method for this activity
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v){
        Intent intent;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (v.getId()){
            case R.id.imgItem:
                if(ActivityCompat.checkSelfPermission(itemAdder.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    final String[] permissions = {Manifest.permission.CAMERA};

                    //Request permission - this is asynchronous
                    ActivityCompat.requestPermissions(itemAdder.this, permissions, REQUEST_IMAGE_CAPTURE_PERMISSION);
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

            case R.id.btnDate:
                showDialog(999);
                break;

            case R.id.spCollections:
                //first we need to get collections from firebase
                Log.d(TAG, "collectionPickDialog: showing collections pick dialog");

                //get string array of collections from arrayList
                String[] collectionsArray = new String[collectionTitleArrayList.size()];
                for(int i = 0; i<collectionTitleArrayList.size(); i++){
                    collectionsArray[i] = collectionTitleArrayList.get(i);
                }

                builder.setTitle("Pick Collection")
                        .setItems(collectionsArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //handle item click
                                //get clicked item from list
                                selectedCollectionTitle = collectionTitleArrayList.get(which);
                                selectedCollectionId = collectionIdArrayList.get(which);
                                goalLimit = Integer.parseInt(collectionGoalArrayList.get(which));

                                //set to collection textview
                                spCollections.setText(selectedCollectionTitle);

                                getNumberOfItems(selectedCollectionId, ref);

                                Log.d(TAG, "onClick: Selected Collection: " + selectedCollectionId +
                                        " " + selectedCollectionTitle);
                            }
                        })
                        .show();
                break;
            case R.id.btnAddItem:
                //validate data
                Log.d(TAG, "validateData: validating data...");

                //get data
                title = txtItemName.getText().toString();
                description = txtItemDescription.getText().toString();

                //validate data
                if(TextUtils.isEmpty(title)){
                    Toast.makeText(this, "Enter Title", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(description)){
                    Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(selectedCollectionTitle)){
                    Toast.makeText(this, "Pick Collection", Toast.LENGTH_SHORT).show();
                }
                else if(imageUrl==null){
                    Toast.makeText(this, "Pick image", Toast.LENGTH_SHORT).show();
                }
                else{
                    if((itemCount + 1) > goalLimit){
                        Toast.makeText(this, "The goal amount of items in " + selectedCollectionTitle + " has already been reached.\n" +
                                "No more items can be added.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        builder.setTitle("Add new item")
                                .setMessage("Add new item: " + txtItemName.getText() + "?")
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addNewItem();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //cancel adding new item
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                }
                break;
        }
    }


}

/* References
TutorialsPoint. 2021. How to Go back to previous activity in android.
[ONLINE] Available at: https://www.tutorialspoint.com/how-to-go-back-to-previous-activity-in-android. [Accessed 02 June 2021].

Android Tutorials. 2021. Android Tutorials.
[ONLINE] Available at: https://devofandroid.blogspot.com/. [Accessed 02 June 2021].

Stack Overflow. 2021. Dialog to pick image from gallery or from camera.
[ONLINE] Available at: https://stackoverflow.com/questions/10165302/dialog-to-pick-image-from-gallery-or-from-camera. [Accessed 12 July 2021].

Stack Overflow. 2021. java - Convert string to date format in android - Stack Overflow.
[ONLINE] Available at: https://stackoverflow.com/questions/8878886/convert-string-to-date-format-in-android. [Accessed 14 July 2021].

*/
