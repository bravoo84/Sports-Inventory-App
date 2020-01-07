package com.sportsCommittee.sportsinventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sportsCommittee.sportsinventory.Entity.StudentData;
import com.sportsCommittee.sportsinventory.FirebaseImageLoaderPack.FirebaseImageLoader;

public class ReturnTransaction extends AppCompatActivity {

    Toolbar toolbar;
    ImageView icon;
    TextView inventory_name;
    ListView listView;

    String inventoryName,iconURL;

    FirebaseDatabase database;
    FirebaseListAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();
        if(!validation())return;
        adapter.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_transaction);

        init();

        Query query = FirebaseDatabase.getInstance().getReference("inventory/"+inventoryName).child("issuedBy");

        Log.d("ListView",inventoryName);

        FirebaseListOptions<StudentData> options = new FirebaseListOptions.Builder<StudentData>()
                                                        .setLifecycleOwner(ReturnTransaction.this)
                                                        .setLayout(R.layout.student_list)
                                                        .setQuery(query, StudentData.class)
                                                        .build();

        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(View v, Object model, int position) {
                TextView student_id = (TextView)v.findViewById(R.id.student_id);
                TextView doi = (TextView) v.findViewById(R.id.doi);


                StudentData student = (StudentData)model;
                student_id.setText(student.getStudentId());
                doi.setText(student.getDateOfIssue());
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView sid = view.findViewById(R.id.student_id);
                String temp = sid.getText().toString();

                TextView doi = view.findViewById(R.id.doi);


                Intent intent = new Intent(ReturnTransaction.this, ReturnDetails.class);
                intent.putExtra("inventory_name",inventoryName);
                intent.putExtra("iconURL",iconURL);
                intent.putExtra("student_id",temp);
                intent.putExtra("doi",doi.getText().toString());
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
            startActivity(new Intent(ReturnTransaction.this,NoConnection.class));
            return false;
        }
        return true;
    }


    public boolean isLoggedIn(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Intent intent = new Intent(ReturnTransaction.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return false;
        }
        return true;
    }

    public void init() {

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Return");
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColorLight));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        icon = (ImageView) findViewById(R.id.icon);
        inventory_name = (TextView) findViewById(R.id.inventory_name);


        Intent intent = getIntent();
        inventoryName = intent.getStringExtra("inventory_name");
        iconURL = intent.getStringExtra("iconURL");

        inventory_name.setText(inventoryName);

        StorageReference img_ref = FirebaseStorage.getInstance().getReferenceFromUrl(iconURL);
        Glide.with(ReturnTransaction.this)
                .using(new FirebaseImageLoader())
                .load(img_ref)
                .into(icon);

        listView = findViewById(R.id.student_list);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home) finish();

        return true;
    }

}
