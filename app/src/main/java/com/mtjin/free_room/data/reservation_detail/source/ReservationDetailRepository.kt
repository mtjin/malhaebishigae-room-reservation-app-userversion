package com.mtjin.free_room.data.reservation_detail.source

import com.mtjin.free_room.model.Reservation
import io.reactivex.Flowable

interface ReservationDetailRepository {

    fun requestReservations(timestamp: Long, roomId: Long): Flowable<List<Reservation>>
}