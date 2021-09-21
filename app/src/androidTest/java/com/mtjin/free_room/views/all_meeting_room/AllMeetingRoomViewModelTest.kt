package com.mtjin.free_room.views.all_meeting_room

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import com.mtjin.free_room.TAG
import com.mtjin.free_room.getOrAwaitValue
import com.mtjin.free_room.data.all_meeting_room.source.AllMeetingRoomRepository
import com.mtjin.free_room.data.all_meeting_room.source.AllMeetingRoomRepositoryImpl
import com.mtjin.free_room.data.all_meeting_room.source.local.AllMeetingRoomLocalDataSource
import com.mtjin.free_room.data.all_meeting_room.source.remote.AllMeetingRoomRemoteDataSource
import com.mtjin.free_room.model.MeetingRoom
import com.mtjin.free_room.utils.NetworkManager
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@RunWith(MockitoJUnitRunner::class)
class AllMeetingRoomViewModelTest {

    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // context
    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val localRoomList = ArrayList<MeetingRoom>()
    private val remoteRoomList = ArrayList<MeetingRoom>()
    private val searchText = "Remote"

    private lateinit var repository: AllMeetingRoomRepository

    @Mock
    lateinit var localDataSource: AllMeetingRoomLocalDataSource

    @Mock
    lateinit var remoteDataSource: AllMeetingRoomRemoteDataSource

    private lateinit var networkManager: NetworkManager
    private lateinit var viewModel: AllMeetingRoomViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        networkManager = NetworkManager(appContext)
        for (i in 1..2) {
            localRoomList.add(MeetingRoom(id = i.toLong(), name = "Local$i"))
        }
        for (i in 1..3) {
            remoteRoomList.add(MeetingRoom(id = i.toLong(), name = "Remote$i"))
        }
        `when`(localDataSource.getMeetingRooms())
            .thenReturn(Single.just(localRoomList))

        `when`(
            localDataSource.insertMeetingRoom(remoteRoomList)
        ).thenReturn(Completable.complete())

        `when`(remoteDataSource.requestAllMeetingRooms())
            .thenReturn(Single.just(remoteRoomList))

        `when`(remoteDataSource.requestSearchRooms(searchText))
            .thenReturn(Single.just(remoteRoomList))

        repository = AllMeetingRoomRepositoryImpl(remoteDataSource, localDataSource)
        viewModel = AllMeetingRoomViewModel(repository, networkManager)

    }

    @After
    fun tearDown() {
    }

    @Test
    fun getMeetingRoomList() {
        viewModel.requestAllRooms()
        assertEquals(viewModel.isLoading.value, true)
        viewModel.meetingRoomList.getOrAwaitValue().toObservable()
            .observeOn(Schedulers.io())
            .subscribe {
                assertEquals(viewModel.isLoading.value, false)
                assertNotNull(it)
                assertEquals(localRoomList, it)
                Log.d(TAG, localRoomList.toString())
                Log.d(TAG, it.toString())
            }
    }

}