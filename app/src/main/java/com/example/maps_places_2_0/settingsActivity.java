package com.example.maps_places_2_0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.maps_places_2_0.controller.SaveSharedPreferences;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class settingsActivity extends AppCompatActivity {

    Spinner transportation_mode;
    Spinner units;
    Spinner map_type;
    Spinner language;
    ImageView backButton;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        transportation_mode = findViewById(R.id.transportation_mode);
        units = findViewById(R.id.units);
        map_type = findViewById(R.id.map_type);
        language = findViewById(R.id.language);

        backButton = findViewById(R.id.backButton2);

        ArrayAdapter<CharSequence> transportation_mode_ = ArrayAdapter.createFromResource(this,R.array.transportation_mode,android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> units_ = ArrayAdapter.createFromResource(this,R.array.units,android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> map_type_ = ArrayAdapter.createFromResource(this,R.array.map_type,android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> language_ = ArrayAdapter.createFromResource(this,R.array.language,android.R.layout.simple_spinner_item);

        transportation_mode_.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        units_.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        map_type_.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language_.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        transportation_mode.setAdapter(transportation_mode_);
        units.setAdapter(units_);
        map_type.setAdapter(map_type_);
        language.setAdapter(language_);

        transportation_mode.setSelection(transportation_mode_.getPosition(SaveSharedPreferences.getPrefTransportationMode(this)));
        units.setSelection(units_.getPosition(SaveSharedPreferences.getPrefUnits(this)));
        map_type.setSelection(map_type_.getPosition(map_type_.getItem(SaveSharedPreferences.getPrefMapType(this))));
        language.setSelection(language_.getPosition(SaveSharedPreferences.getPrefLanguage(this)));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsActivity.super.onBackPressed();
            }
        });



        transportation_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SaveSharedPreferences.setPrefSettings(settingsActivity.this,"transportation_mode",transportation_mode_.getItem(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SaveSharedPreferences.setPrefSettings(settingsActivity.this,"units",units_.getItem(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        map_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SaveSharedPreferences.setPrefSettings(settingsActivity.this,"map_type",String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SaveSharedPreferences.setPrefSettings(settingsActivity.this,"language",language_.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

}