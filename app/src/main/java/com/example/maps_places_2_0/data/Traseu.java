package com.example.maps_places_2_0.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.maps_places_2_0.controller.AppController;
import com.example.maps_places_2_0.model.Graf.Drum;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Traseu {
    List<LatLng> path;
    long duration;
    long distance;
    String error;
    public List<LatLng> getPath(String url, final AsyncResponse callback){
        path = new ArrayList<LatLng>();
        duration = 0;
        distance = 0;
        error = "";
        JsonObjectRequest jsonObjectRequest123 = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject jsonRespRouteDuration = null;
                JSONArray routes = null;
                JSONArray legs = null;
                JSONArray steps = null;

                try {
                    List<String> polyline = new ArrayList<String>();
                    jsonRespRouteDuration = new JSONObject(String.valueOf(response));
                    routes = jsonRespRouteDuration.getJSONArray("routes");
                    for(int i = 0; i< routes.length(); i++){
                        legs = ((JSONObject)routes.get(i)).getJSONArray("legs");
                        for(int j=0;j<legs.length();j++){
                            duration += (Integer)(((JSONObject)legs.get(j)).getJSONObject("duration").get("value"));
                            distance += (Integer)(((JSONObject)legs.get(j)).getJSONObject("distance").get("value"));
                            steps = ((JSONObject)legs.get(j)).getJSONArray("steps");
                            for(int k =0; k<steps.length();k++){
                                polyline.add((String)((JSONObject)((JSONObject)steps.get(k)).get("polyline")).get("points"));
                            }
                        }
                    }
                    path = decodePolyline(polyline);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(callback != null) callback.processFinishedTraseu(path,duration,distance,error);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error","onErrorResponse: " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest123);

        return path;
    }
    private List<LatLng> decodePolyline(List<String> encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();

        for(int i = 0; i< encoded.size();i++) {
            int index = 0, len = encoded.get(i).length();
            int lat = 0, lng = 0;
            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.get(i).charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.get(i).charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
        }
        return poly;
    }
}
