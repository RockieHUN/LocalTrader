package com.example.localtrader.Utils

import android.content.Context

class MySharedPref {

    companion object{

        fun saveToSharedPref(context : Context, email : String, password : String)
        {
            val sharedPref = context.getSharedPreferences("credentials", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.clear()
            editor.putString("email", email)
            editor.putString("password", password)
            editor.apply()
        }

        fun getFromSharedPref(context : Context) : Map<String,String>
        {
            val sharedPref = context.getSharedPreferences("credentials", Context.MODE_PRIVATE)
            val credentials = sharedPref.all


            val map = mutableMapOf<String,String>()
            if (credentials.containsKey("email") && credentials.containsKey("password"))
            {
                map["email"] = credentials["email"] as String
                map["password"] = credentials ["password"] as String
            }

            return map
        }
    }
}