package com.mtjin.free_room.views.reservation


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mtjin.free_room.R
import com.mtjin.free_room.base.BaseViewModel
import com.mtjin.free_room.model.Reservation
import com.mtjin.free_room.data.reservation.source.ReservationRepository
import com.mtjin.free_room.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class ReservationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val networkManager: NetworkManager,
    private val context: Context
) : BaseViewModel() {
    var startHour = 0
    var startMinute = 0

    private var disabledTimeList = ArrayList<Pair<Long, Long>>() //이용 불가능한 시간리스트
    private var startTimestamp: Long = -1 // 시작 시간 타임스탬프
    private var endTimestamp: Long = -1 // 종료 시간 타임스탬프
    private val isAllSelected: Boolean get() = (date.value != "" && startTime.value != "" && endTime.value != "") // 모든 경우의 수 선택 유무
    private val isSelectedUntilDate: Boolean get() = date.value != "" // 날짜까지 선택유무
    private val isSelectedUntilStartTime: Boolean get() = (date.value != "" && startTime.value != "") // 시작시간까지 선택유무
    var roomId: Long = -1

    private val _messageSet = MutableLiveData<MessageSet>()
    private val _selectCalendar = SingleLiveEvent<Unit>()
    private val _selectTime = SingleLiveEvent<String>()
    private val _reserveList = MutableLiveData<ArrayList<Reservation>>()
    private val _successRequestAvailableTime = SingleLiveEvent<Unit>()
    private val _reservationResult = SingleLiveEvent<Boolean>()
    private val _date = MutableLiveData<String>("")
    private val _startTime = MutableLiveData<String>("")
    private val _endTime = MutableLiveData<String>("")
    private val _checkReservationDialog = SingleLiveEvent<Unit>()

    // two-way binding
    var content: MutableLiveData<String> = MutableLiveData("")
    var register: MutableLiveData<String> = MutableLiveData("")

    // get
    val messageSet: LiveData<MessageSet> get() = _messageSet
    val selectCalendar: LiveData<Unit> get() = _selectCalendar
    val selectTime: LiveData<String> get() = _selectTime
    val reservationList: LiveData<ArrayList<Reservation>> get() = _reserveList
    val successRequestAvailableTime: LiveData<Unit> get() = _successRequestAvailableTime
    val reservationResult: LiveData<Boolean> get() = _reservationResult
    val date: LiveData<String> get() = _date
    val startTime: LiveData<String> get() = _startTime
    val endTime: LiveData<String> get() = _endTime
    val checkReservationDialog: LiveData<Unit> get() = _checkReservationDialog


    fun showCalendar() {
        _selectCalendar.call()
    }

    fun showStartTimePicker() {
        if (isSelectedUntilDate) {
            _selectTime.value = context.getString(R.string.start_time_text)
        } else {
            _messageSet.value = MessageSet.DATE_ERROR
        }
    }

    fun showEndTimePicker() {
        when {
            isSelectedUntilStartTime -> {
                _selectTime.value = context.getString(R.string.end_time_text)
            }
            !isSelectedUntilDate -> {
                _messageSet.value = MessageSet.DATE_ERROR
            }
            else -> {
                _messageSet.value = MessageSet.TIME_ERROR
            }
        }
    }

    fun requestAvailableTime(timestamp: Long) {
        initDateTime() // 날짜,시간 초기화
        if (networkManager.checkNetworkState()) {
            compositeDisposable.add(
                reservationRepository.requestAvailableTime(timestamp = timestamp, roomId = roomId)
                    .map { it.filter { room -> room.endTimestamp > getTimestamp() } }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgress() }
                    .doAfterTerminate { hideProgress() }
                    .subscribe({ reservations ->
                        //초기화
                        disabledTimeList.clear()
                        for (reservation in reservations) {
                            disabledTimeList.add(
                                Pair(
                                    reservation.startTimestamp,
                                    reservation.endTimestamp
                                )
                            )
                        }
                        _reserveList.value = reservations as ArrayList<Reservation>
                        _successRequestAvailableTime.call()
                    }, {
                        Timber.d(it)
                    })
            )
        } else {
            _messageSet.value = MessageSet.NETWORK_ERROR
        }
    }

    fun checkReservationAvailable() {
        when {
            !networkManager.checkNetworkState() -> {
                _messageSet.value = MessageSet.NETWORK_ERROR
            }
            !isAllSelected -> {
                _messageSet.value = MessageSet.BOTH_DATE_TIME_EMPTY
            }
            content.value.toString().trim().isEmpty() -> {
                _messageSet.value = MessageSet.CONTENT_EMPTY
            }
            register.value.toString().trim().isEmpty() -> {
                _messageSet.value = MessageSet.REGISTER_EMPTY
            }
            !(checkStartTimeAvailable(startTimestamp) && checkEndTimeAvailable(endTimestamp)) -> {
                _messageSet.value = MessageSet.RESERVATION_FULL_ERROR
            }
            else -> _checkReservationDialog.call()
        }
    }

    fun requestReservation() {
        val reserve = Reservation(
            id = combineTimestamp(
                "${date.value}-${endTime.value}".convertDateFullToTimestamp(),
                getRandomKey()
            ),
            meetingRoomId = roomId,
            dateTimestamp = date.value.toString().convertDateToTimestamp(),
            startTimestamp = startTimestamp,
            endTimestamp = endTimestamp,
            userId = uuid,
            register = register.value.toString().trim(),
            content = content.value.toString().trim()
        )
        compositeDisposable.add(
            reservationRepository.requestReservation(reserve, roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showLottieProgress() }
                .doAfterTerminate { hideLottieProgress() }
                .subscribe({
                    _reservationResult.value = true
                }, {
                    Timber.d(it)
                    _reservationResult.value = false
                })
        )
    }

    fun checkStartTimeAvailable(timestamp: Long): Boolean {
        for (time in disabledTimeList) {
            if (timestamp >= time.first && timestamp < time.second) {
                return false
            }
        }
        startTimestamp = timestamp
        if (timestamp <= getTimestamp()) { // 현재시간보다 이전의 경우
            return false
        }
        return true
    }

    fun checkEndTimeAvailable(timestamp: Long): Boolean {
        for (time in disabledTimeList) {
            if ((timestamp > time.first && timestamp <= time.second) || (startTimestamp <= time.first && timestamp > time.first)) {
                return false
            }
        }
        if (timestamp <= startTimestamp) {
            return false
        }
        endTimestamp = timestamp
        return true
    }

    fun getEditTextInputInfo() {
        if (register.value == "" && content.value == "") {
            register.value = reservationRepository.registerInput
            content.value = reservationRepository.contentInput
        }
    }

    private fun initDateTime() {
        _date.value = ""
        _startTime.value = ""
        _endTime.value = ""
    }

    fun setDate(date: String) {
        _date.value = date
        _startTime.value = ""
        _endTime.value = ""
    }

    fun setStartTime(timestamp: Long, startHour: Int, startMinute: Int) {
        _startTime.value = timestamp.convertTimestampToTime()
        _endTime.value = ""
        this.startHour = startHour
        this.startMinute = startMinute
    }

    fun setEndTime(timestamp: Long) {
        _endTime.value = timestamp.convertTimestampToTime()
    }

    enum class MessageSet {
        CONTENT_EMPTY,
        REGISTER_EMPTY,
        NETWORK_ERROR,
        BOTH_DATE_TIME_EMPTY,
        DATE_ERROR,
        TIME_ERROR,
        RESERVATION_FULL_ERROR
    }
}