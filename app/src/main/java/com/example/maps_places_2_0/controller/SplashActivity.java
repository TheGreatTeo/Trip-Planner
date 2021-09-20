package com.example.maps_places_2_0.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.maps_places_2_0.LogInActivity;
import com.example.maps_places_2_0.MainActivity;
import com.example.maps_places_2_0.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseUser user_logged;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user_logged = FirebaseAuth.getInstance().getCurrentUser();
        if(user_logged != null)
        {
            openActivity(MainActivity.class);
        }
        else
        {
            setContentView(R.layout.activity_splash);
            Thread timer = new Thread(){
                public void run() {
                    try
                    {
                        sleep(1000);
                    }catch(InterruptedException e) { }
                    finally
                    {
                        openActivity(LogInActivity.class);
                    }
                }
            };
            timer.start();
        }
    }
    private void openActivity(Class activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        finish();
    }
}