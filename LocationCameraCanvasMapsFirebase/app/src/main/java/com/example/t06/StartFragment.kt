package com.example.t06

import android.app.Activity
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getExternalFilesDirs
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_start.view.*
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*
/**
 * A simple [Fragment] subclass.
 */
class StartFragment : Fragment(), adapter.OnItemClickListener {

    private val REQUEST_CODE = 1
    private val model: ViewModel by activityViewModels()

    //////////////////////////////////////////////////
    private var coroutineJob: Job? = null
    var latlong: Location? = null
    lateinit var  geocoder : Geocoder
    lateinit var sensorManager: SensorManager
    lateinit var accelerometer: Sensor
    lateinit var magnetometer: Sensor
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var addresses: List<Address> = emptyList()

    val locationRequest = LocationRequest.create()?.apply {
        interval = 1000
        fastestInterval = 500
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    //Gets the date and time
    val sdf = SimpleDateFormat("M/dd/yyyy hh:mm:ss")
    val currentDate = sdf.format(Date())
    //////////////////////////////////////////////////

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        super.onCreateView(inflater, container, savedInstanceState)

        //Creates the recycler view
        view.recycler_view.adapter = adapter(model.pictureList, this)
        view.recycler_view.layoutManager = LinearLayoutManager(activity)
        view.recycler_view.setHasFixedSize(true)

        if(savedInstanceState != null)  {
        }
        //Used to get the coordinates
            geocoder = Geocoder(activity, Locale.getDefault())
            sensorManager = activity?.getSystemService(SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)


        var  locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    model.coordinates ="Lat:${location?.latitude} Long:${location?.longitude}"
                    model.lat = "${location?.latitude}"
                    model.long = "${location?.longitude}"
                }
            }
        }
        //passing the callback and the request to the location client
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            null)
        //////////////////////////////////////////////////////////


        //When floating action button is clicked, the camera activates.
        view.fab_btn.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //dispatchTakePictureIntent()
            if (takePictureIntent.resolveActivity(activity?.packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            } else {
            }
        }
        return view
    }

    //Floating button is pressed which opens the camera.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)  {

        //Gets the location, then stores the coordinates, lat+long, and time.
        //Meets requirement 5 (location and addressed retrieved after floating button is pressed).
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.

                location?.let { latlong = it }
                //Gets the coordinates and places them into a String in the ViewModel.
                model.coordinates = "Lat:${location?.latitude} Long:${location?.longitude}"
                latlong?.let{ addressForLocation(it)}
                model.date = currentDate
            }

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)    {
            model.image1 = data?.extras?.get("data") as Bitmap
            view?.let { Navigation.findNavController(it).navigate(R.id.action_startFragment_to_imageFragment) }
        }
        else    {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    //Used to get the address.
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

    //Used to place the address value into a list.
    fun addressForLocation(location: Location){

        coroutineJob?.cancel()
        coroutineJob = CoroutineScope(Dispatchers.IO).launch {
            val addressDeffered = async {
                getAddress(location)
            }
            val result = addressDeffered.await()
            withContext(Dispatchers.Main) {
                model.address = result
            }
        }
    }

    //Used to kill the coroutine.
    override fun onDestroy() {
        super.onDestroy()

        coroutineJob?.cancel()
    }


    //Clicking on recyclerView to go to google earth
    override fun onItemClick(user: info, position: Int) {

        val intent = Intent(context, MapsActivity::class.java)
        startActivity(intent)
        //Toast.makeText(activity, user.text2, Toast.LENGTH_SHORT).show()
    }


    /** High resolution camera
    var currentPhotoPath: String = ""
    val REQUEST_TAKE_PHOTO = 1
    private var filePath: Uri? = null
    internal var storage:FirebaseDatabase? = null
    internal var storageReference:StorageReference?= null


    @Throws(IOException::class)
    private fun createImageFile(): File {
        //Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: Array<out File> = requireActivity().getExternalFilesDirs(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir[0]
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {takePictureIntent->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    //Error occurred while creating the File ...
                    null
                }

                photoFile?.also {

                    Log.d("doWork 1: ", "A")
                    val photoURI: Uri = FileProvider.getUriForFile(

                        requireActivity(), "com.example.android.fileprovider",
                                it
                    )
                    Log.d("doWork 2: ", "B")
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }
    //High resolution attempt//////////////////////////////////////////////////////////
    */
}




















