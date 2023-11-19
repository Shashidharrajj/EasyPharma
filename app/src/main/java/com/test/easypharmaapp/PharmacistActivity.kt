package com.test.easypharmaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.test.easypharmaapp.ui.theme.EasyPharmaAppTheme

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
    val image: Painter = painterResource(id = R.drawable.newpharma)
    val pharmacyName = "XYZ Pharmacy"
    val medicines = listOf(
        Medicine("1", "Aspirin"),
        Medicine("2", "Paracetamol"),
    )

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
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
        MedicineList(medicines)
    }
}

@Composable
fun MedicineList(medicines: List<Medicine>) {
    LazyColumn {
        items(medicines) { medicine ->
            MedicineItem(medicine) {
            }
        }
    }
}

@Composable
fun MedicineItem(medicine: Medicine, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable(onClick = onClick),
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
                text = "${medicine.name} ",
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}





data class Medicine(
    val id: String,
    val name: String
)
