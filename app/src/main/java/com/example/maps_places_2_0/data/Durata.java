package com.example.maps_places_2_0.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.maps_places_2_0.controller.AppController;
import com.example.maps_places_2_0.model.Graf.Drum;

import org.json.JSONException;
import org.json.JSONObject;

public class Durata {
    Drum drum;
    String error;
    public Drum getDistance(String url,int nr_destinatii, final AsyncResponse callback){
        drum = new Drum(nr_destinatii);
        error = "";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                for(int i = 0; i < nr_destinatii-1; i++) {
                    for (int j = i + 1; j < nr_destinatii; j++) {
                        JSONObject jsonRespRouteDuration = null;
                        try {
                            jsonRespRouteDuration = new JSONObject(String.valueOf(response))
                                    .getJSONArray("rows")
                                    .getJSONObject(i)
                                    .getJSONArray("elements")
                                    .getJSONObject(j)
                                    .getJSONObject("duration");
                            String distance_str = jsonRespRouteDuration.get("value").toString();
                            Long dist = Long.parseLong(distance_str);
                            drum.adaugaMuchie(i, j, dist);
                            drum.adaugaMuchie(j, i, dist);
                        } catch (JSONException e) {
                            error = e.getMessage();
                            Log.d("JSON",""+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
                if(callback != null) callback.processFinishedDurata(drum,error);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error123","onErrorResponse: " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

        return drum;
    }

}
