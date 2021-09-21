package com.mtjin.free_room.data.reservation.source.remote

import com.mtjin.free_room.model.Reservation
import io.reactivex.Completable
import io.reactivex.Flowable

interface ReservationRemoteDataSource {
    fun requestAvailableTime(timestamp: Long, roomId: Long): Flowable<List<Reservation>>

    fun requestReservation(reservation: Reservation, roomId: Long): Completable

    fun sendNotification(reservation: Reservation)
}