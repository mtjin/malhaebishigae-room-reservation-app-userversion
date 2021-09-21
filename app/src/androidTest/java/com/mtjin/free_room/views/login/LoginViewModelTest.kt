package com.mtjin.free_room.views.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mtjin.free_room.getOrAwaitValue
import com.google.firebase.auth.FirebaseAuth
import com.mtjin.free_room.data.login.source.LoginRepository
import io.reactivex.Completable
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var loginRepository: LoginRepository

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        `when`(loginRepository.googleLogin()).thenReturn(Completable.complete())
        viewModel = LoginViewModel(loginRepository)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun firebaseUidNotEmptyAfterGoogleLogin() {
        viewModel.googleLogin()
        if(FirebaseAuth.getInstance().uid == ""){
            fail("구글로그인이 완료되면 uid 있어야합니다.")
        }
    }

    @Test
    fun googleLoginSuccess() {
        assertEquals(viewModel.isLoading.value, false)
        viewModel.googleLogin()
        assertEquals(viewModel.isLoading.value, true)
        viewModel.googleLoginResult.getOrAwaitValue()
        assertEquals(viewModel.googleLoginResult.value, true)
    }

//    @Test 커버리지 측정시 안됨
//    fun googleLoginFail() {
//        `when`(loginRepository.googleLogin()).thenReturn(Completable.error(Throwable("에러발생")))
//        assertEquals(viewModel.isLoading.value, false)
//        viewModel.googleLogin()
//        assertEquals(viewModel.isLoading.value, true)
//        viewModel.googleLoginResult.getOrAwaitValue()
//        assertEquals(viewModel.googleLoginResult.value, false)
//    }
}