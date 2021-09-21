package com.mtjin.free_room.data.all_meeting_room.source.local

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.common.internal.Asserts
import com.mtjin.free_room.data.database.RoomDatabase
import com.mtjin.free_room.model.MeetingRoom
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class AllMeetingRoomLocalDataSourceImplTest {

    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    // context
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    // 로컬
    private lateinit var localMeetingRoomDao: MeetingRoomDao
    private lateinit var localDataSource: AllMeetingRoomLocalDataSource

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        localMeetingRoomDao = Room.databaseBuilder(
            appContext,
            RoomDatabase::class.java,
            "WorksRoom.db"
        ).build().meetingRoomDao()
        localDataSource = AllMeetingRoomLocalDataSourceImpl(localMeetingRoomDao)
    }

    @After
    fun tearDown() {

    }

    @Test
    fun insertMeetingRoom() {
        localDataSource.insertMeetingRoom(ArrayList<MeetingRoom>())
            .test()
            .awaitDone(3000, TimeUnit.MILLISECONDS)
            .assertOf {
                it.assertSubscribed()
                it.assertComplete()
            }
    }

    @Test
    fun getMeetingRooms() {
        localDataSource.getMeetingRooms()
            .test()
            .awaitDone(3000, TimeUnit.MILLISECONDS)
            .assertOf {
                it.assertSubscribed()
                Asserts.checkNotNull(it)
            }
    }

    @Test
    fun deleteMeetingRoom() {
        localDataSource.deleteMeetingRoom(MeetingRoom())
            .test()
            .awaitDone(3000, TimeUnit.MILLISECONDS)
            .assertOf {
                it.assertSubscribed()
                it.assertComplete()
            }
    }
}
