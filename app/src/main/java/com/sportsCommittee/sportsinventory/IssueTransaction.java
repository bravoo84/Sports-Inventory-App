package com.sportsCommittee.sportsinventory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
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

    String formattedDate,dueDate;

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


                DueDate obj = new DueDate();

                String day = (String) DateFormat.format("dd",date);
                String month = (String) DateFormat.format("MM",date);
                String year = (String) DateFormat.format("yyyy",date);

                dueDate = obj.addDays(Integer.parseInt(day),Integer.parseInt(month),Integer.parseInt(year),3);



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
                            ref = ref.child("dueDate");
                            ref.setValue(dueDate);



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

class DueDate {

    // Find values of day and month from
// offset of result year.
    int m2, d2;

    DueDate(){}

    // Return if year is leap year or not.
    boolean isLeap(int y) {
        if (y % 100 != 0 && y % 4 == 0 || y % 400 == 0)
            return true;

        return false;
    }

    // Given a date, returns number of days elapsed
// from the beginning of the current year (1st
// jan).
    int offsetDays(int d, int m, int y) {
        int offset = d;

        if (m - 1 == 11)
            offset += 335;
        if (m - 1 == 10)
            offset += 304;
        if (m - 1 == 9)
            offset += 273;
        if (m - 1 == 8)
            offset += 243;
        if (m - 1 == 7)
            offset += 212;
        if (m - 1 == 6)
            offset += 181;
        if (m - 1 == 5)
            offset += 151;
        if (m - 1 == 4)
            offset += 120;
        if (m - 1 == 3)
            offset += 90;
        if (m - 1 == 2)
            offset += 59;
        if (m - 1 == 1)
            offset += 31;

        if (isLeap(y) && m > 2)
            offset += 1;

        return offset;
    }

    // Given a year and days elapsed in it, finds
// date by storing results in d and m.
    void revoffsetDays(int offset, int y) {
        int[] month = {0, 31, 28, 31, 30, 31, 30,
                31, 31, 30, 31, 30, 31};

        if (isLeap(y))
            month[2] = 29;

        int i;
        for (i = 1; i <= 12; i++) {
            if (offset <= month[i])
                break;
            offset = offset - month[i];
        }

        d2 = offset;
        m2 = i;
    }

    // Add x days to the given date.
    String addDays(int d1, int m1, int y1, int x) {
        int offset1 = offsetDays(d1, m1, y1);
        int remDays = isLeap(y1) ? (366 - offset1) : (365 - offset1);

        // y2 is going to store result year and
        // offset2 is going to store offset days
        // in result year.
        int y2, offset2 = 0;
        if (x <= remDays) {
            y2 = y1;
            offset2 = offset1 + x;
        } else {
            // x may store thousands of days.
            // We find correct year and offset
            // in the year.
            x -= remDays;
            y2 = y1 + 1;
            int y2days = isLeap(y2) ? 366 : 365;
            while (x >= y2days) {
                x -= y2days;
                y2++;
                y2days = isLeap(y2) ? 366 : 365;
            }
            offset2 = x;
        }
        revoffsetDays(offset2, y2);
        return d2+"-"+m2+"-"+y2;

    }

}

