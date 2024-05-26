package com.example.androidbasics

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.compose.AsyncImage
import com.example.androidbasics.ui.theme.AndroidBasicsTheme
import java.io.File

class MainActivity : ComponentActivity() {

    private lateinit var workManager: WorkManager
    private val viewmodel by viewModels<CompressionViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        workManager = WorkManager.getInstance(applicationContext)
        setContent {
            val workerResult = viewmodel.workerId?.let {
                workManager.getWorkInfoByIdLiveData(it).observeAsState().value
            }
            LaunchedEffect(key1 = workerResult?.outputData) {
                if (workerResult?.outputData != null) {
                    val filePath =
                        workerResult.outputData.getString(PhotoCompressionWorker.KEY_RESUL_URI)
                    filePath?.let { path ->
                        val bitmap = BitmapFactory.decodeFile(path)
                        viewmodel.updateBitmap(bitmap)
                    }
                }
            }

            AndroidBasicsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        viewmodel.originalUri?.let {
                            println("viewmodel uri:$it")
                            Text(text = "Original Photo")
                            AsyncImage(model = it, contentDescription = null)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        viewmodel.bitmap?.let {
                            println("viewmodel bitmap:${it.asImageBitmap()}")
                            Text(text = "Compressed Photo")
                            Image(bitmap = it.asImageBitmap(), contentDescription = null)
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            else
                intent.getParcelableExtra(Intent.EXTRA_STREAM)

        viewmodel.updateUri(uri)

        val request = OneTimeWorkRequestBuilder<PhotoCompressionWorker>()
            .setInputData(
                workDataOf(
                    PhotoCompressionWorker.KEY_ORIGINAL_URI to uri.toString(),
                    PhotoCompressionWorker.KEY_EXPECTED_SIZE to 1024 * 20L
                )
            )
            /* .setConstraints(
                 Constraints.Builder()
                     .setRequiredNetworkType(NetworkType.CONNECTED)
                     .build()
             )*/
            .build()
        viewmodel.updateWorkerId(request.id)
        workManager.enqueue(request)
    }
}