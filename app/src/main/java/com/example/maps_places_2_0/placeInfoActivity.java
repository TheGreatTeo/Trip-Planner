package com.example.maps_places_2_0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maps_places_2_0.controller.SaveSharedPreferences;

public class placeInfoActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView placeName;
    private TextView placeAddress;
    private TextView placeOpeningHours;
    private Button placePhoneNumber;
    private TextView placePriceLevel;
    private Button placeWebsiteUri;
    private TextView placeRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);

        backButton = findViewById(R.id.backButton2);
        placeName = findViewById(R.id.placeName);
        placeAddress = findViewById(R.id.placeAdress);
        placeOpeningHours = findViewById(R.id.placeHours);
        placePhoneNumber = findViewById(R.id.placePhone);
        placePriceLevel = findViewById(R.id.placePrice);
        placeWebsiteUri = findViewById(R.id.placeWebsite);
        placeRating = findViewById(R.id.placeRating);

        placeName.setText(SaveSharedPreferences.getName(this));
        placeAddress.setText(SaveSharedPreferences.getAddress(this));
        placeOpeningHours.setText(SaveSharedPreferences.getOpeningHours(this));
        placePhoneNumber.setText(SaveSharedPreferences.getPhoneNumber(this));
        placePriceLevel.setText(SaveSharedPreferences.getPriceLevel(this));
        placeWebsiteUri.setText(SaveSharedPreferences.getWebsiteUri(this));
        placeRating.setText(SaveSharedPreferences.getRating(this));

        if(SaveSharedPreferences.getWebsiteUri(this) != "-"){
            placeWebsiteUri.setTextColor(ContextCompat.getColor(this,R.color.purple_700));
        }
        if(SaveSharedPreferences.getPhoneNumber(this) != "-"){
            placePhoneNumber.setTextColor(ContextCompat.getColor(this,R.color.purple_700));
        }

        placeWebsiteUri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(placeWebsiteUri.getText() != "-") {
                    Intent browerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(placeWebsiteUri.getText())));
                    startActivity(browerIntent);
                }
            }
        });

        placePhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(placePhoneNumber.getText() != "-") {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + placePhoneNumber.getText()));
                    startActivity(phoneIntent);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeInfoActivity.super.onBackPressed();
            }
        });
    }

}