package com.example.androidbasics

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.example.androidbasics.ui.theme.AndroidBasicsTheme
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidBasicsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        val byteArray = _01_ResourceUri(this@MainActivity)
                        _02_FileUri(this@MainActivity, byteArray!!)
                        _03_ContentUri(this@MainActivity)
                    }
                }
            }
        }
    }
}

fun _01_ResourceUri(context: Context): ByteArray? {
    return context.contentResolver.openInputStream(Uri.parse("android.resource://${context.packageName}/drawable/ic_launcher_background"))
        ?.use {
            it.readBytes()
        }.also {
            println("resource size: ${it?.size}")
        }
}

fun _02_FileUri(context: Context, byteArray: ByteArray) {
    val file = File(context.filesDir, "myImage.jpg")
    FileOutputStream(file).use {
        it.write(byteArray)
    }
    println(file.toURI())
}

@Composable
fun _03_ContentUri(context: Context) {

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            println(uri)
            uri?.let {
                context.contentResolver.openInputStream(it)?.use { reader ->
                    reader.readBytes()
                }.also { bytes ->
                    println("original size:${bytes?.size}")

                    val file = File(context.filesDir, "profile.app")
                    FileOutputStream(file).use { writer ->
                        writer.write(bytes)
                    }

                    println(file.toURI())
                    println("copied size:${file.readBytes().size}")
                }
            }

        }

    Button(onClick = { launcher.launch("image/*") }) {
        Text(text = "Pick an Imasge")
    }
}
