package com.example.maps_places_2_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.maps_places_2_0.controller.AuthHandler;
import com.google.firebase.auth.FirebaseAuth;

public class changePassword extends AppCompatActivity {

    private EditText newPassowrd;
    private EditText oldPassowrd;
    private EditText emailChange;
    private Button resetButton;
    private FirebaseAuth auth;
    private AuthHandler authHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        auth = FirebaseAuth.getInstance();
        authHandler = new AuthHandler(auth,this);

        newPassowrd = findViewById(R.id.newPassword);
        oldPassowrd = findViewById(R.id.oldPassword);
        emailChange = findViewById(R.id.emailChange);
        resetButton = findViewById(R.id.resetButton);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authHandler.resetPassword(emailChange,newPassowrd,oldPassowrd);
            }
        });
    }

    private void openLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        finish();
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        openLogInActivity();
    }
}