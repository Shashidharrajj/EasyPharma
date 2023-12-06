package com.test.onlinestoreapp

import android.app.ProgressDialog
import android.content.Context
import kotlin.math.*


object HelperClass {

    private var pDialog: ProgressDialog? = null

    fun showProgress(context: Context) {
        pDialog?.dismiss()
        pDialog = ProgressDialog(context)
        pDialog?.setMessage("Please wait...")
        pDialog?.setTitle("Easy Pharma App")
        pDialog?.setCancelable(false)
        pDialog?.show()
    }

    fun hideProgress() {
        pDialog?.dismiss()
        pDialog = null
    }


    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371 // Radius of the earth in kilometers
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(lonDistance / 2) * sin(lonDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c // Returns the distance in kilometers
    }

}
