package com.test.onlinestoreapp

import android.app.ProgressDialog
import android.content.Context

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
}
