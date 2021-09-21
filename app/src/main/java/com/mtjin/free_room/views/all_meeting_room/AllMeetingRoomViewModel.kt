package com.mtjin.free_room.views.all_meeting_room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mtjin.free_room.base.BaseViewModel
import com.mtjin.free_room.model.MeetingRoom
import com.mtjin.free_room.data.all_meeting_room.source.AllMeetingRoomRepository
import com.mtjin.free_room.utils.NetworkManager
import com.mtjin.free_room.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class AllMeetingRoomViewModel @Inject constructor(
    private val allMeetingRoomRepository: AllMeetingRoomRepository,
    private val networkManager: NetworkManager
) : BaseViewModel() {
    private val _meetingRoomList = MutableLiveData<ArrayList<MeetingRoom>>()
    private val _networkState = SingleLiveEvent<Unit>()

    val meetingRoomList: LiveData<ArrayList<MeetingRoom>> get() = _meetingRoomList
    val networkState: LiveData<Unit> get() = _networkState

    fun requestAllRooms() {
        if (networkManager.checkNetworkState()) {
            compositeDisposable.add(
                allMeetingRoomRepository.requestAllMeetingRooms()
                    .map { it.filter { room -> !room.isDisabled } }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgress() }
                    .doAfterTerminate { hideProgress() }
                    .subscribe({ rooms ->
                        _meetingRoomList.value = rooms as ArrayList<MeetingRoom>?
                    }, {
                        Timber.d(it)
                    })
            )
        } else {
            _networkState.call()
            compositeDisposable.add(
                allMeetingRoomRepository.requestLocalAllMeetingRooms()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgress() }
                    .doAfterTerminate { hideProgress() }
                    .subscribe({ rooms ->
                        _meetingRoomList.value = rooms as ArrayList<MeetingRoom>?
                    }, {
                        Timber.d(it)
                    })
            )
        }
    }

    fun requestSearchRooms(roomName: String) {
        if (networkManager.checkNetworkState()) {
            compositeDisposable.add(
                allMeetingRoomRepository.requestSearchRooms(roomName)
                    .map { it.filter { room -> !room.isDisabled } }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ rooms ->
                        _meetingRoomList.value = rooms as ArrayList<MeetingRoom>?
                    }, {
                        Timber.d(it)
                    })
            )
        } else {
            _networkState.call()
        }
    }
}