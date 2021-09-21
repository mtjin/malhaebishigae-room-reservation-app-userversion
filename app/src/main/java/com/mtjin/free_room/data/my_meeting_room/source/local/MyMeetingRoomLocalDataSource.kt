package com.mtjin.free_room.data.my_meeting_room.source.local

import com.mtjin.free_room.model.MyMeetingRoom
import io.reactivex.Completable
import io.reactivex.Single

interface MyMeetingRoomLocalDataSource {
    fun insertMeetingRoom(myMeetingRooms: List<MyMeetingRoom>): Completable
    fun getMyMeetingRooms(): Single<List<MyMeetingRoom>>
    fun deleteMyMeetingRoom(myMeetingRoom: MyMeetingRoom): Completable
}