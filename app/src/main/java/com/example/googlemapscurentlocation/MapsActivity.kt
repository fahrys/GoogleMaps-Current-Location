package com.example.googlemapscurentlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity() {

    private lateinit var mMap: GoogleMap
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var currentAddress:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        getCurrentLocation()
    }

    @SuppressLint("RestrictedApi")
    fun getCurrentLocation() {
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        val locationRequest = LocationRequest ()
                .setInterval(3000)
                .setFastestInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest ,
                object : LocationCallback(){
                    override fun onLocationResult(p0: LocationResult) {
                        super.onLocationResult(p0)
                    for (location in p0.locations) {
                        mapFragment.getMapAsync(OnMapReadyCallback {
                            mMap = it
                            if (ActivityCompat.checkSelfPermission(this@MapsActivity , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MapsActivity , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(this@MapsActivity , arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                            }
                            mMap.isMyLocationEnabled = true
                            mMap.uiSettings.isZoomControlsEnabled = true
                            val locationResult = LocationServices.getFusedLocationProviderClient(this@MapsActivity).lastLocation
                            locationResult.addOnCompleteListener(this@MapsActivity ) {
                                if (it.isSuccessful && it.result != null) {

                                    var currentLocation = it.result
                                    var currentLatitude = currentLocation.latitude
                                    var currentLogtitude = currentLocation.longitude
                                    val geocoder = Geocoder(this@MapsActivity)
                                    var geocoderresult = geocoder.getFromLocation(currentLatitude , currentLogtitude , 1)
                                    currentAddress = geocoderresult[0].getAddressLine(0)
                                    var myLocation = LatLng(currentLatitude, currentLogtitude,)
                                    mMap.addMarker(MarkerOptions().position(myLocation).title(currentAddress)).showInfoWindow()
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
                                }
                            }

                        })

                    }
                    }
                },
                Looper.myLooper()
        )
    }
}