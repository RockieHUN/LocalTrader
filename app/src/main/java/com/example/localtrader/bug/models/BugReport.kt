package com.example.localtrader.bug.models

import android.os.Build
import android.os.Build.VERSION_CODES

data class BugReport(

    var userId: String = "",
    var userFirstName: String = "",
    var userLastName: String = "",
    var userEmail : String = "",

    var category : String ="",
    var description : String ="",

    var reportId: String = "",

    var manufacturer: String = Build.MANUFACTURER,
    var model: String = Build.MODEL,
    var versionRelease: String = Build.VERSION.RELEASE,
    var buildVersion: String = VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name

)