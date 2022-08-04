package com.example.t06

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_MAGNETIC_FIELD
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_GAME
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.Math.toDegrees
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), SensorEventListener {



    private var coroutineJob: Job? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var sensorManager: SensorManager
    lateinit var image: ImageView

    lateinit var accelerometer: Sensor
    lateinit var magnetometer: Sensor

    var currentDegree = 0.0f
    var lastAccelerometer = FloatArray(3)
    var lastMagnetometer = FloatArray(3)
    var lastAccelerometerSet = false
    var lastMagnetometerSet = false

    val locationRequest = LocationRequest.create()?.apply {
        interval = 1000
        fastestInterval = 500
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    lateinit var address: TextView
    lateinit var coordinates: TextView
    lateinit var search: Button
    lateinit var rotate: Button

    var latlong: Location? = null

    lateinit var  geocoder : Geocoder
    var addresses: List<Address> = emptyList()




    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        geocoder = Geocoder(this, Locale.getDefault())

        coordinates = findViewById(R.id.coordinates)
        address = findViewById(R.id.address)
        search = findViewById(R.id.search)

        rotate = findViewById(R.id.rotate)

        image = findViewById<ImageView>(R.id.compass)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.


                location?.let{latlong=it}

coordinates.text="Lat:${location?.latitude} Long:${location?.longitude}"

            }



      var  locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    coordinates.text="Lat:${location?.latitude} Long:${location?.longitude}"
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            null)



        search.setOnClickListener{

            latlong?.let{ printAddressForLocation(it)}

        }


        rotate.setOnClickListener{


            //TODO: if you want to activate the button, disable the accelerometer based image rotation first.
//         val rotateAnimation = RotateAnimation(
//                    currentDegree,
//                    currentDegree+45f,
//                    RELATIVE_TO_SELF, 0.5f,
//                    RELATIVE_TO_SELF, 0.5f)
//                rotateAnimation.duration = 1000
//                rotateAnimation.fillAfter = true
//
//                image.startAnimation(rotateAnimation)
//            currentDegree += 45f

        }
    }


    fun printAddressForLocation(location: Location){

        coroutineJob?.cancel()


        coroutineJob = CoroutineScope(Dispatchers.IO).launch {


            val addressDeferred = async {
                getAddress(location)
            }


            val result = addressDeferred.await()


            withContext(Dispatchers.Main) {

                    address.text = result

            }

        }


    }



    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, magnetometer)
    }
    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(this, accelerometer, SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnetometer, SENSOR_DELAY_GAME)
    }

    fun getAddress(location: Location): String {

        //adapted from https://developer.android.com/training/location/display-address.html
        try {
            addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                // In this sample, we get just a single address.
                1)
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
         return "Error: Service Not Available --$ioException"

        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            return "Error: Invalid lat long used--$illegalArgumentException"
        }

        if(addresses.isEmpty())
            return "No address found :("

        return "Address: "+addresses.get(0).getAddressLine(0)+"\n"+"City:"+addresses.get(0).getLocality()+"\n"+"Zip code:"+addresses.get(0).getPostalCode()
    }

    override fun onDestroy() {
        super.onDestroy()

        coroutineJob?.cancel()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor === accelerometer) {
            lowPass(event.values, lastAccelerometer)
            lastAccelerometerSet = true
        } else if (event.sensor === magnetometer) {
            lowPass(event.values, lastMagnetometer)
            lastMagnetometerSet = true
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            val r = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, null, lastAccelerometer, lastMagnetometer)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                val degree = (toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360

                val rotateAnimation = RotateAnimation(
                    currentDegree,
                    -degree,
                    RELATIVE_TO_SELF, 0.5f,
                    RELATIVE_TO_SELF, 0.5f)
                rotateAnimation.duration = 1000
                rotateAnimation.fillAfter = true

                image.startAnimation(rotateAnimation)
                currentDegree = -degree
            }
        }
    }

    fun lowPass(input: FloatArray, output: FloatArray) {
        val alpha = 0.05f

        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
    }


}
