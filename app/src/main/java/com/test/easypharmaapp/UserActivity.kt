package com.test.easypharmaapp

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
    var searchText by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf("") }
    var isItemSelected by remember { mutableStateOf(false) }
    var medicineCart by remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                backgroundColor = Color.White
            ) {
                Button(
                    onClick = {

                        if (medicineCart.isEmpty()) {
                            Toast.makeText(context, "Medicine cart is empty", Toast.LENGTH_SHORT).show()
                        }
                        else {

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
                value = if (isItemSelected) selectedItem else searchText,
                onValueChange = {
                    searchText = it
                    isItemSelected = false
                },
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            if (!isItemSelected) {
                val searchResults = searchItems(searchText)
                searchResults.forEach { item ->
                    Text(
                        text = item,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                selectedItem = item
                                isItemSelected = true
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Medicine Cart:", style = MaterialTheme.typography.h6)

            Button(
                onClick = {
                    if(selectedItem.isNotEmpty()) {
                        medicineCart = medicineCart + selectedItem
                        selectedItem = ""
                        isItemSelected = false
                    }
                },
                modifier = Modifier.align(Alignment.End),
                enabled = selectedItem.isNotEmpty()
            ) {
                Text("Add to Cart")
            }

            LazyColumn {
                items(medicineCart) { medicine ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = 4.dp
                    ) {
                        Text(
                            text = medicine,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

fun searchItems(query: String): List<String> {
    val medicinesList = listOf("Medicine 1", "Medicine 2", "Medicine 3", "Medicine 4", "Medicine 5", "Medicine 6", "Medicine 7", "Medicine 8")
    return if (query.isEmpty()) {
        listOf()
    } else {
        medicinesList.filter { it.contains(query, ignoreCase = true) }
    }
}
