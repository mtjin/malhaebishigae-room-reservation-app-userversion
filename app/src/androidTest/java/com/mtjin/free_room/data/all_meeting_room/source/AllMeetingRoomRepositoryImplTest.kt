package com.mtjin.free_room.data.all_meeting_room.source

import android.util.Log
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.mtjin.free_room.TAG
import com.mtjin.free_room.data.all_meeting_room.source.local.AllMeetingRoomLocalDataSource
import com.mtjin.free_room.data.all_meeting_room.source.local.AllMeetingRoomLocalDataSourceImpl
import com.mtjin.free_room.data.all_meeting_room.source.remote.AllMeetingRoomRemoteDataSource
import com.mtjin.free_room.data.all_meeting_room.source.remote.AllMeetingRoomRemoteDataSourceImpl
import com.mtjin.free_room.data.database.RoomDatabase
import com.mtjin.free_room.model.MeetingRoom
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class AllMeetingRoomRepositoryImplTest {

    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var localDataSource: AllMeetingRoomLocalDataSource

    @Mock
    lateinit var remoteDataSource: AllMeetingRoomRemoteDataSource

    private lateinit var repository: AllMeetingRoomRepository

    private val localRoomList = ArrayList<MeetingRoom>()
    private val remoteRoomList = ArrayList<MeetingRoom>()
    private val searchText = "Rem"

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        for (i in 1..2) {
            localRoomList.add(MeetingRoom(id = i.toLong(), name = "Local$i"))
        }
        for (i in 1..3) {
            remoteRoomList.add(MeetingRoom(id = i.toLong(), name = "Remote$i"))
        }

        Mockito.`when`(localDataSource.getMeetingRooms())
            .thenReturn(Single.just(localRoomList))

        Mockito.`when`(
            localDataSource.insertMeetingRoom(remoteRoomList)
        ).thenReturn(Completable.complete())

        Mockito.`when`(remoteDataSource.requestAllMeetingRooms())
            .thenReturn(Single.just(remoteRoomList))

        Mockito.`when`(remoteDataSource.requestSearchRooms(searchText))
            .thenReturn(Single.just(remoteRoomList))

        repository = AllMeetingRoomRepositoryImpl(remoteDataSource, localDataSource)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun requestAllMeetingRooms() {
        repository.requestRemoteAllMeetingRooms()
            .test()
            .awaitDone(2000, TimeUnit.SECONDS)
            .assertOf {
                it.assertSubscribed() // Subscribe 되었는지
                Assert.assertNotNull(it)  // Null 체크
                Assert.assertEquals(remoteRoomList.size, it.values()[0].size) // 리모트 데이터 개수만큼 받아오는지
                Log.d(TAG, "리모트에서 온 데이터 -> " + it.values().toString())
            }
    }

    @Test // 로컬과 리모트에서 데이터가 각각 알맞게 오고 둘이 합쳐졌는지
    fun requestRemoteAllMeetingRooms() {
        repository.requestAllMeetingRooms()
            .test()
            .awaitDone(2000, TimeUnit.SECONDS)
            .assertOf {
                it.assertSubscribed() // Subscribe 되었는지
                it.assertComplete() // Complete 되었는지
                Assert.assertNotNull(it) // Null 체크
                Assert.assertEquals(2, it.values().count()) // local과 remote 두 개를 합쳤는지 (concat)
                Assert.assertEquals(
                    localRoomList.size,
                    it.values()[0].count()
                ) // local 에서 전달한 개수 확인

                Assert.assertEquals(
                    remoteRoomList.size,
                    it.values()[1].count()
                ) // remote 에서 전달한 개수 확인

                Log.d(TAG, "전체 데이터 -> " + it.values().toString()) // 전체
                Log.d(
                    TAG,
                    "로컬 데이터 -> " + it.values()[0].count().toString()
                )
                Log.d(
                    TAG,
                    "리모트 데이터 -> " + it.values()[1].count().toString()
                )
            }
    }

    @Test // 로컬에서 알맞게 데이터 전달받는지
    fun requestLocalAllMeetingRooms() {
        repository.requestLocalAllMeetingRooms().test()
            .awaitDone(2000, TimeUnit.SECONDS)
            .assertOf {
                it.assertSubscribed()
                it.assertComplete()
                Assert.assertEquals(
                    localRoomList.size,
                    it.values()[0].count()
                ) // local 에서 전달한 개수 확인
            }
    }

    @Test //검색한 결과
    fun requestSearchRooms() {
        repository.requestSearchRooms(searchText).test()
            .awaitDone(2000, TimeUnit.SECONDS)
            .assertOf {
                it.assertSubscribed()
                it.assertComplete()
                Assert.assertEquals(remoteRoomList.size, it.values()[0].count())
                Log.d(
                    TAG,
                    "requestSearchRooms() -> " + remoteRoomList.size +
                            " , " + it.values()[0].toString()
                )
            }
    }

    @Test //비어있는 리스트 받을 수 있는지
    fun requestAllMeetingRoomsCanReceiveEmptyList() {
        Mockito.`when`(localDataSource.getMeetingRooms())
            .thenReturn(Single.just(ArrayList<MeetingRoom>()))

        Mockito.`when`(remoteDataSource.requestAllMeetingRooms())
            .thenReturn(Single.just(ArrayList<MeetingRoom>()))

        val repository = AllMeetingRoomRepositoryImpl(remoteDataSource, localDataSource)
        repository.requestAllMeetingRooms()
            .test()
            .awaitDone(3000, TimeUnit.SECONDS)
            .assertOf {
                it.assertSubscribed() // Subscribe 되었는지
                it.assertComplete() // Complete 되었는지
                Assert.assertEquals(0, it.values()[0].size)
                Log.d(
                    TAG,
                    "requestAllMeetingRoomsCanReceiveEmptyList() -> " + it.values().toString()
                ) // 받은 리스트
            }
    }

    @Test
    fun repositoryRealDataTest() {
        // context
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // 리모트
        Firebase.initialize(appContext)
        val remote = AllMeetingRoomRemoteDataSourceImpl(Firebase.database.reference)
        val remoteSource = remote.requestAllMeetingRooms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        // 리모트 소스 테스트
        remoteSource.test().awaitDone(3000, TimeUnit.MILLISECONDS).assertOf {
            it.assertSubscribed() // Subscribe 되었는지
            Assert.assertNotNull(it) // 받은 값이 Null 이 아닌지
            Log.d(TAG, "repositoryRealDataTest() 리모트 데이터 -> " + it.values().toString()) // 받은 리스트
        }

        // 로컬
        val localMeetingRoomDao = Room.databaseBuilder(
            appContext,
            RoomDatabase::class.java,
            "WorksRoom.db"
        ).build().meetingRoomDao() // 룸 디비

        val local = AllMeetingRoomLocalDataSourceImpl(localMeetingRoomDao)

        val localSource = local.getMeetingRooms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        // 로컬 소스 테스트
        localSource.test().awaitDone(3000, TimeUnit.MILLISECONDS).assertOf {
            it.assertSubscribed() // Subscribe 되었는지
            Assert.assertNotNull(it) // 받은 값이 Null 이 아닌지
            Log.d(TAG, "repositoryRealDataTest() 로컬 데이터 -> " + it.values().toString()) // 받은 리스트
        }

        // 레포지토리 ( 리모트 + 로컬 )
        val repository = AllMeetingRoomRepositoryImpl(remote, local)
        val repositorySource = repository.requestAllMeetingRooms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        // 레포지토리 테스트
        repositorySource.test().awaitDone(3000, TimeUnit.MILLISECONDS).assertOf {
            it.assertSubscribed() // Subscribe 되었는지
            Assert.assertNotNull(it) // 받은 값이 Null 이 아닌지
            Log.d(TAG, "repositoryRealDataTest() 레포지토리 데이터 -> " + it.values().toString()) // 받은 리스트
        }
    }
}
