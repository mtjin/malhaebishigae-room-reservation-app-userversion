package com.mtjin.free_room.views.setting

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mtjin.free_room.data.setting.source.SettingRepository
import com.mtjin.free_room.getOrAwaitValue
import io.reactivex.Completable
import junit.framework.Assert.assertFalse
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class SettingViewModelTest {
    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var repository: SettingRepository

    lateinit var viewModel: SettingViewModel


    @Before
    fun setUp() {
        `when`(repository.deleteAll()).thenReturn(Completable.complete())
        viewModel = SettingViewModel(repository)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun deleteCache() {
        assertFalse(viewModel.isLottieLoading.value!!)
        viewModel.deleteCache()
        viewModel.deleteCacheMsg.getOrAwaitValue(3000, TimeUnit.MILLISECONDS)
    }
}