package com.mtjin.free_room.views.login

import androidx.lifecycle.LiveData
import com.mtjin.free_room.base.BaseViewModel
import com.mtjin.free_room.data.login.source.LoginRepository
import com.mtjin.free_room.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    BaseViewModel() {

    private val _googleLoginResult = SingleLiveEvent<Boolean>()

    val googleLoginResult: LiveData<Boolean> get() = _googleLoginResult

    fun googleLogin() {
        compositeDisposable.add(
            loginRepository.googleLogin()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgress() }
                .doAfterTerminate { hideProgress() }
                .subscribe({
                    _googleLoginResult.value = true
                }, {
                    _googleLoginResult.value = false
                    Timber.d(it)
                })
        )
    }
}