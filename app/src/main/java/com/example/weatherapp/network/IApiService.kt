package com.example.weatherapp.network
import com.example.weatherapp.constant.Const.Companion.openWeatherMapApikey
import com.example.weatherapp.model.Wheather.ForecastResult
import com.example.weatherapp.model.Wheather.WeatherResult
import retrofit2.http.GET
import retrofit2.http.Query

interface IApiService {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double=0.0,
        @Query("lon") lng: Double=0.0,
        @Query("units") units: String="metric",
        @Query("appid") appid: String = openWeatherMapApikey
    ):WeatherResult

    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double=0.0,
        @Query("lon") lng: Double=0.0,
        @Query("units") units: String="metric",
        @Query("appid") appid: String = openWeatherMapApikey
    ):ForecastResult
}