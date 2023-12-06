package com.test.easypharmaapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


class PharmacyDetailPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pharmacistName = intent.getStringExtra("pharmacistName") ?: "N/A"
        val email = intent.getStringExtra("email") ?: "N/A"
        val pharmacyName = intent.getStringExtra("pharmacyName") ?: "N/A"
        val phoneNumber = intent.getStringExtra("phoneNumber") ?: "N/A"
        val latitude = intent.getStringExtra("pharmacyLatitude") ?: "N/A"
        val longitude = intent.getStringExtra("pharmacyLongitude") ?: "N/A"

        setContent {
            PharmacyDetails(
                pharmacistName = pharmacistName,
                email = email,
                pharmacyName = pharmacyName,
                phoneNumber = phoneNumber,
                pharmacyLatitude = latitude,
                pharmacyLongitude = longitude

            )
        }
    }
}

@Composable
fun PharmacyDetails(pharmacistName: String, email: String, pharmacyName: String, phoneNumber: String, pharmacyLatitude: String, pharmacyLongitude: String) {
  var context= LocalContext.current;
    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pharmacy Details",
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Pharmacy Name: $pharmacyName", style = MaterialTheme.typography.h6)
            Text("Pharmacist Name: $pharmacistName")
            Text("Email: $email")
            Text("Phone Number: $phoneNumber")
            Spacer(modifier = Modifier.height(16.dp))
            val image = painterResource(id = R.drawable.navigate_icon)
            Image(
                painter = image,
                contentDescription = "Navigate",
                modifier = Modifier
                    .size(108.dp)
                    .clickable {
                        val map_intent = Uri.parse("google.navigation:q=$pharmacyLatitude,$pharmacyLongitude")
                        val mapIntent = Intent(Intent.ACTION_VIEW, map_intent)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    }
            )

        }
    }
}
