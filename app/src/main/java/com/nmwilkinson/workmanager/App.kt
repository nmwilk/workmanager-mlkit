package com.nmwilkinson.workmanager

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier.AUTODRAW
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import kotlinx.coroutines.launch
import java.time.Duration
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class App : Application() {

    lateinit var recogniser: DigitalInkRecognizer

    val viewModel: AppViewModel = AppViewModel()

    override fun onCreate() {
        super.onCreate()

        val remoteModelManager = RemoteModelManager.getInstance()
        val autodrawModel = DigitalInkRecognitionModel.builder(AUTODRAW).build()

        viewModel.viewModelScope.launch {
            var isDownloaded = remoteModelManager.isModelDownloaded(autodrawModel).asCoroutine()
            Log.d("WRK", "already got model? $isDownloaded")
            if (isDownloaded == false) {
                isDownloaded = remoteModelManager.download(
                    autodrawModel,
                    DownloadConditions.Builder().build()
                ).asCoroutineSuccess()

                Log.d("WRK", "downloaded $isDownloaded")
            }

            if (isDownloaded == true) {
                Log.d("WRK", "creating recogniser")
                recogniser = DigitalInkRecognition.getClient(
                    DigitalInkRecognizerOptions.builder(autodrawModel).build()
                )
            }

            val instance = WorkManager.getInstance(this@App)

            val periodicRequest =
                PeriodicWorkRequestBuilder<InkWorker>(Duration.ofMinutes(1)).build()

            instance.enqueueUniquePeriodicWork(
                "InkRecognition",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                periodicRequest
            )
        }
    }

    private var random = 0
    fun randomInt(size: Int): Int {
        random = (random + 1) % size
        return random
    }
}

suspend fun <T> Task<T>.asCoroutine(): T? =
    suspendCoroutine { continuation ->
        addOnSuccessListener { continuation.resume(it) }
        addOnFailureListener { continuation.resume(null) }
    }

suspend fun Task<Void>.asCoroutineSuccess(): Boolean =
    suspendCoroutine { continuation ->
        addOnSuccessListener { continuation.resume(true) }
        addOnFailureListener { continuation.resume(false) }
    }
