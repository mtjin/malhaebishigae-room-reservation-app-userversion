package com.mtjin.free_room.data.all_meeting_room.source

import com.mtjin.free_room.model.MeetingRoom
import com.mtjin.free_room.data.all_meeting_room.source.local.AllMeetingRoomLocalDataSource
import com.mtjin.free_room.data.all_meeting_room.source.remote.AllMeetingRoomRemoteDataSource
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AllMeetingRoomRepositoryImpl @Inject constructor(
    private val allMeetingRoomRemoteDataSource: AllMeetingRoomRemoteDataSource,
    private val allMeetingRoomLocalDataSource: AllMeetingRoomLocalDataSource
) : AllMeetingRoomRepository {

    override fun requestAllMeetingRooms(): Flowable<List<MeetingRoom>> {
        return allMeetingRoomLocalDataSource.getMeetingRooms()
            .observeOn(Schedulers.io())
            .onErrorReturn { listOf() }
            .flatMapPublisher { cachedRooms ->
                if (cachedRooms.isEmpty()) {
                    requestRemoteAllMeetingRooms().toFlowable().onErrorReturn { listOf() }
                } else {
                    val local = Single.just(cachedRooms)
                    val remote = requestRemoteAllMeetingRooms().onErrorResumeNext { local }
                    Single.concat(local, remote)
                }
            }
    }


    override fun requestRemoteAllMeetingRooms(): Single<List<MeetingRoom>> {
        return allMeetingRoomRemoteDataSource.requestAllMeetingRooms().observeOn(Schedulers.io())
            .flatMap { rooms ->
                allMeetingRoomLocalDataSource.insertMeetingRoom(rooms).andThen(Single.just(rooms))
            }
    }

    override fun requestLocalAllMeetingRooms(): Single<List<MeetingRoom>> =
        allMeetingRoomLocalDataSource.getMeetingRooms()

    override fun requestSearchRooms(roomName: String): Single<List<MeetingRoom>> =
        allMeetingRoomRemoteDataSource.requestSearchRooms(roomName).observeOn(Schedulers.io())

}