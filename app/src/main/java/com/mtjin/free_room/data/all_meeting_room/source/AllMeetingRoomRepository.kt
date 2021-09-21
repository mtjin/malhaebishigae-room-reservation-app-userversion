package com.mtjin.free_room.data.all_meeting_room.source

import com.mtjin.free_room.model.MeetingRoom
import io.reactivex.Flowable
import io.reactivex.Single

interface AllMeetingRoomRepository {
    fun requestAllMeetingRooms(): Flowable<List<MeetingRoom>>
    fun requestRemoteAllMeetingRooms(): Single<List<MeetingRoom>>
    fun requestLocalAllMeetingRooms(): Single<List<MeetingRoom>>
    fun requestSearchRooms(roomName: String) : Single<List<MeetingRoom>>
}