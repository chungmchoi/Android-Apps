package com.example.t06

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var database: DatabaseReference
// ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        database = Firebase.database.reference

    }

    //val rootRef = database.child("users")

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        var lat1 = 0.0
        var long1 = 0.0

        //Reading latitude and longitude data from firebase.
        FirebaseDatabase.getInstance().reference.child("users").child("1").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                var map = p0.value as Map<String, ImageFragment.firebaseSave>
                lat1 = map["lat"].toString().toDouble()
                long1 = map["long"].toString().toDouble()
            }
        })


        mMap = googleMap
        val coordinates = LatLng(lat1, long1)
        mMap.addMarker(MarkerOptions().position(coordinates))
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Blacksburg"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates))
    }

    ///////////////////////////////////////////////////////////
}


