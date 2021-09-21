package com.mtjin.free_room.data.my_meeting_room.source.local

import android.util.Log
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.common.internal.Asserts
import com.mtjin.free_room.TAG
import com.mtjin.free_room.data.database.RoomDatabase
import com.mtjin.free_room.model.MyMeetingRoom
import com.mtjin.free_room.utils.getTimestamp
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class MyMeetingRoomLocalDataSourceImplTest {

    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    // context
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    // 로컬
    private lateinit var dao: MyMeetingRoomDao
    private lateinit var localDataSource: MyMeetingRoomLocalDataSource

    private val myRoomList = ArrayList<MyMeetingRoom>()
    private val myRoomFake = MyMeetingRoom(id = 11111, timestamp = 1, roomName = "FAKE1")

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        dao = Room.databaseBuilder(
            appContext,
            RoomDatabase::class.java,
            "WorksRoom.db"
        ).build().myMeetingRoomDao()
        localDataSource = MyMeetingRoomLocalDataSourceImpl(dao)

        myRoomList.add(MyMeetingRoom(id = 11111, timestamp = 1, roomName = "FAKE1"))
        myRoomList.add(MyMeetingRoom(id = 22222, timestamp = 2, roomName = "FAKE2"))
        myRoomList.add(MyMeetingRoom(id = 33333, timestamp = 3, roomName = "FAKE3"))
    }

    @After
    fun tearDown() {
    }

    @Test //데이터가 로컬에 추가되었음을 확인
    fun insertMeetingRoom() {
        localDataSource.insertMeetingRoom(myRoomList)
            .test()
            .awaitDone(3000, TimeUnit.MILLISECONDS)
            .assertOf {
                it.assertNoErrors()
                it.assertComplete()
                it.assertSubscribed()
            }
    }


    @Test // 현재시간 이후의 데이터만 가져온다.
    fun getMyMeetingRooms() {
        localDataSource.getMyMeetingRooms()
            .test()
            .awaitDone(3000, TimeUnit.MILLISECONDS)
            .assertOf {
                it.assertComplete()
                it.assertSubscribed()
                it.assertNoErrors()
                Asserts.checkNotNull(it)
                if (it.valueCount() > 0) {
                    for (room in it.values()[0]) {
                        if (room.endTimestamp < getTimestamp()) {
                            Assert.fail("내 예약관리에서 현재보다 이전시간의 데이터를 로컬에서 불러오는 에러")
                        }
                    }
                }
            }
    }

    @Test // 추가된 데이터를 삭제할 수 있어야한다.
    fun deleteMyMeetingRoom() {
        localDataSource.deleteMyMeetingRoom(myRoomFake)
            .test()
            .awaitDone(3000, TimeUnit.MILLISECONDS)
            .assertOf {
                it.assertSubscribed()
                it.assertNoErrors()
                Log.d(TAG, it.values().toString())
            }
    }
}