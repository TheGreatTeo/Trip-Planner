package com.example.maps_places_2_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.maps_places_2_0.controller.AuthHandler;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {


    private EditText email_text;
    private EditText password_text;
    private EditText password_text2;
    private Button signUp2;
    private FirebaseAuth auth;
    private AuthHandler authHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        authHandler = new AuthHandler(auth,this);

        email_text = findViewById(R.id.emailChange);
        password_text = findViewById(R.id.oldPassword);
        password_text2 = findViewById(R.id.newPassword);

        signUp2 = findViewById(R.id.resetButton);

        signUp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authHandler.registerUser(email_text,password_text,password_text2);
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