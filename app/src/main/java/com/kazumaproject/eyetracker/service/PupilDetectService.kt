package com.kazumaproject.eyetracker.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.kazumaproject.eyetracker.R

class PupilDetectService : LifecycleService() {

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name = "Pupil Detection Service"
        val id = "pupil_detection_service"
        val notifyDescription = "Detect pupil location"

        if (manager.getNotificationChannel(id) == null) {
            val mChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
            mChannel.apply {
                description = notifyDescription
            }
            manager.createNotificationChannel(mChannel)
        }

        val notification = NotificationCompat.Builder(this).apply {
            setContentTitle("title")
            setContentText("text")
            setSmallIcon(R.drawable.ic_stat_name)
        }.build()

        startForeground(1, notification)

        Thread(
            Runnable {
                (0..5).map {
                    Thread.sleep(1000)

                }

                stopForeground(Service.STOP_FOREGROUND_DETACH)

            }).start()

        return START_STICKY
    }
}