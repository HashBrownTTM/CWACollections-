package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cwacollections.adapters.AdapterItem;
import com.example.cwacollections.databinding.ActivityCollectionItemsBinding;
import com.example.cwacollections.models.ModelItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class collectionItems extends AppCompatActivity implements View.OnClickListener{
    private RelativeLayout relativeLayout;

    private RecyclerView rvItems;
    LinearLayout llNoCollection;
    ImageButton btnBack, btnStats;

    //view binding
    private ActivityCollectionItemsBinding binding;

    //arrayList to hold list of data of type ModelItem
    private ArrayList<ModelItem> itemArrayList;
    //adapter
    private AdapterItem adapterItem;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Items");

    private String collectionId, collectionTitle;
    private int goal, item;

    TextView lblGoal;

    private static final String TAG = "ITEM_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollectionItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        llNoCollection = findViewById(R.id.llNoCollection);
        llNoCollection.setVisibility(View.INVISIBLE);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        btnStats = findViewById(R.id.btnStats);
        btnStats.setOnClickListener(this);
        lblGoal = findViewById(R.id.lblGoal);
        lblGoal.setOnClickListener(this);

        rvItems = findViewById(R.id.rvItems);
        rvItems.setHasFixedSize(true);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        //get data from intent
        Intent intent = getIntent();
        collectionId = intent.getStringExtra("collectionId");
        collectionTitle = intent.getStringExtra("collectionTitle");
        goal = Integer.parseInt(intent.getStringExtra("goal"));

        lblGoal.setText("" + goal);

        //set item category
        binding.lblCollectionName.setText(collectionTitle);

        //initialise list before adding data
        itemArrayList = new ArrayList<>();

        loadItemList(collectionId, ref);

        //edit text change listener, search
        binding.txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //called as and when user type each letter
                try{
                    adapterItem.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d(TAG, "onTextChanged: "+e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadItemList(String collectionId, DatabaseReference ref) {
        //get all items from firebase > Items
        ref.orderByChild("collectionId").equalTo(collectionId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            //get data
                            ModelItem model = ds.getValue(ModelItem.class);
                            //add to list
                            itemArrayList.add(model);
                            Collections.sort(itemArrayList, new Comparator<ModelItem>() {
                                @Override
                                public int compare(ModelItem o1, ModelItem o2) {
                                    return o1.getDateObtained().compareTo(o2.getDateObtained());
                                }
                            });

                            Log.d(TAG, "onDataChange: " + model.getId() + " " + model.getTitle());
                        }
                        //setup adapter
                        adapterItem = new AdapterItem(collectionItems.this, itemArrayList);
                        //set adapter to recycle view
                        rvItems.setAdapter(adapterItem);

                        String gValue = binding.lblGoal.getText().toString();
                        binding.lblGoal.setText(adapterItem.getItemCount() + "/" + gValue);
                        item = adapterItem.getItemCount();
                        if(item == 0){
                            llNoCollection.setVisibility(View.VISIBLE);
                        }
                        else{
                            llNoCollection.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(collectionItems.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //onClick method for this activity
    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.btnBack:
                //goes to the previous activity
                finish();
                break;
            case R.id.lblGoal:
                intent = new Intent(collectionItems.this, manageCollections.class);
                intent.putExtra("goal", ""+goal);
                intent.putExtra("item", ""+item);
                intent.putExtra("title", ""+collectionTitle);
                startActivity(intent);
                break;
            case R.id.btnStats:
                intent = new Intent(collectionItems.this, collectionEditor.class);
                intent.putExtra("collectionId", collectionId);
                startActivity(intent);
                break;
        }
    }
}

/* References
Book App Firebase | 06 Show Books Admin | Android Studio | Java. 2021. YouTube video, added by Atif Pervaiz
[ONLINE]. Available at: https://youtu.be/vgWihyzAv-U. [Accessed 14 July 2021].

*/