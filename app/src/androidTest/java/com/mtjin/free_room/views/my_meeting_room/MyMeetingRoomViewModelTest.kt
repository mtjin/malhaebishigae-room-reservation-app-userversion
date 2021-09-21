package com.mtjin.free_room.views.my_meeting_room

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import com.mtjin.free_room.TAG
import com.mtjin.free_room.getOrAwaitValue
import com.mtjin.free_room.data.my_meeting_room.source.MyMeetingRoomRepository
import com.mtjin.free_room.model.MyMeetingRoom
import com.mtjin.free_room.utils.NetworkManager
import com.mtjin.free_room.utils.getTimestamp
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@RunWith(MockitoJUnitRunner::class)
class MyMeetingRoomViewModelTest {
    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // context
    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var networkManager: NetworkManager
    private lateinit var viewModel: MyMeetingRoomViewModel

    @Mock
    lateinit var repository: MyMeetingRoomRepository

    private val roomList = ArrayList<MyMeetingRoom>()
    private val removeItem =
        MyMeetingRoom(id = 1, roomName = "room1", endTimestamp = getTimestamp())
    private val timestamp = getTimestamp()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        networkManager = NetworkManager(appContext)

        for (i in 1..3) {
            roomList.add(
                MyMeetingRoom(
                    id = i.toLong(),
                    roomName = "room$i",
                    endTimestamp = timestamp
                )
            )
        }
        roomList.add(
            MyMeetingRoom(
                id = 999,
                roomName = "room999",
                endTimestamp = timestamp - 100000
            )
        )

        `when`(repository.requestMyMeetingRooms()).thenReturn(Flowable.just(roomList))
        `when`(repository.cancelReservation(removeItem)).thenReturn(Completable.complete())
        viewModel = MyMeetingRoomViewModel(repository, networkManager)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun requestMyMeetingRoomsTimeShouldGreaterThanCurrentTime() {
        viewModel.requestMyMeetingRooms()
        Assert.assertEquals(viewModel.isLoading.value, true)
        viewModel.myMeetingRoomList.getOrAwaitValue().toObservable()
            .observeOn(Schedulers.io())
            .subscribe {
                assertEquals(viewModel.isLoading.value, false)
                assertNotNull(it)
                assertEquals(roomList, it)
                for (room in roomList) {
                    if (room.endTimestamp < timestamp) {
                        fail("현재시간 이후의 시간들만 보여야합니다.")
                    }
                }
                Log.d(TAG, roomList.toString())
                Log.d(TAG, it.toString())
            }
    }

    @Test
    fun cancelReservation() {
        Assert.assertEquals(viewModel.isLottieLoading.value, false)
        viewModel.cancelReservation(removeItem)
        Assert.assertEquals(viewModel.cancelResultMsg.getOrAwaitValue(), true)
    }
}