package com.mtjin.free_room.views.reservation

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mtjin.free_room.R
import com.mtjin.free_room.base.BaseFragment
import com.mtjin.free_room.databinding.FragmentReservationBinding
import com.mtjin.free_room.utils.*
import com.mtjin.free_room.views.dialog.BottomDialogFragment
import com.mtjin.free_room.views.main.MainActivity
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_reservation.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class ReservationFragment :
    BaseFragment<FragmentReservationBinding>(R.layout.fragment_reservation),
    TimePickerDialog.OnTimeSetListener {
    private val safeArgs: ReservationFragmentArgs by navArgs()
    private lateinit var reserveAdapter: ReservationAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ReservationViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ReservationViewModel::class.java)
    }

    override fun init() {
        (activity as MainActivity).mainComponent.inject(this)
        binding.vm = viewModel
        processIntent()
        initView()
        initAdapter()
        initViewModelCallback()
    }

    private fun initView() {
        viewModel.getEditTextInputInfo()
        binding.run {
            etRegister.doOnTextChanged { _, _, _, _ ->
                tilRegister.error = null
            }
            etContent.doOnTextChanged { _, _, _, _ ->
                tilContent.error = null
            }
        }
    }

    private fun initViewModelCallback() {
        with(viewModel) {
            selectCalendar.observe(this@ReservationFragment, Observer {
                showCalendarPickerDialog()
            })

            selectTime.observe(this@ReservationFragment, Observer {
                showTimePickerDialog(it)
            })

            isLottieLoading.observe(this@ReservationFragment, Observer { loadingState ->
                when (loadingState) {
                    true -> showProgressDialog()
                    false -> hideProgressDialog()
                }
            })

            checkReservationDialog.observe(this@ReservationFragment, Observer {
                val dialog = BottomDialogFragment("예약하시겠습니까?") { yes ->
                    if (yes) viewModel.requestReservation()
                }
                dialog.show(requireActivity().supportFragmentManager, dialog.tag)
            })

            messageSet.observe(this@ReservationFragment, Observer {
                when (messageSet.value) {
                    ReservationViewModel.MessageSet.NETWORK_ERROR -> showInfoSnackBar(
                        getString(
                            R.string.network_error_msg
                        )
                    )
                    ReservationViewModel.MessageSet.CONTENT_EMPTY -> {
                        binding.tilContent.error = getString(R.string.enter_conference_name_msg)
                        binding.etContent.requestFocus()
                    }
                    ReservationViewModel.MessageSet.REGISTER_EMPTY -> {
                        binding.tilRegister.error = getString(R.string.enter_officer_msg)
                        binding.etRegister.requestFocus()
                    }
                    ReservationViewModel.MessageSet.BOTH_DATE_TIME_EMPTY -> showInfoSnackBar(
                        getString(
                            R.string.select_both_date_time_msg
                        )
                    )
                    ReservationViewModel.MessageSet.DATE_ERROR -> showInfoSnackBar(getString(R.string.select_date_first_msg))
                    ReservationViewModel.MessageSet.TIME_ERROR -> showInfoSnackBar(
                        getString(R.string.select_start_time_first_msg)
                    )
                    ReservationViewModel.MessageSet.RESERVATION_FULL_ERROR
                    -> showInfoSnackBar(getString(R.string.already_reserve_full_msg))
                }

            })

            reservationResult.observe(this@ReservationFragment, Observer { result ->
                if (result) {
                    showPositiveSnackBar(getString(R.string.reserve_complete_msg))
                    val direction: NavDirections =
                        ReservationFragmentDirections.actionReservationFragmentToBottomNav2()
                    findNavController().navigate(direction)
                } else {
                    showNegativeSnackBar(getString(R.string.reserve_fail_msg))
                }
            })

            successRequestAvailableTime.observe(this@ReservationFragment, Observer {
                binding.tvAvailableTime.visibility = View.VISIBLE
                binding.rvTimes.visibility = View.VISIBLE
            })
        }
    }

    private fun initAdapter() {
        reserveAdapter = ReservationAdapter()
        binding.rvTimes.adapter = reserveAdapter
    }

    private fun processIntent() {
        safeArgs.room.run {
            binding.item = this
            viewModel.roomId = id
            toolbar.title = getString(R.string.meeting_room_reservation_input_text) + name
        }
    }

    private fun showCalendarPickerDialog() {
        val c = Calendar.getInstance()
        val dialog = DatePickerDialog(
            thisContext,
            OnDateSetListener { _, _year, _month, _dayOfMonth ->
                val year = _year.toString()
                val month =
                    if (_month + 1 < 10) "0" + (_month + 1) else (_month + 1).toString()
                val date =
                    if (_dayOfMonth < 10) "0$_dayOfMonth" else _dayOfMonth.toString()
                val pickedDate = "$year-$month-$date"
                Timber.d("PickedDate:: $pickedDate") //2020-07-28
                viewModel.requestAvailableTime(timestamp = pickedDate.convertDateToTimestamp())
                viewModel.setDate(pickedDate)
            }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.MONTH]
        )
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()
    }

    private fun showTimePickerDialog(dialogTitle: String) {
        val startHour: Int
        val startMinute: Int
        if (dialogTitle == getString(R.string.start_time_text)) {
            getTimestamp().let {
                startHour = it.convertTimestampToHour()
                startMinute = it.convertTimestampToMinute()
            }
        } else {
            startHour = viewModel.startHour
            startMinute = viewModel.startMinute
        }
        TimePickerDialog.newInstance(
            this@ReservationFragment,
            startHour,
            startMinute,
            true
        ).apply {
            title = dialogTitle
            enableSeconds(false)
        }.show(requireActivity().fragmentManager, "TimePickerDialog")
    }

    override fun onTimeSet(_view: TimePickerDialog?, _hourOfDay: Int, _minute: Int, _second: Int) {
        val hourOfDay: String = _hourOfDay.toString().convertHourDoubleDigit() //두자리수의 String 으로 변환
        val minute: String = _minute.toString().convertMinuteDoubleDigit()
        val timestamp =
            (viewModel.date.value + '-' + hourOfDay + ':' + minute).convertDateFullToTimestamp()
        when {
            timestamp <= getTimestamp() -> showInfoSnackBar(getString(R.string.select_after_current_time_text))

            _view?.title.toString() == getString(R.string.start_time_text) -> {// 시작시간 선택시
                if (viewModel.checkStartTimeAvailable(timestamp)) {
                    viewModel.setStartTime(timestamp, _hourOfDay, _minute)
                    if (_hourOfDay < 23 && viewModel.checkEndTimeAvailable(timestamp.convertNextHourTimestamp())) { // 현재시간보다 한시간 뒤 예약가능하고 시작시간이 23시가 아니면 끝시간 자동 세팅
                        viewModel.setEndTime(timestamp.convertNextHourTimestamp())
                    }
                } else {
                    showInfoSnackBar(getString(R.string.select_another_time_msg))
                }
            }
            else -> { // 종료시간 선택시
                if (viewModel.checkEndTimeAvailable(timestamp)) viewModel.setEndTime(timestamp)
                else showInfoSnackBar(getString(R.string.select_another_time_msg))
            }
        }
    }
}