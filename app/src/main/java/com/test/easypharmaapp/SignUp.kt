package com.test.easypharmaapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.test.easypharmaapp.ui.theme.EasyPharmaAppTheme
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.test.onlinestoreapp.HelperClass

class SignUp : ComponentActivity() {

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var latitude: String;
    private lateinit var longitude: String;

    fun getLatitude(): String {
        return latitude
    }

    fun getLongitude(): String {
        return longitude
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EasyPharmaAppTheme {
                SignUpScreenCompose()
                FirebaseApp.initializeApp(this)

            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        setupLocationRequest()
        startLocationUpdates()

    }

    private fun setupLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            maxWaitTime = 20000
        }

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {

                for (location in locationResult.locations) {

                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                    fusedLocationClient.removeLocationUpdates(locationCallback)


                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )

        }
    }


    @Composable
    fun SignUpScreenCompose() {
        var context = LocalContext.current;
        var pharmacistName by remember { mutableStateOf("") }
        var pharmacyName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var phoneNumber by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var agreedToTerms by remember { mutableStateOf(false) }


        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.newpharma),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.h4,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(5.dp))

                TextField(
                    value = pharmacistName,
                    onValueChange = { pharmacistName = it },
                    label = { Text("Pharmacist Name") }
                )
                Spacer(modifier = Modifier.height(5.dp))

                TextField(
                    value = pharmacyName,
                    onValueChange = { pharmacyName = it },
                    label = { Text("Pharmacy Name") }
                )
                Spacer(modifier = Modifier.height(5.dp))

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
                Spacer(modifier = Modifier.height(5.dp))

                TextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(5.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(5.dp))

                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(5.dp))

                Row {
                    Checkbox(
                        checked = agreedToTerms,
                        onCheckedChange = { agreedToTerms = it }
                    )
                    Text(
                        text = "By accepting you are agree to our terms of service.",
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))

                Button(
                    onClick = {

                        val latitude = (context as SignUp).getLatitude()
                        val longitude = (context as SignUp).getLongitude()
                        if (email.isNotBlank() && password.isNotBlank() && pharmacistName.isNotBlank() && pharmacyName.isNotBlank() && agreedToTerms) {
                            doSignUpAuth(
                                context,
                                email,
                                password,
                                pharmacistName,
                                pharmacistName,
                                phoneNumber,
                                latitude,
                                longitude
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Please fill all fields and agree to terms.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)
                ) {
                    Text(
                        text = "Sign Up",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun SignUpScreenPreview() {
        EasyPharmaAppTheme {
            SignUpScreenCompose()
        }


    }


    fun doSignUpAuth(
        context: Context,
        email: String,
        password: String,
        pharmacistName: String,
        pharmacyName: String,
        phoneNumber: String,
        latitude: String,
        longitude: String
    ) {
        val auth = FirebaseAuth.getInstance()
        HelperClass.showProgress(context)
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val uid = task.result?.user?.uid
                savePharmacistDetails(
                    context,
                    uid,
                    pharmacistName,
                    pharmacyName,
                    email,
                    phoneNumber,
                    latitude,
                    longitude
                )
            } else {

                HelperClass.hideProgress()
                Toast.makeText(
                    context,
                    "Signup failed: ${task.exception?.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun savePharmacistDetails(
        context: Context,
        uid: String?,
        pharmacistName: String,
        pharmacyName: String,
        email: String,
        phoneNumber: String,
        latitude: String,
        longitude: String
    ) {
        uid?.let {
            val database = FirebaseDatabase.getInstance().reference
            val pharmacistDetails = Pharmacist(
                uid,
                pharmacistName,
                pharmacyName,
                email,
                phoneNumber,
                latitude,
                longitude
            )

            database.child("pharmacists").child(uid).setValue(pharmacistDetails)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        HelperClass.hideProgress()
                        (context as? Activity)?.finish()
                        Toast.makeText(context, "Register Succesfully!", Toast.LENGTH_LONG).show()


                    } else {
                        HelperClass.hideProgress()
                    }
                }
        }
    }

}
data class Pharmacist(
    val uid: String="",
    val name: String="",
    val pharmacyName: String="",
    val email: String="",
    val phoneNumber: String="",
    val latitude: String="",
    val longitude: String=""

)



