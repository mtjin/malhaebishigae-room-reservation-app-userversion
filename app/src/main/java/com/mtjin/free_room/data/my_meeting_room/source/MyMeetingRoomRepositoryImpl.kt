package com.mtjin.free_room.data.my_meeting_room.source

import com.mtjin.free_room.model.MyMeetingRoom
import com.mtjin.free_room.data.my_meeting_room.source.local.MyMeetingRoomLocalDataSource
import com.mtjin.free_room.data.my_meeting_room.source.remote.MyMeetingRoomRemoteDataSource
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MyMeetingRoomRepositoryImpl @Inject constructor(
    private val myMeetingRoomRemoteDataSource: MyMeetingRoomRemoteDataSource,
    private val myMeetingRoomLocalDataSource: MyMeetingRoomLocalDataSource
) : MyMeetingRoomRepository {
    override fun requestMyMeetingRooms(): Flowable<List<MyMeetingRoom>> {
        return myMeetingRoomLocalDataSource.getMyMeetingRooms()
            .onErrorReturn { listOf() }
            .flatMapPublisher { cachedMyRooms ->
                if (cachedMyRooms.isEmpty()) {
                    requestRemoteMyMeetingRooms().onErrorReturn { listOf() }
                } else {
                    val local = Flowable.just(cachedMyRooms)
                    val remote =
                        requestRemoteMyMeetingRooms()
                    Flowable.concat(local, remote)
                }
            }
    }


    override fun requestRemoteMyMeetingRooms(): Flowable<List<MyMeetingRoom>> {
        return myMeetingRoomRemoteDataSource.requestMyMeetingRooms().observeOn(Schedulers.io())
            .flatMap { myRooms ->
                myMeetingRoomLocalDataSource.insertMeetingRoom(myRooms)
                    .andThen(Flowable.just(myRooms))
            }
    }

    override fun requestLocalMyMeetingRooms(): Single<List<MyMeetingRoom>> {
        return myMeetingRoomLocalDataSource.getMyMeetingRooms()
    }

    override fun cancelReservation(myMeetingRoom: MyMeetingRoom): Completable =
        myMeetingRoomRemoteDataSource.cancelReservation(myMeetingRoom).observeOn(Schedulers.io())
            .andThen(myMeetingRoomLocalDataSource.deleteMyMeetingRoom(myMeetingRoom))
}