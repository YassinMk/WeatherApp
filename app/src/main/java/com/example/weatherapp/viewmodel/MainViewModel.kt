package com.example.weatherapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.MyLatLng
import com.example.weatherapp.model.Wheather.ForecastResult
import com.example.weatherapp.model.Wheather.WeatherResult
import com.example.weatherapp.network.RetrofitClient
import kotlinx.coroutines.launch

enum class STATE{
    LOADING,
    SUCCESS,
    FAILED
}
class MainViewModel: ViewModel() {
   var state by mutableStateOf(STATE.LOADING)
    var wheatherResponse : WeatherResult by mutableStateOf(WeatherResult())
    var forecastResponse :ForecastResult by mutableStateOf(ForecastResult())
    var errorMessage :String by mutableStateOf("")

    fun getWeatherByLocation(latLng:MyLatLng){
        viewModelScope.launch {
            state = STATE.LOADING
            val apiService = RetrofitClient.getInstance()
            try {
                val apiResponse = apiService.getWeather(latLng.latitude, latLng.longitude)
                wheatherResponse =apiResponse
                state = STATE.SUCCESS
            }catch (e:Exception){
                state = STATE.FAILED
                errorMessage = e.message.toString()
            }
        }
    }
    fun getForecastByLocation(latLng:MyLatLng){
        viewModelScope.launch {
            state = STATE.LOADING
            val apiService = RetrofitClient.getInstance()
            try {
                val apiResponse = apiService.getForecast(latLng.latitude, latLng.longitude)
                forecastResponse =apiResponse
                state = STATE.SUCCESS
            }catch (e:Exception){
                state = STATE.FAILED
                errorMessage = e.message.toString()
            }
        }
    }

}