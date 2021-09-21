package com.mtjin.free_room.data.all_meeting_room.source.remote

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.mtjin.free_room.TAG
import com.mtjin.free_room.model.MeetingRoom
import org.hamcrest.Matchers.instanceOf
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class AllMeetingRoomRemoteDataSourceImplTest {

    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    // context
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Firebase.initialize(appContext)
    }

    @After
    fun tearDown() {
    }

    @Test //전체회의실 불러오기 테스트
    fun requestAllMeetingRooms() {
        val remote = AllMeetingRoomRemoteDataSourceImpl(Firebase.database.reference)
        val remoteSource = remote.requestAllMeetingRooms()

        remoteSource.test().awaitDone(3000, TimeUnit.MILLISECONDS).assertOf {
            it.assertSubscribed() // Subscribe 되었는지
            it.assertComplete() // Complete 되었는지
            Assert.assertNotNull(it) // 받은 값이 Null 이 아닌지
            Assert.assertThat(
                it.values()[0][0],
                instanceOf(MeetingRoom::class.java)
            ) // 리스트 데이터 타입체크
            Log.d(TAG, "리모트 데이터 -> " + it.values().toString()) // 받은 리스트
        }
    }

    @Test //검색한 회의실 불러오기 테스트
    fun requestSearchRooms() {
        val remote = AllMeetingRoomRemoteDataSourceImpl(Firebase.database.reference)
        var remoteSource = remote.requestSearchRooms("기")

        // 검색 되야하는 것
        remoteSource.test().awaitDone(3000, TimeUnit.MILLISECONDS).assertOf {
            it.assertSubscribed() // Subscribe 되었는지
            it.assertComplete() // Complete 되었는지
            Assert.assertNotNull(it) // 받은 값이 Null 이 아닌지
            Assert.assertThat(
                it.values()[0][0],
                instanceOf(MeetingRoom::class.java)
            ) // 리스트 데이터 타입체크
            Log.d(TAG, "리모트 데이터 -> " + it.values().toString()) // 받은 리스트
        }

        // 검색 되면 안돼는 것
        remoteSource = remote.requestSearchRooms("!@#$%$%")
        remoteSource.test().awaitDone(3000, TimeUnit.MILLISECONDS).assertOf {
            it.assertSubscribed() // Subscribe 되었는지
            it.assertComplete() // Complete 되었는지
            Assert.assertNotNull(it) // 받은 값이 Null 이 아닌지
            Assert.assertEquals(0, it.values()[0].size) // 검색결과가 0개인지
            Log.d(TAG, "리모트 데이터 -> " + it.values().toString()) // 받은 리스트
        }
    }
}
