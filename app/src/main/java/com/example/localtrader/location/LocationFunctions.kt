package com.example.localtrader.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class LocationFunctions {

    companion object{

        private const val permissionId = 1000

        fun getLocation(context : Context){
        }

        fun checkPermissions(context: Context) : Boolean{
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        }

        fun requestPermission(activity : Activity){
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                permissionId
            )
        }

        //fun isLocationServiceEnabled(context : Context) : Boolean{
            //val locationManager = getSystemService(context) as LocationManager
        //}
    }
}