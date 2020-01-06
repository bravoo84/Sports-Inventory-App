package com.sportsCommittee.sportsinventory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.sportsCommittee.sportsinventory.Entity.Inventory;
import com.sportsCommittee.sportsinventory.FirebaseImageLoaderPack.FirebaseImageLoader;

import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    Toolbar toolbar;
    ListView listView;


    FirebaseListAdapter adapter;

    ArrayList<String> iconURL;



    @Override
    protected void onStart() {
        super.onStart();
        if(!validation())return;
        adapter.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        Log.d("Main","hellooooo");

        Query query = FirebaseDatabase.getInstance().getReference().child("inventory");

        FirebaseListOptions<Inventory> options = new FirebaseListOptions.Builder<Inventory>()
                                                    .setLifecycleOwner(MainActivity.this)
                                                    .setLayout(R.layout.activity_inventory_list)
                                                    .setQuery(query,Inventory.class).build();

        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(View v, Object model, int position) {



                ImageView icon = (ImageView)v.findViewById(R.id.icon);
                TextView inventory_name = (TextView)v.findViewById(R.id.inventory_name);
                TextView availability = (TextView)v.findViewById(R.id.availability);

                Inventory inventory = (Inventory) model;
                inventory_name.setText(inventory.getInventoryName());
                availability.setText("Available : "+Long.toString(inventory.getAvailable()));

                Log.d("Adapter!!",inventory.getIconURL());

                //icon.setBackgroundResource(0);
                StorageReference img_ref = FirebaseStorage.getInstance().getReferenceFromUrl(inventory.getIconURL());
                iconURL.add(inventory.getIconURL());

                Glide.with(MainActivity.this)
                        .using(new FirebaseImageLoader())
                        .load(img_ref)
                        .into(icon);


            }
        };



        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView temp = (TextView) view.findViewById(R.id.inventory_name);

                Intent intent = new Intent(MainActivity.this, InventoryTransaction.class);
                intent.putExtra("inventory_name",temp.getText().toString());
                intent.putExtra("iconURL",iconURL.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public boolean validation(){
        if(!isInternetConnected()|| !isLoggedIn()) return true;
        return false;
    }


    public boolean isInternetConnected(){

        if(!ConnectivityCheck.isNetworkConnected(getApplicationContext())){
            startActivity(new Intent(MainActivity.this,NoConnection.class));
            return false;
        }
        return true;
    }


    public boolean isLoggedIn(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return false;
        }
        return true;
    }

    public void init() {

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Inventory List");
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColorLight));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = (ListView)findViewById(R.id.inventory_list);

        iconURL = new ArrayList<>();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int res_id = item.getItemId();


        if(item.getItemId() == android.R.id.home){
                finish();
        }

        if(res_id == R.id.user_profile){
            Intent intent = new Intent(MainActivity.this,UserProfile.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }



}

