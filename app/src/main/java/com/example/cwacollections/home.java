package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.discord.panels.OverlappingPanelsLayout;
import com.example.cwacollections.adapters.AdapterCollection;
import com.example.cwacollections.classes.AppBarStateChangeListener;
import com.example.cwacollections.models.ModelCollection;
import com.example.cwacollections.models.ModelItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class home extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, Toolbar.OnMenuItemClickListener {
    NavigationView navigationView;
    Toolbar toolbar;
    RecyclerView rvCollections;
    RelativeLayout toolbarHeading;
    AppBarLayout appBarLayout;
    NestedScrollView nestedScrollView;

    FrameLayout startPanel, endPanel, centerPanel;
    OverlappingPanelsLayout overlappingPanelsLayout;

    FloatingActionButton btnAddCollection, btnGoToTop;
    TextView lblProfile, lblAboutUs, lblHomeHeading, llNoCollection, lblCollectionNum, lblDeleteAll;

    MenuItem searchIcon;

    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //arrayList to store collection
    private ArrayList<ModelCollection> collectionArrayList;
    //adapter
    private AdapterCollection adapterCollection;

    private static final String TAG = "COLLECTION_LIST_TAG";

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Hooks
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        startPanel = findViewById(R.id.start_panel);
        centerPanel = findViewById(R.id.center_panel);
        endPanel = findViewById(R.id.end_panel);
        overlappingPanelsLayout = findViewById(R.id.overlapping_panels);

        toolbarHeading = findViewById(R.id.toolbarHeading);
        appBarLayout = findViewById(R.id.appBarLayout);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        rvCollections = findViewById(R.id.rvCollections);
        lblHomeHeading = findViewById(R.id.lblHomeHeading);
        lblCollectionNum = findViewById(R.id.lblCollectionNum);

        llNoCollection = findViewById(R.id.llNoCollection);
        llNoCollection.setVisibility(View.INVISIBLE);

        //Clickable Views
        lblProfile = findViewById(R.id.lblProfile);
        lblAboutUs = findViewById(R.id.lblAboutUs);
        lblDeleteAll = findViewById(R.id.lblDeleteAll);
        btnGoToTop = findViewById(R.id.btnGoToTop);
        btnAddCollection = findViewById(R.id.btnAddCollection);

        lblProfile.setOnClickListener(this);
        lblAboutUs.setOnClickListener(this);
        lblDeleteAll.setOnClickListener(this);
        btnGoToTop.setOnClickListener(this);
        btnAddCollection.setOnClickListener(this);

        //Tool Bar Navigation Click
        toolbar.setNavigationOnClickListener(v -> overlappingPanelsLayout.openStartPanel());

        //Menu Item Click Listeners
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setOnMenuItemClickListener(this);

        //Initialises the MenuItem
        searchIcon = new MenuDefault().mMenuItem;

        //Setting Tooltips
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            btnAddCollection.setTooltipText("Add new collection");
        }

        //init firebase auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        CollapsingToolbarBehaviour();

        CheckDeviceOrientation();

        RecyclerViewToTopBehaviour();
    }

    private void checkUser() {
        //get current user
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            //not logged in, goto main screen
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else{
            //logged in, get user info
            uid = firebaseUser.getUid();
            loadCollections(uid);
        }
    }

    private void loadCollections(String uid) {
        //initialise arrayList
        collectionArrayList = new ArrayList<>();

        //get all collections from firebase > Collections
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Collections");
        ref.orderByChild("uid").equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear arrayList before adding data into it
                        collectionArrayList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            //get data
                            ModelCollection model = ds.getValue(ModelCollection.class);

                            //add to arrayList
                            collectionArrayList.add(model);
                        }
                        //setup adapter
                        adapterCollection = new AdapterCollection(home.this, collectionArrayList);
                        //set adapter to recycle view
                        rvCollections.setAdapter(adapterCollection);

                        int collectionCount = adapterCollection.getItemCount();

                        if(collectionCount == 1){
                            lblCollectionNum.setText(collectionCount + " collection");
                            llNoCollection.setVisibility(View.INVISIBLE);
                        }
                        else if(collectionCount > 1){
                            lblCollectionNum.setText(collectionCount + " collections");
                            llNoCollection.setVisibility(View.INVISIBLE);
                        }
                        else if(collectionCount == 0){
                            lblCollectionNum.setText("");
                            llNoCollection.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(home.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void CollapsingToolbarBehaviour() {
        //Checks whether the Collapsing Toolbar is collapsed or expanded, StackOverflow (2022)
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
                AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;
                fadeIn.setDuration(200);
                fadeIn.setFillAfter(true);
                fadeOut.setDuration(200);
                fadeOut.setFillAfter(true);

                if(Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()){
                    //Collapsed
                    //Toast.makeText(collectionItems.this, "Collapsed", Toast.LENGTH_SHORT).show();

                    //toolbarHeading.setVisibility(View.INVISIBLE);
                    //lblHomeHeading.setVisibility(View.VISIBLE);

                    //toolbarHeading.startAnimation(fadeOut);
                }
                else if (verticalOffset == 0){
                    //Expanded
                    //Toast.makeText(collectionItems.this, "Expanded", Toast.LENGTH_SHORT).show();

                    //toolbarHeading.setVisibility(View.VISIBLE);
                    //lblHomeHeading.setVisibility(View.INVISIBLE);


                    //toolbarHeading.startAnimation(fadeIn);
                }
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if(state == State.EXPANDED){
                    lblHomeHeading.setVisibility(View.INVISIBLE);
                }
                else if(state == State.COLLAPSED){
                    lblHomeHeading.setVisibility(View.VISIBLE);
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
            rvCollections.setLayoutManager(gridLayoutManager);

            appBarLayout.setExpanded(false);
            nestedScrollView.setNestedScrollingEnabled(false);
        }
        else {
            // portrait
            appBarLayout.setExpanded(true);
            nestedScrollView.setNestedScrollingEnabled(true);
        }
    }

    private void RecyclerViewToTopBehaviour() {
        rvCollections.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { // No scrolling
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnGoToTop.setVisibility(View.GONE);
                        }
                    }, 2000); // delay of 2 seconds before hiding the fab
                }
                else{

                    btnGoToTop.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { // scrolling down
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnGoToTop.setVisibility(View.GONE);
                        }
                    }, 2000); // delay of 2 seconds before hiding the fab

                } else if (dy < 0) { // scrolling up

                    btnGoToTop.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void checkCollections() {
        DatabaseReference firstReference = FirebaseDatabase.getInstance().getReference("Collections");
        firstReference.orderByChild("uid").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        ModelCollection modelCollection = dataSnapshot.getValue(ModelCollection.class);

                        assert modelCollection != null;
                        deleteCollectionItems(modelCollection);
                    }

                    Toast.makeText(home.this, "All collection data has been deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteCollectionItems(ModelCollection modelCollection) {
        //get id of collection to delete
        String collectionId = modelCollection.getId();

        //Delete the items related to the collection first
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Items");
        ref.orderByChild("collectionId").equalTo(collectionId)
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

                                deleteItemImage(itemUrl, collectionId);
                            }
                        }
                        else{
                            //if collection has no items
                            deleteCollection(collectionId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void deleteItemImage(String itemUrl, String collectionId) {
        //delete the images related to the deleted items
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(itemUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        deleteCollection(collectionId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteCollection(String collectionId) {
        //delete the collection, firebase DB > Collections > collectionId
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Collections");
        ref1.child(collectionId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //deleted successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        //failed to delete
                        Toast.makeText(home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (searchIcon.isActionViewExpanded()){
            searchIcon.collapseActionView();
        }
        else if(startPanel.isShown()){
            overlappingPanelsLayout.closePanels();
        }
        else if(endPanel.isShown()){
            overlappingPanelsLayout.closePanels();
        }
        else if(!searchIcon.isActionViewExpanded() && !startPanel.isShown() && !endPanel.isShown()){
            super.onBackPressed();
        }
    }

    //onClick methods
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.btnAddCollection:
                intent = new Intent(home.this, collectionAdder.class);
                intent.putExtra("fragment_layout", "1");
                startActivity(intent);
                break;

            case R.id.btnGoToTop:
                nestedScrollView.scrollTo(0, 0);
                break;

            case R.id.lblProfile:
                intent = new Intent(home.this, Profile.class);
                startActivity(intent);
                break;

            case R.id.lblAboutUs:
                intent = new Intent(home.this, AboutUs.class);
                startActivity(intent);
                break;

            case R.id.lblDeleteAll:
                checkCollections();
                break;
        }
    }

    //navigation items "OnClick" method
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull  MenuItem menuItem) {
        Intent intent;
        switch (menuItem.getItemId()){
            case R.id.nav_logout:
                Confirmation();
                break;

            case R.id.nav_new_collection:
                intent = new Intent(home.this, collectionAdder.class);
                startActivity(intent);
                break;

            case R.id.nav_new_item:
                intent = new Intent(home.this, itemAdder.class);
                startActivity(intent);
                break;

            case R.id.nav_collections:
                break;

            case R.id.nav_game:
                intent = new Intent(home.this, memoryGame.class);
                startActivity(intent);
                break;

            /*case R.id.nav_account:
                intent = new Intent(home.this, userInfo.class);
                startActivity(intent);
                break;*/
        }

        //drawerLayout.closeDrawer(GravityCompat.START);
        overlappingPanelsLayout.closePanels();
        return true;
    }

    private void Confirmation() {
        Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_CONTEXT_MENU);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_confirm);

        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        TextView lblCancel = dialog.findViewById(R.id.lblCancel);
        TextView lblQuestion = dialog.findViewById(R.id.lblQuestion);

        lblQuestion.setText("Are you sure you want to log out?");

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //signs the user out once the button is clicked
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(home.this, "Signed out successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(home.this, LoginOrRegister.class);
                startActivity(intent);
                finish();
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

    //Toolbar menu items "OnClick" method
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemSettings:
                overlappingPanelsLayout.openEndPanel();

                if (searchIcon.isActionViewExpanded()){
                    searchIcon.collapseActionView();
                }

                break;

            case R.id.itemSearch:
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
                        adapterCollection.getFilter().filter(newText);
                        return true;
                    }
                });
                break;
        }

        return false;
    }
}

/* References
Sikander, T. 2021. Navigation Drawer Material Design in Android - 2020 - Taimoor Sikander, Android studio, Android development.
[ONLINE] Available at: https://www.taimoorsikander.com/create-a-navigation-drawer-material-design-in-android-studio-tutorial/. [Accessed 02 June 2021].

Android Tutorials. 2021. Android Tutorials.
[ONLINE] Available at: https://devofandroid.blogspot.com/. [Accessed 02 June 2021].

Book App Firebase | 04 Show Book Categories | Android Studio | Java. 2021. YouTube video, added by Atif Pervaiz
[ONLINE]. Available at: https://youtu.be/j6GrP2MdFos. [Accessed 02 June 2021].

Stack Overflow. 2022. default - Find out if Android device is portrait or landscape for normal usage? - Stack Overflow.
[ONLINE] Available at: https://stackoverflow.com/questions/3674933/find-out-if-android-device-is-portrait-or-landscape-for-normal-usage. [Accessed 04 May 2022].
*/