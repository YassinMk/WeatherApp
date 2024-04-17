package com.example.weatherapp.constant

class Const {
    companion object {
        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        const val openWeatherMapApikey = "ae6170a64d465935e51cee2d1d45fc8d";

        const val colorBg1 = 0xff08203e;
        const val colorBg2 = 0xff557c93;
        const val cardColor = 0xFF333639;

        const val LOADING = "Loading ... "
        const val NA = "N/A"

    }
}