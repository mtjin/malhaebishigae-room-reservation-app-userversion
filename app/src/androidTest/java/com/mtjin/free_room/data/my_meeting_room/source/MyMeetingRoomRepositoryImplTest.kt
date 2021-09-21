package com.mtjin.free_room.data.my_meeting_room.source

import android.util.Log
import com.mtjin.free_room.TAG
import com.mtjin.free_room.data.my_meeting_room.source.local.MyMeetingRoomLocalDataSource
import com.mtjin.free_room.data.my_meeting_room.source.remote.MyMeetingRoomRemoteDataSource
import com.mtjin.free_room.model.MyMeetingRoom
import com.mtjin.free_room.utils.getTimestamp
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class MyMeetingRoomRepositoryImplTest {

    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var localDataSource: MyMeetingRoomLocalDataSource

    @Mock
    lateinit var remoteDataSource: MyMeetingRoomRemoteDataSource

    private lateinit var repository: MyMeetingRoomRepository

    private val localRoomList = ArrayList<MyMeetingRoom>()
    private val remoteRoomList = ArrayList<MyMeetingRoom>()
    private val removeRoom =
        MyMeetingRoom(id = 1, roomName = "room", endTimestamp = getTimestamp() + 10000000)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        for (i in 1..2) {
            localRoomList.add(
                MyMeetingRoom(
                    id = i.toLong(),
                    roomName = "localRoom$i",
                    endTimestamp = getTimestamp()
                )
            )
        }
        for (i in 1..3) {
            remoteRoomList.add(
                MyMeetingRoom(
                    id = i.toLong(),
                    roomName = "remoteRoom$i",
                    endTimestamp = getTimestamp()
                )
            )
        }

        `when`(localDataSource.deleteMyMeetingRoom(removeRoom)).thenReturn(Completable.complete())
        `when`(localDataSource.insertMeetingRoom(localRoomList)).thenReturn(Completable.complete())
        `when`(localDataSource.getMyMeetingRooms()).thenReturn(Single.just(localRoomList))
        `when`(remoteDataSource.requestMyMeetingRooms()).thenReturn(Flowable.just(remoteRoomList))
        `when`(
            remoteDataSource.cancelReservation(removeRoom)
        ).thenReturn(Completable.complete())

        repository = MyMeetingRoomRepositoryImpl(
            remoteDataSource, localDataSource
        )
    }

    @After
    fun tearDown() {

    }

    @Test
    fun requestMyMeetingRooms() {
        repository.requestMyMeetingRooms().test()
            .awaitDone(3000, TimeUnit.MILLISECONDS)
            .assertOf {
                it.assertSubscribed()
                it.assertNotComplete()
                Assert.assertNotNull(it) // Null 체크
                Log.d(TAG, it.values().toString())
                Assert.assertEquals(
                    localRoomList.size,
                    it.values()[0].count()
                ) // local 에서 전달한 개수 확인
            }
    }

    @Test
    fun requestRemoteMyMeetingRooms() {
        repository.requestRemoteMyMeetingRooms().test()
            .awaitDone(3000, TimeUnit.MILLISECONDS)
            .assertOf {
                it.assertSubscribed()
                Assert.assertNotNull(it)
                Log.d(TAG, it.values().toString())
            }
    }

    @Test
    fun requestLocalMyMeetingRooms() {
        repository.requestLocalMyMeetingRooms().test()
            .awaitDone(3000, TimeUnit.MILLISECONDS)
            .assertOf {
                it.assertSubscribed()
                it.assertComplete()
                Assert.assertEquals(localRoomList.size, it.values()[0].size)
            }
    }

    @Test
    fun cancelReservation() {
        repository.cancelReservation(removeRoom)
            .test()
            .awaitDone(3000, TimeUnit.MILLISECONDS)
            .assertOf {
                it.assertSubscribed()
                it.assertComplete()
            }
    }
}