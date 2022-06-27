package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cwacollections.adapters.AdapterItem;
import com.example.cwacollections.models.ModelItem;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class collectionItems extends AppCompatActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private RecyclerView rvItems;
    Toolbar toolbar;
    RelativeLayout toolbarHeading;
    ImageButton btnBack;
    FloatingActionButton btnAddItem;
    AppBarLayout appBarLayout;
    NestedScrollView nestedScrollView;

    TextView lblCollectionName, lblTCollectionName, llNoCollection;

    //arrayList to hold list of data of type ModelItem
    private ArrayList<ModelItem> itemArrayList;
    //adapter
    private AdapterItem adapterItem;

    MenuItem searchIcon;

    private String collectionId, collectionTitle, colColour = "";
    private int goal, itemCount;

    TextView lblGoal;

    private static final String TAG = "ITEM_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_items);

        toolbar = findViewById(R.id.toolbar);
        lblCollectionName = findViewById(R.id.lblCollectionName);
        lblTCollectionName = findViewById(R.id.lblTCollectionName);

        llNoCollection = findViewById(R.id.llNoCollection);
        llNoCollection.setVisibility(View.INVISIBLE);

        toolbarHeading = findViewById(R.id.toolbarHeading);
        appBarLayout = findViewById(R.id.appBarLayout);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        //Clickable Views
        btnBack = findViewById(R.id.btnBack);
        lblGoal = findViewById(R.id.lblGoal);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnBack.setOnClickListener(this);
        lblGoal.setOnClickListener(this);
        btnAddItem.setOnClickListener(this);

        toolbar.setOnMenuItemClickListener(this);

        rvItems = findViewById(R.id.rvItems);
        rvItems.setHasFixedSize(true);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        //Initialises the MenuItem
        searchIcon = new MenuDefault().mMenuItem;

        //get data from intent
        Intent intent = getIntent();
        collectionId = intent.getStringExtra("collectionId");
        collectionTitle = intent.getStringExtra("collectionTitle");
        goal = Integer.parseInt(intent.getStringExtra("goal"));
        colColour = intent.getStringExtra("colColour");

        //set item category
        lblCollectionName.setText(collectionTitle);
        lblTCollectionName.setText(collectionTitle);

        loadItemList(collectionId);

        CollapsingToolbarBehaviour();

        CheckDeviceOrientation();
    }

    private void loadItemList(String collectionId) {
        //initialise list before adding data
        itemArrayList = new ArrayList<>();

        //get all items from firebase > Items
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Items");
        ref.orderByChild("collectionId").equalTo(collectionId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemArrayList.clear();

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

                        lblGoal.setText("");
                        lblGoal.setText(adapterItem.getItemCount() + "/" + goal);
                        itemCount = adapterItem.getItemCount();

                        if(itemCount == 0){
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

    private void CollapsingToolbarBehaviour() {
        //Checks whether the Collapsing Toolbar is collapsed or expanded, StackOverflow (2022)
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()){
                    //Collapsed
                    //Toast.makeText(collectionItems.this, "Collapsed", Toast.LENGTH_SHORT).show();

                    toolbarHeading.setVisibility(View.INVISIBLE);
                    lblTCollectionName.setVisibility(View.VISIBLE);

                }
                else if (verticalOffset == 0){
                    //Expanded
                    //Toast.makeText(collectionItems.this, "Expanded", Toast.LENGTH_SHORT).show();

                    toolbarHeading.setVisibility(View.VISIBLE);
                    lblTCollectionName.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void CheckDeviceOrientation() {
        //checks if the app is in either landscape or portrait mode, Stack Overflow (2022)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // landscape
            //Toast.makeText(this, "App is in landscape mode now", Toast.LENGTH_SHORT).show();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
            rvItems.setLayoutManager(gridLayoutManager);

            appBarLayout.setExpanded(false);
            nestedScrollView.setNestedScrollingEnabled(false);
        }
        else {
            // portrait
            appBarLayout.setExpanded(true);
            nestedScrollView.setNestedScrollingEnabled(true);
        }
    }

    public void closeSearchView(){
        if (searchIcon.isActionViewExpanded()){
            searchIcon.collapseActionView();
        }
    }

    @Override
    public void onBackPressed() {
        if (searchIcon.isActionViewExpanded()){
            searchIcon.collapseActionView();
        }
        else{
            super.onBackPressed();
        }
    }

    //onClick method for this activity
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v){
        Intent intent;

        closeSearchView();

        switch (v.getId()){
            case R.id.btnBack:
                //goes to the previous activity
                finish();
                break;

            case R.id.btnAddItem:
                intent = new Intent(collectionItems.this, itemAdder.class);
                startActivity(intent);
                break;
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent intent;

        closeSearchView();

        switch (item.getItemId()){
            case R.id.itemEdit:
                intent = new Intent(collectionItems.this, collectionEditor.class);
                intent.putExtra("collectionId", collectionId);
                startActivity(intent);
                break;

            case R.id.itemItemSearch:
                searchIcon = item;
                SearchView searchView = (SearchView) item.getActionView();

                searchView.setQueryHint("Search...");

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapterItem.getFilter().filter(newText);
                        return true;
                    }
                });
                break;

            case R.id.itemProgress:
                intent = new Intent(collectionItems.this, collectionProgress.class);
                intent.putExtra("goal", ""+goal);
                intent.putExtra("itemCount", ""+itemCount);
                intent.putExtra("title", ""+collectionTitle);
                intent.putExtra("colColour", colColour);
                startActivity(intent);
                break;
        }

        return false;
    }
}

/* References
Book App Firebase | 06 Show Books Admin | Android Studio | Java. 2021. YouTube video, added by Atif Pervaiz. [ONLINE]
Available at: https://youtu.be/vgWihyzAv-U. [Accessed 14 July 2021].

Collapsing Toolbar in Android Studio | Collapsing Toolbar. 2020. YouTube video, added by Coding With Tea. [ONLINE]
Available at: https://youtu.be/xuWMi5IEFls. [Accessed 15 May 2022].

Stack Overflow. 2022. android - How can I determine that CollapsingToolbar is collapsed? - Stack Overflow. [ONLINE]
Available at: https://stackoverflow.com/a/31872915. [Accessed 15 May 2022].

*/