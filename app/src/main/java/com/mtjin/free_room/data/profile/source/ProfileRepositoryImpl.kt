package com.mtjin.free_room.data.profile.source

import android.graphics.Bitmap
import com.mtjin.free_room.model.User
import com.mtjin.free_room.data.profile.source.local.ProfileLocalDataSource
import com.mtjin.free_room.data.profile.source.remote.ProfileRemoteDataSource
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileRemoteDataSource: ProfileRemoteDataSource,
    private val profileLocalDataSource: ProfileLocalDataSource
) : ProfileRepository {
    override fun requestProfile(id: String): Flowable<User> {
        return profileLocalDataSource.getUser(id)
            .observeOn(Schedulers.io())
            .onErrorReturn { User() }
            .flatMapPublisher { cachedUser ->
                if (cachedUser.id == "") {
                    requestRemoteProfile(id).toFlowable().onErrorReturn { User() }
                } else {
                    val local = Single.just(cachedUser)
                    val remote = requestRemoteProfile(id).onErrorResumeNext { local }
                    Single.concat(local, remote)
                }
            }
    }

    override fun requestRemoteProfile(id: String): Single<User> {
        return profileRemoteDataSource.requestProfile(id)
            .observeOn(Schedulers.io())
            .flatMap { user ->
                profileLocalDataSource.insertUser(user).andThen(Single.just(user))
            }
    }

    override fun changeProfile(user: User): Completable {
        return profileRemoteDataSource.changeProfile(user)
            .observeOn(Schedulers.io())
            .doOnComplete { profileLocalDataSource.insertUser(user) }
    }

    override fun saveQrCodeFile(bitmap: Bitmap): Completable {
        return profileLocalDataSource.saveQrCodeFile(bitmap)
    }
}