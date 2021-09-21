package com.mtjin.free_room.views.my_meeting_room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mtjin.free_room.base.BaseViewModel
import com.mtjin.free_room.model.MyMeetingRoom
import com.mtjin.free_room.data.my_meeting_room.source.MyMeetingRoomRepository
import com.mtjin.free_room.utils.NetworkManager
import com.mtjin.free_room.utils.SingleLiveEvent
import com.mtjin.free_room.utils.getTimestamp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class MyMeetingRoomViewModel @Inject constructor(
    private val myMeetingRoomRepository: MyMeetingRoomRepository,
    private val networkManager: NetworkManager
) : BaseViewModel() {
    private val _myMeetingRoomList = MutableLiveData<ArrayList<MyMeetingRoom>>()
    private val _myMeetingRoomListEmpty = SingleLiveEvent<Boolean>()
    private val _networkState = SingleLiveEvent<Unit>()
    private val _cancelResultMsg = SingleLiveEvent<Boolean>()

    val myMeetingRoomList: LiveData<ArrayList<MyMeetingRoom>> get() = _myMeetingRoomList
    val myMeetingRoomListEmpty: LiveData<Boolean> get() = _myMeetingRoomListEmpty
    val networkState: LiveData<Unit> get() = _networkState
    val cancelResultMsg: LiveData<Boolean> get() = _cancelResultMsg

    fun requestMyMeetingRooms() {
        if (networkManager.checkNetworkState()) {
            compositeDisposable.add(
                myMeetingRoomRepository.requestMyMeetingRooms()
                    .map { it.filter { room -> room.endTimestamp > getTimestamp() } }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        showProgress()
                    }
                    .subscribe({ myRooms ->
                        _myMeetingRoomList.value = myRooms as ArrayList<MyMeetingRoom>
                        _myMeetingRoomListEmpty.value = myRooms.isNullOrEmpty()
                        hideProgress() // Flowable + 파이어베이스 비동기 처리 때문에 여기다가 둠
                    }, {
                        Timber.d(it)
                    })
            )
        } else {
            compositeDisposable.add(
                myMeetingRoomRepository.requestLocalMyMeetingRooms()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        showProgress()
                    }
                    .doAfterTerminate { hideProgress() }
                    .subscribe({ myRooms ->
                        _myMeetingRoomList.value = myRooms as ArrayList<MyMeetingRoom>
                        _myMeetingRoomListEmpty.value = myRooms.isNullOrEmpty()
                        hideProgress()
                    }, {
                        Timber.d(it)
                    })
            )
            _networkState.call()
        }
    }

    fun cancelReservation(myMeetingRoom: MyMeetingRoom) {
        if (networkManager.checkNetworkState()) {
            compositeDisposable.add(
                myMeetingRoomRepository.cancelReservation(myMeetingRoom)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showLottieProgress() }
                    .doAfterTerminate { hideLottieProgress() }
                    .subscribe({
                        _cancelResultMsg.value = true
                    }, {
                        Timber.d(it)
                        _cancelResultMsg.value = false
                    })
            )
        } else {
            _networkState.call()
        }
    }

}