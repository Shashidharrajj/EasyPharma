package com.test.easypharmaapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.firebase.database.*import com.test.easypharmaapp.ui.theme.EasyPharmaAppTheme
import com.test.onlinestoreapp.HelperClass
import com.test.onlinestoreapp.HelperClass.calculateDistance
import kotlinx.coroutines.delay

class PharmaciesPage : ComponentActivity() {

    private lateinit var Requestlocation: LocationRequest
    private lateinit var callback_location: LocationCallback
    private lateinit var location_client: FusedLocationProviderClient

    val my_lat = mutableStateOf("")
    val my_lon = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        location_client = LocationServices.getFusedLocationProviderClient(this)
        HelperClass.showProgress(this)

        setContent {
            EasyPharmaAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var db=LocalDatabase(this)
                    val cartMedicineIds = db.get_cart()
                    pharamacies_list(cartMedicineIds,my_lat,my_lon)

                }
            }
            Requestlocation = LocationRequest.create().apply {
                interval = 1000
                fastestInterval = 1000
                priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                maxWaitTime = 10000
            }

            callback_location = object : LocationCallback() {

                override fun onLocationResult(locationResult: LocationResult) {

                    for (location in locationResult.locations) {

                        my_lat.value = location.latitude.toString()
                        my_lon.value = location.longitude.toString()
                        location_client.removeLocationUpdates(callback_location)
                        System.out.println("PNAME"+location.latitude.toString())


                    }
                }
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                location_client.requestLocationUpdates(Requestlocation, callback_location, null)

            }
        }
    }
}

@Composable
fun pharamacies_list(mids: List<String>, my_latitude: MutableState<String>, my_longitude: MutableState<String>) {

    var pharmacies_with_medicines by remember { mutableStateOf<List<Pharmacy>>(listOf()) }
    var context = LocalContext.current;

    LaunchedEffect(key1 = my_latitude.value, key2 = my_longitude.value) {
        if (my_latitude.value.isNotEmpty() && my_longitude.value.isNotEmpty()) {

            search_pharmacies(context, mids) { results ->
                pharmacies_with_medicines = results.map { pharmacy ->

                    pharmacy.distance = calculateDistance(my_latitude.value.toDouble(), my_longitude.value.toDouble(),
                        pharmacy.latitude.toDouble(), pharmacy.longitude.toDouble())
                    pharmacy }.sortedBy { it.distance }


            }

            delay(2000)
            HelperClass.hideProgress()


        }
    }
    LazyColumn {
        pharmacies_with_medicines.forEach { pharmacy ->
            item {
                val distance = if (my_latitude.value.isNotEmpty() && my_longitude.value.isNotEmpty()) {
                    calculateDistance(my_latitude.value.toDouble(), my_longitude.value.toDouble(),
                        pharmacy.latitude.toDouble(), pharmacy.longitude.toDouble())
                } else {
                    0.0
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                    .clickable {
                    val intent = Intent(context, PharmacyDetailPage::class.java)
                    intent.putExtra("pharmacistName", pharmacy.pharmacistName)
                    intent.putExtra("email", pharmacy.email)
                    intent.putExtra("pharmacyName", pharmacy.pharmacyName)
                    intent.putExtra("phoneNumber", pharmacy.phoneNumber)
                    intent.putExtra("pharmacyLatitude", pharmacy.latitude)
                    intent.putExtra("pharmacyLongitude", pharmacy.longitude)

                        context.startActivity(intent)
                },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pharmacy: ${pharmacy.pharmacyName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${"%.0f".format(distance)} km away",
                        fontSize = 12.sp,

                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(pharmacy.medicines) { medicine ->
                MedicineCard(medicine = medicine)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}



@Composable
fun MedicineCard(medicine: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(text = medicine, style = MaterialTheme.typography.subtitle1)
        }
    }
}


fun search_pharmacies(context: Context, medicines: List<String>, callback: (List<Pharmacy>) -> Unit) {
    val db = FirebaseDatabase.getInstance()
    val pharmacist_reference = db.getReference("pharmacists")
    val local_db = LocalDatabase(context)

    pharmacist_reference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val pharmacies = mutableListOf<Pharmacy>()

            snapshot.children.forEach { pharmacist ->
                val pname = pharmacist.child("pharmacyName").getValue(String::class.java) ?: return@forEach
                val pharmacy_latitude = pharmacist.child("latitude").getValue(String::class.java) ?: return@forEach
                val pharmacy_longitude = pharmacist.child("longitude").getValue(String::class.java) ?: return@forEach
                val pharmacist_name = pharmacist.child("name").getValue(String::class.java) ?: return@forEach
                val pharmacy_email = pharmacist.child("email").getValue(String::class.java) ?: return@forEach
                val pharmacy_phone = pharmacist.child("phoneNumber").getValue(String::class.java) ?: return@forEach

                val stock = pharmacist.child("stock").children.mapNotNull { it.key }

                val availableMedicines = stock.intersect(medicines.toSet()).toList()
                if (availableMedicines.isNotEmpty()) {
                    val medicineNames = local_db.get_names_medicines(availableMedicines)
                    pharmacies.add(Pharmacy(pharmacist_name,pharmacy_email,pname,pharmacy_phone, pharmacy_latitude, pharmacy_longitude, medicineNames.values.toList()))
                }
            }

            callback(pharmacies)
        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    })
}


data class Pharmacy(
    val pharmacistName: String,
    val email: String,
    val pharmacyName: String,
    val phoneNumber: String,

    val latitude: String,
    val longitude: String,
    val medicines: List<String>,
    var distance: Double = 0.0
)


