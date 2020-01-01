package com.sportsCommittee.sportsinventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        if(!validation())return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

}
