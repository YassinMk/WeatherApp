package com.example.weatherapp.viewmodel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weatherapp.constant.Const.Companion.NA
import com.example.weatherapp.constant.Const.Companion.cardColor
import com.example.weatherapp.model.Wheather.ForecastResult
import com.example.weatherapp.utils.Utils.Companion.buildIcon
import org.jetbrains.annotations.Async
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@Composable
fun ForecastSection(forecastResponse: ForecastResult){
    return Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){
        forecastResponse.list?.let {
            listForecast->
            if(listForecast.size>0){
                LazyRow(
                    modifier = Modifier.fillMaxSize()
                ){
                    items(listForecast!!){
                        currentItem->
                        currentItem.let{
                            item->
                            var temp =""
                            var icon =""
                            var time=""
                            item.main?.let {main->
                                temp = if (main == null) NA else "${main.temp}°C"
                            }
                            item.weather.let { weather->
                                icon = if (weather == null) NA else buildIcon(weather[0].icon!!,
                                    isBigSize = false )
                            }
                            item.dt?.let {dateTime->
                                time = if(dateTime == null) NA else timestampToHumanDate(dateTime.toLong(),"HH:mm")
                            }

                            ForcastTitle(temp = temp , image = icon , time = time)
                        }

                    }
                }
            }
        }
    }
}



@Composable
fun ForcastTitle(temp: String, image: String, time: String) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors= CardDefaults.cardColors(
            containerColor = Color(cardColor).copy(alpha = 0.7f), // Hardcoded white color
            contentColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(80.dp),
            ){
            Text(text =temp.ifEmpty { NA } , color = Color.White)
            AsyncImage(model = image, contentDescription = image,
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp),
                contentScale = ContentScale.FillBounds)

            Text(text = time.ifEmpty { NA }, color = Color.White)

        }
    }
}

fun timestampToHumanDate(timestamp: Long, format: String): String {
    val date = Date(timestamp * 1000L)
    val sdf = SimpleDateFormat(format)
    sdf.timeZone = TimeZone.getDefault() // set your desired timezone
    return sdf.format(date)
}