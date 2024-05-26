package com.example.androidbasics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

class PhotoCompressionWorker(
    private val context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val inputs = params.inputData
            val uri = inputs.getString(KEY_ORIGINAL_URI)
            val expectedSize = inputs.getLong(KEY_EXPECTED_SIZE, 1024 * 20)
            val bytes = applicationContext.contentResolver.openInputStream(Uri.parse(uri))?.use {
                it.readBytes()
            } ?: return@withContext Result.failure()

            val option = BitmapFactory.Options().apply {
                outConfig = Bitmap.Config.ARGB_8888
            }

            val bitmap =
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, option)

            var outputBytes: ByteArray
            var qualityLevel = 100L
            do {
                ByteArrayOutputStream().use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, qualityLevel.toInt(), stream)
                    outputBytes = stream.toByteArray()
                    qualityLevel -= (qualityLevel * 0.1).roundToInt()
                }
            } while (qualityLevel > 5 && outputBytes.size > expectedSize)

            val file = File(context.cacheDir, "${params.id}.jpg")
            file.writeBytes(outputBytes)

            Result.success(workDataOf(KEY_RESUL_URI to file.absolutePath))
        }
    }

    companion object {
        const val KEY_ORIGINAL_URI = "KEY_ORIGINAL_URI"
        const val KEY_EXPECTED_SIZE = "KEY_COMPRESSION_LEVEL"
        const val KEY_RESUL_URI = "KEY_RESUL_URI"
    }
}