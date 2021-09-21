package com.mtjin.free_room.data.reservation.source

import com.mtjin.free_room.model.Reservation
import com.mtjin.free_room.data.reservation.source.local.ReservationLocalDataSource
import com.mtjin.free_room.data.reservation.source.remote.ReservationRemoteDataSource
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(
    private val reservationRemoteDataSource: ReservationRemoteDataSource,
    private val reservationLocalDataSource: ReservationLocalDataSource
) :
    ReservationRepository {
    override fun requestAvailableTime(
        timestamp: Long,
        roomId: Long
    ): Flowable<List<Reservation>> =
        reservationRemoteDataSource.requestAvailableTime(timestamp, roomId)
            .observeOn(Schedulers.io())

    override fun requestReservation(reservation: Reservation, roomId: Long): Completable =
        reservationRemoteDataSource.requestReservation(reservation, roomId)
            .observeOn(Schedulers.io()).doOnComplete {
                registerInput = reservation.register
                contentInput = reservation.content
            }

    override var registerInput: String
        get() = reservationLocalDataSource.registerInput
        set(value) {
            reservationLocalDataSource.registerInput = value
        }
    override var contentInput: String
        get() = reservationLocalDataSource.contentInput
        set(value) {
            reservationLocalDataSource.contentInput = value
        }
}