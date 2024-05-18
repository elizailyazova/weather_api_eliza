package com.example.weather_api_eliza.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.weather_api_eliza.R;
import com.example.weather_api_eliza.databinding.FragmentHomeBinding;
import com.example.weather_api_eliza.models.Clouds;
import com.example.weather_api_eliza.models.Main;
import com.example.weather_api_eliza.models.Model;
import com.example.weather_api_eliza.models.Sys;
import com.example.weather_api_eliza.models.Wind;
import com.example.weather_api_eliza.remote_data.RetrofitBuilder;
import com.example.weather_api_eliza.remote_data.WeatherApi;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    LottieAnimationView rainy_lotty;
    Integer temperature;
    Integer tempMaximal;
    Integer tempMinimal;
    int humadity_c;
    String currentTime = java.text.DateFormat.getDateTimeInstance().format(new Date());
    final String apiKey = WeatherApi.URL_KEY;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.rainLotty.setAnimation(R.raw.rainy_lotty);
        binding.snowLotty.setAnimation(R.raw.snow_lotty);


        binding.localtime.setText(currentTime);

        Call<Model> call = RetrofitBuilder.getInstance().getCurrentWeather("Bishkek", apiKey);
        call.enqueue(new Callback<Model>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Main main_model = response.body().getMain_model();
                    Wind wind_model = response.body().getWind_model();
                    Clouds clouds_model = response.body().getClouds_model();
                    Sys sys_model = response.body().getSys_model();

                    Double temp = main_model.getTemp();
                    Double tempMax = main_model.getTempMax();
                    Double tempMin = main_model.getTempMin();

                    temperature = makeFromFaringate(temp);
                    tempMaximal = makeFromFaringate(tempMax);
                    tempMinimal = makeFromFaringate(tempMin);

                    binding.tempMain.setText(String.valueOf(temperature) + " °C");

                    if (temperature <= 14) {
                        binding.sun.setVisibility(View.INVISIBLE);
                        setNoHotWeather();

                    binding.maxMinTemp.setText(String.valueOf(tempMaximal) + "°C↑ \n"
                                               + String.valueOf(tempMinimal) + " °C↓");

                    binding.cityName.setText("Bishkek");
                    binding.humidity.setText(main_model.getHumidity() + " %");
                    humadity_c = main_model.getHumidity();
                    if (humadity_c >= 55) {
                        rainy_possible();
                    }
                    if (temperature <= 0) {
                        snow_possible();
                    }
                    binding.pressure.setText(main_model.getPressure() + "\nmBar");
                    binding.wind.setText(wind_model.getSpeed() + "m/s");
                    binding.cloudy.setText(clouds_model.getAll() + " %");

                    binding.sunrise.setText(String.valueOf(getCurrDateTime(sys_model.getSunrise())));
                    binding.sunset.setText(String.valueOf(getCurrDateTime(sys_model.getSunset())));

                    binding.timeZone.setText(String.valueOf(response.body().getTimezone()));

                }
                setCondition();

                if (response.body().getTimezone() <= 6500 && response.body().getTimezone() >= -27500) {
                    setNight();
                } else {
                    setDay();
                }
            }}

            @Override
            public void onFailure(Call<Model> call, @NonNull Throwable throwable) {
                Toast.makeText(requireActivity(), "No WeatherForecast data",
                        Toast.LENGTH_SHORT).show();
                Log.e("Tag", throwable.getLocalizedMessage());
            }
        });
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.slideUpBottomSheet.setVisibility(View.VISIBLE);
        Log.d("Visibility", "slideUp_bottom_sheet: " + binding.slideUpBottomSheet.getVisibility());


        binding.slideUpBottomSheet.setOnClickListener(v -> {

            if (binding.bottomSheet.getVisibility() == View.GONE) {
                binding.bottomSheet.setVisibility(View.VISIBLE);
            } else {
                binding.bottomSheet.setVisibility(View.GONE);
            }
            binding.rainLotty.setVisibility(View.INVISIBLE);
            binding.snowLotty.setVisibility(View.INVISIBLE);
            binding.blueSky.setVisibility(View.VISIBLE);
            binding.badWeatherSky.setVisibility(View.INVISIBLE);
            binding.inputCity.setText("...");
            binding.condition.setText("...");
            binding.isRainOrNot.setText("...");
            binding.isSnowOrNot.setText("...");
            binding.sun.setVisibility(View.INVISIBLE);     });

        binding.search.setOnClickListener(v1 -> {
            if (!binding.inputCity.getText().toString().isEmpty()) {

                Call<Model> call = RetrofitBuilder.getInstance()
                        .getCurrentWeather(binding.inputCity.getText().toString(), apiKey);
                call.enqueue(new Callback<Model>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<Model> call, @NonNull Response<Model> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Main main_model = response.body().getMain_model();
                            Wind wind_model = response.body().getWind_model();
                            Clouds clouds_model = response.body().getClouds_model();
                            Sys sys_model = response.body().getSys_model();

                            Double temp = main_model.getTemp();
                            Double tempMax = main_model.getTempMax();
                            Double tempMin = main_model.getTempMin();

                            temperature = makeFromFaringate(temp);
                            tempMaximal = makeFromFaringate(tempMax);
                            tempMinimal = makeFromFaringate(tempMin);

                            binding.tempMain.setText(String.valueOf(temperature) + " °C");
                            binding.maxMinTemp.setText(String.valueOf(tempMaximal) + "°C↑ \n"
                                    + String.valueOf(tempMinimal) + " °C↓");
                            binding.cityName.setText(binding.inputCity.getText().toString());
                            binding.humidity.setText(main_model.getHumidity() + " %");
                            binding.pressure.setText(main_model.getPressure() + "\nmBar");
                            binding.wind.setText(wind_model.getSpeed() + "m/s");
                            binding.cloudy.setText(clouds_model.getAll() + " %");
                            binding.sunrise.setText(String.valueOf(getCurrDateTime(sys_model.getSunrise())));
                            binding.sunset.setText(String.valueOf(getCurrDateTime(sys_model.getSunset())));
                            binding.timeZone.setText(String.valueOf(response.body().getTimezone()));

                            setCondition();

                            if (response.body().getTimezone() <= 6500 && response.body().getTimezone() >= -27500) {
                                setNight();
                            } else {
                                setDay();
                            }
                        }}


                    @Override
                    public void onFailure(Call<Model> call, @NonNull Throwable throwable) {
                        Toast.makeText(requireActivity(), "No WeatherForecast data"
                                + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                binding.bottomSheet.setVisibility(View.GONE);
            } else {
                Toast.makeText(requireActivity(), "Input name of city!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setDay() {
        binding.nightSky.setVisibility(View.INVISIBLE);
        binding.blueSky.setVisibility(View.VISIBLE);
    }

    public void setNight() {
        binding.sun.setVisibility(View.INVISIBLE);
        binding.nightSky.setVisibility(View.VISIBLE);
        binding.blueSky.setVisibility(View.INVISIBLE);

    }

    public int makeFromFaringate(double tt) {
        Integer gr = (int) (tt - 273.15);
        return gr;
    }

    public void setCondition() {
        if (temperature > 20 && temperature <= 50) {
            binding.blueSky.setVisibility(View.VISIBLE);
            binding.condition.setText("Day \nhotter");
            dryWeather(); }
        if (temperature <= 20 && temperature > 14) {
            binding.blueSky.setVisibility(View.VISIBLE);
            binding.condition.setText("Day: \nlight \nsunny");
            dryWeather();
        } else {
            if(temperature >= 12 && temperature <= 14) {
                setNoHotWeather();
                binding.condition.setText("Day: \ncloudy");
                rainy_monitoring();
            } else {
                if (temperature >= 10 && temperature <12) {
                    setNoHotWeather();
                    binding.condition.setText("Day: \ncold");
                    rainy_monitoring();
                } else {
                    if (temperature < 9) {
                        setNoHotWeather();
                        binding.condition.setText("Day: \n very \ncold");
                        snow_monitoring();
                    }
                }
            }
        }
    }
    public void rainy_monitoring() {
        if (humadity_c <= 55) {
            binding.rainLotty.setVisibility(View.INVISIBLE);
            binding.isRainOrNot.setText("");
            dryWeather();
        } else {
            rainy_possible();
    } }


    public void snow_monitoring() {
        if (temperature >= 0) {
            binding.snowLotty.setVisibility(View.INVISIBLE);
            binding.isSnowOrNot.setText("");
            dryWeather();
        } else {
            snow_possible();
        } }


    public void setNoHotWeather() {
        binding.sun.setVisibility(View.INVISIBLE);
        binding.blueSky.setVisibility(View.INVISIBLE);
        binding.badWeatherSky.setVisibility(View.VISIBLE);
    }


    public String getCurrDateTime (long m) {
        String new_m = java.text.DateFormat.getDateTimeInstance().format(new Date(m));
        return new_m;
    }

    public void rainy_possible() {
        binding.isRainOrNot.setVisibility(View.VISIBLE);
        binding.isRainOrNot.setText("rain is \npossible");
        binding.rainLotty.setVisibility(View.VISIBLE);
        binding.sun.setVisibility(View.INVISIBLE);
        binding.badWeatherSky.setVisibility(View.VISIBLE);
    }

    public void snow_possible() {
        binding.isSnowOrNot.setVisibility(View.VISIBLE);
        binding.isSnowOrNot.setText("snow is \npossible");
        binding.snowLotty.setVisibility(View.VISIBLE);
        binding.sun.setVisibility(View.INVISIBLE);
        binding.badWeatherSky.setVisibility(View.VISIBLE);
    }


    public void dryWeather() {
        binding.rainLotty.setVisibility(View.INVISIBLE);
        binding.snowLotty.setVisibility(View.INVISIBLE);
        binding.badWeatherSky.setVisibility(View.INVISIBLE);
        binding.blueSky.setVisibility(View.VISIBLE);
        binding.sun.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}