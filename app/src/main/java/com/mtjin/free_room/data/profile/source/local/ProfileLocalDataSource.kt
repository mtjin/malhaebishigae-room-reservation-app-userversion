package com.mtjin.free_room.data.profile.source.local

import android.graphics.Bitmap
import com.mtjin.free_room.model.User
import io.reactivex.Completable
import io.reactivex.Single

interface ProfileLocalDataSource {
    fun insertUser(user: User): Completable
    fun getUser(id: String): Single<User>
    fun deleteUser(user: User): Completable
    fun saveQrCodeFile(bitmap: Bitmap) : Completable
}