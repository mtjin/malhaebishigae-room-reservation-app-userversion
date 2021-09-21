package com.mtjin.free_room.views.reservation_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mtjin.free_room.base.BaseViewModel
import com.mtjin.free_room.model.Reservation
import com.mtjin.free_room.data.reservation_detail.source.ReservationDetailRepository
import com.mtjin.free_room.utils.NetworkManager
import com.mtjin.free_room.utils.SingleLiveEvent
import com.mtjin.free_room.utils.getTimestamp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class ReservationDetailViewModel @Inject constructor(
    private val reservationDetailRepository: ReservationDetailRepository,
    private val networkManager: NetworkManager
) : BaseViewModel() {
    var roomId: Long = -1

    private val _selectCalendar = SingleLiveEvent<Unit>()
    private val _networkState = SingleLiveEvent<Unit>()
    private val _goReservation = SingleLiveEvent<Unit>()
    private val _reservationList = MutableLiveData<ArrayList<Reservation>>()

    val selectCalendar: LiveData<Unit> get() = _selectCalendar
    val networkState: LiveData<Unit> get() = _networkState
    val goReservation: LiveData<Unit> get() = _goReservation
    val reservationList: LiveData<ArrayList<Reservation>> get() = _reservationList

    fun showCalendar() {
        _selectCalendar.call()
    }

    fun goReservation() {
        _goReservation.call()
    }

    fun requestReservations(timestamp: Long) {
        if (!networkManager.checkNetworkState()) {
            _networkState.call()
        } else {
            compositeDisposable.add(
                reservationDetailRepository.requestReservations(timestamp, roomId)
                    .map { it.filter { room -> room.endTimestamp > getTimestamp() } }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgress() }
                    .doAfterTerminate { hideProgress() }
                    .subscribe({ reservations ->
                        _reservationList.value = reservations as ArrayList<Reservation>
                    }, {
                        Timber.d(it)
                    })
            )
        }
    }

}