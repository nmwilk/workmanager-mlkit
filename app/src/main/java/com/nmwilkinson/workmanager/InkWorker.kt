package com.nmwilkinson.workmanager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.mlkit.vision.digitalink.Ink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Math.toRadians
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.cos
import kotlin.math.sin

class InkWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    @SuppressLint("SimpleDateFormat")
    private val dateFormatter = SimpleDateFormat("HH:mm:ss")

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val app = applicationContext as App
        Log.d("WRK", "starting recognition")
        app.viewModel.addString("Processing at ${dateFormatter.format(Date())}")
        inks.entries.forEach { (name, ink) ->
            val recogniseResult = app.recogniser.recognize(ink).asCoroutine()
            val results = recogniseResult?.candidates?.take(3)?.joinToString { it.text }
            app.viewModel.addString("$name recognised as: $results")
        }
        Result.success()
    }

    companion object {
        private val angles = (1..36).map { it * 10 }
        val inks = mapOf(
            "Circle" to Ink.builder().apply {
                addStroke(Ink.Stroke.builder().apply {
                    angles.forEachIndexed { index, i ->
                        val x = (50 + 50 * cos(toRadians(i.toDouble()))).toFloat()
                        val y = (50 + 50 * sin(toRadians(i.toDouble()))).toFloat()
                        addPoint(Ink.Point.create(x, y, index * 100L))
                    }
                }.build())
            }.build(),
            "Basketball" to Ink.builder().apply {
                addStroke(Ink.Stroke.builder().apply {
                    angles.forEachIndexed { index, i ->
                        val x = (50 + 50 * cos(toRadians(i.toDouble()))).toFloat()
                        val y = (50 + 50 * sin(toRadians(i.toDouble()))).toFloat()
                        addPoint(Ink.Point.create(x, y, index * 100L))
                    }
                }.build())
                addStroke(Ink.Stroke.builder().apply {
                    angles.forEachIndexed { index, i ->
                        val x = (50 + 20 * cos(toRadians(i.toDouble()))).toFloat()
                        val y = (50 + 20 * sin(toRadians(i.toDouble()))).toFloat()
                        addPoint(Ink.Point.create(x, y, index * 100L))
                    }
                }.build())
                addStroke(Ink.Stroke.builder().apply {
                    addPoint(Ink.Point.create(0f, 50f, 0))
                    addPoint(Ink.Point.create(100f, 50f, 200))
                }.build())

            }.build(),
            "Olympic Rings" to Ink.builder().apply {
                addStroke(Ink.Stroke.builder().apply {
                    listOf(
                        Point(100, 200),
                        Point(200, 100),
                        Point(300, 100),
                        Point(150, 200),
                        Point(250, 200)
                    ).forEach { p ->
                        angles.forEachIndexed { index, i ->
                            val x = (p.x + 45 * cos(toRadians(i.toDouble()))).toFloat()
                            val y = (p.y + 45 * sin(toRadians(i.toDouble()))).toFloat()
                            addPoint(Ink.Point.create(x, y, index * 100L))
                        }
                    }
                }.build())
            }.build(),
            "Square" to Ink.builder().apply {
                addStroke(Ink.Stroke.builder().apply {
                    addPoint(Ink.Point.create(0f, 0f, 0))
                    addPoint(Ink.Point.create(100f, 0f, 200))
                    addPoint(Ink.Point.create(100f, 100f, 300))
                    addPoint(Ink.Point.create(0f, 100f, 400))
                    addPoint(Ink.Point.create(0f, 0f, 500))
                }.build())
            }.build(),
            "Laptop" to Ink.builder().apply {
                addStroke(Ink.Stroke.builder().apply {
                    addPoint(Ink.Point.create(250f, 250f, 0))
                    addPoint(Ink.Point.create(750f, 250f, 200))
                    addPoint(Ink.Point.create(750f, 500f, 300))
                    addPoint(Ink.Point.create(250f, 500f, 400))
                    addPoint(Ink.Point.create(250f, 250f, 500))
                }.build())
                addStroke(Ink.Stroke.builder().apply {
                    addPoint(Ink.Point.create(250f, 500f, 0))
                    addPoint(Ink.Point.create(100f, 600f, 200))
                    addPoint(Ink.Point.create(900f, 600f, 300))
                    addPoint(Ink.Point.create(750f, 500f, 400))
                }.build())
            }.build()
        )
    }
}