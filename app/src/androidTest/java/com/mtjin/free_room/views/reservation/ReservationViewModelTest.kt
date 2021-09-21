package com.mtjin.free_room.views.reservation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import com.mtjin.free_room.data.reservation.source.ReservationRepository
import com.mtjin.free_room.model.Reservation
import com.mtjin.free_room.utils.NetworkManager
import com.mtjin.free_room.utils.getTimestamp
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@RunWith(MockitoJUnitRunner::class)
class ReservationViewModelTest {
    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var repository: ReservationRepository

    // context
    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var networkManager: NetworkManager
    private lateinit var viewModel: ReservationViewModel
    private val timestamp = getTimestamp()
    private val roomId: Long = 1
    private val reservationList = ArrayList<Reservation>()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        networkManager = NetworkManager(appContext)
        // 현재시간 이후
        reservationList.add(Reservation(id = 1, endTimestamp = timestamp + 100000))
        reservationList.add(Reservation(id = 2, endTimestamp = timestamp + 100000))
        reservationList.add(Reservation(id = 3, endTimestamp = timestamp + 100000))
        // 현재시간 이전
        reservationList.add(Reservation(id = 4, endTimestamp = timestamp - 100000))

        `when`(repository.requestAvailableTime(timestamp, roomId)).thenReturn(
            Flowable.just(
                reservationList
            )
        )
        `when`(
            repository.requestReservation(
                reservationList[0],
                1
            )
        ).thenReturn(
            Completable.complete()
        )

        viewModel = ReservationViewModel(repository, networkManager, appContext)
    }

    @After
    fun tearDown() {
    }

//    @Test
//    fun reservationAvailableTimeShouldGreaterThanCurrentTime() {
//        viewModel.requestAvailableTime(timestamp)
//        Assert.assertEquals(viewModel.isLoading.value, true)
//        viewModel.reservationList.getOrAwaitValue().toObservable()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doAfterTerminate { Assert.assertEquals(viewModel.isLoading.value, false) }
//            .subscribe {
//                Assert.assertNotNull(it)
//                if (it.endTimestamp < timestamp) {
//                    Assert.fail("현재 시간보다 이전시간은 필터링 되야합니다.")
//                }
//            }
//    }
//
//    @Test
//    fun `키키키`(){}
//
//    @Test
//    fun requestReservationSuccess() {
//        viewModel.startTime
//        Assert.assertEquals(viewModel.isLottieLoading.value, false)
//        viewModel.requestReservation()
//        viewModel.reservationResult.getOrAwaitValue()
//        Assert.assertEquals(viewModel.isLottieLoading.value, true)
//
//    }
}