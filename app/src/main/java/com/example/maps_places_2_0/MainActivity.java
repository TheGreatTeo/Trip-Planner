package com.example.maps_places_2_0;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.maps_places_2_0.controller.AppController;
import com.example.maps_places_2_0.controller.SaveSharedPreferences;
import com.example.maps_places_2_0.data.AsyncResponse;
import com.example.maps_places_2_0.data.Durata;
import com.example.maps_places_2_0.data.Traseu;
import com.example.maps_places_2_0.model.Graf.Muchie;
import com.example.maps_places_2_0.model.Graf.Drum;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback, NavigationView.OnNavigationItemSelectedListener {

    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_exit:
                FirebaseAuth.getInstance().signOut();
                SaveSharedPreferences.clearEmail(getApplicationContext());
                openActivity(LogInActivity.class);
                break;
            case R.id.nav_add:
                places.clear();
                locationID.clear();
                mMap.clear();
                locationList.clear();
                arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationList);
                listView.setAdapter(arrayAdapter);
                nr_places_text.setText(0 + " place");
                durationText.setText("");
                onBackPressed();
                break;
            case R.id.nav_settings:
                openActivity(settingsActivity.class);
                break;

        }
        return true;
    }

    String API_KEY;
    private Polyline polyline = null;
    private ListView listView;
    private ArrayList<String> locationList;
    private List<String> locationID = new ArrayList<String>();
    private ArrayAdapter<String> arrayAdapter;
    public Drum drum;
    List<LatLng> path = new ArrayList<LatLng>();
    //public SharedPreferences sharedPreferences;
    public String[] traseu_optimizat;
    private LatLng currentLocationLatLng;
    private RequestQueue requestQueue;
    private Button getLocations;
    private Button optimize;
    private GoogleMap mMap;
    private View mapView;
    private SupportMapFragment mapFragment;
    private AutocompleteSupportFragment autocompleteSupportFragment;
    private List<Place> places = new ArrayList<Place>();
    private List<Place> places_optimized = new ArrayList<Place>();
    private boolean mLocationPermissionGranted = false;
    private LatLng new_place;
    private FirebaseUser user_logged;
    private Button route;

    private TextView durationText;
    private TextView dateText;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    private Button location;
    private LocationManager locationManager;
    private TextView nr_places_text;
    private DrawerLayout drawer;
    private Button menu_button;
    private NavigationView navigationView;
    private String transportation_mode;
    private String units;
    private long route_duration = 0;
    private long route_distance = 0;
    private PolylineOptions polylineOptions = null;
    private TextView emailNav;
    private View headerView;
    private List<Marker> markerList = new ArrayList<Marker>();
    //place info

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user_logged = FirebaseAuth.getInstance().getCurrentUser();
        SaveSharedPreferences.setEmail(this, user_logged);
        if (user_logged == null && SaveSharedPreferences.getEmail(this).isEmpty()) {
            openActivity(LogInActivity.class);
        }
        setContentView(R.layout.nav_activity_main);
        AppController.getInstance().onCreate();

        dateText = findViewById(R.id.dateText);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEE MMM dd");
        date = dateFormat.format(calendar.getTime());
        dateText.setText(date);


        transportation_mode = SaveSharedPreferences.getPrefTransportationMode(this);
        units = SaveSharedPreferences.getPrefUnits(this);
        listView = findViewById(R.id.listView);
        locationList = new ArrayList<>();
        nr_places_text = findViewById(R.id.nr_places);
        nr_places_text.setText(places.size() + " places");
        durationText = findViewById(R.id.duration);
        durationText.setText("");

        //volley
        requestQueue = Volley.newRequestQueue(this);

        //buttons
        getLocations = findViewById(R.id.getLocations);
        optimize = findViewById(R.id.optimize);
        location = findViewById(R.id.location);

        //map
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);


        //google_search
        autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setPlaceFields(
                Arrays.asList(Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.LAT_LNG,
                        Place.Field.ADDRESS,
                        Place.Field.OPENING_HOURS,
                        Place.Field.PHONE_NUMBER,
                        Place.Field.PRICE_LEVEL,
                        Place.Field.WEBSITE_URI,
                        Place.Field.RATING,
                        Place.Field.PHOTO_METADATAS));

        //places
        API_KEY = getString(R.string.google_places_key);
        Places.initialize(getApplicationContext(), API_KEY);

        //drawer menu
        drawer = findViewById(R.id.drawer_layout);
        menu_button = findViewById(R.id.menu_button);
        navigationView = findViewById(R.id.nav_view);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        emailNav = (TextView) headerView.findViewById(R.id.emailNav);
        emailNav.setText(SaveSharedPreferences.getEmail(this));

        //when a place is selected from the autocomplete list
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                if (!locationID.contains(place.getId())) {
                    Log.d("Place", "Place: " + place.getName() + ", " + place.getId());
                    new_place = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    places.add(place);
                    if (places.size() == 1) {
                        nr_places_text.setText(places.size() + " place");
                    } else {
                        nr_places_text.setText(places.size() + " places");
                    }
                    locationList.add(place.getName());
                    locationID.add(place.getId());
                    Log.d("LatLng", "LatLng: " + new_place);
                    MarkerOptions markerOptions = new MarkerOptions().position(place.getLatLng());
                    Marker marker;
                    marker = mMap.addMarker(markerOptions);
                    markerList.add(marker);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new_place, 12));

                } else {
                    Toast.makeText(MainActivity.this, "Aceasta locatie a fost selectata deja", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d("Error", "An error occurred: " + status);
            }
        });

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(places.get(position).getName());
                builder.setMessage(places.get(position).getAddress());

                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        places.remove(position);
                        locationID.remove(position);
                        locationList.remove(position);
                        arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, locationList);
                        listView.setAdapter(arrayAdapter);
                        if (places.size() > 1)
                            nr_places_text.setText(places.size() + " places");
                        else
                            nr_places_text.setText(places.size() + " place");
                        markerList.get(position).remove();
                        markerList.remove(position);
                        if(places.size() == 0)
                            mMap.clear();
                    }
                });


                builder.setNegativeButton("Navigate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        transportation_mode = SaveSharedPreferences.getPrefTransportationMode(MainActivity.this);
                        Uri intentUri = Uri.parse("google.navigation:q=" + places.get(position).getLatLng().latitude + "," + places.get(position).getLatLng().longitude + "&mode=" + transportation_mode.toLowerCase().charAt(0));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });
                builder.setNeutralButton("Details", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String opening_hours = "\n\n\n-\n\n\n";
                        if (places.get(position).getOpeningHours() != null) {
                            List<String> hours = places.get(position).getOpeningHours().getWeekdayText();
                            opening_hours = "";
                            for (int i = 0; i < hours.size(); i++) {
                                opening_hours += hours.get(i) + "\n";
                            }
                        }

                        String price_level = "-";
                        if (places.get(position).getPriceLevel() != null) {
                            int price = places.get(position).getPriceLevel();
                            price_level = "";
                            for (int i = 0; i < price; i++) {
                                price_level += "$";
                            }
                        }
                        String phone = "-";
                        if (places.get(position).getPhoneNumber() != null) {
                            phone = places.get(position).getPhoneNumber();
                        }
                        String rating = "-";
                        if (places.get(position).getRating() != null) {
                            rating = String.valueOf(places.get(position).getRating());
                        }
                        String website = "-";
                        if (places.get(position).getWebsiteUri() != null) {
                            website = String.valueOf(places.get(position).getWebsiteUri());
                        }

                        SaveSharedPreferences.setName(MainActivity.this, places.get(position).getName());
                        SaveSharedPreferences.setAddress(MainActivity.this, places.get(position).getAddress());
                        SaveSharedPreferences.setOpeningHours(MainActivity.this, opening_hours);
                        SaveSharedPreferences.setPhoneNumber(MainActivity.this, phone);
                        SaveSharedPreferences.setPriceLevel(MainActivity.this, price_level);
                        SaveSharedPreferences.setWebsiteUri(MainActivity.this, website);
                        SaveSharedPreferences.setRating(MainActivity.this, rating);
                        openActivity(placeInfoActivity.class);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        getLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (places.size() <= 0) {
                    Toast.makeText(getApplicationContext(), "Please create a route", Toast.LENGTH_LONG).show();
                } else {
                    if (places_optimized.equals(places))
                        Toast.makeText(MainActivity.this, "Route already created!", Toast.LENGTH_LONG).show();
                    else {
                        API_KEY = getString(R.string.google_places_key);
                        getCurrentLocation();
                        transportation_mode = SaveSharedPreferences.getPrefTransportationMode(MainActivity.this);
                        int nr_destinatii = places.size() + 1;
                        drum = new Drum(nr_destinatii);
                        String loc = "";
                        String separator = "";
                        String mode = "&mode=" + transportation_mode.toLowerCase();
                        String language = "&language=en-EN";
                        String key = "&key=" + API_KEY;
                        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?&origins=";
                        for (int i = 0; i < nr_destinatii; i++) {
                            LatLng locations;

                            if (i == nr_destinatii - 1) {
                                locations = currentLocationLatLng;
                                Log.d("Locatie", currentLocationLatLng.toString());
                                separator = "";
                            } else {
                                locations = places.get(i).getLatLng();
                                separator = "|";
                            }

                            loc = loc + locations.latitude + "," + locations.longitude + separator;
                        }
                        url = url + loc + "&destinations=" + loc + mode + language + key;
                        drum = new Durata().getDistance(url, nr_destinatii, new AsyncResponse() {
                            @Override
                            public void processFinishedDurata(Drum drum, String error) {
                                if (error != "") {
                                    Toast.makeText(MainActivity.this, "Change the transportation mode!", Toast.LENGTH_LONG).show();
                                }
                                Log.d("Drum", "Drum:" + drum);
                            }

                            @Override
                            public void processFinishedTraseu(List<LatLng> path, long duration, long distance, String error) {
                            }
                        });
                        nr_destinatii = places.size() + 1;
                        loc = "";
                        separator = "|";
                        mode = "&mode=" + transportation_mode.toLowerCase();
                        language = "&language=en-EN";
                        key = "&key=" + API_KEY;
                        url = "https://maps.googleapis.com/maps/api/directions/json?&origin=" +
                                currentLocationLatLng.latitude + "," + currentLocationLatLng.longitude +
                                "&destination=" + places.get(nr_destinatii - 2).getLatLng().latitude + "," + places.get(nr_destinatii - 2).getLatLng().longitude +
                                mode +
                                "&waypoints=";
                        for (int i = 0; i < nr_destinatii - 2; i++) {
                            LatLng locations;
                            locations = places.get(i).getLatLng();
                            if (i == nr_destinatii - 3) {
                                separator = "";
                            }
                            loc = loc + locations.latitude + "," + locations.longitude + separator;
                        }
                        url = url + loc + language + key;
                        path = new Traseu().getPath(url, new AsyncResponse() {
                            @Override
                            public void processFinishedTraseu(List<LatLng> path, long duration, long distance, String error) {
                                if (error != "") {
                                    Toast.makeText(MainActivity.this, "Change the transportation mode!", Toast.LENGTH_LONG).show();
                                } else {
                                    PolylineOptions polylineOptions = new PolylineOptions();
                                    Log.d("size", "" + path.size());
                                    for (int i = 0; i < path.size(); i++) {
                                        polylineOptions.add(path.get(i));
                                    }
                                    polylineOptions.width(10);
                                    polylineOptions.color(Color.BLUE);
                                    if (polyline != null) {
                                        polyline.remove();
                                    }
                                    polyline = mMap.addPolyline(polylineOptions);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 12f));
                                    Log.d("Duration_", "" + duration);
                                    route_duration = duration;
                                    route_distance = distance;
                                    durationText.setText(formatDurationAndDistance(route_duration, route_distance));
                                }
                            }

                            @Override
                            public void processFinishedDurata(Drum distance, String error) {
                            }
                        });

                    }
                }
            }
        });

        //optimizes the route !REMINDER PSEUDOCODE
        optimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (places.size() <= 0) {
                    Toast.makeText(getApplicationContext(), "Please create a route", Toast.LENGTH_LONG).show();
                } else {
                    if (places_optimized.equals(places))
                        Toast.makeText(MainActivity.this, "Route already optimized", Toast.LENGTH_LONG).show();
                    else {
                        List<Integer> visited = new LinkedList<>();
                        if (drum == null || drum.getNr_destinatii() - places.size() != 1)
                            Toast.makeText(MainActivity.this, "Create the new route first", Toast.LENGTH_LONG).show();
                        else {
                            transportation_mode = SaveSharedPreferences.getPrefTransportationMode(MainActivity.this);
                            places_optimized.clear();
                            drum.printGraf();
                            LinkedList<Muchie>[] lista = drum.getListaAdiacenta();
                            int nr_destinatii = places.size() + 1;
                            String[] traseu = new String[nr_destinatii];
                            int k = 0;
                            int current = nr_destinatii - 1;
                            visited.add(current);
                            while (visited.size() < nr_destinatii) {
                                long min = 999999999;
                                if (current == nr_destinatii - 1) traseu[k] = "Locatie curenta";
                                else traseu[k] = places.get(current).getName();
                                System.out.println(traseu[k]);
                                int save_dest = 0;
                                for (int i = 0; i < lista[current].size(); i++) {
                                    if (!visited.contains(lista[current].get(i).getDestinatie())) {
                                        if (lista[current].get(i).getDistanta() < min) {
                                            min = lista[current].get(i).getDistanta();
                                            save_dest = lista[current].get(i).getDestinatie();
                                        }
                                    }
                                }
                                visited.add(save_dest);
                                current = save_dest;
                                k++;
                            }
                            traseu[k] = places.get(current).getName();
                            System.out.println(traseu[k]);
                            traseu_optimizat = traseu;
                            String traseu_optimizat = "";
                            for (int i = 0; i < nr_destinatii; i++) {
                                if (i != nr_destinatii - 1) {
                                    traseu_optimizat = traseu_optimizat + traseu[i] + " -> ";
                                } else {
                                    traseu_optimizat = traseu_optimizat + traseu[i];
                                }
                            }
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Traseu optimizat")
                                    .setMessage(traseu_optimizat)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            List<Marker> markerListCopy = new ArrayList<Marker>();
                            locationList.clear();
                            for (int i = 1; i < visited.size(); i++) {
                                places_optimized.add(places.get(visited.get(i)));
                                locationList.add(places.get(visited.get(i)).getName());
                                markerListCopy.add(markerList.get(visited.get(i)));
                            }
                            arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, locationList);
                            listView.setAdapter(arrayAdapter);
                            places.clear();
                            markerList.clear();
                            for (int i = 0; i < places_optimized.size(); i++) {
                                places.add(places_optimized.get(i));
                                markerList.add(markerListCopy.get(i));
                            }
                            polyline.remove();
                            path = new ArrayList<LatLng>();
                            nr_destinatii = places.size() + 1;
                            String loc = "";
                            String separator = "|";
                            String mode = "&mode=" + transportation_mode.toLowerCase();
                            String language = "&language=en-EN";
                            String key = "&key=" + API_KEY;
                            String url = "https://maps.googleapis.com/maps/api/directions/json?&origin=" +
                                    currentLocationLatLng.latitude + "," + currentLocationLatLng.longitude +
                                    "&destination=" + places.get(nr_destinatii - 2).getLatLng().latitude + "," + places.get(nr_destinatii - 2).getLatLng().longitude +
                                    mode +
                                    "&waypoints=";
                            for (int i = 0; i < nr_destinatii - 2; i++) {
                                LatLng locations;
                                locations = places.get(i).getLatLng();
                                if (i == nr_destinatii - 3) {
                                    separator = "";
                                }
                                loc = loc + locations.latitude + "," + locations.longitude + separator;
                            }
                            url = url + loc + language + key;
                            path = new Traseu().getPath(url, new AsyncResponse() {
                                @Override
                                public void processFinishedTraseu(List<LatLng> path, long duration, long distance, String error) {
                                    if (error != "") {
                                        Toast.makeText(MainActivity.this, "Change the transportation mode!", Toast.LENGTH_LONG).show();
                                    } else {
                                        PolylineOptions polylineOptions = new PolylineOptions();
                                        Log.d("size", "" + path.size());
                                        for (int i = 0; i < path.size(); i++) {
                                            polylineOptions.add(path.get(i));
                                        }
                                        polylineOptions.width(10);
                                        polylineOptions.color(Color.BLUE);
                                        polyline = mMap.addPolyline(polylineOptions);
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 12f));
                                        Log.d("Duration", "" + duration);
                                        route_duration = duration;
                                        route_distance = distance;
                                        durationText.setText(formatDurationAndDistance(route_duration, route_distance));

                                    }
                                }

                                @Override
                                public void processFinishedDurata(Drum distance, String error) {

                                }
                            });
                        }

                    }
                }
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    Log.d("Success", "S");
                } else
                    getLocationPermission();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("isServicesOK", "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    getCurrentLocation();
                } else {
                    getLocationPermission();
                }
            }
        }

    }

    //gets current location
    public void getCurrentLocation() {
        LocationRequest locationRequest;
        locationRequest = LocationRequest.create().setInterval(10000).setFastestInterval(3000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            LatLng latLng = new LatLng(locationResult.getLocations().get(latestLocationIndex).getLatitude(), locationResult.getLocations().get(latestLocationIndex).getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                            currentLocationLatLng = latLng;
                            LatLng boundMin = new LatLng(currentLocationLatLng.latitude - 1, currentLocationLatLng.longitude - 1);
                            LatLng boundMax = new LatLng(currentLocationLatLng.latitude + 1, currentLocationLatLng.longitude + 1);
                            autocompleteSupportFragment.setLocationBias(RectangularBounds.newInstance(boundMin, boundMax));
                            Log.d("Current location", "LatLng: " + latLng);
                        }
                    }
                }, Looper.getMainLooper());
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        return;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        getLocationPermission();
        getCurrentLocation();
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        if (polylineOptions != null) {
            mMap.addPolyline(polylineOptions);
        }
        mMap.setMapType(SaveSharedPreferences.getPrefMapType(MainActivity.this) + 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        user_logged = FirebaseAuth.getInstance().getCurrentUser();
        if (user_logged == null && SaveSharedPreferences.getEmail(this).isEmpty()) {
            openActivity(LogInActivity.class);
        }

        if (mMap != null) {
            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            }

            if (SaveSharedPreferences.isPreferencesSet(this)) {
                mMap.setMapType(SaveSharedPreferences.getPrefMapType(this) + 1);
            } else {
                mMap.setMapType(1);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        user_logged = FirebaseAuth.getInstance().getCurrentUser();
        if (user_logged == null && SaveSharedPreferences.getEmail(this).isEmpty()) {
            openActivity(LogInActivity.class);
        }

        if (mMap != null) {
            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            }

            if (SaveSharedPreferences.isPreferencesSet(this)) {
                mMap.setMapType(SaveSharedPreferences.getPrefMapType(this) + 1);
            } else {
                mMap.setMapType(1);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        user_logged = FirebaseAuth.getInstance().getCurrentUser();
        if (user_logged == null && SaveSharedPreferences.getEmail(this).isEmpty()) {
            openActivity(LogInActivity.class);
        }
        if (mMap != null) {
            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            }

            if (SaveSharedPreferences.isPreferencesSet(this)) {
                mMap.setMapType(SaveSharedPreferences.getPrefMapType(this) + 1);
            } else {
                mMap.setMapType(1);
            }
        }
    }

    private void openActivity(Class activity) {
        Intent intent = new Intent(MainActivity.this, activity);
        startActivity(intent);
    }

    private String formatDurationAndDistance(long duration, long distance) {
        units = SaveSharedPreferences.getPrefUnits(this);
        Log.d("Units",""+units);
        String time = "";
        String dist = "";
        String format = "";
        String units_str = "";
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        if(units.equals("Metric") || units.equals("")){
            units_str = " km";
            if (distance < 100) {
                dist = "0.1" + units_str;
            } else {
                dist = decimalFormat.format((double) distance / 1000) + units_str;
            }
        }else if(units.equals("Imperial")){
            distance = (long)(distance * 1.09);
            units_str = " mi";
            if (distance <= 176) {
                dist = "0.1" + units_str;
            } else {
                dist = decimalFormat.format((double) distance / 1760) + units_str;
            }
        }
        int hours = (int) duration / 3600;
        int rest = (int) duration - hours * 3600;
        int minutes = rest / 60;
        if (duration < 60) {
            hours = 0;
            minutes = 1;
        }
        if (duration < 3600) {
            time += minutes + " min";
        } else {
            time += hours + " hr " + minutes + " min";
        }

        if (distance == 0 || duration == 0) {
            format = "";
        } else
            format = time + " (" + dist + ")";

        return format;
    }
}
