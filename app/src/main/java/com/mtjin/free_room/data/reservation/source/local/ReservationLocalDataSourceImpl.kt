package com.mtjin.free_room.data.reservation.source.local

import com.mtjin.free_room.data.reservation.source.local.ReservationLocalDataSource
import com.mtjin.free_room.utils.PreferenceManager
import javax.inject.Inject

class ReservationLocalDataSourceImpl @Inject constructor(private val preferenceManager: PreferenceManager) :
    ReservationLocalDataSource {
    override var registerInput: String
        get() = preferenceManager.registerInput
        set(value) {
            preferenceManager.registerInput = value
        }

    override var contentInput: String
        get() = preferenceManager.contentInput
        set(value) {
            preferenceManager.contentInput = value
        }
}