package com.mtjin.free_room.data.my_meeting_room.source.local

import com.mtjin.free_room.model.MyMeetingRoom
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class MyMeetingRoomLocalDataSourceImpl @Inject constructor(
    private val myMeetingRoomDao: MyMeetingRoomDao
) : MyMeetingRoomLocalDataSource {
    override fun insertMeetingRoom(myMeetingRooms: List<MyMeetingRoom>): Completable =
        myMeetingRoomDao.insertMyMeetingRoom(myMeetingRooms)

    override fun getMyMeetingRooms(): Single<List<MyMeetingRoom>> =
        myMeetingRoomDao.getMyMeetingRooms()

    override fun deleteMyMeetingRoom(myMeetingRoom: MyMeetingRoom): Completable =
        myMeetingRoomDao.deleteMyMeetingRoom(myMeetingRoom)
}