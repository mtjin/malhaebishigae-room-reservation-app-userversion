package com.mtjin.free_room.fcm

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mtjin.free_room.R
import com.mtjin.free_room.utils.EXTRA_NOTIFICATION_MESSAGE
import com.mtjin.free_room.utils.EXTRA_NOTIFICATION_TITLE
import com.mtjin.free_room.views.main.MainActivity
import timber.log.Timber
import java.util.concurrent.TimeUnit


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("onMessageReceived: ")
        val to = remoteMessage.to.toString()
        val title = remoteMessage.data["title"].toString()
        val body = remoteMessage.data["body"].toString()
        if (remoteMessage.data["isScheduled"] == "false") { // 즉시 전송
            sendNotification(
                to,
                title,
                body
            )
        } else { // 예약전송
            // This is Scheduled Notification, Schedule it
            val scheduledTime = remoteMessage.data["scheduledTime"]
            scheduleAlarm(scheduledTime.toString(), title, message = body)
        }
    }

    private fun sendNotification(to: String, title: String, body: String) {
        Timber.d("sendNotification: ")
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_launcher_background
                )
            )
            .setSmallIcon(R.drawable.ic_calendar)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setContentIntent(pendingIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    private fun scheduleAlarm(
        scheduledTime: String,
        title: String,
        message: String
    ) {

        val alarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent =
            Intent(applicationContext, NotificationBroadcastReceiver::class.java).let { intent ->
                intent.putExtra(EXTRA_NOTIFICATION_TITLE, title)
                intent.putExtra(EXTRA_NOTIFICATION_MESSAGE, message)
                PendingIntent.getBroadcast(applicationContext, 0, intent, 0)
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                scheduledTime.toLong() - TimeUnit.MINUTES.toMillis(15),// 15분전알림
                alarmIntent
            )
        } else {
            alarmMgr.setExact(
                AlarmManager.RTC_WAKEUP,
                scheduledTime.toLong() - TimeUnit.MINUTES.toMillis(15),// 15분전알림
                alarmIntent
            )
        }
    }

    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
    }
}