package com.mtjin.free_room.views.reservation_detail

import android.app.DatePickerDialog
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mtjin.free_room.R
import com.mtjin.free_room.base.BaseFragment
import com.mtjin.free_room.databinding.FragmentReservationDetailBinding
import com.mtjin.free_room.utils.convertCurrentTimestampToDateTimestamp
import com.mtjin.free_room.utils.convertDateToTimestamp
import com.mtjin.free_room.utils.convertTimestampToDate
import com.mtjin.free_room.utils.getTimestamp
import com.mtjin.free_room.views.main.MainActivity
import kotlinx.android.synthetic.main.fragment_reservation_detail.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ReservationDetailFragment :
    BaseFragment<FragmentReservationDetailBinding>(R.layout.fragment_reservation_detail) {
    private val meetingRoomSafeArgs: ReservationDetailFragmentArgs by navArgs()
    private lateinit var reservationDetailAdapter: ReservationDetailAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ReservationDetailViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ReservationDetailViewModel::class.java)
    }

    override fun init() {
        (activity as MainActivity).mainComponent.inject(this)
        binding.vm = viewModel
        processIntent()
        initAdapter()
        initViewModelCallback()
        requestReservations()
    }

    private fun requestReservations() {
        binding.tvSelectedDate.text = getTimestamp().convertTimestampToDate()
        viewModel.requestReservations(getTimestamp().convertCurrentTimestampToDateTimestamp())
    }

    private fun initViewModelCallback() {
        with(viewModel) {
            selectCalendar.observe(this@ReservationDetailFragment, Observer {
                val c = Calendar.getInstance()
                val dialog = DatePickerDialog(
                    thisContext,
                    DatePickerDialog.OnDateSetListener { _, _year, _month, _dayOfMonth ->
                        val year = _year.toString()
                        val month =
                            if (_month + 1 < 10) "0" + (_month + 1) else (_month + 1).toString()
                        val date =
                            if (_dayOfMonth < 10) "0$_dayOfMonth" else _dayOfMonth.toString()
                        val pickedDate = "$year-$month-$date"
                        Timber.d("PickedDate:: $pickedDate") //2020-07-28
                        binding.tvSelectedDate.text = pickedDate
                        viewModel.requestReservations(pickedDate.convertDateToTimestamp())
                    }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.MONTH]
                )
                dialog.datePicker.minDate = System.currentTimeMillis() - 1000
                dialog.show()
            })

            networkState.observe(this@ReservationDetailFragment, Observer {
                showInfoSnackBar(getString(R.string.network_error_msg))
            })

            goReservation.observe(this@ReservationDetailFragment, Observer {
                val directions =
                    ReservationDetailFragmentDirections.actionReservationDetailFragmentToReservationFragment(
                        meetingRoomSafeArgs.room
                    )
                findNavController().navigate(directions)
            })
        }
    }

    private fun initAdapter() {
        reservationDetailAdapter = ReservationDetailAdapter()
        binding.rvReservations.adapter = reservationDetailAdapter
    }

    private fun processIntent() {
        meetingRoomSafeArgs.room.run {
            binding.item = this
            viewModel.roomId = id
            toolbar.title = "회의실 상세정보 : $name"
        }
        meetingRoomSafeArgs.myMeetingRoom.run {
            if (roomName != "") {
                binding.myMeetingRoom = this
                binding.textInputRegister.visibility = View.VISIBLE
                binding.tvRegister.visibility = View.VISIBLE
                binding.textInputContent.visibility = View.VISIBLE
                binding.tvContent.visibility = View.VISIBLE
                binding.textInputTime.visibility = View.VISIBLE
                binding.tvTime.visibility = View.VISIBLE
            }
        }


        binding.item = meetingRoomSafeArgs.room
        viewModel.roomId = meetingRoomSafeArgs.room.id
    }
}