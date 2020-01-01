package com.sportsCommittee.sportsinventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {


    Toolbar toolbar;
    EditText et_username, et_password;
    String username, password;
    Button login;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    ConnectivityCheck connectivityCheck;

    @Override
    protected void onStart() {
        super.onStart();
        if (!isInternetConnected()) return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        init();
        firebaseAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isInternetConnected()) return;

                progressBar.setVisibility(View.VISIBLE);

                username = et_username.getText().toString();
                password = et_password.getText().toString();

                firebaseAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {

                                    user = firebaseAuth.getCurrentUser();

                                    Toast.makeText(LoginActivity.this, "Logged in!", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(LoginActivity.this, "Incorrect User Id/Password!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });


    }

    public void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        et_username = (EditText) findViewById(R.id.username);
        et_password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        toolbar.setTitle("Login");
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColorLight));
        setSupportActionBar(toolbar);

    }


    public boolean isInternetConnected() {

        if (!ConnectivityCheck.isNetworkConnected(getApplicationContext())) {
            startActivity(new Intent(LoginActivity.this, NoConnection.class));
            return false;
        }
        return true;
    }


}










//          login.setOnClickListener(new View.OnClickListener() {
//              @Override
//              public void onClick(View v) {
//                  firebaseAuth.sendPasswordResetEmail(et_username.getText().toString())
//                          .addOnCompleteListener(new OnCompleteListener<Void>() {
//                              @Override
//                              public void onComplete(@NonNull Task<Void> task) {
//                                  if(task.isSuccessful()){
//                                      Toast.makeText(LoginActivity.this,"Link sent!",Toast.LENGTH_LONG).show();
//                                  }else{
//                                      Toast.makeText(LoginActivity.this,"Failed !",Toast.LENGTH_LONG).show();
//                                  }
//                              }
//                          });
//              }
//          });

