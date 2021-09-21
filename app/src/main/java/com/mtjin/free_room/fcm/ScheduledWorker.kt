package com.mtjin.free_room.fcm

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mtjin.free_room.utils.EXTRA_NOTIFICATION_MESSAGE
import com.mtjin.free_room.utils.EXTRA_NOTIFICATION_TITLE
import com.mtjin.free_room.utils.NotificationUtil
import com.mtjin.free_room.utils.PreferenceManager

class ScheduledWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // 알람 On/Off
        val prefManager = PreferenceManager(applicationContext)
        if (prefManager.alarmSetting) {
            // Get Notification Data
            val title = inputData.getString(EXTRA_NOTIFICATION_TITLE).toString()
            val message = inputData.getString(EXTRA_NOTIFICATION_MESSAGE).toString()
            // FCM 전송
            NotificationUtil(applicationContext).showNotification(
                title,
                message
            )
        }
        return Result.success()

    }
}