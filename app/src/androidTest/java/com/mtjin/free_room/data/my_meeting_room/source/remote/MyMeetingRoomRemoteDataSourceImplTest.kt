package com.mtjin.free_room.data.my_meeting_room.source.remote

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.mtjin.free_room.TAG
import com.mtjin.free_room.model.MyMeetingRoom
import com.mtjin.free_room.utils.getTimestamp
import com.mtjin.free_room.views.main.MainActivity
import io.reactivex.Flowable
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
class MyMeetingRoomRemoteDataSourceImplTest {

    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java)

    // context
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    private val myRoomList = ArrayList<MyMeetingRoom>()

    private val remoteDataSource = MyMeetingRoomRemoteDataSourceImpl(Firebase.database.reference)

    @Mock
    private lateinit var remote: MyMeetingRoomRemoteDataSource

    @Before
    fun setUp() {
        Thread.sleep(5000)
        MockitoAnnotations.initMocks(this)
        Firebase.initialize(appContext)

        val timestamp = getTimestamp()
        myRoomList.add(
            MyMeetingRoom(
                id = 1,
                roomName = "room",
                endTimestamp = timestamp + 10000000
            )
        )
        myRoomList.add(
            MyMeetingRoom(
                id = 2,
                roomName = "room2",
                endTimestamp = timestamp + 10000000
            )
        )
        myRoomList.add(
            MyMeetingRoom(
                id = 3,
                roomName = "room3",
                endTimestamp = timestamp - 1000000
            )
        )
    }

    @After
    fun tearDown() {
    }

    @Test // 내가 예약한 회의실이 있는 경우 리스트 반환
    fun requestMyMeetingRooms() {
        `when`(remote.requestMyMeetingRooms()).thenReturn(Flowable.just(myRoomList))
        remote.requestMyMeetingRooms()
            .test()
            .awaitDone(3000, TimeUnit.MILLISECONDS).assertOf {
                it.assertSubscribed() // Subscribe 되었는지
                it.assertComplete() // Complete 되었는지
                Log.d(TAG, it.values().toString())
            }
    }

    @Test
    fun myMeetingRoomTimestampShouldAfterNow() {
        val now = getTimestamp()

        remoteDataSource.requestMyMeetingRooms().test().awaitDone(2000, TimeUnit.MILLISECONDS)
            .assertOf {
                for (myRoom in it.values()[0]) {
                    if (myRoom.endTimestamp < now) {
                        Assert.fail()
                    }
                }
            }

    }
}