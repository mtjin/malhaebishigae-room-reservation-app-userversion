package com.mtjin.free_room.views.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mtjin.free_room.base.BaseViewModel
import com.mtjin.free_room.data.setting.source.SettingRepository
import com.mtjin.free_room.utils.BUSINESS_CODE
import com.mtjin.free_room.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class SettingViewModel @Inject constructor(private val settingRepository: SettingRepository) :
    BaseViewModel() {
    var businessCode = MutableLiveData<String>()

    private val _showLogoutDialog = SingleLiveEvent<Unit>()
    private val _deleteCacheMsg = SingleLiveEvent<Unit>()
    private val _showDeleteCacheDialog = SingleLiveEvent<Unit>()
    private val _businessCodeResponse = SingleLiveEvent<Int>()

    val showLogoutDialog: LiveData<Unit> = _showLogoutDialog
    val deleteCacheMsg: LiveData<Unit> = _deleteCacheMsg
    val showDeleteCacheDialog: LiveData<Unit> = _showDeleteCacheDialog
    val businessCodeResponse: LiveData<Int> = _businessCodeResponse

    fun deleteCache() {
        compositeDisposable.add(
            settingRepository.deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgress() }
                .doAfterTerminate { hideProgress() }
                .subscribe({
                    _deleteCacheMsg.call()
                }, {
                    Timber.d(it)
                })
        )
    }

    fun setSettingAlarm(on: Boolean) {
        settingRepository.alarmSetting = on
    }

    fun getSettingAlarm(): Boolean = settingRepository.alarmSetting


    fun showDeleteCacheDialog() {
        _showDeleteCacheDialog.call()
    }

    fun showLogoutDialog() {
        _showLogoutDialog.call()
    }

    fun initBusinessCode() {
        businessCode.value = settingRepository.businessCode
    }

    fun saveBusinessCode() {
        if (businessCode.value.toString().isBlank()) {
            _businessCodeResponse.value = 1
        } else {
            settingRepository.businessCode = businessCode.value.toString()
            BUSINESS_CODE = settingRepository.businessCode
            _businessCodeResponse.value = 2
        }
    }

}