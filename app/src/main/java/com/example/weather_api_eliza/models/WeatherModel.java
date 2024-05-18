package com.example.weather_api_eliza.models;

import android.content.Intent;

import com.google.gson.annotations.SerializedName;

public class WeatherModel {
    @SerializedName("icon")
    String icon;
    @SerializedName("cod")
    Integer cod;
}
