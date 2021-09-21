package com.mtjin.free_room.data.login.source

import com.mtjin.free_room.data.login.source.remote.LoginRemoteDataSource
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val loginRemoteDataSource: LoginRemoteDataSource) :
    LoginRepository {
    override fun googleLogin(): Completable =
        loginRemoteDataSource.googleLogin().observeOn(Schedulers.io())

}