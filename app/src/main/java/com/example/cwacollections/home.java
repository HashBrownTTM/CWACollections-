package com.example.cwacollections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cwacollections.adapters.AdapterCollection;
import com.example.cwacollections.databinding.ActivityHomeBinding;
import com.example.cwacollections.models.ModelCollection;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class home extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    //Button btnLogout, btnNext;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    LinearLayout llNoCollection;

    //view binding
    private ActivityHomeBinding binding;

    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //arrayList to store collection
    private ArrayList<ModelCollection> collectionArrayList;
    //adapter
    private AdapterCollection adapterCollection;

    private static final String TAG = "COLLECTION_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        //Hooks
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        llNoCollection = findViewById(R.id.llNoCollection);
        llNoCollection.setVisibility(View.INVISIBLE);

        //init firebase auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        //Tool Bar
        setSupportActionBar(toolbar);

        //Navigation Drawer
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //edit text change listener, search
        binding.txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //called as and when user type each letter
                try{
                    adapterCollection.getFilter().filter(s);
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
            String uid = firebaseUser.getUid();
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
                        binding.rvCollections.setAdapter(adapterCollection);

                        int collectionCount = adapterCollection.getItemCount();
                        if(collectionCount == 0){
                            llNoCollection.setVisibility(View.VISIBLE);
                        }
                        else{
                            llNoCollection.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(home.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    //onClick methods
    @Override
    public void onClick(View v){
        Intent intent;
        /* switch (v.getId()){
            case R.id.btnLogout:
                -- code
                break;
        } */
    }

    //navigation items "OnClick" method
    @Override
    public boolean onNavigationItemSelected(@NonNull  MenuItem menuItem) {
        Intent intent;
        switch (menuItem.getItemId()){
            case R.id.nav_logout:
                //signs the user out once the button is clicked
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(home.this, "Signed out successfully", Toast.LENGTH_SHORT).show();

                intent = new Intent(home.this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_new_collection:
                intent = new Intent(home.this, collectionAdder.class);
                intent.putExtra("fragment_layout", "1");
                startActivity(intent);
                break;

            case R.id.nav_new_item:
                intent = new Intent(home.this, itemAdder.class);
                intent.putExtra("fragment_layout", "1");
                startActivity(intent);
                break;

            case R.id.nav_collections:
                break;

            case R.id.nav_game:
                intent = new Intent(home.this, memoryGame.class);
                startActivity(intent);
                break;
            case R.id.nav_account:
                intent = new Intent(home.this, userInfo.class);
                startActivity(intent);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

/* References
Sikander, T. 2021. Navigation Drawer Material Design in Android - 2020 - Taimoor Sikander, Android studio, Android development.
[ONLINE] Available at: https://www.taimoorsikander.com/create-a-navigation-drawer-material-design-in-android-studio-tutorial/. [Accessed 02 June 2021].

Android Tutorials. 2021. Android Tutorials.
[ONLINE] Available at: https://devofandroid.blogspot.com/. [Accessed 02 June 2021].

Book App Firebase | 04 Show Book Categories | Android Studio | Java. 2021. YouTube video, added by Atif Pervaiz
[ONLINE]. Available at: https://youtu.be/j6GrP2MdFos. [Accessed 02 June 2021].
*/