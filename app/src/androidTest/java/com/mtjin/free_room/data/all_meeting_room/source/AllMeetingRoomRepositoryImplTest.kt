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
                it.assertSubscribed() // Subscribe ????????????
                Assert.assertNotNull(it)  // Null ??????
                Assert.assertEquals(remoteRoomList.size, it.values()[0].size) // ????????? ????????? ???????????? ???????????????
                Log.d(TAG, "??????????????? ??? ????????? -> " + it.values().toString())
            }
    }

    @Test // ????????? ??????????????? ???????????? ?????? ????????? ?????? ?????? ???????????????
    fun requestRemoteAllMeetingRooms() {
        repository.requestAllMeetingRooms()
            .test()
            .awaitDone(2000, TimeUnit.SECONDS)
            .assertOf {
                it.assertSubscribed() // Subscribe ????????????
                it.assertComplete() // Complete ????????????
                Assert.assertNotNull(it) // Null ??????
                Assert.assertEquals(2, it.values().count()) // local??? remote ??? ?????? ???????????? (concat)
                Assert.assertEquals(
                    localRoomList.size,
                    it.values()[0].count()
                ) // local ?????? ????????? ?????? ??????

                Assert.assertEquals(
                    remoteRoomList.size,
                    it.values()[1].count()
                ) // remote ?????? ????????? ?????? ??????

                Log.d(TAG, "?????? ????????? -> " + it.values().toString()) // ??????
                Log.d(
                    TAG,
                    "?????? ????????? -> " + it.values()[0].count().toString()
                )
                Log.d(
                    TAG,
                    "????????? ????????? -> " + it.values()[1].count().toString()
                )
            }
    }

    @Test // ???????????? ????????? ????????? ???????????????
    fun requestLocalAllMeetingRooms() {
        repository.requestLocalAllMeetingRooms().test()
            .awaitDone(2000, TimeUnit.SECONDS)
            .assertOf {
                it.assertSubscribed()
                it.assertComplete()
                Assert.assertEquals(
                    localRoomList.size,
                    it.values()[0].count()
                ) // local ?????? ????????? ?????? ??????
            }
    }

    @Test //????????? ??????
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

    @Test //???????????? ????????? ?????? ??? ?????????
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
                it.assertSubscribed() // Subscribe ????????????
                it.assertComplete() // Complete ????????????
                Assert.assertEquals(0, it.values()[0].size)
                Log.d(
                    TAG,
                    "requestAllMeetingRoomsCanReceiveEmptyList() -> " + it.values().toString()
                ) // ?????? ?????????
            }
    }

    @Test
    fun repositoryRealDataTest() {
        // context
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // ?????????
        Firebase.initialize(appContext)
        val remote = AllMeetingRoomRemoteDataSourceImpl(Firebase.database.reference)
        val remoteSource = remote.requestAllMeetingRooms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        // ????????? ?????? ?????????
        remoteSource.test().awaitDone(3000, TimeUnit.MILLISECONDS).assertOf {
            it.assertSubscribed() // Subscribe ????????????
            Assert.assertNotNull(it) // ?????? ?????? Null ??? ?????????
            Log.d(TAG, "repositoryRealDataTest() ????????? ????????? -> " + it.values().toString()) // ?????? ?????????
        }

        // ??????
        val localMeetingRoomDao = Room.databaseBuilder(
            appContext,
            RoomDatabase::class.java,
            "WorksRoom.db"
        ).build().meetingRoomDao() // ??? ??????

        val local = AllMeetingRoomLocalDataSourceImpl(localMeetingRoomDao)

        val localSource = local.getMeetingRooms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        // ?????? ?????? ?????????
        localSource.test().awaitDone(3000, TimeUnit.MILLISECONDS).assertOf {
            it.assertSubscribed() // Subscribe ????????????
            Assert.assertNotNull(it) // ?????? ?????? Null ??? ?????????
            Log.d(TAG, "repositoryRealDataTest() ?????? ????????? -> " + it.values().toString()) // ?????? ?????????
        }

        // ??????????????? ( ????????? + ?????? )
        val repository = AllMeetingRoomRepositoryImpl(remote, local)
        val repositorySource = repository.requestAllMeetingRooms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        // ??????????????? ?????????
        repositorySource.test().awaitDone(3000, TimeUnit.MILLISECONDS).assertOf {
            it.assertSubscribed() // Subscribe ????????????
            Assert.assertNotNull(it) // ?????? ?????? Null ??? ?????????
            Log.d(TAG, "repositoryRealDataTest() ??????????????? ????????? -> " + it.values().toString()) // ?????? ?????????
        }
    }
}
