package com.example.maps_places_2_0.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseUser;

public class SaveSharedPreferences {
    static final String PREF_NAME = "name";
    static final String PREF_ADDRESS = "address";
    static final String PREF_OPENING_HOURS = "opening_hours";
    static final String PREF_PHONE_NUMBER = "phone_number";
    static final String PREF_PRICE_LEVEL = "price_level";
    static final String PREF_WEBSITE_URI = "website_uri";
    static final String PREF_RATING = "rating";
    static final String PREF_EMAIL= "email";
    static final String PREF_TRANSPORTATION_MODE= "transportation_mode";
    static final String PREF_UNITS= "units";
    static final String PREF_MAP_TYPE= "map_type";
    static final String PREF_LANGUAGE= "language";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static String getPrefTransportationMode(Context ctx) {
        String transp = "driving";
        if(getSharedPreferences(ctx).getString(PREF_TRANSPORTATION_MODE,"")!= ""){
            transp = getSharedPreferences(ctx).getString(PREF_TRANSPORTATION_MODE,"");
        }
        return transp;
    }

    public static String getPrefUnits(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_UNITS, "");
    }

    public static int getPrefMapType(Context ctx) {
        int map_type = 0;
        if(getSharedPreferences(ctx).getString(PREF_MAP_TYPE,"") != "") {
            map_type = Integer.parseInt(getSharedPreferences(ctx).getString(PREF_MAP_TYPE, ""));
        }
        return map_type;
    }

    public static String getPrefLanguage(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_LANGUAGE, "");
    }

    public static void setPrefSettings(Context ctx, String type, CharSequence select){
        Editor editor = getSharedPreferences(ctx).edit();
        switch (type){
            case "transportation_mode":
                editor.putString(PREF_TRANSPORTATION_MODE,select.toString());
                editor.commit();
                break;
            case "units":
                editor.putString(PREF_UNITS,select.toString());
                editor.commit();
                break;
            case "map_type":
                editor.putString(PREF_MAP_TYPE,select.toString());
                editor.commit();
                break;
            case "language":
                editor.putString(PREF_LANGUAGE,select.toString());
                editor.commit();
                break;
        }
    }

    public static boolean isPreferencesSet(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return (sharedPreferences.getAll().size() > 4);
    }

    public static void setEmail(Context ctx, FirebaseUser user)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_EMAIL, user.getEmail());
        editor.commit();
    }

    public static String getEmail(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_EMAIL, "");
    }
    public static void setName(Context ctx, String name)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_NAME, name);
        editor.commit();
    }

    public static String getName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_NAME, "");
    }
    public static void setAddress(Context ctx, String address)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_ADDRESS, address);
        editor.commit();
    }

    public static String getAddress(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_ADDRESS, "");
    }
    public static void setOpeningHours(Context ctx, String opening_hours)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_OPENING_HOURS, opening_hours);
        editor.commit();
    }

    public static String getOpeningHours(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_OPENING_HOURS, "");
    }
    public static void setPhoneNumber(Context ctx, String phone_number)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PHONE_NUMBER, phone_number);
        editor.commit();
    }

    public static String getPhoneNumber(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_PHONE_NUMBER, "");
    }
    public static void setPriceLevel(Context ctx, String price_level)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PRICE_LEVEL, price_level);
        editor.commit();
    }


    public static String getPriceLevel(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_PRICE_LEVEL, "");
    }

    public static void setWebsiteUri(Context ctx, String website_uri)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_WEBSITE_URI, website_uri);
        editor.commit();
    }
    public static String getWebsiteUri(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_WEBSITE_URI, "");
    }
    public static void setRating(Context ctx, String rating)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_RATING, rating);
        editor.commit();
    }

    public static String getRating(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_RATING, "");
    }

    public static void clearEmail(Context ctx)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_EMAIL); //clear all stored data
        editor.commit();
    }
}
