package com.mtjin.free_room.data.login.source.remote

import io.reactivex.Completable

interface LoginRemoteDataSource {
    fun googleLogin(): Completable
}