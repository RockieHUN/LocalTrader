package com.example.localtrader.location

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat

class PermissionRequests {

    companion object{

        var locationPermission = false

        fun requestLocationPermission(activity : Activity, permissionId : Int){
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                permissionId
            )
        }

    }
}