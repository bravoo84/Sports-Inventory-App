package com.sportsCommittee.sportsinventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sportsCommittee.sportsinventory.FirebaseImageLoaderPack.FirebaseImageLoader;

public class UpdateTransaction extends AppCompatActivity {


    Toolbar toolbar;
    ImageView icon;
    TextView inventory_name;
    EditText increment;
    Button update;

    DatabaseReference ref1,ref2;
    String inventoryName;
    String iconURL;

    Long available, totalStock,incr;


    @Override
    protected void onStart() {
        super.onStart();
        if(!validation())return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_transaction);

        init();

        ref1 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://sports-inventory-app.firebaseio.com/inventory/"+inventoryName+"/available");
        ref2 = FirebaseDatabase.getInstance().getReference("inventory/"+inventoryName).child("totalStock");


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                incr = Long.parseLong(increment.getText().toString());
                if(!isInternetConnected())return;


                ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        available = (Long) dataSnapshot.getValue();

                        Log.d("DATAA"," "+inventoryName);

                        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                totalStock = (Long) dataSnapshot.getValue();

                                ref1.setValue(available + incr);
                                ref2.setValue(totalStock + incr).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(UpdateTransaction.this,"Updated Successfully!!",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(UpdateTransaction.this,MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UpdateTransaction.this,"Please try again.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }


    public boolean validation(){
        if(!isInternetConnected()|| !isLoggedIn()) return true;
        return false;
    }


    public boolean isInternetConnected(){

        if(!ConnectivityCheck.isNetworkConnected(getApplicationContext())){
            startActivity(new Intent(UpdateTransaction.this,NoConnection.class));
            return false;
        }
        return true;
    }


    public boolean isLoggedIn(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Intent intent = new Intent(UpdateTransaction.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return false;
        }
        return true;
    }

    public void init() {

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Update Inventory");
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColorLight));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        icon = (ImageView) findViewById(R.id.icon);
        inventory_name = (TextView) findViewById(R.id.inventory_name);
        increment = (EditText) findViewById(R.id.increase);
        update = (Button) findViewById(R.id.update_btn);


        Intent intent = getIntent();
        inventoryName = intent.getStringExtra("inventory_name");
        iconURL = intent.getStringExtra("iconURL");

        inventory_name.setText(inventoryName);

        StorageReference img_ref = FirebaseStorage.getInstance().getReferenceFromUrl(iconURL);
        Glide.with(UpdateTransaction.this)
                .using(new FirebaseImageLoader())
                .load(img_ref)
                .into(icon);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home) finish();

        return true;
    }

}
