package com.mtjin.free_room.views.my_meeting_room

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mtjin.free_room.R
import com.mtjin.free_room.base.BaseFragment
import com.mtjin.free_room.model.MeetingRoom
import com.mtjin.free_room.databinding.FragmentMyMeetingRoomBinding
import com.mtjin.free_room.views.dialog.BottomDialogFragment
import com.mtjin.free_room.views.main.MainActivity
import javax.inject.Inject

class MyMeetingRoomFragment :
    BaseFragment<FragmentMyMeetingRoomBinding>(R.layout.fragment_my_meeting_room) {
    private lateinit var myMeetingRoomAdapter: MyMeetingRoomAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: MyMeetingRoomViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MyMeetingRoomViewModel::class.java)
    }

    override fun init() {
        (activity as MainActivity).mainComponent.inject(this)
        binding.vm = viewModel
        initAdapter()
        initViewModelCallback()
        requestMyMeetingRooms()
    }

    private fun requestMyMeetingRooms() {
        viewModel.requestMyMeetingRooms()
    }

    private fun initViewModelCallback() {
        with(viewModel) {
            networkState.observe(this@MyMeetingRoomFragment, Observer {
                showInfoSnackBar(getString(R.string.network_err_then_load_local_text))
            })

            cancelResultMsg.observe(this@MyMeetingRoomFragment, Observer { result ->
                if (result) showPositiveSnackBar(getString(R.string.reservation_cancel_success_text))
                else showNegativeSnackBar(getString(R.string.reservation_cancel_failure_text))
            })

            isLottieLoading.observe(this@MyMeetingRoomFragment, Observer { loadingState ->
                if (loadingState) showProgressDialog()
                else hideProgressDialog()
            })

            myMeetingRoomListEmpty.observe(this@MyMeetingRoomFragment, Observer { empty ->
                if (empty) binding.tvNoReservation.visibility = View.VISIBLE
                else binding.tvNoReservation.visibility = View.GONE
            })
        }
    }

    private fun initAdapter() {
        myMeetingRoomAdapter = MyMeetingRoomAdapter({ myRoom -> //상세보기
            val directions =
                MyMeetingRoomFragmentDirections.actionBottomNav2ToReservationDetailFragment(
                    MeetingRoom(myRoom.id, myRoom.roomName, myRoom.image, myRoom.num, false), myRoom
                )
            findNavController().navigate(directions)
        }) { room ->
            val dialog =
                BottomDialogFragment(
                    getString(R.string.reservation_cancel_question_text)
                ) { yes ->
                    if (yes) {
                        viewModel.cancelReservation(room)
                    }
                }
            dialog.show(requireActivity().supportFragmentManager, dialog.tag)
        }
        binding.rvRooms.adapter = myMeetingRoomAdapter
    }
}