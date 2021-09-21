package com.mtjin.free_room.data.all_meeting_room.source.local

import com.mtjin.free_room.model.MeetingRoom
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class AllMeetingRoomLocalDataSourceImpl @Inject constructor(private val meetingRoomDao: MeetingRoomDao) :
    AllMeetingRoomLocalDataSource {
    override fun insertMeetingRoom(meetingRooms: List<MeetingRoom>): Completable =
        meetingRoomDao.insertMeetingRoom(meetingRooms)

    override fun getMeetingRooms(): Single<List<MeetingRoom>> = meetingRoomDao.getMeetingRooms()

    override fun deleteMeetingRoom(meetingRoom: MeetingRoom): Completable =
        meetingRoomDao.deleteMeetingRoom(meetingRoom)

}