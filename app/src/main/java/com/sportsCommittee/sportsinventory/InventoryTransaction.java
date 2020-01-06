package com.sportsCommittee.sportsinventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sportsCommittee.sportsinventory.FirebaseImageLoaderPack.FirebaseImageLoader;

import java.util.ArrayList;

public class InventoryTransaction extends AppCompatActivity {


    Toolbar toolbar;
    Button issue,return_btn,update;
    ImageView icon;
    TextView inventory_name;

    @Override
    protected void onStart() {
        super.onStart();
        if(!validation())return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_transaction);


        init();

        Intent intent = (Intent) getIntent();

        String inventoryName = intent.getStringExtra("inventory_name");
        inventory_name.setText(inventoryName);

        String iconURL = intent.getStringExtra("iconURL");
        StorageReference img_ref = FirebaseStorage.getInstance().getReferenceFromUrl(iconURL);

        Glide.with(InventoryTransaction.this)
                .using(new FirebaseImageLoader())
                .load(img_ref)
                .into(icon);

        issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    public boolean validation(){
        if(!isInternetConnected()|| !isLoggedIn()) return true;
        return false;
    }


    public boolean isInternetConnected(){

        if(!ConnectivityCheck.isNetworkConnected(getApplicationContext())){
            startActivity(new Intent(InventoryTransaction.this,NoConnection.class));
            return false;
        }
        return true;
    }


    public boolean isLoggedIn(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Intent intent = new Intent(InventoryTransaction.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return false;
        }
        return true;
    }

    public void init() {

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Inventory Transaction");
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColorLight));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        icon = (ImageView) findViewById(R.id.icon);
        inventory_name = (TextView) findViewById(R.id.inventory_name);
        issue = (Button)findViewById(R.id.issue_btn);
        return_btn = (Button) findViewById(R.id.return_btn);
        update = (Button) findViewById(R.id.update_btn);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home) finish();

        return true;
    }
}