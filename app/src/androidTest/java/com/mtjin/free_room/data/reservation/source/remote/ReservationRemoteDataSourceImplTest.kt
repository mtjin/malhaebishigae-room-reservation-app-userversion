package com.mtjin.free_room.data.reservation.source.remote

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.mtjin.free_room.TAG
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.mtjin.free_room.model.Reservation
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class ReservationRemoteDataSourceImplTest {

    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    // context
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    private lateinit var remote: ReservationRemoteDataSource

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Firebase.initialize(appContext)
        remote = ReservationRemoteDataSourceImpl(Firebase.database.reference, appContext)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun requestAvailableTime() {
        remote.requestAvailableTime(timestamp = Long.MAX_VALUE, roomId = 1)
            .test()
            .awaitDone(3000, TimeUnit.MILLISECONDS)
            .assertOf {
                it.assertSubscribed()
                it.assertNoErrors()
                Assert.assertNotNull(it.values()[0])
                Log.d(TAG, it.values()[0].toString())
            }

        remote.requestAvailableTime(timestamp = 0, roomId = 1)
            .test()
            .awaitDone(3000, TimeUnit.MILLISECONDS)
            .assertOf {
                it.assertSubscribed()
                it.assertNoErrors()
                Assert.assertNotNull(it.values()[0])
                Log.d(TAG, it.values()[0].toString())
            }
    }

    @Test
    fun requestReservation() {
        remote.requestReservation(Reservation(), 1)
            .test()
            .awaitDone(3000, TimeUnit.MILLISECONDS)
            .assertOf {
                it.assertSubscribed()
                it.assertNoErrors()
                it.assertComplete()
            }
    }
}