package com.mtjin.free_room.views.all_meeting_room

import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mtjin.free_room.R
import com.mtjin.free_room.base.BaseFragment
import com.mtjin.free_room.model.MyMeetingRoom
import com.mtjin.free_room.databinding.FragmentAllMeetingRoomBinding
import com.mtjin.free_room.views.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AllMeetingRoomFragment :
    BaseFragment<FragmentAllMeetingRoomBinding>(R.layout.fragment_all_meeting_room) {
    private lateinit var meetingRoomAdapter: AllMeetingRoomAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: AllMeetingRoomViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(AllMeetingRoomViewModel::class.java)
    }

    private val searchSubject = PublishSubject.create<String>()

    override fun init() {
        (activity as MainActivity).mainComponent.inject(this)
        binding.vm = viewModel
        initView()
        initAdapter()
        initViewModelCallback()
        requestAllRooms()
    }

    private fun requestAllRooms() {
        viewModel.requestAllRooms()
    }

    private fun initViewModelCallback() {
        with(viewModel) {
            networkState.observe(this@AllMeetingRoomFragment, Observer {
                showInfoSnackBar(getString(R.string.network_err_then_load_local_text))
            })
        }
    }

    private fun initAdapter() {
        meetingRoomAdapter = AllMeetingRoomAdapter({ room -> //상세보기
            val directions =
                AllMeetingRoomFragmentDirections.actionBottomNav1ToReservationDetailFragment(
                    room,
                    MyMeetingRoom()
                )
            findNavController().navigate(directions)
        }, { room -> //예약하기
            val directions =
                AllMeetingRoomFragmentDirections.actionBottomNav1ToReservationFragment(room)
            findNavController().navigate(directions)
        })
        binding.rvRooms.adapter = meetingRoomAdapter
    }

    private fun initView() {
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            searchSubject.onNext(text.toString())
        }
        compositeDisposable.add(
            searchSubject
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(1000, TimeUnit.MILLISECONDS)
                .filter { it.isNotBlank() }
                .map { it.trim() }
                .subscribe { roomName ->
                    Timber.d("검색한 회의실이름 -> %s", roomName)
                    viewModel.requestSearchRooms(roomName)
                }
        )
    }
}