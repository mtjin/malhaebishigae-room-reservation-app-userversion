package com.mtjin.free_room.views.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mtjin.free_room.base.BaseViewModel
import com.mtjin.free_room.data.profile.source.ProfileRepository
import com.mtjin.free_room.data.setting.source.SettingRepository
import com.mtjin.free_room.utils.BUSINESS_CODE
import com.mtjin.free_room.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class SettingViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
    private val profileRepository: ProfileRepository
) :
    BaseViewModel() {
    var businessCode = MutableLiveData<String>()

    private val _showLogoutDialog = SingleLiveEvent<Unit>()
    private val _deleteCacheMsg = SingleLiveEvent<Unit>()
    private val _showDeleteCacheDialog = SingleLiveEvent<Unit>()
    private val _businessCodeResponse = SingleLiveEvent<SaveBusinessCodeResponse>()

    val showLogoutDialog: LiveData<Unit> = _showLogoutDialog
    val deleteCacheMsg: LiveData<Unit> = _deleteCacheMsg
    val showDeleteCacheDialog: LiveData<Unit> = _showDeleteCacheDialog
    val businessCodeResponse: LiveData<SaveBusinessCodeResponse> = _businessCodeResponse

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
            _businessCodeResponse.value = SaveBusinessCodeResponse.EMPTY_INPUT
        } else {
            compositeDisposable.add(
                profileRepository.insertUserByBusinessCode(
                    businessCode.value.toString()
                ).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete { deleteCache() }
                    .doOnSubscribe { showLottieProgress() }
                    .doAfterTerminate { hideLottieProgress() }
                    .subscribe({
                        settingRepository.businessCode = businessCode.value.toString()
                        BUSINESS_CODE = settingRepository.businessCode
                        _businessCodeResponse.value = SaveBusinessCodeResponse.SUCCESS
                    }, {
                        _businessCodeResponse.value = SaveBusinessCodeResponse.FAILURE
                        Timber.d(it)
                    })
            )
        }
    }

    enum class SaveBusinessCodeResponse {
        SUCCESS,
        FAILURE,
        EMPTY_INPUT,
    }

}