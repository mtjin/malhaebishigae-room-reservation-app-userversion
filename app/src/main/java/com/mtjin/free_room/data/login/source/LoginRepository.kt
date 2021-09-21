package com.mtjin.free_room.data.login.source

import io.reactivex.Completable

interface LoginRepository {
    fun googleLogin(): Completable
}