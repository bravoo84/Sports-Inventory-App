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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.concurrent.TimeUnit;

public class ReturnDetails extends AppCompatActivity {


    Toolbar toolbar;
    ImageView icon;
    TextView inventory_name;

    String inventoryName,iconURL,studentId,dateOfIssue;

    TextView student_id,date_of_issue, fine_tv,due_days;
    RadioGroup radioGroup;
    RadioButton radioButton;

    Button submit;


    DatabaseReference ref1,ref2;

    int daysBetween;
    int dueDays;
    int fine;
    String flag="";

    @Override
    protected void onStart() {
        super.onStart();
        if(!validation())return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_details);
        init();

        daysBetween = (int)calculateDays(dateOfIssue);

        dueDays = (daysBetween<=3)? 0 : dueDays - 3;

        due_days.append(" "+dueDays);



    }

   public void checkButton(View v){
        int radioId = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(radioId);

       Toast.makeText(getApplicationContext(),radioButton.getText().toString(),Toast.LENGTH_LONG).show();

       if(radioId == R.id.no_damage){

           flag = "No damage";
           if(dueDays>0){
               fine = 0;
               dueDays--;
               fine+=10;

               int prev = 5;
               while(dueDays-->0){
                   fine+=prev;
                   prev+=5;
               }
           }

           fine_tv.setVisibility(View.VISIBLE);
           fine_tv.setText("Fine : Rs. "+fine);

       }else if(radioId == R.id.lost){

           flag = "Lost";
           fine = 0;
           fine+=50;
           int prev = 5;
           while(dueDays-->0){
               fine+=prev;
               prev+=5;
           }

           fine_tv.setVisibility(View.VISIBLE);
           fine_tv.setText("Fine : Rs. "+fine);

       }else if(radioId == R.id.broken){

           flag = "Broken";
           fine = 0;
           int prev = 5;
           while(dueDays-->0){
               fine+=prev;
               prev+=5;
           }

           fine_tv.setVisibility(View.VISIBLE);
           fine_tv.setText("Fine : Rs. "+fine);

       }


       submit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(!isInternetConnected())return;

               DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://sports-inventory-app.firebaseio.com/inventory/"+inventoryName+"/issuedBy");
               ref.child(studentId).removeValue(new DatabaseReference.CompletionListener() {
                   @Override
                   public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                       if(databaseError!=null){
                           Toast.makeText(getApplicationContext(),"Failed. Try again!",Toast.LENGTH_SHORT).show();
                           return;
                       }


                       DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://sports-inventory-app.firebaseio.com/inventory/"+inventoryName+"/pastIssues");


                       reference = reference.child(studentId);
                       DatabaseReference temp = reference;

                       reference = reference.child("studentId");
                       reference.setValue(studentId);

                       reference = temp;
                       reference = reference.child("dateOfIssue");
                       reference.setValue(dateOfIssue);

                       reference = temp;
                       reference = reference.child("dateOfReturn");
                       SimpleDateFormat df = new SimpleDateFormat("DD-MM-YYYY");
                       reference.setValue(df.format(Calendar.getInstance().getTime()));

                       reference = temp;
                       reference = reference.child("status");
                       reference.setValue(flag);

                       reference = temp;
                       reference = reference.child("fine");
                       reference.setValue(fine);


                       ref1 = FirebaseDatabase.getInstance().getReference("inventory/"+inventoryName).child("available");
                       ref2 = FirebaseDatabase.getInstance().getReference("inventory/"+inventoryName).child("totalStock");






                       if(flag.equals("Lost") || flag.equals("Broken")){


                           ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   Long val = (Long)dataSnapshot.getValue();
                                   ref2.setValue(val-1);
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });



                       }else{
                           ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   Long val = (Long)dataSnapshot.getValue();
                                   ref1.setValue(val+1);
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });
                       }

                       Toast.makeText(getApplicationContext(),"Successful!",Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(ReturnDetails.this,MainActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                       startActivity(intent);
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
            startActivity(new Intent(ReturnDetails.this,NoConnection.class));
            return false;
        }
        return true;
    }


    public boolean isLoggedIn(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Intent intent = new Intent(ReturnDetails.this,LoginActivity.class);
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
        student_id = (TextView) findViewById(R.id.student_id);
        date_of_issue = (TextView) findViewById(R.id.doi);
        due_days = (TextView) findViewById(R.id.due_days);
        fine_tv = (TextView)findViewById(R.id.fine_tv);
        radioGroup = (RadioGroup) findViewById(R.id.radio_grp);
        submit = (Button) findViewById(R.id.submit);


        Intent intent = getIntent();
        inventoryName = intent.getStringExtra("inventory_name");
        iconURL = intent.getStringExtra("iconURL");
        studentId = intent.getStringExtra("student_id");
        dateOfIssue = intent.getStringExtra("doi");



        inventory_name.setText(inventoryName);
        student_id.append(studentId);


        date_of_issue.append(dateOfIssue);

        StorageReference img_ref = FirebaseStorage.getInstance().getReferenceFromUrl(iconURL);
        Glide.with(ReturnDetails.this)
                .using(new FirebaseImageLoader())
                .load(img_ref)
                .into(icon);




    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home) finish();

        return true;
    }

    public float calculateDays(String dateOfIssue){
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateBeforeString = dateOfIssue;
        String dateAfterString = myFormat.format(Calendar.getInstance().getTime());

        float daysBetween = 0;

        try {
            Date dateBefore = myFormat.parse(dateBeforeString);
            Date dateAfter = myFormat.parse(dateAfterString);
            long difference = dateAfter.getTime() - dateBefore.getTime();
            daysBetween = (difference / (1000*60*60*24));

            System.out.println("Number of Days between dates: "+daysBetween);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return daysBetween;
    }

}



