package com.sportsCommittee.sportsinventory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class IssueTransaction extends AppCompatActivity {

    Toolbar toolbar;
    TextView inventory_name;
    ImageView icon;
    EditText studentId;
    Button issue;

    FirebaseDatabase database;
    DatabaseReference ref;

    String formattedDate;

    @Override
    protected void onStart() {
        super.onStart();
        if(!validation())return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_transaction);

        init();

        database = FirebaseDatabase.getInstance();

        Intent intent = (Intent) getIntent();
        final String inventoryName = intent.getStringExtra("inventory_name");
        inventory_name.setText(inventoryName);

        String iconURL = intent.getStringExtra("iconURL");
        StorageReference img_ref = FirebaseStorage.getInstance().getReferenceFromUrl(iconURL);

        Glide.with(IssueTransaction.this)
                .using(new FirebaseImageLoader())
                .load(img_ref)
                .into(icon);

        issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isInternetConnected())return;

                final String stdId = studentId.getText().toString();

                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("DD-MM-YYYY");
                formattedDate = df.format(date);

                ref = database.getReferenceFromUrl("https://sports-inventory-app.firebaseio.com/inventory/"+inventoryName+"/issuedBy/");

                ref.orderByKey().equalTo(stdId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Toast.makeText(IssueTransaction.this,"Student has already issued!!",Toast.LENGTH_LONG).show();
                        }
                        else{

                            Log.d("Issue!!",stdId+" "+formattedDate);
                            ref = ref.child(stdId);
                            DatabaseReference temp = ref;
                            ref = ref.child("studentId");
                            ref.setValue(stdId);

                            ref = temp;
                            ref = ref.child("dateOfIssue");
                            ref.setValue(formattedDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    final DatabaseReference avail_ref = FirebaseDatabase.getInstance().getReference("inventory/"+inventoryName).child("available");

                                    avail_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Long available = (Long)dataSnapshot.getValue();
                                            avail_ref.setValue(available-1);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    Toast.makeText(IssueTransaction.this,"Issued Successfully!!",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(IssueTransaction.this,MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(IssueTransaction.this,"Issuing failed. Try again!!",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
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
            startActivity(new Intent(IssueTransaction.this,NoConnection.class));
            return false;
        }
        return true;
    }


    public boolean isLoggedIn(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Intent intent = new Intent(IssueTransaction.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return false;
        }
        return true;
    }

    public void init() {

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Issue");
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColorLight));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        icon = (ImageView) findViewById(R.id.icon);
        inventory_name = (TextView) findViewById(R.id.inventory_name);
        studentId = (EditText) findViewById(R.id.student_id);
        issue = (Button) findViewById(R.id.issue_btn);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home) finish();

        return true;
    }
}
