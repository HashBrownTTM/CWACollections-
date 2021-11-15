package com.example.cwacollections.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cwacollections.filters.FilterCollections;
import com.example.cwacollections.collectionItems;
import com.example.cwacollections.models.ModelCollection;
import com.example.cwacollections.databinding.RowCollectionBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterCollection extends RecyclerView.Adapter<AdapterCollection.HolderCollection> implements Filterable{

    private Context context;
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
        long timestamp = model.getTimestamp();
        int Goal = model.getGoal();

        //set label text
        holder.lblCollection.setText(collection);

        //handle click, delete category
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //confirm delete dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                    .setMessage("Are you sure you want to delete " + model.getCollection() + "?")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //confirm deleting
                            deleteCollection(model, holder);
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
        });

        //handle item click, goto collectionItems, also pass item and collectionId
        holder.cvCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, collectionItems.class);
                intent.putExtra("collectionId", id);
                intent.putExtra("collectionTitle", collection);
                intent.putExtra("goal", ""+Goal);
                context.startActivity(intent);
            }
        });

    }

    private void deleteCollection(ModelCollection model, HolderCollection holder) {
        Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show();

        //get id of category to delete
        String id = model.getId();

        //firebase DB > Collections > categoryId
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
        //ui views of row_collection.xml
        TextView lblCollection;
        ImageButton btnDelete;

        public HolderCollection(@NonNull View itemView){
            super(itemView);

            //initialize ui views
            cvCollection = binding.cvCollection;
            lblCollection = binding.lblCollection;
            btnDelete = binding.btnDelete;
        }
    }
}

/* References
Book App Firebase | 04 Show Book Categories | Android Studio | Java. 2021. YouTube video, added by Atif Pervaiz
[ONLINE]. Available at: https://youtu.be/j6GrP2MdFos. [Accessed 13 July 2021].

*/
