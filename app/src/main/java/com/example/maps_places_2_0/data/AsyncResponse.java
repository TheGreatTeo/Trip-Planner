package com.example.maps_places_2_0.data;

import com.example.maps_places_2_0.model.Graf.Drum;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface AsyncResponse {
    void processFinishedDurata(Drum distance,String error);
    void processFinishedTraseu(List<LatLng> path,long duration,long distance,String error);
}
