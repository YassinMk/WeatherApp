package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.weatherapp.constant.Const.Companion.colorBg1
import com.example.weatherapp.constant.Const.Companion.colorBg2
import com.example.weatherapp.constant.Const.Companion.permissions
import com.example.weatherapp.model.MyLatLng
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.coroutineScope
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.Wheather.ForecastResult
import com.example.weatherapp.model.Wheather.WeatherResult
import com.example.weatherapp.view.WeatherSection
import com.example.weatherapp.viewmodel.ForecastSection
import com.example.weatherapp.viewmodel.MainViewModel
import com.example.weatherapp.viewmodel.STATE


class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationProviderCIient: FusedLocationProviderClient
    private lateinit var locationCa11back: LocationCallback
    private lateinit var mainViewModel: MainViewModel
    private var locationRequired: Boolean= false


    override fun onResume(){
        super.onResume()
        if(locationRequired){
            startLocationUpdate()
        }
    }

    override fun onPause(){
        super.onPause()
        locationCa11back?.let{
            fusedLocationProviderCIient.removeLocationUpdates(it)
        }
    }
    @SuppressLint("MissingPermission")
    private fun startLocationUpdate(){
        locationCa11back?.let{
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,100
            )
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(3000)
                .setMaxUpdateDelayMillis(100)
                .build()
            fusedLocationProviderCIient?.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLocationClient()
        initViewModel()
        setContent {
            var currentLocation by remember {
                mutableStateOf(MyLatLng(0.0,0.0))
            }

            locationCa11back= object : LocationCallback(){
                override fun onLocationResult(p0: LocationResult){
                    super.onLocationResult(p0)
                    for (location in p0.locations){
                        currentLocation = MyLatLng(location.latitude,location.longitude)
                    }
                    fetchWeatherInformation(mainViewModel,currentLocation)
                }
                //Fetch Api

            }
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    locationScreen(this@MainActivity,currentLocation)
                }
            }
        }
    }

    private fun initViewModel(){
        mainViewModel = ViewModelProvider(this@MainActivity)[MainViewModel::class.java]
    }
    @Composable
    private fun locationScreen(context: Context,currentLocation: MyLatLng){
        //Request runtime permission
        val launcherMultiplePermission = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionMap ->
            val areGranted = permissionMap.values.reduce { accepted, next ->
                accepted && next
            }
            if (areGranted) {
                locationRequired = true
                startLocationUpdate()
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        val systemUiController = rememberSystemUiController()
        DisposableEffect(key1 = true, effect = {
            systemUiController.isSystemBarsVisible = false
            onDispose {
                systemUiController.isSystemBarsVisible = true
            }

        })
        LaunchedEffect(key1 = currentLocation, block = {
            coroutineScope {
                if(permissions.all{
                    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                    }){
                    startLocationUpdate()
                }
                else{
                    launcherMultiplePermission.launch(permissions)
                }

            }

        })
        val gradient = Brush.linearGradient(
            colors = listOf(Color(colorBg1), Color(colorBg2)),
            start= Offset(1000f,-1000f),
            end = Offset(1000f,1000f),

        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)

        ){
            val screenHeight = LocalConfiguration.current.screenHeightDp.dp;
            val marginTop = screenHeight * 0.1f;
            val marginTopPx = with(LocalDensity.current) { marginTop.toPx()}
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        layout(placeable.width, placeable.height + marginTopPx.toInt()) {
                            placeable.placeRelative(0, marginTopPx.toInt())
                        }

                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                    when (mainViewModel.state) {
                        STATE.LOADING -> {
                            loadingSection()
                        }

                        STATE.FAILED -> {
                            errorSection(mainViewModel.errorMessage)
                        }
                        else -> {
                            WeatherSection(mainViewModel.wheatherResponse)
                            ForecastSection(mainViewModel.forecastResponse)
                        }
                    }

            }

        }
    }

    private fun fetchWeatherInformation(mainViewModel: MainViewModel,currentLocation: MyLatLng){
        mainViewModel.state = STATE.LOADING
        mainViewModel.getWeatherByLocation(currentLocation)
        //mainViewModel.getForecastByLocation(currentLocation)
        mainViewModel.state = STATE.SUCCESS
    }




    @Composable
    private fun loadingSection(){
        return Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ){
           CircularProgressIndicator(color =Color.White)
        }
    }

    @Composable
    private fun errorSection(errorMessage: String){
        return Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ){
            Text(text =errorMessage, color = Color.White )
        }

    }
    private fun initLocationClient(){
        fusedLocationProviderCIient = LocationServices.getFusedLocationProviderClient(this)
    }
}
