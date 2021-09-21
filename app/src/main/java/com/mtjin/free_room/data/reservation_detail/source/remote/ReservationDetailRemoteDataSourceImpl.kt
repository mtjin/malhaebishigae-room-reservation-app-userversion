package com.mtjin.free_room.data.reservation_detail.source.remote

import com.mtjin.free_room.model.Reservation
import com.mtjin.free_room.utils.BUSINESS_CODE
import com.mtjin.free_room.utils.DATE_TIMESTAMP
import com.mtjin.free_room.utils.RESERVATION
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject

class ReservationDetailRemoteDataSourceImpl @Inject constructor(private val database: DatabaseReference) :
    ReservationDetailRemoteDataSource {
    override fun requestReservations(
        timestamp: Long,
        roomId: Long
    ): Flowable<List<Reservation>> {
        return Flowable.create<List<Reservation>>({ emitter ->
            val reserveListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reservationLists: ArrayList<Reservation> = ArrayList()
                    for (reserveSnapShot: DataSnapshot in snapshot.children) {
                        reserveSnapShot.getValue(Reservation::class.java)?.let {
                            if (it.meetingRoomId == roomId) reservationLists.add(it)
                        }
                    }
                    emitter.onNext(reservationLists)
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
}