package com.example.cwacollections.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cwacollections.filters.FilterItems;
import com.example.cwacollections.databinding.RowItemBinding;
import com.example.cwacollections.home;
import com.example.cwacollections.itemEditor;
import com.example.cwacollections.models.ModelItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterItem extends RecyclerView.Adapter<AdapterItem.HolderItem> implements Filterable {

    public Context context;
    //arraylist to hold list of data of type ModelItem
    public ArrayList<ModelItem> itemArrayList, filterList;

    //view binding row_item.xml
    public RowItemBinding binding;

    //instance of the filter class
    public FilterItems filter;

    private static final String TAG = "ITEM_ADAPTER_TAG";

    //progress dialog
    private ProgressDialog progressDialog;

    String title = "", description = "", dateObtained = "";

    //constructor
    public AdapterItem(Context context, ArrayList<ModelItem> itemArrayList) {
        this.context = context;
        this.itemArrayList = itemArrayList;
        this.filterList = itemArrayList;

        //initialise progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

    }

    @NonNull
    @Override
    public HolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind row_item.xml
        binding = RowItemBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderItem(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterItem.HolderItem holder, int position) {
        /*Get, set, and handle data and clicks...*/
        //get data
        ModelItem model = itemArrayList.get(position);
        title = model.getTitle();
        description = model.getDescription();
        dateObtained = model.getDateObtained();
        String uid = model.getUid();
        long timestamp = model.getTimestamp();

        //set data
        holder.lblTitle.setText(title);
        holder.lblDescription.setText(description);
        holder.lblDate.setText(dateObtained);

        //load other details
        loadCollection(model, holder);
        loadItemFromUrl(model, holder);

        //handle click, show dialog with options 1)Edit and 2)Delete
        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptionsDialog(model, holder);
            }
        });
    }

    private void moreOptionsDialog(ModelItem model, HolderItem holder) {
        String itemId = model.getId();
        String itemUrl = model.getUrl();
        String itemTitle = model.getTitle();

        //options to show in dialog
        String[] options ={"Edit", "Share", "Delete"};

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Option")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle dialog option click
                        if (which == 0) {
                            //edit selected, open itemEditor activity
                            Intent intent = new Intent(context, itemEditor.class);
                            intent.putExtra("itemId", itemId);
                            context.startActivity(intent);
                        }
                        else if(which==1){
                            //sends item data
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            String body = "" + itemUrl + "\n\n" +
                                    "Item name: " + title + "\n" +
                                    "Description:\n" + description + "\n" +
                                    "Date Obtained: " + dateObtained;
                            intent.putExtra(Intent.EXTRA_TEXT, body);
                            context.startActivity(Intent.createChooser(intent, "Share using.."));
                        }
                        else if(which==2){
                            //delete selected
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Delete")
                                    .setMessage("Are you sure you want to delete " + model.getTitle() + "?")
                                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show();
                                            deleteItem(model, holder);

                                            Intent intent = new Intent(context, home.class);
                                            context.startActivity(intent);
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
    }

    private void deleteItem(ModelItem model, HolderItem holder) {
        String itemId = model.getId();
        String itemUrl = model.getUrl();
        String itemTitle = model.getTitle();

        Log.d(TAG, "deleteItem: Deleting...");
        progressDialog.setMessage("Deleting " + itemTitle + "...");
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
                                        Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Failed to delete from db due to "+e.getMessage());
                                        progressDialog.dismiss();
                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to delete from storage due to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadItemFromUrl(ModelItem model, HolderItem holder) {
        //use url to get file and its metadata from firebase storage
        /*see Retrieve Images From Firebase Storage using URL Stored in Realtime Database
          (Image Uploader Part2), 2020*/
        String itemUrl = model.getUrl();
        Glide.with(context)
                .load(itemUrl)
                .into(holder.picView);

    }

    private void loadCollection(ModelItem model, HolderItem holder) {
        //get collection using collectionId
        String collectionId = model.getCollectionId();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Collections");
        ref.child(collectionId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String collection = ""+snapshot.child("collection").getValue();

                        //set to collection textview
                        holder.lblCollection.setText(collection);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    //returns the number of records/the list size
    public int getItemCount() {
        return itemArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterItems(filterList, this);
        }
        return filter;
    }

    //View holder class to hold UI views for row_item.xml
    public class HolderItem extends RecyclerView.ViewHolder{
        //UI Views of row_item.xml
        ImageView picView;
        TextView lblTitle, lblDescription, lblCollection, lblDate;
        ImageButton btnMore;

        public HolderItem(@NonNull View itemView) {
            super(itemView);

            //initialise UI views
            picView = binding.picView;
            lblTitle = binding.lblTitle;
            lblDescription = binding.lblDescription;
            lblCollection = binding.lblCollection;
            lblDate = binding.lblDate;
            btnMore = binding.btnMore;

        }
    }
}

/*References
Retrieve Images From Firebase Storage using URL Stored in Realtime Database (Image Uploader Part2). 2020. YouTube video, added by CodingSTUFF.
[ONLINE]. Available at: https://youtu.be/iEcokZOv5UY. [Accessed 12 July 2021].

Book App Firebase | 06 Show Books Admin | Android Studio | Java. 2021. YouTube video, added by Atif Pervaiz.
[ONLINE]. Available at: https://youtu.be/vgWihyzAv-U. [Accessed 12 July 2021].

Book App Firebase | 07 Edit Delete Books Admin | Android Studio | Java. 2021. YouTube video, added by Atif Pervaiz
[ONLINE]. Available at: https://youtu.be/Vp8PcHuBzys. [Accessed 14 July 2021].

*/
