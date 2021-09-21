package com.mtjin.free_room.data.reservation_detail.source.remote

import com.mtjin.free_room.model.Reservation
import io.reactivex.Flowable

interface ReservationDetailRemoteDataSource {
    fun requestReservations(timestamp: Long, roomId: Long): Flowable<List<Reservation>>
}