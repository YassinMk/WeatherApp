package com.example.weatherapp.model.Wheather

import com.google.gson.annotations.SerializedName

data class ForecastResult(
    @SerializedName("cod") var cod : String?=null,
    @SerializedName("message") var message : Int?=null,
    @SerializedName("cnt") var cnt : Int?=null,
    @SerializedName("list") var list : List<CustomList>?= arrayListOf(),
    @SerializedName("city") var city : City?=null,
)