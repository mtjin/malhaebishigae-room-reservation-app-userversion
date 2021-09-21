package com.mtjin.free_room.data.profile.source

import android.graphics.Bitmap
import com.mtjin.free_room.model.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface ProfileRepository {
    fun requestProfile(id: String): Flowable<User>
    fun requestRemoteProfile(id: String): Single<User>
    fun changeProfile(user: User): Completable
    fun saveQrCodeFile(bitmap: Bitmap): Completable
}