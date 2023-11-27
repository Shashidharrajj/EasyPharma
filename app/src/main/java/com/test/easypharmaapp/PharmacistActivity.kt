package com.test.easypharmaapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase
import com.test.easypharmaapp.ui.theme.EasyPharmaAppTheme
import com.test.onlinestoreapp.HelperClass

class PharmacistActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EasyPharmaAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    PharmacistScreen()
                }
            }
        }
    }
}

@Composable
fun PharmacistScreen() {
    var context= LocalContext.current;
    val image: Painter = painterResource(id = R.drawable.newpharma)
    val pharmacyName = "XYZ Pharmacy"
    val medicines = listOf(
        Medicine("1", "Aspirin"),
        Medicine("2", "Paracetamol"),
    )
    var showDialog by remember { mutableStateOf(false) }
    var selectedMedicine by remember { mutableStateOf<Medicine?>(null) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Action") },
            text = { Text("Are you sure you want to add ${selectedMedicine?.name} to your stock?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false

                    val shared_prefs = context.getSharedPreferences("Easypharma", Context.MODE_PRIVATE)
                    val uid= shared_prefs.getString("uid", null)

                   selectedMedicine?.let {
                        if (uid != null) {
                            add_med_to_pharmacist_stock(context,uid, it.id,1)
                        }
                    }






                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Image(
            painter = image,
            contentDescription = "Pharmacy Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
        Text(
            text = pharmacyName,
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
        )
        MedicineList(medicines) { medicine ->
            selectedMedicine = medicine
            showDialog = true
        }
    }
}


@Composable
fun MedicineList(medicines: List<Medicine>, onItemClick: (Medicine) -> Unit) {
    LazyColumn {
        items(medicines) { medicine ->
            MedicineItem(medicine, onClick = { onItemClick(medicine) })
        }
    }
}

@Composable
fun MedicineItem(medicine: Medicine, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = 4.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${medicine.name}",
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}




data class Medicine(
    val id: String,
    val name: String
)


fun add_med_to_pharmacist_stock(context: Context,pid: String, mid: String, quantity: Int) {
    HelperClass.showProgress(context)
    val database_pharmacist = FirebaseDatabase.getInstance()
    val pharmacist_stock_ref = database_pharmacist.getReference("/pharmacists/$pid/stock/$mid")

    pharmacist_stock_ref.setValue(quantity).addOnSuccessListener {

            Toast.makeText(context,"Successfully Added to stock",Toast.LENGTH_SHORT).show()

            HelperClass.hideProgress()

        }
        .addOnFailureListener {
            Toast.makeText(context,"Failed to Added to stock",Toast.LENGTH_SHORT).show()
            HelperClass.hideProgress()


        }
}