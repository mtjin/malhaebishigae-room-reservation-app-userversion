package com.mtjin.free_room.data.reservation_detail.source

import com.mtjin.free_room.model.Reservation
import com.mtjin.free_room.data.reservation_detail.source.remote.ReservationDetailRemoteDataSource
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ReservationDetailRepositoryImpl @Inject constructor(private val reservationDetailRemoteDataSource: ReservationDetailRemoteDataSource) :
    ReservationDetailRepository {
    override fun requestReservations(
        timestamp: Long,
        roomId: Long
    ): Flowable<List<Reservation>> =
        reservationDetailRemoteDataSource.requestReservations(timestamp, roomId)
            .observeOn(Schedulers.io())


}