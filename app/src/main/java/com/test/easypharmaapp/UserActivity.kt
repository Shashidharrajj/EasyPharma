package com.test.easypharmaapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserActivityPageCompose()
        }
    }
}

@Composable
fun UserActivityPageCompose() {
    var context= LocalContext.current;
    var search_text by remember { mutableStateOf("") }
    var selected_item by remember { mutableStateOf("") }
    var isSelected by remember { mutableStateOf(false) }
    var medicine_cart by remember { mutableStateOf(listOf<Medicine>()) }
    LaunchedEffect(Unit) {

        medicine_cart = loadCartItems(context)
    }
    Scaffold(
        bottomBar = {
            BottomAppBar(
                backgroundColor = Color.White
            ) {
                Button(
                    onClick = {

                        if (medicine_cart.isEmpty()) {
                            Toast.makeText(context, "Medicine cart is empty", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            val intent = Intent(context, PharmaciesPage::class.java)
                            context.startActivity(intent)
                        }
                    },

                    modifier = Modifier.fillMaxWidth().padding(8.dp),colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)) {
                    Text("Search Pharmacies")

                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("Search Medicines", style = MaterialTheme.typography.h5)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = if (isSelected) selected_item else search_text,
                onValueChange = {
                    search_text = it
                    isSelected = false
                },
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            if (!isSelected) {
                val searchResults = search_items(context, search_text)
                searchResults.forEach { medicine ->
                    Text(
                        text = medicine.name,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                val db_helper = LocalDatabase(context)
                                db_helper.addToCart(medicine.id, medicine.name)
                                medicine_cart = medicine_cart + medicine
                                Toast.makeText(context,"Added to cart",Toast.LENGTH_SHORT).show()
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Medicine Cart:", style = MaterialTheme.typography.h6)

            LazyColumn {
                items(medicine_cart) { medicine ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = medicine.name,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 16.sp
                            )

                            Icon(

                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier
                                    .padding(16.dp)
                                    .clickable {

                                        val localDatabase = LocalDatabase(context)
                                        localDatabase.deleteFromCart(medicine.id)
                                        medicine_cart = medicine_cart.filter { it.id != medicine.id }
                                        Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show()


                                    }
                            )
                        }
                    }
                }
            }


        }
    }
}

fun search_items(context: Context, query: String): List<Medicine> {
    val db_helper = LocalDatabase(context)
    val medicine_list = db_helper.get_all_medicines()

    return if (query.isEmpty()) {
        listOf()
    } else {
        medicine_list.filter { it.name.contains(query, ignoreCase = true) }
    }
}


fun loadCartItems(context: Context): List<Medicine> {
    val db_helper = LocalDatabase(context)
    return db_helper.get_cart_items()
}

