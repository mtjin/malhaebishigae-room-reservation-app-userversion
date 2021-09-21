package com.mtjin.free_room.data.all_meeting_room.source.remote

import com.mtjin.free_room.model.MeetingRoom
import io.reactivex.Single

interface AllMeetingRoomRemoteDataSource {
    fun requestAllMeetingRooms(): Single<List<MeetingRoom>>
    fun requestSearchRooms(roomName: String): Single<List<MeetingRoom>>
}