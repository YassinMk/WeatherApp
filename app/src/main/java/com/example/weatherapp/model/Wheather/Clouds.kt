package com.example.weatherapp.model.Wheather

import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all") var all : Int?=null,
)