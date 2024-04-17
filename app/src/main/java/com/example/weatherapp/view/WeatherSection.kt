package com.example.weatherapp.view

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.constant.Const.Companion.LOADING
import com.example.weatherapp.constant.Const.Companion.NA
import com.example.weatherapp.model.Wheather.WeatherResult
import com.example.weatherapp.utils.Utils.Companion.buildIcon
import com.example.weatherapp.utils.Utils.Companion.timetampToHumanDate
import com.example.weatherapp.viewmodel.STATE
import com.mikepenz.iconics.IconicsColor
import org.jetbrains.annotations.Async
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.iconics.utils.color
import com.mikepenz.iconics.utils.colorInt
import android.graphics.Color as AndroidColor
import com.mikepenz.iconics.utils.sizeDp

@Composable
fun WeatherSection(weatherResponse: WeatherResult){
    /*title Section */
    var title=""
    if(!weatherResponse.name.isNullOrEmpty()){
        weatherResponse.name?.let {
            title=it
        }
    }else{
        weatherResponse.coord?.let {
            title="${it.lat}/${it.lon}"
        }
    }
    /*subTitle Section */
    var subTitle=""
    val dateVal = (weatherResponse.dt ?: 0)
    subTitle = if(dateVal ==0) LOADING
    else timetampToHumanDate(dateVal.toLong(),"dd-MM-yyyy")
    var icon = ""
    var description=""


    weatherResponse.weather.let {
        if(it!!.size>0){
            description = if(it[0].description == null) LOADING else it[0].description!!
            icon = if(it[0].icon==null) LOADING else it[0].icon!!
        }
    }
    /*temp Section */
    var temp=""
    weatherResponse.main?.let {
        temp = "${it.temp}°C"
    }
    /*wind Section */
    var wind=""
    weatherResponse.wind?.let {
        wind = if(it == null) LOADING else "${it.speed}"
    }
    /*Clouds*/
    var clouds=""
    weatherResponse.clouds?.let {
        clouds = if(it == null) LOADING else "${it.all}"
    }
    /*SNOW*/
    var snow=""
    weatherResponse.snow?.let {
        snow = if(it!!.d1h == null) NA else "${it.d1h}"
    }
    WheatherTitleSection(text=title , subText = subTitle , fontSize=30.sp)
    WheatherImage(icon = icon)
    WheatherTitleSection(text = temp, subText=description, fontSize = 60.sp)
    Row(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        WeatherInfo(icon =FontAwesome.Icon.faw_wind , text= wind)
        WeatherInfo(icon =FontAwesome.Icon.faw_cloud, text= clouds)
        WeatherInfo(icon = FontAwesome.Icon.faw_snowman, text = snow)

    }


}




fun faIconToImageBitmap(context: Context, faIcon: IIcon): ImageBitmap {
    val drawable = IconicsDrawable(context, faIcon).apply {
        sizeDp = 48.dp.value.toInt()
        colorInt = AndroidColor.WHITE
    }
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap.asImageBitmap()
}
@Composable
fun WeatherInfo(icon: IIcon, text: String) {
    val context = LocalContext.current
    val imageBitmap = faIconToImageBitmap(context, icon)
    Column{
        Icon(bitmap = imageBitmap, contentDescription = text)
        Text(text = text, fontSize = 24.sp, color = Color.White)
    }
}

@Composable
fun WheatherImage(icon: String) {
    AsyncImage(
      model = buildIcon(icon),contentDescription = icon, modifier = Modifier
            .width(200.dp)
            .height(200.dp)
    )

}

@Composable
fun WheatherTitleSection(text: String, subText: String, fontSize: TextUnit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text, fontSize = fontSize, color = Color.White, fontWeight = FontWeight.Bold)
        Text(subText, fontSize = 14.sp, color = Color.White)
    }
}



