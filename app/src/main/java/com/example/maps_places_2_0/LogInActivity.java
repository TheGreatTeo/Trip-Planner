package com.example.maps_places_2_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.maps_places_2_0.controller.SaveSharedPreferences;
import com.example.maps_places_2_0.controller.AuthHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    private EditText email_text;
    private EditText password_text;
    private TextView resetPass;
    private Button logIn;
    private TextView signUp;
    private FirebaseAuth auth;
    private FirebaseUser user_logged;
    private AuthHandler authHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user_logged = FirebaseAuth.getInstance().getCurrentUser();
        if(user_logged != null && !SaveSharedPreferences.getEmail(this).isEmpty()){
            openActivity(MainActivity.class);
        }
        setContentView(R.layout.activity_log_in);

        auth = FirebaseAuth.getInstance();
        authHandler = new AuthHandler(auth,this);
        email_text = findViewById(R.id.emailChange);
        password_text = findViewById(R.id.oldPassword);
        logIn = findViewById(R.id.logIn);
        signUp = findViewById(R.id.signUp);
        resetPass = findViewById(R.id.forgot);

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openActivity(changePassword.class);
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authHandler.userLogin(email_text,password_text);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(SignUpActivity.class);
            }
        });
    }

    private void openActivity(Class activity){
        Intent intent = new Intent(this, activity);
        finish();
        startActivity(intent);
    }
}

