package com.mtjin.free_room.data.all_meeting_room.source.local

import com.mtjin.free_room.model.MeetingRoom
import io.reactivex.Completable
import io.reactivex.Single

interface AllMeetingRoomLocalDataSource {
    fun insertMeetingRoom(meetingRooms: List<MeetingRoom>): Completable
    fun getMeetingRooms(): Single<List<MeetingRoom>>
    fun deleteMeetingRoom(meetingRoom: MeetingRoom): Completable
}