package com.mtjin.free_room.data.login.source.remote


import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
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
class LoginRemoteDataSourceImplTest {

    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var remote: LoginRemoteDataSource

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun googleLogin() {
        Firebase.initialize(appContext)
        remote = LoginRemoteDataSourceImpl(Firebase.database.reference)
        remote.googleLogin().test().awaitDone(3000, TimeUnit.MILLISECONDS).assertOf {
            it.assertSubscribed()
            it.assertComplete()
        }
    }

}