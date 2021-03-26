package com.example.localtrader.location

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.example.localtrader.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class BusinessLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private val locationRequestCode = 10
    private lateinit var mMap: GoogleMap
    private val location : MutableLiveData<LatLng> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_location)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        supportActionBar?.hide()
    }

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
        mMap = googleMap

        mMap.setOnMapLongClickListener {
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(it).title("Vállalkozás helye"))
            this.location.value = it
        }

        val zoomLevel = 15f

        location.observe(this,{
            if (location.value != null){
                val currentLocation = LatLng(location.value!!.latitude, location.value!!.longitude)
                mMap.addMarker(MarkerOptions().position(currentLocation).title("Vállalkozás helye"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel))
            }
        })
        getLocation()

        setUpListeners()
    }

    private fun setUpListeners(){
        findViewById<Button>(R.id.submit_button).setOnClickListener {
            returnResult()
            finish()
        }
    }

    private fun returnResult(){
        val intent = Intent()
        intent.putExtra("longitude", location.value?.longitude)
        intent.putExtra("latitude", location.value?.latitude)
        setResult(RESULT_OK, intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //if got permission
        if (requestCode == locationRequestCode){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                getLocation()
            }
            //if didn't get permission SECOND time
            else{

            }
        }
    }


    private fun getLocation(){

        //if didn't get permission FIRST time, request permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            PermissionRequests.requestLocationPermissionActivity(this,locationRequestCode)
        }
        else{
            val locationClient = LocationServices.getFusedLocationProviderClient(this)
            locationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null){
                    this.location.value = LatLng(location.latitude, location.longitude)
                }
            }
        }
    }
}