package com.mtjin.free_room.views.profile

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mtjin.free_room.base.BaseViewModel
import com.mtjin.free_room.model.User
import com.mtjin.free_room.data.profile.source.ProfileRepository
import com.mtjin.free_room.utils.NetworkManager
import com.mtjin.free_room.utils.SingleLiveEvent
import com.mtjin.free_room.utils.fcmToken
import com.mtjin.free_room.utils.uuid
import com.gun0912.tedpermission.TedPermissionResult
import com.tedpark.tedpermission.rx2.TedRx2Permission
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject


class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val networkManager: NetworkManager,
    private val context: Context
) : BaseViewModel() {
    lateinit var qrCodeBitmap: Bitmap

    private val _qrCode = MutableLiveData<String>("")
    val userId = MutableLiveData<String>("")
    val userPw = MutableLiveData<String>("")
    private val _reviseState = MutableLiveData<Boolean>(false)
    private val _networkState = SingleLiveEvent<Unit>()
    private val _idPwEmptyMsg = SingleLiveEvent<Unit>()
    private val _reviseFailMsg = SingleLiveEvent<Unit>()
    private val _idPwOverLengthMsg = SingleLiveEvent<Unit>()
    private val _reviseSuccessMsg = SingleLiveEvent<Unit>()
    private val _permissionResultMsg = SingleLiveEvent<Boolean>()
    private val _saveQrCodeResultMsg = SingleLiveEvent<Boolean>()

    val reviseState: LiveData<Boolean> get() = _reviseState
    val networkState: LiveData<Unit> get() = _networkState
    val idPwEmptyMsg: LiveData<Unit> get() = _idPwEmptyMsg
    val reviseFailMsg: LiveData<Unit> get() = _reviseFailMsg
    val idPwOverLengthMsg: LiveData<Unit> get() = _idPwOverLengthMsg
    val reviseSuccessMsg: LiveData<Unit> get() = _reviseSuccessMsg
    val permissionResultMsg: LiveData<Boolean> get() = _permissionResultMsg
    val saveQrCodeResultMsg: LiveData<Boolean> get() = _saveQrCodeResultMsg

    fun requestProfile() {
        if (!networkManager.checkNetworkState()) {
            _networkState.call()
        } else {
            compositeDisposable.add(
                profileRepository.requestProfile(uuid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgress() }
                    .doAfterTerminate { hideProgress() }
                    .subscribe({ user ->
                        _qrCode.value = user.id
                        userId.value = user.name
                        userPw.value = user.pw
                    }, {
                        Timber.d(it)
                    })
            )
        }
    }

    fun requestRevise() {
        _reviseState.value?.let { state ->
            if (!state) {
                _reviseState.value = true
            } else {
                val name = userId.value
                val pw = userPw.value
                when {
                    name?.length!! > 20 || pw?.length!! > 20 -> _idPwOverLengthMsg.call()
                    name.isNullOrBlank() || pw.isNullOrBlank() -> _idPwEmptyMsg.call()
                    else -> {
                        compositeDisposable.add(
                            profileRepository.changeProfile(
                                User(
                                    id = uuid,
                                    fcm = fcmToken,
                                    name = name,
                                    pw = pw
                                )
                            )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe { showLottieProgress() }
                                .doAfterTerminate { hideLottieProgress() }
                                .subscribe({
                                    _reviseState.value = false
                                    _reviseSuccessMsg.call()
                                }, {
                                    _reviseFailMsg.call()
                                    Timber.d(it)
                                })
                        )
                    }
                }
            }
        }
    }

    private fun saveQrCodeFile() {
        compositeDisposable.add(
            profileRepository.saveQrCodeFile(qrCodeBitmap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showLottieProgress() }
                .doAfterTerminate { hideLottieProgress() }
                .subscribe({
                    _saveQrCodeResultMsg.value = true
                }, {
                    _saveQrCodeResultMsg.value = false
                    Timber.d(it)
                })

        )
    }

    fun checkPermission() {
        compositeDisposable.add(
            TedRx2Permission.with(context)
                .setRationaleTitle("이미지 다운로드 권한 요청")
                .setRationaleMessage("이미지 다운로드를 하기 위해서는 사용자님의 권한 수락을 필요로 합니다.")
                .setPermissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .request()
                .subscribe({ tedPermissionResult: TedPermissionResult ->
                    if (tedPermissionResult.isGranted) {
                        saveQrCodeFile()
                    } else {
                        _permissionResultMsg.value = false
                    }
                },
                    { Timber.d(it) }
                )
        )
    }

}