package com.test.easypharmaapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.test.easypharmaapp.ui.theme.EasyPharmaAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

        }
        checkLocationPermission()
        setContent {
            EasyPharmaAppTheme {

                MainScreenContent()
            }
        }
    }

    private fun checkLocationPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) -> {

            }
            else -> {

                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}

@Composable
fun MainScreenContent() {

    var context= LocalContext.current;
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.newpharma),
            contentDescription = "Image",
            modifier = Modifier.size(200.dp).align(Alignment.Center)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {

            Text(
                text = "Select your account type",
                style = MaterialTheme.typography.h6,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Button(onClick = {


                val intent = Intent(context, UserActivity::class.java)
                context.startActivity(intent)

            }) {
                Text("Search Medicine Pharmacy ")
            }

            Button(onClick = {

                val intent = Intent(context, Login::class.java)
                context.startActivity(intent)


            }) {
                Text("Pharmacist (seller)")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EasyPharmaAppTheme {
        MainScreenContent()
    }
}