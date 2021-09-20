package com.example.maps_places_2_0.controller;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.maps_places_2_0.LogInActivity;
import com.example.maps_places_2_0.MainActivity;
import com.example.maps_places_2_0.SignUpActivity;
import com.example.maps_places_2_0.controller.SaveSharedPreferences;
import com.example.maps_places_2_0.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthHandler extends AppCompatActivity {

    private FirebaseAuth auth;
    private Context context;

    public AuthHandler(FirebaseAuth auth, Context context) {
        this.auth = auth;
        this.context = context;
    }

    public String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger nr = new BigInteger(1, messageDigest);

            String hash = nr.toString(16);
            while (hash.length() < 32) {
                hash = "0" + hash;
            }
            return hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException((e));
        }
    }


    public void registerUser(EditText email_text, EditText password_text, EditText password_text2) {
        String email = String.valueOf(email_text.getText());
        String password = String.valueOf(password_text.getText());
        String password2 = String.valueOf(password_text2.getText());

        if (!password.equals(password2)) {
            password_text2.setError("Passwords not matching");
            password_text2.requestFocus();
            Log.d("Error", "Passwords not matching");
            return;
        }
        if (password.isEmpty()) {
            password_text.setError("Password is requird");
            password_text.requestFocus();
            Log.d("Error", "Password is requird");
            return;
        }
        if (password.length() < 6) {
            password_text.setError("Min 6 characters");
            password_text.requestFocus();
            Log.d("Error", "Min 6 characters");
            return;
        }
        if (email.isEmpty()) {
            email_text.setError("Email is requird");
            email_text.requestFocus();
            Log.d("Error", "Email is requird");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_text.setError("Please provide a valid email");
            email_text.requestFocus();
            Log.d("Error", "Incorrect email");
            return;
        }

        auth.createUserWithEmailAndPassword(email, getMD5(password)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User(email, getMD5(password));
                    FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context.getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
                                openActivity(LogInActivity.class);
                            } else {
                                Toast.makeText(context.getApplicationContext(), "Unsuccess!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Unsuccess", "" + e.toString());
                        }
                    });
                } else {
                    Toast.makeText(context.getApplicationContext(), "Unsuccess!", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Unsuccess", "" + e.toString());
            }
        });
    }

    public void userLogin(EditText email_text, EditText password_text) {
        String email = String.valueOf(email_text.getText());
        String password = String.valueOf(password_text.getText());

        if (password.isEmpty()) {
            password_text.setError("Password is requird");
            password_text.requestFocus();
            Log.d("Error", "Password is requird");
            return;
        }
        if (password.length() < 6) {
            password_text.setError("Min 6 characters");
            password_text.requestFocus();
            Log.d("Error", "Min 6 characters");
            return;
        }
        if (email.isEmpty()) {
            email_text.setError("Email is requird");
            email_text.requestFocus();
            Log.d("Error", "Email is requird");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_text.setError("Please provide a valid email");
            email_text.requestFocus();
            Log.d("Error", "Incorect email");
            return;
        }

        auth.signInWithEmailAndPassword(email, getMD5(password)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {
                        Toast.makeText(context.getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
                        SaveSharedPreferences.setEmail(context.getApplicationContext(), user);
                        openActivity(MainActivity.class);
                    } else {
                        Toast.makeText(context.getApplicationContext(), "Email needs confirmation! Check inbox!", Toast.LENGTH_LONG).show();
                        user.sendEmailVerification();
                    }

                } else {
                    Toast.makeText(context.getApplicationContext(), "Unsuccess!", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Error", "" + e.toString());
            }
        });
    }

    public void resetPassword(EditText email_text, EditText password_text, EditText oldPassword) {
        String email = String.valueOf(email_text.getText());
        String password = String.valueOf(password_text.getText());
        if (email.isEmpty()) {
            email_text.setError("Email is requird");
            email_text.requestFocus();
            Log.d("Error", "Email is requird");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_text.setError("Please provide a valid email");
            email_text.requestFocus();
            Log.d("Error", "Incorrect email");
            return;
        }

        if (password.isEmpty()) {
            password_text.setError("Password is requird");
            password_text.requestFocus();
            Log.d("Error", "Password is requird");
            return;
        }
        if (password.length() < 6) {
            password_text.setError("Min 6 characters");
            password_text.requestFocus();
            Log.d("Error", "Min 6 characters");
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(email, getMD5(String.valueOf(oldPassword.getText())));

        auth.signInWithEmailAndPassword(email, getMD5(String.valueOf(oldPassword.getText()))).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(email, getMD5(String.valueOf(oldPassword.getText())));
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(getMD5(password)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("password").setValue(getMD5(password)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(context.getApplicationContext(), "Reset success!", Toast.LENGTH_LONG).show();
                                                        FirebaseAuth.getInstance().signOut();
                                                        openActivity(LogInActivity.class);
                                                    } else
                                                        Toast.makeText(context.getApplicationContext(), "Reset unsuccess!", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        } else
                                            Toast.makeText(context.getApplicationContext(), "Reset unsuccess!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else
                                Toast.makeText(context.getApplicationContext(), "Wrong credentials!", Toast.LENGTH_LONG).show();
                        }
                    });
                } else
                    Toast.makeText(context.getApplicationContext(), "Wrong credentials!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openActivity(Class activity) {
        Intent intent = new Intent(context.getApplicationContext(), activity);
        finish();
        context.startActivity(intent);
    }
}
