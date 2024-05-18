package com.example.weather_api_eliza.remote_data;

import com.example.weather_api_eliza.models.WeatherModel;
import com.example.weather_api_eliza.models.Model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    String URL_KEY = "3f2a5dffecfcb27f558aa47b6906f109" ;
    String BASE_URL  = "https://api.openweathermap.org";

    @GET("/data/2.5/weather")
    Call<Model> getCurrentWeather(
            @Query("q") String name,
            @Query("appid") String key);


    @GET("/data/2.5/weather/?q=London&appid=3f2a5dffecfcb27f558aa47b6906f109")
    Call<WeatherModel> getWeather(
            @Query("q") String name,
            @Query("appid") String key);

}
