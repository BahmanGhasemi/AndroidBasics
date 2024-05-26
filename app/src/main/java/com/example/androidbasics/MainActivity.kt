package com.example.androidbasics

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidbasics.ui.theme.AndroidBasicsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 99)

        enableEdgeToEdge()
        setContent {
            AndroidBasicsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            space = 16.dp,
                            alignment = Alignment.CenterVertically
                        )
                    ) {

                        Button(onClick = {

                            Intent(applicationContext, MyService::class.java).also {
                                it.action = MyService.Actions.Start.toString()
                                startService(it)
                            }
                        }) {
                            Text(text = "Start")
                        }

                        Button(onClick = {
                            Intent(applicationContext, MyService::class.java).also {
                                it.action = MyService.Actions.Stop.toString()
                                startService(it)
//                                stopService(it)
                            }

                        }) {
                            Text(text = "Stop")
                        }

                    }
                }
            }
        }
    }
}