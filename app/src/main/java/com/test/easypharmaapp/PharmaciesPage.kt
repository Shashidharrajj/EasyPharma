package com.test.easypharmaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.database.*import com.test.easypharmaapp.ui.theme.EasyPharmaAppTheme

class PharmaciesPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EasyPharmaAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    pharamacies_list()
                }
            }
        }
    }
}

@Composable
fun pharamacies_list() {
    var pharmacies_with_medicines by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        search_pharmacies(listOf("1", "2")) { results ->
            pharmacies_with_medicines = results
        }
    }

    LazyColumn {
        pharmacies_with_medicines.forEach { (pharmacist, medicines) ->
            item {
                Text(text = "Pharmacist $pharmacist", style = MaterialTheme.typography.h6)
            }
            items(medicines) { medicine ->
                Text(text = medicine)
            }
        }
    }
}

fun search_pharmacies(medicines: List<String>, callback: (Map<String, List<String>>) -> Unit) {
    val db = FirebaseDatabase.getInstance()
    val pharmacist_reference = db.getReference("pharmacists")

    pharmacist_reference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val results = mutableMapOf<String, MutableList<String>>()

            snapshot.children.forEach { pharmacist ->
                val pname = pharmacist.child("name").getValue(String::class.java) ?: return@forEach
                val stock = pharmacist.child("stock").children.mapNotNull { it.key }

                val availableMedicines = stock.intersect(medicines.toSet()).toList()
                if (availableMedicines.isNotEmpty()) {
                    results[pname] = availableMedicines as MutableList<String>
                }
            }

            callback(results)
        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    })
}