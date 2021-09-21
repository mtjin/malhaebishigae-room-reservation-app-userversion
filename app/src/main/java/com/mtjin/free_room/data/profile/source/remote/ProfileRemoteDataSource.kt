package com.mtjin.free_room.data.profile.source.remote

import com.mtjin.free_room.model.User
import io.reactivex.Completable
import io.reactivex.Single

interface ProfileRemoteDataSource {
    fun requestProfile(id: String): Single<User>
    fun changeProfile(user : User) : Completable
}