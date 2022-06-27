package com.example.cwacollections.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cwacollections.collectionEditor;
import com.example.cwacollections.filters.FilterCollections;
import com.example.cwacollections.collectionItems;
import com.example.cwacollections.itemEditor;
import com.example.cwacollections.models.ModelCollection;
import com.example.cwacollections.databinding.RowCollectionBinding;
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

public class AdapterCollection extends RecyclerView.Adapter<AdapterCollection.HolderCollection> implements Filterable{

    private final Context context;
    //arraylist to hold list of data of type ModelCollection
    public ArrayList<ModelCollection> collectionArrayList, filterList;

    //view binding
    private RowCollectionBinding binding;

    //instance of the filter class
    public FilterCollections filter;

    public AdapterCollection(Context context, ArrayList<ModelCollection> collectionArrayList){
        this.context = context;
        this.collectionArrayList = collectionArrayList;
        this.filterList = collectionArrayList;
    }

    @NonNull
    @Override
    public HolderCollection onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        //bind row_collection.xml
        binding = RowCollectionBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderCollection(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCollection.HolderCollection holder, int position) {
        /*Get, set, and handle data and clicks...*/
        // get data
        ModelCollection model = collectionArrayList.get(position);
        String id = model.getId();
        String collection = model.getCollection();
        String uid = model.getUid();
        String colColour = model.getColColour();
        long timestamp = model.getTimestamp();
        int Goal = model.getGoal();

        //set label text
        holder.lblCollection.setText(collection);
        holder.folderColour.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colColour)));

        //handle item click, goto collectionItems, also pass item and collectionId
        holder.cvCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, collectionItems.class);
                intent.putExtra("collectionId", id);
                intent.putExtra("collectionTitle", collection);
                intent.putExtra("goal", ""+Goal);
                intent.putExtra("colColour", ""+colColour);
                context.startActivity(intent);
            }
        });

        holder.cvCollection.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //options to show in dialog
                String[] options ={"Edit", "Delete"};

                AlertDialog.Builder optionBuilder = new AlertDialog.Builder(context);
                optionBuilder.setTitle("Choose Option")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //handle dialog option click
                                if (which == 0) {
                                    //edit selected, open itemEditor activity
                                    Intent intent = new Intent(context, collectionEditor.class);
                                    intent.putExtra("collectionId", id);
                                    context.startActivity(intent);
                                }
                                else if(which==1){
                                    //delete selected
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Delete")
                                            .setMessage("Are you sure you want to delete " + model.getCollection() + "?")
                                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //confirm deleting
                                                    deleteCollectionItems(model);
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
                return true;
            }
        });
    }

    private void deleteCollectionItems(ModelCollection model) {
        //get id of collection to delete
        String id = model.getId();

        //Delete the items related to the collection first
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Items");
        reference.orderByChild("collectionId")
                .equalTo(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            //if collection has items
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                ModelItem modelItem = dataSnapshot.getValue(ModelItem.class);

                                //get the url for the items' images
                                assert modelItem != null;
                                String itemUrl = modelItem.getUrl();

                                //deletes the collection items
                                dataSnapshot.getRef().removeValue();

                                deleteItemImage(itemUrl, id);
                            }
                        }
                        else{
                            //if collection does not have items
                            deleteCollection(id);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void deleteItemImage(String itemUrl, String id) {
        //delete the images related to the deleted items
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(itemUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        deleteCollection(id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteCollection(String id) {
        //delete the collection, firebase DB > Collections > collectionId
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Collections");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //deleted successfully
                        Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        //failed to delete
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    //returns the number of records/the list size
    public int getItemCount() {
        return collectionArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterCollections(filterList, this);
        }
        return filter;
    }

    //view holder class to hold UI views for row_collection.xml
    class HolderCollection extends RecyclerView.ViewHolder{

        CardView cvCollection;
        View folderColour;
        //ui views of row_collection.xml
        TextView lblCollection;

        public HolderCollection(@NonNull View itemView){
            super(itemView);

            //initialize ui views
            cvCollection = binding.cvCollection;
            lblCollection = binding.lblCollection;
            folderColour = binding.folderColour;
        }
    }
}

/* References
Book App Firebase | 04 Show Book Categories | Android Studio | Java. 2021. YouTube video, added by Atif Pervaiz
[ONLINE]. Available at: https://youtu.be/j6GrP2MdFos. [Accessed 13 July 2021].

*/
