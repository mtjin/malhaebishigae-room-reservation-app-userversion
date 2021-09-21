package com.mtjin.free_room.data.reservation.source.remote

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.mtjin.free_room.model.Reservation
import com.mtjin.free_room.fcm.NotificationBroadcastReceiver
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.mtjin.free_room.utils.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ReservationRemoteDataSourceImpl @Inject constructor(
    private val database: DatabaseReference,
    private val context: Context
) :
    ReservationRemoteDataSource {
    override fun requestAvailableTime(
        timestamp: Long,
        roomId: Long
    ): Flowable<List<Reservation>> {
        return Flowable.create<List<Reservation>>({ emitter ->
            val reserveListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reservationInfoList = ArrayList<Reservation>()
                    for (reserveSnapShot: DataSnapshot in snapshot.children) {
                        reserveSnapShot.getValue(Reservation::class.java)?.let {
                            if (it.meetingRoomId == roomId) reservationInfoList.add(it)
                        }
                    }
                    emitter.onNext(reservationInfoList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    emitter.onError(databaseError.toException())
                }
            }
            database.child(RESERVATION).child(BUSINESS_CODE)
                .orderByChild(DATE_TIMESTAMP).equalTo(timestamp.toDouble())
                .addValueEventListener(reserveListener)
            emitter.setCancellable {
                database.child(RESERVATION).child(BUSINESS_CODE)
                    .removeEventListener(reserveListener)
            }
        }, BackpressureStrategy.BUFFER)
    }

    override fun requestReservation(reservation: Reservation, roomId: Long): Completable {
        return Completable.create { emitter ->
            database.child(RESERVATION).child(BUSINESS_CODE)
                .child(reservation.id.toString()).setValue(reservation)
                .addOnSuccessListener {
                    sendNotification(reservation)
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    override fun sendNotification(reservation: Reservation) {
        val title = reservation.content
        val message = convertTimeToFcmMessage(
            date = reservation.dateTimestamp,
            startTime = reservation.startTimestamp
        )
        val scheduledTime = reservation.startTimestamp
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent =
            Intent(context, NotificationBroadcastReceiver::class.java).let { intent ->
                intent.putExtra(EXTRA_NOTIFICATION_TITLE, title)
                intent.putExtra(EXTRA_NOTIFICATION_MESSAGE, message)
                PendingIntent.getBroadcast(context, 0, intent, 0)
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                scheduledTime - TimeUnit.MINUTES.toMillis(15),// 15분전알림
                alarmIntent
            )
        } else {
            alarmMgr.setExact(
                AlarmManager.RTC_WAKEUP,
                scheduledTime - TimeUnit.MINUTES.toMillis(15),// 15분전알림
                alarmIntent
            )
        }
    }
}