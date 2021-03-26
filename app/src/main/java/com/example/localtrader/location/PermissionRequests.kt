package com.example.localtrader.location

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class PermissionRequests {

    companion object{

        var locationPermission = false

        fun requestLocationPermissionFragment(fragment : Fragment, permissionId : Int){
            fragment.requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                permissionId
            )
        }

        fun requestLocationPermissionActivity(activity : Activity, permissionId : Int){
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                permissionId
            )
        }

    }
}