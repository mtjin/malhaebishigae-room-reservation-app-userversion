package com.mtjin.free_room.data.reservation.source

import com.mtjin.free_room.model.Reservation
import io.reactivex.Completable
import io.reactivex.Flowable

interface ReservationRepository {
    fun requestAvailableTime(timestamp: Long, roomId: Long): Flowable<List<Reservation>>

    fun requestReservation(reservation: Reservation, roomId: Long): Completable

    var registerInput: String
    var contentInput: String
}